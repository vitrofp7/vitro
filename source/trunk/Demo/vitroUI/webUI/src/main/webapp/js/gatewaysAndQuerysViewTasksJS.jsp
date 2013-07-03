<%@page session='false' contentType='application/x-javascript' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*'
 %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>

// select or unselect all checkboxes for the listed VSNs
 function switchSelectAllVSNs()
 {
    numOfChecked = 0;
    objVSNsSlctd = document.formbasic.elements['quiCbox[]'];
    var realVSNsSlctd ="";
    var strVSNsSlctd ="";
    var strAllAvailVSNs ="";
    if(objVSNsSlctd != null)
    {
        if(typeof objVSNsSlctd.type=="string")
        {
            realVSNsSlctd = new Array();
            realVSNsSlctd[0]=objVSNsSlctd;
        }
        else
        {
            realVSNsSlctd = objVSNsSlctd;
        }
        for (counter = 0; counter < realVSNsSlctd.length; counter++)
        {
            // needed for post string creation (not here) todo: move this where appropriate)
            strAllAvailVSNs = (counter == 0)? "allvsn[]="+ realVSNsSlctd[counter].value: strAllAvailVSNs + '&'+ "allvsn[]="+ realVSNsSlctd[counter].value;

            // If a checkbox has been selected it will return true
            // (If not it will return false)
            if (realVSNsSlctd[counter].checked)
            {
                strVSNsSlctd = (numOfChecked == 0)? "selvsn[]="+counter : strVSNsSlctd +'&'+ "selvsn[]="+counter;
                numOfChecked += 1;
            }
        }
        if(numOfChecked > 0)
        {
            //unselect all
            for (counter = 0; counter < realVSNsSlctd.length; counter++)
            {
                realVSNsSlctd[counter].checked = false;
            }
        }
        else
        {
            //select all
            for (counter = 0; counter < realVSNsSlctd.length; counter++)
            {
                realVSNsSlctd[counter].checked = true;
            }
        }
    }
    if(strAllAvailVSNs =='')
    {
        alert('No VSNs exist in the list so no action can be performed!');
        return;
    }

 }

// perform batch actions on the selected Composite Services   (VSPeditVitroComposedServices.jsp)
 function actOnSelectedCompositeServices(actionValue)
 {
    if (actionValue == '')
    {
        alert('No action was specified!');
        return;
    }
    var realVSNsSlctd ="";
    var strVSNsSlctd ="";
    var strAllAvailVSNs ="";
    numOfChecked = 0;
    objVSNsSlctd = document.getElementsByName('selectRow');

    if(objVSNsSlctd != null)
    {
        if(typeof objVSNsSlctd.type=="string")
        {
            realVSNsSlctd = new Array();
            realVSNsSlctd[0]=objVSNsSlctd;
        }
        else
        {
            realVSNsSlctd = objVSNsSlctd;
        }
        for (counter = 0; counter < realVSNsSlctd.length; counter++)
        {
            // needed for post string creation (not here) todo: move this where appropriate)
            strAllAvailVSNs = (counter == 0)? "allvsn[]="+ realVSNsSlctd[counter].value: strAllAvailVSNs + '&'+ "allvsn[]="+ realVSNsSlctd[counter].value;

            // If a checkbox has been selected it will return true
            // (If not it will return false)
            if (realVSNsSlctd[counter].checked)
            {
                strVSNsSlctd = (numOfChecked == 0)? "selvsn[]="+counter : strVSNsSlctd +'&'+ "selvsn[]="+counter;
                numOfChecked += 1;
            }
        }
    }
    // first check if anything exists in the list and if anything was selected
    if(strAllAvailVSNs =='')
    {
        alert('No Composed Services exist in the list so no action can be performed!');
        //getObject('Submit0').disabled = false;
        return;
    }
    else if( strVSNsSlctd =='')
    {
        alert('You should select at least one Composed Service from the list!');
        //getObject('Submit0').disabled = false;
        return;
    }

    // in this case, (new UI) we skip the confirmation box (for now until a popup confirmation dialogue is re-introduced)
    if (actionValue == 'removeBatch')
    {
        proceedWithActionOnSelectedCompositeServices(true, actionValue);
    }
    else if(actionValue == 'stopBatch')
    {
        proceedWithActionOnSelectedCompositeServices(true, actionValue);

    }
    else if(actionValue == 'startBatch')
    {
        proceedWithActionOnSelectedCompositeServices(true, actionValue);
    }
    else {
        proceedWithActionOnSelectedCompositeServices(false, null);
    }
 }

