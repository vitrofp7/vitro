<%@page session='false' contentType='text/html'
        import="presentation.webgui.vitroappservlet.Common"
        %>
<html>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>
    <title>Advanced interface for setting up a VSN</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css"/>
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

<form action="#" id="formAdvanced" onSubmit="return false;">
    <table border="1" width="800">
        <tr bgcolor="#F3F783">
            <td colspan="2">
                <b>Step 1: Select areas and smart devices</b>
            </td>
        </tr>
        <tr bgcolor="#BFDEE3">
            <td>Select location</td>
            <td>Select smart devices</td>
        </tr>
        <tr>
            <td valign="top">
                <input disabled type="checkbox" name="allCoveredAreasCb" value="ON" checked="checked"/>All covered areas<br>
                <a href="<%=request.getContextPath()%>/roleEndUser/regionSelectionOrig.jsp" target="_blank">Add specific location(s)</a>
            </td>
            <td valign="top">
                <table border="0">
                    <tr>
                        <td><input disabled type="checkbox" name="allSmartDevicedInAreaCb" value="ON"
                                   checked="checked"/>All smart devices in this area
                        </td>
                    </tr>
                    <tr>
                        <td><a href="<%=request.getContextPath()%>/roleEndUser/regionSelectionOrig.jsp" target="_blank">Add specific smart device(s)</a></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr bgcolor="#F3F783">
            <td colspan="2">
                <b>Step 2: Select Capability, Function(s) per capability and a Style per capability</b>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" width="100%">
                    <tr bgcolor="#BFDEE3">
                        <td>Select Capability</td>
                        <td>Select Function</td>
                        <td>Select Style for this Capability</td>
                    </tr>
                    <tr>
                        <td valign="top">
                            <table border="0">
                                <tr>
                                    <td><a href="#" onclick="alert('Function not yet implemented');return false;">Add new capability</a></td>
                                </tr>
                            </table>
                        </td>
                        <td valign="top">
                            <table border="0">
                                <tr>
                                    <td><a href="#" onclick="alert('Function not yet implemented');return false;">Add new function(s)</a></td>
                                </tr>
                            </table>
                        </td>
                        <td valign="top">
                            <table border="0">
                                <tr>
                                    <td><select name="styleforcapability" style="font: 11px tahoma">
                                        <option value="#">No styles found</option>
                                    </select>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr bgcolor="#F3F783">
            <td colspan="2">
                <b>Step 3: Define Query parameters</b>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0">
                    <tr>
                        <td>
                            Issuing period (secs):
                        </td>
                        <td>
                            <input type="text" name="periodOfIssue" value="40" size="5"/>
                        </td>
                        <td>
                            Use &quot;0&quot; for one-time issue.
                        </td>
                    </tr>
                    <tr>
                        <td>
                            History length:
                        </td>
                        <td>
                            <input type="text" name="NumofHistoryResultsForQuery" value="10" size="4"/>
                        </td>
                        <td>
                            Use &quot;0&quot; for no history.
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <table border="0">
                                <td>
                                    <input type="checkbox" id="aggregateCb" name="aggregateCb" value="ON" checked="checked"/>
                                </td>
                                <td>
                                    <label for="aggregateCb"> Send aggregated messages</label>
                                </td>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2">
                            <INPUT NAME="SUBMIT2" TYPE="BUTTON" VALUE="Submit Request for VSN to VITRO"
                                   style="cursor:pointer; background-color:#8D8DF6; color:#FCFDBD;  font: 11px tahoma">
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</form>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>
</html>