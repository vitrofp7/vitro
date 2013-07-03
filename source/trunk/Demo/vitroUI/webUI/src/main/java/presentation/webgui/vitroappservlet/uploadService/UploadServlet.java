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
package presentation.webgui.vitroappservlet.uploadService;

import presentation.webgui.vitroappservlet.StaxHelper.SMTools;
import com.ctc.wstx.stax.WstxInputFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import presentation.webgui.vitroappservlet.uploadService.fileupload.MonitoredDiskFileItemFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.XMLInputFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 *
 * This class is called for the files uploading (and monitoring of the uploading).
 * Its final response is placed in an iframe inside the calling .jsp (or html). (this
 * iframe is the target in the form that uploads the files).
 * This response is actually creating invisible code, in the sense that it only
 * writes javascript so as to call a javascript function in the parent (of the iframe) html (= the calling html)
 * which will update the <div></div> tag that has the id of "status". (using the doStatus() function).
 * 
 * This class is called normally once (due to the form "action" parameter). Then the doPost code calls the doFileUpload(),
 * which awaits until the file is fully uploaded and then processes it. When the doFileUpload() finishes up, it always calls the
 * sendCompleteResponse(), which makes sure that the calling .jsp will launch the killUpdate() javascript function
 * and will put some final status code and do some final tasks if necessary.
 * 
 * This class is however also called (its doPost actually) periodically (due to some Ajax code in the calling jsp upon submission of the form)
 * In this periodical calls, the doPost always launched the doStatus() method which updates the div tag in the calling jsp.
 *
 */
