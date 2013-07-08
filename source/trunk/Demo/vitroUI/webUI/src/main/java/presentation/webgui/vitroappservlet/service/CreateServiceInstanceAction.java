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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.service.common.abstractservice.AbstractServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.engine.UserNode;

/**
 * Handles the creation of a ServiceInstance
 * # superceded
 */
public class CreateServiceInstanceAction extends HttpServlet{

	private static final String PARAM_GATEWAY_ID_LIST = "gatewayIdList";
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		ServletContext application = getServletConfig().getServletContext();
		
		HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
       
		
		UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
		infoGWHM = ssUN.getGatewaysToSmartDevsHM();
        
		Collection<GatewayWithSmartNodes> gatewayIdList = infoGWHM.values();
		
        
        req.setAttribute(PARAM_GATEWAY_ID_LIST, gatewayIdList);
        
        String createAction = req.getParameter("create");
        
        String serviceNameTxtBx = req.getParameter("serviceNameTxtBx");
        String tagsCSVtxtbx = req.getParameter("tagsCSVtxtbx");
        
        String[] gateways = req.getParameterValues("gateways");
        //String[] involvedCaps = req.getParameterValues("InvolvedCaps");
        //String[] functions = req.getParameterValues("functions");

        String[] involvedCaps = req.getParameterValues("rulCap[]");
        String[] functions = req.getParameterValues("rulFunctSens[]");
        String[] rulFunctAct = req.getParameterValues("rulFunctAct[]");
        String[] rulFunctThresh = req.getParameterValues("rulFunctThresh[]");
        String[] rulBoundSens = req.getParameterValues("rulBoundSens[]");
        String[] rulBoundAct = req.getParameterValues("rulBoundAct[]");

        String[] rulHasTrigger = req.getParameterValues("rulHasTrigger[]");
        String[] rulTrigCond = req.getParameterValues("rulTrigCond[]");
        String[] rulTrigBound = req.getParameterValues("rulTrigBound[]");
        String[] rulTrigAct = req.getParameterValues("rulTrigAct[]");
        String[] rulTrigCapsAct = req.getParameterValues("rulTrigCapsAct[]");
        String[] rulTrigVal = req.getParameterValues("rulTrigVal[]");
        String[] rulTrigBoundAct = req.getParameterValues("rulTrigBoundAct[]");
        String[] rulTrigNodesAct = req.getParameterValues("rulTrigNodesAct[]");
        
        //"true" when checked, null otherwise
        String allowDTNCheck = req.getParameter("allowDTNCxBx");
      //"true" when checked, null otherwise
        String encryptionCheck = req.getParameter("encryptionCxBx");
      //"true" when checked, null otherwise
        String trackingCheck = req.getParameter("trackingCxBx");
      //"true" when checked, null otherwise
        String compositionCheck = req.getParameter("compositionCxBx");
        //"true" when checked, null otherwise
        String rulesANDforNotifyCheck = req.getParameter("rulesANDforNotifyCxBx");
        //"true" when checked, null otherwise
        String continuationCheck = req.getParameter("continuationCxBx");


        String slaMessage = req.getParameter("slaMessage");
        
        String subscriptionValue = req.getParameter("subscriptionRadio");
        String samplingRateValue = req.getParameter("samplingRate");
		
        logger.debug("allowDTNCheck " + allowDTNCheck);
        logger.debug("encryptionCheck " + encryptionCheck);
        logger.debug("trackingCheck " + trackingCheck);
        logger.debug("compositionCheck " + compositionCheck);
		logger.debug("serviceNameTxtBx " + serviceNameTxtBx);
		logger.debug("tagsCSVtxtbx " + tagsCSVtxtbx);
		logger.debug("slaMessage " + slaMessage);
		logger.debug("subscription " + subscriptionValue);
		logger.debug("samplingRate " + samplingRateValue);
        logger.debug("rulesANDforNotify " + rulesANDforNotifyCheck);
        logger.debug("continuation  " + continuationCheck);
		
