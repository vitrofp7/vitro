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
 * Model3dIndexEntry.java
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

import java.io.File;
import java.io.StringWriter;
import java.util.Vector;

/**
 * <pre>
 *	&lt;metaEntry&gt;
 *		&lt;metafilename&gt;metaKML1.xml&lt;/metafilename&gt;
 *		&lt;modelfilename&gt;KML1.kml&lt;/modelfilename&gt;
 *		&lt;gatewayList&gt;
 * 			&lt;gateway&gt;
 *				&lt;id&gt;q&lt;/gatewayId&gt; &lt;-------------	This is the peer id for the Gateway. Many KML files can have the same	gatewayId.
 *											They will be merged if necessary.
 *											(Supposedly filled in by the Gateway that sent the file).
 *											(HOWEVER keep in mind that multiple Gateways could be inside a building)
 *				&lt;name&gt;Bobos&lt;/gatewayName&gt; &lt;-----	This is the peer name for the Gateway. If we can require it to be unique it can
 *											assist us in "searching" appropriate KML files.
 *											(Supposedly filled in by the Gateway that sent the file).
 *				&lt;defaultInterface&gt;1&lt;/defaultInterface&gt; &lt;------------------------	The default interface for this KML and this gateway.
 *			&lt;/gateway&gt;
 *				.
 *				.
 *				.		    
 *		&lt;/gatewayList&gt;												
 * 	&lt;/metaEntry&gt; 	&lt;----------	indicates a metafile name describing a KML. Meta files are stored inside the .\KML\meta
 * </pre>	
 * @author antoniou
 */
public class Model3dIndexEntry {
    private Logger logger = Logger.getLogger(Model3dIndexEntry.class);
    private String metaFileName;
    private String modelFileName;
    
    private Vector<Model3dMetaGatewayEntry> metaGatewaysVec;

    private static final String metaFilenameTag = "metaFilename";    
    private static final String modelFilenameTag = "modelFilename";    
    private static final String gwListTag = "gatewayList";
    private static final String gwEntryTag = "gateway";
    
    private static final String undefinedModelFilename = "unknown";
    private static final String undefinedMetaFilename = "unknown";

    /** Creates a new invalid instance of Model3dIndexEntry */
    public Model3dIndexEntry()
    {
        this.metaFileName = Model3dIndexEntry.getUndefinedMetaFilename();
        this.modelFileName = Model3dIndexEntry.getUndefinedModelFilename();
        this.metaGatewaysVec = new Vector<Model3dMetaGatewayEntry>();
    }
    
    /** Creates a new instance of Model3dIndexEntry */
    public Model3dIndexEntry(String givMetaFileName, String givModelFileName, Vector<Model3dMetaGatewayEntry> givMappedGateways) {
        this.metaFileName = givMetaFileName;
        this.modelFileName = givModelFileName;
        this.metaGatewaysVec = givMappedGateways;
    }
    
