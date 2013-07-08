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
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/vspCreateEditServiceFormActionsJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>

    <title>Create a new VITRO high level service (VSP view)</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">
	
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />

	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-edit').addClass("active");
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
<input type="hidden" name="create" value="true">
<table style="text-align: left; width: 692px; height: 60px;"
       border="1" cellpadding="2" cellspacing="2">
    <tbody>
    <tr bgcolor="#F3F783">
        <td colspan="2"><span style="font-weight: bold;">Composite Service Template Definition (High level VITRO service)</span>
        </td>
    </tr>
    <tr>
        <td style="vertical-align: top; width: 128px; height: 32px; text-align: right;">Service Name:</td>
        <td style="vertical-align: top; width: 544px; height: 32px;"><input name="serviceNameTxtBx"></td>
    </tr>
    <tr>
        <td style="vertical-align: top; width: 128px; height: 30px; text-align: right;">Search tags: <br></td>
        <td style="vertical-align: top; width: 544px; height: 30px;"><input name="tagsCSVtxtbx">&nbsp; (comma separated string values)</td>
    </tr>
    </tbody>
</table>
<br>
<br/>
<table style="text-align: left; width: 444px;" border="1"
       cellpadding="1" cellspacing="2">
    <tbody>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Subscription</span></td>
        <td colspan="3">
            <input type="radio" name="subscriptionRadio" value="true"> Subscription<br>
            <input type="radio" name="subscriptionRadio" value="false" checked> One-Shot<br>
        </td>
    </tr>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Sampling Rate (seconds)</span></td>
        <td colspan="3">
            <input type="text" name="samplingRate" value="40"/><br>

        </td>
    </tr>
    </tbody>
</table>

<table style="text-align: left; width: 444px;" border="1"
       cellpadding="2" cellspacing="2">
    <tbody>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">SLA</span></td>
        <td colspan="3">
            <textarea name="slaMessage" id="slaMessage" rows="4" cols="50">Lorem ipsum dolor sit amet, consectetur
                adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim
                veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute
                irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur
                sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est
                laborum.</textarea>

        </td>
    </tr>
    </tbody>

</table>


<br/>

<table style="text-align: left; width: 444px;" border="1"
       cellpadding="2" cellspacing="2">
    <tbody>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Encryption</span></td>
        <td colspan="3"><input name="encryptionCxBx" id="encryptionCxBx" value="true" type="checkbox"></td>
    </tr>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Allow DTN</span></td>
        <td colspan="3"><input name="allowDTNCxBx" id="allowDTNCxBx" value="true" type="checkbox"></td>
    </tr>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">RFID Tracking</span></td>
        <td colspan="3"><input name="trackingCxBx" id="trackingCxBx" value="true" type="checkbox"></td>
    </tr>
    <tr bgcolor="#BFDEE3">
        <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Composition</span></td>
        <td colspan="3"><input name="compositionCxBx" id="compositionCxBx" value="true" type="checkbox"></td>
    </tr>
    </tbody>

</table>

<br/>
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
                    <select id="gateways" name="gateways" multiple="multiple">
                        <c:forEach items="${gatewayIdList}" var="gateway">
                            <option> ${gateway.id} </option>
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

<div id="definedRulesDiv" style="display: none">
    <input type="hidden" id="autoIncForDefinedRules" value="0" />
    <table id="definedRulesTbl" style="text-align: left; width: 444px;" border="1" cellpadding="2" cellspacing="2">
        <tbody>
        <tr bgcolor="#BFDEE3">
            <td colspan="7"><span style="text-decoration: underline; font-weight: bold;">Defined Rules</span></td>
        </tr>
        <tr bgcolor="#BFDAEF">
            <td>Capability</td><td>Function</td><td style="white-space:nowrap;">Get/Set Value</td><td style="white-space:nowrap;">Use Trigger</td><td>Condition On Returned Value</td><td style="white-space:nowrap;">Trigger Action</td><td>Remove</td>
        </tr>
        </tbody>
    </table>
    <input name="rulesANDforNotifyCxBx" id="rulesANDforNotifyCxBx" value="false" type="checkbox">
    <label for="rulesANDforNotifyCxBx">
        <span>All rules' conditions should be met before sending a notification</span>
    </label>
 </div>
