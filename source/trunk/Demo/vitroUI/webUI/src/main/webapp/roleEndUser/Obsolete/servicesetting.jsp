<%@ page session='false' contentType="text/html;charset=UTF-8" language="java"  %>
<%@page import='java.util.*, presentation.webgui.vitroappservlet.Common' %>
<html>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Service Settings</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-edit').addClass("active");
 	});     	
	</script>
</head>
<body><%-- For the menu --%>
    <%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
			<!-- begin the side menu -->
			<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
			<!-- begin the DDBody -->
			<%= Common.printDDBody(application.getRealPath("/"), request) %>
			<!-- begin the footer for the application -->
			</div>
		</div>
	</div>
    <!-- begin the footer for the application -->
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
</body>
</html>
