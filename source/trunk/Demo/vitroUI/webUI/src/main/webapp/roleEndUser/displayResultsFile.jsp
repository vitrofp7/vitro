<%@page session='false' contentType="text/html" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %><%
    String querUid = "";    
    querUid = request.getParameter("quid");
    // get the UserNode object
    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    IndexOfQueries myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
    String entireXMLofResultsFiletoType = "";
    if(myIndexOfQueries.getQueryDefinitionById(querUid) != null)
    {
        if(myIndexOfQueries.getQueryDefinitionById(querUid).getLatestQueryResultFile() == null)
       {
            entireXMLofResultsFiletoType = "No results found!";
       }
       else
       {
            entireXMLofResultsFiletoType = myIndexOfQueries.getQueryDefinitionById(querUid).getLatestQueryResultFile().toTmpString();
            if(entireXMLofResultsFiletoType == null || entireXMLofResultsFiletoType == "")
            {
                entireXMLofResultsFiletoType = "No results were found!";
            }
            else
            {
                entireXMLofResultsFiletoType = entireXMLofResultsFiletoType.replaceAll("<", "&lt;");
                entireXMLofResultsFiletoType = entireXMLofResultsFiletoType.replaceAll(">", "&gt;");
            }   
       }        
    }
    else
    {
        entireXMLofResultsFiletoType = "No such query ("+ querUid +") exists in the Index!";
    }
    %><pre><%=entireXMLofResultsFiletoType %></pre>
