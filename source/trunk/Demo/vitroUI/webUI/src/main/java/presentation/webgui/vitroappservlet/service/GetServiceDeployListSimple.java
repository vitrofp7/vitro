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
import presentation.webgui.vitroappservlet.service.uiwrapper.UIServiceInstance;
import vitro.vspEngine.service.common.abstractservice.AbstractServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class GetServiceDeployListSimple  extends HttpServlet {

    private Logger logger = Logger.getLogger(getClass());

    private static final String PARAM_SERVICE_INSTANCE_LIST = "serviceInstanceList";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<ServiceInstance> instanceList = AbstractServiceManager.getInstance().getInstanceList();
        if (instanceList!=null){
	        List<UIServiceInstance> uiInstanceList = new ArrayList<UIServiceInstance>(instanceList.size());
	
	        for(ServiceInstance instance : instanceList){
	            UIServiceInstance currentuiInstance = new UIServiceInstance(instance);
	            uiInstanceList.add(currentuiInstance);
	        }
	
	        req.setAttribute(PARAM_SERVICE_INSTANCE_LIST, uiInstanceList);	
        }

        String nextJSP = "/roleEndUser/serviceDeployCasual.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }
}

