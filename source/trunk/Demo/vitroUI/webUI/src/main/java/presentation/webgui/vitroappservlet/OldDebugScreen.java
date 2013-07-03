/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
package presentation.webgui.vitroappservlet;

import org.apache.log4j.Logger;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dIndex;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dStylesList;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.common.ConfigDetails;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.query.IndexOfQueries;
import vitro.vspEngine.service.query.NotificationsFromVSNs;
import vitro.vspEngine.service.query.QueryDefinition;
import vitro.vspEngine.service.query.QueryScheduler;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Restored old screen for debug purposes (not accessible through menu)
 * url: /roleEndUser/oldDebugScreen
 */
public class OldDebugScreen extends HttpServlet {

    private Logger logger = Logger.getLogger(OldDebugScreen.class);
    private String logLevel = "info";

    private UserNode ssUN = null;

    private QueryScheduler myQueryScheduler = null;

    private IndexOfQueries myIndexOfQueries;

    private ServletContext context;

    /**
     * Constructor
     */
    public OldDebugScreen() {
    }


    /**
     * doGet method of the Servlet. Handles http GET requests
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        context = getServletConfig().getServletContext();
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        ssUN = (UserNode) (context.getAttribute("ssUN"));
        if(ssUN == null) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("</head>");
            out.println("<body>");
            out.println("No valid VSP object was instantiated.");
            out.println("</body>");
            out.println("</html>");
            return;
        }

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"utf-8\">");
        out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        out.println("<link rel=\"shortcut icon\" href=\"" + request.getContextPath() + "/img/favicon2.ico\" type=\"image/x-icon\" />");
        out.println("<title>Vitro Application: Gateways and VSNs</title>");
        out.println("<link href=\"" + request.getContextPath() + "/css/bootstrap.css\" rel=\"stylesheet\">");
        out.println("<link href=\"" + request.getContextPath() + "/css/vitrodemo.css\" rel=\"stylesheet\">");
        out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/jquery-1.7.2.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/dbDataRetrieveInterfaceJS.jsp\"></script>");
        out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/dbAlertsRetrieveInterfaceJS.jsp\"></script>");

        //out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/prototype.js\"></script>");
        //out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/scriptaculous/scriptaculous.js\"></script>");
        //out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/modalbox/modalbox.js\"></script>");
        out.println("<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/getXMLRequest.js\"></script> " +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/js/modalbox/modalbox.css\"  media=\"screen\" />" +
                "<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/gatewaysAndQuerysViewTasksJS.jsp\"></script>");
        out.println("<script type=\"text/javascript\">" +
                "$(document).ready(function(){ " +
                "$('#dashboardMonitorButton').addClass(\"active\");" +
                "});" +
                "</script>");
        out.println("<script type=\"text/javascript\">" +
                "function meepFunction(replyVal,parValues){ " +
                "alert('callBack Return: '+parValues+ ' retVal: ' + replyVal);" +
                "}" +
                "</script>");
        out.println("</head>");
        out.println("<body>");
        //out.println("<!-- For the menu --><script type=\"text/javascript\" src=\""+request.getContextPath()+"/js/dropdownMenu/XulMenu.js\"></script><script type=\"text/javascript\" src=\""+request.getContextPath()+"/js/dropdownMenu/parseMenuHtml.jsp\"></script>");
        out.println("<!-- For the dropdown menu -->");
        out.println(Common.printDDMenu(context.getRealPath("/"), request));
        out.println("<!-- end of dropdown menu -->");

        out.println("<div id=\"dhtmltooltip\"></div>" +
                "<script type=\"text/javascript\" src=\"" + request.getContextPath() + "/js/tooltip/tooltip.js\"></script>");
        out.println("<p></p><div />"); // style=\"margin-top: +10%;\"/>");
        out.println("<div class=\"container-fluid\">" +
                "<div class=\"row-fluid\">" +
                "<div class=\"span12\">");
        out.println("<table border=\"1\" width=\"80%\">");
        out.println("<tr bgcolor=\"#F3F783\"><td colspan=2><div align=\"center\"><strong>VITRO Gateways connected</strong></div></td></tr>");
        out.println("<tr bgcolor=\"#BFDEE3\"><td><strong>Gateway name</strong></td><td><strong>Gateway ID</strong></td></tr>");

        logger.info("VSP gateways and VSNs view request");
        String bgrowcolor = "#F7F7FC";
        Set<String> keysOfGIds;
        try {
            if (ssUN != null) {
                keysOfGIds = ssUN.getGatewaysToSmartDevsHM().keySet();
            } else {
                keysOfGIds = (new HashMap<String, GatewayWithSmartNodes>()).keySet();
            }
        } catch (Exception exc1) {
            keysOfGIds = (new HashMap<String, GatewayWithSmartNodes>()).keySet();
            ;
        }

        Iterator<String> itgwId = keysOfGIds.iterator();
        while (itgwId.hasNext()) {
            String currGwId = itgwId.next();
            Gateway currGw = ssUN.getGatewaysToSmartDevsHM().get(currGwId);
            Vector<SmartNode> tmpSmartDevVec = ssUN.getGatewaysToSmartDevsHM().get(currGwId).getSmartNodesVec();

            String gateId = currGw.getId();
            String gateName = currGw.getName();

            out.println("<tr bgcolor=" + bgrowcolor + ">");
            out.println("<td>");
            //out.println(a.getName());
            out.println(gateName);
            out.println("</td>");
            out.println("<td>");
            //out.println(a.getPeerID());
            out.println(gateId);
            out.println("</td>");
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("<p></p>");
        out.println("<form method=\"post\" id=\"formBasicId\" action=\"#\" name=\"formbasic\"  onSubmit=\"return false;\" > ");
        out.println("<table border=\"1\" width=\"85%\" id=\"allVSNsTbl\" >");
        out.println("<tr bgcolor=\"#F3F783\"><td height colspan=\"12\"><div align=\"center\"><strong>");
        myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
        out.println("Deployed VSNs");
        out.println("</strong></div></td></tr>");
        out.println("<tr bgcolor=\"#F3C683\"><td align=\"left\" height colspan=\"12\"><div>");
        out.println("<input type=\"button\" name=\"selectall\" value=\"(Un)Select All\" onclick=\"switchSelectAllVSNs();return false;\" />");
        out.println("&nbsp;&nbsp;&nbsp; ");
        out.println("<input type=\"button\" name=\"stopselected\" value=\"Stop Selected\" onclick=\"actOnSelectedVSNs('stopBatch');return false;\" />");
        out.println("&nbsp;&nbsp;&nbsp; ");
        out.println("<input type=\"button\" name=\"startselected\" value=\"Start Selected\" onclick=\"actOnSelectedVSNs('startBatch');return false;\" />");
        out.println("&nbsp;&nbsp;&nbsp; ");
        out.println("<input type=\"button\" name=\"deleteselected\" value=\"Remove Selected\" onclick=\"actOnSelectedVSNs('removeBatch');return false;\" />");
//       out.println("&nbsp;&nbsp;&nbsp; ");
        out.println("<img style=\"width: 20px; height: 20px;\" alt=\"*new*\" title=\"New feature!\" src=\"" + request.getContextPath() + "/img/newIcon20.png\" />");
        out.println("</div></td></tr>");
        out.println("<tr bgcolor=\"#BFDEE3\">");
        out.println("<td  align=\"center\"><strong>Select &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Application &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Definition &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Location(s) &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Schedule &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Output Options &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Results (Google Earth link)&nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Results (Auto-refreshed Google Earth link)&nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Results (XML) &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Action &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Charges &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("<td  align=\"center\"><strong>Alerts and notifications &nbsp;&nbsp;&nbsp;</strong></td>");
        out.println("</tr>");

        Set<String> st0 = myIndexOfQueries.getAllQueriesDefHashMap().keySet();

        Iterator<String> it0 = st0.iterator();
        String tmpqDefuId = "";
        Set<QueryDefinition> unsortedQueries = new HashSet<QueryDefinition>();
        while (it0.hasNext()) {
            tmpqDefuId = it0.next();
            unsortedQueries.add(myIndexOfQueries.getAllQueriesDefHashMap().get(tmpqDefuId));
        }

        TreeSet<QueryDefinition> sortedQueriesSet = new TreeSet<QueryDefinition>(new Comparator<QueryDefinition>() {
            @Override
            public int compare(QueryDefinition o1, QueryDefinition o2) {
                if (o1.getInitCreationTS() > o2.getInitCreationTS())
                    return 1;
                else if (o1.getInitCreationTS() < o2.getInitCreationTS())
                    return -1;
                else
                    return 0;
            }
        });
        sortedQueriesSet.addAll(unsortedQueries);

        Iterator<QueryDefinition> querIt0 = sortedQueriesSet.iterator();
        QueryDefinition currQueryDef;
        int orderInTable = 0;
        while (querIt0.hasNext()) {

            currQueryDef = querIt0.next();
            tmpqDefuId = currQueryDef.getuQid();
            String querDefFriendlyName = currQueryDef.getFriendlyName();
            String querDefDetails = currQueryDef.getDetailsInHtml();
            Set<String> querGWIds = currQueryDef.getInvolvedGatewayIds();
            Vector<NotificationsFromVSNs> thisQueryNotifVec = currQueryDef.getNotificationsFromVSNsVec();
            StringBuilder csvGwsBuild = new StringBuilder();
            for (String aGwId : querGWIds) {
                csvGwsBuild.append(" " + aGwId + ",");
            }
            if(csvGwsBuild.length() >0 ){
                csvGwsBuild.deleteCharAt(csvGwsBuild.length() - 1);
            }


//          long creationTimeStamp = currQueryDef.getInitCreationTS();

            out.println("<tr id=\"tr_" + tmpqDefuId + "\">");
            out.println("<td><input type=\"checkbox\" name=\"quiCbox[]\" id=\"quidCbox_" + orderInTable + "\" value=\"" + tmpqDefuId + "\" /></td>");
            out.println("<td  align=\"left\"><label for=\"quidCbox_" + orderInTable + "\" >" +
                    "<span onMouseover=\"ddrivetip('" + querDefDetails + "','yellow', 450)\" onMouseout=\"hideddrivetip()\"> " +
                    "<strong>VSN: " + querDefFriendlyName + "</strong></a> " +
                    " </span>" +
                    "</label></td>");
            out.println("<td  align=\"left\">" +
                    "<a href=\"" + request.getContextPath() + "/roleEndUser/displayQueryDef.jsp?quid=" + tmpqDefuId + "&format=html\" target=\"_blank\" >html</a>, " +
                    "<a href=\"" + request.getContextPath() + "/roleEndUser/displayQueryDef.jsp?quid=" + tmpqDefuId + "&format=xml\" target=\"_blank\" >xml</a>, " +
                    "<a href=\"" + request.getContextPath() + "/roleEndUser/displayQueryDef.jsp?quid=" + tmpqDefuId + "&format=text\" target=\"_blank\" >text</a> " +
                    "</td>");

            out.println("<td>" + csvGwsBuild.toString() + "</td>");
            out.println("<td>&nbsp;</td>");
            out.println("<td>&nbsp;</td>");
            out.println("<td > <a href=\"" + request.getContextPath() + "/roleEndUser/ViewResults?quid=" + tmpqDefuId + "\" target=\"_blank\" >KML file</a></td>");
            out.println("<td > <span id=\"refreshPeriodHrefDiv_" + tmpqDefuId + "\"><a href=\"" + request.getContextPath() + "/roleEndUser/RefreshableResults?quid=" + tmpqDefuId + "&period=30\" target=\"_blank\" >Updateable KML</a></span> every <input type=text id=\"refreshPer_" + tmpqDefuId + "\" value=\"30\" maxlength=\"4\" size=\"4\" onChange=\"modifiedRefreshPeriod('" + tmpqDefuId + "')\" > secs</td>");
            out.println("<td > <a href=\"" + request.getContextPath() + "/roleEndUser/displayResultsFile.jsp?quid=" + tmpqDefuId + "\" target=\"_blank\" >Results file</a></td>");
            out.println("<td > <a href=\"#\" onClick=\"changeQueryRunningStatus('" + tmpqDefuId + "');return false;\"><span id=\"queryStatusActionTextDiv_" + tmpqDefuId + "\">" + displayQueryStatusAction(tmpqDefuId) + "</span></a></td>");
            out.println("<td>&nbsp;</td>");
            if(thisQueryNotifVec.size() == 0)
            {
                out.println("<td>No notifications</td>");
            }
            else
            {
                out.println("<td><a href=\"" + request.getContextPath() + "/roleEndUser/displayNotifications.jsp?quid=" + tmpqDefuId + "\" target=\"_blank\" >" +Integer.toString(thisQueryNotifVec.size())+ " notifications</td>");

            }
            out.println("</tr>");
            orderInTable += 1;
        }
        out.println("</table>");
        out.println("</form>");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getServiceSensorListForComposedService(1);' value='getServiceSensorListForComposedService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getServiceSensorListForPartialService(1);' value='getServiceSensorListForPartialService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getSensorCapabilityListForComposedService(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0xca3\" );' value='getSensorCapabilityListForComposedService 0xca3' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getSensorCapabilityListForComposedService(1,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0x295\" );' value='getSensorCapabilityListForComposedService 0x295' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getSensorCapabilityListForComposedService(1,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0x42f\" );' value='getSensorCapabilityListForComposedService 0x42f' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getSensorCapabilityListForComposedService(1,\"vitrogw_cti\",\"xxxxx\" );' value='getSensorCapabilityListForComposedService xxxx' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getSensorCapabilityListForComposedService(1,\"yyyyyy\",\"xxxxx\" );' value='getSensorCapabilityListForComposedService xxxx' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getSensorCapabilityListForPartialService(1,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0xca8\");' value='getSensorCapabilityListForPartialService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getDataCapabilityForComposedService(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0xca3\",3 );' value='getDataCapabilityForComposedService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getDataCapabilityForPartialService(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0xca3\",3 );' value='getDataCapabilityForPartialService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getMostRecentDataCapabilityForComposedService(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0xca3\",3 );' value='getMostRecentDataCapabilityForComposedService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getMostRecentDataCapabilityForPartialService(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0xca3\",3 );' value='getMostRecentDataCapabilityForPartialService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getComposedServiceCapabilityList(1);' value='getComposedServiceCapabilityList' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getPartialServiceCapabilityList(1);' value='getPartialServiceCapabilityList' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getPartialServicesForComposedServiceID(1);' value='getPartialServicesForComposedServiceID' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getComposedServiceList();' value='getComposedServiceList' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getAlarmHistoryComposedService(3);' value='getAlarmHistoryComposedService' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getAlarmHistorybySensor(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0x42f\");' value='getAlarmHistorybySensor' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getAlarmHistorybyCapability(3,3);' value='getAlarmHistorybyCapability id 3 3' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getAlarmHistorybyCapability(3,66);' value='getAlarmHistorybyCapability id 3 66' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getAlarmHistorybySensorCapability(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0x42f\",3);' value='getAlarmHistorybySensorCapability' /><br />");
        out.println("<input type='button' class=\"btn btn-small\" onclick='getAlarmHistorybySensorCapability(3,\"vitrogw_cti\",\"urn:wisebed:ctitestbed:0x42f\",3, meepFunction);' value='getAlarmHistorybySensorCapability' /><br />");


        out.println("</div>" +
                "</div>" +
                "</div>");
        out.println("<!-- begin the footer for the application -->");
        out.println(Common.printFooter(request, context ));
        out.println("<!-- end of footer -->");

        out.println("</body></html>");
        out.flush();
    }

    /**
     * The doPost method of the Servlet. Handles http POST requests. Internally is uses a call to the doGet method
     * so that both will have the same functionality.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }


    String displayQueryStatusAction(String quid) {
        String actionToReturn = "Stop";
        int currStatus = myIndexOfQueries.getQueryDefinitionById(quid).getRunningStatus();
        if (currStatus == QueryDefinition.STATUS_PAUSED)
            actionToReturn = "Start";
        else if (currStatus == QueryDefinition.STATUS_RUNNING)
            actionToReturn = "Stop";
        return actionToReturn;
    }

}


