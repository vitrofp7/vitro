<%@page session='false' contentType='text/html' import='java.util.*, vitro.vspEngine.service.engine.UserNode, presentation.webgui.vitroappservlet.Model3dservice.*'
%>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<head>
<title>Browse and edit styles...</title>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/editStylesTasksJS.jsp"></script>
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
<table border = "1">
    <tr bgcolor="#F3F783"><td colspan="2"><strong>List of custom styles</strong></td></tr>
    <tr bgcolor="#BFDEE3"><td>Capability</td><td>Style #</td></tr>
    <%
    // capabilities LIST
    // get userPeer object
    Set<String> capsSet = (new HashMap<String, String>()).keySet();
    //HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
    try
    {
        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        capsSet = ssUN.getCapabilitiesTable().keySet();

    }
    catch (Exception e)
    {
         e.printStackTrace();
    }
    Iterator<String> capsIt = capsSet.iterator();
    int i=0;
    String currentCap;
    while(capsIt.hasNext()) {
        currentCap = capsIt.next();
    %>
    <tr>
        <td><%=currentCap %></td>
        <td>
            <table width="100%">
                <%
                Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
                Vector<Model3dStylesEntry> stylesForCurrCap = myStylesIndex.getEntriesForCap(currentCap);
                if(stylesForCurrCap != null && !stylesForCurrCap.isEmpty()) {
                    for(int j1=0; j1<stylesForCurrCap.size(); j1++)
                    {
                     %>   
                    <tr>
                        <td>Style id:<%=stylesForCurrCap.elementAt(j1).getStyleId()%></td>
                        <td><a href="<%=request.getContextPath()+"/roleEndUser/ViewStyle?suid="+stylesForCurrCap.elementAt(j1).getStyleId()%>" target="_blank"><img alt="Preview" src="<%=request.getContextPath()%>/img/magnytrue16.png" /></a></td>
                        <td><a href="#" onclick="alert('Edit not yet implemented');return false;"><img alt="Edit" src="<%=request.getContextPath()%>/img/edit1_greyedout.png" /></a></td>
                        <td><a href="#" onClick="deleteThisStyle('<%=stylesForCurrCap.elementAt(j1).getStyleId() %>' ); return false;"><img alt="Remove" src="<%=request.getContextPath()%>/img/delete16.png" /></a></td>
                    </tr>
                    <%
                    }
                }
                else
                { 
                %>
                <tr>
                    <td colspan="4">No Styles defined</td>
                </tr>
                <%
                }
                %>
            </table>
        </td>
    </tr>
    <%
    i++;
    } %>
</table>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>