		if(gateways != null){
			for(int i = 0; i < gateways.length;i++){
				logger.debug("gateways[" + i + "] " + gateways[i]);
			}
		}
		
		List<Capability> capabilities = new ArrayList<Capability>(1);
		
		if(involvedCaps != null){
			for(int i = 0; i < involvedCaps.length;i++){
				logger.debug("involvedCaps[" + i + "] " + involvedCaps[i]);
                // TODO: needs check for null!    and check for actuating capabiliy functions or sensing
                String selFunct = "LAST" ; //default value
                String selFunctTheshValue = "" ; //default value
                if(functions!=null && functions.length > i && !functions[i].isEmpty()){
                    selFunct = functions[i];
                    selFunctTheshValue =  rulBoundSens[i];
                    logger.debug("functions[" + i + "] " + functions[i]);
                }
                else if(rulFunctAct!=null && rulFunctAct.length > i && !rulFunctAct[i].isEmpty()){
                    selFunct = rulFunctAct[i];
                    selFunctTheshValue =  rulBoundAct[i];
                    logger.debug("functions[" + i + "] " + rulFunctAct[i]);
                }
				Capability currentCapability = new Capability();
                currentCapability.setFunction(selFunct);

                currentCapability.setFunctionThresholdSign(rulFunctThresh[i]);
                currentCapability.setFunctionThresholdValue(selFunctTheshValue);

                currentCapability.setHasTrigger(rulHasTrigger[i]);
                currentCapability.setTriggerConditionSign(rulTrigCond[i]);
                currentCapability.setTriggerConditionValue(rulTrigBound[i]);

                currentCapability.setTriggerAction(rulTrigAct[i]);
                currentCapability.setTriggerActuationName(rulTrigCapsAct[i]);
                currentCapability.setTriggerGenTextValue(rulTrigVal[i]);
                currentCapability.setTriggerActuationValue(rulTrigBoundAct[i]);
                currentCapability.setTriggerActuationNodes(rulTrigNodesAct[i]);

				currentCapability.setName(involvedCaps[i]);
				capabilities.add(currentCapability);
				
			}
		}
		
		slaMessage = slaMessage == null ? null : slaMessage.trim();
		boolean alloDTN = allowDTNCheck == null ? false : true;
		boolean encryption = encryptionCheck == null ? false : true;
		boolean rfidTracking = trackingCheck == null ? false : true;
		boolean composition = compositionCheck == null ? false : true;
        boolean rulesANDforNotify = rulesANDforNotifyCheck  == null ? false : true;
        boolean continuationFlg =      continuationCheck == null ? false : true;
//		List<String> involvedCapList = involvedCaps == null ? null : Arrays.asList(involvedCaps);
		
		List<String> gatewayList = gateways == null ? null : Arrays.asList(gateways);
		boolean subscription = subscriptionValue == null || subscriptionValue.trim().equals("false") ? false : true;
		long samplingRate = 0;
		
		try{
			samplingRate = Long.parseLong(samplingRateValue);
		} catch(NumberFormatException ex){
			samplingRate = 1;
		}
		
		
		if(createAction.equals("true")){
			
			AbstractServiceManager.getInstance().createServiceInstance(serviceNameTxtBx, Arrays.asList(tagsCSVtxtbx.split(",")), gatewayList, capabilities, encryption, alloDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify,continuationFlg);
		} else{
			String serviceInstanceId = req.getParameter("serviceInstanceId"); 
			AbstractServiceManager.getInstance().updateServiceInstance(Integer.parseInt(serviceInstanceId), serviceNameTxtBx, Arrays.asList(tagsCSVtxtbx.split(",")), gatewayList, capabilities, encryption, alloDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify,continuationFlg);
		}
		
		
        
        String nextJSP = "/roleVSP/GetServiceInstanceListAction";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);
		
	}
	
	

}
