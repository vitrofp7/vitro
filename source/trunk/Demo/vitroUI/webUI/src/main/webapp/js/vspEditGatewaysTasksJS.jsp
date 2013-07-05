<%@page session='false' contentType='application/x-javascript' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="java.sql.*" %>
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

//
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

function transactAftermath(strmode, gwid, errno, errordescr)
{
// here and only here we show any response of any kind to the end user.
    if(strmode == "requestUpdate")
    {
        if(errno == 0)
        {
            if(errordescr.toLowerCase() != "OK".toLowerCase())
                alert(errordescr);
            else
                alert('Update request was sent to gateway ' + gwid + '!\nPlease refresh the page to see the updated timestamp of the last update!');
        }
        else
            alert("Problem processing data: "+errordescr);
    }
    else if  (strmode == "purge")
    {
        if(errno == 0)
        {
            if(errordescr.toLowerCase() != "OK".toLowerCase())
                alert(errordescr);
            else
            {
                document.getElementById('gwLastUpdateAdvTSDiv_'+gwid).innerHTML = "N/A";
                alert('Resources from Gateway ' + gwid + ' were purged!\nPlease issue an update request to receive an up-to-date description of the Gateway\'s resources!');
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
                
                alert('Gateway ' + gwid + ' status was switched!\nPlease refresh the page to see the effects of this operation!');
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

    if(strmode == "requestUpdate")
        targetHandlerUrl = "<%=request.getContextPath()%>/roleVSP/nRequestGatewayUpdate.jsp";
    else    if  (strmode == "purge")
        targetHandlerUrl = "<%=request.getContextPath()%>/roleVSP/nRequestGatewayPurge.jsp"
    else    if (strmode == "Disable")
        targetHandlerUrl = "<%=request.getContextPath()%>/roleVSP/nRequestGatewayDisable.jsp"
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
    var gwid = "";

    var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
    for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
        var node = answer.childNodes.item(iNode);
        if(node.tagName == "gwid")
        {
            gwid = myGetTextXML(node);
        }
        else if(node.tagName == "error")
        {
            errorno = node.getAttribute('errno');
            errordescr = node.getAttribute('errdesc');
        }
    }
    transactAftermath(strmode, gwid, errorno, errordescr);
}

function requestGatewayUpdate(gwid, disable)
{
    // todo: there should be a disabled check here (if a gateway is set disabled then why try to update it?)
    //alert('Gateway ' + gwid + ' has state ' + disable + '... ');
    if(gwid == null || disable == 'true')
    {	
    	alert('Gateway ' + gwid + ' is disabled... No update is possible');
        return;
	}	
    // (To Do) error checking. +++
    strSend = 'gwid='+ gwid;
    //alert(strSend);
    RPost(strSend, "requestUpdate");
}


function purgeGatewayResources(gwid, disable)
{
     if(gwid == null || disable == 'true')
    {	
    	alert('Gateway ' + gwid + ' is disabled... No purge is possible');
        return;
	}	
    // (To Do) error checking. +++
    strSend = 'gwid='+ gwid;
    //alert(strSend);
    RPost(strSend, "purge");
}

function switchGatewayState(gwid, disable)
	{
	 var targetHandlerUrl = "";
    if(gwid == null )
    {	
    	alert ('The selected gateway has no ID');
        return;
	}
	 strSend = 'gwid='+ gwid;
    //alert("Refresh the page and cross your fingers");
     //targetHandlerUrl = "<%=request.getContextPath()%>/roleVSP/nRequestGatewayDisable.jsp"
    RPost(strSend, "Disable");
}




