<%--
  ~ #--------------------------------------------------------------------------
  ~ # Copyright (c) 2013 VITRO FP7 Consortium.
  ~ # All rights reserved. This program and the accompanying materials
  ~ # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
  ~ # http://www.gnu.org/licenses/lgpl-3.0.html
  ~ #
  ~ # Contributors:
  ~ #     Antoniou Thanasis (Research Academic Computer Technology Institute)
  ~ #     Paolo Medagliani (Thales Communications & Security)
  ~ #     D. Davide Lamanna (WLAB SRL)
  ~ #     Alessandro Leoni (WLAB SRL)
  ~ #     Francesco Ficarola (WLAB SRL)
  ~ #     Stefano Puglia (WLAB SRL)
  ~ #     Panos Trakadas (Technological Educational Institute of Chalkida)
  ~ #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
  ~ #     Andrea Kropp (Selex ES)
  ~ #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
  ~ #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
  ~ #
  ~ #--------------------------------------------------------------------------
  --%>

<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="vitro.vspEngine.service.persistence.DBCommons" %>
<%@ page import="vitro.vspEngine.service.persistence.DBRegisteredGateway" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.*" %>
<%@ page import="vitro.vspEngine.logic.model.GatewayWithSmartNodes" %>
<%@ page import="vitro.vspEngine.logic.model.SmartNode" %>
<%@ page import="presentation.webgui.vitroappservlet.Model3dservice.*" %>
<%@ page import="vitro.vspEngine.logic.model.SensorModel" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.*" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSmartNodeOfGateway" %>
<%
    Logger logger = Logger.getLogger(this.getClass());
    String xmerrordescr="";
    int errno = 0;
    String responseResult="";
    String functionSignature="";
    final String  NEW_LINE_STR="__nl__";
    String staticprefixCapability =vitro.vspEngine.logic.model.Capability.dcaPrefix;

    String pGatewayID = "";
    String pSensorID = "";
    String pSensorIDList = "";
    String pEquivListID = "";
    String pAction = "";
    String reqData = "";
    String reqParamNamesStr ="";
    String reqParamValuesStr = "";
    Enumeration<String> reqDataNames;

    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    try {
        reqData = request.getQueryString();
        reqDataNames = request.getParameterNames();

        functionSignature = request.getParameter("fid");
        pGatewayID = request.getParameter("pGatewayID");
        pSensorID =  request.getParameter("pSensorID");
        pSensorIDList = request.getParameter("pSensorIDList");
        pEquivListID = request.getParameter("pEquivListID");
        pAction =  request.getParameter("pAction");
    }catch(Exception ex) {
        errno = 1;
        xmerrordescr="Error in post data format!";
        functionSignature = "";
        reqDataNames = null;

        pGatewayID = "";
        pSensorID =  "";
        pSensorIDList =  "";
        pEquivListID = "";
        pAction = "";
    }
    if(reqDataNames !=null )
    {
        while (reqDataNames.hasMoreElements()) {
            String parName =  reqDataNames.nextElement();
            // process element
            reqParamNamesStr += parName + ",";

        }
        //remove trailing comma
        reqParamNamesStr = reqParamNamesStr.replaceAll("\\s*,\\s*$", "");
    }

    if(functionSignature!=null && !functionSignature.isEmpty()) {
        //  -----------------------------------------------------------------------------
        // getGWInfo(vgwID,callback)
        // returns: gwId, gwName,centerPointXLong,centerPointYLat,isInMem,isEnabled\n
        if(functionSignature.compareToIgnoreCase("getGWInfo") ==0
            && pGatewayID !=null) {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();
            // TODO: or we could use Hibernate to be more consistent?
            //DBRegisteredGateway currGwDB = DBCommons.getDBCommons().getRegisteredGateway(pGatewayID);
            DBRegisteredGateway currGwDB = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pGatewayID);

            String friendlyName = "";
            String centerPointX ="";
            String centerPointY ="";
            String isInMem = "0";
            String isEnabled = "0";
            if(currGwDB!=null)
            {
                if(ssUN!=null)
                {
                    if(ssUN.getGatewaysToSmartDevsHM()!=null && !ssUN.getGatewaysToSmartDevsHM().isEmpty() && ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)
                            && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID)!=null    && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec().isEmpty()) {

                        isInMem = "1";
                    }
                }
                //check if gw exists in actively registered gateways (in mem)
                //
                friendlyName = currGwDB.getFriendlyName();
                if(currGwDB.getStatus()== false){ //when false, it means ENABLED!!!
                    isEnabled = "1";
                }
                //NEW NEW NEW NEW
                // The VGW does not have to be connected to find out its centerpoints!
                GeoPoint vgwfirstRoomCenter = null;
                try{
                    Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
                    HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM = new HashMap<String,  Vector<Model3dInterfaceEntry>>();

                    StringBuilder coordsFound = new StringBuilder();
                    int totalNodes = 0;
                    int ignoredNodesWithNoCoordInfo = 0;
                    // A gateway can have a VECTOR of models associated with it (not just a single file -though it is preferred that way)
                    Vector<Model3dIndexEntry> currIndexEntriesVec = myModelsIndex.getIndexEntriesByGatewayId(pGatewayID);
                    for(int i = 0 ; i < currIndexEntriesVec.size(); i++)
                    {
                        String tmpModelFilename = currIndexEntriesVec.elementAt(i).getModelFileName();
                        String tmpMetaFilenameFullPath = Model3dIndex.getIndexPath() + currIndexEntriesVec.elementAt(i).getMetaFileName();
                        long tmpDefaultInterfaceIdforCurrGw = currIndexEntriesVec.elementAt(i).getDefaultInterfaceIdForGwId(pGatewayID);
                        Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                        if(tmpMetaFile!=null)
                        {
                            // outPrintWriter.print("<p /><pre>"+tmpMetaFile.toString()+"</pre>");
                            //
                            // get the related Model3dInterfaceEntry from the specified metafile.
                            //
                            Model3dInterfaceEntry tmpRelInterfaceEntry = tmpMetaFile.findInterfaceEntry(pGatewayID, tmpDefaultInterfaceIdforCurrGw);
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
                    //// $$$$$$$$$$$$$$$$$$$$$$$$$$$4
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

                            if(currInterfaceEntry.getGwId().compareToIgnoreCase(pGatewayID) == 0) {

                                // we use a copy constructor to work on a fixed copy and not affect the actual room vector in the interface.
                                Vector<Model3dRoomEntry> roomsToDisplayInThisInterfaceVec = currInterfaceEntry.getRoomsVec();
                                if(roomsToDisplayInThisInterfaceVec!=null && roomsToDisplayInThisInterfaceVec.size()>0)
                                {
                                    vgwfirstRoomCenter = roomsToDisplayInThisInterfaceVec.elementAt(0).getRoomPoly().getCenterPoint();
                                    break;
                                }
                                //
                            }
                        }
                    }
                }
                catch(Exception e)
                {
                    logger.debug("Error while trying to find gw centerpoint of " + pGatewayID);
                }
                if (vgwfirstRoomCenter != null)
                {
                    Coordinate gwXyz = new Coordinate();
                    gwXyz.setX(vgwfirstRoomCenter.getLongitude());
                    gwXyz.setY(vgwfirstRoomCenter.getLatitude());
                    if(gwXyz.getX().doubleValue() == 0.0 && gwXyz.getY().doubleValue() == 0.0)
                    {
                        gwXyz.setX(10.3333);   // long // TODO: To be removed before final

                        gwXyz.setY(44.8313);  // lat   // TODO: To be removed before final
                    }
                    centerPointX = Double.toString(gwXyz.getX());
                    centerPointY = Double.toString(gwXyz.getY());
                }
            }
            resStrBld.append(pGatewayID);
            resStrBld.append(",");
            resStrBld.append(friendlyName.replaceAll(Pattern.quote(","), ""));
            resStrBld.append(",");
            resStrBld.append(centerPointX);
            resStrBld.append(",");
            resStrBld.append(centerPointY);
            resStrBld.append(",");
            resStrBld.append(isInMem);
            resStrBld.append(",");
            resStrBld.append(isEnabled);
            responseResult = resStrBld.toString();
        }
        // ------------------------------------------------------------------------------------------------
        // getSensorListForVGW(vgwID,callback)
        // returns: sensorId,centerPointXLong,centerPointYLat,isEnabled,isEnabledStatusSynched, csvlistofCaps\n
        else if(functionSignature.compareToIgnoreCase("getSensorListForVGW") == 0
                && pGatewayID !=null) {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();
            DBRegisteredGateway currGwDB = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pGatewayID);

            String centerPointX ="";
            String centerPointY ="";
            String isEnabled = "0";
            HashMap<String, Vector<SmartNode>> allGWsToNodesHM = new HashMap<String, Vector<SmartNode>>();

            if(currGwDB!=null)
            {
                //check if gw exists in actively registered gateways (in mem)
                //
                if(ssUN!=null)
                {
                    if(ssUN.getGatewaysToSmartDevsHM()!=null && !ssUN.getGatewaysToSmartDevsHM().isEmpty() && ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)
                            && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID)!=null  && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec().isEmpty()){
                        GeoPoint vgwfirstRoomCenter = null;
                        try{
                            // CODE FROM translate to kml (VisualResultsModel)
                            Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
                            HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM = new HashMap<String,  Vector<Model3dInterfaceEntry>>();

                            StringBuilder coordsFound = new StringBuilder();
                            int totalNodes = 0;
                            int ignoredNodesWithNoCoordInfo = 0;
                            // A gateway can have a VECTOR of models associated with it (not just a single file -though it is preferred that way)
                            Vector<Model3dIndexEntry> currIndexEntriesVec = myModelsIndex.getIndexEntriesByGatewayId(pGatewayID);
                            for(int i = 0 ; i < currIndexEntriesVec.size(); i++)
                            {
                                String tmpModelFilename = currIndexEntriesVec.elementAt(i).getModelFileName();
                                String tmpMetaFilenameFullPath = Model3dIndex.getIndexPath() + currIndexEntriesVec.elementAt(i).getMetaFileName();
                                long tmpDefaultInterfaceIdforCurrGw = currIndexEntriesVec.elementAt(i).getDefaultInterfaceIdForGwId(pGatewayID);
                                Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                                if(tmpMetaFile!=null)
                                {
                                    // outPrintWriter.print("<p /><pre>"+tmpMetaFile.toString()+"</pre>");
                                    //
                                    // get the related Model3dInterfaceEntry from the specified metafile.
                                    //
                                    Model3dInterfaceEntry tmpRelInterfaceEntry = tmpMetaFile.findInterfaceEntry(pGatewayID, tmpDefaultInterfaceIdforCurrGw);
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

                            GatewayWithSmartNodes currGwObj = ssUN.getGatewaysToSmartDevsHM().get(pGatewayID);
                            Vector<SmartNode> currSmartNodesVec = currGwObj.getSmartNodesVec();

                            // find how many smart nodes don't have coordinates
                            // find the center of the room for this vgw if any is assigned
                            // if both above are not 0 or null, then placePointsInACircle and assign them with the coordinates!
                            // TODO: since this is approximate, maybe we should not work on the real in-memory smartnodes, but on copies?
                            int numOfunassisignedPoints = 0;
                            Iterator<SmartNode> smartNodeIterator = currSmartNodesVec.iterator();
                            while(smartNodeIterator.hasNext()){
                                SmartNode tmpNode = smartNodeIterator.next();
                                Coordinate myXyz = tmpNode.getCoordLocation();
                                if(myXyz.getX()== null  || myXyz.getX().isNaN()  || myXyz.getY() == null || myXyz.getY().isNaN() ||
                                        (myXyz.getX().doubleValue() == 0.0 && myXyz.getY().doubleValue() == 0.0) )
                                {
                                    numOfunassisignedPoints++;
                                }
                            }
                            //out.println("UNASSIGNED POINTS IN: "+currGwId+" ARE: "+ Integer.toString(numOfunassisignedPoints));
                            //// $$$$$$$$$$$$$$$$$$$$$$$$$$$4
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

                                    if(currInterfaceEntry.getGwId().compareToIgnoreCase(pGatewayID) == 0) {

                                        // we use a copy constructor to work on a fixed copy and not affect the actual room vector in the interface.
                                        Vector<Model3dRoomEntry> roomsToDisplayInThisInterfaceVec = currInterfaceEntry.getRoomsVec();
                                        if(roomsToDisplayInThisInterfaceVec!=null && roomsToDisplayInThisInterfaceVec.size()>0)
                                        {
                                            vgwfirstRoomCenter = roomsToDisplayInThisInterfaceVec.elementAt(0).getRoomPoly().getCenterPoint();
                                            break;
                                        }
                                        //
                                    }
                                }
                            }
                            ////$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
                            if(vgwfirstRoomCenter!=null && numOfunassisignedPoints>0 ) {
                                //radius in kilometers
                                Vector<GeoPoint> tmpVecOfGeo = GeoPoint.placePointsOnACircle(vgwfirstRoomCenter, 0.020, numOfunassisignedPoints );
                                if(tmpVecOfGeo!=null)
                                {   int assignedPoints = 0;
                                    Iterator<SmartNode> smartNodeIt2 = currSmartNodesVec.iterator();
                                    while(smartNodeIt2.hasNext()){
                                        SmartNode tmpNode = smartNodeIt2.next();
                                        Coordinate myXyz = tmpNode.getCoordLocation();
                                        if(myXyz.getX()== null  || myXyz.getX().isNaN()  || myXyz.getY() == null || myXyz.getY().isNaN() ||
                                                (myXyz.getX().doubleValue() == 0.0 && myXyz.getY().doubleValue() == 0.0) )
                                        {
                                            myXyz.setX(tmpVecOfGeo.elementAt(assignedPoints).getLongitude());
                                            myXyz.setY(tmpVecOfGeo.elementAt(assignedPoints).getLatitude());
                                            assignedPoints++;
                                        }
                                    }
                                }
                            }
                            // allGWsVector.addAll(currSmartNodesVec); //new replaced      (20/02/13)
                            Vector<SmartNode> allNodesInGWVector = new Vector<SmartNode>();
                            allNodesInGWVector.addAll(currSmartNodesVec);
                            allGWsToNodesHM.put(pGatewayID,allNodesInGWVector);
                        }
                        catch(Exception e)
                        {
                            //allGWsVector = null;
                            allGWsToNodesHM.put(pGatewayID,new Vector<SmartNode>());
                            logger.debug("Error while trying to present the smart nodes of " + pGatewayID +". Resources were probably not retrieved yet. Please wait a little longer.");
                        }
                        if (vgwfirstRoomCenter != null)
                        {
                            Coordinate gwXyz = new Coordinate();
                            gwXyz.setX(vgwfirstRoomCenter.getLongitude());
                            gwXyz.setY(vgwfirstRoomCenter.getLatitude());
                            if(gwXyz.getX().doubleValue() == 0.0 && gwXyz.getY().doubleValue() == 0.0)
                            {
                                gwXyz.setX(10.3333);   // long // TODO: To be removed before final
                                gwXyz.setY(44.8313);  // lat   // TODO: To be removed before final
                            }
                            centerPointX = Double.toString(gwXyz.getX());
                            centerPointY = Double.toString(gwXyz.getY());

                        }
                    }
                } // ssUN object != null
            }
            int ignoredNodesWithNoCoordInfo = 0;
            if(!allGWsToNodesHM.isEmpty() && allGWsToNodesHM.containsKey(pGatewayID)){
                Vector<SmartNode> allNodesInGWVector = allGWsToNodesHM.get(pGatewayID);
                for(SmartNode iSmNode : allNodesInGWVector) {
                    resStrBld.append(iSmNode.getId());
                    resStrBld.append(",");

                    Coordinate myXyz = iSmNode.getCoordLocation();
                    if(myXyz.getX()== null  || myXyz.getX().isNaN()  || myXyz.getY() == null || myXyz.getY().isNaN() )
                    {
                        ignoredNodesWithNoCoordInfo+=1;
                        continue;        // do not show nodes that are valid but have no coordinates!! //TODO: how do we overcome this?
                    }
                    else
                    {
                        if(myXyz.getX().doubleValue() == 0.0 && myXyz.getY().doubleValue() == 0.0)
                        {
                            myXyz.setX(10.3333);   // long // TODO: To be removed before final

                            myXyz.setY(44.8313);  // lat   // TODO: To be removed before final
                        }
                        //coordsFound.append("("+ Double.toString(myXyz.getX()) + ","+ Double.toString(myXyz.getY()) + "),");
                    }
                    resStrBld.append(Double.toString(myXyz.getX()));
                    resStrBld.append(",");
                    resStrBld.append(Double.toString(myXyz.getY()));
                    resStrBld.append(",");
                    resStrBld.append( (iSmNode.getRegistryProperties().isEnabled()? "1": "0") );
                    resStrBld.append(",");
                    boolean isEnabledStatusSynched = false;

                    if(iSmNode.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch() <= 0 ||
                            (iSmNode.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch() > 0 && iSmNode.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch() <= iSmNode.getRegistryProperties().getTimeStampEnabledStatusSynch() )
                             ) {
                        isEnabledStatusSynched = true;
                    }

                    resStrBld.append(isEnabledStatusSynched? "1": "0");
                    resStrBld.append(",");

                    // IN THE END OF THE CSV WE LIST INFO ABOUT THE NODE's capabilities
                    Iterator<SensorModel> capsIt = iSmNode.getCapabilitiesVector().iterator();
                    // ????????
                    SensorModel currentCap;
                    while(capsIt.hasNext()) {
                        currentCap = capsIt.next();
                        String currentCapName = currentCap.getName();
                        String currentCapNameNoPrefix =  currentCap.getName().replaceAll(Pattern.quote(staticprefixCapability),"" );
                        String currentCapIconName = vitro.vspEngine.logic.model.Capability.getDefaultIcon(currentCapNameNoPrefix);
                        resStrBld.append(currentCapName);
                        resStrBld.append(",");
                        resStrBld.append(currentCapNameNoPrefix);
                        resStrBld.append(",");
                        resStrBld.append(currentCapIconName);
                        if(capsIt.hasNext())
                            resStrBld.append(",");
                    }
                    resStrBld.append(NEW_LINE_STR);
                }
            }
            responseResult = resStrBld.toString();
        }
        // --------------------------------------------------------------------------------------------------------------
        // getCapilitiesListForVGW(vgwID,callback)
        // returns: capabilityName, capabilityNameNoPrefix, (todo later: isSupportedbyActiveSensors)\n
        else if(functionSignature.compareToIgnoreCase("getCapabilitiesListForVGW") == 0
                && pGatewayID !=null){
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();
            DBRegisteredGateway currGwDB = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pGatewayID);

            HashMap<String, Vector<SensorModel>> capsToSensorsForThisVGW = new HashMap<String, Vector<SensorModel>>();
            if(currGwDB!=null)
            {
                //check if gw exists in actively registered gateways (in mem)
                //
                if(ssUN!=null)
                {
                    // getCapabilitiesForGW TODO: this could be the implementation of that ssUN method!
                    if(ssUN.getGatewaysToSmartDevsHM()!=null && !ssUN.getGatewaysToSmartDevsHM().isEmpty() && ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)
                            && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID)!=null
                            && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec().isEmpty()
                            && ssUN.getCapabilitiesTable() != null && !ssUN.getCapabilitiesTable().isEmpty()) {

                        GatewayWithSmartNodes thisGwWithSmNds = ssUN.getGatewaysToSmartDevsHM().get(pGatewayID);
                        if(thisGwWithSmNds!=null && thisGwWithSmNds.getSmartNodesVec()!=null && !thisGwWithSmNds.getSmartNodesVec().isEmpty()){

                            for(SmartNode iCurrNode : thisGwWithSmNds.getSmartNodesVec()) {

                                // for each sensor model of this node, check if it is contained in the  capsToSensorsForThisVGW HM
                                boolean foundTheSensorModel = false;
                                for(SensorModel currNodeSensModel : iCurrNode.getCapabilitiesVector()){
                                    foundTheSensorModel = false;
                                    for (String capNameFullTmp: capsToSensorsForThisVGW.keySet()){
                                        if(capsToSensorsForThisVGW.get(capNameFullTmp)!=null && SensorModel.vectorContainsSensorModel(capsToSensorsForThisVGW.get(capNameFullTmp), currNodeSensModel.getGatewayId() ,currNodeSensModel.getSmID()  ) ) {
                                            foundTheSensorModel = true;
                                            break;
                                        }
                                    }
                                    if(!foundTheSensorModel) {

                                        //Add it to the hashmap
                                        String candCapNameKey = currNodeSensModel.getName();
                                        Vector<SensorModel> modelsInThisCandCap = null;
                                        if(!capsToSensorsForThisVGW.containsKey(candCapNameKey)) {
                                            modelsInThisCandCap = new Vector<SensorModel>();
                                            capsToSensorsForThisVGW.put(candCapNameKey,modelsInThisCandCap);
                                        }
                                        modelsInThisCandCap = capsToSensorsForThisVGW.get(candCapNameKey);
                                        modelsInThisCandCap.addElement(currNodeSensModel);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if(capsToSensorsForThisVGW != null && !capsToSensorsForThisVGW.isEmpty()) {
                for(String fullCapNameTmp : capsToSensorsForThisVGW.keySet()) {
                    resStrBld.append(fullCapNameTmp);
                    resStrBld.append(",");
                    String currentCapNameNoPrefix =  fullCapNameTmp.replaceAll(Pattern.quote(staticprefixCapability),"" );
                    resStrBld.append(currentCapNameNoPrefix);
                    String currentCapIconName = vitro.vspEngine.logic.model.Capability.getDefaultIcon(currentCapNameNoPrefix);
                    resStrBld.append(",");
                    resStrBld.append(currentCapIconName);
                    resStrBld.append(NEW_LINE_STR);

                }
            }
            responseResult = resStrBld.toString();
        }
        // ---------------------------------------------------------------------------------------------------------------------
        // setEnableDisableSensorForVGW(vgwID, sensorID, actionTk, callback)
        // returns: successFlag\n
        else if(functionSignature.compareToIgnoreCase("setEnableDisableSensorForVGW") == 0
                && pGatewayID !=null
                && pSensorID !=null
                && pAction !=null){
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pAction);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();

            DBRegisteredGateway currGwDB = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pGatewayID);
            boolean errorCase = false;
            boolean cannotChangeValueDueToSetByVGWid = false;
            if(currGwDB!=null)
            {
                //check if gw exists in actively registered gateways (in mem)
                //
                if(ssUN!=null)
                {
                    if(ssUN.getGatewaysToSmartDevsHM()!=null && !ssUN.getGatewaysToSmartDevsHM().isEmpty() && ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)
                            && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID)!=null
                            && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec().isEmpty()){
                        try{

                            GatewayWithSmartNodes currGwObj = ssUN.getGatewaysToSmartDevsHM().get(pGatewayID);
                            Vector<SmartNode> currSmartNodesVec = currGwObj.getSmartNodesVec();

                            Iterator<SmartNode> smartNodeIterator = currSmartNodesVec.iterator();
                            while(smartNodeIterator.hasNext()){
                                SmartNode tmpNode = smartNodeIterator.next();
                                if(tmpNode.getId().compareToIgnoreCase(pSensorID) == 0) {
                                    if(tmpNode.getRegistryProperties().isEnabledStatusWasInitiatedByVGW()) {
                                        // then we cannot change the status (because the VGW has decided that the node failed
                                        logger.debug("Cannot change node status because it was set by the VGW!");
                                        cannotChangeValueDueToSetByVGWid = true;
                                        errorCase = true;
                                        break;
                                    }
                                    else {
                                        if(pAction.compareToIgnoreCase("enable") == 0) {
                                            tmpNode.getRegistryProperties().setEnabled(true);
                                        }else if(pAction.compareToIgnoreCase("disable")== 0)  {
                                            tmpNode.getRegistryProperties().setEnabled(false);
                                        } else {
                                            logger.error("invalid status change requested from a user");
                                            errorCase = true;
                                        }
                                        if(!errorCase) {
                                            tmpNode.getRegistryProperties().setTimeStampEnabledStatusRemotelySynch(new Date().getTime());
                                            tmpNode.getRegistryProperties().setEnabledStatusWasInitiatedByVGW(false);
                                            tmpNode.getRegistryProperties().setTimeStampEnabledStatusSynch(0); //invalidate the confirmation timestamp from the vgw
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            errorCase = true;
                            logger.error("An exception was raised while setting enable disable status for vgwid: " + pGatewayID + " node: " + pSensorID);
                        }
                    }

                }

            }
            if(!errorCase) {
                String msgToSend = DisabledNodesVGWSynch.getInstance().createMessageForVGW(pGatewayID);
                if( msgToSend!= null && !msgToSend.trim().isEmpty())  {
                  // send the message
                   if(ssUN!=null)
                   {   // this is only for debug
                       //ssUN.sendSelfAlertMessage(pGatewayID, msgToSend);
                       // this is the actual send
                       ssUN.sendDirectCommand(pGatewayID, msgToSend);
                   }
               } else {
                  errorCase = true;
               }

            }
           //again check errorCase at the end
            if(cannotChangeValueDueToSetByVGWid) {
                resStrBld.append(2);
            }
            else if(!errorCase){
                resStrBld.append(1);
            }
            else {
                resStrBld.append(0);
            }
            responseResult = resStrBld.toString();

        }
        // ---------------------------------------------------------------------------------------------------------------------------
        // getEquivSetsListForVGW(vgwID, callback)
        // return: listId, updateLocalDateStr, updateLocalTSLong, remoteSynchDateStr, remoteSynchTSLong, isMarkedForDeletion, listof(vgwId,sensorId)\n
        else if(functionSignature.compareToIgnoreCase("getEquivSetsListForVGW") == 0
                && pGatewayID!=null ){
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();
            DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pGatewayID);
            if(tmpDbRGw!=null)
            {
                AbstractSetOfEquivNodesManager abstractSetOfEquivNodesManager = AbstractSetOfEquivNodesManager.getInstance();
                List<SetOfEquivalentSensorNodes> allSetOfEquivalentSensorNodesList = abstractSetOfEquivNodesManager.getSetOfEquivNodesListForGwId(pGatewayID);
                for (SetOfEquivalentSensorNodes setEqTmpIter : allSetOfEquivalentSensorNodesList)
                {
                    Date tsLocalFromDB = setEqTmpIter.getTimestampUpdateLocal();
                    String tsLocalFromDBStr = "";
                    long tsLocalFromDBLong = 0;
                    if(tsLocalFromDB!=null) {
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        // Using DateFormat format method we can create a string
                        // representation of a date with the defined format.
                        tsLocalFromDBStr= df.format(tsLocalFromDB);
                        tsLocalFromDBLong= tsLocalFromDB.getTime();
                    }
                    Date tsRemoteFromDB = setEqTmpIter.getTimestampSynchedRemotely();
                    String tsRemoteFromDBStr = "";
                    long tsRemoteFromDBLong = 0;
                    if(tsRemoteFromDB!=null) {
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        // Using DateFormat format method we can create a string
                        // representation of a date with the defined format.
                        tsRemoteFromDBStr= df.format(tsRemoteFromDB);
                        tsRemoteFromDBLong= tsRemoteFromDB.getTime();
                    }
                    resStrBld.append(setEqTmpIter.getId());
                    resStrBld.append(",");
                    resStrBld.append(tsLocalFromDBStr);
                    resStrBld.append(",");
                    resStrBld.append(Long.toString(tsLocalFromDBLong));
                    resStrBld.append(",");
                    resStrBld.append(tsRemoteFromDBStr);
                    resStrBld.append(",");
                    resStrBld.append(Long.toString(tsRemoteFromDBLong));
                    resStrBld.append(",");
                    resStrBld.append(setEqTmpIter.isMarkedTobeDeleted()?"1":"0");
                    resStrBld.append(",");
                    String innerStr = "";
                    DBSelectionOfSmartNodes curSelectionSMNds = setEqTmpIter.getInterchngblNodes();
                    if(curSelectionSMNds!=null) {
                        AbstractSelectionOfSmartNodesManager abstractSelectionOfSmartNodesManager = AbstractSelectionOfSmartNodesManager.getInstance();
                        curSelectionSMNds = abstractSelectionOfSmartNodesManager.getSelectionOfSmartNodes(curSelectionSMNds.getId());
                        List<DBSmartNodeOfGateway> theNodesList = curSelectionSMNds.getDBSmartNodeOfGatewayList();
                        if(theNodesList!=null) {
                            StringBuilder innetStrBld = new StringBuilder();

                            for(DBSmartNodeOfGateway nodeInList: theNodesList){
                                DBRegisteredGateway gwOfNode = nodeInList.getParentGateWay(); //redundant but should work
                                if(gwOfNode!=null) {
                                    innetStrBld.append(gwOfNode.getRegisteredName());
                                    innetStrBld.append(",");
                                    innetStrBld.append(nodeInList.getIdWithinGateway());
                                    innetStrBld.append(",");
                                }else {
                                    logger.error("Could not get parent gw info from node in equivalency set");
                                }
                            }
                            innerStr = innetStrBld.toString();
                            //remove trailing comma
                            innerStr = innerStr.replaceAll("\\s*,\\s*$", "");
                        }
                    }
                    resStrBld.append(innerStr);
                    resStrBld.append(NEW_LINE_STR);
                }
            }

            responseResult = resStrBld.toString();

        }
        // ---------------------------------------------------------------------------------------------------------------------------
        // getNodesInEquivListForVGW(vgwID,equivListID,callback)
        // return: sensorId, isEnabled\n
        else if(functionSignature.compareToIgnoreCase("getNodesInEquivListForVGW")  == 0
                && pGatewayID !=null
                && pEquivListID !=null){

            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pEquivListID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();

            responseResult = resStrBld.toString();


        }
        // ---------------------------------------------------------------------------------------------------------------------------
        //
        // actUponEquivSetsListForVGW(vgwID,sensorID,equivListID,actionTk,callback)
        // returns:
        // for "add": newEntryID
        //              or -1 if error in inserting
        //              and -2 if found a duplicate
        //              and -3 ? if less than two nodes are defined in selection
        // for "delete": 0 if marked as deleted
        //              -1 otherwise (eg entry was not found?)
        else if(functionSignature.compareToIgnoreCase("actUponEquivSetsListForVGW") ==0
                && pGatewayID !=null
                //&& pSensorIDList !=null
                //&& pEquivListID !=null
                && pAction !=null){
            //logger.debug("ONe");
            if(pSensorIDList == null) {
                pSensorIDList = "";
            }
            if(pEquivListID == null) {
                pEquivListID = "";
            }
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorIDList);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pEquivListID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pAction);
            reqParamValuesStr=reqParamValsStrBld.toString();

            logger.debug("PARAMS: " + reqParamValuesStr);
            //logger.debug("two");
            StringBuilder resStrBld = new StringBuilder();
            if(pAction.compareToIgnoreCase("add")==0) {
                //logger.debug("three");
                DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pGatewayID);
                if(tmpDbRGw!=null)
                {
                    //logger.debug("four");
                    DBSelectionOfSmartNodes newSelectionOfNodes = new DBSelectionOfSmartNodes();
                    List<DBSmartNodeOfGateway> theSelectionList =  new ArrayList<DBSmartNodeOfGateway>();
                    newSelectionOfNodes.setDBSmartNodeOfGatewayList(theSelectionList);

                    String[] unparsedNodeSelectionArray = pSensorIDList.split(",");
                    if(unparsedNodeSelectionArray!=null && unparsedNodeSelectionArray.length > 0)
                    {
                        for(int k1= 0; k1 < unparsedNodeSelectionArray.length; k1++ )
                        {
                            String[] gatewayIdNodeId = unparsedNodeSelectionArray[k1].split("::##::");
                            if(gatewayIdNodeId!=null && gatewayIdNodeId.length==2
                                    && gatewayIdNodeId[0].compareToIgnoreCase(pGatewayID) == 0)
                            {
                                //gatewayIdNodeId[0];//gateId
                                //gatewayIdNodeId[1];//nodeId
                                DBSmartNodeOfGateway candNode = new DBSmartNodeOfGateway();

                                candNode.setParentGateWay(tmpDbRGw);
                                candNode.setIdWithinGateway(gatewayIdNodeId[1]);
                                theSelectionList.add(candNode);
                            }
                        }
                    }
                    //logger.debug("five");
                    Date tsOfLocalUpdate = new Date();
                    AbstractSetOfEquivNodesManager abstractSetOfEquivNodesManager = AbstractSetOfEquivNodesManager.getInstance();
                    Integer newEntryID = -1;
                    if(newSelectionOfNodes == null)   {
                        logger.debug("newSelectionOfNodes is NULL");
                    }
                    newEntryID = abstractSetOfEquivNodesManager.createSetOfEquivNodesReturnId(pGatewayID, newSelectionOfNodes, tsOfLocalUpdate);
                    resStrBld.append(newEntryID);
                    String msgToSend = EquivNodeListsVGWSynch.getInstance().createMessageForVGW(pGatewayID);
                    if( msgToSend!= null && !msgToSend.trim().isEmpty())  {
                        // send the message
                        if(ssUN!=null)
                        {   // this is only for debug
                            //ssUN.sendSelfAlertMessage(pGatewayID, msgToSend);
                            // this is the actual send
                            ssUN.sendDirectCommand(pGatewayID, msgToSend);
                        }
                    } else {
                        logger.error("No message to send!");
                    }

                }
            } else if(pAction.compareToIgnoreCase("delete")==0) {
                // deleted the selected equivalence list
                // (mark as deleted in DB and send update to VGW)
                //
                Integer statusValue= 0;
                Integer theListIDToBeDeleted = -1;
                if(pEquivListID== null || pEquivListID.isEmpty()) {
                    statusValue=-1;
                } else {
                    try {
                        theListIDToBeDeleted = Integer.valueOf(pEquivListID);
                    }
                    catch (Exception ex)
                    {
                        logger.error("Error while formating the id of an equiv list marked for deletion", ex);
                        statusValue=-2;
                    }
                }
                if(theListIDToBeDeleted > 0) {
                    AbstractSetOfEquivNodesManager  abstractSetOfEquivNodesManager = AbstractSetOfEquivNodesManager.getInstance();
                    SetOfEquivalentSensorNodes foundSet = abstractSetOfEquivNodesManager.getSetOfEquivNodes(theListIDToBeDeleted);
                    if(foundSet!=null) {
                        try {
                            abstractSetOfEquivNodesManager.updateSetOfEquivNodesMarkDeleted(theListIDToBeDeleted);
                        }catch(Exception e1) {
                            logger.error("Error while marking for deletion an equiv list", e1);
                            statusValue=-4; //process error
                        }
                        if(statusValue == 0) {
                            //send confirmation to VGW to delete it eventually
                            String msgToSend = EquivNodeListsVGWSynch.getInstance().createMessageForVGW(pGatewayID);
                            if( msgToSend!= null && !msgToSend.trim().isEmpty())  {
                                // send the message
                                if(ssUN!=null)
                                {   // this is only for debug
                                    //ssUN.sendSelfAlertMessage(pGatewayID, msgToSend);
                                    // this is the actual send
                                    ssUN.sendDirectCommand(pGatewayID, msgToSend);
                                }
                            } else {
                                logger.error("No message to send after marked deletion of equiv list!");
                            }
                        }
                    } else {
                        statusValue=-3; // not found
                    }
                }
                resStrBld.append(statusValue);
            }
            resStrBld.append(NEW_LINE_STR);
            responseResult = resStrBld.toString();
        }
    }
%>

<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <reply funct="<%=functionSignature %>" value="<%=responseResult %>" parNames="<%=reqParamNamesStr %>" parValues="<%=reqParamValuesStr %>" qstr="<%=reqData %>" ></reply>
</Answer>