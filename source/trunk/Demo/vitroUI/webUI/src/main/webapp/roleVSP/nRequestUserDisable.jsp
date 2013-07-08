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

