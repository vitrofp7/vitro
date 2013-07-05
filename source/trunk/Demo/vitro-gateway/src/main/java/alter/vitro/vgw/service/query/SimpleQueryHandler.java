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
package alter.vitro.vgw.service.query;

import alter.vitro.vgw.communication.GWCommandMQMessageConsumerProducer;
import alter.vitro.vgw.service.ContinuationOfProvisionService;
import alter.vitro.vgw.service.TrustRoutingQueryService;
import alter.vitro.vgw.service.VitroGatewayService;
import alter.vitro.vgw.service.query.wrappers.*;
import alter.vitro.vgw.service.query.xmlmessages.aggrquery.*;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvsp.EnableNodesReqType;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp.EquivListNodesReqType;
import alter.vitro.vgw.service.query.xmlmessages.response.OutType;
import alter.vitro.vgw.service.resourceRegistry.ResourceAvailabilityService;
import alter.vitro.vgw.wsiadapter.DbConInfoFactory;
import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;
import alter.vitro.vgw.wsiadapter.WsiAdapterCon;
import alter.vitro.vgw.wsiadapter.WsiAdapterConFactory;

import java.math.BigInteger;
import java.sql.Timestamp;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import vitro.vgw.exception.VitroGatewayException;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 */
public class SimpleQueryHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static Schema schema = null;
    private static Schema enDisFromVSPschema = null;
    private static Schema equivSynchFromVSPschema = null;
    private String myPeerId;
    private String myPeerName;
    private MessageProducer myPipeDataProducer;
    private Session myPipeDataSession;
    
    private WsiAdapterCon myDCon;

    private static SimpleQueryHandler instance = null;

    public static SimpleQueryHandler getInstance() {
        if(instance == null) {
            instance = new  SimpleQueryHandler();
        }
        return instance;
    }

    /**
     *
     * Constructor method (for handling responses to queries)
     * TODO: resolve this obscurity with the two constructors (for the user and gw sides)
     */
    // TODO: maybe make this into singleton
    private SimpleQueryHandler()
    {
        myPeerId=null; // not needed
        myPeerName =null; // not needed
        myPipeDataProducer=null; // not needed
        myPipeDataSession=null; // not needed
        this.myDCon = null;
        if(schema == null)
        {
            try {
                schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/java/alter/vitro/vgw/service/query/xmlmessages/aggrquery/PublicQueryAggrMsg.xsd"));

            } catch(SAXException saxEx)
            {
                logger.error("Exception while initializing schema from PublicQueryAggrMsg.xsd", saxEx);
            }
        }
        if(enDisFromVSPschema == null)
        {
            try {
                enDisFromVSPschema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/java/alter/vitro/vgw/service/query/xmlmessages/enablednodessynch/fromvsp/fromvsp.xsd"));

            } catch(SAXException saxEx)
            {
                logger.error("Exception while initializing schema  (enable synch) from fromvsp.xsd", saxEx);
            }
        }
        if(equivSynchFromVSPschema == null)
        {
            try {
                equivSynchFromVSPschema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/java/alter/vitro/vgw/service/query/xmlmessages/equivlistsynch/fromvsp/fromvsp.xsd"));

            } catch(SAXException saxEx)
            {
                logger.error("Exception while initializing schema (equiv list synch) from fromvsp.xsd", saxEx);
            }
        }
    }

    /**
     *
     * Init method. Required for handling AND responding to queries
     */
    public void initInstance(String pPeerId, String pPeerName, GWCommandMQMessageConsumerProducer gwCommandMQMessageConsumerProducer, WsiAdapterCon myDCon) {
        myPeerId=pPeerId;
        myPeerName =pPeerName;
        myPipeDataProducer= gwCommandMQMessageConsumerProducer.getProducer();
        myPipeDataSession= gwCommandMQMessageConsumerProducer.getSession();
        
        this.myDCon = myDCon;
        if(schema == null)
        {
            try {
                schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/java/alter/vitro/vgw/service/query/xmlmessages/aggrquery/PublicQueryAggrMsg.xsd"));
            } catch(SAXException saxEx)
            {
                logger.error("Exception while initializing schema from PublicQueryAggrMsg.xsd", saxEx);
            }
        }
        if(enDisFromVSPschema == null)
        {
            try {
                enDisFromVSPschema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/java/alter/vitro/vgw/service/query/xmlmessages/enablednodessynch/fromvsp/fromvsp.xsd"));
            } catch(SAXException saxEx)
            {
                logger.error("Exception while initializing schema  (enable synch) from fromvsp.xsd", saxEx);
            }
        }
        if(equivSynchFromVSPschema == null)
        {
            try {
                equivSynchFromVSPschema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new File("src/main/java/alter/vitro/vgw/service/query/xmlmessages/equivlistsynch/fromvsp/fromvsp.xsd"));
            } catch(SAXException saxEx)
            {
                logger.error("Exception while initializing schema (equiv list synch) from fromvsp.xsd", saxEx);
            }
        }
    }
    /**
     * Method processQuery:
     * <p/>
     *
     * @param queryXMLStr The XML query Message to be processed
     * @return 0, if OK, -1 if not. If OK, it will have generated and forwarded the  XML Response Message
     *         to the sender of the query
     *         TODO: here the code is blocking...  But this will eventually be replaced by a VSN controller within the VGW
     */
    public int processQuery(String queryXMLStr) {
        // a usernode query is the object for the expected query from a user application
        UserNodeQuery query = new UserNodeQuery(queryXMLStr);

        if(query.getQueryId() == 0 ||  query.getSrc().isEmpty() ||query.getQuery().isEmpty())
        {
            // DEBUG:
            //System.out.println("Not a query!!!");
            if (queryXMLStr == null) {
                queryXMLStr= "";
            }
            logger.error("Not a valid query!!!::" + queryXMLStr);
            return -1;
        }

        UserNodeResponse response;

        //System.out.println("Processing query...");
        //System.out.println(queryXMLStr);
        logger.debug("Processing query or command...");
        //logger.debug(queryXMLStr);


        if(query.isSpecialCommand()) {
                    // special cases (this could be a static method "isSpecialQueryId)
            if(query.getQueryId() == UserNodeQuery.SPECIAL_ENABLE_NODES_COMMAND_ID) {
               //
                // CASE OF ENABLE/DISABLE NODES MESSAGE
                //
                logger.debug("Handling ENABLE-DISABLE Message!");
                try {
                    InputStream stream;
                    stream = new ByteArrayInputStream(query.getQuery().getBytes());
                    // create a JAXBContext capable of handling classes generated into package
                    javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvsp");
                    // create an Unmarshaller
                    javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    if(enDisFromVSPschema == null) {
                        logger.error("Could not acquire schema info for en-dis: fromvsp.xsd");
                        return -1;
                    }
                    unmarshaller.setSchema(enDisFromVSPschema);

                    // unmarshal an instance document into a tree of Java content
                    // objects composed of classes from the package.
                    // TODO: fixing the xsd not to reference types of elements outside the elements will clear up the unmarshaller
                    EnableNodesReqType recvdQuery  = (EnableNodesReqType) ((JAXBElement)unmarshaller.unmarshal(stream )).getValue();

                    //preliminary check to see if we have other types of queries
                    if(recvdQuery.getMessageType().trim().compareToIgnoreCase(UserNodeQuery.COMMAND_TYPE_ENABLENODES) == 0)
                    {
                        // start preparing the response
                        String msgToSend = ResourceAvailabilityService.getInstance().createSynchConfirmationForVSP(recvdQuery);
                        if(msgToSend!=null || !msgToSend.trim().isEmpty() ) {
                            //send the confirmation message with the right header
                            StringBuilder toAddHeaderBld = new StringBuilder();
                            // based on a UserNodeResponse structure we should have a queryId (which here is the messageType again, as src which should be the VSPCore, and a body)
                            toAddHeaderBld.append(UserNodeResponse.COMMAND_TYPE_ENABLENODES_RESP);
                            toAddHeaderBld.append(UserNodeResponse.headerSpliter);
                            toAddHeaderBld.append(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg());

                            toAddHeaderBld.append(UserNodeResponse.headerSpliter);
                            toAddHeaderBld.append(msgToSend);
                            msgToSend = toAddHeaderBld.toString();
                            sendResponse(msgToSend);
                        } else {
                            logger.debug("No message to send for enable disable list confirmation");
                        }
                        logger.debug("Successfully Handled ENABLE-DISABLE Message!");
                        logger.debug(ResourceAvailabilityService.getInstance().printDevicesAndEnabledStatus());
                    }
                    else
                    {
                        // DEBUG
                        logger.debug("Error: Invalid command type received!" + recvdQuery.getMessageType().trim());
                    }

                } catch (javax.xml.bind.JAXBException je) {
                    logger.error("JaxBException in processQuery method", je);
                    return -1;
                }
                catch (Exception eall) {
                    logger.error("General Exception in processQuery method",eall);
                    return -1;
                }
            } else if (query.getQueryId() == UserNodeQuery.SPECIAL_EQUIV_LIST_SYNCH_COMMAND_ID) {
                //
                // CASE OF SYNCH EQUIV LISTS MESSAGE
                //
                logger.debug("Handling SYNCH EQUIV LISTS MESSAGE!");
                try {
                    InputStream stream;
                    stream = new ByteArrayInputStream(query.getQuery().getBytes());
                    // create a JAXBContext capable of handling classes generated into package
                    javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp");
                    // create an Unmarshaller
                    javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                    if(equivSynchFromVSPschema == null) {
                        logger.error("Could not acquire schema info for equiv synch: fromvsp.xsd");
                        return -1;
                    }
                    unmarshaller.setSchema(equivSynchFromVSPschema);

                    // unmarshal an instance document into a tree of Java content
                    // objects composed of classes from the package.
                    // TODO: fixing the xsd not to reference types of elements outside the elements will clear up the unmarshaller
                    EquivListNodesReqType recvdQuery  = (EquivListNodesReqType) ((JAXBElement)unmarshaller.unmarshal(stream )).getValue();

                    //preliminary check to see if we have other types of queries
                    if(recvdQuery.getMessageType().trim().compareToIgnoreCase(UserNodeQuery.COMMAND_TYPE_EQUIV_LIST_SYNCH) == 0)
                    {
                        // start preparing the response
                        //response = new UserNodeResponse();
                        // TODO:
                        // return processAggregateQuery(new QueryAggrMsg(recvdQuery), query.getQueryId(),response);
                        String msgToSend = ContinuationOfProvisionService.getInstance().createSynchConfirmationForVSP(recvdQuery);
                        if(msgToSend!=null || !msgToSend.trim().isEmpty() ) {
                            //send the confirmation message with the right header
                            StringBuilder toAddHeaderBld = new StringBuilder();
                            // based on a UserNodeResponse structure we should have a queryId (which here is the messageType again, as src which should be the VSPCore, and a body)
                            toAddHeaderBld.append(UserNodeResponse.COMMAND_TYPE_EQUIV_LIST_SYNCH_RESP);
                            toAddHeaderBld.append(UserNodeResponse.headerSpliter);
                            toAddHeaderBld.append(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg());

                            toAddHeaderBld.append(UserNodeResponse.headerSpliter);
                            toAddHeaderBld.append(msgToSend);
                            msgToSend = toAddHeaderBld.toString();
                            sendResponse(msgToSend);
                        } else {
                            logger.debug("No message to send for equiv list confirmation");
                        }
                        logger.debug("Successfully Handled SYNCH EQUIV LISTS Message!");
                        logger.debug(ContinuationOfProvisionService.getInstance().printEquivListsAndReplacementLists());
                    }
                    else
                    {
                        // DEBUG
                        logger.debug("Error: Invalid command type received!" + recvdQuery.getMessageType().trim());
                    }
                } catch (javax.xml.bind.JAXBException je) {
                    logger.error("JaxBException in processQuery method", je);
                    return -1;
                }
                catch (Exception eall) {
                    logger.error("General Exception in processQuery method",eall);
                    return -1;
                }
            }
        }
        else {

            // Parse the message from the query string.
            // find what kind of message we are dealing with, and process it accordingly
            //
            try {
                InputStream stream;
                stream = new ByteArrayInputStream(query.getQuery().getBytes());
                // create a JAXBContext capable of handling classes generated into package
                javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.aggrquery");
                // create an Unmarshaller
                javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                if(schema == null) {
                    logger.error("Could not acquire schema info for: PublicQueryAggrMsg.xsd");
                    return -1;
                }
                unmarshaller.setSchema(schema);

                // unmarshal an instance document into a tree of Java content
                // objects composed of classes from the package.
                // TODO: fixing the xsd not to reference types of elements outside the elements will clear up the unmarshaller
                MyQueryAggrType recvdQuery  = (MyQueryAggrType) ((JAXBElement)unmarshaller.unmarshal(stream )).getValue();

                //preliminary check to see if we have other types of queries
                if(recvdQuery.getMessageType().trim().equals(QueryAggrMsg.getThisMsgType()))
                {
                    // start preparing the response
                    response = new UserNodeResponse();
                    return processAggregateQuery(new QueryAggrMsg(recvdQuery), query.getQueryId(),response);
                }
                else
                {
                    // DEBUG
                    logger.debug("Error: Invalid query type received!" + recvdQuery.getMessageType().trim());
                }
            } catch (javax.xml.bind.JAXBException je) {
                logger.error("JaxBException in processQuery method", je);
                return -1;
            }
            catch (Exception eall) {
                logger.error("General Exception in processQuery method",eall);
                return -1;
            }
        }
        return 0;
    }


    /**
     * Aggregate type queries (Aggregated queries are created to be issued per gateway, but they are also
     * used to be issued per mote sensor)
     *
     * @param aggrQuery    The QueryAggrMsg that arrived
     * @param wrpResponse The wrapped Response to be sent as a reply to the query.
     * @return 0 if the query was processed successfully and -1 if errors were encountered
     */

    public int processAggregateQuery(QueryAggrMsg aggrQuery, int specificQueryId, UserNodeResponse wrpResponse) {
        // new: these two are needed to associate the security Coap messages at the VSP with partial services


//        List<MoteType> motesAndTheirSensorAndFunctVec;
        Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFunctVec;
        boolean isHist;

        boolean asynch;
        boolean encrypt;
        boolean dtn;
        boolean security;
        boolean continuation;

        Vector<ReqFunctionOverData> reqFunctionVec;

        // Get the counter of the query. It will be copied to the response message
        int qCount =aggrQuery.getQuery().getQueryCount();
        //Vector fileToSend = new Vector();

        String uniqQueryDefID = aggrQuery.getQuery().getQueryDefID().trim();

        String value; //Support for various data types is added by the DataTypeAdapter class

        try {

            logger.debug("Processing Aggregated query with id " + uniqQueryDefID + " and count: "+Integer.toString(qCount));

            motesAndTheirSensorAndFunctVec = aggrQuery.getMotesAndTheirSensorAndFunctVec();
            reqFunctionVec = aggrQuery.getReqFunctionVector();

            isHist = aggrQuery.getQuery().isIsHistory();
            //new 21/03/13
            asynch = aggrQuery.getQuery().isAsynch();
            encrypt= aggrQuery.getQuery().isEncrypt();
            dtn = aggrQuery.getQuery().isDtn();
            security = aggrQuery.getQuery().isSecurity();
            continuation = aggrQuery.getQuery().isContinuation();

            if(dtn)
            {
                if(!myDCon.isDTNModeSupported()){
                    logger.info("DTN mode was requested but is unsupported by this VGW!");
                }
                myDCon.setDtnPolicy(dtn); // this includes in its implementation a check for DTN mode support (we don't have to include it in an else block
                if(myDCon.getDtnPolicy()){
                    logger.info("DTN mode was requested and activated!");
                }
                else {
                    logger.info("DTN mode was requested but WAS NOT activated!");
                }
            }
            // TODO: Fix the code that handles the History function
            // e.g. Call the function that puts the file (if requested) to be sent into a vector
            if (isHist) {
                ; // (++++)
            }

            //Perform the search to see if the peer is aware of such an attribute.
            List<String> out_decidedDeployStatus = new ArrayList<String>(1);
            // [0] is the original nodeId, [1] the replacing node id and [2] the capability
            // skip the entries that [0] is the same with [1]
            List<String[]> out_replacementLocalInfoList = new ArrayList<String[]>();
            Vector<ReqResultOverData> allValuesVec = findAggrAttrValue(uniqQueryDefID, motesAndTheirSensorAndFunctVec, reqFunctionVec, out_decidedDeployStatus, out_replacementLocalInfoList);

            // Get my identity in responderName so that the peer that made the query knows who is answering him

            // Create the response message.
            ResponseAggrMsg prm = new ResponseAggrMsg(uniqQueryDefID, myPeerId, myPeerName, allValuesVec, qCount);
            if(out_decidedDeployStatus!=null && out_decidedDeployStatus.size()>0) {
                prm.setDeployStatus(out_decidedDeployStatus.get(0));
            }

            Vector<RespServContinuationReplacementStruct> continuationInfoVec = new Vector<RespServContinuationReplacementStruct>();
            if(out_replacementLocalInfoList!=null && out_replacementLocalInfoList.size() > 0) {
                for(String[] entryInConList : out_replacementLocalInfoList){
                    if(entryInConList[0].compareToIgnoreCase(entryInConList[1] )!=0 ){
                        RespServContinuationReplacementStruct entryInFinalContList = new RespServContinuationReplacementStruct(entryInConList[0], entryInConList[1], entryInConList[2]);
                        continuationInfoVec.addElement(entryInFinalContList);
                    }
                }
            }
            else {
                logger.debug("No Replacement info found!");
            }
            prm.setServiceContinuationList(continuationInfoVec);

            logger.info("Sending response!");
            logger.debug(prm.toString());

            // Append the response message with some special info.
            wrpResponse.setQueryId(specificQueryId);
            wrpResponse.setSrc(myPeerId); //not really needed though
            wrpResponse.setResponse(prm.toString());

            // Send the response to the requesting end user
            this.sendResponse(wrpResponse.toString());

            // Signal that the resolver handled sending the response.
            return 0; //means OK
        }
        catch (Exception e) {
            // Signal that the query should be re-propagated.
            logger.error("General exception from processAggregateQuery method" , e);
            return -1; // means error
        }
    }