    /** Creates a new valid instance of Model3dInterfaceEntry from an Input XML stream*/
    public Model3dIndexEntry(SMInputCursor givenCursor) {
        this.metaFileName = Model3dIndexEntry.getUndefinedMetaFilename();
        this.modelFileName = Model3dIndexEntry.getUndefinedModelFilename();
        this.metaGatewaysVec = new Vector<Model3dMetaGatewayEntry>();
        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().compareToIgnoreCase(this.getMetaFilenameTag()) == 0 )
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.metaFileName = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().compareToIgnoreCase(this.getModelFilenameTag()) == 0 )
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.modelFileName = childInElement2.getText();
                            break;
                        }
                    }
                }                                
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().compareToIgnoreCase(this.getGwListTag() ) == 0 ) {
                    SMInputCursor childInElement2 = childInElement.childCursor();
                    while(childInElement2.getNext() != null) {
                        if(!childInElement2.getCurrEvent().hasText() &&
                                childInElement2.getLocalName().compareToIgnoreCase(this.getGwEntryTag() ) == 0)
                            this.metaGatewaysVec.add(new Model3dMetaGatewayEntry(childInElement2));
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
            
            tmpElement1 =  parElement.addElement(this.getMetaFilenameTag());
            tmpElement1.addCharacters(this.getMetaFileName());

            tmpElement1 =  parElement.addElement(this.getModelFilenameTag());
            tmpElement1.addCharacters(this.getModelFileName());
            
            if(this.getMetaGatewaysVec().size() > 0) {
                tmpElement1 =  parElement.addElement(this.getGwListTag());
                
                for(int i = 0; i < this.getMetaGatewaysVec().size(); i++) 
                {
                    tmpElement2 =  tmpElement1.addElement(this.getGwEntryTag());
                    this.getMetaGatewaysVec().elementAt(i).createInfoInDocument(tmpElement2);
                }
            }
            
        } catch(Exception e) {
            return;
        }
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
            outputRootEl = doc.addElement(Model3dIndex.getMetaEntryTag());
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
     * Compares two Model3dIndexEntrye objects.
     * @param targEntry the target Model3dIndexEntry to compare to
     * @return true if objects express the same Index Entry , or false otherwise
     */
    public boolean equals(Model3dIndexEntry targEntry) {
        if(this.getModelFileName().compareToIgnoreCase(targEntry.getModelFileName()) == 0 &&
                this.getMetaFileName().compareToIgnoreCase(targEntry.getMetaFileName()) == 0 )
        {
            //
            // check if vector of gateways is the same too.
            //
            Vector<Model3dMetaGatewayEntry> targetGwsVec = targEntry.getMetaGatewaysVec();
            Vector<Model3dMetaGatewayEntry> tmpComparisonVec =  new Vector<Model3dMetaGatewayEntry>(targetGwsVec);
            int i = 0;
            for(i = 0; i < this.getMetaGatewaysVec().size(); i++) {
                for(int j = 0;  tmpComparisonVec.size() > 0 && j < tmpComparisonVec.size() ; j++) {
                    if(this.getMetaGatewaysVec().elementAt(i).equals(tmpComparisonVec.elementAt(j))) {
                        tmpComparisonVec.removeElementAt(j);
                        j -=1;
                    }
                }
            }
            // if at the end, the iterator of the source Vector of smart devs has not the value of the size of the target vector ( in other words they are not of equal size)
            // or the clone of the target Vector of Rooms has still some unmatched elements then they are not equal
            if( ( i !=  targetGwsVec.size()) || (tmpComparisonVec.size()!= 0) ) {
                return false;
            }
            return true;
        } else
            return false;
    }
    
    /**
     *
     * @return false if there is no such mapping defined or true else.
     */
    public boolean checkForGivenMapping(String givGwId)
    {
        for(int j=0; j < this.getMetaGatewaysVec().size(); j++) {
            if( this.getMetaGatewaysVec().elementAt(j).getGwid().compareToIgnoreCase(givGwId) == 0) {
                return true;
            }
        }
        return false;
    }

    public String getMetaFileName() {
        return metaFileName;
    }
    
    public String getModelFileName() {
        return modelFileName;
    }

    public Vector<Model3dMetaGatewayEntry> getMetaGatewaysVec() {
        return metaGatewaysVec;
    }

    public static String getModelFilenameTag() {
        return modelFilenameTag;
    }
    
    public static String getMetaFilenameTag() {
        return metaFilenameTag;
    }

    public static String getGwEntryTag() {
        return gwEntryTag;
    }

    public static String getGwListTag() {
        return gwListTag;
    }

    public static String getUndefinedMetaFilename() {
        return undefinedMetaFilename;
    }

    public static String getUndefinedModelFilename() {
        return undefinedModelFilename;
    }

    /**
     * Finds the default interface for a given mapping (model-gateway)
     * @param givGwId the gateway id of the gateway in the mapping.
     * @return the Interface Id for this model-gateway mapping or Model3dMetaGatewayEntry.getNoDefaultInterfaceDefined() if no default interface exists for this mapping.
     */
    public long getDefaultInterfaceIdForGwId(String givGwId)
    {
        long interfaceIdtoReturn = Model3dMetaGatewayEntry.getNoDefaultInterfaceDefined();
        Model3dMetaGatewayEntry tmpGwEntry = findGatewayEntry(givGwId);
        if(tmpGwEntry!=null) {
                interfaceIdtoReturn = tmpGwEntry.getDefaultInterface();
        }
        return interfaceIdtoReturn;
    }
    
    /**
     * Finds the maximum interface id for a given mapping (model-gateway)
     * @param givGwId the gateway id of the gateway in the mapping.
     * @return the maximum Interface Id for this model-gateway mapping or Model3dInterfaceEntry.getUnknownInterfaceId() if no interfaces exist for this mapping.
     */    
    public long getMaxInterfaceIdForGwId(String givGwId)
    {
        long maxInterfaceIdtoReturn = Model3dInterfaceEntry.getUnknownInterfaceId();
        Model3dMetaGatewayEntry tmpGwEntry = findGatewayEntry(givGwId);
        if(tmpGwEntry!=null) {
            // then there is a metafilename for this. (the above check might be redundant)
            Vector<Model3dInterfaceEntry> tmpVecOfInterfaces = getInterfacesVecForGwId(givGwId) ;
            if(tmpVecOfInterfaces !=null ) {
                for(int k=0; k< tmpVecOfInterfaces.size(); k++) {
                    long tmpCurrInterfaceforGw = tmpVecOfInterfaces.elementAt(k).getIntefaceId();
                    if(tmpCurrInterfaceforGw > maxInterfaceIdtoReturn )
                        maxInterfaceIdtoReturn  = tmpCurrInterfaceforGw;
                }
            }
        }
        return maxInterfaceIdtoReturn;
    }
    
     /**
     * Finds the minimum interface id for a given mapping (model-gateway)
     * @param givGwId the gateway id of the gateway in the mapping.
     * @return the minimum Interface Id for this model-gateway mapping, or Model3dInterfaceEntry.getUnknownInterfaceId() if no interfaces exist for this mapping.
     */    
    public long getMinInterfaceIdForGwId(String givGwId)
    {
        long minInterfaceIdtoReturn = Model3dInterfaceEntry.getUnknownInterfaceId();
        Model3dMetaGatewayEntry tmpGwEntry = findGatewayEntry(givGwId);
        if(tmpGwEntry!=null) {
            // then there is a metafilename for this. (the above check might be reduntant)
            Vector<Model3dInterfaceEntry> tmpVecOfInterfaces = getInterfacesVecForGwId(givGwId) ;
            if(tmpVecOfInterfaces !=null ) {
                for(int k=0; k< tmpVecOfInterfaces.size(); k++) {
                    long tmpCurrInterfaceforGw = tmpVecOfInterfaces.elementAt(k).getIntefaceId();
                    if(k==0)
                        minInterfaceIdtoReturn  = tmpCurrInterfaceforGw;
                    else if(tmpCurrInterfaceforGw < minInterfaceIdtoReturn )
                        minInterfaceIdtoReturn  = tmpCurrInterfaceforGw;
                }
            }
        }
        return minInterfaceIdtoReturn;
    }   
    
    
    /**
     * Checks if this model is associated with the specified gateway
     * @param givGwId the gateway id of the gateway in the mapping.
     * @return a Model3dMetaGatewayEntry object if a mapping exists, and null otherwise.
     */
    public Model3dMetaGatewayEntry findGatewayEntry(String givGwId)
    {
        boolean gatewayEntryFound = false;
        for(int j=0; j < this.getMetaGatewaysVec().size(); j++) {
            if( this.getMetaGatewaysVec().elementAt(j).getGwid().compareToIgnoreCase(givGwId)==0) {
                 gatewayEntryFound = true;
                 return this.getMetaGatewaysVec().elementAt(j);
            }
        }            
        return null;
    }
    
    /**
     * Gets the vector of interfaces for a given mapping (model-gateway)
     * @param givGwId the gateway id of the gateway in the mapping.
     * @return a vector of interfaces for this model-gateway mapping or an empty vector if no interfaces are defined!
     */    
    public Vector<Model3dInterfaceEntry> getInterfacesVecForGwId(String givGwId)
    {
        Vector<Model3dInterfaceEntry> tmpVecToReturn = new Vector<Model3dInterfaceEntry>();
        Model3dMetaGatewayEntry tmpGwEntry = findGatewayEntry(givGwId);
        if(tmpGwEntry!=null) {
            String tmpMetaFilenameFullPath =  Model3dIndex.getIndexPath() + this.getMetaFileName();
            Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
            if(tmpMetaFile!=null && !tmpMetaFile.getMetaInterfacesVecForGwId(givGwId).isEmpty()) {
                tmpVecToReturn = tmpMetaFile.getMetaInterfacesVecForGwId(givGwId);
            }            
        }
        return  tmpVecToReturn;
    }
    
    /**
     * Appends a new Interface for the given mapping (model-gateway). 
     * @param givGwId the gateway id of the gateway in the mapping.
     * @param givInterfaceNew the new interface to append
     * @return true if successful, false otherwise.
     */    
    public synchronized boolean appendInterfaceForGwId(String givGwId,  Model3dInterfaceEntry givInterfaceNew)
    {
        Model3dMetaGatewayEntry tmpGwEntry = findGatewayEntry(givGwId);
        // we want the default interface id for the gateway
        // since it will be the largest interface number + 1.
        if(givInterfaceNew.getIntefaceId() == Model3dInterfaceEntry.getUnknownInterfaceId())
        {
            long newInterfaceId = getMaxInterfaceIdForGwId( givGwId);
            if(newInterfaceId == Model3dInterfaceEntry.getUnknownInterfaceId() && tmpGwEntry!=null)
                return false;
            else if(newInterfaceId == Model3dInterfaceEntry.getUnknownInterfaceId() && tmpGwEntry==null)
            {
                newInterfaceId = 0;
            }
            else
                newInterfaceId += 1;
            givInterfaceNew.setIntefaceId(newInterfaceId);
        }
        //get interfaceVec from Metafile.        
        Vector<Model3dInterfaceEntry> tmpVecOfInterfaces = new Vector<Model3dInterfaceEntry>();
        String tmpMetaFilenameFullPath =  Model3dIndex.getIndexPath() + this.getMetaFileName();
        Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
        if(tmpMetaFile==null) // if metafile does not exist then create it!
        {
            // Create the pertinent metaFile now
            tmpMetaFile = new Model3dMetafile(tmpVecOfInterfaces);
        }
        tmpVecOfInterfaces = tmpMetaFile.getMetaInterfacesVecForAllGwIds();
        tmpVecOfInterfaces.addElement(givInterfaceNew);
        tmpMetaFile.writeBackToFile(tmpMetaFilenameFullPath);

        return true;
    }
    
    /**
     * Sets the specified-by-its-id interface to be the default. Requires a trailing call to Model3dIndex.writeIndexBackToFile() for the change to be persistent.
     * @param givGwId the gateway id of the gateway in the mapping.
     * @param givInterfaceId the id of the inerface we want to set as default
     * @return true if successful, false otherwise.
     */
     public synchronized boolean setDefaultInterfaceIdForGwId(String givGwId, long givInterfaceId)
     {
        boolean returnStatus = false;        
        Vector<Model3dInterfaceEntry> tmpVecofInterfaces = new Vector<Model3dInterfaceEntry>();
        String tmpMetaFilenameFullPath =  Model3dIndex.getIndexPath() + this.getMetaFileName();
        Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
        tmpVecofInterfaces = tmpMetaFile.getMetaInterfacesVecForGwId(givGwId);
        for(int i = 0 ; i< tmpVecofInterfaces.size(); i++)
        {
            if(tmpVecofInterfaces.elementAt(i).getIntefaceId() == givInterfaceId)
            { // we have confirmed that such an interface exists
                Model3dMetaGatewayEntry tmpGwEntry = findGatewayEntry(givGwId);
                if(tmpGwEntry!=null)
                {
                    // we get the gateway entry in the pertinent Model3dIndexEntry.
                    tmpGwEntry.setDefaultInterface(givInterfaceId);
                    returnStatus = true;
                }
                break;
            }            
        }
        return returnStatus;
     }
     
     /**
      *
      * Deletes an interface from the pertinent metafile
      * @param givGwId the gateway id of the gateway in the mapping.
      * @param givInterfaceId the id of the inerface we want to remove.
      * @return true if successful, false otherwise.
      */
     public synchronized boolean removeInterfaceForGw(String givGwId, long givInterfaceId)
     {
        boolean returnStatus = false;  
        Vector<Model3dInterfaceEntry> tmpVecofInterfaces = new Vector<Model3dInterfaceEntry>();
        String tmpMetaFilenameFullPath = Model3dIndex.getIndexPath() + this.getMetaFileName();
        Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
        tmpVecofInterfaces = tmpMetaFile.getMetaInterfacesVecForAllGwIds();
        for(int i = 0 ; i< tmpVecofInterfaces.size(); i++)
        {
            if(tmpVecofInterfaces.elementAt(i).getIntefaceId() == givInterfaceId && tmpVecofInterfaces.elementAt(i).getGwId().compareToIgnoreCase(givGwId)==0)
            { // we have confirmed that such an interface exists
                tmpVecofInterfaces.removeElementAt(i);
                tmpMetaFile.writeBackToFile(tmpMetaFilenameFullPath);
                returnStatus = true;
                break;
            }            
        }
        return returnStatus;
    }
     
     /**
      * Deletes the entry for a specified gateway. Requires a trailing call to Model3dIndex.writeIndexBackToFile() for the change to be persistent.
      * @param givGwId the gateway id of the gateway in the mapping.
      * @return true if successful, false otherwise.
      */
     public synchronized boolean deleteGatewayEntry(String givGwId)
     {
         boolean returnStatus = false;
         Vector<Model3dMetaGatewayEntry> tmpVecofGateways = this.getMetaGatewaysVec();
         for(int i = 0 ; i< tmpVecofGateways.size(); i++)
         {
            if(tmpVecofGateways.elementAt(i).getGwid().compareToIgnoreCase(givGwId) == 0)
            { // we have confirmed that such an interface exists
                tmpVecofGateways.removeElementAt(i);
                returnStatus = true;
                break;
            }            
         }
         return returnStatus;
     }    
     
     /**
      * Deletes the metafile for this entry. (should be done in conjuction with a deletion of the Model3dIndexEntry).
      * @return true if successful, false otherwise.
      */
     public synchronized boolean deleteMetafile()
     {
         boolean returnStatus = false;
         String tmpMetaFilenameFullPath =  Model3dIndex.getIndexPath() + this.getMetaFileName();
         File metaFile = new File(tmpMetaFilenameFullPath);
         if(metaFile.exists())
         {
             returnStatus = metaFile.delete();
         }
         else
             returnStatus = true;
         
         return returnStatus;
     }    
     
}
