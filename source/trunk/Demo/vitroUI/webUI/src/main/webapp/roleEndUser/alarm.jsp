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
<%@ page import="presentation.webgui.vitroappservlet.Common"%>
<%@ page import="vitro.vspEngine.service.engine.UserNode"%>
<%@ page import="vitro.vspEngine.service.geo.Coordinate"%>
<%@ page import="java.util.regex.Pattern"%>
<%@ page import="vitro.vspEngine.service.geo.GeoPoint"%>
<%@ page import="vitro.vspEngine.logic.model.*"%>
<%@ page import="presentation.webgui.vitroappservlet.Model3dservice.*"%>
<%@ page import="java.util.*"%>
<%@ page session='false' contentType="text/html;charset=UTF-8"
	language="java"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import='java.lang.*'%>
<%@ page import="vitro.vspEngine.logic.model.Capability"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="shortcut icon"
	href="<%=request.getContextPath()%>/img/favicon2.ico"
	type="image/x-icon" />

<title>Alarms</title>
<link href="<%=request.getContextPath()%>/css/bootstrap.css"
	rel="stylesheet">
<link href="<%=request.getContextPath()%>/css/vitrodemo.css"
	rel="stylesheet">

<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/alarmMonitoringJS.jsp"></script>
	<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/dbAlertsRetrieveInterfaceJS.jsp"></script>
<script type="text/javascript"
	src="<%=request.getContextPath()%>/js/dygraph-combined.js"></script>
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



<link rel="shortcut icon"
	href="<%=request.getContextPath()%>/ico/favicon.png">
<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardAlarmButton').addClass("active");
 	});    

	var parameters = [];    
	var involvedSensorsComplete = "";
	var involvedServiceComplete = "";
	var serviceName = "";
	var involvedSensorsCapabilities = [];
	var involvedSensorsCapabilitiesName = [];
	var chosenCapability;
	var chosenCapabilityIndex;
	var sensordata = [];
	var defDate;
    var lastNotifyIdPrinted;
	var isSomethingAl;
	var isSomethingData;
	var timerData = 0;
	var timerAlarm = 0;
	var isServiceFirstLoaded = [];
	var sensInvId = [];
	var nextIdx;
	
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
    
    



