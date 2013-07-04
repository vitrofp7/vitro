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
package presentation.webgui.vitroappservlet.service;

import org.apache.log4j.Logger;
import presentation.webgui.vitroappservlet.service.uiwrapper.UIComposedService;
import vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;
import vitro.vspEngine.service.engine.UserNode;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO:
 */
public class RemoveListOfComposedSelectedAction extends HttpServlet {

    private static final String PARAM_COMPOSED_SERVICE_ID = "compservId";
    private static final String PARAM_LIST_OF_SELECTED_COMPOSED_SERVICE_IDS = "ListOfSelected";



    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.debug("RemoveListOfComposedSelectedAction - doGet() - Start");
        ServletContext application = getServletConfig().getServletContext();

        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));

        String listOfSelected[] = req.getParameterValues(PARAM_LIST_OF_SELECTED_COMPOSED_SERVICE_IDS);

        if(listOfSelected!=null)
        {
            int composedServiceId = 0;

            for (int j=0; j<listOfSelected.length;j++)
            {
                composedServiceId = Integer.parseInt(listOfSelected[j]);
                logger.debug("InstanceID value " + composedServiceId + " in list with length " + listOfSelected.length);

                AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
                FullComposedService composedService = manager.getComposedService(composedServiceId);
                UIComposedService uiComposedService = new UIComposedService(composedService);

                AbstractComposedServiceManager.getInstance().removeComposedService(composedServiceId);
            }

        }


        logger.debug("RemoveListOfComposedSelectedAction - doGet() - Ended");

        String nextJSP = "/roleEndUser/GetComposedServiceListAction";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }

}
