<%@page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'
        %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Manage my profile... (Simple end user view)</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>

</head>
<body>   
    <!-- DDMenu -->
    <%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
			<!-- begin the DDBody -->
			<%= Common.printDDBody(application.getRealPath("/"), request) %>
			<!-- begin the footer for the application -->
		</div>
	</div>
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
</body>