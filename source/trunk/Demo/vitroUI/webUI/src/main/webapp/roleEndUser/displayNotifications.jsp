<%@page session='false' contentType="text/html" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %><%
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
