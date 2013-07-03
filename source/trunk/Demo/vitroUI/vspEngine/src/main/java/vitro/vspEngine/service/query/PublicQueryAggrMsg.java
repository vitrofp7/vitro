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
 * PublicQueryAggrMsg.java
 *
 */

package vitro.vspEngine.service.query;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.Vector;


/**
 * This class PublicQueryAggrMsg (Public Query Aggregated Message) is supposed to do what the simple PublicQueryMsg does,
 * but it is designed to create a compact query per Gateway and not per mote.
 * This means that eventually only one message will be sent to each gateway per query.
 * <pre>
 *  The new type of message is structured as follows. It contains:
 *      <ul>
 *      <li> The Unique query definition ID that this partial query belongs to:
 *          &lt;queryDefID&gt;
 *               3323223523523
 *          &lt;/queryDefID&gt;
 *      <li> The Unique query ID that this partial query has inside this query definition:
 *          &lt;query-count&gt;
 *               4
 *          &lt;/query-count&gt;
 *      <li> a Vector of moteIds correlated with sensorModelIds as an XML:
 *          &lt;motesList&gt;
 *              &lt;mote&gt;
 *                  &lt;moteid&gt;1&lt;/moteid&gt;
 *                  &lt;funcOnSensor&gt;
 *                      &lt;sensorModelid&gt;4&lt;/sensorModelid&gt;
 *                      &lt;fid&gt;1&lt;/fid&gt;
 *                      &lt;fid&gt;3&lt;/fid&gt;
 *                      &lt;fid&gt;4&lt;/fid&gt;
 *                  &lt;/funcOnSensor&gt;
 *                  &lt;funcOnSensor&gt;
 *                      &lt;sensorModelid&gt;2&lt;/sensorModelid&gt;
 *                      &lt;fid&gt;2&lt;/fid&gt;
 *                      &lt;fid&gt;5&lt;/fid&gt;
 *                  &lt;/funcOnSensor&gt;
 *              &lt;/mote&gt;
 *              &lt;mote&gt;
 *                  .
 *                  .
 *              &lt;/mote&gt;
 *          &lt;/motesList&gt;
 *      <li> the History field (true/false) (To be superceded)
 *      <li> the function field as an XML:
 *           &lt;reqFunctionsList&gt;
 *              &lt;reqFunction&gt;
 *                  &lt;id&gt;1&lt;/id&gt;
 *                  &lt;description&gt;Average of Values&lt;/description&gt;
 *                  &lt;timePeriod&gt;  (Optional)
 *                      &lt;from&gt;timestamp_x&lt;/from&gt;
 *                      &lt;to&gt;timestamp_y&lt;/to&gt;
 *                  &lt;/timePeriod&gt;
 *                  &lt;thresholdField&gt; (Optional. we can have at most two of these tags -when we define a interval)
 *                      &lt;thresholdRelation&gt;Larger&lt;/thresholdRelation&gt;
 *                      &lt;thresholdValue&gt;33&lt;/thresholdValue&gt;
 *                  &lt;/thresholdField&gt;
 *                  &lt;thresholdField&gt;
 *                      &lt;thresholdRelation&gt;Lower or Equal&lt;/thresholdRelation&gt;
 *                      &lt;thresholdValue&gt;5&lt;/thresholdValue&gt;
 *                  &lt;/thresholdField&gt;
 *              &lt;/reqFunction&gt;
 *              &lt;reqFunction&gt;
 *                      .
 *                      .
 *                      .
 *              &lt;/reqFunction&gt;
 *        &lt;/reqFunctionsList&gt;
 * <p/>
 * </pre>
 *
 * @author antoniou
 */
public class PublicQueryAggrMsg {
    private Logger logger = Logger.getLogger(PublicQueryAggrMsg.class);
    private Vector<QueriedMoteAndSensors> motesSensorsAndFunctVec;
    private boolean isHistory;
    private boolean continuationEnabledFlag;
    private boolean asynchronousFlag;
    private boolean dtnEnabledFlag;
    private boolean securityEnabledFlag;
    private boolean encryptionEnabledFlag;
    private Vector<ReqFunctionOverData> uniqueFunctionVec;
    private int queryCount;
    private String queryDefID;
    private static final String thisMsgType = "aggregatedQuery";

    private static final String queryDefIDTag = "queryDefID";
    private static final String messageTypeTag = "message-type";
    private static final String queryCountTag = "query-count";
    private static final String requestedFunctionsListTag = "reqFunctionsList";
    private static final String requestedFunctionTag = "reqFunction";

