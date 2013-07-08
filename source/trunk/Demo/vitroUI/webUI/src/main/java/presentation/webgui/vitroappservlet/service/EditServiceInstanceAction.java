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
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import presentation.webgui.vitroappservlet.service.uiwrapper.ComboOptionDescriptor;
import presentation.webgui.vitroappservlet.service.uiwrapper.UIServiceInstance;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.common.abstractservice.AbstractServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;

/**
 * #superceded
 */
public class EditServiceInstanceAction  extends HttpServlet{

	private static final String PARAM_GATEWAY_ID_LIST = "gatewayIdList";
	private static final String PARAM_ALL_CAPABILITIES = "allCapabilities";
	private static final String PARAM_ALL_FUNCTIONS = "allFunctions";
    private static final String PARAM_ALL_FUNCTIONS_NAMES = "allFuncNames";
	private static final String PARAM_SUPPORTED_CAPABILITIES = "supportedCapabilities";
    private static final String PARAM_SUPPORTED_ACT_CAPABILITIES = "supportedActuateCapabilities";

	private static final String PARAM_SERVICE_INSTANCE_NAME = "serviceInstanceName";
	private static final String PARAM_SERVICE_INSTANCE_SEARCH_TAG_LIST = "serviceInstanceSearchTagList";
	private static final String PARAM_SERVICE_INSTANCE_ID = "serviceInstanceId";
	
	private static final String PARAM_SERVICE_ALLOWDTN = "serviceAllowDTN";
	private static final String PARAM_SERVICE_TRACKING = "tracking";
	private static final String PARAM_SERVICE_COMPOSITION = "composition";
	private static final String PARAM_SERVICE_ENCRYPTION = "encription";
	private static final String PARAM_SERVICE_SLA_MESSAGE = "slaMessage";
	private static final String PARAM_SERVICE_SUBSCRIPTION = "subscription";
	private static final String PARAM_SERVICE_SAMPLING_RATE = "samplingRate";
    private static final String PARAM_SERVICE_RULES_AND_FOR_NOTIFY = "rulesANDforNotify";
    private static final String PARAM_SERVICE_CONTINUATION = "continuation";

	public static final String[] CAPABILITIES_FUNCTION_LIST = {"LAST", "MIN", "MAX", "AVG"};
    public static final String[] CAPABILITIES_FUNCTION_NAMES_LIST = {"Latest Value", "Min Value", "Max Value", "Avg Value"};
	
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		logger.debug("EditServiceInstanceAction - doGet() - Start");
		
		ServletContext application = getServletConfig().getServletContext();
		
		HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
        HashMap<String,Vector<SensorModel>> ssUNCapHM = new HashMap<String, Vector< SensorModel >>();
        Set<String> capsSet = (new HashMap<String, String>()).keySet();
		
		UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        // TODO: Edit Service Instance should not depend on the capabilities detected on the ssUN, but on the capabilities stored
        // TODO: add ssUN null check!
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
       
        while(itgwId.hasNext())
        {
            String currGwId = itgwId.next();
            Vector<SmartNode> allSmartDevOfGwVec = infoGWHM.get(currGwId).getSmartNodesVec();
            
            String gateId = infoGWHM.get(currGwId).getId();
            String gateName = infoGWHM.get(currGwId).getName();
            
            SmartNode aSmartDeviceOfGw;
            
            for (int j=0; j<allSmartDevOfGwVec.size(); j++) {
                aSmartDeviceOfGw = (SmartNode) allSmartDevOfGwVec.elementAt(j);
                Vector<SensorModel> tmpSensorsModelsVec = aSmartDeviceOfGw.getCapabilitiesVector();
            // TODO: there really should be an easier way to find the supported capabilities of a smartDevice.

                for(int op=0; op < tmpSensorsModelsVec.size(); op++)
                {
                    Iterator<String> capsIt = capsSet.iterator();
                    String currentCap;
                    while(capsIt.hasNext()) {
                        currentCap = capsIt.next();
                        Vector<SensorModel> tmpSensVec = ssUNCapHM.get(currentCap);
                        for (int sv =  0 ; sv < tmpSensVec.size(); sv++)
                        {
                            if((tmpSensVec.elementAt(sv).getGatewayId().equalsIgnoreCase(currGwId) && tmpSensVec.elementAt(sv).getSmID().equals(tmpSensorsModelsVec.elementAt(op).getSmID()))
                                    && (supportedCapsOnGWVec.isEmpty() || !supportedCapsOnGWVec.contains(currentCap)) )
                            {
                                
                                supportedCapsOnGWVec.add(currentCap);
                                if(vitro.vspEngine.logic.model.Capability.isActuatingCapability(currentCap) && (supportedActCapsOnGWVec.isEmpty() || !supportedActCapsOnGWVec.contains(currentCap)))
                                {
                                    supportedActCapsOnGWVec.add(currentCap);
                                }
                            }
                        }
                    }

                }
            }
        }
        
