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
