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
 * Model3dRoomPolygon.java
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
 *      &lt;roomPolygon&gt;
 *              &lt;type&gt;cube&lt;/type&gt;    &lt;----------------------	The cube is placed according to its center point 
 * 									defined in the CenterPoint tag) 
 * 									AND is rotated until that one of its edges is parallel
 * 									(or approx parallel) to the reference line of the 3d Building
 * 									The reference line of the Building, is automatically picked
 * 									to be any Line from the Outline of the Buildings Base.
 * 									(To do) Future work: type could also be:
 * 									(+) "rectangle" : we would need length, width, height and use the
 * 									"length dimension" as ref line.
 * 									(+) "customDrawnInplace" : where we just dump the KML contents of
 * 									this tag at the room position. (The room is described in real world
 *									positioning co-ords).
 * 									(To do) In the distant Far Far Future: Check for overlapping rooms,
 * 									sensors outside the rooms, rooms overlapping with building etc.
 * 									For now this (the checks) is assumed to be done by the GUI, or that
 * 									at least a user/provider can fix it manually by making a new interface.
 * 		&lt;centerPoint&gt;&lt;/centerPoint&gt; 	&lt;-------------	The crossing of its base's diagonals. 
 * 		&lt;size1&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 		&lt;size2&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 		&lt;size3&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 	&lt;/roomPolygon&gt;
 *</pre>
 * @author antoniou
 */
public class Model3dRoomPolygon {
    private Logger logger = Logger.getLogger(Model3dRoomPolygon.class);
    private String type;
    private GeoPoint centerPoint;
    private double size1;
    private double size2;
    private double size3;
    private double height;
    private double elevation;
    
    private static final String polyTypeTag = "type";
    private static final String polyCenterPointTag = "centerPoint";
    private static final String size1Tag = "size1";
    private static final String size2Tag = "size2";
    private static final String size3Tag = "size3";
    private static final String heightTag = "height";
    private static final String elevationTag = "elevation";
    
    private static final String polyTypeCube = "cube";
    private static final String polyTypeRectangle = "rectangle";
    private static final String polyTypeUnknown = "unknown";
    
    private static final double polySizeUndefined = -1;
    private static final double minSize = 0.50; // at least 50 centimeters size
    private static final double minHeight = 2.0; // at least 2 meters in height
    private static final double defaultElevation = 0.0; // default elevation is zero

    /** Default constructor. Creates an invalid new instance of Model3dRoomPolygon */    
    public Model3dRoomPolygon()
    {
        this.type = Model3dRoomPolygon.getPolyTypeUnknown();
        this.centerPoint = new GeoPoint();
        this.size1 = Model3dRoomPolygon.getPolySizeUndefined();
        this.size2 = Model3dRoomPolygon.getPolySizeUndefined();
        this.size3 = Model3dRoomPolygon.getPolySizeUndefined();
        this.height = minHeight; 
        this.elevation = defaultElevation; 
    }
    
    
    /** Creates a new instance of Model3dRoomPolygon */
    public Model3dRoomPolygon(String givType, GeoPoint givCenterPoint, double givSize1, double givSize2, double givSize3, double givHeight, double givElevation) {
        this.type = givType;
        this.centerPoint = givCenterPoint;
        
        if(givSize1 > 0)
            this.size1 = givSize1;
        else
            this.size1 = minSize;
        
        if(givSize2 > 0)
            this.size2 = givSize2;
        else
            this.size2 = Model3dRoomPolygon.getPolySizeUndefined();
        
        if(givSize3 > 0)
            this.size3 = givSize3;
        else
            this.size3 = Model3dRoomPolygon.getPolySizeUndefined();
        
        if(givHeight > 0)
            this.height = givHeight;
        else
            this.height = minHeight;
        
        if(givElevation > 0)
            this.elevation = givElevation;
        else
            this.elevation = defaultElevation;
    }

