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
 * Model3dInterfaceEntry.java
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
 *<pre>
 *	&lt;interface&gt;
 *      	&lt;gwid&gt;q&lt;/gwid&gt;
 * 		&lt;iid&gt;1&lt;/iid&gt;
 * 		&lt;description&gt;Happy mode&lt;/description&gt;
 *		&lt;lineOfRef&gt;
 *			&lt;startPoint&gt;&lt;/startPoint&gt;
 *			&lt;endPoint&gt;&lt;/endPoint&gt;
 *		&lt;/lineOfRef>
 * 		&lt;rooms&gt;
 * 			&lt;room&gt;
 * 				&lt;name&gt;&lt;/name&gt;
 * 				&lt;roomPolygon&gt;
 * 					&lt;type&gt;cube&lt;/type&gt;    &lt;----------------------	The cube is placed according to its center point 
 * 																	(defined in the CenterPoint tag) 
 * 																	AND is rotated until that one of its edges is parallel
 * 																	(or approx parallel) to the reference line of the 3d Building
 * 																	The reference line of the Building, is automatically picked
 * 																	to be any Line from the Outline of the Buildings Base.
 * 																	(To do) Future work: type could also be:
 * 																	(+) "rectangle" : we would need length, width, height and use the
 * 																		"length dimension" as ref line.
 * 																	(+) "customDrawnInplace" : where we just dump the KML contents of
 * 																	this tag at the room position. (The room is described in real world
 *																	positioning co-ords).
 * 																	(To do) In the distant Far Far Future: Check for overlapping rooms,
 * 																	sensors outside the rooms, rooms overlapping with building etc.
 * 																	For now this (the checks) is assumed to be done by the GUI, or that
 * 																	at least a user/provider can fix it manually by making a new interface.
 * 					&lt;centerPoint&gt;&lt;/centerPoint&gt; 	&lt;-------------	The crossing of its base's diagonals. 
 * 					&lt;size1&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 					&lt;size2&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 					&lt;size3&gt;&lt;/size&gt;          &lt;----------------------	3 sizes interpreted according to room type
 * 				&lt;/roomPolygon&gt;
 * 				&lt;sensingDevices&gt;    &lt;-----------------------------	Motes. not sensors.
 * 					&lt;smartDev&gt;
 * 						&lt;id&gt;SensorNetworkId::moteid&lt;/id&gt;    &lt;----- 	unique id in the gateway (required)
 * 						&lt;pointCoords&gt;				&lt;----- 	co-ords are optional. If not defined, motes are scattered randomly
 * 														in the room. (Caution here, so that they won't overlap).
 * 														if a mote has mupltiple sensors (a usual case), and a query has
 * 														defined to get values from a number x of them, x &gt; 1, then the
 * 														sensors will be placed next to each other, at the mote's location.
 * 						&lt;/pointCoords&gt;
 * 					&lt;/smartDev&gt;
 * 						.
 * 						.
 * 						.
 * 				&lt;/sensingDevices&gt;
 * 			&lt;/room&gt;
 * 				.
 * 				.
 * 				.
 * 		&lt;/rooms&gt;
 * 	&lt;/interface&gt;
 *</pre>
 *
 *
 * @author antoniou
 */
public class Model3dInterfaceEntry {
    private Logger logger = Logger.getLogger(Model3dInterfaceEntry.class);
    private String gwId;
    private long intefaceId;
    private String description;
    private Model3dLineOfReference lineOfRef;
    private Vector<Model3dRoomEntry> roomsVec;
        
    private static final String gwIdTag = "gwid";    
    private static final String interfaceIdTag = "iid";    
    private static final String descriptionTag = "description";
    private static final String roomsListTag = "rooms";
    private static final String roomEntryTag = "room";
    private static final String lineOfRefTag = "lineOfRef";
    
    private static final long unknownInterfaceId = -1;
    private static final String unknownGatewayId = "unknown";
    private static final String defaultDescription = "noDescription";
    
    /** Creates a new however invalid instance of Model3dInterfaceEntry */
    public Model3dInterfaceEntry() {
        this.gwId = Model3dInterfaceEntry.getUnknownGatewayId();
        this.intefaceId = Model3dInterfaceEntry.getUnknownInterfaceId();
        this.description = Model3dInterfaceEntry.getDefaultDescription();
        this.lineOfRef = new Model3dLineOfReference();
        this.roomsVec = new Vector<Model3dRoomEntry>();        
    }
    
    public Model3dInterfaceEntry(String givGwId, long givInterfId, String givDesc, Model3dLineOfReference givLineOfRef, Vector<Model3dRoomEntry> givRoomsVec) {
        if(givDesc==null)
            this.gwId = Model3dInterfaceEntry.getUnknownGatewayId();
        else
            this.gwId = givGwId;
            
        this.intefaceId = givInterfId;
        
        if(givDesc==null)
            this.description = Model3dInterfaceEntry.getDefaultDescription();
        else
            this.description = givDesc;            
        
        if(givLineOfRef==null)
            this.lineOfRef = new Model3dLineOfReference();            
        else            
            this.lineOfRef = givLineOfRef;
        
        if(givRoomsVec==null)
            this.roomsVec =  new Vector<Model3dRoomEntry>(); 
        else
            this.roomsVec = givRoomsVec;
    }
    
    /** Creates a new valid instance of Model3dInterfaceEntry from an Input XML stream*/
    public Model3dInterfaceEntry(SMInputCursor givenCursor) {
        this.gwId = Model3dInterfaceEntry.getUnknownGatewayId();
        this.intefaceId = Model3dInterfaceEntry.getUnknownInterfaceId();
        this.description = Model3dInterfaceEntry.getDefaultDescription();
        this.lineOfRef = new Model3dLineOfReference();        
        this.roomsVec = new Vector<Model3dRoomEntry>();        
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getGwIdTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.gwId = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getInterfaceIdTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.intefaceId = Long.parseLong(childInElement2.getText());
                            break;
                        }
                    }                    
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getDescriptionTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.description = childInElement2.getText();
                            break;
                        }
                    }  
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getLineOfRefTag().toLowerCase() ) ) 
                {
                    this.lineOfRef = new Model3dLineOfReference(childInElement);
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getRoomsListTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childCursor();
                    while(childInElement2.getNext() != null)
                    {
                        if(!childInElement2.getCurrEvent().hasText() &&
                        childInElement2.getLocalName().toLowerCase().equals(this.getRoomEntryTag().toLowerCase() ) )
                            this.roomsVec.add(new Model3dRoomEntry(childInElement2));
                    }
                }
            }
        } catch(Exception e) {
            this.description = e.getMessage();
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
            tmpElement1 =  parElement.addElement(this.getGwIdTag());
            tmpElement1.addCharacters(this.getGwId());
            
            tmpElement1 =  parElement.addElement(this.getInterfaceIdTag());
            tmpElement1.addCharacters(Long.toString(this.getIntefaceId()) );

            tmpElement1 =  parElement.addElement(this.getDescriptionTag());            
            tmpElement1.addCharacters(this.getDescription());

            tmpElement1 =  parElement.addElement(this.getLineOfRefTag());
            this.getLineOfRef().createInfoInDocument(tmpElement1);
            
            if(this.getRoomsVec().size() > 0) {
                tmpElement1 =  parElement.addElement(this.getRoomsListTag());
                
                for(int i = 0; i < this.getRoomsVec().size(); i++) 
                {
                    tmpElement2 =  tmpElement1.addElement(this.getRoomEntryTag());
                    this.getRoomsVec().elementAt(i).createInfoInDocument(tmpElement2);
                }
            }
            
        } catch(Exception e) {
            return;
        }
    }    
    
    /**
     * Compares two Model3dInterfaceEntry objects.
     * We DON'T compare Interface descriptions OR Interface IDs since they are irrelevant!
     * @param targEntry the target Model3dInterfaceEntry to compare to
     * @return true if objects express the same Interface entry, or false otherwise
     */
    public boolean equals(Model3dInterfaceEntry targEntry) { 
    
        if(this.getGwId().equals(targEntry.getGwId()) ) 
        {
            //
            // check if lines of reference are the same too
            //
            if(! this.getLineOfRef().equals(targEntry.getLineOfRef()))
            {
                return false;
            }            
            //
            // check if vector of rooms is the same too.
            //
            Vector<Model3dRoomEntry> targetRoomsVec = targEntry.getRoomsVec();
            Vector<Model3dRoomEntry> tmpComparisonVec =  new Vector<Model3dRoomEntry>(targetRoomsVec);
            int i = 0;
            for(i = 0; i < this.getRoomsVec().size(); i++)
            {
                for(int j = 0;  tmpComparisonVec.size() > 0 && j < tmpComparisonVec.size() ; j++)
                {                
                    if(this.getRoomsVec().elementAt(i).equals(tmpComparisonVec.elementAt(j)))
                    {                    
                        tmpComparisonVec.removeElementAt(j);
                        j -=1;
                    }
                }
            }
            // if at the end, the iterator of the source Vector of smart devs has not the value of the size of the target vector ( in other words they are not of equal size)
            // or the clone of the target Vector of Rooms has still some unmatched elements then they are not equal          
            if( ( i !=  targetRoomsVec.size()) || (tmpComparisonVec.size()!= 0) )
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
     * @return  the XML String representing this Interface entry's XML fields
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
            outputRootEl = doc.addElement(Model3dMetafile.getInterfaceEntryTag());
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
    
    
    public String getDescription() {
        return description;
    }

    public static String getGwIdTag() {
        return gwIdTag;
    }

    public static String getDescriptionTag() {
        return descriptionTag;
    }

    public static String getInterfaceIdTag() {
        return interfaceIdTag;
    }

    public static String getRoomEntryTag() {
        return roomEntryTag;
    }

    public static String getRoomsListTag() {
        return roomsListTag;
    }

    public static String getLineOfRefTag() {
        return lineOfRefTag;
    }
    


    public String getGwId() {
        return gwId;
    }

    public long getIntefaceId() {
        return intefaceId;
    }

    public Vector<Model3dRoomEntry> getRoomsVec() {
        return roomsVec;
    }    

    public Model3dLineOfReference getLineOfRef() {
        return lineOfRef;
    }

    public static String getUnknownGatewayId() {
        return unknownGatewayId;
    }

    public static long getUnknownInterfaceId() {
        return unknownInterfaceId;
    }

    public static String getDefaultDescription() {
        return defaultDescription;
    }

    public void setIntefaceId(long intefaceId) {
        this.intefaceId = intefaceId;
    }



}
