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

import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.abstractservice.dao.SmartNodeOfGatewayDAO;
import vitro.vspEngine.service.persistence.DBSmartNodeOfGateway;

import javax.persistence.EntityManager;
import java.util.List;

/**
 */
public class AbstractSmartNodeOfGatewayManager  extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractSmartNodeOfGatewayManager.class);

    private static AbstractSmartNodeOfGatewayManager instance = new AbstractSmartNodeOfGatewayManager();

    private AbstractSmartNodeOfGatewayManager(){
        super();
    }

    public static AbstractSmartNodeOfGatewayManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_REMOVE_SMART_NODE_OF_GATEWAY = "removeSmartNodeOfGateway"; //TODO: to support later
    private static String METHOD_INSERT_SMART_NODE_OF_GATEWAY = "insertSmartNodeOfGateway"; //TODO: to support later
    private static String METHOD_CREATE_SMART_NODE_OF_GATEWAY = "createSmartNodeOfGateway";   //TODO: to support later
    private static String METHOD_UPDATE_SMART_NODE_OF_GATEWAY_BY_ID = "updateSmartNodeOfGateway"; //TODO: to support later
    private static String METHOD_GET_SMART_NODE_OF_GATEWAY_LIST = "getSmartNodeOfGatewayList";
    private static String METHOD_GET_SMART_NODE_OF_GATEWAY_BY_ID = "getSmartNodeOfGatewayById";

    //////////// INTERFACE //////////////////////////////////////
    public DBSmartNodeOfGateway getSmartNodeOfGateway(int DBSmartNodeOfGatewayId){
        logger.debug("getSmartNodeOfGateway - DBSmartNodeOfGatewayId = " + DBSmartNodeOfGatewayId);
        return (DBSmartNodeOfGateway)startResultMethodInTransaction(METHOD_GET_SMART_NODE_OF_GATEWAY_BY_ID, DBSmartNodeOfGatewayId);
    }


    public List<DBSmartNodeOfGateway> getSmartNodeOfGatewayList(){
        logger.debug("getSmartNodeOfGatewayList - Start");
        return (List<DBSmartNodeOfGateway>)startResultMethodInTransaction(METHOD_GET_SMART_NODE_OF_GATEWAY_LIST);
    }


    //////////////  IMPLEMENTATIONS /////////////////////////////
    private DBSmartNodeOfGateway getSmartNodeOfGatewayById(EntityManager manager, Object... params ){
        DBSmartNodeOfGateway result = null;

        int DBSmartNodeOfGatewayId = (Integer)params[0];

        SmartNodeOfGatewayDAO selectionOfSmartNodesDAO = SmartNodeOfGatewayDAO.getInstance();
        result = selectionOfSmartNodesDAO.getSmartNodeOfGateway(manager, DBSmartNodeOfGatewayId);

        return result;
    }


    private List<DBSmartNodeOfGateway> getSmartNodeOfGatewayList(EntityManager manager){

        SmartNodeOfGatewayDAO selectionOfSmartNodesDAO = SmartNodeOfGatewayDAO.getInstance();

        List<DBSmartNodeOfGateway> result = selectionOfSmartNodesDAO.getSmartNodeOfGatewayList(manager);

        return result;
    }


    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
/*        if(methodName.equals(METHOD_INSERT_SMART_NODE_OF_GATEWAY)){
            insertSmartNodeOfGateway(manager, params);
        } else if(methodName.equals(METHOD_CREATE_SMART_NODE_OF_GATEWAY)){
            createSmartNodeOfGateway(manager, params);
        } else if(methodName.equals(METHOD_UPDATE_SMART_NODE_OF_GATEWAY_BY_ID)){
            updateSmartNodeOfGateway(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_SMART_NODE_OF_GATEWAY)){
            removeSmartNodeOfGateway(manager, params);
        }*/
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_SMART_NODE_OF_GATEWAY_LIST)){
            result = getSmartNodeOfGatewayList(manager);
        } else if(methodName.equals(METHOD_GET_SMART_NODE_OF_GATEWAY_BY_ID)){
            result = getSmartNodeOfGatewayById(manager, params);
        }

        return result;
    }
}
