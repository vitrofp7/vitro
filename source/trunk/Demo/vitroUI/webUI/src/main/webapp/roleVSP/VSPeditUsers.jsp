<%@page session='false' contentType='text/html' import="java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*,  vitro.vspEngine.service.query.*"%>

 <%@ page import="org.apache.log4j.Logger" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="vitro.vspEngine.service.persistence.*" %>
<%@ page import='java.sql.*,
            org.apache.commons.configuration.Configuration,
            org.apache.commons.configuration.PropertiesConfiguration,
            org.apache.shiro.authc.credential.*' %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Manage Registered End users (VSP view)</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/vspEditUsersTasksJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
    
    

<script>
jQuery(function(){
			$("#submit").click(function(){
			$(".error").hide();
			var hasError = false;
			var passwordVal = $("#password").val();
			var checkVal = $("#password-check").val();
			if (passwordVal == '') {
				$("#password").after('<span class="error">Please enter a password.</span>');
				hasError = true;
			} else if (checkVal == '') {
				$("#password-check").after('<span class="error">Please re-enter your password.</span>');
				hasError = true;
			} else if (passwordVal != checkVal ) {
				$("#password-check").after('<span class="error">Passwords do not match.</span>');
				hasError = true;
			}
			if(hasError == true) {return false;}
			});
	});
</script>

 <%-- <script type="text/javascript"> 
	    $(document).ready(function() { 
	      $("#form1").validate({ 
	        rules: { 
          loginname: "required",// simple rule, converted to {required:true} 
	          email_address: {// compound rule 
	          required: true, 
          email: true 
	        },
			  role_name: "required",
			  password :"required",	
			  password-check :"required"
	         
	        comment: { 
	          required: true 
	        } 
	        }, 
	        messages: { 
	          loginname: "Please enter a login name.",
			  role_name: "Please select a role"
	        } 
	      }); 
	    }); 
	  </script> 
--%>
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
<!-- this comment gets all the way to the browser -->
<%-- this comment gets discarded when the JSP is translated into a Servlet --%>
<%
	Logger logger = Logger.getRootLogger();
	
    Vector<DBRegisteredUsers> regUserssVec = DBCommons.getDBCommons().getRegisteredUsersEntries();
    Iterator<DBRegisteredUsers> regUsersIter = regUserssVec.iterator();
    out.println("<table border=\"1\" width=\"80%\">");
    out.println("<tr bgcolor=\"#F3F783\"><td colspan=6><div align=\"center\"><strong>VITRO User Registered within the VITRO UI</strong></div></td></tr>");
    out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Login name</strong></td><td><strong>User role</strong></td><td><strong>Email address</strong></td><td><strong>Delete</strong></td><td colspan=2><strong>Disable</strong></td></tr>");

    int rowAlt = 0;
    while(regUsersIter.hasNext())
    {
        DBRegisteredUsers currUser = regUsersIter.next();

    // Now do something with the ResultSet ....
    // Fetch each row from the result set
        String bgrowcolor = "#F7FFFA";
        if (rowAlt > 0)
        {
            bgrowcolor  = "#F7F7FC";
        }
        rowAlt++;
        rowAlt = rowAlt %2;

        int userId = currUser.getIdUsers();
        String userLoginName = currUser.getloginName();// this is the one used in registration messages
        
        String roleName = currUser.getrole_name();
        String email  = currUser.getemailAddress();
        int role  = currUser.getIdRole();
        String lastDate = "N/A";
        Boolean disabled = currUser.getStatus();
        
        if ( currUser.getLastadvtimestamp() > 0 )
        {
            lastDate = currUser.getLastDate();
        }

        out.println("<tr bgcolor="+bgrowcolor+">");
        out.println("<td>");
        out.println(userLoginName);
        out.println("</td>");
        out.println("<td>");
        out.println(roleName);
        out.println("</td>");
        out.println("<td>");
        out.println(email);
        out.println("</span></td>");
        out.println("<td>");
        out.println("<a href=\"javascript:void(0)\" onclick=\"purgeUser('"+userLoginName+"', '"+currUser.getStatus()+"');return false;\" >Delete User</a>");
        out.println("</td>");
        out.println("<td>");
        if (disabled==false)
        {
        out.println("<a href=\"javascript:void(0)\" onclick=\"switchUserState('"+userLoginName+"', '"+currUser.getStatus()+"');return false;\" >Disable User</a>");
        
        }
        else
        {
        out.println("<a href=\"javascript:void(0)\" onclick=\"switchUserState('"+userLoginName+"', '"+currUser.getStatus()+"');return false;\" >Enable</a>");
        }
        out.println("</td>");
        out.println("</tr>");
        out.println("<tr bgcolor="+bgrowcolor+">");
        out.println("<td>");
        out.println("</td>");
        out.println("</tr>");
    }
    
    out.println("</table>");
    out.println("<br/>");
    
    
    
    out.println("<form method=\"post\" name=\"form1\" id=\"form1\" action=\"#\" onSubmit=\"return false;\">");
    out.println("<fieldset>");
    out.println("<table border=\"1\" width=\"80%\">");
    out.println("<tr bgcolor=\"#F3F783\"><td colspan=6><div align=\"center\"><strong>Create a new user</strong></div></td></tr>");
    out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Login name</strong></td><td><strong>User role</strong></td><td><strong>Email address</strong></td><td><strong>Password</strong></td><td><strong>Repeat Password</strong></td></tr>");
    out.println("<tr>");
    out.println("<td>");
    out.println("<input type=\"text\" name=\"loginname\" maxlength=\"40\" value=\"\">");
    out.println("</td>");
    out.println("<td>");
  	out.println("<select name=\"user_role\"><option selected>Select user role</option><option>VSP</option><option>WSIE</option><option>User</option></select>");
    out.println("</td>");
    out.println("<td>");
    out.println("<input type=\"text\" name=\"email_address\" maxlength=\"40\" value=\"\">");    
    out.println("</td>");
    out.println("<td>");
    out.println("<input type=\"password\" name=\"password\" id=\"password\" value=\"\" size=\"32\" />");
	out.println("</td>");
	out.println("<td>");
	out.println("<input type=\"password\" name=\"password-check\" id=\"password-check\" value=\"\" size=\"32\" />");
	out.println("</td>");
	out.println("<td>");
	out.println("<input type=\"button\" value=\"Insert new user\" id=\"submit\" onClick=\"ReceiveUserToInsert('insertUser');\">");
    out.println("</td>");
    out.println("</tr>");
    out.println("<tr>");                            
	out.println("<td colspan=\"6\" class=\"progressResultmsgs\">");   
    out.println("<table class=\"progressResultmsgs\">");  
    out.println("<tr>");  
    out.println("<td class=\"progressResultmsgs\">");  
    out.println("<div id=\"progressMsg\"><img alt=\"Indicator\" src=\""+request.getContextPath()+"/img/indicator.gif\" /> Loading...</div>");
    out.println("</td>");
    out.println("<td class=\"progressResultmsgs\">");  
    out.println("<tr>");  
    out.println("<div id=\"resultMsg\" style=\"display:none;position:absolute;\">&nbsp;</div>");
    out.println("</td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</fieldset>");
    out.println("</form>");
    
%>
</div>
</div>
</div>
</div>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>
</html>