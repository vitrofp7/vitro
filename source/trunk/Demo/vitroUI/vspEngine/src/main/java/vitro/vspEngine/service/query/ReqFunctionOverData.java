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
 * ReqFunctionOverData.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package vitro.vspEngine.service.query;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;

import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * This holds an object that represents the requested Function to be applied over the data returned from a query
 * It can be obtained from a valid XML description
 * and it can be used to create a requested Function XML description.
 * <p/>
 * TODO: Correct this schema. it is not consistent with the current version of threshold field
 * Typically we should parse something like:
 * <pre>
 * &lt;reqFunction&gt;
 *      &lt;id&gt;1&lt;/id&gt;
 *      &lt;description&gt;Average of Values&lt;/description&gt;
 *      &lt;timePeriod&gt;
 *      &lt;from&gt;timestamp_x&lt;/from&gt;
 *      &lt;to&gt;timestamp_y&lt;/to&gt;
 *      &lt;/timePeriod&gt;
 *      &lt;thresholdField&gt;
 *          &lt;Larger&gt;33&lt;/Larger&gt;
 *          &lt;LowerOrEqual&gt;73&lt;/LowerOrEqual&gt;
 *      &lt;/thresholdField&gt;
 * &lt;/reqFunction&gt;
 * <p/>
 * The values for the "from" and "to" elements can be identical, to indicate a specific moment in time.
 * Only the root "reqFunction" and the "description" element are mandatory
 * </pre>
 *
 * @author antoniou
 */
public class ReqFunctionOverData {

    private Logger logger = Logger.getLogger(ReqFunctionOverData.class);
    private static final String descriptionTag = "description";
    private static final String idTag = "id";
    private static final String timePeriodTag = "timePeriod";
    private static final String thresholdFieldTag = "thresholdField";

    public static final int unknownFuncId = -1;
    public static final String unknownFunc = "unknown";
    public static final String avgFunc = "Average of Values";
    public static final String maxFunc = "Maximum Value";
    public static final String minFunc = "Minimum Value";
    public static final String lastValFunc = "Last Value";
    public static final String histValFunc = "History of Values";
    public static final String setValFunc = "Set Value";

    public static final String avgFuncAbbr = "AVG";
    public static final String maxFuncAbbr = "MAX";
    public static final String minFuncAbbr = "MIN";
    public static final String lastValFuncAbbr = "Last";
    public static final String histValFuncAbbr = "HISTORY";
    public static final String setValFuncAbbr = "SET";

    //public static final String NODE_LEVEL_PREFIX  = "NL";
    public static final String GW_LEVEL_PREFIX = "gwlevel";
    public static final String GW_LEVEL_SEPARATOR = "_";
    public static final String ruleRuleBinaryAndFunc = "Rule Binary AND";
    public static final String ruleRuleIfThenFunc = "Rule IF THEN";
    
    //valid functions FOR NODE LEVEL operations
    static final String m_validFunctions[] = {ReqFunctionOverData.avgFunc,
            ReqFunctionOverData.maxFunc,
            ReqFunctionOverData.minFunc,
            ReqFunctionOverData.lastValFunc,
            ReqFunctionOverData.histValFunc,
            ReqFunctionOverData.setValFunc,
            ReqFunctionOverData.unknownFunc
            };
    
    static final String m_gwLevelValidFunctions[] = {ReqFunctionOverData.avgFunc,
            ReqFunctionOverData.maxFunc,
            ReqFunctionOverData.minFunc,
            ReqFunctionOverData.ruleRuleBinaryAndFunc,
            ReqFunctionOverData.ruleRuleIfThenFunc
            };

    static final String m_validXMLElements[] = {ReqFunctionOverData.descriptionTag,
            ReqFunctionOverData.idTag,
            ReqFunctionOverData.timePeriodTag,
            ReqFunctionOverData.thresholdFieldTag};

    private String m_funcDesc;
    private int m_funcId;
    private TimeIntervalStructure requiredTimeInterval;
    private ThresholdStructure requiredThresholds;


