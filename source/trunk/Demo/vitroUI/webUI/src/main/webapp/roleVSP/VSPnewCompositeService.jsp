<%@page import="org.jboss.logging.Logger"%>
<%@page import="org.hibernate.mapping.AuxiliaryDatabaseObject"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.net.URL"%>
<%@page import="vitro.virtualsensor.communication.unica.DCAClient"%>
<%@page import="vitro.virtualsensor.VirtualSensor"%>
<%@page import="vitro.virtualsensor.NodeController"%>
<%@page import="vitro.virtualsensor.SensorInformation"%>
<%@page import="vitro.dcaintercom.communication.common.Config" %>
<%@page import="vitro.dcaintercom.communication.*"%>
<%@page import="vitro.virtualsensor.utils.*"%>

<%@page session='false' contentType='text/html'
        import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'%>
<%@page import="presentation.webgui.vitroappservlet.Common"%>
<%@page import="presentation.webgui.vitroappservlet.ViewMyRegisteredIslands"%>
<%@ page import="vitro.vspEngine.logic.model.Capability" %>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>

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

<%
    String clientName = request.getParameter("clientName");//clientAppLogicalName
    String gateway = request.getParameter("oGateway");
    String appName = request.getParameter("subscriptionName");//subscriptionLogicalName
    //String subscriptionType = request.getParameter("subscriptionType");//eventKind
    String operation = request.getParameter("operation");//composition of services field Gateway:Phenomenom:UOM
    String inputs = request.getParameter("inputs");
    String output = request.getParameter("output"); //output
    //String responseURI = "http://150.140.5.43:80/vitroui/SubscriptionResponse";      // for testing (not used anymore)
    //String notifyURI = "http://150.140.5.43:80/vitroui/SubscriptionNotify";          // for testing (not used anymore)
%>
<head>

    
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Create new composite service (VSP view)</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-edit').addClass("active");
 	});     
    </script>
</head>

