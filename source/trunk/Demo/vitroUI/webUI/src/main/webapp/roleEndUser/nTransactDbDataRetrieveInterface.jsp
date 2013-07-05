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
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.FullComposedService" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.ServiceInstance" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSmartNodeOfGateway" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.*" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSelectionOfGateways" %>
<%@ page import="vitro.vspEngine.service.persistence.DBRegisteredGateway" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.logic.model.GatewayWithSmartNodes" %>
<%@ page import="vitro.vspEngine.logic.model.Gateway" %>
<%@ page import="vitro.vspEngine.logic.model.SmartNode" %>
<%@ page import="vitro.vspEngine.logic.model.SensorModel" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Observation" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="presentation.webgui.vitroappservlet.Model3dservice.*" %>
<%
    Logger logger = Logger.getLogger(this.getClass());
    String staticprefixCapability =vitro.vspEngine.logic.model.Capability.dcaPrefix;
    String xmerrordescr="";
    int errno = 0;
    String responseResult="";
    String functionSignature="";
    final String  NEW_LINE_STR="__nl__";

    String pServiceID =  "";
    String pInstanceID = "";
    String pGatewayID = "";
    String pSensorID = "";
    String pCapabilityID = "";
    String reqData = "";
    String reqParamNamesStr ="";
    String reqParamValuesStr = "";
    Enumeration<String> reqDataNames;

    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    try {
        reqData = request.getQueryString();
        reqDataNames = request.getParameterNames();

        functionSignature = request.getParameter("fid");
        pServiceID = request.getParameter("pServiceID");
        pInstanceID = request.getParameter("pInstanceID");
        pGatewayID = request.getParameter("pGatewayID");
        pSensorID = request.getParameter("pSensorID");
        pCapabilityID = request.getParameter("pCapabilityID");
    }catch(Exception ex) {
        errno = 1;
        xmerrordescr="Error in post data format!";
        functionSignature = "";
        reqDataNames = null;
        pServiceID ="";
        pInstanceID =  "";
        pGatewayID = "";
        pSensorID =  "";
        pCapabilityID =  "";
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
        if(functionSignature.equalsIgnoreCase("getServiceSensorListForComposedService") &&
                pServiceID!=null)
        {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            HashMap<String, Vector<String>>  gwToSmDevListHM = new HashMap<String,Vector<String>>();
            HashMap<Integer, Vector<Integer>> partialServiceIdToVectorOfCapsIds = new HashMap<Integer, Vector<Integer>>();
            int iServiceID = -1;
            try {
                iServiceID = Integer.parseInt(pServiceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id composed serv id: " + pServiceID);
            }
            // parameter: (serviceID)
            // return all the sensors for this Composed Service. If a whole gateway is involved then the sensor id becomes "all the (assumed) active motes of the gateway")
            // the flag in the end shows if a sensor also still exists in the last resource update from its gateway (or it has failed, so eg. it won't send any new values anymore -albeit the stored VSN specification still explicitly has this sensor in it).
            //                                                                                                      (or the gateway is offline entirely = has never send any resource update)
            //      flag == 0 :the sensor is not included in the latest resource update
            //      flag == 1: the sensor is included
            // "gateway_id,sensor_id,flag\n"
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {
                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(partialServiceTmpIter.getId());
                        //
                        if(!partialServiceIdToVectorOfCapsIds.containsKey(partialServiceTmpIter.getId())) {
                            partialServiceIdToVectorOfCapsIds.put(partialServiceTmpIter.getId(), new Vector<Integer>());
                        }
                        logger.debug("getServiceSensorListForComposedService - Get OBSERVED CAPS!");
                        List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                        try{
                            storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                        }catch(Exception ex002)
                        {
                            storedCapsAndRulesList =null;
                        }
                        if(storedCapsAndRulesList != null)
                        {

                            AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                             // for each capability in the partial Service
                            for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                            {

                                Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                                String currCapName = storedCapAndRules.getName();
                                partialServiceIdToVectorOfCapsIds.get(partialServiceTmpIter.getId()).addElement(storedCapAndRules.getId());
                                // for now we only allow sets of nodes from the map, so we check this list (not the regions list)
                                List<DBSelectionOfSmartNodes>  dbSelectionOfSmartNodeses = storedCapAndRules.getDBSelectionOfSmartNodesList();
                                if(dbSelectionOfSmartNodeses!=null)
                                {
                                    AbstractSelectionOfSmartNodesManager selectionOfSmartNodesManager =  AbstractSelectionOfSmartNodesManager.getInstance();
                                    for (DBSelectionOfSmartNodes  dbSelectionOfNodesTmpIter: dbSelectionOfSmartNodeses)
                                    {
                                        DBSelectionOfSmartNodes dbSelectionOfNodes = selectionOfSmartNodesManager.getSelectionOfSmartNodes(dbSelectionOfNodesTmpIter.getId());

                                        List<DBSmartNodeOfGateway> dbSmartNodeOfGatewayList = dbSelectionOfNodes.getDBSmartNodeOfGatewayList();
                                        if(dbSmartNodeOfGatewayList!=null)
                                        {
                                            AbstractSmartNodeOfGatewayManager abstractSmartNodeOfGatewayManager =  AbstractSmartNodeOfGatewayManager.getInstance();
                                            for (DBSmartNodeOfGateway dbSmartNodeTmpIter: dbSmartNodeOfGatewayList)
                                            {
                                                DBSmartNodeOfGateway dbSmartNode =  abstractSmartNodeOfGatewayManager.getSmartNodeOfGateway(dbSmartNodeTmpIter.getId());
                                                String currgwName = dbSmartNode.getParentGateWay().getRegisteredName();
                                                String nodeId = dbSmartNode.getIdWithinGateway();
                                                if(!gwToSmDevListHM.containsKey(currgwName)){
                                                    gwToSmDevListHM.put(currgwName, new Vector<String>());
                                                }
                                                Vector<String> smDevVec = gwToSmDevListHM.get(currgwName);
                                                if(!smDevVec.contains(nodeId))
                                                {
                                                    smDevVec.addElement(nodeId);
                                                }
                                            }
                                        }
                                    }
                                }
                                List<DBSelectionOfGateways>  dbSelectionOfGatewayseses = storedCapAndRules.getDBSelectionOfGatewaysList();
                                //get gateways as unique selections too. For now, we don't check for overlaps and identical sets
                                if(dbSelectionOfGatewayseses!=null){
                                    AbstractSelectionOfGatewaysManager selectionOfGatewaysManager =  AbstractSelectionOfGatewaysManager.getInstance();
                                    for (DBSelectionOfGateways  dbSelectionOfGatewaysTmpIter: dbSelectionOfGatewayseses)
                                    {
                                        DBSelectionOfGateways dbSelectionOfGateways = selectionOfGatewaysManager.getSelectionOfGateways(dbSelectionOfGatewaysTmpIter.getId());

                                        List<DBRegisteredGateway> dbRegisteredGatewayList = dbSelectionOfGateways.getDBRegisteredGatewayList();
                                        if(dbRegisteredGatewayList!=null)
                                        {
                                            AbstractGatewayManager abstractGatewayManager =  AbstractGatewayManager.getInstance();
                                            for (DBRegisteredGateway dbRegGatewayTmpIter: dbRegisteredGatewayList)
                                            {
                                                DBRegisteredGateway dRegGateway =  abstractGatewayManager.getDBRegisteredGatewayByIncId(dbRegGatewayTmpIter.getIdregisteredGateway());
                                                String currgwName = dRegGateway.getRegisteredName();
                                                String nodeId = QueryContentDefinition.selAllMotes;//special value, shows to ALL motes of gateway (not used here though)
                                                if(!gwToSmDevListHM.containsKey(currgwName)){
                                                    gwToSmDevListHM.put(currgwName, new Vector<String>());
                                                }
                                                Vector<String> smDevVec = gwToSmDevListHM.get(currgwName);
                                                //
                                                // get all nodes registered in the gateway from the SSUN object
                                                // put them in the smDevVec BUT
                                                // we need to not repeat the same nodes.
                                                //
                                                //
                                                if (ssUN != null) {
                                                        Gateway currGw = ssUN.getGatewaysToSmartDevsHM().get(currgwName);
                                                        if(currGw!=null) {
                                                            Vector<SmartNode> tmpActiveSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(currgwName).getSmartNodesVec();
                                                            for (SmartNode tmpSmNd : tmpActiveSmartDevVec)
                                                            {
                                                                if(!smDevVec.contains(tmpSmNd.getId()))
                                                                {
                                                                    smDevVec.add(tmpSmNd.getId());
                                                                }
                                                            }
                                                        }

                                                } else {
                                                   ; // TODO what do we do if the gateway is not connected?
                                                    smDevVec.add(QueryContentDefinition.selAllMotes);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // New for service continuation:
                // go through each resulted sensor,
                //
                //  TODO: we could have a more elaborate structure to not search verbosely for every sensor's last measurement,
                //      but only for sensors that are involved in the specific capability...
                // -------------***********-------------------------------------------------------------------------------
                // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                // %%%%%% Start OF NODE PLACEMENT AND ENABLED INFO %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                //check if gw exists in actively registered gateways (in mem)
                //
                HashMap<String, HashMap<String, Vector<String>>> gwToSmDevToSmDevInfo = new HashMap<String, HashMap<String, Vector<String>>>();
                HashMap<String, Vector<SmartNode>> allGWsToNodesHM = new HashMap<String, Vector<SmartNode>>();
                if(ssUN!=null)
                {
                    for(String itGWName: gwToSmDevListHM.keySet()){

                        if(ssUN.getGatewaysToSmartDevsHM()!=null
                                && !ssUN.getGatewaysToSmartDevsHM().isEmpty()
                                && ssUN.getGatewaysToSmartDevsHM().containsKey(itGWName)
                                && ssUN.getGatewaysToSmartDevsHM().get(itGWName)!=null
                                && ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec()!= null
                                && !ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec().isEmpty()){
                            GeoPoint vgwfirstRoomCenter = null;
                            try{
                                // CODE FROM translate to kml (VisualResultsModel)
                                Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
                                HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM = new HashMap<String,  Vector<Model3dInterfaceEntry>>();

                                StringBuilder coordsFound = new StringBuilder();
                                int totalNodes = 0;
                                int ignoredNodesWithNoCoordInfo = 0;
                                // A gateway can have a VECTOR of models associated with it (not just a single file -though it is preferred that way)
                                Vector<Model3dIndexEntry> currIndexEntriesVec = myModelsIndex.getIndexEntriesByGatewayId(itGWName);
                                for(int i = 0 ; i < currIndexEntriesVec.size(); i++)
                                {
                                    String tmpModelFilename = currIndexEntriesVec.elementAt(i).getModelFileName();
                                    String tmpMetaFilenameFullPath = Model3dIndex.getIndexPath() + currIndexEntriesVec.elementAt(i).getMetaFileName();
                                    long tmpDefaultInterfaceIdforCurrGw = currIndexEntriesVec.elementAt(i).getDefaultInterfaceIdForGwId(itGWName);
                                    Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                                    if(tmpMetaFile!=null)
                                    {
                                        // outPrintWriter.print("<p /><pre>"+tmpMetaFile.toString()+"</pre>");
                                        //
                                        // get the related Model3dInterfaceEntry from the specified metafile.
                                        //
                                        Model3dInterfaceEntry tmpRelInterfaceEntry = tmpMetaFile.findInterfaceEntry(itGWName, tmpDefaultInterfaceIdforCurrGw);
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

                                GatewayWithSmartNodes currGwObj = ssUN.getGatewaysToSmartDevsHM().get(itGWName);
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

                                        if(currInterfaceEntry.getGwId().compareToIgnoreCase(itGWName) == 0) {

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
                                allGWsToNodesHM.put(itGWName,allNodesInGWVector);
                            }
                            catch(Exception e)
                            {
                                //allGWsVector = null;
                                allGWsToNodesHM.put(itGWName,new Vector<SmartNode>());
                                logger.debug("Error while trying to calculate pos info of the smart nodes of " + itGWName +". Resources were probably not retrieved yet. Please wait a little longer.");
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
                                //centerPointX = Double.toString(gwXyz.getX());
                                //centerPointY = Double.toString(gwXyz.getY());
                            }
                        }
                    }

                    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                    // %%%% END OF NODE PLACEMENT AND ENABLED INFO       %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
                    // %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


                    // / 1. go through each resulted sensor,
                    // Get Enabled Status and Calculate Position Info in an array of 3 String Items [0] enabled 0 or 1, [1] Longitute, [2] Latitude
                    //
                    for(String itGWName: gwToSmDevListHM.keySet()){
                        gwToSmDevToSmDevInfo.put(itGWName, new HashMap<String, Vector<String>>());
                        for(String itSnId: gwToSmDevListHM.get(itGWName)) {
                            Vector<String> smDevInfoVec = new Vector<String>();
                            gwToSmDevToSmDevInfo.get(itGWName).put(itSnId, smDevInfoVec);

                            // get enabled status and position info

                            //int ignoredNodesWithNoCoordInfo = 0;
                            /* if(allGWsToNodesHM.isEmpty()) {
                                logger.debug("THIS MAP IS EMPTY");

                            }  else if(!allGWsToNodesHM.containsKey(itGWName)) {
                                logger.debug("THIS MAP HAS NOT KEY");

                            }         */
                            if(!allGWsToNodesHM.isEmpty() && allGWsToNodesHM.containsKey(itGWName)){
                                Vector<SmartNode> allNodesInGWVector = allGWsToNodesHM.get(itGWName);
                                for(SmartNode iSmNode : allNodesInGWVector) {
                                    if(iSmNode.getId().compareToIgnoreCase(itSnId)==0) {
                                        // GET ENABLED VALUE
                                        smDevInfoVec.addElement( (iSmNode.getRegistryProperties().isEnabled()? "1": "0") ); // [0]

                                        Coordinate myXyz = iSmNode.getCoordLocation();
                                        if(myXyz.getX()== null  || myXyz.getX().isNaN()  || myXyz.getY() == null || myXyz.getY().isNaN() )
                                        {
                                            //ignoredNodesWithNoCoordInfo+=1;
                                            smDevInfoVec.addElement("");  // [1]
                                            smDevInfoVec.addElement("");  // [2]
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
                                        smDevInfoVec.addElement(Double.toString(myXyz.getX()));        // [1]
                                        smDevInfoVec.addElement(Double.toString(myXyz.getY()));        // [2]
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    // 2.  again go through all the partialServices, Capabilities, the resulted sensors and get Last Observations to search for Continuation Info,
                    // todo reconsider this code. It might bring multiple time the same node as replacement for capabilities of different partial services...
                    for(String itGWName: gwToSmDevListHM.keySet()){
                        for(String itSnId: gwToSmDevListHM.get(itGWName)) {
                            Vector<SmartNode> uniqueReplacementSmartNodesVec = new Vector<SmartNode>();
                            Vector<String> smInfoVec = gwToSmDevToSmDevInfo.get(itGWName).get(itSnId);

                            for(Integer partServiceIdKey: partialServiceIdToVectorOfCapsIds.keySet()){
                                Vector<Integer> vecOfCaps = partialServiceIdToVectorOfCapsIds.get(partServiceIdKey);
                                for(Integer justACapIdKey: vecOfCaps) {


                                    AbstractObservationManager  abstractObservationManager = AbstractObservationManager.getInstance();

                                    Observation lastObs = abstractObservationManager.getLastObservationForFilters(partServiceIdKey, justACapIdKey, itGWName, itSnId);
                                    if (lastObs != null)
                                    {
                                        Date nowTm = new Date();
                                        long recentTimeWindowdiff = nowTm.getTime() - lastObs.getReceivedTimestamp().getTime();
                                        long diffSeconds = recentTimeWindowdiff / 1000;
                                        if(diffSeconds > 200) // more than 3 minutes (number is in seconds), then ignore, probably stale observation
                                        {
                                            continue;
                                        }
                                        logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                        logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                        logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                        logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                        if( lastObs.getSensorName()!=null && lastObs.getReplacmntSensorName()!=null &&
                                                !lastObs.getReplacmntSensorName().isEmpty() &&
                                                lastObs.getSensorName().compareToIgnoreCase(lastObs.getReplacmntSensorName())!=0){
                                            logger.debug("Found a SERVICE CONTINUATION SENSOR FOR CAPABILITY!!!" + lastObs.getResource());
                                            logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                            logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                            logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                            logger.debug("DIFF TIME IS " + Long.toString(diffSeconds));
                                            // Add info to vector of info for this sensor. We add this in quintets since the
                                            //  loop can discover other rep sensors too for other capabilities of this sensor
                                            // [3] repSensorId, [4]repLong, [5]repLat, [6]forCapabilityId, [7]forCapabilityName
                                            smInfoVec.addElement(lastObs.getReplacmntSensorName());
                                            smInfoVec.addElement(Integer.toString(lastObs.getCapabilityID()));
                                            smInfoVec.addElement(lastObs.getResource());
                                            String currentCapNameNoPrefix = lastObs.getResource().replaceAll(Pattern.quote(staticprefixCapability),"" );;
                                            String currentCapIconName = vitro.vspEngine.logic.model.Capability.getDefaultIcon(currentCapNameNoPrefix);
                                            smInfoVec.addElement(currentCapIconName);


                                            boolean foundInUniqFakeNodesVes = false;
                                            for(SmartNode repSmrtNodeInVec : uniqueReplacementSmartNodesVec){
                                                if(repSmrtNodeInVec.getId().compareToIgnoreCase(lastObs.getReplacmntSensorName())== 0){
                                                    foundInUniqFakeNodesVes = true;
                                                    break;
                                                }
                                            }
                                            if(!foundInUniqFakeNodesVes)
                                            {
                                                SmartNode tmpFakeNode = new SmartNode();
                                                tmpFakeNode.setId(lastObs.getReplacmntSensorName());
                                                tmpFakeNode.setName(lastObs.getReplacmntSensorName());
                                                uniqueReplacementSmartNodesVec.addElement(tmpFakeNode);
                                            }
                                            smInfoVec.addElement("");    //is changed when the position is calculated bellow
                                            smInfoVec.addElement("");    //is changed when the position is calculated bellow

                                        }
                                    }
                                }
                            }
                            // go through replacement node ids.
                            // put them in a small circle around the node:
                            // calculate
                            // TODO: rep nodes position in a circle around the sensor node
                            //
                            GeoPoint orginalNodeCenterPoint = null;
                            try{
                                int numOfReplacementPoints = uniqueReplacementSmartNodesVec.size();
                                if(smInfoVec.size()>2) {
                                    orginalNodeCenterPoint = new GeoPoint(smInfoVec.elementAt(2), smInfoVec.elementAt(1), "0");
                                }
                                if(orginalNodeCenterPoint!=null && numOfReplacementPoints>0 ) {
                                    logger.debug("THIS numOfReplacementPoints" + Integer.toString(numOfReplacementPoints));
                                    logger.debug("THIS numOfReplacementPoints" + Integer.toString(numOfReplacementPoints));
                                    logger.debug("THIS numOfReplacementPoints" + Integer.toString(numOfReplacementPoints));
                                    //radius in kilometers
                                    Vector<GeoPoint> tmpVecOfGeo = GeoPoint.placePointsOnACircle(orginalNodeCenterPoint, 0.007, numOfReplacementPoints );
                                    if(tmpVecOfGeo!=null)
                                    {   int assignedPoints = 0;
                                        Iterator<SmartNode> smartNodeIt2 = uniqueReplacementSmartNodesVec.iterator();
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
                                        for(SmartNode tmpFakeReplNode : uniqueReplacementSmartNodesVec) {
                                            for(int y = 3; y < smInfoVec.size(); y+=6) {
                                                // 4 elements for original node, 6-plets of records for replacement nodes, 5th and 6th elements in the 6plets.
                                                if(smInfoVec.elementAt(y).compareToIgnoreCase(tmpFakeReplNode.getId())==0){
                                                    smInfoVec.set(y+ 4 , Double.toString(tmpFakeReplNode.getCoordLocation().getX()));
                                                    smInfoVec.set(y+ 4 + 1, Double.toString(tmpFakeReplNode.getCoordLocation().getY()));
                                                    //don't break because it could be multiple times there in the smInfoVec
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception exx) {
                                logger.error("Error while calculating orbits around original sensor");
                            }
                        }
                    }
                } // ssUN object != null
                // -------------***********-------------------------------------------------------------------------------
                // end new for service continuation
                //
                //

                StringBuilder resStrBld = new StringBuilder();
                // in the end we parse the gwToSmDevListHM and return the CSV
                for(String itGWName: gwToSmDevListHM.keySet()){
                    for(String itSnId: gwToSmDevListHM.get(itGWName)) {
                        Vector<String> smInfoVec = gwToSmDevToSmDevInfo.get(itGWName).get(itSnId);
                        boolean nodeFoundAsActive = false;
                        if(ssUN != null && ssUN.getGatewaysToSmartDevsHM().containsKey(itGWName)
                                && ssUN.getGatewaysToSmartDevsHM().get(itGWName)!=null && ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec().isEmpty()){
                            Vector<SmartNode> tmpActiveSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec();

                            for (SmartNode tmpSmNd : tmpActiveSmartDevVec)
                            {
                                if(itSnId.compareTo(tmpSmNd.getId())==0)
                                {
                                    nodeFoundAsActive = true;
                                    resStrBld.append(itGWName);
                                    resStrBld.append(",");
                                    resStrBld.append(itSnId);
                                    resStrBld.append(",");
                                    resStrBld.append("1");
                                    for(String itemInInfo: smInfoVec) {
                                        resStrBld.append(",");
                                        resStrBld.append(itemInInfo);
                                    }
                                    resStrBld.append(NEW_LINE_STR);
                                    break;
                                }
                            }
                            if(!nodeFoundAsActive){
                                resStrBld.append(itGWName);
                                resStrBld.append(",");
                                resStrBld.append(itSnId);
                                resStrBld.append(",");
                                resStrBld.append("0");
                                // Then, there is no info for it in the smInfoVec (because it is updated only when not is found active (in-mem)
                                // So, append only dummy info:
                                //
                                resStrBld.append(",");
                                resStrBld.append("1"); //enabled
                                resStrBld.append(",");
                                resStrBld.append("");//long
                                resStrBld.append(",");
                                resStrBld.append("");//lat
                                //for(String itemInInfo: smInfoVec) {
                                //    resStrBld.append(",");
                                //    resStrBld.append(itemInInfo);
                                //}
                                resStrBld.append(NEW_LINE_STR);
                            }
                        }
                        else {
                            resStrBld.append(itGWName);
                            resStrBld.append(",");
                            resStrBld.append(itSnId);
                            resStrBld.append(",");
                            resStrBld.append("0");
                            // Then, there is no info for it in the smInfoVec (because it is updated only when not is found active (in-mem)
                            // So, append only dummy info:
                            //
                            resStrBld.append(",");
                            resStrBld.append("1"); //enabled
                            resStrBld.append(",");
                            resStrBld.append("");//long
                            resStrBld.append(",");
                            resStrBld.append("");//lat
                            //for(String itemInInfo: smInfoVec) {
                            //    resStrBld.append(",");
                            //    resStrBld.append(itemInInfo);
                            //}
                            resStrBld.append(NEW_LINE_STR);
                        }
                    }
                }
                responseResult = resStrBld.toString();
            }

        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getServiceSensorListForPartialService") &&
                pInstanceID!=null) {
            // TODO:  this function does not yet support service continuation info
            // parameter:  (instanceID)
            // return all the sensors for this Partial Service. If a whole gateway is involved then the sensor id becomes "all the (assumed) active motes of the gateway")
            // the flag in the end shows if a sensor also still exists in the last resource update from its gateway (or it has failed, so eg. it won't send any new values anymore -albeit the stored VSN specification still explicitly has this sensor in it).
            //                                                                                                      (or the gateway is offline entirely = has never send any resource update)
            //      flag == 0 :the sensor is not included in the latest resource update
            //      flag == 1: the sensor is included
            // "gateway_id,sensor_id,flag\n"
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pInstanceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            int iInstanceID = -1;
            try {
                iInstanceID = Integer.parseInt(pInstanceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id partial serv id: " + pInstanceID);
            }
            HashMap<String, Vector<String>>  gwToSmDevListHM = new HashMap<String, Vector<String>>();
            AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();

            ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(iInstanceID);
            //
            logger.debug("getServiceSensorListForPartialService - Get OBSERVED CAPS!");
            List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
            try{
                storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
            }catch(Exception ex002)
            {
                storedCapsAndRulesList =null;
            }
            if(storedCapsAndRulesList != null)
            {
                AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                // for each capability in the partial Service
                for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                {
                    Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                    String currCapName = storedCapAndRules.getName();

                    // for now we only allow sets of nodes from the map, so we check this list (not the regions list)
                    List<DBSelectionOfSmartNodes>  dbSelectionOfSmartNodeses = storedCapAndRules.getDBSelectionOfSmartNodesList();
                    if(dbSelectionOfSmartNodeses!=null)
                    {
                        AbstractSelectionOfSmartNodesManager selectionOfSmartNodesManager =  AbstractSelectionOfSmartNodesManager.getInstance();
                        for (DBSelectionOfSmartNodes  dbSelectionOfNodesTmpIter: dbSelectionOfSmartNodeses)
                        {
                            DBSelectionOfSmartNodes dbSelectionOfNodes = selectionOfSmartNodesManager.getSelectionOfSmartNodes(dbSelectionOfNodesTmpIter.getId());

                            List<DBSmartNodeOfGateway> dbSmartNodeOfGatewayList = dbSelectionOfNodes.getDBSmartNodeOfGatewayList();
                            if(dbSmartNodeOfGatewayList!=null)
                            {
                                AbstractSmartNodeOfGatewayManager abstractSmartNodeOfGatewayManager =  AbstractSmartNodeOfGatewayManager.getInstance();
                                for (DBSmartNodeOfGateway dbSmartNodeTmpIter: dbSmartNodeOfGatewayList)
                                {
                                    DBSmartNodeOfGateway dbSmartNode =  abstractSmartNodeOfGatewayManager.getSmartNodeOfGateway(dbSmartNodeTmpIter.getId());
                                    String currgwName = dbSmartNode.getParentGateWay().getRegisteredName();
                                    String nodeId = dbSmartNode.getIdWithinGateway();
                                    if(!gwToSmDevListHM.containsKey(currgwName)){
                                        gwToSmDevListHM.put(currgwName, new Vector<String>());
                                    }
                                    Vector<String> smDevVec = gwToSmDevListHM.get(currgwName);
                                    if(!smDevVec.contains(nodeId))
                                    {
                                        smDevVec.addElement(nodeId);
                                    }
                                }
                            }
                        }
                    }
                    List<DBSelectionOfGateways>  dbSelectionOfGatewayseses = storedCapAndRules.getDBSelectionOfGatewaysList();
                    //get gateways as unique selections too. For now, we don't check for overlaps and identical sets
                    if(dbSelectionOfGatewayseses!=null){
                        AbstractSelectionOfGatewaysManager selectionOfGatewaysManager =  AbstractSelectionOfGatewaysManager.getInstance();
                        for (DBSelectionOfGateways  dbSelectionOfGatewaysTmpIter: dbSelectionOfGatewayseses)
                        {
                            DBSelectionOfGateways dbSelectionOfGateways = selectionOfGatewaysManager.getSelectionOfGateways(dbSelectionOfGatewaysTmpIter.getId());

                            List<DBRegisteredGateway> dbRegisteredGatewayList = dbSelectionOfGateways.getDBRegisteredGatewayList();
                            if(dbRegisteredGatewayList!=null)
                            {
                                AbstractGatewayManager abstractGatewayManager =  AbstractGatewayManager.getInstance();
                                for (DBRegisteredGateway dbRegGatewayTmpIter: dbRegisteredGatewayList)
                                {
                                    DBRegisteredGateway dRegGateway =  abstractGatewayManager.getDBRegisteredGatewayByIncId(dbRegGatewayTmpIter.getIdregisteredGateway());
                                    String currgwName = dRegGateway.getRegisteredName();
                                    String nodeId = QueryContentDefinition.selAllMotes;//special value, shows to ALL motes of gateway (not used here though)
                                    if(!gwToSmDevListHM.containsKey(currgwName)){
                                        gwToSmDevListHM.put(currgwName, new Vector<String>());
                                    }
                                    Vector<String> smDevVec = gwToSmDevListHM.get(currgwName);
                                    //
                                    // get all nodes registered in the gateway from the SSUN object
                                    // put them in the smDevVec BUT
                                    // we need to not repeat the same nodes.
                                    //
                                    //
                                    if (ssUN != null) {
                                        Gateway currGw = ssUN.getGatewaysToSmartDevsHM().get(currgwName);
                                        if(currGw!=null) {
                                            Vector<SmartNode> tmpActiveSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(currgwName).getSmartNodesVec();
                                            for (SmartNode tmpSmNd : tmpActiveSmartDevVec)
                                            {
                                                if(!smDevVec.contains(tmpSmNd.getId()))
                                                {
                                                    smDevVec.add(tmpSmNd.getId());
                                                }
                                            }
                                        }

                                    } else {
                                        ; // TODO what do we do if the gateway is not connected?
                                        smDevVec.add(QueryContentDefinition.selAllMotes);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            StringBuilder resStrBld = new StringBuilder();
            // in the end we parse the gwToSmDevListHM and return the CSV
            for(String itGWName: gwToSmDevListHM.keySet()){
                for(String itSnId: gwToSmDevListHM.get(itGWName)) {
                    boolean nodeFoundAsActive = false;
                    if(ssUN != null && ssUN.getGatewaysToSmartDevsHM().containsKey(itGWName)
                            && ssUN.getGatewaysToSmartDevsHM().get(itGWName)!=null && ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec().isEmpty()){
                        Vector<SmartNode> tmpActiveSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(itGWName).getSmartNodesVec();
                        for (SmartNode tmpSmNd : tmpActiveSmartDevVec)
                        {
                            if(itSnId.compareTo(tmpSmNd.getId())==0)
                            {
                                nodeFoundAsActive = true;
                                resStrBld.append(itGWName);
                                resStrBld.append(",");
                                resStrBld.append(itSnId);
                                resStrBld.append(",");
                                resStrBld.append("1");
                                resStrBld.append(NEW_LINE_STR);
                                break;
                            }
                        }
                        if(!nodeFoundAsActive){
                            resStrBld.append(itGWName);
                            resStrBld.append(",");
                            resStrBld.append(itSnId);
                            resStrBld.append(",");
                            resStrBld.append("0");
                            resStrBld.append(NEW_LINE_STR);
                        }
                    }
                    else {
                        resStrBld.append(itGWName);
                        resStrBld.append(",");
                        resStrBld.append(itSnId);
                        resStrBld.append(",");
                        resStrBld.append("0");
                        resStrBld.append(NEW_LINE_STR);
                    }
                }
            }
            responseResult = resStrBld.toString();
        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getSensorCapabilityListForComposedService")&&
                pServiceID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty()) {
            // parameters: (serviceID, gatewayID, sensorID)
            // return all the Capabilities *REQUESTED* for this Sensor in all of the composed service.
            // flags are 0 for false, and 1 for true
            // "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,sensor_is_active_flag,sensor_supports_this_capability_flag,capability_is_supported_at_all_flag\n"
            //
            // parse all caps per partial service. keep only the capabilities that contain this sensor!
            // additionally check if this sensor is active, and if it supports the given capability!

            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            HashMap<String, Vector<String>>  partServIdToCapabilitiesCSV_HM = new HashMap<String,Vector<String>>();
            int iServiceID = -1;
            try {
                iServiceID = Integer.parseInt(pServiceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id composed serv id: " + pServiceID);
            }

            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {
                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        String currPartServiceId = Integer.toString(partialServiceTmpIter.getId());
                        ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(partialServiceTmpIter.getId());
                        //
                        logger.debug("getSensorCapabilityListForComposedService - Get OBSERVED CAPS!");
                        List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                        try{
                            storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                        }catch(Exception ex002)
                        {
                            storedCapsAndRulesList =null;
                        }
                        if(storedCapsAndRulesList != null)
                        {
                            AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                            // for each capability in the partial Service
                            boolean foundTheNodeInAnyCapability = false; // moved this outside this loop
                            for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                            {
                                Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                                //
                                //
                                //
                                String currCapName = storedCapAndRules.getName();
                                // ### FIND NODE FOR CAPABILITY ##############################################
                                //
                                // found node (could become a method)
                                //
                                boolean foundTheNodeInThisCapability = false;
                                List<DBSelectionOfSmartNodes>  dbSelectionOfSmartNodeses = storedCapAndRules.getDBSelectionOfSmartNodesList();
                                if(dbSelectionOfSmartNodeses!=null)
                                {
                                    AbstractSelectionOfSmartNodesManager selectionOfSmartNodesManager =  AbstractSelectionOfSmartNodesManager.getInstance();
                                    for (DBSelectionOfSmartNodes  dbSelectionOfNodesTmpIter: dbSelectionOfSmartNodeses)
                                    {
                                        DBSelectionOfSmartNodes dbSelectionOfNodes = selectionOfSmartNodesManager.getSelectionOfSmartNodes(dbSelectionOfNodesTmpIter.getId());

                                        List<DBSmartNodeOfGateway> dbSmartNodeOfGatewayList = dbSelectionOfNodes.getDBSmartNodeOfGatewayList();
                                        if(dbSmartNodeOfGatewayList!=null && !foundTheNodeInThisCapability)
                                        {
                                            AbstractSmartNodeOfGatewayManager abstractSmartNodeOfGatewayManager =  AbstractSmartNodeOfGatewayManager.getInstance();
                                            for (DBSmartNodeOfGateway dbSmartNodeTmpIter: dbSmartNodeOfGatewayList)
                                            {
                                                DBSmartNodeOfGateway dbSmartNode =  abstractSmartNodeOfGatewayManager.getSmartNodeOfGateway(dbSmartNodeTmpIter.getId());
                                                String currgwName = dbSmartNode.getParentGateWay().getRegisteredName();
                                                String nodeId = dbSmartNode.getIdWithinGateway();
                                                // compare with given node (of post parameters)
                                                // if match, add the capability to the vector (if not exists)
                                                if(currgwName.compareToIgnoreCase(pGatewayID) == 0 && nodeId.compareToIgnoreCase(pSensorID) == 0)
                                                {
                                                    foundTheNodeInThisCapability = true;
                                                    foundTheNodeInAnyCapability = true;
                                                    if(!partServIdToCapabilitiesCSV_HM.containsKey(currPartServiceId)){
                                                        partServIdToCapabilitiesCSV_HM.put(currPartServiceId, new Vector<String>());
                                                    }
                                                    Vector<String> capsCsvVec = partServIdToCapabilitiesCSV_HM.get(currPartServiceId);
                                                    StringBuilder capsCsvBld = new StringBuilder();
                                                    capsCsvBld.append(storedCapAndRules.getId());
                                                    capsCsvBld.append(",");
                                                    capsCsvBld.append(storedCapAndRules.getName());
                                                    capsCsvBld.append(",");
                                                    capsCsvBld.append(storedCapAndRules.getFunction());
                                                    capsCsvBld.append(",");
                                                    capsCsvBld.append(storedCapAndRules.getHasTrigger().compareToIgnoreCase("yes")==0 ? 1:0);
                                                    capsCsvBld.append(",");
                                                    capsCsvBld.append(storedCapAndRules.getTriggerConditionSign());
                                                    capsCsvBld.append(",");
                                                    capsCsvBld.append(storedCapAndRules.getTriggerConditionValue());
                                                    capsCsvVec.addElement(capsCsvBld.toString());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if(!foundTheNodeInThisCapability) //check if it is included in whole gateway selected sets
                                {
                                    List<DBSelectionOfGateways>  dbSelectionOfGatewayseses = storedCapAndRules.getDBSelectionOfGatewaysList();
                                    //get gateways as unique selections too. For now, we don't check for overlaps and identical sets
                                    if(dbSelectionOfGatewayseses!=null){
                                        AbstractSelectionOfGatewaysManager selectionOfGatewaysManager =  AbstractSelectionOfGatewaysManager.getInstance();
                                        for (DBSelectionOfGateways  dbSelectionOfGatewaysTmpIter: dbSelectionOfGatewayseses)
                                        {
                                            DBSelectionOfGateways dbSelectionOfGateways = selectionOfGatewaysManager.getSelectionOfGateways(dbSelectionOfGatewaysTmpIter.getId());

                                            List<DBRegisteredGateway> dbRegisteredGatewayList = dbSelectionOfGateways.getDBRegisteredGatewayList();
                                            if(dbRegisteredGatewayList!=null && !foundTheNodeInThisCapability)
                                            {
                                                AbstractGatewayManager abstractGatewayManager =  AbstractGatewayManager.getInstance();
                                                for (DBRegisteredGateway dbRegGatewayTmpIter: dbRegisteredGatewayList)
                                                {
                                                    DBRegisteredGateway dRegGateway =  abstractGatewayManager.getDBRegisteredGatewayByIncId(dbRegGatewayTmpIter.getIdregisteredGateway());
                                                    String currgwName = dRegGateway.getRegisteredName();
                                                    if(currgwName.compareToIgnoreCase(pGatewayID) == 0 )
                                                    {
                                                        foundTheNodeInThisCapability = true;
                                                        foundTheNodeInAnyCapability= true;
                                                        if(!partServIdToCapabilitiesCSV_HM.containsKey(currPartServiceId)){
                                                            partServIdToCapabilitiesCSV_HM.put(currPartServiceId, new Vector<String>());
                                                        }
                                                        Vector<String> capsCsvVec = partServIdToCapabilitiesCSV_HM.get(currPartServiceId);
                                                        StringBuilder capsCsvBld = new StringBuilder();
                                                        capsCsvBld.append(storedCapAndRules.getId());
                                                        capsCsvBld.append(",");
                                                        capsCsvBld.append(storedCapAndRules.getName());
                                                        capsCsvBld.append(",");
                                                        capsCsvBld.append(storedCapAndRules.getFunction());
                                                        capsCsvBld.append(",");
                                                        capsCsvBld.append(storedCapAndRules.getHasTrigger().compareToIgnoreCase("yes")==0 ? 1:0);
                                                        capsCsvBld.append(",");
                                                        capsCsvBld.append(storedCapAndRules.getTriggerConditionSign());
                                                        capsCsvBld.append(",");
                                                        capsCsvBld.append(storedCapAndRules.getTriggerConditionValue());
                                                        capsCsvVec.addElement(capsCsvBld.toString());
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // ## END FIND NODE FOR CAPABILITY ############################################
                            }
                            //if(!foundTheNodeInAnyCapability) {
                            //    String tmpErrMsg ="Requested node id: "+ pSensorID + " was not found for this partial service!! ::"+ currPartServiceId;
                            //    logger.debug(tmpErrMsg);
                            //    errno = 1;
                            //    xmerrordescr=tmpErrMsg;
                            //    //todo do we break here?  // NO IT"S WRONG TO BREAK!
                            //}
                        }
                        else { //new: error case logged
                            String tmpErrMsg ="Partial Service ID had no capabilities!! ::" + currPartServiceId;
                            logger.debug(tmpErrMsg);
                            errno = 1;
                            xmerrordescr=tmpErrMsg;
                            // todo do we beak here?
                        }
                    }
                }
                else { //new: error case logged
                    String tmpErrMsg ="Service ID had no partial services!! ::" + pServiceID;
                    logger.debug(tmpErrMsg);
                    errno = 1;
                    xmerrordescr=tmpErrMsg;
                }
            }
            else { //new: error case logged
                String tmpErrMsg ="Service ID was not found::" + pServiceID;
                logger.debug(tmpErrMsg);
                errno = 1;
                xmerrordescr=tmpErrMsg;
            }

            // Check if node exists in active resource update
            boolean nodeIsActive = false;
            SmartNode tmpActiveNode = null;
            if(ssUN != null && ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)
                    && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID)!=null && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec().isEmpty()){
                Vector<SmartNode> tmpActiveSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec();
                for (SmartNode tmpSmNd : tmpActiveSmartDevVec)
                {
                    if(pSensorID.compareTo(tmpSmNd.getId())==0 && !nodeIsActive)
                    {
                        nodeIsActive = true;
                        tmpActiveNode =  tmpSmNd;
                        break;
                    }
                }
            }    else {
                //new: error case logged
                String tmpErrMsg ="";
                if (ssUN == null) {
                    tmpErrMsg ="VSP node not found";
                } else if (!ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)) {
                    tmpErrMsg ="VSP does not include gateway: " + pGatewayID;
                }
                logger.debug(tmpErrMsg);
                errno = 1;
                xmerrordescr=tmpErrMsg;
            }

            StringBuilder resStrBld = new StringBuilder();
            for(String itPartSrvId: partServIdToCapabilitiesCSV_HM.keySet()){
                for(String itCapCsv: partServIdToCapabilitiesCSV_HM.get(itPartSrvId)) {

                    resStrBld.append(itPartSrvId);
                    resStrBld.append(",");
                    resStrBld.append(itCapCsv);
                    resStrBld.append(",");

                    String[] capCsvTokens = itCapCsv.split(",");
                    String capName = capCsvTokens[1];
                    resStrBld.append(nodeIsActive? "1": "0");
                    resStrBld.append(",");
                    boolean nodeSupportsCap = false;
                    // Check for each capability if the node actually (still or ever) supports it
                    if(tmpActiveNode!=null && tmpActiveNode.getCapabilitiesVector()!=null && tmpActiveNode.getCapabilitiesVector().size()>0){
                        Iterator<SensorModel> capsIt = tmpActiveNode.getCapabilitiesVector().iterator();
                        SensorModel currentCap;
                        while(capsIt.hasNext() &&!nodeSupportsCap) {
                            currentCap = capsIt.next();
                            if(currentCap.getName().compareToIgnoreCase(capName) ==0 )
                            {
                                nodeSupportsCap=true;
                                break;
                            }
                        }
                    }
                    resStrBld.append(nodeSupportsCap? "1": "0");
                    resStrBld.append(",");
                    // check if the capability even exists anywhere supported in the DVNS
                    if(ssUN != null && ssUN.getCapabilitiesTable().containsKey(capName)) {
                        resStrBld.append("1");
                        resStrBld.append(NEW_LINE_STR);
                    }
                    else {
                        resStrBld.append("0");
                        resStrBld.append(NEW_LINE_STR);
                    }
                }
            }
            responseResult = resStrBld.toString();
        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getSensorCapabilityListForPartialService")&&
                pInstanceID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty() ) {
            // parameters: (instanceID, gatewayID, sensorID)
            // return all the Capabilities *REQUESTED* for this Sensor for only the specified partial service.
            // flags are 0 for false, and 1 for true
            // "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,sensor_is_active_flag,sensor_supports_this_capability_flag,capability_is_supported_at_all_flag\n"
            //
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pInstanceID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            HashMap<String, Vector<String>>  partServIdToCapabilitiesCSV_HM = new HashMap<String,Vector<String>>();
            int iInstanceID = -1;
            try {
                iInstanceID = Integer.parseInt(pInstanceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id partial serv id: " + pInstanceID);
            }
            AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
            String currPartServiceId = Integer.toString(iInstanceID);
            ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(iInstanceID);
            //
            logger.debug("getSensorCapabilityListForPartialService - Get OBSERVED CAPS!");
            List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
            try{
                storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
            }catch(Exception ex002)
            {
                storedCapsAndRulesList =null;
            }
            if(storedCapsAndRulesList != null)
            {
                AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                // for each capability in the partial Service
                for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                {
                    Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                    //
                    //
                    //
                    String currCapName = storedCapAndRules.getName();
                    // ### FIND NODE FOR CAPABILITY ##############################################
                    //
                    // found node (could become a method)
                    //
                    boolean foundTheNodeInThisCapability = false;
                    List<DBSelectionOfSmartNodes>  dbSelectionOfSmartNodeses = storedCapAndRules.getDBSelectionOfSmartNodesList();
                    if(dbSelectionOfSmartNodeses!=null)
                    {
                        AbstractSelectionOfSmartNodesManager selectionOfSmartNodesManager =  AbstractSelectionOfSmartNodesManager.getInstance();
                        for (DBSelectionOfSmartNodes  dbSelectionOfNodesTmpIter: dbSelectionOfSmartNodeses)
                        {
                            DBSelectionOfSmartNodes dbSelectionOfNodes = selectionOfSmartNodesManager.getSelectionOfSmartNodes(dbSelectionOfNodesTmpIter.getId());

                            List<DBSmartNodeOfGateway> dbSmartNodeOfGatewayList = dbSelectionOfNodes.getDBSmartNodeOfGatewayList();
                            if(dbSmartNodeOfGatewayList!=null && !foundTheNodeInThisCapability)
                            {
                                AbstractSmartNodeOfGatewayManager abstractSmartNodeOfGatewayManager =  AbstractSmartNodeOfGatewayManager.getInstance();
                                for (DBSmartNodeOfGateway dbSmartNodeTmpIter: dbSmartNodeOfGatewayList)
                                {
                                    DBSmartNodeOfGateway dbSmartNode =  abstractSmartNodeOfGatewayManager.getSmartNodeOfGateway(dbSmartNodeTmpIter.getId());
                                    String currgwName = dbSmartNode.getParentGateWay().getRegisteredName();
                                    String nodeId = dbSmartNode.getIdWithinGateway();
                                    // compare with given node (of post parameters)
                                    // if match, add the capability to the vector (if not exists)
                                    if(currgwName.compareToIgnoreCase(pGatewayID) == 0 && nodeId.compareToIgnoreCase(pSensorID) == 0)
                                    {
                                        foundTheNodeInThisCapability = true;
                                        if(!partServIdToCapabilitiesCSV_HM.containsKey(currPartServiceId)){
                                            partServIdToCapabilitiesCSV_HM.put(currPartServiceId, new Vector<String>());
                                        }
                                        Vector<String> capsCsvVec = partServIdToCapabilitiesCSV_HM.get(currPartServiceId);
                                        StringBuilder capsCsvBld = new StringBuilder();
                                        capsCsvBld.append(storedCapAndRules.getId());
                                        capsCsvBld.append(",");
                                        capsCsvBld.append(storedCapAndRules.getName());
                                        capsCsvBld.append(",");
                                        capsCsvBld.append(storedCapAndRules.getFunction());
                                        capsCsvBld.append(",");
                                        capsCsvBld.append(storedCapAndRules.getHasTrigger().compareToIgnoreCase("yes")==0 ? 1:0);
                                        capsCsvBld.append(",");
                                        capsCsvBld.append(storedCapAndRules.getTriggerConditionSign());
                                        capsCsvBld.append(",");
                                        capsCsvBld.append(storedCapAndRules.getTriggerConditionValue());
                                        capsCsvVec.addElement(capsCsvBld.toString());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if(!foundTheNodeInThisCapability)
                    {
                        List<DBSelectionOfGateways>  dbSelectionOfGatewayseses = storedCapAndRules.getDBSelectionOfGatewaysList();
                        //get gateways as unique selections too. For now, we don't check for overlaps and identical sets
                        if(dbSelectionOfGatewayseses!=null){
                            AbstractSelectionOfGatewaysManager selectionOfGatewaysManager =  AbstractSelectionOfGatewaysManager.getInstance();
                            for (DBSelectionOfGateways  dbSelectionOfGatewaysTmpIter: dbSelectionOfGatewayseses)
                            {
                                DBSelectionOfGateways dbSelectionOfGateways = selectionOfGatewaysManager.getSelectionOfGateways(dbSelectionOfGatewaysTmpIter.getId());

                                List<DBRegisteredGateway> dbRegisteredGatewayList = dbSelectionOfGateways.getDBRegisteredGatewayList();
                                if(dbRegisteredGatewayList!=null && !foundTheNodeInThisCapability)
                                {
                                    AbstractGatewayManager abstractGatewayManager =  AbstractGatewayManager.getInstance();
                                    for (DBRegisteredGateway dbRegGatewayTmpIter: dbRegisteredGatewayList)
                                    {
                                        DBRegisteredGateway dRegGateway =  abstractGatewayManager.getDBRegisteredGatewayByIncId(dbRegGatewayTmpIter.getIdregisteredGateway());
                                        String currgwName = dRegGateway.getRegisteredName();
                                        if(currgwName.compareToIgnoreCase(pGatewayID) == 0 )
                                        {
                                            foundTheNodeInThisCapability = true;
                                            if(!partServIdToCapabilitiesCSV_HM.containsKey(currPartServiceId)){
                                                partServIdToCapabilitiesCSV_HM.put(currPartServiceId, new Vector<String>());
                                            }
                                            Vector<String> capsCsvVec = partServIdToCapabilitiesCSV_HM.get(currPartServiceId);
                                            StringBuilder capsCsvBld = new StringBuilder();
                                            capsCsvBld.append(storedCapAndRules.getId());
                                            capsCsvBld.append(",");
                                            capsCsvBld.append(storedCapAndRules.getName());
                                            capsCsvBld.append(",");
                                            capsCsvBld.append(storedCapAndRules.getFunction());
                                            capsCsvBld.append(",");
                                            capsCsvBld.append(storedCapAndRules.getHasTrigger().compareToIgnoreCase("yes")==0 ? 1:0);
                                            capsCsvBld.append(",");
                                            capsCsvBld.append(storedCapAndRules.getTriggerConditionSign());
                                            capsCsvBld.append(",");
                                            capsCsvBld.append(storedCapAndRules.getTriggerConditionValue());
                                            capsCsvVec.addElement(capsCsvBld.toString());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // ## END FIND NODE FOR CAPABILITY ############################################
                }
            }
            // Check if node exists in active resource update
            boolean nodeIsActive = false;
            SmartNode tmpActiveNode = null;
            if(ssUN != null && ssUN.getGatewaysToSmartDevsHM().containsKey(pGatewayID)
                    && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID)!=null && ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec()!= null && !ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec().isEmpty()){
                Vector<SmartNode> tmpActiveSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(pGatewayID).getSmartNodesVec();
                for (SmartNode tmpSmNd : tmpActiveSmartDevVec)
                {
                    if(pSensorID.compareTo(tmpSmNd.getId())==0 && !nodeIsActive)
                    {
                        nodeIsActive = true;
                        tmpActiveNode =  tmpSmNd;
                        break;
                    }
                }
            }

            StringBuilder resStrBld = new StringBuilder();
            for(String itPartSrvId: partServIdToCapabilitiesCSV_HM.keySet()){
                for(String itCapCsv: partServIdToCapabilitiesCSV_HM.get(itPartSrvId)) {

                    resStrBld.append(itPartSrvId);
                    resStrBld.append(",");
                    resStrBld.append(itCapCsv);
                    resStrBld.append(",");

                    String[] capCsvTokens = itCapCsv.split(",");
                    String capName = capCsvTokens[1];
                    resStrBld.append(nodeIsActive? "1": "0");
                    resStrBld.append(",");
                    boolean nodeSupportsCap = false;
                    // Check for each capability if the node actually (still or ever) supports it
                    if(tmpActiveNode!=null && tmpActiveNode.getCapabilitiesVector()!=null && tmpActiveNode.getCapabilitiesVector().size()>0){
                        Iterator<SensorModel> capsIt = tmpActiveNode.getCapabilitiesVector().iterator();
                        SensorModel currentCap;
                        while(capsIt.hasNext() &&!nodeSupportsCap) {
                            currentCap = capsIt.next();
                            if(currentCap.getName().compareToIgnoreCase(capName) ==0 )
                            {
                                nodeSupportsCap=true;
                                break;
                            }
                        }
                    }
                    resStrBld.append(nodeSupportsCap? "1": "0");
                    resStrBld.append(",");
                    // check if the capability even exists anywhere supported in the DVNS
                    if(ssUN != null && ssUN.getCapabilitiesTable().containsKey(capName)) {
                        resStrBld.append("1");
                        resStrBld.append(NEW_LINE_STR);
                    }
                    else {
                        resStrBld.append("0");
                        resStrBld.append(NEW_LINE_STR);
                    }
                }
            }
            responseResult = resStrBld.toString();

        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getDataCapabilityForComposedService")&&
                pCapabilityID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty() &&
                pServiceID!=null ) {
            // parameters: (capabilityID,gatewayID,sensorID,serviceID))
            // return:
            // "date,value\n"
            //
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            // since we don't define which function we should just return the LAST VALUE function (which is included always)
            //
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pCapabilityID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pServiceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            int iServiceID = -1;
            try {
                iServiceID = Integer.parseInt(pServiceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id composed serv id: " + pServiceID);
            }
            int iCapID = -1;
            try {
                iCapID = Integer.parseInt(pCapabilityID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id capability id: " + pCapabilityID);
            }

            int thePartialServId = -1;
            boolean foundThePartialServiceForThisCapId = false;
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {

                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        String currPartServiceId = Integer.toString(partialServiceTmpIter.getId());
                        ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(partialServiceTmpIter.getId());
                        //
                        logger.debug("getSensorCapabilityListForComposedService - Get OBSERVED CAPS!");
                        List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                        try{
                            storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                        }catch(Exception ex002)
                        {
                            storedCapsAndRulesList =null;
                        }
                        if(storedCapsAndRulesList != null && !foundThePartialServiceForThisCapId)
                        {
                            AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                            // for each capability in the partial Service
                            for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                            {
                                Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                                //
                                //
                                //
                                if(storedCapAndRulesIterTmp.getId() == iCapID) {
                                    foundThePartialServiceForThisCapId = true;
                                    thePartialServId = partialServiceTmpIter.getId();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            StringBuilder resStrBld = new StringBuilder();
            if(foundThePartialServiceForThisCapId) {
                AbstractObservationManager  abstractObservationManager = AbstractObservationManager.getInstance();
                // ALSO for uberdust this would return duplicates, since uberdust doesn't update very often (not the same frequency)
                //List<Observation> allObsList = abstractObservationManager.getObservationList();
                List<Observation> allObsList = abstractObservationManager.getObservationListForFilters(thePartialServId, iCapID, pGatewayID, pSensorID);
                for (Observation obsTmpIter : allObsList)
                {
                    //Observation obsTmp = abstractObservationManager.getObservation(obsTmpIter.getId());
//                    if(obsTmpIter.getPartialServiceID() == thePartialServId  &&
//                            obsTmpIter.getCapabilityID()==iCapID &&
//                            obsTmpIter.getGatewayRegName().compareToIgnoreCase(pGatewayID) == 0 &&
//                            ( ( obsTmpIter.isGatewayLevel() && obsTmpIter.getSensorName().compareToIgnoreCase(ResultAggrStruct.MidSpecialForAggregateMultipleValues) == 0 ) || ( obsTmpIter.getSensorName().compareToIgnoreCase(pSensorID) ==0 ) ) &&
//                            obsTmpIter.getAggreagatedSensorsNum() > 0 )
//                    {
                    if(obsTmpIter.getRefFunctNameEssential()!= null && obsTmpIter.getRefFunctNameEssential().compareToIgnoreCase(ReqFunctionOverData.lastValFunc) == 0) {
                        Date tsFromDB = obsTmpIter.getTimestamp();
                        String tsFromDBStr = "";
                        if(tsFromDB!=null) {
                            DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            // Using DateFormat format method we can create a string
                            // representation of a date with the defined format.
                            tsFromDBStr= df.format(tsFromDB);
                        }
                        resStrBld.append(tsFromDBStr);
                        resStrBld.append(",");
                        resStrBld.append(obsTmpIter.getValue());
                        resStrBld.append(NEW_LINE_STR);
                   // }
                    }
                }
            }
            responseResult = resStrBld.toString();

        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getDataCapabilityForPartialService")&&
                pCapabilityID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty() &&
                pInstanceID!=null ) {
            // parameters: (capabilityID,gatewayID,sensorID,instanceID)
            // return:
            // "date,value\n"
            //
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pCapabilityID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pInstanceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            int iInstanceID = -1;
            try {
                iInstanceID = Integer.parseInt(pInstanceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id partial serv id: " + pInstanceID);
            }
            int iCapID = -1;
            try {
                iCapID = Integer.parseInt(pCapabilityID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id capability id: " + pCapabilityID);
            }

            StringBuilder resStrBld = new StringBuilder();

            AbstractObservationManager  abstractObservationManager = AbstractObservationManager.getInstance();
            //List<Observation> allObsList = abstractObservationManager.getObservationList();
            List<Observation> allObsList = abstractObservationManager.getObservationListForFilters(iInstanceID, iCapID, pGatewayID, pSensorID);
            for (Observation obsTmpIter : allObsList)
            {
                //Observation obsTmp = abstractObservationManager.getObservation(obsTmpIter.getId());
//                if(obsTmpIter.getPartialServiceID() == iInstanceID  &&
//                        obsTmpIter.getCapabilityID()==iCapID &&
//                        obsTmpIter.getGatewayRegName().compareToIgnoreCase(pGatewayID) == 0 &&
//                        ( ( obsTmpIter.isGatewayLevel() && obsTmpIter.getSensorName().compareToIgnoreCase(ResultAggrStruct.MidSpecialForAggregateMultipleValues) == 0 ) || ( obsTmpIter.getSensorName().compareToIgnoreCase(pSensorID) ==0 ) ) &&
//                        obsTmpIter.getAggreagatedSensorsNum() > 0 )
//                {
                    Date tsFromDB = obsTmpIter.getTimestamp();
                    String tsFromDBStr = "";
                    if(tsFromDB!=null) {
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        // Using DateFormat format method we can create a string
                        // representation of a date with the defined format.
                        tsFromDBStr= df.format(tsFromDB);
                    }
                    resStrBld.append(tsFromDBStr);
                    resStrBld.append(",");
                    resStrBld.append(obsTmpIter.getValue());
                    resStrBld.append(NEW_LINE_STR);
                //}
            }
            responseResult = resStrBld.toString();

        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getMostRecentDataCapabilityForComposedService")&&
                pCapabilityID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty() &&
                pServiceID!=null) {
            // parameters: (capabilityID,gatewayID,sensorID,serviceID)
            responseResult="";
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pCapabilityID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pServiceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            int iServiceID = -1;
            try {
                iServiceID = Integer.parseInt(pServiceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id composed serv id: " + pServiceID);
            }
            int iCapID = -1;
            try {
                iCapID = Integer.parseInt(pCapabilityID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id capability id: " + pCapabilityID);
            }


            int thePartialServId = -1;
            boolean foundThePartialServiceForThisCapId = false;
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {

                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        String currPartServiceId = Integer.toString(partialServiceTmpIter.getId());
                        ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(partialServiceTmpIter.getId());
                        //
                        logger.debug("getSensorCapabilityListForComposedService - Get OBSERVED CAPS!");
                        List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                        try{
                            storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                        }catch(Exception ex002)
                        {
                            storedCapsAndRulesList =null;
                        }
                        if(storedCapsAndRulesList != null && !foundThePartialServiceForThisCapId)
                        {
                            AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                            // for each capability in the partial Service
                            for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                            {
                                Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                                //
                                //
                                //
                                if(storedCapAndRulesIterTmp.getId() == iCapID) {
                                    foundThePartialServiceForThisCapId = true;
                                    thePartialServId = partialServiceTmpIter.getId();
                                    break;
                                }
                            }
                        }
                    }
                }
            }



            StringBuilder resStrBld = new StringBuilder();
            if(foundThePartialServiceForThisCapId) {
                AbstractObservationManager  abstractObservationManager = AbstractObservationManager.getInstance();
                Observation lastObs = abstractObservationManager.getLastObservationForFilters(thePartialServId, iCapID, pGatewayID, pSensorID);
                if (lastObs != null)
                {
                    Date tsFromDB = lastObs.getTimestamp();
                    String tsFromDBStr = "";
                    if(tsFromDB!=null) {
                        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        // Using DateFormat format method we can create a string
                        // representation of a date with the defined format.
                        tsFromDBStr= df.format(tsFromDB);
                    }
                    resStrBld.append(tsFromDBStr);
                    resStrBld.append(",");
                    resStrBld.append(lastObs.getValue());
                    resStrBld.append(NEW_LINE_STR);
                }
            }

            responseResult= resStrBld.toString();

        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getMostRecentDataCapabilityForPartialService")&&
                pCapabilityID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty() &&
                pInstanceID!=null ) {
            // parameters: (capabilityID,gatewayID,sensorID,instanceID)
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pCapabilityID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pInstanceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            responseResult="";
            int iInstanceID = -1;
            try {
                iInstanceID = Integer.parseInt(pInstanceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id partial serv id: " + pInstanceID);
            }
            int iCapID = -1;
            try {
                iCapID = Integer.parseInt(pCapabilityID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id capability id: " + pCapabilityID);
            }

            StringBuilder resStrBld = new StringBuilder();

            AbstractObservationManager  abstractObservationManager = AbstractObservationManager.getInstance();
            Observation lastObs = abstractObservationManager.getLastObservationForFilters(iInstanceID, iCapID, pGatewayID, pSensorID);
            if (lastObs != null)
            {
                Date tsFromDB = lastObs.getTimestamp();
                String tsFromDBStr = "";
                if(tsFromDB!=null) {
                    DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    // Using DateFormat format method we can create a string
                    // representation of a date with the defined format.
                    tsFromDBStr= df.format(tsFromDB);
                }
                resStrBld.append(tsFromDBStr);
                resStrBld.append(",");
                resStrBld.append(lastObs.getValue());
                resStrBld.append(NEW_LINE_STR);
            }
            responseResult = resStrBld.toString();


        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getComposedServiceCapabilityList")&&
                pServiceID!=null)
        {
            // parameters: (serviceID)
            // return all the Capabilities *REQUESTED* for the composed service.
            // TODO: for each capability it could also return the list of sensor it was requested to be applied on (later)
            // flags are 0 for false, and 1 for true
            // "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,cap_supported_flag\n
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            HashMap<String, Vector<String>>  partServIdToCapabilitiesCSV_HM = new HashMap<String,Vector<String>>();
            int iServiceID = -1;
            try {
                iServiceID = Integer.parseInt(pServiceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id composed serv id: " + pServiceID);
            }

            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {
                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        String currPartServiceId = Integer.toString(partialServiceTmpIter.getId());
                        ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(partialServiceTmpIter.getId());
                        //
                        logger.debug("getComposedServiceCapabilityList - Get OBSERVED CAPS!");
                        List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                        try{
                            storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                        }catch(Exception ex002)
                        {
                            storedCapsAndRulesList =null;
                        }
                        if(storedCapsAndRulesList != null)
                        {
                            AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                            // for each capability in the partial Service
                            for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                            {
                                Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                                String currCapName = storedCapAndRules.getName();
                                if(!partServIdToCapabilitiesCSV_HM.containsKey(currPartServiceId)){
                                    partServIdToCapabilitiesCSV_HM.put(currPartServiceId, new Vector<String>());
                                }
                                Vector<String> capsCsvVec = partServIdToCapabilitiesCSV_HM.get(currPartServiceId);
                                StringBuilder capsCsvBld = new StringBuilder();
                                capsCsvBld.append(storedCapAndRules.getId());
                                capsCsvBld.append(",");
                                capsCsvBld.append(storedCapAndRules.getName());
                                capsCsvBld.append(",");
                                capsCsvBld.append(storedCapAndRules.getFunction());
                                capsCsvBld.append(",");
                                capsCsvBld.append(storedCapAndRules.getHasTrigger().compareToIgnoreCase("yes")==0 ? 1:0);
                                capsCsvBld.append(",");
                                capsCsvBld.append(storedCapAndRules.getTriggerConditionSign());
                                capsCsvBld.append(",");
                                capsCsvBld.append(storedCapAndRules.getTriggerConditionValue());
                                capsCsvVec.addElement(capsCsvBld.toString());
                            }
                        }
                    }
                }
            }

            StringBuilder resStrBld = new StringBuilder();
            for(String itPartSrvId: partServIdToCapabilitiesCSV_HM.keySet()){
                for(String itCapCsv: partServIdToCapabilitiesCSV_HM.get(itPartSrvId)) {

                    resStrBld.append(itPartSrvId);
                    resStrBld.append(",");
                    resStrBld.append(itCapCsv);
                    resStrBld.append(",");

                    String[] capCsvTokens = itCapCsv.split(",");
                    String capName = capCsvTokens[1];
                                        if(ssUN != null && ssUN.getCapabilitiesTable().containsKey(capName)) {
                        resStrBld.append("1");
                        resStrBld.append(NEW_LINE_STR);
                    }
                    else {
                        resStrBld.append("0");
                        resStrBld.append(NEW_LINE_STR);
                    }
                }
            }
            responseResult = resStrBld.toString();

        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getPartialServiceCapabilityList")&&
                pInstanceID!=null)
        {
            // parameters: (instanceID)
            // return all the Capabilities *REQUESTED* for the specific partial service.
            // TODO: for each capability it could also return the list of sensor it was requested to be applied on (later)
            //partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,cap_supported_flag\n
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pInstanceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            HashMap<String, Vector<String>>  partServIdToCapabilitiesCSV_HM = new HashMap<String,Vector<String>>();
            int iInstanceID = -1;
            try {
                iInstanceID = Integer.parseInt(pInstanceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id partial serv id: " + pInstanceID);
            }

            AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
            String currPartServiceId = Integer.toString(iInstanceID);
            ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(iInstanceID);
            //
            logger.debug("getComposedServiceCapabilityList - Get OBSERVED CAPS!");
            List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
            try{
                storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
            }catch(Exception ex002)
            {
                storedCapsAndRulesList =null;
            }
            if(storedCapsAndRulesList != null)
            {
                AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();
                // for each capability in the partial Service
                for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                {
                    Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                    String currCapName = storedCapAndRules.getName();
                    if(!partServIdToCapabilitiesCSV_HM.containsKey(currPartServiceId)){
                        partServIdToCapabilitiesCSV_HM.put(currPartServiceId, new Vector<String>());
                    }
                    Vector<String> capsCsvVec = partServIdToCapabilitiesCSV_HM.get(currPartServiceId);
                    StringBuilder capsCsvBld = new StringBuilder();
                    capsCsvBld.append(currPartServiceId);
                    capsCsvBld.append(",");
                    capsCsvBld.append(storedCapAndRules.getId());
                    capsCsvBld.append(",");
                    capsCsvBld.append(storedCapAndRules.getName());
                    capsCsvBld.append(",");
                    capsCsvBld.append(storedCapAndRules.getFunction());
                    capsCsvBld.append(",");
                    capsCsvBld.append(storedCapAndRules.getHasTrigger().compareToIgnoreCase("yes")==0 ? 1:0);
                    capsCsvBld.append(",");
                    capsCsvBld.append(storedCapAndRules.getTriggerConditionSign());
                    capsCsvBld.append(",");
                    capsCsvBld.append(storedCapAndRules.getTriggerConditionValue());
                    capsCsvVec.addElement(capsCsvBld.toString());
                }
            }


            StringBuilder resStrBld = new StringBuilder();
            for(String itPartSrvId: partServIdToCapabilitiesCSV_HM.keySet()){
                for(String itCapCsv: partServIdToCapabilitiesCSV_HM.get(itPartSrvId)) {
                    //resStrBld.append(itPartSrvId);
                    //resStrBld.append(",");
                    resStrBld.append(itCapCsv);
                    resStrBld.append(",");

                    String[] capCsvTokens = itCapCsv.split(",");
                    String capName = capCsvTokens[2];
                    if(ssUN != null && ssUN.getCapabilitiesTable().containsKey(capName)) {
                        resStrBld.append("1");
                        resStrBld.append(NEW_LINE_STR);
                    }
                    else {
                        resStrBld.append("0");
                        resStrBld.append(NEW_LINE_STR);
                    }
                }
            }
            responseResult = resStrBld.toString();
        }
        // -----------------------------------------------------------------------------------------------------------------------------------------------
        else if(functionSignature.equalsIgnoreCase("getPartialServicesForComposedServiceID")&&
                pServiceID!=null)
        {
            // ---------------------------------
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValuesStr=reqParamValsStrBld.toString();

            StringBuilder resStrBld = new StringBuilder();
            int iServiceID = -1;
            try {
                iServiceID = Integer.parseInt(pServiceID);
            } catch(NumberFormatException exnmr) {
                logger.error("Could not convert to id composed serv id: " + pServiceID);
            }
            // (serviceID)
            // return CSV of all the ids of partial services
            // "partialServiceId1,..., partialServiceId2"
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        resStrBld.append(partialServiceTmpIter.getId());
                        resStrBld.append(",");
                    }
                }
            }
            responseResult = resStrBld.toString();
            responseResult = responseResult.replaceAll("\\s*,\\s*$", "");
        }
        else {
            errno = 1;
            xmerrordescr="Unsupported Interface Function Requested!";
        }
    }
    else {
        errno = 1;
        xmerrordescr="No Interface Function Requested!";
    }


     responseResult = responseResult.replaceAll(Pattern.quote(NEW_LINE_STR)+"$" , "");
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <reply funct="<%=functionSignature %>" value="<%=responseResult %>" parNames="<%=reqParamNamesStr %>" parValues="<%=reqParamValuesStr %>" qstr="<%=reqData %>" ></reply>
</Answer>
