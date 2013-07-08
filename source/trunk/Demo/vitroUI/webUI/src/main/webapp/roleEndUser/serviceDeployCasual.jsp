<%@page session='false' contentType='text/html'
        import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'
        %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
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
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>
    <title>Select, configure and deploy a VITRO service (Casual user mode)</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/compositeServiceCasualTransactJS.jsp"></script>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-new').addClass("active");
 	});    
	</script>

</head>
<body><%-- For the menu --%>
    <%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
    <%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
    <!-- this comment gets all the way to the browser -->
    <%-- this comment gets discarded when the JSP is translated into a Servlet --%>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
			<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
			<div class="well">
<table width="100%" border="0"><tbody><tr><td>
<form action="#" name="findServices">
    <table style="text-align: left; width: 100%;" border="1" cellpadding="2" cellspacing="2">
        <tbody>
        <tr>
            <td style="vertical-align: top; width: 182px; text-align: right;">Keywords
                Search:<br>
            </td>
            <td style="vertical-align: top; width: 1417px;"><input maxlength="255" tabindex="1" name="searchkeysTxtBx"> <input value="Search..." name="searchfiltersbtn" type="button">
            </td>
        </tr>
        <tr>
            <td style="vertical-align: top; width: 182px; text-align: right;">Sort by:<br>
            </td>
            <td style="vertical-align: top; width: 1417px;"><input checked="checked" name="sortbyrdgrp" value="sortbestmatch" id="sortbestmatch"  type="radio"><label for="sortbestmatch">Best
                match&nbsp;&nbsp;</label>  <input name="sortbyrdgrp" value="sortpopularity" id="sortpopularity" type="radio"><label for="sortpopularity">Popularity&nbsp;&nbsp;</label> <br>
            </td>
        </tr>
        </tbody>
    </table>
    <br>
    <table style="text-align: left; width: 100%;" border="1" cellpadding="2" cellspacing="2">
        <tbody>
        <tr bgcolor="#F3F783"><td  colspan="7" style="vertical-align: top; text-align: left; font-weight: bold;">
            VITRO Services:
        </td></tr>
        <tr bgcolor="#BFDEE3">
           <%-- <td style="vertical-align: top; width: 58px; text-align: center; font-weight: bold;">Selection<br>
            </td>--%>
            <td style="vertical-align: top; width: 471px; text-align: center; font-weight: bold;">Service<br>
            </td>
            <td style="vertical-align: top; width: 163px; text-align: center; font-weight: bold;">Details<br>
            </td>
            <td style="vertical-align: top; width: 175px; text-align: center; font-weight: bold;">Popularity<br>
            </td>
            <td style="vertical-align: top; width: 170px; text-align: center; font-weight: bold;">User Rating<br>
            </td>
            <td style="vertical-align: top; width: 730px; text-align: center; font-weight: bold;">Review/Edit
                Configuration<br>
            </td>
            <td style="vertical-align: top; width: 60px; text-align: center; font-weight: bold;">Action
                <br>
            </td>
        </tr>
        <tr>
            <%--<td style="vertical-align: top; width: 58px; text-align: center;"><input name="selectRow" type="checkbox"><br>
            </td>--%>
            <td style="vertical-align: top; width: 471px;">Monitor
                Temperature in Patras and Rome<br>
            </td>
            <td style="vertical-align: top; width: 163px;"><a href="#">More...</a><br>
            </td>
            <td style="vertical-align: top; width: 175px; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
            <td style="vertical-align: top; width: 170px; text-align: right;">90%<br>
            </td>
            <td style="vertical-align: top; width: 60px; text-align: center;"><a href="#">Default...</a><br>
            </td>
            <td>
                <a href="#" onClick="deployCompositeService('Monitor Temperature in Patras and Rome', 'pre0');return false;"><span id="DeployCommandTextDiv_0">Deploy</span></a>
            </td>
        </tr>
        <tr>
            <%--<td style="vertical-align: top; width: 58px; text-align: center;"><input name="selectRow" type="checkbox"><br>
            </td>--%>
            <td style="vertical-align: top; width: 471px;">Monitor Temperature in Colombes France<br>
            </td>
            <td style="vertical-align: top; width: 163px;"><a href="#">More...</a><br>
            </td>
            <td style="vertical-align: top; width: 175px; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
            <td style="vertical-align: top; width: 170px; text-align: right;">90%<br>
            </td>
            <td style="vertical-align: top; width: 60px; text-align: center;"><a href="#">Default...</a><br>
            </td>
            <td>
                <a href="#" onClick="deployCompositeService('Monitor Temperature in Colombes France', 'pre3');return false;"><span id="DeployCommandTextDiv_3">Deploy</span></a>
            </td>
        </tr>
        <tr>
            <%--<td style="vertical-align: top; text-align: center;"><input name="selectRow" type="checkbox"></td>
            --%><td style="vertical-align: top;">Fire Early Warning in CTI offices<br>
            </td>
            <td style="vertical-align: top;"><a href="#">More...</a></td>
            <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
            <td style="vertical-align: top; text-align: right;">95%<br>
            </td>
            <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
            </td>
            <td>
                <a href="#" onClick="deployCompositeService('Fire Early Warning in CTI offices', 'pre1');return false;"><span id="DeployCommandTextDiv_1">Deploy</span></a>
            </td>
        </tr>
        <tr>
            <%--<td style="vertical-align: top; text-align: center;"><input name="selectRow" type="checkbox" /></td>
            --%><td style="vertical-align: top;">Fire Early Warning in Thales building<br>
            </td>
            <td style="vertical-align: top;"><a href="#">More...</a></td>
            <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
            <td style="vertical-align: top; text-align: right;">95%<br>
            </td>
            <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
            </td>
            <td>
                <a href="#" onClick="deployCompositeService('Fire Early Warning in Thales building','pre4');return false;"><span id="DeployCommandTextDiv_4">Deploy</span></a>
            </td>
        </tr>
        <tr style="display: none;">
           <%-- <td style="vertical-align: top; text-align: center;"><input name="selectRow" type="checkbox"></td>
           --%> <td style="vertical-align: top;">Monitor temperature from exactly 3 sensors<br>
            </td>
            <td style="vertical-align: top;"><a href="#">More...</a></td>
            <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
            <td style="vertical-align: top; text-align: right;">85%<br>
            </td>
            <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
            </td>
            <td>
                <a href="#" onClick="deployCompositeService('Monitor temperature from exactly 3 sensors', 'pre2');return false;"><span id="DeployCommandTextDiv_2">Deploy</span></a>
            </td>
        </tr>
        <c:forEach items="${serviceInstanceList}"  var="uiServiceInstance">
            <tr>
                    <%--<td style="vertical-align: top; width: 58px; text-align: center;"><input name="selectRow" type="checkbox" value="${uiServiceInstance.serviceInstance.id}"><br>
                    </td>--%>
                <td style="vertical-align: top; ">${uiServiceInstance.serviceInstance.name}<br></td>
                <td style="vertical-align: top; "><a href="#">More...</a></td>
                <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                <td style="vertical-align: top; text-align: right;">90%<br>
                </td>
                <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
                </td>
                <td>
                    <a href="#" onClick="deployCompositeService('${uiServiceInstance.serviceInstance.name}','${uiServiceInstance.serviceInstance.id}');return false;"><span id="DeployCommandTextDivNew_${uiServiceInstance.serviceInstance.id}">Deploy</span></a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <!-- <a href="#">Previous page</a>&nbsp; <a href="#">Next page</a> -->
    <br/>
    <!-- <input value="Negotiate Selected" name="StartNegotiate" type="button">&nbsp; <input value="Clear Selection" name="clearselectionbtn" type="button">&nbsp; <input value="Show VITRO sample services" name="loadsampleservicesbtn" type="button" style="display: none;"> -->
</form>
</td></tr></tbody></table>
</div>
</div>
</div>
    <!-- begin the footer for the application -->
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
</body>
</html>