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
 * Model3dStyleSpecialCase.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.query.ReqResultOverData;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.StringWriter;
import java.util.Vector;

/**
 *<pre>
 * 			&lt;special&gt;
 * 				&lt;value&gt;Not supported&lt;/value&gt;
 * 				&lt;icon&gt;&lt;/icon&gt;
 * 				&lt;color1&gt;white&lt;/color1&gt;
 *               		&lt;smprefab&gt;lala2.kml&lt;/smprefab&gt;
 * 			&lt;/special&gt;
 * </pre>
 * @author antoniou
 */
public class Model3dStyleSpecialCase {
    private Logger logger = Logger.getLogger(Model3dStyleSpecialCase.class);
    private String specialValue;
    private String iconFilename;
    private String color1;
    private String smallPrefabFilename;
    
    private static final String valueTag = "value";
    private static final String iconTag = "icon";
    private static final String color1Tag = "color1";
    private static final String prefabTag = "smprefab";
    
    private static final String undefinedPrefabFilename = "unknown";
    private static final String undefinedIconFilename = "unknown";
    public static final String UNDEFINEDCOLOR1 = "unknown";
    
    private static final String undefinedSpecialValue = "unknown";
    
    public static String[] validSpecialValues = {ReqResultOverData.specialValueBinary, ReqResultOverData.specialValueNoReading, ReqResultOverData.specialValueNotSupported, ReqResultOverData.specialValuePending, ReqResultOverData.specialValueTimedOut, undefinedSpecialValue};
    
    /** Creates a new instance of Model3dStyleSpecialCase */
    public Model3dStyleSpecialCase() {
        this.specialValue = Model3dStyleSpecialCase.undefinedSpecialValue;
        this.color1 = Model3dStyleSpecialCase.UNDEFINEDCOLOR1;
        this.iconFilename = Model3dStyleSpecialCase.undefinedIconFilename;
        this.smallPrefabFilename = Model3dStyleSpecialCase.undefinedPrefabFilename;
    }
    
    /** Creates a new instance of Model3dStyleSpecialCase */
    public Model3dStyleSpecialCase(String givVal, String givColor1, String givIconFilename,  String givSmallPrefabFilename) {
        if(isValidSpecialValue(givVal))
        {
            this.specialValue = givVal;
        }
        else 
            this.specialValue = Model3dStyleSpecialCase.undefinedSpecialValue;
       
        if(givColor1==null || !Model3dCommon.isValidColorString(givColor1) )
             this.color1 = Model3dStyleSpecialCase.UNDEFINEDCOLOR1;
        else
             this.color1 = givColor1;
        
        if(givIconFilename == null || givIconFilename.equals(""))
        {
            if(this.specialValue.equals(ReqResultOverData.specialValueNoReading) )
            {
                this.iconFilename = Model3dStylesEntry.defaultNoReadingIconFilename;
            }
            else if(this.specialValue.equals(ReqResultOverData.specialValueNotSupported))
            {
                this.iconFilename = Model3dStylesEntry.defaultUnavailableIconFilename;                
            }
            else if(this.specialValue.equals(ReqResultOverData.specialValuePending))
            {
                 this.iconFilename = Model3dStylesEntry.defaultPendingIconFilename;
            }
            else if(this.specialValue.equals(ReqResultOverData.specialValueTimedOut))
            {
                this.iconFilename = Model3dStylesEntry.defaultTimedOutIconFilename;
            }
            else
                this.iconFilename = Model3dStyleSpecialCase.undefinedIconFilename;
        }
        else
            this.iconFilename = givIconFilename;
        
        if(givSmallPrefabFilename == null || givSmallPrefabFilename.equals(""))
            this.smallPrefabFilename = Model3dStyleSpecialCase.undefinedPrefabFilename;
        else
            this.smallPrefabFilename = givSmallPrefabFilename;
    }

    /**
     * Creates a new instance of Model3dStyleSpecialCase
     * @param givenCursor the XML part of the styles file that describes a Style Special Case entry
     */
    public Model3dStyleSpecialCase(SMInputCursor givenCursor) {
        this.specialValue = Model3dStyleSpecialCase.undefinedSpecialValue;
        this.iconFilename = Model3dStyleSpecialCase.undefinedIconFilename;
        this.color1 = Model3dStyleSpecialCase.UNDEFINEDCOLOR1;
        this.smallPrefabFilename = Model3dStyleSpecialCase.undefinedPrefabFilename;
        
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getValueTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.specialValue = childInElement2.getText();
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
     *
     */
    public void createInfoInDocument(SMOutputElement parElement) {
        try{
            SMOutputElement tmpElement1;
            SMOutputElement tmpElement2;
            tmpElement1 =  parElement.addElement(this.getValueTag() );
            tmpElement1.addCharacters(this.getSpecialValue()  );

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
     * Compares two Model3dStyleSpecialCase objects.
     * @param targEntry the target Model3dStyleSpecialCase to compare to
     * @return true if objects express the same  Special Case entry, or false otherwise
     */
    public boolean equals( Model3dStyleSpecialCase targEntry) {
        if(this.specialValue.equals(targEntry.specialValue) &&
                this.color1.equals(targEntry.color1) &&
                this.smallPrefabFilename.equals(targEntry.smallPrefabFilename) &&
                this.iconFilename.equals(targEntry.iconFilename)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Style Special Case entry's XML fields
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
            outputRootEl = doc.addElement(Model3dStylesEntry.getSpecialCaseTag());
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

    
    private static boolean isValidSpecialValue(String candSpVal)
    {
        if(candSpVal==null)
            return false;
        for(int i = 0; i < validSpecialValues.length; i++)
        {
            if(candSpVal.equals(validSpecialValues[i]))
            {
                return true;
            }
        }
        return false;
    }
    
    public static Vector<Model3dStyleSpecialCase> fillMissingEntries(Vector<Model3dStyleSpecialCase> givSpecialCasesVec)
    {
        Vector<Model3dStyleSpecialCase> toReturnVec =  new Vector<Model3dStyleSpecialCase>();
        if(givSpecialCasesVec != null)
        {            
            toReturnVec = givSpecialCasesVec;
        }
        
        for(int i = 0; i < validSpecialValues.length - 1; i++) // we don't fill the undefined special value.'
        {
            boolean foundAMatch = false;
            for(int j = 0 ; j <toReturnVec.size(); j++)
            {
                if(toReturnVec.elementAt(j).getSpecialValue().equals(validSpecialValues[i]))
                {
                    foundAMatch = true;
                    break;
                }               
            }
            if(!foundAMatch)
            {
                toReturnVec.addElement(new Model3dStyleSpecialCase(validSpecialValues[i], null, null, null));
            }
        }
        
        return toReturnVec;
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

    public static String getValueTag() {
        return valueTag;
    }

    public String getColor1() {
        return color1;
    }

    public String getIconFilename() {
        return iconFilename;
    }

    public String getSmallPrefabFilename() {
        return smallPrefabFilename;
    }

    public String getSpecialValue() {
        return specialValue;
    }    

    public static String[]  getValidSpecialValues() {
        return validSpecialValues;
    }


}
