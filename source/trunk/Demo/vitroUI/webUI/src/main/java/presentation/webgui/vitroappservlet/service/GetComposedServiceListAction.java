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

package presentation.webgui.vitroappservlet.service;

import org.apache.log4j.Logger;
import presentation.webgui.vitroappservlet.service.uiwrapper.UIComposedService;
import vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.query.IndexOfQueries;
import vitro.vspEngine.service.query.QueryDefinition;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Should retrieve a list of the composed services stored in the DB
 */
public class GetComposedServiceListAction extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    private static final String PARAM_COMPOSED_SERVICE_LIST = "composedServiceList";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext application = getServletConfig().getServletContext();

        List<FullComposedService> allComposedServiceList = AbstractComposedServiceManager.getInstance().getComposedServiceList();

        if (allComposedServiceList!=null){
            List<UIComposedService> uiComposedServiceList = new ArrayList<UIComposedService>(allComposedServiceList.size());

            for(FullComposedService aComposedService : allComposedServiceList){
                UIComposedService currentuiComposedService = new UIComposedService(aComposedService);
                currentuiComposedService.setAppPath(application.getContextPath());
                currentuiComposedService.setUniqueQid(new StringBuilder().append(IndexOfQueries.COMPOSED_DB_PREFIX).append(currentuiComposedService.getComposedService().getId()).toString() );
                currentuiComposedService.setStatus(QueryDefinition.displayQueryStatusAction(currentuiComposedService.getUniqueQid()));
                //NO: We no longer we omit the predeployed services because we don't edit those. (after debugging)
                //if(!aComposedService.isPredeployed()) {
                    uiComposedServiceList.add(currentuiComposedService);
                //}
            }

            req.setAttribute(PARAM_COMPOSED_SERVICE_LIST, uiComposedServiceList);
        }
        String nextJSP = "/roleEndUser/VSPeditVitroComposedServices.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }

}
