<%@page session='false' contentType='application/x-javascript' import='java.util.*'
 %>
 
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
    
 function transactAftermath(action, errno, errordescr)
 {
    // here and only here we change the status html text.
    if(errno == 0)
    {
        if(errordescr.toLowerCase() != "OK".toLowerCase())
            alert(errordescr);
        else
            window.location.reload( true );
    }
    else
        alert("Problem processing data: "+errordescr);    
 }  
 
 function RPost(strpost){
	var xmlhttp=null;
	xmlhttp=getXMLHTTPRequest();
	if (xmlhttp==null )
	{
            alert("Your browser does not support XMLHTTP.");
	}
	else
	{
            //alert('<%=request.getContextPath()%>/actionsaux/nTasksForEditStyles.jsp');
            xmlhttp.open("POST", "<%=request.getContextPath()%>/actionsaux/nTasksForEditStyles.jsp", true);
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
                    }
                }
            }
	 }
}
     
function ParseXML(oxml)
{
        var errorno = "";
        var errordescr = "";
        var action = "";
        
        var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one        
        for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
            var node = answer.childNodes.item(iNode);     
            if(node.tagName == "action")
            {
                action = myGetTextXML(node);
            }     
            else if(node.tagName == "error")
            {
                errorno = node.getAttribute('errno');
                errordescr = node.getAttribute('errdesc');
            }
        }
	transactAftermath(action, errorno, errordescr);
}
 
 function deleteThisStyle(styleid)
 {
    input_box=confirm("Are you sure you want to delete this style?");
    if (input_box!=true)
    { 
        // Cancel is clicked
        return;
    }
    if(styleid== null || styleid =="")
        return;
                
    // (To Do) error checking. +++
    strSend = 'styleid='+styleid +'&'+
              'action=deleteStyle';              
    //alert(strSend);
    RPost(strSend);
 }
 
 