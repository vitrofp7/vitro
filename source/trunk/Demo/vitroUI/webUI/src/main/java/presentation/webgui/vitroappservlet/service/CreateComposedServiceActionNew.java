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
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager;
import vitro.vspEngine.service.common.abstractservice.AbstractGatewayManager;
import vitro.vspEngine.service.common.abstractservice.AbstractServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;
import vitro.vspEngine.service.persistence.DBSmartNodeOfGateway;

/**
 * To replace the old CreateServiceInstanceAction class
 *
 *  Define a new (composite) service from the interface and deploy it too (or call the methods/class to do that)
 */
public class CreateComposedServiceActionNew extends HttpServlet{

    private static final String PARAM_GATEWAY_ID_LIST = "gatewayIdList";
    private static final String PARAM_NODES_SELECTION_ID_LIST = "nodesIdsList";
    private static final String PARAM_REGION_SELECTION_LIST = "nodesIdsList";

    private Logger logger = Logger.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext application = getServletConfig().getServletContext();

        HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();


        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        infoGWHM = ssUN.getGatewaysToSmartDevsHM();

        Collection<GatewayWithSmartNodes> gatewayIdList = infoGWHM.values();


        req.setAttribute(PARAM_GATEWAY_ID_LIST, gatewayIdList);    // ???? TODO: To be removed ?

        String createAction = req.getParameter("create");

        String serviceNameTxtBx = req.getParameter("compositeServiceName");
        String tagsCSVtxtbx = req.getParameter("tagsCSVtxtbx");    // TODO: add this as a hidden field or abstract it



        //String[] gateways = req.getParameterValues("gateways");
        //String[] involvedCaps = req.getParameterValues("InvolvedCaps");
        //String[] functions = req.getParameterValues("functions");
        // iterate through the defined partial services and create service instance definitions.
        // Each partial service has selections of specific nodes/whole gateways or regions, a selection of capabilities applied to these selections
        // and a selection of functions to be applied to these capabilities.
        // NOTE: Our Service Instance supports 1 node selection, 1 selection of gateways and 1 selection of regions, and the associated set of capabilities (which include the functions)
        //      will be applied to all these selections
        // TODO: service instance should be connected to selections of nodes with different IDs
        //                              -->>-- to selections of gateways with different IDs
        //                              -->>-- to selections of regions with different IDs
        //                              -->>-- selections of capabilities (with their functions) LINKED to selections of nodes/regions/gateways
        //      The backend (the service content definition) supports more flexibility (multiple node selections, and different capabilities per selection, as well as compressing the functions list to the unique ones).
        // From the UI the Service Instance (in our DB) corresponds to the partial service (all rows) defined from the partial services table.
        //  uniqSubServiceCompId[]
        //  serviceCompPartNodes[]
        //  serviceCompPartFuncts[]
        //  serviceCompPartCaps[]
        //  serviceCompPartThresh[]
        //  serviceCompPartFreq[]

        String[] newUI_serviceInstanceIdsForGrouping = req.getParameterValues("uniqSubServiceCompId[]");
        String[] newUI_nodeSelectionForServiceInstance = req.getParameterValues("serviceCompPartNodes[]");
        String[] newUI_functionForSelectedCapabilities = req.getParameterValues("serviceCompPartFuncts[]");
        String[] newUI_selectedCapabilityForInstance = req.getParameterValues("serviceCompPartCaps[]");
        String[] newUI_thresholdForCapability = req.getParameterValues("serviceCompPartThresh[]");
        String[] newUI_frequencyForCapability = req.getParameterValues("serviceCompPartFreq[]");



