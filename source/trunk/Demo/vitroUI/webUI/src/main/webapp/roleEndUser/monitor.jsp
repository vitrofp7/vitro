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
<%@page import='java.lang.*' %>
<%@ page import="vitro.vspEngine.logic.model.Capability" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>

    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Service Monitoring</title>
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
		$('#dashboardMonitorButton').addClass("active");
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
    <script type="text/javascript"
	src="<%=request.getContextPath()%>/js/dygraph-combined.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/geoxml3.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/ProjectedOverlay.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/dbAlertsRetrieveInterfaceJS.jsp"></script>
	<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/alarmMonitoringJS.jsp"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/noty/jquery.noty.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/noty/layouts/top.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/noty/layouts/center.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/noty/themes/default.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/dbDataRetrieveInterfaceJS.jsp"></script>

    
    
    
    
    <script type="text/javascript">
    

    function resetViewForNewService()
    {
      //  runBGTask  = false;
        $.noty.clearQueue();
        $.noty.closeAll();
        removeAllRules();
    }


    //
    // TODO: logic for removing rules. Removes a row from the table.
    //
    function removeAllRules()
    {
        var alertsNotificationsTbl=document.getElementById("alertsNotificationsTbl");
        if(alertsNotificationsTbl!=null)
        {
            try {
                while(alertsNotificationsTbl.rows.length>1)
                {
                    rowIndex = alertsNotificationsTbl.rows.length - 1;
                    alertsNotificationsTbl.deleteRow(rowIndex);
                }
                if(alertsNotificationsTbl.rows.length <= 1) {
                    document.getElementById('alertsAndNotifyDiv').style.display = 'none';
                }
            }
            catch(e)
            {
                alert(e);
            }
        }
    }
    
    function answer_callback_alarm(answeral,parametrsCSVal)
    {
    	var msg = "";
    	var tmp;
    	var alarms =  answeral.toString().split(/\n/);
    	//console.log("answer length is "+answeral.length);
    	if (answeral.length==0)
    		isSomethingAl=false;
    	else
    		isSomethingAl=true;
    		
    		if (isSomethingAl)
    		{
                var internalDateRef = defDate;
                var internalLastNotifyIdPrinted =lastNotifyIdPrinted;
                var maxInternalLastNotifyIdPrinted = -1;
                var currCapId  = "-1";
                var currPartServId = "-1";
                var currNotifyType = "-1";
                var tmpPartServId = "";
                var tmpNotifyType = "";
                for (var i = 0; i < alarms.length; i++) {

                    tmp = alarms[i].toString().split(',');

                    var parsedNotifyDBId = -2;
                    try{
                        //alert(   document.getElementById('subServiceThreshIns').value);
                        parsedNotifyDBId = parseInt(tmp[1]);
                    }
                    catch(e)
                    {
                        console.log('ERROR: invalid alert id: '+ tmp[1]);
                    }
                    if(parsedNotifyDBId <= maxInternalLastNotifyIdPrinted) {
                         continue;
                    }

                    parameters[3] = tmp[3]; //capId
                    tmpPartServId = tmp[2]; //partServId
                    tmpNotifyType = tmp[13];  //notifyType
                    if(parameters[3].toString()!=currCapId.toString() || tmpPartServId.toString()!=currPartServId.toString() || tmpNotifyType.toString()!=currNotifyType.toString()) {
                       // console.log('[][]][]][][[ CURR CAP ID = ' + currCapId.toString() + 'param 3' + parameters[3].toString());
                       // console.log('[][]][]][][[ internadef date = ' + internalDateRef);
                       // console.log('[][]]][]][ Gloabl def date = ' + defDate);
                        currCapId = parameters[3];
                        currPartServId = tmpPartServId;
                        currNotifyType = tmpNotifyType;
                        internalDateRef = defDate;
                        internalLastNotifyIdPrinted = lastNotifyIdPrinted;
                    }

                    if(dates.compare(defDate,"1970/01/01")==0)  {
                        defDate = tmp[12];
                        console.log('ALALLAALLA defDate = ' + defDate);
                    }
                    //if (dates.compare(internalDateRef,tmp[12])<0)

                        //console.log('Parse Db id !!::'+ parsedNotifyDBId);
                    //console.log('Ref Db id !!::'+ internalLastNotifyIdPrinted);

                    if (internalLastNotifyIdPrinted <parsedNotifyDBId)
                    {

                        internalDateRef = tmp[12];
                        internalLastNotifyIdPrinted = parsedNotifyDBId;
                        if(maxInternalLastNotifyIdPrinted <internalLastNotifyIdPrinted )   {
                            maxInternalLastNotifyIdPrinted = internalLastNotifyIdPrinted;
                        }
                        //console.log('[][FOR LOOP [[ internadef date = ' + internalDateRef);
                        //console.log('[][FOR LOOP [[ internadef date = ' + tmp[12]);

                        parameters[0]=tmp[9]; //gID
                        parameters[1] = tmp[0]; //serviceID
                        parameters[2] = tmp[10]; //sensId

                        parameters[4] = tmp[4]; //capName
                        parameters[5] = tmp[11]; // Value
                        parameters[6] = tmp[14]; //Message
                        parameters[7] = tmp[5]; //Func Name
                        addNewAlarmNotify('Alarm ',internalDateRef, tmp[14],tmp[13]);
                    }
                }
                defDate= internalDateRef;
                lastNotifyIdPrinted = maxInternalLastNotifyIdPrinted;
                incrementCalls(sID);
    	    }
    	//addNewAlarmNotify('Alarm ',defDate, tmp[4],tmp[13]);
    	//console.log(sensordata);
    }
    
    
    
    function showAlertDetails(ruleId, title, time, msg, alType)
    {
        if(alType == 1)
        {
            var alert = generate('alert');
            $.noty.setText(alert.options.id, title + ' ('+time+') <br />' +msg);
        }
        else {
            var notification = generate('notification');
            $.noty.setText(notification.options.id, title + ' ('+time+') <br />' +msg);
        }
    }
    
    function addNewAlarmNotify(pAlertTitle, pAlertTime, pAlertDetails, pAlertType )
    {

        var sIdx = 0;

        // 2. add a row to the notifications table
        //console.log("In function I have "+ pAlertTitle+" "+pAlertTime+" "+pAlertDetails+" "+pAlertType);
        var alertsNotificationsTbl=document.getElementById("alertsNotificationsTbl");
        //var rowCount = table.rows.length;
        //update the autoinc.
        var nextIdx = parseInt(document.getElementById("autoIncForAlertsNotifications").value)  + 1;
        document.getElementById("autoIncForAlertsNotifications").value = nextIdx;
        var parsedAlertType = -1;
        try{
            //alert(   document.getElementById('subServiceThreshIns').value);
            parsedAlertType = parseInt(pAlertType);
        }
        catch(e)
        {
            console.log('ERROR: invalid alert type: '+ pAlertType);
        }
        if(parsedAlertType == 3 || parsedAlertType ==1 || parsedAlertType ==4)
        {
            //var row=alertsNotificationsTbl.insertRow(-1); // add at the end
            var row=alertsNotificationsTbl.insertRow(1);
            var cellMsg=row.insertCell(0);
            var cellTime=row.insertCell(1);
            var cellDetails=row.insertCell(2);
            var cellAddRem=row.insertCell(3);

            var alertMsgVal = '';
            var alertMsgDisp = '';
            var alertTimeVal = '';
            var alertTimeDisp = pAlertTime;
            var alertDetailsDisp = pAlertDetails;

            if(parsedAlertType == 3)
            {
                row.style.background="#a4e6a3";
                alertMsgDisp = pAlertTitle + ' ID: ' + nextIdx + ' raised';
             //if (isYourFirstTime(sID)==0)
             //   showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, pAlertDetails, pAlertType);
                cellMsg.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"alertMsg[]\" id=\"alertMsg_"+ nextIdx +"\" value=\""+alertMsgVal+"\" />"+ alertMsgDisp;
                cellTime.innerHTML= "<input type=\"hidden\" name=\"alertTime[]\" id=\"alertTime_"+ nextIdx +"\"  value=\""+alertTimeVal+"\" />"+  alertTimeDisp;  //"New:: " + nextIdx;

                cellDetails.innerHTML="<a href=alarm.jsp?servId="+parameters[1]+"&gateId="+encodeURIComponent(parameters[0])+"&sensName="+encodeURIComponent(parameters[2])+"&capId="+parameters[3]+" ><img src=\"<%=request.getContextPath()%>/img/demo/demoNotifyDetails32.png\" title=\"Details\" style=\"height: 32px;\" /></a>";

                //TODO there should be an alert/confirmation for deleting
                cellAddRem.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeRule(\"uniqRuleId_"+nextIdx+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
                if(document.getElementById('alertsAndNotifyDiv').style.display == 'none')
                {
                    document.getElementById('alertsAndNotifyDiv').style.display = 'block';
                }

            }
            if(parsedAlertType == 1)
            {
                row.style.background="#ffe6a3";
                alertMsgDisp = 'Node '+parameters[2]+' has a parent Node '+parameters[6]+' with PFI value ' + parameters[5];
                //if (isYourFirstTime(sID)==0)
               // showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, pAlertDetails, pAlertType);
                cellMsg.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"alertMsg[]\" id=\"alertMsg_"+ nextIdx +"\" value=\""+alertMsgVal+"\" />"+ alertMsgDisp;
                cellTime.innerHTML= "<input type=\"hidden\" name=\"alertTime[]\" id=\"alertTime_"+ nextIdx +"\"  value=\""+alertTimeVal+"\" />"+  alertTimeDisp;  //"New:: " + nextIdx;

                cellDetails.innerHTML="<a href=alarm.jsp?servId="+parameters[1]+"&gateId="+encodeURIComponent(parameters[0])+"&sensName="+encodeURIComponent(parameters[2])+"&capId="+parameters[3]+" ><img src=\"<%=request.getContextPath()%>/img/demo/demoNotifyDetails32.png\" title=\"Details\" style=\"height: 32px;\" /></a>";

                //TODO there should be an alert/confirmation for deleting
                cellAddRem.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeRule(\"uniqRuleId_"+nextIdx+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
                if(document.getElementById('alertsAndNotifyDiv').style.display == 'none')
                {
                    document.getElementById('alertsAndNotifyDiv').style.display = 'block';
                }

            }
            if(parsedAlertType == 4)
            {
                row.style.background="#a4f6ff";
                alertMsgDisp = pAlertTitle + ' ID: ' + nextIdx + ' (all) raised';
                //if (isYourFirstTime(sID)==0)
                //   showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, pAlertDetails, pAlertType);
                cellMsg.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"alertMsg[]\" id=\"alertMsg_"+ nextIdx +"\" value=\""+alertMsgVal+"\" />"+ alertDetailsDisp;
                cellTime.innerHTML= "<input type=\"hidden\" name=\"alertTime[]\" id=\"alertTime_"+ nextIdx +"\"  value=\""+alertTimeVal+"\" />"+  alertTimeDisp;  //"New:: " + nextIdx;

                cellDetails.innerHTML="<a href=alarm.jsp?servId="+parameters[1]+"&gateId="+encodeURIComponent(parameters[0])+"&sensName="+encodeURIComponent(parameters[2])+"&capId="+parameters[3]+" ><img src=\"<%=request.getContextPath()%>/img/demo/demoNotifyDetails32.png\" title=\"Details\" style=\"height: 32px;\" /></a>";

                //TODO there should be an alert/confirmation for deleting
                cellAddRem.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeRule(\"uniqRuleId_"+nextIdx+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
                if(document.getElementById('alertsAndNotifyDiv').style.display == 'none')
                {
                    document.getElementById('alertsAndNotifyDiv').style.display = 'block';
                }
            }
        }
    }
    
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
    
    
    // dupicate function
    //function answer_callback_SensorList(answersl,parametrsCSVsl)
    //{
    //var ids;
    //    involvedSensorsComplete = answersl;
    //    quickRedrawMarkersInSensorList();

    //for(var i=0; i<involvedSensorsComplete.length-1; i++) {
//    	ids = involvedSensorsComplete[i].split(/[\s","]+/);
    //	
//    	involvedSensorsIDs[i][1] = ids[1];

    //}

    //}
    
    

    
    
var parameters = [];    
var data = [];
var involvedSensorsComplete = [];
var involvedServiceComplete = [];
var involvedCComplete = [];
var sensorCapabilities = [];
var involvedSensorsIDs = [];
var involvedSensorsCapabilities = [];
var involvedSensorsCapabilitiesName = [];
var involvedCapabilitiesComplete = [];
var sensorMonitored = "";
var sensordata = [];
var sID;
var gID;
var sensID;
var defDate;
var lastNotifyIdPrinted;
var lastDate;
var isSomethingAl;
var isSomethingData;
var timerData = 0;
var timerAlarm = 0;
var isServiceFirstLoaded = [];
var sensInvId = [];
var kmlData;
var chosenCapability;
var chosenCapabilityIndex;
var myParser;
var title ='sensor reading';
var kmlFile;
var loadBeginning = "block";



function toggle2(showHideDiv, switchTextDiv) {
var ele = document.getElementById(showHideDiv);
var text = document.getElementById(switchTextDiv);
var capList = document.getElementById("capability-list");
var legList = document.getElementById("legList");
if(loadBeginning == "block") {
		ele.style.display = "none";
	loadBeginning = "none";		
	text.innerHTML = "";
	capList.style.display = "none";	
	legList.style.display = "none";	
	

	}
else {
	loadBeginning = "block";
	ele.style.display = "block";
	text.innerHTML = "close";
	capList.style.display = "block";
	legList.style.display = "block";
}
}

function isYourFirstTime(serId)
{
	for (i = 0; i < sensInvId.length; i++) {
		if (sensInvId[i]==serId)
			{
			if (isServiceFirstLoaded[i]>0)
			{return 0;
			break;
			}
			else
			{return 1;
			break;
			}
			}
	}
	}
	
	function incrementCalls(serId)
	{
		for (i = 0; i < sensInvId.length; i++) {
			if (sensInvId[i]==serId)
				{
				isServiceFirstLoaded[i]++;
				break;
				}
				}
		}
		

function answer_callback_ServiceList(answerserl,parametrsCSVsl)
{
		
    //var ids;
   // console.log('DEBUG:: Reached callback function for service List '+answerserl);
    involvedServiceComplete = answerserl;
    var isclocal = involvedServiceComplete;
	idserv = isclocal.toString().split(/\n/);
    var out = '';
    var serv = [];
    var servId = [];
    var tmp;

    var count = 0;

    
    for (i = 0; i < idserv.length; i++) {
	  
	    tmp = idserv[i].toString().split(',');

	    if (tmp[tmp.length-1].toString()==1)
		{
            isServiceFirstLoaded[count] = 0;
            sensInvId[count] = tmp[0];
            count++;
		}
    }

    out += '<option value="">Please select a service</option>';
    for (i = 0; i < idserv.length; i++) {

        tmp = idserv[i].toString().split(',');
        serv[i] = tmp[2];
        servId[i] = tmp[0];
       
        if (tmp[tmp.length-1].toString()==1)
        //  out += '<option value='+invsenscap[i]+'|'+name[i]+' >' + name[i] + '</option>';
            out += '<option value="'+servId[i]+'|'+serv[i]+'" >' + serv[i] + '</option>';
    
    }
    document.querySelector( '#myServiceSelect' ).innerHTML = out;
    javascript:togglelist('service-list');

        
    
}

function togglelist(switchTextDiv) {
	var text = document.getElementById(switchTextDiv);
	if(text.style.display == "none") {

		text.style.display = "block";
	}
}	


function load()
{
javascript:toggle2('graph','myHeader');
var sensListLength;
getComposedServiceList(answer_callback_ServiceList);




}
    
function compareSensId(invSensComplete,sensorName)
{
    var replyValRows =  invSensComplete.toString().split(/\n/);
    if (replyValRows.length==0)
    {
        //console.log("No rows in reply (sensorList info)!");
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
            var rtSensNodeId = tmpArr[1];
            var rtIsInMem = tmpArr[2];
            var rtIsEnabled = tmpArr[3];
            var rtLongitudOptional = "";
            var rtLatitudeOptional = "";
            if(tmpArr.length > 4) {
                rtLongitudOptional = tmpArr[4];
                rtLatitudeOptional = tmpArr[5];
            }
           // console.log("ids is "+rtSensNodeId);
            if(rtSensNodeId.toLowerCase() == sensorName.toLowerCase())
            {
                gID= rtGwId;
                sensID=rtSensNodeId;
                break;
            }
        }
    }
}


function showGraph(capId)
{
    var tmp;

    var graph = document.getElementById("graph");

    if(graph.style.display == "block") {

        graph.style.display = "none";
    }

    tmp = capId.split(/\|/); // new changed from   (/[\s|]+/);

    chosenCapability = tmp[1];
    chosenCapabilityIndex = tmp[0];
    //alert ("Retrieving data for "+tmp[1]+ " "+gID+" "+sensID+" "+sID);
    getDataCapabilityForComposedService(chosenCapabilityIndex,gID,sensID,sID,answer_callback_DataFile);

    timerData = intervalTriggerSensors();


}


function answer_callback_SensorList(answersl,parametrsCSVsl)
{
    //var ids;
   // console.log('DEBUG:: Reached callback function for sensor List');
    involvedSensorsComplete = answersl;
    cleanMapAndFillWithNodesOfCap(globalCapabilitytoViewInGMs);
    //for(var i=0; i<involvedSensorsComplete.length-1; i++) {
    //	ids = involvedSensorsComplete[i].split(/[\s","]+/);
    //
    //	involvedSensorsIDs[i][1] = ids[1];
    if(answersl == null || typeof answersl === "undefined" || parametrsCSVsl == null ||  typeof parametrsCSVsl === "undefined")
    {
        return;
    }

    parArr = parametrsCSVsl.toString().split(',');
    if(parArr ==null || parArr[0]=="")
    {
       // console.log("Ajax reply came but did not have gw info!");
        return;
    }
    var gwId = parArr[0];
    var replyValRows =  answersl.toString().split(/\n/);
    //console.log("answer length is "+answeral.length);
    if (replyValRows.length==0)
    {
       // console.log("No rows in reply (sensorList info)!");
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
            var rtSensNodeId = tmpArr[1];
            var rtIsInMem = tmpArr[2];
            var rtIsEnabled = tmpArr[3];
            if(rtIsEnabled=='1' ){
                setMarkerNodeEnabledIcon(rtGwId, rtSensNodeId, true);
            }  else {
                setMarkerNodeEnabledIcon(rtGwId, rtSensNodeId, false);
            }
            var rtLongitudOptional = "";
            var rtLatitudeOptional = "";
            if(tmpArr.length > 4) {
                rtLongitudOptional = tmpArr[4];
                rtLatitudeOptional = tmpArr[5];
                var sourceNodeLatlng = null;
                if(tmpArr[4]!="" && tmpArr[5]!="") {
                    sourceNodeLatlng  = new google.maps.LatLng(rtLatitudeOptional, rtLongitudOptional);
                }
                if(tmpArr.length > 6) {
                    var uniqRepNodeIds = [];
                    var rtRepNodeIds = [];
                    var rtRepNodeCapId = [];
                    var rtRepNodesFullCap = [];
                    var rtRepNodesCapIcon = [];
                    var rtRepNodeLongit = [];
                    var rtRepNodeLatit = [];


                    for(var j1 = 6; j1 <tmpArr.length ;j1+=6) {

                        //skip if empty
                        if(tmpArr[j1] == "") {
                            continue;
                        }

                        rtRepNodeIds.push(tmpArr[j1]);
                        var foundInUniqVec = false;
                        for(j2 = 0; j2 < uniqRepNodeIds.length; j2 ++) {
                            if(uniqRepNodeIds[j2] ==tmpArr[j1] ) {
                                foundInUniqVec=true;
                                break;
                            }

                        }
                        if(!foundInUniqVec) {
                            uniqRepNodeIds.push(tmpArr[j1]);
                        }
                        rtRepNodeCapId.push(tmpArr[j1+1]);
                        rtRepNodesFullCap.push(tmpArr[j1+2]);
                        rtRepNodesCapIcon.push(tmpArr[j1+3]);
                        rtRepNodeLongit.push(tmpArr[j1+4]);
                        rtRepNodeLatit.push(tmpArr[j1+5]);
                    }

                    //since a replcment node can offer multiple capabilities, we do a second pass and group them

                    for(var gk = 0; gk<uniqRepNodeIds.length; gk++) {
                        var capsIdsForRepNode= [];
                        var capsFullNamesForRepNode = [];
                        var capsIconsForRepNode = [];
                        var longForRepNode = 0.0;
                        var latForRepNode = 0.0;

                        for(var dk = 0; dk<rtRepNodeIds.length; dk++) {
                            if(uniqRepNodeIds[gk] == rtRepNodeIds[dk]) {
                                capsIdsForRepNode.push(rtRepNodeCapId[dk]);
                                capsFullNamesForRepNode.push(rtRepNodesFullCap[dk]);
                                capsIconsForRepNode.push(rtRepNodesCapIcon[dk]);
                                longForRepNode = rtRepNodeLongit[dk];  //we don't mind overwriting it since it would be the same value
                                latForRepNode = rtRepNodeLatit[dk];
                            }

                            // and put the marker finally
                            var capsDispTableIcons = '';
                            capsDispTableIcons ='<table border=\"0\"><tr><td colspan=\"2\"><strong>Covers node '+rtSensNodeId+ ' for Capabilities</strong></td></tr>';

                            for(var j1 = 0; j1 <capsIdsForRepNode.length ;j1++) {
                                var friendNameCap = getCapabilityFriendlyName(capsFullNamesForRepNode[j1]);
                                capsDispTableIcons += '<tr><td>';
                                capsDispTableIcons +=friendNameCap; // friendly name;
                                capsDispTableIcons +='</td><td><img src=\"<%=request.getContextPath() %>/img/'+ capsIconsForRepNode[j1] +'\" style=\"height: 16px;width: 16px;\" />';
                                capsDispTableIcons +='</td></tr>';
                            }
                            capsDispTableIcons += '</table>';
                            // ++++++++++++++++++++++++++++++++++++++++++
                            //console.log('caps arr init: ' + capsArrInit);
                            //console.log('disp table:' + capsDispTableIcons);

                            var myLatlng = null;
                            if(latForRepNode!="" && longForRepNode!="") {
                                myLatlng = new google.maps.LatLng(latForRepNode, longForRepNode);
                                var contentString = '<p><b>Helper Node '+ uniqRepNodeIds[gk]+' ('+rtGwId+') </b><br />'+
                                        capsDispTableIcons +'<br />' +
                                        '</p>';
                                //console.log('content string: ' + contentString);
                                putReplcmntMarkerWithInfo(myLatlng, contentString, uniqRepNodeIds[gk], rtGwId);
                                //console.log('After put marker info');

                                // and add a polyline to the source node
                                if(sourceNodeLatlng !=null && myLatlng!=null) {
                                    addReplcmentPolyline(myLatlng, sourceNodeLatlng );
                                }
                            }
                            else{
                                console.log("No coord info for replacement node. Can't place a marker!")
                            }
                        }
                    }
                }
            }
        }
    }
}   // for changing the icon for disabled node:
    function setMarkerNodeEnabledIcon(pGwid,pNodeId, pEnabled) {
        var theSpecifiedMarker =  null;
        if(AllMapItems.markers !=null && AllMapItems.markers.length > 0) {
            for(var i=0, marker; marker = AllMapItems.markers[i]; i++) {
                if(AllMapItems.markerGWOfNodesIds[i]==pGwid &&
                        AllMapItems.markerNodeIds[i] == pNodeId) {
                    //foundmarker
                    theSpecifiedMarker = AllMapItems.markers[i];
                    break;
                }
            }
            if(theSpecifiedMarker!=null) {
                if(pEnabled == true) {
                    drawDefaultUnselectedIcon(theSpecifiedMarker,'allCaps');
                }else {
                    drawDisabledIcon(theSpecifiedMarker, 'allCaps');
                }
            }
        }
    }

    //for continuation markers
    function putReplcmntMarkerWithInfo(myLatlng, contentString, nodeId, gateId) {
        // Add markers to the map

        // Marker sizes are expressed as a Size of X,Y
        // where the origin of the image (0,0) is located
        // in the top left of the image.

        // Origins, anchor positions and coordinates of the marker
        // increase in the X direction to the right and in
        // the Y direction down.
        var image = new google.maps.MarkerImage(mapCapToIcon32('replcNode'),
                // This marker is 20 pixels wide by 32 pixels tall.
                new google.maps.Size(32, 32),
                // The origin for this image is 0,0.
                new google.maps.Point(0, 0),
                // The anchor for this image is the base of the flagpole at 0,32.
                new google.maps.Point(0, 32));
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

        var idxMarker = AllMapItems.continuationMarkers.push(marker) - 1;
        AllMapItems.continuationMarkerNodeIds.push(nodeId);
        AllMapItems.continuationMarkersDescription.push(contentString);
        AllMapItems.continuationGwOfNodeIds.push(gateId);
            //AllMapItems.markersAddress.push("undefined");
            // var point = new google.maps.LatLng(southWest.lat() + latSpan * Math.random(), southWest.lng() + lngSpan * Math.random());
            // for selection with polygons (poin needs to be LatLng point   TODO: should be merged with marker!
            //var idxMarker = MarkerSelection.allnodeMarkers.push(marker) - 1;

        google.maps.event.addListener(marker, 'click', function() {
            update_timeout_for_Dbl_Vs_SingleClick = setTimeout(function() {
                onContinuationMarkerClick(idxMarker);
            }, 200);
        });

        google.maps.event.addListener(marker, 'dblclick', function(event) {
            clearTimeout(update_timeout_for_Dbl_Vs_SingleClick);
            onContinuationMarkerDblClick(idxMarker);
        });

        marker.setMap(AllMapItems.map);

    }

    function addReplcmentPolyline(myLatlng, sourceNodeLatlng ){
        var rayCoords = [
            myLatlng,
            sourceNodeLatlng
        ];
        var rayPath = new google.maps.Polyline({
            path: rayCoords,
            strokeColor: '#FF0000',
            strokeOpacity: 1.0,
            strokeWeight: 2
        });

        AllMapItems.continuationPolylines.push(rayPath);
        rayPath.setMap(AllMapItems.map);
    }

    function plot_graph(values) {
    var name;
	name = getCapabilityFriendlyName(chosenCapability);
	//console.log("Name is "+name);
	var graph = document.getElementById("graph");
    var width = parseInt(graph.style.width);
	if(graph.style.display == "none") {

		graph.style.display = "block";
	}
       	data=values;

     //   var labels = ['X'];
        
     //     labels.push('Y');
                g = new Dygraph(graph, data,
            {
                title: name,
                xlabel: "Date",
                ylabel: name,
                labels: ["Date",name],
                legend: 'always',
                labelsDivStyles: { 'textAlign': 'right' },
                showRangeSelector: true
            });
      }
	

function answer_callback_CapabilitiesList(answercl,parametrsCSVcl)
{
	var graph = document.getElementById("graph");
	if(graph.style.display == "block") {

		graph.style.display = "none";
	}
//console.log("answer is "+answercl);
involvedCapabilitiesComplete = answercl.split(/[\s" "]+/);
//alert("involvedCapabilitiesComplete "+involvedCapabilitiesComplete);
var cap;
involvedSensorsCapabilities = [];
involvedSensorsCapabilitiesName = [];
	//alert("involvedCapabilitiesComplete.length "+involvedCapabilitiesComplete.length); 
	for(var i=0; i<involvedCapabilitiesComplete.length; i++) {
	cap = involvedCapabilitiesComplete[i].split(/[\s","]+/);
	//alert("cap1 e cap2 "+cap[1]+" "+cap[2]);
	involvedSensorsCapabilities[i]=cap[1];
	involvedSensorsCapabilitiesName[i]=cap[2];
	//alert("involvedCapabilitiesComplete.length "+involvedCapabilitiesComplete.length); 
//	console.log("PLacemark "+this.myname);

}
showcapabilities(involvedSensorsCapabilities,involvedSensorsCapabilitiesName);
}

function answer_callback_DataFile(answerdf,parametrsCSVdf)
{
sensordata=answerdf;
//console.log(sensordata);

if (answerdf.length==0)
    		isSomethingData=false;
    	else
    		isSomethingData=true;

if (isSomethingData)
plot_graph(sensordata);
}







var dates = {
	    convert:function(d) {
	        // Converts the date in d to a date-object. The input can be:
	        //   a date object: returned without modification
	        //  an array      : Interpreted as [year,month,day]. NOTE: month is 0-11.
	        //   a number     : Interpreted as number of milliseconds
	        //                  since 1 Jan 1970 (a timestamp) 
	        //   a string     : Any format supported by the javascript engine, like
	        //                  "YYYY/MM/DD", "MM/DD/YYYY", "Jan 31 2009" etc.
	        //  an object     : Interpreted as an object with year, month and date
	        //                  attributes.  **NOTE** month is 0-11.
	        return (
	            d.constructor === Date ? d :
	            d.constructor === Array ? new Date(d[0],d[1],d[2]) :
	            d.constructor === Number ? new Date(d) :
	            d.constructor === String ? new Date(d) :
	            typeof d === "object" ? new Date(d.year,d.month,d.date) :
	            NaN
	        );
	    },
	    compare:function(a,b) {
	        // Compare two dates (could be of any type supported by the convert
	        // function above) and returns:
	        //  -1 : if a < b
	        //   0 : if a = b
	        //   1 : if a > b
	        // NaN : if a or b is an illegal date
	        // NOTE: The code inside isFinite does an assignment (=).
	        return (
	            isFinite(a=this.convert(a).valueOf()) &&
	            isFinite(b=this.convert(b).valueOf()) ?
	            (a>b)-(a<b) :
	            NaN
	        );
	    },
	    inRange:function(d,start,end) {
	        // Checks if date in d is between dates in start and end.
	        // Returns a boolean or NaN:
	        //    true  : if d is between start and end (inclusive)
	        //    false : if d is before start or after end
	        //    NaN   : if one or more of the dates is illegal.
	        // NOTE: The code inside isFinite does an assignment (=).
	       return (
	            isFinite(d=this.convert(d).valueOf()) &&
	            isFinite(start=this.convert(start).valueOf()) &&
	            isFinite(end=this.convert(end).valueOf()) ?
	            start <= d && d <= end :
	            NaN
	        );
	    }
	}



function showcapabilities(invsenscap,invsenscapName){
    var out = '';
	var name = [];
	out += '<option value="">[Please select]</option>';
    for (i = 0; i < invsenscap.length; i++) {
	name[i]= getCapabilityFriendlyName(invsenscapName[i]);
      out += '<option value='+invsenscap[i]+'|'+name[i]+' >' + name[i] + '</option>';
    }
    document.querySelector( '#mySelect' ).innerHTML = out;
  }
    
    
<%   String defaultCapability = Capability.PHENOMENOM_TEMPERATURE;
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

var defaultCapability = '<%=defaultCapability %>';//startupCapability;
var globalCapabilitytoViewInGMs = defaultCapability;
MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;

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
    continuationMarkers: [],
    continuationMarkerNodeIds: [],
    continuationMarkersDescription: [],
    continuationPolylines: [],
    continuationGwOfNodeIds: [],
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

var onContinuationMarkerClick = function(idxContMarker){
    var graph = document.getElementById("graph");

    if(graph.style.display == "block") {

        graph.style.display = "none";
    }
    var capList = document.getElementById("capability-list");
    if (capList.style.display == "block")
    //javascript:toggle2('graph','myHeader');
    {
        var ele = document.getElementById("graph");
        var text = document.getElementById("myHeader");
        var legList = document.getElementById("legList");
        ele.style.display = "none";
        text.innerHTML = "";
        capList.style.display = "none";
        legList.style.display = "none";
    }

    var curr_marker = AllMapItems.continuationMarkers[idxContMarker]; // this;
    var latLng = curr_marker.getPosition();
    sensNameID = curr_marker.id;
    var contentToSet = createContentInfoHeader(AllMapItems.continuationMarkerNodeIds[idxContMarker]) + AllMapItems.continuationMarkersDescription[idxContMarker];
    contentToSet += createContentInfoFooter();
    infoWindow.setContent(contentToSet);
    infoWindow.open(AllMapItems.map, curr_marker);
}

var onContinuationMarkerDblClick = function(idxContMarker) {
    //alert('Double click!' + AllMapItems.markers[idxContMarker].getTitle()) ;
    var curr_marker = AllMapItems.continuationMarkers[idxContMarker];
    infoWindow.close();
    MarkerSelection.Clean(AllMapItems.map);
    AllMapItems.map.setZoom(19);
    AllMapItems.map.panTo(curr_marker.position);
}

var onMarkerClick = function(idxMarker) {
	var graph = document.getElementById("graph");
	
	if(graph.style.display == "block") {

		graph.style.display = "none";
	}	
    var curr_marker = AllMapItems.markers[idxMarker]; // this;
    var latLng = curr_marker.getPosition();
   // var ID = curr_marker.getId();
   sensNameID = curr_marker.id;
   
	compareSensId(involvedSensorsComplete,sensNameID);
	var answercl = "";
	var parametrsCSVcl = "";
	//console.log("gID sensID sID "+ gID + ", "+ sensID + ", "+ sID  );
	//alert("gID sensID sID "+ gID + ", "+ sensID + ", "+ sID);	
	getSensorCapabilityListForComposedService(sID,gID,sensID,answer_callback_CapabilitiesList);
	
	var isShown = document.getElementById("capability-list");
	if (isShown.style.display == "none")
		{
		
		var text = document.getElementById("myHeader");
		var capList = document.getElementById("capability-list");
		var legList = document.getElementById("legList");
			text.innerHTML = "close";
			capList.style.display = "block";
			legList.style.display = "block";
		//javascript:toggle2('graph','myHeader');

		}
    var foundMatch = false;
    for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
        if (curr_marker == itMarker) {
            if (AllMapItems.markersAddress[n] == "undefined") {
                geocodePosition(latLng, n);
            }
            setTimeout(function() {
                var contentToSet = createContentInfoHeader(AllMapItems.markerNodeIds[n]) + AllMapItems.markersDescription[n];

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

    AllMapItems.map.setZoom(19);
    AllMapItems.map.panTo(curr_marker.position);

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
    // remove all continuation markers and items

    if(AllMapItems.continuationMarkers!=null && AllMapItems.continuationMarkers.length > 0) {
         for (var n = 0, marker; marker = AllMapItems.continuationMarkers[n]; n++) {
                AllMapItems.continuationMarkers[n].setMap(null);
        }
        AllMapItems.continuationMarkers.length = 0;
        AllMapItems.continuationMarkerNodeIds.length = 0;
        AllMapItems.continuationMarkersDescription.length = 0;
        AllMapItems.continuationPolylines.length = 0;
        AllMapItems.continuationGwOfNodeIds.length = 0;
    }
    if(AllMapItems.continuationPolylines!=null && AllMapItems.continuationPolylines.length > 0) {
        for (var n = 0, contPolyline; contPolyline = AllMapItems.continuationPolylines[n]; n++) {
            AllMapItems.continuationPolylines[n].setMap(null);
        }
        AllMapItems.continuationPolylines.length =0;
    }
}


function cleanMapAndFillWithNodesOfCap(capability) {
    clearNotificationMsgField();
    infoWindow.close();
    globalCapabilitytoViewInGMs = capability;
    MarkerSelection.selectedCapability = globalCapabilitytoViewInGMs;
    AllMapItems.removeAllOverlays();
    AllMapItems.toggleMarkers(true, capability);
    //filter to show markers only of the ajax sensor list

    quickRedrawMarkersInSensorList();
    ////
    MarkerSelection.Display(AllMapItems.map);
//    mapSelectionChanged();
}

function intervalTriggerAlarm() {
	  return setInterval( function() {
		  getAlarmHistoryComposedService(sID,answer_callback_alarm);}, 30000 );
	}

	
	function intervalTriggerSensors() {
		  return setInterval( function() {
			  getDataCapabilityForComposedService(chosenCapabilityIndex,gID,sensID,sID,answer_callback_DataFile);}, 30000 );
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

// won't recheck for new discovered markers! Uses the cached ones in the tables!
// // to be used when the callbacks return!
function quickRedrawMarkersInSensorList() {
     if(AllMapItems.markers!=null) {
         if (involvedSensorsComplete.toString().length>1)
         {   var markerFoundInList = false;
             for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
                 if (isSensorinList(AllMapItems.markerGWOfNodesIds[n].toLowerCase(),AllMapItems.markerNodeIds[n].toLowerCase())) {
                     console.log('Putting marker with info for: ' + AllMapItems.markerGWOfNodesIds[n].toLowerCase() + '**' +AllMapItems.markerNodeIds[n].toLowerCase());
                     markerFoundInList = true;
                     itMarker.setMap(AllMapItems.map);
                 }
                 else
                 {
                     itMarker.setMap(null);
                 }
             }
         }  else {
             AllMapItems.toggleMarkers(false);
         }
     }

}

// puts the marker but keeps it hidden
function putMarkerWithInfo(myLatlng, contentString, nodeId, gateId, capsarray) {
    var isAlreadyDrawn = false;
    if(AllMapItems.markers!=null) {
        for (var n = 0, itMarker; itMarker = AllMapItems.markers[n]; n++) {
          if(AllMapItems.markerNodeIds[n].toLowerCase() == nodeId.toLowerCase() && AllMapItems.markerGWOfNodesIds[n].toLowerCase() == gateId.toLowerCase()  ){
              isAlreadyDrawn = true;
          }
        }
    }
    if(isAlreadyDrawn)
    {
        return;
    }
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
        title:nodeId,
        id:nodeId
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


function isSensorinList(gatewayId, sensorIdentifier)
{
	
	var isclocal = involvedSensorsComplete;
	console.log('---- Checking if sensor '+ gatewayId.toLowerCase() + '**' +sensorIdentifier.toLowerCase()+ ' belongs to the list to plot!') ;
	// ids = isclocal.toString().split(/[\s","]+/);
	var toBePlotted = false;
    var replyValRows =  isclocal.toString().split(/\n/);
    if (replyValRows.length==0)
    {
        console.log("No rows in reply (sensorList info)!");
        return;
    }
    else
    {
        console.log("Ids length is " +replyValRows.length);
        for (i = 0; i < replyValRows.length; i++) {
            if(replyValRows[i].toString()=="")  {
                continue;
            }
            tmpArr = replyValRows[i].toString().split(',');
            var rtGwId  = tmpArr[0];
            var rtSensNodeId = tmpArr[1];
            var rtIsInMem = tmpArr[2];
            var rtIsEnabled = tmpArr[3];
            var rtLongitudOptional = "";
            var rtLatitudeOptional = "";
            if(tmpArr.length > 4) {
                rtLongitudOptional = tmpArr[4];
                rtLatitudeOptional = tmpArr[5];
            }
         //   console.log("ids is "+rtSensNodeId);
         //   console.log("Comparing sensor id to "+ rtSensNodeId.toLowerCase()+' of gw: ' + rtGwId.toLowerCase());
            if(rtSensNodeId.toLowerCase() == sensorIdentifier.toLowerCase() && rtGwId.toLowerCase() == gatewayId.toLowerCase() )
            {
                toBePlotted=true;
                console.log('** Sensor matched! **');
                //break;
            }
        }
        if(toBePlotted)
        {
            console.log('Debug plotting sensor: '+sensorIdentifier);
        }
    }
    return toBePlotted;
}

function initialize() {
	var myOptions = {
        /* center on somewhere central (e.g parma italy) 44.8 , 10.3333,  */
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

    // ------------------------------------------
    var myLatlng;
    var contentString = '';
    var nodeId = 'node';
    var gateId = 'gw';
<%
    ArrayList<String> supportedCapabilities = new ArrayList<String>(); //for use with jstl. We have to set the context of the page to it
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

                        if(currInterfaceEntry.getGwId().equals(currGwId)) {

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
            totalNodes = 0;%>
        	getServiceSensorListForComposedService(sID,answer_callback_SensorList);

<%            StringBuilder supportedCapabilitiesBld;
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
            //alert("List length is "+involvedSensorsComplete.toString().length );
            //alert('invlvd sensor:' + involvedSensorsComplete.toString() ) ;
            //alert('node id:'+ nodeId);
            //var existsInListFlg = false;
            if (involvedSensorsComplete.toString().length>1) {
            //{
            //    if (isSensorinList(gateId,nodeId)) {
            //        console.log('Putting marker with info for: ' + gateId + '**' +nodeId);
            //        existsInListFlg = true;
            //
            //    }
            }
            else {
                console.log('DEBUG: No list returned (ajax) yet!');
            }
            // put all markers (filtering will happen with javascript later -after ajax returns)
            putMarkerWithInfo(myLatlng, contentString, nodeId, gateId, capsArr);
            //if (!existsInListFlg) {
            //    console.log('DEBUG: Sensor '+ gateId + '**' +nodeId+ ' does not belong in list!');
                //the marker is put on the map but is not shown
                //putMarkerWithInfo(myLatlng, contentString, nodeId, gateId, capsArr, true);
            //}
<%              }
            }
            
        }

%>
    MarkerSelection.areaSpan = document.getElementById('selectedm2Surface');
    MarkerSelection.areaSpanKm = document.getElementById('selectedkm2Surface');
    MarkerSelection.singleMarkersSelectedSpan = document.getElementById('selectedNodes'); //new:
    if(MarkerSelection.singleMarkersSelectedSpan == null) {
        console.log('DEBUG NULL MARKERS SPAN');
    }   else {
        console.log('DEBUG NOT NULL SPAN');
    }
    //MarkerSelection.selectedMarkersSpan = document.getElementById('selectedNodes');

    MarkerSelection.Clean(AllMapItems.map);

    cleanMapAndFillWithNodesOfCap(globalCapabilitytoViewInGMs);
//    google.maps.event.addListener(map, 'click', mapClick);

}
    

    
    
    
    
    function showMap(selectedService) 
    {
    	isSomethingData = false;
    	isSomethingAl=true;
    	
    	var selectionControl = document.getElementById('service-list');
        var graph = document.getElementById("graph");
		if(graph.style.display == "block") {

			graph.style.display = "none";
		}
    	var capList = document.getElementById("capability-list");
		if (capList.style.display == "block")
		//javascript:toggle2('graph','myHeader');
        {
        	
        	var ele = document.getElementById("graph");
        	var text = document.getElementById("myHeader");
        	var legList = document.getElementById("legList");

        			ele.style.display = "none";

        		text.innerHTML = "";
        		capList.style.display = "none";	
        		legList.style.display = "none";	
        		

        }
			defDate = "1970/01/01";
            lastNotifyIdPrinted = -1;
			nextIdx = 0;
			document.getElementById("autoIncForAlertsNotifications").value = 0;
			for (var i= 0; i < isServiceFirstLoaded.length; i++ )
			{
			isServiceFirstLoaded[i] = 0;
		
			}	
			resetViewForNewService();

			clearInterval(timerAlarm);
			clearInterval(timerData);
			
			

			
        if( selectionControl ==null || selectedService== null || selectedService=="")
        {
            return;    //new. to avoid console error
        }

     	var str = selectedService;

    	var pieces = str.split(/\|/); // new changed from /[\s|]+/
    	var servId = pieces[pieces.length-2];
    	//alert("servId is "+servId);
    	var webapp = '<%=request.getContextPath()%>';
    	var serviceId=servId.split(/[\s"db_stored__-__"]+/);
    	
    	sID = serviceId[serviceId.length-1];	
    	//console.log("sID is "+sID);
    	var answersl = "";
    	var parametrsCSVsl = "";

    	if (sID >0)
    	{
    	    getServiceSensorListForComposedService(sID,answer_callback_SensorList);
    	    getAlarmHistoryComposedService(sID,answer_callback_alarm);
    	

    		timerAlarm = intervalTriggerAlarm();




    	}
    	//getServiceSensorListForComposedService(serviceId[serviceId.length-1],answer_callback_SensorList);
    	//kmlFile = "http://"+window.location.host + pieces[pieces.length-1];
    	//xmlFile = "http://"+window.location.host + webapp+ "/roleEndUser/displayResultsFile.jsp?quid=" + servId + "&target=_blank"; 
    	//console.log("kml is "+kmlFile);
    	//console.log("xml is "+xmlFile);
    
        initialize();
    
    
   
    
    }

</script>
</head>
<body onload="load()">
<!-- for the dropdown Menu -->
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
<div class="span9">
<div class="well">

<legend style="padding-left: 10px;">Services</legend>
<%-- 						<c:choose>
							<c:when test="${empty friendlyName}"> No services available </c:when>
							<c:otherwise>
							<div class="btn-toolbar">
							    <select class="btn span3" name="InvolvedServices" id="InvolvedServices"  onChange="showMap(this.value);" >
								<option value="">[Please select]</option>
								<c:forEach items="${friendlyName}" var="Service" varStatus="loop" begin="0" end="${fn:length(friendlyName) - 1}">
								    <option value="${serviceIdList[loop.index]}|${resultFile[loop.index]}">${Service} </option>
								</c:forEach>
							    </select>
							</div>
							</c:otherwise>
							</c:choose> --%>

					<div id="service-list" class="btn-toolbar" style="display: block;">
						<select class="btn span3" onchange='showMap(this.value)'
							id="myServiceSelect">
						</select>
					</div>
<hr />
<div class="row-fluid">
<div class="span1" style="display:none;">
            <legend style="text-decoration: underline;font-size: x-small; line-height: 18px; margin-top: 44px;">Filter by capability:</legend>
            <table border="0">
                <tr class="nav nav-stacked">
                    <td>
                        <input
                                name="availableCapabilitiesRBGroup" id="allCapsRB" value="allCaps"
                                type="radio" onClick="cleanMapAndFillWithNodesOfCap('allCaps')" />
                        <td width="0">
                        </td>
                        <td>
                            <label for="allCapsRB">
                                <img title="all Capabilities" src="<%=request.getContextPath() %>/img/smartNodeIcon32.png" onclick="setCheckedValue(document.getElementById('allCapsRB'), 'allCaps');cleanMapAndFillWithNodesOfCap('allCaps');" style="height: 24px;width: 24px;" />
                            </label>
                        </td>
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

            %>
            <tr class="nav nav-stacked"><td>
            <input
                    name="availableCapabilitiesRBGroup" id="<%=currCapNoPrefix %>RB" value="<%=currCapNoPrefix %>"
                    type="radio"
                <% if(currCapNoPrefix.equalsIgnoreCase(defaultCapability)) { out.print("checked=\"checked\""); } %>
                    onClick="cleanMapAndFillWithNodesOfCap('<%=currCapNoPrefix %>')" />
                </td>
                <td width="0">
                </td>
                <td>
                    <label for="<%=currCapNoPrefix %>RB">
                        <img title="<%=Capability.getFriendlyUIName(currCapNoPrefix) %>" src="<%=request.getContextPath() %>/img/<%=Capability.getDefaultIcon(currCapNoPrefix) %>" onclick="setCheckedValue(document.getElementById('<%=currCapNoPrefix %>RB'), '<%=currCapNoPrefix %>');cleanMapAndFillWithNodesOfCap('<%=currCapNoPrefix %>');" style="height: 24px;width: 24px;" />
                    </label>
                </td>
            </tr>
            <%
                if (currCapNoPrefix.equalsIgnoreCase(defaultCapability)) {
                    foundDefaultCapability = true;
                }
            }
            pageContext.setAttribute("supportedCapabilities",supportedCapabilities);
        }
        else { %>
          <tr class="nav nav-stacked"><td colspan="3">No nodes were found</td></tr>
    <% } %>

            </table>
</div>


<div class="span12" style="height: 500px;" id="map_span">
            <div id="map_canvas" style="height:92%;">
                <!-- display a google maps container -->
            </div>
            
</div>
<div class="span2" style="height: 500px; display:none;" id="side_span">

    <div style="display:none; height: 120px; overflow-y: scroll;">Selection surface (m&sup2;):&nbsp;<span id="selectedm2Surface"></span>
            <br/>
            Selection surface (km&sup2;):&nbsp;<span id="selectedkm2Surface"></span>
            </div>
	     <br/>

            <div  style="display:none;">Notification&nbsp:&nbsp;<span id="errorMessage"></span></div>
            <br/>

            <div  style="display:none;">Selected&nbspnodes:&nbsp;<br/><span id="selectedNodes"></span></div>
            <br/>
            <p style="height: 180px;" />

            
</div>
</div>
<div id="headerDiv">
						<a id="myHeader" href="javascript:toggle2('graph','myHeader');">close</a>
					</div>
					<div id="contentDiv">
						
						<legend id="legList" style="padding-left: 10px; display: block;">Sensor
							Capabilities</legend>
						<div id="capability-list" class="btn-toolbar"
							style="display: block;">
							<select class="btn span3" onchange='showGraph(this.value)'
								id="mySelect">
							</select>

						</div>
						<div id="graph"
							style="height: 320px; display: block;"></div>
					</div>



</div>
    
</div>
<div class="span3">
					<div class="well"><legend style="padding-left: 10px;">Alarm history</legend>
						<div id="startToBeRemovedDiv" style="display: none;">
                                                        <a href="javascript:void(0);" onclick="getAlerts(); document.getElementById('startToBeRemovedDiv').style.display = 'none';"></a>
                                                    </div>
                                                    <br/><br/>
                                                    <div id="alertsAndNotifyDiv" style="display: none; height: 700px; overflow-y: scroll;">
                                                        <input type="hidden" id="autoIncForAlertsNotifications" value="0" />
                                                        <table id="alertsNotificationsTbl" class="table table-bordered table-striped" style="text-align: left; width: 440px;" border="0"  cellspacing="2">
                                                            <thead>
                                                                    <tr class="legend">
                                                                    <td>Message</td><td>Time</td><td>Details</td><td>Action</td>
                                                                </tr>
                                                            </thead>
                                                            <tbody>

                                                            </tbody>
                                                        </table>
                                                    </div>
					</div>
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
</body>
</html>

