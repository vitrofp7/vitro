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
package presentation.webgui.vitroappservlet;

import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.engine.UserNode;
import presentation.webgui.vitroappservlet.KMLPresentationService.KMLTranslate3dInterface;
import presentation.webgui.vitroappservlet.Model3dservice.*;
import presentation.webgui.vitroappservlet.StaxHelper.SMTools;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

public class ModelCreator extends HttpServlet
{
    private static final String modePreview = "preview";
    private static final String modeSubmitMap = "submit";
    
    private String kmlfileName;
    private String gwId;
    private String mode;
    private String linerefFrom;
    private String linerefTo;
    private String[] givenRoomNames; 
    private String[] givenRoomCenterCoords; 
    private String[] givenRoomSchemeTypes;
    private String[] givenRoomSize1;
    private String[] givenRoomHeight;
    private String[] givenRoomElevation;
    private String[] givenMappedSmartDevs;
    private String[] givenRoomIdxForMappedSmartDevs;
            
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    HttpSession session = request.getSession();
    
    this.kmlfileName = request.getParameter("vmsf3dKMLFile");
    this.gwId = request.getParameter("vmsfGwToMap");
    this.linerefFrom = request.getParameter("vmsflineRefFrom");
    this.linerefTo = request.getParameter("vmsflineRefTo");

    this.mode = request.getParameter("vmsfMode");

    
    this.givenRoomNames = request.getParameterValues("vmsfRoomNameBox[]");
    this.givenRoomCenterCoords = request.getParameterValues("vmsfRoomCoordBox[]");
    this.givenRoomSchemeTypes =  request.getParameterValues("vmsfSelectARoomTypeBox[]");
    this.givenRoomSize1 = request.getParameterValues("vmsfRoomsSizeBox[]");
    this.givenRoomHeight = request.getParameterValues("vmsfRoomsHeightBox[]");
    this.givenRoomElevation = request.getParameterValues("vmsfRoomsElevationBox[]");
    this.givenMappedSmartDevs = request.getParameterValues("vmsffixedMotesMapBox[]");
    this.givenRoomIdxForMappedSmartDevs = request.getParameterValues("vmsffixedMotesToWhichRoomMapBox[]");
        
    // For the moment just:
    
    // 1) open the requested kml file. 
    // 2) read it (WstxInputFactory) (at first just the siblings children of the root)    
    // 3) WstxOutputFactory and write it to a String (in parallel)    
    // 4) display the result String.
    if(this.mode == null || this.mode.equals(""))
    {
        this.mode = ModelCreator.modePreview; // by default we assume preview mode
    }
    
    
    if(kmlfileName==null)
    {
        kmlfileName="";
    }
    //
    // (+ To do) should check if file exists and rename it (?) so as not to overwrite?
    //
    String realPath = this.getServletContext().getRealPath("/");
    int lastslash = realPath.lastIndexOf(File.separator);
    realPath = realPath.substring(0, lastslash);  
    
    File inFile = new File(realPath+File.separator+"Models"+File.separator+"Large"+File.separator + kmlfileName);
    // error state check
    if(kmlfileName.equals("") || !inFile.exists())
    {
        if(kmlfileName=="")
        {
            if(this.mode.equals(ModelCreator.modeSubmitMap))
            {
                response.setContentType ("text/xml; charset=UTF-8");                
                PrintWriter outPrintWriter = response.getWriter();
                outPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
                outPrintWriter.println("<Answer>");
                outPrintWriter.println("<error errno=\"1\" errdesc=\"No filename for the 3d model was defined!\"></error>");
                outPrintWriter.println("</Answer>");
                outPrintWriter.flush();
                outPrintWriter.close();
            }
            else
            {
                response.setContentType("text/html");
                PrintWriter outPrintWriter = response.getWriter();
                outPrintWriter.print("<b>Error</b>: No filename for the 3d model was defined!");               
                outPrintWriter.flush();
                outPrintWriter.close();
            }            
        }
        else if(!inFile.exists())
        { 
            if(this.mode.equals(ModelCreator.modeSubmitMap))
            {
                response.setContentType ("text/xml; charset=UTF-8");                
                PrintWriter outPrintWriter = response.getWriter();
                outPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
                outPrintWriter.println("<Answer>");
                outPrintWriter.println("<error errno=\"1\" errdesc=\"The file with the specified filename for the 3d model does not exist anymore!!\"></error>");
                outPrintWriter.println("</Answer>");
                outPrintWriter.flush();
                outPrintWriter.close();
            }
            else
            {
                response.setContentType("text/html");
                PrintWriter outPrintWriter = response.getWriter();
                outPrintWriter.print("<b>Error</b>: The file with the specified filename for the 3d model does not exist anymore!");
                outPrintWriter.flush();
                outPrintWriter.close();
            }            
        }
        return;        
    }    
    