        //String[] involvedCaps = req.getParameterValues("rulCap[]");
        //String[] functions = req.getParameterValues("rulFunctSens[]");
        //String[] rulFunctAct = req.getParameterValues("rulFunctAct[]");
//        String[] rulFunctThresh = req.getParameterValues("rulFunctThresh[]");
//        String[] rulBoundSens = req.getParameterValues("rulBoundSens[]");
//        String[] rulBoundAct = req.getParameterValues("rulBoundAct[]");
//
//        String[] rulHasTrigger = req.getParameterValues("rulHasTrigger[]");
//        String[] rulTrigCond = req.getParameterValues("rulTrigCond[]");
//        String[] rulTrigBound = req.getParameterValues("rulTrigBound[]");
//        String[] rulTrigAct = req.getParameterValues("rulTrigAct[]");
//        String[] rulTrigCapsAct = req.getParameterValues("rulTrigCapsAct[]");
//        String[] rulTrigVal = req.getParameterValues("rulTrigVal[]");
//        String[] rulTrigBoundAct = req.getParameterValues("rulTrigBoundAct[]");
//        String[] rulTrigNodesAct = req.getParameterValues("rulTrigNodesAct[]");

        //"true" when checked, null otherwise
//        String allowDTNCheck = req.getParameter("allowDTNCxBx");
        String allowDTNCheck = req.getParameter("selectDTN"); // TODO: why string?
        String requireContinuationCheck = req.getParameter("selectCont");

        //"true" when checked, null otherwise
        //String encryptionCheck = req.getParameter("encryptionCxBx");
        String encryptionCheck = null; // means false by default.

        //"true" when checked, null otherwise
        //String trackingCheck = req.getParameter("trackingCxBx");
        String trackingCheck = null;  // means false by default.

        //"true" when checked, null otherwise
        //String compositionCheck = req.getParameter("compositionCxBx");
        String compositionCheck = null;   // means false by default. TODO: This is probably obsolete now

        //"true" when checked, null otherwise
        //String rulesANDforNotifyCheck = req.getParameter("rulesANDforNotifyCxBx");
        String rulesANDforNotifyCheck = "true";// by default when ALL conditions are matched, then a special/extra notification is sent (in addition to the ones that notify about the partial alerts)

        //String slaMessage = req.getParameter("slaMessage");
        String slaMessage = "EULA/SLA text here";

        String subscriptionValue = req.getParameter("subscriptionRadio");

        // TODO: set it as the minimum of all sampling rates defined (for the composed service). Keep the value for the separate instances (?)
        // String samplingRateValue = req.getParameter("samplingRate");
        String samplingRateValue =Integer.toString(Capability.defaultSamplingPeriod) ;// todo: Temporary for debug

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

//        if(gateways != null){
//            for(int i = 0; i < gateways.length;i++){
//                logger.debug("gateways[" + i + "] " + gateways[i]);
//            }
//        }
        boolean globalAllowDTN = false;
        boolean globalContinuation = false;
        boolean globalAsynchronous  = false;
        boolean globalEncryption   = false;

