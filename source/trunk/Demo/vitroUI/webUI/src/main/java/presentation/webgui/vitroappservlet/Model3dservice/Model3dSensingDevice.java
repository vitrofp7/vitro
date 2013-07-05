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
 * Model3dSensingDevice.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.geo.GeoPoint;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.StringWriter;

/**
 *<pre>
 * 	&lt;sensingDevices&gt;    &lt;-----------------------------	Motes/webcams. not just sensors.
 * 		&lt;smartDev&gt;
 * 			&lt;id&gt;SensorNetworkId::moteid&lt;/id&gt;    &lt;----- 	unique id in the gateway (required)
 * 			&lt;pointCoords&gt;				&lt;----- 	co-ords are optional. If not defined, motes are scattered randomly
 * 											in the room. (Caution here, so that they won't overlap).
 * 											if a mote has mupltiple sensors (a usual case), and a query has
 * 											defined to get values from a number x of them, x &gt; 1, then the
 * 											sensors will be placed next to each other, at the mote's location.
 * 			&lt;/pointCoords&gt;
 *              &lt;/smartDev&gt;
 * 		.
 * 		.
 * 		.
 * 	&lt;/sensingDevices&gt;
 * </pre>
 * @author antoniou
 */
public class Model3dSensingDevice {
    private Logger logger = Logger.getLogger(Model3dSensingDevice.class);
    
    private String smartDevId;
    private GeoPoint smartDevPoint;
    
    private static final String idTag = "id";
    private static final String pointCoordsTag = "pointCoords";
    
    /** Creates a new instance of Model3dSensingDevice */
    public Model3dSensingDevice(String givId, GeoPoint givPoint) {
        this.smartDevId = givId;
        this.smartDevPoint = givPoint;
    }
    
    /**
     * Creates a new instance of Model3dSensingDevice
     * @param givenCursor the XML part of the meta file that describes a Sensing Device entry
     */
    public Model3dSensingDevice(SMInputCursor givenCursor) {
        smartDevId = "";
        smartDevPoint = new GeoPoint(); // default invalid point
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getIdTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.smartDevId = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getPointCoordsTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.smartDevPoint = GeoPoint.parseStringGeodesicCoords(childInElement2.getText(), GeoPoint.tokenOrderLatLonAlt, ",", GeoPoint.noElevationOverride);
                            break;
                        }
                    }
                }
            }
        } catch(Exception e) {
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
            tmpElement1 =  parElement.addElement(this.getIdTag());
            tmpElement1.addCharacters(this.getSmartDevId());
            
            tmpElement1 =  parElement.addElement(this.getPointCoordsTag());
            tmpElement1.addCharacters(this.getSmartDevPoint().toStringGeodesicCoords(GeoPoint.tokenOrderLatLonAlt, ","));
            
        } catch(Exception e) {
            return;
        }
    }
    
    /**
     * Compares two Model3dSensingDevice objects.
     * @param targEntry the target Model3dSensingDevice to compare to
     * @return true if objects express the same Sensing Device entry, or false otherwise
     */
    public boolean equals(Model3dSensingDevice targEntry) {
        if(this.getSmartDevId().equals(targEntry.getSmartDevId()) &&
                this.getSmartDevPoint().equals(targEntry.getSmartDevPoint()) ) {
            return true;
        } else
            return false;
    }    
    
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Sensing Device entry's XML fields
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
            outputRootEl = doc.addElement(Model3dRoomEntry.getSmartDevEntryTag());
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
    
    

    public static String getIdTag() {
        return idTag;
    }

    public static String getPointCoordsTag() {
        return pointCoordsTag;
    }

    public String getSmartDevId() {
        return smartDevId;
    }

    public GeoPoint getSmartDevPoint() {
        return smartDevPoint;
    }    
}
