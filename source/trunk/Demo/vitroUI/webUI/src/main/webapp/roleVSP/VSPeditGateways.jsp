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

<!DOCTYPE html>
<%@page session='false' contentType='text/html' import="java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*,  vitro.vspEngine.service.query.*"%>
 <%@ page import="org.apache.log4j.Logger" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="vitro.vspEngine.service.persistence.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="vitro.vspEngine.logic.model.Capability" %>
<%   String defaultCapability = Capability.PHENOMENOM_TEMPERATURE;
    String staticprefixCapability = Capability.dcaPrefix; %>
<html>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Manage WSIs of Enabler (WSIE view)</title>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>VITRO Gateways management (VSP view)</title>
 	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#WSIE').addClass("active");
 	});     
    </script>
    <script type="text/javascript"
            src="http://maps.googleapis.com/maps/api/js?key=AIzaSyCEgVsh2dojyU0qWl5l2yyYIgM4uy-FqyA&sensor=false">
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/vspEditGatewaysTasksJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/capabilityIconsJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/googleMapsMarkerSelectionJS.jsp"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/dbVGWInfoRetrieveInterfaceJS.jsp"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>

    <script type="text/javascript">
        var capabilityPrefix = '<%=staticprefixCapability %>';

        function getCapabilityFriendlyName(pFullDCAName) {
        <% String staticprefixCapabilityloc =Capability.dcaPrefix;%>
            var capabilityPrefix = '<%=staticprefixCapabilityloc %>';

            var simpCap = pFullDCAName.replace(/^<%=staticprefixCapabilityloc %>/, '');
        <%

            Set<String> possibleSimpleCapNames = Capability.validSimpleCapNames();
            Iterator<String> itSimpCap = possibleSimpleCapNames.iterator();
            int countofIfs = 0;
            while(itSimpCap.hasNext())
            {
                String tmpSimpCap = itSimpCap.next();
                if (countofIfs ==0) {
                    countofIfs++;

                %>
            if(simpCap == '<%=tmpSimpCap %>')
            {
                return '<%=Capability.getFriendlyUIName(tmpSimpCap)%>';
            }
                    <% } else { %>
            else if(simpCap == '<%=tmpSimpCap %>')
            {
                return '<%=Capability.getFriendlyUIName(tmpSimpCap)%>';
            }
                    <%}

                    }      //end of while loop
                    %>
            else {
                return   simpCap;
            }
        }
        //var update_timeout_for_Dbl_Vs_SingleClick = null;

        //var geocoder = new google.maps.Geocoder();
        var globalCapabilitytoViewInGMs = 'allCaps';
        MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;
        var update_timeout_for_Dbl_Vs_SingleClick = null;
        var infoWindow = new google.maps.InfoWindow;

        var AllMapItems = {
            map: null,
            markers: [],
            markerNodeIds: [],
            markerNodeEnabledStatus: [],
            markerNodeStatusSynched: [],
            markerGWOfNodesIds: [],
            markersCaps: [],
            markersDescription: [],
            markersAddress: [],
            polyline: null,
            polygon: null

        };

        function mapCleanup(hideMapDivFlag){
            //alert("Calling Map Cleanup!");
            //console.log("Calling Map Cleanup!");
            if(AllMapItems.map!=null) {
                MarkerSelection.Clean(AllMapItems.map);
                AllMapItems.deleteAllOverlays();
            }
            if(document.getElementById('gwInfoNameAHRefId')){
                document.getElementById('gwInfoNameAHRefId').innerHTML = '';
            }
            removeAllCapabilitiesFromRadioButtonList();
            setCheckedValue(document.getElementById('allCapsRB'), 'allCaps');

            removeAllHTMLRowsOfEquivLists();
            if(typeof hideMapDivFlag === "undefined" ) {
             //assume hideMapDivFlag
                 showMapDiv(false);
            } else {
                if(hideMapDivFlag == true) {
                    showMapDiv(false);
                } else  {
                    showMapDiv(true);
                }
            }

        }
        // hiding and re-displaying the google maps div could have some side-effects)
        // so we use the resize trigger of the map to restore its original size (a display: none obj was no size)
        function showMapDiv(flag) {
            if(document.getElementById('gmMapRowDiv1') && document.getElementById('gmMapRowDiv2') ) {
                if(typeof flag === "undefined" ) {
                    //assume show
                    {
                        $('#gmMapRowDiv1').css('display','block');
                        $('#gmMapRowDiv2').css('display','block');
                        $('#gmMapRowDiv3').css('display','block');
                        if(AllMapItems.map != null) {
                            google.maps.event.trigger(AllMapItems.map, 'resize');
                        }
                    }
                } else
                {
                    if(flag == true) {
                        $('#gmMapRowDiv1').css('display','block');
                        $('#gmMapRowDiv2').css('display','block');
                        $('#gmMapRowDiv3').css('display','block');
                        if(AllMapItems.map != null) {
                            google.maps.event.trigger(AllMapItems.map, 'resize');
                        }
                    } else
                    {
                        $('#gmMapRowDiv1').css('display','none');
                        $('#gmMapRowDiv2').css('display','none');
                        $('#gmMapRowDiv3').css('display','none');
                    }
                }
            }
        }

        var onMarkerClick = function(idxMarker) {
            var curr_marker = AllMapItems.markers[idxMarker]; // this;
            var latLng = curr_marker.getPosition();
            var foundMatch = false;
            for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
                if (curr_marker == itMarker) {
                    var contentToSet = createContentInfoHeader(AllMapItems.markerNodeIds[n]) + AllMapItems.markersDescription[n];

                    contentToSet += createContentInfoFooter();
                    infoWindow.setContent(contentToSet);
                    infoWindow.open(AllMapItems.map, curr_marker);
                    // todo: we could add a check here (+ delay) to see if a node is currently enabled or disabled (similar to the check for geolocation that was removed)
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                infoWindow.setContent('No valid description found!');
                infoWindow.open(AllMapItems.map, curr_marker);
            }

        }

        var onMarkerDblClick = function(idxMarker) {
            //alert('Double click!' + AllMapItems.markers[idxMarker].getTitle()) ;
            var curr_marker = AllMapItems.markers[idxMarker];
            infoWindow.close();
            MarkerSelection.Clean(AllMapItems.map);
            //change color to selected color AND clear selection list AND clear selection area AND put only this one to selection list
            // for the moment single click selects only one node (no multiple selections) so, clear the previous one, which can be kept globally.
            // new: purge singleMarkersSelected table and push only this marker
            MarkerSelection.singleMarkersSelected.push(curr_marker);
            drawSelectedIcon(curr_marker, MarkerSelection.selectedCapability);
            AllMapItems.map.setZoom(19);
            AllMapItems.map.panTo(curr_marker.position);
            if(MarkerSelection.singleMarkersSelectedSpan!=null){
                MarkerSelection.singleMarkersSelectedSpan.innerHTML = curr_marker.getTitle() + ':' + MarkerSelection.selectedCapability;
            }
            //return false; //will this prevent the simple click event to be fired/serviced?
//    mapSelectionChanged();
        }

        //new:
        var onMarkerRightClick = function(event) {

            var curr_marker = this;

            // push only if not yet selected
            // if selected... DESELECT IT. Even in Map Selection Mode ? Does this work with zooom and redraw?
            var alreadySelected = false;
            infoWindow.close();
            for (var i = 0; !alreadySelected && i < MarkerSelection.singleMarkersSelected.length; ++i) {
                if(curr_marker == MarkerSelection.singleMarkersSelected[i])
                {
                    alreadySelected = true;
                    MarkerSelection.trimSingleMarkerSelected(i);

                }
            }
            for(var i =0; !alreadySelected && i < MarkerSelection.selectedMarkers.length; ++i) {
                if(curr_marker == MarkerSelection.selectedMarkers[i])
                {
                    alreadySelected = true;
                    MarkerSelection.trimRegionMarkerSelected(i);
                }
            }
            if(alreadySelected )
            {
                MarkerSelection.updateSelectionInSpan();
                return;
            }
            MarkerSelection.singleMarkersSelected.push(curr_marker);
            drawSelectedIcon(curr_marker, MarkerSelection.selectedCapability);
            MarkerSelection.updateSelectionInSpan();
//    mapSelectionChanged();
        }

        // new:
        function anyMapSelectionExists() {
            retVal = false;
            if ( ( MarkerSelection.singleMarkersSelected && MarkerSelection.singleMarkersSelected.length > 0)|| (MarkerSelection.selectedMarkers && MarkerSelection.selectedMarkers.length > 0)) {
                retVal = true;
            }
            return retVal;
        }


        function createContentInfoHeader(nodeId) {
            return '<div id="content">' +
                    '<div id="siteNotice">' +
                    '</div>' +
                    '<h1 id="firstHeading" class="firstHeading">Info for Node</h1>' +
                    '<div id="bodyContent">';
        }

        function createContentInfoFooter() {
            return '</div>' +
                    '</div>';
        }

        // puts the marker but keeps it hidden
        function putMarkerWithInfo(myLatlng, contentString, nodeId, gateId, nodeEnabledStatus, nodeStatusSynched, capsarray) {
            // Add markers to the map

            // Marker sizes are expressed as a Size of X,Y
            // where the origin of the image (0,0) is located
            // in the top left of the image.

            // Origins, anchor positions and coordinates of the marker
            // increase in the X direction to the right and in
            // the Y direction down.
            var image = new google.maps.MarkerImage(mapCapToIcon32(''),
                    // This marker is 20 pixels wide by 32 pixels tall.
                    new google.maps.Size(32, 32),
                    // The origin for this image is 0,0.
                    new google.maps.Point(0, 0),
                    // The anchor for this image is the base of the flagpole at 0,32.
                    new google.maps.Point(0, 32));
//            // Shapes define the clickable region of the icon.
//            // The type defines an HTML &lt;area&gt; element 'poly' which
//            // traces out a polygon as a series of X,Y points. The final
//            // coordinate closes the poly by connecting to the first
//            // coordinate.
            var shape = {
                coord: [0, 0, 0, 32, 32, 32, 32 , 0],
                type: 'poly'
            };

            var marker;
            marker = new google.maps.Marker({
                position:myLatlng,
                map: null,
                icon: image,
                shape: shape,
                title:nodeId
            });
            var idxMarker = AllMapItems.markers.push(marker) -1;
            AllMapItems.markerNodeIds.push(nodeId);
            AllMapItems.markerNodeEnabledStatus.push(nodeEnabledStatus);
            AllMapItems.markerNodeStatusSynched.push(nodeStatusSynched);
            AllMapItems.markerGWOfNodesIds.push(gateId);
            AllMapItems.markersCaps.push(capsarray);
            AllMapItems.markersDescription.push(contentString);
            //AllMapItems.markersAddress.push("undefined");
            // var point = new google.maps.LatLng(southWest.lat() + latSpan * Math.random(), southWest.lng() + lngSpan * Math.random());
            // for selection with polygons (poin needs to be LatLng point   TODO: should be merged with marker!
            //var idxMarker = MarkerSelection.allnodeMarkers.push(marker) - 1;

            google.maps.event.addListener(marker, 'click', function() {
                update_timeout_for_Dbl_Vs_SingleClick = setTimeout(function() {
                    onMarkerClick(idxMarker);
                }, 200);
            });

            google.maps.event.addListener(marker, 'dblclick', function(event) {
                clearTimeout(update_timeout_for_Dbl_Vs_SingleClick);
                onMarkerDblClick(idxMarker);
            });

            google.maps.event.addListener(marker, 'rightclick', onMarkerRightClick);
        }

        function mapClick(event) {
            infoWindow.close();
            MarkerSelection.points.push(event.latLng);
        //    MarkerSelection.ShowHideONOFF = 0;
            MarkerSelection.Display(AllMapItems.map)
        //    mapSelectionChanged();
        }

        //Should be called when the map selection of nodes is changed (to trigger changes like populating the dropdowns for node selection and capabilities)
        function getSelectionInHiddenField(showMode) {
            if(MarkerSelection.SelectionTotalLength() > 0)

            {
                //clean dropdown for nodes (?) To be used only if we want to show the selection of nodes (one by one)
                //todo:? clean dropdown for node capabilities and fill it in with filtered capabiities of node selection (It would be awkward for the UI since it would reset the capabilities dropdown)
                var hdActiveMapSelection = document.getElementById('activeMapSelectionHD');
                var csvNodesList = '';
                if(hdActiveMapSelection!= null )
                {
                    hdActiveMapSelection.value = '';
                    var currMarker = null;
                    for (var i = 0;MarkerSelection.singleMarkersSelected && i < MarkerSelection.singleMarkersSelected.length; ++i) {
                        currMarker = MarkerSelection.singleMarkersSelected[i];
                        for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
                            if (currMarker == itMarker) {
                                csvNodesList += AllMapItems.markerGWOfNodesIds[n]+'::##::'+AllMapItems.markerNodeIds[n] + ',';
                                break;
                            }
                        }
                    }
                    for (var i = 0; MarkerSelection.selectedMarkers && i < MarkerSelection.selectedMarkers.length; ++i) {
                        currMarker = MarkerSelection.selectedMarkers[i];
                        for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
                            if (currMarker == itMarker) {
                                csvNodesList += AllMapItems.markerGWOfNodesIds[n]+'::##::'+AllMapItems.markerNodeIds[n] + ',';
                                break;
                            }
                        }
                    }
                    hdActiveMapSelection.value = csvNodesList.replace(/(,\s*$)/g, '');
                    if(showMode!=null && showMode=='alert')
                    {
                        alert(hdActiveMapSelection.value);
                    }
                }
            } else {
                var hdActiveMapSelection = document.getElementById('activeMapSelectionHD');
                if(hdActiveMapSelection!= null )
                {
                    hdActiveMapSelection.value = '';
                }
            }
        }
        //
        //
        //
        function addNewEquivListing() {
            // 2. add a row to the equiv listings table
            var equivListsTable=document.getElementById("equivListsTable");
            var rowCount = equivListsTable.rows.length;

            var nextIdx = parseInt(document.getElementById("autoIncForEquivListRowIds").value)  + 1;

            // -------| node selections
            var nodeSelectDisp = "";
            var nodeSelectVal = "";
            var uniqSelHDId = '';
            if( document.getElementById("hdIdOfSelectedGw") && document.getElementById('activeMapSelectionHD') && document.getElementById('activeMapSelectionHD').value!= '' ) {
                nodeSelectVal = document.getElementById('activeMapSelectionHD').value;

                var pGwid = document.getElementById("hdIdOfSelectedGw").value;
                // ++++
                var selectedItemArr = nodeSelectVal.toString().split(',');
                if(selectedItemArr == null || selectedItemArr.length < 2) {
                    alert('Please add more than 2 nodes in the equivalency list!');
                } else {
                    actUponEquivSetsListForVGW(pGwid, nodeSelectVal,'','add',afterAddingNewEquivListReturns);
                }
             }
            else  {
                //todo: informative alert
                alert('No node selection was made on the map');
                // DEBUG TO BE REMOVED -- IT WORKS
                // removeAllHTMLRowsOfEquivLists();
                return;
            }
        }

        //
        // TODO: this could require AJAX!
        function showEquivListingNodeSelectionDetails(hdSelectionId)
        {
            alert(document.getElementById(hdSelectionId).value.split(/[,]+/).join('\n'));
//	 $('#alert_placeholder').html('<div class="alert span3"><a class="close" data-dismiss="alert">?</a><span>'+document.getElementById(hdSelectionId).value.split(/[,]+/).join('<br />')+'</span></div>')

        }

        //
        // TODO: removeEquivListing() . Also would require some AJAX (and delete all rows and reinsert them updated!
        function removeEquivListing(pGwid, equivListingId) {
            var equivListsTable=document.getElementById("equivListsTable");
            if(equivListsTable!=null)
            {
                if(equivListingId!=null && equivListingId!= "") {
                    dbListingIdArr = equivListingId.toString().split('_');
                    if(dbListingIdArr!=null && dbListingIdArr.length== 2 && dbListingIdArr[0]!="" ) {
                        dbListingId = dbListingIdArr[1];
                        actUponEquivSetsListForVGW(pGwid,'',dbListingId,'delete',afterDeletingEquivListReturns);
                    }
                    else {
                        alert("An invalid List selected for deletion. Cannot proceed with deletion!");
                    }
                }
                //deleteRowByHiddenId('equivListsTable', equivListingId);
            }
        }

        // logic for removing all HTML rows for equiv lists from table (before getting the new ones with Ajax)
        function removeAllHTMLRowsOfEquivLists()
        {
            var equivListsTable=document.getElementById("equivListsTable");
            if(equivListsTable!=null)
            {
                var rowIndex = 0;

                try {
                    while(equivListsTable.rows.length>2)
                    {
                        rowIndex = equivListsTable.rows.length - 1;
                        equivListsTable.deleteRow(rowIndex);
                    }
                }
                catch(e)
                {
                    alert(e);
                }
            }
        }

        function mapInitialize() {
            var myOptions = {
                // TODO To be removed before final
                center: new google.maps.LatLng(44.8000, 10.3333),
                zoom: 5,
                /*mapTypeId: google.maps.MapTypeId.ROADMAP*/
                mapTypeId: google.maps.MapTypeId.SATELLITE
            };
            var map = new google.maps.Map(document.getElementById("map_canvas"),
                    myOptions);
            AllMapItems.map = map;
            MarkerSelection.allnodeMarkers = AllMapItems.markers;
            MarkerSelection.allnodeEnabledStatus = AllMapItems.markerNodeEnabledStatus;
            MarkerSelection.allnodeSynchedStatus = AllMapItems.markerNodeStatusSynched;
            MarkerSelection.Clean(AllMapItems.map);


            google.maps.event.addListener(map, 'click', mapClick);
        }

    </script>
    <script type="text/javascript">

        //
        // logic for removing all Capabilities from Radio Button List
        function removeAllCapabilitiesFromRadioButtonList()
        {
            var rdbCapsTable=document.getElementById("rdbCapsTable");
            if(rdbCapsTable!=null)
            {
                var rowIndex = 0;

                try {
                    while(rdbCapsTable.rows.length>1)
                    {
                        rowIndex = rdbCapsTable.rows.length - 1;
                        rdbCapsTable.deleteRow(rowIndex);
                    }
                }
                catch(e)
                {
                    alert(e);
                }
            }
        }
        //
        // Removes a row from the Capabilities Radio button table
        //
        function removeCapRadioButton(capRadioId)
        {
            var rdbCapsTable=document.getElementById("rdbCapsTable");
            if(rdbCapsTable!=null)
            {
                deleteRowByHiddenId('rdbCapsTable', capRadioId);
            }
        }

        // aux function
        //aux function
        function deleteRowByHiddenId(tableID, rowHiddenId)  {
            try {
                var hiddenIdObj = document.getElementById(rowHiddenId);
                var rowIndex = hiddenIdObj.parentNode.parentNode.rowIndex;
                var table = document.getElementById(tableID);
                table.deleteRow(rowIndex);
            }
            catch(e)
            {
                alert(e);
            }
        }

        function deleteRow(tableID, rowId) {
            try {
                var table = document.getElementById(tableID);
                var rowCount = table.rows.length;

                for(var i=0; i< rowCount; i++) {
                    var row = table.rows[i];
                    if(row.id == rowId) {
                        table.deleteRow(i);
                        rowCount--;
                        i--;
                    }
                }
            }
            catch(e)
            {
                alert(e);
            }
        }

        // set the radio button with the given value as being checked
        // do nothing if there are no radio buttons
        // if the given value does not exist, all the radio buttons
        // are reset to unchecked
        function setCheckedValue(radioObj, newValue) {
            if(!radioObj)
                return;
            var radioLength = radioObj.length;
            if(radioLength == undefined) {
                radioObj.checked = (radioObj.value == newValue.toString());
                return;
            }
            for(var i = 0; i < radioLength; i++) {
                radioObj[i].checked = false;
                if(radioObj[i].value == newValue.toString()) {
                    radioObj[i].checked = true;
                }
            }
        }

        function getCheckedValue(radioObj) {
            if(!radioObj)
                return null;
            var radioLength = radioObj.length;
            if(radioLength == undefined) {
                if (radioObj.checked == true){
                    return radioObj.value
                } else {
                    return null;
                }
            }
            for(var i = 0; i < radioLength; i++) {
                if(radioObj[i].checked  == true) {
                    return radioObj[i].value;
                }
            }
        }
        // end of aux functions

        /**
         * Shows or hides all marker overlays on the map.
         */
        AllMapItems.toggleMarkers = function(opt_enable, capabilityName) {
            if (typeof opt_enable == 'undefined') {
                opt_enable = !AllMapItems.markers[0].getMap();
            }
            if (typeof capabilityName == 'undefined') {
                for (var n = 0, marker; marker = AllMapItems.markers[n]; n++) {
                    marker.setMap(opt_enable ? AllMapItems.map : null);
                }
            }
            else {
                for (var n = 0, marker; marker = AllMapItems.markers[n]; n++) {
                    for (var itarr = 0, tmpCap; tmpCap = AllMapItems.markersCaps[n][itarr]; itarr++) {
                        // toggle to opt_enable only if the title ends in the capability string
                        if (tmpCap.toString() == capabilityName.toString() || capabilityName.toString() == 'allCaps') {
                            marker.setMap(opt_enable ? AllMapItems.map : null);

                            drawAsUnselectedIcon(marker, capabilityName);
                            break;
                        }
                    }
                }
            }
        };

        //////
        /**
         * Shows or hides the poly-line overlay on the map.
         */
        AllMapItems.togglePolyline = function(opt_enable) {
            if (typeof opt_enable == 'undefined') {
                opt_enable = !AllMapItems.polyline.getMap();
            }
            AllMapItems.polyline.setMap(opt_enable ? AllMapItems.map : null);
        };

        /**
         * Shows or hides the polygon overlay on the map.
         */
        AllMapItems.togglePolygon = function(opt_enable) {
            if (typeof opt_enable == 'undefined') {
                opt_enable = !AllMapItems.polygon.getMap();
            }
            AllMapItems.polygon.setMap(opt_enable ? AllMapItems.map : null);
        };

        // hides overlays
        AllMapItems.clearAllOverlays = function() {
            AllMapItems.toggleMarkers(false);
            if (AllMapItems.polyline != null && AllMapItems.polyline.getMap()) {
                AllMapItems.togglePolyline(false);
            }
            if (AllMapItems.polygon != null && AllMapItems.polygon.getMap()) {
                AllMapItems.togglePolygon(false);
            }
        };

        // deletes all overlays (permanently)
        AllMapItems.deleteMarkers = function() {
            if(AllMapItems.markers!= null && AllMapItems.markers.length > 0 ) {
                for (var n = 0, marker; marker = AllMapItems.markers[n]; n++) {
                    AllMapItems.markers[n].setMap(null);
                    AllMapItems.markersCaps[n].length = 0;
                }
                AllMapItems.markers.length = 0;
                AllMapItems.markerNodeEnabledStatus.length = 0;
                AllMapItems.markerNodeStatusSynched.length = 0;
                AllMapItems.markerNodeIds.length = 0;
                AllMapItems.markerGWOfNodesIds.length = 0;
                AllMapItems.markersCaps.length = 0;
                AllMapItems.markersDescription.length = 0;
            }

        }
        AllMapItems.deleteAllOverlays = function() {
            AllMapItems.deleteMarkers();
            if (AllMapItems.polyline != null ) {
                AllMapItems.polyline.setMap(null);
                AllMapItems.polyline = null;
            }
            if (AllMapItems.polygon != null) {
                AllMapItems.polygon.setMap(null);
                AllMapItems.polygon = null;
            }
        };

        function cleanMapAndFillWithNodesOfCap(capability) {
            //debug
            //console.log("DEBUG ALL STORED CAPS FOR NODES");
            //for (var n = 0, marker; marker = AllMapItems.markers[n]; n++) {
           //     console.log("Marker " + n) ;
           //     for (var itarr = 0, tmpCap; tmpCap = AllMapItems.markersCaps[n][itarr]; itarr++) {
           //         // toggle to opt_enable only if the title ends in the capability string
           //         console.log(tmpCap.toString());
           //     }
           // }
            //end of debug
            //clearNotificationMsgField();
            infoWindow.close();
            globalCapabilitytoViewInGMs = capability;
            MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;
            AllMapItems.clearAllOverlays();
            AllMapItems.toggleMarkers(true, capability);
            ////
            MarkerSelection.Display(AllMapItems.map);
            //    mapSelectionChanged();
        }

        function afterGetEquivListReturns(replyVal,parValues) {
            console.log("Start afterGetEquivListReturns - reply: " + replyVal + " params: " + parValues);
            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }

            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }
            var gwId = parArr[0];
            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (equiv list get info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');

                    var equivListsTable=document.getElementById("equivListsTable");
                    var rowCount = 0;
                    if(equivListsTable) {
                        rowCount = equivListsTable.rows.length;
                    }

                    var nextIdx = parseInt(document.getElementById("autoIncForEquivListRowIds").value)  + 1;

                    // -------| node selections
                    var nodeSelectDisp = "";
                    var nodeSelectVal = "";
                    var uniqSelHDId = '';  // has the db id  (identifies the node selection)
                    var uniqListDbId = ''; //also has the db id  (identifies the list)
                    var uniqSelTableShownIndex = ''//an inc number   ?

                    var rtSelId =   tmpArr[0];
                    uniqSelTableShownIndex = rtSelId; //nextIdx;
                    uniqSelHDId = 'equivListNodes_'+ rtSelId;   //was + nextIdx
                    uniqListDbId = 'uniqEquivListPartId_'+ rtSelId;
                    var rtNodeVSPtsStr = tmpArr[1];
                    var rtNodeVSPtsLong = tmpArr[2];
                    var rtNodeVGWtsStr = tmpArr[3];
                    var rtNodeVGWtsLong = tmpArr[4];
                    var listEquivSynchedStatus='unknown';
                    if(rtNodeVSPtsLong && rtNodeVGWtsLong && rtNodeVSPtsLong > 0 && rtNodeVGWtsLong >= rtNodeVSPtsLong) {
                        listEquivSynchedStatus = "synched";
                    } else {
                        listEquivSynchedStatus = "unsynched";
                    }
                    var isMarkedForDeletion = tmpArr[5];
                    var statusForDeletionDisp = "";
                    if(isMarkedForDeletion!=null && isMarkedForDeletion == '1') {
                        listEquivSynchedStatus = "Deleted - Awaiting Synch";
                    }
                    var numOfNodes = 0;
                    for(var j1 = 6; j1 <tmpArr.length ;j1+=2) {
                        var rtNodeGW = tmpArr[j1];
                        var rtNodeId = tmpArr[j1+1];
                        nodeSelectVal  += rtNodeGW    +'::##::'+ rtNodeId;
                        numOfNodes+=1;
                        if(j1 < tmpArr.length - 2) {
                            nodeSelectVal +=',';
                        }

                    }


                    nodeSelectDisp = numOfNodes + " nodes&nbsp;<a href=\"javascript:void(0);\" onclick=\'javascript:showEquivListingNodeSelectionDetails(\""+uniqSelHDId+"\");\'>(view)</a>";
                    // TODO: keep in mind that with AJAX before entering this method, we should erase all rows from the table!

                    //update the autoinc.
                    document.getElementById("autoIncForEquivListRowIds").value = nextIdx;
                    var row=equivListsTable.insertRow(-1); // add at the end
                    var cellEquivListDBId=row.insertCell(0);
                    var cellNodeSelect=row.insertCell(1);
                    var cellSynchStatus=row.insertCell(2);
                    var cellAction=row.insertCell(3);

                    // we add the node selection as hidden field outside the cells to be kept when the fields are deleted,
                    cellEquivListDBId.innerHTML=''+uniqSelTableShownIndex;
                    cellNodeSelect.innerHTML="<input type=\"hidden\" id=\""+ uniqListDbId +"\" value=\""+''+"\" /><input type=\"hidden\" name=\"equivListNodes[]\" id=\""+uniqSelHDId+"\" value=\""+nodeSelectVal+"\" /><div>"+ nodeSelectDisp+ "</div>";
                    cellSynchStatus.innerHTML= "("+listEquivSynchedStatus+")";
                    // todo delete action should need a confirmation
                    cellAction.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeEquivListing(\""+gwId+"\",\""+uniqListDbId+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
                }
            }
        }

        function afterAddingNewEquivListReturns(replyVal,parValues) {
            console.log("Start afterAddingNewEquivListReturns - reply: " + replyVal + " params: " + parValues);
            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }

            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }
            var gwId = parArr[0];
            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (equiv list add info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');
                    if(tmpArr[0] && tmpArr[0] > 0) {
                        removeAllHTMLRowsOfEquivLists();
                        getEquivSetsListForVGW(gwId,afterGetEquivListReturns);
                        alert("The new equivalency list was added successfully. Awaiting synchronization with VGW...");
                    }
                    else if(tmpArr[0] && tmpArr[0] == -1) {
                        alert("An error occurred while trying to add the new equivalency list. Please check your selection and try again.");
                    } else if(tmpArr[0] && tmpArr[0] == -2)  {
                        alert("This list already exists in the DB.");
                    } else if(tmpArr[0] && tmpArr[0] == -3)  {
                        alert("Please select more than 2 nodes for a new equivalency list.");
                    }
                    else {
                        alert("An unexpected error occurred while trying to add the new equivalency list. Please check your selection and try again.");
}
                }
            }


        }

        ///
        ///
        ///
        function afterDeletingEquivListReturns(replyVal,parValues) {
            console.log("Start afterDeletingEquivListReturns - reply: " + replyVal + " params: " + parValues);
            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }

            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }
            var gwId = parArr[0];
            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (equiv list add info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');
                    if(tmpArr[0] && tmpArr[0] >= 0) {
                        removeAllHTMLRowsOfEquivLists();
                        getEquivSetsListForVGW(gwId,afterGetEquivListReturns);
                        alert("The equivalency list was removed successfully. Awaiting synchronization with VGW...");
                    }
                    else if(tmpArr[0] && tmpArr[0] < 0) {
                        alert("An error occurred while trying to remove the equivalency list. Please check your selection and try again.");
                    }
                    else{
                        alert("An unexpected error occurred while trying to remove the equivalency list. Please check your selection and try again.");
                    }
                }
            }
        }

        //
        //
        //
        function afterGWSensorEnableDisableReturns(replyVal,parValues) {
            infoWindow.close();
            //console.log("Start afterGWSensorEnableDisableReturns - reply: " + replyVal + " params: " + parValues);
            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }

            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }
            var gwId = parArr[0];
            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (sensorList info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');
                }
            }
        }


        //
        //
        //
        function afterGWCapabilitesListReturns(replyVal,parValues)
        {
            // console.log("Start afterGWCapabilitesListReturns - reply: " + replyVal + " params: " + parValues);
            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }
            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }
            removeAllCapabilitiesFromRadioButtonList();
            var gwId = parArr[0];

            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (Capabilities RB info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');
                    var rtFullCap  = tmpArr[0];
                    var rtNoPrefixCap = tmpArr[1];
                    var rtGwIconCap = tmpArr[2];
                    var friendNameCap = getCapabilityFriendlyName(rtFullCap);
                    // Add new cap option in the radio buttons list:
                    var sIdx = 0;
                    var rdbCapsTable=document.getElementById("rdbCapsTable");
                    var rowCount = rdbCapsTable.rows.length;
                    var nextIdx = parseInt(document.getElementById("autoIncForCapRBRowIds").value)  + 1;
                    //update the autoinc.
                    document.getElementById("autoIncForCapRBRowIds").value = nextIdx;

                    var row=rdbCapsTable.insertRow(-1); // add at the end
                    row.className = "nav nav-stacked";
                    var cellRBInput=row.insertCell(0);
                    var cellCapIcon=row.insertCell(1);
                    cellRBInput.innerHTML="<input type=\"hidden\" id=\"uniqCapRBId_"+ nextIdx +"\"  value=\"\" />" +
                            "<input type=\"radio\" name=\"availableCapabilitiesRBGroup\" id=\""+rtNoPrefixCap+ "RB\" value=\""+rtNoPrefixCap+ "\" onClick=\"cleanMapAndFillWithNodesOfCap('"+rtNoPrefixCap+ "')\" />";
                    cellCapIcon.innerHTML="<label for=\""+rtNoPrefixCap+ "RB\"><img title=\""+ friendNameCap+ "\" src=\"<%=request.getContextPath() %>/img/"+ rtGwIconCap + "\" onclick=\"setCheckedValue(document.getElementById('"+rtNoPrefixCap+ "RB'), \'"+rtNoPrefixCap+"\');cleanMapAndFillWithNodesOfCap('"+rtNoPrefixCap+"');\" style=\"height: 24px;width: 24px;\" /></label>";

                }
                getSensorListForVGW(gwId,afterGWSensorsListReturns);

            }
        }
        //
        //
        //
        function afterGWSensorsListReturns(replyVal,parValues) {
            //console.log("Start afterGWSensorsListReturns - reply: " + replyVal + " params: " + parValues);
            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }

            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }
            var gwId = parArr[0];
            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (sensorList info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');
                    var rtNodeId  = tmpArr[0];
                    var rtNodeLongit = tmpArr[1];
                    var rtNodeLatit = tmpArr[2];
                    var rtNodeEnabled = tmpArr[3];
                    var nodeEnabledStatus = 'unknown';
                    if(rtNodeEnabled) {
                        if(rtNodeEnabled=='1' ){
                            nodeEnabledStatus = "enabled";
                        }  else {
                            nodeEnabledStatus = "disabled";
                        }
                    }
                    var rtNodeSynched = tmpArr[4];
                    var nodeEnablStatusSynched ='unknown';
                    if(rtNodeSynched) {
                        if(rtNodeSynched=='1' ){
                            nodeEnablStatusSynched = "synched";
                        }  else {
                            nodeEnablStatusSynched = "unsynched";
                        }
                    }

                    var rtNodeCaps = [];
                    var rtNodeCapsNoPrefix = [];
                    var rtNodeCapsIcon = [];

                    var capsDispTableIcons = '';
                    capsDispTableIcons ='<table border=\"0\"><tr><td colspan=\"2\"><strong>Supported Capabilities</strong></td></tr>';

                    for(var j1 = 5; j1 <tmpArr.length ;j1+=3) {
                        rtNodeCaps.push(tmpArr[j1]);
                        rtNodeCapsNoPrefix.push(tmpArr[j1+1]);
                        var friendNameCap = getCapabilityFriendlyName(tmpArr[j1]);

                        capsDispTableIcons += '<tr><td>';
                        capsDispTableIcons +=friendNameCap; // friendly name;
                        capsDispTableIcons +='</td><td><img src=\"<%=request.getContextPath() %>/img/'+ tmpArr[j1+2] +'\" style=\"height: 16px;width: 16px;\" />';
                        capsDispTableIcons +='</td></tr>';

                        rtNodeCapsIcon.push(tmpArr[j1+2]);
                    }
                    capsDispTableIcons += '</table>';
                    // ++++++++++++++++++++++++++++++++++++++++++
                    //console.log('caps arr init: ' + capsArrInit);
                    //console.log('disp table:' + capsDispTableIcons);
                    myLatlng = new google.maps.LatLng(rtNodeLatit, rtNodeLongit);
                    // Cache Geo-coding results in order to not abuse Google Geo-coding service.
                    // Before calling the google geocode api, send it to out server and query if it is in our cache (e.g. DB entry)
                    // If it is not, call the geocode, store it in your cache and return the result.
                    //
                    //nearestAddress = geocodePosition(myLatlng);
                    // nearestAddress = '*feature temporarily disabled*';
                    contentString = '<p><b>Info for Node '+ rtNodeId+' ('+gwId+') </b> (' +
                            [myLatlng.lat(), myLatlng.lng()].join(', ') +
                            '): <br />'+
                            '<span class=\"add-on pull-left\">status&nbsp;('+nodeEnabledStatus+'::'+ nodeEnablStatusSynched+')&nbsp;</span>'+
                            '<span class=\"add-on\"><a href=\"javascript:void(0);\" class=\"btn btn-mini btn-info pull-left\" onclick=\"setEnableDisableSensorForVGW(\''+gwId+'\',\''+rtNodeId+'\',\'enable\', afterGWSensorEnableDisableReturns);return false;\" >Enable</a></span>'+
                            '<span class=\"add-on\"><a href=\"javascript:void(0);\" class=\"btn btn-mini btn-info pull-left\" onclick=\"setEnableDisableSensorForVGW(\''+gwId+'\',\''+rtNodeId+'\',\'disable\', afterGWSensorEnableDisableReturns);return false;\" >Disable</a></span>'+
                            capsDispTableIcons +'<br />' +
                            '</p>';
                    //console.log('content string: ' + contentString);
                    putMarkerWithInfo(myLatlng, contentString, rtNodeId, gwId, rtNodeEnabled, rtNodeSynched, rtNodeCapsNoPrefix);
                    //console.log('After put marker info');

                }
                capCheckedInRB = getCheckedValue(document.getElementById('allCapsRB'));
                if(capCheckedInRB!=null) {
                    cleanMapAndFillWithNodesOfCap(capCheckedInRB);
                } else
                {
                    cleanMapAndFillWithNodesOfCap('allCaps');
                }
            }

            getEquivSetsListForVGW(gwId,afterGetEquivListReturns);
        }

        function afterGWInfoReturns(replyVal,parValues)
        {
            //console.log("Start afterGWInfoReturns - reply: " + replyVal + " params: " + parValues);

            if(replyVal == null || typeof replyVal === "undefined" || parValues == null ||  typeof parValues === "undefined")
            {
                return;
            }

            parArr = parValues.toString().split(',');
            if(parArr ==null || parArr[0]=="" || document.getElementById("hdIdOfSelectedGw").value != parArr[0])
            {
                console.log("Ajax reply came for a different gw than the one selected!");
                return;
            }

            var replyValRows =  replyVal.toString().split(/\n/);
            //console.log("answer length is "+answeral.length);
            if (replyValRows.length==0)
            {
                console.log("No rows in reply (gw info)!");
                return;
            }
            else
            {
                for (i = 0; i < replyValRows.length; i++) {
                    if(replyValRows[i].toString()=="")  {
                        continue;
                    }
                    tmpArr = replyValRows[i].toString().split(',');
                    var rtGwId  = tmpArr[0];
                    var rtGwFriendName = tmpArr[1];
                    var rtGwLongit = tmpArr[2];
                    var rtGwLatit = tmpArr[3];
                    var rtGwInMem = tmpArr[4];
                    var rtGwEnabled = tmpArr[5];

                    var strGWHasResources = '(No Resources discovered)';
                    if(rtGwInMem)   {
                        if(rtGwInMem == '1'){
                            strGWHasResources = '';
                        }else {
                            rtGwInMem = '0';
                        }
                    }  else {
                        rtGwInMem = '0';
                    }


                    var srtGWEnabled = '(Unknown status)';
                    if(rtGwEnabled)   {
                        if(rtGwEnabled == '1'){
                            srtGWEnabled = '(Enabled)';
                        }else {
                            srtGWEnabled = '(Disabled)';
                        }
                    }  else {
                        rtGwEnabled = '0';
                    }


                    if (document.getElementById('gwInfoNameAHRefId') ) {
                        document.getElementById('gwInfoNameAHRefId').innerHTML = '&nbsp;&nbsp;&nbsp;Showing&nbsp;resources&nbsp;for:&nbsp;'+ rtGwFriendName + '&nbsp;' + strGWHasResources + '&nbsp;' + srtGWEnabled;
                    }

                    if(!(typeof rtGwLongit === "undefined") && rtGwLongit!= "" && !(typeof rtGwLatit === "undefined") && rtGwLatit!= "" && AllMapItems.map!=null ){
                        infoWindow.close();
                        AllMapItems.map.setZoom(19);
                        gwLatlng = new google.maps.LatLng(rtGwLatit, rtGwLongit);
                        AllMapItems.map.panTo(gwLatlng);
                    }
                    if(rtGwInMem == "0") {
                        alert("No resources have been discovered for this VGW: "+rtGwId);
                    }
                    else {
                        getCapabilitiesListForVGW(rtGwId,afterGWCapabilitesListReturns);
                    }
                }
            }
        }

        function viewGatewayResources(pVgwId, pStatus) {
            if(typeof pStatus === "undefined" || pStatus == null ) {
                console.log("Start - Viewing Resources for: "+ pVgwId);
            } else {
                console.log("Start - Viewing Resources for: "+ pVgwId + " of status: "+ pStatus);
            }
            document.getElementById("hdIdOfSelectedGw").value = pVgwId;
            getGWInfo(pVgwId, afterGWInfoReturns);
        }


    </script>
