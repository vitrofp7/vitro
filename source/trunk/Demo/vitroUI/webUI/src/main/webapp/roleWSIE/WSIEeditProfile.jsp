<%@page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'
        %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Manage my profile... (WSIE view)</title>
 	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-edit').addClass("active");
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
				<%-- Page content --%>
				
				</div>		
			</div>
		</div>		
	</div>	
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>