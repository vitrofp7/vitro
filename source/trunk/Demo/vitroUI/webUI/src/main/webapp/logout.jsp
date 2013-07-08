<%@page session='false' contentType='text/html'
        %>
<%@ page import="org.apache.shiro.web.util.WebUtils" %>
<%@ page import="org.apache.shiro.web.env.WebEnvironment" %>
<%@ page import="org.apache.shiro.web.mgt.WebSecurityManager" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.apache.shiro.subject.Subject" %>
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
    <title>Logout</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css"/>
    <link rel="stylesheet" type="text/css"
          href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css"/>
</head>
<body><%-- For the menu --%>
<table>
    <tr>
        <td>
            Please wait while we are logging you out...
        </td>
    </tr>
</table>
<!-- this comment gets all the way to the browser -->
<%-- this comment gets discarded when the JSP is translated into a Servlet --%>
    <%
        WebEnvironment webEnv = WebUtils.getRequiredWebEnvironment(application);
        WebSecurityManager webSecurityManager = webEnv.getWebSecurityManager();
        if(webSecurityManager!=null) {
            SecurityUtils.setSecurityManager(webSecurityManager);

            // A simple Shiro environment is set up
            // get the currently executing user:
            Subject currentUser = SecurityUtils.getSubject();
            currentUser.logout();
        }
    %>
<p></p>
</body>
</html>