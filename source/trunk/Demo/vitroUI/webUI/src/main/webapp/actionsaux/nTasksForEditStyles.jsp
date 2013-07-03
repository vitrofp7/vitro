<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.Model3dservice.*' %>
<%
        
	String xmerrordescr="";
        int errno = 0;
        
        // form field parameters
        String action = "";
        String styleid = "";
                
        styleid = request.getParameter("styleid");
        action = request.getParameter("action");
        
        if(styleid!=null && !styleid.trim().equals(""))
        {
            if(action.compareToIgnoreCase("deleteStyle")==0) // deleting the interface for a mapping
            {
                if(Model3dStylesList.deleteStyleEntry(styleid) == true)
                {
                    errno = 0;
                    xmerrordescr = "OK";
                }
                else
                {
                    errno = 1; 
                    xmerrordescr = "Could not delete the specified style!";
                }
            }
        }
        else
        {
            errno = 2; 
            xmerrordescr = "Improper call to handle the selected style. Wrong parameters!";
        }
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <action><%=action %></action>
</Answer>

