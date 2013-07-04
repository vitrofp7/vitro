<%@page session='false' contentType='application/x-javascript' import='java.util.*'
 %>
    // we use different GLOBAL updaters for each upload so as not to mix things up...
    var updaterDefaultIconfile = null;
    var updaterDefaultPrefabfile = null;
    var updaterSpecialValueIconfile = null;
    var updaterSpecialValuePrefabfile = null;
    var updaterNumericRangeIconfile = null;
    var updaterNumericRangePrefabfile = null;
    
    function finalFileuploadsteps(divTag, submitButtonid)
    {
        setTimeout("document.getElementById('"+submitButtonid+"').disabled = false;", 400);
        setTimeout("document.getElementById('"+divTag+"').innerHTML = '';", 400);
        setTimeout("Element.show('"+divTag+"');", 401); 
    }
     
    function updateAddToSelectionBox(selectElementId, myReturnedAjaxMsg)
    {
        objSelectionList = document.getElementById(selectElementId);
        if(objSelectionList != null && myReturnedAjaxMsg != null && myReturnedAjaxMsg != '')            
        {
            try 
            {
                // work around for updating the innerHTML of a selection object (because innerHTML of internet Explorer is buggy!!!)
                    var oOption = document.createElement("OPTION");
                    oOption.text = myReturnedAjaxMsg;
                    oOption.value = myReturnedAjaxMsg;
                    document.getElementById(selectElementId).add(oOption);              
            }
            catch(error)
            {
                objSelectionList.innerHTML += "<option value =\""+myReturnedAjaxMsg+"\">"+myReturnedAjaxMsg+"</option> ";
            }    
            //alert(objSelectionList.innerHTML);
            return true;
        }    
        return false;
    }
    
    
    
    function updateFileUploadTasks(switchMode, divTag, submitButtonid, myReturnedAjaxMsg)
    {
        var objSelectionList = null;
        var updatedselectDefaultIconfile = false;
        var updatedselectDefaultPrefabfile = false;
        var updatedselectSpecialValueIconfile = false;
        var updatedselectSpecialValuePrefabfile = false;
        var updatedselectNumericRangeIconfile = false;
        var updatedselectNumericRangePrefabfile = false;
        
        // update all icon selection boxes concurrently. Do the same for all prefab selection boxes (depending on the mode).
        if(switchMode == "DefaultIconfile" ||
                switchMode == "SpecialValueIconfile" ||
                switchMode == "NumericRangeIconfile") 
        {
            updatedselectDefaultIconfile =  updateAddToSelectionBox('selectDefaultIconfile', myReturnedAjaxMsg);
            updatedselectSpecialValueIconfile = updateAddToSelectionBox('selectSpecialValueIconfile', myReturnedAjaxMsg);
            updatedselectNumericRangeIconfile = updateAddToSelectionBox('selectNumericRangeIconfile', myReturnedAjaxMsg);        
        }
        else if(switchMode == "DefaultPrefabfile" ||
                    switchMode == "SpecialValuePrefabfile" ||
                    switchMode == "NumericRangePrefabfile") 
        {
            updatedselectDefaultPrefabfile = updateAddToSelectionBox('selectDefaultPrefabfile', myReturnedAjaxMsg);
            updatedselectSpecialValuePrefabfile = updateAddToSelectionBox('selectSpecialValuePrefabfile', myReturnedAjaxMsg);
            updatedselectNumericRangePrefabfile = updateAddToSelectionBox('selectNumericRangePrefabfile', myReturnedAjaxMsg);        
        }
        
        if((switchMode == "DefaultIconfile" && updatedselectDefaultIconfile) ||
               (switchMode == "DefaultPrefabfile" && updatedselectDefaultPrefabfile) || 
               (switchMode == "SpecialValueIconfile" && updatedselectSpecialValueIconfile) || 
               (switchMode == "SpecialValuePrefabfile" && updatedselectSpecialValuePrefabfile) || 
               (switchMode == "NumericRangeIconfile" && updatedselectNumericRangeIconfile) ||
               (switchMode == "NumericRangePrefabfile" && updatedselectNumericRangePrefabfile) )
        {
                selectFromGenericfileBox(switchMode, myReturnedAjaxMsg);                                
        }
        
        setTimeout("Effect.Fade('"+divTag+"')", 100);    
        setTimeout("finalFileuploadsteps('"+divTag+"','"+submitButtonid+"')", 880);

    }
    
    
  function startStatusCheck(switchMode, fileformid, statusDiv, buttonid)
  {  
 //     alert("start::"+switchMode+"::"+fileformid+"::"+statusDiv+"::"+buttonid);


    $(buttonid).disabled = true;
    if(switchMode == 'DefaultIconfile')
    {
        if(CkIcon(document.getElementById(fileformid)) == false)
        {
            $(buttonid).disabled = false;
            return false;
        }    
        updaterDefaultIconfile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorDefaultIconfile});
    }   
    else if(switchMode == 'DefaultPrefabfile')
    {
        if(CkPrefab(document.getElementById(fileformid)) == false)
        {
            $(buttonid).disabled = false;
            return false;
        }    
       // alert('c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv);
        updaterDefaultPrefabfile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorDefaultPrefabfile});
    }
    else if(switchMode == 'SpecialValueIconfile')
    {
        if(CkIcon(document.getElementById(fileformid)) == false)
        {
            $(buttonid).disabled = false;
            return false;
        }    
        updaterSpecialValueIconfile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorSpecialValueIconfile});
    }   
    else if(switchMode == 'SpecialValuePrefabfile')
    {
        if(CkPrefab(document.getElementById(fileformid)) == false)
        {
            $(buttonid).disabled = false;
            return false;
        }    
        updaterSpecialValuePrefabfile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorSpecialValuePrefabfile});
    }   
    else if(switchMode == 'NumericRangeIconfile')
    {
        if(CkIcon(document.getElementById(fileformid)) == false)
        {
            $(buttonid).disabled = false;
            return false;
        }    
        updaterNumericRangeIconfile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorNumericRangeIconfile});
    }   
    else if(switchMode == 'NumericRangePrefabfile')
    {
        if(CkPrefab(document.getElementById(fileformid)) == false)
        {
            $(buttonid).disabled = false;
            return false;
        }    
        updaterNumericRangePrefabfile = new Ajax.PeriodicalUpdater(
                                statusDiv, // the div tag id
                                '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                                {asynchronous:true, frequency:1, method: 'get', parameters: 'c=status&mode='+switchMode+'&buttonid='+buttonid+'&statusdiv='+statusDiv, onFailure: reportErrorNumericRangePrefabfile});
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
  function reportErrorDefaultIconfile(request)
  {
    $('submitUploadDefaultIconfileButton').disabled = false;

    $('statusDefaultIconfileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';

    killPeriodicUpdate('DefaultIconfile');
  }
  
  function reportErrorDefaultPrefabfile(request)
  {
    $('submitUploadDefaultPrefabfileButton').disabled = false;

    $('statusDefaultPrefabfileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';
    
    killPeriodicUpdate('DefaultPrefabfile');
  }

  function reportErrorSpecialValueIconfile(request)
  {
    $('submitUploadSpecialValueIconfileButton').disabled = false;

    $('statusSpecialValueIconfileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';

    killPeriodicUpdate('SpecialValueIconfile');
  }
  
  function reportErrorSpecialValuePrefabfile(request)
  {
    $('submitUploadSpecialValuePrefabfileButton').disabled = false;

    $('statusSpecialValuePrefabfileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';
    
    killPeriodicUpdate('SpecialValuePrefabfile');
  }
  
  function reportErrorNumericRangeIconfile(request)
  {
    $('submitUploadNumericRangeIconfileButton').disabled = false;

    $('statusNumericRangeIconfileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';

    killPeriodicUpdate('NumericRangeIconfile');
  }
  
  function reportErrorNumericRangePrefabfile(request)
  {
    $('submitUploadNumericRangePrefabfileButton').disabled = false;

    $('statusNumericRangePrefabfileUpl').innerHTML = '<div class="error"><b>Error communicating with server. Please try again.</b></div>';
    
    killPeriodicUpdate('NumericRangePrefabfile');
  }
  
  //
  // used only to kill the periodic update of Ajax. It is not the final killUpdate function(that follows immediately after this code)
  //
  function killPeriodicUpdate(switchMode)
  {
    //alert("KillPeriodic::"+switchMode);
  
    if(switchMode == 'DefaultIconfile' && updaterDefaultIconfile != null)
    {
        updaterDefaultIconfile.stop();
    }
    else if(switchMode == 'DefaultPrefabfile' && updaterDefaultPrefabfile != null)
    {
        updaterDefaultPrefabfile.stop();
    }
    else if(switchMode == 'SpecialValueIconfile' && updaterSpecialValueIconfile != null)
    {
        updaterSpecialValueIconfile.stop();
    }
    else if(switchMode == 'SpecialValuePrefabfile' && updaterSpecialValuePrefabfile != null)
    {
        updaterSpecialValuePrefabfile.stop();
    }
    else if(switchMode == 'NumericRangeIconfile' && updaterNumericRangeIconfile != null)
    {
        updaterNumericRangeIconfile.stop();
    }
    else if(switchMode == 'NumericRangePrefabfile' && updaterNumericRangePrefabfile != null)
    {
        updaterNumericRangePrefabfile.stop();
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
        if(switchMode == 'DefaultIconfile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorDefaultIconfile});
        }
        else if(switchMode == 'DefaultPrefabfile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorDefaultPrefabfile});
        }
        else if(switchMode == 'SpecialValueIconfile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorSpecialValueIconfile});
        }
        else if(switchMode == 'SpecialValuePrefabfile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorSpecialValuePrefabfile});
        }
        else if(switchMode == 'NumericRangeIconfile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorNumericRangeIconfile});
        }
        else if(switchMode == 'NumericRangePrefabfile')
        {
            new Ajax.Updater(statusDiv, // the div tag id
                      '<%=request.getContextPath()%>/roleVSP/Upload', // the servlet
                     {asynchronous:true, method: 'get', parameters: 'c=status&mode='+switchMode+'&fin=true', onFailure: reportErrorNumericRangePrefabfile});
        }
    }
  }
  
   function CkPrefab(obj)
   {
        val=obj.value.substring(obj.value.length-4, obj.value.length);
        if (val!='.dae')
        {
            alert(val+' is not a .dae file');
            return false;
        }
        else if( val.replace(/^\s+|\s+$/g, '') =='')
        {
            return false;
        }        
   }

   function CkIcon(obj)
   {
        val=obj.value.substring(obj.value.length-4, obj.value.length);
        if (val!='.ico' && val!='.gif' && val!='.png' && val!='.bmp' && val!='.jpg')
        {
            alert(val+' is not a supported image file (ico, gif, png, bmp, jpg)');
            return false;
        }
        else if( val.replace(/^\s+|\s+$/g, '') =='')
        {
            return false;
        }
        
   }
   
   