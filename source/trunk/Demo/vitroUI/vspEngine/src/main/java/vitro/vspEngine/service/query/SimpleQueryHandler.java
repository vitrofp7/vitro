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
package vitro.vspEngine.service.query;

import com.ctc.wstx.stax.WstxInputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.service.common.ConfigDetails;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

import vitro.vspEngine.service.common.StaxHelper.SMTools;
import vitro.vspEngine.service.common.abstractservice.AbstractNotificationManager;
import vitro.vspEngine.service.common.abstractservice.AbstractObservationManager;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;
import vitro.vspEngine.service.engine.UserNode;

/**
 * TODO: ideally we should define an interface or an inherit structure for these handlers to better organise the code
 * @author antoniou
 */
public class SimpleQueryHandler {
    
    private String myPeerId;
    private String myPeerName;
    private MessageProducer myPipeDataProducer;
    private Session myPipeDataSession;

    private Logger logger = Logger.getLogger(SimpleQueryHandler.class);

    /**
     * 
     * Constructor method (for handling responses to queries)
     * TODO: resolve this obscurity with the two constructors (for the user and gw sides)
     */
    public SimpleQueryHandler()
    {
        myPeerId=null; // not needed
        myPeerName =null; // not needed
        myPipeDataProducer=null; // not needed
        myPipeDataSession=null; // not needed
    }

    /**
     *
     * @param responseXMLStr
     */
    public void processResponse(String responseXMLStr, String vgwId) {
        if(vgwId == null || vgwId.isEmpty()) {
            processResponse(responseXMLStr);
        }
        // case of simple TEXT commands (eg REPORT) . REPORT is send from a VGW after being initialized and having sent its resources!
        if(responseXMLStr.compareToIgnoreCase("REPORT") == 0){
            if(UserNode.getUserNode() !=null ) {
                UserNode.getUserNode().sendDirectCommand(vgwId, "VSP reporting back");
                // synch Enabled/Disabled nodes
                String msgToSynchEnableDisableNodes = DisabledNodesVGWSynch.getInstance().createMessageForVGWFromCache(vgwId);
                if(msgToSynchEnableDisableNodes!=null && !msgToSynchEnableDisableNodes.trim().isEmpty()) {
                    UserNode.getUserNode().sendDirectCommand(vgwId, msgToSynchEnableDisableNodes);
                }
                // synch  Equiv stored lists (one-way from VSP towards VGW)
                String msgToSynchOneWayEquivLists = EquivNodeListsVGWSynch.getInstance().createMessageForVGW(vgwId);
                if(msgToSynchOneWayEquivLists!=null && !msgToSynchOneWayEquivLists.trim().isEmpty()) {
                    UserNode.getUserNode().sendDirectCommand(vgwId, msgToSynchOneWayEquivLists);
                }
            }
        }
    }




