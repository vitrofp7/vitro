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
 * Model3dStyleNumericCase.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.StringWriter;
import java.util.Vector;

/**
 * <pre>
 * 			&lt;range&gt;
 * 				&lt;From&gt;25&lt;/From&gt;
 * 				&lt;To&gt;200&lt;/To&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;Yellow&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/range&gt;
 * </pre>
 * @author antoniou
 */
public class Model3dStyleNumericCase {
    private Logger logger = Logger.getLogger(Model3dStyleNumericCase.class);
    private String fromValue;
    private String toValue;
    private String iconFilename;
    private String color1;
    private String smallPrefabFilename;
    
    private static final String valueFromTag = "From";
    private static final String valueToTag = "To";
    private static final String iconTag = "icon";
    private static final String color1Tag = "color1";
    private static final String prefabTag = "smprefab";
    
    private static final String defaultFromValue = ""; // means -inf
    private static final String defaultToValue = ""; // means +inf
    private static final String undefinedPrefabFilename = "unknown";
    private static final String undefinedIconFilename = "unknown";
    public static final String UNDEFINEDCOLOR1 = "unknown";
    
    
    /** Creates a new instance of Model3dStyleNumericCase */
    public Model3dStyleNumericCase() {
        this.fromValue =  Model3dStyleNumericCase.defaultFromValue;
        this.toValue =  Model3dStyleNumericCase.defaultToValue;
        this.iconFilename = Model3dStyleNumericCase.undefinedIconFilename;
        this.color1 = Model3dStyleNumericCase.UNDEFINEDCOLOR1;
        this.smallPrefabFilename = Model3dStyleNumericCase.undefinedPrefabFilename;
    }
    
    /** Creates a new instance of Model3dStyleNumericCase */
    public Model3dStyleNumericCase(String givValFrom, String givValTo, String givColor1, String givIconFilename, String givSmallPrefabFilename) {
        
        if(givValFrom == null) 
            this.fromValue = Model3dStyleNumericCase.defaultFromValue;
        else
        {
            try{
                Double.parseDouble(givValFrom);
                this.fromValue =  givValFrom;
            }
            catch(NumberFormatException e)
            {
                this.fromValue = Model3dStyleNumericCase.defaultFromValue;
            }
        }
        
        if(givValTo == null) 
            this.toValue = Model3dStyleNumericCase.defaultToValue;
        else
        {
            try{
                Double.parseDouble(givValTo);
                this.toValue =  givValTo;
            }
            catch(NumberFormatException e)
            {
                this.toValue = Model3dStyleNumericCase.defaultToValue;
            }
        }
        
        if(givColor1==null || !Model3dCommon.isValidColorString(givColor1) )
             this.color1 = Model3dStyleNumericCase.UNDEFINEDCOLOR1;
        else
             this.color1 = givColor1;
        
        if(givIconFilename == null || givIconFilename.equals(""))
            this.iconFilename = Model3dStyleNumericCase.undefinedIconFilename;
        else
            this.iconFilename = givIconFilename;
        
        if(givSmallPrefabFilename == null || givSmallPrefabFilename.equals(""))
            this.smallPrefabFilename = Model3dStyleNumericCase.undefinedPrefabFilename;
        else
            this.smallPrefabFilename = givSmallPrefabFilename;
    }
    
