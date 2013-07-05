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
package alter.vitro.vgw.service;

/**
 */

import alter.vitro.vgw.communication.GWCommandMQMessageConsumerProducer;
import alter.vitro.vgw.communication.SOSMQMessageProducer;
import alter.vitro.vgw.model.CGateway;
import alter.vitro.vgw.model.CGatewayWithSmartDevices;
//import alter.vitro.vgw.model.CSmartDevice;
import alter.vitro.vgw.model.CSmartDevice;
import alter.vitro.vgw.service.query.SimpleQueryHandler;
import alter.vitro.vgw.service.query.UserNodeResponse;
import alter.vitro.vgw.service.query.wrappers.*;
import alter.vitro.vgw.service.query.xmlmessages.aggrquery.ReqFunctionType;
import alter.vitro.vgw.service.resourceRegistry.ResourceAvailabilityService;
import alter.vitro.vgw.wsiadapter.DbConInfoFactory;
import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;
import alter.vitro.vgw.wsiadapter.WsiAdapterCon;
import alter.vitro.vgw.wsiadapter.WsiAdapterConFactory;
import net.opengis.sos.v_1_0_0.InsertObservation;
import net.opengis.sos.v_1_0_0.InsertObservationResponse;
import net.opengis.sos.v_1_0_0.RegisterSensor;
import net.opengis.sos.v_1_0_0.RegisterSensorResponse;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vitro.vgw.communication.idas.IdasProxy;
import vitro.vgw.communication.idas.IdasProxyImpl;
import vitro.vgw.communication.request.VgwRequestObservation;
import vitro.vgw.communication.response.VgwResponse;
import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.model.Node;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.rscontroller.RSController;
import vitro.vgw.utils.Utils;


import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
//import java.util.Vector;

/**
 * Ideally this class will be merged with the original one, or conform to a defined interface and then use a factory for the communication with the external middleware (IDAS, backup app)
 */
public class VitroGatewayService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static final int INIT_FLAG = 1;
    public static final int POST_INIT_FLAG = 2;
    //TODO: this will eventually be removed from here:
    private String assignedGatewayUniqueIdFromReg = "vitrogw_tcs" ;  //default value
    private String DVNSUrl = "";
    private String activeMQBrokerUrl = "failover://tcp://amethyst.cti.gr:61616";    //default value
    private boolean useIdas = false;
    private String wsiAdapterName = "TCSWSIAdapter";
    private boolean wsiDTNSupport = false;
    private String wsiDbConInfoName = "http";
    private boolean wsiTrustCoapMessagingSupport = false;
    private boolean activateTrustCoapMessagingPollAfterInitFlg = false;
    private boolean gwWasInitiated = false;

    private Map<String, String> idasNodeMapping;          //changed here to String to String (node actual id in the wsi -to- assigned node id from IDAS)

    //private List<CSmartDevice> cachedSmartDevicesFromLastDiscovery = null;
