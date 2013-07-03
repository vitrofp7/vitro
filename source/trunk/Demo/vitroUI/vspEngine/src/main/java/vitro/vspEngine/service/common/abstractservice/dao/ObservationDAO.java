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
import vitro.vspEngine.service.common.abstractservice.model.Observation;
import vitro.vspEngine.service.query.ResultAggrStruct;

import javax.persistence.EntityManager;
import java.util.List;

/**
 */
public class ObservationDAO {
    private static ObservationDAO instance = new ObservationDAO();

    private Logger logger = Logger.getLogger(ObservationDAO.class);

    private ObservationDAO(){
        super();
    }

    public static ObservationDAO getInstance(){
        return instance;
    }

    public List<Observation> getObservationList(EntityManager manager){
        logger.debug("getObservationList() - Start");
        List<Observation> result =  manager.createQuery("SELECT instance FROM Observation instance", Observation.class).getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (Observation observationTmp : result) {
        }

        return result;

    }

    public List<Observation> getObservationListForFilters(EntityManager manager, int instanceID, int capID, String gatewayID, String sensorID){
        logger.debug("getObservationListForFilters() - Start");
        List<Observation> result =  manager.createQuery("SELECT instance FROM Observation instance WHERE " +
                "instance.partialServiceID = :pServId"+
                " AND instance.capabilityID = :pCapId " +
                " AND instance.gatewayRegName LIKE :pGwName" +
                " AND (( instance.gatewayLevel = 1 AND instance.sensorName LIKE :pSensorNameSpecialValAggrMultiple ) OR (instance.sensorName LIKE :pSensorName)) "+
                " AND instance.aggreagatedSensorsNum > 0 " +
                " ORDER BY instance.receivedTimestamp ASC", Observation.class)
                .setParameter("pServId", instanceID)
                .setParameter("pCapId", capID)
                .setParameter("pGwName", gatewayID)
                .setParameter("pSensorNameSpecialValAggrMultiple", ResultAggrStruct.MidSpecialForAggregateMultipleValues)
                .setParameter("pSensorName", sensorID)
                .getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (Observation observationTmp : result) {
        }

        return result;
    }

    public Observation getLastObservationForFilters(EntityManager manager, int instanceID, int capID, String gatewayID, String sensorID){
        logger.debug("getLastObservationForFilters() - Start");
        List<Observation> resultLst =  manager.createQuery("SELECT instance FROM Observation instance WHERE " +
                "instance.partialServiceID = :pServId"+
                " AND instance.capabilityID = :pCapId " +
                " AND instance.gatewayRegName LIKE :pGwName" +
                " AND (( instance.gatewayLevel = 1 AND instance.sensorName LIKE :pSensorNameSpecialValAggrMultiple ) OR (instance.sensorName LIKE :pSensorName)) "+
                " AND instance.aggreagatedSensorsNum > 0 " +
                " ORDER BY instance.receivedTimestamp DESC", Observation.class)
                .setParameter("pServId", instanceID)
                .setParameter("pCapId", capID)
                .setParameter("pGwName", gatewayID)
                .setParameter("pSensorNameSpecialValAggrMultiple", ResultAggrStruct.MidSpecialForAggregateMultipleValues)
                .setParameter("pSensorName", sensorID)
                .setMaxResults(1)
                .getResultList();


        Observation result = null;
        if(resultLst.size() > 0)
        {
            result =resultLst.get(0);
        }
        //Extract gateway data from db via JPA NOT COMMENT THIS LINES

        return result;
    }

    public Observation getObservation(EntityManager manager, int observationId){
        logger.debug("getObservation() - observationId = " + observationId);
        Observation observation = manager.find(Observation.class, observationId);

        //Extract selection data JPA NOT COMMENT THIS LINES

        return observation;

    }
}
