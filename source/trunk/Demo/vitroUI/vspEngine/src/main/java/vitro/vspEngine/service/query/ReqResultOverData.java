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
 * RespResultOverData.java
 */

package vitro.vspEngine.service.query;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Vector;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

/**
 * Describes the "reqFunction" tag structure in the following schema for results to aggregate queries
 * An Aggregate public Response Message can have multiple result fields (if multiple functions where specified in the query).
 * <pre>
 * Follows an XML Format:
 * 		&lt;reqFunction&gt;
 *                  &lt;fid&gt;3&lt;/fid&gt;
 *                  &lt;outList&gt;
 *                      &lt;out&gt;
 *                          &lt;mid&gt;2&lt;/mid&gt; (or -1 if aggregate function)
 *                          &lt;sid&gt;1&lt;/sid&gt;
 *                          &lt;val&gt;1112&lt;/val&gt; (might be an entire file of CSV (comma separated) values if History Of Values Function)
 *                          &lt;time&gt;
 *                              &lt;from&gt;timestamp1&lt;/from&gt;
 *                              &lt;to&gt;timestamp2&lt;/to&gt;
 *                          &lt;/time&gt;
 *                          &lt;NumOfAggrVal&gt;4&lt;/ NumOfAggrVal&gt; (Has meaning when mid = -1 or even when the function is History)
 *                      &lt;/out&gt;
 *                      &lt;out&gt;
 *                          .
 *                          .
 *                      &lt;/out&gt;
 *                  &lt;/outList&gt;
 *              &lt;/reqFunction&gt;
 *  </pre>
 *
 * @author antoniou
 */
public class ReqResultOverData {
    private Logger logger = Logger.getLogger(ReqResultOverData.class);
    int fid;
    Vector<ResultAggrStruct> allResultsforFunct;

    public static int modeFillWithTimeouts = 1;

    public static final String specialValuePending = "Pending response";
    public static final String specialValueNotSupported = "Unsupported";
    public static final String specialValueBinary = "Binary value";
    public static final String specialValueNoReading = "No reading";
    public static final String specialValueTimedOut = "Timed out";

    private static final String fidTag = "fid";
    private static final String outTag = "out";
    private static final String outListTag = "outList"; // TODO: this is NOT USED. But it SHOULD BE!


    /**
     * Creates a new instance of RespResultOverData with timed-out values for all defined sensors.
     *
     * @param givenfid the unique function id for this function.
     * @param motesSensorsAndFunctionsForQueryVec
     *                 the Vector that defines the querried sensors for each mote and the pertinent functions for each sensor
     * @param mode     For this call it is set to  ReqResultOverData.modeFillWithTimeouts . This parameter is used only to differentiate this constructor's definition from the other one (there was some ambiguity with the vector arguments)
     */
    public ReqResultOverData(int givenfid, Vector<QueriedMoteAndSensors> motesSensorsAndFunctionsForQueryVec, int mode) {
        this.fid = givenfid;

        this.allResultsforFunct = new Vector<ResultAggrStruct>();
        //new 22/04
        HashMap<String, Vector<String>> uniqMotesToSensorsIdsForFidHM = new HashMap<String, Vector<String>>();

        //--
        for (int i = 0; i < motesSensorsAndFunctionsForQueryVec.size(); i++) {
            QueriedMoteAndSensors tmpMoteAndItsSensors = motesSensorsAndFunctionsForQueryVec.elementAt(i);
            //new 22/04
            String moteId = tmpMoteAndItsSensors.getMoteId();
            if(!uniqMotesToSensorsIdsForFidHM.containsKey(moteId) ) {
                uniqMotesToSensorsIdsForFidHM.put(moteId, new Vector<String>());
            }          //---
            Vector<ReqSensorAndFunctions> tmpSensorsAndItsFunctionsVec = tmpMoteAndItsSensors.getQueriedSensorIdsAndFuncVec();

            for (int j = 0; j < tmpSensorsAndItsFunctionsVec.size(); j++) {


                ReqSensorAndFunctions tmpSensorAndItsFunctions = tmpSensorsAndItsFunctionsVec.elementAt(j);
                String sid = tmpSensorAndItsFunctions.getSensorModelId();

                if (tmpSensorAndItsFunctions.getFunctionsOverSensorModelVec().contains(Integer.valueOf(givenfid))) {
                    //new 22/04
                    if(!uniqMotesToSensorsIdsForFidHM.get(moteId).contains(sid) ) {
                        uniqMotesToSensorsIdsForFidHM.get(moteId).addElement(sid);
                        this.allResultsforFunct.add(new ResultAggrStruct(moteId, sid, ReqResultOverData.specialValueTimedOut, 0, null));
                    }
                }
            }
        }
    }

    /**
     * Creates a new instance of RespResultOverData
     *
     * @param givenfid           the unique id for the function for which the results are contained in this object
     * @param allResultsforFunct a Vector with the results per mid/sid key.
     */
    public ReqResultOverData(int givenfid, Vector<ResultAggrStruct> allResultsforFunct) {
        this.fid = givenfid;
        this.allResultsforFunct = allResultsforFunct;
    }

