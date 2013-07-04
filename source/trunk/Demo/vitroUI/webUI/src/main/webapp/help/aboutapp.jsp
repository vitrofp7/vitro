<%@page session='false' contentType='text/html'  %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>About this application...</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
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
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>