public class UploadServlet extends HttpServlet
{
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    doPost(request,response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
  {
    HttpSession session = request.getSession();

    if("status".equals(request.getParameter("c")))
    {
        if("true".equals(request.getParameter("fin")))
        {
            doStatus(request, session, response, true);
        }
        else
        {
            doStatus(request, session, response, false);
        }
    }
    else
    {
        doFileUpload(session, request, response);
    }
  }

  /**
   *
   * Needed because getParameter does not work with form enctype="multipart/form-data" (that uploads a file)
   */
  private FileItem myrequestGetParameter(ServletFileUpload upload, HttpServletRequest request, HashMap<String, String> myFileRequestParamsHM)
  {  
      FileItem fileToReturn = null;
      try{
          List items = upload.parseRequest(request);
          for (Iterator i = items.iterator(); i.hasNext();)
          {
                FileItem fileItem = (FileItem) i.next();
                if (fileItem.isFormField())
                {
                    myFileRequestParamsHM.put(fileItem.getFieldName(), fileItem.getString());
                    fileItem.delete();
                }
                else
                {
                    fileToReturn = fileItem;
                }
          }          
      }
      catch(Exception e)
      {
          return fileToReturn;
      }      
      return fileToReturn;
  }
  
  private void doFileUpload(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
      
      String fname = "";
      HashMap<String, String> myFileRequestParamsHM = new HashMap<String, String>();
      
      try {
          FileUploadListener listener = new FileUploadListener(request.getContentLength());
          FileItemFactory factory = new MonitoredDiskFileItemFactory(listener);
          
          ServletFileUpload upload = new ServletFileUpload(factory);
//        upload.setSizeMax(83886080); /* the unit is bytes */
          
          FileItem fileItem = null;
          fileItem = myrequestGetParameter(upload, request, myFileRequestParamsHM);
          
          String mode = myFileRequestParamsHM.get("mode");
          
          session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
          
          boolean hasError = false;
          
          if (fileItem!=null) {
              /**
               * (for KML only files) ( not prefabs (collada) or icons or images)
               */        
              WstxInputFactory f = null;
              XMLStreamReader2 sr = null;
              SMInputCursor iroot = null;
              if(mode.equals("3dFile") ||
                      mode.equals("LinePlaceMarksFile")||
                      mode.equals("RoomCenterPointsFile"))
              {
                  f = new WstxInputFactory();
                  f.configureForConvenience();
                  // Let's configure factory 'optimally'...
                  f.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
                  f.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
                  
                  sr = (XMLStreamReader2)f.createXMLStreamReader(fileItem.getInputStream());
                  iroot = SMInputFactory.rootElementCursor(sr);
                  // If we needed to store some information about preceding siblings,
                  // we should enable tracking. (we need it for  mygetElementValueStaxMultiple method)
                  iroot.setElementTracking(SMInputCursor.Tracking.PARENTS);
                  
                  iroot.getNext();
                  if (!"kml".equals(iroot.getLocalName().toLowerCase())) {
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "Root element not kml, as expected, but "+iroot.getLocalName());
                      return;
                  }
              }
              
              fname = "";
              if(mode.equals("3dFile")) {
                  if((fileItem.getSize() / 1024 ) > 25096) { // with woodstox stax, file size should not be a problem. Let's put some limit however!
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "File is very large for XML handler to process!");
                      return;
                  }

                  fname = "";
                  String[] elementsToFollow = {"document","name"};
                  Vector<String> resultValues = SMTools.mygetElementValueStax(iroot, elementsToFollow, 0);
                  if(resultValues!= null && !resultValues.isEmpty()) {
                      fname = resultValues.elementAt(0);
                  }
                  
                  if(!fname.equals("")) {
                      // check for kml extension and Add it if necessary!!
                      int lastdot = fname.lastIndexOf('.');
                      if(lastdot!=-1) {
                          if(lastdot == 0) // if it is the first char then ignore it and add an extension anyway
                          {
                              fname += ".kml";
                          } 
                          else if(lastdot < fname.length() - 1)
                          {
                              if(!(fname.substring(lastdot+1).toLowerCase().equals("kml")) )
                              {
                                  fname += ".kml";
                              }
                          }
                          else if(lastdot== fname.length() -1)
                          {
                              fname += "kml";
                          }
                      } else 
                      {
                          fname += ".kml";
                      }
                      
                      String realPath = this.getServletContext().getRealPath("/");
                      int lastslash = realPath.lastIndexOf(File.separator);
                      realPath = realPath.substring(0, lastslash);
                      // too slow
                      //FileWriter out = new FileWriter(realPath+File.separator+"KML"+File.separator+fname);
                      //document.sendToWriter(out);
                      // too slow
                      //StringWriter outString = new StringWriter();
                      //document.sendToWriter(outString);
                      //out.close();
                      
                      // fast - do not process and store xml file, just store it.
                      File outFile = new File(realPath+File.separator+"Models"+File.separator+"Large"+File.separator+fname);
                      outFile.createNewFile();
                      FileWriter tmpoutWriter = new FileWriter(outFile);
                      BufferedWriter buffWriter = new BufferedWriter(tmpoutWriter);
                      buffWriter.write(new String(fileItem.get()));
                      buffWriter.flush();
                      buffWriter.close();
                      tmpoutWriter.close();
                  } else {
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "No name tag found inside the KML file!");
                      return;
                  }
              } 
              else if(mode.equals("LinePlaceMarksFile")) {
                  fname = "";                  
                  String[] elementsToFollow = {"document","folder","placemark","point","coordinates"};
                  Vector<String> resultValues = SMTools.mygetElementValueStax(iroot, elementsToFollow, 0);
                  if(resultValues!= null && resultValues.size() < 2) {
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "File does not contain 2 placemarks!");
                      return;
                  }
                  
                  for(int i=0; (i < resultValues.size()) && (i < 2); i++) {
                      fname = fname + ":" + resultValues.elementAt(i);
                  }
              } 
              else if(mode.equals("RoomCenterPointsFile")) {
                  fname = "";
                  // here: process PlaceMarks for rooms (centerpoints) in the building
                  String[] elementsToFollow0 = {"document","folder","placemark","point","coordinates"};
                  String[] elementsToFollow1 = {"document","folder","placemark","name"};
                  // add elements to follow for room names and coordinates        
                  Vector<String[]> elementsToFollow = new Vector<String[]> ();
                  elementsToFollow.add(elementsToFollow0);
                  elementsToFollow.add(elementsToFollow1);
                  Vector<Vector<String>> resultValues = new Vector<Vector<String>>();
                  SMTools.mygetMultipleElementValuesStax(iroot, elementsToFollow, resultValues);
                  
                  Vector<String> resultValuesForCoords = resultValues.elementAt(0);
                  Vector<String> resultValuesForNames = resultValues.elementAt(1);
                  
                  if(resultValuesForCoords == null || resultValuesForCoords.size() == 0 ||
                          resultValuesForNames == null || resultValuesForCoords.size() == 0 ||
                          resultValuesForCoords.size() != resultValuesForNames.size() ) {
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "File does not contain valid data for rooms!");
                      return;
                  }
                  
