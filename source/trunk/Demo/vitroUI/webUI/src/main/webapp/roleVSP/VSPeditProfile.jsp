<%@page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*,  vitro.vspEngine.service.query.*'
        %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Manage VITRO Provider profile... (VSP view)</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/queryParamTransactJS.jsp"></script>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
</head>
<body><%-- For the menu --%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
<!-- this comment gets all the way to the browser -->
<%-- this comment gets discarded when the JSP is translated into a Servlet --%>
<p></p>
<div>&nbsp;</div>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>