</head>
<body onload="mapInitialize();"><%-- For the menu --%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
				<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
				<!--<div class="well"> -->
                <div class="row-fluid">
                    <div class="well">
                        <!-- <div class="span12">  -->

<%
	Logger logger = Logger.getRootLogger();
	
    Vector<DBRegisteredGateway> regGatewayssVec = DBCommons.getDBCommons().getRegisteredGatewayEntries();
    Iterator<DBRegisteredGateway> regGatewaysIter = regGatewayssVec.iterator();
    out.println("<table width=\"100%\" class=\"table table-striped servicelist reborder\" >");
    //out.println("<tr > <td colspan=8 style=\"vertical-align: top; text-align: center;\">\n" +
    //        "<input id=\"middwConnectorCbx\" name=\"selectMiddWConnector\" type=\"checkbox\"/> <label for=\"middwConnectorCbx\">Use primary framework</label>\n" +
    //        "&nbsp;&nbsp;<input type=\"button\" id=\"reloadResViaSelConnectorBtn\" name=\"reloadResViaSelConnectorBtn\" value=\"Purge All and Reload Resources\" /></td> </tr>");
    out.println("<tr style=\"background-color: #F3F783!important;\" ><td colspan=8><div align=\"center\"><strong>VITRO Gateways Registered with the VSP</strong></div></td></tr>");
    out.println("<tr style=\"background-color: #BFDEE3!important;\" ><td><strong>Gateway name</strong></td><td><strong>Gateway Description</strong></td><td><strong>Gateway ID</strong></td><td><strong>Last Received Adv On</strong></td><td colspan=4><strong>Action</strong></td></tr>");

    int rowAlt = 0;
    while(regGatewaysIter.hasNext())
    {
        DBRegisteredGateway currGateway = regGatewaysIter.next();

    // Now do something with the ResultSet ....
    // Fetch each row from the result set
        String bgrowcolor = "#F7FFFA";
        if (rowAlt > 0)
        {
            bgrowcolor  = "#F7F7FC";
        }
        rowAlt++;
        rowAlt = rowAlt %2;

        int gateId = currGateway.getIdregisteredGateway();
        String gateRegisteredName = currGateway.getRegisteredName();// this is the one used in registration messages
        
        String gateFriendlyName = currGateway.getFriendlyName();
        String gateIp  = currGateway.getIp();
        String gatePort  = currGateway.getListeningport();
        String lastDate = "N/A";
        Boolean disabled = currGateway.getStatus();
        
        if ( currGateway.getLastadvtimestamp() > 0 )
        {
            lastDate = currGateway.getLastDate();
        }

        out.println("<tr bgcolor="+bgrowcolor+">");
        out.println("<td>");
        out.println(gateRegisteredName);
        out.println("</td>");
        out.println("<td>");
        out.println(gateFriendlyName);
        out.println("</td>");
        out.println("<td>");
        out.println(gateId);
        out.println("</td>");
        out.println("<td><span id=\"gwLastUpdateAdvTSDiv_"+gateRegisteredName+"\">");
        out.println(lastDate);
        out.println("</span></td>");
        out.println("<td>");    %>
                        <a class="btn btn-small" href="javascript:void(0);" onclick="mapCleanup();requestGatewayUpdate('<%=gateRegisteredName%>', '<%=currGateway.getStatus()%>');return false;" ><img title="Request Status Update" src="<%=request.getContextPath()%>/img/getResources52h.png" style="height: 32px;width: 32px;" /></a>
        <% out.println("</td>");
        out.println("<td>");  %>
                        <a class="btn btn-small" href="javascript:void(0);" onclick="mapCleanup();purgeGatewayResources('<%=gateRegisteredName%>', '<%=currGateway.getStatus()%>');return false;" ><img title="Purge resources" src="<%=request.getContextPath()%>/img/purgeResources52h.png" style="height: 32px;width: 32px;" /></a>
        <% out.println("</td>");
        out.println("<td>");
        if (disabled==false)
        {   %>
                        <a class="btn btn-small" href="javascript:void(0);" onclick="mapCleanup();switchGatewayState('<%=gateRegisteredName%>', '<%=currGateway.getStatus()%>');return false;" ><img title="Disable VGW" src="<%=request.getContextPath()%>/img/disableGw52h.png" style="height: 32px;width: 32px;" /></a>
           <%
        }
        else
        {    %>
                        <a class="btn btn-small" href="javascript:void(0);" onclick="mapCleanup();switchGatewayState('<%=gateRegisteredName%>', '<%=currGateway.getStatus()%>');return false;" ><img title="Enable VGW" src="<%=request.getContextPath()%>/img/enableGw52h.png" style="height: 32px;width: 32px;" /></a>

            <%}
        out.println("</td>");
        out.println("<td>");
        %>
                        <a class="btn btn-small" href="javascript:void(0);" onclick="mapCleanup(false);viewGatewayResources('<%=gateRegisteredName%>', '<%=currGateway.getStatus()%>');$('html, body').animate({ scrollTop: ($('#gmMapRowDiv1').offset().top  - 62) }, 'slow');return false;" ><img title="View resources" src="<%=request.getContextPath()%>/img/ViewResources52h.png" style="height: 32px;width: 32px;" /></a>
         <%
        out.println("</td>");
        out.println("</tr>");
    }
    out.println("</table>");

