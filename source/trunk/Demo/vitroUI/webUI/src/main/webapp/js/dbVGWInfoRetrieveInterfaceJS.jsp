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

var cbGWInfo;
var cbSensorListForVGW;
var cbCapabilitiesListForVGW;
var cbEnableDisableSensorForVGW; // action is "enable" or "disable" node
var cbEquivSetsListForVGW;
var cbActUponEquivSetsListForVGW; // action is "add" or "remove" node or "removeAll" to delete the entire equivList.
var cbNodesInEquivListForVGW;

/**  gw info like friendly name and such
*/
function getGWInfo(vgwID,callback)
{
    var functionSignature = "getGWInfo";
    //some validation checks
    if(typeof vgwID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbGWInfo=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "";
    var pSensorIDList = "";
    var pEquivListID = "";
    var pAction = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);
}

/**
* List of sensors involved in a VGW
* @return sensorList // show also if they are enabled or disabled.
*/
function getSensorListForVGW(vgwID,callback)
{
    var functionSignature = "getSensorListForVGW";
    //some validation checks
    if(typeof vgwID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbSensorListForVGW=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "";
    var pSensorIDList = "";
    var pEquivListID = "";
    var pAction = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);

}


/**
* List of capabilities involved in a VGW
* @return capabilitiesList // could show also if they belong only to disabled sensors (or at least one enabled supports them)
*/
function getCapabilitiesListForVGW(vgwID,callback)
{
    var functionSignature = "getCapabilitiesListForVGW";
    //some validation checks
    if(typeof vgwID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbCapabilitiesListForVGW=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "";
    var pSensorIDList = "";
    var pEquivListID = "";
    var pAction = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);
}

/**
* Sets a sensor of a VGW explicitly as enabled or disabled
* @return 0 for ok. otherwise error.
*/
function setEnableDisableSensorForVGW(vgwID, sensorID, actionTk, callback)
{
    var functionSignature = "setEnableDisableSensorForVGW";
    //some validation checks
    if(typeof vgwID === "undefined" || typeof sensorID === "undefined" || typeof actionTk === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbEnableDisableSensorForVGW=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "" +sensorID ;
    var pSensorIDList = "";
    var pEquivListID = "";
    var pAction = "" + actionTk;
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);
}

/**
* Get the list of this VGW equivalency sets
* @list of equivalency sets (CSV entries with info per item)
*/
function getEquivSetsListForVGW(vgwID, callback)
{
    var functionSignature = "getEquivSetsListForVGW";
    //some validation checks
    if(typeof vgwID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbEquivSetsListForVGW=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "";
    var pSensorIDList = "";
    var pEquivListID = "";
    var pAction = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);
}

/**
* Get the list of nodes for this VGW and this particular equivalency set id
* @list of nodes
*/
function getNodesInEquivListForVGW(vgwID,equivListID,callback)
{
    var functionSignature = "getNodesInEquivListForVGW";
    //some validation checks
    if(typeof vgwID === "undefined" || typeof equivListID === "undefined" ) {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbNodesInEquivListForVGW=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "";
    var pSensorIDList = "";
    var pEquivListID = ""+equivListID;
    var pAction = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);
}

/**
* Get the list of nodes for this VGW and this particular equivalency set id
* action is "add" or "remove" node or "removeAll" to delete the entire equivList.
* @return a status (0 for ok)
*/
function actUponEquivSetsListForVGW(vgwID,sensorIDList,equivListID,actionTk,callback)
{
    var functionSignature = "actUponEquivSetsListForVGW";
    //some validation checks
    if(typeof vgwID === "undefined" || typeof sensorIDList === "undefined"  || typeof equivListID === "undefined" ||  typeof actionTk === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbActUponEquivSetsListForVGW=callback;
    }

    var pGatewayID = "" +vgwID;
    var pSensorID = "";
    var pSensorIDList = ""+sensorIDList;
    var pEquivListID = ""+equivListID;
    var pAction = ""+actionTk;
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pSensorIDList='+ pSensorIDList+ '&pEquivListID='+ pEquivListID+ '&pAction='+ pAction;
    getVGWData(rPost);
}


// AUX AJAX FUNCTIONS  // --------------------------------------------
function getVGWData(rPostData)
{
    //alert(runBGTask);
    //runBGTask  = true;
    //serializedData = '';
    //alert(rPostData);
    // rPostData = '';
    // fire off the request to /nTransactDbVGWInfoRetrieveInterface.jsp
    var request = $.ajax({
        url: "<%=request.getContextPath()%>/roleEndUser/nTransactDbVGWInfoRetrieveInterface.jsp",
        type: "post",
        data: rPostData
    });

    // callback handler that will be called on success
    request.done(function (response, textStatus, jqXHR){
        // log a message to the console
        //console.log("Hooray, ajax jquery worked!");
        //if(runBGTask){
        ParseVGWInterfaceXML(response);      //do not use the parseXML (because it calls other method in another JS)
        //}
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
        //
        //
        //call itself again
        //if(runBGTask){
        //}
    });

}

function handleVGWResult(errorno, errordescr,replyVal, functSig, parValues,qstr){
    if(errorno=="" || errorno!="0") {
        alert(errordescr);
    }
    else {
        console.log('Debug:: Function: ' +functSig  + ' \nParam Values Val:' + parValues  +' \nReply Val:' + replyVal ); // debug: + ' \nparNames Val:' + parNames  + ' \nqstr Val:' + qstr );
        // TODO: Add and condition branch, to handle the output of each function separately!
        if(functSig=='getGWInfo') {
            if (!(typeof cbGWInfo === "undefined")) {
                cbGWInfo(replyVal,parValues);
            }
        }
        else if(functSig=='getSensorListForVGW') {
            if (!(typeof cbSensorListForVGW === "undefined")) {
                cbSensorListForVGW(replyVal,parValues);
            }
        }
        else if(functSig=='getCapabilitiesListForVGW') {
            if (!(typeof cbCapabilitiesListForVGW === "undefined")) {
                cbCapabilitiesListForVGW(replyVal,parValues);
            }
        }
        else if(functSig=='setEnableDisableSensorForVGW') {
            if (!(typeof cbEnableDisableSensorForVGW === "undefined")) {
                cbEnableDisableSensorForVGW(replyVal,parValues);
            }
        }
        else if(functSig=='getEquivSetsListForVGW') {
            if (!(typeof cbEquivSetsListForVGW === "undefined")) {
                cbEquivSetsListForVGW(replyVal,parValues);
            }
        }
        else if(functSig=='getNodesInEquivListForVGW') {
            if (!(typeof cbNodesInEquivListForVGW === "undefined")) {
                cbNodesInEquivListForVGW(replyVal,parValues);
            }
        }
        else if(functSig=='actUponEquivSetsListForVGW') {
            if (!(typeof cbActUponEquivSetsListForVGW === "undefined")) {
                cbActUponEquivSetsListForVGW(replyVal,parValues);
            }
        }
    }
}


// Ajax for changing query status
//
//
// compatibility function for various browsers' xml parsing
function myParseVGWInterfaceXMLGetTextXML(node)
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

function ParseVGWInterfaceXML(oxml)
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
        handleVGWResult(errorno, errordescr,replyVal,functSig,parValuesCSV,qstr);
    }
}
