<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.FullComposedService" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.ServiceInstance" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractCapabilityManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractServiceManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractNotificationManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Notification" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%
    Logger logger = Logger.getLogger(this.getClass());
    String xmerrordescr="";
    int errno = 0;
    String responseResult="";
    String functionSignature="";
    final String  NEW_LINE_STR="__nl__";

    String pServiceID =  "";
    String pInstanceID = "";
    String pGatewayID = "";
    String pSensorID = "";
    String pCapabilityID = "";
    String reqData = "";
    String reqParamNamesStr ="";
    String reqParamValuesStr = "";
    Enumeration<String> reqDataNames;

    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    try {
        reqData = request.getQueryString();
        reqDataNames = request.getParameterNames();

        functionSignature = request.getParameter("fid");
        pServiceID = request.getParameter("pServiceID");
        pInstanceID = request.getParameter("pInstanceID");
        pGatewayID = request.getParameter("pGatewayID");
        pSensorID = request.getParameter("pSensorID");
        pCapabilityID = request.getParameter("pCapabilityID");
    }catch(Exception ex) {
        errno = 1;
        xmerrordescr="Error in post data format!";
        functionSignature = "";
        reqDataNames = null;
        pServiceID ="";
        pInstanceID =  "";
        pGatewayID = "";
        pSensorID =  "";
        pCapabilityID =  "";
    }
    if(reqDataNames !=null )
    {
        while (reqDataNames.hasMoreElements()) {
            String parName =  reqDataNames.nextElement();
            // process element
            reqParamNamesStr += parName + ",";

        }
        //remove trailing comma
        reqParamNamesStr = reqParamNamesStr.replaceAll("\\s*,\\s*$", "");
    }

    if(functionSignature!=null && !functionSignature.isEmpty()) {
        //
        // getComposedServiceList(callback)
        // returns: compoServiceDBId,serviceUniqueId,serviceName,isDeployed,isRunning\n
        if(functionSignature.compareToIgnoreCase("getComposedServiceList") == 0) {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            //no parameters for this function
            reqParamValuesStr=reqParamValsStrBld.toString();
            List<FullComposedService> reqCompoServiceList = AbstractComposedServiceManager.getInstance().getComposedServiceList();
            if(reqCompoServiceList != null)
            {
                AbstractComposedServiceManager compoSrvcManager =  AbstractComposedServiceManager.getInstance();
                IndexOfQueries myIndexOfQueries = IndexOfQueries.getIndexOfQueries();

                StringBuilder resStrBld = new StringBuilder();
                for (FullComposedService reqCompoServiceTmpIter : reqCompoServiceList)
                {
                    FullComposedService comboServiceTmp = compoSrvcManager.getComposedService(reqCompoServiceTmpIter.getId());
                    //
                    StringBuilder uQueryDefIdBuild = new StringBuilder();
                    uQueryDefIdBuild.append(IndexOfQueries.COMPOSED_DB_PREFIX);
                    uQueryDefIdBuild.append(comboServiceTmp.getId());
                    String uQueryDefId =uQueryDefIdBuild.toString();
                    resStrBld.append(comboServiceTmp.getId());
                    resStrBld.append(",");
                    resStrBld.append(uQueryDefId);
                    resStrBld.append(",");
                    resStrBld.append(comboServiceTmp.getFriendlyName());
                    resStrBld.append(",");
                    // find if it is deployed and running (or paused)
                    if(myIndexOfQueries.getAllQueriesDefHashMap().containsKey(uQueryDefId )) {
                        resStrBld.append(1);
                        resStrBld.append(",");
                        if(myIndexOfQueries.getAllQueriesDefHashMap().get(uQueryDefId).getRunningStatus() == QueryDefinition.STATUS_RUNNING){
                            resStrBld.append("1");
                        } else {
                            resStrBld.append("0");
                        }

                    }
                    else {
                        resStrBld.append(0);
                        resStrBld.append(",");
                        resStrBld.append(0);
                    }
                    resStrBld.append(NEW_LINE_STR);
                }

                responseResult = resStrBld.toString();
            }

        }
        //
        // getAlarmHistoryComposedService(serviceID,callback)
        // returns: compoServiceDBId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestamp,alertType,message\n
        else if(functionSignature.compareToIgnoreCase("getAlarmHistoryComposedService") ==0 &&
                    pServiceID!=null) {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValuesStr=reqParamValsStrBld.toString();
            // find associated partial services, get alarms list
            int iServiceID = Integer.parseInt(pServiceID);

            StringBuilder resStrBld = new StringBuilder();
            AbstractNotificationManager abstractNotificationManager = AbstractNotificationManager.getInstance();

            int thePartialServId = -1;
            boolean foundThePartialServiceForThisCapId = false;
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {

                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        thePartialServId = partialServiceTmpIter.getId();
                        List<Notification> allNotfsList = abstractNotificationManager.getNotificationListForFilters(thePartialServId, -1, "", "");
                        for (Notification notfsTmpIter : allNotfsList)
                        {
                            Date tsFromDB = notfsTmpIter.getTimestamp();
                            String tsFromDBStr = "";
                            if(tsFromDB!=null) {
                                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                // Using DateFormat format method we can create a string
                                // representation of a date with the defined format.
                                tsFromDBStr= df.format(tsFromDB);
                            }
                            //format the return CSV
                            // composedServiceId, alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message\n
                            resStrBld.append(iServiceID);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getId());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getPartialServiceID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getCapabilityID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getResource());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctName());
                            resStrBld.append(",");
                            resStrBld.append("1"); // trigger flag is always set for notifications
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctTriggerSign());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getBoundValue());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getGatewayRegName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getSensorName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getValue());
                            resStrBld.append(",");
                            resStrBld.append(tsFromDBStr);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getNotificationType());
                            resStrBld.append(",");
                            // remove possible commas in the notification message
                            resStrBld.append(notfsTmpIter.getNotificationText().replaceAll(Pattern.quote(","), ""));
                            resStrBld.append(NEW_LINE_STR);
                        }
                    }
                }
            }
            responseResult = resStrBld.toString();
        }
        //
        // getAlarmHistorybySensor(serviceId,gatewayId,sensorId,callback)
        // returns: compoServiceDBId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message\n
        else if(functionSignature.compareToIgnoreCase("getAlarmHistorybySensor") ==0 &&
                pServiceID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty()) {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValuesStr=reqParamValsStrBld.toString();
            // find associated partial services, get alarms list for a specific sensor
            int iServiceID = Integer.parseInt(pServiceID);

            StringBuilder resStrBld = new StringBuilder();
            AbstractNotificationManager abstractNotificationManager = AbstractNotificationManager.getInstance();

            int thePartialServId = -1;
            boolean foundThePartialServiceForThisCapId = false;
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {
                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        thePartialServId = partialServiceTmpIter.getId();
                        List<Notification> allNotfsList = abstractNotificationManager.getNotificationListForFilters(thePartialServId, -1, pGatewayID, pSensorID);

                        for (Notification notfsTmpIter : allNotfsList)
                        {
                            Date tsFromDB = notfsTmpIter.getTimestamp();
                            String tsFromDBStr = "";
                            if(tsFromDB!=null) {
                                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                // Using DateFormat format method we can create a string
                                // representation of a date with the defined format.
                                tsFromDBStr= df.format(tsFromDB);
                            }
                            //format the return CSV
                            // composedServiceId, alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message
                            resStrBld.append(iServiceID);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getId());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getPartialServiceID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getCapabilityID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getResource());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctName());
                            resStrBld.append(",");
                            resStrBld.append("1"); // trigger flag is always set for notifications
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctTriggerSign());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getBoundValue());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getGatewayRegName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getSensorName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getValue());
                            resStrBld.append(",");
                            resStrBld.append(tsFromDBStr);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getNotificationType());
                            resStrBld.append(",");
                            // remove possible commas in the notification message
                            resStrBld.append(notfsTmpIter.getNotificationText().replaceAll(Pattern.quote(","), ""));
                            resStrBld.append(NEW_LINE_STR);
                         }
                    }
                }
            }
            responseResult = resStrBld.toString();

        }
        //
        //getAlarmHistorybyCapability(serviceId,capabilityId,callback)
        // returns: compoServiceDBId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message\n
        else if(functionSignature.compareToIgnoreCase("getAlarmHistorybyCapability") ==0 &&
                pServiceID!=null &&
                pCapabilityID!=null) {
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pCapabilityID);
            reqParamValuesStr=reqParamValsStrBld.toString();
            // find associated partial services, get alarms list for a specific capability
            int iServiceID = Integer.parseInt(pServiceID);
            int iCapID =  Integer.parseInt(pCapabilityID);

            StringBuilder resStrBld = new StringBuilder();
            AbstractNotificationManager abstractNotificationManager = AbstractNotificationManager.getInstance();

            int thePartialServId = -1;
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {

                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        thePartialServId = partialServiceTmpIter.getId();
                        List<Notification> allNotfsList = abstractNotificationManager.getNotificationListForFilters(thePartialServId, iCapID, "", "");
                        for (Notification notfsTmpIter : allNotfsList)
                        {
                            Date tsFromDB = notfsTmpIter.getTimestamp();
                            String tsFromDBStr = "";
                            if(tsFromDB!=null) {
                                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                // Using DateFormat format method we can create a string
                                // representation of a date with the defined format.
                                tsFromDBStr= df.format(tsFromDB);
                            }
                            //format the return CSV
                            // composedServiceId, alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message
                            resStrBld.append(iServiceID);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getId());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getPartialServiceID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getCapabilityID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getResource());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctName());
                            resStrBld.append(",");
                            resStrBld.append("1"); // trigger flag is always set for notifications
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctTriggerSign());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getBoundValue());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getGatewayRegName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getSensorName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getValue());
                            resStrBld.append(",");
                            resStrBld.append(tsFromDBStr);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getNotificationType());
                            resStrBld.append(",");
                            // remove possible commas in the notification message
                            resStrBld.append(notfsTmpIter.getNotificationText().replaceAll(Pattern.quote(","), ""));
                            resStrBld.append(NEW_LINE_STR);
                        }
                    }
                }
            }
            responseResult = resStrBld.toString();
        }
        //
        //getAlarmHistorybySensorCapability(serviceId,gatewayId,sensorId,capabilityId,callback)
        // returns: compoServiceDBId,alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message\n
        else if(functionSignature.compareToIgnoreCase("getAlarmHistorybySensorCapability") ==0 &&
                pServiceID!=null &&
                pGatewayID!=null && !pGatewayID.trim().isEmpty() &&
                pSensorID!=null && !pSensorID.trim().isEmpty() &&
                pCapabilityID!=null) {
            //store the param values to return them back (to be able to connect the calling parameters with the end result)
            StringBuilder reqParamValsStrBld = new StringBuilder();
            reqParamValsStrBld.append(pServiceID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pGatewayID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pSensorID);
            reqParamValsStrBld.append(",");
            reqParamValsStrBld.append(pCapabilityID);
            reqParamValuesStr=reqParamValsStrBld.toString();
            // find associated partial services, get alarms list for a specific sensor and capability
            int iServiceID = Integer.parseInt(pServiceID);
            int iCapID =  Integer.parseInt(pCapabilityID);

            StringBuilder resStrBld = new StringBuilder();
            AbstractNotificationManager abstractNotificationManager = AbstractNotificationManager.getInstance();

            int thePartialServId = -1;
            boolean foundThePartialServiceForThisCapId = false;
            FullComposedService reqCompoService = AbstractComposedServiceManager.getInstance().getComposedService(iServiceID);
            if(reqCompoService != null)
            {
                //get Partial Services
                List<ServiceInstance> partialServicesList = reqCompoService.getServiceInstanceList();
                if(partialServicesList != null)
                {

                    AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                    for (ServiceInstance partialServiceTmpIter : partialServicesList)
                    {
                        thePartialServId = partialServiceTmpIter.getId();
                        List<Notification> allNotfsList = abstractNotificationManager.getNotificationListForFilters(thePartialServId, iCapID, pGatewayID, pSensorID);
                        for (Notification notfsTmpIter : allNotfsList)
                        {
                            Date tsFromDB = notfsTmpIter.getTimestamp();
                            String tsFromDBStr = "";
                            if(tsFromDB!=null) {
                                DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                // Using DateFormat format method we can create a string
                                // representation of a date with the defined format.
                                tsFromDBStr= df.format(tsFromDB);
                            }
                            //format the return CSV
                            // composedServiceId, alarmDBId,partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,gwId,sensorId,value,timestampValue,alertType,message
                            resStrBld.append(iServiceID);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getId());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getPartialServiceID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getCapabilityID());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getResource());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctName());
                            resStrBld.append(",");
                            resStrBld.append("1"); // trigger flag is always set for notifications
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getRefFunctTriggerSign());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getBoundValue());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getGatewayRegName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getSensorName());
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getValue());
                            resStrBld.append(",");
                            resStrBld.append(tsFromDBStr);
                            resStrBld.append(",");
                            resStrBld.append(notfsTmpIter.getNotificationType());
                            resStrBld.append(",");
                            // remove possible commas in the notification message
                            resStrBld.append(notfsTmpIter.getNotificationText().replaceAll(Pattern.quote(","), ""));
                            resStrBld.append(NEW_LINE_STR);
                        }
                    }
                }
            }
            responseResult = resStrBld.toString();
        }
    } else {
        errno = 1;
        xmerrordescr="No Interface Function Requested!";
    }

    responseResult = responseResult.replaceAll(Pattern.quote(NEW_LINE_STR)+"$" , "");
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <reply funct="<%=functionSignature %>" value="<%=responseResult %>" parNames="<%=reqParamNamesStr %>" parValues="<%=reqParamValuesStr %>" qstr="<%=reqData %>" ></reply>
</Answer>