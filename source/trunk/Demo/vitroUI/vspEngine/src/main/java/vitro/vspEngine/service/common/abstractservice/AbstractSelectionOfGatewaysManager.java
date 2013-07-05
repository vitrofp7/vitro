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
import vitro.vspEngine.service.common.abstractservice.dao.SelectionOfGatewaysDAO;
import vitro.vspEngine.service.persistence.DBSelectionOfGateways;

import javax.persistence.EntityManager;
import java.util.List;

/**
 */
public class AbstractSelectionOfGatewaysManager extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractSelectionOfGatewaysManager.class);

    private static AbstractSelectionOfGatewaysManager instance = new AbstractSelectionOfGatewaysManager();

    private AbstractSelectionOfGatewaysManager(){
        super();
    }

    public static AbstractSelectionOfGatewaysManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_REMOVE_SELECTION_OF_GATEWAYS = "removeSelectionOfGateways"; //TODO: to support later
    private static String METHOD_INSERT_SELECTION_OF_GATEWAYS = "insertSelectionOfGateways"; //TODO: to support later
    private static String METHOD_CREATE_SELECTION_OF_GATEWAYS = "createSelectionOfGateways";   //TODO: to support later
    private static String METHOD_UPDATE_SELECTION_OF_GATEWAYS_BY_ID = "updateSelectionOfGateways"; //TODO: to support later
    private static String METHOD_GET_SELECTION_OF_GATEWAYS_LIST = "getSelectionOfGatewaysList";
    private static String METHOD_GET_SELECTION_OF_GATEWAYS_BY_ID = "getSelectionOfGatewaysById";

    //////////// INTERFACE //////////////////////////////////////
    public DBSelectionOfGateways getSelectionOfGateways(int dbSelectionOfGatewaysId){
        logger.debug("getSelectionOfGateways - dbSelectionOfGatewaysId = " + dbSelectionOfGatewaysId);
        return (DBSelectionOfGateways)startResultMethodInTransaction(METHOD_GET_SELECTION_OF_GATEWAYS_BY_ID, dbSelectionOfGatewaysId);
    }


    public List<DBSelectionOfGateways> getSelectionOfGatewaysList(){
        logger.debug("getSelectionOfGatewaysList - Start");
        return (List<DBSelectionOfGateways>)startResultMethodInTransaction(METHOD_GET_SELECTION_OF_GATEWAYS_LIST);
    }


    //////////////  IMPLEMENTATIONS /////////////////////////////
    private DBSelectionOfGateways getSelectionOfGatewaysById(EntityManager manager, Object... params ){
        DBSelectionOfGateways result = null;

        int dbSelectionOfGatewaysId = (Integer)params[0];

        SelectionOfGatewaysDAO selectionOfGatewaysDAO = SelectionOfGatewaysDAO.getInstance();
        result = selectionOfGatewaysDAO.getSelectionOfGateways(manager, dbSelectionOfGatewaysId);

        return result;
    }


    private List<DBSelectionOfGateways> getSelectionOfGatewaysList(EntityManager manager){

        SelectionOfGatewaysDAO selectionOfGatewaysDAO = SelectionOfGatewaysDAO.getInstance();

        List<DBSelectionOfGateways> result = selectionOfGatewaysDAO.getSelectionOfGatewaysList(manager);

        return result;
    }


    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
/*        if(methodName.equals(METHOD_INSERT_SELECTION_OF_GATEWAYS)){
            insertSelectionOfGateways(manager, params);
        } else if(methodName.equals(METHOD_CREATE_SELECTION_OF_GATEWAYS)){
            createSelectionOfGateways(manager, params);
        } else if(methodName.equals(METHOD_UPDATE_SELECTION_OF_GATEWAYS_BY_ID)){
            updateSelectionOfGateways(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_SELECTION_OF_GATEWAYS)){
            removeSelectionOfGateways(manager, params);
        }*/
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_SELECTION_OF_GATEWAYS_LIST)){
            result = getSelectionOfGatewaysList(manager);
        } else if(methodName.equals(METHOD_GET_SELECTION_OF_GATEWAYS_BY_ID)){
            result = getSelectionOfGatewaysById(manager, params);
        }

        return result;
    }
    
}
