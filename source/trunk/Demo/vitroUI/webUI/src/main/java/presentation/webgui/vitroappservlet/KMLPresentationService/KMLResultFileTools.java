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
 * KMLResultFileTools.java
 *
 */

package presentation.webgui.vitroappservlet.KMLPresentationService;

import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.query.QueryDefinition;
import vitro.vspEngine.service.query.ReqFunctionOverData;
import vitro.vspEngine.service.engine.UserNode;
import presentation.webgui.vitroappservlet.Model3dservice.*;
import presentation.webgui.vitroappservlet.StaxHelper.SMTools;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMBufferedFragment;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author antoniou
 */
public class KMLResultFileTools {
        
    
    
    public static void previewMergedModelandInterface(HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM, HashMap<Gateway, Vector<SmartNode>> myGwToSmDevsHM, HttpServletResponse response, String realPath,  UserNode myssUN )
    {
        mergeModelsAndAddFolders(allModelFilesToInterfacesHM, myGwToSmDevsHM, null, response, realPath, "", myssUN);
    }
    
    // ++++++ also add code to skip name tags directly under document... and put a name tag of its own...e.g. VisualResults.kml
    // +++++ !!!! create extra folders INSIDE the document, for each file (with the name tag of each file) so as to be obvious which model is which.
    // translate interfaces to KML folders under the document elements!
    // STaxMate does not put attributes in buffered fragments so we parse the file a second time for the styles at the moment!
    //
    // for the styles we could create a buffered element to contain all the styles, and keep a vector with unique syle ids (which are a parameteer in the style tag!) so as not to repeat styles
    // styles should be placed under the document folder, while all else, under a new folder created for each file. 
    public static void mergeModelsAndAddFolders(HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM, HashMap<Gateway, Vector<SmartNode>> myGwToSmDevsHM, QueryDefinition qdef, HttpServletResponse response, String realPath, String requestFullUrlPath, UserNode myssUN )
    {
        PrintWriter outPrintWriter = null;
    
        //
        // open an output XML stream
        //
        WstxOutputFactory fout = new WstxOutputFactory();    
        fout.configureForXmlConformance();
        
        SMOutputDocument doc = null;
        SMOutputElement outputRootEl = null;
        SMOutputElement outputDocumentEl = null;
        //Vector<String> uniqueStyleIdsVec = new Vector<String>();
        SMBufferedFragment uniqueMergedStylesFrag = null;
        try{  
            //response.setContentType("text/xml");  // for debug
            //response.setHeader("Content-disposition","attachment; filename=\"visualResults.xml\""); // for debug
            response.setContentType("text/kml");
            //response.setHeader("Content-disposition","attachment; filename=\"visualResults.kml\""); // for debug
            response.setHeader("Content-disposition","inline; filename=\"visualResults.kml\"");
            response.setHeader("Pragma", "public"); //HTTP 1.0 
            response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
            
            outPrintWriter = response.getWriter();
        // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outPrintWriter);   
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);        
        //
        // Add under <document> the info from ALL the interfaces
        //                
        // Copy the rest of the documents (specified by the filenames) inside the output stream.        
            Set<String> tmpFilenameKeys = allModelFilesToInterfacesHM.keySet();
            Iterator<String> itFilenames = tmpFilenameKeys.iterator();
            int fileCounter = 0;
            while(itFilenames.hasNext()) {                
                String tmpFilename = itFilenames.next();
                //outPrintWriter.print(realPath+File.separator+"Models"+File.separator+"Large"+File.separator+ tmpFilenamerealPath+File.separator+"KML"+File.separator+ tmpFilename);
                File inFile = new File(realPath+File.separator+"Models"+File.separator+"Large"+File.separator+ tmpFilename);
                FileReader tmpInReader = new FileReader(inFile);
                WstxInputFactory fin = new WstxInputFactory();
                fin.configureForConvenience();
                fin.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE); // <-- NEEDED TO GET ATTRIBUTES!
                // input
                XMLStreamReader2 sr = (XMLStreamReader2)fin.createXMLStreamReader(tmpInReader);
                SMInputCursor inputRootElement = SMInputFactory.rootElementCursor(sr);
                inputRootElement.getNext();
                if(fileCounter==0 ||  outputDocumentEl==null) // only for the first file. Parse until the document tag (while copying to output file) and then 
                                    // add all interfaces info. //Finally copy the rest of the first document. 
                {
                    doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
                    outputRootEl = doc.addElement(inputRootElement.getLocalName());
                    // Defines linefeed to use, tabs for indentation (from 2, step by 1)
                    SMTools.cloneAllAttributestoOutputElement(outputRootEl, inputRootElement);
                    // now get next element which should be the document element!
                    SMInputCursor inputDocumentElement = inputRootElement.childMixedCursor(); 
                    while(inputDocumentElement.getNext()!=null ) // only one document element 
                    {
                        if( !inputDocumentElement.getCurrEvent().hasText() )
                        {
                            //outPrintWriter.print("::"+inputDocumentElement.getLocalName()+"::");
                            outputDocumentEl = outputRootEl.addElement(inputDocumentElement.getLocalName());
                            SMTools.cloneAllAttributestoOutputElement(outputDocumentEl, inputDocumentElement);
                            // under the document element we should create the UNIQUE styles list (???)
                            // (+++++++++++++++++) TO ADD CODE HERE FOR BUFFERED FRAGMENT FOR STYLES!!!!!! )                            
                            /*uniqueMergedStylesFrag = outputDocumentEl.createBufferedFragment();
                            outputDocumentEl.addBuffered(uniqueMergedStylesFrag);
                             */
                            // now under documentEl add the interfaces you need.
                            if(qdef!=null) {
                                // follow a structure : Selected capability - Selected Functions for this capability - Room (structure and Sensors (of this capability) )
                                Vector<ReqFunctionOverData> theUniqueFunctionVec =  qdef.getQContent().getUniqueFunctionVec();
                                SMOutputElement allCapsFolderEl = KMLProcessTools.myAddNewKmlRadioButtonFolderElement(outputDocumentEl, "Capabilities");

                                // TODO: New. evaluate if it needs more post-processing (less verbose output)
                                //Set<String> setOfSelgCaps = qdef.getQContent().getGenCapQuerriedTofuncIDsHM().keySet();
                                for (Map.Entry<Integer, HashMap<String, Vector<Integer>> > capabilitiesSelectionHMEntry : qdef.getQContent().getCapabilitiesSelectionHM().entrySet()) {
                                    for (Map.Entry<String,  Vector<Integer>> capToFunctionsEntry : capabilitiesSelectionHMEntry.getValue().entrySet()) {
                                        String cap = capToFunctionsEntry.getKey();
                                        String simpleCapName = cap.replaceAll(Pattern.quote(Capability.dcaPrefix), "");
                                        SMOutputElement currCapFolderEl = KMLProcessTools.myAddNewKmlFolderElement(allCapsFolderEl, simpleCapName, "0");
                                        Vector<Integer> tmpFuncIdsForCurrCap = capToFunctionsEntry.getValue();
                                        // Here we put the dynamically created png legend based on the selected style
                                        // ++++ FOR NOW: Since we don;t have style selection currently, we get the first matching style for the capability.
                                        // (++++ future to do: implement style selection)
                                        // If there is no style for the capability (OR no style with the given id (= in style selection not yet implemented))
                                        // then show nothing
                                        //
                                        Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
                                        Vector<Model3dStylesEntry> stylesForCurrCap = myStylesIndex.getEntriesForCap(cap);
                                        if(stylesForCurrCap != null && !stylesForCurrCap.isEmpty()) {
                                            // FOR NOW, we work with elementAt(0) (first matching entry for this capability)
                                            SMOutputElement currStyleForCapFolderEl = KMLProcessTools.myAddNewKmlFolderElement(currCapFolderEl, "legend", "1");
                                            KMLTranslate3dStyle.showStyleLegend(currStyleForCapFolderEl, stylesForCurrCap.elementAt(0).getStyleId(), requestFullUrlPath);
                                        }
                                        SMOutputElement allFunctFolderEl = KMLProcessTools.myAddNewKmlRadioButtonFolderElement(currCapFolderEl, "Functions");
                                        // we got the vector of unique function ids. Now we get each function definition that correspond to each function id
                                        for(int i = 0; i< theUniqueFunctionVec.size(); i++) {
                                            ReqFunctionOverData currReqFunct = theUniqueFunctionVec.elementAt(i);
                                            if(tmpFuncIdsForCurrCap.contains(currReqFunct.getfuncId()) ) {
                                                // for this requested function,
                                                // (+++) maybe put some more info about this function here
                                                String friendlyFuncName = currReqFunct.getfuncName();      //todo: a method get function friendly name
                                                if(ReqFunctionOverData.isValidGatewayReqFunct(friendlyFuncName))
                                                {
                                                    String[] descriptionTokens = friendlyFuncName.split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                                                    if(descriptionTokens!=null && descriptionTokens.length>1)
                                                    {
                                                        friendlyFuncName = descriptionTokens[1];
                                                    }
                                                }

                                                SMOutputElement currFunctFolderEl = KMLProcessTools.myAddNewKmlFolderElement(allFunctFolderEl, friendlyFuncName, "0" );
                                                //
                                                // TODO: check for gateway level function and if it is an aggregate then show only one node with the result
                                                buildTheRoomsInFile(allModelFilesToInterfacesHM, myGwToSmDevsHM, currFunctFolderEl, myssUN, qdef, currReqFunct, cap);
                                            }
                                        }
                                    }
                                }

                                //
                                // Also append a common style for balloon for sensors reading information.
                                //
                                KMLTranslate3dStyle.appendStyleForSensorBalloon(outputDocumentEl);
                                //
                                // (To Do) Also append a list of styles for ALL the styles involved in the current QueryDefinition
                                //
                                // TODO: New. evaluate if it needs more post-processing (less verbose output)
                                //Set<String> setOfSelgCaps = qdef.getQContent().getGenCapQuerriedTofuncIDsHM().keySet();
                                for (Map.Entry<Integer, HashMap<String, Vector<Integer>> > capabilitiesSelectionHMEntry : qdef.getQContent().getCapabilitiesSelectionHM().entrySet()) {
                                    for (Map.Entry<String,  Vector<Integer>> capToFunctionsEntry : capabilitiesSelectionHMEntry.getValue().entrySet()) {
                                        String cap = capToFunctionsEntry.getKey();
                                        // (++++ AGAIN !!! FOR NOW: Since we don;t have style selection currently, we get the first matching style for the capability.)
                                        // (++++ future to do: implement style selection)
                                        // If there is no style for the capability (OR no style with the given id (= in style selection not yet implemented))
                                        // then show nothing
                                        //
                                        Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
                                        Vector<Model3dStylesEntry> stylesForCurrCap = myStylesIndex.getEntriesForCap(cap);
                                        if(stylesForCurrCap != null && !stylesForCurrCap.isEmpty()) {
                                            // (++++ FOR NOW), we work with elementAt(0) (first matching entry for this capability)
                                            KMLTranslate3dStyle.appendStyleForValuesOfCapability(outputDocumentEl, stylesForCurrCap.elementAt(0).getStyleId(), requestFullUrlPath);
                                        }
                                    }
                                }
                            }
                            else // if qdef == null then we only see the involved models and rooms. No results and no Capability-Function-Room structure.
                            {
                                SMOutputElement allRoomsFolderEl = KMLProcessTools.myAddNewKmlFolderElement(outputDocumentEl, "AllRooms", "0");
                                buildTheRoomsInFile(allModelFilesToInterfacesHM, myGwToSmDevsHM, allRoomsFolderEl, myssUN, null, null, null);
                            }
                            //
                            // Put a list of the Unique styles (To do)(this is an ugly hack). (Maybe if the staxmate buffee fragment with attributes issue is resolved we will abondon this hack)
                            //
                            appendUniqueMergedStylesHeader(outputDocumentEl, tmpFilenameKeys, outPrintWriter, realPath);
                            //
                            //
                            KMLTranslate3dStyle.appendStyleForRoomPlaceMark(outputDocumentEl, requestFullUrlPath);
                            
                            SMOutputElement outputFileFolderEl = null;
                            outputFileFolderEl = outputDocumentEl.addElement("Folder");
                            // ++ go through the first level of elements UNDER the Document element of the Input file. 
                            // find unique style tags and add them to the uniqueMergedStylesFrag
                            // All tags except style tags should be copied under the new Folder output element, WITH THEIR ATTRIBUITES
                            SMInputCursor childInputUnderDocu = inputDocumentElement.childMixedCursor();
                            while(childInputUnderDocu.getNext()!=null ) // only one document element 
                            {
                                if(childInputUnderDocu.getCurrEvent().hasText() )
                                {
                                    String myText = childInputUnderDocu.getText();
                                    outputFileFolderEl.addCharacters(myText);  // text goes under the parent
                                }
                                else // if is not text
                                {
                                    if(! childInputUnderDocu.getLocalName().equals("Style"))
                                    {
                                        // then copy the rest of the input document under the special folder for each file
                                        SMOutputElement outputInsideFileFolderEl = null;
                                        outputInsideFileFolderEl = outputFileFolderEl.addElement(childInputUnderDocu.getLocalName());
                                        SMTools.cloneAllAttributestoOutputElement(outputInsideFileFolderEl, childInputUnderDocu);                                        
                                        parseXMLperLevel(outputInsideFileFolderEl, childInputUnderDocu);
                                    }                                        
                                }
                            }                            
                            break;  // only one document element 
                        }
                    }
                }
                else
                {
                    // move to the document tag (without copying anything to the output file. Then copy everything recursively.
                    // get next element which should be the document element!
                    SMInputCursor inputDocumentElement = inputRootElement.childCursor(); 
                    while(inputDocumentElement.getNext()!=null )
                    {
                        if( !inputDocumentElement.getCurrEvent().hasText() )
                        {
                            // (+++++) code repeated here maybe make it a method?
                            SMOutputElement outputFileFolderEl = null;
                            outputFileFolderEl = outputDocumentEl.addElement("Folder");
                            // ++ go through the first level of elements UNDER the Document element of the Input file. 
                            // find unique style tags and add them to the uniqueMergedStylesFrag
                            // All tags except style tags should be copied under the new Folder output element, WITH THEIR ATTRIBUITES
                            SMInputCursor childInputUnderDocu = inputDocumentElement.childMixedCursor();
                            while(childInputUnderDocu.getNext()!=null ) // only one document element 
                            {
                                if(childInputUnderDocu.getCurrEvent().hasText() )
                                {
                                    String myText = childInputUnderDocu.getText();
                                    outputFileFolderEl.addCharacters(myText);  // text goes under the parent
                                }
                                else // if is not text
                                {
                                    if(!childInputUnderDocu.getLocalName().equals("Style"))
                                    {
                                        // then copy the rest of the input document under the special folder for each file
                                        SMOutputElement outputInsideFileFolderEl = null;
                                        outputInsideFileFolderEl = outputFileFolderEl.addElement(childInputUnderDocu.getLocalName());
                                        SMTools.cloneAllAttributestoOutputElement(outputInsideFileFolderEl, childInputUnderDocu);                                        
                                        parseXMLperLevel(outputInsideFileFolderEl, childInputUnderDocu);
                                    }                                        
                                }
                            }
                            break;  // only one document element 
                        }
                    }
                }
                tmpInReader.close();
                fileCounter++;
            }
            /*if(uniqueMergedStylesFrag!= null)
            {
                uniqueMergedStylesFrag.release();                
            }*/
            doc.closeRoot();
            outPrintWriter.flush();
            outPrintWriter.close();            
        } 
        catch(Exception e) 
        {
            if(outPrintWriter==null)
            {
                try{
                    response.setContentType("text/html");
                    outPrintWriter = response.getWriter();
                    KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", e.getMessage());
                    outPrintWriter.flush();
                    outPrintWriter.close();
                }
                catch(Exception e1)
                {
                    ;
                }
            }
        }
    }
    
    
  static private void buildTheRoomsInFile(HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM, HashMap<Gateway, Vector<SmartNode>> myGwToSmDevsHM, SMOutputElement roomsParentElement,  UserNode myssUN, QueryDefinition qdef, ReqFunctionOverData currReqFunct, String currCap)
  throws javax.xml.stream.XMLStreamException
  {
      //build the rooms.
      // we need a new LOOP AGAIN over all the interfaces for each filename
      Set<String> tmpFilenameKeys2 = allModelFilesToInterfacesHM.keySet();
      Iterator<String> itFilenames2 = tmpFilenameKeys2.iterator();
      while(itFilenames2.hasNext()) {
          String tmpFilename2 = itFilenames2.next();
          Vector<Model3dInterfaceEntry> tmpInterfaceVec = allModelFilesToInterfacesHM.get(tmpFilename2);
          for(int k = 0; k <tmpInterfaceVec.size(); k++) {
              //
              // First we filter the sensors we want to display (all in requested area, or all that have the requested capability)
              //
              Model3dInterfaceEntry currInterfaceEntry = tmpInterfaceVec.elementAt(k);
              Set<Gateway> tmpGwKeys = myGwToSmDevsHM.keySet();
              Iterator<Gateway> itGw = tmpGwKeys.iterator();
              while(itGw.hasNext()) {
                  Gateway tmpGw = itGw.next();
                  if(currInterfaceEntry.getGwId().equals(tmpGw.getId())) {

                      Vector<SmartNode> allRequestedSmDevInThisGw = myGwToSmDevsHM.get(tmpGw); // superset of smart devices that actually can respond to the chosen capability
                      // (To do) +++ we can filter this vector of smart devices. For now we use it as it is.
                      
                      //
                      // We then filter the rooms we will display.  (all in requested area (even with 0 sensors to display),
                      //                                               (or just the ones that have sensors to be displayed)
                      //
                      // we use a copy constructor to work on a fixed copy and not affect the actual room vector in the interface.
                      Vector<Model3dRoomEntry> roomsToDisplayInThisInterfaceVec = new  Vector<Model3dRoomEntry>(currInterfaceEntry.getRoomsVec());
                      //
                      // If no room has sensors, then the first valid room, gets all the sensors of the gateway!
                      //
                      KMLTranslate3dInterface.checkIfAllNodesGoToOneRoomAndPlaceThem(currInterfaceEntry.getGwId(), myssUN, roomsToDisplayInThisInterfaceVec);

                      //
                      // (to do) ++ later put some filter options. For the moment we just show rooms that HAVE requested sensors to display (or any sensors at all)
                      //
                      if(allRequestedSmDevInThisGw!=null)
                      {
                          // --- end of new code for handling all empty rooms
                          for(int o=0; o<roomsToDisplayInThisInterfaceVec.size(); o++) {
                                if(roomsToDisplayInThisInterfaceVec.elementAt(o).getSensorsThatMatch(allRequestedSmDevInThisGw).isEmpty()) {
                                  roomsToDisplayInThisInterfaceVec.removeElementAt(o);
                                  o--;
                                }
                           }
                      }
                      // In the above 2 steps we create and update a new Room vector that we pass as an argument to KMLTranslate3dInterface.buildKMLRoomFolders.
                      KMLTranslate3dInterface.buildKMLRoomFolders(roomsParentElement,
                              currInterfaceEntry.getLineOfRef(),
                              roomsToDisplayInThisInterfaceVec,
                              allRequestedSmDevInThisGw,
                              qdef,
                              currInterfaceEntry.getGwId(),
                              currReqFunct,
                              currCap,
                              myssUN);
                      break;// only one match is needed with current Interface and its gateway id.
                  }
              }
          }
      }
      
  }
    
  static private void parseXMLperLevel(SMOutputElement parentOutEl, SMInputCursor parentInpCurs)
  throws javax.xml.stream.XMLStreamException
  {            
      SMInputCursor childInElement = parentInpCurs.childMixedCursor(); 
      String myText=""; 
      while (childInElement.getNext() != null) 
      {
          if(childInElement.getCurrEvent().hasText())
          {
              myText = childInElement.getText();
              parentOutEl.addCharacters(myText);  // text goes under the parent
          }
          else
          {
              SMOutputElement childOutElement = null;
              childOutElement = parentOutEl.addElement(childInElement.getLocalName());
              SMTools.cloneAllAttributestoOutputElement(childOutElement, childInElement);
              parseXMLperLevel(childOutElement, childInElement);
          }                
      }
  }    

