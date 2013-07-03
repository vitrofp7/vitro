<!DOCTYPE html>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.service.geo.Coordinate" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="vitro.vspEngine.service.geo.GeoPoint" %>
<%@ page import="vitro.vspEngine.logic.model.*" %>
<%@ page import="presentation.webgui.vitroappservlet.Model3dservice.*" %>
<%@ page import="java.util.*" %>
<%@ page session='false' contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>

    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Service Creation</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">


    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
    <!-- for jqGrid support
    <link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/smoothness/jquery-ui-1.10.1.custom.css" >
    <link rel="stylesheet" type="text/css" media="screen" href="<%=request.getContextPath()%>/css/jqgrid/ui.jqgrid.css" />
    <script src="<%=request.getContextPath()%>/css/jqgrid/grid.locale-en.js" type="text/javascript"></script>
    <script src="<%=request.getContextPath()%>/css/jqgrid/jquery.jqGrid.min.js" type="text/javascript"></script>
    end: for jqGrid support -->

    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-custnew').addClass("active");
 	});     
$(function() {
   var maxHeight=0;
   $('#side_span').each(function(){
      if($(this).height()>maxHeight) {
       maxHeight=$(this).height();
      }
   });

    $('#map_span').height(maxHeight);
});
	</script>

    <script type="text/javascript"
        src="http://maps.googleapis.com/maps/api/js?key=AIzaSyCEgVsh2dojyU0qWl5l2yyYIgM4uy-FqyA&sensor=false">
    </script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/capabilityIconsJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/googleMapsMarkerSelectionJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/newServiceJS.jsp"></script>
    <script type="text/javascript">
<%   String defaultCapability = "allCaps";//Capability.PHENOMENOM_TEMPERATURE;
    String staticprefixCapability =Capability.dcaPrefix; %>

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

var defaultCapability = '<%=defaultCapability %>';//startupCapability;
var globalCapabilitytoViewInGMs = defaultCapability;
MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;

//var globalCapabilitytoViewInGMs = 'allCaps';
//MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;

var capabilityPrefix = '<%=staticprefixCapability %>';

var update_timeout_for_Dbl_Vs_SingleClick = null;

var geocoder = new google.maps.Geocoder();
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

// function to show the nearest address for a node
function geocodePosition(pos, indexInArray) {
    var currAddrPos = '';
    geocoder.geocode({
        location: pos
    }, function(responses, status) {

        if (responses && responses.length > 0) {
            currAddrPos = responses[0].formatted_address + '';
            // responds asynchronously so there is no meaning in returning somethin here!
            // should store the value in a DB!
            AllMapItems.markersAddress[indexInArray] = currAddrPos;
        } else {
            currAddrPos = 'Cannot determine address at this location.';
            currAddrPos += ' ';
            if (status) {
                switch (status) {
                    case google.maps.GeocoderStatus.ERROR:
                        currAddrPos += 'Error accessing Google servers.';
                        break;
                    case google.maps.GeocoderStatus.INVALID_REQUEST:
                        currAddrPos += 'This GeocoderRequest was invalid.';
                        break;
                    case google.maps.GeocoderStatus.OK:
                        currAddrPos += 'The response contains a valid GeocoderResponse.';
                        break;
                    case google.maps.GeocoderStatus.OVER_QUERY_LIMIT:
                        currAddrPos += 'The webpage has gone over the requests limit in too short a period of time.';
                        break;
                    case google.maps.GeocoderStatus.REQUEST_DENIED:
                        currAddrPos += 'The webpage is not allowed to use the geocoder.';
                        break;
                    case google.maps.GeocoderStatus.UNKNOWN_ERROR:
                        currAddrPos += 'A geocoding request could not be processed due to a server error. The request may succeed if you try again.';
                        break;
                    case google.maps.GeocoderStatus.ZERO_RESULTS:
                        currAddrPos += 'No results were returned.';
                        break;

                    default:
                        currAddrPos += 'Unknown error.';
                        break;
                }
            }
            AllMapItems.markersAddress[indexInArray] = currAddrPos;
        }
    });
    //return currAddrPos;   // TODO to remove! the function in geocode responds asynchronously so there is no meaning in returning somethin here!
}

