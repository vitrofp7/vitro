<%@page session='false' contentType='application/x-javascript' import='java.util.*'
 %>

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
    
    // AJAX for listing motes for selected gateway
    function transactAftermathSelectGW(check, statusDescription)
    {	           
        if(check != 0)
        {
            alert(statusDescription);            
        }    
        document.getElementById('selectGwToMap').disabled = false;  
    }

    function RPostSelectGW(strpost)
    {
	var xmlhttp=null;
	xmlhttp=getXMLHTTPRequest();
	if (xmlhttp==null )
	{
            alert("Your browser does not support XMLHTTP.");
	}
	else
	{
            xmlhttp.open("POST", "<%=request.getContextPath()%>/actionsaux/nTransactListGatewMotes.jsp", true);
            xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

            xmlhttp.send(strpost);
            xmlhttp.onreadystatechange=function()
            {
                if (xmlhttp.readyState==4)
                {
                    if (xmlhttp.status==200)
                    {
                        XML_Response = xmlhttp.responseXML;
                        ParseXMLSelectGW(XML_Response);
                    }
                    else
                    {
                        alert("Problem retrieving XML data");
                        document.getElementById('selectGwToMap').disabled = false;
                    }
                }
            }
	 }
    }
    
    function ParseXMLSelectGW(oxml)
    {
        //
        // Careful. Parsing an XML with childnodes returns the closing tags as well, so always check which tag you are processing!
        //
	//var xmldoc = oxml.documentElement;
        var errorno = "";
        var errordescr = "";
        var gwName = "";
        var gwId = "";
        
        var arrMoteId = null;
        var arrMoteName = null;
        var arrMoteLat = null;
        var arrMoteLong = null;
        
        var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
        //alert("Answer' fields "+answer.childNodes.length);

        for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
            var node = answer.childNodes.item(iNode);             
            if(node.tagName == "gwid")
            {
                gwId = myGetTextXML(node);
            }              
            else if(node.tagName =="gwname")
            {
                gwName = myGetTextXML(node);               
            }
            else if(node.tagName == "motes")
            {               
                var len = parseInt(node.childNodes.length / 2);
                //alert("motes' fields "+len);
                arrMoteId = new Array(len);
                arrMoteName = new Array(len);
                arrMoteLat = new Array(len);
                arrMoteLong = new Array(len);
                    
                var cnt = 0;
                for (i = 0; i < node.childNodes.length; i++) {            //<-- loop through the "mote" tags of a "motes" parent tag
                    
                    var sibl = node.childNodes.item(i);    
                    if(sibl.tagName == "mote")
                    {
                        for (x = 0; x < sibl.childNodes.length; x++) {        //<-- loop through the child tags of a "mote" parent tag
                    
                            var sibl2 = sibl.childNodes.item(x);
                            if (sibl2.tagName == "id") 
                            {
                                arrMoteId[cnt] = myGetTextXML(sibl2);
                            }
                            else if(sibl2.tagName == "name")                        
                            {
                                arrMoteName[cnt] = myGetTextXML(sibl2);
                            }
                            else if(sibl2.tagName =="location")
                            {
                                for (k = 0; k < sibl2.childNodes.length; k++) { //<-- loop through the child tags of a "location" parent tag
                                    var sibl3 = sibl2.childNodes.item(k);
                                    if (sibl3.tagName == "latitude") 
                                    {
                                        arrMoteLat[cnt] = myGetTextXML(sibl3);
                                    }
                                    else if(sibl3.tagName == "longitude")                        
                                    {
                                        arrMoteLong[cnt] = myGetTextXML(sibl3);
                                    }                                
                                }                            
                            }
                        }
                        cnt = cnt + 1;
                    }     
                }
            }
            else if(node.tagName == "error")
            {
                errorno = node.getAttribute('errno');
                errordescr = node.getAttribute('errdesc');
            }
        }
        if(arrMoteId !=null && arrMoteName != null && arrMoteLat != null && arrMoteLong != null && 
            arrMoteId.length == arrMoteName.length && arrMoteId.length == arrMoteLat.length && arrMoteId.length == arrMoteLong.length  &&
              gwName !="" && gwId !="" )
        {
            var ListAllMotesInGwHtml = "";
            for(j=0; j < arrMoteId.length; j++)
            {
                // TODO name is removed, and location because we don't provide it yet! (?,?) was shown
                //ListAllMotesInGwHtml +="<div style=\"display: inline\" id=\"SmartDevsToMapDiv_"+j+"\"><input type=checkbox name=\"SmartDevsToMap[]\" id=\"SmartDevsToMap_"+j+"\" value=\""+arrMoteId[j]+"\"  /><label id=\"SmartDevsToMapLabel_"+j+"\" for=\"SmartDevsToMap_"+j+"\" >"+arrMoteName[j]+"("+arrMoteLat[j]+","+arrMoteLong[j]+") ("+arrMoteId[j]+")</label><br></div>";
                ListAllMotesInGwHtml +="<div style=\"display: inline\" id=\"SmartDevsToMapDiv_"+j+"\"><input type=checkbox name=\"SmartDevsToMap[]\" id=\"SmartDevsToMap_"+j+"\" value=\""+arrMoteId[j]+"\"  /><label id=\"SmartDevsToMapLabel_"+j+"\" for=\"SmartDevsToMap_"+j+"\" >"+arrMoteId[j]+"</label><br></div>";
            }
            document.getElementById('checkSmartDevsToMapDiv').innerHTML = ListAllMotesInGwHtml;
        }
        resetRoomMotesMappings();        
	transactAftermathSelectGW(errorno, errordescr);
    }
    //
    // End of AJAX for listing gateway motes related code
    //
    
    //
    // handles the Onchange event in the selection box for a gateway to map
    //
    function gwSelectionTransact()
    {    
        document.getElementById('selectGwToMap').disabled = true;
        document.getElementById('checkSmartDevsToMapDiv').innerHTML ="<div id=\"SmartDevsToMapDiv_0\" style=\"display: inline\"><input type=checkbox name=\"SmartDevsToMap[]\" id=\"SmartDevsToMap_0\" value=\"\" disabled=\"true\" />No devices available</div>";
        resetRoomMotesMappings(); // we will also call it at the end (if sucessful).
                                  // this here saves us from calling it before each "error" return
        
        var optionGwToMapArr = document.getElementById('selectGwToMap').options;
        var sGwToMapIdx = document.getElementById('selectGwToMap').selectedIndex;                
        if(optionGwToMapArr[sGwToMapIdx].value == '#' || optionGwToMapArr[sGwToMapIdx].value == '')
        {
            document.getElementById('selectGwToMap').disabled = false;
            return;
        }
        
        var gwPostString = 'gwid='+ optionGwToMapArr[sGwToMapIdx].value +'&'+
                           'gwname='+ optionGwToMapArr[sGwToMapIdx].innerHTML; // <--the name is used only for description purposes.

      //alert(gwPostString);
        RPostSelectGW(gwPostString);
    }
    
    // 
    // Start of Ajax for model mapping submission.
    //
    var globalstatusSubmitFinalDesc;
    
    function RPostSubmitFinalMap(strpost)
    {
	var xmlhttp=null;
	xmlhttp=getXMLHTTPRequest();
	if (xmlhttp==null )
	{
            alert("Your browser does not support XMLHTTP.");
	}
	else
	{
            xmlhttp.open("POST", "<%=request.getContextPath()%>/roleVSP/MakeModel", true);
            xmlhttp.setRequestHeader('Content-Type','application/x-www-form-urlencoded');

            xmlhttp.send(strpost);
            xmlhttp.onreadystatechange=function()
            {
                if (xmlhttp.readyState==4)
                {
                    if (xmlhttp.status==200)
                    {
                        XML_Response = xmlhttp.responseXML;
                        ParseXMLSubmitFinalMap(XML_Response);
                    }
                    else
                    {
                        //alert("Problem retrieving XML data");
                        //document.getElementById('SubmitModelMapFinal').disabled = false;
                        Effect.Fade('progressMsgSubmitFinal');
                        globalstatusSubmitFinalDesc ="Problem retrieving XML data";
                        setTimeout("showSubmitFinalResultStatus()", 600);
                    }
                }
            }
	 }
    }    
    
    function ParseXMLSubmitFinalMap(oxml)
    {
        //
        // Careful. Parsing an XML with childnodes returns the closing tags as well, so always check which tag you are processing!
        //
	//var xmldoc = oxml.documentElement;
        var errorno = "";
        var errordescr = "";
                
        var answer = oxml.getElementsByTagName('Answer').item(0); // root element should be just one
        for (var iNode = 0; iNode <  answer.childNodes.length; iNode++) { //<--- loop through the child tags of root Answer
            var node = answer.childNodes.item(iNode);    
            if(node.tagName == "error")
            {
                errorno = node.getAttribute('errno');
                errordescr = node.getAttribute('errdesc');
            }
        }    
	transactAftermathSubmitFinalMap(errorno, errordescr);
    }
    
    function transactAftermathSubmitFinalMap(check, statusDescription)
    {	           
        Effect.Fade('progressMsgSubmitFinal');
        globalstatusSubmitFinalDesc = statusDescription;
        setTimeout("showSubmitFinalResultStatus()", 600);
    }
    
    function showSubmitFinalResultStatus()
    {
        document.getElementById('resultMsgSubmitFinal').innerHTML = globalstatusSubmitFinalDesc;
        Effect.Appear('resultMsgSubmitFinal');
        setTimeout("document.getElementById('SubmitModelMapFinal').disabled = false;", 600);   
    }
    
      //
      // View Model So Far OR Submit a Mapping
      //
      function submitViewModelSoFarForm()
      {
            var argus = arguments.length;
            var mode = "";
            if(argus >= 1)
                mode = arguments[0];
                
            globalstatusSubmitFinalDesc = '&nbsp;';
            document.getElementById('resultMsgSubmitFinal').innerHTML ='&nbsp;';
            
            document.getElementById('SubmitModelMapFinal').disabled = true;
             
            objformViewModelSoFar = document.getElementById('formViewModelSoFar');
            if(objformViewModelSoFar != null )
            {
                var vmsfFormInnerHTML = "";
                var RPostForAjax = "";
                //var FormPostDebug="";
                //
                // Selected 3d KML file
                //
                var option3dKMLFilesArr = document.getElementById('selectAKml3dFile').options;
                var s3dKMLFilesIdx = document.getElementById('selectAKml3dFile').selectedIndex;
                
                if(option3dKMLFilesArr[s3dKMLFilesIdx].value == '#')
                {
                    alert('No valid KML file was selected for the model!');
                    document.getElementById('SubmitModelMapFinal').disabled = false;
                    return false;
                }
                vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsf3dKMLFile\" ID=\"vmsf3dKMLFile\"  value =\""+option3dKMLFilesArr[s3dKMLFilesIdx].value+"\" />";
                RPostForAjax += "vmsf3dKMLFile="+option3dKMLFilesArr[s3dKMLFilesIdx].value+"&";
                //
                // Selected gateway
                //
                var optionGwToMapArr = document.getElementById('selectGwToMap').options;
                var sGwToMapIdx = document.getElementById('selectGwToMap').selectedIndex;
                
                if(optionGwToMapArr[sGwToMapIdx].value == '#')
                {
                    optionGwToMapArr[sGwToMapIdx].value = "";
                }
                vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfGwToMap\" ID=\"vmsfGwToMap\"  value =\""+optionGwToMapArr[sGwToMapIdx].value+"\" />";
                RPostForAjax += "vmsfGwToMap="+optionGwToMapArr[sGwToMapIdx].value+"&";
                //
                // Line reference From 
                //
                objlineRefFrom = document.getElementById('lineRefFrom');
                if(objlineRefFrom != null && objlineRefFrom !="" && objlineRefFrom !="#")
                {
                    vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsflineRefFrom\" ID=\"vmsflineRefFrom\"  value =\""+objlineRefFrom.value+"\" />";
                    RPostForAjax += "vmsflineRefFrom="+objlineRefFrom.value+"&";
                }
                //
                // Line reference To 
                //
                objlineRefTo = document.getElementById('lineRefTo');
                if(objlineRefTo != null && objlineRefTo !="" && objlineRefTo !="#")
                {
                    vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsflineRefTo\" ID=\"vmsflineRefTo\"  value =\""+objlineRefTo.value+"\" />";
                    RPostForAjax += "vmsflineRefTo="+objlineRefTo.value+"&";
                }
                //
                // Rooms selected
                //
                objRoomNamesSlctd = document.formRoom.elements['RoomNameBox[]'];
                objRoomCoordSlctd = document.formRoom.elements['RoomCoordBox[]'];
                objRoomSizeXSlctd = document.formRoom.elements['RoomsSizeBox[]'];
                objRoomElevationSlctd = document.formRoom.elements['RoomsElevationBox[]'];
                objRoomHeightSlctd = document.formRoom.elements['RoomsHeightBox[]'];
                
                var realRoomNamesSlctd;
                var realRoomCoordSlctd;
                var realRoomSizeXSlctd;
                var realRoomElevationSlctd;
                var realRoomHeightSlctd;
                if(objRoomNamesSlctd != null && objRoomCoordSlctd!=null && objRoomSizeXSlctd!=null && objRoomElevationSlctd!=null && objRoomHeightSlctd!=null) 
                {
                    // these arrays have all the same size (and typeof type e.g array or string), so we just loop through one of them and get the corresponding values from the others!
                    if(typeof objRoomNamesSlctd.type=="string")
                    {
                        realRoomNamesSlctd = new Array();
                        realRoomCoordSlctd = new Array();
                        realRoomSizeXSlctd = new Array();
                        realRoomElevationSlctd = new Array();
                        realRoomHeightSlctd = new Array();
                        realRoomNamesSlctd[0]=objRoomNamesSlctd;
                        realRoomCoordSlctd[0]=objRoomCoordSlctd;
                        realRoomSizeXSlctd[0]=objRoomSizeXSlctd;
                        realRoomElevationSlctd[0]=objRoomElevationSlctd;
                        realRoomHeightSlctd[0]=objRoomHeightSlctd;
                    }
                    else
                    {
                        realRoomNamesSlctd = objRoomNamesSlctd;
                        realRoomCoordSlctd = objRoomCoordSlctd;
                        realRoomSizeXSlctd = objRoomSizeXSlctd;
                        realRoomElevationSlctd = objRoomElevationSlctd;
                        realRoomHeightSlctd = objRoomHeightSlctd;                        
                    }
                    
                    for (counter = 0; counter < realRoomNamesSlctd.length; counter++)
                    {        
                        var roomName = realRoomNamesSlctd[counter].value;
                        var roomCoord = realRoomCoordSlctd[counter].value;
                        var roomSizeX = realRoomSizeXSlctd[counter].value;
                        var roomElevation = realRoomElevationSlctd[counter].value;
                        var roomHeight = realRoomHeightSlctd[counter].value;
                        
                        var optionRoomTypeArr = document.getElementById('selectARoomTypeBox_'+counter).options;
                        var sRoomTypeIdx = document.getElementById('selectARoomTypeBox_'+counter).selectedIndex;
                    
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfRoomNameBox[]\" ID=\"vmsfRoomNameBox_"+counter+"\"  value =\""+roomName+"\" />";
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfRoomCoordBox[]\" ID=\"vmsfRoomCoordBox_"+counter+"\"  value =\""+roomCoord+"\" />";
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfSelectARoomTypeBox[]\" ID=\"vmsfSelectARoomTypeBox_"+counter+"\"  value =\""+optionRoomTypeArr[sRoomTypeIdx].value+"\" />";
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfRoomsSizeBox[]\" ID=\"vmsfRoomsSizeBox_"+counter+"\"  value =\""+roomSizeX+"\" />";                        
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfRoomsElevationBox[]\" ID=\"vmsfRoomsElevationBox_"+counter+"\"  value =\""+roomElevation+"\" />";                        
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfRoomsHeightBox[]\" ID=\"vmsfRoomsHeightBox_"+counter+"\"  value =\""+roomHeight+"\" />";                        
                        
                        RPostForAjax += "vmsfRoomNameBox[]="+roomName+"&";
                        RPostForAjax += "vmsfRoomCoordBox[]="+roomCoord+"&";
                        RPostForAjax += "vmsfSelectARoomTypeBox[]="+optionRoomTypeArr[sRoomTypeIdx].value+"&";
                        RPostForAjax += "vmsfRoomsSizeBox[]="+roomSizeX+"&";
                        RPostForAjax += "vmsfRoomsElevationBox[]="+roomElevation+"&";
                        RPostForAjax += "vmsfRoomsHeightBox[]="+roomHeight+"&";
                    }
                }
                //
                // Mappings defined (motes to rooms)
                //
                //
                var objfixedMotesMapBox = document.formRoomToMotesMap.elements['fixedMotesMapBox[]'];
                var objfixedMotesToWhichRoomMapBox = document.formRoomToMotesMap.elements['fixedMotesToWhichRoomMapBox[]'];
                var realfixedMotesMapBox;
                var realfixedMotesToWhichRoomMapBox;
                if(objfixedMotesMapBox!= null && objfixedMotesToWhichRoomMapBox!=null)
                {
                    // these arrays have all the same size (and typeof type e.g array or string), so we just loop through one of them and get the corresponding values from the others!
                    if(typeof objfixedMotesMapBox.type=="string")
                    {
                        realfixedMotesMapBox = new Array();
                        realfixedMotesToWhichRoomMapBox = new Array();
                        realfixedMotesMapBox[0]=objfixedMotesMapBox;
                        realfixedMotesToWhichRoomMapBox[0]=objfixedMotesToWhichRoomMapBox;
                    }
                    else
                    {
                        realfixedMotesMapBox = objfixedMotesMapBox;
                        realfixedMotesToWhichRoomMapBox = objfixedMotesToWhichRoomMapBox;
                    }
                    for (counter = 0; counter < realfixedMotesMapBox.length; counter++)
                    { 
                        var moteId = realfixedMotesMapBox[counter].value;
                        var roomForMoteIdx = realfixedMotesToWhichRoomMapBox[counter].value;
                        
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsffixedMotesMapBox[]\" ID=\"vmsffixedMotesMapBox_"+counter+"\"  value =\""+moteId+"\" />";
                        vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsffixedMotesToWhichRoomMapBox[]\" ID=\"vmsffixedMotesToWhichRoomMapBox_"+counter+"\"  value =\""+roomForMoteIdx+"\" />";
                        RPostForAjax += "vmsffixedMotesMapBox[]="+moteId+"&";
                        RPostForAjax += "vmsffixedMotesToWhichRoomMapBox[]="+roomForMoteIdx+"&";
                    }
                }
                vmsfFormInnerHTML += "<input type=hidden NAME=\"vmsfMode\" ID=\"vmsfMode\"  value =\""+mode+"\" />";
                RPostForAjax += "vmsfMode="+mode; // this is the final POST parameter so no trailing "&"
                
                //alert(vmsfFormInnerHTML);
                //alert(RPostForAjax);
                var objHiddenVMSFFormFields = document.getElementById('vmsfvmsfFormDiv');
                objHiddenVMSFFormFields.innerHTML = vmsfFormInnerHTML;
                if(mode=="preview")
                {
                    objformViewModelSoFar.submit();
                    document.getElementById('SubmitModelMapFinal').disabled = false;
                 }
                else if(mode=="submit")
                {
                    Element.show('progressMsgSubmitFinal');
                    RPostSubmitFinalMap(RPostForAjax);
                }
            }
      }
      //
      // End of Ajax for model mapping submission.
      //
      
      function resetRoomMotesMappings()
      {
            document.getElementById('submitMapping_0').disabled = true;
            document.getElementById('resetMappings_0').disabled = true;
            //
            // clear top div that lists the fixed (submited) mappings
            //
            document.getElementById('FixedRoomToMotesMappingDiv').innerHTML ="";
            //
            // unhide all motes
            // 
            objSmartDevsToMapCB = document.formRoomToMotesMap.elements['SmartDevsToMap[]'];
            var realSmartDevsToMapCB;
            if(objSmartDevsToMapCB != null) 
            {
                if(typeof objSmartDevsToMapCB.type=="string")
                {
                    realSmartDevsToMapCB = new Array();
                    realSmartDevsToMapCB[0]=objSmartDevsToMapCB;
                }
                else
                {
                    realSmartDevsToMapCB = objSmartDevsToMapCB;
                }
                for (counter = 0; counter < realSmartDevsToMapCB.length; counter++)
                {
                    n = document.getElementById('SmartDevsToMapDiv_'+counter);
                    n.style['display'] = 'inline';
                    n = document.getElementById('SmartDevsToMap_'+counter);                
                    n.checked = false;
                }
            }    
            document.getElementById('checkSDToMapStatusDiv').innerHTML ="";
            //
            // re-fill "rooms to map" selection box
            //
            renewRoomsToMapSelectioninHTML = "";            
            renewRoomsToMapOPTIONShtml = "";
            var disabledflag = "";
            var roomsCount = 0;
            
            objRoomNamesSlctd = null;
            objRoomNamesSlctd = document.formRoom.elements['RoomNameBox[]'];    
            var realRoomNamesSlctd; 
            //                    
            // Careful : When only one entry is defined the variable is NO LONGER AN ARRAY AND the LENGTH IS UNDEFINED!!!!!!
            // NEW CODE (for handling hidden arrays that could be strings (one value)!!)
            if(typeof objRoomNamesSlctd.type=="string")
            {
                realRoomNamesSlctd = new Array();
                realRoomNamesSlctd[0] = objRoomNamesSlctd;
            }
            else
            {
                realRoomNamesSlctd = objRoomNamesSlctd;
            }        
            if(realRoomNamesSlctd == null || 
                    realRoomNamesSlctd.length == 0 || 
                    realRoomNamesSlctd[0] == null ||
                    realRoomNamesSlctd[0].value=="#" ||
                    realRoomNamesSlctd[0].value==""  )
            {
                renewRoomsToMapOPTIONShtml = "<option value =\"#\">No room</option>";
                disabledflag = "disabled=\"true\"";
            }                
            else
            {                
                for (counter = 0; counter < realRoomNamesSlctd.length; counter++)  //<--- parse all defined rooms    
                {        
                    roomsCount+=1; 
                    roomName = realRoomNamesSlctd[counter].value;
                    renewRoomsToMapOPTIONShtml += "<option id=\"optionForRoomToMap_"+counter+"\" value =\""+counter+"\">"+roomName+"</option>";
                } 
            }    
            renewRoomsToMapSelectioninHTML += "<select NAME=\"selectARoomToMap\" ID=\"selectARoomToMap\" "+disabledflag+" >";
            renewRoomsToMapSelectioninHTML += renewRoomsToMapOPTIONShtml;
            renewRoomsToMapSelectioninHTML += "</select>";
            
            var objRoomsToMapListDiv =  document.getElementById('selectARoomToMapDiv');
            objRoomsToMapListDiv.innerHTML = renewRoomsToMapSelectioninHTML;
            
            if(roomsCount == 1  && 
                    realSmartDevsToMapCB != null  &&
                    realSmartDevsToMapCB[0] !=null &&
                    realSmartDevsToMapCB[0].value != "" &&
                    realSmartDevsToMapCB[0].value !="#")// <--- If only one room is selected, then directly map it to the available sensors (if any).
            {
                // select all available sensors
                for (counter = 0; counter < realSmartDevsToMapCB.length; counter++)
                {            
                    n = document.getElementById('SmartDevsToMap_'+counter);                
                    n.checked = true;
                }
                // call addFixedRoomMotesMapping()
                addFixedRoomMotesMapping();
            }            
            else
            {
                // re-enable buttons
                document.getElementById('submitMapping_0').disabled = false;
                document.getElementById('resetMappings_0').disabled = false;
            }
            return true;
      }      
      
      function addFixedRoomMotesMapping()
      {            
        
        document.getElementById('submitMapping_0').disabled = true;
        document.getElementById('resetMappings_0').disabled = true;

        var objFixedRoomToMotesMappingDiv = document.getElementById('FixedRoomToMotesMappingDiv'); // <-- should contain a row and 3 columns (or less than 3 columns if colspan is used)
        var FixedRoomToMotesMappingDivInHTML = objFixedRoomToMotesMappingDiv.innerHTML;
        
        var objRoomsToMapListDiv =  document.getElementById('selectARoomToMapDiv');
        var renewRoomsToMapSelectioninHTML =  "";
        //
        // If no room is selected or no motes are checked return false with an alert
        //
        if(document.getElementById('selectARoomToMap') == null)
        {
            alert('All rooms are already mapped!');
            document.getElementById('submitMapping_0').disabled = false;
            document.getElementById('resetMappings_0').disabled = false;
            return false;
        }
        // .options always returns an array so we don't have to re-check if it is an array (even if it has only one value)
        var selectionOfRoomToMapArr1 = document.getElementById('selectARoomToMap').options;                
        var sFixRoomToMapIdx = document.getElementById('selectARoomToMap').selectedIndex; 
        
        if(selectionOfRoomToMapArr1[sFixRoomToMapIdx].value == "#" || selectionOfRoomToMapArr1[sFixRoomToMapIdx].value =="")
        {
            alert('No room was selected! Please try again...');
            document.getElementById('submitMapping_0').disabled = false;
            document.getElementById('resetMappings_0').disabled = false;            
            return false;
        }
        //
        // Get Room selected and print it in the left cell of the table
        // Paint the row with a distinct color
        // Remove the room selected from the selection box
        //
        FixedRoomToMotesMappingDivInHTML +="<table>";
        FixedRoomToMotesMappingDivInHTML +="<tr bgcolor=\"#EEAEE0\">";
        FixedRoomToMotesMappingDivInHTML +="<td valign=\"top\">";
        FixedRoomToMotesMappingDivInHTML +="<input type=\"hidden\" name=\"fixedRoomMapBox[]\" value=\""+selectionOfRoomToMapArr1[sFixRoomToMapIdx].value+"\" >"+selectionOfRoomToMapArr1[sFixRoomToMapIdx].innerHTML;
        FixedRoomToMotesMappingDivInHTML +="</td>";
        
        objRoomNamesSlctd = document.formRoom.elements['RoomNameBox[]']; 
        var realRoomNamesSlctd;
        remainingRooms = 0;
        var renewRoomsToMapOPTIONShtml = "";
        for(i = 0; i < selectionOfRoomToMapArr1.length; i++) //<--- for each (remaining) room in the mapping step
        {
            if(typeof objRoomNamesSlctd.type=="string")
            {
                realRoomNamesSlctd = new Array();
                realRoomNamesSlctd[0] = objRoomNamesSlctd;
            }
            else
            {
                realRoomNamesSlctd = objRoomNamesSlctd;
            }
            
            for (counter = 0; counter < realRoomNamesSlctd.length; counter++)  //<--- parse all defined rooms and check for match (counter of defined rooms with the values of mapping rooms)
                                                                                // exclude the selected room from the renewed list
            {              
                if(counter == selectionOfRoomToMapArr1[i].value && i!= sFixRoomToMapIdx)
                {
                    remainingRooms +=1;
                    roomName = realRoomNamesSlctd[counter].value;
                    renewRoomsToMapOPTIONShtml += "<option id=\"optionForRoomToMap_"+counter+"\" value =\""+counter+"\">"+roomName+"</option>";
                }
            }            
        }   
        if(remainingRooms == 0)
        {
            renewRoomsToMapSelectioninHTML += "All rooms were mapped";
        }    
        else
        {
            renewRoomsToMapSelectioninHTML += "<select NAME=\"selectARoomToMap\" ID=\"selectARoomToMap\"  >";
            renewRoomsToMapSelectioninHTML += renewRoomsToMapOPTIONShtml;
            renewRoomsToMapSelectioninHTML += "</select>";
        }   
        //
        // Get the motes selected and print them in the right cell
        // Remove the motes selected from the checkboxes list.
        //
        objSmartDevsToMapCB = document.formRoomToMotesMap.elements['SmartDevsToMap[]'];
        var realSmartDevsToMapCB;        
        var numOfSelectedDevs = 0;
        
        //new code
        if(typeof objSmartDevsToMapCB.type=="string")
        {
            realSmartDevsToMapCB = new Array();
            realSmartDevsToMapCB[0] = objSmartDevsToMapCB;
        }
        else
        {
            realSmartDevsToMapCB = objSmartDevsToMapCB;
        }        
        if(realSmartDevsToMapCB == null || 
                realSmartDevsToMapCB.length == 0 || 
                realSmartDevsToMapCB[0] == null ||
                realSmartDevsToMapCB[0].value=="#" ||
                realSmartDevsToMapCB[0].value==""  )
        {
            alert('No motes were found! Please select a gateway...');
            document.getElementById('submitMapping_0').disabled = false;
            document.getElementById('resetMappings_0').disabled = false;            
            return false;
        }
        FixedRoomToMotesMappingDivInHTML +="<td  colspan=\"2\" valign=\"top\" >";
               
        for (counter = 0; counter < realSmartDevsToMapCB.length; counter++)
        {
            // If a checkbox has been selected it will return true
            // (If not it will return false)
            if (realSmartDevsToMapCB[counter].checked)
            {
                var objMoteLabel =  document.getElementById('SmartDevsToMapLabel_'+counter);
                FixedRoomToMotesMappingDivInHTML +="<input type=\"hidden\" name=\"fixedMotesMapBox[]\" value=\""+realSmartDevsToMapCB[counter].value+"\" >"+objMoteLabel.innerHTML+"<br>";
                FixedRoomToMotesMappingDivInHTML +="<input type=\"hidden\" name=\"fixedMotesToWhichRoomMapBox[]\" value=\""+selectionOfRoomToMapArr1[sFixRoomToMapIdx].value+"\" >";
                n = document.getElementById('SmartDevsToMapDiv_'+counter);
                n.style['display'] = 'none';
                n = document.getElementById('SmartDevsToMap_'+counter);                
                n.checked = false;
                numOfSelectedDevs +=1;
            }
        }    
        FixedRoomToMotesMappingDivInHTML +="</td>";
        if(numOfSelectedDevs==0) //<--- if no motes are selected
        {
            alert('No motes were selected! Please try again...');
            document.getElementById('submitMapping_0').disabled = false;
            document.getElementById('resetMappings_0').disabled = false;            
            return false;
        }                
        
        FixedRoomToMotesMappingDivInHTML +="</tr>";
        FixedRoomToMotesMappingDivInHTML +="</table>";
        
        objFixedRoomToMotesMappingDiv.innerHTML = FixedRoomToMotesMappingDivInHTML;
        objRoomsToMapListDiv.innerHTML = renewRoomsToMapSelectioninHTML;
        
        objfixedMotesMapBox = document.formRoomToMotesMap.elements['fixedMotesMapBox[]'];
        var realfixedMotesMapBox;
        //new code
        if(typeof objfixedMotesMapBox.type=="string")
        {
            realfixedMotesMapBox = new Array();
            realfixedMotesMapBox[0] = objfixedMotesMapBox;
        }
        else
        {
            realfixedMotesMapBox = objfixedMotesMapBox;
        }    
        
        if(realSmartDevsToMapCB.length == realfixedMotesMapBox.length)
            document.getElementById('checkSDToMapStatusDiv').innerHTML = "All motes were mapped!";
          
        //    
        // If some motes remain to be mapped AND after this call ONLY ONE room remains
        // then automatically add them to that room!
        // NEW: this code is commented because if a gateway is to be mapped to more than one models
        //      then probably we don't want all of the gateway's motes to be mapped to rooms of one model
        //      (and the folowing code adds automatically the remaining motes to the final room).
        /*
        if(realSmartDevsToMapCB.length > realfixedMotesMapBox.length && remainingRooms == 1) 
        {
            //
            // Select only those motes that have not been selected.
            //
            for (counter = 0; counter < realSmartDevsToMapCB.length; counter++)
            {            
                n = document.getElementById('SmartDevsToMapDiv_'+counter);
                if( n.style['display'] != "none")
                {
                    n = document.getElementById('SmartDevsToMap_'+counter);                
                    n.checked = true;
                }
            }
            addFixedRoomMotesMapping(); // recursive call
        }            
        */
        // re-enable buttons
        document.getElementById('submitMapping_0').disabled = false;
        document.getElementById('resetMappings_0').disabled = false;        
        return true;
      
      }

      function resetSelectionBoxFro3dKML()
      {
            var optionArr = document.getElementById('selectAKml3dFile').options;
            document.getElementById('selectAKml3dFile').focus();
            optionArr[0].selected = true;
      }

      //
      // should be issued on an  onChange event of the KML 3d file selection box.
      // the default option (no selection) should have a value of '#'
      function selectFrom3dKMLBox(optionValue)
      { 
            var optionArr = document.getElementById('selectAKml3dFile').options;
            var sIdx = null;
            if(optionValue == null)
                sIdx = document.getElementById('selectAKml3dFile').selectedIndex;
            else
            {
                for(i = 0; i < optionArr.length; i++)
                {
                    if(optionArr[i].value == optionValue)
                    {           
                        document.getElementById('selectAKml3dFile').focus();
                        optionArr[i].selected = true;
                        sIdx = i;
                        break;
                    }
                }
            }
            //alert(sIdx);
            if(sIdx >=0)
            {
                //alert('Hey '+optionArr[sIdx].value);
                if(optionArr == null || optionArr[sIdx] == null || optionArr[sIdx].value == '#' || optionArr[sIdx].value == '')
                {                
                    document.getElementById('spanForViewUploaded3dFile').innerHTML = '<a href="" onclick="return false;">View model</a>';
                }
                else
                {
                    document.getElementById('spanForViewUploaded3dFile').innerHTML = '<a href="<%=request.getContextPath()%>/Models/Large/'+optionArr[sIdx].value+'">View model</a>';
                }
            }            
      }

    function Ck(obj)
    {
        val=obj.value.substring(obj.value.length-4, obj.value.length);
        if (val!='.kml')
        {
            alert(val+' is not a .kml file');
            return false;
        }
        else if( val.replace(/^\s+|\s+$/g, '') =='')
        {
            return false;
        }
        
    }
  