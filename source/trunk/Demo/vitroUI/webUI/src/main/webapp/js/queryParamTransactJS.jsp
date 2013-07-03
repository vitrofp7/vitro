<%@page session='false' contentType='application/x-javascript' import='java.util.*'
 %>

 
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
    getObject('Submit0').disabled = false;
}

function transactAftermath(check, statusDescription, mode)
{
	if(mode=='newQuery')
	{              
            Effect.Fade('progressMsg');
            globalstatusDesc = statusDescription;
            setTimeout("showResultStatus()", 600);
	}
}


function RPost(strpost, mode){
	var xmlhttp=null;
	xmlhttp=getXMLHTTPRequest();
	if (xmlhttp==null )
	{
            alert("Your browser does not support XMLHTTP.");
	}
	else
	{
            xmlhttp.open("POST", "<%=request.getContextPath()%>/roleEndUser/nTransactQueryParam.jsp", true);
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
                        ParseXML(XML_Response, mode);
                    }
                    else
                    {
                        //alert("Problem retrieving XML data");
                        Effect.Fade('progressMsg');
                        globalstatusDesc ="Problem retrieving XML data";
                        setTimeout("showResultStatus()", 600);
                    }
                }
            }
	 }
}
     
function ParseXML(oxml, mode)
{
	var doc = oxml.documentElement;
	var xmlerror = doc.getElementsByTagName('error');
	var errortag = xmlerror[0];
	var errorno = errortag.getAttribute('errno');
	var errordescr = errortag.getAttribute('errdesc');
	transactAftermath(errorno, errordescr, mode);
}

  
  
  
// first argument is the action we take. For the moment only 'newQuery' is valid.
function QueryTransact()
{         
        getObject('Submit0').disabled = true;

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
                getObject('Submit0').disabled = false;
                return;
	}
        
        //validation / error checking
        if( strAllAvailGateways =='')
        {
        	alert('Since no gateways were found, it is not possible to request a VSN at this time!');
                getObject('Submit0').disabled = false;
                return;
        }
        else if( strGatewaysSlctd =='' && strSmDevSlctd =='')
        {
        	alert('You should select at least one gateway or one smart device to query!');
                getObject('Submit0').disabled = false;
                return;
        }
        else if( strCapsSlctd  =='')
        {
        	alert('You should select at least one generic capability to measure in your query!');
                getObject('Submit0').disabled = false;                
                return;
        }
        else if( strFunctsSlctd =='')
        {
        	alert('You should select at least one function to be applied to the readings for the selected capabilities');
                getObject('Submit0').disabled = false;                
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

