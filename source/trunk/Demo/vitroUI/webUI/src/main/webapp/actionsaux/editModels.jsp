<%@page session='false' contentType='text/html' import='java.util.*, vitro.vspEngine.service.engine.*, presentation.webgui.vitroappservlet.*, presentation.webgui.vitroappservlet.Model3dservice.*'
%>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="vitro.vspEngine.logic.model.GatewayWithSmartNodes" %>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Browse and edit available models...</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/editModelMappingTasksJS.jsp"></script>
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
    <tr bgcolor="#F3F783"><td colspan="2"><strong>List of submitted mappings</strong></td></tr>
    <tr bgcolor="#BFDEE3"><td>Gateway</td><td>Model(s) and Interface(s)</td></tr>
<%
    Model3dIndex myModelsIndex = Model3dIndex.getModel3dIndex();
    // get userPeer object
    HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
    try
    {
            UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
            infoGWHM = ssUN.getGatewaysToSmartDevsHM();

    }
    catch (Exception e)
    {
         e.printStackTrace();
    }
                            
    // List the avail gateways and also calculate the Total (overall) number of smartDevices. Each iteration of the loop adds the number of smartDevices of one gateway
    Set<String> keysOfGIds;
    try{
            if(infoGWHM != null)
            {
                keysOfGIds = infoGWHM.keySet();
            }
            else
            {
                keysOfGIds = (new HashMap<String, GatewayWithSmartNodes>()).keySet();
            }
        }
        catch (Exception e)
        {
            keysOfGIds = (new HashMap<String, GatewayWithSmartNodes>()).keySet();;
        }
    Iterator<String> itgwId = keysOfGIds.iterator();
    while(itgwId.hasNext()) {
        String currGwId = itgwId.next();
        String gateId = infoGWHM.get(currGwId).getId();
        String gateName = infoGWHM.get(currGwId).getName();
    %>
    <tr>
        <td>
            <%=gateName%>
        </td>
        <td>
            <table border="1">
                                <%
                                Vector<Model3dIndexEntry> currIndexEntriesVec = myModelsIndex.getIndexEntriesByGatewayId(currGwId);
                                if(currIndexEntriesVec == null || currIndexEntriesVec.isEmpty())
                                {
                                     %>
                                    <tr><td colspan="2">No Models defined</td></tr>
                                    <% 
                                }
                                else
                                {
                                    for(int j = 0 ; j < currIndexEntriesVec.size(); j++)
                                    {
                                        String tmpModelFilename = currIndexEntriesVec.elementAt(j).getModelFileName();
                                        String tmpMetaFilenameFullPath =  Model3dIndex.getIndexPath() + currIndexEntriesVec.elementAt(j).getMetaFileName();
                                        long tmpDefaultInterfaceIdforCurrGw = currIndexEntriesVec.elementAt(j).getDefaultInterfaceIdForGwId(currGwId);
                                        //                    outPrintWriter.print(tmpModelFilename +" on ");
                                        //                    outPrintWriter.print(Long.toString(tmpDefaultInterfaceIdforCurrGw) +",");
                                        %>
                                        <tr>
                                        <td><%=tmpModelFilename%></td>
                                        <td>
                                            <table>
                                        <%                                        
                                        Model3dMetafile tmpMetaFile = Model3dMetafile.parseMetafileFromFile(tmpMetaFilenameFullPath);
                                        if(tmpMetaFile!=null && !tmpMetaFile.getMetaInterfacesVecForGwId(currGwId).isEmpty())
                                        {           
                                            for(int k=0; k< tmpMetaFile.getMetaInterfacesVecForGwId(currGwId).size(); k++)
                                            {
                                                long tmpCurrInterfaceforGw = tmpMetaFile.getMetaInterfacesVecForGwId(currGwId).elementAt(k).getIntefaceId();
                                                boolean isDefault = false;
                                                if(tmpCurrInterfaceforGw == tmpDefaultInterfaceIdforCurrGw) isDefault = true;
                                                    
                                        %>
                                            <tr>
                                                <td>
                                        <%      if(isDefault) { %><strong><% } %>
                                                    Interface <%=Long.toString(tmpCurrInterfaceforGw)%>
                                        <%      if(isDefault) { %>(default) </strong><% } %>
                                                </td>
                                        <%      if(isDefault) { %>        
                                                <td><a href="#" onclick="return false;"><img alt="The default interface" src="<%=request.getContextPath()%>/img/fullStar.png" /></a></td>
                                        <%      } 
                                                else { %>
                                                <td><a href="#" onclick="makeDefaultThisInterface('<%=tmpModelFilename %>', '<%=currGwId %>', '<%=Long.toString(tmpCurrInterfaceforGw)%>' );return false;"><img alt="Make default" src="<%=request.getContextPath()%>/img/emptyStar.png" /></a></td>
                                        <%      } %>
                                                <td><a href="<%=request.getContextPath()+"/roleVSP/PreviewModelAndInterfaces?modelFilenamesBox[]="+tmpModelFilename+"&gatewayIdsBox[]="+currGwId+"&interfaceIdsBox[]="+Long.toString(tmpCurrInterfaceforGw)%>" target="_blank"><img alt="Preview" src="<%=request.getContextPath()%>/img/magnytrue16.png" /></a></td>
                                                <td><a href="#" onclick="alert('Edit not yet implemented');return false;"><img alt="Edit" src="<%=request.getContextPath()%>/img/edit1_greyedout.png" /></a></td>
                                                <td><a href="#" onClick="deleteThisInterface('<%=tmpModelFilename %>', '<%=currGwId %>', '<%=Long.toString(tmpCurrInterfaceforGw)%>' ); return false;"><img alt="Remove" src="<%=request.getContextPath()%>/img/delete16.png" /></a></td>
                                            </tr>                                               
                                        <%
                                            }
                                        }
                                        else
                                        {
                                        %>        
                                                <tr><td colspan="4">No interfaces defined (error state)</td></tr>
                                        <%        
                                        }
                                        %>
                                            </table>
                                        </td>
                                        </tr>
                                        <%
                                    }
                                }
                                %>                                
            </table>
        </td>
    </tr>
    <%
    } %>
</table>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>