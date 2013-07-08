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
import vitro.vspEngine.service.common.abstractservice.dao.GatewayDAO;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/*
 * This class uses JPA to persist data
 * 
 * To manage transactions without Spring a little infrastructure has been implemented to avoid code duplication.
 * 
 * To implement new business method follow this steps:
 * 
 * 1 - write the businessMethod with the correct interface that delegating to "startXXXXMethodInTransaction" the control
 * 2 - write the business method using "EntityManager manager, Object... params " parameters
 * 3 - update method "callXXXXMethod" to allow the correct mapping
 * 
 * */
public class AbstractGatewayManager extends JPAManager{

    private Logger logger = Logger.getLogger(AbstractGatewayManager.class);

    private static AbstractGatewayManager instance = new AbstractGatewayManager();

    private AbstractGatewayManager(){
        super();
    }

    public static AbstractGatewayManager getInstance(){
        return instance;
    }

    ///////////////////////////////////////

    private static String METHOD_REMOVE_GATEWAY_BY_INC_ID = "removeDBRegisteredGateway";
    private static String METHOD_REMOVE_GATEWAY_BY_NAME = "removeDBRegisteredGatewayByName";
    private static String METHOD_INSERT_GATEWAY = "insertDBRegisteredGateway";
    private static String METHOD_CREATE_GATEWAY = "createDBRegisteredGateway";
    private static String METHOD_GET_GATEWAY_LIST = "getDBRegisteredGatewayList";
    private static String METHOD_GET_GATEWAY_BY_ID = "getDBRegisteredGatewayByIncId";
    private static String METHOD_GET_GATEWAY_BY_NAME = "getDBRegisteredGatewayByName";
    private static String METHOD_UPDATE_GATEWAY_BY_ID = "updateDBRegisteredGateway";

    //////////// INTERFACE //////////////////////////////////////

    public void removeDBRegisteredGateway(int instanceId){
        logger.debug("removeDBRegisteredGateway - dbRegisteredGateway = " + instanceId);
        startVoidMethodInTransaction(METHOD_REMOVE_GATEWAY_BY_INC_ID, instanceId);
    }

    public void removeDBRegisteredGatewayByName(String gwName){
        logger.debug("removeDBRegisteredGatewayByName - dbRegisteredGateway = " + gwName);
        startVoidMethodInTransaction(METHOD_REMOVE_GATEWAY_BY_NAME, gwName);
    }

    public List<DBRegisteredGateway> getDBRegisteredGatewayList(){
        logger.debug("getDBRegisteredGatewayList - Start");
        return (List<DBRegisteredGateway>)startResultMethodInTransaction(METHOD_GET_GATEWAY_LIST);
    }
    //todo: implement if needed
    public void createDBRegisteredGateway(String gwName){
        logger.debug("(Do not use) createDBRegisteredGateway - gwName = " + gwName  );
        startVoidMethodInTransaction(METHOD_CREATE_GATEWAY, gwName);
    }
   //todo: implement if needed
    public void updateDBRegisteredGateway(int instanceId, String gwName){
        logger.debug("(Do not use) updateDBRegisteredGateway - gwName = " + gwName );
        startVoidMethodInTransaction(METHOD_UPDATE_GATEWAY_BY_ID, gwName);
    }

    public void insertDBRegisteredGateway(DBRegisteredGateway instance){
        startVoidMethodInTransaction(METHOD_INSERT_GATEWAY, instance);
    }

    public DBRegisteredGateway getDBRegisteredGatewayByIncId(int serviceInstanceId){
        logger.debug("getDBRegisteredGatewayByIncId - serviceInstanceId = " + serviceInstanceId);
        return (DBRegisteredGateway)startResultMethodInTransaction(METHOD_GET_GATEWAY_BY_ID, serviceInstanceId);
    }

    public DBRegisteredGateway getDBRegisteredGatewayByName(String gwName){
        logger.debug("getDBRegisteredGatewayByName - serviceInstanceId = " + gwName);
        return (DBRegisteredGateway)startResultMethodInTransaction(METHOD_GET_GATEWAY_BY_NAME, gwName);
    }

    ////////// IMPLEMENTATIONS ///////////////////////////////////

    private DBRegisteredGateway getDBRegisteredGatewayByIncId(EntityManager manager, Object... params ){
        DBRegisteredGateway result = null;
        int instanceId = (Integer)params[0];
        GatewayDAO gatewayDAO = GatewayDAO.getInstance();
        result = gatewayDAO.getGatewayByIncId(manager, instanceId);
        return result;
    }


    private DBRegisteredGateway getDBRegisteredGatewayByName (EntityManager manager, Object... params ){
        DBRegisteredGateway result = null;

        String instanceId = (String)params[0];

        GatewayDAO gatewayDAO = GatewayDAO.getInstance();
        result = gatewayDAO.getGateway(manager, instanceId);

        return result;
    }

    private void insertDBRegisteredGatewayImpl(EntityManager manager, Object... params ){
        DBRegisteredGateway serviceInstance = (DBRegisteredGateway) params[0];
        manager.merge(serviceInstance);
    }

    private void createDBRegisteredGateway(EntityManager manager, Object... params ){
       //todo

    }

    private void updateDBRegisteredGateway(EntityManager manager, Object... params ){
        //todo


    }

    private void removeDBRegisteredGateway(EntityManager manager, Object... params ){

        Integer instanceId = (Integer) params[0];

        DBRegisteredGateway dbRegisteredGateway = manager.find(DBRegisteredGateway.class, instanceId);
        manager.remove(dbRegisteredGateway);

    }

    private void removeDBRegisteredGatewayByName(EntityManager manager, Object... params ){

        String gwName = (String) params[0];

        DBRegisteredGateway dbRegisteredGateway = manager.find(DBRegisteredGateway.class, gwName);
        manager.remove(dbRegisteredGateway);

    }

    private List<DBRegisteredGateway> getDBRegisteredGatewayList(EntityManager manager){

        GatewayDAO gatewayDAO = GatewayDAO.getInstance();

        List<DBRegisteredGateway> result = gatewayDAO.getInstanceList(manager);

        return result;
    }


    ///////////// MAPPING METHODS ////////////////////////////

    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
        if(methodName.equals(METHOD_INSERT_GATEWAY)){
            insertDBRegisteredGatewayImpl(manager, params);
        } else if(methodName.equals(METHOD_CREATE_GATEWAY)){
            createDBRegisteredGateway(manager, params);
        } else if(methodName.equals(METHOD_UPDATE_GATEWAY_BY_ID)){
            updateDBRegisteredGateway(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_GATEWAY_BY_INC_ID)){
            removeDBRegisteredGateway(manager, params);
        }
        else if(methodName.equals(METHOD_REMOVE_GATEWAY_BY_NAME)){
            removeDBRegisteredGatewayByName(manager, params);
        }
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_GATEWAY_LIST)){
            result = getDBRegisteredGatewayList(manager);
        } else if(methodName.equals(METHOD_GET_GATEWAY_BY_ID)){
            result = getDBRegisteredGatewayByIncId(manager, params);
        }
        else if(methodName.equals(METHOD_GET_GATEWAY_BY_NAME)){
            result = getDBRegisteredGatewayByName(manager, params);
        }
        

        return result;
    }

}
