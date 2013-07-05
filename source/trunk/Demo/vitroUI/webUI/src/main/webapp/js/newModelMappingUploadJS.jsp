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

// we use different GLOBAL updaters for each upload so as not to mix things up...
    var updater3dFile = null;
    var updaterLinePlaceMarksFile = null;
    
    function finalKmluploadsteps(divTag, submitButtonid)
    {
        setTimeout("document.getElementById('"+submitButtonid+"').disabled = false;", 400);
        setTimeout("document.getElementById('"+divTag+"').innerHTML = '';", 400);
        setTimeout("Element.show('"+divTag+"');", 401); 
    }
     
    function updateFileUploadTasks(switchMode, divTag, submitButtonid, myReturnedAjaxMsg)
    {
        var objSelectionList = null;
        if(switchMode == "3dFile")
        {
            objSelectionList = document.getElementById('selectAKml3dFile');
            if(objSelectionList != null && myReturnedAjaxMsg != null && myReturnedAjaxMsg != '')            
            {
                try 
                {
                // work around for updating the innerHTML of a selection object (because innerHTML of internet Explorer is buggy!!!)
                    var oOption = document.createElement("OPTION");
                    oOption.text = myReturnedAjaxMsg;
                    oOption.value = myReturnedAjaxMsg;
                    document.getElementById('selectAKml3dFile').add(oOption);              
                }
                catch(error)
                {
                    objSelectionList.innerHTML += "<option value =\""+myReturnedAjaxMsg+"\">"+myReturnedAjaxMsg+"</option> ";
                }    
                //alert(objSelectionList.innerHTML);
                selectFrom3dKMLBox(myReturnedAjaxMsg);
            }
        }
        else if(switchMode == "LinePlaceMarksFile")
        {
            //split myReturnedAjaxMsg on ':' char then put the results in the input text boxes
            var mySplitResult = myReturnedAjaxMsg.split(":");
            objlineRefFrom = document.getElementById('lineRefFrom');
            objlineRefTo = document.getElementById('lineRefTo');
            // the mySplitResult[0] index contains null
            objlineRefFrom.value =  mySplitResult[1];
            objlineRefTo.value =  mySplitResult[2];
        }    
        else if(switchMode == 'RoomCenterPointsFile')
        {
            //alert('RoomCenters'+myReturnedAjaxMsg);
            var resultHTML = "";
            var fillSelectionRoomMapHTML = "";
            var roomErrorsFound = false;
            if(myReturnedAjaxMsg != null && myReturnedAjaxMsg != '')
            {
                var myRoomsSplitResult = myReturnedAjaxMsg.split(";");
                fillSelectionRoomMapHTML = "<select NAME=\"selectARoomToMap\" ID=\"selectARoomToMap\"  >";
                resultHTML  = "<table border=\"0\"  cellspacing=\"5\">";
                resultHTML += "<tr bgcolor=\"#BFDEE3\"><td>Room Name</td><td>Type</td><td>Size (m)</td><td>Elevation Height (m)</td><td>Room Height (m)</td></tr>";
                var counter = 0;
                for(i = 1 ; i < myRoomsSplitResult.length; i++)
                {
                    var myNamesCoordsSplitResult = myRoomsSplitResult[i].split(":");
                    var roomName = myNamesCoordsSplitResult[0];
                    var roomCoord = myNamesCoordsSplitResult[1];
                    if(roomName=="" || roomCoord =="")
                    {
                        roomErrorsFound = true;
                        break;
                    }                    
                    counter = i -1; // so to start counting from 0
                    resultHTML += "<tr>";
                    resultHTML += "<td>"+roomName+"<input type=hidden NAME=\"RoomNameBox[]\" ID=\"RoomNameBox_"+counter+"\"  value =\""+roomName+"\" /><input type=hidden NAME=\"RoomCoordBox[]\" ID=\"RoomCoordBox_"+counter+"\"  value =\""+roomCoord+"\" /></td>";
                    resultHTML += "<td><select NAME=\"selectARoomTypeBox[]\" ID=\"selectARoomTypeBox_"+counter+"\"  ><option value =\"cube\">cubic</option></select></td>";
                    resultHTML += "<td><input type=text id=\"RoomsSizeBox[]\" name=\"RoomSizeBox_"+counter+"\" value=\"4\"  maxlength=\"10\" size=\"8\" /></td>";
                    resultHTML += "<td><input type=text id=\"RoomsElevationBox[]\" name=\"RoomElevationBox_"+counter+"\" value=\"0\" maxlength=\"4\" size=\"4\" /></td>";
                    resultHTML += "<td><input type=text id=\"RoomsHeightBox[]\" name=\"RoomsHeightBox_"+counter+"\" value=\"3\" maxlength=\"4\" size=\"4\" /></td>";
                    resultHTML += "</tr>";
                    
                    fillSelectionRoomMapHTML += "<option id=\"optionForRoomToMap_"+counter+"\" value =\""+counter+"\">"+roomName+"</option>";
                }
                resultHTML += "</table>";
                fillSelectionRoomMapHTML += "</select>";
             }
            objRoomsListDiv = document.getElementById('enumOfRoomsDiv');
            objRoomsToMapListDiv =  document.getElementById('selectARoomToMapDiv');
            
            if(roomErrorsFound == false && objRoomsListDiv != null && myReturnedAjaxMsg != null && myReturnedAjaxMsg != '')
            {                
                objRoomsListDiv.innerHTML = resultHTML;
                objRoomsToMapListDiv.innerHTML = fillSelectionRoomMapHTML ;
                //alert(resultHTML);
                resetRoomMotesMappings();
            }
            else
            {
                objRoomsListDiv.innerHTML = "<table border=\"0\"  cellspacing=\"5\">";
                objRoomsListDiv.innerHTML += "<tr bgcolor=\"#BFDEE3\"><td>Room Name</td><td>Type</td><td>Size (m)</td><td>Elevation Height (m)</td><td>Room Height (m)</td></tr>";
                objRoomsListDiv.innerHTML += "<tr>";
                objRoomsListDiv.innerHTML += "<td>No rooms defined<input type=hidden NAME=\"RoomNameBox[]\" ID=\"RoomNameBox_0\"  value =\"\" /><input type=hidden NAME=\"RoomCoordBox[]\" ID=\"RoomCoordBox_0\"  value =\"\" /></td>";
                objRoomsListDiv.innerHTML += "<td><select NAME=\"selectARoomTypeBox[]\" ID=\"selectARoomTypeBox_0\" disabled=\"true\" ><option value =\"cube\">cubic</option></select></td>";
                objRoomsListDiv.innerHTML += "<td><input type=text id=\"RoomsSizeBox[]\" name=\"RoomSizeBox_0\" value=\"4\"  maxlength=\"10\" size=\"8\" disabled=\"true\" /></td>";
                objRoomsListDiv.innerHTML += "<td><input type=text id=\"RoomsElevationBox[]\" name=\"RoomElevationBox_0\" value=\"0\" maxlength=\"4\" size=\"4\" disabled=\"true\" /></td>";
                objRoomsListDiv.innerHTML += "<td><input type=text id=\"RoomsHeightBox[]\" name=\"RoomHeightBox_0\" value=\"3\" maxlength=\"4\" size=\"4\" disabled=\"true\" /></td>";                
                objRoomsListDiv.innerHTML += "</tr>";
                objRoomsListDiv.innerHTML += "</table>";                                
                objRoomsToMapListDiv.innerHTML = "";
                resetRoomMotesMappings(); 
            }                        
        }
        setTimeout("Effect.Fade('"+divTag+"')", 100);    
        setTimeout("finalKmluploadsteps('"+divTag+"','"+submitButtonid+"')", 880);

    }
    
    
  function startStatusCheck(switchMode, fileformid, statusDiv, buttonid)
  {  
 //     alert("start::"+switchMode+"::"+fileformid+"::"+statusDiv+"::"+buttonid);

    if(Ck(document.getElementById(fileformid)) == false)
    {
        return false;
    }    
    $(buttonid).disabled = true;
    if(switchMode == '3dFile')
    {
       // alert('c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv);
        updater3dFile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportError3dFile});
    }
    else if(switchMode == 'LinePlaceMarksFile')
    {
        updaterLinePlaceMarksFile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorLinePlaceMarksFile});
    }   
    else if(switchMode == 'RoomCenterPointsFile')
    {
        updaterRoomCenterPointsFile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorRoomCenterPointsFile});   
    }
    else
    {
        return false;
    }
    return true;
  }

  //
  // CAREFUL! reportError functions are specific for EACH upload type (one definition per type)
  //
  function reportError3dFile(request)
  {
    $('submitUploadKML3dFileButton').disabled = false;

    $('status3dFileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';
    
    killPeriodicUpdate('3dFile');
  }

  function reportErrorLinePlaceMarksFile(request)
  {
    $('submitUploadKMLLinePlaceMarksButton').disabled = false;

    $('statusKMLLinePlaceMarksUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';

    killPeriodicUpdate('LinePlaceMarksFile');
  }

  function reportErrorRoomCenterPointsFile(request)
  {
    $('submitUploadKMLRoomCenterPointsButton').disabled = false;

    $('statusKMLRoomCenterPointsUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';
    
    killPeriodicUpdate('CenterPointsFile');
  }  
  
  //
  // used only to kill the periodic update of Ajax. It is not the final killUpdate function(that follows immediately after this code)
  //
  function killPeriodicUpdate(switchMode)
  {
    //alert("KillPeriodic::"+switchMode);
  
    if(switchMode == '3dFile' && updater3dFile != null)
    {
        updater3dFile.stop();
    }
    else if(switchMode == 'LinePlaceMarksFile' && updaterLinePlaceMarksFile != null)
    {
        updaterLinePlaceMarksFile.stop();
    }
    else if(switchMode == 'RoomCenterPointsFile' && updaterRoomCenterPointsFile != null)
    {
        updaterRoomCenterPointsFile.stop();
    }
  }  
  
  // this is the final killUpdate message
  function killUpdateFinal(switchMode, statusDiv, submitButtonid, message)
  {    
   // alert("KillFInal::"+switchMode+"::"+statusDiv+"::"+message);
    killPeriodicUpdate(switchMode);
    
    if(message != '')
    {
      $(statusDiv).innerHTML = '<div class="error"><b>Error processing results: ' + message + '</b></div>';
      setTimeout("document.getElementById('"+submitButtonid+"').disabled = false;", 300);
    }
    else
    {
        if(switchMode == '3dFile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportError3dFile});
        }
        else if(switchMode == 'LinePlaceMarksFile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorLinePlaceMarksFile});
        }
        else if(switchMode == 'RoomCenterPointsFile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorRoomCenterPointsFile});
        }
    }
  }
