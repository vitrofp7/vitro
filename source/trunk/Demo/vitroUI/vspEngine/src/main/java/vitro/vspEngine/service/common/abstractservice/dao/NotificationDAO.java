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
package vitro.vspEngine.service.common.abstractservice.dao;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.abstractservice.model.Notification;
import vitro.vspEngine.service.query.ResultAggrStruct;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

/**
 */
public class NotificationDAO {

    private static NotificationDAO instance = new NotificationDAO();

    private Logger logger = Logger.getLogger(NotificationDAO.class);

    private NotificationDAO(){
        super();
    }

    public static NotificationDAO getInstance(){
        return instance;
    }

    public List<Notification> getNotificationList(EntityManager manager){
        logger.debug("getNotificationList() - Start");
        List<Notification> result =  manager.createQuery("SELECT instance FROM Notification instance", Notification.class).getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES

        return result;

    }

    public List<Notification> getNotificationListForFilters(EntityManager manager, int instanceID, int capID, String gatewayID, String sensorID){
        logger.debug("getNotificationListForFilters() - Start");
        List<Notification> result = null;
        StringBuilder sqlQueryBld = new StringBuilder();
        sqlQueryBld.append("SELECT instance FROM Notification instance WHERE ");
        sqlQueryBld.append(" instance.partialServiceID = :pServId");
        if(capID !=-1)
        {
            sqlQueryBld.append(" AND instance.capabilityID = :pCapId ");
        }
        if(gatewayID!= null && gatewayID.trim().compareTo("")!=0){
            sqlQueryBld.append(" AND instance.gatewayRegName LIKE :pGwName");
        }
        if(sensorID!=null && sensorID.trim().compareTo("")!=0){
            sqlQueryBld.append(" AND (( instance.gatewayLevel = 1 AND instance.sensorName LIKE :pSensorNameSpecialValAggrMultiple ) OR (instance.sensorName LIKE :pSensorName)) ");
        }
        //sqlQueryBld.append(" AND instance.level = :pGWLEVELCONST");
        sqlQueryBld.append(" AND instance.aggreagatedSensorsNum > 0 ");
        //sqlQueryBld.append(" ORDER BY instance.receivedTimestamp ASC ");
        //sqlQueryBld.append(" ORDER BY instance.timestamp ASC ");
        sqlQueryBld.append(" ORDER BY instance.timestamp DESC ");
        sqlQueryBld.append(" LIMIT 300 ");
        String theFinalQueryStr = sqlQueryBld.toString();

        TypedQuery sqlQueryNotification =  manager.createQuery(theFinalQueryStr, Notification.class);
        sqlQueryNotification.setParameter("pServId", instanceID);
        //sqlQueryNotification.setParameter("pGWLEVELCONST", Notification.LEVEL_GATEWAY);
        if(capID !=-1)
        {
            sqlQueryNotification.setParameter("pCapId", capID);
        }
        if(gatewayID!= null && gatewayID.trim().compareTo("")!=0){
            sqlQueryNotification.setParameter("pGwName", gatewayID);
        }
        if(sensorID!=null && sensorID.trim().compareTo("")!=0){
            sqlQueryNotification.setParameter("pSensorNameSpecialValAggrMultiple", ResultAggrStruct.MidSpecialForAggregateMultipleValues)
                    .setParameter("pSensorName", sensorID);
        }


        result =  sqlQueryNotification.getResultList();

        Collections.reverse(result);  // we reverse the list because we want ASCENDING ORDER (we used desc in the query to limit the results to recent ones)
        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (Notification notificationTmp : result) {
        }

        return result;
    }

    public Notification getNotification(EntityManager manager, int notificationListId){
        logger.debug("getNotification() - notificationListId = " + notificationListId);
        Notification dbNotificationTmp = manager.find(Notification.class, notificationListId);

        //Extract selection data JPA NOT COMMENT THIS LINES

        return dbNotificationTmp;

    }

}