private static void appendUniqueMergedStylesHeader(SMOutputElement outputDocumentEl, Set<String> givFilenameKeys, PrintWriter outPrintWriter, String realPath) {
      Vector<String> uniqueStyleIdsVec = new Vector<String>();

      Iterator<String> itFilenames = givFilenameKeys.iterator();
      try{
          while(itFilenames.hasNext()) {
              String tmpFilename = itFilenames.next();
              //outPrintWriter.print(realPath+File.separator+"KML"+File.separator+ tmpFilename);
              File inFile = new File(realPath+File.separator+"Models"+File.separator+"Large"+File.separator+ tmpFilename);
              FileReader tmpInReader = new FileReader(inFile);
              WstxInputFactory fin = new WstxInputFactory();
              fin.configureForConvenience();
              fin.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE); // <-- NEEDED TO GET ATTRIBUTES!
              // input
              XMLStreamReader2 sr = (XMLStreamReader2)fin.createXMLStreamReader(tmpInReader);
              SMInputCursor inputRootElement = SMInputFactory.rootElementCursor(sr);
              inputRootElement.getNext();
              SMInputCursor inputDocumentElement = inputRootElement.childCursor();
              while(inputDocumentElement.getNext()!=null ) {
                  if( !inputDocumentElement.getCurrEvent().hasText() ) {
                      SMInputCursor childInputUnderDocu = inputDocumentElement.childMixedCursor();
                      while(childInputUnderDocu.getNext()!=null )
                      {
                          if(childInputUnderDocu.getCurrEvent().hasText() ) {
                              ; // ignore it
                          } 
                          else // if is not text
                          {
                              if(childInputUnderDocu.getLocalName().equals("Style")) {
                                  if(!uniqueStyleIdsVec.contains(childInputUnderDocu.getAttrValue("id")) ) {
                                      uniqueStyleIdsVec.add(childInputUnderDocu.getAttrValue("id"));
                                      SMOutputElement outputInsideDocumentEl = null;
                                      outputInsideDocumentEl = outputDocumentEl.addElement("Style");
                                      SMTools.cloneAllAttributestoOutputElement(outputInsideDocumentEl, childInputUnderDocu);
                                      parseXMLperLevel(outputInsideDocumentEl, childInputUnderDocu);
                                  }
                              }
                          }
                      }
                      break; //only one document element allowed.
                  }
              }
              tmpInReader.close();
          }
      } catch (Exception e) {
         ;// outPrintWriter.print("ERROR: "+e.getMessage());
      }
  }    
}
