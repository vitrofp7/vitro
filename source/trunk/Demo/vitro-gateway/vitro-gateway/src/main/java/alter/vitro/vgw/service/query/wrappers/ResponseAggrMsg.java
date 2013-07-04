/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
package alter.vitro.vgw.service.query.wrappers;

import alter.vitro.vgw.service.query.xmlmessages.response.*;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class ResponseAggrMsg /*extends QueryResponseType */ {
    private static Logger logger = LoggerFactory.getLogger(ResponseAggrMsg.class);
    private final static String thisMsgType = "aggregatedResponse";

    public final static String DEPLOY_STATUS_SERVICE_UNKNOWN = "UNKNOWN"; //service deployment support status has not been evaluated yet
    public final static String DEPLOY_STATUS_SERVICE_IMPOSSIBLE = "IMPOSSIBLE"; // no resources to support it (any more or since the start)
    public final static String DEPLOY_STATUS_SERVICE_PARTIAL = "PARTIAL"; // some resources support it, but not all requested
    public final static String DEPLOY_STATUS_SERVICE_CONTINUATION = "CONTINUATION"; // in essence all resources support it, except that some have been replaced by equivalent
    public final static String DEPLOY_STATUS_SERVICE_POSSIBLE = "OK"; // all requested resources support it.
    public final static String DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO = "PARTIALCONTINUATION"; // some of the requested resources support it, and service continuation has also been used.

    public final static int DEPLOY_STATUS_SERVICE_UNKNOWN_INT = 0; //service deployment support status has not been evaluated yet
    public final static int DEPLOY_STATUS_SERVICE_IMPOSSIBLE_INT = 5; // no resources to support it (any more or since the start)
    public final static int DEPLOY_STATUS_SERVICE_PARTIAL_INT = 4; // some resources support it, but not all requested
    public final static int DEPLOY_STATUS_SERVICE_CONTINUATION_INT= 3; // in essence all resources support it, except that some have been replaced by equivalent
    public final static int DEPLOY_STATUS_SERVICE_POSSIBLE_INT = 1; // all requested resources support it.
    public final static int DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO_INT = 2; // some of the requested resources support it, and service continuation has also been used.


    static final String VALID_DEPLOY_STATUSES[] = {
            DEPLOY_STATUS_SERVICE_UNKNOWN,
            DEPLOY_STATUS_SERVICE_IMPOSSIBLE,
            DEPLOY_STATUS_SERVICE_PARTIAL,
            DEPLOY_STATUS_SERVICE_CONTINUATION,
            DEPLOY_STATUS_SERVICE_POSSIBLE,
            DEPLOY_STATUS_SERVICE_PARTIAL_CONT_COMBO
    };

    private QueryResponseType response;

    /**
     * Returns a string that declares the type of this message. This is put for extensibility purposes.
     *
     * @return String with the query message type.
     */
    public static String getThisMsgType() {
        return thisMsgType;
    }

    /*
    * Constructor
    *
     */
    public ResponseAggrMsg(QueryResponseType pResponse)
    {
        setQuery(pResponse);

    }

    /**
     * Constructor method.
     * Creates a new instance of ResponseAggrMsg with timed-out entries for all sensors defined and all functions
     *
     * @param queryDefID                    The unique ID for the query definition that this response belongs to. This is different from the queryId of the JXTA message that this
     *                                      message replies to.
     * @param responderName                 The Name of the peer that sends this response.
     * @param responderPeerID               The jxta unique Peer ID of the peer that sends this response.
     * @param motesSensorsAndFunctionsForQueryVec The map that defines the querried sensors, indexed by the motes that have them
     * @param functionVec                   the vector of selected functions to be applied
     * @param qCount                        the query count of the JXTA message that this response replies to.
     */
    public ResponseAggrMsg(String queryDefID, String responderPeerID, String responderName, Vector<QueriedMoteAndSensors> motesSensorsAndFunctionsForQueryVec, Vector<ReqFunctionOverData> functionVec, int qCount) {
         try{
             javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.response");
             // create an object to marshal
             ObjectFactory theFactory = new ObjectFactory();
             response = theFactory.createQueryResponseType();
         }
         catch (javax.xml.bind.JAXBException je) {
             je.printStackTrace();
             response = new QueryResponseType();
         }
         setDeployStatus(DEPLOY_STATUS_SERVICE_UNKNOWN);

        response.setQueryDefID(queryDefID);
        response.setResponderPeerID(responderPeerID);
        response.setResponderName(responderName);
        response.setQueryCount(Integer.toString(qCount));
        response.setMessageType(ResponseAggrMsg.getThisMsgType());
        RespFunctionsListType rflType = new RespFunctionsListType();
        response.setReqFunctionsList(rflType);
        //
        //RespFunctionsListType rflType = response.getReqFunctionsList();
        List<RespFunctionType> rfList = rflType.getReqFunction();
        // For every function in the Vector create a ReqResultOverData with all data values "timed out" for the sensors defined here!
        for (int i = 0; i < functionVec.size(); i++) {
            rfList.add(new ReqResultOverData(functionVec.elementAt(i).getfuncId(), motesSensorsAndFunctionsForQueryVec, ReqResultOverData.modeFillWithTimeouts));
        }
        response.setReqFunctionsList(rflType);   // probably redundant


        ServContinuationListType servContListType = new ServContinuationListType();
        response.setServContList(servContListType);
        List<ServContReplcItemType> pListOfReplcItems = servContListType.getServContReplcItem();
        // for (int i = 0; i < allReplceStructVec.size(); i++) {
        //    pListOfReplcItems.add(allReplceStructVec.elementAt(i));
        //}

    }

    /**
     * Constructor method.
     * Creates a new instance of ResponseAggrMsg
     *
     * @param queryDefID      The unique ID for the query definition that this response belongs to. This is different from the queryId of the JXTA message that this
     *                        message replies to.
     * @param responderName   The Name of the peer that sends this response.
     * @param responderPeerID The jxta unique Peer ID of the peer that sends this response.
     * @param allValuesVec    A vector of ReqResultOverData objects, that contain results for a requested Function.
     * @param qCount          the query count of the JXTA message that this response replies to.
     */
    public ResponseAggrMsg(String queryDefID, String responderPeerID, String responderName, Vector<ReqResultOverData> allValuesVec, int qCount) {
        try{
            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.response");
            // create an object to marshal
            ObjectFactory theFactory = new ObjectFactory();
            response = theFactory.createQueryResponseType();
        }
        catch (javax.xml.bind.JAXBException je) {
            je.printStackTrace();
            response = new QueryResponseType();
        }
        setDeployStatus(DEPLOY_STATUS_SERVICE_UNKNOWN);

        response.setQueryDefID(queryDefID);
        response.setResponderPeerID(responderPeerID);
        response.setResponderName(responderName);
        response.setQueryCount(Integer.toString(qCount));
        response.setMessageType(ResponseAggrMsg.getThisMsgType());
        RespFunctionsListType rflType = new RespFunctionsListType();
        response.setReqFunctionsList(rflType);
        //
        //RespFunctionsListType rflType = response.getReqFunctionsList();
        List<RespFunctionType> rfList = rflType.getReqFunction();
        // For every function in the Vector create a ReqResultOverData with all data values "timed out" for the sensors defined here!
        for (int i = 0; i < allValuesVec.size(); i++) {
            rfList.add(allValuesVec.elementAt(i));
        }
        response.setReqFunctionsList(rflType);

        ServContinuationListType servContListType = new ServContinuationListType();
        response.setServContList(servContListType);
        List<ServContReplcItemType> pListOfReplcItems = servContListType.getServContReplcItem();
        // for (int i = 0; i < allReplceStructVec.size(); i++) {
        //    pListOfReplcItems.add(allReplceStructVec.elementAt(i));
        //}
    }

    public QueryResponseType getResponse() {
        return response;
    }

    public void setQuery(QueryResponseType pResponse) {
        this.response = pResponse;
    }

    public void setDeployStatus(String pStatus) {
        if(this.response!=null && isValidDeplyStatus(pStatus)){
            this.response.setServiceDeployStatus(pStatus);
        }
    }

    public void setServiceContinuationList(Vector<RespServContinuationReplacementStruct> pVectorOfReplcItems) {
        if(this.response!=null) {
            if(this.response.getServContList() ==null){
                ServContinuationListType servContListType = new ServContinuationListType();
                response.setServContList(servContListType);
            }
            this.response.getServContList().getServContReplcItem().clear();
            if(pVectorOfReplcItems!=null) {
                for(RespServContinuationReplacementStruct itemI : pVectorOfReplcItems) {
                    this.response.getServContList().getServContReplcItem().add(itemI);
                }
            }
        }
    }


    /*
    public void setServiceContinuationList(List<ServContReplcItemType> pListOfReplcItems) {
        if(this.response!=null) {
            if(this.response.getServContList() ==null){
                ServContinuationListType servContListType = new ServContinuationListType();
                response.setServContList(servContListType);
            }
            this.response.getServContList().getServContReplcItem().clear();
            if(pListOfReplcItems!=null) {
                this.response.getServContList().getServContReplcItem().addAll(pListOfReplcItems);
            }
        }
    } */

    /**
     *     valid deploy status FOR GATEWAY LEVEL operations
     */
    public static boolean isValidDeplyStatus(String pStatus) {
        int i;
        for (i = 0; i < VALID_DEPLOY_STATUSES.length; i++) //
        {
            if (pStatus.compareToIgnoreCase(VALID_DEPLOY_STATUSES[i]) == 0){
                return true;
            }
        }
        logger.error("An invalid deploy status was specified!!!");
        return false;
    }

    public String toString()
    {
        String retStr = "";
        try {
            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.response");
            ObjectFactory theFactory = new ObjectFactory();
            javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            JAXBElement<QueryResponseType> myAggrResponseMsgEl = theFactory.createQueryResponse(response);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            marshaller.marshal(myAggrResponseMsgEl, baos);
//            marshaller.marshal(myAggrResponseMsgEl, new java.io.FileOutputStream("ResponseTest.xml"));
            retStr = baos.toString(HTTP.UTF_8);

        } catch (javax.xml.bind.JAXBException je) {
            je.printStackTrace();
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return retStr;
    }

}
