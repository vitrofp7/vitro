<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@page session='false' contentType='text/html'
        %>
<html>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Application Server Error (404)</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
</head>
<body>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
<p style="text-align: center;">Server returned an error code (404)</p>
<%= Common.printFooter(request, application) %>
</body>
</html>