    /**
     * Creates a new instance of Model3dStyleNumericCase
     * @param givenCursor the XML part of the styles file that describes a Style Special Case entry
     */
    public Model3dStyleNumericCase(SMInputCursor givenCursor) {
        this.fromValue =  Model3dStyleNumericCase.defaultFromValue;
        this.toValue =  Model3dStyleNumericCase.defaultToValue;
        this.iconFilename = Model3dStyleNumericCase.undefinedIconFilename;
        this.color1 = Model3dStyleNumericCase.UNDEFINEDCOLOR1;
        this.smallPrefabFilename = Model3dStyleNumericCase.undefinedPrefabFilename;
        
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getValueFromTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.fromValue = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getValueToTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.toValue = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getIconTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.iconFilename = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getColor1Tag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.color1 = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getPrefabTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.smallPrefabFilename = childInElement2.getText();
                            break;
                        }
                    }
                }
            }
        } 
        catch(Exception e) 
        {
            return; // the default (though invalid) values are already set.
        }                
    }
    
    /**
     *
     * @param parElement the parent element (if not the root) in the given XML document.
     */
    public void createInfoInDocument(SMOutputElement parElement) {
        try{
            SMOutputElement tmpElement1;
            SMOutputElement tmpElement2;
            tmpElement1 =  parElement.addElement(this.getValueFromTag() );
            tmpElement1.addCharacters(this.getFromValue()  );
            
            tmpElement1 =  parElement.addElement(this.getValueToTag() );
            tmpElement1.addCharacters(this.getToValue() );

            tmpElement1 =  parElement.addElement(this.getIconTag() );
            tmpElement1.addCharacters(this.getIconFilename() );

            tmpElement1 =  parElement.addElement(this.getColor1Tag() );
            tmpElement1.addCharacters(this.getColor1() );

            tmpElement1 =  parElement.addElement(this.getPrefabTag() );
            tmpElement1.addCharacters(this.getSmallPrefabFilename() );
        }
        catch (Exception e)
        {
            return;
        }
    }
    
    /**
     * Compares two Model3dStyleNumericCase objects.
     * @param targEntry the target Model3dStyleNumericCase to compare to
     * @return true if objects express the same Numeric Case entry, or false otherwise
     */
    public boolean equals(  Model3dStyleNumericCase targEntry) {
        if(this.fromValue == targEntry.fromValue &&
                this.toValue == targEntry.toValue &&
                this.color1.equals(targEntry.color1) &&
                this.smallPrefabFilename.equals(targEntry.smallPrefabFilename) &&
                this.iconFilename.equals(targEntry.iconFilename))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
   
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Style Numeric Case entry's XML fields
     */
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
            outputRootEl = doc.addElement(Model3dStylesEntry.getNumericCaseTag());
            createInfoInDocument(outputRootEl);
            doc.closeRoot();
        } catch(Exception e) {
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
     *
     * Sorts the given ranges in the givenUnsortedVector.
     *
     */
    public static Vector<Model3dStyleNumericCase> fillMissingAndSortNumericRanges(Vector<Model3dStyleNumericCase> givenUnsortedVector) {
        Vector<Model3dStyleNumericCase> toReturnSortedVec = new Vector<Model3dStyleNumericCase>();
        if(givenUnsortedVector != null && givenUnsortedVector.size() >0 ) {
            
            // get a fixed copy of the givenUnsortedVector.
            Vector<Model3dStyleNumericCase> tmpVec = new Vector<Model3dStyleNumericCase>(givenUnsortedVector);
            Vector<Model3dStyleNumericCase> workingVec = new Vector<Model3dStyleNumericCase>();
            //
            // REMOVE from tmpVec those ranges that overlap with previously seen ones (the earlier entires in the vector "win" here).
            //
            workingVec = removeOverlappingRanges(tmpVec);
            // get a fixed copy of the "cleared from overlaps" workingVec.
            tmpVec = new Vector<Model3dStyleNumericCase>(workingVec);
            workingVec.clear();
            //
            // Now that we cleared the overlapping cases, find and add the missing ranges to the tmpVec if any.
            // 
            fillMissingRanges(tmpVec);
            
            // Finally sort all ranges that you have in the  tmpVec
            if(givenUnsortedVector!=null && tmpVec.size() > 0) {
                // ranges are sorted based on their "From" fields
                double tmpCurrSmallerFrom = 0.0; // the real initialization takes place in the for loop
                boolean refFromInitialized = false;
                int candidateElement = -1;
                for(int i=0; i< tmpVec.size(); i++) {
                    if(!refFromInitialized && !tmpVec.elementAt(i).getFromValue().equals("")) // initialize tmpCurrSmallerFrom
                    {
                        tmpCurrSmallerFrom = Double.parseDouble(tmpVec.elementAt(i).getFromValue());
                        refFromInitialized = true;
                    }
                    
                    if(tmpVec.elementAt(i).getFromValue().equals("")) {
                        toReturnSortedVec.addElement(tmpVec.elementAt(i));
                        tmpVec.removeElementAt(i);
                        i = -1; // search again from the start
                        candidateElement = -1;
                        refFromInitialized = false;
                    } else {
                        if(refFromInitialized && (Double.parseDouble(tmpVec.elementAt(i).getFromValue()) <= tmpCurrSmallerFrom) ) {
                            tmpCurrSmallerFrom = Double.parseDouble(tmpVec.elementAt(i).getFromValue());
                            candidateElement = i;
                        }
                        // if we reached the end, we add the smaller candidateElement we found.
                        if((i == tmpVec.size() -1) && candidateElement >= 0) {
                            toReturnSortedVec.addElement(tmpVec.elementAt(candidateElement));
                            tmpVec.removeElementAt(candidateElement);
                            i = -1; // search again from the start
                            candidateElement = -1;
                            refFromInitialized = false;
                        }
                    }
                }
            }
             
        }
        else
        {
            toReturnSortedVec.addElement(new Model3dStyleNumericCase("", "", null, null, null) );
        }
        return toReturnSortedVec;
    }
    
    /**
     * REMOVE from givenVec those ranges that overlap with previously seen ones (the earlier entires in the vector "win" here).
     * 
     */
    private static Vector<Model3dStyleNumericCase> removeOverlappingRanges(Vector<Model3dStyleNumericCase> givenVec)
    {
        //
        // If the range specified is valid but has been already defined or overlaps with a defined range!!!!
        // return false with an alert
        //
        Vector<Model3dStyleNumericCase> toReturnVec = new Vector<Model3dStyleNumericCase>();
        for(int i = 0; i < givenVec.size(); i++) {
            boolean  errorFlag = false;
            // check for overlaps
            for(int j = 0; j < toReturnVec.size(); j++) {
                // ranges are closed right and open left intervals   e.g [x,y)
                // check inf cases first
                 String realFromVal = givenVec.elementAt(i).getFromValue();
                 String realToVal = givenVec.elementAt(i).getToValue();
                 String valFromInBox = toReturnVec.elementAt(j).getFromValue();
                 String valToInBox = toReturnVec.elementAt(j).getToValue();                
                
                if( ( valFromInBox.equals("") &&  valToInBox.equals("")  ) ||
                        ( realFromVal.equals("") && valFromInBox.equals(""))  ||
                        (realToVal.equals("") && valToInBox.equals("")) ||
                        ( realFromVal.equals("") &&  !realToVal.equals("") && !valFromInBox.equals("") && Double.parseDouble(realToVal) >  Double.parseDouble(valFromInBox) ) ||
                        ( valFromInBox.equals("") &&  !valToInBox.equals("") && !realFromVal.equals("") && Double.parseDouble(valToInBox) >  Double.parseDouble(realFromVal) ) ||
                        ( realToVal.equals("") &&  !realFromVal.equals("") && !valToInBox.equals("") && Double.parseDouble(realFromVal) <   Double.parseDouble(valToInBox) ) ||
                        ( valToInBox.equals("") &&  !valFromInBox.equals("") && !realToVal.equals("") && Double.parseDouble(valFromInBox) <  Double.parseDouble(realToVal) ) ) {
                    errorFlag = true;
                    break;
                } else if( !valFromInBox.equals("") && !valToInBox.equals("") && !realToVal.equals("") &&  !realFromVal.equals("") &&
                        ( ( Double.parseDouble(realFromVal) >=  Double.parseDouble(valFromInBox) && Double.parseDouble(realFromVal) <  Double.parseDouble(valToInBox) ) ||
                          ( Double.parseDouble(realToVal) >  Double.parseDouble(valFromInBox) && Double.parseDouble(realToVal) <=  Double.parseDouble(valToInBox) )  ||
                          ( Double.parseDouble(realFromVal) <=  Double.parseDouble(valFromInBox) && Double.parseDouble(realToVal) >=  Double.parseDouble(valToInBox) )   )   ) {
                    errorFlag = true;
                    break;
                }
                
            }
            if(!errorFlag) {
                toReturnVec.addElement(givenVec.elementAt(i));
            }
        }
        return toReturnVec;
    }
    
    /**
     * FIIL IN the ranges that are missing from givenVec in order to cover the full (-inf, +inf) space.
     * 
     */
    private static String globalNumRangeNextFrom;
    private static String globalNumRangeNextTo;
    private static String globalNumRangeNextRangeMode;
    
    private static void fillMissingRanges(Vector<Model3dStyleNumericCase> givenVec)
    {
        if(givenVec != null && givenVec.size() > 0)
        {            
            String realFromVal = givenVec.elementAt(0).getFromValue();
            String realToVal = givenVec.elementAt(0).getToValue();
            
            globalNumRangeNextFrom = "";
            globalNumRangeNextTo = "";
            globalNumRangeNextRangeMode = "goRight";
            if(realToVal.equals(""))
                globalNumRangeNextRangeMode = "goLeft";
            
            
            if(realFromVal.equals("") && realToVal.equals("")) 
            {
                // then nothing is to be filled, we are done.
                return;
            } 
            else 
            {
                if(globalNumRangeNextRangeMode.equals("goRight") ) // search for the next available From value.
                {
                    String refToVal = realToVal;
                    searchNextEntryToTheRight(refToVal, givenVec);
                }
                if(globalNumRangeNextRangeMode.equals("goLeft") )// search for the next available To value. (we don't use an else if because
                {
                    String refFromVal = realFromVal;
                    searchNextEntryToTheLeft(refFromVal, givenVec);
                }
            }
            if(! (globalNumRangeNextFrom.equals("") && globalNumRangeNextTo.equals("") ))
            {
                givenVec.addElement(new Model3dStyleNumericCase(globalNumRangeNextFrom, globalNumRangeNextTo, null, null, null));
                // and redo the same thing until we get both global from and to to be "".
                fillMissingRanges(givenVec);
            }
        }
        return;
    }    
    
    private static void searchNextEntryToTheRight(String refToVal, Vector<Model3dStyleNumericCase> givenVec)
    {
            String candidateEntryFrom = "";
            String candidateEntryTo = "";
            
            globalNumRangeNextFrom = refToVal;
            globalNumRangeNextTo = "";
            
            if(givenVec==null)
                return;
            
            for(int j = 0; j < givenVec.size(); j++)
            {
                if(!refToVal.equals("") && !givenVec.elementAt(j).getFromValue().equals("") && Double.parseDouble(refToVal) <=   Double.parseDouble(givenVec.elementAt(j).getFromValue()) )
                {
                    if( globalNumRangeNextTo.equals("") || 
                            (!globalNumRangeNextTo.equals("") && Double.parseDouble(globalNumRangeNextTo) > Double.parseDouble(givenVec.elementAt(j).getFromValue())  ) )
                    {
                        globalNumRangeNextTo = givenVec.elementAt(j).getFromValue();
                        candidateEntryFrom = givenVec.elementAt(j).getFromValue();
                        candidateEntryTo = givenVec.elementAt(j).getToValue();
                    }
                }
            }
            if(refToVal.equals(globalNumRangeNextTo) && candidateEntryTo.equals("") )
            {
                    globalNumRangeNextRangeMode = "goLeft";
            }
            else if(refToVal.equals(globalNumRangeNextTo) && !candidateEntryTo.equals("") )
            {// recursive call with candidateEntryTo as refToVal.
                    searchNextEntryToTheRight(candidateEntryTo, givenVec);
            }        
    }
    
    private static void searchNextEntryToTheLeft(String refFromVal, Vector<Model3dStyleNumericCase> givenVec)
    {
            String candidateEntryFrom = "";
            String candidateEntryTo = "";
            globalNumRangeNextFrom = "";
            globalNumRangeNextTo = refFromVal;

            if(givenVec==null)
                return;
            
            for(int j = 0; j < givenVec.size(); j++)
            {
                if(!refFromVal.equals("") && !givenVec.elementAt(j).getToValue().equals("") && Double.parseDouble(refFromVal) >=  Double.parseDouble(givenVec.elementAt(j).getToValue()) )
                {
                    if( globalNumRangeNextFrom.equals("") || 
                            (!globalNumRangeNextFrom.equals("") && Double.parseDouble(globalNumRangeNextFrom) <  Double.parseDouble(givenVec.elementAt(j).getToValue())  ) )
                    {
                        globalNumRangeNextFrom = givenVec.elementAt(j).getToValue();
                        candidateEntryFrom = givenVec.elementAt(j).getFromValue();
                        candidateEntryTo = givenVec.elementAt(j).getToValue();
                    }
                }
            }    
            if(refFromVal.equals(globalNumRangeNextFrom) && candidateEntryFrom.equals("") )
            {
                globalNumRangeNextFrom = "";
                globalNumRangeNextTo = "";
                return;
            }
            else if(refFromVal.equals(globalNumRangeNextFrom) && !candidateEntryFrom.equals("") )
            {// recursive call with candidateEntryFrom as refFromVal.
                searchNextEntryToTheLeft(candidateEntryFrom,  givenVec);
            }        
    }
    /**
     *
     * Tests for a match with a given double value
     */
    public boolean belongsInside(double testValue)
    {
        //try{
            if(( this.fromValue.equals("") && this.toValue.equals("") )||
                    (this.fromValue.equals("") && !this.toValue.equals("") && testValue < Double.parseDouble(this.toValue) ) ||
                    (this.toValue.equals("") && !this.fromValue.equals("") && testValue >= Double.parseDouble(this.fromValue) ) ||
                    (!this.fromValue.equals("") && !this.toValue.equals("") && testValue >= Double.parseDouble(this.fromValue) && testValue < Double.parseDouble(this.toValue))  )
            {
                return true;
            } 
            else
                return false;
        //}       
        //catch (Exception e)
        //{
        //    return false;
        //}
    }
    
    public String getColor1() {
        return color1;
    }

    public String getDefaultFromValue() {
        return defaultFromValue;
    }

    public String getDefaultToValue() {
        return defaultToValue;
    }

    public String getFromValue() {
        return fromValue;
    }

    public String getToValue() {
        return toValue;
    }

    public String getIconFilename() {
        return iconFilename;
    }

    public String getSmallPrefabFilename() {
        return smallPrefabFilename;
    }

    
    public static String getValueFromTag() {
        return valueFromTag;
    }

    public static String getValueToTag() {
        return valueToTag;
    }

    public static String getColor1Tag() {
        return color1Tag;
    }

    public static String getIconTag() {
        return iconTag;
    }

    public static String getPrefabTag() {
        return prefabTag;
    }

    
    
}
