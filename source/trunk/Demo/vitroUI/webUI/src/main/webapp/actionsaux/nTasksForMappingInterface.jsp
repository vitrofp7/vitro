<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.Model3dservice.*' %>
<%
        
	String xmerrordescr="";
        int errno = 0;
        
        // form field parameters
        String action = "";
        String modelfilename = "";
        String gwid = "";
        long interfaceToHandle = Model3dInterfaceEntry.getUnknownInterfaceId();
                
        modelfilename = request.getParameter("modelfn");
        gwid = request.getParameter("gwid");
        interfaceToHandle = Long.parseLong(request.getParameter("iid"));
        action = request.getParameter("action");
        
        if(modelfilename!=null && !modelfilename.trim().equals("") &&
            gwid!=null && !gwid.trim().equals("") && 
            interfaceToHandle >= 0)
        {
            if(action.equals("deleteInt")) // deleting the interface for a mapping
            {
                if(Model3dIndex.deleteInterfaceEntry(modelfilename, gwid, interfaceToHandle) == true)
                {
                    errno = 0;
                    xmerrordescr = "OK";
                }
                else
                {
                    errno = 1; 
                    xmerrordescr = "Could not delete the specified interface!";
                }
            }
            else if(action.equals("defaultInt")) // setting the default interface for a mapping
            {
                if(Model3dIndex.setDefaultInterfaceEntry(modelfilename, gwid, interfaceToHandle) == true)
                {
                    errno = 0;
                    xmerrordescr = "OK";
                }
                else
                {
                    errno = 1; 
                    xmerrordescr = "Could not change the default interface!";
                }
            }
        }
        else
        {
            errno = 2; 
            xmerrordescr = "Improper call to handle an interface. Wrong parameters!";
        }
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <action><%=action %></action>
</Answer>