<html>
    <body>
        <%=Common.printDDMenu(application.getRealPath("/"), request)%>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
			<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
			<div class="well">
		<div class="row-fluid">
        <form method="post" id="formBasicId" action="VSPnewCompositeService.jsp" name="formbasic">
            <div class="span4">	
							<legend>
                                <strong>Service details</strong>
                            </legend>	
							<label for="clientName">Client name</label>
                            <input type="text" name="clientName" placeholder="Client Name">
							<label for="subscriptionName">Subscription name</label>
							<input type="text" name="subscriptionName" placeholder="Subscription name">
            </div>
            <div class="span4">
                <%
                    ViewMyRegisteredIslands viewI = ViewMyRegisteredIslands.getViewMyRegisteredIslands();
                    String restCon = viewI.sendResponseConcentratorsList();
                    String recevCon = viewI.getResponseReceivedCon();
                    String[] conList = viewI.processingDataConcentratorsId();
                %>
							<legend>
                                <strong>Gateway names</strong>
                            </legend>	
							<select id="gatewaysList" multiple="multiple" disabled="disabled">

								<%
									for (int i = 0; i < conList.length; i++) {%>
								<option value="<%=conList[i]%>">
									<%=conList[i]%>
								</option>
								<%}%>
							</select>

 							<legend>
                                <strong>Phenomena List</strong>
                            </legend>	
						   <select id="phenomenaList" multiple="multiple" disabled="disabled">

								<% String[] phenomena = UNICAData.getPhenomena();
									for (int i = 0; i < phenomena.length; i++) {%>
								<option value="<%=phenomena[i]%>">
									<%=phenomena[i]%>
								</option>
								<%}%>
							</select>
			</div>
            <div class="span4">
 							<legend>
                                <strong>Inputs</strong>
                            </legend>	
							<label for="inputs">Inputs</label>
							<input type="text" name="inputs" placeholder = "Gateway:phenomenom:uom">
                        <!--<label> Help: Gateway:phenomenom:uom </label> -->
							<legend>
                                <strong>Output</strong>
                            </legend>	
							<label for="output">Output</label>
							<input type="text" name="output" placeholder = "Phenomenom:UOM">
                        <!--<label> Help: Phenomenom:uom</label>  -->
 							<legend>
                                <strong>Output gateway</strong>
                            </legend>	
							<label for="oGateway">Gateway</label>
							<input type="text" name="oGateway" placeholder = "Output gateway">
							<legend>
                                <strong>Operation to perform</strong>
                            </legend>	
							<label for="operation">Operation</label>
							<input type="text" name="operation" placeholder = "Operation to perform">
                       <!-- <label> Introduce an expression </label>  -->

            </div>
            <div class="row-fluid">
			<input class="btn btn-large pull-right" type="submit" name="submit" id="submit" value="Submit">           
            </div>
        </form>

        <%
            NodeController n = NodeController.getInstance();
            if (clientName == null || appName == null || operation == null || inputs == null || output == null) {
            } else {
                if (clientName.equals("") || appName.equals("") || operation.equals("") || inputs.equals("") || output.equals("")) {
                    out.println("<form>");
                    out.println("<tr><td>");
                    out.println("Please introduce all the parameters");
                    out.println("</td></tr>");
                    out.println("</form>");
                } else {
                    ViewMyRegisteredIslands v = ViewMyRegisteredIslands.getViewMyRegisteredIslands();
                    String subscriptionType = "Observation";

                    Logger log = Logger.getLogger(getClass());

                    log.info("New Composite Service subscription");
                    log.info("clientName " + clientName);
                    log.info("appName " + appName);
                    log.info("subscriptionType " + subscriptionType);
                    log.info("operation " + operation);
                    log.info("output gateway" + gateway);
                    log.info("inputs " + inputs);
                    log.info("output " + output);
                    
                    ArrayList<ArrayList<String>> parameters;
                    if (((parameters = v.splitQuery(inputs)) == null) && (parameters.size() == 3)) {
                        out.println("<form>");

                        out.println("<tr><td>");
                        out.println("Please introduce a proper value for the inputs");
                        out.println("</td></tr>");
                        out.println("</form>");

                    } else {
                        ArrayList<String> gateways = parameters.get(0);
                        ArrayList<String> phenomena2 = parameters.get(1);
                        ArrayList<String> inputUOMs = parameters.get(2);

                        String[] output_aux = output.split(":");

                        if (output_aux.length != 2) {
                            out.println("<form>");

                            out.println("<tr><td>");
                            out.println("Please introduce a proper value for the output");
                            out.println("</td></tr>");
                            out.println("</form>");
                        } else {
                            String outputPhenom = output_aux[0];
                            String outputUom = output_aux[1];
                            for (int i = 0; i < inputUOMs.size(); i++) {
                                n.addMsgBox(inputUOMs.get(i));
                                log.info("The system is using an observable for " + inputUOMs.get(i));
                            }

                            String finalXPath = v.getQueryXPath(gateways, phenomena2);

                            if (finalXPath == null || finalXPath.equals("")) {
                                out.println("<form>");

                                out.println("<tr><td>");
                                out.println("Error computing the XPath. Please try again");
                                out.println("</td></tr>");
                                out.println("</form>");

                            } else {
                                ArrayList<SensorInformation> inputSensorInformation = new ArrayList<SensorInformation>();
                                for (int i = 0; i < gateways.size(); i++) {
                                    SensorInformation iSI = new SensorInformation("");
                                    iSI.setGateway(gateways.get(i));
                                    iSI.setOutputUOM(inputUOMs.get(i));
                                    // todo: prefix should not be explicit here
                                    iSI.setPhenomenom(Capability.dcaPrefix + phenomena2.get(i));
                                    inputSensorInformation.add(iSI);
                                }

                                //Create the system that computes the received data
                                VirtualSensor vs = n.addVirtualSensor("", gateway, outputPhenom, outputUom, operation, inputSensorInformation, appName);
                                log.info("A new Virtual Sensor has been created" + vs.getSensorInformation().getId());
                                log.info("GATEWAY: " + vs.getGw());
                                log.info("OUTPUT" + vs.getSensorInformation().getOutputUOM());

                                //Subscribe a new client
                                DCAClient client = n.addClient(clientName, Config.getConfig().getServiceIDVITRO(), n.getSubscriptionResponseURL(), subscriptionType, n.getNotificationResponseURL(), Config.getConfig().getServiceNameVITRO(), clientName, appName, finalXPath);
                                client.subscribe();
                            }
                        }
                    }
                }
            }
        %>
			</div>
			</div>
		</div>
	</div>

        <!-- begin the footer for the application -->
        <%=Common.printFooter(request, application)%>
        <!-- end of footer -->
    </body>
</html>