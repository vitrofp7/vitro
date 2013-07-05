/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package presentation.webgui.vitroappservlet;

import vitro.vspEngine.service.common.ConfigDetails;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.query.IndexOfQueries;
import vitro.vspEngine.service.query.NotificationsFromVSNs;
import vitro.vspEngine.service.query.QueryDefinition;
import vitro.vspEngine.service.query.QueryScheduler;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.persistence.*;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dIndex;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dStylesList;
import org.apache.log4j.Logger;

import javax.servlet.RequestDispatcher;
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
 * An early version of the Servlet for an application server port of the GUI.
 * (and the KML processing)
 */
public class GatewaysAndQuerysView extends HttpServlet {
	
	private static final String PARAM_SERVICE_ID_LIST = "serviceIdList";
	private static final String PARAM_RESULT_FILE = "resultFile";
	private static final String PARAM_FRIENDLY_NAME = "friendlyName";
    private Logger LOG;
    private String logLevel = "info";

    private UserNode ssUN = null;

    private QueryScheduler myQueryScheduler = null;

    private IndexOfQueries myIndexOfQueries;

    private ServletContext context;

    /**
     * Constructor
     */
    public GatewaysAndQuerysView() {
    }


    /**
     * doGet method of the Servlet. Handles http GET requests
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        ArrayList<String>  serviceIdList= new ArrayList<String>();
        ArrayList<String>  serviceNameList= new ArrayList<String>();
        ArrayList<String> resultList = new ArrayList<String>();
        



        myIndexOfQueries = IndexOfQueries.getIndexOfQueries();

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
            Set<String> querGWIds = currQueryDef.getInvolvedGatewayIds();
            StringBuilder csvGwsBuild = new StringBuilder();
            for (String aGwId : querGWIds) {
                csvGwsBuild.append(" " + aGwId + ",");
            }
            if(csvGwsBuild.length() >0 ){
                csvGwsBuild.deleteCharAt(csvGwsBuild.length() - 1);
            }


//          long creationTimeStamp = currQueryDef.getInitCreationTS();
            System.out.println(req.getContextPath() + "/roleEndUser/ViewResults?quid=" + tmpqDefuId + "&target=_blank");
            resultList.add(req.getContextPath() + "/roleEndUser/ViewResults?quid=" + tmpqDefuId + "&target=_blank");
            serviceNameList.add(querDefFriendlyName);
            serviceIdList.add(tmpqDefuId);
           
            orderInTable += 1;
        }
       
        
        System.out.println(serviceNameList);
        req.setAttribute(PARAM_SERVICE_ID_LIST, serviceIdList);
        req.setAttribute(PARAM_FRIENDLY_NAME, serviceNameList);
        req.setAttribute(PARAM_RESULT_FILE, resultList);

        String nextJSP = "/roleEndUser/monitor.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }

    /**
     * The doPost method of the Servlet. Handles http POST requests. Internally is uses a call to the doGet method
     * so that both will have the same functionality.
     */



    /**
     * The init method of the Servlet. Performs initialisation tasks. Here is were the UserPeer is instantiated
     */
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        context = servletConfig.getServletContext();
        Common.getCommon().setAppContext(context);
        // todo: this was moved out of the ssUN == null block bellow... check if regressions occur
        ConfigDetails.getConfigDetails(context.getRealPath("/"));

        Model3dIndex.setIndexPath(context.getRealPath("/") + File.separator + "Models" + File.separator);
        Model3dStylesList.setIndexFilenameandPath(context.getRealPath("/") + File.separator + "Models" + File.separator + "stylesIndex.xml");
        long startupEpoch = System.currentTimeMillis() ; // /1000;
        // Store in the Context
        context.setAttribute("startupEpoch", startupEpoch);

        ssUN = (UserNode) (context.getAttribute("ssUN"));

        if (ssUN == null) { // initialization of peer object and other related properties. After these are registered in the context, the following code won't be re-executed!
            initLogger(logLevel);
            LOG.info("Startup");
            try {
                // initiate a Query Scheduler!!!
                LOG.info("Starting VSN task scheduler!");
                myQueryScheduler = QueryScheduler.getQueryScheduler();
                myQueryScheduler.startScheduler();
                // VSP core engine node is the ssUN variable.
                LOG.debug("No VSP core engine node found in Context! Creating and staring One!");
                //
                ssUN = UserNode.getUserNode();
                // TODO: populate with existing received ADs in local folder (??)
                ssUN.switchInternalCommEngine(UserNode.ACTIVEMQ_COMM_MODE);
//                ssUN.switchInternalCommEngine(UserNode.DCA_COMM_MODE); // TODO:
                ssUN.startEngine();
                // Store in the Context
                context.setAttribute("ssUN", ssUN);

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    /**
     * The destroy method of the Servlet. It is called to gracefully stop the servlet
     */
    @Override
    public void destroy() {
        LOG.info("Destroying VGWs and VSNs view Servlet");
        if (myQueryScheduler != null)
            myQueryScheduler.stopScheduler();

        //close activeMQ pipes of the Engine Node for sending direct commands!
        ssUN = (UserNode) (context.getAttribute("ssUN"));
        if (ssUN != null)
        {
            ssUN.stopEngine();
        }
    }

    private void initLogger(String ll) {
        LOG = Logger.getLogger(this.getClass());
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