//    // TEMP TEMP
/*    public static List removeElementAt(List input, int index) {
        List result = new ArrayList();
        for(int i=0; i< input.size(); i++ )
        {
            if(i!=index)
                result.add(input.get(i)) ;

        }

        return result;
    }*/

    /** aux function
     * prevent duplicate entries
     * @param localReplacedResources
     * @param replacementInfo
     */
    private void addToLocalReplacementInfoList(List<String[]> localReplacedResources, String[] replacementInfo) {
         boolean entryExists = false;
         if(localReplacedResources!=null && replacementInfo!=null && replacementInfo.length==3) {
             for(String[] localItem : localReplacedResources) {
                    if(localItem.length == 3 &&
                            (( localItem[0].compareToIgnoreCase(replacementInfo[0])== 0 && localItem[2].compareToIgnoreCase(replacementInfo[2]) == 0)
                                || (localItem[1].compareToIgnoreCase(replacementInfo[1])== 0 && localItem[2].compareToIgnoreCase(replacementInfo[2]) == 0)
                            ) ) {
                        entryExists = true;
                    }
             }
         }
        if(!entryExists) {
            localReplacedResources.add(replacementInfo);
        }
    }

    /** Reverse lookup for the original moteID that was replaced
     *
     * @param localReplacedResources
     * @param replacementMoteId
     * @param capabilityId
     * @return
     */
    private String[] getLocalReplacemntInfoListItem(List<String[]> localReplacedResources, String replacementMoteId, String capabilityId) {
        String[] retItem = null;
        if(localReplacedResources!=null && replacementMoteId!=null && capabilityId!=null) {
            for(String[] localItem : localReplacedResources) {
                if(localItem.length == 3 &&
                        localItem[1].compareToIgnoreCase(replacementMoteId)== 0 &&
                        localItem[2].compareToIgnoreCase(capabilityId)== 0) {
                    retItem = localItem;
                    break;
                }
            }
        }
        return retItem;
    }
     //
     // Method findAggrAttrValue finds values for all motes and sensors specified in the HashMap argument
     //
     //
    private Vector<ReqResultOverData> findAggrAttrValue(String pQueryDefId, Vector<QueriedMoteAndSensors> pMotesAndTheirSensorAndFunctsVec, Vector<ReqFunctionOverData> reqFunctionVec, List<String> serviceDeployStatusStr, List<String[]> localReplacedResources) {


        //
        // ADDED CODE -- OPTIMIZATION PENDING +++++
        //
        // --------------- SERVICE CONTINUATION PREP
        // TODO: SERVICE CONTINUATION PREP
        //service Continuation Additions:
        //String serviceDeployStatusStr = ResponseAggrMsg.DEPLOY_STATUS_SERVICE_UNKNOWN;
        serviceDeployStatusStr.add(ResponseAggrMsg.DEPLOY_STATUS_SERVICE_UNKNOWN);
        // deploy status flags
        boolean serviceDeployAllNodesAvailable = true;
        boolean serviceDeployContinuationEmployed = false;
        boolean serviceDeployPartiallyPossible = false;
        boolean serviceDeployImpossible = false;
        // [0] is the original nodeId, [1] the replacing node id and [2] the capability
        //List<String[]> localReplacedResources = new ArrayList<String[]>();

        //
        //
        // TODO: 1.Use the motesAndTheirSensorAndFunctVec to get the requested motes and the requested capabilities.
        // TODO: 2.Check wth Continuation Service and Resource Availability Service.
        //      TODO.   2a. If all nodes are available then Deploy_Status = ResponseAggrMsg.DEPLOY_STATUS_SERVICE_POSSIBLE.
        //              2b. If a node in the requested motes is unavailable (or future: a requested resource is unavailable)
        //                Check the equivalent nodes for matches for this capability.
        //                If a match is found, replace the node in the motesAndTheirSensorAndFunctsVec with the replacement node
        //                  and keep this replacing tracked/stored locally (as well as the cache of the continuationService)
        //                  when the results are found, replace the original mote back, but also send the extra xml that says that the values from that node for that capability are from the replacement node
        //                  TODO: Careful! a node could be replaced by more than one nodes, based on the capabilities requested! TEST THIS CASE!
        //                  TODO: Careful! a node could be replaced for one capability, but not for another!
        //                  Also set the flag serviceContinuationEmployed to true.
        //                      if at the end only this flag is set then update the Deploy_Status to  ResponseAggrMsg.DEPLOY_STATUS_SERVICE_CONTINUATION
        //               If a match is not found then remove this node from the results.
        //                  Also set the flag servicePartiallyPossible to true.
        //                  if at the end only this flag is set then update the Deploy_Status ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL
        //              If a the end both flags  serviceContinuationEmployed and servicePartiallyPossible are true
        //                  and not the serviceImpossible flag then update the Deploy_Status to ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO
        //
        //              Finally if NO nodes are available for the service set the serviceImpossible flag to true and
        //                  update the deploy_status to  ResponseAggrMsg.DEPLOY_STATUS_SERVICE_IMPOSSIBLE
        // END: SERVICE CONTINUATION PREP
        Vector<QueriedMoteAndSensors> originalMotesAndTheirSensorAndFunctsVec = pMotesAndTheirSensorAndFunctsVec;
        Vector<QueriedMoteAndSensors> newMotesAndTheirSensorAndFunctsVec = new Vector<QueriedMoteAndSensors>();

        List<String> allInvolvedMoteIdsList = new ArrayList<String>();
        for(QueriedMoteAndSensors aMoteAndSensors : originalMotesAndTheirSensorAndFunctsVec) {
            allInvolvedMoteIdsList.add(aMoteAndSensors.getMoteid());
        }
        logger.debug("Queried motes and sensors:");
        for(QueriedMoteAndSensors aMoteAndSensors : originalMotesAndTheirSensorAndFunctsVec) {
            logger.debug("Mote Id: " + aMoteAndSensors.getMoteid());
            if(aMoteAndSensors.getQueriedSensorIdsAndFuncVec() != null && !aMoteAndSensors.getQueriedSensorIdsAndFuncVec().isEmpty()){
                HashMap<String, Vector<Integer> > functionsForCapabilityOfThisMoteHM = new HashMap<String, Vector<Integer> >();
                for( ReqSensorAndFunctions sensAndFuncts : aMoteAndSensors.getQueriedSensorIdsAndFuncVec() ) {
                    logger.debug("     Capabilities: " + sensAndFuncts.getSensorModelid()); // TODO: we could probably acquire the friendly name too from some map
                    //TODO: this isNodeResourceAvailable could be also done ideally within the ContinuationOfProvisionService within the findNextEquivalaneNode funciton (also could be synchronized)
                    //logger.debug("DDDDD Size of functs:"+ Integer.toString(sensAndFuncts.getFunctionsOverSensorModelVec().size()));
                    //{
                    //    int smid = sensAndFuncts.getSensorModelIdInt();
                    //    //logger.debug("For mote "+fullMoteId +" and sensor "+Integer.toString(smid) + " function vector size is "+reqFunctionVec.size());
                    //    for (Integer inFunctVec : sensAndFuncts.getFunctionsOverSensorModelVec()) {
                    //        logger.debug("Fid: " + inFunctVec);
                    //    }
                   // }

                    functionsForCapabilityOfThisMoteHM.put(sensAndFuncts.getSensorModelid(), sensAndFuncts.getFunctionsOverSensorModelVec()) ;
                    if( !ResourceAvailabilityService.getInstance().isNodeResourceAvailable(pQueryDefId, aMoteAndSensors.getMoteid(),sensAndFuncts.getSensorModelid()) ) {
                        logger.debug("Node id: " +  aMoteAndSensors.getMoteid() + " unavailable for: " + sensAndFuncts.getSensorModelid());
                        String[] replacementInfo = ContinuationOfProvisionService.getInstance().findNextEquivalentNode(pQueryDefId, allInvolvedMoteIdsList, aMoteAndSensors.getMoteid(), sensAndFuncts.getSensorModelid());
                        if(replacementInfo == null) {
                            //
                            logger.debug("Could not find replacement node for " + sensAndFuncts.getSensorModelid() + " vsn id: " + pQueryDefId);
                            serviceDeployPartiallyPossible = true;
                        } else {
                            logger.debug("Found replacement node "+ replacementInfo[1] +" for node " + replacementInfo[0]  + " for " + replacementInfo[2] + " vsn id: " + pQueryDefId);
                            serviceDeployContinuationEmployed = true;
                            // to prevent duplicates (though there really should not be such case)
                            addToLocalReplacementInfoList(localReplacedResources, replacementInfo);

                        }

                    }   //end if: node capability is not available
                    else{ //capability is available
                        // add self as a replacement (locally)
                        // a node could be available for some capabilities but not for others
                        String[] replacementInfo =  {aMoteAndSensors.getMoteid(), aMoteAndSensors.getMoteid(), sensAndFuncts.getSensorModelid() };
                        logger.debug("Adding self to local cache");
                        addToLocalReplacementInfoList(localReplacedResources, replacementInfo);
                    }
                } //end for loop for this node's capability

                //loop through the localReplacedResources for this node and update the newMotesAndTheirSensorAndFunctsVec
                List<String> consideredReplacementNodes = new ArrayList<String>();
                for(String[] entryLocal : localReplacedResources) {
                    //logger.debug("Checking  localReplacedResources for: " + entryLocal[0]);
                    if(entryLocal[0].compareToIgnoreCase(aMoteAndSensors.getMoteid()) == 0) {
                        String idOfOneReplacingNode = entryLocal[1];
                        if(!consideredReplacementNodes.contains(idOfOneReplacingNode)) {
                            //logger.debug("INNER Checking  localReplacedResources for: " + idOfOneReplacingNode);
                            consideredReplacementNodes.add(idOfOneReplacingNode);

                            Vector<ReqSensorAndFunctions> replacementNodeSensorAndFuncts = new  Vector<ReqSensorAndFunctions>();
                            QueriedMoteAndSensors replacementMoteAndSensors = new QueriedMoteAndSensors(idOfOneReplacingNode, replacementNodeSensorAndFuncts);
                            // inner loop again to find all capabilities that this node (idOfOneReplacingNode) is a replacement for
                            for(String[] entryLocalInner : localReplacedResources) {
                                if(entryLocalInner[0].compareToIgnoreCase(aMoteAndSensors.getMoteid()) == 0 &&
                                        entryLocalInner[1].compareToIgnoreCase(idOfOneReplacingNode) == 0) {
                                    //logger.debug("INNER MATCh FOUND for: " +  entryLocalInner[1] + " capability: " + entryLocalInner[2] );
                                    String capabilityToAdd = entryLocalInner[2];
                                    int capabilityToAddInt = ReqSensorAndFunctions.invalidSensModelId;
                                    try{
                                        capabilityToAddInt = Integer.valueOf(capabilityToAdd);
                                    }catch (Exception ex33) {
                                        logger.error("Could not convert capability id to int for replacement capability: " + capabilityToAdd);
                                    }
                                    //logger.error("CAP TO ADD" + capabilityToAdd);
                                    if(functionsForCapabilityOfThisMoteHM.containsKey(capabilityToAdd)
                                            && functionsForCapabilityOfThisMoteHM.get(capabilityToAdd)!=null
                                            && !functionsForCapabilityOfThisMoteHM.get(capabilityToAdd).isEmpty())
                                    {
                                        //logger.error("FOUND IN HASHMAP!!!");
                                        Vector<Integer> funcsOverThisCapability = functionsForCapabilityOfThisMoteHM.get(capabilityToAdd);
                                        //int smid = capabilityToAddInt;
                                        //logger.debug("DEB DEB For mote "+aMoteAndSensors.getMoteid() +" and sensor "+Integer.toString(smid) + " function vector size is "+reqFunctionVec.size());
                                        //for (Integer inFunctVec : funcsOverThisCapability) {
                                        //    logger.debug("DEB DEB Fid: " + inFunctVec);
                                        //}
                                        ReqSensorAndFunctions thisSensorAndFuncts = new ReqSensorAndFunctions(capabilityToAddInt, funcsOverThisCapability);
                                        //thisSensorAndFuncts.getSensorModelid();
                                        //thisSensorAndFuncts.getFunctionsOverSensorModelVec().size();
                                        //logger.debug("DEB DEB 333 For  sensor "+ thisSensorAndFuncts.getSensorModelid()+ " function vector size is "+ thisSensorAndFuncts.getFunctionsOverSensorModelVec().size());
                                        //for (Integer inFunctVec : funcsOverThisCapability) {
                                        //    logger.debug("DEB DEB 333 Fid: " + inFunctVec);
                                        //}
                                        replacementNodeSensorAndFuncts.addElement(thisSensorAndFuncts);
                                    }
                                }
                            }
                            if(!replacementNodeSensorAndFuncts.isEmpty()) {
                                //logger.error("ADDING ELEMENT TO NEW MOTES LIST!!!" + replacementMoteAndSensors.getMoteid() + ":: " + Integer.toString(replacementMoteAndSensors.getQueriedSensorIdsAndFuncVec().size()));
                                replacementMoteAndSensors.setQueriedSensorIdsAndFuncVec(replacementNodeSensorAndFuncts);
                                newMotesAndTheirSensorAndFunctsVec.addElement(replacementMoteAndSensors);
                            }
                        }
                    }
                }
                //functionsForCapabilityOfThisMoteHM.clear();
            }
        } //end for loop for this node of queried motes
        if(newMotesAndTheirSensorAndFunctsVec==null || newMotesAndTheirSensorAndFunctsVec.isEmpty() ) {
            serviceDeployImpossible = true;
            logger.debug("Service Deploy is impossible for vsn id: " + pQueryDefId);
        }

        // decide status
        String statusDecidedStr = ResponseAggrMsg.DEPLOY_STATUS_SERVICE_UNKNOWN;
        if(serviceDeployImpossible)
        {
            statusDecidedStr= ResponseAggrMsg.DEPLOY_STATUS_SERVICE_IMPOSSIBLE;
        }else if(serviceDeployContinuationEmployed && serviceDeployPartiallyPossible) {
            statusDecidedStr= ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO;
        } else if(serviceDeployContinuationEmployed) {
            statusDecidedStr= ResponseAggrMsg.DEPLOY_STATUS_SERVICE_CONTINUATION;
        }else if(serviceDeployPartiallyPossible) {
            statusDecidedStr= ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL;
        } else if(serviceDeployAllNodesAvailable
                && !serviceDeployImpossible
                && !serviceDeployContinuationEmployed
                && !serviceDeployPartiallyPossible) {
            statusDecidedStr = ResponseAggrMsg.DEPLOY_STATUS_SERVICE_POSSIBLE;
        }
        serviceDeployStatusStr.set(0, statusDecidedStr);
        logger.debug("Decided DEPLOY STATUS WAS: "+ serviceDeployStatusStr.get(0));
        // We proceed here because even if service deploy is not possible, a reply will be sent with the status and empty lists (TODO consider)
        // However we also send (near the end of this method, alert messages for the deploy status if <> OK
        //
         //
        // TODO: To skip redundant queries in network
        // TODO: Count the reqFunction in reqFunction Vec (Debug print them) (also check that they are executed even if gateway level for each node-which should not happen)
        // TODO: Verify that if a function is gateway level and its removed(?) from the reqFunctionVec then it's not executed by the wsi adapter!
        //
        //
        // TODO: handle conditions for aggregate (gateway level functions).
        //
        //clone the reqFunctionsVec   . TODO. this is not cloning though, we pass references to the added elements
        Vector<ReqFunctionOverData> onlyNodeReqFunctVec = new Vector<ReqFunctionOverData>();
        Vector<ReqFunctionOverData> onlyGwLevelReqFunctVec = new Vector<ReqFunctionOverData>();
        for (int i =0 ; i<reqFunctionVec.size() ; i++)
        {
            if(ReqFunctionOverData.isValidGatewayReqFunct(reqFunctionVec.elementAt(i).getfuncName()))
                onlyGwLevelReqFunctVec.addElement(reqFunctionVec.elementAt(i));
            else
            {
                onlyNodeReqFunctVec.addElement(reqFunctionVec.elementAt(i));
            }
        }
        //
        // get the involved capabilities per gatewaylevel function, and then remove the function id from those sensorModels!
        //
        //  Produce a hashmap of gwLevel function name to Vector of capabilities (sensorModelId from the query/request)
        HashMap<String, Vector<String>>  gwLevelFunctToCapsList  = new HashMap<String, Vector<String>>();    // todo: IMPORTANT later we should group sensormodelIds per capability they belong to, but for now sensormodelid == capability!
        Iterator<ReqFunctionOverData> gwLevelFunctsIter = onlyGwLevelReqFunctVec.iterator();
        while(gwLevelFunctsIter.hasNext())
        {
            Vector<String> myInvolvedCaps = new Vector<String>();
            ReqFunctionOverData tmpGwLevelFunct =  gwLevelFunctsIter.next();
            // new change to new Vector of motes (19/04)
            Iterator<QueriedMoteAndSensors> onMotesSensFunctsVecIter = newMotesAndTheirSensorAndFunctsVec.iterator();
            while(onMotesSensFunctsVecIter.hasNext())
            {
                QueriedMoteAndSensors tmpMoteAndSenAndFuncts = onMotesSensFunctsVecIter.next();
                Iterator<ReqSensorAndFunctions> sensAndFunctsIter =  tmpMoteAndSenAndFuncts.getQueriedSensorIdsAndFuncVec().iterator();

                while(sensAndFunctsIter.hasNext())
                {
                    ReqSensorAndFunctions sensAndFuncts = sensAndFunctsIter.next();
                    //Vector<Integer> sensfunctsVector = sensAndFuncts.getFunctionsOverSensorModelVec();
                    int initSize = sensAndFuncts.getFid().size();

                    for(int k = initSize-1; k >=0 ; k--)
                    {
                        int sensfid = sensAndFuncts.getFid().get(k).intValue();
                        if(sensfid == tmpGwLevelFunct.getfuncId())
                        {
                            if(!myInvolvedCaps.contains(sensAndFuncts.getSensorModelid()))
                            {
                                myInvolvedCaps.addElement(sensAndFuncts.getSensorModelid());
                            }

                            // TODO: WHY??? (NOT NEEDED ANYMORE because we use the onlyNodeReqFunctVec to query the sensor and that filters out the functions in the adapter) ::here we should also delete the fid from the sensor model (but the simple way does not work for some reason, so it is left for future)

                            //List tmpList =  removeElementAt(sensAndFuncts.getFid(),k);
                            //sensAndFuncts.getFid().clear();
                            //sensAndFuncts.getFid().addAll(tmpList);
                            //sensAndFuncts.getFunctionsOverSensorModelVec().clear();

                        }
                    }
                }
            }
            gwLevelFunctToCapsList.put(tmpGwLevelFunct.getfuncName(),myInvolvedCaps);
        }
        //
        //
        //
        Vector<ReqResultOverData> allResultsRead = new Vector<ReqResultOverData>();
        //WsiAdapterCon myDCon = WsiAdapterConFactory.createMiddleWCon("uberdust", DbConInfoFactory.createConInfo("restHttp"));
        // DONE: The translateAggrQuery should not be executed for gateway level functions (skip them here or in the adapter con class.(?)
        // new changed to the new vector of motes : 19/04
        logger.debug("Submitting query to the network");
        // ASK ONLY FOR NODE LEVEL FUNCTIONS (TODO: Essentially for now, only last value is a node level function sent from the VSP, although other node level functions are supported)
        allResultsRead = myDCon.translateAggrQuery(newMotesAndTheirSensorAndFunctsVec, onlyNodeReqFunctVec);
        logger.debug("After Submitting query to the network");
        //
        //
        // TODO: All gateway level functions reference a node level function at some point (either directly eg max or two hops eg "IF MAX "
        //
        //
        // Handle gateway level functions
        // first order of business, delete everything within them (some connectors could put latest values of all nodes, but we want to do it the more proper way)
        // then get the values of the referenced function(s)
        // aggregate the values and produce a single result. TODO: here UOMs of different sensor models could come into play. Handle this in the future!
        //
        //
        // 1. we create a new derived structure with unique fid keyed entries for required Result over data.
        Vector<ReqResultOverData> allUniqueFunctionsWithResults = new Vector<ReqResultOverData>();
        Iterator<ReqResultOverData> messyResultsIter  = allResultsRead.iterator();
        // Loop over all resultOverData. They are keyed by fid, but there can be multiple of the same fid!
        // So here we merge those of same fid.
        while(messyResultsIter.hasNext())     //OUTER loop
        {
            ReqResultOverData tmpResStructFromMessyVec = messyResultsIter.next();

            //ReqResultOverData tmpResStructMatched = null;
            boolean foundTheFid = false;
            Iterator<ReqResultOverData> uniqueFuncResultsIter = allUniqueFunctionsWithResults.iterator();
            while(uniqueFuncResultsIter.hasNext())    //for the first pass of the OUTER loop the allUniqueFunctionsWithResults is empty
            {
                ReqResultOverData uniqueFunctResult = uniqueFuncResultsIter.next();
                if(uniqueFunctResult.getFidInt() == tmpResStructFromMessyVec.getFidInt() )
                {
                    foundTheFid = true;
                    uniqueFunctResult.getOut().addAll(tmpResStructFromMessyVec.getAllResultsforFunct());
                    break;
                }
            }
            if(!foundTheFid)
            {
                allUniqueFunctionsWithResults.addElement(new ReqResultOverData(tmpResStructFromMessyVec.getFidInt(), tmpResStructFromMessyVec.getAllResultsforFunct() ));
            }

        }
        //
        // Repeat this process slightly altered to add the unique Gw level functions
        //
        Iterator<ReqFunctionOverData> gwfunctIter =  onlyGwLevelReqFunctVec.iterator();
        while(gwfunctIter.hasNext())     //OUTER loop
        {
            ReqFunctionOverData tmpReqGwFunct = gwfunctIter.next();
            //ReqResultOverData tmpResStructMatched = null;
            boolean foundTheFid = false;
            Iterator<ReqResultOverData> uniqueFuncResultsIter = allUniqueFunctionsWithResults.iterator();
            while(uniqueFuncResultsIter.hasNext())    //for the first pass of the OUTER loop the allUniqueFunctionsWithResults is empty
            {
                ReqResultOverData uniqueFunctResult = uniqueFuncResultsIter.next();
                if(uniqueFunctResult.getFidInt() == tmpReqGwFunct.getfuncId() )
                {
                    foundTheFid = true;
                    break;
                }
            }
            if(!foundTheFid)
            {
                allUniqueFunctionsWithResults.addElement(new ReqResultOverData(tmpReqGwFunct.getfuncId(), new Vector<ResultAggrStruct>() ));
            }

        }
        // end of 1.
        //
        // 2. Go through all the gateway level functions (all of which are missing values right now).
        //    For each gateway level function, go through all the results for this function.
        //
        gwfunctIter =  onlyGwLevelReqFunctVec.iterator();
        while(gwfunctIter.hasNext())
        {
            ReqFunctionOverData tmpGwFunct = gwfunctIter.next();


            Iterator<ReqResultOverData> resultsIter  = allUniqueFunctionsWithResults.iterator();
            // loop over all resultOverData for this specific function (matching is made in the next two lines)
            while(resultsIter.hasNext())
            {
                ReqResultOverData tmpResForGWFunct = resultsIter.next();
                if(tmpResForGWFunct.getFidInt() == tmpGwFunct.getfuncId())
                {

                    // descriptionTokens[0] : GW LEVEL PREFIX
                    // descriptionTokens[1] : FUNCTION NAME
                    // descriptionTokens[2] : REFERENCED FUNCTION ID
                    String[] descriptionTokens = tmpGwFunct.getfuncName().split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                    //
                    // 3. Handle min, max and avg gateway level functions. (IF THEN FUNCTIONS ARE HANDLED AS ANOTHER CASE - THEY ARE ONE HOP HIGHER)
                    //    MIN, MAX, and AVG are all one hop (reference) away from a node level function (last value)
                    if( descriptionTokens !=null && descriptionTokens.length > 2  &&
                            (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.maxFunc) ||
                                    descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.minFunc) ||
                                    descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.avgFunc)
                            )
                        )
                    {
                        logger.debug("Clearing up values for gw funct name: " + tmpGwFunct.getfuncName());
                        // cleanup of output list  (it should however be already empty now that we rightfully only poll the WSI for node level functions)
                        tmpResForGWFunct.getOut().clear();
                        tmpResForGWFunct.getAllResultsforFunct().clear();

                        //after cleanup of output list
                        logger.debug("Filling up values for gw funct name: " + tmpGwFunct.getfuncName());
                        if ( descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.maxFunc) )
                        {
                            // MAX FUNCTION   =======================================
                            int aggregatedValues = 0;

                            int refFunct = ReqFunctionOverData.unknownFuncId;
                            try{
                                refFunct =   Integer.valueOf(descriptionTokens[2]);
                            } catch (Exception exfrtm) {
                                logger.error("Reference function id was set as unknown!");
                            }
                            HashMap<String, Long>  capToTsFromMinLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToTsToMaxLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToMaxValueLong = new HashMap<String, Long>();
                            //
                            Iterator<ReqResultOverData> resultsIter002 = allUniqueFunctionsWithResults.iterator();
                            // INNER LOOP THROUGH FUNCTIONS with results, searching for the referenced NODE level function
                            while(resultsIter002.hasNext())
                            {
                                ReqResultOverData tmpRes = resultsIter002.next();
                                if(tmpRes.getFidInt() == refFunct)
                                {
                                    // for every GENERIC capability requested( the generic capability is coded as hashcode() )
                                    for(String currCapSidStr: gwLevelFunctToCapsList.get(tmpGwFunct.getfuncName()))
                                    {
                                        if(!capToMaxValueLong.containsKey(currCapSidStr))
                                        {
                                            capToMaxValueLong.put(currCapSidStr,Long.valueOf( Long.MIN_VALUE));
                                            capToTsFromMinLong.put(currCapSidStr, Long.valueOf(Long.MAX_VALUE));
                                            capToTsToMaxLong.put(currCapSidStr,Long.valueOf( Long.MIN_VALUE));
                                        }

                                        Iterator<OutType> tmpOutItemIter = tmpRes.getOut().iterator();
                                        while(tmpOutItemIter.hasNext())
                                        {
                                            ResultAggrStruct tmpOutItem = new ResultAggrStruct(tmpOutItemIter.next());
                                            if(currCapSidStr.trim().equalsIgnoreCase(tmpOutItem.getSid().trim()) )
                                            {
                                                try
                                                {
                                                    long longValToCompare =Long.parseLong(tmpOutItem.getVal());
                                                    if(longValToCompare > capToMaxValueLong.get(currCapSidStr).longValue())
                                                    {
                                                        capToMaxValueLong.put(currCapSidStr,Long.valueOf( longValToCompare));
                                                    }
                                                    if(capToTsFromMinLong.get(currCapSidStr).longValue() > tmpOutItem.getTis().getFromTimestamp().getTime() )
                                                    {
                                                        capToTsFromMinLong.put(currCapSidStr, Long.valueOf(tmpOutItem.getTis().getFromTimestamp().getTime()));
                                                    }
                                                    if(capToTsToMaxLong.get(currCapSidStr).longValue() < tmpOutItem.getTis().getToTimestamp().getTime())
                                                    {
                                                        capToTsToMaxLong.put(currCapSidStr, Long.valueOf(tmpOutItem.getTis().getToTimestamp().getTime()));
                                                    }
                                                    aggregatedValues +=1;

                                                }catch (Exception e)
                                                {
                                                    logger.error("Invalid format to aggregate");
                                                }
                                            }
                                        }
                                        ResultAggrStruct thisAggrResult = new ResultAggrStruct(ResultAggrStruct.MidSpecialForAggregateMultipleValues, Integer.valueOf(currCapSidStr), Long.toString(capToMaxValueLong.get(currCapSidStr)), aggregatedValues, new TimeIntervalStructure(new Timestamp(capToTsFromMinLong.get(currCapSidStr)), new Timestamp(capToTsToMaxLong.get(currCapSidStr)))) ;
                                        tmpResForGWFunct.getOut().add(thisAggrResult);
                                    }
                                }
                            }
                        }
                        else if (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.minFunc) )
                        {
                            // MIN FUNCTION   =======================================
                            int aggregatedValues = 0;

                            int refFunct = ReqFunctionOverData.unknownFuncId;
                            try{
                                refFunct =   Integer.valueOf(descriptionTokens[2]);
                            } catch (Exception exfrtm) {
                                logger.error("Reference function id was set as unknown!");
                            }
                            HashMap<String, Long>  capToTsFromMinLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToTsToMaxLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToMinValueLong = new HashMap<String, Long>();
                            //
                            Iterator<ReqResultOverData> resultsIter002 = allUniqueFunctionsWithResults.iterator();
                            while(resultsIter002.hasNext())
                            {

                                ReqResultOverData tmpRes = resultsIter002.next();
                                if(tmpRes.getFidInt() == refFunct)
                                {
                                    // for every GENERIC capability requested( the genereic capability is coded as hashcode() )
                                    for(String currCapSidStr: gwLevelFunctToCapsList.get(tmpGwFunct.getfuncName()))
                                    {
                                        if(!capToMinValueLong.containsKey(currCapSidStr))
                                        {
                                            capToMinValueLong.put(currCapSidStr,Long.valueOf( Long.MAX_VALUE));
                                            capToTsFromMinLong.put(currCapSidStr, Long.valueOf(Long.MAX_VALUE));
                                            capToTsToMaxLong.put(currCapSidStr,Long.valueOf( Long.MIN_VALUE));
                                        }

                                        Iterator<OutType> tmpOutItemIter = tmpRes.getOut().iterator();
                                        while(tmpOutItemIter.hasNext())
                                        {
                                            ResultAggrStruct tmpOutItem = new ResultAggrStruct(tmpOutItemIter.next());
                                            if(currCapSidStr.trim().equalsIgnoreCase(tmpOutItem.getSid().trim()) )
                                            {
                                                try
                                                {
                                                    long longValToCompare =Long.parseLong(tmpOutItem.getVal());
                                                    if(longValToCompare < capToMinValueLong.get(currCapSidStr).longValue())
                                                    {
                                                        capToMinValueLong.put(currCapSidStr,Long.valueOf( longValToCompare));
                                                    }
                                                    if(capToTsFromMinLong.get(currCapSidStr).longValue() > tmpOutItem.getTis().getFromTimestamp().getTime() )
                                                    {
                                                        capToTsFromMinLong.put(currCapSidStr, Long.valueOf(tmpOutItem.getTis().getFromTimestamp().getTime()));
                                                    }
                                                    if(capToTsToMaxLong.get(currCapSidStr).longValue() < tmpOutItem.getTis().getToTimestamp().getTime())
                                                    {
                                                        capToTsToMaxLong.put(currCapSidStr, Long.valueOf(tmpOutItem.getTis().getToTimestamp().getTime()));
                                                    }
                                                    aggregatedValues +=1;

                                                }catch (Exception e)
                                                {
                                                    logger.error("Invalid format to aggregate");
                                                }
                                            }
                                        }
                                        ResultAggrStruct thisAggrResult = new ResultAggrStruct(ResultAggrStruct.MidSpecialForAggregateMultipleValues, Integer.valueOf(currCapSidStr), Long.toString(capToMinValueLong.get(currCapSidStr)), aggregatedValues, new TimeIntervalStructure(new Timestamp(capToTsFromMinLong.get(currCapSidStr)), new Timestamp(capToTsToMaxLong.get(currCapSidStr)))) ;
                                        logger.debug("Adding a result");
                                        tmpResForGWFunct.getOut().add(thisAggrResult);
                                        logger.debug("Added a result");

                                    }
                                }
                            }
                        }
                        else if (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.avgFunc) )
                        {
                            // AVG FUNCTION   =======================================
                            int aggregatedValues = 0;
                            int refFunct = ReqFunctionOverData.unknownFuncId;
                            try{
                                refFunct =   Integer.valueOf(descriptionTokens[2]);
                            } catch (Exception exfrtm) {
                                logger.error("Reference function id was set as unknown!");
                            }
                            HashMap<String, Long>  capToTsFromMinLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToTsToMaxLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToAvgValueLong = new HashMap<String, Long>();
                            //
                            Iterator<ReqResultOverData> resultsIter002 = allUniqueFunctionsWithResults.iterator();

                            while(resultsIter002.hasNext())
                            {

                                ReqResultOverData tmpRes = resultsIter002.next();
                                /*System.out.println("LLLLLLLL TEST 3");
                                StringBuilder tmpRsOD = new StringBuilder();
                                tmpRsOD.append("resf fid:");
                                tmpRsOD.append(tmpRes.getFidInt());
                                tmpRsOD.append(" AND ref funct:");
                                tmpRsOD.append(refFunct);
                                System.out.println("OOOOOOOOOOOOOO TEST 3B" + tmpRsOD.toString());*/
                                if(tmpRes.getFidInt() == refFunct)
                                {
                                    // for every GENERIC capability requested( the genereic capability is coded as hashcode() )
                                    for(String currCapSidStr: gwLevelFunctToCapsList.get(tmpGwFunct.getfuncName()))
                                    {
                                        if(!capToAvgValueLong.containsKey(currCapSidStr))
                                        {
                                            capToAvgValueLong.put(currCapSidStr,Long.valueOf( 0));
                                            capToTsFromMinLong.put(currCapSidStr, Long.valueOf(Long.MAX_VALUE));
                                            capToTsToMaxLong.put(currCapSidStr,Long.valueOf( Long.MIN_VALUE));
                                        }

                                        Iterator<OutType> tmpOutItemIter = tmpRes.getOut().iterator();
                                        while(tmpOutItemIter.hasNext())
                                        {
                                            ResultAggrStruct tmpOutItem = new ResultAggrStruct(tmpOutItemIter.next());
                                            if(currCapSidStr.trim().equalsIgnoreCase(tmpOutItem.getSid().trim()) )
                                            {
                                                try
                                                {
                                                    long longValOfSensor = Long.parseLong(tmpOutItem.getVal());
                                                    long valPrevious = capToAvgValueLong.get(currCapSidStr).longValue();
                                                    long newVal = valPrevious + longValOfSensor;
                                                    capToAvgValueLong.put(currCapSidStr,Long.valueOf( newVal));

                                                    //
                                                    if(capToTsFromMinLong.get(currCapSidStr).longValue() > tmpOutItem.getTis().getFromTimestamp().getTime() )
                                                    {
                                                        capToTsFromMinLong.put(currCapSidStr, Long.valueOf(tmpOutItem.getTis().getFromTimestamp().getTime()));
                                                    }
                                                    if(capToTsToMaxLong.get(currCapSidStr).longValue() < tmpOutItem.getTis().getToTimestamp().getTime())
                                                    {
                                                        capToTsToMaxLong.put(currCapSidStr, Long.valueOf(tmpOutItem.getTis().getToTimestamp().getTime()));
                                                    }
                                                    aggregatedValues +=1;

                                                }catch (Exception e)
                                                {
                                                    logger.error("Invalid format to aggregate");
                                                }

                                            }
                                        }
                                        Double avgVal = Double.valueOf(capToAvgValueLong.get(currCapSidStr).longValue()) / Double.valueOf(aggregatedValues);
                                        /*StringBuilder tmpRs = new StringBuilder();
                                        tmpRs.append("Result:");
                                        tmpRs.append(avgVal);
                                        tmpRs.append(" aggr vals:");
                                        tmpRs.append(aggregatedValues);
                                        System.out.println("OOOOOOOOOOOOOO TEST 3C" + tmpRs.toString());*/
                                        ResultAggrStruct thisAggrResult = new ResultAggrStruct(ResultAggrStruct.MidSpecialForAggregateMultipleValues, Integer.valueOf(currCapSidStr), Double.toString(avgVal), aggregatedValues, new TimeIntervalStructure(new Timestamp(capToTsFromMinLong.get(currCapSidStr)), new Timestamp(capToTsToMaxLong.get(currCapSidStr)))) ;

                                        tmpResForGWFunct.getOut().add(thisAggrResult);
                                        //System.out.println("OOOOOOOOOOOOOO TEST 3D" + tmpRs.toString());
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }// end of while loop on ONE HOP REFERENCE GW FUNCTIONs (MIN, MAX, AVG

        // Start of while loop on 2nd HOP reference GW function (need the one hops already filled in)
        // TODO: we don't handle/anticipate the case where the IF_THEN function references another IF_THEN function (even repeatedly). More flexibility could be implemented!!
        gwfunctIter =  onlyGwLevelReqFunctVec.iterator(); // gets a NEW iterator
        while(gwfunctIter.hasNext())
        {
            ReqFunctionOverData tmpGwFunct = gwfunctIter.next();

            Iterator<ReqResultOverData> resultsIter  = allUniqueFunctionsWithResults.iterator();
            // loop over all resultOverData for this specific function (matching is made in the next two lines)
            while(resultsIter.hasNext())
            {
                ReqResultOverData tmpResForGWFunct = resultsIter.next();

                if(tmpResForGWFunct.getFidInt() == tmpGwFunct.getfuncId())
                {

                    // descriptionTokens[0] : GW LEVEL PREFIX
                    // descriptionTokens[1] : FUNCTION NAME
                    // descriptionTokens[2] : REFERENCED FUNCTION ID
                    String[] descriptionTokens = tmpGwFunct.getfuncName().split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);

                    if( descriptionTokens !=null && descriptionTokens.length > 2  &&
                            (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.ruleRuleBinaryAndFunc) ||
                                    descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.ruleRuleIfThenFunc))
                            )
                    {
                        logger.debug("Clearing up values for gw funct name: " + tmpGwFunct.getfuncName());
                        // cleanup of output list  (it should however be already empty now that we rightfully only poll the WSI for node level functions)
                        tmpResForGWFunct.getOut().clear();
                        tmpResForGWFunct.getAllResultsforFunct().clear();
                        //after cleanup of output list
                        logger.debug("Filling values for funct name: " + tmpGwFunct.getfuncName());
                        if(descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.ruleRuleBinaryAndFunc))
                        {


                            //TODO: handle a binary rule (condition1 and condition2)
                        }
                        else if(descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.ruleRuleIfThenFunc) )
                        {

                            logger.debug("Filling values for funct name: " + tmpGwFunct.getfuncName());
                            //handle a binary rule (condition1 then do 3)
                            // 1: check if the referenced function has results that meet the conditions in its threshold
                            int consideredValues = 0;
                            int refFunct = ReqFunctionOverData.unknownFuncId;
                            try{
                                refFunct =   Integer.valueOf(descriptionTokens[2]);
                            } catch (Exception exfrtm) {
                                logger.error("Reference function id was set as unknown!");
                            }
                            HashMap<String, Long>  capToTsFromMinLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToTsToMaxLong = new HashMap<String, Long>();
                            HashMap<String, Long>  capToConditionValueLong = new HashMap<String, Long>();
                            //
                            Iterator<ReqResultOverData> resultsIter002 = allUniqueFunctionsWithResults.iterator();
                            while(resultsIter002.hasNext())
                            {
                                ReqResultOverData tmpRes = resultsIter002.next();
                                if(tmpRes.getFidInt() == refFunct)
                                {
                                    // for every GENERIC capability requested( the genereic capability is coded as hashcode() )
                                    for(String currCapSidStr: gwLevelFunctToCapsList.get(tmpGwFunct.getfuncName()))
                                    {
                                        if(!capToConditionValueLong.containsKey(currCapSidStr))
                                        {
                                            capToTsFromMinLong.put(currCapSidStr, Long.valueOf(Long.MAX_VALUE));
                                            capToTsToMaxLong.put(currCapSidStr,Long.valueOf( Long.MIN_VALUE));
                                            capToConditionValueLong.put(currCapSidStr,Long.valueOf( 0));
                                        }

                                        Iterator<OutType> tmpOutItemIter = tmpRes.getOut().iterator();
                                        while(tmpOutItemIter.hasNext())
                                        {
                                            ResultAggrStruct tmpOutItem = new ResultAggrStruct(tmpOutItemIter.next());

                                            if(currCapSidStr.trim().equalsIgnoreCase(tmpOutItem.getSid().trim()) )
                                            {
                                                try
                                                {
                                                    // TODO: Actually here we need to find in the original ReqFunctVec (that contains the full function definitions, not just the function id)
                                                    //      the thresholds set. Before we search for the thresholds in the referenced function but now (better) we get them from this function (If_then)

                                                    boolean foundTheCurrentFunctionInTheOriginalReqFunctionVec = false;
                                                    long longValOfSensor = Long.parseLong(tmpOutItem.getVal());
                                                    ReqFunctionOverData currentFunctionInCondition =null;
                                                    for (int kx1=0; kx1<reqFunctionVec.size(); kx1++)
                                                    {
                                                        if(reqFunctionVec.elementAt(kx1).getfuncId() == tmpResForGWFunct.getFidInt())
                                                        {
                                                            currentFunctionInCondition= reqFunctionVec.elementAt(kx1);
                                                            foundTheCurrentFunctionInTheOriginalReqFunctionVec = true;
                                                            break;
                                                        }
                                                    }
                                                    // but also find the reference function in the condition to include details in the notification
                                                    boolean foundTheReferencedFunctionInTheOriginalReqFunctionVec = false;
                                                    ReqFunctionOverData referencedFunctionInCondition =null;
                                                    for (int kx1=0; kx1<reqFunctionVec.size(); kx1++)
                                                    {
                                                        if(reqFunctionVec.elementAt(kx1).getfuncId() == tmpResForGWFunct.getFidInt())
                                                        {
                                                            referencedFunctionInCondition= reqFunctionVec.elementAt(kx1);
                                                            foundTheReferencedFunctionInTheOriginalReqFunctionVec = true;
                                                            break;
                                                        }
                                                    }
                                                    if(foundTheCurrentFunctionInTheOriginalReqFunctionVec)  // the referred function here must have a threshold field because it's an evaluation of a condition
                                                    {
                                                        if (currentFunctionInCondition!=null
                                                                && currentFunctionInCondition.getThresholdField() != null
                                                                && !currentFunctionInCondition.getThresholdField().isEmpty()) {
                                                            logger.debug("-------- INTO EVALUATING CONDITION NOW! ");
                                                            ThresholdStructure requiredThresholds = new ThresholdStructure(currentFunctionInCondition.getThresholdField());
                                                            if (requiredThresholds.getLowerBound() != null && !requiredThresholds.getLowerBound().isEmpty()) {
                                                                logger.debug("Condition low parameter: " + requiredThresholds.getLowerBound().trim());
                                                                //    TODO: handle other conditions for services (lower than, equals, between)
                                                                long lowbound = Long.parseLong(requiredThresholds.getLowerBound());
                                                                if(longValOfSensor >= lowbound)
                                                                {
                                                                    logger.debug("Sensor: "+ tmpOutItem.getMid() +". Condition is met: " + Long.toString(longValOfSensor) + " >= " + requiredThresholds.getLowerBound().trim());
                                                                    consideredValues =1;
                                                                    ResultAggrStruct thisAggrResult = new ResultAggrStruct(tmpOutItem.getMid(), Integer.valueOf(currCapSidStr), Long.toString(longValOfSensor), consideredValues, new TimeIntervalStructure(new Timestamp(Long.valueOf(tmpOutItem.getTis().getFromTimestamp().getTime())), new Timestamp(Long.valueOf(tmpOutItem.getTis().getToTimestamp().getTime())))) ;
                                                                    tmpResForGWFunct.getOut().add(thisAggrResult);
                                                                    // DONE: Send an alert notification
                                                                    NotificationsFromVSNs newNotify = new NotificationsFromVSNs();
                                                                    newNotify.setQueryDefId(pQueryDefId);
                                                                    newNotify.setVgwID(myPeerId);
                                                                    // get continuation info. Careful, we have not yet replaced the replacemntIDs with the original nodes in the measurements here (it's done later)
                                                                    // but we have to set the MoteId to the Original Id and the replacementId to the replacement node
                                                                    String[] replaceItem = getLocalReplacemntInfoListItem(localReplacedResources, tmpOutItem.getMid(), tmpOutItem.getSid());
                                                                    if(replaceItem!=null && replaceItem[0]!=null && !replaceItem[0].isEmpty()
                                                                            && replaceItem[0].compareToIgnoreCase(replaceItem[1]) !=0) {
                                                                        newNotify.setMoteID(replaceItem[0]);
                                                                        newNotify.setReplacmntID(tmpOutItem.getMid());
                                                                    }
                                                                    else{
                                                                        newNotify.setMoteID(tmpOutItem.getMid());
                                                                        newNotify.setReplacmntID("");
                                                                    }
                                                                    newNotify.setValue(longValOfSensor);
                                                                    if(tmpOutItem.getTis()!=null && tmpOutItem.getTis().isTimestampFromDefined())
                                                                    newNotify.setValueTimestamp(Long.toString(tmpOutItem.getTis().getFromTimestamp().getTime()));
                                                                    newNotify.setBoundValue(lowbound);
                                                                    newNotify.setRefFunctName(referencedFunctionInCondition.getfuncName());
                                                                    newNotify.setRefFunctTriggerSign("gt"); //default for lower bound conditions
                                                                    newNotify.setCapabilityCode(tmpOutItem.getSid().trim());
                                                                    newNotify.setTimestamp(Long.toString(System.currentTimeMillis()) );
                                                                    newNotify.setType(NotificationsFromVSNs.CRITICAL_TYPE);
                                                                    newNotify.setLevel(NotificationsFromVSNs.GATEWAY_LEVEL);
                                                                    newNotify.setRefFunctId(referencedFunctionInCondition.getfuncId());
                                                                    newNotify.setMessage("Condition was met for node id: "+ newNotify.getMoteID()+  " value: " +longValOfSensor+ " capability code:__"+tmpOutItem.getSid().trim());
                                                                    // Send the response to the requesting end user
                                                                    //System.out.println("Sending Notification!");
                                                                    String notifMsgToSend = NotificationsFromVSNs.getAlertDelimitedString(newNotify);
                                                                    this.sendResponse(notifMsgToSend);

                                                                }
                                                                else
                                                                {
                                                                    logger.debug("Sensor: "+ tmpOutItem.getMid() +" with value: "+ Long.toString(longValOfSensor) +" does not meet Condition!");
                                                                }
                                                            }
                                                        }

                                                    }

                                                }catch (Exception e)
                                                {
                                                    logger.error("Invalid format to aggregate");
                                                }

                                            }
                                        }
                                       //
                                        //
                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
        // Add trailing section for service deployability and replacements list
        // Careful! for the replacements list, skip the entries where the node replaces itself
        // DONE: RECONSTRUCT the Vector<ReqResultOverData> allUniqueFunctionsWithResults for the original nodes!
        //
        //
        logger.debug("BEFORE RECONSTRUCTION");
        if(allUniqueFunctionsWithResults!=null) {
            logger.debug("IN RECONSTRUCTION");
            for(ReqResultOverData aResultOverData : allUniqueFunctionsWithResults) {
                String functionId = aResultOverData.getFid();
                // replacing is needed only for node level functions and possibly for if then functions referring to last values of sensors (not for aggregate GW level or if_then over aggregates)
                 // &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&+++++++++++++++++++++++++++++++++++==
                /*
                boolean isGwLevel = false;
                Iterator<ReqFunctionOverData> gwfunctIterLocal =  onlyGwLevelReqFunctVec.iterator();
                while(gwfunctIterLocal.hasNext())     //OUTER loop
                {
                    ReqFunctionOverData tmpReqGwFunct = gwfunctIterLocal.next();
                    if(Integer.toString(tmpReqGwFunct.getfuncId()).equalsIgnoreCase(functionId)){
                        isGwLevel = true;
                        break;
                    }

                }
                if(!isGwLevel) {
                */
                    logger.debug("FID:: " + functionId);
                    if(aResultOverData.getAllResultsforFunct()!=null)
                    {
                        if(aResultOverData.getAllResultsforFunct().isEmpty()){
                            logger.debug("has no results!!");
                        } else {
                            logger.debug("found results!!");
                        }

                        Vector<ResultAggrStruct> newReconstructedResultVec = null;
                        boolean foundAtLeastOneResultForSpecificMoteId = false;
                        for(ResultAggrStruct thisResult : aResultOverData.getAllResultsforFunct()) {
                            if(thisResult.getMid().compareToIgnoreCase(ResultAggrStruct.MidSpecialForAggregateMultipleValues) != 0) {
                                if(!foundAtLeastOneResultForSpecificMoteId){
                                    foundAtLeastOneResultForSpecificMoteId = true;
                                    newReconstructedResultVec = new Vector<ResultAggrStruct>();
                                }
                                String[] replaceItem = getLocalReplacemntInfoListItem(localReplacedResources, thisResult.getMid(), thisResult.getSid());
                                if(replaceItem!=null && replaceItem[0]!=null && !replaceItem[0].isEmpty()) {
                                    logger.debug("Back to replacing node :" + thisResult.getMid() + " with original node: " + replaceItem[0]);
                                    thisResult.setMid(replaceItem[0]);
                                    newReconstructedResultVec.addElement(thisResult);
                                }
                            }
                        }
                        if(foundAtLeastOneResultForSpecificMoteId) {
                            aResultOverData.setAllResultsforFunct(newReconstructedResultVec);
                        }
                    }
               /* } */
            }
        }

        //

        // DEBUG:
        logger.debug("The gateway has collected results and is ready to send them!");
        //return allResultsRead;    // Support for various data types is added by the DataTypeAdapter class
        //    ********************** COAP MESSAGES BACK TO GATEWAY *******************************
        //   ALSO SEND ANY SECURITY MESSAGES
        //   TODO: we could clean the cache after sending these messages (?)
        if(!VitroGatewayService.getVitroGatewayService().isWsiTrustCoapMessagingSupport() ){
            logger.debug("No SUPPORT FOR SENDING TRUST SECURITY INFO back to VSP!");
        }
        if(!VitroGatewayService.getVitroGatewayService().isTrustRoutingCoapMessagingActive()) {
            logger.debug("No ACTIVATION FOR SENDING TRUST SECURITY INFO back to VSP!");
        }


        if(VitroGatewayService.getVitroGatewayService().isWsiTrustCoapMessagingSupport() &&
                VitroGatewayService.getVitroGatewayService().isTrustRoutingCoapMessagingActive()){
            logger.debug("Attempting to send TRUST SECURITY INFO back to VSP!");
            HashMap<String, InfoOnTrustRouting> cacheTrustCoapCopy = new HashMap<String, InfoOnTrustRouting>(TrustRoutingQueryService.getInstance().getCachedDirectoryOfTrustRoutingInfo());
            String aRefCapCode = "";
            int aRefFunctId = 1;// last value is always in the request
            if(originalMotesAndTheirSensorAndFunctsVec !=null){
                try {
                    aRefCapCode = originalMotesAndTheirSensorAndFunctsVec.firstElement().getQueriedSensorIdsAndFuncVec().get(0).getSensorModelid();
                }catch(Exception e339){
                    logger.error("Could not acquire sample capability id for security TRUST alert ");
                }
                try {
                    aRefFunctId = originalMotesAndTheirSensorAndFunctsVec.firstElement().getQueriedSensorIdsAndFuncVec().get(0).getFunctionsOverSensorModelVec().firstElement();
                }catch(Exception e339){
                    logger.error("Could not acquire sample function id for security TRUST alert ");
                }
            }
            if(cacheTrustCoapCopy!=null) {

                for(String sourceNodeId: cacheTrustCoapCopy.keySet() ) {
                    InfoOnTrustRouting tmpInfoOnTrust = cacheTrustCoapCopy.get(sourceNodeId);
                    HashMap<String, Integer> tmpParentIdToPFiHM = tmpInfoOnTrust.getParentIdsToPFI();
                    for(String parentNodeId: tmpParentIdToPFiHM.keySet())
                    {
                    // TODO: Send a SECURITY notification
                        NotificationsFromVSNs newNotify = new NotificationsFromVSNs();
                        newNotify.setQueryDefId(pQueryDefId);
                        newNotify.setVgwID(myPeerId);
                        newNotify.setMoteID(sourceNodeId);
                        newNotify.setValue(tmpParentIdToPFiHM.get(parentNodeId));
                        // TODO: Demo: change to current timestamp which is more reliable
                        newNotify.setValueTimestamp(Long.toString(System.currentTimeMillis())); // the time stamp for the PFI value
                        newNotify.setTimestamp(Long.toString(System.currentTimeMillis()));  //the time stamp of the notification

                        //newNotify.setTimestamp(tmpInfoOnTrust.getTimestamp() );
                        //newNotify.setValueTimestamp(tmpInfoOnTrust.getTimestamp());
                        newNotify.setType(NotificationsFromVSNs.SECURITY_TYPE);
                        newNotify.setLevel(NotificationsFromVSNs.GATEWAY_LEVEL);
                        // we need sample valid funct ids and capability codes related to this VSN , to associate it at the VSP level with a partial service!
                        newNotify.setRefFunctId(aRefFunctId);
                        newNotify.setCapabilityCode(aRefCapCode);
                        // the message field is here used to store the parent ID.
                        newNotify.setMessage(parentNodeId);
                        // Send the response to the requesting end user
                        //System.out.println("Sending Notification!");
                        String notifMsgToSend = NotificationsFromVSNs.getAlertDelimitedString(newNotify);
                        try{
                            this.sendResponse(notifMsgToSend);
                            logger.debug("Sent one TRUST SECURITY INFO back to VSP!");
                        }catch(Exception securSendExc){
                            logger.error("Could not send Security Type notification" , securSendExc);
                        }
                    }
                }
            }
            //
            /*
            logger.debug("Sending a dummy message security for TRUST-DEBUG");
            {   //---------------------------------------------------------------------
                // TODO: Send a SECURITY notification
                NotificationsFromVSNs newNotify = new NotificationsFromVSNs();
                newNotify.setQueryDefId(pQueryDefId);
                newNotify.setVgwID(myPeerId);
                newNotify.setMoteID("urn:wisebed:ctitestbed:0xca2");
                newNotify.setValue(400);
                newNotify.setValueTimestamp(Long.toString(new Date().getTime()));
                newNotify.setTimestamp(Long.toString(new Date().getTime()));
                newNotify.setType(NotificationsFromVSNs.SECURITY_TYPE);
                newNotify.setLevel(NotificationsFromVSNs.GATEWAY_LEVEL);

                newNotify.setRefFunctId(aRefFunctId);
                newNotify.setCapabilityCode(aRefCapCode);
                // the message field is here used to store the parent ID.
                newNotify.setMessage("urn:wisebed:ctitestbed:0xCC");
                // Send the response to the requesting end user
                //System.out.println("Sending Notification!");
                String notifMsgToSend = NotificationsFromVSNs.getAlertDelimitedString(newNotify);
                try{
                    this.sendResponse(notifMsgToSend);
                    logger.debug("Sent one TRUST SECURITY INFO back to VSP!");
                }catch(Exception securSendExc){
                    logger.error("Could not send Security Type notification" , securSendExc);
                }

                //---------------------------------------------------------------------
            }
            */

        } //end of if we have to send the security Coap Routing Trust Messages

       // %%%%%%%%%% DIRECTLY INFORM THE GATEWAY OF PROBLEMATIC DEPLOY STATUS:
        if(serviceDeployImpossible || serviceDeployContinuationEmployed
                || serviceDeployPartiallyPossible  ) {
            String aRefMote = "";
            String aRefCapCode = "";
            int aRefFunctId = 1;// last value is always in the request
            if(originalMotesAndTheirSensorAndFunctsVec !=null){
                try {
                    aRefMote = originalMotesAndTheirSensorAndFunctsVec.firstElement().getMoteid();
                }catch(Exception e339){
                    logger.error("Could not acquire sample ref node it for DEPLOY ABILITY STATUS alert ");
                }

                try {
                    aRefCapCode = originalMotesAndTheirSensorAndFunctsVec.firstElement().getQueriedSensorIdsAndFuncVec().get(0).getSensorModelid();
                }catch(Exception e339){
                    logger.error("Could not acquire sample capability for DEPLOY ABILITY STATUS alert ");
                }
                try {
                    aRefFunctId = originalMotesAndTheirSensorAndFunctsVec.firstElement().getQueriedSensorIdsAndFuncVec().get(0).getFunctionsOverSensorModelVec().firstElement();
                }catch(Exception e339){
                    logger.error("Could not acquire sample function id for DEPLOY ABILITY STATUS alert ");
                }
            }
             String strMessage = "";
            long deployValue = ResponseAggrMsg.DEPLOY_STATUS_SERVICE_POSSIBLE_INT;
            if(serviceDeployImpossible)
            {
                strMessage = "The requested VSN cannot be supported by this island: " + myPeerId ;
                // case ResponseAggrMsg.DEPLOY_STATUS_SERVICE_IMPOSSIBLE;
                deployValue =  ResponseAggrMsg.DEPLOY_STATUS_SERVICE_IMPOSSIBLE_INT;
            }else if(serviceDeployContinuationEmployed && serviceDeployPartiallyPossible) {
                // case ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO;
                strMessage = "The requested VSN is partially supported using service continuation on this island: " + myPeerId ;

                deployValue =  ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO_INT;
            } else if(serviceDeployContinuationEmployed) {
                // case ResponseAggrMsg.DEPLOY_STATUS_SERVICE_CONTINUATION;
                strMessage = "The requested VSN is supported using service continuation on this island: " + myPeerId ;
                deployValue =  ResponseAggrMsg.DEPLOY_STATUS_SERVICE_CONTINUATION_INT;
            }else if(serviceDeployPartiallyPossible) {
                // case ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL;
                strMessage = "The requested VSN is partially supported on this island: " + myPeerId ;
                deployValue =  ResponseAggrMsg.DEPLOY_STATUS_SERVICE_PARTIAL_INT;
            }
            // SEND THE NOTIFICATION::
            // TODO: Send a DEPLOY_STATUS_TYPE notification
            NotificationsFromVSNs newNotify = new NotificationsFromVSNs();
            newNotify.setQueryDefId(pQueryDefId);
            newNotify.setVgwID(myPeerId);
            newNotify.setMoteID(aRefMote);
            newNotify.setValue(deployValue);
            // TODO: Demo: change to current timestamp which is more reliable
            newNotify.setValueTimestamp(Long.toString(System.currentTimeMillis())); // the time stamp for the PFI value
            newNotify.setTimestamp(Long.toString(System.currentTimeMillis()));  //the time stamp of the notification

            //newNotify.setTimestamp(tmpInfoOnTrust.getTimestamp() );
            //newNotify.setValueTimestamp(tmpInfoOnTrust.getTimestamp());
            newNotify.setType(NotificationsFromVSNs.DEPLOY_STATUS_TYPE);
            newNotify.setLevel(NotificationsFromVSNs.GATEWAY_LEVEL);
            // we need sample valid funct ids and capability codes related to this VSN , to associate it at the VSP level with a partial service!
            newNotify.setRefFunctId(aRefFunctId);
            newNotify.setCapabilityCode(aRefCapCode);
            // the message field is here used to store the parent ID.
            newNotify.setMessage(strMessage);
            // Send the response to the requesting end user
            //System.out.println("Sending Notification!");
            String notifMsgToSend = NotificationsFromVSNs.getAlertDelimitedString(newNotify);
            try{
                this.sendResponse(notifMsgToSend);
                logger.debug("Sent one DEPLOY STATUS info back to VSP!");
            }catch(Exception securSendExc){
                logger.error("Could not send DEPLOY STATUS notification" , securSendExc);
            }
        }
        return allUniqueFunctionsWithResults;
    }

    /**
    * @param s
     */
    public void sendResponse(String s) throws JMSException
    {
        try
        {
            TextMessage message = myPipeDataSession.createTextMessage(s);
            // Here we are sending the message!
            myPipeDataProducer.send(message);
            // DEBUG
            // System.out.println("GateWay sent a response: '" + message.getText() + "'");

        }
        catch (Exception e)
        {
            logger.error("An error occurred while trying to send a reply", e);
            e.printStackTrace();
        }
    }
}
