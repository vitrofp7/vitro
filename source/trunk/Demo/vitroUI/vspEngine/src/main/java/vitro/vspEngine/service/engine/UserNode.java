/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vitro.vspEngine.service.engine;

/**
 *
 * @author antoniou <antoniou@cti.gr>
 */


import org.apache.log4j.Logger;

import vitro.vspEngine.logic.exception.EngineException;
import vitro.vspEngine.logic.model.*;
import vitro.vspEngine.service.communication.DummyDCACommUtils;
import vitro.vspEngine.service.communication.SOSMQMessageConsumer;
import vitro.vspEngine.service.communication.UserNodeCommandMQMessageConsumerProducer;
import vitro.vspEngine.service.persistence.DBCommons;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.vspEngine.service.query.*;

import java.util.*;
import javax.jms.JMSException;

/**
 */
public class UserNode implements VSPCoreNode{
    public static final int UNSET_COMM_MODE = -1;
    public static final int ACTIVEMQ_COMM_MODE = 0;
    public static final int DCA_COMM_MODE = 1;
    public static final int DEFAULT_COMM_MODE = ACTIVEMQ_COMM_MODE;
    private Logger LOG;
    private String logLevel = "info";

    private static UserNode myUserNode = null;
    private String engineName;// = "VSP Core Engine";
    private String engineID;// = "VSPCoreEngine";
    private HashMap<String, UserNodeCommandMQMessageConsumerProducer> pipesForCommandstoGateways;

    private HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM;    // will contain maps from a gateway id to GatewayWithSmartNodes  objects
    private HashMap<String, Vector<SensorModel>> capHMap;    // will store the "Generic capability id"/"SensorModel id" pairs of the capabilities found on all gateways
    private HashMap<String, Gateway> registeredGatewaysHM; // TODO: Remove? This is a hashmap of registered (VS discovered actual online ones). We could retrieve it from DB so we don't really need it here.

    private IndexOfQueries myIndexOfQueryDefinitions;     // stores an index of Active Queries.

    //private boolean channelDCAUsed = false;      // At this point we keep both communication channels open in parallel,
                                             // but in the end, only one should be kept and it should be possible to switch
                                             // via some Factory pattern
    private int commMode; // Can have one of the values:  ACTIVEMQ_COMM_MODE and UNSET_COMM_MODE to be valid!

    private SOSMQMessageConsumer consumerMQPipe = null;
    /**
     * Constructor.
     * Creates a new instance of UserNode
     */
    private UserNode() {
        LOG = Logger.getLogger(this.getClass());
        LOG.info("Instantiating Engine!");
        gatewaysToSmartDevsHM = new HashMap<String, GatewayWithSmartNodes>();
        //capHMap = (Map)Collections.synchronizedMap(new HashMap<String, Vector<SensorModel>>());
        capHMap = new HashMap<String, Vector<SensorModel>>();
        registeredGatewaysHM = new HashMap<String, Gateway>();   // little different from the gateways to SmartDevs HM which is what we discovered (Vs what is preregistered for resource inquiries)

        myIndexOfQueryDefinitions = IndexOfQueries.getIndexOfQueries();

        this.setCommMode(UNSET_COMM_MODE);

        pipesForCommandstoGateways = new HashMap<String, UserNodeCommandMQMessageConsumerProducer>();        
    }


    /**
     * This is the function the world uses to get the UserNode engine object.
     * It follows the Singleton pattern
     */
    public static UserNode getUserNode() {
        if (myUserNode == null) {
            myUserNode = new UserNode();
            // TODO: should not set this as fixed!!!!
            myUserNode.engineName= "VSP Core Engine";
            myUserNode.engineID = "VSPCoreEngine";
            if (myUserNode.getCommMode() == UserNode.UNSET_COMM_MODE)
            {
                // TODO: initial comm mode could be retrieved from a config file or a parameter in getUserNode() but since we already have too many calls to replace (of getUserNode) let's set it by default...
                // TODO: (cont) A user can change the commMode explicitly before staring the Engine. Also we could support online switching between the available options (two so far)
                myUserNode.switchInternalCommEngine(UserNode.DEFAULT_COMM_MODE);
            }
        }
        return myUserNode;
    }


    // -----------------------------------------------------------------------------------------

    /**
     * Returns the Hashmap with the info for all the discovered gateways.
     * It will be needed by the GUI in order to form the queries based
     * on the user's selections.
     *
     * @return The  gatewaysToSmartDevsHM hashmap. This hashmap will map Gateway ids to GatewayWithSmartNode objects
     */
    public HashMap<String, GatewayWithSmartNodes> getGatewaysToSmartDevsHM() {

        return gatewaysToSmartDevsHM;
    }

