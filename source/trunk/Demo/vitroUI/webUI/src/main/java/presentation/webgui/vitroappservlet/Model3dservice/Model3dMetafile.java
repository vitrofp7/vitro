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
 * Model3dMetafile.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Vector;

/**
 * metafile.xml : 	A metafile corresponds with a `1-1` relation to a 3d model file. (Large Static Model).
 *              TODO: support not setting nodes, but just rooms, in which case all nodes are assigned in the first room.
 *				The meta file is supposedly created by a GUI in a UserPeer, from the User that sent it. 
 *				It has an inherent association with a GATEWAY. Also, the motes are already placed in rooms.
 *				Subsequently it can, however, be updated by new interfaces defined e.g. by the service providers.
 *				An interface can be used among other things to:
 *				1. Reallocate motes in rooms
 *				2. Redefine room placement
 *				3. Redesign rooms
 *				4. Select different styles (this maybe should be left out)
 *				Every query goes through all the meta files defined in the KML_index, to find matching KMLs
 *				(Keep in mind matching KMLs could be more than one -so they should be eventually merged in the dynamic KML).
 *
 * Path: ./KML/meta/metafile.xml
 * 
 * 
 * <pre>
 * &lt;meta&gt;
 * 	&lt;interfacesDefined&gt;
 * 		&lt;interface&gt;
 * 			&lt;gwid&gt;q&lt;/gwid&gt;
 * 			&lt;iid&gt;1&lt;/iid&gt;
 * 			&lt;description&gt;Happy mode&lt;/description&gt;
 * 			&lt;rooms&gt;
 * 				&lt;room&gt;
 * 					&lt;name&gt;&lt;/name&gt;
 * 					&lt;roomPolygon&gt;
 * 						&lt;type&gt;cube&lt;/type&gt;    &lt;----------------------	The cube is placed according to its center point 
 * 																		(defined in the CenterPoint tag) 
 * 																		AND is rotated until that one of its edges is parallel
 * 																		(or approx parallel) to the reference line of the 3d Building
 * 																		The reference line of the Building, is automatically picked
 * 																		to be any Line from the Outline of the Buildings Base.
 * 																		(To do) Future work: type could also be:
 * 																		(+) "rectangle" : we would need length, width, height and use the
 * 																			"length dimension" as ref line.
 * 																		(+) "customDrawnInplace" : where we just dump the KML contents of
 * 																		this tag at the room position. (The room is described in real world
 *																		positioning co-ords).
 * 																		(To do) In the distant Far Far Future: Check for overlapping rooms,
 * 																		sensors outside the rooms, rooms overlapping with building etc.
 * 																		For now this (the checks) is assumed to be done by the GUI, or that
 * 																		at least a user/provider can fix it manually by making a new interface.
 * 						&lt;centerPoint&gt;&lt;/centerPoint&gt; 	&lt;-------------	The crossing of its base's diagonals. 
 * 						&lt;size1&gt;&lt;/size1&gt;          &lt;----------------------	3 sizes interpreted according to room type.
 * 						&lt;size2&gt;&lt;/size2&gt;          &lt;----------------------	3 sizes interpreted according to room type.
 * 						&lt;size3&gt;&lt;/size3&gt;          &lt;----------------------	3 sizes interpreted according to room type.
 * 					&lt;/roomPolygon&gt;
 * 					&lt;sensingDevices&gt;    &lt;-----------------------------	Motes. not sensors.
 * 						&lt;smartDev&gt;
 * 							&lt;id&gt;SensorNetworkId::moteid&lt;/id&gt;    &lt;----- 	unique id in the gateway (required)
 * 							&lt;co-ords&gt;							&lt;----- 	co-ords are optional. If not defined, motes are scattered randomly
 * 																		in the room. (Caution here, so that they won't overlap).
 * 																		if a mote has mupltiple sensors (a usual case), and a query has
 * 																		defined to get values from a number x of them, x &gt; 1, then the
 * 																		sensors will be placed next to each other, at the mote's location.
 * 								&lt;latitude&gt;&lt;/latitude&gt;
 * 								&lt;longitude&gt;&lt;/longitude&gt;
 * 							&lt;/co-ords&gt;
 * 						&lt;/smartDev&gt;
 * 							.
 * 							.
 * 							.
 * 					&lt;/sensingDevices&gt;
 * 				&lt;/room&gt;
 * 					.
 * 					.
 * 					.
 * 			&lt;/rooms&gt;
 * 		&lt;/interface&gt;
 * 			.
 * 			.
 * 			.
 * 	&lt;/interfacesDefined&gt;
 * &lt;/meta&gt;
 * </pre>
 * @author antoniou
 */