    if(this.mode.equals(ModelCreator.modePreview))
    {
        previewModel(response, inFile);
    }
    else if(this.mode.equals(ModelCreator.modeSubmitMap))
    {
        submitModel(response);
    }
    else
    {
        response.setContentType("text/html");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.print("<b>Error</b>: no valid mode was specified! ("+this.mode+")");
        outPrintWriter.flush();
        outPrintWriter.close();
    }        
  }
  
  private void submitModel(HttpServletResponse response) throws IOException
  {
      //
      // Check gwId for nullness
      // 
      Model3dIndex.getModel3dIndex();

      UserNode myssUN = (UserNode)this.getServletContext().getAttribute("ssUN");
      HashMap<String, GatewayWithSmartNodes> infoGWHM = myssUN.getGatewaysToSmartDevsHM();
      
      String gwNameFound = "";
      Set<String> keysOfGIds = infoGWHM.keySet();
      Iterator<String> itgwId = keysOfGIds.iterator();
      while(itgwId.hasNext()) {
          String currGwId = itgwId.next();
          Gateway currGw = infoGWHM.get(currGwId);
          Vector<SmartNode> tmpSmartDevVec = infoGWHM.get(currGwId).getSmartNodesVec();
          
          String tmpgateId = currGw.getId();
          String tmpgateName = currGw.getName();
          if(tmpgateId.equals(this.gwId)) {
              gwNameFound = tmpgateName;
          }
      } 
      String givInterfaceDesc = Model3dInterfaceEntry.getDefaultDescription(); 
             
      GeoPoint linerefFromGeoPoint = GeoPoint.parseStringGeodesicCoords(this.linerefFrom, GeoPoint.tokenOrderLonLatAlt, ",", GeoPoint.noElevationOverride);
      GeoPoint linerefToGeoPoint = GeoPoint.parseStringGeodesicCoords(this.linerefTo, GeoPoint.tokenOrderLonLatAlt, ",", GeoPoint.noElevationOverride);
                  
      // get reference line
        Model3dLineOfReference givLineOfRef = new Model3dLineOfReference(linerefFromGeoPoint, linerefToGeoPoint);  // default has azimuth 0.
        
       // fill in the givRoomsVec vector with valid Model3dRoomEntry      
      Vector<Model3dRoomEntry> givRoomsVec = retrieveRoomsVector();              

      if(Model3dIndex.addNewModelIndexEntry(this.kmlfileName, this.gwId, gwNameFound, givInterfaceDesc, givLineOfRef, givRoomsVec, Model3dIndex.getUpdateModeInsert() ) == true)
      {      
           response.setContentType("text/xml; charset=UTF-8");
           PrintWriter outPrintWriter = response.getWriter();
           outPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
           outPrintWriter.println("<Answer>");
           outPrintWriter.println("<error errno=\"0\" errdesc=\"Done!\"></error>");
           outPrintWriter.println("</Answer>");
           outPrintWriter.flush();
           outPrintWriter.close();
      }
      else
      {
           response.setContentType("text/xml; charset=UTF-8");
           PrintWriter outPrintWriter = response.getWriter();
           outPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
           outPrintWriter.println("<Answer>");
           outPrintWriter.println("<error errno=\"1\" errdesc=\"Something went wrong!\"></error>");
           outPrintWriter.println("</Answer>");
           outPrintWriter.flush();
           outPrintWriter.close();
      }
  }  
  
  private void previewModel(HttpServletResponse response, File inFile) throws IOException
  {
    PrintWriter outPrintWriter = response.getWriter();

    FileReader tmpInReader = new FileReader(inFile);
    WstxInputFactory fin = new WstxInputFactory();
    fin.configureForConvenience();
    fin.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE); // <-- NEEDED TO GET ATTRIBUTES!
    
    //response.setContentType("text/xml");  // for debug
    //response.setHeader("Content-disposition","attachment; filename=\"previewModel.xml\""); // for debug
    response.setContentType("text/kml");
    response.setHeader("Content-disposition","inline; filename=\"previewModel.kml\"");

    //response.setHeader("Content-disposition","attachment; filename=\"previewModel.kml\"");
    //response.setHeader("Cache-Control","no-cache"); //HTTP 1.1 <-- causes problems in explorer
    response.setHeader("Pragma", "public"); //HTTP 1.0 
    response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    
    WstxOutputFactory fout = new WstxOutputFactory();    
    fout.configureForXmlConformance();    
    SMOutputDocument doc = null;
    SMOutputElement outputRootEl = null;
    try{        
        // output
        XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outPrintWriter);   
        doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
        // Need to store some information about preceding siblings,
        // so let's enable tracking. (to do) maybe we don't need this)
        //
        //    it.setElementTracking(SMInputCursor.Tracking.VISIBLE_SIBLINGS);
        // input
        XMLStreamReader2 sr = (XMLStreamReader2)fin.createXMLStreamReader(tmpInReader);
        SMInputCursor inputRootElement = SMInputFactory.rootElementCursor(sr);
        inputRootElement.getNext();
        
        doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
        outputRootEl = doc.addElement(inputRootElement.getLocalName());
        // Defines linefeed to use, tabs for indentation (from 2, step by 1)
        SMTools.cloneAllAttributestoOutputElement(outputRootEl, inputRootElement);
        
        parseXMLperLevel(outputRootEl, inputRootElement);               
        doc.closeRoot();
        tmpInReader.close();
    }
    catch(Exception e)
    {
            // apparently we can do this (re-set the content-type and disposition. It works with Mozilla and Opera.
            // IT DOES NOT work with Explorer who goes on to read the KML created.
            response.setContentType("text/xml");  // for debug          
            response.setHeader("Content-disposition","attachment; filename=\"ErrorInPreviewModel.xml\""); // for debug
            outPrintWriter.print("Error:"+e.getMessage());
    }    
    outPrintWriter.flush();
    outPrintWriter.close();
  }
  
  private void parseXMLperLevel(SMOutputElement parentOutEl, SMInputCursor parentInpCurs)
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
              if(childInElement.getLocalName() == "Document")
              {
                  parentOutEl.setIndentation("\r\n\t\t  \t  \t  \t  \t  \t  \t  \t  \t  ", 0, 3); // this works (with some unnoticeable spaces for indentation so DON'T TOUCH!
                  childOutElement = parentOutEl.addElement(childInElement.getLocalName());              
                  SMTools.cloneAllAttributestoOutputElement(childOutElement, childInElement);
                  GeoPoint linerefFromGeoPoint = GeoPoint.parseStringGeodesicCoords(this.linerefFrom, GeoPoint.tokenOrderLonLatAlt, ",", GeoPoint.noElevationOverride);
                  GeoPoint linerefToGeoPoint = GeoPoint.parseStringGeodesicCoords(this.linerefTo, GeoPoint.tokenOrderLonLatAlt, ",", GeoPoint.noElevationOverride);
                  
                  // fill in the givRoomsVec vector with valid Model3dRoomEntry
                  Vector<Model3dRoomEntry> givRoomsVec = retrieveRoomsVector(); 
                  KMLTranslate3dInterface.insertProcessedData(childOutElement, linerefFromGeoPoint, linerefToGeoPoint, givRoomsVec);
              }  
              if(childOutElement == null)
              {
                  childOutElement = parentOutEl.addElement(childInElement.getLocalName());
                  SMTools.cloneAllAttributestoOutputElement(childOutElement, childInElement);
              }
              parseXMLperLevel(childOutElement, childInElement);
          }                
      }
  } 
  
  private Vector<Model3dRoomEntry> retrieveRoomsVector()
  {
       Vector<Model3dRoomEntry> givRoomsVec = new Vector<Model3dRoomEntry>();
       // insert rooms
       if(this.givenRoomNames!=null && this.givenRoomNames.length > 0
               && this.givenRoomCenterCoords!=null && this.givenRoomCenterCoords.length > 0
               && this.givenRoomNames.length == this.givenRoomCenterCoords.length ) 
       {
           for(int i = 0; i < givenRoomNames.length; i++) 
           {
               GeoPoint roomCenterGeoPoint = GeoPoint.parseStringGeodesicCoords(this.givenRoomCenterCoords[i], GeoPoint.tokenOrderLonLatAlt, "," , Double.parseDouble(this.givenRoomElevation[i]));
               if(roomCenterGeoPoint!=null && roomCenterGeoPoint.isValidPoint()) {
                   String givName = givenRoomNames[i];
                   Model3dRoomPolygon givRoomPoly = new Model3dRoomPolygon(this.givenRoomSchemeTypes[i], roomCenterGeoPoint, Double.parseDouble(this.givenRoomSize1[i]), 0, 0, Double.parseDouble(this.givenRoomHeight[i]), Double.parseDouble(this.givenRoomElevation[i]) );
                   Vector<Model3dSensingDevice> givSmartDevVec = new Vector<Model3dSensingDevice>();
                   if(this.givenMappedSmartDevs!=null && this.givenMappedSmartDevs.length > 0 &&
                           this.givenRoomIdxForMappedSmartDevs!=null &&  this.givenRoomIdxForMappedSmartDevs.length >0 &&
                           this.givenMappedSmartDevs.length == this.givenRoomIdxForMappedSmartDevs.length)
                   {
                        for(int j=0; j < givenRoomIdxForMappedSmartDevs.length; j++) 
                        {
                            if(Integer.parseInt(this.givenRoomIdxForMappedSmartDevs[j]) == i)
                            {
                                // (To do) (maybe later change): for the moment we add the roomCenterGeoPoint as the point for the motes inside.
                                Model3dSensingDevice tmpSensDev = new Model3dSensingDevice(this.givenMappedSmartDevs[j], roomCenterGeoPoint) ;
                                givSmartDevVec.add(tmpSensDev);
                            }
                        }
                   }
                   givRoomsVec.add(new Model3dRoomEntry(givName, givRoomPoly, givSmartDevVec )  );
               }
           }
       }
       return givRoomsVec;
  }
             
}
