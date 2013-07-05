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

import vitro.vspEngine.service.common.abstractservice.dao.SelectionOfSmartNodesDAO;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;

import javax.persistence.EntityManager;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 */
public class AbstractSelectionOfSmartNodesManager extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractSelectionOfSmartNodesManager.class);

    private static AbstractSelectionOfSmartNodesManager instance = new AbstractSelectionOfSmartNodesManager();

    private AbstractSelectionOfSmartNodesManager(){
        super();
    }

    public static AbstractSelectionOfSmartNodesManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_REMOVE_SELECTION_OF_SMART_NODES = "removeSelectionOfSmartNodes"; //TODO: to support later
    private static String METHOD_INSERT_SELECTION_OF_SMART_NODES = "insertSelectionOfSmartNodes"; //TODO: to support later
    private static String METHOD_CREATE_SELECTION_OF_SMART_NODES = "createSelectionOfSmartNodes";   //TODO: to support later
    private static String METHOD_UPDATE_SELECTION_OF_SMART_NODES_BY_ID = "updateSelectionOfSmartNodes"; //TODO: to support later
    private static String METHOD_GET_SELECTION_OF_SMART_NODES_LIST = "getSelectionOfSmartNodesList";
    private static String METHOD_GET_SELECTION_OF_SMART_NODES_BY_ID = "getSelectionOfSmartNodesById";

    //////////// INTERFACE //////////////////////////////////////
    public DBSelectionOfSmartNodes getSelectionOfSmartNodes(int dbSelectionOfSmartNodesId){
        logger.debug("getSelectionOfSmartNodes - dbSelectionOfSmartNodesId = " + dbSelectionOfSmartNodesId);
        return (DBSelectionOfSmartNodes)startResultMethodInTransaction(METHOD_GET_SELECTION_OF_SMART_NODES_BY_ID, dbSelectionOfSmartNodesId);
    }


    public List<DBSelectionOfSmartNodes> getSelectionOfSmartNodesList(){
        logger.debug("getSelectionOfSmartNodesList - Start");
        return (List<DBSelectionOfSmartNodes>)startResultMethodInTransaction(METHOD_GET_SELECTION_OF_SMART_NODES_LIST);
    }


    //////////////  IMPLEMENTATIONS /////////////////////////////
    private DBSelectionOfSmartNodes getSelectionOfSmartNodesById(EntityManager manager, Object... params ){
        DBSelectionOfSmartNodes result = null;

        int dbSelectionOfSmartNodesId = (Integer)params[0];

        SelectionOfSmartNodesDAO selectionOfSmartNodesDAO = SelectionOfSmartNodesDAO.getInstance();
        result = selectionOfSmartNodesDAO.getSelectionOfSmartNodes(manager, dbSelectionOfSmartNodesId);

        return result;
    }


    private List<DBSelectionOfSmartNodes> getSelectionOfSmartNodesList(EntityManager manager){

        SelectionOfSmartNodesDAO selectionOfSmartNodesDAO = SelectionOfSmartNodesDAO.getInstance();

        List<DBSelectionOfSmartNodes> result = selectionOfSmartNodesDAO.getSelectionOfSmartNodesList(manager);

        return result;
    }


    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
/*        if(methodName.equals(METHOD_INSERT_SELECTION_OF_SMART_NODES)){
            insertSelectionOfSmartNodes(manager, params);
        } else if(methodName.equals(METHOD_CREATE_SELECTION_OF_SMART_NODES)){
            createSelectionOfSmartNodes(manager, params);
        } else if(methodName.equals(METHOD_UPDATE_SELECTION_OF_SMART_NODES_BY_ID)){
            updateSelectionOfSmartNodes(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_SELECTION_OF_SMART_NODES)){
            removeSelectionOfSmartNodes(manager, params);
        }*/
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_SELECTION_OF_SMART_NODES_LIST)){
            result = getSelectionOfSmartNodesList(manager);
        } else if(methodName.equals(METHOD_GET_SELECTION_OF_SMART_NODES_BY_ID)){
            result = getSelectionOfSmartNodesById(manager, params);
        }

        return result;
    }
}
