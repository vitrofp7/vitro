<%@page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'
        %>
<%@ page import="vitro.dcaintercom.communication.common.*" %>
<%@ page import="vitro.dcaintercom.communication.unica.*" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="presentation.webgui.vitroappservlet.ViewMyRegisteredIslands" %>
<%@ page import="vitro.vspEngine.service.persistence.DBCommons" %>
<%@ page import="vitro.vspEngine.service.persistence.DBRegisteredGateway" %>
<% String[] devices=request.getParameterValues("Devices");%>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Manage WSIs of Enabler (WSIE view)</title>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Manage Registered Islands</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#WSIE').addClass("active");
 	});     
    </script>



    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
</head>
<html>
<body><%-- For the menu --%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
	<div class="container-fluid">
		<div class="row-fluid">
			<div class="span2">
				<%= Common.printSideMenu(application.getRealPath("/"), request) %>
			</div>
			<div class="span10">
				<div class="well">
	
<%
		out.println("<table border=\"1\" class=\"table reborder\">");
		out.println("<tr bgcolor=\"#F3F783\"><td colspan=\"7\"><div align=\"center\"><strong>Parameters of the gateway</strong></div></td></tr>");
	
		ViewMyRegisteredIslands viewI = ViewMyRegisteredIslands.getViewMyRegisteredIslands();
		//Processing concentrators
		String restCon = viewI.sendResponseConcentratorsList();
		String recevCon = viewI.getResponseReceivedCon();
		String[] conList = viewI.processingDataConcentratorsId();
		String[] conListAddr = viewI.processingDataConcentratorsIPadd();
		String[] conListLoc = viewI.processingDataConcentratorsLocation();

		//Processing devices
		String restDev = viewI.sendResponseDevicesList();
		String recevDev = viewI.getResponseReceivedDev();
		String[] devList = viewI.processingDataDevicesId();
		String[] devListCreationTime = viewI.processingDataDevicesCreationTime();
		String[] devListRegTime = viewI.processingDataDevicesRegistrationTime();
		String[] devListStatus = viewI.processingDataDevicesStatus();

		//Relation between devices and concentrators
		Vector<String[]> devicesTable = viewI.getDataDevicesTable();

		//Show the concentrators information	
		//out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Concentrators name</strong></td>  <td> <strong> Concentrators IP address</strong></td><td> <strong> Location(Lat,long)</strong></td><td> <strong> Mark to see devices </strong></td></tr>");
    out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Gateway name</strong></td>  <td> <strong> Gateway host IP address</strong></td><td> <strong> Mark to see devices </strong></td></tr>");

    int lengthOfTheList = conList.length;
		out.println("<form action=\"WSIEeditIslands.jsp\" id=\"EditWSI\" method=\"post\">");
		for (int i = 0; i < lengthOfTheList; i++) {

            boolean foundmatchingReggedVGW = false;
            Vector<String> regGWsRegNamesVec;
            regGWsRegNamesVec = DBCommons.getDBCommons().getRegisteredGatewayRegNames();
            Iterator<String> itgwregnames = regGWsRegNamesVec.iterator();

            while (itgwregnames.hasNext()) {
                String currGwId = itgwregnames.next();
                if(currGwId.equalsIgnoreCase(conList[i]))
                {
                    foundmatchingReggedVGW = true;
                    break;
                }
            }
            if(!foundmatchingReggedVGW)
                continue; //do not display it.
			out.println("<tr><td>");
			out.println(conList[i]);
			out.println("<td>");
			out.println(conListAddr[i]);
			out.println("</td>");
			//out.println("<td>");
			//out.println("("+conListLoc[i]+")");
			//out.println("</td>");
			out.println("<td><input type=\"checkbox\" name=\"Devices\"value="+i+"></td>");
			out.println("</td></tr>");
		}// End for i
		out.println("</table>");
		out.println("<table>");
		out.println("<tr>");
		out.println("<td><input type=\"submit\" id=\"See Devices\" class=\"button\"	value=\"See Devices\" /> &nbsp;&nbsp;&nbsp;&nbsp;</td>");
		out.println("</tr>");
		out.println("</table>");
		out.println("</p>");
		out.println("</form> ");
		
		//Show the devices information 	
		if (devices != null) {
			out.println("<table border=\"1\" class=\"table reborder\">");
			out.println("<tr bgcolor=\"#F3F783\"><td colspan=7><div align=\"center\"><strong>Parameters of the devices</strong></div></td></tr>");
			for (int k = 0; k < devices.length; k++) {
				int ind = Integer.parseInt(devices[k]);
				Integer[] devConInd = viewI.relationConcentratorsDevicesIndexes(conList[ind]);
				out.println("<tr><td colspan=\"3\"><strong> Name of the gateway: "+ conList[ind] + "</td></tr>");
				//out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Devices ID</strong></td>  <td> <strong> Creation Time</strong></td><td> <strong> Registration Time</strong></td><td> <strong> Status</strong></td></tr>");
                out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Devices ID</strong></td>  <td> <strong> Creation Time</strong></td><td> <strong> Registration Time</strong></td></tr>");
                int lengthOfTheListDevCon = devConInd.length;

				String[] auxName = devicesTable.elementAt(0);
				String[] auxCreTime = devicesTable.elementAt(1);
				String[] auxRegTime = devicesTable.elementAt(2);
				String[] auxStatus = devicesTable.elementAt(3);

				for (int i = 0; i < lengthOfTheListDevCon; i++) {
					out.println("<tr>");
					out.println("<td>");
					out.println(auxName[devConInd[i]]);
					out.println("</td>");					
					out.println("<td>");
					out.println(auxCreTime[devConInd[i]]);
					out.println("</td>");
					out.println("<td>");
					out.println(auxRegTime[devConInd[i]]);
					out.println("</td>");
					out.println("<td style=\"visibility: hidden;\">");
					//if(Integer.parseInt(auxStatus[devConInd[i]])==0){
					//	out.println("OFF");
					//}else{
					//	out.println("ON");
					//}
					out.println("</td>");
					out.println("</tr>");
				}// End for i
			}//End for k
		}//End If devices
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