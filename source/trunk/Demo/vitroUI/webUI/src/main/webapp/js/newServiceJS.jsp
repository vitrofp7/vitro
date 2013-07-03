<%@page session='false' contentType='application/x-javascript' language="java" %>

function addDummySubServicePartialToComposite(){
    // todo: Check if there are any partial subservices to add
    var sIdx = 0;
    var subServicesPartsTbl=document.getElementById("subServicesTbl");
    var definedSubServicesTbl=document.getElementById("definedSubServicesTbl");
	
    var nextIdx = parseInt(document.getElementById("autoIncForDefinedSubServices").value)  + 1;
    document.getElementById("autoIncForDefinedSubServices").value = nextIdx;
        var subServicesPartsTblrow = subServicesPartsTbl.rows[2];
		var definedSubServicesTblrow=definedSubServicesTbl.insertRow(-1); // add at the end
	    var cellSubServiceID = definedSubServicesTblrow.insertCell(0);
        var cellNodeSelect=definedSubServicesTblrow.insertCell(1);
        var cellCapSelect=definedSubServicesTblrow.insertCell(2);
        var cellThresh=definedSubServicesTblrow.insertCell(3);
        var cellFreq=definedSubServicesTblrow.insertCell(4);
		cellFreq.style.display="none";
        var cellAction=definedSubServicesTblrow.insertCell(5);
        cellSubServiceID.innerHTML="<input type=\"hidden\" id=\"uniqSubServiceCompId_"+ nextIdx +"\"  value=\"\" />"+ nextIdx;
        var idOfRow = 'xx';
        var purSubServPartId = 'oo';
        for(var j = 0, col; col = subServicesPartsTblrow.cells[j]; j++)
        {
           var divControl = col.getElementsByTagName('div')[0];
            var varCellContent = '';
            if(divControl){
                varCellContent = divControl.innerHTML;
            }
            if(j==0) {
                idOfRow = col.getElementsByTagName('input')[0].id;    //first input (hidden) element is the unique id of the row (prefixed by uniqSubServicePartId_)
                //purSubServPartId = idOfRow.replace('uniqSubServicePartId_','');
                cellNodeSelect.innerHTML="<input type=\"hidden\" id=\"uniqserviceCompPartId_"+ purSubServPartId +"\"  value=\"\" /><input type=\"hidden\" name=\"serviceCompPartFuncts[]\" id=\"serviceCompPartFuncts_"+ purSubServPartId +"\" value=\""+ purSubServPartId +"\" /><input type=\"hidden\" name=\"serviceCompPartNodes[]\" id=\"serviceCompPartNodes_"+ purSubServPartId +"\" value=\""+ purSubServPartId +"\" />"+ varCellContent;
            }
            else if(j==1)// capabilities
            {
                cellCapSelect.innerHTML= "<input type=\"hidden\" name=\"serviceCompPartCaps[]\" id=\"serviceCompPartCaps_"+ purSubServPartId +"\"  value=\""+ purSubServPartId +"\" />"+  varCellContent;
            }else if(j==2) // threshold
            {
                cellThresh.innerHTML="<input type=\"hidden\" name=\"serviceCompPartThresh[]\" id=\"serviceCompPartThresh_"+ purSubServPartId +"\"  value=\""+ purSubServPartId +"\" />"+  varCellContent;
            }else if(j==3) // Freq
            {
                cellFreq.innerHTML="<input type=\"hidden\" name=\"serviceCompPartFreq[]\" id=\"serviceCompPartFreq_"+ purSubServPartId +"\"  value=\""+purSubServPartId+"\" />"+  varCellContent;

            }else if(j==4) // action
            {
                cellAction.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeserviceCompPart(\"uniqserviceCompPartId_"+purSubServPartId+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
            }		
		}

    if(document.getElementById('definedSubServicesDiv').style.display == 'none')
    {
        document.getElementById('definedSubServicesDiv').style.display = 'block';
    }
}


// adding to composite service
function addSubServicePartialToComposite(){
    // todo: Check if there are any partial subservices to add
    var sIdx = 0;

    var subServicesPartsTbl=document.getElementById("subServicesTbl");

    var definedSubServicesTbl=document.getElementById("definedSubServicesTbl");

    var subServicesPartsTblRowCount = subServicesPartsTbl.rows.length;
	
	
    if(subServicesPartsTblRowCount <= 3)
    {
        alert('Nothing to add. Please define a subservice first.');
        return;
    }
	
	
    // the new SubService ID
    var nextIdx = parseInt(document.getElementById("autoIncForDefinedSubServices").value)  + 1;
    document.getElementById("autoIncForDefinedSubServices").value = nextIdx;
    //get all rows (except the first three) from subServicesPartsTbl table and add them as a new subservice here.
    for(var i=3; i< subServicesPartsTblRowCount; i++) {
        var subServicesPartsTblrow = subServicesPartsTbl.rows[i];
        var definedSubServicesTblrow=definedSubServicesTbl.insertRow(-1); // add at the end
        //subServiceNodeSelectionIns, activeMapSelectionHD, subServiceCapSelectionIns, subServiceThreshIns, subServiceFreqIns
        var cellSubServiceID = definedSubServicesTblrow.insertCell(0);
        var cellNodeSelect=definedSubServicesTblrow.insertCell(1);
        var cellCapSelect=definedSubServicesTblrow.insertCell(2);
        var cellThresh=definedSubServicesTblrow.insertCell(3);
        var cellFreq=definedSubServicesTblrow.insertCell(4);
 		cellFreq.style.display="none";
        var cellAction=definedSubServicesTblrow.insertCell(5);
        cellSubServiceID.innerHTML="<input type=\"hidden\" name=\"uniqSubServiceCompId[]\" id=\"uniqSubServiceCompId_"+ nextIdx +"\"  value=\""+ nextIdx+ "\" />"+ nextIdx;
        var idOfRow = 'xx';
        var purSubServPartId = 'oo';
        for(var j = 0, col; col = subServicesPartsTblrow.cells[j]; j++)
        {
            var divControl = col.getElementsByTagName('div')[0];
            var varCellContent = '';
            if(divControl){
                varCellContent = divControl.innerHTML;
            }
            if(j==0) {
                idOfRow = col.getElementsByTagName('input')[0].id;    //first input (hidden) element is the unique id of the row (prefixed by uniqSubServicePartId_)
                purSubServPartId = idOfRow.replace('uniqSubServicePartId_','');
                cellNodeSelect.innerHTML="<input type=\"hidden\" id=\"uniqserviceCompPartId_"+ purSubServPartId +"\"  value=\"\" /><input type=\"hidden\" name=\"serviceCompPartFuncts[]\" id=\"serviceCompPartFuncts_"+ purSubServPartId +"\" value=\""+ document.getElementById('subServicePartFuncts_' +purSubServPartId).value  +"\" /><input type=\"hidden\" name=\"serviceCompPartNodes[]\" id=\"serviceCompPartNodes_"+ purSubServPartId +"\" value=\""+document.getElementById('subServicePartNodes_' +purSubServPartId).value+"\" />"+ varCellContent;
            }
            else if(j==1)// capabilities
            {
                cellCapSelect.innerHTML= "<input type=\"hidden\" name=\"serviceCompPartCaps[]\" id=\"serviceCompPartCaps_"+ purSubServPartId +"\"  value=\""+document.getElementById('subServicePartCaps_' +purSubServPartId).value+"\" />"+  varCellContent;
            }else if(j==2) // threshold
            {
                cellThresh.innerHTML="<input type=\"hidden\" name=\"serviceCompPartThresh[]\" id=\"serviceCompPartThresh_"+ purSubServPartId +"\"  value=\""+document.getElementById('subServicePartThresh_' +purSubServPartId).value+"\" />"+  varCellContent;
            }else if(j==3) // Freq
            {
                cellFreq.innerHTML="<input type=\"hidden\" name=\"serviceCompPartFreq[]\" id=\"serviceCompPartFreq_"+ purSubServPartId +"\"  value=\""+document.getElementById('subServicePartFreq_' +purSubServPartId).value+"\" />"+  varCellContent;

            }else if(j==4) // action
            {
                cellAction.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeserviceCompPart(\"uniqserviceCompPartId_"+purSubServPartId+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
            }
        }
    }
    removeAllSubServicesParts();
    resetAllSubServiceInsFields('all');

    if(document.getElementById('definedSubServicesDiv').style.display == 'none')
    {
        document.getElementById('definedSubServicesDiv').style.display = 'block';
    }
}


// adding subservices
function addNewSubServicePart()
{
    var sIdx = 0;
    // 2. add a row to the subservices table

    var subServicesTbl=document.getElementById("subServicesTbl");
    var rowCount = subServicesTbl.rows.length;

    var nextIdx = parseInt(document.getElementById("autoIncForSubServiceParts").value)  + 1;

    // -------| functions and node selections
    var nodeSelectFunctDisp = "";
    var nodeSelectFunctVal = "";
    var nodeSelectDisp = "";
    var nodeSelectVal = "";
    if(document.getElementById('subServiceNodeSelectionIns') && document.getElementById('activeMapSelectionHD') ) {
        var subServiceNodeSelectionInsArr = document.getElementById('subServiceNodeSelectionIns').options;
        sIdx = document.getElementById('subServiceNodeSelectionIns').selectedIndex;
        if(subServiceNodeSelectionInsArr[sIdx].value != '#' && subServiceNodeSelectionInsArr[sIdx].value !='' && document.getElementById('activeMapSelectionHD').value!= '')
        {
            nodeSelectFunctDisp = subServiceNodeSelectionInsArr[sIdx].innerHTML;
            nodeSelectFunctVal = subServiceNodeSelectionInsArr[sIdx].value;
            nodeSelectVal = document.getElementById('activeMapSelectionHD').value;
            uniqSelHDId = 'subServicePartNodes_' + nextIdx;
            nodeSelectDisp = "<a href=\"javascript:void(0);\" onclick=\'javascript:showSubServicePartNodeSelectionDetails(\""+uniqSelHDId+"\");\'>(view)</a>";
        }
        else  {
            //todo: informative alert
            alert('No node selection was made on the map or the dropdown list');
            return;
        }
    }
    else  {
        //todo: informative alert
        return;
    }

// -------| Capability
    var capDisp = "";
    var capVal = "";
    if(document.getElementById('subServiceCapSelectionIns') && document.getElementById('subServiceCapSelectionIns') ) {
        var subServiceCapSelectionInsArr = document.getElementById('subServiceCapSelectionIns').options;
        sIdx = document.getElementById('subServiceCapSelectionIns').selectedIndex;
        if(subServiceCapSelectionInsArr[sIdx].value != '#' && subServiceCapSelectionInsArr[sIdx].value !='')
        {
            capDisp = subServiceCapSelectionInsArr[sIdx].innerHTML;
            capVal = subServiceCapSelectionInsArr[sIdx].value;
        }
        else
        {
            //todo: informative alert
            alert('No capability was selected!');
            return;
        }
    }

// -------| Threshold
    var threshDisp = "";
    var threshVal = "";
    if(document.getElementById('subServiceThreshIns') && document.getElementById('subServiceThreshIns').value!='' ) {
        threshDisp = document.getElementById('subServiceThreshIns').value;
        try{
            //alert(   document.getElementById('subServiceThreshIns').value);
            threshVal = parseFloat(document.getElementById('subServiceThreshIns').value);
        }
        catch(e)
        {
            //alert(threshDisp);
            threshDisp ="";
            threshVal='invalid';
        }
        if( isNaN(threshVal))
        {
            threshDisp ="";
            threshVal= null;
        }
}
// -------| Frequency
    var freqDisp = "";
    var freqVal = "";
    if(document.getElementById('subServiceFreqIns') && document.getElementById('subServiceFreqIns').value!='' ) {
        freqDisp = document.getElementById('subServiceFreqIns').value;
        try{
            freqVal = parseFloat(document.getElementById('subServiceFreqIns').value);
        }
        catch(e)
        {
            //alert(freqDisp);
            freqDisp ="";
            freqVal='invalid';
        }
        if( isNaN(freqVal))
        {
            freqDisp ="";
            freqVal= null;
        }
    }

    //update the autoinc.
    document.getElementById("autoIncForSubServiceParts").value = nextIdx;
    var row=subServicesTbl.insertRow(-1); // add at the end
    //var row=subServicesTbl.insertRow(2);
    //subServiceNodeSelectionIns, activeMapSelectionHD, subServiceCapSelectionIns, subServiceThreshIns, subServiceFreqIns
    var cellNodeSelect=row.insertCell(0);
    var cellCapSelect=row.insertCell(1);
    var cellThresh=row.insertCell(2);
    var cellFreq=row.insertCell(3);
	cellFreq.style.display="none";
    var cellAction=row.insertCell(4);

    //does not work well
    if(rowCount % 2 == 1)
    {
        row.style.background="#a4e6a3";
    }
    // we add the node selection as hidden field outside the cells to be kept when the fields are deleted, for the composite serrvices bellow
    cellNodeSelect.innerHTML="<input type=\"hidden\" id=\"uniqSubServicePartId_"+ nextIdx +"\"  value=\"\" /><input type=\"hidden\" name=\"subServicePartFuncts[]\" id=\"subServicePartFuncts_"+ nextIdx +"\" value=\""+nodeSelectFunctVal+"\" /><div>"+ nodeSelectFunctDisp + "&nbsp;"+nodeSelectDisp+ "</div>";
    cellCapSelect.innerHTML= "<input type=\"hidden\" name=\"subServicePartCaps[]\" id=\"subServicePartCaps_"+ nextIdx +"\"  value=\""+capVal+"\" /><div>"+  capDisp+ "</div>";  //"New:: " + nextIdx;
    cellThresh.innerHTML="<input type=\"hidden\" name=\"subServicePartThresh[]\" id=\"subServicePartThresh_"+ nextIdx +"\"  value=\""+threshVal+"\" /><div>"+  threshDisp+ "</div>";
    cellFreq.innerHTML="<input type=\"hidden\" name=\"subServicePartFreq[]\" id=\"subServicePartFreq_"+ nextIdx +"\"  value=\""+freqVal+"\" /><div>"+  freqDisp+ "</div>";
    //TODO there should be an alert/confirmation for deleting
    cellAction.innerHTML="<a href=\"javascript:void(0);\" onclick=\'removeSubServicePart(\"uniqSubServicePartId_"+nextIdx+"\");\'><img src=\"<%=request.getContextPath()%>/img/demo/demoDeleteRow32.png\" title=\"Remove\" style=\"height: 32px; width: 32px;\" /></a>";
    if(document.getElementById('subServicesDiv').style.display == 'none')
    {
        document.getElementById('subServicesDiv').style.display = 'block';
    }
    document.getElementById('spanSetsOfSelectionsHD').innerHTML+= "<input type=\"hidden\" name=\"subServicePartNodes[]\" id=\"subServicePartNodes_"+ nextIdx +"\" value=\""+nodeSelectVal+"\" />";
    resetAllSubServiceInsFields('all');
}

function showSubServicePartNodeSelectionDetails(hdSelectionId)
{
     alert(document.getElementById(hdSelectionId).value.split(/[,]+/).join('\n'));
//	 $('#alert_placeholder').html('<div class="alert span3"><a class="close" data-dismiss="alert">�</a><span>'+document.getElementById(hdSelectionId).value.split(/[,]+/).join('<br />')+'</span></div>')
        
}

function resetViewForNewService()
{
    removeAllSubServicesParts();
    resetAllSubServiceInsFields('all');
    removeAllSubServicesFromComposite();
}

//
// mode: if 'all' resets everything. Else there are cases or partial reset
//
function resetAllSubServiceInsFields(mode)
{
    if(mode=='all')
    {
    ///dropdowns //set selected index to 0
        document.getElementById('subServiceNodeSelectionIns').selectedIndex = 0;
        document.getElementById('activeMapSelectionHD').value= "";
        document.getElementById('subServiceCapSelectionIns').selectedIndex = 0;
        document.getElementById('subServiceThreshIns').value= "";
        document.getElementById('subServiceFreqIns').value= "";
    }
}

//
// logic for removing partials subservices.
//
function removeAllSubServicesParts()
{
    var subServicesTbl=document.getElementById("subServicesTbl");
    if(subServicesTbl!=null)
    {
        var rowIndex = 0;
        try {
            while(subServicesTbl.rows.length>3)
            {
                rowIndex = subServicesTbl.rows.length - 1;
                subServicesTbl.deleteRow(rowIndex);
            }
        }
        catch(e)
        {
            alert(e);
        }
    }
}

//
// logic for removing all subservices from composite service (different from the table of partial subservices)
// TODO: instead of removing row by row, we could remove subservices (we would need another function to handle that, also handle removing single subservices only without resetting the whole composite service)
function removeAllSubServicesFromComposite()
{
    var definedSubServicesTbl=document.getElementById("definedSubServicesTbl");
    if(definedSubServicesTbl!=null)
    {
        var rowIndex = 0;

        try {
            while(definedSubServicesTbl.rows.length>2)
            {
                rowIndex = definedSubServicesTbl.rows.length - 1;
                definedSubServicesTbl.deleteRow(rowIndex);
            }
            if(definedSubServicesTbl.rows.length == 2) {
                document.getElementById('definedSubServicesDiv').style.display = 'none';
            }
        }
        catch(e)
        {
            alert(e);
        }
    }
}


//
// Removes a row from the table. (partial subservices)
//
function removeSubServicePart(subServicePartId)
{
    var subServicesTbl=document.getElementById("subServicesTbl");
    if(subServicesTbl!=null)
    {
        deleteRowByHiddenId('subServicesTbl', subServicePartId);
    }
}

//
// Removes a row from the table. (composite service)
//
function removeserviceCompPart(subServicePartId)
{
    var definedSubServicesTbl=document.getElementById("definedSubServicesTbl");
    if(definedSubServicesTbl!=null)
    {
        deleteRowByHiddenId('definedSubServicesTbl', subServicePartId);
    }
    var rowCount = definedSubServicesTbl.rows.length;
    if(rowCount<= 2)
    {
        document.getElementById('definedSubServicesDiv').style.display = 'none';
    }
}

//aux function
function deleteRowByHiddenId(tableID, rowHiddenId)  {
    try {
        var hiddenIdObj = document.getElementById(rowHiddenId);
        var rowIndex = hiddenIdObj.parentNode.parentNode.rowIndex;
        var table = document.getElementById(tableID);
        table.deleteRow(rowIndex);
    }
    catch(e)
    {
        alert(e);
    }
}


function deleteRow(tableID, rowId) {
    try {
        var table = document.getElementById(tableID);
        var rowCount = table.rows.length;

        for(var i=0; i< rowCount; i++) {
            var row = table.rows[i];
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


function validateNewServiceBeforeSubmit(){
    var definedSubServicesTbl=document.getElementById("definedSubServicesTbl");
    if(definedSubServicesTbl!=null)
    {
        var rowCount = definedSubServicesTbl.rows.length;
        if(rowCount <=2)
        {
            alert("Please define a composed service containing at least one partial service, before submitting the definition!");
            return false;
        }
    }
    var subServicesTbl=document.getElementById("subServicesTbl");
    if(subServicesTbl!=null)
    {
        var rowCount = subServicesTbl.rows.length;
        if(rowCount >3)
        {
            alert("Please add your partial services to the composed service, or clear the partial services table before submitting the service definition!");
            return false;
        }

    }
    var serviceNameTxbx=document.getElementById("compositeServiceName");
    if(serviceNameTxbx!=null && $.trim(serviceNameTxbx.value.toString())=='') {
        alert("Please provide a name for your VITRO service!");
        return false;
    }
    return true;
}