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
 * VisualResultsModel.java
 *
 */

package presentation.webgui.vitroappservlet;

import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.query.IndexOfQueries;
import vitro.vspEngine.service.query.QueryDefinition;
import vitro.vspEngine.service.engine.UserNode;
import presentation.webgui.vitroappservlet.KMLPresentationService.KMLProcessTools;
import presentation.webgui.vitroappservlet.KMLPresentationService.KMLResultFileTools;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dIndex;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dIndexEntry;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dInterfaceEntry;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dMetafile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author antoniou
 */
public class VisualResultsModel extends HttpServlet
{
    String uQueryId;
    String requestFullUrlPath = ""; 
    /** Creates a new instance of VisualResultsModel */
    UserNode myssUN = null;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO (+++++) this specific system folder name "VitroAppServlet" should be somehow parsed from a config file or SOMETHING.
        requestFullUrlPath =  request.getScheme()+"://" + request.getServerName() + ":" +request.getServerPort() + request.getContextPath();//"/VitroAppServlet";

        HttpSession session = request.getSession();
        Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
        myssUN = (UserNode)this.getServletContext().getAttribute("ssUN");
        this.uQueryId = request.getParameter("quid");
        IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
        QueryDefinition qdef = IndexOfQueryDefs.getQueryDefinitionById(this.uQueryId);
        if(qdef == null)
        {
            // error message (To do) (Add code)
            response.setContentType("text/html");
            PrintWriter outPrintWriter = response.getWriter();
            KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", "No such query exists in Index!");
            outPrintWriter.flush();
            outPrintWriter.close();
            return;
        }
        else
        {
            HashMap<Gateway, Vector<SmartNode>> myGwToSmDevsHM = qdef.processQueryAndOnlyGetAnalysedCandidateHM();
            // 1. First step. Just Print out the names::ids of affected gateways
            // 2. Second step. Print which of the gateways have a Model file associated. Print that Model's file name
            //                  Also print default Interface per gateway.
            Set<Gateway> tmpGwKeys = myGwToSmDevsHM.keySet();
            Iterator<Gateway> itGw = tmpGwKeys.iterator();
//            response.setContentType("text/html");
//            PrintWriter outPrintWriter = response.getWriter();
//           outPrintWriter.print("<b>Affected Gateways</b>:<br>");
            // SOME GATEWAYS COULD BE MAPPED TO THE SAME MODEL FILE. 
            // WE NEED NOW A HASHMAP OF UNIQUE MODELFILE NAMES DETECTED FROM THE PREVIOUS STEP, MAPPED TO 
            // A Model3dInterfaceEntry (that has embedded pairs of gid:interfaceid) OBJECT. 
            HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM = new HashMap<String,  Vector<Model3dInterfaceEntry>>();
            while(itGw.hasNext())
            {
                Gateway currGw = itGw.next();
//                outPrintWriter.print("<b>"+currGw.getName()+"::"+currGw.getId()+"</b>##");
                // A gateway can have a VECTOR of models associated with it (not just a single file -though it is prefferred that way)
                Vector<Model3dIndexEntry> currIndexEntriesVec = myModelsIndex.getIndexEntriesByGatewayId(currGw.getId());
                for(int i = 0 ; i < currIndexEntriesVec.size(); i++)
                {
                    String tmpModelFilename = currIndexEntriesVec.elementAt(i).getModelFileName();
                    String tmpMetaFilenameFullPath = Model3dIndex.getIndexPath() + currIndexEntriesVec.elementAt(i).getMetaFileName();
                    long tmpDefaultInterfaceIdforCurrGw = currIndexEntriesVec.elementAt(i).getDefaultInterfaceIdForGwId(currGw.getId());
//                    outPrintWriter.print(tmpModelFilename +" on ");
//                    outPrintWriter.print(Long.toString(tmpDefaultInterfaceIdforCurrGw) +",");
                    Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                    if(tmpMetaFile!=null)
                    {
                        
//                       outPrintWriter.print("<p /><pre>"+tmpMetaFile.toString()+"</pre>");
                        //
                        // get the related Model3dInterfaceEntry from the specified metafile.
                        //
                        Model3dInterfaceEntry tmpRelInterfaceEntry = tmpMetaFile.findInterfaceEntry(currGw.getId(), tmpDefaultInterfaceIdforCurrGw);
                        if(tmpRelInterfaceEntry!=null)
                        {
                            if(allModelFilesToInterfacesHM.containsKey(tmpModelFilename)) {
                                Vector<Model3dInterfaceEntry> tmpVec = allModelFilesToInterfacesHM.get(tmpModelFilename);
                                tmpVec.add(tmpRelInterfaceEntry);
                            } else {
                                 Vector<Model3dInterfaceEntry> tmpVecWithSingleEntry= new  Vector<Model3dInterfaceEntry>();
                                 tmpVecWithSingleEntry.add(tmpRelInterfaceEntry);
                                 allModelFilesToInterfacesHM.put(tmpModelFilename, tmpVecWithSingleEntry);
                            }
                        }
                    }
                }
//                outPrintWriter.print("<br>");
            }
             
            Set<String> tmpFilenameKeys = allModelFilesToInterfacesHM.keySet();
            Iterator<String> itFilenames = tmpFilenameKeys.iterator();
//            outPrintWriter.print("<p /><b>Final models to interfaces map</b>:<br>");
//            if(tmpFilenameKeys.isEmpty())
//            {
//               outPrintWriter.print("<p /><b>No files match!</b>");
//            }
            
            // Check for file exists (preliminary)!to eliminate files that dont exist anymore for various reasons....
            String realPath = this.getServletContext().getRealPath("/");
            int lastslash = realPath.lastIndexOf(File.separator);
            realPath = realPath.substring(0, lastslash);  
            while(itFilenames.hasNext())                
            {
                String tmpFilename = itFilenames.next();
//                outPrintWriter.print("<br><b>"+realPath+File.separator+"Models"+File.separator+"Large"+File.separator+ tmpFilename+"</b><br>");
                File inFile = new File(realPath+File.separator+"Models"+File.separator+"Large"+File.separator+ tmpFilename);
                if(!inFile.exists())
                {
//                    outPrintWriter.print("File doesn't exist!");
                    allModelFilesToInterfacesHM.remove(tmpFilename);
                }
            }
            //
            // Now we can merge Models and ADD folders for the interfaces (rooms).
            // 
            if(allModelFilesToInterfacesHM.size() > 0)
            {                
                KMLResultFileTools.mergeModelsAndAddFolders(allModelFilesToInterfacesHM, myGwToSmDevsHM, qdef, response, realPath, requestFullUrlPath, myssUN);
            }
            else
            {
                response.setContentType("text/html");
                PrintWriter outPrintWriter = response.getWriter();
                KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", "No matching model files were found!");
                outPrintWriter.flush();
                outPrintWriter.close();
            }
//            outPrintWriter.flush();
//            outPrintWriter.close();

            // 3. Crude merge the found (IF ANY) KMLs. Link to Google Earth and present them
            
            // 4. Translate Interfaces per gateway to KML folder. Append it to the merged file.
            //    Create a CAPABILITY>FUNCTION>ROOMS>SENSORS structure.

        }
    }
    
}