    /**
     * Returns the HashMap with the info for all the discovered
     * id/type pairs of the capabilities (e.g. [1, temperature], [2, light] etc.)
     * It will be needed by the GUI for the presentation of the available data.
     *
     * @return The capHMap HashMap. will store the "Generic capability id"/"SensorModel id" pairs of the capabilities found on all gateways
     *         NOTE: There should be a [b]common terminology[/b] of the types of capabilities among gateways (e.g. Light sensors, Microphone, web camera etc)
     */
    public HashMap<String, Vector<SensorModel>> getCapabilitiesTable() {

        return capHMap;
    }

    /**Retrieve the ID for this node
     *
     * @return the ID for this VSP engine node
     */
    public String getPeerID() {
        return engineID;
    }

    /**Retrieve the Name for this node
     *
     * @return the Name for this VSP engine node
     */
    public String getPeerName() {
        return engineName;
    }

    // *************************************************************  METHODS *******************************************************************

    /**
     *
     * @param pCommMode
     * @return   -1 if unsuccesful, and 0 is successful
     */
    public int switchInternalCommEngine(int pCommMode) {
        int retval = -1;
        if(pCommMode != UserNode.ACTIVEMQ_COMM_MODE &&
                pCommMode != UserNode.DCA_COMM_MODE)
        {
            LOG.info("Invalid middleware communication mechanism was requested. The switch was not performed.");
        }
        else
        {
            int currentMode = getCommMode();
            String newModeStr = "INVALID COMM MODE";
            switch(pCommMode) {
                case UserNode.ACTIVEMQ_COMM_MODE:
                    // TODO: do stuff to gracefully close the DCA comm channels if it was previously set as DCA comm (or always just in case)
                    if(currentMode != UserNode.ACTIVEMQ_COMM_MODE)
                    {
                        setCommMode(UserNode.ACTIVEMQ_COMM_MODE);
                        retval = 0;
                    }
                    break;
                case UserNode.DCA_COMM_MODE:
                    // TODO: do stuff to gracefully close the ACTIVEMQ comm channels if it was previously set as DCA comm (or always just in case)
                    if(currentMode != UserNode.DCA_COMM_MODE)
                    {
                        setCommMode(UserNode.DCA_COMM_MODE);
                        retval = 0;
                    }
                    break;
            }
            if(getCommMode() == UserNode.ACTIVEMQ_COMM_MODE)
            {
                newModeStr="direct (activeMQ) comm";
            }
            else if(getCommMode() == UserNode.DCA_COMM_MODE)
            {
                newModeStr="native DCA comm" ;
            }
            if(retval == 0)
                LOG.info("The Middleware communication mechanism was switched to "+newModeStr +" successfuly.");
            else
                LOG.error("Could not set the communication mechanism!");
        }
        return retval;
    }


