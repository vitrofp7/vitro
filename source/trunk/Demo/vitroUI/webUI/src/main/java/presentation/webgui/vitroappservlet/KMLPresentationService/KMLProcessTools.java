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
 * KMLProcessTools.java
 *
 */

package presentation.webgui.vitroappservlet.KMLPresentationService;

import vitro.vspEngine.service.geo.GeoPoint;
import org.codehaus.staxmate.out.SMBufferedElement;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.PrintWriter;
import java.util.Vector;

/**
 *
 * @author antoniou
 */
public class KMLProcessTools {
    
    
 public static void myKMLShowMessage(PrintWriter outPrintWriter, String strTitle, String strDescription)
 {
     outPrintWriter.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.1\"><Document><open>1</open><Folder><open>1</open>");
     outPrintWriter.print("<name>"+strTitle+"</name>");
     outPrintWriter.print("<description>"+strDescription+"</description>");
     outPrintWriter.print("</Folder></Document></kml>");
 }
    
    
 public static SMBufferedElement myAddNewKmlFolderBufferedElement(SMOutputElement parentElement, String folderName, String folderOpenValue)
   throws javax.xml.stream.XMLStreamException
   {
       SMBufferedElement folderEl = parentElement.createBufferedElement(parentElement.getNamespace(), "Folder");
       
       SMOutputElement folderNameEl = folderEl.addElement("name");
       folderNameEl.addCharacters(folderName);
            
       SMOutputElement folderOpenEl = folderEl.addElement("open");
       folderOpenEl.addCharacters(folderOpenValue);
        
       return folderEl;
   }
 
 
   public static SMOutputElement myAddNewKmlRadioButtonFolderElement(SMOutputElement parentElement, String folderName)
   throws javax.xml.stream.XMLStreamException
   {
       SMOutputElement folderEl = parentElement.addElement("Folder");              
                  
       SMOutputElement folderNameEl = folderEl.addElement("name");
       folderNameEl.addCharacters(folderName);
            
       SMOutputElement folderStyleEl = folderEl.addElement("Style");
       
       SMOutputElement folderListStyleEl = folderStyleEl.addElement("ListStyle");
       SMOutputElement folderlistItemTypeEl = folderListStyleEl.addElement("listItemType");        
       folderlistItemTypeEl.addCharacters("radioFolder");
        
       return folderEl;
   }
  
   public static SMOutputElement myAddNewKmlFolderElement(SMOutputElement parentElement, String folderName, String folderOpenValue)
   throws javax.xml.stream.XMLStreamException
   {
       SMOutputElement folderEl = parentElement.addElement("Folder");              
                  
       SMOutputElement folderNameEl = folderEl.addElement("name");
       folderNameEl.addCharacters(folderName);
            
       SMOutputElement folderOpenEl = folderEl.addElement("open");
       folderOpenEl.addCharacters(folderOpenValue);
        
       return folderEl;
   }
  
   public static SMOutputElement myAddNewKmlPlacemarkElement(SMOutputElement parentElement, String plName, String plCoordinates)
   throws javax.xml.stream.XMLStreamException
   {
       SMOutputElement placemarkEl = parentElement.addElement("Placemark");
       
       SMOutputElement placemarkNameEl = placemarkEl.addElement("name");
       placemarkNameEl.addCharacters(plName);
       
       SMOutputElement pointEl = placemarkEl.addElement("Point");

       SMOutputElement coordsEl = pointEl.addElement("coordinates");
       coordsEl.addCharacters(plCoordinates);    
       
       SMOutputElement altitudeModeEl = pointEl.addElement("altitudeMode");
       altitudeModeEl.addCharacters("relativeToGround");       
       
       return placemarkEl;
   }

   public static SMOutputElement myAddNewKmlPlacemarkElement(SMOutputElement parentElement, String plName, String plDescription, Vector<String> plStyleUrlVec, String plCoordinates)
   throws javax.xml.stream.XMLStreamException
   {
       SMOutputElement placemarkEl = parentElement.addElement("Placemark");
       
       SMOutputElement placemarkNameEl = placemarkEl.addElement("name");
       placemarkNameEl.addCharacters(plName);
       
       SMOutputElement placemarkDescriptionEl = placemarkEl.addElement("description");
       placemarkDescriptionEl.addCharacters(plDescription);

       if( plStyleUrlVec!=null)
       {
           for(int i = 0; i< plStyleUrlVec.size(); i++) {
               String plStyleUrl = plStyleUrlVec.elementAt(i);
               if(plStyleUrl!=null && !plStyleUrl.equals("")) {
                   if(plStyleUrl.indexOf("#")!= 0)
                       plStyleUrl = "#"+plStyleUrl;
                   
                   SMOutputElement placemarkStyleUrlEl = placemarkEl.addElement("styleUrl");
                   placemarkStyleUrlEl.addCharacters(plStyleUrl);
               }
           }
       }
       
       SMOutputElement pointEl = placemarkEl.addElement("Point");

       SMOutputElement coordsEl = pointEl.addElement("coordinates");
       coordsEl.addCharacters(plCoordinates);     
       
       SMOutputElement altitudeModeEl = pointEl.addElement("altitudeMode");
       altitudeModeEl.addCharacters("relativeToGround");
       
       return placemarkEl;
   }
   
   
   
   public static SMOutputElement myAddNewKMLGeometryPlacemarkElement(SMOutputElement parentElement, String plName, GeoPoint[] outerRegionPoints, GeoPoint[] innerRegionPoints, String givStyleUrl)
   throws javax.xml.stream.XMLStreamException 
   {
       SMOutputElement placemarkEl = parentElement.addElement("Placemark");
       
       SMOutputElement placemarkNameEl = placemarkEl.addElement("name");
       placemarkNameEl.addCharacters(plName);
       
       if(givStyleUrl != null)
       {
           if(givStyleUrl.indexOf("#")!= 0)
               givStyleUrl = "#"+givStyleUrl;
           
           SMOutputElement placemarkStyleUrlEl = placemarkEl.addElement("styleUrl");
           placemarkStyleUrlEl.addCharacters(givStyleUrl);                     
       }
       
       SMOutputElement multiGeomEl = placemarkEl.addElement("MultiGeometry");
       SMOutputElement polygonEl = multiGeomEl.addElement("Polygon");
       // important if we don't want just a flat surface
       
       SMOutputElement extrudeEl = polygonEl.addElement("extrude");
       extrudeEl.addCharacters("1");
       
       SMOutputElement altitudeModeEl = polygonEl.addElement("altitudeMode");
       altitudeModeEl.addCharacters("relativeToGround");
       
       SMOutputElement outerBoundaryIsEl = polygonEl.addElement("outerBoundaryIs");
       SMOutputElement outLinRingEl = outerBoundaryIsEl.addElement("LinearRing");
       SMOutputElement outCoordinatesEl = outLinRingEl.addElement("coordinates");
       for(int i = 0 ; i < outerRegionPoints.length; i++)
       {
           if(outerRegionPoints[i].isValidPoint())
               outCoordinatesEl.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[i], 3) +" ");
       }
       if(outerRegionPoints.length > 0) // final closing edge
       {
           if(outerRegionPoints[0].isValidPoint())
               outCoordinatesEl.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[0], 3) +" ");
       }
       if(innerRegionPoints !=null && innerRegionPoints.length >0) 
       {
           SMOutputElement innerBoundaryIsEl = polygonEl.addElement("innerBoundaryIs");
           SMOutputElement inLinRingEl = innerBoundaryIsEl.addElement("LinearRing");
           SMOutputElement inCoordinatesEl = inLinRingEl.addElement("coordinates");
           for(int i = 0 ; i < innerRegionPoints.length; i++)
           {
               if(innerRegionPoints[i].isValidPoint())
                   inCoordinatesEl.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[i], 3) +" ");
           }
           if(innerRegionPoints.length > 0) // final closing edge
           {
               if(innerRegionPoints[0].isValidPoint())
                   inCoordinatesEl.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[0], 3) +" ");
           }
       }       
       return placemarkEl;
   }
   
    /**
    * Translates vector of outer and a vector of inner points to a KML placemark/room
    *
    *
    */
   public static SMOutputElement myAddNewKMLGeometryPlacemarkElevatedElement(SMOutputElement parentElement, String plName, GeoPoint[] outerRegionPoints, GeoPoint[] innerRegionPoints, double givRoomHeight, double givRoomElevation, String givStyleUrl)
   throws javax.xml.stream.XMLStreamException 
   {
       SMOutputElement placemarkEl = parentElement.addElement("Placemark");
       
       SMOutputElement placemarkNameEl = placemarkEl.addElement("name");
       placemarkNameEl.addCharacters(plName);
       
       if(givStyleUrl != null)
       {
           if(givStyleUrl.indexOf("#")!= 0)
               givStyleUrl = "#"+givStyleUrl;
           
           SMOutputElement placemarkStyleUrlEl = placemarkEl.addElement("styleUrl");
           placemarkStyleUrlEl.addCharacters(givStyleUrl);                     
       }
       
       SMOutputElement multiGeomEl = placemarkEl.addElement("GeometryCollection");
       
       double roomTopHeight = givRoomElevation + givRoomHeight;
       double roomTopInnerFaceHeight = roomTopHeight + 0.000000000000001;
       double roomBaseInnerFaceHeight = givRoomElevation + 0.30;  // fixed floor thickness is 30 centimeters thick
       //
       // Outer Base polygon
       //
       SMOutputElement polygonElOuterBase = multiGeomEl.addElement("Polygon");
              
       SMOutputElement altitudeModeElOuterBase = polygonElOuterBase.addElement("altitudeMode");
       altitudeModeElOuterBase.addCharacters("relativeToGround");
       
       SMOutputElement outerBoundaryIsElOuterBase = polygonElOuterBase.addElement("outerBoundaryIs");
       SMOutputElement outLinRingElOuterBase = outerBoundaryIsElOuterBase.addElement("LinearRing");
       SMOutputElement outCoordinatesElOuterBase = outLinRingElOuterBase.addElement("coordinates");
       for(int i = 0 ; i < outerRegionPoints.length; i++)
       {
           if(outerRegionPoints[i].isValidPoint())
               outCoordinatesElOuterBase.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[i], givRoomElevation) +" ");
       }
       if(outerRegionPoints.length > 0) // final closing edge
       {
           if(outerRegionPoints[0].isValidPoint())
               outCoordinatesElOuterBase.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[0], givRoomElevation) +" ");
       }
       //
       // Outer faces (as many as the points on the base)
       //
       for(int i = 0 ; i < outerRegionPoints.length; i++)
       {
           SMOutputElement polygonElOuterFace = multiGeomEl.addElement("Polygon");
           
           SMOutputElement altitudeModeElOuterFace = polygonElOuterFace.addElement("altitudeMode");
           altitudeModeElOuterFace.addCharacters("relativeToGround");
           
           SMOutputElement outerBoundaryIsElOuterFace = polygonElOuterFace.addElement("outerBoundaryIs");
           SMOutputElement outLinRingElOuterFace = outerBoundaryIsElOuterFace.addElement("LinearRing");
           SMOutputElement outCoordinatesElOuterFace = outLinRingElOuterFace.addElement("coordinates");
           if(outerRegionPoints[i].isValidPoint() && outerRegionPoints[(i+1)%outerRegionPoints.length].isValidPoint())
           {
               outCoordinatesElOuterFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[i], givRoomElevation) +" ");
               outCoordinatesElOuterFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[(i+1)%outerRegionPoints.length], givRoomElevation) +" ");
               outCoordinatesElOuterFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[(i+1)%outerRegionPoints.length], roomTopHeight) +" ");
               outCoordinatesElOuterFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[i], roomTopHeight) +" ");
               outCoordinatesElOuterFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[i], givRoomElevation) +" ");
           }           
       }
       //
       // Top Face polygon (outer AND inner -if defined)
       //
       SMOutputElement polygonElTopFace = multiGeomEl.addElement("Polygon");
              
       SMOutputElement altitudeModeElTopFace = polygonElTopFace.addElement("altitudeMode");
       altitudeModeElTopFace.addCharacters("relativeToGround");
            // outer edges for Top Face
       SMOutputElement outerBoundaryIsElTopFace = polygonElTopFace.addElement("outerBoundaryIs");
       SMOutputElement outLinRingElTopFace = outerBoundaryIsElTopFace.addElement("LinearRing");
       SMOutputElement outCoordinatesElTopFace = outLinRingElTopFace.addElement("coordinates");
       for(int i = 0 ; i < outerRegionPoints.length; i++)
       {
           if(outerRegionPoints[i].isValidPoint())
               outCoordinatesElTopFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[i], roomTopHeight) +" ");
       }
       if(outerRegionPoints.length > 0) // final closing edge
       {
           if(outerRegionPoints[0].isValidPoint())
               outCoordinatesElTopFace.addCharacters(KMLProcessTools.toGEPlacemarkString(outerRegionPoints[0], roomTopHeight) +" ");
       }       
           // inner edges for Top Face     
       if(innerRegionPoints !=null && innerRegionPoints.length >0) 
       {
           SMOutputElement innerBoundaryIsElTopFace = polygonElTopFace.addElement("innerBoundaryIs");
           SMOutputElement inLinRingElTopFace = innerBoundaryIsElTopFace.addElement("LinearRing");
           SMOutputElement inCoordinatesElTopFace = inLinRingElTopFace.addElement("coordinates");
           for(int i = 0 ; i < innerRegionPoints.length; i++)
           {
               if(innerRegionPoints[i].isValidPoint())
                   inCoordinatesElTopFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[i], roomTopInnerFaceHeight) +" ");
           }
           if(innerRegionPoints.length > 0) // final closing edge
           {
               if(innerRegionPoints[0].isValidPoint())
                   inCoordinatesElTopFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[0], roomTopInnerFaceHeight) +" ");
           }
           //
           // Inner Faces of Room Side Walls (as many as the points in the innerRegionPoints Vector)
           //
           for(int i = 0 ; i < innerRegionPoints.length; i++)
           {
               SMOutputElement polygonElInnerFace = multiGeomEl.addElement("Polygon");
               
               SMOutputElement altitudeModeElInnerFace = polygonElInnerFace.addElement("altitudeMode");
               altitudeModeElInnerFace.addCharacters("relativeToGround");
               
               SMOutputElement outerBoundaryIsElInnerFace = polygonElInnerFace.addElement("outerBoundaryIs");
               SMOutputElement inLinRingElInnerFace = outerBoundaryIsElInnerFace.addElement("LinearRing");
               SMOutputElement inCoordinatesElInnerFace = inLinRingElInnerFace.addElement("coordinates");
               if(innerRegionPoints[i].isValidPoint() && innerRegionPoints[(i+1)%innerRegionPoints.length].isValidPoint()) {
                   inCoordinatesElInnerFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[i], roomTopInnerFaceHeight) +" ");
                   inCoordinatesElInnerFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[(i+1)%innerRegionPoints.length], roomTopInnerFaceHeight) +" ");
                   inCoordinatesElInnerFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[(i+1)%innerRegionPoints.length], roomBaseInnerFaceHeight) +" ");
                   inCoordinatesElInnerFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[i], roomBaseInnerFaceHeight) +" ");
                   inCoordinatesElInnerFace.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[i], roomTopInnerFaceHeight) +" ");
               }
           }        
           //
           // Inner Face for Room base
           //
           SMOutputElement polygonElInnerBase = multiGeomEl.addElement("Polygon");
           SMOutputElement altitudeModeElInnerBase = polygonElInnerBase.addElement("altitudeMode");
           altitudeModeElInnerBase.addCharacters("relativeToGround");

           SMOutputElement innerBoundaryIsElInnerBase = polygonElInnerBase.addElement("outerBoundaryIs");
           SMOutputElement inLinRingElInnerBase = innerBoundaryIsElInnerBase.addElement("LinearRing");
           SMOutputElement inCoordinatesElInnerBase = inLinRingElInnerBase.addElement("coordinates");
           for(int i = 0 ; i < innerRegionPoints.length; i++)
           {
               if(innerRegionPoints[i].isValidPoint())
                   inCoordinatesElInnerBase.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[i], roomBaseInnerFaceHeight) +" ");
           }
           if(innerRegionPoints.length > 0) // final closing edge
           {
               if(innerRegionPoints[0].isValidPoint())
                   inCoordinatesElInnerBase.addCharacters(KMLProcessTools.toGEPlacemarkString(innerRegionPoints[0], roomBaseInnerFaceHeight) +" ");
           }
       }       
       
       return placemarkEl;
   }
   
   
   /**
    * Translates a geodesic point to a KML placemark String
    *
    *
    */
    public static String toGEPlacemarkString(GeoPoint givGeoPoint, double height)      
    {
            String strHeight = (height == GeoPoint.NoHeightSet)? "0" : Double.toString(height);
            return Double.toString(givGeoPoint.getLongitude())+","+Double.toString(givGeoPoint.getLatitude())+"," + strHeight;
    } 
   
   
}
