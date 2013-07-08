
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

//
// TODO: logic for setting up rules. Adds a row in the table.
//
function addNewRule()
{

        var sIdx = 0;
        //
        // TODO: 1. Rule fields validation
        //
// -------| InvolvedCaps
        var InvolvedCapsDisp = "";
        var InvolvedCapsVal = "";
        if(document.getElementById('InvolvedCaps').style.display != 'none') {
            var InvolvedCapsArr = document.getElementById('InvolvedCaps').options;
            sIdx = document.getElementById('InvolvedCaps').selectedIndex;
            if(InvolvedCapsArr[sIdx].value != '#' && InvolvedCapsArr[sIdx].value !='')
            {
                InvolvedCapsDisp = InvolvedCapsArr[sIdx].innerHTML;
                InvolvedCapsVal = InvolvedCapsArr[sIdx].value;
            }
        }
        //
        // TODO: 1.1 We don't support multiple rules for the same capability for now, so prevent this
        //
        try {
            objRulCapsSlctd = document.serviceCompositionFrm.elements['rulCap[]'];
            var realCapsSlctd;
            if(objRulCapsSlctd != null)
            {
                if(typeof objRulCapsSlctd.type=="string")
                {
                    realCapsSlctd = new Array();
                    realCapsSlctd[0]=objRulCapsSlctd;
                }
                else
                {
                    realCapsSlctd = objRulCapsSlctd;
                }
                for (counter = 0; counter < realCapsSlctd.length; counter++)
                {
                    if (realCapsSlctd[counter].value == InvolvedCapsVal)
                    {
                        alert('Please select a different Capability. A rule is already defined for this capability!');
                        return;
                    }
                }
            }
        }
        catch(e)
        {
            alert(e);
        }

        // 2. add a row to the saved rules table

            var definedRulesTbl=document.getElementById("definedRulesTbl");
            //var rowCount = table.rows.length;
            //update the autoinc.
            var nextIdx = parseInt(document.getElementById("autoIncForDefinedRules").value)  + 1;
            document.getElementById("autoIncForDefinedRules").value = nextIdx;

            var row=definedRulesTbl.insertRow(-1);
            var cellCap=row.insertCell(0);
            var cellFunct=row.insertCell(1);
            var cellGSVal=row.insertCell(2);
            var cellUseTrigg=row.insertCell(3);
            var cellCondTrigg=row.insertCell(4);
            var cellActionTrigg=row.insertCell(5);
            var cellAddRem=row.insertCell(6);


// -------| functionsSens
var functionsSensDisp = "";
var functionsSensVal = "";
if(document.getElementById('functionsSens').style.display != 'none') {
    var functionsSensArr = document.getElementById('functionsSens').options;
    sIdx = document.getElementById('functionsSens').selectedIndex;
    if(functionsSensArr[sIdx].value != '#' && functionsSensArr[sIdx].value !='')
    {
        functionsSensDisp = functionsSensArr[sIdx].innerHTML;
        functionsSensVal = functionsSensArr[sIdx].value;
    }
}
// -------| functionsAct
var functionsActDisp = "";
var functionsActVal = "";
if(document.getElementById('functionsAct').style.display != 'none') {
    var functionsActArr = document.getElementById('functionsAct').options;
    sIdx = document.getElementById('functionsAct').selectedIndex;
    if(functionsActArr[sIdx].value != '#' && functionsActArr[sIdx].value !='')
    {
        functionsActDisp = functionsActArr[sIdx].innerHTML;
        functionsActVal = functionsActArr[sIdx].value;
    }
}
// -------| functionsThreshold
var functionsThresholdDisp = "";
var functionsThresholdVal = "";
if(document.getElementById('functionsThreshold').style.display != 'none') {
    var functionsThresholdArr = document.getElementById('functionsThreshold').options;
    sIdx = document.getElementById('functionsThreshold').selectedIndex;
    if(functionsThresholdArr[sIdx].value != '#' && functionsThresholdArr[sIdx].value !='')
    {
        functionsThresholdDisp = functionsThresholdArr[sIdx].innerHTML;
        functionsThresholdVal = functionsThresholdArr[sIdx].value;
    }
}
// -------| thresBoundSensTxb
    var thresBoundSensTxbVal = "";
if(document.getElementById('thresBoundSensTxb').style.display != 'none') {
    thresBoundSensTxbVal = document.getElementById('thresBoundSensTxb').value;
}
// -------| thresBoundActSel
var thresBoundActSelDisp = "";
var thresBoundActSelVal = "";
if(document.getElementById('thresBoundActSel').style.display != 'none') {
    var thresBoundActSelArr = document.getElementById('thresBoundActSel').options;
    sIdx = document.getElementById('thresBoundActSel').selectedIndex;
    if(thresBoundActSelArr[sIdx].value != '#' && thresBoundActSelArr[sIdx].value !='')
    {
        thresBoundActSelDisp = thresBoundActSelArr[sIdx].innerHTML;
        thresBoundActSelVal = thresBoundActSelArr[sIdx].value;
    }
}
// -------| notifyFlag
var notifyFlagDisp = "";
var notifyFlagVal = "";
if(document.getElementById('notifyFlag').style.display != 'none') {
    var notifyFlagArr = document.getElementById('notifyFlag').options;
    sIdx = document.getElementById('notifyFlag').selectedIndex;
    if(notifyFlagArr[sIdx].value != '#' && notifyFlagArr[sIdx].value !='')
    {
        notifyFlagDisp = notifyFlagArr[sIdx].innerHTML;
        notifyFlagVal = notifyFlagArr[sIdx].value;
    }
}
// -------| triggerCondition
var triggerConditionDisp = "";
var triggerConditionVal = "";
if(document.getElementById('triggerCondition').style.display != 'none') {
    var triggerConditionArr = document.getElementById('triggerCondition').options;
    sIdx = document.getElementById('triggerCondition').selectedIndex;
    if(triggerConditionArr[sIdx].value != '#' && triggerConditionArr[sIdx].value !='')
    {
        triggerConditionDisp = triggerConditionArr[sIdx].innerHTML;
        triggerConditionVal = triggerConditionArr[sIdx].value;
    }
}
// -------| triggerBoundSens
var triggerBoundSensVal = "";
if(document.getElementById('triggerBoundSens').style.display != 'none') {
    triggerBoundSensVal = document.getElementById('triggerBoundSens').value;
}
// -------| triggerAction
var triggerActionDisp = "";
var triggerActionVal = "";
if(document.getElementById('triggerAction').style.display != 'none') {
    var triggerActionArr = document.getElementById('triggerAction').options;
    sIdx = document.getElementById('triggerAction').selectedIndex;
    if(triggerActionArr[sIdx].value != '#' && triggerActionArr[sIdx].value !='')
    {
        triggerActionDisp = triggerActionArr[sIdx].innerHTML;
        triggerActionVal = triggerActionArr[sIdx].value;
    }
}
// -------| InvolvedCapsTriggerActuate
var involvedCapsTriggerActuateDisp = "";
var involvedCapsTriggerActuateVal = "";
if(document.getElementById('InvolvedCapsTriggerActuate').style.display != 'none') {
    var involvedCapsTriggerActuateArr = document.getElementById('InvolvedCapsTriggerActuate').options;
    sIdx = document.getElementById('InvolvedCapsTriggerActuate').selectedIndex;
    if(involvedCapsTriggerActuateArr[sIdx].value != '#' && involvedCapsTriggerActuateArr[sIdx].value !='')
    {
        involvedCapsTriggerActuateDisp = involvedCapsTriggerActuateArr[sIdx].innerHTML;
        involvedCapsTriggerActuateVal = involvedCapsTriggerActuateArr[sIdx].value;
    }
}
// -------| triggerValue
var triggerValueVal = "";
if(document.getElementById('triggerValue').style.display != 'none') {
    triggerValueVal = document.getElementById('triggerValue').value;
}
// -------| triggerBoundAct
var triggerBoundActDisp = "";
var triggerBoundActVal = "";
if(document.getElementById('triggerBoundAct').style.display != 'none') {
    var triggerBoundActArr = document.getElementById('triggerBoundAct').options;
    sIdx = document.getElementById('triggerBoundAct').selectedIndex;
    if(triggerBoundActArr[sIdx].value != '#' && triggerBoundActArr[sIdx].value !='')
    {
        triggerBoundActDisp = triggerBoundActArr[sIdx].innerHTML;
        triggerBoundActVal = triggerBoundActArr[sIdx].value;
    }
}
// -------| triggerNodesAct
var triggerNodesActDisp = "";
var triggerNodesActVal = "";
if(document.getElementById('triggerNodesAct').style.display != 'none') {
    var triggerNodesActArr = document.getElementById('triggerNodesAct').options;
    sIdx = document.getElementById('triggerNodesAct').selectedIndex;
    if(triggerNodesActArr[sIdx].value != '#' && triggerNodesActArr[sIdx].value !='')
    {
        triggerNodesActDisp = triggerNodesActArr[sIdx].innerHTML;
        triggerNodesActVal = triggerNodesActArr[sIdx].value;
    }
}
// -------|  END

        cellCap.innerHTML="<input type=\"hidden\" id=\"uniqRuleId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"rulCap[]\" id=\"rulCap_"+ nextIdx +"\" value=\""+InvolvedCapsVal+"\" />"+ InvolvedCapsDisp;
        cellFunct.innerHTML= "<input type=\"hidden\" name=\"rulFunctSens[]\" id=\"rulFunctSens_"+ nextIdx +"\"  value=\""+functionsSensVal+"\" />"+  functionsSensDisp;  //"New:: " + nextIdx;
        cellFunct.innerHTML+= "<input type=\"hidden\" name=\"rulFunctAct[]\" id=\"rulFunctAct_"+ nextIdx +"\" value=\""+functionsActVal+"\" />"+  functionsActDisp;
        cellGSVal.innerHTML="<input type=\"hidden\" name=\"rulFunctThresh[]\" id=\"rulFunctThresh_"+ nextIdx +"\" value=\""+functionsThresholdVal+"\" />"+  functionsThresholdDisp;
        cellGSVal.innerHTML+="<input type=\"hidden\" name=\"rulBoundSens[]\" id=\"rulBoundSens_"+ nextIdx +"\" value=\""+thresBoundSensTxbVal+"\" />"+  thresBoundSensTxbVal;
        cellGSVal.innerHTML+="<input type=\"hidden\" name=\"rulBoundAct[]\"  id=\"rulBoundAct_"+ nextIdx +"\" value=\""+thresBoundActSelVal+"\" />"+  thresBoundActSelDisp;

        cellUseTrigg.innerHTML="<input type=\"hidden\" name=\"rulHasTrigger[]\" id=\"rulHasTrigger_"+ nextIdx +"\" value=\""+notifyFlagVal+"\" />"+  notifyFlagDisp;
        cellCondTrigg.innerHTML="<input type=\"hidden\" name=\"rulTrigCond[]\" id=\"rulTrigCond_"+ nextIdx +"\" value=\""+triggerConditionVal+"\" />"+  triggerConditionDisp;
        cellCondTrigg.innerHTML+="<input type=\"hidden\" name=\"rulTrigBound[]\" id=\"rulTrigBound_"+ nextIdx +"\" value=\""+triggerBoundSensVal+"\" />"+  triggerBoundSensVal;

        cellActionTrigg.innerHTML="<input type=\"hidden\" name=\"rulTrigAct[]\" id=\"rulTrigAct_"+ nextIdx +"\" value=\""+triggerActionVal+"\" />"+  triggerActionDisp + "<br/>";
        cellActionTrigg.innerHTML+="<input type=\"hidden\" name=\"rulTrigCapsAct[]\" id=\"rulTrigCapsAct_"+ nextIdx +"\" value=\""+involvedCapsTriggerActuateVal+"\" />"+  involvedCapsTriggerActuateDisp +"<br/>";
        cellActionTrigg.innerHTML+="<input type=\"hidden\" name=\"rulTrigVal[]\" id=\"rulTrigVal_"+ nextIdx +"\" value=\""+triggerValueVal+"\" />"+  triggerValueVal;
        cellActionTrigg.innerHTML+="<input type=\"hidden\" name=\"rulTrigBoundAct[]\" id=\"rulTrigBoundAct_"+ nextIdx +"\" value=\""+triggerBoundActVal+"\" />"+  triggerBoundActDisp+"<br/>";
        cellActionTrigg.innerHTML+="<input type=\"hidden\" name=\"rulTrigNodesAct[]\" id=\"rulTrigNodesAct_"+ nextIdx +"\" value=\""+triggerNodesActVal+"\" />"+  triggerNodesActDisp;

            //TODO there should be an alert/confirmation for deleting
            cellAddRem.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeRule(\"uniqRuleId_"+nextIdx+"\");\'>Remove</a>";
        if(document.getElementById('definedRulesDiv').style.display == 'none')
        {
            document.getElementById('definedRulesDiv').style.display = 'block';
        }
        // TODO 3. Reset fields for new rule
        resetAllRuleFields('all');
}

//
// TODO: logic for removing rules. Removes a row from the table.
//
function removeRule(ruleId)
{
    var definedRulesTbl=document.getElementById("definedRulesTbl");
    if(definedRulesTbl!=null)
    {
        deleteRowByHiddenId('definedRulesTbl', ruleId);
        if(definedRulesTbl.rows.length == 2) {
            document.getElementById('rulesANDforNotifyCxBx').checked = false;
            document.getElementById('definedRulesDiv').style.display = 'none';
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

        for(var i=0; i<rowCount; i++) {
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


//
// mode: if 'all' resets everything. Else there are cases or partial reset
//
function resetAllRuleFields(mode)
{
    // InvolvedCaps
    // TODO:!!!!!!!!!!!!!!!
    if(mode=='all')
    {
        // InvolvedCaps   //set selected index to 0
        document.getElementById('InvolvedCaps').selectedIndex = 0;
        document.getElementById('functionsSens').selectedIndex = 0;
        document.getElementById('functionsSens').style.display = "none";
        document.getElementById('functionsAct').selectedIndex = 0;
        document.getElementById('functionsAct').style.display = "none";
        document.getElementById('thresBoundActSel').selectedIndex = 0;
        document.getElementById('functionsThreshold').style.display = "none";
        document.getElementById('thresBoundSensTxb').value = "";
        document.getElementById('thresBoundSensTxb').style.display = "none";
        document.getElementById('thresBoundActSel').selectedIndex = 0;
        document.getElementById('thresBoundActSel').style.display = "none";
        document.getElementById('notifyFlag').selectedIndex = 0;
        document.getElementById('notifyFlag').style.display = "none";
    }
    else if(mode=='newSens')
    {
        document.getElementById('functionsSens').selectedIndex = 0;     //sens
        document.getElementById('functionsSens').style.display = "block";
        document.getElementById('functionsAct').selectedIndex = 0;
        document.getElementById('functionsAct').style.display = "none";
        document.getElementById('functionsThreshold').selectedIndex = 0;
        document.getElementById('functionsThreshold').style.display = "block";
        document.getElementById('thresBoundSensTxb').value = "";
        document.getElementById('thresBoundSensTxb').style.display = "block";
        document.getElementById('thresBoundActSel').selectedIndex = 0;
        document.getElementById('thresBoundActSel').style.display = "none";
        document.getElementById('notifyFlag').selectedIndex = 0;
        document.getElementById('notifyFlag').style.display = "block";
    }
    else if(mode=='newAct')
    {
        document.getElementById('functionsSens').selectedIndex = 0;
        document.getElementById('functionsSens').style.display = "none";
        document.getElementById('functionsAct').selectedIndex = 0;
        document.getElementById('functionsAct').style.display = "block";
        document.getElementById('functionsThreshold').selectedIndex = 0;
        document.getElementById('functionsThreshold').style.display = "none";
        document.getElementById('thresBoundSensTxb').style.display = "none";
        document.getElementById('thresBoundActSel').selectedIndex = 0;
        document.getElementById('thresBoundActSel').style.display = "block";
        document.getElementById('notifyFlag').selectedIndex = 0;
        document.getElementById('notifyFlag').style.display = "none";     // an actuating capability cannot be linked to a trigger!
    }

    document.getElementById('triggerCondition').selectedIndex = 0;
    document.getElementById('triggerCondition').style.display = "none";
    document.getElementById('triggerBoundSens').value = "";
    document.getElementById('triggerBoundSens').style.display = "none";
    document.getElementById('triggerAction').selectedIndex = 0;
    document.getElementById('triggerAction').style.display = "none";
    document.getElementById('InvolvedCapsTriggerActuate').selectedIndex = 0;
    document.getElementById('InvolvedCapsTriggerActuate').style.display = "none";
    document.getElementById('triggerValue').value = "";
    document.getElementById('triggerValue').style.display = "none";
    document.getElementById('triggerBoundAct').selectedIndex = 0;
    document.getElementById('triggerBoundAct').style.display = "none";
    document.getElementById('triggerNodesAct').selectedIndex = 0;
    document.getElementById('triggerNodesAct').style.display = "none";
}

// ----------------------------------------------
// AJAX for selectedInvCapability
//
function RPostSelectInvCapability(strpost)
{
    var xmlhttp=null;
    xmlhttp=getXMLHTTPRequest();
    if (xmlhttp==null )
    {
        alert("Your browser does not support XMLHTTP.");
    }
    else
    {
        xmlhttp.open("POST", "<%=request.getContextPath()%>/actionsaux/nTransactIsActuatingCapability.jsp", true);
        xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

        xmlhttp.send(strpost);
        xmlhttp.onreadystatechange=function()
        {
            if (xmlhttp.readyState==4)
            {
                if (xmlhttp.status==200)
                {
                    XML_Response = xmlhttp.responseXML;
                    //alert(XML_Response);
                    ParseXMLSelectInvolvedCapability(XML_Response);
                }
                else
                {
                    alert("Problem retrieving XML data");
                    document.getElementById('InvolvedCaps').disabled = false;
                }
            }
        }
    }
}

function ParseXMLSelectInvolvedCapability(oxml)
{
    var errorno = "";
    var errordescr = "";
    var statusmy = "";
    var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
    //alert("Answer' fields "+answer.childNodes.length);

    for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
        var node = answer.childNodes.item(iNode);
        if(node.tagName == "result")
        {
            statusmy = node.getAttribute('status');
            //alert(statusmy);
            if(statusmy==0)
            {
                //resetRuleForSensedCapability
                //alert("Sensing");
                resetAllRuleFields('newSens');
            }else
            {
                //resetRuleForActuatingCapability
                //alert("Actuating");
                resetAllRuleFields('newAct');
            }

        }
        else if(node.tagName == "error")
        {
            errorno = node.getAttribute('errno');
            errordescr = node.getAttribute('errdesc');
        }
    }
    transactAftermathSelectedCapability(errorno, errordescr);
}

function transactAftermathSelectedCapability(check, statusDescription)
{
    if(check != 0)
    {
        alert(statusDescription);
    }
    document.getElementById('InvolvedCaps').disabled = false;
}

//
//  TODO: To be called on document.onload (or we could have an empty "Select Capability" field on top in the selection).
//
function onSelectedCapabilitySet()
{
    //1. prompt selected value
    document.getElementById('InvolvedCaps').disabled = true;

    var optionInvolvedCapabilityForRuleArr = document.getElementById('InvolvedCaps').options;
    var sCapabilityForRuleIdx = document.getElementById('InvolvedCaps').selectedIndex;
    if(optionInvolvedCapabilityForRuleArr[sCapabilityForRuleIdx].value == '#' || optionInvolvedCapabilityForRuleArr[sCapabilityForRuleIdx].value == '')
    {
        //1.1 if "" empty then do resetfields (alert though).
        //alert("empty capability set!");
        resetAllRuleFields('all');
        document.getElementById('InvolvedCaps').disabled = false;
        return;
    }

    var capPostString = 'capid='+ optionInvolvedCapabilityForRuleArr[sCapabilityForRuleIdx].value;
    //alert(capPostString);
    RPostSelectInvCapability(capPostString);
    //check if actuating or not and display the correct drop down selections.
    //3. call isActuating (AJAX) and alert the result
    //4. if true show the right controls, hide the others!

}

function onSelectedCapFunctionSet()
{
    // do nothing;
}

function onSetTriggerSet()
{
    document.getElementById('notifyFlag').disabled = true;
    var optionsEnableTriggerForRuleArr = document.getElementById('notifyFlag').options;
    var sEnableTriggerForRuleIdx = document.getElementById('notifyFlag').selectedIndex;
    if(optionsEnableTriggerForRuleArr[sEnableTriggerForRuleIdx].value == '#' || optionsEnableTriggerForRuleArr[sEnableTriggerForRuleIdx].value == '' || optionsEnableTriggerForRuleArr[sEnableTriggerForRuleIdx].value == 'NO')
    {
        //should be enabled only for sensing Capabilities
        document.getElementById('triggerCondition').style.display = "none";
        document.getElementById('triggerCondition').selectedIndex = 0;
        document.getElementById('triggerBoundSens').style.display = "none";
        document.getElementById('triggerBoundSens').value = "";
        document.getElementById('triggerAction').style.display = "none";
        document.getElementById('triggerAction').selectedIndex = 0;
        document.getElementById('InvolvedCapsTriggerActuate').style.display = "none";
        document.getElementById('InvolvedCapsTriggerActuate').selectedIndex = 0;
        document.getElementById('triggerValue').style.display = "none";
        document.getElementById('triggerValue').value = "";
        document.getElementById('triggerBoundAct').style.display = "none";
        document.getElementById('triggerBoundAct').selectedIndex = 0;
        document.getElementById('triggerNodesAct').style.display = "none";
        document.getElementById('triggerNodesAct').selectedIndex = 0;
    }
    else if(optionsEnableTriggerForRuleArr[sEnableTriggerForRuleIdx].value == 'YES') {
        document.getElementById('triggerCondition').style.display = "block";
        document.getElementById('triggerCondition').selectedIndex = 0;
        document.getElementById('triggerBoundSens').style.display = "block";
        document.getElementById('triggerBoundSens').value = "";
        document.getElementById('triggerAction').style.display = "block";
        document.getElementById('triggerAction').selectedIndex = 0;
        document.getElementById('InvolvedCapsTriggerActuate').style.display = "none";
        document.getElementById('InvolvedCapsTriggerActuate').selectedIndex = 0;
        document.getElementById('triggerValue').style.display = "none";
        document.getElementById('triggerValue').value = "";
        document.getElementById('triggerBoundAct').style.display = "none";
        document.getElementById('triggerBoundAct').selectedIndex = 0;
        document.getElementById('triggerNodesAct').style.display = "none";
        document.getElementById('triggerNodesAct').selectedIndex = 0;
    }
    document.getElementById('notifyFlag').disabled = false;
    return;
}

function onTriggerActionSet()
{
    document.getElementById('triggerAction').disabled = true;
    var optionTriggerActionForRuleArr = document.getElementById('triggerAction').options;
    var sTriggerActionForRuleIdx = document.getElementById('triggerAction').selectedIndex;
    if(optionTriggerActionForRuleArr[sTriggerActionForRuleIdx].value == '#' || optionTriggerActionForRuleArr[sTriggerActionForRuleIdx].value == '' || optionTriggerActionForRuleArr[sTriggerActionForRuleIdx].value == 'set')
    {
        document.getElementById('InvolvedCapsTriggerActuate').style.display = "block";
        document.getElementById('InvolvedCapsTriggerActuate').selectedIndex = 0;
        document.getElementById('triggerValue').style.display = "none";
        document.getElementById('triggerValue').value = "";
        document.getElementById('triggerBoundAct').style.display = "block";
        document.getElementById('triggerBoundAct').selectedIndex = 0;
        document.getElementById('triggerNodesAct').style.display = "block";
        document.getElementById('triggerNodesAct').selectedIndex = 0;
    }
    else if(optionTriggerActionForRuleArr[sTriggerActionForRuleIdx].value == 'email' || optionTriggerActionForRuleArr[sTriggerActionForRuleIdx].value =='sms' || optionTriggerActionForRuleArr[sTriggerActionForRuleIdx].value =='history') {
        document.getElementById('InvolvedCapsTriggerActuate').style.display = "none";
        document.getElementById('InvolvedCapsTriggerActuate').selectedIndex = 0;
        document.getElementById('triggerValue').style.display = "block";
        document.getElementById('triggerValue').value = "";
        document.getElementById('triggerBoundAct').style.display = "none";
        document.getElementById('triggerBoundAct').selectedIndex = 0;
        document.getElementById('triggerNodesAct').style.display = "none";
        document.getElementById('triggerNodesAct').selectedIndex = 0;
    } else {
        // hide all
        document.getElementById('InvolvedCapsTriggerActuate').style.display = "none";
        document.getElementById('InvolvedCapsTriggerActuate').selectedIndex = 0;
        document.getElementById('triggerValue').style.display = "none";
        document.getElementById('triggerValue').value = "";
        document.getElementById('triggerBoundAct').style.display = "none";
        document.getElementById('triggerBoundAct').selectedIndex = 0;
        document.getElementById('triggerNodesAct').style.display = "none";
        document.getElementById('triggerNodesAct').selectedIndex = 0;

    }
    document.getElementById('triggerAction').disabled = false;
    return;
}

function onTriggerActionActuationSet()
{

}

//
// To be called when the form is submitted to evaluate field values.  Do not allow nulls where value is expected. And at least one rule set!!
//
function submitNewServiceForm()
{

}
// ----------------------------------------------