    private static final String motesListTag = "motesList";
    private static final String moteTag = "mote";

    private static final String isHistoryTag = "isHistory";
    private static final String myDocumentRootTag = "myQueryAggr";
    //new tags
    private static final String asynchTag = "asynch";
    private static final String encryptTag = "encrypt";
    private static final String dtnTag = "dtn";
    private static final String securityTag = "security";
    private static final String continuationTag = "continuation";




    /**
     * Constructor method.
     * Creates a new instance of PublicQueryAggrMsg which is
     * a query object using the given attribute.
     * @param queryDefID
     * @param motesSensorsandFunctionsVec The mapping of MoteIds->ListOfSensorModelIds and functions of the motes and their sensors from which we want to retrieve a value.
     * @param isHist                The boolean "flag" that indicates if the query is for a single value or for a history of values. (To do) this should be removed from here. It serves Absolutely NO purpose WHATSOEVER!
     * @param desiredUniqueFunctionVec    A vector of ReqFunctionOverData with the XML description of the desired function
     * @param qCount                An integer indicating the position of this query in the ordering of a set of queries
     * @author antoniou
     */
    public PublicQueryAggrMsg(String queryDefID, Vector<QueriedMoteAndSensors> motesSensorsandFunctionsVec, boolean isHist, Vector<ReqFunctionOverData> desiredUniqueFunctionVec, int qCount) {
        this.queryDefID = queryDefID;
        this.motesSensorsAndFunctVec = motesSensorsandFunctionsVec;
        this.isHistory = isHist;
        this.uniqueFunctionVec = desiredUniqueFunctionVec;
        this.queryCount = qCount;
        this.setAsynchronousFlag(false);
        this.setContinuationEnabledFlag(false);
        this.setDtnEnabledFlag(false);
        this.setEncryptionEnabledFlag(false);
        this.setSecurityEnabledFlag(false);
    }


    public PublicQueryAggrMsg(String queryDefID, Vector<QueriedMoteAndSensors> motesSensorsandFunctionsVec, boolean isHist, Vector<ReqFunctionOverData> desiredUniqueFunctionVec, int qCount,
                              boolean pContinuationEnabledFlag,
                              boolean pAsynchronousFlag,
                              boolean pDtnEnabledFlag,
                              boolean pSecurityEnabledFlag,
                              boolean pEncryptionEnabledFlag) {
        this.queryDefID = queryDefID;
        this.motesSensorsAndFunctVec = motesSensorsandFunctionsVec;
        this.isHistory = isHist;
        this.uniqueFunctionVec = desiredUniqueFunctionVec;
        this.queryCount = qCount;
        this.setAsynchronousFlag(pAsynchronousFlag);
        this.setContinuationEnabledFlag(pContinuationEnabledFlag);
        this.setDtnEnabledFlag(pDtnEnabledFlag);
        this.setEncryptionEnabledFlag(pEncryptionEnabledFlag);
        this.setSecurityEnabledFlag(pSecurityEnabledFlag);
    }



