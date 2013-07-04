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
/*
 * PublicResponseAggrMsg.java
 *
 */

package vitro.vspEngine.service.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Vector;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;

import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 * The PublicResponseAggrMsg class (Public Response Aggregate Message) is designed to create a compact response per Gateway and not per mote
 * <p/>
 * <b>However</b> it is now also used for the responses from single motes as well, for the shake of keeping things unified in the code.
 * <p/>
 * This means that eventually only one message will be sent back to the user per gateway per query.
 * <pre>
 * The new type of message is structured as follows. It contains:
 * 		&lt;responderName&gt;Vega&lt;/responderName&gt;
 * 		&lt;responderPeerID&gt;urn:uuid-jdklflklkaslkfslkafklalkfsfll&lt;/responderPeerID&gt;
 * 		&lt;queryDefID&gt;3323223523523&lt;/queryDefID&gt;
 *              &lt;query-count&gt;0&lt;/query-count&gt;
 * 		&lt;reqFunctionsList&gt;
 *                  &lt;reqFunction&gt;
 *                      &lt;fid&gt;4&lt;/fid&gt;
 *                      &lt;outList&gt;
 *                          &lt;out&gt;
 *                              &lt;mid&gt;2&lt;/mid&gt; (or -1 if aggregate function)
 *                              &lt;sid&gt;1&lt;/sid&gt;
 *                              &lt;val&gt;1112&lt;/val&gt; (will be an entire file of CSV (comma separated) values if History Of Values Function)
 *                              &lt;time&gt;
 *                                  &lt;from&gt;timestamp1&lt;/from&gt;
 *                                  &lt;to&gt;timestamp2&lt;/to&gt;
 *                              &lt;/time&gt;
 *                              &lt;NumOfAggrVal&gt;4&lt;/ NumOfAggrVal&gt; (Has meaning when mid = -1 or even when the function is History)
 *                          &lt;/out&gt;
 *                          &lt;out&gt;
 *                              .
 *                              .
 *                          &lt;/out&gt;
 *                      &lt;/outList&gt;
 *                  &lt;/reqFunction&gt;
 *                  &lt;reqFunction&gt;
 *                      .
 *                      .
 *                      .
 *                  &lt;/reqFunction&gt;
 * 		&lt;/reqFunctionsList&gt;
 * <p/>
 *  each reqFunction tag corresponds to a different requested function.
 *  </pre>
 *
 * @author antoniou
 */
public class PublicResponseAggrMsg {
    private static Logger logger = Logger.getLogger(PublicResponseAggrMsg.class);
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



    private String responderName;
    private String responderPeerID; // this ID is also on the wrapper message, but we need it here also, because
    // we can store this object directly and conveniently to a temp file that will be
    // immediately associated with a Gateway peerID.
    private String queryDefID; // this ID is the unique ID for this query definition (and is different for the query id).
    // It is used to connect this partial reply to the query definition that was issued.
    private int queryCount;    // Not "response count", as it is determined by the query.
    Vector<ReqResultOverData> allValuesVec;
    private String serviceDeployStatus;
    Vector<RespServContinuationReplacementStruct> vectorOfReplcItems; // for continuation info

    private final static String thisMsgType = "aggregatedResponse";

    private static final String messageTypeTag = "message-type";
    private static final String queryCountTag = "query-count";
    private static final String requestedFunctionsListTag = "reqFunctionsList";
    private static final String requestedFunctionTag = "reqFunction";
    private static final String responderNameTag = "responderName";
    private static final String responderPeerIDTag = "responderPeerID";
    private static final String queryDefIDTag = "queryDefID";
    private static final String myDocumentRootTag = "QueryResponse";
    private static final String servContListTag = "servContList";
    private static final String servContListItemTag = "servContReplcItem";
    private static final String deployStatusTag = "serviceDeployStatus";


