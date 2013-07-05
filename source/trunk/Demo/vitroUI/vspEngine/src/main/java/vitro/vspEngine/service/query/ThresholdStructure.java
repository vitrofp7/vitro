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
 * ThresholdStructure.java
 *
 */

package vitro.vspEngine.service.query;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

/**
 * Holds a Threshold object that will be used with the Required Function to be applied on the data returned from the query
 *
 * @author antoniou
 */
public class ThresholdStructure {
    private Logger logger = Logger.getLogger(QueryDefinition.class);
    private String upperBound;
    boolean upperBoundClosed;
    boolean upperBoundSet;

    private String lowerBound;
    boolean lowerBoundClosed;
    boolean lowerBoundSet;
    
    public static final String THRESHOLD_EQUAL = "Equal";
    public static final String THRESHOLD_LARGER = "Larger";
    public static final String THRESHOLD_LARGEROREQUAL = "LargerOrEqual";
    public static final String THRESHOLD_LOWER = "Lower";
    public static final String THRESHOLD_LOWEROREQUAL = "LowerOrEqual";
    public static final String THRESHOLD_UNKNOWN = "unknown";

    
    static final String m_validThresRelations[] = {THRESHOLD_LARGER, THRESHOLD_LARGEROREQUAL, THRESHOLD_LOWER, THRESHOLD_LOWEROREQUAL, THRESHOLD_EQUAL, THRESHOLD_UNKNOWN};

    static final String thresholdFieldListTag ="thresholdFieldList"; // TODO: for now it is only used for standalone doc creation (testing purposes)
    static final String thresholdFieldTag ="thresholdField";
    static final String thresholdRelationTag ="thresholdRelation";
    static final String thresholdValueTag ="thresholdValue";
    /**
     * Creates a default empty threshold object
     */
    public ThresholdStructure() {
        upperBoundSet = false;
        lowerBoundSet = false;
        upperBound = "";
        lowerBound = "";
    }

    /**
     * Creates a threshold object from supplied arguments
     *
     * @param threshHM is a HashMap that contains Strings with Threshold Relations mapped to the pertinent values. Each Key String in the Vector
     *                 must be a valid description for a Threshold relation (e.g. Larger, LargerOrEqual, Equal, Lower, LowerOrEqual)
     */
    public ThresholdStructure(HashMap<String, String> threshHM) {
        upperBoundSet = false;
        lowerBoundSet = false;
        upperBound = "";
        lowerBound = "";
        Set tmpset1 = threshHM.keySet();
        Iterator it1 = tmpset1.iterator();
        while (it1.hasNext()) {
            String tmpkey = (String) it1.next();
            if (isValidThresholdRelation(tmpkey)) {
                dealWithRelationAndValue(tmpkey, (String) threshHM.get(tmpkey));
            }
        }
    }

