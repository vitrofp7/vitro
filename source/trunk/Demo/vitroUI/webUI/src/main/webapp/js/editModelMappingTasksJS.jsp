<%@page session='false' contentType='application/x-javascript' import='java.util.*'
 %>
 
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
            //alert('<%=request.getContextPath()%>/actionsaux/nTasksForMappingInterface.jsp');
            xmlhttp.open("POST", "<%=request.getContextPath()%>/actionsaux/nTasksForMappingInterface.jsp", true);
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
 
 function deleteThisInterface(modelfn, gwid, interfid)
 {
    input_box=confirm("Are you sure you want to delete this interface?");
    if (input_box!=true)
    { 
        // Cancel is clicked
        return;
    }
    if(modelfn == null || modelfn =="" || gwid == null || gwid =="" || interfid == null || interfid =="" )
        return;
        
        
    // (To Do) error checking. +++
    strSend = 'modelfn='+ modelfn +'&'+
              'gwid='+ gwid +'&'+
              'iid='+interfid +'&'+
              'action=deleteInt';              
    //alert(strSend);
    RPost(strSend);
 }
 
 
 function makeDefaultThisInterface(modelfn, gwid, interfid)
 {
    input_box=confirm("Are you sure you want to make this interface the default interface?");
    if (input_box!=true)
    { 
        // Cancel is clicked
        return;
    }
    if(modelfn == null || modelfn =="" || gwid == null || gwid =="" || interfid == null || interfid =="" )
        return;
        
        
    // (To Do) error checking. +++
    strSend = 'modelfn='+ modelfn +'&'+
              'gwid='+ gwid +'&'+
              'iid='+interfid +'&'+
              'action=defaultInt';
    //alert(strSend);
    RPost(strSend);    
 }
 