<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" %>
<%@page import="vitro.vspEngine.logic.model.Capability" %>
<%
    int resultStatus=0;
    String xmlerrordescr="OK";
    int errno = 0;

    // out parameter is the capability unique urn
    String myCapabilityID;
    myCapabilityID= request.getParameter("capid");

    if(myCapabilityID!=null && !myCapabilityID.trim().isEmpty())
    {
        if(Capability.isActuatingCapability(myCapabilityID))
        {
            resultStatus=1;
        }
    }
    else
    {
        errno = -1;
        xmlerrordescr="Warning. Wrong capability parameter posted!";
    }
%>
<Answer>
    <result status="<%=Integer.toString(resultStatus) %>" ></result>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmlerrordescr %>"></error>
</Answer>