    /**
     * Method sendAnAggrQuery:
     * <p/>
     * Wrapper of resolver's method sendQuery. It sends a query for sensor
     * values using the Resolver Service of the previously joined group.
     * The details of the query are given as parameters and the QueryMsg is
     * created based on them.
     *
     * @param uQDefID               The unique ID of this query definition, generated by the query processor and kept in this Peer object's IndexOfQueries
     * @param gateID                The id of the gateway peer that is to respond to the query
     * @param motesAndTheirSensorAndFuncVec The hashmap mapping SmartNode-ids to their requested sensor models
     * @param isHistory             The isHist boolean argument, to indicate if a history of values is asked or not
     * @param functionVec           A vector of the specified functions for the data as ReqFunctionOverData objects.
     * @param thisQueryOrderNum     The counter of the query in a set of queries (will be used to distinguish the results that will be received)
     * @return int of the queryId unique to the User peer       //TODO: have an invalid value too
     */
    public int sendAnAggrQuery(String uQDefID, String gateID, Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFuncVec, boolean isHistory, Vector<ReqFunctionOverData> functionVec, int thisQueryOrderNum ,
                               boolean continuationEnabledFlag,
                               boolean asynchronousFlag,
                               boolean dtnEnabledFlag,
                               boolean securityEnabledFlag,
                               boolean encryptionEnabledFlag) {


        /**
         *  Only the Unique functions that are actually used in the query (referenced by ids per sensorModel) should be included in the issued message (and not all in each query to a gateway or a specific mote message)
         * are embedded in the message
         */
        // create the query
        PublicQueryAggrMsg myQuery = new PublicQueryAggrMsg(uQDefID, motesAndTheirSensorAndFuncVec, isHistory, functionVec, thisQueryOrderNum,
                continuationEnabledFlag,
                asynchronousFlag,
                dtnEnabledFlag,
                securityEnabledFlag,
                encryptionEnabledFlag);
        //LOG.debug("The query is here::" + myQuery.toString() + "::");

        // wrap the query in a resolver query.
        // Alternatively:
        /*
         * TODO: fix the way we produce random UNIQUE qid (must check with other existing ids)!!!
         */
        //
        UserNodeQuery rQuery = new UserNodeQuery();
        //rQuery.setHandlerName(handlerName);
//        rQuery.setCredential(null);
        rQuery.setSrc(this.getPeerID());
        rQuery.setQuery(myQuery.toString());
        Random r1 = new Random();
        int tmpQid = 0;

        tmpQid = Math.abs(r1.nextInt()) % 100000000;
        tmpQid += thisQueryOrderNum;   // Qid is different for queries of the same definition
        // +++ ideally here we should take into account the  uQid of the Query Definition
        if (tmpQid < 0) {
            tmpQid = (-1) * tmpQid;
        }
        rQuery.setQueryId(tmpQid);

        LOG.debug("This QueryId is::" + Integer.toString(rQuery.getQueryId()) + ", source id is:" + rQuery.getSrc());
        if(this.getCommMode() == UserNode.DCA_COMM_MODE)
        {
            DummyDCACommUtils.getDummyDCACommUtils().executeAggrQuery(uQDefID, gateID, motesAndTheirSensorAndFuncVec, isHistory, functionVec, thisQueryOrderNum);
        }
        else {
            // send the query
            sendDirectCommand(gateID, rQuery.toString());
            LOG.debug("Sending Query:" + rQuery.toString());
        }
        LOG.debug("Sent Query OK to :" + gateID);
        //System.out.println(rQuery.toString());
        return rQuery.getQueryId();
    }


    /**
     *
     */
    private void initConsumerMQForSOSMsgs()
    {
        LOG.debug("Starting MQ Consumer Connection...");
        consumerMQPipe = new SOSMQMessageConsumer();
        try {
            consumerMQPipe.init(UserNode.getUserNode());
            consumerMQPipe.run();
        } catch (JMSException jmse) {
            jmse.printStackTrace();
            System.exit(1);
        }
        LOG.debug("Achieved MQ Consumer Connection");
    }

    private void init() throws EngineException {
        LOG.debug("Initializing VSP Core Node Service");
        try {
            //begin a separate thread for this in order to not delay the application server from starting up
            RequestResourcesThread reqResThread = new RequestResourcesThread();
            reqResThread.start();
        } catch (Exception e) {
            LOG.error(e.getMessage());
            throw new EngineException("Error while retrieving VGWs resources");
        }
    }

    @Override
    public void startEngine() {
        try {
            // init the consumer pipe, before requesting the resources from each gateway
            initConsumerMQForSOSMsgs();    // this is a single channel for consuming SOS registration messages from EVERY possible VGW! (there are checks in the message handlers about whether it's from a registered gateway or not)
            myUserNode.init();
        } catch (EngineException ex) {
            LOG.error("engine init failed!");
        }
    }

    @Override
    public void stopEngine() {
        LOG.info("Shutting down activeMQ pipes for each gateway... ");
        stopAllMQPipes();
        LOG.info("activeMQ gateway pipes were closed. ");
    }

    /**
     * 
     * @param gateID
     * @param CommandContent 
     */
    public void sendDirectCommand(String gateID, String CommandContent)
    {
        if(gateID == null || gateID.trim().isEmpty() ||
                CommandContent==null || CommandContent.trim().isEmpty()) {
            LOG.debug("No content or vgid defined for send Direct Command");
            return;
        }
        try
        {
            if(!pipesForCommandstoGateways.containsKey(gateID))
            {
                UserNodeCommandMQMessageConsumerProducer gatewayPipe = new  UserNodeCommandMQMessageConsumerProducer();
                pipesForCommandstoGateways.put(gateID, gatewayPipe);
                gatewayPipe.run(gateID);
            }
            //gatewayPipe.sendText("REPORT");
            pipesForCommandstoGateways.get(gateID).sendText(CommandContent);
        }
        catch (JMSException jmse)
        {
            jmse.printStackTrace();
            LOG.error("Error sending a command to gateway via pipe!");
        }
    }

