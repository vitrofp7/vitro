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
 * Model3dLineOfReference.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.geo.GeoCalculus;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.StringWriter;

/**
 * <pre>
 *  &lt;lineOfRef&gt;
 *	&lt;startPoint&gt;&lt;/startPoint&gt;
 *	&lt;endPoint&gt;&lt;/endPoint&gt;
 *  &lt;/lineOfRef>
 * </pre>
 * @author antoniou
 */
public class Model3dLineOfReference {
    private Logger logger = Logger.getLogger(Model3dLineOfReference.class);
    private double azimuth;
    
    private static final String azimuthTag = "azimuth";
    
    /** 
     * Creates a new default (azimuth = 0) instance of Model3dLineOfReference 
     */
    public Model3dLineOfReference() 
    {
        this.azimuth = 0.0;
    }

    /** Creates a new instance of Model3dLineOfReference */
    public Model3dLineOfReference(GeoPoint givStartPoint, GeoPoint givEndPoint) 
    {   
        if(givStartPoint!=null && givStartPoint.isValidPoint() &&
                givEndPoint!=null && givEndPoint.isValidPoint())
        {
            this.azimuth = GeoCalculus.approxGCAzimuth(givStartPoint, givEndPoint);
        }
        else
        {
            this.azimuth = 0.0;
        }
    }      
    
    /**
     * Creates a new instance of Model3dLineOfReference
     * @param givenCursor the XML part of the meta file that describes a Line Of Reference entry
     */
    public Model3dLineOfReference(SMInputCursor givenCursor) 
    {
        this.azimuth = 0.0;
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getAzimuthTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.azimuth = Double.parseDouble(childInElement2.getText());
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
            tmpElement1 =  parElement.addElement(this.getAzimuthTag());
            tmpElement1.addCharacters(Double.toString(this.getAzimuth()) );
            
        } catch(Exception e) {
            return;
        }
    }
    
    /**
     * Compares two Model3dLineOfReference objects.
     * Their calculated Azimuths are compared actually...
     * (To do) (add code, maybe allow some error margin in the equality of azimuths)
     * @param targEntry the target  Model3dLineOfReference to compare to
     * @return true if objects express the same  Line Of Reference entry, or false otherwise
     */
    public boolean equals(Model3dLineOfReference targEntry) {
        if(this.getAzimuth() == targEntry.getAzimuth()) {
            return true;
        } else {
            return false;
        }
    }    
    
    
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Line Of Reference entry's XML fields
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
            outputRootEl = doc.addElement(Model3dInterfaceEntry.getLineOfRefTag());
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

    public double getAzimuth() {
        return azimuth;
    }

    public static String getAzimuthTag() {
        return azimuthTag;
    }

}