    /**
     * Constructor method.
     * Creates a new instance of PublicResponseAggrMsg with timed-out entries for all sensors defined and all functions
     *
     * @param queryDefID                    The unique ID for the query definition that this response belongs to. This is different from the queryId of the partial message that this
     *                                      message replies to.
     * @param responderName                 The Name of the peer that sends this response.
     * @param responderPeerID               The unique Peer ID of the peer that sends this response.
     * @param motesSensorsAndFunctionsForQueryVec The map that defines the queried sensors, indexed by the motes that have them
     * @param functionVec                   the vector of selected functions to be applied
     * @param qCount                        the query count of the message that this response replies to.
     */
    public PublicResponseAggrMsg(String queryDefID, String responderPeerID, String responderName, Vector<QueriedMoteAndSensors> motesSensorsAndFunctionsForQueryVec, Vector<ReqFunctionOverData> functionVec, int qCount) {
        this.queryDefID = queryDefID;
        this.responderPeerID = responderPeerID;
        this.responderName = responderName;
        this.queryCount = qCount;
        this.allValuesVec = new Vector<ReqResultOverData>();
        setDeployStatus(DEPLOY_STATUS_SERVICE_UNKNOWN);
        this.vectorOfReplcItems = new Vector<RespServContinuationReplacementStruct>();
        // For every function in the Vector create a ReqResultOverData with all data values "timed out" for the sensors defined here!
        for (int i = 0; i < functionVec.size(); i++) {
            if(ReqFunctionOverData.isValidGatewayReqFunct(functionVec.elementAt(i).getfuncName()))
            {
                //TODO: the place holder here does not work! It prints out one only placeholder that does not get updated! (even though multiple capabilities could be selected)
//                Vector<QueriedMoteAndSensors> aggrMotesAndSensorsAndFunctsVec = new Vector<QueriedMoteAndSensors>();
//                QueriedMoteAndSensors tmpQuerMotAndSensors = new QueriedMoteAndSensors();
//                aggrMotesAndSensorsAndFunctsVec.addElement(tmpQuerMotAndSensors);
//                
//                tmpQuerMotAndSensors.setMoteId(SmartNode.invalidId);
//                Vector<ReqSensorAndFunctions> tmpReqSensorFuncts = new Vector<ReqSensorAndFunctions>();
//                tmpQuerMotAndSensors.setQueriedSensorIdsAndFuncVec(tmpReqSensorFuncts);
//                
//                
//                for (int k = 0; k < motesSensorsAndFunctionsForQueryVec.size(); k++) {
//                    QueriedMoteAndSensors tmpMoteAndItsSensors = motesSensorsAndFunctionsForQueryVec.elementAt(k);
//                    String moteId = tmpMoteAndItsSensors.getMoteId();
//                    Vector<ReqSensorAndFunctions> tmpSensorsAndItsFunctionsVec = tmpMoteAndItsSensors.getQueriedSensorIdsAndFuncVec();
//                    for (int j = 0; j < tmpSensorsAndItsFunctionsVec.size(); j++) {
//                        ReqSensorAndFunctions tmpSensorAndItsFunctions = tmpSensorsAndItsFunctionsVec.elementAt(j);
//                        int sid = tmpSensorAndItsFunctions.getSensorModelId();
//                        if (tmpSensorAndItsFunctions.getFunctionsOverSensorModelVec().contains(Integer.valueOf(functionVec.elementAt(i).getfuncId()))) {
//                            Vector<Integer> functsForModel = new Vector<Integer>();
//                            functsForModel.addElement(Integer.valueOf(functionVec.elementAt(i).getfuncId()));
//                            ReqSensorAndFunctions tmpReqSensorAndFuncts = new ReqSensorAndFunctions(sid, functsForModel);
//                            
//                            tmpReqSensorFuncts.addElement(tmpReqSensorAndFuncts);
//                        }
//                    }
//                }                        
//                
//                this.allValuesVec.addElement(new ReqResultOverData(functionVec.elementAt(i).getfuncId(), aggrMotesAndSensorsAndFunctsVec, ReqResultOverData.modeFillWithTimeouts));                
//           
            }
            else
                this.allValuesVec.addElement(new ReqResultOverData(functionVec.elementAt(i).getfuncId(), motesSensorsAndFunctionsForQueryVec, ReqResultOverData.modeFillWithTimeouts));
        }
    }

    /**
     * Constructor method.
     * Creates a new instance of PublicResponseAggrMsg
     *
     * @param queryDefID      The unique ID for the query definition that this response belongs to. This is different from the queryId of the partial message that this
     *                        message replies to.
     * @param responderName   The Name of the peer that sends this response.
     * @param responderPeerID The unique Peer ID of the peer that sends this response.
     * @param allValuesVec    A vector of ReqResultOverData objects, that contain results for a requested Function.
     * @param qCount          the query count of the message that this response replies to.
     */
    public PublicResponseAggrMsg(String queryDefID, String responderPeerID, String responderName, Vector<ReqResultOverData> allValuesVec, int qCount) {
        this.queryDefID = queryDefID;
        this.responderPeerID = responderPeerID;
        this.responderName = responderName;
        this.allValuesVec = allValuesVec;
        this.queryCount = qCount;
        setDeployStatus(DEPLOY_STATUS_SERVICE_UNKNOWN);
        this.vectorOfReplcItems = new Vector<RespServContinuationReplacementStruct>();
    }