function proceedWithActionOnSelectedCompositeServices(pYesNo, pActionValue)
{
    if(pYesNo ==false)
    {
        return;
    }
    else
    {
        var realVSNsSlctd ="";
        var strVSNsSlctd ="";
        var strAllAvailVSNs ="";
        numOfChecked = 0;
        objVSNsSlctd = document.getElementsByName('selectRow');
        //todo: code repetition should be removed from here or above!
        if(objVSNsSlctd != null)
        {
            if(typeof objVSNsSlctd.type=="string")
            {
                realVSNsSlctd = new Array();
                realVSNsSlctd[0]=objVSNsSlctd;
            }
            else
            {
                realVSNsSlctd = objVSNsSlctd;
            }
            for (counter = 0; counter < realVSNsSlctd.length; counter++)
            {
                // needed for post string creation (not here) todo: move this where appropriate)
                strAllAvailVSNs = (counter == 0)? "allvsn[]="+ realVSNsSlctd[counter].value: strAllAvailVSNs + '&'+ "allvsn[]="+ realVSNsSlctd[counter].value;

                // If a checkbox has been selected it will return true
                // (If not it will return false)
                if (realVSNsSlctd[counter].checked)
                {
                    strVSNsSlctd = (numOfChecked == 0)? "selvsn[]="+counter : strVSNsSlctd +'&'+ "selvsn[]="+counter;
                    numOfChecked += 1;
                }
            }
        }

        if(numOfChecked > 0)
        {
            // Prepare the POST string
            strAllAvailVSNs = (strAllAvailVSNs=='')? strAllAvailVSNs : strAllAvailVSNs + '&';
            strVSNsSlctd = (strVSNsSlctd=='')? strVSNsSlctd : strVSNsSlctd + '&';
            strSend=  strAllAvailVSNs + strVSNsSlctd + 'psaction='+pActionValue;
            strSend+= '&cmp=1';
            //alert(strSend);
            RPost(strSend);
        }
    }
}


