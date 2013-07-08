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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import presentation.webgui.vitroappservlet.ViewMyRegisteredIslands;

/**
 * OBSOLETE???
 */
public class CreateServiceServletCompositeService extends HttpServlet{

    private static final long serialVersionUID = 1L;
    private static final String PARAM_GATEWAY_ID_LIST = "gatewayIdList";
    private static final String PARAM_SUPPORTED_CAPABILITIES = "supportedCapabilities";

    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        ServletContext application = getServletConfig().getServletContext();

        ViewMyRegisteredIslands v = ViewMyRegisteredIslands.getViewMyRegisteredIslands();

        String gwsXML = v.sendResponseConcentratorsList();
        String gws = v.getResponseReceivedCon();
        String [] gwList = v.processingDataConcentratorsId();
        Collection<String> gatewayIdList = Arrays.asList(gwList);


        for (int i = 0; i< gwList.length; i++){
                logger.debug(gwList[i]);
        }
        req.setAttribute(PARAM_GATEWAY_ID_LIST, gwList);
        req.setAttribute(PARAM_SUPPORTED_CAPABILITIES, gatewayIdList);


        String nextJSP = "/roleVSP/VSPnewCompositeService.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);
    }
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	doPost(req, resp);
    }
}