%>
                        <!-- </div> -->
                    </div>
                </div>
                <div class="row-fluid"  id="gmMapRowDiv1" name="gmMapRowDiv1" style="display: none;">
                    <div class="span12">
                        <div class= "span2">
                            <input type="hidden" id="activeMapSelectionHD" name="activeMapSelection" value="" />
                        </div>
                        <div class="span10 btn-toolbar">
                            <a class="btn btn-small" href="javascript:void(0);" onclick="MarkerSelection.DeleteLastPoint(AllMapItems.map);" ><img title="Delete last marker" src="<%=request.getContextPath()%>/img/demo/demoUndo32.png" style="height: 16px;width: 16px;" /></a>
                            <a class="btn btn-small" href="javascript:void(0);" onclick="MarkerSelection.Clean(AllMapItems.map);" ><img title="Clear Map Selection" src="<%=request.getContextPath()%>/img/demo/demoSelectionNew32.png" style="height: 16px;width: 16px;" /></a>
                            <a class="btn btn-small" href="javascript:void(0);" onclick="MarkerSelection.SwitchSelectAll(AllMapItems.map);" ><img title="(un)select all" src="<%=request.getContextPath()%>/img/demo/demoCircleAllNone32.png" style="height: 16px;width: 16px;" /></a>
                            <a class="btn btn-small" href="javascript:void(0);" onclick="javascript:getSelectionInHiddenField();addNewEquivListing();" ><img title="Add nodes to an equivalency list" src="<%=request.getContextPath()%>/img/demo/listAdd32sm.png" style="height: 16px;width: 16px;" /></a>
                            <a class="" href="javascript:void(0);" id="gwInfoNameAHRefId" style="text-decoration: none !important; text-emphasis: none; font-size: small;"></a>
                            <a class="" href="javascript:void(0);" id="gwdummyspace" style="text-decoration: none !important; text-emphasis: none; font-size: small;">&nbsp;&nbsp;&nbsp;&nbsp;</a>
                            <a class="btn btn-small" href="javascript:void(0);" onclick="mapCleanup(false);viewGatewayResources(document.getElementById('hdIdOfSelectedGw').value);$('html, body').animate({ scrollTop: ($('#gmMapRowDiv1').offset().top  - 62) }, 'slow');return false;" ><img title="Refresh resources on map" src="<%=request.getContextPath()%>/img/refreshMap52h.png" style="height: 16px;width: 16px;" /></a>
                        </div>
                    </div>
                </div>
                <div class="row-fluid"  id="gmMapRowDiv2" name="gmMapRowDiv2" style="display: none;" >
                    <div class="span1">
                        <legend style="text-decoration: underline;font-size: x-small; line-height: 18px; margin-top: 44px;">Filter by capability:</legend>
                        <input type="hidden" id="autoIncForCapRBRowIds" value="0" />
                        <input type="hidden" id="autoIncForEquivListRowIds" value="0" />
                        <input type="hidden" id="hdIdOfSelectedGw" value="" />
                        <table id="rdbCapsTable" border="0">
                        <tr class="nav nav-stacked">
                            <td>
                                <input
                                        name="availableCapabilitiesRBGroup" id="allCapsRB" value="allCaps"
                                        type="radio" onClick="cleanMapAndFillWithNodesOfCap('allCaps')" checked="checked" />
                            </td>
                            <td>
                                <label for="allCapsRB">
                                    <img title="all Capabilities" src="<%=request.getContextPath() %>/img/smartNodeIcon32.png" onclick="setCheckedValue(document.getElementById('allCapsRB'), 'allCaps');cleanMapAndFillWithNodesOfCap('allCaps');" style="height: 24px;width: 24px;" />
                                </label>
                            </td>
                        </tr>
                    </table>
                    </div>
                    <div class="span11" style="height: 500px !important;" id="map_span">
                        <!-- <div class="span12" style="height: 500px;" id="map_span"> -->
                            <div id="map_canvas" style="height:92%;">
                                <!-- display a google maps container -->
                            </div>
                        <!-- </div> -->
                    </div>
                </div>
                <div class="row-fluid"  id="gmMapRowDiv3" name="gmMapRowDiv3" style="display: none;" >
                    <!-- equivalency listings -->
                    <div class="span12">
                        <!-- <legend style="text-decoration: underline;">Node Equivalency Listings</legend> -->
                        <table width="100%" class="table table-striped servicelist reborder" id="equivListsTable">
                            <tr style="background-color: #F3F783!important;" ><td colspan=4><div align="center"><strong>Node Equivalency Listings</strong></div></td></tr>
                            <tr style="background-color: #BFDEE3!important;" ><td><strong>ID</strong></td><td><strong>Nodes</strong></td><td><strong>Synched Status</strong></td><td><strong>Action</strong></td></tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>
</html>