    /**
     * Creates a new instance of ReqFunctionOverData from the given parameters
     *
     * @param funcDesc The String description of the function. It must have one of the allowed values
     * @param times    The TimeIntervalStructure argument of the function
     * @param thresh   The ThresholdStructure argument of the function
     */
    public ReqFunctionOverData(String funcDesc, int funcId, TimeIntervalStructure times, ThresholdStructure thresh) {
        this.m_funcDesc = ReqFunctionOverData.unknownFunc;
        if (!funcDesc.equals("") && (isValidReqFunct(funcDesc) || ReqFunctionOverData.isValidGatewayReqFunct(funcDesc))) {
            this.m_funcDesc = funcDesc;
        }
        this.m_funcId = funcId;
        if (times == null) {
            this.requiredTimeInterval = new TimeIntervalStructure();
        } else {
            this.requiredTimeInterval = times;
        }
        if (thresh == null) {
            this.requiredThresholds = new ThresholdStructure();
        } else {
            this.requiredThresholds = thresh;
        }
    }

    /**
     * Creates a new instance of ReqFunctionOverData
     *
     * @param givenCursor the XML part of a query (As a TextElement) that describes the required Function
     */
    public ReqFunctionOverData(SMInputCursor givenCursor) {
        this.m_funcDesc = ReqFunctionOverData.unknownFunc;
        this.m_funcId = ReqFunctionOverData.unknownFuncId;
        this.requiredTimeInterval = new TimeIntervalStructure();
        this.requiredThresholds = new ThresholdStructure();

        String tmpFuncDesc = "";

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(ReqFunctionOverData.descriptionTag.toLowerCase() ) && tmpFuncDesc.equals("") )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpFuncDesc = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(ReqFunctionOverData.idTag.toLowerCase() ) ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.m_funcId = Integer.parseInt(childInElement2.getText());
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(ReqFunctionOverData.timePeriodTag.toLowerCase() ) ) {
                        this.requiredTimeInterval.parseTimeInterval(childInElement);
                    }
                    else if (childInElement.getLocalName().toLowerCase().equals(ReqFunctionOverData.thresholdFieldTag.toLowerCase() ) ) {
                        this.requiredThresholds.parseBound(childInElement);
                    }
                }
            }
            if (!tmpFuncDesc.equals("") && (isValidReqFunct(tmpFuncDesc) || ReqFunctionOverData.isValidGatewayReqFunct(tmpFuncDesc)) ) {
                m_funcDesc = tmpFuncDesc;
            }
        }
        catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }
    }

    //valid functions FOR NODE LEVEL operations
    private boolean isValidReqFunct(String funcName) {
        int i;
        //logger.debug("Checking!!!" + funcName);
        for (i = 0; i < m_validFunctions.length - 1; i++) // -1 because we excluded the "unknown" final entry
        {
            if (funcName.equalsIgnoreCase(m_validFunctions[i]))
                return true;
            /*else{     // extra check for case, we have encapsulated it and appended a suffix to differentiate from identical function of the same name
                String[] descriptionTokens = funcName.split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                // eg example NL_Last Value_2
                //logger.debug("descriptionTokens[0] !!!" + descriptionTokens[0]);
                if( descriptionTokens !=null && descriptionTokens.length > 1
                        && ((descriptionTokens[0].equalsIgnoreCase(ReqFunctionOverData.NODE_LEVEL_PREFIX ) && descriptionTokens[1].equalsIgnoreCase(m_validFunctions[i]) ) ))
                {
                    return true;
                }
            } */
        }
        logger.error("An invalid node level function was specified!!!");
        return false;
    }

    
    //valid functions FOR GATEWAY LEVEL operations
    static public boolean isValidGatewayReqFunct(String funcName) {
        int i;
        int lenOfValidPrefix = 0;
        for (i = 0; i < m_gwLevelValidFunctions.length; i++)
        {
            StringBuilder tmpValidPrefixBuild = new StringBuilder();
            tmpValidPrefixBuild.append(ReqFunctionOverData.GW_LEVEL_PREFIX);
            tmpValidPrefixBuild.append( ReqFunctionOverData.GW_LEVEL_SEPARATOR);
            tmpValidPrefixBuild.append(m_gwLevelValidFunctions[i]);

            lenOfValidPrefix = tmpValidPrefixBuild.toString().length();
            if (funcName.length() >= lenOfValidPrefix && funcName.substring(0, lenOfValidPrefix).equals(tmpValidPrefixBuild.toString()))
                return true;
        }
        System.out.println("An invalid gateway level function was specified!!!");
        return false;
    }

    /**
     * @param document   the desired MIME type representation for the query.
     * @param parElement the parent element (if not the root) in the given XML document. Null means the root element.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(PublicQueryAggrMsg.getRequestedFunctionTag());
            }
            else {
                tmpElementOuter =  document.addElement(PublicQueryAggrMsg.getRequestedFunctionTag());
            }

            tmpElement1 = tmpElementOuter.addElement(ReqFunctionOverData.descriptionTag) ;
            tmpElement1.addCharacters(  this.getfuncName());

            tmpElement1 = tmpElementOuter.addElement(ReqFunctionOverData.idTag) ;
            tmpElement1.addCharacters(  Integer.toString(this.getfuncId()));

            if (this.requiredThresholds != null) {
                this.requiredThresholds.createInfoInDocument(document, tmpElementOuter);
            }
            if (this.requiredTimeInterval != null) {
                this.requiredTimeInterval.createInfoInDocument(document, tmpElementOuter);
            }

        } catch(Exception e) {
            return;
        }
        return;
    }

    /**
     * Returns the function description in a String
     *
     * @return The function description
     */
    public String getfuncName() {

            return this.m_funcDesc;
    }

    /**
     * Should return the inner functional name of a node level or gateway level function
     * @return
     */
    /*
    public String getfuncInnerTokenName() {
        String[] descriptionTokens = this.m_funcDesc.split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        // Extra check ONLY for Node Level functions!
        // eg example NL_Last Value_2 or Last Value_2
        //logger.debug("descriptionTokens[0] !!!" + descriptionTokens[0]);
        if( descriptionTokens !=null && descriptionTokens.length > 1
                && (
                        (descriptionTokens[0].equalsIgnoreCase(ReqFunctionOverData.NODE_LEVEL_PREFIX ) && !descriptionTokens[1].isEmpty() )
                        || (descriptionTokens[0].equalsIgnoreCase(ReqFunctionOverData.GW_LEVEL_PREFIX) && !descriptionTokens[1].isEmpty() )
                    ))
        {
            return descriptionTokens[1];
        }
        else
            return this.m_funcDesc;
    } */

    /**
     * Sets the function description 
     * @param pfuncDesc the function description/Name. This takes specific pre-defined values.
     */
    public void setfuncName(String pfuncDesc) {
        this.m_funcDesc = pfuncDesc;
    }    
    
    /**
     * Returns the function unique Id within this query definition
     *
     * @return The function unique id
     */
    public int getfuncId() {
        return m_funcId;
    }

    /**
     * Sets the function unique Id within this query definition
     *
     * @param funcId The function unique id
     */
    public void setfuncId(int funcId) {
        this.m_funcId = funcId;
    }

    public ThresholdStructure getRequiredThresholds(){
        return this.requiredThresholds;
    }
            
    

    /**
     * Compares two ReqFunctionOverData objects. The ids are not compared, since we are only interested in the function's functionality/parameters
     *
     * @param targetFunc the target ReqFunctionOverData to compare to
     * @return true if objects express the same Requested Function, or false otherwise
     */
    public boolean equals(ReqFunctionOverData targetFunc) {
        if (!this.getfuncName().equals(targetFunc.getfuncName())) {
            return false;
        }
        if ((this.requiredTimeInterval == null && targetFunc.requiredTimeInterval != null) ||
                (this.requiredTimeInterval != null && targetFunc.requiredTimeInterval == null) ||
                (!this.requiredTimeInterval.equals(targetFunc.requiredTimeInterval))) {
            return false;
        }
        if ((this.requiredThresholds == null && targetFunc.requiredThresholds != null) ||
                (this.requiredThresholds != null && targetFunc.requiredThresholds == null) ||
                (!this.requiredThresholds.equals(targetFunc.requiredThresholds))) {
            return false;
        }
        // at the end
        return true;
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
            createInfoInDocument(doc, null);
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

    /**
     * (To do) This code is UNUSED for the time being and will change...
     * (To do) Change/ complete the code here.
     * For now it skips the proper Function calculation and
     * just returns the latest value read!!!
     * Careful! the v Vector has always some elements (even with "No reading" values or no time interval info).
     * It must never be null or of 0 (zero) size!!!!! THIS SHOULD BE TAKEN CARE FROM THE MIDDLEWDATACON INTERFACE!
     *
     * @param motesAndTheirSensorHM A hashmap of mote ids mapped to their sensors (those of them that are involved in the current Query)
     * @param v                     A Vector of ResultAggrStruct, that was filled by the MiddleWDataCon object with info retrieved from the database per Sensor Model Id. These data
     *                              should be further processed here, according to the desired function.
     * @param reqFunctionVec        A Vector with the desired functions to be applied over the data of the previous argument
     * @return a Vector of ReqResultOverData structures, that will construct the PublicResponseAggrMsg message.
     */
    public static Vector<ReqResultOverData> executeFuctOverData(HashMap<String, Vector<Integer>> motesAndTheirSensorHM, Vector<ResultAggrStruct> v, Vector<ReqFunctionOverData> reqFunctionVec) {
        /**
         *
         * + There should be data info inside the entries of the v Vector so that we can calculate functions like Average/Min/Max
         * + What do we do if the value is not numeric. Would the API allow such a value to reach this call?
         *
         * + if no timeperiod is set in a function, then we assume the Latest Value.
         *
         * 1) Go through Vector of Functions. For each function do what you have to do with the Vector of values PER SENSOR  AND if NEEDED overall 
         * and
         * 2) create a new entry of ReqResultOverData in the returned Vector Vector<ReqResultOverData>
         *
         *
         */
        Vector<ReqResultOverData> retVecofResults = new Vector<ReqResultOverData>();
        for (int i = 0; i < reqFunctionVec.size(); i++) {
            if (reqFunctionVec.get(i).getfuncName() == ReqFunctionOverData.unknownFunc) {
                continue; // return "UNDEFINED"; (?????)
            }
            //if(dataType == "double" || dataType == "float" || dataType == "int" || dataType == "long" || dataType == "short" )
            //{
            //        // numeric value
            //        
            //}    
            //else if (reqFunctionVec.get(i).m_func.equals("Last Value") ||  reqFunctionVec.get(i).m_func.equals("History of Values") )
            //{ // if data value is not numeric then no numeric functions can be applied
            //    // handle History ?????
            //}
            else {
                // for a start, we assume that we always will return the latest value PER SENSOR
                Set<String> tmpset1 = motesAndTheirSensorHM.keySet();
                Iterator<String> it1 = tmpset1.iterator();
                Vector<ResultAggrStruct> vRas = new Vector<ResultAggrStruct>();
                while (it1.hasNext()) {
                    String fullMoteId = (String) it1.next();
                    Vector<Integer> tmpVecSmIds = motesAndTheirSensorHM.get(fullMoteId);

                    for (int k = 0; k < tmpVecSmIds.size(); k++) // Scan through ALL Sensors that we care about
                    {
                        ResultAggrStruct mostRecentValue = new ResultAggrStruct(SmartNode.invalidId, SensorModel.invalidId, ReqResultOverData.specialValueNoReading, 0, null); //dummy initialization. for the compiler to SHUT UP.
                        // it is safely overriden in the code (I think).
                        if (v.size() > 0) // this is always true, AND SHOULD BE TAKEN CARE FROM THE MIDDLEWDATACON INTERFACE!
                        {
                            int firstmatch = 0;
                            for (int j = 0; j < v.size(); j++) // Scan through ALL results and match with the current Sensor and MoteID we care about
                            {
                                if (tmpVecSmIds.get(k).toString().equals( v.get(j).sid) && fullMoteId.equals(v.get(j).mid)) {     //TODO: check if correct now that sid is string
                                    if (firstmatch == 0) {
                                        mostRecentValue = v.get(j);  // referent entry
                                    } else
                                    if (v.get(j).tis.isAnyTimestampDefined() && (v.get(j).tis.timeperiod_from.after(mostRecentValue.tis.timeperiod_from))) {
                                        mostRecentValue = v.get(j);
                                    }
                                    firstmatch++;
                                }
                            }
                        }
                        vRas.addElement(mostRecentValue);
                    } // go to next sensor                    
                } // go to next MoteId  
                retVecofResults.addElement(new ReqResultOverData(reqFunctionVec.get(i).getfuncId(), vRas));
            }
        }// go to next Function

        return retVecofResults;

    }
}

