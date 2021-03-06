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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.engine.UserNode;

/**
 * The servlet for the New Composite VITRO Service (vsp role view)
 *  OBSOLETE, or to be used for advanced service creation (by the VSP!)
 */
public class CreateServiceServletPrepareForm extends HttpServlet{

	private static final String PARAM_GATEWAY_ID_LIST = "gatewayIdList";
	private static final String PARAM_SUPPORTED_CAPABILITIES = "supportedCapabilities";
    private static final String PARAM_SUPPORTED_ACT_CAPABILITIES = "supportedActuateCapabilities";
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ServletContext application = getServletConfig().getServletContext();
		
		HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
		HashMap<String,Vector<SensorModel>> ssUNCapHM = new HashMap<String, Vector< SensorModel >>();
        Set<String> capsSet = (new HashMap<String, String>()).keySet();
		
		UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
		infoGWHM = ssUN.getGatewaysToSmartDevsHM();
		ssUNCapHM = ssUN.getCapabilitiesTable();
        capsSet = ssUN.getCapabilitiesTable().keySet();
        
		Collection<GatewayWithSmartNodes> gatewayIdList = infoGWHM.values();
		ArrayList<String> supportedCapsOnGWVec = new ArrayList<String>();
        ArrayList<String> supportedActCapsOnGWVec = new ArrayList<String>();

        ////////////////////////////////////////////////
		Set<String> keysOfGIds = infoGWHM.keySet();
		Iterator<String> itgwId = keysOfGIds.iterator();
		int i = 0;

		while (itgwId.hasNext()) {
			String currGwId = itgwId.next();
			Vector<SmartNode> allSmartDevOfGwVec = infoGWHM.get(currGwId)
					.getSmartNodesVec();

			String gateId = infoGWHM.get(currGwId).getId();
			String gateName = infoGWHM.get(currGwId).getName();

			SmartNode aSmartDeviceOfGw;

			for (int j = 0; j < allSmartDevOfGwVec.size(); j++) {
				aSmartDeviceOfGw = (SmartNode) allSmartDevOfGwVec.elementAt(j);
				Vector<SensorModel> tmpSensorsModelsVec = aSmartDeviceOfGw
						.getCapabilitiesVector();
				// TODO: there really should be an easier way to find the supported capabilities of a smartDevice.

				for (int op = 0; op < tmpSensorsModelsVec.size(); op++) {
					Iterator<String> capsIt = capsSet.iterator();
					String currentCap;
					while (capsIt.hasNext()) {
						currentCap = capsIt.next();
						Vector<SensorModel> tmpSensVec = ssUNCapHM.get(currentCap);
						for (int sv = 0; sv < tmpSensVec.size(); sv++) {
							if ((tmpSensVec.elementAt(sv).getGatewayId()
									.equalsIgnoreCase(currGwId) && tmpSensVec
									.elementAt(sv)
									.getSmID()
									.equals(tmpSensorsModelsVec.elementAt(op)
											.getSmID()))
									&& (supportedCapsOnGWVec.isEmpty() || !supportedCapsOnGWVec
											.contains(currentCap))) {

								supportedCapsOnGWVec.add(currentCap);
                                if(Capability.isActuatingCapability(currentCap) && (supportedActCapsOnGWVec.isEmpty() || !supportedActCapsOnGWVec.contains(currentCap)))
                                {
                                    supportedActCapsOnGWVec.add(currentCap);
                                }
							}
						}
					}
				}
			}
		}

        
        req.setAttribute(PARAM_GATEWAY_ID_LIST, gatewayIdList);
        req.setAttribute(PARAM_SUPPORTED_CAPABILITIES, supportedCapsOnGWVec);
        req.setAttribute(PARAM_SUPPORTED_ACT_CAPABILITIES, supportedActCapsOnGWVec);

        String nextJSP = "/roleVSP/VSPnewVitroService.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);
		
	}
	
	

}
