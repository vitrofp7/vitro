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
 * Model3dStyles.java
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
import java.util.Random;
import java.util.Vector;

/**
 *
 * <pre>
 * &lt;stylesIndex&gt;
 * 	&lt;style&gt;
 * 		&lt;id&gt;&lt;/id&gt;
 * 		&lt;suggestedforCapability&gt; 		&lt;------	Optional. Recommendation so that this style can better depict readings for this
 * 												capability. This implies that the styles are created from a GUI that knows
 * 												the available list of capabilities, and their exact naming.
 * 			Thermistor
 * 		&lt;/suggestedforCapability&gt;
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
 * 	&lt;style&gt;
 * 			.
 * 			.
 * 	&lt;/style&gt;
 * &lt;/stylesIndex&gt;
 * </pre>
 *
 * @author antoniou
 */
public class Model3dStylesList {
    
    private static Vector<Model3dStylesEntry> listofStyleEntriesVec;
    
    private static final String rootIndexTag = "stylesIndex";
    private static final String styleEntryTag = "styleEntry";
    
    //private static final String mIndexFilenameandPath = ConfigDetails.getConfigDetails().getPathToPeer()+File.separator+"Models"+File.separator+"stylesIndex.xml";
    // Allow the Path to be set from the servlet
    private static String mIndexFilenameandPath ;

    /** Creates a new instance of Model3dStylesList */
    public Model3dStylesList() {
        this.listofStyleEntriesVec = new Vector<Model3dStylesEntry>();
    }
    
    private static Model3dStylesList myModel3dStylesList = null;
    /**
     *
     * This is the function the world uses to get the Index Of Styles Definitions.
     * It follows the Singleton pattern
     *
     */
    public static Model3dStylesList getModel3dStylesList(){
        if(myModel3dStylesList == null) {
            myModel3dStylesList = new Model3dStylesList();
            parseStylesIndexFromFile();
        }
        return myModel3dStylesList;
    }
    