    private boolean isValidThresholdRelation(String desc) {
        int i;
        for (i = 0; i < m_validThresRelations.length - 1; i++) // -1 because we excluded the "unknown" final entry
        {
            if (desc.equals(m_validThresRelations[i]))
                return true;
        }
        System.out.println("An invalid threshold relation was specified!!!");
        return false;
    }
//
//    // Fixed this. we assume that the relationship is a value of a thresholdRelation element
    /// TODO: Careful: The thresholdField tag has max occurs == "2" in a requested Function.
    //          But we only keep ONE Threshold Structure for a function, which is updated when called multiple times <=2 for a thresholdField tag
    // TODO: FIX THIS LATER (should be within a thresholdFieldsVec tag and each thresholdField should have one Relation and one Value.
    //
    public void parseBound(SMInputCursor givenCursor) {
        String tmpRelationType = "";
        String tmpValue = "";

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(ThresholdStructure.thresholdRelationTag.toLowerCase()) )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpRelationType = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(ThresholdStructure.thresholdValueTag.toLowerCase())  )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpValue = childInElement2.getText();
                                break;
                            }
                        }
                    }
                }
            }
            if(!tmpRelationType.isEmpty() && !tmpValue.isEmpty())
                dealWithRelationAndValue(tmpRelationType, tmpValue);
        }
        catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }
    }


    private void dealWithRelationAndValue(String relation, String txtValue) {
        if ((relation.equals(ThresholdStructure.THRESHOLD_LARGER) || relation.equals(ThresholdStructure.THRESHOLD_LARGEROREQUAL)) && !isLowerBoundSet()) {
            this.lowerBound = txtValue;
            lowerBoundSet = true;
            lowerBoundClosed = (relation.equals(ThresholdStructure.THRESHOLD_LARGER)) ? false : true;
        } else if ((relation.equals(ThresholdStructure.THRESHOLD_LOWER) || relation.equals(ThresholdStructure.THRESHOLD_LOWEROREQUAL)) && !isUpperBoundSet()) {
            this.upperBound = txtValue;
            upperBoundSet = true;
            upperBoundClosed = (relation.equals(ThresholdStructure.THRESHOLD_LOWER)) ? false : true;
        } else if (relation.equals(ThresholdStructure.THRESHOLD_EQUAL)) {
            upperBoundSet = true;
            upperBoundClosed = true;
            lowerBoundSet = true;
            lowerBoundClosed = true;
            this.upperBound = txtValue;
            this.lowerBound = this.getUpperBound();
        }
    }

    /**
     * Creates XML structured info on this Threshold object, under the parent Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document. Probably the <pre><ReqFunction></pre>, but it could also be null.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElementOuter01 = null;               // for upper bound or equality
        SMOutputElement tmpElementOuter02 = null; // max 2 times in a requested function  (for lower bound
        SMOutputElement tmpElement1;

        try{
            if (this.isSetThreshold()) {
                if (parElement != null) {
                    tmpElementOuter = parElement;
                }
                else {
                    tmpElementOuter = document.addElement(ThresholdStructure.thresholdFieldListTag);
                }
                if(this.isUpperBoundSet() ) {
                        tmpElementOuter01 = tmpElementOuter.addElement(ThresholdStructure.thresholdFieldTag);
                }
                // not else if. we need to check both these cases, and both could be true
                if(this.isLowerBoundSet() && ! (this.isUpperBoundSet() && this.upperBound.equalsIgnoreCase(this.lowerBound) ) ){
                        tmpElementOuter02 = tmpElementOuter.addElement(ThresholdStructure.thresholdFieldTag);
                }


                if(this.isUpperBoundSet() && this.isLowerBoundSet() && this.upperBound.equalsIgnoreCase(this.lowerBound))
                {
                    tmpElement1 =  tmpElementOuter01.addElement(ThresholdStructure.thresholdRelationTag );
                    tmpElement1.addCharacters( ThresholdStructure.THRESHOLD_EQUAL);
                    tmpElement1 =  tmpElementOuter01.addElement(ThresholdStructure.thresholdValueTag );
                    tmpElement1.addCharacters( this.getUpperBound());            //in equality in doesn't matter which of the two we use for value
                }
                else
                {
                    if(this.isUpperBoundSet())
                    {
                        String relationStr =  ThresholdStructure.THRESHOLD_LOWER;
                        if(isUpperBoundClosed())
                        {
                            relationStr = ThresholdStructure.THRESHOLD_LOWEROREQUAL;
                        }

                        tmpElement1 =  tmpElementOuter01.addElement(ThresholdStructure.thresholdRelationTag );
                        tmpElement1.addCharacters( relationStr);

                        tmpElement1 =  tmpElementOuter01.addElement(ThresholdStructure.thresholdValueTag );
                        tmpElement1.addCharacters( this.getUpperBound());

                    }
                    if(this.isLowerBoundSet())
                    {
                        String relationStr =  ThresholdStructure.THRESHOLD_LARGER;
                        if(this.isLowerBoundClosed())
                        {
                            relationStr = ThresholdStructure.THRESHOLD_LARGEROREQUAL;
                        }

                        tmpElement1 =  tmpElementOuter02.addElement(ThresholdStructure.thresholdRelationTag );
                        tmpElement1.addCharacters( relationStr);


                        tmpElement1 =  tmpElementOuter02.addElement(ThresholdStructure.thresholdValueTag );
                        tmpElement1.addCharacters( this.getLowerBound());
                    }
                }
            }

        } catch(Exception e) {
            System.out.println("error");
            e.printStackTrace();
            e.getMessage();
            return;
        }
    }



    public boolean isUpperBoundSet() {
        return upperBoundSet;
    }

    public boolean isUpperBoundClosed() {
        return upperBoundClosed;
    }

    public boolean isLowerBoundSet() {
        return lowerBoundSet;
    }

    public boolean isLowerBoundClosed() {
        return lowerBoundClosed;
    }

    public boolean isSetThreshold() {
        return (isUpperBoundSet() || isLowerBoundSet());
    }

    /**
     * Compares two ThresholdStructure objects
     *
     * @param targetThres the target ThresholdStructure to compare to
     * @return true if objects express the same Threshold, or false otherwise
     */
    public boolean equals(ThresholdStructure targetThres) {
        if (!this.isSetThreshold() && !targetThres.isSetThreshold())
            return true;

        if ((this.isSetThreshold() && !targetThres.isSetThreshold()) ||
                (!this.isSetThreshold() && targetThres.isSetThreshold()) ||
                (this.isSetThreshold() && this.isLowerBoundSet() && !targetThres.isLowerBoundSet()) ||
                (this.isSetThreshold() && !this.isLowerBoundSet() && targetThres.isLowerBoundSet()) ||
                (this.isSetThreshold() && this.isUpperBoundSet() && !targetThres.isUpperBoundSet()) ||
                (this.isSetThreshold() && !this.isUpperBoundSet() && targetThres.isUpperBoundSet())) {
            return false;
        }
        if ((this.isLowerBoundSet() && this.isLowerBoundClosed() && !targetThres.isLowerBoundClosed()) ||
                (this.isLowerBoundSet() && !this.isLowerBoundClosed() && targetThres.isLowerBoundClosed()) ||
                (this.isLowerBoundSet() && this.isUpperBoundClosed() && !targetThres.isUpperBoundClosed()) ||
                (this.isLowerBoundSet() && !this.isUpperBoundClosed() && targetThres.isUpperBoundClosed())) {
            return false;
        }

        if (this.isLowerBoundSet() && !this.lowerBound.equalsIgnoreCase(targetThres.lowerBound) ||
                this.isUpperBoundSet() && !this.upperBound.equalsIgnoreCase(targetThres.upperBound)) {
            return false;
        }
        // at the end
        return true;
    }
    
    public static void main(String[] args) throws IOException {
        HashMap<String, String> threshHM = new HashMap<String, String>();
        threshHM.put( ThresholdStructure.THRESHOLD_EQUAL, "12222");
        try{
            ThresholdStructure threshTmp = new ThresholdStructure(threshHM);
            if(threshTmp.upperBoundSet)
                System.out.println("Upper Bound: " + threshTmp.getUpperBound());
            if(threshTmp.lowerBoundSet)
                System.out.println("Lower Bound: " + threshTmp.getLowerBound());
            System.out.println("output: \n" + threshTmp.toString());

        }catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
                
    }


    public String toString() {
        StringWriter outStringWriter = new StringWriter();
        WstxOutputFactory fout = new WstxOutputFactory();
        fout.configureForXmlConformance();
        SMOutputDocument doc = null;
        SMOutputElement outputRootEl = null;
        try{
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            createInfoInDocument(doc, null);
            doc.closeRoot();
        } catch(Exception e) {
            return e.getMessage();
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
     * @return the upperBound
     */
    public String getUpperBound() {
        return upperBound;
    }

    /**
     * @return the lowerBound
     */
    public String getLowerBound() {
        return lowerBound;
    }
}
        