        int numOfPartialServices = 0;
        HashMap<String, String>  partialServiceToNumOfCaps = new HashMap<String, String>();
        if(newUI_serviceInstanceIdsForGrouping!= null){
            //TODO: how many subServiceIDs, how many capabilities per service ID
            int oldServiceInstanceId = -1;
            int currServiceInstanceId = -1;
            int startPoint = 0;
            int numofCapsInServ = 0;
            for(int i = 0; i < newUI_serviceInstanceIdsForGrouping.length;i++){
                logger.debug("subserviceID[" + i + "] " + newUI_serviceInstanceIdsForGrouping[i]);
                logger.debug("nodeSelection[" + i + "] " + newUI_nodeSelectionForServiceInstance[i]);
                logger.debug("function[" + i + "] " + newUI_functionForSelectedCapabilities[i]);
                logger.debug("capability[" + i + "] " + newUI_selectedCapabilityForInstance[i]);
                logger.debug("threshold[" + i + "] " + newUI_thresholdForCapability[i]);
                logger.debug("frequency[" + i + "] " + newUI_frequencyForCapability[i]);

                numofCapsInServ++;
                // We assume that serviceIds are stored in continuous batches (not distributed in the table)
                try{
                    oldServiceInstanceId = Integer.parseInt(newUI_serviceInstanceIdsForGrouping[i]);
                } catch(NumberFormatException ex){
                    oldServiceInstanceId = -1;
                }
                //should not happen
                if(oldServiceInstanceId == -1) //invalid value
                    break;                // get out

                logger.debug("instance Partial Id: " + Integer.toString(oldServiceInstanceId));


                if(currServiceInstanceId == -1 || currServiceInstanceId !=  oldServiceInstanceId ) {
                    // update previous entry (if exists)
                    if(currServiceInstanceId > -1) {
                        partialServiceToNumOfCaps.put(newUI_serviceInstanceIdsForGrouping[startPoint], Integer.toString(numofCapsInServ-1) + ","+ Integer.toString(startPoint)  );
                        numOfPartialServices++;
                  //      logger.debug("partial Service: " + newUI_serviceInstanceIdsForGrouping[startPoint] + " numOfCaps: " + Integer.toString(numofCapsInServ-1) + " startPoint: "+ Integer.toString(startPoint) );
                        numofCapsInServ=0;
                    }
                    // begins a new Partial Service
                    currServiceInstanceId = oldServiceInstanceId;
                    startPoint = i;
                    partialServiceToNumOfCaps.put(newUI_serviceInstanceIdsForGrouping[i], Integer.toString(numofCapsInServ) + ","+ Integer.toString(startPoint)  );
                    numofCapsInServ=1;

                }
            }
            // we need an extra call for this at the end
            if(currServiceInstanceId > -1) {
                partialServiceToNumOfCaps.put(newUI_serviceInstanceIdsForGrouping[startPoint], Integer.toString(numofCapsInServ) + ","+ Integer.toString(startPoint)  );
                numOfPartialServices++;
                //logger.debug("partial Service: " + newUI_serviceInstanceIdsForGrouping[startPoint] + " numOfCaps: " + Integer.toString(numofCapsInServ) + " startPoint: "+ Integer.toString(startPoint) );
            }


            int numOfInvolvedCapabilities = newUI_serviceInstanceIdsForGrouping.length;
            List<ServiceInstance> allServiceInstances = new ArrayList<ServiceInstance>(numOfPartialServices );
            //List<Capability> capabilities = new ArrayList<Capability>(numOfInvolvedCapabilities);

            // LOOP for all partial services
            for (Map.Entry<String, String> partialServiceToNumOfCapsHMEntry : partialServiceToNumOfCaps.entrySet()) {
                String subServId = partialServiceToNumOfCapsHMEntry.getKey();
                String numofCapsInServ_StartPoint = partialServiceToNumOfCapsHMEntry.getValue();
                String[] tokenArr = numofCapsInServ_StartPoint.split(",");

                if(tokenArr!=null && tokenArr.length==2)
                {

                    int numOfCaps = Integer.parseInt(tokenArr[0]);
                    int startPointAct = Integer.parseInt(tokenArr[1]);
                    List<Capability> capabilitiesForPartialService = new ArrayList<Capability>(numOfCaps);

                    //logger.debug("------ LOOPING THROUGH partial Service: " +   subServId);
                    //logger.debug("------ Caps Num: " +   Integer.toString(numOfCaps));
                    //logger.debug("------ startPointAct: " +   Integer.toString(startPointAct));

                    for(int i = startPointAct; i < startPointAct+numOfCaps;i++){
                        // TODO: needs check for null!    and check for actuating capability functions or sensing
                        String selFunct = "LAST" ; //default value      TODO: get valid function names
                        String selTrigThreshValue = "" ; //default value
                        //logger.debug("++++ LOOPING THROUGH Caps");
                        if(newUI_functionForSelectedCapabilities!=null && newUI_functionForSelectedCapabilities.length > i && !newUI_functionForSelectedCapabilities[i].isEmpty()){
                            selFunct = newUI_functionForSelectedCapabilities[i];
                            selTrigThreshValue =  newUI_thresholdForCapability[i];
                        }
                        // TODO: later support actuation from the UI. For now, it's removed
                        Capability currentCapability = new Capability();
                        currentCapability.setFunction(selFunct);

                        currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
                        currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

                        if(selTrigThreshValue !=null && !selTrigThreshValue.trim().isEmpty()) {
                            currentCapability.setHasTrigger(Capability.WITH_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
                            currentCapability.setTriggerConditionSign(Capability.defaultTriggerConditionSign); // default is greater than (or equal implied)
                            currentCapability.setTriggerConditionValue(selTrigThreshValue.trim());
                            currentCapability.setTriggerAction(Capability.defaultTriggerAction); // should be set only if we have a trigger! Default is notifyAndContinue

                        }
                        else {
                            currentCapability.setHasTrigger(Capability.WITHOUT_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
                            currentCapability.setTriggerConditionSign(Capability.NO_TRIGGER_CONDITION_SIGN); //
                            currentCapability.setTriggerConditionValue(Capability.NO_THRESHOLD_VALUE);
                            currentCapability.setTriggerAction(Capability.NO_TRIGGER_ACTION); // should be set only if we have a trigger!

                        }

                        currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
                        currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
                        currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
                        currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

                        currentCapability.setName(newUI_selectedCapabilityForInstance[i]);
                        //set the involved nodes selection
                        List<DBSelectionOfSmartNodes> selectionsListOfSmartNodes = new  ArrayList<DBSelectionOfSmartNodes>(1);// the ui currently supports one selection per capability (from map)
                        DBSelectionOfSmartNodes aSelection = new DBSelectionOfSmartNodes();
                        int explicitNodesInSelection = 0;
                        // TODO split the value of on comma and then on '::##::' to get gatewayId and nodeId (within gateway)
                        String unparsedNodeSelection = newUI_nodeSelectionForServiceInstance[i];
                        String[] unparsedNodeSelectionArray = unparsedNodeSelection.split(",");
                        Vector<DBSmartNodeOfGateway> tmpVecofSms = new Vector<DBSmartNodeOfGateway>();
                        if(unparsedNodeSelectionArray!=null && unparsedNodeSelectionArray.length > 0)
                        {
                            for(int k1= 0; k1 < unparsedNodeSelectionArray.length; k1++ )
                            {
                                String[] gatewayIdNodeId = unparsedNodeSelectionArray[k1].split("::##::");
                                if(gatewayIdNodeId!=null && gatewayIdNodeId.length==2)
                                {
                                    //gatewayIdNodeId[0];//gateId
                                    //gatewayIdNodeId[1];//nodeId
                                    DBSmartNodeOfGateway tmpNode = new DBSmartNodeOfGateway();
                                    // TODO get from DAO the specific DBRegisteredGateway object
                                    // TODO maybe move this inside a ServiceManager
                                    DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(gatewayIdNodeId[0]);
                                    if(tmpDbRGw!=null)
                                    {
                                        tmpNode.setParentGateWay(tmpDbRGw);
                                        tmpNode.setIdWithinGateway(gatewayIdNodeId[1]);
                                        tmpVecofSms.add(tmpNode);
                                        explicitNodesInSelection++;
                                    }
                                }
                            }
                        }

                        List<DBSmartNodeOfGateway> nodesInGateway = new ArrayList<DBSmartNodeOfGateway>(tmpVecofSms);
                        aSelection.setDBSmartNodeOfGatewayList(nodesInGateway);
                        selectionsListOfSmartNodes.add(aSelection);

                        currentCapability.setDBSelectionOfSmartNodesList(selectionsListOfSmartNodes);
                        capabilitiesForPartialService.add(currentCapability);

                    }   //end of for loop over all capability rows for this service
                    slaMessage = slaMessage == null ? null : slaMessage.trim();
                    boolean allowDTN = (allowDTNCheck == null || allowDTNCheck.equalsIgnoreCase("false"))? false : true;
                    globalAllowDTN = allowDTN;
                    boolean encryption = encryptionCheck == null ? false : true;
                    globalEncryption = encryption;
                    boolean rfidTracking = trackingCheck == null ? false : true;
                    boolean composition = compositionCheck == null ? false : true;
                    boolean rulesANDforNotify = rulesANDforNotifyCheck  == null ? false : true;
                    boolean continuation = (requireContinuationCheck == null || requireContinuationCheck.equalsIgnoreCase("false"))? false : true;
                    globalContinuation = continuation;

                    //		List<String> involvedCapList = involvedCaps == null ? null : Arrays.asList(involvedCaps);

                    // This is a very verbose list of all node selection sets. It could be possibly trimmed (perhaps in the service Definition)?
                    List<String> selectionOfnodesOfgatewaysList = newUI_nodeSelectionForServiceInstance == null ? null : Arrays.asList(newUI_nodeSelectionForServiceInstance);
                    List<String> selectionOfgatewaysList = null;
                    List<String> selectionOfregionsList = null;;

                    boolean subscription = subscriptionValue == null || subscriptionValue.trim().equals("false") ? false : true;
                    globalAsynchronous = subscription;
                    long samplingRate = 0;


                    try{
                        samplingRate = Long.parseLong(samplingRateValue);
                    } catch(NumberFormatException ex){
                        samplingRate = Capability.defaultSamplingPeriod;       // TODO: this is somewhat counter-intuitive (Period is the inverse of rate)
                    }
                    //logger.debug("Before NEW instance");
                    // check whether to create or update
                    // TODO: first handle the creation (definition) (and deployment)
                    //if(createAction.equals("true")){

                    int retId = AbstractServiceManager.getInstance().createServiceInstanceReturnId(serviceNameTxtBx+"_"+subServId, Arrays.asList(tagsCSVtxtbx.split(",")), new ArrayList<String>(0), capabilitiesForPartialService, encryption, allowDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, globalContinuation);
                    //}  TODO: if create the serviceInstance via the AbstractServiceManager, would it be better? could we re-use the definitions even within the same composed service?
//                    ServiceInstance currServiceInstance = new ServiceInstance();
//
//                    currServiceInstance.setName(serviceNameTxtBx+"_"+subServId);
//                    currServiceInstance.setSearchTagList(Arrays.asList(tagsCSVtxtbx.split(",")));
//                    currServiceInstance.setObservedCapabilities(capabilitiesForPartialService);
//
//                    currServiceInstance.setAllowDTN(allowDTN);
//                    currServiceInstance.setEncryption(encryption);
//                    currServiceInstance.setRfidTracking(rfidTracking);
//                    currServiceInstance.setComposition(composition);
//                    currServiceInstance.setSlaMessage(slaMessage);
//                    currServiceInstance.setSubscriptionEnabled(subscription);
//                    currServiceInstance.setSamplingRate(samplingRate);
//                    currServiceInstance.setRulesANDforNotify(rulesANDforNotify);
//                    // TODO: or we could do something more elegant here (computing a list of involved gateways ?)
//                    currServiceInstance.setGatewayList(null);
                    ServiceInstance currServiceInstance = AbstractServiceManager.getInstance().getServiceInstance(retId);
                    if(currServiceInstance!=null){
                        allServiceInstances.add(currServiceInstance);
                        //logger.debug("adding NEW instance");
                    }else {
                        logger.debug("Error - NOT FOUND NEW instance");
                    }
                }
            }  //ends the loop on all partial services
            // TODO finally create the definition of the composite service!
            if(createAction.equals("true")){
                AbstractComposedServiceManager.getInstance().createComposedService(serviceNameTxtBx, serviceNameTxtBx, Arrays.asList(tagsCSVtxtbx.split(",")), allServiceInstances, globalAllowDTN, false, "",globalContinuation, globalEncryption,globalAsynchronous);
            }

        }   //end of if we have defined partial services



        //
        // TODO: for now, we won't proceed to the management page
        //

        String nextJSP = "/roleEndUser/newservice.jsp";
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(nextJSP);
        dispatcher.forward(req,resp);

    }



}

