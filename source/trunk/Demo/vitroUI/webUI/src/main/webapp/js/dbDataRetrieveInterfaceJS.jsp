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

/**
* List of sensors involved in a composite service
* @return sensorList
*/

var cbServiceSensorListForComposedService;
var cbSensorCapabilityListForComposedService;
var cbDataListForComposedService;
var answer="";
function getServiceSensorListForComposedService(serviceID,callback)
{
    var functionSignature = "getServiceSensorListForComposedService";
    //some validation checks
    if(typeof serviceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbServiceSensorListForComposedService=callback;
    }

    var pServiceID =  "" +serviceID;
    var pInstanceID = "";
    var pGatewayID = "";
    var pSensorID = "";
    var pCapabilityID = "";
    var rPost = "";
    var answer = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
	
}

/**
* List of sensors involved in a partial service
* @return sensorList
*/
function getServiceSensorListForPartialService(instanceID,callback)
{
    var functionSignature = "getServiceSensorListForPartialService";
    //some validation checks
    if(typeof instanceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbxxxxxxx = callback;
    }

    var pServiceID =  "";
    var pInstanceID = "" +instanceID;
    var pGatewayID = "";
    var pSensorID = "";
    var pCapabilityID = "";
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
	
}


/**
* List of capabilities associated to a sensor and this (composed) service
* TODO we could also have a version of this for partialServiceIds
* @return capabilityList
*/
function getSensorCapabilityListForComposedService(serviceID, gatewayID, sensorID,callback)
{
    var functionSignature = "getSensorCapabilityListForComposedService";
    //some validation checks
    if(typeof serviceID === "undefined" || typeof gatewayID === "undefined" || typeof sensorID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbSensorCapabilityListForComposedService = callback;
    }
	//alert("Received values "+serviceID + " "+ gatewayID + " " +sensorID);

    var pServiceID =  "" +serviceID;
    var pInstanceID = "";
    var pGatewayID = "" + gatewayID;
    var pSensorID = "" + sensorID;
    var pCapabilityID = "";
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);

}


/**
* List of capabilities associated to a sensor and this (composed) service
* TODO we could also have a version of this for partialServiceIds
* @return capabilityList
*/
function getSensorCapabilityListForPartialService(instanceID, gatewayID, sensorID, callback)
{
    var functionSignature = "getSensorCapabilityListForPartialService";
    //some validation checks
    if(typeof instanceID === "undefined" || typeof gatewayID === "undefined" || typeof sensorID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        // cbXXXX = callback;
    }


    var pServiceID =  "";
    var pInstanceID = "" +instanceID;
    var pGatewayID = "" + gatewayID;
    var pSensorID = "" + sensorID;
    var pCapabilityID = "";
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}



/**
* Data collected by a sensor for the composed service
* @return CSV data
*/
function getDataCapabilityForComposedService(capabilityID,gatewayID,sensorID,serviceID,callback)
{
    var functionSignature = "getDataCapabilityForComposedService";
    //some validation checks
    if(typeof capabilityID === "undefined" || typeof gatewayID === "undefined" || typeof sensorID === "undefined" || typeof serviceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        cbDataListForComposedService = callback;
    }


    var pServiceID =  ""+serviceID;
    var pInstanceID = "" ;
    var pGatewayID = "" + gatewayID;
    var pSensorID = "" + sensorID;
    var pCapabilityID = "" +capabilityID;
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}


/**
* Data collected by a sensor for the partial service
* @return CSV data
*/
function getDataCapabilityForPartialService(capabilityID,gatewayID,sensorID,instanceID,callback)
{
    var functionSignature = "getDataCapabilityForPartialService";
    //some validation checks
    if(typeof capabilityID === "undefined" || typeof gatewayID === "undefined" || typeof sensorID === "undefined" || typeof instanceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbXXXXX = callback;
    }

    var pServiceID =  "";
    var pInstanceID = "" +instanceID;
    var pGatewayID = "" + gatewayID;
    var pSensorID = "" + sensorID;
    var pCapabilityID = "" +capabilityID;
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}

/**
* Data collected by a sensor for the Composed service (latest received value)
* @return data
*/
function getMostRecentDataCapabilityForComposedService(capabilityID,gatewayID,sensorID,serviceID,callback)
{
    var functionSignature = "getMostRecentDataCapabilityForComposedService";
    //some validation checks
    if(typeof capabilityID === "undefined" || typeof gatewayID === "undefined" || typeof sensorID === "undefined" || typeof serviceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbXXXXX = callback;
    }
    var pServiceID =  ""+serviceID ;
    var pInstanceID = "" ;
    var pGatewayID = "" + gatewayID;
    var pSensorID = "" + sensorID;
    var pCapabilityID = "" +capabilityID;
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}

/**
* Data collected by a sensor for the partial service (latest received value)
* @return data
*/
function getMostRecentDataCapabilityForPartialService(capabilityID,gatewayID,sensorID,instanceID,callback)
{
    var functionSignature = "getMostRecentDataCapabilityForPartialService";
    //some validation checks
    if(typeof capabilityID === "undefined" || typeof gatewayID === "undefined" || typeof sensorID === "undefined" || typeof instanceID === "undefined") {
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbXXXXX = callback;
    }
    var pServiceID =  "";
    var pInstanceID = "" +instanceID;
    var pGatewayID = "" +gatewayID;
    var pSensorID = "" + sensorID;
    var pCapabilityID = "" +capabilityID;
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}