    /**
     * Method processResponse:
     * <p/>
     * Every handler has to implement this method, too.
     * Processes the response received from another peer.
     * Here we just print the attribute-value pair.
     *
     * @param responseXMLStr The Resolver Query Message to be processed
     */
    public void processResponse(String responseXMLStr) {

        // TODO: try to check if it is a Notification and not a VSN reply
        boolean isNotification = false;
        try {
            //logger.deubg("Checking response for notification...");
            String[] responseTokens=responseXMLStr.split(NotificationsFromVSNs.alertDelimiter);
            if(responseTokens!=null && responseTokens.length > 0 && responseTokens[0].equalsIgnoreCase(NotificationsFromVSNs.ALERT_PREFIX))
            {
                logger.debug("FOUND notification...");
                isNotification = true;
                NotificationsFromVSNs newNotify = new NotificationsFromVSNs(responseXMLStr);
                QueryDefinition tmpQueryDef = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(newNotify.getQueryDefId());
                if(tmpQueryDef!=null)
                {
                //      logger.deubg("ADDED NOTIFICATION notification...");
                    tmpQueryDef.addNotificationForVSN(newNotify);
                    // store this Notification to the DB!
                    // similar code to writeObservationsToDB of FinalResultEntryPerDef class
                    int pCompositeServiceId = -1;
                    int pPartialServiceId = -1;
                    int pCapabilityId = -1;
                    String pResource = "";
                    ////////////////////////////////////////////////////////////////////////////
                    // we need to match with CapabilityId (sid) and with ufid
                    QueryContentDefinition tmpThisQContent = tmpQueryDef.getQContent();
                    if(tmpThisQContent!=null && !tmpThisQContent.isEmptyQueryContent())
                    {
                        int prFuncId = newNotify.getRefFunctId();
                        String pGatewayName = newNotify.getVgwID();
                        String pSensorId = newNotify.getMoteID();
                        ReqFunctionOverData tmpCurrFunct = tmpThisQContent.getUniqueFunctionById(prFuncId);
                        if(tmpCurrFunct!=null)
                        {
                            String currentqeuryDefNoPrefix =  tmpQueryDef.getuQid().replaceAll(Pattern.quote(IndexOfQueries.COMPOSED_DB_PREFIX), "");
                            currentqeuryDefNoPrefix =  currentqeuryDefNoPrefix.replaceAll(Pattern.quote(IndexOfQueries.PREDEPLOYED_PREFIX), "");

                            try {
                                pCompositeServiceId = Integer.parseInt(currentqeuryDefNoPrefix);
                            }catch (Exception e22) {
                                logger.error("Could not parse Composite Service Db Id");
                            }
                            String pHashOfCap = newNotify.getCapabilityCode();
                            // TODO: could this be a Vector of capabilities ? (in a many-to-many relationship)
                            List<String> dummyRefFunctNameLst = new ArrayList<String>();
                            dummyRefFunctNameLst.add(0,""); //the funct name,  gateway level prefix and referencs in query definition
                            dummyRefFunctNameLst.add(1,""); //only the funct name
                            List<Boolean> dummyRefIsDefinitionFunctList = new  ArrayList<Boolean>();
                            dummyRefIsDefinitionFunctList.add(false); // not actually used in the code bellow
                            vitro.vspEngine.service.common.abstractservice.model.Capability dbAssocCap = FinalResultEntryPerDef.findCapabilityDBByFunctIdAndHashOfCap(tmpQueryDef.getuQid(), prFuncId, pHashOfCap, pGatewayName, pSensorId, true, dummyRefFunctNameLst, dummyRefIsDefinitionFunctList);
                            if(dbAssocCap!=null) {
                                pCapabilityId = dbAssocCap.getId();
                                pResource = dbAssocCap.getName();
                            }
                            // TODO: could this be a Vector of Service Instances ? (in a many-to-many relationship)
                            ServiceInstance siTmp = FinalResultEntryPerDef.findPartialServiceDBByCapId(pCompositeServiceId, pCapabilityId);
                            if(siTmp!=null) {
                                pPartialServiceId = siTmp.getId();
                            }
                            else {
                                logger.error("Could not found a corresponding partial service id for cap id: "+ pCapabilityId);
                            }

                            if(dbAssocCap!=null && siTmp!=null) {


                                String pReplcmntId = newNotify.getReplacmntID();

                                //if(newNotify.getCapabilityCode()!= null && !newNotify.getCapabilityCode().isEmpty())
                                //{
                                //pResource = Capability.getNameFromSensorModel(newNotify.getCapabilityCode()) ;
                                //StringBuilder pRBldTmp = new StringBuilder();
                                //pRBldTmp.append(Capability.dcaPrefix);
                                //pRBldTmp.append(pResource);
                                //pResource= pRBldTmp.toString();
                                //}

                                Date pValueTimestamp;
                                Date pNotificationTimestamp;
                                pValueTimestamp = new java.util.Date(Long.parseLong(newNotify.getValueTimestamp()));
                                pNotificationTimestamp = new java.util.Date(Long.parseLong(newNotify.getTimestamp()));
                                float pValue = 0;
                                pValue = newNotify.getValue();
                                String pUom = "";
                                String pNotificMsg = newNotify.getMessage();
                                int pNotificType = newNotify.getType();
                                int aggregatedSensorsNum = 1; //always for notifications this is set fixed (and ignored)

                                boolean pGatewayLevelFunct= false;
                                boolean pCrossGatewayLevelFunct= false;
                                if(newNotify.getLevel() == NotificationsFromVSNs.VSP_LEVEL) {
                                    pCrossGatewayLevelFunct= true;
                                }
                                if(newNotify.getLevel() == NotificationsFromVSNs.GATEWAY_LEVEL) {
                                    pGatewayLevelFunct= true;
                                }

                                long pBoundValue =newNotify.getBoundValue();
                                String  pRefFunctName = newNotify.getRefFunctName();
                                String pRefFunctTriggerSign=newNotify.getRefFunctTriggerSign();
                                int pLevel=newNotify.getLevel();  //maybe redundant
                                int pRefFunctId=newNotify.getRefFunctId();

                                AbstractNotificationManager.getInstance().createNotification(
                                        pPartialServiceId,
                                        pCapabilityId,
                                        pGatewayName,
                                        pSensorId,
                                        pReplcmntId,
                                        pGatewayLevelFunct,
                                        pCrossGatewayLevelFunct,
                                        aggregatedSensorsNum,
                                        pResource,
                                        pValueTimestamp,
                                        pNotificationTimestamp,
                                        pValue,
                                        pUom,
                                        pNotificMsg,
                                        pNotificType,
                                        pBoundValue,
                                        pRefFunctName,
                                        pRefFunctTriggerSign,
                                        pLevel,
                                        pRefFunctId
                                );
                            }
                         
                            else {
                                logger.error("An invalid observation was found, with no associated capability or partial service!");
                            }
                        }
                    }
                    ///////////////////////////////////////////////////////////////////////////
                }  else
                {
                    logger.error("Error while searching for Query Definition for received notification!");
                }
            }
        }catch (Exception ex)
        {
            logger.error(ex.getMessage());
            ex.printStackTrace();
        }
        if(isNotification)
            return;


        UserNodeResponse response = new UserNodeResponse(responseXMLStr);
        if(response.getQueryId() == 0 ||  response.getSrc().isEmpty() ||response.getResponse().isEmpty()) 
        {
            // DEBUG
            logger.debug("Not a query response!!!");
            return ;
        }

        logger.debug("Processing response...");

        // Parse the message from the query string.
        // find what kind of message we are dealing with, and process it accordingly
        XMLStreamReader2 sr = null;
        try {
            InputStream stream = new ByteArrayInputStream((response.getResponse()).getBytes());
            WstxInputFactory f = null;

            SMInputCursor iroot = null;

            f = new WstxInputFactory();
            f.configureForConvenience();
            // Let's configure factory 'optimally'...
            f.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            f.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);

            sr = (XMLStreamReader2)f.createXMLStreamReader(stream);
            iroot = SMInputFactory.rootElementCursor(sr);
            // If we needed to store some information about preceding siblings,
            // we should enable tracking. (we need it for  mygetElementValueStaxMultiple method)
            iroot.setElementTracking(SMInputCursor.Tracking.PARENTS);

            iroot.getNext();

            String messageTypeVal = "";
            String[] elementsToFollow = {"message-type"};
            Vector<String> resultValues = SMTools.mygetElementValueStax(iroot, elementsToFollow, 0);
            if(resultValues!= null && !resultValues.isEmpty()) {
                messageTypeVal = resultValues.elementAt(0);
            }

            if (messageTypeVal.compareToIgnoreCase(PublicResponseAggrMsg.getThisMsgType()) ==0 ) {
                stream.reset();
                logger.debug("Start -Processing incoming aggrResponse");
                processAggregateResponse(stream, response.getQueryId());
                logger.debug("End - Processing incoming aggrResponse");
                return;
            }
            else if(messageTypeVal.compareToIgnoreCase(DisabledNodesVGWSynch.fromVGWMsgType) == 0) {
                stream.reset();
                logger.debug("Start -Processing incoming disable enable nodes msg");
                DisabledNodesVGWSynch.getInstance().parseVGWMsg(stream);
                logger.debug("End -Processing incoming disable enable nodes msg");
                return;
            }

            else if(messageTypeVal.compareToIgnoreCase(EquivNodeListsVGWSynch.fromVGWMsgType) == 0) {
                stream.reset();
                logger.debug("Start -Processing incoming equiv lists synch confirm msg");
                EquivNodeListsVGWSynch.getInstance().parseVGWMsg(stream);
                logger.debug("End -Processing incoming equiv lists synch confirm  msg");
                return;
            }

            else{
                logger.error("Not a message type that VITRO can handle: " +messageTypeVal );
            }
        }
        catch (Exception e) {
            // This is not the right type of response message, or
            // the message is improperly formed. Ignore the message,
            // do nothing.
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        finally {
            if(sr != null){
                try {
                    sr.closeCompletely();
                }catch (XMLStreamException ex2)
                {
                    logger.error("Error while trying to close up XML reader");
                }
            }
        }
    }


