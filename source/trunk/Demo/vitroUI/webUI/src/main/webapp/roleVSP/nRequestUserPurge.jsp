<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.logic.model.SensorModel" %>
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
                        if(ssUN.getGatewaysToSmartDevsHM().containsKey(gwid)
                                && ssUN.getGatewaysToSmartDevsHM().get(gwid)!=null && ssUN.getGatewaysToSmartDevsHM().get(gwid).getSmartNodesVec()!= null)
                        {
                            //ssUN.getGatewaysToSmartDevsHM().remove(gwid);
                            ssUN.getGatewaysToSmartDevsHM().get(gwid).getSmartNodesVec().clear();
                            // todo: also cleanup the Capabilities vector, if no one else supports them? The sensor models indicate their gateway so they can be removed. And then the orphaned capabilities can be removed.
                            // TODO: what happens to ongoing VSNs for this gateway then?
                            Set<String> capsSet = ssUN.getCapabilitiesTable().keySet();
                            Iterator<String> capsIt = capsSet.iterator();
                            String currentCap;
                            Vector<String>  capsToDeleteVec = new Vector<String>();
                            while(capsIt.hasNext()) {
                                currentCap = capsIt.next();
                                Vector<SensorModel> tmpSensVec = ssUN.getCapabilitiesTable().get(currentCap);
                                for (int j = tmpSensVec.size() -1 ; j >= 0; j--)
                                {
                                    if(tmpSensVec.elementAt(j).getGatewayId().equalsIgnoreCase(gwid))
                                    {
                                        tmpSensVec.removeElementAt(j);

                                    }
                                }
                                if(tmpSensVec.isEmpty())
                                {
                                    capsToDeleteVec.addElement(currentCap);
                                }
                            }
                            // remove any capabilities with empty vectors
                            for (int j = 0 ; j < capsToDeleteVec.size(); j++)
                            {
                                ssUN.getCapabilitiesTable().remove(capsToDeleteVec.elementAt(j));
                            }

                        }
                        DBCommons.getDBCommons().updateRcvGatewayAdTimestamp(gwid, true);
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
    <error errno="<% Integer.toString(errno); %>" errdesc="<%=xmerrordescr %>"></error>
    <gwid><%=gwid %></gwid>
</Answer>