<table style="text-align: left; width: 444px;" border="1" cellpadding="2" cellspacing="2">
    <tbody>
    <tr bgcolor="#BFDEE3">
        <td colspan="7"><span style="text-decoration: underline; font-weight: bold;">New Rules</span></td>
    </tr>
    <tr bgcolor="#BFDEFF">
        <td>Capability</td><td>Function</td><td style="white-space:nowrap;">Get/Set Value</td><td style="white-space:nowrap;">Use Trigger</td><td>Condition On Returned Value</td><td style="white-space:nowrap;">Trigger Action</td><td>Add</td>
    </tr>
    <tr >
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> No gateway connected </c:when>
                <c:otherwise>
                    <select name="InvolvedCaps" id="InvolvedCaps"  onChange="onSelectedCapabilitySet()" >
                        <option value="">[Please select]</option>
                        <c:forEach items="${supportedCapabilities}" var="capabilty">
                            <option value="${capabilty}"> ${capabilty} </option>
                        </c:forEach>
                    </select>
                </c:otherwise>
            </c:choose>
        </td>
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> &nbsp; </c:when>
                <c:otherwise>
                    <select name="functionsSens" id="functionsSens" style="display: none;" >
                        <option value="LAST">Latest Value</option>
                        <option value="MIN">Min Value</option>
                        <option value="MAX">Max Value</option>
                        <option value="AVG">Avg Value</option>
                    </select>
                    <select name="functionsAct" id="functionsAct" style="display: none;">
                        <option value="LAST">Latest Value</option>
                        <option value="SET">Set Value</option>
                    </select>
                </c:otherwise>
            </c:choose>
        </td>
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> &nbsp; </c:when>
                <c:otherwise>
                    <select name="functionsThreshold" id="functionsThreshold" style="display: none;">
                        <option value="">&nbsp;</option>
                        <option value="eq">=</option>
                        <option value="gt">&gt;=</option>
                        <option value="lt">&lt;=</option>
                    </select>
                    <input type="text" id="thresBoundSensTxb" size="7" style="display: none;" />
                    <select name="thresBoundActSel" id="thresBoundActSel" style="display: none;">
                        <option value="ON">ON</option>
                        <option value="OFF">OFF</option>
                    </select>
                </c:otherwise>
            </c:choose>
        </td>
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> &nbsp; </c:when>
                <c:otherwise>
                    <select name="notifyFlag" id="notifyFlag" style="display: none;" onChange="onSetTriggerSet()" >
                        <option value="">&nbsp;</option>
                        <option value="YES">YES</option>
                        <option value="NO">NO</option>
                    </select>
                </c:otherwise>
            </c:choose>
        </td>
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> &nbsp; </c:when>
                <c:otherwise>
                    <select name="triggerCondition" id="triggerCondition" style="display: none;">
                        <option value="">&nbsp;</option>
                        <option value="gt">&gt;=</option>
                        <option value="lt">&lt;=</option>
                    </select>
                    <input type="text" id="triggerBoundSens" size="7" style="display: none;" />
                </c:otherwise>
            </c:choose>
        </td>
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> &nbsp; </c:when>
                <c:otherwise>
                    <select name="triggerAction" id="triggerAction" style="display: none;" onchange="onTriggerActionSet()" >
                        <option value="">[Please select]</option>
                        <option value="set">Set Actuator</option>
                        <option value="history">History</option>
                        <option value="terminate">Terminate Service</option>
                        <option value="notifyAndContinue">Notify and continue</option>
                        <option value="email">Send e-mail to: </option>
                        <option value="sms">Send SMS to:</option>
                    </select>
                    <c:choose>
                        <c:when test="${empty gatewayIdList}"> No gateway connected </c:when>
                        <c:otherwise>
                            <select name="InvolvedCapsTriggerActuate" id="InvolvedCapsTriggerActuate" style="display: none;">
                                <option value="">[Please select]</option>
                                <c:forEach items="${supportedActuateCapabilities}" var="capabilty">
                                    <option value="${capabilty}"> ${capabilty} </option>
                                </c:forEach>
                            </select>
                        </c:otherwise>
                    </c:choose>
                    <input type="text" id="triggerValue" size="7" style="display: none;" />
                    <select name="triggerBoundAct" id="triggerBoundAct" style="display: none;">
                        <option value="ON">ON</option>
                        <option value="OFF">OFF</option>
                    </select>
                    <select name="triggerNodesAct" id="triggerNodesAct" style="display: none;" >
                        <option value="All">All Nodes</option>
                        <option value="Selection">Selection</option>
                    </select>
                </c:otherwise>
            </c:choose>
        </td>
        <td style="white-space:nowrap;">
            <c:choose>
                <c:when test="${empty gatewayIdList}"> &nbsp; </c:when>
                <c:otherwise>
                    <a href="javascript:void(0);" onclick='addNewRule()'>Add</a>
                </c:otherwise>
            </c:choose>
        </td>
    </tr>
</table>
<br>
<input name="createInstance" value="Create Service Instance"
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