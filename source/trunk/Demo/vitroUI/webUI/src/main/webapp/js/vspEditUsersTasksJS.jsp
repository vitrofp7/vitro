<%@page session='false' contentType='application/x-javascript' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="java.sql.*" %>
//
// Ajax for changing query status
//
//
// compatibility function for various browsers' xml parsing

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
function showResultStatus()
{
    getObject('resultMsg').innerHTML = globalstatusDesc;
    Effect.Appear('resultMsg');
    setTimeout("enableSubmitAndAlertResult();", 600);



}

function enableSubmitAndAlertResult()
{
    if (globalstatusDesc.toLowerCase() != "OK".toLowerCase())
        alert('There was an error while submitting your VSN request: ' + globalstatusDesc);
    else
        alert('Your VSN deploy request was submitted successfully\nFrom the top menu, select Monitor Service to view its status!');

    globalstatusDesc = '&nbsp;';
    getObject('resultMsg').innerHTML ='&nbsp;';
    getObject('submit').disabled = false;
}



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

function transactAftermath(strmode, userid, errno, errordescr)
{
// here and only here we show any response of any kind to the end user.
    if  (strmode == "purge")
    {
        if(errno == 0)
        {
            if(errordescr.toLowerCase() != "OK".toLowerCase())
                alert(errordescr);
            else
            {
                document.getElementById('gwLastUpdateAdvTSDiv_'+userid).innerHTML = "N/A";
                alert('Resources from user ' + userid + ' were purged!\nPlease issue an update request to receive an up-to-date description of the Gateway\'s resources!');
            }
        }
        else
            alert("Problem processing data: "+errordescr);
    }
    else
    {
        if(errno == 0)
        {
            if(errordescr.toLowerCase() != "OK".toLowerCase())
                alert(errordescr);
            else
            {
                
                alert('User ' + userid + ' status was switched!\nPlease refresh the page to see the effects of this operation!');
            }
        }
        else
            alert("Problem processing data: "+errordescr);
    }
    
    //Effect.Fade('progressMsg');
    //globalstatusDesc = statusDescription;
    //setTimeout("showResultStatus()", 600);
}

function RPost(strpost, strmode){
    var targetHandlerUrl = "";

	if  (strmode == "purge")
        targetHandlerUrl = "<%=request.getContextPath()%>/roleVSP/nRequestUserPurge.jsp"
    else    if (strmode == "Disable")
    	
        targetHandlerUrl = "<%=request.getContextPath()%>/roleVSP/nRequestUserDisable.jsp"
    var xmlhttp=null;

    xmlhttp=getXMLHTTPRequest();
    if (xmlhttp==null )
    {
        alert("Your browser does not support XMLHTTP.");
    }
    else
    {
        
        xmlhttp.open("POST", targetHandlerUrl, true);

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
                    ParseXML(XML_Response, strmode);
                }
                else if (xmlhttp.status==500)
                	{alert("Cazzo succede...");}
                else
                {
                    alert("Problem retrieving XML data");
                    //alert(xmlhttp.responseXML.xml);
                    //globalstatusDesc ="Problem retrieving XML data";
                }
            }
        }
    }
}

function ParseXML(oxml, strmode)
{
    var errorno = "";
    var errordescr = "";
    var userid = "";

    var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
    for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
        var node = answer.childNodes.item(iNode);
        if(node.tagName == "userid")
        {
            userid = myGetTextXML(node);
        }
        else if(node.tagName == "error")
        {
            errorno = node.getAttribute('errno');
            errordescr = node.getAttribute('errdesc');
        }
    }
    transactAftermath(strmode, userid, errorno, errordescr);
}


function purgeUserResources(userid, disable)
{
     if(userid == null || disable == 'true')
    {	
    	alert('User ' + userid + ' is disabled... No purge is possible');
        return;
	}	
    strSend = 'userid='+ userid;

    RPost(strSend, "purge");
}

function switchUserState(userid, disable)
	{
	 var targetHandlerUrl = "";
    if(userid == null )
    {	
    	alert ('The selected gateway has no ID');
        return;
	}
	alert ('userid vale '+userid+ ' e ha stato ' + disable);
	 strSend = 'userid='+ userid;
    RPost(strSend, "Disable");
}

