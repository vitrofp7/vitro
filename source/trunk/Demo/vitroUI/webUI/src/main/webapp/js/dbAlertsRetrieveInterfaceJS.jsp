<%@page session='false' contentType='application/x-javascript' language="java" %>

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

//global Vars for callback functions
var cbComposedServiceList;
var cbAlarmHistoryComposedService;
var cbAlarmHistorybySensor;
var cbAlarmHistorybyCapability;
var cbAlarmHistorybySensorCapability;

/**
* List of composed services defined in the DB
* @return serviceDBId,serviceUniqueId,serviceName,isDeployed,isRunning\n
*/
function getComposedServiceList(callback) {
    if (!(typeof callback === "undefined")) {
        cbComposedServiceList=callback;
    }
    var functionSignature = "getComposedServiceList";
    var pServiceID =  "";
    var pInstanceID = "";
    var pGatewayID = "";
    var pSensorID = "";
    var pCapabilityID = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getAlertsSrvData(rPost);
}

/**
* List of alarms for this composed service defined in the DB
* @return compoServiceDbId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestamp,alertType,message\n
*/
function getAlarmHistoryComposedService(serviceID,callback)
{
    var functionSignature = "getAlarmHistoryComposedService";
    if(typeof serviceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbAlarmHistoryComposedService=callback;
    }

    var pServiceID =  ""+serviceID;
    var pInstanceID = "";
    var pGatewayID = "";
    var pSensorID = "";
    var pCapabilityID = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getAlertsSrvData(rPost);
}

/**
* List of alarms for this composed service (defined in the DB) and specific sensor
* @return compoServiceDbId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestamp,alertType,message\n
*/
function getAlarmHistorybySensor(serviceID,gatewayId,sensorId,callback){
    var functionSignature = "getAlarmHistorybySensor";
    if(typeof serviceID === "undefined" || typeof gatewayId === "undefined" || typeof sensorId === "undefined" ) {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbAlarmHistorybySensor=callback;
    }

    var pServiceID =  ""+serviceID;
    var pInstanceID = "";
    var pGatewayID = ""+gatewayId;
    var pSensorID = ""+sensorId;
    var pCapabilityID = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getAlertsSrvData(rPost);
}

/**
* List of alarms for this composed service (defined in the DB) and specific capability (all sensors, all gateways)
* @return compoServiceDbId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestamp,alertType,message\n
*/
function getAlarmHistorybyCapability(serviceID,capabilityId,callback){
    var functionSignature = "getAlarmHistorybyCapability";
    if(typeof serviceID === "undefined" || typeof capabilityId === "undefined"  ) {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbAlarmHistorybyCapability=callback;
    }

    var pServiceID =  ""+serviceID;
    var pInstanceID = "";
    var pGatewayID = "";
    var pSensorID = "";
    var pCapabilityID = ""+capabilityId;
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getAlertsSrvData(rPost);
}

/**
* List of alarms for this composed service (defined in the DB) and specific capability and specific sensor
* @return compoServiceDbId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestamp,alertType,message\n
*/
function getAlarmHistorybySensorCapability(serviceID,gatewayId,sensorId,capabilityId,callback) {

    var functionSignature = "getAlarmHistorybySensorCapability";
    if(typeof serviceID === "undefined" || typeof gatewayId === "undefined" || typeof sensorId === "undefined" || typeof capabilityId === "undefined" ) {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbAlarmHistorybySensorCapability=callback;
    }

    var pServiceID =  ""+serviceID;
    var pInstanceID = "";
    var pGatewayID = ""+gatewayId;
    var pSensorID = ""+sensorId;
    var pCapabilityID = ""+capabilityId;
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getAlertsSrvData(rPost);
}


