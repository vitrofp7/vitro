<%@page session='false' contentType="text/html" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %><%
    String querUid = "";
    final String textmode = "text";
    final String tabmode = "tab";
    final String htmlmode = "html";
    final String xmlmode = "xml";

    final String defaultmode = textmode;
    String mode="" ;

    querUid = request.getParameter("quid");
    mode =  request.getParameter("format");

    if(mode==null || mode.trim().isEmpty())
    {
        mode=defaultmode;
    }

    // get the UserNode engine object
    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    IndexOfQueries myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
    String entireDetailsofQueryDeftoType = "";
    if(myIndexOfQueries.getQueryDefinitionById(querUid) != null)
    {
        if(mode.equalsIgnoreCase(xmlmode))
        {
            entireDetailsofQueryDeftoType = myIndexOfQueries.getQueryDefinitionById(querUid).toMyString(false);
            if(entireDetailsofQueryDeftoType == null || entireDetailsofQueryDeftoType.trim().equals(""))
            {
                entireDetailsofQueryDeftoType = "Error retrieving query definition!";
            }
            else
            {
                entireDetailsofQueryDeftoType = entireDetailsofQueryDeftoType.replaceAll("<", "&lt;");
                entireDetailsofQueryDeftoType = entireDetailsofQueryDeftoType.replaceAll(">", "&gt;");
            }
            out.print("<pre>"+entireDetailsofQueryDeftoType+"</pre>");
        }
        else if(mode.equalsIgnoreCase(htmlmode))
        {
            entireDetailsofQueryDeftoType = myIndexOfQueries.getQueryDefinitionById(querUid).getDetailsInHtml();
            if(entireDetailsofQueryDeftoType == null || entireDetailsofQueryDeftoType.trim().equals(""))
            {
                entireDetailsofQueryDeftoType = "Error retrieving query definition!";
            }
            out.print(entireDetailsofQueryDeftoType);
        }
        else if(mode.equalsIgnoreCase(textmode))
        {
            entireDetailsofQueryDeftoType = myIndexOfQueries.getQueryDefinitionById(querUid).getDetailsInText(false);
            if(entireDetailsofQueryDeftoType == null || entireDetailsofQueryDeftoType.trim().equals(""))
            {
                entireDetailsofQueryDeftoType = "Error retrieving query definition!";
            }
            out.print("<pre>"+entireDetailsofQueryDeftoType+"</pre>");
        }
        else if(mode.equalsIgnoreCase(tabmode))
        {

        }
    }
    else
    {
        entireDetailsofQueryDeftoType = "No such query exists in the Index!";
        out.print("<pre>"+entireDetailsofQueryDeftoType+"</pre>");
    }
    %>