    public void sendSelfAlertMessage(String relatedGateID, String alertMessage){
        try
        {
            if(!pipesForCommandstoGateways.containsKey(relatedGateID))
            {
                UserNodeCommandMQMessageConsumerProducer gatewayPipe = new  UserNodeCommandMQMessageConsumerProducer();
                pipesForCommandstoGateways.put(relatedGateID, gatewayPipe);
                gatewayPipe.run(relatedGateID);
            }
            //gatewayPipe.sendText("REPORT");
            pipesForCommandstoGateways.get(relatedGateID).sendSelfAlertText(alertMessage);
        }
        catch (JMSException jmse)
        {
            jmse.printStackTrace();
            LOG.error("Error sending a command to gateway via pipe!");
        }
    }

    @Override
    public List<Capability> getCapabilitiesForGW(String GWId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SmartNode> getSmartNodesForGW(String GWId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Capability> getAllCapabilitiesOverall() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashMap<String, Vector<SensorModel>> getSensorModelsForCapabilities(List<Capability> reqCapabilities) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void requestStatusUpdateFromGWs(List<String> arrayOfGWIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void purgeResourcesFromGWs(List<String> arrayOfGWIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendSubscriptionForCompositeService(String ServiceDefinition) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getActiveVSNsForUser(String userId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerVGW(String VGW) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void negotiateVGW(String VGWID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enableVGW(String VGWID, boolean enableFlag) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isVGWEnabled(String VGWID) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteVGW(String gatewayID) {
        List<String> gwListToPurge = new ArrayList<String>();
        gwListToPurge.add(gatewayID);
        purgeResourcesFromGWs(gwListToPurge);
        // close associated pipes if any
        UserNodeCommandMQMessageConsumerProducer gatewayPipe = pipesForCommandstoGateways.get(gatewayID);
        if(gatewayPipe!=null)
        {
            gatewayPipe.stop();
        }
        DBCommons.getDBCommons().deleteRegisteredGateway(gatewayID);
    }

    @Override
    public void requestCollaborationWithOtherVSPs(List<String> VSPsCredentials) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void endCollaborationWithOtherVSPs(List<String> VSPsCredentials) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void negotiateVSN() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Stops all created activeMQ consumer-producer pipes per gateway, if any
     */
    public void stopAllMQPipes()
    {

        LOG.info("Shutting down activeMQ VSP consumer pipe... ");
        //close activeMQ pipe for receiving gateway responses
        if(consumerMQPipe!=null)
        {
            consumerMQPipe.stop();
        }
        LOG.info("VSP consumer pipe closed. ");

        Set<String> keySet = pipesForCommandstoGateways.keySet();
        for (String tmpGateId:keySet)
        {
            UserNodeCommandMQMessageConsumerProducer gatewayPipe = pipesForCommandstoGateways.get(tmpGateId);
            if(gatewayPipe!=null)
            {
                gatewayPipe.stop();
            }
        }
    }

    public int getCommMode() {
        return commMode;
    }

    // the setter should not be public. The switching is handled by a special method.
    private void setCommMode(int commMode) {
        this.commMode = commMode;
    }

    /**
     * This is the thread to request resources from the islands (if multiple islands are supported, then multiple targets should be queried in order).
     */
    private class RequestResourcesThread extends Thread {

        private HashMap<String, RequestResourcesThreadInfo> regdRunResourceRequests;
        private final int UPDATE_FINISH_HM = 1;
        private final int UPDATE_START_HM = 2;
        public static final int FIND_IN_HM = 3;

        RequestResourcesThread() {
            regdRunResourceRequests = new HashMap<String, RequestResourcesThreadInfo>(); // associates a unique query id with a QueryJXThreadInfo object
        }

        synchronized public RequestResourcesThreadInfo dealWithRunningRequestsHM(int readWriteFlag, String gwID) {

            if (readWriteFlag == FIND_IN_HM) // we read to find a specific gwID
            {
                if (regdRunResourceRequests.containsKey(gwID)) {
                    return regdRunResourceRequests.get(gwID);
                }
                return null;
            } else if (readWriteFlag == UPDATE_START_HM) {
                RequestResourcesThreadInfo tmpRequestResourcesThreadInfo = null;
                if (regdRunResourceRequests.containsKey(gwID)) {
                    tmpRequestResourcesThreadInfo = regdRunResourceRequests.get(gwID);
                } else {
                    tmpRequestResourcesThreadInfo = new RequestResourcesThreadInfo();
                    tmpRequestResourcesThreadInfo = regdRunResourceRequests.put(gwID, tmpRequestResourcesThreadInfo);
                    tmpRequestResourcesThreadInfo = regdRunResourceRequests.get(gwID);
                }
                GregorianCalendar calendar = new GregorianCalendar();
                long currentTimeInMillis = calendar.getTimeInMillis();
                tmpRequestResourcesThreadInfo.setStatus(RequestResourcesThreadInfo.STATUS_STARTED);
                tmpRequestResourcesThreadInfo.setTimeStarted(currentTimeInMillis);

            } else if (readWriteFlag == UPDATE_FINISH_HM) {
                RequestResourcesThreadInfo tmpRequestResourcesThreadInfo = null;
                if (regdRunResourceRequests.containsKey(gwID)) {
                    tmpRequestResourcesThreadInfo = regdRunResourceRequests.get(gwID);
                } else {
                    tmpRequestResourcesThreadInfo = new RequestResourcesThreadInfo();
                    tmpRequestResourcesThreadInfo = regdRunResourceRequests.put(gwID, tmpRequestResourcesThreadInfo);
                    tmpRequestResourcesThreadInfo = regdRunResourceRequests.get(gwID);
                }
                tmpRequestResourcesThreadInfo.setStatus(RequestResourcesThreadInfo.STATUS_ENDED);
            }
            return null;
        }


        public void run() {
            //int nextQueryToCheck = 0;
            // TODO: This should be better integrated
            if(getCommMode() == UserNode.DCA_COMM_MODE)
            {
                DummyDCACommUtils.getDummyDCACommUtils().startDCAEngine(UserNode.getUserNode());
            }
            else
            {
                // TODO: Retrieve Registered VGWs IDs from the DB!
                LOG.debug("Sending initial resource requests to all registered gateways");
                Vector<String> regGWsRegNamesVec;
                regGWsRegNamesVec = DBCommons.getDBCommons().getRegisteredGatewayRegNames();
                Iterator<String> itgwregnames = regGWsRegNamesVec.iterator();

                while (itgwregnames.hasNext()) {
                    final String currGwId = itgwregnames.next();
                    DBRegisteredGateway currGw = DBCommons.getDBCommons().getRegisteredGateway(currGwId);
                    //
                    // Check in turn every entry in DB for registered Gateways.
                    //
                    GregorianCalendar calendar = new GregorianCalendar();
                    long currentTimeInMillis = calendar.getTimeInMillis();
                    if (currGw != null) //new
                    {
                        RequestResourcesThreadInfo tmpRequestInfo = dealWithRunningRequestsHM(FIND_IN_HM, currGwId);
                        if (tmpRequestInfo == null)     // if null then it's the first time it is issued.
                        {
                            try {
                                new Thread() {
                                    public void run() {
                                        dealWithRunningRequestsHM(UPDATE_START_HM, currGwId);
                                        //launch thread for the request
                                        // TODO: to evaluate if we need threaded requests here.... (for the DCA connection we do probably since we handle the responses immediately (not via ActiveMQ asynch) )
                                        DisabledNodesVGWSynch.getInstance().invalidateCacheForVGW(currGwId);
                                        sendDirectCommand(currGwId, "REPORT");
                                        // TODO: ideally we could handle an extra "REPORT_ENDED" message from VGW to send the one-way synch of Equivalency Lists:
                                        // sendDirectCommand() now includes a check for null or empty messages and won't send them if they are such.
                                        String msgToSynchOneWayEquivLists = EquivNodeListsVGWSynch.getInstance().createMessageForVGW(currGwId);
                                        if(msgToSynchOneWayEquivLists!=null && !msgToSynchOneWayEquivLists.trim().isEmpty()) {
                                            sendDirectCommand(currGwId, msgToSynchOneWayEquivLists);
                                        }
                                        dealWithRunningRequestsHM(UPDATE_FINISH_HM, currGwId);
                                    }
                                }.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Thread.currentThread().sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }
            LOG.debug("All resource requests were sent!");
        }
    }
}

class RequestResourcesThreadInfo {
    private long timeStarted;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_ENDED = 2;
    private int status = STATUS_ENDED;

    public RequestResourcesThreadInfo() {
        this.status = STATUS_ENDED;
        GregorianCalendar calendar = new GregorianCalendar();
        long currentTimeInMillis = calendar.getTimeInMillis();
        this.timeStarted = currentTimeInMillis;
    }

    public RequestResourcesThreadInfo(long givTimeStarted, int givStatus) {
        this.status = givStatus;
        this.timeStarted = givTimeStarted;
    }

    public int getStatus() {
        return status;
    }

    public long getTimeStarted() {
        return timeStarted;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTimeStarted(long timeStarted) {
        this.timeStarted = timeStarted;
    }
}