// AUX AJAX FUNCTIONS  // --------------------------------------------
function getAlertsSrvData(rPostData)
{

    // fire off the request to /nTransactDbAlertsRetrieveInterface.jsp
    var request = $.ajax({
        url: "<%=request.getContextPath()%>/roleEndUser/nTransactDbAlertsRetrieveInterface.jsp",
        type: "post",
        data: rPostData
    });

    // callback handler that will be called on success
    request.done(function (response, textStatus, jqXHR){
        // log a message to the console
        console.log("Hooray, ajax alerts jquery worked!");
        ParseAlertsInterfaceXML(response);      //do not use the parseXML (because it calls other method in another JS)
    });

    // callback handler that will be called on failure
    request.fail(function (jqXHR, textStatus, errorThrown){
        // log the error to the console
        console.error(
        "The following error occured: "+
        textStatus, errorThrown
        );
    });

    // callback handler that will be called regardless
    // if the request failed or succeeded
    request.always(function () {
    });
}

function handleAlertsResult(errorno, errordescr,replyVal, functSig, parValues,qstr){
    if(errorno=="" || errorno!="0") {
        alert(errordescr);
    }
    else {
        console.log('Debug:: Function: ' +functSig  + ' \nParam Values Val:' + parValues  +' \nReply Val:' + replyVal ); // debug: + ' \nparNames Val:' + parNames  + ' \nqstr Val:' + qstr );
        // Condition branch, to handle the output of each function separately!
        if(functSig=='getComposedServiceList') {
            if (!(typeof cbComposedServiceList === "undefined")) {
                cbComposedServiceList(replyVal,parValues);
            }
        }
        else if(functSig=='getAlarmHistoryComposedService') {
            if (!(typeof cbAlarmHistoryComposedService === "undefined")) {
                cbAlarmHistoryComposedService(replyVal,parValues);
            }
        }

        else if(functSig=='getAlarmHistorybySensor') {
            if (!(typeof cbAlarmHistorybySensor === "undefined")) {
                cbAlarmHistorybySensor(replyVal,parValues);
            }
        }
        else if(functSig=='getAlarmHistorybyCapability') {
        //
            if (!(typeof cbAlarmHistorybyCapability === "undefined")) {
                cbAlarmHistorybyCapability(replyVal,parValues);
            }
        }
        else if(functSig=='getAlarmHistorybySensorCapability') {
            if (!(typeof cbAlarmHistorybySensorCapability === "undefined")) {
                cbAlarmHistorybySensorCapability(replyVal,parValues);
            }
        }
    }
}


// Ajax for changing query status
//
//
// compatibility function for various browsers' xml parsing
function myParseAlertsInterfaceXMLGetTextXML(node)
{
    if (typeof node.textContent != 'undefined') {
        text = node.textContent;
    }
    else if (typeof node.text != 'undefined') {
        text = node.text;
    }
    else if (typeof node.innerText != 'undefined') {
        text = node.innerText;
    }
    return text;
}

function ParseAlertsInterfaceXML(oxml)
{
    var errorno = "";
    var errordescr = "";
    var replyVal = "";
    var functSig = "";
    var parNames = "";
    var parValuesCSV = "";
    var qstr = "";

    var foundOneEntry =new Boolean();
    foundOneEntry = false;

    var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
    for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
    //
        var node = answer.childNodes.item(iNode);
        if(node.tagName == "reply")
        {
            foundOneEntry = true;
            replyVal = node.getAttribute('value');
            var reg = new RegExp("__nl__","g");
            replyVal  = replyVal.replace(reg, "\n");
            functSig = node.getAttribute('funct');
            parNames = node.getAttribute('parNames');
            parValuesCSV =  node.getAttribute('parValues');
            qstr =   node.getAttribute('qstr');
        }
        else if(node.tagName == "error")
        {
            errorno = node.getAttribute('errno');
            errordescr = node.getAttribute('errdesc');
        }
    }
    if(!foundOneEntry)
    {
        alert('No valid entries found in XML response!');
    }
    else{
        handleAlertsResult(errorno, errordescr,replyVal,functSig,parValuesCSV,qstr);
    }
}



