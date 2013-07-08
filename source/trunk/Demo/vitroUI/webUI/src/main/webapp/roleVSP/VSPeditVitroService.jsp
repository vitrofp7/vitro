<%@page session='false' contentType='text/html' import="presentation.webgui.vitroappservlet.Common"  %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

<html>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Create a new VITRO high level service (VSP view)</title>
 	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
	
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-edit').addClass("active");
 	});     
$(function() {
   var maxHeight=0;
   $('#side_span').each(function(){
      if($(this).height()>maxHeight) {
       maxHeight=$(this).height();
      }
   });

    $('#map_span').height(maxHeight);
});
	</script>
    <script type="text/javascript">
    
    function loadCapabilities(){
    	$.ajax({
      	  url: "<%=request.getContextPath()%>/roleVSP/LoadCapabilitesFromGateways",
      	  cache:false,
      	  data: { gatewayList: $("#capabilitesContainer").selectedIndex }
      	}).done(function( msg ) {
      	$("#capabilitesContainer").html( msg );
      	
      	});	
    }
    
    
    
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

<table>
    <tbody>
    <tr>
        <td>
        <form action="<%=request.getContextPath()%>/roleVSP/CreateServiceInstanceAction" name="serviceCompositionFrm"><br>
        
        <input type="hidden" name="create" value="false">
        <input type="hidden" name="serviceInstanceId" value="${serviceInstanceId}">
        
        <table style="text-align: left; width: 692px; height: 60px;"
               border="1" cellpadding="2" cellspacing="2">
            <tbody>
            <tr  bgcolor="#F3F783"><td colspan="2"><span style="font-weight: bold;">Composite Service Template Definition (High level VITRO service)</span></td></tr>
            <tr>
                <td
                        style="vertical-align: top; width: 128px; height: 32px; text-align: right;">Service
                    Name:</td>
                <td style="vertical-align: top; width: 544px; height: 32px;">
                	<input name="serviceNameTxtBx" value="${serviceInstanceName}">
                </td>
            </tr>
            <tr>
                <td
                        style="vertical-align: top; width: 128px; height: 30px; text-align: right;">Search
                    tags: <br>
                </td>
                <td style="vertical-align: top; width: 544px; height: 30px;">
                	<input name="tagsCSVtxtbx" value="${serviceInstanceSearchTagList}" >&nbsp; (comma separated string values)
                </td>
            </tr>
            </tbody>
        </table>
        <br>
        
        
        
<br />

		<table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
               <tbody>
               		<tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Subscription</span></td>
		                
		                
		                <c:choose>
	                    	<c:when test="${subscription == 'true'}"> 
	                    		<td colspan="3">
				                	<input type="radio" name="subscriptionRadio" value="true" checked> Subscription<br>
									<input type="radio" name="subscriptionRadio" value="false" > One-Shot<br>
				                </td>
	                    	</c:when>
	    
		                    <c:otherwise>
		                    	<td colspan="3">
				                	<input type="radio" name="subscriptionRadio" value="true"> Subscription<br>
									<input type="radio" name="subscriptionRadio" value="false" checked> One-Shot<br>
				                </td>
		                    </c:otherwise>
	                    </c:choose>
		            </tr>
		            
		            <tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Sampling Rate</span></td>
		                <td colspan="3">
		                	<input type="text" name="samplingRate" value="${samplingRate}"/><br>
		                </td>
		            </tr>
               </tbody>
       </table>
                
        
<br />

		<table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
               <tbody>
               		<tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">SLA</span></td>
		                <td colspan="3">
		                	<textarea name="slaMessage" id="slaMessage" rows="4" cols="50">${slaMessage}</textarea>
		                </td>
		            </tr>
               </tbody>
       </table>
        
<br />



<table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
              	
               <tbody>
               		<tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Encryption</span></td>
		                
		                <c:choose>
	                    	<c:when test="${encription == 'true'}"> 
	                    		<td colspan="3"><input name="encryptionCxBx" id="allowDTNCxBx" checked="checked" value="true" type="checkbox"></td>
	                    	</c:when>
	    
		                    <c:otherwise>
		                    	<td colspan="3"><input name="encryptionCxBx" id="allowDTNCxBx" value="false" type="checkbox"></td>
		                    </c:otherwise>
	                    </c:choose>
		                
		                
		            </tr>
               
		            <tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Allow DTN</span></td>
		                
		                <c:choose>
	                    	<c:when test="${serviceAllowDTN == 'true'}"> 
	                    		<td colspan="3"><input name="allowDTNCxBx" id="allowDTNCxBx" checked="checked" value="true" type="checkbox"></td>
	                    	</c:when>
	    
		                    <c:otherwise>
		                    	<td colspan="3"><input name="allowDTNCxBx" id="allowDTNCxBx" value="false" type="checkbox"></td>
		                    </c:otherwise>
	                    </c:choose>
		                
		                
		            </tr>
		            
		            <tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">RFID Tracking</span></td>
		                
		                <c:choose>
	                    	<c:when test="${tracking == 'true'}"> 
	                    		<td colspan="3"><input name="trackingCxBx" id="trackingCxBx" checked="checked" value="true" type="checkbox"></td>
	                    	</c:when>
	    
		                    <c:otherwise>
		                    	<td colspan="3"><input name="trackingCxBx" id="trackingCxBx" value="false" type="checkbox"></td>
		                    </c:otherwise>
	                    </c:choose>
		                
		                
		            </tr>
		            
		            <tr bgcolor="#BFDEE3">
		                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Composition</span></td>
		                
		                <c:choose>
	                    	<c:when test="${composition == 'true'}"> 
	                    		<td colspan="3"><input name="compositionCxBx" id="compositionCxBx" checked="checked" value="true" type="checkbox"></td>
	                    	</c:when>
	    
		                    <c:otherwise>
		                    	<td colspan="3"><input name="compositionCxBx" id="compositionCxBx" value="false" type="checkbox"></td>
		                    </c:otherwise>
	                    </c:choose>
		                
		                
		            </tr>
               </tbody>
               
