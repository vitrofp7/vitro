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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class GetComposedServiceDeployListAdvanced extends HttpServlet {
    private Logger logger = Logger.getLogger(getClass());

    private static final String PARAM_COMPOSED_SERVICE_LIST = "composedServiceList";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<FullComposedService> allComposedServiceList = AbstractComposedServiceManager.getInstance().getComposedServiceList();
        if (allComposedServiceList!=null){

            List<UIComposedService> uiComposedServiceList = new ArrayList<UIComposedService>(allComposedServiceList.size());

            for(FullComposedService aComposedService : allComposedServiceList){
                UIComposedService currentuiComposedService = new UIComposedService(aComposedService);
                //we omit the predeployed services because we already show those
                if(!aComposedService.isPredeployed()) {
                    uiComposedServiceList.add(currentuiComposedService);
                }
            }

            req.setAttribute(PARAM_COMPOSED_SERVICE_LIST, uiComposedServiceList);
        }
        String nextJSP = "/roleEndUser/serviceDeployAdvanced.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }
}
