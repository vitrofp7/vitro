<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="vitro.vspEngine.logic.model.GatewayWithSmartNodes" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.logic.model.Gateway" %>
<%@ page import="vitro.vspEngine.logic.model.SmartNode" %>
<%
        
	String xmlerrordescr="OK";
        int errno = 0;

        // out parameter is the gateways unique ID and name
        String myGatewayID;
        String myGatewayName;

        myGatewayID= request.getParameter("gwid");
        myGatewayName= request.getParameter("gwname");
%>
<Answer>
<%
    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    HashMap<String, GatewayWithSmartNodes> infoGWHM = ssUN.getGatewaysToSmartDevsHM();

        Set<String> keysOfGIds = infoGWHM.keySet();
        Iterator<String> itgwId = keysOfGIds.iterator();
        while(itgwId.hasNext()) {
            String currGwId = itgwId.next();
            Gateway currGw = infoGWHM.get(currGwId);
            Vector<SmartNode> tmpSmartDevVec = infoGWHM.get(currGwId).getSmartNodesVec();
            String gateId = currGw.getId();
            String gateName = currGw.getName();
            
       
            SmartNode tempSmartDevice;
            double longitude;
            double latitude;
            String longStr;
            String latStr;
            GeoPoint gploc;
            String smartDeviceDesc;
            if(!myGatewayID.equals("") && !myGatewayName.equals("") && myGatewayID.equals(gateId))
            {
                %>
    <gwid><%=myGatewayID %></gwid>
    <gwname><%=myGatewayName %></gwname>
                <%
                if(tmpSmartDevVec.size()>0)
                {
               %>
    <motes>
             <% }
                for (int j=0; j < tmpSmartDevVec.size(); j++) 
                {
                    tempSmartDevice = (SmartNode) tmpSmartDevVec.elementAt(j);
                    gploc = (GeoPoint) tempSmartDevice.getLocation();
                    if(gploc.isValidPoint())
                    {
                        longitude = gploc.getLongitude();
                        latitude = gploc.getLatitude();
                        longStr = Double.toString(longitude);
                        latStr = Double.toString(latitude);
                    }
                    else
                    {
                        longStr = "?";
                        latStr = "?";                        
                    }
               //(To do ) Later this XML description of the mote could be a backend hidden job.
               %>
        <mote>
            <id><%=tempSmartDevice.getId() %></id>
            <name><%=tempSmartDevice.getName() %></name>
            <location>
                <longitude><%=longStr %></longitude>
                <latitude><%=latStr %></latitude>
            </location>    
        </mote>    
             <% }
                if(tmpSmartDevVec.size()>0)
                {
                %>
    </motes>                    
             <% }
                break;  // <-- from the foor loop over all gateways (we only want this gateway's motes)
            }
        }
%>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmlerrordescr %>"></error>
</Answer>