public class Model3dMetafile {
    private Logger logger = Logger.getLogger(Model3dMetafile.class);
    private Vector<Model3dInterfaceEntry> metaInterfacesVec;
    
    private static final String metaRootTag = "meta";
    private static final String interfacesDefinedTag = "interfacesDefined";
    private static final String interfaceEntryTag = "interface";
    
    /** Creates a new instance of Model3dMetafile */
    public Model3dMetafile(Vector<Model3dInterfaceEntry> givInterfacesVec) 
    {
        this.metaInterfacesVec = givInterfacesVec;
    }

    
    public Model3dMetafile(SMInputCursor givenCursor) {
        this.metaInterfacesVec = new Vector<Model3dInterfaceEntry>();        
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getInterfacesDefinedTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childCursor();
                    while(childInElement2.getNext() != null)
                    {
                        if(!childInElement2.getCurrEvent().hasText() &&
                        childInElement2.getLocalName().toLowerCase().equals(this.getInterfaceEntryTag().toLowerCase() ) )
                            this.metaInterfacesVec.add(new Model3dInterfaceEntry(childInElement2));
                    }
                }
            }
        } catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }        
        
    }    
    
    
   /**
     *
     * @param parElement the parent element  in the given XML document.
     *
     */
    public void createInfoInDocument(SMOutputElement parElement) {
        try{
            
            SMOutputElement tmpElement1;
            SMOutputElement tmpElement2;
            
            if(this.getMetaInterfacesVecForAllGwIds().size() > 0) {
                tmpElement1 =  parElement.addElement(this.getInterfacesDefinedTag());
                
                for(int i = 0; i < this.getMetaInterfacesVecForAllGwIds().size(); i++) 
                {
                    tmpElement2 =  tmpElement1.addElement(this.getInterfaceEntryTag());
                    this.getMetaInterfacesVecForAllGwIds().elementAt(i).createInfoInDocument(tmpElement2);
                }
            }
            
        } catch(Exception e) {
            return;
        }
    }
    
    /**
     * Compares two Model3dMetafile objects.
     * @param targEntry the target Model3dMetafile to compare to
     * @return true if objects express the same Metafile entry, or false otherwise
     */
    public boolean equals(Model3dMetafile targEntry) { 
        //
        // check if vector of Interfaces is the same too.
        //
        Vector<Model3dInterfaceEntry> targetInterfcsVec = targEntry.getMetaInterfacesVecForAllGwIds();
        Vector<Model3dInterfaceEntry> tmpComparisonVec2 =  new Vector<Model3dInterfaceEntry>(targetInterfcsVec);
        int k = 0;
        for(k = 0; k < this.getMetaInterfacesVecForAllGwIds().size(); k++) {
            for(int j = 0;  tmpComparisonVec2.size() > 0 && j < tmpComparisonVec2.size() ; j++) {
                if(this.getMetaInterfacesVecForAllGwIds().elementAt(k).equals(tmpComparisonVec2.elementAt(j))) {
                    tmpComparisonVec2.removeElementAt(j);
                    j -=1;
                }
            }
        }
        // if at the end, the iterator of the source Vector of smart devs has not the value of the size of the target vector ( in other words they are not of equal size)
        // or the clone of the target Vector of Rooms has still some unmatched elements then they are not equal
        if( ( k !=  targetInterfcsVec.size()) || (tmpComparisonVec2.size()!= 0) ) {
            return false;
        }        
        return true;
    }     

    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this Metafile entry's XML fields
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
            outputRootEl = doc.addElement(Model3dMetafile.getMetaRootTag());
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
    
    public synchronized void writeBackToFile(String FilenameAndPath) {
        File outFile = new File(FilenameAndPath);
        try
        {
            outFile.createNewFile(); // will create it if it does not exist, otherwise will return false (we don't care)
            FileWriter tmpoutWriter = new FileWriter(outFile);
            WstxOutputFactory fout = new WstxOutputFactory();
            fout.configureForXmlConformance();
            SMOutputDocument doc = null;
            SMOutputElement outputRootEl = null;
            
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(tmpoutWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            outputRootEl = doc.addElement(Model3dMetafile.getMetaRootTag());            
            createInfoInDocument(outputRootEl);            
            doc.closeRoot();
            tmpoutWriter.close();
        }
        catch(Exception e)
        {
            return;
        }
    }
    
    /** 
     * Creates a new instance of Metafile entry read from the corresponding file
     * (Used for persistence)
     */
    public static Model3dMetafile parseMetafileFromFile(String filenameAndPath) {
        // Read file and create the Metafile entry
        // check if file exists. If it exists open it and parse it. 
        // Else return
        //
        File inFile = new File(filenameAndPath);
        // error state check
        if(inFile.exists())
        {
            try{               
                // read from file
                FileReader tmpInReader = new FileReader(inFile);
                WstxInputFactory fin = new WstxInputFactory();
                fin.configureForConvenience();
                fin.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE); // <-- NEEDED TO GET ATTRIBUTES!
                // input
                XMLStreamReader2 sr = (XMLStreamReader2)fin.createXMLStreamReader(tmpInReader);
                SMInputCursor inputRootElement = SMInputFactory.rootElementCursor(sr);
                inputRootElement.getNext();
                Model3dMetafile metaFileToReturn = new Model3dMetafile(inputRootElement); 
                tmpInReader.close(); 
                return metaFileToReturn;
                
            }
            catch(Exception e)
            {
                return null;
            }
        }
        else
            return null;
    }    
    
    public Model3dInterfaceEntry findInterfaceEntry(String givenGwId, long interfaceIdGw)
    {
       for(int i = 0 ; i < this.getMetaInterfacesVecForAllGwIds().size(); i++)
       {
           if(this.getMetaInterfacesVecForAllGwIds().elementAt(i).getGwId().equals(givenGwId) &&
                   this.getMetaInterfacesVecForAllGwIds().elementAt(i).getIntefaceId() == interfaceIdGw)
               return this.getMetaInterfacesVecForAllGwIds().elementAt(i);
       }
       return null;
    }
    
    
    public static String getMetaRootTag() {
        return metaRootTag;
    }

    public static String getInterfaceEntryTag() {
        return interfaceEntryTag;
    }

    public static String getInterfacesDefinedTag() {
        return interfacesDefinedTag;
    }    
    
    /**
     *
     * This is the function to call when attempting updates to the Metafile for a model. 
     * It returns the member vector of all interfaces (for all gateways) stored in the Metafile
     *
     */
    public Vector<Model3dInterfaceEntry> getMetaInterfacesVecForAllGwIds() {
        return metaInterfacesVec;
    }

    
    /**
     * This function should only be called for retrieving information purposes. Changes in the vector of interfaces that this function returns are NOT reflected in the Metafile!
     *
     */
    public Vector<Model3dInterfaceEntry> getMetaInterfacesVecForGwId(String givenGwId) {
       Vector<Model3dInterfaceEntry> toReturnInterfacesVec = new Vector<Model3dInterfaceEntry>();
       for(int i = 0 ; i < this.getMetaInterfacesVecForAllGwIds().size(); i++)
       {
           if(this.getMetaInterfacesVecForAllGwIds().elementAt(i).getGwId().equals(givenGwId))
               toReturnInterfacesVec.addElement(this.getMetaInterfacesVecForAllGwIds().elementAt(i));
       }
       return toReturnInterfacesVec;
    }
    
}