    /**
     * Constructor method.
     * Creates a query object by parsing the given input stream.
     * It will be needed for the extraction of the query at the peers
     * that receive the query as a stream.
     *
     * @param stream the InputStream source of the query data.
     */

    public PublicResponseAggrMsg(InputStream stream) throws IOException {

        this.queryDefID = "undefined";
        this.responderPeerID = "undefined";
        this.responderName = "undefined";
        this.allValuesVec = new Vector<ReqResultOverData>();
        this.queryCount = -1;
        setDeployStatus(DEPLOY_STATUS_SERVICE_UNKNOWN);
        this.vectorOfReplcItems = new Vector<RespServContinuationReplacementStruct>();

        XMLStreamReader2 sr = null;
        try{
            WstxInputFactory f = null;

            SMInputCursor inputRootElement = null;

            f = new WstxInputFactory();
            f.configureForConvenience();
            // Let's configure factory 'optimally'...
            f.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            f.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);

            sr = (XMLStreamReader2)f.createXMLStreamReader(stream);
            inputRootElement = SMInputFactory.rootElementCursor(sr);
            // If we needed to store some information about preceding siblings,
            // we should enable tracking. (we need it for  mygetElementValueStaxMultiple method)
            inputRootElement.setElementTracking(SMInputCursor.Tracking.PARENTS);

            inputRootElement.getNext();
            SMInputCursor childInElement = inputRootElement.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() ) {
                    if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.messageTypeTag)==0 ) {

                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        String tmpMessageTypeValue = "";
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpMessageTypeValue = childInElement2.getText();
                                break;
                            }
                        }
                        if(tmpMessageTypeValue.compareToIgnoreCase(PublicResponseAggrMsg.thisMsgType) !=0)
                        {
                            System.out.println("This is not the expected type of message (Aggregate Response)");
                            throw new IOException(); // (++++) maybe throw some other kind of exception
                        }
                        //Model3dStylesList.getListofStyleEntriesVec().add(new Model3dStylesEntry(childInElement));
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.queryCountTag )==0 ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.queryCount = (int) Integer.valueOf( childInElement2.getText());
                                break;
                            }
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.getRequestedFunctionsListTag()) ==0) {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null) {
                            if(!childInElement2.getCurrEvent().hasText() && childInElement2.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.getRequestedFunctionTag() ) ==0) {
                                this.allValuesVec.addElement(new ReqResultOverData(childInElement2));
                            }
                        }

                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.servContListTag ) ==0) {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null) {
                            if(!childInElement2.getCurrEvent().hasText() && childInElement2.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.getServContListItemTag()) ==0) {
                                this.vectorOfReplcItems.addElement(new RespServContinuationReplacementStruct(childInElement2));
                            }
                        }

                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.responderNameTag ) ==0 ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.responderName = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.responderPeerIDTag )==0 ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.responderPeerID = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.queryDefIDTag ) ==0) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.queryDefID = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(PublicResponseAggrMsg.deployStatusTag ) ==0) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.setDeployStatus(childInElement2.getText());
                                break;
                            }
                        }
                    }
                }
            }
            logger.debug("Created PublicResponseAggrMsg from stream!!");
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
            ex.printStackTrace();
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

    public static String getServContListItemTag() {
        return servContListItemTag;
    }

    /*
    *  Get-ters
    */


    /**
     * Returns the Query Count field of this object. This matches the query count field of the query this object replies to.
     *
     * @return The Query Count field of this object.
     */
    public int getQueryCount() {
        return queryCount;
    }

    /**
     * Returns the Name of the peer who replies to the query
     *
     * @return The name of the responding peer
     */
    public String getResponderName() {
        return responderName;
    }

    /**
     * Returns the PeerID of the peer who replies to the query. This peerID is actually replicated in the wrapping message
     * (ResolverResponseMsg) but we put it here too, for convenience.
     *
     * @return The PeerID of the peer who responds to the query
     */
    public String getResponderPeerID() {
        return responderPeerID;
    }

    /**
     * Returns a vector of ReqResultOverData that this message contains.
     *
     * @return Vector of ReqResultOverData structs
     */
    public Vector<ReqResultOverData> getAllValuesVec() {
        return allValuesVec;
    }

    /**
     * Returns the query definition ID that this response belongs to. This ID is the unique ID for this query definition (and is different from the partial query id).
     * It is used to connect this partial reply to the query definition that was issued.
     *
     * @return String with the unique query definition ID.
     */
    public String getQueryDefID() {
        return queryDefID;
    }

    /**
     * Returns a string that declares the type of this message. This is put for extensibility purposes.
     *
     * @return String with the response type.
     */
    public static String getThisMsgType() {
        return thisMsgType;
    }

    /**
     * Returns the tag name of the requested function field in the XML structure.
     *
     * @return the tag name of the requested function field in the XML structure
     */
    public static String getRequestedFunctionTag() {
        return requestedFunctionTag;
    }

    public static String getRequestedFunctionsListTag() {
        return requestedFunctionsListTag;
    }

    public String getDeployStatus() {
            return serviceDeployStatus;
    }

    public void setDeployStatus(String pStatus) {
        if(isValidDeplyStatus(pStatus)){
            serviceDeployStatus = pStatus;
        }
    }

    public Vector<RespServContinuationReplacementStruct>  getServiceContinuationList() {
        return this.vectorOfReplcItems;
    }

    public void setServiceContinuationList(Vector<RespServContinuationReplacementStruct> pVectorOfReplcItems) {
        this.vectorOfReplcItems = pVectorOfReplcItems;
    }

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

    /**
     * Creates XML structured info on this PublicResponseAggrMsg object, under the parent Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document. it could also be null.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(PublicResponseAggrMsg.myDocumentRootTag);
            }
            else {
                tmpElementOuter =  document.addElement(PublicResponseAggrMsg.myDocumentRootTag); //special case for PublicResponseAggrMsg creation
            }

            tmpElement1 =  tmpElementOuter.addElement(PublicResponseAggrMsg.messageTypeTag );
            tmpElement1.addCharacters(  PublicResponseAggrMsg.thisMsgType);

            tmpElement1 =  tmpElementOuter.addElement(PublicResponseAggrMsg.queryDefIDTag );
            tmpElement1.addCharacters(  queryDefID);

            tmpElement1 =  tmpElementOuter.addElement(PublicResponseAggrMsg.queryCountTag );
            tmpElement1.addCharacters(  Integer.toString(queryCount));

            tmpElement1 =  tmpElementOuter.addElement(PublicResponseAggrMsg.responderPeerIDTag );
            tmpElement1.addCharacters(  responderPeerID);

            tmpElement1 =  tmpElementOuter.addElement(PublicResponseAggrMsg.responderNameTag );
            tmpElement1.addCharacters(  responderName);

            if (this.allValuesVec.size() > 0) {

                tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.getRequestedFunctionsListTag());
                for (int k = 0; k < this.allValuesVec.size(); k++) {
                    this.allValuesVec.get(k).createInfoInDocument(document, tmpElement1, false);
                }

            }

            if (this.vectorOfReplcItems.size() > 0) {
                tmpElement1 = tmpElementOuter.addElement(PublicResponseAggrMsg.servContListTag);
                for (int k = 0; k < this.vectorOfReplcItems.size(); k++) {
                    this.vectorOfReplcItems.get(k).createInfoInDocument(document, tmpElement1, false);
                }

            }

            tmpElement1 = tmpElementOuter.addElement(PublicResponseAggrMsg.deployStatusTag);
            tmpElement1.addCharacters(getDeployStatus());
        } catch(Exception e) {
            return;
        }
    }

    /**
     * Gives a String representation of the XML structure for this response object.
     *
     * @return the XML String representing this response.
     */
    public String toString() {
        StringWriter outStringWriter = new StringWriter();
        WstxOutputFactory fout = new WstxOutputFactory();
        fout.configureForXmlConformance();
        SMOutputDocument doc = null;
        try{
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            createInfoInDocument(doc, null);
            doc.closeRoot();
        } catch(Exception e) {
            e.printStackTrace();
            return "Errors encountered while attempting to print this PublicResponseAggrMsg!";
        }
        String retString = "";
        try{
            retString = outStringWriter.toString();
            outStringWriter.close();
        } catch(Exception e) {
            logger.error("Errors encountered while attempting to print this XML document!");
            e.printStackTrace();
        }
        return retString;

    }

}
