<%@page session='false' contentType='text/html'
        %>
<%@ page import="org.apache.shiro.web.util.WebUtils" %>
<%@ page import="org.apache.shiro.web.env.WebEnvironment" %>
<%@ page import="org.apache.shiro.web.mgt.WebSecurityManager" %>
<%@ page import="org.apache.shiro.SecurityUtils" %>
<%@ page import="org.apache.shiro.subject.Subject" %>
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