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
 * Model3dStylesEntry.java
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
 *
 *<pre>
 * 	&lt;style&gt;
 * 		&lt;id&gt;&lt;/id&gt;
 * 		&lt;suggestedforCapability&gt; 		&lt;------	Optional. Recommendation so that this style can better depict readings for this 
 * 												capability. This implies that the styles are created from a GUI that knows 
 * 												the available list of capabilities, and their exact naming.
 * 			Thermistor
 * 		&lt;/suggestedforCapability&gt; 
 * 		&lt;color&gt;#FFFFFF&lt;/color&gt;
 * 		&lt;icon&gt;sssss.ico&lt;/icon&gt;
 *              &lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 		&lt;specialList&gt;
 * 			&lt;special&gt;
 * 				&lt;value&gt;Not supported&lt;/value&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;white&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/special&gt;		
 * 			&lt;special&gt;
 * 				&lt;value&gt;No reading&lt;/value&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;white&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/special&gt;
 * 			&lt;special&gt;
 * 				&lt;value&gt;Timed out&lt;/value&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;grey&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/special&gt;
 *   		&lt;/specialList&gt;
 * 		&lt;numericRangesList&gt;	
 * 			&lt;range&gt;   						&lt;------ Ranges within the same Style entry should not overlap. `From` field should be smaller than the `To` field.
 * 												The interval is essentially a [From, To) space.
 * 				&lt;From&gt;12&lt;/From&gt;
 * 				&lt;To&gt;25&lt;/To&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;Red&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/range&gt;
 * 			&lt;range&gt;
 * 				&lt;From&gt;25&lt;/From&gt;
 * 				&lt;To&gt;200&lt;/To&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;Yellow&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/range&gt;
 * 			.
 * 			.
 * 		&lt;/numericRangesList&gt;	
 * 	&lt;/style&gt;
 *</pre>
 * @author antoniou
 */
public class Model3dStylesEntry {
    private Logger logger = Logger.getLogger(Model3dStylesEntry.class);
    String styleId;
    String corrCapability;
    String globalColor; /** default is white (#FFFFFF)  */
    String globalIconFile; /** optional  */
    String globalSmallPrefabFile; /** optional  */
    Vector<Model3dStyleSpecialCase> specialCasesVec;
    Vector<Model3dStyleNumericCase> numericCasesVec;
    
    public static final String roomPlacemarkIconFilename = "placemark_circle.png";

    public static final String defaultUnavailableIconFilename = "no_16_true.ico";
    public static final String defaultNoReadingIconFilename = "no_small.png";
    public static final String defaultPendingIconFilename = "run.ico";
    public static final String defaultTimedOutIconFilename = "clock32.ico";
    
    
    private static final String idTag = "id";    
    private static final String forCapabilityTag = "suggestedForCapability";    
    private static final String globalColorTag = "color";    
    private static final String globalIconTag = "icon";    
    private static final String globalPrefabTag = "smprefab";
    private static final String specialCasesListTag = "specialList";
    private static final String numericRangesListTag = "numericRangesList";
    private static final String specialCaseTag = "special";
    private static final String numericCaseTag = "range";
    
    private static final String undefinedStyleId  = "unknown";
    private static final String undefinedCorrCapability = "unknown";
    public static final String undefinedPrefabFilename = "unknown";
    public static final String undefinedIconFilename = "unknown";
    private static final String defaultGlobalColor = "#FFFFFF";
    
    /** Creates a new default (and invalid) instance of Model3dStylesEntry */
    public Model3dStylesEntry() {
        this.styleId = Model3dStylesEntry.undefinedStyleId;
        this.corrCapability = Model3dStylesEntry.undefinedCorrCapability;
        this.globalColor = Model3dStylesEntry.defaultGlobalColor;
        this.globalIconFile = Model3dStylesEntry.undefinedIconFilename;
        this.globalSmallPrefabFile = Model3dStylesEntry.undefinedPrefabFilename;
        this.specialCasesVec= new Vector<Model3dStyleSpecialCase>();
        this.numericCasesVec= new Vector<Model3dStyleNumericCase>();
        
    }

