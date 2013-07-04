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
 * PreviewModelAndInterface.java
 *
 */

package presentation.webgui.vitroappservlet;

import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.SmartNode;
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
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author antoniou
 */
public class PreviewModelAndInterface extends HttpServlet
{
    
    private String[] givenModelFilenames; 
    private String[] givenGatewayIds; 
    private String[] givenInterfaceIds;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Check for file exists (preliminary)!to eliminate files that dont exist anymore for various reasons....
        String realPath = this.getServletContext().getRealPath("/");
        int lastslash = realPath.lastIndexOf(File.separator);
        realPath = realPath.substring(0, lastslash);
            
        HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM = new HashMap<String, Vector<Model3dInterfaceEntry>>();
        HashMap<Gateway, Vector<SmartNode>> myGwToSmDevsHM = new HashMap<Gateway, Vector<SmartNode>>();

        UserNode myssUN = (UserNode)this.getServletContext().getAttribute("ssUN");
        
        this.givenModelFilenames = request.getParameterValues("modelFilenamesBox[]");
        this.givenGatewayIds = request.getParameterValues("gatewayIdsBox[]");
        this.givenInterfaceIds =  request.getParameterValues("interfaceIdsBox[]");
        
        if(this.givenModelFilenames!=null && this.givenModelFilenames.length > 0 &&
               this.givenGatewayIds!=null && this.givenGatewayIds.length > 0 &&
                this.givenInterfaceIds!=null && this.givenInterfaceIds.length > 0 )
        {
           //populate the hashmap allModelFilesToInterfacesHM by adding the right interfaces from the ModelIndex.
            Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
            for(int i=0; i<this.givenModelFilenames.length ; i++)
            {
                if(allModelFilesToInterfacesHM.get(this.givenModelFilenames[i]) == null)
                {
                    Vector<Model3dInterfaceEntry> tmpInterfVec = new Vector<Model3dInterfaceEntry>();
                    allModelFilesToInterfacesHM.put(this.givenModelFilenames[i], tmpInterfVec );                    
                }
                Vector<Model3dInterfaceEntry> corrInterfVec = allModelFilesToInterfacesHM.get(this.givenModelFilenames[i]);
                // TODO: ADD CODE HERE!!!!!
                Model3dIndexEntry currIndexEntry = myModelsIndex.getIndexEntryByModelFilename(this.givenModelFilenames[i]);
                if(currIndexEntry != null) {
                    String tmpMetaFilenameFullPath =  Model3dIndex.getIndexPath() + currIndexEntry.getMetaFileName();
                    Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                    if(tmpMetaFile!=null){
                        Model3dInterfaceEntry tmpInterfaceEntry = tmpMetaFile.findInterfaceEntry(this.givenGatewayIds[i], Long.parseLong(this.givenInterfaceIds[i]) );
                        if(tmpInterfaceEntry!=null)
                            corrInterfVec.addElement(tmpInterfaceEntry);
                    }
                }
                myGwToSmDevsHM.put(myssUN.getGatewaysToSmartDevsHM().get(this.givenGatewayIds[i]), null);
            }
        }

        if(allModelFilesToInterfacesHM.size() > 0) {
            KMLResultFileTools.previewMergedModelandInterface(allModelFilesToInterfacesHM, myGwToSmDevsHM, response, realPath, myssUN);
        } else {
            response.setContentType("text/html");
            PrintWriter outPrintWriter = response.getWriter();
            KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", "No mapped models found");
            //KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", "::"+realPath+"::");
            //KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", Integer.toString(allModelFilesToInterfacesHM.get(this.givenModelFilenames[0]).size()));
            //KMLProcessTools.myKMLShowMessage(outPrintWriter, "Error", "::"+this.givenModelFilenames[0]+"::"+ this.givenGatewayIds[0]+"::"+ this.givenInterfaceIds[0]+"::");
            outPrintWriter.flush();
            outPrintWriter.close();
        }
    }
}
