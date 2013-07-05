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
 * KMLTranslate3dInterface.java
 *
 */

package presentation.webgui.vitroappservlet.KMLPresentationService;

import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.geo.GeoSimpleRectRegion;
import vitro.vspEngine.service.geo.GeoCalculus;
import vitro.vspEngine.service.query.QueryDefinition;
import vitro.vspEngine.service.query.ReqFunctionOverData;
import vitro.vspEngine.service.query.ReqResultOverData;
import vitro.vspEngine.service.query.ResultAggrStruct;
import vitro.vspEngine.service.engine.UserNode;
import presentation.webgui.vitroappservlet.Model3dservice.*;
import org.codehaus.staxmate.out.SMBufferedElement;
import org.codehaus.staxmate.out.SMOutputElement;

import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author antoniou
 */
public class KMLTranslate3dInterface {
    
    
    
    /**
     * 
     * calculates and displays also the LineOfReference
     * Caution: This is mainly for use in the preview model mode (while creating the model)
     */   
    public static void insertProcessedData(SMOutputElement parentElement, GeoPoint linerefFromGeoPoint, GeoPoint linerefToGeoPoint, Vector<Model3dRoomEntry> currRoomsVec)
    throws javax.xml.stream.XMLStreamException {
        
        // get reference line
        Model3dLineOfReference givLineOfRef = new Model3dLineOfReference(linerefFromGeoPoint, linerefToGeoPoint);  // default has azimuth 0.
        
        if(linerefFromGeoPoint!=null && linerefFromGeoPoint.isValidPoint() &&
                linerefToGeoPoint!=null && linerefToGeoPoint.isValidPoint() ) {
            
            SMOutputElement folderEl = KMLProcessTools.myAddNewKmlFolderElement(parentElement, "lineOfRef::"+
                    Double.toString(GeoCalculus.ellipsoidDistance(linerefFromGeoPoint, linerefToGeoPoint)) +
                    "," + Double.toString(givLineOfRef.getAzimuth())  , "1");
            KMLProcessTools.myAddNewKmlPlacemarkElement(folderEl, "start", linerefFromGeoPoint.toStringGeodesicCoords(GeoPoint.tokenOrderLonLatAlt, ","));
            KMLProcessTools.myAddNewKmlPlacemarkElement(folderEl, "end", linerefToGeoPoint.toStringGeodesicCoords(GeoPoint.tokenOrderLonLatAlt, ","));
        }
        
        buildKMLRoomFolders(parentElement,  givLineOfRef, currRoomsVec, null, null, null, null, null, null);
    }


    public static boolean checkIfAllNodesGoToOneRoomAndPlaceThem(String currGwId, UserNode myssUN, Vector<Model3dRoomEntry>  currRoomsVec)
    {
        // TODO: before checking the rooms for placing devices, check if any of the rooms has smart devices!
        //       If none of them has, then place ALL smart devices of the GATEWAY in the first room in a round circle
        boolean iHavePutAllDevicesInOneRoom = false;
        boolean atLeastOneRoomHasConfiguredDevices = false;
        int roomIndexToPutAllDevicesIn = -1;
        for(int i = 0 ; i < currRoomsVec.size(); i++) {
            GeoPoint roomCenterGeoPoint = currRoomsVec.elementAt(i).getRoomPoly().getCenterPoint();
            if(roomCenterGeoPoint!=null && roomCenterGeoPoint.isValidPoint()) {
                double usedRadius = 0.0;
                boolean validRoomSize = true;
                try {
                    usedRadius = Math.sqrt(2 ) * 0.5 * currRoomsVec.elementAt(i).getRoomPoly().getSize1();
                } catch(NumberFormatException e2) {
                    validRoomSize = false;
                }
                if(validRoomSize && !Double.isNaN(usedRadius ) )
                {
                    if (currRoomsVec.elementAt(i).getSmartDevVec().size() > 0) {
                        atLeastOneRoomHasConfiguredDevices= true;
                        break;
                    }
                    else if(roomIndexToPutAllDevicesIn < 0)
                    {
                        roomIndexToPutAllDevicesIn = i;
                    }

                }
            }
        }
        if(!atLeastOneRoomHasConfiguredDevices)
        {
            iHavePutAllDevicesInOneRoom = true;
            if(myssUN!=null && currGwId!=null)
            {
                Vector<SmartNode> tmpSmartDevVec = myssUN.getGatewaysToSmartDevsHM().get(currGwId).getSmartNodesVec();
                Iterator<SmartNode> smDevVecIt = tmpSmartDevVec.iterator();
                while(smDevVecIt.hasNext())
                {
                    SmartNode tmpSmDev = smDevVecIt.next();
                    Model3dSensingDevice tmp3dSmDev = new Model3dSensingDevice(tmpSmDev.getId(),tmpSmDev.getLocation());
                    currRoomsVec.elementAt(roomIndexToPutAllDevicesIn).getSmartDevVec().add(tmp3dSmDev);
                }
            }

        }
        return iHavePutAllDevicesInOneRoom;
    }

