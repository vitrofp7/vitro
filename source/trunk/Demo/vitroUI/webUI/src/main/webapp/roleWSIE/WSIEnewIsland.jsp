<!DOCTYPE html>
<%@ page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'%>
<%@ page import="presentation.webgui.vitroappservlet.Common"%>
<%@ page import="vitro.dcaintercom.communication.unica.SensorData"%>
<%@ page import="vitro.dcaintercom.communication.common.IDASHttpRequest"%>
<%@ page import="vitro.dcaintercom.communication.common.XPathString"%>
<%@ page import="vitro.vspEngine.service.persistence.DBCommons" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%
String gatewayName=request.getParameter("gatewayName");
String gatewayFriendlyName=request.getParameter("gatewayFriendlyName");
String createI=request.getParameter("Create");
String deleteI=request.getParameter("Delete");
%>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

	<title>Register a new WSI with VITRO (WSIE view)</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#WSIE').addClass("active");
 	});     
	</script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
</head>
<html>
<body>
	<%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
			<!-- begin the side menu -->
			<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
	<div class="well">
	
<form class="form-horizontal" action="WSIEnewIsland.jsp" id="RegisterWSI" method="post">
    <fieldset>
      <div id="legend" class="">
        <legend class="">Register a new Island</legend>
      </div>
    <div class="control-group">

          <!-- Text input-->
		  <label class="control-label" for="gatewayName" id="gateway-label">Gateway	ID</label>
          <div class="controls">
						<input class="input-xlarge" placeholder="Gateway Name" id="gatewayName" type="text" name="gatewayName" />(*)
         </div>
        </div>

    <div class="control-group">
          <!-- Text input-->
          <label class="control-label" for="gatewayFriendlyName" id="gateway-label2">Gateway Description</label>
          <div class="controls">
            <input id="gatewayFriendlyName" name="gatewayFriendlyName" type="text" placeholder="friendly name" class="input-xlarge">
          </div>
    </div>

    <div class="control-group">

          <!-- Prepended checkbox -->
          <label class="control-label">WSI Setup</label>
          <div class="controls">
           <div class="input-prepend" style="display: block;">
              <span class="add-on">
                <label class="checkbox">
                  <input type="checkbox" class="" name="Create"	value="Create">
                </label>
              </span>
              <input class="span4" placeholder="Register WSI" type="text">
           </div>
           <div class="input-prepend" style="display: block;">
              <span class="add-on">
                <label class="checkbox">
                  <input type="checkbox" class="" name="Delete"	value="Delete">
                </label>
              </span>
              <input class="span4" placeholder="Disable WSI" type="text">
            </div>
          </div>
    </div>
          <!-- Form Actions -->
    <div class="form-actions">
              <button type="submit" class="btn btn-primary">Submit</button>
    </div>
    </fieldset>
</form>

		<%
			out.println("<table border=\"1\" class=\"table reborder\">");
			//Check if the the parameter gatewayName was correctly introduced
			if (gatewayName != null) {
				if (gatewayName.isEmpty()) {
					out.println("<tr bgcolor=\"#F3F783\"><td colspan=7><div align=\"center\"><strong>It is required to add a name for the gateway</strong></div></td></tr>");
				} else {
					//Check the action: create or delete WSIE
					if (deleteI != null && createI != null) {
						out.println("<tr bgcolor=\"#F3F783\"><td colspan=7><div align=\"center\"><strong>It is required to select only one choice, if the WSI is to be deleted or created</strong></div></td></tr>");
					} else if (createI != null) {
						//Selected action was to create
						out.println("<tr bgcolor=\"#BFDEE3\"><td colspan=7><div align=\"center\"><strong> Registered WSI </strong></div></td></tr>");

						//Create the concentrator with the given name of the gateway
						String xML = SensorData.createConcentrator(1001,gatewayName);
						IDASHttpRequest requestedData = IDASHttpRequest.getIDASHttpRequest();
						String responseString = requestedData.sendPOSTToIdasXML(xML);
						XPathString xpathStr = new XPathString(responseString);
						String[] errors;//Array of strings that stores the possible errors
						String[] errorValue;//When there is an error, the error that is.

						try {
							errors = xpathStr.parseXpathValues("//Envelope/Body/addDataByServiceResponse/errorText/text()");
						} catch (Exception ex) {
							errors = new String[1];
						}

						if (errors.length != 0) {
							if (errors[0].equals("OK")) {
								out.println("<tr><td>");
								out.println("Island " + gatewayName	+ " was successfully registered");
								out.println("</td></tr>");
                                // synch with external vitro app
                                DBCommons.getDBCommons().insertRegisteredGateway(gatewayName, gatewayFriendlyName);
							}
						} else {
							try {
								errorValue = xpathStr.parseXpathValues("//Envelope/Body/Fault/faultstring/text()");
							} catch (Exception ex) {
								errorValue = new String[1];
							}
							out.println("<tr><td>");
							out.println("Island " + gatewayName + " could not be registered. Error found");
							out.println("</td></tr>");
							if (errorValue.length != 0) {
								out.println("<tr><td>");
								out.println("<font color=red>The error was: "+ errorValue[0]);
								out.println("</td></tr>");
							}
						}//Close  (errors.length != 0)
					//Close else if (createI != null)
					} else if (deleteI != null) {
						//Selected action was to delete
						out.println("<tr bgcolor=\"#BFDEE3\"><td colspan=7><div align=\"center\"><strong>Delete WSI</strong></div></td></tr>");
					
						//Delete the concentrator with the given name of the gateway
						String xML = SensorData.deleteConcentrator(1001,gatewayName);
						IDASHttpRequest requestedData = IDASHttpRequest.getIDASHttpRequest();
						String responseString = requestedData.sendPOSTToIdasXML(xML);
						XPathString xpathStr = new XPathString(responseString);
						String[] errors;//Array of strings that stores the possible errors
						String[] errorValue;//When there is an error, the error that is.
						try {
							errors = xpathStr.parseXpathValues("//Envelope/Body/deleteDataByServiceResponse/errorText/text()");
						} catch (Exception ex) {
							errors = new String[1];
						}
						if (errors.length != 0) {
							if (errors[0].equals("OK")) {
								out.println("<tr><td>");
								out.println("Island " + gatewayName + " was successfully deleted");
								out.println("</td></tr>");
                                UserNode ssUN = null;
                                try
                                {
                                    ssUN = (UserNode)(application.getAttribute("ssUN"));

                                    ssUN.deleteVGW(gatewayName);

                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
						} else {
							try {
								errorValue = xpathStr.parseXpathValues("//Envelope/Body/Fault/faultstring/text()");
							} catch (Exception ex) {
								errorValue = new String[1];
							}
							out.println("<tr><td>");
							out.println("Island " + gatewayName+ " could not be deleted. Error found");
							if (errorValue.length != 0) {
								out.println("<tr><td>");
								out.println("<font color=red>The error was: "+ errorValue[0]);
								out.println("</td></tr>");
							}
						}//Close  (errors.length != 0)
					} else {
						out.println("<tr bgcolor=\"#F3F783\"><td colspan=7><div align=\"center\"><strong>It is required to select whether the WSI is to be deleted or created</strong></div></td></tr>");
					}
				}
			} else {
				out.println("<tr bgcolor=\"#F3F783\"><td colspan=7><div align=\"center\"><strong>It is required to add a name for the gateway</strong></div></td></tr>");
			}
			out.println("</table>");
		%>

	</div>
			</div>
		</div>
	</div>	
	<!-- begin the footer for the application -->
	<%= Common.printFooter(request, application) %>
	<!-- end of footer -->
</body>
</html>