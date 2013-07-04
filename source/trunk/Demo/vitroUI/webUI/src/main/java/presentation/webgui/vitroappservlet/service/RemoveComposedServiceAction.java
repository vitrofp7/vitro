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
import vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;

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
public class RemoveComposedServiceAction extends HttpServlet {

    private static final String PARAM_COMPOSED_SERVICE_ID = "compservId";


    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.debug("RemoveComposedServiceAction - doGet() - Start");

        ServletContext application = getServletConfig().getServletContext();


        int composedServiceId = Integer.parseInt(req.getParameter(PARAM_COMPOSED_SERVICE_ID));
        logger.debug("Removing composed service "+composedServiceId);

        AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
        FullComposedService composedService = manager.getComposedService(composedServiceId);


        AbstractComposedServiceManager.getInstance().removeComposedService(composedServiceId);


        logger.debug("RemoveComposedServiceAction - doGet() - Removed Service");

        String nextJSP = "/roleEndUser/GetComposedServiceListAction";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }
}