    /**
     * Displays the rooms aligned to given LineOfReference
     * if the argument  allRequestedSmDevInThisGw is null or empty then all SmartNodes within a room will be displayed. Otherwise this argument is used as a filter to
     * depict which smart devices within a room should be displayed.
     *
     */
    public static void buildKMLRoomFolders(SMOutputElement parentElement, Model3dLineOfReference givLineOfRef, Vector<Model3dRoomEntry> currRoomsVec, Vector<SmartNode> allRequestedSmDevInThisGw, QueryDefinition givenQdef, String currGwId, ReqFunctionOverData currFunct, String currCap, UserNode myssUN )
     throws javax.xml.stream.XMLStreamException {

        boolean haveInsertedTheAggrValOnTheFirstNode = false;
        boolean havePlacedTheNodeInGoogleEarth = false;
        boolean foundAggrGwLevelFunct = false;
        String aggrGwLevelFunctFriendlyName="";

        if(currFunct!=null)
        {
            String[] descriptionTokens = currFunct.getfuncName().split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);

            if(descriptionTokens !=null && descriptionTokens.length > 2  &&
                    (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.maxFunc) ||
                            descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.minFunc) ||
                            descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.avgFunc)
                    )
                    ){
                foundAggrGwLevelFunct = true;
                aggrGwLevelFunctFriendlyName = descriptionTokens[1];
            }
        }
                // draw rooms
        if(currRoomsVec.size() > 0) {
            // we actually need this Rooms folder to be one unique external tag, but the existence of it will depend on
            // how many valid rooms we actually have (because we could have none , and then we would end up with an empty Folder Rooms tag).
            //  (actually we shouldn't mind if Google Earth doesn't - edit: and it doesn't)
            // (TO do) (if we don't want empty tags in XML perhaps this property P_AUTOMATIC_EMPTY_ELEMENTS of XMLOutputFactory2 (superclass of WstxOutputFactory)
            // would help)
            //
            // get selected style. We'll need it in coloring per sensor.
            //
            Model3dStylesEntry currWorkingStyle = null;
            Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
            Vector<Model3dStylesEntry> stylesForCurrCap = myStylesIndex.getEntriesForCap(currCap);
            if(stylesForCurrCap != null && !stylesForCurrCap.isEmpty()) {
                // todo (++++ CAREFUL FOR NOW: we work with elementAt(0) (first matching entry for this capability)
                currWorkingStyle = stylesForCurrCap.elementAt(0);
            }

            SMOutputElement folderEl = KMLProcessTools.myAddNewKmlFolderElement(parentElement, "Rooms", "1" );
            /* for intentional error */ // SMOutputElement debugElement = myAddNewKmlFolderElement(folderEl, "haha", "1" );
            //buffered because we will use it out of order in parallel with writing directly in the rooms folder
            SMBufferedElement allStructureDataBuffEl = KMLProcessTools.myAddNewKmlFolderBufferedElement(folderEl, "RmsStructure", "1");

            boolean iHavePutAllDevicesInOneRoom = checkIfAllNodesGoToOneRoomAndPlaceThem(currGwId, myssUN, currRoomsVec);
            /// TODO: ^^ similar code exists in the KMLResultFileTools too!

            for(int i = 0 ; i < currRoomsVec.size(); i++) {
                // <--- for each ROOM found in the Vector --->
                GeoPoint roomCenterGeoPoint = currRoomsVec.elementAt(i).getRoomPoly().getCenterPoint();

                if(roomCenterGeoPoint!=null && roomCenterGeoPoint.isValidPoint()) {
                    // * Room placemark *
                    if(currWorkingStyle!=null)
                    {
                        Vector<String> roomPlaceMarkStyleUrlVec = new Vector<String>();
                        roomPlaceMarkStyleUrlVec.addElement(KMLTranslate3dStyle.roomPlaceMarkStyleUrlId);
                        KMLProcessTools.myAddNewKmlPlacemarkElement(folderEl, currRoomsVec.elementAt(i).getName(), "", roomPlaceMarkStyleUrlVec, roomCenterGeoPoint.toStringGeodesicCoords(GeoPoint.tokenOrderLonLatAlt, ",") );
                    }
                    else
                    {
                        KMLProcessTools.myAddNewKmlPlacemarkElement(folderEl, currRoomsVec.elementAt(i).getName(), roomCenterGeoPoint.toStringGeodesicCoords(GeoPoint.tokenOrderLonLatAlt, ",") );
                    }
                    // draw the room polygon

                    // Case for cube room:
                    if( currRoomsVec.elementAt(i).getRoomPoly().getType().equals(Model3dRoomPolygon.getPolyTypeCube())) {
                        //
                        // Calculate 4 placemarks as cube vertices.
                        // Placemarks will be placed in radius r from the centerpoint.
                        // r is calculated from pythagorium theorem from the value of s (length of an edge)
                        // by default the ref azimuth is set to "0" degrees (towards North)
                        //
                        double usedRadius = 0.0;
                        double usedCubeSideSize = 0.0;
                        double usedRoomHeight = 0.0;
                        double usedRoomElevation = 0.0;
                        boolean validRoomSize = true;
                        try {
                            usedRadius = Math.sqrt(2 ) * 0.5 * currRoomsVec.elementAt(i).getRoomPoly().getSize1();
                            usedCubeSideSize = currRoomsVec.elementAt(i).getRoomPoly().getSize1();
                            usedRoomHeight = currRoomsVec.elementAt(i).getRoomPoly().getHeight();
                            usedRoomElevation = currRoomsVec.elementAt(i).getRoomPoly().getElevation();
                        } catch(NumberFormatException e2) {
                            validRoomSize = false;
                        }
                        if(validRoomSize && !Double.isNaN(usedRadius )) {
                            GeoPoint[] outerCubePoints;
                            GeoPoint[] innerCubePoints;
                            outerCubePoints = new GeoPoint[4];
                            innerCubePoints = new GeoPoint[4];
                            //
                            // Placemark A: Add 45 to the ref Azimuth. (of the parralel line). Find the point in distance == size from the centerpoint
                            // (check if Azimuth overflows recount from 0).
                            // B will be 90 degrees counterclockwise of A,
                            // C will be 180 degrees counterclockwise of A,
                            // D will be 270 degrees counterclockwise of A,
                            // since modulo works with ints we use some rouhg code override
//                                 double newAz;
//                                 for(int kd = 0; kd < 4; kd++)
//                                 {
                            //newAz = (refLineAzimuth + 45 + kd*90 > 360.0) ? (refLineAzimuth + 45 + kd*90 - 360.0) :  refLineAzimuth + 45 + kd*90;
                            // or newer
                            // newAz = GeoCalculus.handleGivenAngleDeg(refLineAzimuth + 45 + kd*90);
                            //outerCubePoints[kd] = GeoCalculus.GCDistanceAzimuth(roomCenterGeoPoint, usedRadius/1000, newAz);
                            //innerCubePoints[kd] = GeoCalculus.GCDistanceAzimuth(roomCenterGeoPoint, (usedRadius- 0.3)/1000 , newAz);
                            //myAddNewKmlPlacemarkElement(allStructureDataEl, this.givenRoomNames[i]+"::Point "+Integer.toString(kd), cubePoints[kd].toGEPlacemarkString(GeoPoint.NoHeightSet));
                            //                                }
                            // or even newer (with new region classes added)
                            GeoSimpleRectRegion outerCubeRegion = new GeoSimpleRectRegion(roomCenterGeoPoint, usedCubeSideSize / 1000, usedCubeSideSize/1000, givLineOfRef.getAzimuth());
                            GeoSimpleRectRegion innerCubeRegion = new GeoSimpleRectRegion(roomCenterGeoPoint, (usedCubeSideSize-0.3)/1000, (usedCubeSideSize-0.3)/1000, givLineOfRef.getAzimuth());
                            outerCubePoints = outerCubeRegion.getVertexPointArray();
                            innerCubePoints = innerCubeRegion.getVertexPointArray();
                            //
                            // try to find the style that should be applied to this room. Aggregate ANY numeric values and find the mean value.
                            // if no numeric values are set then (if numeric reading values exist but no style exists for them, then use global style.
                            // look for special values. If one special value is set then use the corresponding style for it or global style if the special style does not exist.
                            // if more than one special values are set, or we have one value that does not belong in the special values nor the numeric ranges set, then
                            // use the global style for the capability.
                            //
                            // if no style exists for this capability then use null for the styleurl field
                            //

                            /* for intentional error */ //myAddNewKMLGeometryPlacemarkElement(debugElement, this.givenRoomNames[i]+" room geometry", outerCubePoints, innerCubePoints);
                            /**
                             *
                             * Place smart devices (if any) in a circle in this current room
                             */
                            int numOfSpecialValues = 0;
                            int numOfNumericValues = 0;
                            Vector<String> specialValuesFoundVec = new Vector<String>();
                            double avgOfNumericValues = 0.0;
                            boolean doNotColorRoom = false;
                            String styleForRoom = null;

                            if(currRoomsVec.elementAt(i).getSmartDevVec().size() > 0) {
                                Vector<Model3dSensingDevice> tmpRoomSmartNodesVec;
                                if(allRequestedSmDevInThisGw == null || allRequestedSmDevInThisGw.isEmpty()) {
                                    tmpRoomSmartNodesVec = currRoomsVec.elementAt(i).getSmartDevVec();
                                } else {
                                    tmpRoomSmartNodesVec = currRoomsVec.elementAt(i).getSensorsThatMatch(allRequestedSmDevInThisGw);
                                }
                                if(tmpRoomSmartNodesVec != null && !tmpRoomSmartNodesVec.isEmpty()) {
                                    //
                                    // (To do) this should later use the code for inscribed circles for each room polygon (we first need to associate a region with a room polygon though)
                                    //
                                    Vector<GeoPoint> tmpCircularPlacemarksVec;
                                    tmpCircularPlacemarksVec = outerCubeRegion.placePointsInInscribedCircle(tmpRoomSmartNodesVec.size());

                                    if(tmpCircularPlacemarksVec.size() == tmpRoomSmartNodesVec.size())
                                    {
                                        SMOutputElement roomEl = KMLProcessTools.myAddNewKmlFolderElement(folderEl, currRoomsVec.elementAt(i).getName()+" Devices", "1" );

                                        for(int k = 0; k < tmpRoomSmartNodesVec.size() ; k++)
                                        {
                                            // try to fill in the description field with reading data
                                            String plDescription = " ";
                                            String SmartNodeStyleUrl = "";
                                            if(givenQdef != null && currCap != null && myssUN!=null && currGwId!=null && currFunct!=null) {
                                                if(givenQdef.getLatestQueryResultFile() == null) {
                                                    if(currWorkingStyle!=null)
                                                    {
                                                        String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValuePending).getColor1();
                                                        plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                        int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValuePending);
                                                        SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                    }
                                                    plDescription += "No results found yet!";
                                                    specialValuesFoundVec.addElement(ReqResultOverData.specialValuePending);

                                                    numOfSpecialValues++;
                                                }
                                                else
                                                {
                                                    //
                                                    // try to find the sensormodelid for this requested capability, gateway and moteid.
                                                    //
                                                    String relatedSensorModelIdStr = SensorModel.invalidId;
                                                    String relatedSensorModelDataType = SensorModel.defaultDataType;
                                                    Vector<SensorModel> allSensModelsForCapVec = myssUN.getCapabilitiesTable().get(currCap);
                                                    if(allSensModelsForCapVec !=null)
                                                    {

                                                        for(int i1 = 0; i1 < allSensModelsForCapVec.size(); i1++)
                                                        {
                                                            if( ! (allSensModelsForCapVec.elementAt(i1).getGatewayId().equals(currGwId)) )// means that the sensor model is not inside the current gateway we deal with, and should not be considered in this call
                                                            {
                                                                continue;
                                                            }
                                                            String tmpSensModelIdStr = allSensModelsForCapVec.elementAt(i1).getSmID();

                                                            String sensModelDataType =  allSensModelsForCapVec.elementAt(i1).getDataType();
                                                            Vector<SmartNode> tmpSmartDevVec = ((UserNode)(myssUN)).getGatewaysToSmartDevsHM().get(currGwId).getSmartNodesVec();
                                                            for(int j1=0; j1< tmpSmartDevVec.size(); j1++)
                                                            {
                                                                // CAREFUL: TO DO +++ when binary type handling is RE-ENABLED properly then the following commented && clause should be re-evaluated if it needs to
                                                                // be uncommented or if we need to check additional cases here!!!!
                                                                if(tmpSmartDevVec.elementAt(j1).getId().equals(tmpRoomSmartNodesVec.elementAt(k).getSmartDevId())
                                                                    && SensorModel.vectorContainsSensorModel(tmpSmartDevVec.elementAt(j1).getCapabilitiesVector(), currGwId, tmpSensModelIdStr )) // && !(sensModelDataType.equalsIgnoreCase("Binary")  ) )
                                                                {
                                                                    relatedSensorModelIdStr = tmpSensModelIdStr;
                                                                    relatedSensorModelDataType = sensModelDataType;
                                                                }
                                                                else if(ReqFunctionOverData.isValidGatewayReqFunct(currFunct.getfuncName()) &&
                                                                        SensorModel.vectorContainsSensorModel(tmpSmartDevVec.elementAt(j1).getCapabilitiesVector(), currGwId, tmpSensModelIdStr)
                                                                        )
                                                                {
                                                                    if( foundAggrGwLevelFunct) {
                                                                        relatedSensorModelIdStr = tmpSensModelIdStr;
                                                                        relatedSensorModelDataType = sensModelDataType;

                                                                    }

                                                                }
                                                            }
                                                        }

                                                        Vector<ResultAggrStruct> resVec = null;
                                                        if( (!(relatedSensorModelIdStr.equals(SensorModel.invalidId))) && !foundAggrGwLevelFunct)
                                                        {
                                                            resVec = givenQdef.getLatestQueryResultFile().findResultBy(currGwId,  //depending on current gateway (!?!)
                                                                    currFunct,       //depending on current function
                                                                    tmpRoomSmartNodesVec.elementAt(k).getSmartDevId(),
                                                                    relatedSensorModelIdStr); // depending on current capability

                                                        }
                                                        else if(foundAggrGwLevelFunct && (!relatedSensorModelIdStr.equals( SensorModel.invalidId ))){
                                                            resVec = givenQdef.getLatestQueryResultFile().findResultBy(currGwId,  //depending on current gateway (!?!)
                                                                    currFunct,       //depending on current function
                                                                    SmartNode.invalidId,             // TODO: should never be officially a node id though!
                                                                    relatedSensorModelIdStr); // depending on current capability
                                                        }
                                                        if(relatedSensorModelIdStr.equals(SensorModel.invalidId))
                                                        {
                                                            if(currWorkingStyle!=null)
                                                            {
                                                                String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNotSupported).getColor1();
                                                                if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                    designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set
                                                                plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                               int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNotSupported);
                                                               SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                            }
                                                           plDescription += "Unsupported Capability!";
                                                           specialValuesFoundVec.addElement(ReqResultOverData.specialValueNotSupported);

                                                           numOfSpecialValues++;
                                                        }
                                                        else if(resVec==null || (resVec != null && resVec.isEmpty()) )
                                                        {
                                                            if(currWorkingStyle!=null)
                                                            {
                                                                String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNoReading).getColor1();
                                                                if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                    designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set

                                                                plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNoReading);
                                                                SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                            }
                                                            plDescription += "No reading found!";
                                                            specialValuesFoundVec.addElement(ReqResultOverData.specialValueNoReading);

                                                            numOfSpecialValues++;
                                                        }
                                                        else
                                                        {
                                                            if( relatedSensorModelDataType.equals(SensorModel.numericDataType))
                                                            {
                                                                if(!(foundAggrGwLevelFunct && haveInsertedTheAggrValOnTheFirstNode))
                                                                {
                                                                    try{
                                                                        double readValue = Double.parseDouble(resVec.elementAt(0).getVal());
                                                                        if(!Double.isNaN(readValue)) {
                                                                            if(currWorkingStyle!=null) {
                                                                                String designatedColor = currWorkingStyle.getNumericStyleCaseForValue(readValue).getColor1();
                                                                                plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                                int indexOfCase = currWorkingStyle.getNumericStyleCaseIndexForValue(Double.parseDouble(resVec.elementAt(0).getVal()) );
                                                                                SmartNodeStyleUrl = currCap + "_Numeric_" + Integer.toString(indexOfCase);
                                                                            }
                                                                            plDescription += resVec.elementAt(0).getVal() + resVec.elementAt(0).getTis().createInfoInText();

                                                                            numOfNumericValues++;
                                                                            avgOfNumericValues = (readValue + (avgOfNumericValues * (numOfNumericValues-1))) /(double)numOfNumericValues;
                                                                            haveInsertedTheAggrValOnTheFirstNode = true;
                                                                        } else {
                                                                            if(currWorkingStyle!=null) {
                                                                                String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNoReading).getColor1();
                                                                                if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                                    designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set

                                                                                plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                                int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNoReading);
                                                                                SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                                            }
                                                                            plDescription += "No reading found!";
                                                                            specialValuesFoundVec.addElement(ReqResultOverData.specialValueNoReading);

                                                                            numOfSpecialValues++;
                                                                            haveInsertedTheAggrValOnTheFirstNode = true;
                                                                        }
                                                                    } catch(NumberFormatException e2) { // invalid reading
                                                                        specialValuesFoundVec.addElement(resVec.elementAt(0).getVal());
                                                                        if(currWorkingStyle!=null) {
                                                                            String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNoReading).getColor1();
                                                                            if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                                designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set

                                                                            plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                            int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNoReading);
                                                                            SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                                        }
                                                                        plDescription += resVec.elementAt(0).getVal();

                                                                        numOfSpecialValues++;
                                                                        haveInsertedTheAggrValOnTheFirstNode = true;
                                                                    }
                                                                }

                                                            }
                                                            else if( relatedSensorModelDataType.equals(SensorModel.binaryDataType))
                                                            {
                                                                // FOR NOW binary data type means that we have the URL of the pertinent file (image). We should present it through html)
                                                                String readURLValue = resVec.elementAt(0).getVal();
                                                                if(readURLValue!=null && !readURLValue.equals("")) {
                                                                    if(currWorkingStyle!=null) {
                                                                        String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueBinary).getColor1();
                                                                        if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                            designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set
                                                                        plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                        int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueBinary );
                                                                        SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                                    }
                                                                    plDescription += "<img src=\""+readURLValue+"\" alt=\""+currCap+"\">" + resVec.elementAt(0).getTis().createInfoInText();
                                                                    
                                                                    specialValuesFoundVec.addElement(ReqResultOverData.specialValueBinary);
                                                                    numOfSpecialValues++;
                                                                } else{
                                                                    if(currWorkingStyle!=null) {
                                                                        String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNoReading).getColor1();
                                                                        if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                            designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set                                                                        
                                                                        plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                        int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNoReading);
                                                                        SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                                    }
                                                                    plDescription += "No reading found!";
                                                                    specialValuesFoundVec.addElement(ReqResultOverData.specialValueNoReading);
                                                                    
                                                                    numOfSpecialValues++;
                                                                }
                                                            }
                                                            else
                                                            {
                                                                if(currWorkingStyle!=null) {
                                                                    String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNoReading).getColor1();
                                                                    if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                        designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set

                                                                    plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                                    int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNoReading);
                                                                    SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                                }
                                                                plDescription += "No reading found!";
                                                                specialValuesFoundVec.addElement(ReqResultOverData.specialValueNoReading);
                                                                
                                                                numOfSpecialValues++;
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        if(currWorkingStyle!=null)
                                                        {
                                                            String designatedColor = currWorkingStyle.getSpecialStyleCaseForValue(ReqResultOverData.specialValueNoReading).getColor1();
                                                            if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                                                                designatedColor = currWorkingStyle.getGlobalColor(); // get the global default set
                                                            
                                                            plDescription = "<table><tr><td bgcolor=\""+designatedColor+"\">&nbsp;&nbsp;&nbsp;</td></tr></table>";
                                                            int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(ReqResultOverData.specialValueNotSupported);
                                                            SmartNodeStyleUrl = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                        }
                                                        plDescription += "No results for this capability!";
                                                        specialValuesFoundVec.addElement(ReqResultOverData.specialValueNotSupported);                                                 

                                                        numOfSpecialValues++;
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                plDescription = "No Query was specified!";
                                                doNotColorRoom = true;
                                            }                                            
                                            //KMLProcessTools.myAddNewKmlPlacemarkElement(roomEl, tmpRoomSmartNodesVec.elementAt(k).getSmartDevId(), tmpCircularPlacemarksVec.elementAt(k).toStringGeodesicCoords(GeoPoint.tokenOrderLonLatAlt, ",") );
                                            // * SmartNode placemark *
                                            Vector<String> usedStylesForSmartNodePlacemark = new Vector<String>();
                                            //usedStylesForSmartNodePlacemark.addElement("SensorBalloonStyle");
                                            if(!doNotColorRoom && SmartNodeStyleUrl!= null && !SmartNodeStyleUrl.equals(""))
                                                usedStylesForSmartNodePlacemark.addElement(SmartNodeStyleUrl);
                                            if(!(foundAggrGwLevelFunct && haveInsertedTheAggrValOnTheFirstNode && havePlacedTheNodeInGoogleEarth) )
                                            {
                                                String nameForPlacemark = tmpRoomSmartNodesVec.elementAt(k).getSmartDevId();
                                                if(foundAggrGwLevelFunct){
                                                    nameForPlacemark = aggrGwLevelFunctFriendlyName;
                                                }

                                                KMLProcessTools.myAddNewKmlPlacemarkElement(roomEl, nameForPlacemark, plDescription, usedStylesForSmartNodePlacemark, tmpCircularPlacemarksVec.elementAt(k).toStringGeodesicCoords(GeoPoint.tokenOrderLonLatAlt, ",") );
                                                havePlacedTheNodeInGoogleEarth = true;
                                            }
                                            //doNotColorRoom = true;
                                            if(doNotColorRoom)
                                            {
                                                styleForRoom = null;
                                            }
                                            else
                                            {
                                                // 4 cases:
                                                // case 1: There exist 1 or more numeric values in the room, so the room will be painted according to the average.
                                                // case 2: There exist only special values. If all values are the same the room will be painted to the correspoding color of the one and only special value.
                                                // case 3: There exist only special values. If the values vary then the room will be painted to the global color.
                                                // case 4 (default) paint the global color.
                                                //
                                                //                                                
                                                if(currWorkingStyle != null) {                                                                                                    
                                                    // for the format of the styleurls strings look in the KMLTranslate3dStyle class, method appendStyleForValuesOfCapability
                                                    if(numOfNumericValues>0) {
                                                        int indexOfCase = currWorkingStyle.getNumericStyleCaseIndexForValue(avgOfNumericValues);
                                                        if(indexOfCase >=0)
                                                        {
                                                            styleForRoom = currCap + "_Numeric_" + Integer.toString(indexOfCase);
                                                        }
                                                        else
                                                        {
                                                            styleForRoom = null;
                                                        }
                                                    } 
                                                    
                                                    else if(numOfSpecialValues>0 && !specialValuesFoundVec.isEmpty()) 
                                                    {
                                                        String refVal = specialValuesFoundVec.elementAt(0);
                                                        boolean allEntriesAreTheSame = true;
                                                        for(int p1=0; p1 < specialValuesFoundVec.size(); p1++) {
                                                            if(!refVal.equals(specialValuesFoundVec.elementAt(p1))) {
                                                                allEntriesAreTheSame = false;
                                                                break;
                                                            }
                                                        }
                                                        if(allEntriesAreTheSame) 
                                                        {
                                                            int indexOfCase = currWorkingStyle.getSpecialStyleCaseIndexForValue(refVal);
                                                            if(indexOfCase >=0) 
                                                            {
                                                                styleForRoom = currCap + "_Special_" + Integer.toString(indexOfCase);
                                                            }
                                                            else
                                                            {
                                                                styleForRoom = null;
                                                            }
                                                        } 
                                                        else 
                                                        {
                                                            styleForRoom = currCap+"_Global";
                                                        }
                                                    } 
                                                     
                                                    else { // again global case
                                                        styleForRoom = currCap+"_Global";
                                                    }       
                                                }
                                                else
                                                {
                                                    styleForRoom = null;
                                                }
                                            }
                                        }
                                    }
                                }
                            } // end of if currRoomsVec.elementAt(i).getSmartNodesVec().size()  > 0 
                            // draw room geometry AND color it if appropriate.
                            KMLProcessTools.myAddNewKMLGeometryPlacemarkElevatedElement(allStructureDataBuffEl, currRoomsVec.elementAt(i).getName()+" room geometry", outerCubePoints, innerCubePoints, usedRoomHeight, usedRoomElevation, styleForRoom);
                        }
                    }
                }
            }// end for loop on all rooms
            folderEl.addBuffered(allStructureDataBuffEl);            
            allStructureDataBuffEl.release();
        }        
    }

}
