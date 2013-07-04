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
package vitro.vspEngine.service.common.abstractservice;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.abstractservice.dao.ObservationDAO;
import vitro.vspEngine.service.common.abstractservice.model.Observation;

import javax.persistence.EntityManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 */
public class AbstractObservationManager extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractObservationManager.class);

    private static AbstractObservationManager instance = new AbstractObservationManager();

    private AbstractObservationManager(){
        super();
    }

    public static AbstractObservationManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_REMOVE_OBSERVATION = "removeObservation"; //TODO: to support later
    private static String METHOD_INSERT_OBSERVATION = "insertObservation"; //TODO: to support later
    private static String METHOD_CREATE_OBSERVATION = "createObservation";
    private static String METHOD_UPDATE_OBSERVATION_BY_ID = "updateObservation"; //TODO: to support later
    private static String METHOD_GET_OBSERVATION_LIST = "getObservationList";
    private static String METHOD_GET_OBSERVATION_LIST_FOR_FILTERS = "getObservationListForFilters";
    private static String METHOD_GET_LAST_OBSERVATION_FOR_FILTERS = "getLastObservationForFilters";
    private static String METHOD_GET_OBSERVATION_BY_ID = "getObservationById";

    //////////// INTERFACE //////////////////////////////////////
    public Observation getObservation(int observationId){
        logger.debug("getObservation - observationId = " + observationId);
        return (Observation)startResultMethodInTransaction(METHOD_GET_OBSERVATION_BY_ID, observationId);
    }


    public List<Observation> getObservationList(){
        logger.debug("getObservationList - Start");
        return (List<Observation>)startResultMethodInTransaction(METHOD_GET_OBSERVATION_LIST);
    }

    public List<Observation> getObservationListForFilters(int instanceID, int capID, String gatewayID, String sensorID){
        logger.debug("getObservationListForFilters - Start");
        return (List<Observation>)startResultMethodInTransaction(METHOD_GET_OBSERVATION_LIST_FOR_FILTERS, instanceID,  capID,  gatewayID,  sensorID);
    }

    public Observation getLastObservationForFilters(int instanceID, int capID, String gatewayID, String sensorID){
        logger.debug("getLastObservationForFilters - Start");
        return (Observation)startResultMethodInTransaction(METHOD_GET_LAST_OBSERVATION_FOR_FILTERS, instanceID,  capID,  gatewayID,  sensorID);
    }

    public void createObservation(int partialServiceID, int capabilityID,  String gatewayRegName, String sensorID, String replacmntId, boolean gatewayLevel, boolean crossGatewayLevel, int aggreagatedSensorsNum, String resource, Date timestamp, Date receivedTimestamp, float value, String uom, String refFunctName, String refFunctNameEssential, int refFunctIdInQueryDef, boolean isDefinitionFunct){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String reportTS = df.format(timestamp);

        logger.debug("createObservation - partialServiceID = " + Integer.toString(partialServiceID) + "; capabilityID = " + Integer.toString(capabilityID) +  "; gateway Name = " + gatewayRegName +  "; sensor ID = " + sensorID +"; replacmntId = " + replacmntId+ "; gatewayLevel = " +  Boolean.toString(gatewayLevel)  + "; crossGatewayLevel = " + Boolean.toString(crossGatewayLevel) + "; aggreagatedSensorsNum = " + Integer.toString(aggreagatedSensorsNum) + "; resource = " + resource + "; timestamp = " +reportTS + "; value = " + Float.toString( value ) + "; refFunctName = " + refFunctName  + "; Essential FunctName = " + refFunctNameEssential  + "; isDefinitionFunct = " + Boolean.toString(isDefinitionFunct));
        startVoidMethodInTransaction(METHOD_CREATE_OBSERVATION, partialServiceID, capabilityID,  gatewayRegName, sensorID, replacmntId, gatewayLevel, crossGatewayLevel, aggreagatedSensorsNum, resource, timestamp, receivedTimestamp, value, uom, refFunctName, refFunctNameEssential, refFunctIdInQueryDef, isDefinitionFunct);
    }

    //////////////  IMPLEMENTATIONS /////////////////////////////

    /**
     * @param manager  Manager EntityManager
     * @param params params array.
     */
    private void  createObservation(EntityManager manager, Object... params ){
        // TODO Add Observation
        logger.debug("Creating Observation object");
        int pPartialServiceId = (Integer) params[0];
        int pCapabilityId = (Integer) params[1];
        String pGatewayName= (String) params[2];
        String pSensorId= (String) params[3];
        String replacmntId = (String) params[4];
        boolean pGatewayLevelFunct= (Boolean) params[5];
        boolean pCrossGatewayLevelFunct= (Boolean) params[6];
        int aggregatedSensorsNum= (Integer) params[7];
        String pResource= (String) params[8];
        Date pTimestamp= (Date) params[9];
        Date pReceivedTimestamp =(Date) params[10];
        float pValue= (Float) params[11];
        String pUom = (String) params[12];
        String refFunctName = (String)params[13];
        String refFunctNameEssential = (String)params[14];
        int refFunctIdInQueryDef =  (Integer) params[15];
        boolean isDefinitionFunct = (Boolean) params[16];
//
        Observation anObservation = new Observation();
        anObservation.setPartialServiceID(pPartialServiceId);
        anObservation.setCapabilityID(pCapabilityId);
        anObservation.setGatewayRegName(pGatewayName);
        anObservation.setSensorName(pSensorId);
        anObservation.setReplacmntSensorName(replacmntId);
        anObservation.setGatewayLevel(pGatewayLevelFunct);
        anObservation.setCrossGatewayLevel(pCrossGatewayLevelFunct);
        anObservation.setAggreagatedSensorsNum(aggregatedSensorsNum);
        anObservation.setResource(pResource);
        anObservation.setTimestamp(pTimestamp);
        anObservation.setReceivedTimestamp(pReceivedTimestamp);
        anObservation.setValue(pValue);
        anObservation.setUom(pUom);
        anObservation.setRefFunctName(refFunctName);
        anObservation.setRefFunctNameEssential(refFunctNameEssential);
        anObservation.setTheDefinitionFunction(isDefinitionFunct);
        anObservation.setRefFunctIdInQueryDef(refFunctIdInQueryDef);
        manager.merge(anObservation);
    }


    /**
     *
     * @param manager
     * @param params
     * @return an Observation object
     */
    private Observation getObservationById(EntityManager manager, Object... params ){
        Observation result = null;
        int observationId = (Integer)params[0];
        ObservationDAO observationDAO = ObservationDAO.getInstance();
        result = observationDAO.getObservation(manager, observationId);
        return result;
    }


    private List<Observation> getObservationList(EntityManager manager){

        ObservationDAO observationDAO = ObservationDAO.getInstance();

        List<Observation> result = observationDAO.getObservationList(manager);

        return result;
    }

    private List<Observation> getObservationListForFilters(EntityManager manager, Object... params) {

        int pPartialServiceId = (Integer) params[0];
        int pCapabilityId = (Integer) params[1];
        String pGatewayName= (String) params[2];
        String pSensorId= (String) params[3];

        ObservationDAO observationDAO = ObservationDAO.getInstance();

        List<Observation> result = observationDAO.getObservationListForFilters(manager, pPartialServiceId, pCapabilityId, pGatewayName, pSensorId);

        return result;

    }

    private Observation getLastObservationForFilters(EntityManager manager, Object... params) {
        int pPartialServiceId = (Integer) params[0];
        int pCapabilityId = (Integer) params[1];
        String pGatewayName= (String) params[2];
        String pSensorId= (String) params[3];
        ObservationDAO observationDAO = ObservationDAO.getInstance();
        Observation result = observationDAO.getLastObservationForFilters(manager, pPartialServiceId, pCapabilityId, pGatewayName, pSensorId);
        return result;
    }

    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
       if(methodName.equals(METHOD_CREATE_OBSERVATION)){
            createObservation(manager, params);
        }/* else if(methodName.equals(METHOD_INSERT_OBSERVATION)){
            insertObservation(manager, params);
        }else if(methodName.equals(METHOD_UPDATE_OBSERVATION_BY_ID)){
            updateObservation(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_OBSERVATION)){
            removeObservation(manager, params);
        }*/
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_OBSERVATION_LIST)){
            result = getObservationList(manager);
        } else if(methodName.equals(METHOD_GET_OBSERVATION_BY_ID)){
            result = getObservationById(manager, params);
        } else if(methodName.equals(METHOD_GET_OBSERVATION_LIST_FOR_FILTERS)){
            result = getObservationListForFilters(manager, params);
        }  else if(methodName.equals (METHOD_GET_LAST_OBSERVATION_FOR_FILTERS) )
        {
            result = getLastObservationForFilters(manager, params);
        }

        return result;
    }

}
