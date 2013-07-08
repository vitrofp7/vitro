<%@page session='false' contentType='application/x-javascript' import='java.util.*, presentation.webgui.vitroappservlet.Model3dservice.Model3dStyleSpecialCase'
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

//reset selection of dropdowns after reload (need for browsers like firefox)
        function resetSelectionBox(ddlId)
        {
            var optionArr = document.getElementById(ddlId).options;
            optionArr[0].selected = true;
        }

      //
      // should be issued on an  onChange event of a file selection box.
      // the default option (no selection) should have a value of '#'
      function selectFromGenericfileBox(switchmode, optionValue)
      { 
            var optionArr = document.getElementById('select'+switchmode).options;
            var sIdx = null;
            if(optionValue == null)
                sIdx = document.getElementById('select'+switchmode).selectedIndex;
            else
            {
                for(i = 0; i < optionArr.length; i++)
                {
                    if(optionArr[i].value == optionValue)
                    {           
                        document.getElementById('select'+switchmode).focus();
                        optionArr[i].selected = true;
                        sIdx = i;
                        break;
                    }
                }
            }
      }
      
      
      function isHTMLColor(strString)
      {
            var strValidChars = "0123456789ABCDEFabcdef";
            var strChar;
            var blnResult = true;

            if (strString == null || strString.length < 7 || strString.length > 7) return false;

            //  test strString consists of valid characters listed above
            if(strString.charAt(0) != '#')
            {
                blnResult = false;
            }
            else
            {
                for (i = 1; i < strString.length && blnResult == true; i++)
                {
                    strChar = strString.charAt(i);
                    if (strValidChars.indexOf(strChar) == -1)
                    {
                        blnResult = false;
                    }
                }
            }
            return blnResult;
      
      }
      
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

      
      var globalNumRangeNextFrom = "";
      var globalNumRangeNextTo = "";
      var globalNumRangeNextRangeMode = "goRight";
      
      
      function addNumericRangeCase()
      {
        var errorFlag = false;
        document.getElementById('submitNumericRangeCase_0').disabled = true;
        document.getElementById('resetNumericRangeCases_0').disabled = true;
        
        var realFromVal = "";
        var realToVal = "";
        var realColor = "";
        var realIconfile = "";
        var realPrefabfile = "";
        
        var htmlFromVal = "";
        var htmlToVal = "";
        var htmlColor = "";
        var htmlIconfile = "";
        var htmlPrefabfile = "";
        
        var objListNumericRangeCasesDiv = document.getElementById('ListNumericRangeCasesDiv'); 
        var ListNumericRangeCasesDivInHTML = objListNumericRangeCasesDiv.innerHTML;
        
        //
        // If no range (no From AND no To) is specified return false with an alert
        // or not numbers (and no dot)
        // or "From" larger than "To"
        //
        if(document.getElementById('rangeValFrom') == null || document.getElementById('rangeValTo') == null)
        {
            alert('No valid range defined!');
            errorFlag = true;
        }
        else
        {
            realFromVal = document.getElementById('rangeValFrom').value.replace(/^\s+|\s+$/g, '');
            realToVal =  document.getElementById('rangeValTo').value.replace(/^\s+|\s+$/g, '');
            htmlFromVal = realFromVal;
            htmlToVal = realToVal;
            if(realFromVal == "" || (realFromVal.toLowerCase() == "inf") || (realToVal.toLowerCase() == "-inf") )
            {
                realFromVal = "";
                htmlFromVal = "-Inf";
                globalNumRangeNextRangeMode = "goRight";
            }
            if(realToVal == "" || (realToVal.toLowerCase() == "inf") || (realToVal.toLowerCase() == "+inf") )
            {
                realToVal = "";
                htmlToVal = "+Inf";
                globalNumRangeNextRangeMode = "goLeft";
            }
            if((realFromVal != "" && !isNumeric(realFromVal) )||
                (realToVal != "" && !isNumeric(realToVal) ) ||                
               (realFromVal != "" && realToVal != "" &&  parseFloat(realFromVal) >= parseFloat(realToVal) ) )
             {
                alert('No valid range defined!');
                errorFlag = true;
             }
        }        
        if(errorFlag)
        {
            document.getElementById('submitNumericRangeCase_0').disabled  = false;
            document.getElementById('resetNumericRangeCases_0').disabled = false;
            return false;
        }
        
        //
        // If the range specified is valid but has been already defined or overlaps with a defined range!!!!
        // return false with an alert
        //
        objrangeValFromBox = document.formNumericRangeCases.elements['rangeValFromBox[]'];
        objrangeValToBox = document.formNumericRangeCases.elements['rangeValToBox[]'];
        
        var realrangeValFromBox = null;
        var realrangeValToBox = null;
        //new code
        if(objrangeValFromBox != null && objrangeValToBox != null)
        {
            if(typeof objrangeValFromBox.type=="string")
            {
                realrangeValFromBox = new Array();
                realrangeValFromBox[0] = objrangeValFromBox;
            }
            else
            {
                realrangeValFromBox = objrangeValFromBox;
            }    
            //new code
            if(typeof objrangeValToBox.type=="string")
            {
                realrangeValToBox = new Array();
                realrangeValToBox[0] = objrangeValToBox;
            }
            else
            {
                realrangeValToBox = objrangeValToBox;
            }
        
            // check for overlaps
            for(j = 0; j < realrangeValFromBox.length; j++)
            {
                // ranges are closed right and open left intervals   e.g [x,y) 
                
                // check inf cases first
                
                
                if( ( realrangeValFromBox[j].value=="" &&  realrangeValToBox[j].value==""  ) ||
                    ( realFromVal == "" && realrangeValFromBox[j].value=="")  || 
                    (realToVal =="" && realrangeValToBox[j].value=="") ||                     
                    ( realFromVal == "" &&  realToVal !="" && realrangeValFromBox[j].value != "" && parseFloat(realToVal) >  parseFloat(realrangeValFromBox[j].value) ) ||
                    ( realrangeValFromBox[j].value == "" &&  realrangeValToBox[j].value !="" && realFromVal != "" && parseFloat(realrangeValToBox[j].value) >  parseFloat(realFromVal) ) ||
                    ( realToVal == "" &&  realFromVal !="" && realrangeValToBox[j].value != "" && parseFloat(realFromVal) <   parseFloat(realrangeValToBox[j].value) ) ||
                    ( realrangeValToBox[j].value == "" &&  realrangeValFromBox[j].value !="" && realToVal != "" && parseFloat(realrangeValFromBox[j].value) <  parseFloat(realToVal) ) )
                {
                        alert('This range overlaps with an already defined one. Try again!');
                        errorFlag = true;
                        break;
                }                
                else if( realrangeValFromBox[j].value!="" && realrangeValToBox[j].value!="" && realToVal != "" &&  realFromVal !="" && 
                            (   ( parseFloat(realFromVal) >=  parseFloat(realrangeValFromBox[j].value) && parseFloat(realFromVal) <  parseFloat(realrangeValToBox[j].value) ) ||
                                ( parseFloat(realToVal) >  parseFloat(realrangeValFromBox[j].value) && parseFloat(realToVal) <=  parseFloat(realrangeValToBox[j].value) )  ||  
                                ( parseFloat(realFromVal) <=  parseFloat(realrangeValFromBox[j].value) && parseFloat(realToVal) >=  parseFloat(realrangeValToBox[j].value) )   )   )
                {
                        alert('This range overlaps with an already defined one. Try again!');
                        errorFlag = true;
                        break;
                }
                
            }
            if(errorFlag)
            {
                document.getElementById('submitNumericRangeCase_0').disabled  = false;
                document.getElementById('resetNumericRangeCases_0').disabled = false;
                return false;
            }
        }
        
        // .options always returns an array so we don't have to re-check if it is an array (even if it has only one value)
        var selectionOfNumericRangeIconsArr1 = document.getElementById('selectNumericRangeIconfile').options;
        var sNumericRangeIconIdx = document.getElementById('selectNumericRangeIconfile').selectedIndex;         
        if(selectionOfNumericRangeIconsArr1[sNumericRangeIconIdx].value == "#" || selectionOfNumericRangeIconsArr1[sNumericRangeIconIdx].value =="")
        {
            realIconfile = "";
            htmlIconfile = "none";
        }
        else
        {
            realIconfile = selectionOfNumericRangeIconsArr1[sNumericRangeIconIdx].value;
            htmlIconfile = realIconfile;
        }

        var selectionOfNumericRangePrefabsArr1 = document.getElementById('selectNumericRangePrefabfile').options;
        var sNumericRangePrefabIdx = document.getElementById('selectNumericRangePrefabfile').selectedIndex;         
        if(selectionOfNumericRangePrefabsArr1[sNumericRangePrefabIdx].value == "#" || selectionOfNumericRangePrefabsArr1[sNumericRangePrefabIdx].value =="")
        {
            realPrefabfile = "";
            htmlPrefabfile = "none";
        }
        else
        {
            realPrefabfile = selectionOfNumericRangePrefabsArr1[sNumericRangePrefabIdx].value;
            htmlPrefabfile = realPrefabfile;
        }
        // Check color format. 
        if(document.getElementById('pick1188464400field') == null)
        {
            realColor ="";
            htmlColor = "default";
        }
        else
        {
            realColor = document.getElementById('pick1188464400field').value.replace(/^\s+|\s+$/g, '');
            if(realColor == "" || !isHTMLColor(realColor) )
            {
                realColor ="";
                htmlColor = "default";
            }    
            else
            {
                htmlColor = "<a href=\"#\" onclick=\"return false;\" style=\"border: 1px solid #OOOOOO; font-family:Verdana; font-size:10px; text-decoration: none; background-color:"+realColor+";\">&nbsp;&nbsp;&nbsp;</a>";
            }
        }
        //
        // Get defined range  and print it in the table cells
        // Paint the row with a distinct color
        //
        ListNumericRangeCasesDivInHTML +="<table>";
        ListNumericRangeCasesDivInHTML +="<tr bgcolor=\"#FFFEE0\">";
        ListNumericRangeCasesDivInHTML +="<td valign=\"top\">";
        ListNumericRangeCasesDivInHTML +="<input type=\"hidden\" name=\"rangeValFromBox[]\" value=\""+realFromVal+"\" >["+htmlFromVal;
        ListNumericRangeCasesDivInHTML +="<input type=\"hidden\" name=\"rangeValToBox[]\" value=\""+realToVal+"\" >,&nbsp;"+htmlToVal+")";
        ListNumericRangeCasesDivInHTML +="</td>";
        ListNumericRangeCasesDivInHTML +="<td valign=\"top\">";
        ListNumericRangeCasesDivInHTML +="<input type=\"hidden\" name=\"rangeColorBox[]\" value=\""+realColor+"\" >Color:&nbsp;"+htmlColor;
        ListNumericRangeCasesDivInHTML +="</td>";
        ListNumericRangeCasesDivInHTML +="<td valign=\"top\">";
        ListNumericRangeCasesDivInHTML +="<input type=\"hidden\" name=\"rangeIconfileBox[]\" value=\""+realIconfile+"\" >Icon:&nbsp;"+htmlIconfile;
        ListNumericRangeCasesDivInHTML +="</td>";
        ListNumericRangeCasesDivInHTML +="<td valign=\"top\">";
        ListNumericRangeCasesDivInHTML +="<input type=\"hidden\" name=\"rangePrefabfileBox[]\" value=\""+realPrefabfile+"\" >Prefab:&nbsp;"+htmlPrefabfile;
        ListNumericRangeCasesDivInHTML +="</td>";
        ListNumericRangeCasesDivInHTML +="</tr>";
        ListNumericRangeCasesDivInHTML +="</table>";
        
        objListNumericRangeCasesDiv.innerHTML = ListNumericRangeCasesDivInHTML;
                
        // if no errors then do some automagic :)
        if(!errorFlag)        
        {
            if(realFromVal == "" && realToVal == "")
            {
                globalNumRangeNextFrom = "";
                globalNumRangeNextTo = "";
            }
            else
            {
            
                if(globalNumRangeNextRangeMode == "goRight") // search for the next available From value.
                {                
                    refToVal = realToVal;
                    searchNextEntryToTheRight(refToVal, realrangeValFromBox, realrangeValToBox);
                }
                if(globalNumRangeNextRangeMode == "goLeft") // search for the next available To value. (we don't use an else if because
                {
                    refFromVal = realFromVal;
                    searchNextEntryToTheLeft(refFromVal, realrangeValFromBox, realrangeValToBox);
                }            
            }
            document.getElementById('rangeValFrom').value=globalNumRangeNextFrom;
            document.getElementById('rangeValTo').value=globalNumRangeNextTo ;
            
        }
        // re-enable buttons
        document.getElementById('submitNumericRangeCase_0').disabled  = false;
        document.getElementById('resetNumericRangeCases_0').disabled = false; 
        return true;
      
      }
      
      
      function searchNextEntryToTheRight(refToVal, realrangeValFromBox, realrangeValToBox)
      {
            candidateEntryFrom = "";
            candidateEntryTo = "";
            globalNumRangeNextFrom = refToVal;
            globalNumRangeNextTo = "";
            
            if(realrangeValFromBox == null || realrangeValToBox == null)
                return;
            
            for(j = 0; j < realrangeValFromBox.length; j++)
            {
                if(refToVal !="" && realrangeValFromBox[j].value != "" && parseFloat(refToVal) <=   parseFloat(realrangeValFromBox[j].value) )
                {
                    if( globalNumRangeNextTo == "" || 
                            (globalNumRangeNextTo!= "" && parseFloat(globalNumRangeNextTo) > parseFloat(realrangeValFromBox[j].value)  ) )
                    {
                        globalNumRangeNextTo = realrangeValFromBox[j].value;
                        candidateEntryFrom = realrangeValFromBox[j].value;
                        candidateEntryTo = realrangeValToBox[j].value;
                    }
                }
            }
            if(refToVal == globalNumRangeNextTo && candidateEntryTo =="")
            {
                    globalNumRangeNextRangeMode = "goLeft";
            }
            else if(refToVal == globalNumRangeNextTo && candidateEntryTo !="")
            {// recursive call with candidateEntryTo as refToVal.
                    searchNextEntryToTheRight(candidateEntryTo, realrangeValFromBox, realrangeValToBox);
            }
       }

      function searchNextEntryToTheLeft(refFromVal, realrangeValFromBox, realrangeValToBox)
      {
            candidateEntryFrom = "";
            candidateEntryTo = "";
            globalNumRangeNextFrom = "";
            globalNumRangeNextTo = refFromVal;

            if(realrangeValFromBox == null || realrangeValToBox == null)
                return;
            
            for(j = 0; j < realrangeValFromBox.length; j++)
            {
                if(refFromVal !="" && realrangeValToBox[j].value != "" && parseFloat(refFromVal) >=   parseFloat(realrangeValToBox[j].value) )
                {
                    if( globalNumRangeNextFrom == "" || 
                            (globalNumRangeNextFrom!= "" && parseFloat(globalNumRangeNextFrom) <  parseFloat(realrangeValToBox[j].value)  ) )
                    {
                        globalNumRangeNextFrom = realrangeValToBox[j].value;
                        candidateEntryFrom = realrangeValFromBox[j].value;
                        candidateEntryTo = realrangeValToBox[j].value;
                    }
                }
            }    
            if(refFromVal == globalNumRangeNextFrom && candidateEntryFrom =="")
            {
                globalNumRangeNextFrom = "";
                globalNumRangeNextTo = "";
                return;
            }
            else if(refFromVal == globalNumRangeNextFrom && candidateEntryFrom !="")
            {// recursive call with candidateEntryFrom as refFromVal.
                searchNextEntryToTheLeft(candidateEntryFrom,  realrangeValFromBox, realrangeValToBox);
            }
       }    
      
      
      function resetNumericRangeCases()
      {
            document.getElementById('submitNumericRangeCase_0').disabled  = true;
            document.getElementById('resetNumericRangeCases_0').disabled = true;
            //
            // clear top div that lists the defined ranges
            //
            document.getElementById('ListNumericRangeCasesDiv').innerHTML ="";
                
            // re-enable buttons
            document.getElementById('submitNumericRangeCase_0').disabled  = false;
            document.getElementById('resetNumericRangeCases_0').disabled = false; 
            return true;
      }      
      
      //
      // Special Value Cases
      //
      function addSpecialValueCase()
      {
        var errorFlag = false;
        document.getElementById('submitSpecialValueCase_0').disabled = true;
        document.getElementById('resetSpecialValueCases_0').disabled = true;
        
        var realSpecialVal = "";
        var realColor = "";
        var realIconfile = "";
        var realPrefabfile = "";
        
        var htmlSpecialVal = "";
        var htmlColor = "";
        var htmlIconfile = "";
        var htmlPrefabfile = "";
        
        var objListSpecialValueCasesDiv = document.getElementById('ListSpecialValueCasesDiv'); 
        var ListSpecialValueCasesDivInHTML = objListSpecialValueCasesDiv.innerHTML;
        
        //
        // If no special value is selected return false with an alert
        //
        if(document.getElementById('selectSpecialValueCase') == null)
        {
            alert('All special values are already mapped!');
            document.getElementById('submitSpecialValueCase_0').disabled = false;
            document.getElementById('resetSpecialValueCases_0').disabled = false;
            return false;
        }
        // .options always returns an array so we don't have to re-check if it is an array (even if it has only one value)
        var selectionOfSpecialValuesArr1 = document.getElementById('selectSpecialValueCase').options;                
        var sSpecialValueIdx = document.getElementById('selectSpecialValueCase').selectedIndex; 
        
        if(selectionOfSpecialValuesArr1[sSpecialValueIdx].value == "#" || selectionOfSpecialValuesArr1[sSpecialValueIdx].value =="")
        {
            alert('No special value was selected! Please try again...');
            document.getElementById('submitSpecialValueCase_0').disabled = false;
            document.getElementById('resetSpecialValueCases_0').disabled = false;           
            return false;
        }
        
        var objSpecialValueCasesListDiv =  document.getElementById('selectSpecialValueCasesDiv');
        var renewSpecialValueCasesListinHTML =  "";

        remainingSpecialValues = 0;
        var renewSpecialValueCaseOPTIONShtml = "";
        for(i = 0; i < selectionOfSpecialValuesArr1.length; i++) //<--- for each (remaining) special value in the selection box
        {
            if( i!= sSpecialValueIdx)
            {
                remainingSpecialValues +=1;
                tmpSpecialValueCase = selectionOfSpecialValuesArr1[i].value;
                renewSpecialValueCaseOPTIONShtml += "<option value =\""+tmpSpecialValueCase+"\">"+tmpSpecialValueCase+"</option>";
            }
        }
        if(remainingSpecialValues == 0)
        {
            renewSpecialValueCasesListinHTML += "All special values were mapped";
        }    
        else
        {
            renewSpecialValueCasesListinHTML += "<select NAME=\"selectSpecialValueCase\" ID=\"selectSpecialValueCase\"  >";
            renewSpecialValueCasesListinHTML += renewSpecialValueCaseOPTIONShtml;
            renewSpecialValueCasesListinHTML += "</select>";
        }   
        
        
        // .options always returns an array so we don't have to re-check if it is an array (even if it has only one value)
        var selectionOfSpecialValueIconsArr1 = document.getElementById('selectSpecialValueIconfile').options;
        var sSpecialValueIconIdx = document.getElementById('selectSpecialValueIconfile').selectedIndex;         
        if(selectionOfSpecialValueIconsArr1[sSpecialValueIconIdx].value == "#" || selectionOfSpecialValueIconsArr1[sSpecialValueIconIdx].value =="")
        {
            realIconfile = "";
            htmlIconfile = "none";
        }
        else
        {
            realIconfile = selectionOfSpecialValueIconsArr1[sSpecialValueIconIdx].value;
            htmlIconfile = realIconfile;
        }

        var selectionOfSpecialValuePrefabsArr1 = document.getElementById('selectSpecialValuePrefabfile').options;
        var sSpecialValuePrefabIdx = document.getElementById('selectSpecialValuePrefabfile').selectedIndex;         
        if(selectionOfSpecialValuePrefabsArr1[sSpecialValuePrefabIdx].value == "#" || selectionOfSpecialValuePrefabsArr1[sSpecialValuePrefabIdx].value =="")
        {
            realPrefabfile = "";
            htmlPrefabfile = "none";
        }
        else
        {
            realPrefabfile = selectionOfSpecialValuePrefabsArr1[sSpecialValuePrefabIdx].value;
            htmlPrefabfile = realPrefabfile;
        }
        // Check color format. 
        if(document.getElementById('pick1188464300field') == null)
        {
            realColor ="";
            htmlColor = "default";
        }
        else
        {
            realColor = document.getElementById('pick1188464300field').value.replace(/^\s+|\s+$/g, '');
            if(realColor == "" || !isHTMLColor(realColor) )
            {
                realColor ="";
                htmlColor = "default";
            }    
            else
            {
                htmlColor = "<a href=\"#\" onclick=\"return false;\" style=\"border: 1px solid #OOOOOO; font-family:Verdana; font-size:10px; text-decoration: none; background-color:"+realColor+";\">&nbsp;&nbsp;&nbsp;</a>";
            }
        }
        //
        // Get defined range  and print it in the table cells
        // Paint the row with a distinct color
        //
        ListSpecialValueCasesDivInHTML +="<table>";
        ListSpecialValueCasesDivInHTML +="<tr bgcolor=\"#FFFEE0\">";
        ListSpecialValueCasesDivInHTML +="<td valign=\"top\">";
        ListSpecialValueCasesDivInHTML +="<input type=\"hidden\" name=\"specialValuesBox[]\" value=\""+selectionOfSpecialValuesArr1[sSpecialValueIdx].value+"\" >["+selectionOfSpecialValuesArr1[sSpecialValueIdx].value+"]";
        ListSpecialValueCasesDivInHTML +="</td>";
        ListSpecialValueCasesDivInHTML +="<td valign=\"top\">";
        ListSpecialValueCasesDivInHTML +="<input type=\"hidden\" name=\"specialValueColorBox[]\" value=\""+realColor+"\" >Color:&nbsp;"+htmlColor;
        ListSpecialValueCasesDivInHTML +="</td>";
        ListSpecialValueCasesDivInHTML +="<td valign=\"top\">";
        ListSpecialValueCasesDivInHTML +="<input type=\"hidden\" name=\"specialValueIconfileBox[]\" value=\""+realIconfile+"\" >Icon:&nbsp;"+htmlIconfile;
        ListSpecialValueCasesDivInHTML +="</td>";
        ListSpecialValueCasesDivInHTML +="<td valign=\"top\">";
        ListSpecialValueCasesDivInHTML +="<input type=\"hidden\" name=\"specialValuePrefabfileBox[]\" value=\""+realPrefabfile+"\" >Prefab:&nbsp;"+htmlPrefabfile;
        ListSpecialValueCasesDivInHTML +="</td>";
        ListSpecialValueCasesDivInHTML +="</tr>";
        ListSpecialValueCasesDivInHTML +="</table>";
        
        objListSpecialValueCasesDiv.innerHTML = ListSpecialValueCasesDivInHTML;
        objSpecialValueCasesListDiv.innerHTML = renewSpecialValueCasesListinHTML;
                
        // re-enable buttons
        document.getElementById('submitSpecialValueCase_0').disabled  = false;
        document.getElementById('resetSpecialValueCases_0').disabled = false; 
        return true;
      }
      
      function resetSpecialValueCases()
      {
            document.getElementById('submitSpecialValueCase_0').disabled  = true;
            document.getElementById('resetSpecialValueCases_0').disabled = true;
            //
            // clear top div that lists the defined Special Values
            //
            document.getElementById('ListSpecialValueCasesDiv').innerHTML ="";                
            var objSpecialValueCasesListDiv =  document.getElementById('selectSpecialValueCasesDiv');
            objSpecialValueCasesListDiv.innerHTML = "<select NAME=\"selectSpecialValueCase\" id=\"selectSpecialValueCase\" >";
                                                     <%
                                                        String[] allValidSpVal = Model3dStyleSpecialCase.getValidSpecialValues();
                                                        for(int j = 0 ; j < allValidSpVal.length - 1; j++) // length -1 to leave out the "undefined special value" case
                                                        {%>
           objSpecialValueCasesListDiv.innerHTML  += "<option value =\"<%= allValidSpVal[j]%>\" ><%=allValidSpVal[j]%></option>";
                                                      <%}
                                                     %>
           objSpecialValueCasesListDiv.innerHTML  += "</select>";            
            // re-enable buttons
            document.getElementById('submitSpecialValueCase_0').disabled  = false;
            document.getElementById('resetSpecialValueCases_0').disabled = false; 
            return true;
      }  
      
      var globalstatusSubmitFinalDesc;
      //
      // View Jfreechart png or Submit a New Style
      //
      function submitNewStyleSoFarForm()
      {
            var argus = arguments.length;
            var mode = "";
            if(argus >= 1)
                mode = arguments[0];
                
            globalstatusSubmitFinalDesc = '&nbsp;';
            document.getElementById('resultMsgStyleSubmitFinal').innerHTML ='&nbsp;';
            
            document.getElementById('SubmitStyleFinal').disabled = true;
             
            objformNewStyleSoFar = document.getElementById('formNewStyleSoFar');
            if(objformNewStyleSoFar != null )
            {
                var nssfFormInnerHTML = "";
                var RPostForAjax = "";
                //
                // Selected Capability
                //
                var optionCapabilitiesArr = document.getElementById('selectGenericCapability').options;
                var sCapabilitiesIdx = document.getElementById('selectGenericCapability').selectedIndex;
                
                if(optionCapabilitiesArr[sCapabilitiesIdx].value == '#' || optionCapabilitiesArr[sCapabilitiesIdx].value.replace(/^\s+|\s+$/g, '') == '')
                {
                    alert('No valid capability was selected for the style!');
                    document.getElementById('SubmitStyleFinal').disabled = false;
                    return false;
                }
                nssfFormInnerHTML += "<input type=hidden NAME=\"nssfCapability\" ID=\"nssfCapability\"  value =\""+optionCapabilitiesArr[sCapabilitiesIdx].value+"\" />";
                RPostForAjax += "nssfCapability="+optionCapabilitiesArr[sCapabilitiesIdx].value+"&";
                //
                // Selected Global/Default Color
                // (Check color format first)
                // 
                objGlobalColor = document.getElementById('pick1188464214field');
                var  realColor ="#FFFFFF";
                if(objGlobalColor != null)                
                {
                    realColor = objGlobalColor.value.replace(/^\s+|\s+$/g, '');
                    if(realColor == "" || !isHTMLColor(realColor) )
                    {
                        realColor ="#FFFFFF";
                    }                    
                }                
                nssfFormInnerHTML += "<input type=hidden NAME=\"nssfGlobalColor\" ID=\"nssfGlobalColor\"  value =\""+realColor+"\" />";
                RPostForAjax += "nssfGlobalColor="+realColor+"&";
                //
                // Selected Global/Default Icon
                //
                var optionDefaultIconfilesArr = document.getElementById('selectDefaultIconfile').options;
                var sDefaultIconfilesIdx = document.getElementById('selectDefaultIconfile').selectedIndex;
                var realDefaultIconfile = "";
                if(optionDefaultIconfilesArr[sDefaultIconfilesIdx].value == '#' || optionDefaultIconfilesArr[sDefaultIconfilesIdx].value.replace(/^\s+|\s+$/g, '') == '')
                {
                    realDefaultIconfile = ""; 
                }
                else
                {
                    realDefaultIconfile = optionDefaultIconfilesArr[sDefaultIconfilesIdx].value; 
                }
                nssfFormInnerHTML += "<input type=hidden NAME=\"nssfDefaultIconfile\" ID=\"nssfDefaultIconfile\"  value =\""+realDefaultIconfile+"\" />";
                RPostForAjax += "nssfDefaultIconfile="+realDefaultIconfile+"&";
                //
                // Selected Global/Default Prefab
                //
                 var optionDefaultPrefabfilesArr = document.getElementById('selectDefaultPrefabfile').options;
                var sDefaultPrefabfilesIdx = document.getElementById('selectDefaultPrefabfile').selectedIndex;
                var realDefaultPrefabfile = "";
                if(optionDefaultPrefabfilesArr[sDefaultPrefabfilesIdx].value == '#' || optionDefaultPrefabfilesArr[sDefaultPrefabfilesIdx].value.replace(/^\s+|\s+$/g, '') == '')
                {
                    realDefaultPrefabfile = ""; 
                }
                else
                {
                    realDefaultPrefabfile = optionDefaultPrefabfilesArr[sDefaultPrefabfilesIdx].value; 
                }
                nssfFormInnerHTML += "<input type=hidden NAME=\"nssfDefaultPrefabfile\" ID=\"nssfDefaultPrefabfile\"  value =\""+realDefaultPrefabfile+"\" />";
                RPostForAjax += "nssfDefaultPrefabfile="+realDefaultPrefabfile+"&";               
                //
                // For each defined special value, add the attributes in the  nssfFormInnerHTML and RPostForAjax
                //
                //
                // Special Values defined
                //
                objspecialValuesBox = document.formSpecialValueCases.elements['specialValuesBox[]'];
                objspecialValueColorBox = document.formSpecialValueCases.elements['specialValueColorBox[]'];
                objspecialValueIconfileBox = document.formSpecialValueCases.elements['specialValueIconfileBox[]'];
                objspecialValuePrefabfileBox = document.formSpecialValueCases.elements['specialValuePrefabfileBox[]'];
                
                var realspecialValuesBox = null;
                var realspecialValueColorBox = null;
                var realspecialValueIconfileBox = null;
                var realspecialValuePrefabfileBox = null;
                
                //new code
                if(objspecialValuesBox != null && objspecialValueColorBox!=null && objspecialValueIconfileBox!=null && objspecialValuePrefabfileBox!=null)
                {
                    // these arrays have all the same size (and typeof type e.g array or string), so we just loop through one of them and get the corresponding values from the others!
                    if(typeof objspecialValuesBox.type=="string")
                    {
                        realspecialValuesBox = new Array();
                        realspecialValueColorBox = new Array();
                        realspecialValueIconfileBox = new Array(); 
                        realspecialValuePrefabfileBox = new Array();
                        
                        realspecialValuesBox[0] = objspecialValuesBox;
                        realspecialValueColorBox[0] = objspecialValueColorBox;
                        realspecialValueIconfileBox[0] = objspecialValueIconfileBox; 
                        realspecialValuePrefabfileBox[0] = objspecialValuePrefabfileBox;
                    }
                    else
                    {
                        realspecialValuesBox = objspecialValuesBox;
                        realspecialValueColorBox = objspecialValueColorBox;
                        realspecialValueIconfileBox = objspecialValueIconfileBox; 
                        realspecialValuePrefabfileBox = objspecialValuePrefabfileBox;
                    }    
                    for (counter = 0; counter < realspecialValuesBox.length; counter++)
                    {        
                        var specialValue = realspecialValuesBox[counter].value;
                        var specialValueColor = realspecialValueColorBox[counter].value;
                        var specialValueIconfile = realspecialValueIconfileBox[counter].value;
                        var specialValuePrefabfile = realspecialValuePrefabfileBox[counter].value;
                        
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfSpecialValuesBox[]\" ID=\"nssfSpecialValuesBox_"+counter+"\"  value =\""+specialValue+"\" />";
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfSpecialValueColor[]\" ID=\"nssfSpecialValueColor_"+counter+"\"  value =\""+specialValueColor+"\" />";
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfSpecialValueIconfile[]\" ID=\"nssfSpecialValueIconfile_"+counter+"\"  value =\""+specialValueIconfile+"\" />";                        
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfSpecialValuePrefabfile[]\" ID=\"nssfSpecialValuePrefabfile_"+counter+"\"  value =\""+specialValuePrefabfile+"\" />";                        
                        
                        RPostForAjax += "nssfSpecialValuesBox[]="+specialValue+"&";
                        RPostForAjax += "nssfSpecialValueColor[]="+specialValueColor+"&";
                        RPostForAjax += "nssfSpecialValueIconfile[]="+specialValueIconfile+"&";
                        RPostForAjax += "nssfSpecialValuePrefabfile[]="+specialValuePrefabfile+"&";                        
                    }
                }
                //
                // Numeric Ranges defined
                //                
                objrangeValFromBox = document.formNumericRangeCases.elements['rangeValFromBox[]'];
                objrangeValToBox = document.formNumericRangeCases.elements['rangeValToBox[]'];
                objrangeColorBox = document.formNumericRangeCases.elements['rangeColorBox[]'];
                objrangeIconfileBox = document.formNumericRangeCases.elements['rangeIconfileBox[]'];
                objrangePrefabfileBox = document.formNumericRangeCases.elements['rangePrefabfileBox[]'];
                
                var realrangeValFromBox = null;
                var realrangeValToBox = null;
                var realrangeColorBox = null;
                var realrangeIconfileBox = null;
                var realrangePrefabfileBox = null;
                
                //new code
                if(objrangeValFromBox != null && objrangeValToBox != null && objrangeColorBox!=null && objrangeIconfileBox!=null && objrangePrefabfileBox!=null)
                {
                    // these arrays have all the same size (and typeof type e.g array or string), so we just loop through one of them and get the corresponding values from the others!
                    if(typeof objrangeValFromBox.type=="string")
                    {
                        realrangeValFromBox = new Array();
                        realrangeValToBox = new Array();
                        realrangeColorBox = new Array();
                        realrangeIconfileBox = new Array(); 
                        realrangePrefabfileBox = new Array();
                        
                        realrangeValFromBox[0] = objrangeValFromBox;
                        realrangeValToBox[0] = objrangeValToBox;
                        realrangeColorBox[0] = objrangeColorBox;
                        realrangeIconfileBox[0] = objrangeIconfileBox; 
                        realrangePrefabfileBox[0] = objrangePrefabfileBox;
                    }
                    else
                    {
                        realrangeValFromBox = objrangeValFromBox;
                        realrangeValToBox = objrangeValToBox;
                        realrangeColorBox = objrangeColorBox;
                        realrangeIconfileBox = objrangeIconfileBox; 
                        realrangePrefabfileBox = objrangePrefabfileBox;
                    }    
                    for (counter = 0; counter < realrangeValFromBox.length; counter++)
                    {        
                        var rangeFrom = realrangeValFromBox[counter].value;
                        var rangeTo = realrangeValToBox[counter].value;
                        var rangeColor = realrangeColorBox[counter].value;
                        var rangeIconfile = realrangeIconfileBox[counter].value;
                        var rangePrefabfile = realrangePrefabfileBox[counter].value;
                        
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfRangeFromBox[]\" ID=\"nssfRangeFromBox_"+counter+"\"  value =\""+rangeFrom+"\" />";
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfRangeToBox[]\" ID=\"nssfRangeToBox_"+counter+"\"  value =\""+rangeTo+"\" />";
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfRangeColor[]\" ID=\"nssfRangeColor_"+counter+"\"  value =\""+rangeColor+"\" />";
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfRangeIconfile[]\" ID=\"nssfRangeIconfile_"+counter+"\"  value =\""+rangeIconfile+"\" />";                        
                        nssfFormInnerHTML += "<input type=hidden NAME=\"nssfRangePrefabfile[]\" ID=\"nssfRangePrefabfile_"+counter+"\"  value =\""+rangePrefabfile+"\" />";                        
                        
                        RPostForAjax += "nssfRangeFromBox[]="+rangeFrom+"&";
                        RPostForAjax += "nssfRangeToBox[]="+rangeTo+"&";
                        RPostForAjax += "nssfRangeColor[]="+rangeColor+"&";
                        RPostForAjax += "nssfRangeIconfile[]="+rangeIconfile+"&";
                        RPostForAjax += "nssfRangePrefabfile[]="+rangePrefabfile+"&";                        
                    }
                }                
                
                nssfFormInnerHTML += "<input type=hidden NAME=\"nssfMode\" ID=\"nssfMode\"  value =\""+mode+"\" />";
                RPostForAjax += "nssfMode="+mode; // this is the final POST parameter so no trailing "&"
                
                // debug
                //alert(nssfFormInnerHTML);
                //alert(RPostForAjax);

                var objHiddenNSSFFormFields = document.getElementById('nssfnsfFormDiv');
                objHiddenNSSFFormFields.innerHTML = nssfFormInnerHTML;
                
                if(mode=="preview")
                {
                    objformNewStyleSoFar.submit();
                    document.getElementById('SubmitStyleFinal').disabled = false;
                 }
                else if(mode=="submit")
                {
                    Element.show('progressMsgStyleSubmitFinal');
                    RPostSubmitFinalStyle(RPostForAjax);
                }
            }
      }
      
    // 
    // Start of Ajax for new style submission.
    //
    
    function RPostSubmitFinalStyle(strpost)
    {
	var xmlhttp=null;
	xmlhttp=getXMLHTTPRequest();
	if (xmlhttp==null )
	{
            alert("Your browser does not support XMLHTTP.");
	}
	else
	{
            xmlhttp.open("POST", "<%=request.getContextPath()%>/roleVSP/CreateStyle", true);
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
                        //document.getElementById('SubmitStyleFinal').disabled = false;
                        Effect.Fade('progressMsgStyleSubmitFinal');
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
        Effect.Fade('progressMsgStyleSubmitFinal');
        globalstatusSubmitFinalDesc = statusDescription;
        setTimeout("showSubmitFinalResultStatus()", 600);
    }
    
    function showSubmitFinalResultStatus()
    {
        document.getElementById('resultMsgStyleSubmitFinal').innerHTML = globalstatusSubmitFinalDesc;
        Effect.Appear('resultMsgStyleSubmitFinal');
        setTimeout("document.getElementById('SubmitStyleFinal').disabled = false;", 600);   
    }
      
      
      
      
      