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
 * Model3dIndex.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import vitro.vspEngine.service.common.ConfigDetails;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
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
import java.util.Vector;

/**
 *<pre>
 *&lt;Modelsindex&gt;
 *	&lt;metaEntry&gt;
 *		&lt;metafilename&gt;metaKML1.xml&lt;/metafilename&gt;
 *		&lt;modelfilename&gt;KML1.kml&lt;/modelfilename&gt;
 *		&lt;gatewayList&gt;
 * 			&lt;gateway&gt;
 *				&lt;id&gt;q&lt;/gatewayId&gt; &lt;-------------	This is the peer id for the Gateway. Many KML files can have the same	gatewayId.
 *											They will be merged if necessary.
 *											(Supposedly filled in by the Gateway that sent the file).
 *											(HOWEVER bear in mind that multiple Gateways could be inside a building)											
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
 *											directory (See bellow)
 *		.
 *		.
 *		.
 *	&lt;metaEntry&gt;
 *	&lt;/metaEntry&gt;
 *&lt;/Modelsindex&gt;
 *</pre>
 * @author antoniou
 */
public class Model3dIndex {

    private static Vector<Model3dIndexEntry> listofMetaEntriesVec;
    
    private static final String rootIndexTag = "Modelsindex";    
    private static final String metaEntryTag = "metaEntry";
    
    private static final int updateModeUnknown = -1;
    private static final int updateModeInsert = 0;
    private static final int updateModeUpdate = 1;

    //
    // private static final String mIndexPath = ConfigDetails.getConfigDetails().getPathToPeer()+File.separator+"Models"+File.separator;
    // Allow the Path to be set from the servlet
    private static String mIndexPath;

    //
    // 2. Should add new entries. BUT...
    // 3. should check for existing mappings between gateway and model file. If such mapping exists then the interface should be edited (append new interface) 
    //        but no duplicate mappings should exist.
    // 4. Should be written in a file, read from this file, and also write each Meta per model in a file,
    // 5. Assign interface ids in the meta files (?)
    //
    
    /** Creates a new instance of Model3dIndex */
    private Model3dIndex() {
        this.listofMetaEntriesVec = new Vector<Model3dIndexEntry>();
    }
    
    private static Model3dIndex myModel3dIndex = null;
    /**
     *
     * This is the function the world uses to get the Index Of Queries Definitions'.
     * It follows the Singleton pattern
     * 
     */ 
    public static Model3dIndex getModel3dIndex(){
        if(myModel3dIndex == null) {
             myModel3dIndex = new Model3dIndex();
             parseIndexFromFile();
        }
        return myModel3dIndex;
    }    
    