    /** Creates a new instance of Model3dStylesEntry from the given arguments */
    public Model3dStylesEntry(String givStyleId, String givCorrCapability, String givGlobalColor, String givGlobalIconFile, String givGlobalSmallPrefabFile, Vector<Model3dStyleSpecialCase> givSpecialCasesVec, Vector<Model3dStyleNumericCase> givNumericCasesVec ) {
        if(givStyleId==null || givStyleId.equals(""))
            this.styleId = Model3dStylesEntry.undefinedStyleId;
        else
            this.styleId = givStyleId;
        
        if(givGlobalColor==null || !Model3dCommon.isValidColorString(givGlobalColor) )
            this.globalColor = Model3dStylesEntry.defaultGlobalColor;
        else
            this.globalColor = givGlobalColor;
        
        if(givCorrCapability == null || givCorrCapability.equals(""))
            this.corrCapability = Model3dStylesEntry.undefinedCorrCapability;
        else            
            this.corrCapability = givCorrCapability;
        
        if(givGlobalIconFile == null || givGlobalIconFile.equals(""))
            this.globalIconFile = Model3dStylesEntry.undefinedIconFilename;
        else
            this.globalIconFile = givGlobalIconFile;
            
        if(givGlobalSmallPrefabFile == null || givGlobalSmallPrefabFile.equals(""))
            this.globalSmallPrefabFile = Model3dStylesEntry.undefinedPrefabFilename;
        else
            this.globalSmallPrefabFile = givGlobalSmallPrefabFile;
        
        this.specialCasesVec = Model3dStyleSpecialCase.fillMissingEntries(givSpecialCasesVec);
        
        this.numericCasesVec = Model3dStyleNumericCase.fillMissingAndSortNumericRanges(givNumericCasesVec);
        
    }
    
