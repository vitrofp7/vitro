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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.service.engine.UserNode;

/**
 *  *  OBSOLETE, or to be used for advanced service creation (by the VSP!)
 */
public class LoadCapabilitesFromGateways extends HttpServlet{

	private static final String PARAM_GATEWAY_ID_LIST = "gatewayIdList";
	
	private Logger logger = Logger.getLogger(LoadCapabilitesFromGateways.class);
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ServletContext application = getServletConfig().getServletContext();
		
		String gatwayList = req.getParameter("gatewayList");
		
		logger.debug("gatewayList " + gatwayList);
		
//		HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
//       
//		
//		UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
//		infoGWHM = ssUN.getGatewaysToSmartDevsHM();
//        
//		Collection<GatewayWithSmartNodes> gatewayIdList = infoGWHM.values();
//		
//        
//        req.setAttribute(PARAM_GATEWAY_ID_LIST, gatewayIdList);
        
        String nextJSP = "/roleVSP/service/capabilitiesList.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);
		
	}
	
}