function ReceiveUserToInsert()
{         
        getObject('submit').disabled = true;

        globalstatusDesc = '&nbsp;';
        getObject('resultMsg').innerHTML ='&nbsp;';
        
	var argus = arguments.length;
	var ActionValue;       // (possible values: newQuery, deleteQurey, updateQuery or an empty/no parameter)
                                // an empty value has the same effect with newQuery
                               
 	// form fields
        
	var strSend='';
        
        var objGatewaysSlctd; 
        var objSmDevSlctd; 
        var objIndxGwForASmDevArr;
        var objCapsSlctd;
        var objFunctsSlctd;
        var objPeriodSlctd;
        var objHistNumSlctd;
        var objAggrSlctd;
        
        var strAllAvailGateways = '';
        var strGatewaysSlctd = '';
        var strSmDevSlctd = '';
        var strIndxGwForASmDevArr = '';
        var strCapsSlctd = '';
        var strFunctsSlctd = '';
        var strPeriodSlctd = '';
        var strHistNumSlctd = '';
        var strAggrSlctd = '';
        var strSelectedActuationVal = '';
        var numfOfChecked = 0;
        
	if( (argus >= 1) && (argus <= 2))
	{
		ActionValue = arguments[0]; // (possible values: newQuery, deleteQurey, updateQuery)

                if(ActionValue=="newQuery")
		{
                    // gateways selected
                    numfOfChecked = 0;
                    objGatewaysSlctd = document.formbasic.elements['GateWayCBox[]'];
                    var realGatewaysSlctd;
                    //if((typeof objGatewaysSlctd.type!="string")&&(objGatewaysSlctd.length>0)&&(objGatewaysSlctd[0]!=null)&&(objGatewaysSlctd[0].type=="checkbox"))
                    //{
                    //     alert('this is an array!!');
                    // }

                    if(objGatewaysSlctd != null) 
                    {
                        if(typeof objGatewaysSlctd.type=="string")
                        {
                            realGatewaysSlctd = new Array();
                            realGatewaysSlctd[0]=objGatewaysSlctd;
                        }
                        else
                        {
                            realGatewaysSlctd = objGatewaysSlctd;
                        }
                        for (counter = 0; counter < realGatewaysSlctd.length; counter++)
                        {                    
                            strAllAvailGateways = (counter == 0)? "allgw[]="+ realGatewaysSlctd[counter].value: strAllAvailGateways + '&'+ "allgw[]="+ realGatewaysSlctd[counter].value;
                        
                            // If a checkbox has been selected it will return true
                            // (If not it will return false)
                            if (realGatewaysSlctd[counter].checked)
                            {
                                strGatewaysSlctd = (numfOfChecked == 0)? "selgw[]="+counter : strGatewaysSlctd +'&'+ "selgw[]="+counter; 
                                numfOfChecked += 1;
                            }                                                           
                       }
                   }
                   //smart devices selected
                   numfOfChecked = 0;
                   objSmDevSlctd = document.formbasic.elements['SmDevCBox[]'];
                   objIndxGwForASmDevArr = document.formbasic.elements['IndxOfGwCbForThisSmDev[]'];
                   var realSmDevSlctd;
                   var realIndxGwForASmDevArr;
                    // both objects have the same size so we parse them together
                   if(objSmDevSlctd != null && objIndxGwForASmDevArr!=null) 
                   {
                        if(typeof objSmDevSlctd.type=="string")
                        {
                            realSmDevSlctd = new Array();
                            realIndxGwForASmDevArr = new Array();
                            realSmDevSlctd[0]=objSmDevSlctd;
                            realIndxGwForASmDevArr[0]=objIndxGwForASmDevArr;
                        }
                        else
                        {
                            realSmDevSlctd = objSmDevSlctd;
                            realIndxGwForASmDevArr = objIndxGwForASmDevArr;
                        } 
                        for (counter = 0; counter < realSmDevSlctd.length; counter++)
                        {                    
                            // If a checkbox has been selected it will return true
                            // (If not it will return false)
                            if (realSmDevSlctd[counter].checked)
                            {                         
                                strSmDevSlctd = (numfOfChecked == 0)? "smdvs[]="+realSmDevSlctd[counter].value : strSmDevSlctd +'&'+ "smdvs[]="+realSmDevSlctd[counter].value; 
                                strIndxGwForASmDevArr = (numfOfChecked == 0)? "idxGwtoSD[]=" + realIndxGwForASmDevArr[counter].value : strIndxGwForASmDevArr +'&'+ "idxGwtoSD[]="+ realIndxGwForASmDevArr[counter].value;
                                numfOfChecked += 1;
                            }
                        }
                    }
                   //Capabilities selected
                   numfOfChecked = 0;
                   objCapsSlctd = document.formbasic.elements['GenericCapsBox[]'];
                   var realCapsSlctd;
                   if(objCapsSlctd != null) 
                   {
                        if(typeof objCapsSlctd.type=="string")
                        {
                            realCapsSlctd = new Array();
                            realCapsSlctd[0]=objCapsSlctd;
                        }
                        else
                        {
                            realCapsSlctd = objCapsSlctd;
                        } 
                        for (counter = 0; counter < realCapsSlctd.length; counter++)
                        {
                    
                            // If a checkbox has been selected it will return true
                            // (If not it will return false)
                            if (realCapsSlctd[counter].checked)
                            {   
                                strCapsSlctd = (numfOfChecked == 0)? 'gcaps[]='+ realCapsSlctd[counter].value  : strCapsSlctd +'&'+ 'gcaps[]='+ realCapsSlctd[counter].value; 
                                numfOfChecked += 1;
                            }                                                           
                        }
                   }     
                    //Functions selected
                   numfOfChecked = 0;
                   objFunctsSlctd = document.formbasic.elements['FunctionsBox[]'];
                   var realFunctsSlctd;
                   if(objFunctsSlctd != null) 
                   {
                        if(typeof objFunctsSlctd.type=="string")
                        {
                            realFunctsSlctd = new Array();
                            realFunctsSlctd[0]=objFunctsSlctd;
                        }
                        else
                        {
                            realFunctsSlctd = objFunctsSlctd;
                        }  
                        for (counter = 0; counter < realFunctsSlctd.length; counter++)
                        {
                    
                            // If a checkbox has been selected it will return true
                            // (If not it will return false)
                            if (realFunctsSlctd[counter].checked)
                            {   
                                strFunctsSlctd = (numfOfChecked == 0)?  'functs[]='+ realFunctsSlctd[counter].value  : strFunctsSlctd +'&'+ 'functs[]='+ realFunctsSlctd[counter].value; 
                                numfOfChecked += 1;
                            }                                                           
                        }
                   }
                    //new code for getting the actuation value   (Demo)
                    objDdlActuationVal =  getObject('FunctionActuationValueSelect');
                    strSelectedActuationVal = '';
                    if(objDdlActuationVal!=null && objDdlActuationVal.selectedIndex >=0)
                    {
                        strSelectedActuationVal = objDdlActuationVal.options[objDdlActuationVal.selectedIndex].value;
                    }
                   //alert('Actuation Value' + strSelectedActuationVal);
                   // period of query issuing
                   objPeriodSlctd = getObject('periodOfIssueId');
                   strPeriodSlctd = objPeriodSlctd.value;
                   // history length for results
                   objHistNumSlctd = getObject('historyNum');
                   strHistNumSlctd = objHistNumSlctd.value;
                   // aggregate checked
                   objAggrSlctd = document.formbasic.aggregateQueries;
                   strAggrSlctd = objAggrSlctd.checked ? '1': '0';
                   
		}
		else if (ActionValue=="insertUser")
		{
		alert("Ti piacerebbe eh???");
		}
	}
	else
	{
		alert('Error! An error was encountered while processing your form data!');
                getObject('submit').disabled = false;
                return;
	}
        
        //validation / error checking
        if( strAllAvailGateways =='')
        {
        	alert('Since no gateways were found, it is not possible to request a VSN at this time!');
                getObject('submit').disabled = false;
                return;
        }
        else if( strGatewaysSlctd =='' && strSmDevSlctd =='')
        {
        	alert('You should select at least one gateway or one smart device to query!');
                getObject('submit').disabled = false;
                return;
        }
        else if( strCapsSlctd  =='')
        {
        	alert('You should select at least one generic capability to measure in your query!');
                getObject('submit').disabled = false;                
                return;
        }
        else if( strFunctsSlctd =='')
        {
        	alert('You should select at least one function to be applied to the readings for the selected capabilities');
                getObject('submit').disabled = false;                
                return;
        }
                
        Element.show('progressMsg');
       // Prepare the POST string
       strAllAvailGateways = (strAllAvailGateways=='')? strAllAvailGateways : strAllAvailGateways + '&';
       strGatewaysSlctd = (strGatewaysSlctd=='')? strGatewaysSlctd : strGatewaysSlctd + '&';
       strSmDevSlctd = (strSmDevSlctd=='')? strSmDevSlctd : strSmDevSlctd + '&';
       strIndxGwForASmDevArr = (strIndxGwForASmDevArr=='')? strIndxGwForASmDevArr : strIndxGwForASmDevArr + '&';
       strCapsSlctd = (strCapsSlctd=='')? strCapsSlctd : strCapsSlctd + '&';
       strFunctsSlctd = (strFunctsSlctd=='')? strFunctsSlctd : strFunctsSlctd + '&';
       strSend= strAllAvailGateways +
                strGatewaysSlctd +
                strSmDevSlctd +
                strIndxGwForASmDevArr +
                strCapsSlctd +
                strFunctsSlctd +
                'period='+ strPeriodSlctd +'&'+
                'hist='+ strHistNumSlctd +'&'+
                'aggr='+ strAggrSlctd +'&' +
                'act='+ strSelectedActuationVal + '&';
	strSend = strSend + 'psaction='+ActionValue;
	//alert(strSend);
    //alert(ActionValue);
	RPost(strSend, ActionValue);
}