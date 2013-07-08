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
package vitro.vspEngine.service.common.abstractservice;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.abstractservice.dao.NotificationDAO;
import vitro.vspEngine.service.common.abstractservice.model.Notification;

import javax.persistence.EntityManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class AbstractNotificationManager extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractNotificationManager.class);

    private static AbstractNotificationManager instance = new AbstractNotificationManager();

    private AbstractNotificationManager(){
        super();
    }

    public static AbstractNotificationManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_REMOVE_NOTIFICATION = "removeNotification"; //TODO: to support later
    private static String METHOD_INSERT_NOTIFICATION = "insertNotification"; //TODO: to support later
    private static String METHOD_CREATE_NOTIFICATION = "createNotification";
    private static String METHOD_UPDATE_NOTIFICATION_BY_ID = "updateNotification"; //TODO: to support later
    private static String METHOD_GET_NOTIFICATION_LIST = "getNotificationList";
    private static String METHOD_GET_NOTIFICATION_BY_ID = "getNotificationById";
    private static String METHOD_GET_NOTIFICATION_LIST_FOR_FILTERS = "getNotificationListForFilters";

    //////////// INTERFACE //////////////////////////////////////


    public Notification getNotification(int dbNotificationId){
        logger.debug("getNotification - dbNotificationId = " + dbNotificationId);
        return (Notification)startResultMethodInTransaction(METHOD_GET_NOTIFICATION_BY_ID, dbNotificationId);
    }


    public List<Notification> getNotificationList(){
        logger.debug("getNotificationList - Start");
        return (List<Notification>)startResultMethodInTransaction(METHOD_GET_NOTIFICATION_LIST);
    }

    public List<Notification> getNotificationListForFilters(int instanceID, int capID, String gatewayID, String sensorID){
        logger.debug("getNotificationListForFilters - Start");
        return (List<Notification>)startResultMethodInTransaction(METHOD_GET_NOTIFICATION_LIST_FOR_FILTERS, instanceID,  capID,  gatewayID,  sensorID);
    }
    
    public void createNotification(int partialServiceID, int capabilityID,  String gatewayRegName, String sensorID, String replacmntId, boolean gatewayLevel, boolean crossGatewayLevel, int aggreagatedSensorsNum, String resource, Date timestamp, Date receivedTimestamp, float value, String uom, String notificationText, int notificationType,
                                   long boundValue,
                                   String refFunctName,
                                   String refFunctTriggerSign,
                                   int level,
                                   int refFunctId){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String reportTS = df.format(timestamp);

        logger.debug("createNotification - partialServiceID = " + Integer.toString(partialServiceID) + "; capabilityID = " + Integer.toString(capabilityID) +  "; gateway Name = " + gatewayRegName +  "; sensor ID = " + sensorID +"; replacmntId = " + replacmntId+ "; gatewayLevel = " +  Boolean.toString(gatewayLevel)  + "; crossGatewayLevel = " + Boolean.toString(crossGatewayLevel) + "; aggreagatedSensorsNum = " + Integer.toString(aggreagatedSensorsNum) + "; resource = " + resource + "; timestamp = " +reportTS + "; value = " + Float.toString( value ) );
        startVoidMethodInTransaction(METHOD_CREATE_NOTIFICATION, partialServiceID, capabilityID,  gatewayRegName, sensorID, replacmntId, gatewayLevel, crossGatewayLevel, aggreagatedSensorsNum, resource, timestamp, receivedTimestamp, value, uom, notificationText, notificationType,
                boundValue,
                refFunctName,
                refFunctTriggerSign,
                level,
                refFunctId);
    }

    //////////////  IMPLEMENTATIONS /////////////////////////////
    private Notification getNotificationById(EntityManager manager, Object... params ){
        Notification result = null;

        int dbNotificationId = (Integer)params[0];

        NotificationDAO notificationDAO = NotificationDAO.getInstance();
        result = notificationDAO.getNotification(manager, dbNotificationId);

        return result;
    }


    private List<Notification> getNotificationList(EntityManager manager){

        NotificationDAO notificationDAO = NotificationDAO.getInstance();

        List<Notification> result = notificationDAO.getNotificationList(manager);

        return result;
    }

    private List<Notification> getNotificationListForFilters(EntityManager manager, Object... params ) {
        int pPartialServiceId = (Integer) params[0];
        int pCapabilityId = (Integer) params[1];
        String pGatewayName= (String) params[2];
        String pSensorId= (String) params[3];

        NotificationDAO notificationDAO = NotificationDAO.getInstance();

        List<Notification> result = notificationDAO.getNotificationListForFilters(manager, pPartialServiceId, pCapabilityId, pGatewayName, pSensorId);

        return result;
    }

    // arguments  partialServiceID, capabilityID,  gatewayRegName, sensorID, gatewayLevel, crossGatewayLevel, aggreagatedSensorsNum, resource, timestamp, receivedTimestamp, value, uom, notificationText, notificationType)
    /**
     * @param manager  Manager EntityManager
     * @param params params array.
     */
    private void createNotification(EntityManager manager, Object... params ) {
        // TODO Add Notification
        logger.debug("Creating Notification object");
        int pPartialServiceId = (Integer) params[0];
        int pCapabilityId = (Integer) params[1];
        String pGatewayName= (String) params[2];
        String pSensorId= (String) params[3];
        String pReplacmntId= (String) params[4];
        boolean pGatewayLevelFunct= (Boolean) params[5];
        boolean pCrossGatewayLevelFunct= (Boolean) params[6];
        int aggregatedSensorsNum= (Integer) params[7];
        String pResource= (String) params[8];
        Date pTimestamp= (Date) params[9];
        Date pReceivedTimestamp =(Date) params[10];
        float pValue= (Float) params[11];
        String pUom = (String) params[12];
        String pMsgText = (String) params[13];
        int pMsgType = (Integer) params[14];
        long boundValue = (Long) params[15];
        String refFunctName = (String) params[16];
        String refFunctTriggerSign = (String) params[17];
        int level  = (Integer) params[18];
        int refFunctId =  (Integer) params[19];
//
        Notification anNotification = new Notification();
        anNotification.setPartialServiceID(pPartialServiceId);
        anNotification.setCapabilityID(pCapabilityId);
        anNotification.setGatewayRegName(pGatewayName);
        anNotification.setSensorName(pSensorId);
        anNotification.setReplacmntSensorName(pReplacmntId);
        anNotification.setGatewayLevel(pGatewayLevelFunct);
        anNotification.setCrossGatewayLevel(pCrossGatewayLevelFunct);
        anNotification.setAggreagatedSensorsNum(aggregatedSensorsNum);
        anNotification.setResource(pResource);
        anNotification.setTimestamp(pTimestamp);
        anNotification.setReceivedTimestamp(pReceivedTimestamp);
        anNotification.setValue(pValue);
        anNotification.setUom(pUom);
        anNotification.setNotificationText(pMsgText);
        anNotification.setNotificationType(pMsgType);
        anNotification.setBoundValue(boundValue);
        anNotification.setRefFunctName(refFunctName);
        anNotification.setRefFunctTriggerSign(refFunctTriggerSign);
        anNotification.setLevel(level);
        anNotification.setRefFunctId(refFunctId);
        manager.merge(anNotification);
    }

    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
/*        if(methodName.equals(METHOD_INSERT_NOTIFICATION)){
            insertNotification(manager, params);
        } else */if(methodName.equals(METHOD_CREATE_NOTIFICATION)){
            createNotification(manager, params);
        } /*else if(methodName.equals(METHOD_UPDATE_NOTIFICATION_BY_ID)){
            updateNotification(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_NOTIFICATION)){
            removeNotification(manager, params);
        }*/
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_NOTIFICATION_LIST)){
            result = getNotificationList(manager);
        } else if(methodName.equals(METHOD_GET_NOTIFICATION_BY_ID)){
            result = getNotificationById(manager, params);
        } else if(methodName.equals(METHOD_GET_NOTIFICATION_LIST_FOR_FILTERS)){
            result = getNotificationListForFilters(manager, params);
        }

        return result;
    }
}