                  for(int i=0; i < resultValuesForNames.size() ; i++) {
                      // since we use ;  and ':' to seperate rooms, we replace the comma's in the rooms' names.
                      if(resultValuesForNames.elementAt(i).indexOf(';') >= 0 || resultValuesForNames.elementAt(i).indexOf(':') >= 0 ) {
                          String tmp = new String(resultValuesForNames.elementAt(i));
                          tmp.replace(';', ' ');
                          tmp.replace(':', ' ');
                          resultValuesForNames.set(i, tmp);
                      }
                      fname = fname + ";" + resultValuesForNames.elementAt(i) + ":" + resultValuesForCoords.elementAt(i);
                  }
                  
              }
              else if(mode.equals("DefaultIconfile") ||
                      mode.equals("DefaultPrefabfile") ||
                      mode.equals("SpecialValueIconfile") ||
                      mode.equals("SpecialValuePrefabfile") ||
                      mode.equals("NumericRangeIconfile") ||
                      mode.equals("NumericRangePrefabfile") ) 
              {                  
                  fname = "";
                  if((fileItem.getSize() / 1024 ) > 10096) { // no more than 10 Mbs of size for small prefabs or icons
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "File is very large!");
                      return;
                  }
                  fname = fileItem.getName();
                  if(!fname.equals("")) {
                      String realPath = this.getServletContext().getRealPath("/");
                      int lastslash = realPath.lastIndexOf(File.separator);
                      realPath = realPath.substring(0, lastslash);
                      
                      File outFile = new File(realPath+File.separator+"Models"+File.separator+"Media"+File.separator+fname);
                      outFile.createNewFile();
                      /*
                      FileWriter tmpoutWriter = new FileWriter(outFile);
                      BufferedWriter buffWriter = new BufferedWriter(tmpoutWriter);                      
                      buffWriter.write(new String(fileItem.get()));
                      buffWriter.flush();
                      buffWriter.close();
                      tmpoutWriter.close();
                      */
                      fileItem.write(outFile);
                  } else {
                      hasError = true;
                      listener.getFileUploadStats().setCurrentStatus("finito");
                      session.setAttribute("FILE_UPLOAD_STATS"+mode, listener.getFileUploadStats());
                      sendCompleteResponse(myFileRequestParamsHM, response, hasError, "No valid name for uploaded file!");
                      return;
                  }
              }
              
              fileItem.delete();
          }
          
          if(!hasError) {
              sendCompleteResponse(myFileRequestParamsHM, response, hasError, fname);
          } else {
              hasError = true;
              sendCompleteResponse(myFileRequestParamsHM, response, hasError, "Could not process uploaded file. Please see log for details.");
          }
      } catch (Exception e) {
          boolean hasError = true;
          sendCompleteResponse(myFileRequestParamsHM, response,  hasError, "::"+fname +"::"+e.getMessage());
      }
  }

  private void doStatus(HttpServletRequest request, HttpSession session, HttpServletResponse response, boolean isFinalCall) throws IOException
  {           
      
    // Make sure the status response is not cached by the browser
    response.addHeader("Expires", "0");
    response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
    response.addHeader("Pragma", "no-cache");

    // the doStatus is ALWAYS called by normal post (not multipart), so the following should work!
    String mode = request.getParameter("mode");

    FileUploadListener.FileUploadStats fileUploadStats = (FileUploadListener.FileUploadStats) session.getAttribute("FILE_UPLOAD_STATS"+mode);
    if(fileUploadStats == null || fileUploadStats.getCurrentStatus().equals("finito"))
        return;
    
    if(fileUploadStats != null)
    {
      long bytesProcessed = fileUploadStats.getBytesRead();
      long sizeTotal = fileUploadStats.getTotalSize();
      long percentComplete = (long)Math.floor(((double)bytesProcessed / (double)sizeTotal) * 100.0);
      long timeInSeconds = fileUploadStats.getElapsedTimeInSeconds();
      double uploadRate = bytesProcessed / (timeInSeconds + 0.00001);
      double estimatedRuntime = sizeTotal / (uploadRate + 0.00001);

      response.getWriter().println("<b>Upload Status:</b><br/>");

      if(fileUploadStats.getBytesRead() < fileUploadStats.getTotalSize())
      {
        response.getWriter().println("<div class=\"prog-border\"><div class=\"prog-bar\" style=\"width: " + percentComplete + "%;\"></div></div>");
        response.getWriter().println("Uploaded: " + bytesProcessed + " out of " + sizeTotal + " bytes (" + percentComplete + "%) " + (long)Math.round(uploadRate / 1024) + " Kbs <br/>");
        response.getWriter().println("Runtime: " + formatTime(timeInSeconds) + " out of " + formatTime(estimatedRuntime) + " " + formatTime(estimatedRuntime - timeInSeconds) + " remaining <br/>");
      }
      else
      {
        response.getWriter().println("Uploaded: " + bytesProcessed + " out of " + sizeTotal + " bytes<br/>");
        response.getWriter().println("Complete.<br/>");
      }
    }

    if(fileUploadStats != null && fileUploadStats.getBytesRead() == fileUploadStats.getTotalSize())
    {
      response.getWriter().println("<b>Upload complete.</b>");
      response.getWriter().println("<br/><b>Stand by while processing the uploaded file...</b>");
      //response.getWriter().println("<iframe id=\"tmp\"  style='display: none'>"+
      //                             "<html><body onload='window.parent.killPeriodicUpdate('"+mode+"')'></body></html></iframe>"); // <-- does not work!
      
      if(isFinalCall)
      {
          response.getWriter().println("<b>Done!</b>");
          fileUploadStats.setTotalSize(0);
          fileUploadStats.setBytesRead(0);
          fileUploadStats.setCurrentStatus("finito");
          session.setAttribute("FILE_UPLOAD_STATS"+mode, fileUploadStats);
      }
    }
    
  }

  private void sendCompleteResponse(HashMap<String, String> myFileRequestParamsHM, HttpServletResponse response, boolean isErrorStateflag, String message) throws IOException
  {        
      String mode = myFileRequestParamsHM.get("mode");
      
      String statusdiv = myFileRequestParamsHM.get("statusdiv");
      String buttonid = myFileRequestParamsHM.get("buttonid");


      if(isErrorStateflag)
      {
          response.getOutputStream().print("<html><head><script type='text/javascript'>function killUpdateFinal"+mode+"() { window.parent.killUpdateFinal('"+mode+"','"+statusdiv+"','"+buttonid+"','" + message + "'); }</script></head><body onload='killUpdateFinal"+mode+"()'></body></html>");
      }
      else
      {      
          response.getOutputStream().print("<html><head><script type='text/javascript'>function killUpdateFinal"+mode+"() { window.parent.killUpdateFinal('"+mode+"','"+statusdiv+"','"+buttonid+"', ''); window.parent.updateFileUploadTasks('"+mode+"','"+statusdiv+"','"+buttonid+"','"+message+"'); }</script></head><body onload='killUpdateFinal"+mode+"()'></body></html>");
      }
  }

  private String formatTime(double timeInSeconds)
  {
    long seconds = (long)Math.floor(timeInSeconds);
    long minutes = (long)Math.floor(timeInSeconds / 60.0);
    long hours = (long)Math.floor(minutes / 60.0);

    if(hours != 0)
    {
      return hours + "hours " + (minutes % 60) + "minutes " + (seconds % 60) + "seconds";
    }
    else if(minutes % 60 != 0)
    {
      return (minutes % 60) + "minutes " + (seconds % 60) + "seconds";
    }
    else
    {
      return (seconds % 60) + " seconds";
    }
  }
}