/**
* to get the list of capabilities associated to a composed service.
* Then to retrieve data we use the same getDataCapability function.
* @return capabilityList
*/
function getComposedServiceCapabilityList(serviceID,callback)
{
    var functionSignature = "getComposedServiceCapabilityList";
    //some validation checks
    if(typeof serviceID === "undefined"){
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbXXXXX = callback;
    }

    var pServiceID =  ""+serviceID;
    var pInstanceID = "";
    var pGatewayID = "";
    var pSensorID = "" ;
    var pCapabilityID = "";
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}

/**
* to get the list of capabilities associated to a partial service.
* Then to retrieve data we use the same getDataCapability function.
* @return capabilityList
*/
function getPartialServiceCapabilityList(instanceID,callback)
{
    var functionSignature = "getPartialServiceCapabilityList";
    //some validation checks
    if(typeof instanceID === "undefined"){
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbXXXXX = callback;
    }
    var pServiceID =  "";
    var pInstanceID = ""+instanceID;
    var pGatewayID = "";
    var pSensorID = "" ;
    var pCapabilityID = "";
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}


/**
* to get the list of capabilities associated to a partial service.
* Then to retrieve data we use the same getDataCapability function.
* @return partialServicesList  (ids)
*/
function getPartialServicesForComposedServiceID(serviceID,callback)
{
    var functionSignature = "getPartialServicesForComposedServiceID";
    //some validation checks
    if(typeof serviceID === "undefined"){
        console.log("Error " + functionSignature + " - not all arguments were set!");
        return;
    }
    if (!(typeof callback === "undefined")) {
        //cbXXXXX = callback;
    }
    var pServiceID =  "" +serviceID;
    var pInstanceID = "";
    var pGatewayID = "";
    var pSensorID = "" ;
    var pCapabilityID = "";
    var rPost = "";
    rPost = '' + 'fid=' + functionSignature + '&pServiceID='+pServiceID+ '&pInstanceID='+ pInstanceID+ '&pGatewayID='+ pGatewayID+ '&pSensorID='+ pSensorID+ '&pCapabilityID='+ pCapabilityID;
    getSrvData(rPost);
}

// AUX AJAX FUNCTIONS  // --------------------------------------------
function getSrvData(rPostData)
{
    //alert(runBGTask);
    //runBGTask  = true;
    //serializedData = '';
    //alert(rPostData);
    // rPostData = '';
    // fire off the request to /nTransactDbDataRetrieveInterface.jsp
    var request = $.ajax({
        url: "<%=request.getContextPath()%>/roleEndUser/nTransactDbDataRetrieveInterface.jsp",
        type: "post",
        data: rPostData
    });

    // callback handler that will be called on success
    request.done(function (response, textStatus, jqXHR){
        // log a message to the console
        console.log("Hooray, ajax jquery worked!");
        //if(runBGTask){
            ParseInterfaceXML(response);      //do not use the parseXML (because it calls other method in another JS)
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

function handleResult(errorno, errordescr,replyVal, functSig, parValues,qstr){
    if(errorno=="" || errorno!="0") {
        alert(errordescr);
    }
    else {
        console.log('Debug:: Function: ' +functSig  + ' \nParam Values Val:' + parValues  +' \nReply Val:' + replyVal ); // debug: + ' \nparNames Val:' + parNames  + ' \nqstr Val:' + qstr );
        // TODO: Add and condition branch, to handle the output of each function separately!
        if(functSig=='getServiceSensorListForComposedService') {
            if (!(typeof cbServiceSensorListForComposedService === "undefined")) {
		        cbServiceSensorListForComposedService(replyVal,parValues);
            }
        }
        else if(functSig=='getServiceSensorListForPartialService') {
            //
        }
        else if(functSig=='getSensorCapabilityListForComposedService') {
            if (!(typeof cbSensorCapabilityListForComposedService === "undefined")) {
                cbSensorCapabilityListForComposedService(replyVal,parValues);
            }
        }
        else if(functSig=='getSensorCapabilityListForPartialService') {
            //
        }
        else if(functSig=='getDataCapabilityForComposedService') {
            if (!(typeof cbDataListForComposedService === "undefined")) {
                cbDataListForComposedService(replyVal,parValues);
            }
        }
        else if(functSig=='getDataCapabilityForPartialService') {
            //
        }
        else if(functSig=='getMostRecentDataCapabilityForComposedService') {
            //
        }
        else if(functSig=='getMostRecentDataCapabilityForPartialService') {
            //
        }
        else if(functSig=='getComposedServiceCapabilityList') {
            //
        }
        else if(functSig=='getPartialServiceCapabilityList') {
            //
        }
        else if(functSig=='getPartialServicesForComposedServiceID') {
            //
        }

    }
}


// Ajax for changing query status
//
//
// compatibility function for various browsers' xml parsing
function myParseInterfaceXMLGetTextXML(node)
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

function ParseInterfaceXML(oxml)
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
        handleResult(errorno, errordescr,replyVal,functSig,parValuesCSV,qstr);
    }
}