var onMarkerClick = function(idxMarker) {
    var curr_marker = AllMapItems.markers[idxMarker]; // this;
    var latLng = curr_marker.getPosition();
    var foundMatch = false;
    for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
        if (curr_marker == itMarker) {
            if (AllMapItems.markersAddress[n] == "undefined") {
                geocodePosition(latLng, n);
            }
            setTimeout(function() {
                var contentToSet = createContentInfoHeader(AllMapItems.markerNodeIds[n]) + AllMapItems.markersDescription[n];
                /*
                contentToSet += '<p>Latest values of ' + globalCapabilitytoViewInGMs + ' for this node: <a href="<%=request.getContextPath()%>/graphNode.jsp?nodeUrn=' + AllMapItems.markerNodeIds[n] + '&capUrn=' + capabilityPrefix + globalCapabilitytoViewInGMs + '&t=w"  target="_blank">' +
                        'Week</a> ';
                contentToSet += '&nbsp;<a href="<%=request.getContextPath()%>/graphNode.jsp?nodeUrn=' + AllMapItems.markerNodeIds[n] + '&capUrn=' + capabilityPrefix + globalCapabilitytoViewInGMs + '&t=d"  target="_blank">' +
                        'Day</a>';
                contentToSet += '&nbsp;<a href="<%=request.getContextPath()%>/graphNode.jsp?nodeUrn=' + AllMapItems.markerNodeIds[n] + '&capUrn=' + capabilityPrefix + globalCapabilitytoViewInGMs + '&t=h"  target="_blank">' +
                        'Hour</a>';
                contentToSet += '&nbsp;<a href="<%=request.getContextPath()%>/graphNodeC.jsp?nodeUrn=' + AllMapItems.markerNodeIds[n] + '&capUrn=' + capabilityPrefix + globalCapabilitytoViewInGMs + '"  target="_blank">' +
                        'Custom Range</a></p>';
                */

                if (AllMapItems.markersAddress[n] != "undefined") {
                    contentToSet += '<p>Nearest Address: ' + AllMapItems.markersAddress[n] + '</p>';
                }
                contentToSet += createContentInfoFooter();
                infoWindow.setContent(contentToSet);
                infoWindow.open(AllMapItems.map, curr_marker);
            }, 200);
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

// to be moved in the other JS. Also take as parameter the Multiple Selection TextArea text box!
// new:
function putSelectionInMultiSelectionArea() {
    clearNotificationMsgField();
    var prevVal = '';
    var wholeStrToAppend = '';
    var tmpStrToAppend = '';
    //prevVal = $('#multiSelectionTxtBx').val(); //jquery
    prevVal = document.getElementById('multiSelectionTxtBx').value;
    var setFromRegionSelection = '';
    var setFromDistictSelection = '';
    if(anyMapSelectionExists()) {
        if (MarkerSelection.selectedMarkers && MarkerSelection.selectedMarkers.length > 0) {
            setFromRegionSelection += '(';
            for (var i = 0; i < MarkerSelection.selectedMarkers.length; ++i) {
                setFromRegionSelection += MarkerSelection.selectedMarkers[i].getTitle() + ':' + MarkerSelection.selectedCapability + ', ';
            }
            setFromRegionSelection = setFromRegionSelection.replace(/(,\s*$)/g, '');
            setFromRegionSelection = setFromRegionSelection + ')';
        }
        //check also the distinct selections
        if ( MarkerSelection.singleMarkersSelected && MarkerSelection.singleMarkersSelected.length > 0)
        {
            setFromDistictSelection += '(';
            for (var i = 0; i < MarkerSelection.singleMarkersSelected.length; ++i) {
                setFromDistictSelection += MarkerSelection.singleMarkersSelected[i].getTitle() + ':' + MarkerSelection.selectedCapability + ', ';
            }
            setFromDistictSelection = setFromDistictSelection.replace(/(,\s*$)/g, '');
            setFromDistictSelection = setFromDistictSelection + ')';
        }
        wholeStrToAppend = setFromRegionSelection + setFromDistictSelection;
        document.getElementById('multiSelectionTxtBx').value += wholeStrToAppend;
    }

    else
        alert('No selection was detected!');
}

/**
 * The map area can allow only either multiple node selection (Ctrl+Click) or only a single area selection or a combination of the two.
 * It cannot support (currently) multiple regions (so the auxiliary text area can be used for this)
*/
function fetchSensorDataFromMapForService() {
    clearNotificationMsgField();
    selectionControl = document.getElementById('vitroServiceDDL');
    if(selectionControl!= null && selectionControl.options[selectionControl.selectedIndex].value == "")
    {
        alert('Please select a valid service first from the dropdown list.');
        return;
    }
    else if(!anyMapSelectionExists())
    {
        alert('No selection was detected.');
        return;
    }
    //get data for the sensors selected on the map (not in the text area box
    // TODO: use ajax

}

/**
 * The text area is used for storing nodes from multiple selection areas (because the UI only supports one area)
 * So we have two versions of the fetch sensor and remove sensors functions. One that gets sensors from the map and the other from the text area.
 */
function fetchSensorDataFromTextAreaForService() {
    clearNotificationMsgField();
    if (document.getElementById('multiSelectionTxtBx').value.length == 0) {
        document.getElementById('errorMessage').innerHTML = "Please make a sensor selection first!";
    }
    else {
        //clear-up duplicates, bring values for the rest of the sensors (from the VSN results)
        // TODO: use ajax
    }
}

function fetchAllSensorDataforService() {
    clearNotificationMsgField();
    selectionControl = document.getElementById('vitroServiceDDL');
    if(selectionControl!= null && selectionControl.options[selectionControl.selectedIndex].value == "")
    {
        alert('Please select a valid service first from the dropdown list.');
        return;
    }
    // get all sensors from map (for this service) (independent from the current map selection or the nodes in the text area box)
    // get their most recent values for the service and dump them in a scrolling text area.
    // TODO: use ajax
    alert('Attempting to fetching data from every sensor in the Service...');
}

function removeSelectionFromMapForService() {
    clearNotificationMsgField();
    selectionControl = document.getElementById('vitroServiceDDL');
    if(selectionControl!= null && selectionControl.options[selectionControl.selectedIndex].value == "")
    {
        alert('Please select a valid service first from the dropdown list.');
        return;
    }
    else if(!anyMapSelectionExists())
    {
        alert('No selection was detected.');
        return;
    }

    //remove the selected sensors (on map) from the selected VSN
    // TODO: use ajax

}

function removeSelectionFromTextAreaForService() {
    clearNotificationMsgField();
    if (document.getElementById('multiSelectionTxtBx').value.length == 0) {
        document.getElementById('errorMessage').innerHTML = "Please make a sensor selection first!";
    }
    else {
        var xmlhttp = null;
        xmlhttp = getXMLHTTPRequest();
        if (xmlhttp == null) {
            alert("Your browser does not support XMLHTTP.");
        }
        else {

            var url = "removeSelectionFromVSN.jsp";
            var params = "exp=" + encodeURIComponent(document.getElementById('multiSelectionTxtBx').value);

            xmlhttp.open("POST", "<%=request.getContextPath()%>/" + url, true);

            xmlhttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xmlhttp.setRequestHeader("Content-length", params.length);
            xmlhttp.setRequestHeader("Connection", "close");
            //alert("<%=request.getContextPath()%>/" + url);
            //http.open("POST", url, true);
            //xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            //xmlhttp.setRequestHeader("Content-length", params.length);
            //xmlhttp.setRequestHeader("Connection", "close");
            //alert('in4');

            xmlhttp.onreadystatechange = function() {
                //alert('ttt');
                if (xmlhttp.readyState == 4) {
                    if (xmlhttp.status == 200) {
                        alert(xmlhttp.responseText);
                        if (xmlhttp.responseText.substring(0, 0) == "1") {
                            // alert('1');
                            document.getElementById('errorMessage').innerHTML = "The specified selection was removed successfully.";
                        } else {
                            // alert('0');
                            document.getElementById('errorMessage').innerHTML = "Error Removing the specified Selection=" + xmlhttp.responseText;
                        }
                    }
                    else {
                        alert("Problem retrieving AJAX response data");
                    }
                }
            }
            xmlhttp.send(params);
        }
    }

}

/**
 * The notification field shows inline some errors or notifications related to user actions
 *
 */
function clearNotificationMsgField(){
    document.getElementById('errorMessage').innerHTML = "";
}

//        function ExtendedMarker (pMarker, pCapsArr) {
//            this.marker = pMarker;
//            this.capabilities = pCapsArr;
//        }
//
//        ExtendedMarker.prototype.getInfo = function() {
//            return this.marker.getTitle() + ' ' + this.capabilities.join(',');
//        };

function vitroServiceDDLValueChanged(selectionControl) {
    clearNotificationMsgField();
    if( selectionControl.options[selectionControl.selectedIndex].value == "")
        return;
    var selectedVSNid = selectionControl.options[selectionControl.selectedIndex].value;
    alert('Loading sensors for Service with id: '+selectedVSNid);
}

function refreshCurrentVSN() {
    clearNotificationMsgField();
    selectionControl = document.getElementById('vitroServiceDDL');
    if(selectionControl!= null && selectionControl.options[selectionControl.selectedIndex].value != "")
    {
        alert('Refreshing selected service...');
    }
}

function showDetailsForCurrentVSN() {
    clearNotificationMsgField();
    hdTotalMapNodes = document.getElementById('hdTotalMapNodes');
    hdDisplayedMapNodes = document.getElementById('hdDisplayedMapNodes');
    hdIgnoredMapNodes = document.getElementById('hdIgnoredMapNodes');
    if(hdTotalMapNodes!= null && hdDisplayedMapNodes!=null && hdIgnoredMapNodes!=null)
    alert('Total Nodes (with valid capabilities): '+ hdTotalMapNodes.value +' \n'+
           'Displayed Nodes: '+ hdDisplayedMapNodes.value  +' \n' +
           'Ignored Nodes that had no Geographical Coordinates Info: '+ hdIgnoredMapNodes.value  +' \n');
}

function resetSelectionBtnClicked(){
    clearNotificationMsgField();
    document.getElementById('multiSelectionTxtBx').value='';
    MarkerSelection.Clean(AllMapItems.map)
//    mapSelectionChanged();
}

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

AllMapItems.removeAllOverlays = function() {
    AllMapItems.toggleMarkers(false);
    if (AllMapItems.polyline != null && AllMapItems.polyline.getMap()) {
        AllMapItems.togglePolyline(false);
    }
    if (AllMapItems.polygon != null && AllMapItems.polygon.getMap()) {
        AllMapItems.togglePolygon(false);
    }
};


function cleanMapAndFillWithNodesOfCap(capability) {
    clearNotificationMsgField();
    infoWindow.close();
    globalCapabilitytoViewInGMs = capability;
    MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;
    AllMapItems.removeAllOverlays();
    AllMapItems.toggleMarkers(true, capability);
    ////
    MarkerSelection.Display(AllMapItems.map);
//    mapSelectionChanged();
}

function createContentInfoHeader(nodeId) {
    return '<div id="content">' +
            '<div id="siteNotice">' +
            '</div>' +
            '<h1 id="firstHeading" class="firstHeading">Info for Node</h1>' +
            '<div id="bodyContent">';
            //'<h1 id="firstHeading" class="firstHeading">Info for Node ' + nodeId + '</h1>' +
            //'<div id="bodyContent">';
}

function createContentInfoFooter() {
    return '</div>' +
            '</div>';
}

// puts the marker but keeps it hidden
function putMarkerWithInfo(myLatlng, contentString, nodeId, gateId, capsarray) {

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

//            var infowindow;
//            infowindow = new google.maps.InfoWindow({
//                content:contentString
//            });

    var marker;
    marker = new google.maps.Marker({
        position:myLatlng,
        map: null,
        icon: image,
        shape: shape,
        title:nodeId
    });

    var idxMarker =  AllMapItems.markers.push(marker) -1;
    AllMapItems.markerNodeIds.push(nodeId);
    AllMapItems.markerGWOfNodesIds.push(gateId);
    AllMapItems.markersCaps.push(capsarray);
    AllMapItems.markersDescription.push(contentString);
    AllMapItems.markersAddress.push("undefined");

    // var point = new google.maps.LatLng(southWest.lat() + latSpan * Math.random(), southWest.lng() + lngSpan * Math.random());
    // for selection with polygons (poin needs to be LatLng point   TODO: should be merged with marker!
    //MarkerSelection.pointsrand.push(myLatlng);

    //var idxMarker = MarkerSelection.allnodeMarkers.push(marker) - 1;

    //google.maps.event.addListener(marker, 'click', onMarkerClick);

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
        }  else {
            var hdActiveMapSelection = document.getElementById('activeMapSelectionHD');
            if(hdActiveMapSelection!= null )
            {
                hdActiveMapSelection.value = '';
            }
        }
    }
}

