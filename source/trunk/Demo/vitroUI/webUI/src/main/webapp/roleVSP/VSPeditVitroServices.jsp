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

<!DOCTYPE html>
<%@ page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*,vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="vitro.vspEngine.service.persistence.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- #superceded -->
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>VITRO high level services management (VSP view)</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-list').addClass("active");
 	});     
	</script>
	
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
  <!--    <script type="text/javascript" src="<%=request.getContextPath()%>/js/actb.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/table.js"></script>-->
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.uitablefilter.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
<script Language=JavaScript>
function checkAll()
{

	var allRows = document.getElementsByName("selectRow");
	for (var i=0; i<allRows.length;i++)
	{
		if(allRows[i].type =="checkbox")
		{
			allRows[i].checked=true;
		}
	}
}
</script>




<script Language=JavaScript>
function uncheckAll()
{

	var allRows = document.getElementsByName("selectRow");
	for (var i=0; i<allRows.length;i++)
	{
		if(allRows[i].type =="checkbox")
		{
			allRows[i].checked=false;
		}
	}
}
</script>
<script language="JavaScript">
 function createSelectedList()
 {

 	var allRows = document.getElementsByName("selectRow");
 
 	var ListOfSelected=new Array();
 	var Listedvalues ="";
 	var j=1;

 	for (var i=0;i<allRows.length;i++)
 	{
 		if (allRows[i].checked==true)
 		{
 		Listedvalues = Listedvalues+"ListOfSelected="+allRows[i].value+"?";
 		ListOfSelected[j-1]=allRows[i].value;
		j++;
 		}
 	}
 	Listedvalues = Listedvalues+"ListOfSelected="+allRows[allRows.length-1].value;
 	return ListOfSelected;
 	//return Listedvalues;
 }
 </script>
<script type="text/javascript">	

$(function(){
	
	$("#pippo").click(function(){var SelList = createSelectedList();
 		var url= new String("<%=request.getContextPath()%>/roleVSP/RemoveListOfSelectedAction?");
 		 
 		  	  for (var i=0;i<SelList.length-1;i++)
 		  	  {
 			url = url+"ListOfSelected="+ SelList[i] + "&";
 		  	  }
 		  	 url =  url+"ListOfSelected="+ SelList[SelList.length-1];
 		  	
 		   window.location.replace(url);

 	
 	});
});
</script>

<script>
$(function() { 
	  var theTable = $('table.servicelist')

	  theTable.find("tbody > tr").find("td:eq(1)").mousedown(function(){
	    $(this).prev().find(":checkbox").click()
	  });

	  $("#filter").keyup(function() {
	    $.uiTableFilter( theTable, this.value );
	  })

	  $('#filter-form').submit(function(){
	    theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
	    return false;
	  }).focus(); //Give focus to input field
	}); 
</script>

</head>
<body><%-- For the menu --%>

 




<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
			<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
			<div class="well">
	<table class="table" border="0">
	<tbody>
		<tr>
		<td>
        <table id="table2" class="table servicelist reborder">
		<thead>
			<tr style="background-color: #BFDEE3!important;">
			<td colspan="2">
					<h4>VITRO Services:</h4>
			</td>
			<td colspan="2">
					<div class="input-append" style="text-align: right;">
						<form id="filter-form">
						<span class="add-on">Filter:</span>
						<input name="filter" id="filter" value="" maxlength="30" type="text">
						</form>
					</div>
			</td>
			</tr>
			
            <tr bgcolor="#f3c683">
				<td align="left" colspan = "4">
				<div class="row-fluid">
					<div class="btn-group">
						<input class="btn" type="button" name="selectall" value="Select All" onclick="checkAll()" />
						<input class="btn" type="button" name="unselectall" value="Unselect All" onclick="uncheckAll()" />
						<input class="btn" ype="button" name="unselectall" value="Remove Selected" name="pippo" id="pippo" />
					</div>
				</div>
				</td>
			</tr>
            <tr bgcolor="#f3f783">
				<td style="vertical-align: top; width: 58px; text-align: center; font-weight: bold;">Selection<br>
                </td> 
                <td style="vertical-align: top; width: 271px; text-align: center; font-weight: bold;">Service<br>
                </td>
                <td style="vertical-align: top; width: 163px; text-align: center; font-weight: bold;">Tags<br>
                </td>
<!--            <td style="vertical-align: top; width: 175px; text-align: center; font-weight: bold;">Popularity<br>
                </td>-->
                <td style="vertical-align: top; width: 170px; text-align: center; font-weight: bold;">Edit<br>
                </td> 
                <td style="vertical-align: top; width: 160px; text-align: center; font-weight: bold;">Remove<br>
                </td>
            </tr>
</thead>
<tbody>            
            <c:forEach items="${serviceInstanceList}"  var="uiServiceInstance">
            	<tr>
 	                <td style="vertical-align: top; width: 58px; text-align: center;"><input name="selectRow" type="checkbox" value="${uiServiceInstance.serviceInstance.id}"><br>
	                </td>
	                <td style="vertical-align: top; width: 271px;">${uiServiceInstance.serviceInstance.name}<br></td>
 		            <td style="vertical-align: top; width: 163px;">${uiServiceInstance.searchTagsString}</td> 
<!-- 		                <td style="vertical-align: top; width: 175px; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td> -->
<!-- 		                <td style="vertical-align: top; width: 170px; text-align: right;">90%<br></td> -->
	                
	                <td style="vertical-align: top; width: 80px; text-align: center;">
	                	<form action="<%=request.getContextPath()%>/roleVSP/EditServiceInstanceAction">
	                		<input type="hidden" name="instanceId" value="${uiServiceInstance.serviceInstance.id}" />
	                		<input type="submit" value="Edit"/>
	                	</form>
	                </td>
	                <td style="vertical-align: top; width: 80px; text-align: center;">
	                	<form action="<%=request.getContextPath()%>/roleVSP/RemoveServiceInstanceAction">
	                		<input type="hidden" name="instanceId" value="${uiServiceInstance.serviceInstance.id}" />
	                		<input type="submit" value="Remove"/>
	                	</form>
	                </td>
	            </tr>
           	</c:forEach>
            
           
</tbody>
        </table>

       
     <!--   <a href="#">Previous page</a>&nbsp; <a href="#">Next page</a><br>
         -->
        
    <!--  </form>-->
</td></tr></tbody></table>
 <!-- 
<pre><code>
 <script language="javascript" type="text/javascript">
//<![CDATA[	
	var table2_Props = 	{					
					col_0: "none",
					col_1: "none",
					col_2: "select",
					col_3: "none",
					col_4: "none",
					col_5: "none",
					col_6: "none",
					display_all_text: " [ Show all ] ",
					sort_select: true
				};
	setFilterGrid( "table2",table2_Props );
	var table1_Props = 	{					
			grid: false,
		};
setFilterGrid( "table1",table1_Props,-1 );
//]]>
</script>
</code></pre>-->
</div>
</div>
</div>
</div>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>