// --------------------------------------------------------------------------------------------------
 //for oldDebugScreen: perform batch actions on the selected VSNs
 function actOnSelectedVSNs(actionValue)
 {
    if (actionValue == '')
    {
        alert('No action was specified!');
        return;
    }
    var realVSNsSlctd ="";
    var strVSNsSlctd ="";
    var strAllAvailVSNs ="";
    numOfChecked = 0;
    objVSNsSlctd = document.formbasic.elements['quiCbox[]'];

    if(objVSNsSlctd != null)
    {
        if(typeof objVSNsSlctd.type=="string")
        {
            realVSNsSlctd = new Array();
            realVSNsSlctd[0]=objVSNsSlctd;
        }
        else
        {
            realVSNsSlctd = objVSNsSlctd;
        }
        for (counter = 0; counter < realVSNsSlctd.length; counter++)
        {
            // needed for post string creation (not here) todo: move this where appropriate)
            strAllAvailVSNs = (counter == 0)? "allvsn[]="+ realVSNsSlctd[counter].value: strAllAvailVSNs + '&'+ "allvsn[]="+ realVSNsSlctd[counter].value;

            // If a checkbox has been selected it will return true
            // (If not it will return false)
            if (realVSNsSlctd[counter].checked)
            {
                strVSNsSlctd = (numOfChecked == 0)? "selvsn[]="+counter : strVSNsSlctd +'&'+ "selvsn[]="+counter;
                numOfChecked += 1;
            }
        }
    }
    // first check if anything exists in the list and if anything was selected
    if(strAllAvailVSNs =='')
    {
        alert('No VSNs exist in the list so no action can be performed!');
        //getObject('Submit0').disabled = false;
        return;
    }
    else if( strVSNsSlctd =='')
    {
        alert('You should select at least one VSN from the list!');
        //getObject('Submit0').disabled = false;
        return;
    }

    // then, since we have a non-blocking modalbox for the removeBatch, we handle it in separate functions
    if (actionValue == 'removeBatch')
    {
        Modalbox.show('<div class=\'warning\'><p>Are you sure you want to remove the selected VSNs?</p> <table><tr><td><input type=\'button\' value=\'Yes, remove!\' onclick=\'Modalbox.hide({afterHide: function() { proceedWithActionOnSelectedVSNs(true, \"removeBatch\")} }); return false;\' /> or <input type=\'button\' value=\'No, leave them!\' onclick=\'Modalbox.hide({afterHide: function() { proceedWithActionOnSelectedVSNs(false, null) }}); return false;\' /></td></tr></div>', {title: this.title, width: 300});
    }
    else if(actionValue == 'stopBatch')
    {
        Modalbox.show('<div class=\'warning\'><p>Are you sure you want to stop the selected VSNs?</p> <table><tr><td><input type=\'button\' value=\'Yes, stop them!\' onclick=\'Modalbox.hide({afterHide: function() { proceedWithActionOnSelectedVSNs(true, \"stopBatch\") }}); return false;\' /> or <input type=\'button\' value=\'No, leave them!\' onclick=\'Modalbox.hide({afterHide: function() { proceedWithActionOnSelectedVSNs(false, null) }}); return false;\' /></td></tr></div>', {title: this.title, width: 300});

    }
    else if(actionValue == 'startBatch')
    {
        Modalbox.show('<div class=\'warning\'><p>Are you sure you want to start the selected VSNs?</p> <table><tr><td><input type=\'button\' value=\'Yes, start them!\' onclick=\'Modalbox.hide({afterHide: function() { proceedWithActionOnSelectedVSNs(true, \"startBatch\") }}); return false;\' /> or <input type=\'button\' value=\'No, leave them!\' onclick=\'Modalbox.hide({afterHide: function() { proceedWithActionOnSelectedVSNs(false, null) }}); return false;\' /></td></tr></div>', {title: this.title, width: 300});
    }
 }