function initialize() {
    var myOptions = {
        /* center on somewhere central (e.g parma italy) 44.8 , 10.3333,  */
        // TODO To be removed before final
        center: new google.maps.LatLng(44.8000, 10.3333),
        zoom: 5,
        /*mapTypeId: google.maps.MapTypeId.ROADMAP*/
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    var map = new google.maps.Map(document.getElementById("map_canvas"),
            myOptions);
    AllMapItems.map = map;
    MarkerSelection.allnodeMarkers = AllMapItems.markers;
    MarkerSelection.allnodeEnabledStatus = AllMapItems.markerNodeEnabledStatus;
    MarkerSelection.allnodeSynchedStatus = AllMapItems.markerNodeStatusSynched;
    // ------------------------------------------
    var myLatlng;
    var contentString = '';
    var nodeId = 'node';
    var gateId = 'gw';
<%
    ArrayList<String> supportedCapabilities = new ArrayList<String>(); //for use with jstl. We have to set the context of the page to it
    ArrayList<String> supportedCapabilitiesUINames = new ArrayList<String>(); //for use with jstl. We have to set the context of the page to it

    HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
   // GatewayWithSmartNodes currGw = null;
   // Vector<SmartNode> thisGWVector = null;
    Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
    String defaultGW_ID= "vitrogw_cti";
    //Vector<SmartNode> allGWsVector = new Vector<SmartNode>();  //new converted the SmartNode Vector to a HashMap of String (gwiD) to Vector of smartNodes.
    HashMap<String, Vector<SmartNode>> allGWsToNodesHM = new HashMap<String, Vector<SmartNode>>();
    StringBuilder coordsFound = new StringBuilder();
    int totalNodes = 0;
    int ignoredNodesWithNoCoordInfo = 0;
    try
    {
        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        infoGWHM = ssUN.getGatewaysToSmartDevsHM();

// CODE FROM translate to kml (VisualResultsModel)
       HashMap<String, Vector<Model3dInterfaceEntry>> allModelFilesToInterfacesHM = new HashMap<String,  Vector<Model3dInterfaceEntry>>();
        Set<String> keysOfGIds = infoGWHM.keySet();
        Iterator<String> itGw = keysOfGIds.iterator();
        while(itGw.hasNext())
        {
            String currGwId = itGw.next();
    //                outPrintWriter.print("<b>"+currGw.getName()+"::"+currGw.getId()+"</b>##");
            // A gateway can have a VECTOR of models associated with it (not just a single file -though it is preferred that way)

            Vector<Model3dIndexEntry> currIndexEntriesVec = myModelsIndex.getIndexEntriesByGatewayId(currGwId);
            for(int i = 0 ; i < currIndexEntriesVec.size(); i++)
            {
                String tmpModelFilename = currIndexEntriesVec.elementAt(i).getModelFileName();
                String tmpMetaFilenameFullPath = Model3dIndex.getIndexPath() + currIndexEntriesVec.elementAt(i).getMetaFileName();
                long tmpDefaultInterfaceIdforCurrGw = currIndexEntriesVec.elementAt(i).getDefaultInterfaceIdForGwId(currGwId);
                Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                if(tmpMetaFile!=null)
                {
    //                       outPrintWriter.print("<p /><pre>"+tmpMetaFile.toString()+"</pre>");
                    //
                    // get the related Model3dInterfaceEntry from the specified metafile.
                    //
                    Model3dInterfaceEntry tmpRelInterfaceEntry = tmpMetaFile.findInterfaceEntry(currGwId, tmpDefaultInterfaceIdforCurrGw);
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


        String currGwId = "undefined VGW";
        try {
        // TODO: make loop for every VGW.
            //Set<String> keysOfGIds = infoGWHM.keySet();
            Iterator<String> itgwId = keysOfGIds.iterator();
            while(itgwId.hasNext())
            {
                currGwId = itgwId.next();
                GatewayWithSmartNodes currGwObj = infoGWHM.get(currGwId);
                Vector<SmartNode> currSmartNodesVec = currGwObj.getSmartNodesVec();
                 GeoPoint vgwfirstRoomCenter = null;
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

                        if(currInterfaceEntry.getGwId().compareToIgnoreCase(currGwId) == 0) {

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
                allGWsToNodesHM.put(currGwId,allNodesInGWVector);
            }
        }
        catch(Exception e)
        {
            //allGWsVector = null;
            allGWsToNodesHM.put(currGwId,new Vector<SmartNode>());
            System.out.println("Error while trying to present the smart nodes of " + currGwId +". Resources were probably not retrieved yet. Please wait a little longer.");
        }
        if( allGWsToNodesHM!=null && !allGWsToNodesHM.keySet().isEmpty())
        {
            Set<String> keysOfGIds2 = allGWsToNodesHM.keySet();
            Iterator<String> itGw2 = keysOfGIds2.iterator();
            totalNodes = 0;
            StringBuilder supportedCapabilitiesBld;
            StringBuilder supportedCapabilitiesCSVBld;
            StringBuilder supportedCapabilitiesWithIconsTblBld;
            while(itGw2.hasNext())
            {
                currGwId = itGw2.next();
                Vector<SmartNode> currGwNodeVector = allGWsToNodesHM.get(currGwId);
                int currGwNodesNumber =   currGwNodeVector.size();
                totalNodes += currGwNodesNumber;

                for(int i=0; i < currGwNodesNumber; i++)
                {
                    supportedCapabilitiesBld = new StringBuilder();
                    supportedCapabilitiesWithIconsTblBld = new StringBuilder();
                    supportedCapabilitiesCSVBld  = new StringBuilder();
                    supportedCapabilitiesBld.append("<b>Supported Capabilities:</b><br/>");
                    supportedCapabilitiesWithIconsTblBld.append("<table border=\"0\"><tr><td colspan=\"2\"><strong>Supported Capabilities</strong></td></tr>");
                    SmartNode tmpNode = currGwNodeVector.elementAt(i);
                    Coordinate myXyz = tmpNode.getCoordLocation();
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
                        coordsFound.append("("+ Double.toString(myXyz.getX()) + ","+ Double.toString(myXyz.getY()) + "),");
                    }
                    Iterator<SensorModel> capsIt = tmpNode.getCapabilitiesVector().iterator();
                    // ????????
                    SensorModel currentCap;
                    while(capsIt.hasNext()) {
                        currentCap = capsIt.next();
                        String currentCapName = currentCap.getName();
                        String currentCapNameNoPrefix =  currentCap.getName().replaceAll(Pattern.quote(staticprefixCapability),"" );
                        supportedCapabilitiesBld.append(currentCapNameNoPrefix);
                        supportedCapabilitiesBld.append("<br />");

                        supportedCapabilitiesWithIconsTblBld.append("<tr><td>");
                        supportedCapabilitiesWithIconsTblBld.append(Capability.getFriendlyUIName(currentCapNameNoPrefix));
                        supportedCapabilitiesWithIconsTblBld.append("</td><td><img src=\""+request.getContextPath() + "/img/"+ Capability.getDefaultIcon(currentCapNameNoPrefix) +"\" style=\"height: 16px;width: 16px;\" />");

                        supportedCapabilitiesWithIconsTblBld.append("</td></tr>");

                        supportedCapabilitiesCSVBld.append("'"+currentCapNameNoPrefix+"'");
                        if(capsIt.hasNext())
                            supportedCapabilitiesCSVBld.append(",");
                    }
                    supportedCapabilitiesWithIconsTblBld.append("</table>");
                    String nodeId = currGwNodeVector.elementAt(i).getId();
%>
    var capsArr = new Array(<%=supportedCapabilitiesCSVBld.toString()%>);
    var nearestAddress = '';
    nodeId = '<%=nodeId%>';
    gateId = '<%=currGwId%>';
    myLatlng = new google.maps.LatLng(<%=myXyz.getY().toString() %>, <%=myXyz.getX().toString() %>);
    // Cache Geo-coding results in order to not abuse Google Geo-coding service.
    // Before calling the google geocode api, send it to out server and query if it is in our cache (e.g. DB entry)
    // If it is not, call the geocode, store it in your cache and return the result.
    //
    //nearestAddress = geocodePosition(myLatlng);
    // nearestAddress = '*feature temporarily disabled*';
    contentString = '<p><b>Info for Node <%=nodeId %> (<%=currGwId %>) </b> (' +
            [myLatlng.lat(), myLatlng.lng()].join(', ') +
            '): <br /><%=supportedCapabilitiesWithIconsTblBld.toString()%> </p>';
    putMarkerWithInfo(myLatlng, contentString, nodeId, gateId, capsArr);
<%              }
            }
        }

%>
    MarkerSelection.areaSpan = document.getElementById('selectedm2Surface');
    MarkerSelection.areaSpanKm = document.getElementById('selectedkm2Surface');
    MarkerSelection.singleMarkersSelectedSpan = document.getElementById('selectedNodes'); //new:
//    MarkerSelection.selectedMarkersSpan = document.getElementById('selectedNodes');
    ;
    MarkerSelection.Clean(AllMapItems.map);

    //cleanMapAndFillWithNodesOfCap(globalCapabilitytoViewInGMs);
    capCheckedInRB = getCheckedValue(document.getElementById('allCapsRB'));
    if(capCheckedInRB!=null) {
        cleanMapAndFillWithNodesOfCap(capCheckedInRB);
    } else
    {
        cleanMapAndFillWithNodesOfCap('allCaps');
    }
    google.maps.event.addListener(map, 'click', mapClick);

}

</script>
</head>
<body onload="initialize()">
<!-- for the dropdown Menu -->
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
<form action="<%=request.getContextPath()%>/roleEndUser/CreateComposedServiceActionNew" name="serviceCompositionFrm"><br>
<input type="hidden" name="create" value="true">
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
			<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
<div class="span10">
<div class="well">
	<div class="btn-toolbar">
        <div style="display:none;">
				<select class="btn" name="vitroServiceDDL" id="vitroServiceDDL" onchange="vitroServiceDDLValueChanged(this);" >
					<option value="">- Please select a Service -</option>
					<option value="vndId01">Service 1</option>
					<option value="vndId02">Service 2</option>
					<option value="vndId03">Service 3</option>
					<option value="vndId04">Service 4</option>
				</select>
        </div>
        <a class="btn" href="javascript:void(0);" onclick="refreshCurrentVSN();">Refresh map resources</a>
        <a class="btn" href="javascript:void(0);" onclick="showDetailsForCurrentVSN();">Show map details</a>
	</div>
<hr />
<div class="row-fluid">

    <div class="span12">
        <div class= "span2">
        </div>
        <div class="btn-toolbar">
            <a class="btn btn-small" href="javascript:void(0);" onclick="MarkerSelection.DeleteLastPoint(AllMapItems.map);" ><img title="Delete last marker" src="<%=request.getContextPath()%>/img/demo/demoUndo32.png" style="height: 16px;width: 16px;" /></a>
            <a class="btn btn-small" href="javascript:void(0);" onclick="MarkerSelection.Clean(AllMapItems.map);" ><img title="Clear Map Selection" src="<%=request.getContextPath()%>/img/demo/demoSelectionNew32.png" style="height: 16px;width: 16px;" /></a>
            <a class="btn btn-small" href="javascript:void(0);" onclick="MarkerSelection.SwitchSelectAll(AllMapItems.map);" ><img title="(un)select all" src="<%=request.getContextPath()%>/img/demo/demoCircleAllNone32.png" style="height: 16px;width: 16px;" /></a>
            <div style="display: none;">
               <a class="btn btn-small" href="javascript:void(0);" onclick="putSelectionInMultiSelectionArea();" ><img title="Put Map Selection in Container" src="<%=request.getContextPath()%>/img/demo/demoAddSelection32.png" style="height: 16px;width: 16px;" /></a>
               <a class="btn btn-small" href="javascript:void(0);" onclick="fetchSensorDataFromMapForService();" ><img title="Fetch Service Data from Map Selection" src="<%=request.getContextPath()%>/img/demo/demoSelectionMonitor32.png" style="height: 16px;width: 16px;" /></a>
               <a class="btn btn-small" href="javascript:void(0);" onclick="removeSelectionFromMapForService();" ><img title="Remove Map Selection from Service" src="<%=request.getContextPath()%>/img/demo/demoSelectionDelete32.png" style="height: 16px;width: 16px;" /></a>
            </div>
        </div>
    </div>


    <div class="span1">
            <legend style="text-decoration: underline;font-size: x-small; line-height: 18px; margin-top: 44px;">Filter by capability:</legend>
            <table border="0">
                <tr class="nav nav-stacked">
                    <td>
                        <input
                            name="availableCapabilitiesRBGroup" id="allCapsRB" value="allCaps"
                                <% if("allCaps".compareToIgnoreCase(defaultCapability) == 0) { out.print("checked=\"checked\""); } %>
                            type="radio" onClick="cleanMapAndFillWithNodesOfCap('allCaps')" />

                    </td>
                    <td>
                        <label for="allCapsRB">
                            <img title="all Capabilities" src="<%=request.getContextPath() %>/img/smartNodeIcon32.png" onclick="setCheckedValue(document.getElementById('allCapsRB'), 'allCaps');cleanMapAndFillWithNodesOfCap('allCaps');" style="height: 24px;width: 24px;" />
                        </label>
                    </td>
                </tr>
            <%
        if( allGWsToNodesHM!=null && allGWsToNodesHM.keySet()!=null && !allGWsToNodesHM.isEmpty()) {
            //loop through capabilities and present them in radio buttons. Selecting each button should clear the overlay at the map and show only nodes for the selected capability!
            Set<String> capsOfGw = ssUN.getCapabilitiesTable().keySet();
            Iterator<String> itCaps = capsOfGw.iterator();
            boolean foundDefaultCapability = false;
            while (itCaps.hasNext()) {
                String currCap = itCaps.next();
                String currCapNoPrefix = currCap.replaceAll(Pattern.quote(staticprefixCapability),"" );
                supportedCapabilities.add(currCapNoPrefix);
                supportedCapabilitiesUINames.add(Capability.getFriendlyUIName(currCapNoPrefix));

            %>
            <tr class="nav nav-stacked">
                <td>
                    <input
                    name="availableCapabilitiesRBGroup" id="<%=currCapNoPrefix %>RB" value="<%=currCapNoPrefix %>"
                    type="radio"
                <% if(currCapNoPrefix.compareToIgnoreCase(defaultCapability) == 0) { out.print("checked=\"checked\""); } %>
                    onClick="cleanMapAndFillWithNodesOfCap('<%=currCapNoPrefix %>')" />
                </td>
                <td>
                    <label for="<%=currCapNoPrefix %>RB">
                        <img title="<%=Capability.getFriendlyUIName(currCapNoPrefix) %>" src="<%=request.getContextPath() %>/img/<%=Capability.getDefaultIcon(currCapNoPrefix) %>" onclick="setCheckedValue(document.getElementById('<%=currCapNoPrefix %>RB'), '<%=currCapNoPrefix %>');cleanMapAndFillWithNodesOfCap('<%=currCapNoPrefix %>');" style="height: 24px;width: 24px;" />
                    </label>
                </td>
            </tr>
            <%
                if (currCapNoPrefix.compareToIgnoreCase(defaultCapability) == 0) {
                    foundDefaultCapability = true;
                }
            }
            //pageContext.setAttribute("supportedCapabilities",supportedCapabilities);       //for jstl -not anymore
            //pageContext.setAttribute("supportedCapabilitiesUINames",supportedCapabilitiesUINames); //for jstl -not anymore
        }
        else { %>
          <tr class="nav nav-stacked"><td colspan="2">No nodes were found</td></tr>
    <% } %>

            </table>
</div>


<div class="span9" id="map_span">
           	
            <div id="map_canvas" style="height:92%;">
                <!-- display a google maps container -->
            </div>
</div>
<div class="span2" id="side_span">
            <div>

	<div class="well" id="helpnewservice"><strong>Right click</strong><br />on nodes to explicitly add them to the selected set.<br /><br />
                <strong>Double click</strong><br />on a node marker to zoom to it and select it (single selection, will clear all other selected nodes).<br/><br />
                <strong>Left Click</strong> <br />anywhere else on the map to define a selection region and select multiple nodes within it.
                <p/></div>
            <div style="height: 120px; display:none;">Selection surface (m&sup2;):&nbsp;<span id="selectedm2Surface"></span></div>
            <br/> <div style="height: 120px;">
            Selection surface (km&sup2;):&nbsp;<span id="selectedkm2Surface"></span>
            </div>
            <br/>

            <div  style="display:none;">Notification&nbsp:&nbsp;<span id="errorMessage"></span></div>
            <br/>

            <div  style="display:none;">Selected&nbspnodes:&nbsp;<br/><span id="selectedNodes"></span></div>
            <br/>
            <p style="height: 180px;" />

            <div style="display:none;" > Container: <!-- <input maxlength="1500" size="40" name="multiSelection" id="multiSelectionTxtBx" type="text"
                               multiple=""  >   -->
                            <textarea rows="4" cols="40" id="multiSelectionTxtBx" name="multiSelection" ></textarea><br/>
                <!-- Action: <select name="actionTypesLst">
                    <option>LOG entry</option>
                    <option>SMS notification</option>
                    <option>E-mail notification</option>
                    <option>Trigger Custom Action</option>
                </select> --><br/>
                <input class="btn btn-small" value="Remove Container Selection from Service" name="removeSelFromVSNBtn" type="button" onclick="removeSelectionFromTextAreaForService();"><br />
                <input class="btn btn-small" value="Fetch Sensor Container Selection" name="fetchSensorDataBtn" type="button" onclick="fetchSensorDataFromTextAreaForService();"><br />
                <input class="btn btn-small" value="Force Complete Sensor Check" name="fetchAllSensorDataBtn" type="button" onclick="fetchAllSensorDataforService();"><br />
                <input class="btn btn-small" value="Reset" name="resetSelectionBtn" type="button" onclick="resetSelectionBtnClicked();"><br/>

            </div>
</div>
</div>

<div class="row-fluid"><!--  Compose service from sub-services gridviews --->
    <div class="span10 well reborder">
        <div id="subServicesDiv" class="container-fluid">
            <span id="spanSetsOfSelectionsHD" style="display: none" ></span>
            <input type="hidden" id="autoIncForSubServiceParts" value="0" />
			<table id="subServicesTbl" class="table table-bordered table-striped">
				<thead>  
					<tr>
						<td colspan="5">
							<legend style="text-decoration: underline; font-weight: bold;">Partial Service<input type="button" class="btn btn-small  pull-right" id="bedataPartialServiceComposition" value="Add to composition..." onclick='javascript:addSubServicePartialToComposite();' />
								<input type="button" class="btn btn-small  pull-right" id="clearPartialServiceComposition" value="Clear" onclick='javascript:removeAllSubServicesParts();resetAllSubServiceInsFields();' />	
							</legend>							 
						</td>
					</tr>
					<tr class="legend">
						<td><label for="subServiceNodeSelectionIns">Nodes</label></td>
						<td><label for="subServiceCapSelectionIns">Capability</label></td>
 						<td><label for="subServiceThreshIns" style="white-space:nowrap;">Threshold</label></td>
						<td style="display: none;"><label for="subServiceFreqIns" style="display: none;">Frequency (hidden)</label></td>
						<td><label class="pull-right">Action</label></td>
					</tr>
				</thead>  
				<tbody>
					<tr>
						<td>
						<select name="subServiceNodeSelectionIns" id="subServiceNodeSelectionIns" class="span10">
                            <option value="">[Please select]</option>
                            <option value="Last">Node selection (Map)</option>

                            <option value="AVG">Average on Node selection (Map)</option>
                            <option value="MAX">Max on selection (Map)</option>
                            <option value="MIN">Min selection (Map)</option>
                        </select>
						<input type="hidden" id="activeMapSelectionHD" name="activeMapSelection" value="" />
                        <span class="add-on"><a href="javascript:void(0);" class="btn btn-mini btn-info pull-right" onclick="getSelectionInHiddenField('alert');" >Show...</a></span>
						</td>
						<td>
                            <% if(supportedCapabilities==null || supportedCapabilities.isEmpty()) { %>
                            No capabilities detected
                            <% } else { %>
                            <select name="subServiceCapSelectionIns" id="subServiceCapSelectionIns">
                                <option value="">[Please select]</option>
                                <% for(int i=0; i< supportedCapabilities.size(); i++) { %>
                                <option value="<%=staticprefixCapability %><%=supportedCapabilities.get(i)%>"> <%=supportedCapabilitiesUINames.get(i)%> </option>
                                <% } %>
                            </select>
                            <%}
                            %>

						</td>
						<td >
 						<input class="input-large span12" type="text" id="subServiceThreshIns" name="subServiceThreshIns" />
						</td>
						<td style="display: none;">
						<input class="input-small" type="hidden" id="subServiceFreqIns" name="subServiceFreqIns" />					
						</td>
						<td>
						<a class="btn btn-small pull-right" onclick="javascript:getSelectionInHiddenField();addNewSubServicePart();" href="javascript:void(0);">Add...</a>
						</td>
					</tr>
					
				</tbody>
			</table>
        </div>
        <br/>
        <div id="definedSubServicesDiv" class="container-fluid">
            <input type="hidden" id="autoIncForDefinedSubServices" value="0" />
            <table id="definedSubServicesTbl" class="table table-bordered table-striped">
                <thead>
                <tr>
                    <td colspan="6">
						<legend style="text-decoration: underline; font-weight: bold;">Service Composition
						<input type="button" class="btn btn-small pull-right" id="clearServiceComposition" value="Clear" onclick="javascript:removeAllSubServicesFromComposite();">
						</legend>					
					</td>
                </tr>
                <tr bgcolor="#BFDAEF">
                    <td>Partial Service ID</td>
                    <td>Nodes</td>
                    <td>Capability</td>
                    <td>Threshold</td>
					<td style="display: none;">Frequency</td>
                    <td>Action</td>
                </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
    <div class="span2 well reborder">
        <label>Service Name:</label>
		<input class="input-small span12" type="text" id="compositeServiceName" name="compositeServiceName" />
        <br/>
        <label>Service Tags:</label>
        <input class="input-small span12" type="text" id="tagsCSVtxtbx" name="tagsCSVtxtbx" />
        <br/>
		<input name="selectDTN" type="checkbox" class="span2"/>
        <label class="span10">Allow DTN<br/>(delayed response)</label>
        <label>&nbsp;</label>
        <label class="span10" style="display:none !important;">Continuation</label>
        <input name="selectCont" type="checkbox" class="span2" style="display:none !important;" />
        <input type="submit" class="btn btn-small span12" name="VSNDefinitionSubmit" value="Create new service" onclick="return validateNewServiceBeforeSubmit();" />
    </div>
</div>
</div>
    <input value="<%=Integer.toString(totalNodes) %>" name="hdTotalMapNodes" id="hdTotalMapNodes"  type="hidden" ><br />
    <input value="<%=Integer.toString(totalNodes - ignoredNodesWithNoCoordInfo) %>" name="hdDisplayedMapNodes" id="hdDisplayedMapNodes"  type="hidden" ><br />
    <input value="<%=Integer.toString(ignoredNodesWithNoCoordInfo) %>" name="hdIgnoredMapNodes" id="hdIgnoredMapNodes"  type="hidden" ><br />
</div>
  </div>         <!-- external row-fluid -->
    </div> <!-- external container fluid -->
   <!-- begin the footer for the application -->
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->

  <%
    }
    catch (Exception e)
    {
        System.out.println(e.getMessage());
    }
%>
</form>
</body>
</html>


