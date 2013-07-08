<%@page session='false' contentType="text/html" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %><%--
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

<%
    String querUid = "";
    querUid = request.getParameter("quid");
    // get the UserNode object
    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    IndexOfQueries myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
    String entireNotification = "";
    if(myIndexOfQueries.getQueryDefinitionById(querUid) != null)
    {
        if(myIndexOfQueries.getQueryDefinitionById(querUid).getNotificationsFromVSNsVec() == null)
        {
            entireNotification = "No notifications found!";
        }
        else
        {
            Iterator<NotificationsFromVSNs> itmy = myIndexOfQueries.getQueryDefinitionById(querUid).getNotificationsFromVSNsVec().iterator();
            while(itmy.hasNext())
            {
                String tmpAnote = itmy.next().getDetailsInHtml();
                entireNotification +=  tmpAnote + "<br/>";
            }
            if(entireNotification == null || entireNotification == "")
            {
                entireNotification = "No notifications were found!";
            }
            else
            {
                //entireNotification = entireNotification.replaceAll("<", "&lt;");
                //entireNotification = entireNotification.replaceAll(">", "&gt;");
            }
        }
    }
    else
    {
        entireNotification = "No such VSN exists in the Index!";
    }
%><%=entireNotification %>
