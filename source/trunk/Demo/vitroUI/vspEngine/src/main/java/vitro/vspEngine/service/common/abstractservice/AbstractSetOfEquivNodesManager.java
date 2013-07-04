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
import vitro.vspEngine.service.common.abstractservice.dao.SetOfEquivNodesDAO;
import vitro.vspEngine.service.common.abstractservice.model.SetOfEquivalentSensorNodes;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;

import javax.persistence.EntityManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 */
public class AbstractSetOfEquivNodesManager extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractSetOfEquivNodesManager.class);

    private static AbstractSetOfEquivNodesManager instance = new AbstractSetOfEquivNodesManager();

    private AbstractSetOfEquivNodesManager(){
        super();
    }

    public static AbstractSetOfEquivNodesManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_MARK_REMOVED_SET_OF_EQUIV_NODES = "updateSetOfEquivNodesMarkDeleted";
    private static String METHOD_REMOVE_SET_OF_EQUIV_NODES = "removeSetOfEquivNodes"; //TODO: to support later
    private static String METHOD_INSERT_SET_OF_EQUIV_NODES = "insertSetOfEquivNodes"; //TODO: to support later
    private static String METHOD_CREATE_SET_OF_EQUIV_NODES = "createSetOfEquivNodes";   //TODO: to support later
    private static String METHOD_CREATE_SET_OF_EQUIV_NODES_RET_ID = "createSetOfEquivNodesReturnId";   //TODO: to support later
    private static String METHOD_UPDATE_SET_OF_EQUIV_NODES_BY_ID = "updateSetOfEquivNodes"; //TODO: to support later
    private static String METHOD_GET_SET_OF_EQUIV_NODES_LIST = "getSetOfEquivNodesList";
    private static String METHOD_GET_SET_OF_EQUIV_NODES_BY_ID = "getSetOfEquivNodesById";
    private static String METHOD_GET_SET_OF_EQUIV_NODES_BY_VGWID = "getSetOfEquivNodesListForGwId";

    //////////// INTERFACE //////////////////////////////////////
    public SetOfEquivalentSensorNodes getSetOfEquivNodes(int dbSetOfEquivNodesId){
        logger.debug("getSetOfEquivNodes - SetOfEquivNodesId = " + Integer.toString(dbSetOfEquivNodesId));
        return (SetOfEquivalentSensorNodes)startResultMethodInTransaction(METHOD_GET_SET_OF_EQUIV_NODES_BY_ID, dbSetOfEquivNodesId);
    }


    public List<SetOfEquivalentSensorNodes> getSetOfEquivNodesList(){
        logger.debug("getSetOfEquivNodesList - Start");
        return (List<SetOfEquivalentSensorNodes>)startResultMethodInTransaction(METHOD_GET_SET_OF_EQUIV_NODES_LIST);
    }

    public List<SetOfEquivalentSensorNodes> getSetOfEquivNodesListForGwId(String pVgwId){
        logger.debug("getSetOfEquivNodesListForGwId - Start: VGW: "+ pVgwId);
        return (List<SetOfEquivalentSensorNodes>)startResultMethodInTransaction(METHOD_GET_SET_OF_EQUIV_NODES_BY_VGWID, pVgwId);
    }

    public void updateSetOfEquivNodesMarkDeleted(int dbSetOfEquivNodesId){
        logger.debug("updateSetOfEquivNodesMarkDeleted - dbSetOfEquivNodesId = " + Integer.toString(dbSetOfEquivNodesId));
        startVoidMethodInTransaction(METHOD_MARK_REMOVED_SET_OF_EQUIV_NODES, dbSetOfEquivNodesId);
    }

    public void removeSetOfEquivNodes(int dbSetOfEquivNodesId){
        logger.debug("removeSetOfEquivNodes - SetOfEquivNodesId = " + Integer.toString(dbSetOfEquivNodesId));
        startVoidMethodInTransaction(METHOD_REMOVE_SET_OF_EQUIV_NODES, dbSetOfEquivNodesId);
    }

    public int createSetOfEquivNodesReturnId(String vgwId, DBSelectionOfSmartNodes pInterchngblNodes, Date pTimestampUpdateLocal /*, Date pTimestampSynchedRemotely*/){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String localTS = "";
        String remoteTS = "";
        if(pTimestampUpdateLocal != null) {
            localTS = df.format(pTimestampUpdateLocal);
        }
        //if(pTimestampSynchedRemotely !=null) {
        //    remoteTS = df.format(pTimestampSynchedRemotely);
        //}

        logger.debug("createSetOfEquivNodesReturnId - Vgwid = " + vgwId + "; Ts local= " + localTS /*+ "; Ts remote = " + remoteTS +  ";" */);
        return (Integer)startResultMethodInTransaction(METHOD_CREATE_SET_OF_EQUIV_NODES_RET_ID, vgwId, pInterchngblNodes, pTimestampUpdateLocal /*, pTimestampSynchedRemotely*/);
    }

    public void createSetOfEquivNodes(String vgwId, DBSelectionOfSmartNodes pInterchngblNodes, Date pTimestampUpdateLocal, Date pTimestampSynchedRemotely){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String localTS = "";
        String remoteTS = "";
        if(pTimestampUpdateLocal != null) {
            localTS = df.format(pTimestampUpdateLocal);
        }
        if(pTimestampSynchedRemotely !=null) {
            remoteTS = df.format(pTimestampSynchedRemotely);
        }
        logger.debug("createSetOfEquivNodesReturnId - Vgwid = \" + vgwId + \"; Ts local= " + localTS + "; Ts remote = " + remoteTS +  ";");
        startVoidMethodInTransaction(METHOD_CREATE_SET_OF_EQUIV_NODES, vgwId, pInterchngblNodes, pTimestampUpdateLocal, pTimestampSynchedRemotely);
    }

    public void updateSetOfEquivNodes(int dbSetOfEquivNodesId, long timestampConfirm){
        logger.debug("updateSetOfEquivNodes - SetOfEquivNodesId = " + Integer.toString(dbSetOfEquivNodesId) + "; timestampConfirm = " + Long.toString(timestampConfirm) + ";");
        startVoidMethodInTransaction(METHOD_UPDATE_SET_OF_EQUIV_NODES_BY_ID, dbSetOfEquivNodesId, timestampConfirm);
    }

    //////////////  IMPLEMENTATIONS /////////////////////////////
    private SetOfEquivalentSensorNodes getSetOfEquivNodesById(EntityManager manager, Object... params ){
        SetOfEquivalentSensorNodes result = null;

        int dbSetOfEquivNodesId = (Integer)params[0];

        SetOfEquivNodesDAO selectionOfSmartNodesDAO = SetOfEquivNodesDAO.getInstance();
        result = selectionOfSmartNodesDAO.getSetOfEquivNodes(manager, dbSetOfEquivNodesId);

        return result;
    }


    private List<SetOfEquivalentSensorNodes> getSetOfEquivNodesList(EntityManager manager){

        SetOfEquivNodesDAO selectionOfSmartNodesDAO = SetOfEquivNodesDAO.getInstance();

        List<SetOfEquivalentSensorNodes> result = selectionOfSmartNodesDAO.getSetOfEquivNodesList(manager);

        return result;
    }

    private List<SetOfEquivalentSensorNodes> getSetOfEquivNodesListForGwId(EntityManager manager, Object... params ){
        String vgwId = (String) params[0];
        SetOfEquivNodesDAO selectionOfSmartNodesDAO = SetOfEquivNodesDAO.getInstance();

        List<SetOfEquivalentSensorNodes> result = selectionOfSmartNodesDAO.getSetOfEquivNodesListForVgwId(manager, vgwId );

        return result;
    }

    //.................
    private void insertSetOfEquivNodesImpl(EntityManager manager, Object... params ){
        SetOfEquivalentSensorNodes setOfEquivalentSensorNodes = (SetOfEquivalentSensorNodes) params[0];
        manager.merge(setOfEquivalentSensorNodes);
    }

    private int createSetOfEquivNodesReturnId(EntityManager manager, Object... params ){
        logger.debug(" createSetOfEquivNodesReturnId - BEFORE parsing parameters ");

        String vgwId = (String) params[0];
        DBSelectionOfSmartNodes currSelectionSmNodes = (DBSelectionOfSmartNodes)params[1];
        Date pTimestampLocal= (Date) params[2];
        //Date pTimestampRemote = (params[3] == null )? null: (Date) params[3];

        SetOfEquivalentSensorNodes setOfEquivalentSensorNodes = new SetOfEquivalentSensorNodes();
        setOfEquivalentSensorNodes.setVgwId(vgwId);
        setOfEquivalentSensorNodes.setInterchngblNodes(currSelectionSmNodes);
        setOfEquivalentSensorNodes.setTimestampUpdateLocal(pTimestampLocal);  // when the change was made at the VSP
        //setOfEquivalentSensorNodes.setTimestampSynchedRemotely();

        SetOfEquivalentSensorNodes copyOfSetOfEquivalentSensorNodes = manager.merge(setOfEquivalentSensorNodes);
        //if(copyOfSetOfEquivalentSensorNodes != null) {
       //     logger.debug("createSetOfEquivNodesReturnId - new id? : " + Integer.toString(copyOfSetOfEquivalentSensorNodes.getId()));
       // } else {
       //     logger.debug("createSetOfEquivNodesReturnId -  Null Copy object!");
       // }
        return copyOfSetOfEquivalentSensorNodes.getId();
    }


    private void createSetOfEquivNodes(EntityManager manager, Object... params ){
        createSetOfEquivNodesReturnId(manager, params );

    }


    private void updateSetOfEquivNodes(EntityManager manager, Object... params ){
        logger.info("updateSetOfEquivNodes ");
        //TODO:
        Integer instanceId = (Integer) params[0];
        Long pTimestampRemoteLong = (params[1] == null )? null: (Long) params[1];
        Date pTimestampRemote = pTimestampRemoteLong ==null? null: new Date(pTimestampRemoteLong);

        SetOfEquivalentSensorNodes setOfEquivalentSensorNodes = manager.find(SetOfEquivalentSensorNodes.class, instanceId);
        if(setOfEquivalentSensorNodes!=null) {
            setOfEquivalentSensorNodes.setTimestampSynchedRemotely(pTimestampRemote);  //when the change was confirmed from VGW
        }
//
//
        manager.merge(setOfEquivalentSensorNodes);
    }

    private void updateSetOfEquivNodesMarkDeleted(EntityManager manager, Object... params ){

        Integer setId = (Integer) params[0];
        Date pTimestampVSPLocal =  new Date();
        SetOfEquivalentSensorNodes setOfEquivalentSensorNodes = manager.find(SetOfEquivalentSensorNodes.class, setId);

        if(setOfEquivalentSensorNodes!=null) {
            setOfEquivalentSensorNodes.setMarkedTobeDeleted(true);
            setOfEquivalentSensorNodes.setTimestampUpdateLocal(pTimestampVSPLocal);
        }
        manager.merge(setOfEquivalentSensorNodes);
    }

    private void removeSetOfEquivNodes(EntityManager manager, Object... params ){

        Integer setId = (Integer) params[0];

        SetOfEquivalentSensorNodes setOfEquivalentSensorNodes= manager.find(SetOfEquivalentSensorNodes.class, setId);
        if(setOfEquivalentSensorNodes!=null) {
            manager.remove(setOfEquivalentSensorNodes);
        }
    }


    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
        if(methodName.equals(METHOD_INSERT_SET_OF_EQUIV_NODES)){
            insertSetOfEquivNodesImpl(manager, params);
        } else if(methodName.equals(METHOD_CREATE_SET_OF_EQUIV_NODES)){
            createSetOfEquivNodes(manager, params);
        }
         else if(methodName.equals(METHOD_UPDATE_SET_OF_EQUIV_NODES_BY_ID)){
            updateSetOfEquivNodes(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_SET_OF_EQUIV_NODES)){
            removeSetOfEquivNodes(manager, params);
        } else if(methodName.equals(METHOD_MARK_REMOVED_SET_OF_EQUIV_NODES)) {
            updateSetOfEquivNodesMarkDeleted(manager, params);
        }
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_SET_OF_EQUIV_NODES_LIST)){
            result = getSetOfEquivNodesList(manager);
        } else if(methodName.equals(METHOD_GET_SET_OF_EQUIV_NODES_BY_ID)){
            result = getSetOfEquivNodesById(manager, params);
        } else if(methodName.equals(METHOD_GET_SET_OF_EQUIV_NODES_BY_VGWID)) {
            result = getSetOfEquivNodesListForGwId(manager, params);
        } else if(methodName.equals(METHOD_CREATE_SET_OF_EQUIV_NODES_RET_ID)){
            result = createSetOfEquivNodesReturnId(manager, params);
        }

        return result;
    }
}