    /**
     * Process Aggregate type responses (= issued per gateway)
     * query id is a random integer (different for each issue of an aggregate query for the same query definition)
     */
    public void processAggregateResponse(InputStream stream, int queryId) {
        PublicResponseAggrMsg prm;
        String responderName;
        String responderPeerID;
        String moteId;
        String queryDefId;
        int sensorModelId;
        Vector fileReceived;
        String value;    // Support for various data types is added by the DataTypeAdapter class
        int qCount;

        try {
            // Extract the message from the aggregate response.
            prm = new PublicResponseAggrMsg(stream);

            // Extract the details from the response
            responderPeerID = prm.getResponderPeerID();
            responderName = prm.getResponderName();
            qCount = prm.getQueryCount();

            // Print out the answer given in the response.
            // logger.deubg("Peer "+ responderName + " sent me the following value:");
            // logger.deubg(prm.toString());
            logger.debug("Got Response from Peer " + responderName + " of ID: " + responderPeerID);

            // write the XML structured values in a temporary file. The name of this file will be determined by the qCount
            // so that the receiver knows what it "reads" every time a response is received.
            writeAggrValue(prm, qCount, queryId);

            // new: check if all if_then type functions are met and produce and extra notification!
            // TODO: make this more configurable based on service deploy options!
            UserNode ssUN = UserNode.getUserNode();
            if(ssUN!=null)
            {
                // TODO: check if all ïfThen type functions in the results have at least one result
                boolean allIfThenConditionsHaveOneResult = false;
                int aRefUFid = ReqFunctionOverData.unknownFuncId;
                String aRefCapCode = "";
                try{
                    Vector<ReqFunctionOverData> funcVec = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(prm.getQueryDefID()).getQContent().getUniqueFunctionVec();
                    Vector<ReqResultOverData> resultVec =  prm.getAllValuesVec();
                    int numOfIfThenConds = 0; //init value
                    int numOfIfThenCondsWithResults = 0;
                    for (int i=0; i<funcVec.size(); i++)
                    {
                        ReqFunctionOverData funcCurrent = funcVec.elementAt(i);
                        //logger.deubg("FUNC NAME: "+ funcCurrent.getfuncName());

                        if(ReqFunctionOverData.isValidGatewayReqFunct(funcCurrent.getfuncName()) &&
                             (   (funcCurrent.getfuncName().startsWith(ReqFunctionOverData.GW_LEVEL_PREFIX+ReqFunctionOverData.GW_LEVEL_SEPARATOR+ReqFunctionOverData.ruleRuleIfThenFunc+ReqFunctionOverData.GW_LEVEL_SEPARATOR)) ||
                                     (funcCurrent.getfuncName().startsWith(ReqFunctionOverData.GW_LEVEL_PREFIX+ReqFunctionOverData.GW_LEVEL_SEPARATOR+ReqFunctionOverData.ruleRuleBinaryAndFunc+ReqFunctionOverData.GW_LEVEL_SEPARATOR)) ) )
                        {
                            numOfIfThenConds++;
                            for (int j=0; j<resultVec.size(); j++)
                            {
                                ReqResultOverData resCurr = resultVec.elementAt(j);
                                if(resCurr.getFid() == funcCurrent.getfuncId() )
                                {
                                    if(resCurr.getAllResultsforFunct().size() > 0)
                                    {
                                        numOfIfThenCondsWithResults++;
                                        aRefUFid = resCurr.getFid();
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if(numOfIfThenConds >0 && numOfIfThenConds == numOfIfThenCondsWithResults)
                    {
                        // TODO also check this only if for this VITRO SERVICE we require all conditions to be met (instead of at least one).
                        NotificationsFromVSNs newNotify = new NotificationsFromVSNs();
                        newNotify.setQueryDefId(prm.getQueryDefID());
                        newNotify.setVgwID(prm.getResponderPeerID());
                        newNotify.setTimestamp(Long.toString(System.currentTimeMillis()));
                        newNotify.setMessage("All conditions for service alert were met!");
                        newNotify.setType(NotificationsFromVSNs.ALL_CONDITIONS_MET_TYPE);
                        newNotify.setLevel(NotificationsFromVSNs.VSP_LEVEL);
                        // ???? we also need to set indicative Unique fid and capability code (for the processResponse method to be able later on to figure out the partialServiceId and capabilityId
                        //
                        newNotify.setRefFunctId(aRefUFid);     //these won't be used anywhere other than to locate necessary ids (mainly the associated partialServiceId)
                        aRefCapCode = FinalResultEntryPerDef.findSampleCapabilityHashByFunctId(prm.getQueryDefID(),aRefUFid);
                        newNotify.setCapabilityCode(aRefCapCode);
                        logger.debug("GENERIC ALERT::::::::::::::: CapRefCode: "+ aRefCapCode + " RefFunctId: "+Integer.toString(aRefUFid));
                        String notifMsgToSend = NotificationsFromVSNs.getAlertDelimitedString(newNotify);
                        ssUN.sendSelfAlertMessage(prm.getResponderPeerID(), notifMsgToSend);
                        // this is send to the VSP itself and it is handled like the other notifications (so the persistence is handled there)
                    }
                    else
                    {
                        //logger.deubg("FUNC VEC SIZE: "+ Integer.toString(funcVec.size()) + " and IF THEN FUNCTIONS #="+Integer.toString(numOfIfThenConds) +" and met # are: " + Integer.toString(numOfIfThenCondsWithResults));
                    }
                }catch (Exception ex1)
                {
                    ex1.printStackTrace();
                }

            }

            // Create the file that was received (if one was received). (!!!!????) (++++)
        }
        catch (IOException e) {
            // This is not the right type of response message, or
            // the message is improperly formed. Ignore the message,
            // do nothing.
            e.printStackTrace();
        }

    }


    /**
     * Method writeValue:
     * <p/>
     * A value, temporarily needed by the Query processor is written in a temporary
     * file. The Query processor) should be designed in a way that it knows what it
     * reads each time from this file. Then it deletes the file. For better understanding, see the client implementation.
     *
     * @param count   A counter determining the filename, so the reader of this file can later know what it reads
     * @param queryId The queryId to make the filename unique with high probability. (To do) this is still not unique enough.
     *                We need to correlate this with the issued query content (???)
     */
    private void writeAggrValue(PublicResponseAggrMsg prm, int count, int queryId) {
        try {
            FileWriter fw = new FileWriter(ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "temp" + Integer.toString(queryId) + "__" + Integer.toString(count));
            //for(int i=0; i<values.size(); i++)
            //{
            //    fw.write(values.get(i).toString());
            //}
            fw.write(prm.toString());
            fw.close();
        }
        catch(IOException ioe) {
                ioe.printStackTrace();
            }
	} 

}
