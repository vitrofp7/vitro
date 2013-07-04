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
    String userid = "";
    //int currAssumedStatus = QueryDefinition.STATUS_RUNNING;
    //int currActualStatus = QueryDefinition.STATUS_RUNNING;
    //int finalStatus = QueryDefinition.STATUS_RUNNING;

    userid = request.getParameter("userid");
    //currAssumedStatus = Integer.parseInt(request.getParameter("currassumedstatus"));
	System.out.println("User id vale "+userid);
    if(userid!=null && !userid.trim().equals(""))
    {
        // Switch between enable/disable state
        // TODO: we coudl also find a way to confirm that the response was sent in order to get the new timestamp from the gateway
        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        boolean UserFoundInRegistry = false;
        try {
                boolean nodeDisabled;
                Vector<String> regUsersRegNamesVec;
                regUsersRegNamesVec = DBCommons.getDBCommons().getRegisteredUserRegNames();
                Iterator<String> itUserregnames = regUsersRegNamesVec.iterator();
                while (itUserregnames.hasNext() && !UserFoundInRegistry) {
                    String tmpUserRegNameStr = itUserregnames.next();
                    System.out.println("Entrato in user disable con userid come parametro "+userid);
                    if (tmpUserRegNameStr.equalsIgnoreCase(userid)) {
                        UserFoundInRegistry = true;
                        System.out.println("Invio ilm comando con userid come parametro "+userid);
                        DBCommons.getDBCommons().updateStatusUser(userid);
                        
                    }
                }
            
            if(UserFoundInRegistry)
            {
                errno = 0;
                xmerrordescr = "OK";
            }
            else
            {
                errno = 1;
                xmerrordescr = "No such user exists in the registry!";
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
        xmerrordescr = "No user was specified!";
    }
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <userid><%=userid %></userid>
</Answer>

