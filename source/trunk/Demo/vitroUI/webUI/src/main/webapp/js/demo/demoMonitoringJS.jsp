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

var runBGTask = false;

function getObject( Obj ){
    var theObj;
    if ( typeof Obj == 'string'){
        theObj = document.getElementById(Obj);
    }
    else
        theObj = Obj;
    return theObj;
}

var globalstatusDesc = '&nbsp;';


function getAlerts()
{
    //alert(runBGTask);
    runBGTask  = true;
    serializedData = '';
    // fire off the request to /demoAlertsNotifyAjax.jsp
    var request = $.ajax({
        url: "<%=request.getContextPath()%>/roleEndUser/demo/demoAlertsNotifyAjax.jsp",
        type: "post",
        data: serializedData
    });

    // callback handler that will be called on success
    request.done(function (response, textStatus, jqXHR){
        // log a message to the console
        console.log("Hooray, ajax jquery worked!");
        if(runBGTask){
            ParseXML(response);
        }
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
        if(runBGTask){
            getAlerts();
        }
    });

}


// Ajax for changing query status
//
//
// compatibility function for various browsers' xml parsing
function myGetTextXML(node)
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

function ParseXML(oxml)
{
    var errorno = "";
    var errordescr = "";
    var AlertTitle = "";
    var AlertTime = "";
    var AlertDetails = "";
    var AlertType = 0;

    var foundOneEntry =new Boolean();
    foundOneEntry = false;

    var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
    for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
        //
        var node = answer.childNodes.item(iNode);
        if(node.tagName == "AlertTitle")
        {
            foundOneEntry = true;
            AlertTitle = myGetTextXML(node);
        }
        else if(node.tagName == "AlertTime")
        {
            AlertTime = myGetTextXML(node);
        }
        else if(node.tagName == "AlertDetails")
        {
            AlertDetails = myGetTextXML(node);
        }
        else if(node.tagName == "AlertType")
        {
            AlertType = myGetTextXML(node);
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
        addNewDemoNotify(AlertTitle, AlertTime, AlertDetails, AlertType );
    }
}


//
// TODO: logic for setting up rules. Adds a row in the table.
//
function addNewDemoNotify(pAlertTitle, pAlertTime, pAlertDetails, pAlertType )
{

    var sIdx = 0;

    // 2. add a row to the notifications table
    
    var alertsNotificationsTbl=document.getElementById("alertsNotificationsTbl");
    //var rowCount = table.rows.length;
    //update the autoinc.
    var nextIdx = parseInt(document.getElementById("autoIncForAlertsNotifications").value)  + 1;
    document.getElementById("autoIncForAlertsNotifications").value = nextIdx;
    
    //var row=alertsNotificationsTbl.insertRow(-1); // add at the end
    var row=alertsNotificationsTbl.insertRow(2);
    var cellMsg=row.insertCell(0);
    var cellTime=row.insertCell(1);
    var cellDetails=row.insertCell(2);
    var cellAddRem=row.insertCell(3);

    var alertMsgVal = '';
    var alertMsgDisp = pAlertTitle + ' ID: ' + nextIdx;
    var alertTimeVal = '';
    var alertTimeDisp = pAlertTime;
    var alertDetailsDisp = pAlertDetails;

    if(parseInt(pAlertType) == 1)
    {
        row.style.background="#a4e6a3";
        showAlertDetails("uniqRuleId_"+nextIdx, pAlertTitle, alertTimeDisp, pAlertDetails, pAlertType);
    }
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


function generate(type) {
    var n = noty({
        text: type,
        type: type,
        layout: 'center'
    });
    //console.log(type + ' - ' + n.options.id);
    return n;
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

function resetViewForNewService()
{
    runBGTask  = false;
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
            while(alertsNotificationsTbl.rows.length>2)
            {
                rowIndex = alertsNotificationsTbl.rows.length - 1;
                alertsNotificationsTbl.deleteRow(rowIndex);
            }
            if(alertsNotificationsTbl.rows.length <= 2) {
                document.getElementById('alertsAndNotifyDiv').style.display = 'none';
            }
        }
        catch(e)
        {
            alert(e);
        }
    }
}

function removeRule(ruleId)
{
    var alertsNotificationsTbl=document.getElementById("alertsNotificationsTbl");
    if(alertsNotificationsTbl!=null)
    {
        deleteRowByHiddenId('alertsNotificationsTbl', ruleId);
        if(alertsNotificationsTbl.rows.length <= 2) {
            document.getElementById('alertsAndNotifyDiv').style.display = 'none';
        }
    }
}

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

