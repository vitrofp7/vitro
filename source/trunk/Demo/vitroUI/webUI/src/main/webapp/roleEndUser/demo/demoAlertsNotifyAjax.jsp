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
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*,  vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    String xmerrordescr="";
    int errno = 0;

    //sleep for a while
    Random generator = new Random();
    int sleepTime = generator.nextInt( 45000 );
    int celcDegreesAlrtOffset = generator.nextInt( 15 );
    int celcDegreesInfoOffset = generator.nextInt( 10 );
    Thread.sleep( sleepTime );

    String alertTitle = "";
    String alertTime = "";
    String alertDetails = "";
    int alertType = 0; // 0: notification, 1: alert!
    // get an alert message and its time
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");//dd/MM/yyyy
    Date now = new Date();
    String strDate = sdfDate.format(now);
    alertTitle = "Info ";
    alertTime = strDate;
    StringBuilder infoMsgStrBld = new StringBuilder();
    infoMsgStrBld.append("Temperature at location reached ");
    int info_CelcDegrees = celcDegreesInfoOffset + 20;
    infoMsgStrBld.append(info_CelcDegrees);
    infoMsgStrBld.append(" Celsius degrees.");
    alertDetails = infoMsgStrBld.toString();

    if(generator.nextInt(1000) > 200)
    {
        alertTitle ="Alert ";
        alertType =1;
        infoMsgStrBld = new StringBuilder();
        infoMsgStrBld.append("Temperature at location exceeded ");
        int alert_CelcDegrees = celcDegreesAlrtOffset + 30;
        infoMsgStrBld.append(alert_CelcDegrees);
        infoMsgStrBld.append(" Celsius degrees!");
        alertDetails = infoMsgStrBld.toString();
    }

    if(errno == 0)
    {
        xmerrordescr = "OK";
    }

%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <AlertTitle><%=alertTitle %></AlertTitle>
    <AlertTime><%=alertTime %></AlertTime>
    <AlertDetails><%=alertDetails %></AlertDetails>
    <AlertType><%=Integer.toString(alertType) %></AlertType>
</Answer>

