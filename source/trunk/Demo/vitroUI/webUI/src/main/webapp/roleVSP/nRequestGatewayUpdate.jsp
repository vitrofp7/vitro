<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.service.persistence.*" %>
<%@ page import="org.apache.log4j.Logger" %>
<%
    Logger logger = Logger.getLogger(this.getClass());
    String xmerrordescr="";
    int errno = 0;
    QueryDefinition currQueryDef = null;

    // form field parameters
    String gwid = "";
    //int currAssumedStatus = QueryDefinition.STATUS_RUNNING;
    //int currActualStatus = QueryDefinition.STATUS_RUNNING;
    //int finalStatus = QueryDefinition.STATUS_RUNNING;

    gwid = request.getParameter("gwid");
    
    //currAssumedStatus = Integer.parseInt(request.getParameter("currassumedstatus"));

    if(gwid!=null && !gwid.trim().equals(""))
    {
        // get User node, then call send "REPORT" message over the command pipe for the selected gateway!
        // TODO: we coudl also find a way to confirm that the response was sent in order to get the new timestamp from the gateway
        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        boolean gwFoundInRegistry = false;
        try {
            if(ssUN != null)
            {
                Vector<String> regGWsRegNamesVec;
                regGWsRegNamesVec = DBCommons.getDBCommons().getRegisteredGatewayRegNames();
                Iterator<String> itgwregnames = regGWsRegNamesVec.iterator();
                while (itgwregnames.hasNext() && !gwFoundInRegistry) {

                    String tmpGwRegNameStr = itgwregnames.next();
                    if (tmpGwRegNameStr.equalsIgnoreCase(gwid)) {
                        gwFoundInRegistry = true;
                        DisabledNodesVGWSynch.getInstance().invalidateCacheForVGW(gwid);
                        ssUN.sendDirectCommand(gwid, "REPORT");
                        // TODO: ideally we could handle an extra "REPORT_ENDED" message from VGW to send the one-way synch of Equivalency Lists:
                        // sendDirectCommand() now includes a check for null or empty messages and won't send them if they are such.
                        String msgToSynchOneWayEquivLists = EquivNodeListsVGWSynch.getInstance().createMessageForVGW(gwid);
                        if(msgToSynchOneWayEquivLists!=null && !msgToSynchOneWayEquivLists.trim().isEmpty()) {
                            ssUN.sendDirectCommand(gwid, msgToSynchOneWayEquivLists);
                        }
                    }
                }
            }
            if(gwFoundInRegistry)
            {
                errno = 0;
                xmerrordescr = "OK";
            }
            else
            {
                errno = 1;
                xmerrordescr = "No such gateway exists in the registry!";
            }
        }catch (Exception e)
        {
            {
                errno = 3;
                logger.error("An unexpected error occurred" , e);
                xmerrordescr = "An unexpected error occurred:!" + e.getMessage();
            }
        }
    }
    else
    {
        errno = 2;
        xmerrordescr = "No gateway was specified!";
    }
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <gwid><%=gwid %></gwid>
</Answer>