</table>



<br />
        <table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
            <tbody>
            <tr bgcolor="#BFDEE3">
                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Available Gateways</span></td>
            </tr>
            
            <tr>
                
                   <c:choose>
                   	<c:when test="${empty gatewayIdList}"> 
                   		<td style="vertical-align: top; width: 203px; " rowspan="2">
                   			No gateway connected 
                   		</td>
                   	</c:when>
   
                    <c:otherwise>
                    	<td style="vertical-align: top; width: 203px; " rowspan="2">
                   			<select id="gateways" size="15" name="gateways" multiple="multiple">
                    
		                    	<c:forEach items="${gatewayIdList}"  var="option">
		                    		<c:choose>
		                    			<c:when test="${option.selected}">
		                    				<option selected="selected" value="${option.value}" > ${option.label} </option>
		                    			</c:when>
		                    			<c:otherwise>
		                    				<option value="${option.value}" > ${option.label} </option>
		                    			</c:otherwise>
		                    		</c:choose>
		                    		
		                    	</c:forEach>
		                   
		                    </select>
                   		</td>
                    	<td style="vertical-align: top; width: 35px; height: 58px;">
                    		<input name="AddCapBtn" value="Load Capabilities" type="button" onclick="loadCapabilities();"/>
                    		
						</td>
                    </c:otherwise>
                   </c:choose>
            </tr>
            <tr>
                <td style="vertical-align: top; width: 35px;"><br>
                </td>
            </tr>
            </tbody>
        </table>

        <c:choose>
            <c:when test="${rulesANDforNotify == 'true'}">
               <input name="rulesANDforNotifyCxBx" id="rulesANDforNotifyCxBx" checked="checked" value="true" type="checkbox">
            </c:when>

            <c:otherwise>
               <input name="rulesANDforNotifyCxBx" id="rulesANDforNotifyCxBx" value="false" type="checkbox">
            </c:otherwise>
        </c:choose>
        <label for="rulesANDforNotifyCxBx">
            <span>All rules' conditions should be met before sending a notification</span>
        </label>
        <br/>

<table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
            <tbody>
            <tr bgcolor="#BFDEE3">
                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Involved Capabilities</span></td>
            </tr>
            <tr>
                <td style="vertical-align: top; width: 203px;">Available: </td>
                <td style="vertical-align: top; width: 35px;"><br>
                </td>
                <td style="vertical-align: top; width: 180px;">Selected: </td>
            </tr>
            
            <c:choose>
                    	<c:when test="${empty allCapabilities}">
                    	
                    	  <tr>
				                <td style="vertical-align: top; width: 203px;" colspan="3"> No gateway connected </td>
				             
				            </tr>
                    	 </c:when>
    
	                    <c:otherwise>
	                    
	                    	<c:forEach items="${supportedCapabilities}"  var="capabilty">
	                    	
	                    	
	                    	
	                    		<tr>
					                <td style="vertical-align: top; width: 203px;" >
					                    
					                   
					    
						                   
						                    	<select name="InvolvedCaps" >
							                    	<c:forEach items="${allCapabilities}"  var="option">
							                    		<c:choose>
							                    			<c:when test="${capabilty.name == option}">
							                    				<option selected="selected" value="${option}" > ${option} </option>
							                    			</c:when>
							                    			<c:otherwise>
							                    				<option value="${option}" > ${option} </option>
							                    			</c:otherwise>
							                    		</c:choose>
							                    	</c:forEach>    
							                    </select>
						                    
						                    	
						                    
					                    
					                   
					                </td>
					                
					                <td style="vertical-align: top; width: 203px;">
					                    
					                   
				                    	<select name="functions">
				                    		<c:forEach items="${allFunctions}"  var="func">
				                    			<c:choose>
					                    			<c:when test="${capabilty.function == func}">
					                    				<option selected="selected" value="${func}" > ${func} </option>
					                    			</c:when>
					                    			<c:otherwise>
					                    				<option value="${func}" > ${func} </option>
					                    			</c:otherwise>
				                    			</c:choose>
					                    	</c:forEach>    
				          
					                    </select>
					                </td>
					            </tr>
	                    	
	                    				
                    		</c:forEach>
                	 </c:otherwise>
            	</c:choose>
           
            <tr>
                <td style="vertical-align: top; width: 35px;"><br>
                </td>
            </tr>
            </tbody>
        </table>
        
        <br>

     
        
      
         
        <br>
        <input name="createInstance" value="Update Service Instance"
               type="submit">
         
        </form>
        </td>
    </tr>
    </tbody>
</table>
</div>
</div>
</div>

<!-- begin the footer for the application -->
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
</body>
</html>