        /////////////////////////////////////////////////
        
        int instanceId = Integer.parseInt(req.getParameter("instanceId"));
        
        
        AbstractServiceManager manager = AbstractServiceManager.getInstance();
        ServiceInstance serviceInstance = manager.getServiceInstance(instanceId);
        UIServiceInstance uiServiceInstance = new UIServiceInstance(serviceInstance);
        
        
        //// GATEWAY LIST

        List<ComboOptionDescriptor> gatewayOptionsList = new ArrayList<ComboOptionDescriptor>();
        /*
        List<DBRegisteredGateway> instanceGatewayList = serviceInstance.getGatewayList();
        
        
        for(GatewayWithSmartNodes gateway: gatewayIdList){
        	
        	String gatewayName = gateway.getId();
        	boolean gatewaySelected = isGatewayPresent(gatewayName, instanceGatewayList);
        	
        	
        	ComboOptionDescriptor optionDescriptor = new ComboOptionDescriptor(gatewayName, gatewaySelected);
        	logger.debug("gatewayOption - " + optionDescriptor);
        	gatewayOptionsList.add(optionDescriptor);
        }
		*/
        //// CAPABILITIES LIST
        
//        List<ComboOptionDescriptor> capabilitiesOptionsList = new ArrayList<ComboOptionDescriptor>();
        List<Capability> instanceCapabilities =  serviceInstance.getObservedCapabilities();
//        for(String capability: supportedCapsOnGWVec){
//        	
//        	boolean capabilitySelected = instanceCapabilities.contains(new Capability(capability, ""));
//        	
//        	ComboOptionDescriptor optionDescriptor = new ComboOptionDescriptor(capability, capabilitySelected);
//        	logger.debug("capabilityOption - " + optionDescriptor);
//        	capabilitiesOptionsList.add(optionDescriptor);
//        }
        
        
        logger.debug("gatewayOptionsList - " + gatewayOptionsList);
        logger.debug("instanceCapabilities - " + instanceCapabilities);
        logger.debug("CAPABILITIES_FUNCTION_LIST - " + CAPABILITIES_FUNCTION_LIST);
        logger.debug("supportedCapsOnGWVec - " + supportedCapsOnGWVec);
        logger.debug("supportedActCapsOnGWVec - " + supportedActCapsOnGWVec);
        
        req.setAttribute(PARAM_SERVICE_INSTANCE_ID, serviceInstance.getId());
        req.setAttribute(PARAM_SERVICE_INSTANCE_NAME, serviceInstance.getName());
        req.setAttribute(PARAM_SERVICE_INSTANCE_SEARCH_TAG_LIST, uiServiceInstance.getSearchTagsString());
        req.setAttribute(PARAM_GATEWAY_ID_LIST, gatewayOptionsList);
        
        
        req.setAttribute(PARAM_SERVICE_ALLOWDTN, serviceInstance.isAllowDTN());
        req.setAttribute(PARAM_SERVICE_COMPOSITION, serviceInstance.isComposition());
        req.setAttribute(PARAM_SERVICE_RULES_AND_FOR_NOTIFY, serviceInstance.isRulesANDforNotify());
        req.setAttribute(PARAM_SERVICE_CONTINUATION, serviceInstance.isContinuation());
        req.setAttribute(PARAM_SERVICE_TRACKING, serviceInstance.isRfidTracking());
        req.setAttribute(PARAM_SERVICE_ENCRYPTION, serviceInstance.isEncryption());
        req.setAttribute(PARAM_SERVICE_SLA_MESSAGE, serviceInstance.getSlaMessage());
        
        req.setAttribute(PARAM_SERVICE_SUBSCRIPTION, serviceInstance.isSubscriptionEnabled());
        req.setAttribute(PARAM_SERVICE_SAMPLING_RATE, serviceInstance.getSamplingRate());
        
        req.setAttribute(PARAM_SUPPORTED_CAPABILITIES, instanceCapabilities);
        req.setAttribute(PARAM_ALL_FUNCTIONS, CAPABILITIES_FUNCTION_LIST);
        req.setAttribute(PARAM_ALL_FUNCTIONS_NAMES, CAPABILITIES_FUNCTION_NAMES_LIST);
        req.setAttribute(PARAM_ALL_CAPABILITIES, supportedCapsOnGWVec);
        req.setAttribute(PARAM_SUPPORTED_ACT_CAPABILITIES, supportedActCapsOnGWVec);
        
        logger.debug("EditServiceInstanceAction - doGet() - Start");
        
        String nextJSP = "/roleVSP/VSPeditVitroService.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);
		
	}
	
	private boolean isGatewayPresent(String gatewayName, List<DBRegisteredGateway> instanceGatewayList){
		for (DBRegisteredGateway dbRegisteredGateway : instanceGatewayList) {
			if(dbRegisteredGateway.getRegisteredName().equalsIgnoreCase(gatewayName)){
				return true;
			}
		}
		
		return false;
	}
	
	

}