    /** 
     * Creates a new instance of Index Of Model Mappings read from the corresponding file
     * (Used for persistence)
     */
    public static void parseIndexFromFile() {
        // Read file and create the Index
        // check if file exists. If it exists open it and parse it. 
        // Else return
        //
        File inFile = new File(Model3dIndex.getIndexPath()+"modelIndex.xml");
        // error state check
        if(inFile.exists())
        {
            try{
                Model3dIndex.getListofAllMetaEntries().clear();
                FileReader tmpInReader = new FileReader(inFile);
                WstxInputFactory fin = new WstxInputFactory();
                fin.configureForConvenience();
                fin.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE); // <-- NEEDED TO GET ATTRIBUTES!
                // input
                XMLStreamReader2 sr = (XMLStreamReader2)fin.createXMLStreamReader(tmpInReader);
                SMInputCursor inputRootElement = SMInputFactory.rootElementCursor(sr);
                inputRootElement.getNext();
                SMInputCursor childInElement = inputRootElement.childCursor();
                String myText="";
                while (childInElement.getNext() != null) {
                    if(!childInElement.getCurrEvent().hasText() &&
                            childInElement.getLocalName().toLowerCase().equals(Model3dIndex.getMetaEntryTag().toLowerCase() ) ) 
                    {
                        Model3dIndex.getListofAllMetaEntries().add(new Model3dIndexEntry(childInElement));
                    }
                }
                tmpInReader.close();
            }
            catch(Exception e)
            {
                return;
            }
        }
        else
            return;
    }    
    
    /** 
     * Write the instance of IndexOfModelMappings back to the corresponding file
     * (Used for persistence)
     */
    public static synchronized void writeIndexBackToFile() {
         // Write file from the Vector
        File outFile = new File(Model3dIndex.getIndexPath()+"modelIndex.xml");
        try
        {
            outFile.createNewFile(); // will create it if it does not exist, otherwise will return false (we don't care)
            FileWriter tmpoutWriter = new FileWriter(outFile);
            WstxOutputFactory fout = new WstxOutputFactory();
            fout.configureForXmlConformance();
            SMOutputDocument doc = null;
            SMOutputElement outputRootEl = null;
            SMOutputElement outputRootEl2 = null;
            
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(tmpoutWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            outputRootEl = doc.addElement(Model3dIndex.getRootIndexTag());
            
            for(int i = 0; i < Model3dIndex.getListofAllMetaEntries().size(); i++) {
                outputRootEl2 =  outputRootEl.addElement(Model3dIndex.getMetaEntryTag());
                Model3dIndex.getListofAllMetaEntries().elementAt(i).createInfoInDocument(outputRootEl2);
            }
            
            doc.closeRoot();
            tmpoutWriter.close();
        }
        catch(Exception e)
        {
            return;
        }        
    }    


    
    /**
     * Handles creating a new entry for a modelfile-to-gateway mapping in the Model3dIndex and a new model metafile for this mapping (which contains all interfaces for the mapping)
     * If this is the first time this entry is created, the Model3dIndex is updated with the new entry and the newly created metafile will contain only one interface with id 0.
     * If this mapping already exists in the Model3dIndex, only the existing metafile will be updated, by adding a new interface with id equal to the maximum existing id increased by 1.
     * @param givModelFilename the filename for the mapped 3d Model
     * @param gwId the id for the mapped gateway 
     * @param gwName the name of the gateway (mostly needed for description purposes)
     * @param givInterfaceDesc a description string for the interface (namely the mapping instance)
     * @param givLineOfRef the line of reference in the 3d Model, as to which all rooms will be aligned
     * @param givRoomsVec the vector with the defined rooms
     * @param updatemode a switch which defines out action. Can be Model3dIndex.updateModeInsert (for inserting a new interface), or Model3dIndex.updateModeUpdate (for editing an existing interface)
     * @return true if successful, false otherwise.
     */    
    public static synchronized boolean addNewModelIndexEntry(String givModelFilename, String gwId, String gwName, String givInterfaceDesc, Model3dLineOfReference givLineOfRef, Vector<Model3dRoomEntry> givRoomsVec, int updatemode)
    {
        /** 
         *
         *  Two possible things could happen:
         *  if insert_mode
         *  {
         *      check if already exists
         *      if not: create entry, create metafilename, create metafile, write info to metafile, update this vector, write back to index file.
         *      if it does ------> append a new interface! <-----
         *  }
         *  if update_replace_mode
         *  {   (To do) Future work. When "edit interface" is implemented.
         *      check if already exists
         *      if not: revert to insert_mode
         *      if it does: parse entry, edit info in entry (gateway, kml file) (if kml filename is changed check for conflicts (if already exists in another entry etc),
         *                  open metafile, edit metafile (interfaces), write back metafile, write back index file.
         *  }
         *
         */        
        boolean successFlag = false; 
        Model3dIndexEntry existingEntry = checkforGivenEntry(givModelFilename);
        switch(updatemode)
        {
            case Model3dIndex.updateModeInsert:
            {
                String metaFileName = Model3dIndexEntry.getUndefinedMetaFilename();
                long newInterfaceId = Model3dInterfaceEntry.getUnknownInterfaceId();
                if(existingEntry!=null)
                {                   
                    // check if this gateway has an entry in the Model3dIndex. If it has none then add one, and set the above interface as default.
                     if(existingEntry.findGatewayEntry(gwId) == null)
                     {
                        // append a new GatewayEntry to this Model3dIndexEntry
                        newInterfaceId = 0;
                        Model3dMetaGatewayEntry tmpGwEntry = new Model3dMetaGatewayEntry(gwId, gwName, newInterfaceId);
                        existingEntry.getMetaGatewaysVec().addElement(tmpGwEntry);                        
                        Model3dIndex.writeIndexBackToFile();
                     }
                     else
                     {
                         newInterfaceId = Model3dInterfaceEntry.getUnknownInterfaceId(); // this will trigger a search for a proper new interface id in the appendInterfaceForGwId() method
                     }
                     successFlag = existingEntry.appendInterfaceForGwId(gwId, new Model3dInterfaceEntry(gwId,  newInterfaceId, givInterfaceDesc, givLineOfRef, givRoomsVec) );
                }
                else
                {                    
                    // create metafileName from ModelFilename  (add a suffix "meta" AND change the extension to .xml                    
                    // remove any path info from modelfilename
                    int lastslash = givModelFilename.lastIndexOf(File.separator);
                    if(lastslash!=-1 )
                    {
                        if( givModelFilename.length() > lastslash + 1)
                            givModelFilename = givModelFilename.substring(lastslash+1);
                        else 
                            return successFlag; // no filename was given, just a path
                    }
                    String justTheName = "";
                    // get purefilename - no extension
                    int lastdot = givModelFilename.lastIndexOf('.');
                    if(lastdot!=-1) 
                    {
                        if(lastdot == 0) // if it is the first char then ignore it and copy the entire givModelFilename string to the justthename string
                        {
                            justTheName = givModelFilename;
                        }
                        else
                            justTheName = givModelFilename.substring(0, lastdot);
                    }
                    else // no extension is given
                    {
                        justTheName = givModelFilename;
                    }
                    metaFileName = /*Model3dIndex.getIndexPath() + */  justTheName + "Meta.xml";
                    

                    // update the Model3dIndex
                    newInterfaceId = 0;
                    Vector<Model3dMetaGatewayEntry> tmpMappedGateways = new Vector<Model3dMetaGatewayEntry>();
                    long defaultInterfaceSetForGw = newInterfaceId;
                    tmpMappedGateways.add(new Model3dMetaGatewayEntry( gwId, gwName, defaultInterfaceSetForGw));                            
                    Model3dIndexEntry theNewlyMadeEntry = new Model3dIndexEntry(metaFileName, givModelFilename, tmpMappedGateways);                    
                    Model3dIndex.getListofAllMetaEntries().add(theNewlyMadeEntry);                    
                    Model3dIndex.writeIndexBackToFile();

                    // we want the default interface id for the gateway 
                    // if it's the first entry the interface index will be 0.
                    // else it will be the largest interface number + 1.                                        
                    successFlag = theNewlyMadeEntry.appendInterfaceForGwId(gwId, new Model3dInterfaceEntry(gwId, newInterfaceId, givInterfaceDesc, givLineOfRef, givRoomsVec));                    
                }
                break;
            }
            case Model3dIndex.updateModeUpdate:
            {
                // (To do) (add code) future work. When "edit interface" is implemented.
                break;
            }                
            default: return successFlag; 
        }        
        return successFlag; 
    }        
    
    
    /**
     * Deletes the specified interface from a model-gateway mapping.
     * Should take care of consistency issues such as:
     *  a) updating the default interface for the mapping (if we are deleting a default interface then the interface that will be set as default will be the one with the minimum id) and
     *  b) deleting the entire mapping, and the gateway entry from the Model3dIndexEntry if this is the last interface for the specified model-gateway mapping.
     * @param givModelFilename the filename of the model in the mapping
     * @param gwId the id of the gateway in the mapping
     * @param givIntefaceId the id of the interface to be deleted
     * @return true if successful, otherwise false.
     * (To do )(Add code here)
     */
    public static synchronized boolean deleteInterfaceEntry(String givModelFilename, String gwId, long givIntefaceId)
    {
        boolean successFlag = false; 
        Model3dIndexEntry existingEntry = checkforGivenEntry(givModelFilename);
        if(existingEntry!=null)
        {
            //
            // Remove the specified interface from the Metafile
            //
            existingEntry.removeInterfaceForGw(gwId, givIntefaceId);            
            //
            // IF its the only interface then delete the gateway entry from the Model3dIndexEntry as well.
            //
            Vector<Model3dInterfaceEntry> tmpInterfaceVector = existingEntry.getInterfacesVecForGwId(gwId);
            if(tmpInterfaceVector !=null && tmpInterfaceVector.isEmpty()) {
                existingEntry.deleteGatewayEntry(gwId);
                if(existingEntry.getMetaGatewaysVec().isEmpty()) {
                    // then delete Metafile AND the entry from the Model3dIndex
                    existingEntry.deleteMetafile();
                    Model3dIndex.deleteEntry(givModelFilename);
                    existingEntry = null;
                }
            } else if(givIntefaceId == existingEntry.getDefaultInterfaceIdForGwId(gwId)) {
                //
                // IF its the default interface then, change the default interface to a remaining interface with minimum id.
                //
                long newDefaultId = existingEntry.getMinInterfaceIdForGwId(gwId);
                existingEntry.setDefaultInterfaceIdForGwId(gwId, newDefaultId);
            }                       
            //
            // save changes to files.
            //
            Model3dIndex.writeIndexBackToFile();
            return true;
        }
        return false;
    }
    
    
    /**
     * Sets the specified interface entry as the default for the involved model-gateway mapping.
     * @param givModelFilename the filename of the model in the mapping
     * @param gwId the id of the gateway in the mapping
     * @param givIntefaceId the id of the interface to be deleted
     * @return true if successful, otherwise false.
     */
    public static synchronized boolean setDefaultInterfaceEntry(String givModelFilename, String gwId, long givIntefaceId)
    {
        boolean successFlag = false; 
        Model3dIndexEntry existingEntry = checkforGivenEntry(givModelFilename);
        if(existingEntry!=null)
        {
            //
            // get the vector with the interfaces for this gateway/model AND check if the specified interface (to be set as default) exists or not!
            //
            Vector<Model3dInterfaceEntry> tmpInterfaceVector = existingEntry.getInterfacesVecForGwId(gwId);
            if(givIntefaceId != existingEntry.getDefaultInterfaceIdForGwId(gwId)) {
                for(int i=0; i< tmpInterfaceVector.size(); i++)
                {
                    if(tmpInterfaceVector.elementAt(i).getIntefaceId() ==  givIntefaceId)
                    {
                        existingEntry.setDefaultInterfaceIdForGwId(gwId, givIntefaceId);
                        //
                        // save changes to Index.
                        //
                        Model3dIndex.writeIndexBackToFile();
                        successFlag = true;
                        break;
                    }
                }
            }
            else
            {
                successFlag = true;
            }
        }
        return successFlag;
    }


    /**
     *
     * (To do )(Add code here)
     */
    public Vector<Model3dIndexEntry> getIndexEntriesByGatewayId(String givGatewayId)
    {
        Vector<Model3dIndexEntry> toReturnVec = new Vector<Model3dIndexEntry>();
        for(int i = 0; i < this.getListofAllMetaEntries().size(); i++)
        {
           Model3dIndexEntry tmpIndexEntry = this.getListofAllMetaEntries().elementAt(i);
           Vector<Model3dMetaGatewayEntry> tmpMetaGwVec = tmpIndexEntry.getMetaGatewaysVec();
           for(int j = 0; j< tmpMetaGwVec.size(); j++)
           {
               if(tmpMetaGwVec.elementAt(j).getGwid().equals(givGatewayId)) 
               {
                   toReturnVec.add(tmpIndexEntry);
               }
           }           
        }
        return toReturnVec;
    }

    /**
     *
     * (To do )(Add code here)
     */
    public Model3dIndexEntry getIndexEntryByModelFilename(String givModelFilename)
    {
        for(int i = 0; i < this.getListofAllMetaEntries().size(); i++)
        {
           Model3dIndexEntry tmpIndexEntry = this.getListofAllMetaEntries().elementAt(i);
           if(tmpIndexEntry.getModelFileName().equals(givModelFilename))
               return tmpIndexEntry;
        }
        return null;
    }
    
    
    /**
     * Checks if there is an existing entry in the Model3dIndex with the same Model filename as the given one.
     * @param givModelFilename The model filename that will be matched against the existing entries in the Index.
     * @return null if no match was found, or the Model3dIndexEntry found else.
     */    
    private synchronized static Model3dIndexEntry checkforGivenEntry(String givModelFilename)
    {
        Model3dIndex.getModel3dIndex();        
        for(int i=0; i < Model3dIndex.getListofAllMetaEntries().size(); i++)
        {
            Model3dIndexEntry tmpIdxEntry = Model3dIndex.getListofAllMetaEntries().elementAt(i);
            if(tmpIdxEntry.getModelFileName().equals(givModelFilename))
            {
                return tmpIdxEntry;
            }
        }
        return null;
    }
        
    /**
     * Deletes an entry for a model-gateway mapping from the Model3dIndex. Requires a trailing call to Model3dIndex.writeIndexBackToFile() for the change to be persistent.
     * @param givModelFilename the entry to be deleted.
     * @return true if successful, false otherwise.
     */    
    public static synchronized boolean deleteEntry(String givModelFilename)
    {
         boolean returnStatus = false;
         for(int i = 0; i < listofMetaEntriesVec.size(); i++)
         {
             if(listofMetaEntriesVec.elementAt(i).getModelFileName().equals(givModelFilename) )
             {
                 listofMetaEntriesVec.removeElementAt(i);
                 break;
             }
         }
         return returnStatus;
    }
    
    //--------------------
    // simple getters
    
    public static Vector<Model3dIndexEntry> getListofAllMetaEntries() {
        return listofMetaEntriesVec;
    }
    
    public static String getMetaEntryTag() {
        return metaEntryTag;
    }

    public static String getRootIndexTag() {
        return rootIndexTag;
    }

    public static String getIndexPath() {
        return getmIndexPath();
    }


    public static String getmIndexPath() {
        return mIndexPath;
    }

    public static void setIndexPath(String mParamIndexPath) {
        // TODO:add also valid file path format check
        if(mParamIndexPath == null || mParamIndexPath.isEmpty() || mParamIndexPath.trim().equals(""))
            Model3dIndex.mIndexPath = ConfigDetails.getConfigDetails().getPathToPeer()+File.separator+"Models"+File.separator;
        else
            Model3dIndex.mIndexPath = mParamIndexPath;

    }

    public static int getUpdateModeInsert() {
        return updateModeInsert;
    }

    public static int getUpdateModeUpdate() {
        return updateModeUpdate;
    }
}