function plot_graph(values) {
var name;
	name = getCapabilityFriendlyName(chosenCapability);
	var graph = document.getElementById("graph");
        var width = parseInt(graph.style.width);
      
        
		if(graph.style.display == "none") {

			graph.style.display = "block";
		}
        var data = [];
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


function answer_callback_DataFile(answerdf,parametrsCSVdf)
{
	
sensordata=answerdf;

if (answerdf.length==0)
	isSomethingData=false;
else
	isSomethingData=true;

if (isSomethingData)
plot_graph(sensordata);
}


function showGraph(capId)
{
var tmp;

tmp = capId.split(/[\s|]+/);

chosenCapability = tmp[1];
chosenCapabilityIndex = tmp[0];
//alert ("Retrieving data for "+tmp[1]+ " "+gID+" "+sensID+" "+sID);
getDataCapabilityForComposedService(chosenCapabilityIndex,gateId,sensorName,serviceID,answer_callback_DataFile);

timerData = intervalTriggerSensors();


}


function showcapabilities(invsenscap,invsenscapName){
    var out = '';
	var name = [];
	out += '<option value="">Please select a capability</option>';
    for (i = 0; i < invsenscap.length; i++) {
	name[i]= getCapabilityFriendlyName(invsenscapName[i]);
      out += '<option value='+invsenscap[i]+'|'+name[i]+' >' + name[i] + '</option>';
    }
    document.querySelector( '#mySelect' ).innerHTML = out;
    javascript:togglelist('capability-list');

  }




var serviceID = 0;
var sensorName = "";
var capaId = 0;
var gateId = "";



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
	
	
	function togglelist(switchTextDiv) {
		var text = document.getElementById(switchTextDiv);
		if(text.style.display == "none") {

			text.style.display = "block";
		}
	}	
	
	
	
function initialize()
{
	defDate = "1970/01/01";
    lastNotifyIdPrinted = -1;
<c:if test="${param.servId != null}">
//<c:set var="serviceID">${param.servId}</c:set>
serviceID = ${param.servId};
</c:if>
<c:if test="${param.gateId != null}">
//gateId=decodeURIComponent(${param.gateId});
</c:if>
<c:if test="${param.sensName != null}">
//<c:set var="sensorName">${param.sensName}</c:set>
</c:if>
<c:if test="${param.capId != null}">
capaId=${param.capId};
</c:if>

var GET = {};
var query = window.location.search.substring(1).split("&");
for (var i = 0, max = query.length; i < max; i++)
{
    if (query[i] === "") // check for trailing & with no param
        continue;

    var param = query[i].split("=");
    GET[decodeURIComponent(param[0])] = decodeURIComponent(param[1] || "");
}

//use_id is now available in the use_id variable
gateId = GET.gateId;
sensorName = GET.sensName;
//console.log("sensName vale "+sensorName);

if (serviceID != 0 && gateId!="" && sensorName != "" &&capaId !=0  )
	{
	getAlarmHistoryComposedService(serviceID,answer_callback_alarm);
	
 timerAlarm=intervalTriggerAlarm();
	}
	
getComposedServiceList(answer_callback_ServiceList);



}

function isYourFirstTime(serId)
{
//console.log("isServiceFirstLoaded "+isServiceFirstLoaded);
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

function answer_callback_alarm(answeral,parametrsCSVal)
{
    var msg = "";
    var tmp;
    var alarms =  answeral.toString().split(/\n/);
    //console.log("serviceID is "+serviceID);
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
        var currCapId  = "-1";
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
            //console.log('Parse Db id !!::'+ parsedNotifyDBId);
            //console.log('Ref Db id !!::'+ internalLastNotifyIdPrinted);

            if(parsedNotifyDBId <= maxInternalLastNotifyIdPrinted) {
                continue;
            }

            parameters[3] = tmp[3]; //capId
            tmpPartServId = tmp[2]; //partServId
            tmpNotifyType = tmp[13];  //notifyType
            if(parameters[3].toString()!=currCapId.toString() || tmpPartServId.toString()!=currPartServId.toString() || tmpNotifyType.toString()!=currNotifyType.toString()) {
                //console.log('[][]][]][][[ CURR CAP ID = ' + currCapId.toString() + 'param 3' + parameters[3].toString());
                //console.log('[][]][]][][[ internadef date = ' + internalDateRef);
                //console.log('[][]]][]][ Gloabl def date = ' + defDate);
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


            if (internalLastNotifyIdPrinted <parsedNotifyDBId)
            {

                internalDateRef = tmp[12];
                internalLastNotifyIdPrinted = parsedNotifyDBId;
                if(maxInternalLastNotifyIdPrinted <internalLastNotifyIdPrinted )   {
                    maxInternalLastNotifyIdPrinted = internalLastNotifyIdPrinted;
                }
                // console.log('[][FOR LOOP [[ internadef date = ' + internalDateRef);
                // console.log('[][FOR LOOP [[ internadef date = ' + tmp[12]);

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
        incrementCalls(serviceID);
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
    else if(alType == 3)
    {
        var alert = generate('alert');
        $.noty.setText(alert.options.id, title + ' ('+time+') ' +msg);
    }
    
    {
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
    //console.log("Auto inc is "+document.getElementById("autoIncForAlertsNotifications").value);
    nextIdx = parseInt(document.getElementById("autoIncForAlertsNotifications").value)  + 1;
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
        var alertMsgDisp = pAlertTitle + ' ID: ' + nextIdx;
        var alertTimeVal = '';
        var alertTimeDisp = pAlertTime;
        var alertDetailsDisp = pAlertDetails;

        if(parsedAlertType == 3)
        {
            row.style.background="#a4e6a3";
            alertMsgDisp = pAlertTitle + ' ID ' + nextIdx + ' on sensor ' + parameters[2] + ' capability '+getCapabilityFriendlyName(parameters[4]) + ' function '+parameters[7] + ' returned value '+parameters[5] ;
            // console.log("Service id "+serviceID);
            //   if (isYourFirstTime(serviceID)==0)
            //   	{
            //   //     	console.log("isServiceFirstLoaded "+isServiceFirstLoaded[serviceID]);
            //            showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, alertMsgDisp, pAlertType);
            //    	}
            cellMsg.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"alertMsg[]\" id=\"alertMsg_"+ nextIdx +"\" value=\""+alertMsgVal+"\" />"+ alertMsgDisp ;
            cellTime.innerHTML= "<input type=\"hidden\" name=\"alertTime[]\" id=\"alertTime_"+ nextIdx +"\"  value=\""+alertTimeVal+"\" />"+  alertTimeDisp;  //"New:: " + nextIdx;

            cellDetails.innerHTML="<a href=\"javascript:void(0);\" onclick=\'showAlertDetails(\"uniqRuleId_"+nextIdx+"\",\"" +pAlertTitle+"\",\"" +alertTimeDisp+"\",\""+pAlertDetails+"\",\""+pAlertType+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoNotifyDetails32.png\" title=\"Details\" style=\"height: 32px;\" /></a>";

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
            //if (isYourFirstTime(serviceID)==0)
            //    showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, alertMsgDisp, pAlertType);
            cellMsg.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"alertMsg[]\" id=\"alertMsg_"+ nextIdx +"\" value=\""+alertMsgVal+"\" />"+ alertMsgDisp;
            cellTime.innerHTML= "<input type=\"hidden\" name=\"alertTime[]\" id=\"alertTime_"+ nextIdx +"\"  value=\""+alertTimeVal+"\" />"+  alertTimeDisp;  //"New:: " + nextIdx;

            cellDetails.innerHTML="<a href=\"javascript:void(0);\" onclick=\'showAlertDetails(\"uniqRuleId_"+nextIdx+"\",\"" +pAlertTitle+"\",\"" +alertTimeDisp+"\",\""+pAlertDetails+"\",\""+pAlertType+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoNotifyDetails32.png\" title=\"Details\" style=\"height: 32px;\" /></a>";

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
            alertMsgDisp = pAlertTitle + ' ID: ' + nextIdx + ' raised';//if (isYourFirstTime(serviceID)==0)
            //    showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, alertMsgDisp, pAlertType);
            cellMsg.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"alertMsg[]\" id=\"alertMsg_"+ nextIdx +"\" value=\""+alertMsgVal+"\" />"+ alertDetailsDisp;
            cellTime.innerHTML= "<input type=\"hidden\" name=\"alertTime[]\" id=\"alertTime_"+ nextIdx +"\"  value=\""+alertTimeVal+"\" />"+  alertTimeDisp;  //"New:: " + nextIdx;

            cellDetails.innerHTML="<a href=\"javascript:void(0);\" onclick=\'showAlertDetails(\"uniqRuleId_"+nextIdx+"\",\"" +pAlertTitle+"\",\"" +alertTimeDisp+"\",\""+pAlertDetails+"\",\""+pAlertType+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoNotifyDetails32.png\" title=\"Details\" style=\"height: 32px;\" /></a>";

            //TODO there should be an alert/confirmation for deleting
            cellAddRem.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeRule(\"uniqRuleId_"+nextIdx+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
            if(document.getElementById('alertsAndNotifyDiv').style.display == 'none')
            {
                document.getElementById('alertsAndNotifyDiv').style.display = 'block';
            }
        }
    }
        
}

function answer_callback_DataFile(answerdf,parametrsCSVdf)
{
sensordata=answerdf;
//console.log(sensordata);
plot_graph(sensordata);
}


function answer_callback_ServiceList(answerserl,parametrsCSVsl)
{
		
    //var ids;
   // console.log('DEBUG:: Reached callback function for service List '+answerserl);
    involvedServiceComplete = answerserl;
    var isclocal = involvedServiceComplete;
	ids = isclocal.toString().split(/\n/);

    var out = '';
    var tmp;
    var stringtmp="";
    var serv = [];
    var servId = [];
    var count = 0;

    
    for (i = 0; i < ids.length; i++) {
  	  
   	 tmp = ids[i].toString().split(',');

   	if (tmp[tmp.length-1].toString()==1)
   		{
   		isServiceFirstLoaded[count] = 0;
   		sensInvId[count] = tmp[0];
   		count++;
   		}
       }
    
    
    
    out += '<option value="">Please select a service</option>';
 for (i = 0; i < ids.length; i++) {
	
	 tmp = ids[i].toString().split(',');
	serv[i] = tmp[2];
	servId[i] = tmp[0];
console.log("tmp vale "+ tmp);
	if (tmp[tmp.length-1].toString()==1)
	//  out += '<option value='+invsenscap[i]+'|'+name[i]+' >' + name[i] + '</option>';
    out += '<option value='+servId[i]+'|'+serv[i]+' >' + serv[i] + '</option>';
    
    }
    document.querySelector( '#myServiceSelect' ).innerHTML = out;
    javascript:togglelist('service-list');

    
    
}

function answer_callback_SensorList(answersl,parametrsCSVsl)
{
    //var ids;
    console.log('DEBUG:: Reached callback function for sensor List');
    involvedSensorsComplete = answersl;
    var isclocal = involvedSensorsComplete;

    //ids = isclocal.toString().split(/[\s","]+/);
    var out = '';
    var tmp;
    var stringtmp="";
    var sens = "";
    out += '<option value="">Please select a sensor</option>';
    var replyValRows =  answersl.toString().split(/\n/);
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
            sens = tmpArr[1].toLowerCase();
            gateId = tmpArr[0].toLowerCase();
            out += '<option value='+sens+'|'+gateId+' >' + sens + '</option>';
        }
    }
 //   console.log("isclocal "+isclocal + " length "+isclocal.length);
    /*
    for (i = 0; i < ids.length/3; i++) {
    	stringtmp = involvedSensorsComplete[i];
    	//console.log("time "+i+" stringtmp is "+stringtmp);
    	//tmp= stringtmp.split(/[\s,]+/);
    	sens = ids[3*i+1].toLowerCase();
    	gateId = ids[3*i].toLowerCase();
        out += '<option value='+sens+'|'+gateId+' >' + sens + '</option>';
        }
     */
    document.querySelector( '#mySensorSelect' ).innerHTML = out;
    javascript:togglelist('sensor-list');

    //}
}


function answer_callback_CapabilitiesList(answercl,parametrsCSVcl)
{
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

function intervalTriggerAlarm() {
	  return setInterval( function() {
		  getAlarmHistoryComposedService(serviceID,answer_callback_alarm);}, 30000 );
	}

	
	function intervalTriggerSensors() {
		  return setInterval( function() {
			  getDataCapabilityForComposedService(chosenCapabilityIndex,gateId,sensorName,serviceID,answer_callback_DataFile);}, 30000 );
		}	
	
	
function getSensors(choice)
{
	isSomethingData = false;
	isSomethingAl=true;
	var tmp;
	nextIdx = 0;
	var graph = document.getElementById("graph");
	if(graph.style.display == "block") {

		graph.style.display = "none";
	}
	document.getElementById("autoIncForAlertsNotifications").value = 0;
	resetViewForNewService();
	clearInterval(timerAlarm);
	clearInterval(timerData);
	defDate = "1970/01/01";
    lastNotifyIdPrinted = -1;
	tmp = choice.split(/[\s|]+/);
	serviceID= tmp[0];
	serviceName = tmp[1];
	for (var i= 0; i < isServiceFirstLoaded.length; i++ )
		{
		isServiceFirstLoaded[i] = 0;
		
		}
	var textc = document.getElementById("capability-list");
	if(textc.style.display == "block") {

		textc.style.display = "none";
	}

	if (serviceID >0)
	{
	getServiceSensorListForComposedService(serviceID,answer_callback_SensorList);
	getAlarmHistoryComposedService(serviceID,answer_callback_alarm);
			timerAlarm = intervalTriggerAlarm();
	}
	else 
		{
		var text = document.getElementById("sensor-list");
		if(text.style.display == "block") {

			text.style.display = "none";
		}
				
		}
}

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

function getCapabilities(choice)
{
	var graph = document.getElementById("graph");
	if(graph.style.display == "block") {

		graph.style.display = "none";
	}
	var tmp;
	clearInterval(timerData);
	tmp = choice.split(/[\s|]+/);
	sensorName= tmp[0];
	gateId = tmp[1];
	
	getSensorCapabilityListForComposedService(serviceID,gateId,sensorName,answer_callback_CapabilitiesList);
}

	</script>




</head>
<body onload=initialize()>
	<%-- For the menu --%>

	<!-- DDMenu -->
	<%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">

			<div class="span8">
				<div class="well">
					<div id="service-list" class="btn-toolbar" style="display: block;">
						<select class="btn span3" onchange='getSensors(this.value)'
							id="myServiceSelect">
						</select>

					</div>
					<div id="sensor-list" class="btn-toolbar" style="display: none;">
						<select class="btn span3" onchange='getCapabilities(this.value)'
							id="mySensorSelect">
						</select>

					</div>
					<div id="capability-list" class="btn-toolbar"
						style="display: none;">
						<select class="btn span3" onchange='showGraph(this.value)'
							id="mySelect">
						</select>

					</div>

					<br />


					<legend style="padding-left: 10px;">Alarm history</legend>
					<div id="startToBeRemovedDiv" style="display: none;">
						<a href="javascript:void(0);"
							onclick="getAlarmHistoryComposedService(serviceID,answer_callback_alarm); document.getElementById('startToBeRemovedDiv').style.display = 'none';"></a>
					</div>
					<br />
					<br />
					<div id="alertsAndNotifyDiv"
						style="display: none; height: 350px; overflow-y: scroll;">
						<input type="hidden" id="autoIncForAlertsNotifications" value="0" />
						<table id="alertsNotificationsTbl"
							class="table table-bordered table-striped"
							style="text-align: left;" border="0" cellspacing="2">
							<thead>
								<tr class="legend">
									<td>Message</td>
									<td>Time</td>
									<td>Details</td>
									<td>Action</td>
								</tr>
							</thead>
							<tbody>

							</tbody>
						</table>
					</div>

<br />
					<div id="headerDiv">
						<a id="myHeader" style="display: none;" href="javascript:toggle2('graph','myHeader'); ">close</a>
					</div>
					<div id="contentDiv">

						<legend id="legList" style="padding-left: 10px; display: none;">Sensor
							Data</legend>
						<div id="graph" style="height: 320px; display: none;"></div>
					</div>



				</div>


			</div>
			<div class="span4">
				<div class="well" style="height: 300px;">
					<legend style="padding-left: 10px;">Cameras</legend>
					<div class="btn-toolbar">
						<select class="btn span4" name="cameras" size="1" id="selection"
							onchange="select(document.getElementById('selection'))">
							<option value=""></option>
							<option value="http://192.168.100.3/axis-cgi/mjpg/video.cgi?resolution=320x240">Camera 1</option>
							<option value="http://192.168.100.4/axis-cgi/mjpg/video.cgi?resolution=320x240">Camera 2</option>
						</select>
					</div>


					<div id="playerView"
						style="width: 100%; height: 70%; font-size: 1.4em;">
						<SCRIPT LANGUAGE="JavaScript">

							if ((navigator.appName == "Microsoft Internet Explorer")) {
								document.write('<object type="application/x-vlc-plugin" \
								  id="vlc" \
								  width="320" \
								  height="240" \
								  classid="clsid:9BE31822-FDAD-461B-AD51-BE1D1C159921"> \
								  <param name="Volume" value="100" /> \
								  <param name="AutoPlay" value="false" /> \
								  <param name="AutoLoop" value="false" /> \
								</object>');
								}
							else {
								document.write('<embed type="application/x-vlc-plugin" pluginspage="http://www.videolan.org" \
								   width="320" \
								   height="240" \
								   id="vlc"> \
								</embed>');
								}
							</SCRIPT>
					</div>
					<SCRIPT LANGUAGE="JavaScript">


					function select(select) {
					var selIndex = select.selectedIndex;
					var selValue = select.value;
					playStream(selValue)
					}

					function playStream(url)
					{
						try {
							var vlc = document.getElementById('vlc');

							vlc.playlist.stop();
							vlc.playlist.items.clear();
							var id = vlc.playlist.add(url);
							vlc.playlist.playItem(id)
						} catch(e) {

						}
					}
					</SCRIPT>



				</div>
			</div>






		</div>

	</div>
	<!-- begin the footer for the application -->
	<%= Common.printFooter(request, application) %>
	<!-- end of footer -->
</body>
</html>