//for oldDebugScreen:
function proceedWithActionOnSelectedVSNs(pYesNo, pActionValue)
 {
    if(pYesNo ==false)
    {
        return;
    }
    else
    {
        var realVSNsSlctd ="";
        var strVSNsSlctd ="";
        var strAllAvailVSNs ="";
        numOfChecked = 0;
        objVSNsSlctd = document.formbasic.elements['quiCbox[]'];
        //todo: code repetition should be removed from here or above!
        if(objVSNsSlctd != null)
        {
            if(typeof objVSNsSlctd.type=="string")
            {
                realVSNsSlctd = new Array();
                realVSNsSlctd[0]=objVSNsSlctd;
            }
            else
            {
                realVSNsSlctd = objVSNsSlctd;
            }
            for (counter = 0; counter < realVSNsSlctd.length; counter++)
            {
                // needed for post string creation (not here) todo: move this where appropriate)
                strAllAvailVSNs = (counter == 0)? "allvsn[]="+ realVSNsSlctd[counter].value: strAllAvailVSNs + '&'+ "allvsn[]="+ realVSNsSlctd[counter].value;

                // If a checkbox has been selected it will return true
                // (If not it will return false)
                if (realVSNsSlctd[counter].checked)
                {
                    strVSNsSlctd = (numOfChecked == 0)? "selvsn[]="+counter : strVSNsSlctd +'&'+ "selvsn[]="+counter;
                    numOfChecked += 1;
                }
            }
        }

        if(numOfChecked > 0)
        {
            // Prepare the POST string
            strAllAvailVSNs = (strAllAvailVSNs=='')? strAllAvailVSNs : strAllAvailVSNs + '&';
            strVSNsSlctd = (strVSNsSlctd=='')? strVSNsSlctd : strVSNsSlctd + '&';
            strSend=  strAllAvailVSNs + strVSNsSlctd + 'psaction='+pActionValue;
            strSend+= '&cmp=0';
            //alert(strSend);
            RPost(strSend);
        }
    }
 }


 //auxilliary function
 function isNumeric(strString)
 //  check for valid numeric strings	
 {
    var strValidChars = "0123456789.-";
    var strChar;
    var blnResult = true;
 
    if (strString.length == 0) return false;
 
    //  test strString consists of valid characters listed above
    for (i = 0; i < strString.length && blnResult == true; i++)
    {
       strChar = strString.charAt(i);
       if (strValidChars.indexOf(strChar) == -1)
       {
            blnResult = false;
        }
    }
    return blnResult;
 }

    //aux function
    function deleteRow(tableID, rowId) {
        //alert('table: '+ tableID + ' row id: '+ rowId);
        try {
            table = document.getElementById(tableID);
            var rowCount = table.rows.length;
            //alert('rowCount: ' + rowCount);
            for(var i=0; i<rowCount; i++) {
                var row = table.rows[i];
//                var chkbox = row.cells[0].childNodes[0];
//                if(null != chkbox && true == chkbox.checked) {
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
 
 function modifiedRefreshPeriod(qid)
 {
    if(qid == null || qid =="")
        return;
    var newrefreshValue;
    newrefreshValue = document.getElementById('refreshPer_'+qid).value;
    if(newrefreshValue == null ||  (!isNumeric(newrefreshValue)) ||  newrefreshValue <= <%=RefreshableResults.minPeriodOfLinkRefresh %>)
    {
        alert('Minimum value for refresh interval is <%=RefreshableResults.minPeriodOfLinkRefresh %> seconds');
        document.getElementById('refreshPer_'+qid).value = <%=RefreshableResults.minPeriodOfLinkRefresh %>;
        newrefreshValue = <%=RefreshableResults.minPeriodOfLinkRefresh %>;
    }
    document.getElementById('refreshPeriodHrefDiv_'+qid).innerHTML = '<a href="<%=request.getContextPath()%>/roleEndUser/RefreshableResults?quid='+qid+'&period='+newrefreshValue+'" target="_blank" >Updateable KML</a>';
 }

 //new
 function modifiedSamplePeriod(vsnId) {
    if(vsnId == null || vsnId ==""){
        console.log('Invalid VSN id defined');
        return;
    }
    console.log('Modifying period for VSN id: ' + vsnId);
    var newSamplePeriodSecsValue;
    newSamplePeriodSecsValue = document.getElementById('samplePer_'+vsnId).value;
    if(newSamplePeriodSecsValue == null ||  (!isNumeric(newSamplePeriodSecsValue)) || (newSamplePeriodSecsValue!=0 && newSamplePeriodSecsValue < <%=Capability.minSamplingPeriod %>))
    {
        alert('Minimum value for Sample Period is set to  <%= Integer.toString(Capability.minSamplingPeriod) %> seconds');
        document.getElementById('samplePer_'+vsnId).value =  <%= Integer.toString(Capability.minSamplingPeriod) %>;
        newSamplePeriodSecsValue = <%=Integer.toString(Capability.minSamplingPeriod) %>;
    }
    strSend = 'vsnId='+ vsnId +'&'+
    'newPeriod='+ newSamplePeriodSecsValue;
    //alert(strSend);
    RPostChangePeriod(strSend + '&psaction=changeSamplePeriod');

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
    
 function transactAftermath(qid, newQueryStatus, errno, errordescr)
 {
    // here and only here we change the status html text.
    if(errno == 0 && errordescr == 'OK')
    {
        if(newQueryStatus == <%=QueryDefinition.STATUS_RUNNING %>)    {
            document.getElementById('queryStatusActionTextDiv_'+qid).innerHTML = "Stop";
            if(document.getElementById('queryStatusActionImgDiv_'+qid)!=null) {
                document.getElementById('queryStatusActionImgDiv_'+qid).innerHTML = "<img title=\"Pause service\" src=\"<%=request.getContextPath()%>/img/pauseVSN52h.png\" style=\"height: 32px;width: 32px;\" />";
            }
        }
        else {
            document.getElementById('queryStatusActionTextDiv_'+qid).innerHTML = "Start";
            if(document.getElementById('queryStatusActionImgDiv_'+qid)!=null) {
                document.getElementById('queryStatusActionImgDiv_'+qid).innerHTML = "<img title=\"Start service\" src=\"<%=request.getContextPath()%>/img/startVSN52h.png\" style=\"height: 32px;width: 32px;\" />";
            }
        }
        if(errordescr.toLowerCase() != "OK".toLowerCase())
            alert(errordescr);
    }
    else if(errno == 0 && errordescr == 'REMOVED')
    {
        objTr = document.getElementById('tr_'+qid);
        //alert('tr_'+qid);
        //alert(objTr);
        if (objTr != null)
        {
            //alert("Dummy test of removing a VSN");
            objTr.style.display = 'none';
            deleteRow( 'allVSNsTbl', objTr.id);
        }
    }
    else
        alert("Problem processing data: "+errordescr);
    
    //Effect.Fade('progressMsg');
    //globalstatusDesc = statusDescription;
    //setTimeout("showResultStatus()", 600);
 }  

 function RPostChangePeriod(strpost) {
    var xmlhttp=null;
    xmlhttp=getXMLHTTPRequest();
    if (xmlhttp==null )
    {
        alert("Your browser does not support XMLHTTP.");
    }
    else
    {
        xmlhttp.open("POST", "<%=request.getContextPath()%>/roleEndUser/nChangeQueryPeriod.jsp", true);
        xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

        xmlhttp.send(strpost);
        xmlhttp.onreadystatechange=function()
        {
            if (xmlhttp.readyState==4)
            {
                if (xmlhttp.status==200)
                {
                    XML_Response = xmlhttp.responseXML;
                    //alert(xmlhttp.responseXML.xml);
                    ParseXMLChangePeriod(XML_Response);
                }
                else
                {
                    alert("Problem retrieving XML data");
                }
            }
        }
    }
 }

function ParseXMLChangePeriod(oxml)
{
    var errorno = "";
    var errordescr = "";
    var replyVal = "";
    var parNames = "";
    var parValuesCSV = "";
    var qstr = "";
    var vsnId = "";

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
            vsnId = node.getAttribute('vsnId');
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
        //console.log('ONE ONE');
        handleChgdPeriodResult(errorno, errordescr,replyVal,vsnId,parValuesCSV,qstr);
    }
}

function handleChgdPeriodResult(errorno, errordescr,replyVal,vsnId, parValues,qstr){
    if(errorno=="" || errorno!="0") {
        alert(errordescr);
    }
    else {
        console.log('Debug:: \nParam Values Val:' + parValues  +' \nReply Val:' + replyVal ); // debug: + ' \nparNames Val:' + parNames  + ' \nqstr Val:' + qstr );
        if(document.getElementById('samplePer_'+vsnId) !=null) {
            document.getElementById('samplePer_'+vsnId).value =  replyVal;
            alert('Sampling period was adjusted successfully!');
        }
    }

}


 // ----------------------------------------------------------------------------------
 function RPost(strpost){
	var xmlhttp=null;
	xmlhttp=getXMLHTTPRequest();
	if (xmlhttp==null )
	{
            alert("Your browser does not support XMLHTTP.");
	}
	else
	{
            //alert('<%=request.getContextPath()%>/roleEndUser/nChangeQueryStatus.jsp');
            xmlhttp.open("POST", "<%=request.getContextPath()%>/roleEndUser/nChangeQueryStatus.jsp", true);
            xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

            xmlhttp.send(strpost);
            xmlhttp.onreadystatechange=function()
            {
                if (xmlhttp.readyState==4)
                {
                    if (xmlhttp.status==200)
                    {
                        XML_Response = xmlhttp.responseXML;
                        //alert(xmlhttp.responseXML.xml);
                        ParseXML(XML_Response);
                    }
                    else
                    {
                        alert("Problem retrieving XML data");
                        //Effect.Fade('progressMsg');
                        //globalstatusDesc ="Problem retrieving XML data";
                        //setTimeout("showResultStatus()", 600);
                    }
                }
            }
	 }
}
     
function ParseXML(oxml)
{
    var errorno = "";
    var errordescr = "";
    var qid = "";
    var newQueryStatus = "";
    var foundOneEntry =new Boolean();
    foundOneEntry = false;

    var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
    for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
            // should have one vsnList tag
        var node = answer.childNodes.item(iNode);
        if(node.tagName == "vsnList")
        {
            for(var vsnEntryNodeIter = 0; vsnEntryNodeIter < node.childNodes.length; vsnEntryNodeIter++)
            {
                var vsnEntryiNode = node.childNodes.item(vsnEntryNodeIter);
                if(vsnEntryiNode.tagName =="vsnEntry")
                {
                    foundOneEntry=true;
                    for(var vsnEntryNodeChildIter = 0; vsnEntryNodeChildIter < vsnEntryiNode.childNodes.length; vsnEntryNodeChildIter++)
                    {
                        var vsnEntryNodeInnerItem = vsnEntryiNode.childNodes.item(vsnEntryNodeChildIter);
                        if(vsnEntryNodeInnerItem.tagName == "quid")
                        {
                            qid = myGetTextXML(vsnEntryNodeInnerItem);
                        }
                        else if(vsnEntryNodeInnerItem.tagName =="newQueryStatus")
                        {
                            newQueryStatus = myGetTextXML(vsnEntryNodeInnerItem);
                        }
                        else if(vsnEntryNodeInnerItem.tagName == "error")
                        {
                            errorno = vsnEntryNodeInnerItem.getAttribute('errno');
                            errordescr = vsnEntryNodeInnerItem.getAttribute('errdesc');
                        }
                    }
                    transactAftermath(qid, newQueryStatus, errorno, errordescr);
                }
            }
        }
    }
    if(!foundOneEntry)
    {
        alert('No valid entries found in XML response!');
        transactAftermath(qid, newQueryStatus, errorno, errordescr);
    }
}
 
 function changeQueryRunningStatus(qid)
 {
    var assumedQueryStatus = <%=QueryDefinition.STATUS_RUNNING %>;
    // there should be some kind of "offline" check here, if the status has already been changed but the page does not
    // reflect this (because it has not been refreshed in time or sth). So in that case ajax could be avoided.
    if(qid == null || qid =="")
        return;
        
    currDisplayedActionForStatus = document.getElementById('queryStatusActionTextDiv_'+qid).innerHTML;
    if(currDisplayedActionForStatus == "")
    {
        assumedQueryStatus = <%=QueryDefinition.STATUS_RUNNING %>;
    }
    else if(currDisplayedActionForStatus.toLowerCase() == "Stop".toLowerCase())
    {
        assumedQueryStatus = <%=QueryDefinition.STATUS_RUNNING %>;
    }
    else if(currDisplayedActionForStatus.toLowerCase() == "Start".toLowerCase())
    {
        assumedQueryStatus = <%=QueryDefinition.STATUS_PAUSED %>;
    }
    // (To Do) error checking. +++
    strSend = 'quid='+ qid +'&'+
              'currassumedstatus='+ assumedQueryStatus;
    //alert(strSend);
    RPost(strSend + '&psaction=singleStartStop');
 }
 