//    private static final String pSystemClassifier = "system00001";

    private SensorMLMessageAdapter sensorMLMessageAdapter;

    private SOSMQMessageProducer registerSOSpipe;
    private GWCommandMQMessageConsumerProducer commandPipe;

    private RSController rsController;
    private IdasProxy idas;

    private WsiAdapterCon myDCon;
    //private CGatewayWithSmartDevices gwWithNodeDescriptorList;


    /**
     * Creates a new instance of VitroGatewayService
     */
    private VitroGatewayService() {
        idasNodeMapping = new HashMap<String, String>();
    }

    private static VitroGatewayService myVitroGatewayService = null;

    /**
     * This is the function the world uses to get the VitroGateway Service
     * It follows the Singleton pattern
     */
    public static VitroGatewayService getVitroGatewayService() {
        if (myVitroGatewayService == null) {
            myVitroGatewayService = new VitroGatewayService();
        }
        return myVitroGatewayService;
    }

    // TODO: Gateway characteristics should be either parameters for the init or in some configuration file?
    public void init() throws VitroGatewayException {
        logger.debug("init VitroGatewayService");
        //TBD WSI & Sensor Node configurator initialization??

        myDCon = WsiAdapterConFactory.createMiddleWCon(wsiAdapterName, DbConInfoFactory.createConInfo(wsiDbConInfoName));
        myDCon.setDTNModeSupported(isWsiDTNSupport());
        myDCon.setTrustCoapMessagingModeSupported(isWsiTrustCoapMessagingSupport());

        if(!getUseIdas()) // send through pipe to the amethyst activeMQ
        {
            SOSMQMessageProducer.setActiveMQBrokerUrl(this.getActiveMQBrokerUrl());
            GWCommandMQMessageConsumerProducer.setActiveMQBrokerUrl(this.getActiveMQBrokerUrl());
            registerSOSpipe = SOSMQMessageProducer.getRegisterSOSMQManager();
            commandPipe = GWCommandMQMessageConsumerProducer.getMQManager(myDCon);

            try{
                // TODO: the commandPipe will not stop until the process is killed. IMPLEMENT A WAY TO SHUT IT DOWN!
                commandPipe.run(getAssignedGatewayUniqueIdFromReg());
                registerOnDemand(INIT_FLAG);
                commandPipe.sendText("REPORT");
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                logger.error("Error while initializing VGW service",e);
                // TODO: this does not seem to work to close the pipe!!!
                this.shutdown();
            }
        }
        else
        {
            try {
                IdasProxy IdasPrx = new IdasProxyImpl(getDVNSUrl());
                this.setIdas(IdasPrx);
                registerOnDemand(INIT_FLAG);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                // TODO: this does not seem to work to close the pipe!!!
                this.shutdown();
            }
        }
        if(this.activateTrustCoapMessagingPollAfterInitFlg){
            activateTrustRoutingCoapMessagingAfterInit(this.activateTrustCoapMessagingPollAfterInitFlg);
        }
        this.setGwWasInitiated(true);

    }

    /**
     *
     * @param periodInSeconds period of issuing in seconds. TrustRoutingQueryService.NO_PERIOD value means one shot.
     *                        if no period is set then the default period TrustRoutingQueryService.DEFAULT_PERIOD will be used.
     */
    public void setTrustRoutingCoapQueryPeriod( int periodInSeconds)
    {
        TrustRoutingQueryService.setPeriodQueryInterval(periodInSeconds);
    }

    public void setTrustRoutingCoapQueryPerNodeInterPeriod( int interPeriodInSeconds)
    {
        TrustRoutingQueryService.setPeriodBetweenCoapMsgToAnotherNode(interPeriodInSeconds);
    }
    /**
     *
     * @param pSwitch true to enable, false to disable.
     */
    public void activateTrustRoutingCoapMessaging(boolean pSwitch)
    {
        this.activateTrustCoapMessagingPollAfterInitFlg = pSwitch;
        // but if the VGW has already finished initiating (which means it could have skipped to activate the Coap messaging for trust then explicitly start it here) ,
        if(this.isGwWasInitiated() == true){
            activateTrustRoutingCoapMessagingAfterInit(pSwitch);
        }
    }

    private void activateTrustRoutingCoapMessagingAfterInit(boolean pSwitch) {
        if(myDCon == null) {
            logger.error("Call to activateTrustRouting Messaging should be done AFTER the VGW is initialized!");
        }
        else {
            myDCon.setTrustCoapMessagingActive(pSwitch);
            if(isTrustRoutingCoapMessagingActive()){
                TrustRoutingQueryService.getInstance().startScheduler();
            }  else {
                TrustRoutingQueryService.getInstance().stopScheduler();
            }
        }
    }

    public boolean isTrustRoutingCoapMessagingActive(){
        return myDCon.isTrustCoapMessagingActive();
    }


    public Vector<InfoOnTrustRouting> sendTrustRoutingCoapInquiryToAllNodes(){
        Vector<InfoOnTrustRouting>   retVec = new Vector<InfoOnTrustRouting>();
        if(isTrustRoutingCoapMessagingActive()) {
            logger.debug("GETTING ALL NODES FOR WHICH TO SENT TRUST ROUTING COAP MSG");
            List<CSmartDevice> alterNodeDescriptorsList = new ArrayList<CSmartDevice>();
            // TODO: connect this cachedlist with the one in ResourceAvailabilityService. Keep the disabled nodes do not overwrite them!!!
            if(ResourceAvailabilityService.getInstance().getCachedDiscoveredDevices() != null) {
                for(String smIdTmp :ResourceAvailabilityService.getInstance().getCachedDiscoveredDevices().keySet() ) {
                    alterNodeDescriptorsList.add(ResourceAvailabilityService.getInstance().getCachedDiscoveredDevices().get(smIdTmp));
                }
            } else {
                //init a new discovery
                CGateway myGateway = new CGateway();
                myGateway.setId(getAssignedGatewayUniqueIdFromReg());
                myGateway.setName("");
                myGateway.setDescription("");
                CGatewayWithSmartDevices gwWithNodeDescriptorList = myDCon.createWSIDescr(myGateway);
                logger.debug("nodeDescriptorList = {}", gwWithNodeDescriptorList.getSmartDevVec());
                alterNodeDescriptorsList = gwWithNodeDescriptorList.getSmartDevVec();
                ResourceAvailabilityService.getInstance().updateCachedNodeList(alterNodeDescriptorsList, ResourceAvailabilityService.SUBSEQUENT_DISCOVERY);
             }

            Vector<String> allNodeIds = new Vector<String>();
            if(alterNodeDescriptorsList != null) {
                for(CSmartDevice cDev : alterNodeDescriptorsList){
                    logger.debug("Adding node to query for TRUST: " + cDev.getId());
                    allNodeIds.addElement(cDev.getId());
                }
            }
            retVec= myDCon.findRealTimeTrustInfoOnNodes(allNodeIds, TrustRoutingQueryService.getPeriodBetweenCoapMsgToAnotherNode());
            logger.debug("Status of activeMQ pipes: " + (registerSOSpipe == null? "SOS closed " : "SOS open" ) + ", Command queue: "
                    + ((commandPipe.getProducer()  == null || commandPipe.getSession() == null || commandPipe.getConsumer() == null )?  " closed" : "open"));
        }
        else {
            logger.error("Requested to send a Routing Coap Inquiry but Service is inactive!");
        }
        return retVec;
    }

    public void registerOnDemand(int modeFlag) throws Exception
    {
        if(!getUseIdas()) // send through pipe to the amethyst activeMQ
        {
            registerSOSpipe.run();
        }

        //Scan controlled WSI and associated resources

        CGateway myGateway = new CGateway();
        myGateway.setId(getAssignedGatewayUniqueIdFromReg());
        myGateway.setName("");
        myGateway.setDescription("");

        // The code for acquiring GW description is in the createWSIDescr() method
        CGatewayWithSmartDevices gwWithNodeDescriptorList = myDCon.createWSIDescr(myGateway);
        logger.debug("nodeDescriptorList = {}", gwWithNodeDescriptorList.getSmartDevVec());
        List<CSmartDevice> alterNodeDescriptorsList = gwWithNodeDescriptorList.getSmartDevVec();
        ResourceAvailabilityService.getInstance().updateCachedNodeList(alterNodeDescriptorsList, ResourceAvailabilityService.SUBSEQUENT_DISCOVERY);
        // PREPARE TO SEND THE MESSAGE!
        JAXBContext jaxbContext = Utils.getJAXBContext();
        Marshaller mar = jaxbContext.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        // new new
        for (CSmartDevice alterNodeDescriptor : alterNodeDescriptorsList) {

            RegisterSensor registerSensor = sensorMLMessageAdapter.getRegisterSensorMessage(alterNodeDescriptor, gwWithNodeDescriptorList.getGateway());
            if(!getUseIdas()) // send through pipe to the amethyst activeMQ
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mar.marshal(registerSensor, baos);
                String requestXML = baos.toString(HTTP.UTF_8);
                registerSOSpipe.sendText(requestXML);
            }
            else
            {
                RegisterSensorResponse registerSensorResponse = idas.registerSensor(registerSensor);
                String assignedSensorId = registerSensorResponse.getAssignedSensorId();
                // DEBUG
                System.out.println("Registered Sensor with id: " + assignedSensorId);
                //Register assigned sensor id to associate subsequent observation to the correct node
                //TODO: understand if persistence is needed
                idasNodeMapping.put(alterNodeDescriptor.getId(), assignedSensorId);
            }
        }
        // end of --> new new
        /*
        //Register available resources on the external middleware (here the end user / VSP app)
        List<RegisterSensor> registerSensorList = sensorMLMessageAdapter.getRegisterSensorMessage(gwWithNodeDescriptorList);
        // PREPARE TO SEND THE MESSAGE!
        JAXBContext jaxbContext = Utils.getJAXBContext();
        Marshaller mar = jaxbContext.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        for (RegisterSensor registerSensor : registerSensorList) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            mar.marshal(registerSensor, baos);
            String requestXML = baos.toString(HTTP.UTF_8);

            if(!getUseIdas()) // send through pipe to the amethyst activeMQ
            {
                registerSOSpipe.sendText(requestXML);
            }
            else
            {
                IdasProxy IdasPrx = new IdasProxyImpl(getDVNSUrl());
                this.setIdas(IdasPrx);
                RegisterSensorResponse registerSensorResponse = idas.registerSensor(registerSensor);
                String assignedSensorId = registerSensorResponse.getAssignedSensorId();
                // DEBUG
                System.out.println("Registered Sensor with id: " + assignedSensorId);
                idasNodeMapping.put(, assignedSensorId);
            }
        }
          */

        // This closes the pipe for sending registering messages
        if(!getUseIdas()) // send through pipe to the amethyst activeMQ
        {
            registerSOSpipe.stop();
        }
        // SEND TO VSP a confirmation message of nodes/enabled disabled
        // IF VSP was already running and had a cache for this VGW, that this message will be ignored
        // But If VSP was restarted while the VGW was running, then this message will be used to update the VSP!!!
        // send ONLY if you have previously received some request from the VSP
        // which means ONLY if this is NOT the registerOnDemand call in the init(). Otherwise the VGW spoils the cache in the VSP with all nodes set as enabled
        if(modeFlag == POST_INIT_FLAG && isGwWasInitiated()) {
            String msgCurrentEnabledStatusFromCache = ResourceAvailabilityService.getInstance().createSynchConfirmationForVSPFromCurrentStatus_VGWInitiated();
            if(msgCurrentEnabledStatusFromCache != null && !msgCurrentEnabledStatusFromCache.isEmpty()) {
                StringBuilder toAddHeaderBld = new StringBuilder();
                // based on a UserNodeResponse structure we should have a queryId (which here is the messageType again, as src which should be the VSPCore, and a body)
                toAddHeaderBld.append(UserNodeResponse.COMMAND_TYPE_ENABLENODES_RESP);
                toAddHeaderBld.append(UserNodeResponse.headerSpliter);
                toAddHeaderBld.append(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg());

                toAddHeaderBld.append(UserNodeResponse.headerSpliter);
                toAddHeaderBld.append(msgCurrentEnabledStatusFromCache);
                msgCurrentEnabledStatusFromCache = toAddHeaderBld.toString();
                SimpleQueryHandler.getInstance().sendResponse(msgCurrentEnabledStatusFromCache);
            }
        }
    }

    public void shutdown() {
        if(!getUseIdas()) // send through pipe to the amethyst activeMQ
        {
            if(registerSOSpipe!=null)
                registerSOSpipe.stop();
            if(commandPipe!=null)
                commandPipe.stop();
        }
        //shutdown trust routing task! TODO: should be removed and replaced by shutting down the scheduler
        if(isWsiTrustCoapMessagingSupport() && isTrustRoutingCoapMessagingActive()) {
            activateTrustRoutingCoapMessaging(false);
        }
    }

    /*  // ????
    public String ping(@PathParam("input") String input) {

        return input;
    }
      */


    public VgwResponse invokeWSIService(VgwRequestObservation request) throws VitroGatewayException {
        if (!getUseIdas())
        {
            VgwResponse vgwResponse = new VgwResponse();
            vgwResponse.setSuccess(false);
            return vgwResponse;
        }

        // REDUNDANT CODE FOR DEBUGGING PURPOSES (SKIPPING THE REGISTRATION). TODO: remove later (perhaps make  gwWithNodeDescriptorList a private var, although will that create any race conditions for it?)
        CGateway myGateway = new CGateway();
        myGateway.setId(getAssignedGatewayUniqueIdFromReg());
        myGateway.setName("");
        myGateway.setDescription("");

        // TODO: transfer the code for acquiring GW description, in the createWSIDescr() method
        CGatewayWithSmartDevices gwWithNodeDescriptorList = myDCon.createWSIDescr(myGateway);
        logger.debug("nodeDescriptorList = {}", gwWithNodeDescriptorList.getSmartDevVec());
        List<CSmartDevice> alterNodeDescriptorsList = gwWithNodeDescriptorList.getSmartDevVec();
        ResourceAvailabilityService.getInstance().updateCachedNodeList(alterNodeDescriptorsList, ResourceAvailabilityService.INIT_DISCOVERY);
        // END OF REDUNDANT CODE

        logger.debug("request = {}", request);

        //Get requested resource
        String resourceName = request.getObsType().value();
        Resource requestedResource = Resource.getResource(resourceName);
        if(requestedResource == null){
            throw new VitroGatewayException(resourceName + " not managed by VITRO gateway");
        }

        //List<Observation> observationList = rsController.getWSIData(requestedResource);
        Vector< QueriedMoteAndSensors > motesAndTheirSensorAndFunctsVec = new Vector< QueriedMoteAndSensors >();
        Vector< ReqFunctionOverData > reqFunctionVec = new Vector<ReqFunctionOverData>();
        ReqFunctionType rftObject = new ReqFunctionType();
        rftObject.setId(BigInteger.valueOf(1));
        rftObject.setDescription(ReqFunctionOverData.lastValFunc);
        reqFunctionVec.add(new ReqFunctionOverData(rftObject));

        //List<CSmartDevice> alterNodeDescriptorsList = gwWithNodeDescriptorList.getSmartDevVec();
        for (CSmartDevice alterNodeDescriptor : alterNodeDescriptorsList) {
            QueriedMoteAndSensors tmpQueriedMoteAndSensors = new  QueriedMoteAndSensors();
            tmpQueriedMoteAndSensors.setMoteid(alterNodeDescriptor.getId());

            Vector<ReqSensorAndFunctions>  QueriedSensorIdsAndFuncVec = new  Vector<ReqSensorAndFunctions>();
            ReqSensorAndFunctions tmpReqSensingAndFunct = new ReqSensorAndFunctions();

            Integer thedigestInt = request.getObsType().value().hashCode();
            if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

            tmpReqSensingAndFunct.setSensorModelid(Integer.toString(thedigestInt));
            tmpReqSensingAndFunct.getFid().add(BigInteger.valueOf(1));
            QueriedSensorIdsAndFuncVec.add(tmpReqSensingAndFunct);

            tmpQueriedMoteAndSensors.setQueriedSensorIdsAndFuncVec(QueriedSensorIdsAndFuncVec);

            motesAndTheirSensorAndFunctsVec.addElement(tmpQueriedMoteAndSensors);
        }


        Vector<ReqResultOverData> observationsList = myDCon.translateAggrQuery(motesAndTheirSensorAndFunctsVec, reqFunctionVec);

        if(observationsList!=null && !observationsList.isEmpty())
        {
//            // we only have one function, so only one element in the observarionList
//            ReqResultOverData observations = observationsList.elementAt(0);
//            Vector<ResultAggrStruct> tmpResultsStructVec = observations.getAllResultsforFunct();

            for (ReqResultOverData observations : observationsList) {
                Vector<ResultAggrStruct> tmpResultsStructVec = observations.getAllResultsforFunct();
                if(tmpResultsStructVec != null && !tmpResultsStructVec.isEmpty()){
                    for (ResultAggrStruct resultStruct : tmpResultsStructVec) {
                        if(resultStruct.getVal().equalsIgnoreCase(ReqResultOverData.specialValueNoReading) ||
                                resultStruct.getVal().equalsIgnoreCase(ReqResultOverData.specialValuePending) ||
                                        resultStruct.getVal().equalsIgnoreCase(ReqResultOverData.specialValueNotSupported) ||
                                                resultStruct.getVal().equalsIgnoreCase(ReqResultOverData.specialValueBinary) ||
                                                        resultStruct.getVal().equalsIgnoreCase(ReqResultOverData.specialValueTimedOut))
                        {
                            System.out.println("Special values should not be sent!!");
                            continue;
                        }
                        Observation vgwObservation = new Observation();
                        Node theNode = new Node();
                        theNode.setId(resultStruct.getMid());

                        vgwObservation.setNode(theNode);
                        vgwObservation.setResource(requestedResource);
                        // TODO PRODUCES ERROR!!!
                        //System.out.println("Error prone!!");
                        Timestamp tmpTs = resultStruct.getTis().getFromTimestamp();
                        if(tmpTs == null) {
                            java.util.Date today = new java.util.Date();
                            tmpTs = new java.sql.Timestamp(today.getTime());
                        }
                        //System.out.println("Error prone!!");

                        vgwObservation.setTimestamp(tmpTs.getTime());//????
                        vgwObservation.setValue(resultStruct.getVal());
                        //Node node = observation.getNode();
                        String assignedSensorId = idasNodeMapping.get(resultStruct.getMid());
                        logger.debug("Node {} -> assignedSensotId {}", resultStruct.getMid(), assignedSensorId);
                        InsertObservation insertObservation = sensorMLMessageAdapter.getInsertObservationMessage(assignedGatewayUniqueIdFromReg, assignedSensorId, vgwObservation);
                        InsertObservationResponse insertObservationResponse = idas.insertObservation(insertObservation);
                        logger.debug("insertObservationResponse = {}", insertObservationResponse);
                    }
                }
            }
        }
        VgwResponse vgwResponse = new VgwResponse();
        vgwResponse.setSuccess(true);
        return vgwResponse;
    }

    /**
     *
     * Testing purposes
     *
     * @param args  arguments for the main function. Unused.
     */
    public static void main(String[] args) throws IOException {
        VitroGatewayService vgs = VitroGatewayService.getVitroGatewayService();
        try{
            // VitroGatewayService is now singleton , these setters are not static anymore
            vgs.setAssignedGatewayUniqueIdFromReg("vitrogw_tcs");
            vgs.setDVNSUrl("http://195.235.93.106:8002/idas/sml");  // new url
            vgs.setUseIdas(false);
            // CAREFUL:: this is for testing purposes with a localhost ActiveMQ setup.
            vgs.setActiveMQBrokerUrl("failover://tcp://amethyst.cti.gr:61616");
            vgs.setWsiAdapterName("TCSWSIAdapter");
            vgs.setWsiDTNSupport(false); //new: Switch to true if DTN is supported in the controlled WSI by this VGW. Enabling the DTN is done with another method (and on demand by queries that require it)
            vgs.setWsiTrustCoapMessagingSupport(false); // by default it would be false
            vgs.setWsiDbConInfoName("restHttp");

            SensorMLMessageAdapter smlAdapter = new SensorMLMessageAdapter();
            smlAdapter.init();
            vgs.setSensorMLMessageAdapter(smlAdapter);
            vgs.init();
        }
        catch(Exception vgEx)
        {
            System.out.println(vgEx.getMessage());
        }
        //vgs.shutdown() ;
    }


    public String getWsiAdapterName() {
        return wsiAdapterName;
    }

    public void setWsiAdapterName(String wsiAdapterName) {
        this.wsiAdapterName = wsiAdapterName;
    }

    public void setWsiDTNSupport(boolean pWsiDTNSupport) {
        this.wsiDTNSupport = pWsiDTNSupport;
    }


    public String getWsiDbConInfoName() {
        return wsiDbConInfoName;
    }

    public void setWsiDbConInfoName(String wsiDbConInfoName) {
        this.wsiDbConInfoName = wsiDbConInfoName;
    }

    // getters setters
    public String getAssignedGatewayUniqueIdFromReg() {
        return assignedGatewayUniqueIdFromReg;
    }

    public void setAssignedGatewayUniqueIdFromReg(String assignedGatewayUniqueIdFromReg) {
        this.assignedGatewayUniqueIdFromReg = assignedGatewayUniqueIdFromReg;
    }

    public String getDVNSUrl() {
        return DVNSUrl;
    }

    public void setDVNSUrl(String DVNSUrl) {
        this.DVNSUrl = DVNSUrl;
    }

    public boolean getUseIdas() {
        return useIdas;
    }

    public void setUseIdas(boolean useIdas) {
        this.useIdas = useIdas;
    }

    public String getActiveMQBrokerUrl() {
        return activeMQBrokerUrl;
    }

    public void setActiveMQBrokerUrl(String activeMQBrokerUrl) {
        this.activeMQBrokerUrl = activeMQBrokerUrl;
    }


    public RSController getRsController() {
        return rsController;
    }

    public void setRsController(RSController rsController) {
        this.rsController = rsController;
    }

    // commented out for now
    public IdasProxy getIdas() {
        return idas;
    }

    // commented out for now
    public void setIdas(IdasProxy idas) {
        this.idas = idas;
    }

    public SensorMLMessageAdapter getSensorMLMessageAdapter() {
        return sensorMLMessageAdapter;
    }

    public void setSensorMLMessageAdapter(
            SensorMLMessageAdapter sensorMLMessageAdapter) {
        this.sensorMLMessageAdapter = sensorMLMessageAdapter;
    }

    public boolean isWsiDTNSupport() {
        return wsiDTNSupport;
    }

    public boolean isWsiTrustCoapMessagingSupport() {
        return wsiTrustCoapMessagingSupport;
    }

    public void setWsiTrustCoapMessagingSupport(boolean pWsiTrustCoapMessagingSupport) {
        if(pWsiTrustCoapMessagingSupport == false && this.wsiTrustCoapMessagingSupport==true) {
            logger.error("It is not permitted to change this property after setting it to TRUE!");
        }
        else {
            this.wsiTrustCoapMessagingSupport = pWsiTrustCoapMessagingSupport;
        }
    }

    public boolean isGwWasInitiated() {
        return gwWasInitiated;
    }

    public void setGwWasInitiated(boolean gwWasInitiated) {
        this.gwWasInitiated = gwWasInitiated;
    }
}