    /**
     * Creates a new instance of ReqResultOverData
     *
     * @param givenCursor the XML part of a query  that describes the given Result for a requested function
     */
    public ReqResultOverData(SMInputCursor givenCursor) {
        this.fid = ReqFunctionOverData.unknownFuncId;
        this.allResultsforFunct = new Vector<ResultAggrStruct>();

        int tmpFuncId = ReqFunctionOverData.unknownFuncId;
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(ReqResultOverData.fidTag.toLowerCase() ) && (tmpFuncId == ReqFunctionOverData.unknownFuncId) )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpFuncId =Integer.parseInt( childInElement2.getText());
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(ReqResultOverData.outTag.toLowerCase() ) ) {
                        this.allResultsforFunct.addElement(new ResultAggrStruct(childInElement));
                    }
                }
            }
            if (tmpFuncId != ReqFunctionOverData.unknownFuncId) {
                this.fid = tmpFuncId;
            }
        }
        catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }
    }

    /**
     * Method mergeWith. Merges two ReqResultOverData objects.
     *
     * @param targetReqRes the target ReqResultOverData to be merged with this object
     */
    public void mergeWith(ReqResultOverData targetReqRes) {
        //
        // Get the associated  Vector<ResultAggrStruct> and search for matching mid/sids for each mid/sid in the targetReqRes. 
        //
        Vector<ResultAggrStruct> targRes = targetReqRes.allResultsforFunct;
        Vector<ResultAggrStruct> myTmpRes = this.allResultsforFunct;

        for (int i = 0; i < targRes.size(); i++) {
            String targ_mid = targRes.elementAt(i).mid;
            String targ_sid = targRes.elementAt(i).sid;
            boolean matchisfound = false;
            for (int j = 0; j < myTmpRes.size(); j++) {
                String myTmp_mid = myTmpRes.elementAt(j).mid;
                String myTmp_sid = myTmpRes.elementAt(j).sid;
//                System.out.println("Comparing " + targ_mid + " with " + myTmp_mid  +" and " + Integer.toString(myTmp_sid) +" with "+ Integer.toString(targ_sid));
                if (myTmp_mid.equals(targ_mid) && (myTmp_sid.equals(targ_sid)) ){
//                    System.out.println("Match on " + targ_mid + "::" + Integer.toString(targ_sid));
                    //match found
                    //
                    // If there is a match then (probably a case of overwriting a default set timed-out field) overwrite its data
                    // with those from the targetReqRes
                    //                     
                    // System.out.println("Merging with other ResultAggrStruct");
                    myTmpRes.set(j, new ResultAggrStruct(targRes.elementAt(i)));
                    matchisfound = true;
                    break;
                }
            }
            //
            // If there is no match then append this ResultAggrStruct to this object's Vector<ResultAggrStruct>.
            //
            if (matchisfound == false) {
                //              System.out.println("NO match for " + targ_mid +"::" + Integer.toString(targ_sid));
                this.allResultsforFunct.addElement(targRes.elementAt(i));
            }
        }
    }

    /**
     * Returns the id for the specific function of this ReqResultOverData object.
     *
     * @return the id for this specific function.
     */
    public int getFid() {
        return fid;
    }

    /**
     * Returns the vector with results for the specific function of this ReqResultOverData object.
     *
     * @return the vector with results for the specific function of this ReqResultOverData object.
     */
    public Vector<ResultAggrStruct> getAllResultsforFunct() {
        return allResultsforFunct;
    }

    /**
     * Returns the tag name of the out tag in the XML structure of this ReqResultOverData object.
     *
     * @return the tag name of the out tag.
     */
    public static String getOutTag() {
        return outTag;
    }

    /**
     * Method createFunctionInfoInDocument:
     *
     * @param document the desired MIME type representation for the query.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement, boolean tempFlag) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(PublicResponseAggrMsg.getRequestedFunctionTag());
            }
            else {
                tmpElementOuter =  document.addElement(PublicResponseAggrMsg.getRequestedFunctionTag());
            }

            tmpElement1 = tmpElementOuter.addElement(ReqResultOverData.fidTag) ;
            if(!tempFlag)    {
                tmpElement1.addCharacters( Integer.toString(this.fid));
            }
            else
            {
                String tmpFuncName = Integer.toString(this.fid);
                tmpElement1.addCharacters( tmpFuncName);            //todo: we could add a method to access the functions name (not each id which is obscure)
            }

            for (int i = 0; i < this.allResultsforFunct.size(); i++) {
                this.allResultsforFunct.get(i).createInfoInDocument(document, tmpElementOuter, tempFlag);
            }

        } catch(Exception e) {
            return;
        }



    }

    /**
     * Method toString:
     * <p/>
     * no parameters
     *
     * @return the XML String representing this requested Function XML fields
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
            createInfoInDocument(doc, null, false);
            doc.closeRoot();
        } catch(Exception e) {
            e.printStackTrace();
            return "";
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