    /**
     * Constructor method.
     * Creates a query object by parsing the given input stream.
     * It will be needed for the extraction of the query at the peers
     * that receive the query as a stream.
     *
     * @param stream the InputStream source of the query data.
     */
    public PublicQueryAggrMsg(InputStream stream) throws IOException {
        this.queryDefID = "undefined";
        this.isHistory = false;
        this.queryCount = -1;
        this.motesSensorsAndFunctVec = new Vector<QueriedMoteAndSensors>();
        this.uniqueFunctionVec = new Vector<ReqFunctionOverData>();
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
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() ) {
                    if( childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.messageTypeTag.toLowerCase() ) ) {

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
                        if(!tmpMessageTypeValue.toLowerCase().equals(PublicQueryAggrMsg.thisMsgType.toLowerCase()) )
                        {
                            System.out.println("This is not the expected type of message (Aggregate Query)");
                            throw new IOException(); // (++++) maybe throw some other kind of exception
                        }
                        //Model3dStylesList.getListofStyleEntriesVec().add(new Model3dStylesEntry(childInElement));
                    }
                    else if( childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.queryDefIDTag.toLowerCase() ))
                    {
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
                    else if( childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.queryCountTag.toLowerCase() ) ) {
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
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.isHistoryTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                if ( childInElement2.getText().toLowerCase().equals("true")) {
                                    this.isHistory = true;
                                } else {
                                    this.isHistory = false;
                                }
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.asynchTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                if ( childInElement2.getText().toLowerCase().equals("true")) {
                                    this.setAsynchronousFlag(true);
                                } else {
                                    this.setAsynchronousFlag(false);
                                }
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.continuationTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                if ( childInElement2.getText().toLowerCase().equals("true")) {
                                    this.setContinuationEnabledFlag(true);
                                } else {
                                    this.setContinuationEnabledFlag(false);
                                }
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.dtnTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                if ( childInElement2.getText().toLowerCase().equals("true")) {
                                    this.setDtnEnabledFlag(true);
                                } else {
                                    this.setDtnEnabledFlag( false);
                                }
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.encryptTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                if ( childInElement2.getText().toLowerCase().equals("true")) {
                                    this.setEncryptionEnabledFlag(true);
                                } else {
                                    this.setEncryptionEnabledFlag(false);
                                }
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.securityTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                if ( childInElement2.getText().toLowerCase().equals("true")) {
                                    this.setSecurityEnabledFlag(true);
                                } else {
                                    this.setSecurityEnabledFlag(false);
                                }
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.getMotesListTag().toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null) {
                            if(!childInElement2.getCurrEvent().hasText() && childInElement2.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.getMoteTag().toLowerCase() ) ) {
                                this.motesSensorsAndFunctVec.addElement(new QueriedMoteAndSensors(childInElement2));
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.requestedFunctionsListTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null) {
                            if(!childInElement2.getCurrEvent().hasText() && childInElement2.getLocalName().toLowerCase().equals(PublicQueryAggrMsg.getRequestedFunctionTag().toLowerCase() ) ) {
                                this.uniqueFunctionVec.addElement(new ReqFunctionOverData(childInElement2));
                            }
                        }
                    }
                }
            }
            logger.debug("Created PublicQueryAggrMsg from stream!!");
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

    // get-ters

    /**
     * Method getUniqueFunctionVec:
     * <p/>
     * no parameters
     *
     * @return the Vector of ReqFunctionOverData objects for the requested functions.
     */
    public Vector<ReqFunctionOverData> getUniqueFunctionVec() {
        return this.uniqueFunctionVec;
    }

    /**
     * Returns a Vector of QueriedMotesAndSensors objects.
     *
     * @return the Vector of QueriedMotesAndSensors objects with the moteids mapped to a list of their sensors requested by the query.
     */
    public Vector<QueriedMoteAndSensors> getmotesSensorsandFunctVec() {
        return this.motesSensorsAndFunctVec;
    }

    /**
     * Method getIsHistory:
     * <p/>
     * no parameters
     *
     * @return the isHistory "flag".
     */
    public boolean getIsHistory() {

        return isHistory;
    }


    /**
     * Method getQueryCount:
     * <p/>
     * no parameters
     *
     * @return the counter of the query (its order in a set of queries)
     */
    public int getQueryCount() {

        return queryCount;
    }

    /**
     * Returns a string that declares the type of this message. This is put for extensibility purposes.
     *
     * @return String with the query message type.
     */
    public static String getThisMsgType() {
        return thisMsgType;
    }

    /**
     * Returns the query definition ID that this query belongs to. This ID is the unique ID for this query definition (and is different for the partial sent query id).
     * It is used to connect this partial query to the query definition that was issued.
     *
     * @return String with the unique query definition ID.
     */
    public String getQueryDefID() {
        return queryDefID;
    }

    /**
     * Returns the Requested Function tag name in the xml structure of the query message
     *
     * @return String with the Requested Function tag name
     */
    public static String getRequestedFunctionTag() {
        return requestedFunctionTag;
    }

    public static String getRequestedFunctionsListTag() {
        return requestedFunctionsListTag;
    }

    public static String getMotesListTag() {
        return motesListTag;
    }


    /**
     * Returns the Mote tag name in the xml structure of the query message
     *
     * @return String with the Mote tag name
     */
    public static String getMoteTag() {
        return moteTag;
    }


    /**
     * Method createInfoInDocument:
     * Creates XML structured info on this PublicQueryAggrMsg object, under the parent Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document. it could also be null.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(PublicQueryAggrMsg.myDocumentRootTag);
            }
            else {
                tmpElementOuter =  document.addElement(PublicQueryAggrMsg.myDocumentRootTag);
            }

            tmpElement1 =  tmpElementOuter.addElement(PublicQueryAggrMsg.messageTypeTag );
            tmpElement1.addCharacters(  PublicQueryAggrMsg.thisMsgType);

            tmpElement1 =  tmpElementOuter.addElement(PublicQueryAggrMsg.queryDefIDTag );
            tmpElement1.addCharacters(  queryDefID);

            tmpElement1 =  tmpElementOuter.addElement(PublicQueryAggrMsg.queryCountTag );
            tmpElement1.addCharacters(  Integer.toString(queryCount));

            if (this.motesSensorsAndFunctVec.size() > 0) {
                tmpElement1 =  tmpElementOuter.addElement(PublicQueryAggrMsg.getMotesListTag() );
                for (int k = 0; k < this.motesSensorsAndFunctVec.size(); k++) {
                    this.motesSensorsAndFunctVec.get(k).createInfoInDocument( document, tmpElement1);
                }
            }
            //
            if (this.uniqueFunctionVec.size() > 0) {
                tmpElement1 =  tmpElementOuter.addElement(PublicQueryAggrMsg.getRequestedFunctionsListTag()) ;
                for (int k = 0; k < this.uniqueFunctionVec.size(); k++) {
                    //System.out.println("The k = " + Integer.toString(k));
                    //System.out.println(this.uniqueFunctionVec.get(k).toString());
                    this.uniqueFunctionVec.get(k).createInfoInDocument( document, tmpElement1);
                }
            }

            //history tag
            tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.isHistoryTag);
            if (isHistory) {
                tmpElement1.addCharacters(  "true");
            } else {
                tmpElement1.addCharacters(  "false");
            }
            //asynchronous reply tag
            tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.asynchTag);
            if (isAsynchronousFlag()) {
                tmpElement1.addCharacters(  "true");
            } else {
                tmpElement1.addCharacters(  "false");
            }
            //continuation tag
            tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.continuationTag);
            if (isContinuationEnabledFlag()) {
                tmpElement1.addCharacters(  "true");
            } else {
                tmpElement1.addCharacters(  "false");
            }
            //dtn tag
            tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.dtnTag);
            if (isDtnEnabledFlag()) {
                tmpElement1.addCharacters(  "true");
            } else {
                tmpElement1.addCharacters(  "false");
            }
            //encryption tag
            tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.encryptTag);
            if (isEncryptionEnabledFlag()) {
                tmpElement1.addCharacters(  "true");
            } else {
                tmpElement1.addCharacters(  "false");
            }
            //security tag
            tmpElement1 = tmpElementOuter.addElement(PublicQueryAggrMsg.securityTag);
            if (isSecurityEnabledFlag()) {
                tmpElement1.addCharacters(  "true");
            } else {
                tmpElement1.addCharacters(  "false");
            }
        } catch(Exception e) {
            return;
        }

    }



    /*
           * Method toString:
           *
      * no parameters
           * @return  the XML String representing this object's document.
           */