    /**
     * Creates a new instance of Model3dRoomPolygon
     * @param givenCursor the XML part of the meta file that describes a Room Polygon entry
     */
    public Model3dRoomPolygon(SMInputCursor givenCursor) {
        this.type = Model3dRoomPolygon.getPolyTypeUnknown();
        this.centerPoint = new GeoPoint();
        this.size1 = minSize;
        this.size2 = Model3dRoomPolygon.getPolySizeUndefined();
        this.size3 = Model3dRoomPolygon.getPolySizeUndefined();
        this.height = minHeight; 
        this.elevation = defaultElevation; 
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getPolyTypeTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.type = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getPolyCenterPointTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.centerPoint = GeoPoint.parseStringGeodesicCoords(childInElement2.getText(), GeoPoint.tokenOrderLatLonAlt, ",", GeoPoint.noElevationOverride);
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getSize1Tag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.size1 = Double.parseDouble(childInElement2.getText());
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getSize2Tag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.size2 = Double.parseDouble(childInElement2.getText());
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getSize3Tag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.size3 = Double.parseDouble(childInElement2.getText());
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getHeightTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.height = Double.parseDouble(childInElement2.getText());
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getElevationTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.elevation = Double.parseDouble(childInElement2.getText());
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
            tmpElement1 =  parElement.addElement(this.getPolyTypeTag());
            tmpElement1.addCharacters(this.getType());
            
            tmpElement1 =  parElement.addElement(this.getPolyCenterPointTag());
            tmpElement1.addCharacters(this.getCenterPoint().toStringGeodesicCoords(GeoPoint.tokenOrderLatLonAlt, ","));
            
            tmpElement1 =  parElement.addElement(this.getSize1Tag());
            tmpElement1.addCharacters(Double.toString(this.getSize1()) );
            
            tmpElement1 =  parElement.addElement(this.getSize2Tag());
            tmpElement1.addCharacters(Double.toString(this.getSize2()) );

            tmpElement1 =  parElement.addElement(this.getSize3Tag());
            tmpElement1.addCharacters(Double.toString(this.getSize3()) );

            tmpElement1 =  parElement.addElement(this.getHeightTag());
            tmpElement1.addCharacters(Double.toString(this.getHeight()) );

            tmpElement1 =  parElement.addElement(this.getElevationTag());
            tmpElement1.addCharacters(Double.toString(this.getElevation()) );
                        
        } catch(Exception e) {
            return;
        }
    }
    
    /**
     * Compares two Model3dRoomPolygon objects.
     * A RECTANGLE AND CUBE COULD BE THE SAME TOO)
     * @param targEntry the target  Model3dRoomPolygon to compare to
     * @return true if objects express the same  Room Polygon entry, or false otherwise
     */
    public boolean equals( Model3dRoomPolygon targEntry) {
        if(this.getType().equals(targEntry.getType() ) &&
                this.getCenterPoint().equals(targEntry.getCenterPoint()) && 
                this.getSize1() == targEntry.getSize1() && 
                this.getSize2() == targEntry.getSize2() && 
                this.getSize3() == targEntry.getSize3() &&
                this.getHeight() == targEntry.getHeight() &&
                this.getElevation() == targEntry.getElevation()) 
        {
            return true;
        } 
        else if(this.getCenterPoint().equals(targEntry.getCenterPoint()) &&
                this.getType().equals(Model3dRoomPolygon.polyTypeCube) && targEntry.getType().equals(Model3dRoomPolygon.polyTypeRectangle) &&
                this.getSize1() == targEntry.getSize1() && this.getSize1() == targEntry.getSize2() && 
                this.getSize3() == targEntry.getSize3() &&
                this.getHeight() == targEntry.getHeight() &&
                this.getElevation() == targEntry.getElevation())
        {
            return true;
        }
        else if(this.getCenterPoint().equals(targEntry.getCenterPoint()) &&
                this.getType().equals(Model3dRoomPolygon.polyTypeRectangle) && targEntry.getType().equals(Model3dRoomPolygon.polyTypeCube) &&
                this.getSize1() == targEntry.getSize1() && this.getSize2() == targEntry.getSize1() && 
                this.getSize3() == targEntry.getSize3() &&
                this.getHeight() == targEntry.getHeight() &&
                this.getElevation() == targEntry.getElevation())
        {
            return true;
        }
        else
            return false;
    }    
    
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Room Polygon entry's XML fields
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
            outputRootEl = doc.addElement(Model3dRoomEntry.getRoomPolygonTag());
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
    
    
    
    public static String getPolyTypeTag() {
        return polyTypeTag;
    }

    public static String getPolyCenterPointTag() {
        return polyCenterPointTag;
    }

    public static String getSize1Tag() {
        return size1Tag;
    }

    public static String getSize2Tag() {
        return size2Tag;
    }

    public static String getSize3Tag() {
        return size3Tag;
    }

    public static String getElevationTag() {
        return elevationTag;
    }

    public static String getHeightTag() {
        return heightTag;
    }

    public GeoPoint getCenterPoint() {
        return centerPoint;
    }

    public double getSize1() {
        return size1;
    }

    public double getSize2() {
        return size2;
    }

    public double getSize3() {
        return size3;
    }

    public double getElevation() {
        return elevation;
    }

    public double getHeight() {
        return height;
    }
    
    public String getType() {
        return type;
    }

    public static double getPolySizeUndefined() {
        return polySizeUndefined;
    }

    public static String getPolyTypeCube() {
        return polyTypeCube;
    }

    public static String getPolyTypeRectangle() {
        return polyTypeRectangle;
    }

    public static String getPolyTypeUnknown() {
        return polyTypeUnknown;
    }

}
