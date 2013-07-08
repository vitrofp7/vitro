<%--
  ~ #--------------------------------------------------------------------------
  ~ # Copyright (c) 2013 VITRO FP7 Consortium.
  ~ # All rights reserved. This program and the accompanying materials
  ~ # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
  ~ # http://www.gnu.org/licenses/lgpl-3.0.html
  ~ #
  ~ # Contributors:
  ~ #     Antoniou Thanasis (Research Academic Computer Technology Institute)
  ~ #     Paolo Medagliani (Thales Communications & Security)
  ~ #     D. Davide Lamanna (WLAB SRL)
  ~ #     Alessandro Leoni (WLAB SRL)
  ~ #     Francesco Ficarola (WLAB SRL)
  ~ #     Stefano Puglia (WLAB SRL)
  ~ #     Panos Trakadas (Technological Educational Institute of Chalkida)
  ~ #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
  ~ #     Andrea Kropp (Selex ES)
  ~ #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
  ~ #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
  ~ #
  ~ #--------------------------------------------------------------------------
  --%>

<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.FullComposedService" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.ServiceInstance" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSmartNodeOfGateway" %>
<%@ page import="vitro.vspEngine.service.persistence.DBRegisteredGateway" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractGatewayManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractServiceManager" %>
<%@ page import="vitro.vspEngine.service.persistence.DBSelectionOfGateways" %>
<%@ page import="org.apache.log4j.Logger" %>
<%

    Logger logger = Logger.getLogger(this.getClass());

    String xmerrordescr="";
    int errno = 0;

    // form field parameters
    String[] allAvailGateways;
    String[] guiSelectedGateways;
    String[] guiSelectedSmartDevs;
    String[] guiIndxGatewayToSelectedSmartDevs;
    int PeriodSlctd;
    int HistNumSlctd;
    boolean AggrSlctd;
    String actuationValueSetStr;

    String serviceId = "-1";
    String friendName = "";
    serviceId = request.getParameter("pid");
    friendName = request.getParameter("pfriend");


    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    // ----------------------------------------------------------------------

    // AVG temperature in Patras and Rome
    if ( serviceId.equalsIgnoreCase("pre0"))
    {
        // TODO do we need a cross-gateway function AVG, too?
        // first check for the existence of this composite service.
        //get List of Composed Services, check for predeployed == true and predeployedId ==serviceId
        int theStoredComposedServiceId =0;
        FullComposedService theStoredComposedService = null;
        try {
            AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
            List<FullComposedService> theStoredComposedServicesList = manager.getComposedServiceList()   ;
            if(theStoredComposedServicesList!=null) {
                for(FullComposedService itStoredCompServ: theStoredComposedServicesList){
                       if(itStoredCompServ.isPredeployed() && itStoredCompServ.getPredeployedId().equalsIgnoreCase(serviceId))
                       {
                           theStoredComposedService = itStoredCompServ;
                           theStoredComposedServiceId = theStoredComposedService.getId();
                           break;
                       }
                }
            }

        }catch(Exception ex1)
        {
            theStoredComposedService = null;
        }
        if(theStoredComposedService == null)
        {
            String tagsCSVtxt = "Patras,Rome,AVG,Temperature";
            int numOfPartialServices = 1; //should be one with one capability (or two capabilities maybe?)
            List<ServiceInstance> allServiceInstances = new ArrayList<ServiceInstance>();
            int numOfCaps = 1;
            List<Capability> capabilitiesForPartialService = new ArrayList<Capability>(numOfCaps);
            Capability currentCapability = new Capability();
            currentCapability.setFunction("AVG");

            currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
            currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

            currentCapability.setHasTrigger(Capability.WITHOUT_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
            currentCapability.setTriggerConditionSign(Capability.NO_TRIGGER_CONDITION_SIGN); //
            currentCapability.setTriggerConditionValue(Capability.NO_THRESHOLD_VALUE);
            currentCapability.setTriggerAction(Capability.NO_TRIGGER_ACTION); // should be set only if we have a trigger!

            currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
            currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
            currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
            currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

            currentCapability.setName("urn:x-ogc:def:phenomenon:IDAS:1.0:temperature");
            //set the involved nodes selection
            List<DBSelectionOfGateways> selectionsListOfGateways = new  ArrayList<DBSelectionOfGateways>(1);
            DBSelectionOfGateways aSelection = new DBSelectionOfGateways();

            List<DBRegisteredGateway> gatewayList = new ArrayList<DBRegisteredGateway>();
            DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_cti");
            if(tmpDbRGw!=null)
            {
                gatewayList.add(tmpDbRGw);
                logger.debug(" ##################### ADDING GWID: " + Integer.toString(tmpDbRGw.getIdregisteredGateway()) );
            }
            tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_wlab");
            if(tmpDbRGw!=null)
            {
                gatewayList.add(tmpDbRGw);
                logger.debug(" ##################### ADDING GWID: " + Integer.toString(tmpDbRGw.getIdregisteredGateway()) );
            }

            aSelection.setDBRegisteredGatewayList(gatewayList);
            selectionsListOfGateways.add(aSelection);

            currentCapability.setDBSelectionOfGatewaysList(selectionsListOfGateways);

            capabilitiesForPartialService.add(currentCapability);

            String slaMessage = null;
            boolean allowDTN = false ;
            boolean encryption = false;
            boolean rfidTracking = false;
            boolean composition = true;
            boolean rulesANDforNotify = false ;
            boolean continuation = false;

            // This is a very verbose list of all node selection sets. It could be possibly trimmed (perhaps in the service Definition)?
            List<String> selectionOfnodesOfgatewaysList = null;
            List<String> selectionOfgatewaysList = null;
            List<String> selectionOfregionsList = null;;

            boolean subscription = false ;
            long samplingRate =  Capability.defaultSamplingPeriod;       // TODO: this is somewhat counter-intuitive (Period is the inverse of rate)

            int retId = AbstractServiceManager.getInstance().createServiceInstanceReturnId(serviceId+"_1", Arrays.asList(tagsCSVtxt.split(",")), new ArrayList<String>(0), capabilitiesForPartialService, encryption, allowDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuation);

            ServiceInstance currServiceInstance = AbstractServiceManager.getInstance().getServiceInstance(retId);
            if(currServiceInstance!=null){
                allServiceInstances.add(currServiceInstance);
                // Store in DB
                int compSrvRetId = AbstractComposedServiceManager.getInstance().createComposedServiceReturnId(serviceId, friendName, Arrays.asList(tagsCSVtxt.split(",")), allServiceInstances, false, true, serviceId,continuation, encryption, subscription);
                FullComposedService theNewlyPreService = AbstractComposedServiceManager.getInstance().getComposedService(compSrvRetId);
                if(theNewlyPreService != null)
                {
                    theStoredComposedServiceId = compSrvRetId;
                } else {
                    // error
                    xmerrordescr ="Could not find newly created (preset) VSN in DB!";
                    errno = 1;
                }

            }else {
               // error
                xmerrordescr ="Could not create new (preset) VSN in DB!";
                errno = 1;
            }
        }
        // a valid stored composed service id is > 0 either pre-existing or just created.
        if(theStoredComposedServiceId>0)
        {
            //deploy the stored Service
            IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
            QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDef(ssUN, theStoredComposedServiceId);
            if(qdef == null)
            {
                xmerrordescr ="Unable to create new (preset) VSN!";
                errno = 1;
            }
            else
            {
                PeriodSlctd = Capability.defaultSamplingPeriod;
                HistNumSlctd = 0;
                AggrSlctd = true;
                qdef.setFriendlyName(friendName);
                qdef.setDesiredPeriod(PeriodSlctd);
                qdef.setDesiredHistory(HistNumSlctd);
                qdef.setAggregateQueryFlag(AggrSlctd);
                errno = 0;
            }
        }
    }
    // ----------------------------------------------------------------------

    // Fire Early Warning in CTI. (new) 2 Conditions (queried specific motes)
    else if( serviceId.equalsIgnoreCase("pre1") )
    {
        //new code, now stores the triggers in the DB.
        // first check for the existence of this composite service.
        //get List of Composed Services, check for predeployed == true and predeployedId ==serviceId
        int theStoredComposedServiceId =0;
        FullComposedService theStoredComposedService = null;
        try {
            AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
            List<FullComposedService> theStoredComposedServicesList = manager.getComposedServiceList()   ;
            if(theStoredComposedServicesList!=null) {
                for(FullComposedService itStoredCompServ: theStoredComposedServicesList){
                    if(itStoredCompServ.isPredeployed() && itStoredCompServ.getPredeployedId().equalsIgnoreCase(serviceId))
                    {
                        theStoredComposedService = itStoredCompServ;
                        theStoredComposedServiceId = theStoredComposedService.getId();
                        break;
                    }
                }
            }

        }catch(Exception ex1)
        {
            theStoredComposedService = null;
        }
        if(theStoredComposedService == null)
        {
            String tagsCSVtxt = "Patras,CTI,Fire Detection,Early Warning";
            int numOfPartialServices = 1; //should be one with one capability (or two capabilities maybe?)
            List<ServiceInstance> allServiceInstances = new ArrayList<ServiceInstance>();
            int numOfCaps = 1;
            List<Capability> capabilitiesForPartialService = new ArrayList<Capability>(numOfCaps);
            // We will need 2 Capabilities.
            // CAPABILITY 1:             temperature ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            Capability currentCapability = new Capability();
            currentCapability.setFunction("Last");

            currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
            currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

            currentCapability.setHasTrigger(Capability.WITH_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
            currentCapability.setTriggerConditionSign(Capability.defaultTriggerConditionSign); //
            currentCapability.setTriggerConditionValue("300"); //35 in Celsius
            currentCapability.setTriggerAction(Capability.defaultTriggerAction); // should be set only if we have a trigger!

            currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
            currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
            currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
            currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

            currentCapability.setName("urn:x-ogc:def:phenomenon:IDAS:1.0:temperature");
            //set the involved nodes selection

            List<DBSelectionOfSmartNodes> selectionsListOfSmartNodes = new  ArrayList<DBSelectionOfSmartNodes>(1);// the ui currently supports one selection per capability (from map)
            DBSelectionOfSmartNodes aSelection = new DBSelectionOfSmartNodes();
            Vector<DBSmartNodeOfGateway> tmpVecofSms = new Vector<DBSmartNodeOfGateway>();

            DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_cti");
            if(tmpDbRGw!=null)
            {
                DBSmartNodeOfGateway tmpNode = new DBSmartNodeOfGateway();
                tmpNode.setParentGateWay(tmpDbRGw);
                tmpNode.setIdWithinGateway("urn:wisebed:ctitestbed:0x786a");
                tmpVecofSms.add(tmpNode);

                tmpNode = new DBSmartNodeOfGateway();
                tmpNode.setParentGateWay(tmpDbRGw);
                tmpNode.setIdWithinGateway("urn:wisebed:ctitestbed:0x295");
                tmpVecofSms.add(tmpNode);

                tmpNode = new DBSmartNodeOfGateway();
                tmpNode.setParentGateWay(tmpDbRGw);
                tmpNode.setIdWithinGateway("urn:wisebed:ctitestbed:0x42f");
                tmpVecofSms.add(tmpNode);
            }

            List<DBSmartNodeOfGateway> nodesInGateway = new ArrayList<DBSmartNodeOfGateway>(tmpVecofSms);
            aSelection.setDBSmartNodeOfGatewayList(nodesInGateway);
            selectionsListOfSmartNodes.add(aSelection);
            currentCapability.setDBSelectionOfSmartNodesList(selectionsListOfSmartNodes);

            capabilitiesForPartialService.add(currentCapability);
            // CAPABILITY 2:             luminousIntensity ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            currentCapability = new Capability();
            currentCapability.setFunction("Last");

            currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
            currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

            currentCapability.setHasTrigger(Capability.WITH_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
            currentCapability.setTriggerConditionSign(Capability.defaultTriggerConditionSign); //
            currentCapability.setTriggerConditionValue("20");//Threshold for luminosity (something that is met)
            currentCapability.setTriggerAction(Capability.defaultTriggerAction); // should be set only if we have a trigger!

            currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
            currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
            currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
            currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

            currentCapability.setName("urn:x-ogc:def:phenomenon:IDAS:1.0:luminousIntensity");
            //set the involved nodes selection
            aSelection = new DBSelectionOfSmartNodes();
            tmpVecofSms = new Vector<DBSmartNodeOfGateway>();

            tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_cti");
            if(tmpDbRGw!=null)
            {
                DBSmartNodeOfGateway tmpNode = new DBSmartNodeOfGateway();
                tmpNode.setParentGateWay(tmpDbRGw);
                tmpNode.setIdWithinGateway("urn:wisebed:ctitestbed:0x786a");
                tmpVecofSms.add(tmpNode);

                tmpNode = new DBSmartNodeOfGateway();
                tmpNode.setParentGateWay(tmpDbRGw);
                tmpNode.setIdWithinGateway("urn:wisebed:ctitestbed:0x295");
                tmpVecofSms.add(tmpNode);

                tmpNode = new DBSmartNodeOfGateway();
                tmpNode.setParentGateWay(tmpDbRGw);
                tmpNode.setIdWithinGateway("urn:wisebed:ctitestbed:0x42f");
                tmpVecofSms.add(tmpNode);
            }
            selectionsListOfSmartNodes = new  ArrayList<DBSelectionOfSmartNodes>(1);
            nodesInGateway = new ArrayList<DBSmartNodeOfGateway>(tmpVecofSms);
            aSelection.setDBSmartNodeOfGatewayList(nodesInGateway);
            selectionsListOfSmartNodes.add(aSelection);
            currentCapability.setDBSelectionOfSmartNodesList(selectionsListOfSmartNodes);

            capabilitiesForPartialService.add(currentCapability);

            // End Of Capabilities Selections     @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            String slaMessage = null;
            boolean allowDTN = false ;
            boolean encryption = false;
            boolean rfidTracking = false;
            boolean composition = true;
            boolean rulesANDforNotify = false ;
            boolean continuation = false;

            // This is a very verbose list of all node selection sets. It could be possibly trimmed (perhaps in the service Definition)?
            List<String> selectionOfnodesOfgatewaysList = null;
            List<String> selectionOfgatewaysList = null;
            List<String> selectionOfregionsList = null;;

            boolean subscription = false ;
            long samplingRate =  Capability.defaultSamplingPeriod;       // TODO: this is somewhat counter-intuitive (Period is the inverse of rate)

            int retId = AbstractServiceManager.getInstance().createServiceInstanceReturnId(serviceId+"_1", Arrays.asList(tagsCSVtxt.split(",")), new ArrayList<String>(0), capabilitiesForPartialService, encryption, allowDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuation);

            ServiceInstance currServiceInstance = AbstractServiceManager.getInstance().getServiceInstance(retId);
            if(currServiceInstance!=null){
                allServiceInstances.add(currServiceInstance);
                // Store in DB
                int compSrvRetId = AbstractComposedServiceManager.getInstance().createComposedServiceReturnId(serviceId, friendName, Arrays.asList(tagsCSVtxt.split(",")), allServiceInstances, false, true, serviceId,continuation, encryption, subscription);
                FullComposedService theNewlyPreService = AbstractComposedServiceManager.getInstance().getComposedService(compSrvRetId);
                if(theNewlyPreService != null)
                {
                    theStoredComposedServiceId = compSrvRetId;
                } else {
                    // error
                    xmerrordescr ="Could not find newly created (preset) VSN in DB!";
                    errno = 1;
                }

            }else {
                // error
                xmerrordescr ="Could not create new (preset) VSN in DB!";
                errno = 1;
            }
        }
        // a valid stored composed service id is > 0 either pre-existing or just created.
        if(theStoredComposedServiceId>0)
        {
            //deploy the stored Service
            IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
            QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDef(ssUN, theStoredComposedServiceId);
            if(qdef == null)
            {
                xmerrordescr ="Unable to create new (preset) VSN!";
                errno = 1;
            }
            else
            {
                PeriodSlctd = Capability.defaultSamplingPeriod;
                HistNumSlctd = 0;
                AggrSlctd = true;
                qdef.setFriendlyName(friendName);
                qdef.setDesiredPeriod(PeriodSlctd);
                qdef.setDesiredHistory(HistNumSlctd);
                qdef.setAggregateQueryFlag(AggrSlctd);
                errno = 0;
            }
        }

        // ----------------------------------------------------------------------
    }
    else if (serviceId.equalsIgnoreCase("pre2"))
    { // Exactly 3 sensors (not implemented yet)
        xmerrordescr ="This composite Service is not supported yet!";
        errno = 1;
        // ----------------------------------------------------------------------
    }
    else if (serviceId.equalsIgnoreCase("pre3"))
    {
        //Monitor Temperature in Colombes France
        // first check for the existence of this composite service.
        //get List of Composed Services, check for predeployed == true and predeployedId == serviceId
        int theStoredComposedServiceId =0;
        FullComposedService theStoredComposedService = null;
        try {
            AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
            List<FullComposedService> theStoredComposedServicesList = manager.getComposedServiceList()   ;
            if(theStoredComposedServicesList!=null) {
                for(FullComposedService itStoredCompServ: theStoredComposedServicesList){
                    if(itStoredCompServ.isPredeployed() && itStoredCompServ.getPredeployedId().equalsIgnoreCase(serviceId))
                    {
                        theStoredComposedService = itStoredCompServ;
                        theStoredComposedServiceId = theStoredComposedService.getId();
                        break;
                    }
                }
            }

        }catch(Exception ex1)
        {
            theStoredComposedService = null;
        }
        if(theStoredComposedService == null)
        {
            String tagsCSVtxt = "Colombes,France,AVG,Temperature";
            int numOfPartialServices = 1; //should be one with one capability (or two capabilities maybe?)
            List<ServiceInstance> allServiceInstances = new ArrayList<ServiceInstance>();
            int numOfCaps = 1;
            List<Capability> capabilitiesForPartialService = new ArrayList<Capability>(numOfCaps);
            Capability currentCapability = new Capability();
            currentCapability.setFunction("AVG");

            currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
            currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

            currentCapability.setHasTrigger(Capability.WITHOUT_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
            currentCapability.setTriggerConditionSign(Capability.NO_TRIGGER_CONDITION_SIGN); //
            currentCapability.setTriggerConditionValue(Capability.NO_THRESHOLD_VALUE);
            currentCapability.setTriggerAction(Capability.NO_TRIGGER_ACTION); // should be set only if we have a trigger!

            currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
            currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
            currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
            currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

            currentCapability.setName("urn:x-ogc:def:phenomenon:IDAS:1.0:temperature");
            //set the involved nodes selection
            List<DBSelectionOfGateways> selectionsListOfGateways = new  ArrayList<DBSelectionOfGateways>(1);
            DBSelectionOfGateways aSelection = new DBSelectionOfGateways();

            List<DBRegisteredGateway> gatewayList = new ArrayList<DBRegisteredGateway>();
            DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_tcs");
            if(tmpDbRGw!=null)
            {
                gatewayList.add(tmpDbRGw);
            }

            aSelection.setDBRegisteredGatewayList(gatewayList);
            selectionsListOfGateways.add(aSelection);

            currentCapability.setDBSelectionOfGatewaysList(selectionsListOfGateways);

            capabilitiesForPartialService.add(currentCapability);

            String slaMessage = null;
            boolean allowDTN = false ;
            boolean encryption = false;
            boolean rfidTracking = false;
            boolean composition = true;
            boolean rulesANDforNotify = false ;
            boolean continuation = false;

            // This is a very verbose list of all node selection sets. It could be possibly trimmed (perhaps in the service Definition)?
            List<String> selectionOfnodesOfgatewaysList = null;
            List<String> selectionOfgatewaysList = null;
            List<String> selectionOfregionsList = null;;

            boolean subscription = false ;
            long samplingRate =  Capability.defaultSamplingPeriod;       // TODO: this is somewhat counter-intuitive (Period is the inverse of rate)

            int retId = AbstractServiceManager.getInstance().createServiceInstanceReturnId(serviceId+"_1", Arrays.asList(tagsCSVtxt.split(",")), new ArrayList<String>(0), capabilitiesForPartialService, encryption, allowDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuation);

            ServiceInstance currServiceInstance = AbstractServiceManager.getInstance().getServiceInstance(retId);
            if(currServiceInstance!=null){
                allServiceInstances.add(currServiceInstance);
                // Store in DB
                int compSrvRetId = AbstractComposedServiceManager.getInstance().createComposedServiceReturnId(serviceId, friendName, Arrays.asList(tagsCSVtxt.split(",")), allServiceInstances, false, true, serviceId, continuation, encryption, subscription);
                FullComposedService theNewlyPreService = AbstractComposedServiceManager.getInstance().getComposedService(compSrvRetId);
                if(theNewlyPreService != null)
                {
                    theStoredComposedServiceId = compSrvRetId;
                } else {
                    // error
                    xmerrordescr ="Could not find newly created (preset) VSN in DB!";
                    errno = 1;
                }

            }else {
                // error
                xmerrordescr ="Could not create new (preset) VSN in DB!";
                errno = 1;
            }
        }
        // a valid stored composed service id is > 0 either pre-existing or just created.
        if(theStoredComposedServiceId>0)
        {
            //deploy the stored Service
            IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
            QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDef(ssUN, theStoredComposedServiceId);
            if(qdef == null)
            {
                xmerrordescr ="Unable to create new (preset) VSN!";
                errno = 1;
            }
            else
            {
                PeriodSlctd = Capability.defaultSamplingPeriod;
                HistNumSlctd = 0;
                AggrSlctd = true;
                qdef.setFriendlyName(friendName);
                qdef.setDesiredPeriod(PeriodSlctd);
                qdef.setDesiredHistory(HistNumSlctd);
                qdef.setAggregateQueryFlag(AggrSlctd);
                errno = 0;
            }
        }

    }
    // Fire Early Warning in Thales (uses all motes )
    else if( serviceId.equalsIgnoreCase("pre4") )
    {
        //new code, now stores the triggers in the DB.
        // first check for the existence of this composite service.
        //get List of Composed Services, check for predeployed == true and predeployedId ==serviceId
        int theStoredComposedServiceId =0;
        FullComposedService theStoredComposedService = null;
        try {
            AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
            List<FullComposedService> theStoredComposedServicesList = manager.getComposedServiceList()   ;
            if(theStoredComposedServicesList!=null) {
                for(FullComposedService itStoredCompServ: theStoredComposedServicesList){
                    if(itStoredCompServ.isPredeployed() && itStoredCompServ.getPredeployedId().equalsIgnoreCase(serviceId))
                    {
                        theStoredComposedService = itStoredCompServ;
                        theStoredComposedServiceId = theStoredComposedService.getId();
                        break;
                    }
                }
            }

        }catch(Exception ex1)
        {
            theStoredComposedService = null;
        }
        if(theStoredComposedService == null)
        {
            String tagsCSVtxt = "Thales,Colombes,France,Fire Detection,Early Warning";
            int numOfPartialServices = 1; //should be one with one capability (or two capabilities maybe?)
            List<ServiceInstance> allServiceInstances = new ArrayList<ServiceInstance>();
            int numOfCaps = 1;
            List<Capability> capabilitiesForPartialService = new ArrayList<Capability>(numOfCaps);
            // We will need 2 Capabilities.
            // CAPABILITY 1:             temperature ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            Capability currentCapability = new Capability();
            currentCapability.setFunction("Last");

            currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
            currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

            currentCapability.setHasTrigger(Capability.WITH_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
            currentCapability.setTriggerConditionSign(Capability.defaultTriggerConditionSign); //
            currentCapability.setTriggerConditionValue("300"); //35 in Celsius
            currentCapability.setTriggerAction(Capability.defaultTriggerAction); // should be set only if we have a trigger!

            currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
            currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
            currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
            currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

            currentCapability.setName("urn:x-ogc:def:phenomenon:IDAS:1.0:temperature");
            //set the involved nodes selection
            List<DBSelectionOfGateways> selectionsListOfGateways = new  ArrayList<DBSelectionOfGateways>(1);
            DBSelectionOfGateways aSelection = new DBSelectionOfGateways();

            List<DBRegisteredGateway> gatewayList = new ArrayList<DBRegisteredGateway>();
            DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_tcs");
            if(tmpDbRGw!=null)
            {
                gatewayList.add(tmpDbRGw);
            }

            aSelection.setDBRegisteredGatewayList(gatewayList);
            selectionsListOfGateways.add(aSelection);

            currentCapability.setDBSelectionOfGatewaysList(selectionsListOfGateways);

            capabilitiesForPartialService.add(currentCapability);
            // CAPABILITY 2:             luminousIntensity ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            currentCapability = new Capability();
            currentCapability.setFunction("Last");

            currentCapability.setFunctionThresholdSign(Capability.defaultThresholdSign);
            currentCapability.setFunctionThresholdValue(Capability.defaultThresholdValue);

            currentCapability.setHasTrigger(Capability.WITH_TRIGGER);   // should be set only if we have a trigger! (a threshold in the UI) or set to NO otherwise
            currentCapability.setTriggerConditionSign(Capability.defaultTriggerConditionSign); //
            currentCapability.setTriggerConditionValue("20");//Threshold for luminosity (something that is met)
            currentCapability.setTriggerAction(Capability.defaultTriggerAction); // should be set only if we have a trigger!

            currentCapability.setTriggerActuationName(Capability.defaultTriggerActuationName);
            currentCapability.setTriggerGenTextValue(Capability.defaultTriggerGenTextValue);
            currentCapability.setTriggerActuationValue(Capability.defaultTriggerActuationValue);
            currentCapability.setTriggerActuationNodes(Capability.defaultTriggerActuationNodes);

            currentCapability.setName("urn:x-ogc:def:phenomenon:IDAS:1.0:luminousIntensity");
            //set the involved nodes selection
            selectionsListOfGateways =  new  ArrayList<DBSelectionOfGateways>(1);
            aSelection = new DBSelectionOfGateways();

            gatewayList = new ArrayList<DBRegisteredGateway>();
            tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName("vitrogw_tcs");
            if(tmpDbRGw!=null)
            {
                gatewayList.add(tmpDbRGw);
            }

            aSelection.setDBRegisteredGatewayList(gatewayList);
            selectionsListOfGateways.add(aSelection);

            currentCapability.setDBSelectionOfGatewaysList(selectionsListOfGateways);

            capabilitiesForPartialService.add(currentCapability);

            // End Of Capabilities Selections     @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
            String slaMessage = null;
            boolean allowDTN = false ;
            boolean encryption = false;
            boolean rfidTracking = false;
            boolean composition = true;
            boolean rulesANDforNotify = false ;
            boolean continuation = false;

            // This is a very verbose list of all node selection sets. It could be possibly trimmed (perhaps in the service Definition)?
            List<String> selectionOfnodesOfgatewaysList = null;
            List<String> selectionOfgatewaysList = null;
            List<String> selectionOfregionsList = null;;

            boolean subscription = false ;
            long samplingRate =  Capability.defaultSamplingPeriod;       // TODO: this is somewhat counter-intuitive (Period is the inverse of rate)

            int retId = AbstractServiceManager.getInstance().createServiceInstanceReturnId(serviceId+"_1", Arrays.asList(tagsCSVtxt.split(",")), new ArrayList<String>(0), capabilitiesForPartialService, encryption, allowDTN, rfidTracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuation);

            ServiceInstance currServiceInstance = AbstractServiceManager.getInstance().getServiceInstance(retId);
            if(currServiceInstance!=null){
                allServiceInstances.add(currServiceInstance);
                // Store in DB
                int compSrvRetId = AbstractComposedServiceManager.getInstance().createComposedServiceReturnId(serviceId, friendName, Arrays.asList(tagsCSVtxt.split(",")), allServiceInstances, false, true, serviceId,continuation, encryption, subscription);
                FullComposedService theNewlyPreService = AbstractComposedServiceManager.getInstance().getComposedService(compSrvRetId);
                if(theNewlyPreService != null)
                {
                    theStoredComposedServiceId = compSrvRetId;
                } else {
                    // error
                    xmerrordescr ="Could not find newly created (preset) VSN in DB!";
                    errno = 1;
                }

            }else {
                // error
                xmerrordescr ="Could not create new (preset) VSN in DB!";
                errno = 1;
            }
        }
        // a valid stored composed service id is > 0 either pre-existing or just created.
        if(theStoredComposedServiceId>0)
        {
            //deploy the stored Service
            IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
            QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDef(ssUN, theStoredComposedServiceId);
            if(qdef == null)
            {
                xmerrordescr ="Unable to create new (preset) VSN!";
                errno = 1;
            }
            else
            {
                PeriodSlctd = Capability.defaultSamplingPeriod;
                HistNumSlctd = 0;
                AggrSlctd = true;
                qdef.setFriendlyName(friendName);
                qdef.setDesiredPeriod(PeriodSlctd);
                qdef.setDesiredHistory(HistNumSlctd);
                qdef.setAggregateQueryFlag(AggrSlctd);
                errno = 0;
            }
        }

        // ----------------------------------------------------------------------
    }
    else
    {
        int intServiceId = -1;
        try {
            intServiceId = Integer.parseInt(serviceId);
        }
        catch (Exception ex003)
        {
            xmerrordescr ="Unable to create this composite Service!";
            errno = 1;
            intServiceId = -1;
        }
        if(intServiceId > 0)
        {
            IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
            QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDef(ssUN, intServiceId);
            if(qdef == null)
            {
                xmerrordescr ="Unable to create new Query!";
                errno = 1;
            }
            else
            {
                PeriodSlctd = Capability.defaultSamplingPeriod;
                HistNumSlctd = 0;
                AggrSlctd = true;
                qdef.setFriendlyName(friendName);
                qdef.setDesiredPeriod(PeriodSlctd);
                qdef.setDesiredHistory(HistNumSlctd);
                qdef.setAggregateQueryFlag(AggrSlctd);
                errno = 0;
            }
        }

    }
    if(errno == 0)
    {
        xmerrordescr = "OK";
    }


%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <QueryDefId></QueryDefId>
    <KMLJSPFileUrl></KMLJSPFileUrl>
</Answer>

