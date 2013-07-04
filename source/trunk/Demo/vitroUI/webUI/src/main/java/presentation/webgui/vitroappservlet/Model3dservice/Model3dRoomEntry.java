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
 * Model3dRoomEntry.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import org.apache.log4j.Logger;
import vitro.vspEngine.logic.model.SmartNode;
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
 *	&lt;room&gt;
 * 		&lt;name&gt;&lt;/name&gt;
 *              &lt;roomPolygon&gt;
 * 			&lt;type&gt;cube&lt;/type&gt;    &lt;----------------------	The cube is placed according to its center point 
 * 											(defined in the CenterPoint tag) 
 * 											AND is rotated until that one of its edges is parallel
 * 											(or approx parallel) to the reference line of the 3d Building
 * 											The reference line of the Building, is automatically picked
 *                                                                                      to be any Line from the Outline of the Buildings Base.
 * 											(To do) Future work: type could also be:
 * 											(+) "rectangle" : we would need length, width, height and use the
 * 											"length dimension" as ref line.
 * 											(+) "customDrawnInplace" : where we just dump the KML contents of
 * 											this tag at the room position. (The room is described in real world
 *											positioning co-ords).
 * 											(To do) In the distant Far Far Future: Check for overlapping rooms,
 * 											sensors outside the rooms, rooms overlapping with building etc.
 * 											For now this (the checks) is assumed to be done by the GUI, or that
 * 											at least a user/provider can fix it manually by making a new interface.
 * 			&lt;centerPoint&gt;&lt;/centerPoint&gt; 	&lt;-------------	The crossing of its base's diagonals. 
 * 			&lt;size1&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 			&lt;size2&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 			&lt;size3&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 		&lt;/roomPolygon&gt;
 * 		&lt;sensingDevices&gt;    &lt;-----------------------------	Motes. not sensors.
 * 			&lt;smartDev&gt;
 * 				&lt;id&gt;SensorNetworkId::moteid&lt;/id&gt;    &lt;----- 	unique id in the gateway (required)
 * 				&lt;pointCoords&gt;							&lt;----- 	co-ords are optional. If not defined, motes are scattered randomly
 * 															in the room. (Caution here, so that they won't overlap).
 * 															if a mote has mupltiple sensors (a usual case), and a query has
 *                                                                                                                      defined to get values from a number x of them, x &gt; 1, then the
 * 															sensors will be placed next to each other, at the mote's location.
 * 				&lt;/pointCoords&gt;
 *                      &lt;/smartDev&gt;
 * 			.
 * 			.
 * 			.
 * 		&lt;/sensingDevices&gt;
 *	&lt;/room&gt;
 * </pre>
 * @author antoniou
 */
public class Model3dRoomEntry {
    private Logger logger = Logger.getLogger(Model3dRoomEntry.class);
    private String name;
    private Model3dRoomPolygon roomPoly; 
    private Vector<Model3dSensingDevice> smartDevVec;
    
    private static final String roomNameTag = "name";
    private static final String roomPolygonTag = "roomPolygon";
    private static final String smartDevListTag = "sensingDevices";
    private static final String smartDevEntryTag = "smartDev";
    
    /** Default constructor. Creates an invalid new instance of Model3dRoomEntry */
    public Model3dRoomEntry()
    {
        this.name = "";
        this.roomPoly = new Model3dRoomPolygon();
        this.smartDevVec = new Vector<Model3dSensingDevice>();
    }
    
    /** Creates a new instance of Model3dRoomEntry */
    public Model3dRoomEntry(String givName, Model3dRoomPolygon givRoomPoly, Vector<Model3dSensingDevice> givSmartDevVec) {
        this.name = givName;
        this.roomPoly = givRoomPoly;
        this.smartDevVec = givSmartDevVec;
        
    }

    /** 
     * Creates a new instance of Model3dRoomEntry 
     * @param givenCursor the XML part of the meta file that describes a Room entry
     */
    public Model3dRoomEntry(SMInputCursor givenCursor) {
        this.name = "";
        this.roomPoly = new Model3dRoomPolygon();
        this.smartDevVec = new Vector<Model3dSensingDevice>();
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getRoomNameTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {                    
                            this.name = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getRoomPolygonTag().toLowerCase() ) ) 
                {
                    this.roomPoly = new Model3dRoomPolygon(childInElement);
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getSmartDevListTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childCursor();
                    while(childInElement2.getNext() != null)
                    {
                        if(!childInElement2.getCurrEvent().hasText() &&
                        childInElement2.getLocalName().toLowerCase().equals(this.getSmartDevEntryTag().toLowerCase() ) )
                            this.smartDevVec.add(new Model3dSensingDevice(childInElement2));
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
            SMOutputElement tmpElement2;
            tmpElement1 =  parElement.addElement(this.getRoomNameTag());
            tmpElement1.addCharacters(this.getName());
            
            tmpElement1 =  parElement.addElement(this.getRoomPolygonTag());
            this.getRoomPoly().createInfoInDocument(tmpElement1);
            
            if(this.getSmartDevVec().size() > 0) {
                tmpElement1 =  parElement.addElement(this.getSmartDevListTag());
                
                for(int i = 0; i < this.getSmartDevVec().size(); i++) 
                {
                    tmpElement2 =  tmpElement1.addElement(this.getSmartDevEntryTag());
                    this.getSmartDevVec().elementAt(i).createInfoInDocument(tmpElement2);
                }
            }
            
        } catch(Exception e) {
            return;
        }
    }    

    /**
     * Compares two Model3dRoomEntry objects.
     * @param targEntry the target Model3dRoomEntry to compare to
     * @return true if objects express the same Room entry, or false otherwise
     */
    public boolean equals(Model3dRoomEntry targEntry) {
        if(this.getName().equals(targEntry.getName()) &&
                this.getRoomPoly().equals(targEntry.getRoomPoly()) ) {
            //
            // check if vector of motes is the same too.
            //
            Vector<Model3dSensingDevice> targetSDVec = targEntry.getSmartDevVec();
            Vector<Model3dSensingDevice> tmpComparisonVec =  new Vector<Model3dSensingDevice>(targetSDVec);
            int i = 0;
            for(i = 0; i < this.getSmartDevVec().size(); i++)
            {
                for(int j = 0;  tmpComparisonVec.size() > 0 && j < tmpComparisonVec.size() ; j++)
                {                
                    if(this.getSmartDevVec().elementAt(i).equals(tmpComparisonVec.elementAt(j)))
                    {                    
                        tmpComparisonVec.removeElementAt(j);
                        j -=1;
                    }
                }
            }
            // if at the end, the iterator of the source Vector of smart devs has not the value of the size of the target vector ( in other words they are not of equal size)
            // or the clone of the target Vector of smart devs has still some unmatched elements then they are not equal          
            if( ( i !=  targetSDVec.size()) || (tmpComparisonVec.size()!= 0) )
            {
                return false;
            } 
            return true;
        } 
        else
            return false;
    }        
    
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Room entry's XML fields
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
            outputRootEl = doc.addElement(Model3dInterfaceEntry.getRoomEntryTag());
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
    
    public static String getRoomNameTag() {
        return roomNameTag;
    }

    public static String getRoomPolygonTag() {
        return roomPolygonTag;
    }

    public static String getSmartDevListTag() {
        return smartDevListTag;
    }
    
    public static String getSmartDevEntryTag() {
        return smartDevEntryTag;
    }
    
    
    public String getName() {
        return name;
    }

    public Model3dRoomPolygon getRoomPoly() {
        return roomPoly;
    }

    public Vector<Model3dSensingDevice> getSmartDevVec() {
        return smartDevVec;
    }
        
    public Vector<Model3dSensingDevice> getSensorsThatMatch(Vector<SmartNode> requestedSmDevInThisInterface)
    {
        Vector<Model3dSensingDevice> toReturnVec = new Vector<Model3dSensingDevice>();
        Vector<Model3dSensingDevice> tmpSensorsInRoomVec = this.getSmartDevVec();                                                            
        for(int i = 0; i < tmpSensorsInRoomVec.size(); i++)
        {
            for(int j = 0 ; j < requestedSmDevInThisInterface.size(); j++)
            {
                if(tmpSensorsInRoomVec.elementAt(i).getSmartDevId().equals(requestedSmDevInThisInterface.elementAt(j).getId()))
                {
                    toReturnVec.add(tmpSensorsInRoomVec.elementAt(i));
                    break; // break the inner loop (first match is enough), and check for the next device in the room if it is to be included as a match.
                }
            }
        }                
        return toReturnVec;
    }

    
    
}