    /**
     * Creates a new instance of Index Of Styles read from the corresponding file
     * (Used for persistence)
     */
    public static void parseStylesIndexFromFile() {
        // Read file and create the Index
        // check if file exists. If it exists open it and parse it.
        // Else return
        //
        File inFile = new File(Model3dStylesList.getIndexFilenameandPath());
        // error state check
        if(inFile.exists()) {
            try{
                Model3dStylesList.getListofStyleEntriesVec().clear();
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
                            childInElement.getLocalName().toLowerCase().equals(Model3dStylesList.getStyleEntryTag().toLowerCase() ) ) {
                        Model3dStylesList.getListofStyleEntriesVec().add(new Model3dStylesEntry(childInElement));
                    }
                }
                tmpInReader.close();
            } catch(Exception e) {
                return;
            }
        } else
            return;
    }
    
    /**
     * Write the instance of Index Of Styles back to the corresponding file
     * (Used for persistence)
     */
    public static synchronized void writeIndexBackToFile() {
        // Write file from the Vector
        File outFile = new File(Model3dStylesList.getIndexFilenameandPath());
        try {
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
            outputRootEl = doc.addElement(Model3dStylesList.getRootIndexTag());
            
            for(int i = 0; i < Model3dStylesList.getListofStyleEntriesVec().size(); i++) {
                outputRootEl2 =  outputRootEl.addElement(Model3dStylesList.getStyleEntryTag());
                Model3dStylesList.getListofStyleEntriesVec().elementAt(i).createInfoInDocument(outputRootEl2);
            }            
            doc.closeRoot();
            tmpoutWriter.close();
        } catch(Exception e) {
            return;
        }
    }

    public static String getmIndexFilenameandPath() {
        return mIndexFilenameandPath;
    }

    public static void setIndexFilenameandPath(String mParamIndexFilenameandPath) {
        // TODO:add also valid file path format check
        if(mIndexFilenameandPath == null || mIndexFilenameandPath.isEmpty() || mIndexFilenameandPath.trim().equals(""))
            Model3dStylesList.mIndexFilenameandPath  = ConfigDetails.getConfigDetails().getPathToPeer()+File.separator+"Models"+File.separator+"stylesIndex.xml" ;
        else
            Model3dStylesList.mIndexFilenameandPath = mParamIndexFilenameandPath;

    }

    /**
     *
     * (To do )(Add code here)
     */    
    public synchronized boolean addNewStyleEntry(String givStyleId, String givForCapab, String givGlobColor, String givGlobIconFilename, String givGlobPrefabFilename, Vector<Model3dStyleSpecialCase> givSpecialCasesVec, Vector<Model3dStyleNumericCase> givNumericCasesVec)
    {       
        /** 
         *  if givStyleId == null or ""
         *  {
         *      check if identical style already exists.
         *      if not: create entry.
         *      if it does return true;
         *  }
         *  if givStyleId not null and not "" (update_mode) (To do) Future work.
         *  {   
         *      check if indicated style entry exists (by id)
         *      if not: return false;
         *      if it does: get entry, edit it and put it back in the vector.
         *  }
         *
         */        
        boolean successFlag = false; 
        if(givStyleId==null || givStyleId=="")
        {
            //
            // Create new uId for the new candidate entry
            //
            String candidateNewStyleId = "0012";
            do            
            {
                Random r = new Random();
                candidateNewStyleId = Long.toString(Math.abs(r.nextLong()), 36);
            }
            while((!Model3dStylesList.getListofStyleEntriesVec().isEmpty()) && (Model3dStylesList.containsIdKey(candidateNewStyleId)));
            
            // (to do) +++ add code about filename validity, if they exist etc...
            Model3dStylesEntry tmpCandStyleEntry = new Model3dStylesEntry( candidateNewStyleId, givForCapab, givGlobColor, givGlobIconFilename, givGlobPrefabFilename, givSpecialCasesVec,  givNumericCasesVec);
            // check if this createdEntry is identical to an existing one, and if not then add it to the StylesList.
            // otherwise just return true;
            if(Model3dStylesList.existsStyleEntry(tmpCandStyleEntry))
            {
               successFlag = true;
            }
            else
            {
                // Append the new entry, write back to styles file and then return true
                Model3dStylesList.getListofStyleEntriesVec().add(tmpCandStyleEntry);                    
                Model3dStylesList.writeIndexBackToFile();
                successFlag = true;
            }
            
        }
        else // if there is an givStyleId 
        {
            successFlag = false; // update mode. Future work. For now just return false;
        }
        return successFlag; 
    }
    
    /**
     * This is used to delete a style entry from the list of styles
     * @param givStyleid id of the style to be deleted.
     * @return true if successful, false otherwise.
     */
    public static synchronized boolean deleteStyleEntry(String givStyleid)
    {
        boolean successFlag = false; 
        for(int i = 0 ; i < Model3dStylesList.getListofStyleEntriesVec().size(); i++)
        {
            if(Model3dStylesList.getListofStyleEntriesVec().elementAt(i).getStyleId().equals(givStyleid))
            {
                Model3dStylesList.getListofStyleEntriesVec().removeElementAt(i);
                Model3dStylesList.writeIndexBackToFile();
                successFlag = true;
                break;
            }
        }
        return successFlag; 
    }
    
    public static Vector<Model3dStylesEntry> getEntriesForCap(String cap)
    {
        Vector<Model3dStylesEntry>  tmpToReturnVec = new Vector<Model3dStylesEntry>();
        for(int i = 0 ; i < getListofStyleEntriesVec().size(); i++)
        {
            if(getListofStyleEntriesVec().elementAt(i).getCorrCapability().equals(cap))
            {
                tmpToReturnVec.addElement(getListofStyleEntriesVec().elementAt(i));
            }
        }
        return tmpToReturnVec;
    }
    
    public static Model3dStylesEntry getStyleWithId(String uStyleId)
    {
        for(int i = 0 ; i < getListofStyleEntriesVec().size(); i++)
        {
            if(getListofStyleEntriesVec().elementAt(i).getStyleId().equals(uStyleId))
            {
                return getListofStyleEntriesVec().elementAt(i);
            }
        }
        return null;
    }
    
    public static Vector<Model3dStylesEntry> getListofStyleEntriesVec() {
        return listofStyleEntriesVec;
    }
    
    public static String getStyleEntryTag() {
        return styleEntryTag;
    }
    
    public static String getRootIndexTag() {
        return rootIndexTag;
    }
    
    public static String getIndexFilenameandPath() {
        return getmIndexFilenameandPath();
    }
    
    public static boolean containsIdKey(String candIdKey)
    {
        for(int i=0; i < Model3dStylesList.getListofStyleEntriesVec().size(); i++)
        {
            if(Model3dStylesList.getListofStyleEntriesVec().elementAt(i).getStyleId().equals(candIdKey))
                return true;
        }
        return false;
    }
    
    public static boolean existsStyleEntry(Model3dStylesEntry candStyleEntry)
    {
        for(int i=0; i < Model3dStylesList.getListofStyleEntriesVec().size(); i++)
        {
            if(Model3dStylesList.getListofStyleEntriesVec().elementAt(i).equals(candStyleEntry))
                return true;
        }
        return false;
    }
    
}