//    public String toString() {
//        try {
//            StringWriter out = new StringWriter();
//            StructuredTextDocument doc = (StructuredTextDocument) getDocument(new MimeMediaType("text/xml"));
//            doc.sendToWriter(out);
//            return out.toString();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            return "Errors encountered while attempting to print this PublicQueryAggrMsg!";
//        	}
//    	}

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
            return "Errors encountered while attempting to print this PublicQueryAggrMsg!";
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

    public boolean isContinuationEnabledFlag() {
        return continuationEnabledFlag;
    }

    public void setContinuationEnabledFlag(boolean continuationEnabledFlag) {
        this.continuationEnabledFlag = continuationEnabledFlag;
    }

    public boolean isAsynchronousFlag() {
        return asynchronousFlag;
    }

    public void setAsynchronousFlag(boolean asynchronousFlag) {
        this.asynchronousFlag = asynchronousFlag;
    }

    public boolean isDtnEnabledFlag() {
        return dtnEnabledFlag;
    }

    public void setDtnEnabledFlag(boolean dtnEnabledFlag) {
        this.dtnEnabledFlag = dtnEnabledFlag;
    }

    public boolean isSecurityEnabledFlag() {
        return securityEnabledFlag;
    }

    public void setSecurityEnabledFlag(boolean securityEnabledFlag) {
        this.securityEnabledFlag = securityEnabledFlag;
    }

    public boolean isEncryptionEnabledFlag() {
        return encryptionEnabledFlag;
    }

    public void setEncryptionEnabledFlag(boolean encryptionEnabledFlag) {
        this.encryptionEnabledFlag = encryptionEnabledFlag;
    }
}
