<%@page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'
%>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="java.util.regex.Matcher" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="vitro.vspEngine.logic.model.SensorModel" %>
<%@ page import="vitro.vspEngine.logic.model.GatewayWithSmartNodes" %>
<%@ page import="vitro.vspEngine.logic.model.SmartNode" %>
<%@ page import="vitro.vspEngine.logic.model.Capability" %>
<html>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>A simple interface for setting up a VSN</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/queryParamTransactJS.jsp"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
 
</head>
<body><%-- For the menu --%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
<!-- this comment gets all the way to the browser -->
<%-- this comment gets discarded when the JSP is translated into a Servlet --%>
<div id="dhtmltooltip"></div>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/tooltip/tooltip.js"></script>
<p></p>
    <form method="post" id="formBasicId" action="#" name="formbasic"  onSubmit="return false;" >            
        <table border = "1">
            <tr bgcolor="#F3F783"><td colspan="5"><strong>Select WSIs (VGWs) or specific smart objects to include in the VSN</strong></td></tr>
            <tr bgcolor="#BFDEE3"><td>&nbsp; Gateways Discovered &nbsp; </td><td>&nbsp;  Smart Objects&nbsp;  </td><td>&nbsp;  Capabilities List&nbsp;  </td><td>&nbsp;  Function&nbsp;  </td><td>&nbsp;  Action&nbsp;  </td></tr>
            <tr>
                <td valign="top">
                    <div class="verbosecol" >
                        <table border = "0">
                            <%
                                boolean foundActuatingCapabilityGlobally = false;
                                HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
                                HashMap<String,Vector<SensorModel>> ssUNCapHM = new HashMap<String, Vector< SensorModel >>();
                                Set<String> capsSet = (new HashMap<String, String>()).keySet();
                                UserNode ssUN = null;
                                try
                                {
                                    ssUN = (UserNode)(application.getAttribute("ssUN"));
                                    infoGWHM = ssUN.getGatewaysToSmartDevsHM();
                                    ssUNCapHM = ssUN.getCapabilitiesTable();
                                    capsSet = ssUN.getCapabilitiesTable().keySet();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                int numOfSmartDevices = 0;

                                // List the avail gateways and also calculate the Total (overall) number of smartDevices. Each iteration of the loop adds the number of smartDevices of one gateway
                                Set<String> keysOfGIds = infoGWHM.keySet();
                                Iterator<String> itgwId = keysOfGIds.iterator();
                                int i = 0;
                                StringBuilder supportedCapabilitiesOnGwBld;
                                while(itgwId.hasNext())
                                {
                                    String currGwId = itgwId.next();
                                    Vector<SmartNode> allSmartDevOfGwVec = infoGWHM.get(currGwId).getSmartNodesVec();
                                    numOfSmartDevices = numOfSmartDevices + allSmartDevOfGwVec.size();
                                    String gateId = infoGWHM.get(currGwId).getId();
                                    String gateName = infoGWHM.get(currGwId).getName();
                                    supportedCapabilitiesOnGwBld = new StringBuilder();
                                    supportedCapabilitiesOnGwBld.append("<b>Supported Capabilities on this WSI:</b><br/>");
                                    SmartNode aSmartDeviceOfGw;
                                    Vector<String> supportedCapsOnGWVec = new Vector<String>();
                                    for (int j=0; j<allSmartDevOfGwVec.size(); j++) {
                                        aSmartDeviceOfGw = (SmartNode) allSmartDevOfGwVec.elementAt(j);
                                        Vector<SensorModel> tmpSensorsModelsVec = aSmartDeviceOfGw.getCapabilitiesVector();
                                    // TODO: there really should be an easier way to find the supported capabilities of a smartDevice.

                                        for(int op=0; op < tmpSensorsModelsVec.size(); op++)
                                        {
                                            Iterator<String> capsIt = capsSet.iterator();
                                            String currentCap;

                                            while(capsIt.hasNext()) {
                                                currentCap = capsIt.next();
                                                // todo: prefix should not be explicit here!
                                                String currentCapSimpleName = currentCap.replaceAll(Pattern.quote(Capability.dcaPrefix), "");

                                                Vector<SensorModel> tmpSensVec = ssUNCapHM.get(currentCap);
                                                for (int sv =  0 ; sv < tmpSensVec.size(); sv++)
                                                {
                                                    if((tmpSensVec.elementAt(sv).getGatewayId().equalsIgnoreCase(currGwId) && tmpSensVec.elementAt(sv).getSmID().equals(tmpSensorsModelsVec.elementAt(op).getSmID()))
                                                            && (supportedCapsOnGWVec.isEmpty() || !supportedCapsOnGWVec.contains(currentCap)) )
                                                    {
                                                        supportedCapabilitiesOnGwBld.append(currentCapSimpleName);
                                                        supportedCapabilitiesOnGwBld.append("<br />");
                                                        supportedCapsOnGWVec.addElement(currentCap);
                                                    }
                                                }
                                            }

                                        }
                                    }

                            %>
                            <tr>
                                <td>
                                    <INPUT TYPE=CHECKBOX NAME="GateWayCBox[]" ID="GateWayCBox_<%=Integer.toString(i) %>"  value="<%=gateId %>">
                                </td>
                                <td>
                                    <label for="GateWayCBox_<%=Integer.toString(i) %>">
                                        <span onMouseover="ddrivetip('<%=supportedCapabilitiesOnGwBld.toString() %>','yellow', 360)" onMouseout="hideddrivetip()"> <%=gateName%>  (<%=Integer.toString(infoGWHM.get(currGwId).getSmartNodesVec().size()) %> devices) </span>
                                    </label>
                                </td>
                            </tr>
                            <% i++;
                            } %>
                        </table>
                    </div>
                </td>
                <td valign="top">
                    <div class="verbosecol" >
                        <table border = "0">
                            <%
                                Iterator<String> itgwId2 = keysOfGIds.iterator();
                                i = 0;
                                while(itgwId2.hasNext())
                                {
                                    String currGwId = itgwId2.next();
                                    Vector<SmartNode> tmpSmartDevVec = infoGWHM.get(currGwId).getSmartNodesVec();
                                    String gateId = infoGWHM.get(currGwId).getId();
                                    String gateName = infoGWHM.get(currGwId).getName();

                                    SmartNode tempSmartDevice;
                                    double longitude;
                                    double latitude;
                                    String longStr="";
                                    String latStr="";
                                    GeoPoint gploc;
                                    String smartDeviceDesc;
                                    StringBuilder supportedCapabilitiesBld;
                                    for (int j=0; j<tmpSmartDevVec.size(); j++) {
                                        supportedCapabilitiesBld = new StringBuilder();
                                        supportedCapabilitiesBld.append("<b>Supported Capabilities:</b><br/>");
                                        tempSmartDevice = (SmartNode) tmpSmartDevVec.elementAt(j);
                                        gploc = (GeoPoint) tempSmartDevice.getLocation();
                                        if(gploc.isValidPoint())
                                        {
                                            longitude = gploc.getLongitude();
                                            latitude = gploc.getLatitude();
                                            longStr = Double.toString(longitude);
                                            latStr = Double.toString(latitude);
                                        }
                                        else
                                        {
                                            longStr = "?";
                                            latStr = "?";
                                        }
                                        // TODO: resume all info when geolocation is available, and proper friendly name

                                        //smartDeviceDesc = tempSmartDevice.getName()+"-("+longStr+", "+latStr+") : "+ tempSmartDevice.getId()+"@"+gateName;
                                        if(ssUN != null && ssUN.getCommMode() == UserNode.DCA_COMM_MODE)
                                        {
                                            smartDeviceDesc = tempSmartDevice.getId().replaceFirst(Pattern.quote(gateId+"."), "")+"@"+gateName;
                                        }
                                        else
                                        {
                                            smartDeviceDesc = tempSmartDevice.getId()+"@"+gateName;
                                        }
                                        Vector<SensorModel> tmpSensorsModelsVec = tempSmartDevice.getCapabilitiesVector();
                                        // TODO: there really should be an easier way to find the supported capabilities of a smartDevice.
                                        boolean foundActuatingCapabilityInDevice = false;
                                        for(int op=0; op < tmpSensorsModelsVec.size(); op++)
                                        {
                                            boolean foundActuatingCapabilityInSensor = false;
                                            Iterator<String> capsIt = capsSet.iterator();
                                            String currentCap;
                                            while(capsIt.hasNext()) {
                                                currentCap = capsIt.next();
                                                // todo: prefix should not be explicit here!
                                                String currentCapSimpleName = currentCap.replaceAll(Pattern.quote(Capability.dcaPrefix), "");

                                                Vector<SensorModel> tmpSensVec = ssUNCapHM.get(currentCap);
                                                for (int sv =  0 ; sv < tmpSensVec.size(); sv++)
                                                {
                                                    if(tmpSensVec.elementAt(sv).getGatewayId().equalsIgnoreCase(currGwId) && tmpSensVec.elementAt(sv).getSmID().equals(tmpSensorsModelsVec.elementAt(op).getSmID()))
                                                    {
                                                        //Matcher m = Pattern.compile("\\d+$").matcher(currentCap);
                                                        //int numberInTheEnd = -1;  // TODO: for now, we discover an actuator by searching for a trailing number in their name
                                                        //if(m.find()) {
                                                        if (Capability.isActuatingCapability(currentCap)){
                                                            //numberInTheEnd = Integer.parseInt(m.group());
                                                            foundActuatingCapabilityInSensor = true;
                                                            foundActuatingCapabilityInDevice = true;
                                                            foundActuatingCapabilityGlobally = true;
                                                        }
                                                        supportedCapabilitiesBld.append(currentCapSimpleName);
                                                        if(foundActuatingCapabilityInSensor) {supportedCapabilitiesBld.append(": <i>Actuator</i>");}
                                                        supportedCapabilitiesBld.append("<br />");

                                                    }
                                                }
                                            }
                                        }
                            %>
                            <tr <% if(foundActuatingCapabilityInDevice) {out.print("bgcolor=\"#88cbcf\"");} %>  >
                                <td>
                                    <INPUT TYPE=CHECKBOX NAME="SmDevCBox[]" ID="SmDevCBox_<%=Integer.toString(i) %>_<%=Integer.toString(j) %>" value="<%=tempSmartDevice.getId() %>">
                                    <INPUT TYPE=HIDDEN NAME="IndxOfGwCbForThisSmDev[]" value="<%=Integer.toString(i) %>">
                                </td>
                                <td><label for="SmDevCBox_<%=Integer.toString(i) %>_<%=Integer.toString(j) %>">
                                    <span onMouseover="ddrivetip('<%=supportedCapabilitiesBld.toString() %>','yellow', 360)" onMouseout="hideddrivetip()"> <%=smartDeviceDesc %> </span>
                                </label>
                                </td>
                            </tr>
                            <%
                                    }
                                    i++;
                                }
                            %>
                        </table>
                    </div>
                </td>
                <td valign="top">
                    <div class="verbosecol" >
                        <table border = "0">
                            <%
                                // capabilities LIST
                                //Set<String> capsSet = (new HashMap<String, String>()).keySet();
                                //Set capsSet = ssBP.getCapabilitiesTable().keySet();
                                TreeSet<String> sortedCapsSet = new TreeSet<String>(new Comparator<String>(){
                                // @Override  // <-- annotation does not work (for Tomcat) inside JSP pages!
                                public int compare(String o1, String o2) {
                                    return o1.compareToIgnoreCase(o2);
                                }});
                                sortedCapsSet.addAll(capsSet);
                                i = 0;
                                for (String currentCap : sortedCapsSet) {
                                    String currentCapSimpleName = currentCap.replaceAll(Pattern.quote(Capability.dcaPrefix), "");

//                                Iterator<String> capsIt = capsSet.iterator();
//                                i=0;
//                                String currentCap;
//                                while(capsIt.hasNext()) {
//                                    currentCap = capsIt.next();
                            %>
                            <tr>
                                <td>
                                    <INPUT TYPE=CHECKBOX NAME="GenericCapsBox[]" ID="GenericCapsBox_<%=Integer.toString(i) %>" value="<%=currentCap %>">
                                </td>
                                <td><label for="GenericCapsBox_<%=Integer.toString(i) %>">
                                    <%=currentCapSimpleName%>
                                </label>
                                </td>
                            </tr>
                            <%
                                    i++;
                                }
                            %>
                        </table>
                    </div>
                </td>
                <td valign="top">
                    <table border = "0">
                        <tr>
                            <td>
                                <INPUT TYPE=CHECKBOX NAME="FunctionsBox[]" ID="FunctionsBox_0" checked="true" value="<%= ReqFunctionOverData.lastValFunc %>">
                            </td>
                            <td><label for="FunctionsBox_0"> 
                                    Latest Value
                                </label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <INPUT TYPE=CHECKBOX NAME="FunctionsBox[]" ID="FunctionsBox_1" value="<%= ReqFunctionOverData.avgFunc %>">
                            </td>
                            <td><label for="FunctionsBox_1">                                 
                                    Average Value
                                </label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <INPUT TYPE=CHECKBOX NAME="FunctionsBox[]" ID="FunctionsBox_2" value="<%= ReqFunctionOverData.maxFunc %>">
                            </td>
                            <td><label for="FunctionsBox_2"> 
                                    Max Value
                                </label>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <INPUT TYPE=CHECKBOX NAME="FunctionsBox[]" ID="FunctionsBox_3" value="<%= ReqFunctionOverData.minFunc %>">
                            </td>
                            <td><label for="FunctionsBox_3"> 
                                    Min Value
                                </label>
                            </td>
                        </tr>
                        <% if(foundActuatingCapabilityGlobally) { %>
                        <tr>
                            <td>
                                <INPUT TYPE=CHECKBOX NAME="FunctionsBox[]" ID="FunctionsBox_4" value="<%= ReqFunctionOverData.setValFunc %>">
                            </td>
                            <td><label for="FunctionsBox_4">
                                Set Value
                            </label>
                                <select name="FunctionActuationValueSelect" id="FunctionActuationValueSelect">
                                    <option value="1">On</option>
                                    <option value="0">Off</option>
                                </select>
                            </td>
                        </tr>
                        <% } %>
                    </table>
                </td>
                <td valign="top">
                    <table border = "0">
                        <tr>
                            <td>
                                Issuing period (secs):
                            </td>
                            <td>
                                <input type="text" id="periodOfIssueId" name="periodOfIssue" value="40" size="5" />
                            </td>
                        </tr>
                        <tr>
                            <td>
                                History length:
                            </td>
                            <td>
                                <input type="text" id="historyNum" name="NumofHistoryResultsForQuery" value="10" size="4" />
                            </td>
                        </tr>
                        <tr style="display:none;">
                            <td colspan="2">
                                <table border = "0">
                                    <td>
                                        <input type="checkbox" id="aggregateCb" name="aggregateQueries" value="ON" checked="checked"  />
                                    </td>
                                    <td>
                                        <label for="aggregateCb" >Send aggregated requests</label>
                                    </td>
                                </table>
                            </td>            
                        </tr>
                        <tr>
                            <td colspan="2">                                 
                                <input type="button" id="Submit0" value="Submit Request for VITRO VSN" name="Submit0" onClick="QueryTransact('newQuery');"  style="cursor:pointer; background-color:#8D8DF6; color:#FCFDBD;  font: 11px tahoma"  />
                            </td>
                        </tr>
                        <tr>                             
                            <td colspan="2" class="progressResultmsgs">
                                <table class="progressResultmsgs">
                                    <tr>
                                        <td class="progressResultmsgs">
                                             <div id="progressMsg" style="display:none;position:absolute;"><img alt="Indicator" src="<%=request.getContextPath()%>/img/indicator.gif" /> Loading...</div>
                                        </td>
                                        <td class="progressResultmsgs">
                                             <div id="resultMsg" style="display:none;position:absolute;">&nbsp;</div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>    
                    </table>
                </td>
            </tr>
        </table>
    </form>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>
</html>