    /**
     * Creates a new instance of Model3dStylesEntry 
     * @param givenCursor the XML part of the styles file that describes a Styles' entry
     */
    public Model3dStylesEntry(SMInputCursor givenCursor) {
        this.styleId = Model3dStylesEntry.undefinedStyleId;
        this.corrCapability = Model3dStylesEntry.undefinedCorrCapability;
        this.globalColor = Model3dStylesEntry.defaultGlobalColor;
        this.globalIconFile = Model3dStylesEntry.undefinedIconFilename;
        this.globalSmallPrefabFile = Model3dStylesEntry.undefinedPrefabFilename;
        this.specialCasesVec= new Vector<Model3dStyleSpecialCase>();
        this.numericCasesVec= new Vector<Model3dStyleNumericCase>();
        
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getIdTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.styleId = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getForCapabilityTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.corrCapability = childInElement2.getText();
                            break;
                        }
                    }                    
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getGlobalColorTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.globalColor = childInElement2.getText();
                            break;
                        }
                    }                    
                }                
                
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getGlobalIconTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.globalIconFile = childInElement2.getText();
                            break;
                        }
                    }
                }                
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getGlobalPrefabTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.globalSmallPrefabFile = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getSpecialCasesListTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childCursor();
                    while(childInElement2.getNext() != null)
                    {
                        if(!childInElement2.getCurrEvent().hasText() &&
                        childInElement2.getLocalName().toLowerCase().equals(this.getSpecialCaseTag().toLowerCase() ) )
                            this.specialCasesVec.add(new Model3dStyleSpecialCase(childInElement2));
                    }
                }  
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getNumericRangesListTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childCursor();
                    while(childInElement2.getNext() != null)
                    {
                        if(!childInElement2.getCurrEvent().hasText() &&
                        childInElement2.getLocalName().toLowerCase().equals(this.getNumericCaseTag().toLowerCase() ) )
                            this.numericCasesVec.add(new Model3dStyleNumericCase(childInElement2));
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
            tmpElement1 =  parElement.addElement(this.getIdTag());
            tmpElement1.addCharacters(this.getStyleId());
            
            tmpElement1 =  parElement.addElement(this.getForCapabilityTag());
            tmpElement1.addCharacters(this.getCorrCapability());

            tmpElement1 =  parElement.addElement(this.getGlobalColorTag());
            tmpElement1.addCharacters(this.getGlobalColor());
            
            tmpElement1 =  parElement.addElement(this.getGlobalIconTag());
            tmpElement1.addCharacters(this.getGlobalIconFile());

            tmpElement1 =  parElement.addElement(this.getGlobalPrefabTag());
            tmpElement1.addCharacters(this.getGlobalSmallPrefabFile());
            
            if(this.getSpecialCasesVec().size() > 0) {
                tmpElement1 =  parElement.addElement(this.getSpecialCasesListTag());
                
                for(int i = 0; i < this.getSpecialCasesVec().size(); i++) 
                {
                    tmpElement2 =  tmpElement1.addElement(this.getSpecialCaseTag());
                    this.getSpecialCasesVec().elementAt(i).createInfoInDocument(tmpElement2);
                }
            }

            if(this.getNumericCasesVec().size() > 0) {
                tmpElement1 =  parElement.addElement(this.getNumericRangesListTag());
                
                for(int i = 0; i < this.getNumericCasesVec().size(); i++) 
                {
                    tmpElement2 =  tmpElement1.addElement(this.getNumericCaseTag());
                    this.getNumericCasesVec().elementAt(i).createInfoInDocument(tmpElement2);
                }
            }
            
        }
        catch(Exception e) {
            return;
        }
    }
    
    /**
     * Compares two  Model3dStylesEntry objects.
     * We don't compare their ids. Just their contents (their "definitions")
     * @param targEntry the target  Model3dStylesEntry to compare to
     * @return true if objects express the same Style entry, or false otherwise
     */
    public boolean equals(  Model3dStylesEntry targEntry) {
            //
            // check if corresponfing capabilities are the same too
            //
            if(! this.getCorrCapability().equals(targEntry.getCorrCapability()))
            {
                return false;
            }            
            //
            // check if set global color is the same too
            //
            if(! this.getGlobalColor().equals(targEntry.getGlobalColor()))
            {
                return false;
            }  
            
            //
            // check if set icons are the same too
            //
            if(! this.getGlobalIconFile().equals(targEntry.getGlobalIconFile()))
            {
                return false;
            }            
            //
            // check if set prefabs are the same too
            //
            if(! this.getGlobalSmallPrefabFile().equals(targEntry.getGlobalSmallPrefabFile()))
            {
                return false;
            }            
            //
            // check if vector of special cases is the same too.
            //
            Vector<Model3dStyleSpecialCase> targetSpecialCasesVec = targEntry.getSpecialCasesVec();
            Vector<Model3dStyleSpecialCase> tmpComparisonVec1 =  new Vector<Model3dStyleSpecialCase>(targetSpecialCasesVec);
            int i = 0;
            for(i = 0; i < this.getSpecialCasesVec().size(); i++)
            {
                for(int j = 0;  tmpComparisonVec1.size() > 0 && j < tmpComparisonVec1.size() ; j++)
                {                
                    if(this.getSpecialCasesVec().elementAt(i).equals(tmpComparisonVec1.elementAt(j)))
                    {                    
                        tmpComparisonVec1.removeElementAt(j);
                        j -=1;
                    }
                }
            }
            // if at the end, the iterator of the source Vector of smart devs has not the value of the size of the target vector ( in other words they are not of equal size)
            // or the clone of the target Vector of Rooms has still some unmatched elements then they are not equal          
            if( ( i !=  targetSpecialCasesVec.size()) || (tmpComparisonVec1.size()!= 0) )
            {
                return false;
            } 
            //
            // check if vector of numeric range cases is the same too. (++++)
            //
            Vector<Model3dStyleNumericCase> targetNumericCasesVec = targEntry.getNumericCasesVec();
            Vector<Model3dStyleNumericCase> tmpComparisonVec2 =  new Vector<Model3dStyleNumericCase>(targetNumericCasesVec);
            i = 0;
            for(i = 0; i < this.getNumericCasesVec().size(); i++)
            {
                for(int j = 0;  tmpComparisonVec2.size() > 0 && j < tmpComparisonVec2.size() ; j++)
                {                
                    if(this.getNumericCasesVec().elementAt(i).equals(tmpComparisonVec2.elementAt(j)))
                    {                    
                        tmpComparisonVec2.removeElementAt(j);
                        j -=1;
                    }
                }
            }
            // if at the end, the iterator of the source Vector of smart devs has not the value of the size of the target vector ( in other words they are not of equal size)
            // or the clone of the target Vector of Rooms has still some unmatched elements then they are not equal          
            if( ( i !=  targetNumericCasesVec.size()) || (tmpComparisonVec2.size()!= 0) )
            {
                return false;
            } 
            
            return true;
    }
   
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Style entry's XML fields
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
            outputRootEl = doc.addElement(Model3dStylesList.getStyleEntryTag());
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
     * @param val the given val that will be matched against the defined numeric ranges
     * @Return the index of the matching numeric case in the Numeric Cases Vector, or -1 if no match was found!
     *
     */
    public int getNumericStyleCaseIndexForValue(double val)
    {
        int indexToReturn = -1;
        for(int i = 0; i < this.getNumericCasesVec().size(); i++)
        {
            if(this.getNumericCasesVec().elementAt(i).belongsInside(val))
            {
                indexToReturn = i;
                break;
            }
        }
        return indexToReturn;
    }
    
    
    /**
     * @param val the given val that will be matched against the defined numeric ranges
     * @Return the matching numeric case in the Numeric Cases Vector, or null if no match was found!
     *
     */    
    public Model3dStyleNumericCase getNumericStyleCaseForValue(double val)
    {
        Model3dStyleNumericCase numCaseToReturn = null;
        for(int i = 0; i < this.getNumericCasesVec().size(); i++)
        {
            if(this.getNumericCasesVec().elementAt(i).belongsInside(val))
            {
                numCaseToReturn = this.getNumericCasesVec().elementAt(i);
                break;
            }
        }
        return numCaseToReturn;
    }    
    
    /**
     * @param val the given val that will be matched against the defined special values
     * @Return the index of the matching numeric case in the Special Cases Vector, or -1 if no match was found!
     *
     */
    public int getSpecialStyleCaseIndexForValue(String val)
    {
        int indexToReturn = -1;
        for(int i = 0; i < this.getSpecialCasesVec().size(); i++)
        {
            if(this.getSpecialCasesVec().elementAt(i).getSpecialValue().equals(val))
            {
                indexToReturn = i;
                break;
            }
        }
        return indexToReturn;
    }
    
    /**
     * @param val the given val that will be matched against the defined special values
     * @Return the matching numeric case in the Special Cases Vector, or null if no match was found!
     *
     */  
    public Model3dStyleSpecialCase getSpecialStyleCaseForValue(String val)
    {
        Model3dStyleSpecialCase spCaseToReturn = null;
        for(int i = 0; i < this.getSpecialCasesVec().size(); i++)
        {
            if(this.getSpecialCasesVec().elementAt(i).getSpecialValue().equals(val))
            {
                spCaseToReturn = this.getSpecialCasesVec().elementAt(i);
                break;
            }
        }
        return spCaseToReturn;
    }
    
    
    // getters
    public String getStyleId() {
        return styleId;
    }

     public Vector<Model3dStyleNumericCase> getNumericCasesVec() {
        return numericCasesVec;
    }


    public Vector<Model3dStyleSpecialCase> getSpecialCasesVec() {
        return specialCasesVec;
    }
   

    public String getGlobalIconFile() {
        return globalIconFile;
    }

    public String getGlobalSmallPrefabFile() {
        return globalSmallPrefabFile;
    }
    
    public String getCorrCapability() {
        return corrCapability;                
    }

    public String getGlobalColor() {
        return globalColor;
    }
    


    public static String getIdTag() {
        return idTag;
    }

    public static String getSpecialCasesListTag() {
        return specialCasesListTag;
    }

    public static String getNumericRangesListTag() {
        return numericRangesListTag;
    }

    public static String getSpecialCaseTag() {
        return specialCaseTag;
    }

    public static String getForCapabilityTag() {
        return forCapabilityTag;
    }
    public static String getGlobalIconTag() {
        return globalIconTag;
    }

    public static String getGlobalPrefabTag() {
        return globalPrefabTag;
    }

    public static String getNumericCaseTag() {
        return numericCaseTag;
    }

    public static String getGlobalColorTag() {
        return globalColorTag;
    }



    
    
}
