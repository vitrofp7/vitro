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
import vitro.vspEngine.service.common.abstractservice.dao.CapabilityDAO;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;

import javax.persistence.EntityManager;
import java.util.List;

/**
 */
public class AbstractCapabilityManager extends JPAManager{
    private Logger logger = Logger.getLogger(AbstractCapabilityManager.class);

    private static AbstractCapabilityManager instance = new AbstractCapabilityManager();

    private AbstractCapabilityManager(){
        super();
    }

    public static AbstractCapabilityManager getInstance(){
        return instance;
    }
    ///////////////////////////////////////
    private static String METHOD_REMOVE_CAPABILITY = "removeCapability"; //TODO: to support later
    private static String METHOD_INSERT_CAPABILITY = "insertCapability"; //TODO: to support later
    private static String METHOD_CREATE_CAPABILITY = "createCapability";   //TODO: to support later
    private static String METHOD_UPDATE_CAPABILITY_BY_ID = "updateCapability"; //TODO: to support later
    private static String METHOD_GET_CAPABILITY_LIST = "getCapabilityList";
    private static String METHOD_GET_CAPABILITY_BY_ID = "getCapabilityById";

    //////////// INTERFACE //////////////////////////////////////
    public Capability getCapability(int capabilityId){
        logger.debug("getCapability - capabilityId = " + capabilityId);
        return (Capability)startResultMethodInTransaction(METHOD_GET_CAPABILITY_BY_ID, capabilityId);
    }


    public List<Capability> getCapabilityList(){
        logger.debug("getCapabilityList - Start");
        return (List<Capability>)startResultMethodInTransaction(METHOD_GET_CAPABILITY_LIST);
    }


    //////////////  IMPLEMENTATIONS /////////////////////////////
    private Capability getCapabilityById(EntityManager manager, Object... params ){
        Capability result = null;

        int capabilityId = (Integer)params[0];

        CapabilityDAO capabilityDAO = CapabilityDAO.getInstance();
        result = capabilityDAO.getCapability(manager, capabilityId);

        return result;
    }


    private List<Capability> getCapabilityList(EntityManager manager){

        CapabilityDAO capabilityDAO = CapabilityDAO.getInstance();

        List<Capability> result = capabilityDAO.getCapabilityList(manager);

        return result;
    }


    ///////////// MAPPING METHODS ///////////////////////////////
    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
/*        if(methodName.equals(METHOD_INSERT_CAPABILITY)){
            insertCapability(manager, params);
        } else if(methodName.equals(METHOD_CREATE_CAPABILITY)){
            createCapability(manager, params);
        } else if(methodName.equals(METHOD_UPDATE_CAPABILITY_BY_ID)){
            updateCapability(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_CAPABILITY)){
            removeCapability(manager, params);
        }*/
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_CAPABILITY_LIST)){
            result = getCapabilityList(manager);
        } else if(methodName.equals(METHOD_GET_CAPABILITY_BY_ID)){
            result = getCapabilityById(manager, params);
        }

        return result;
    }


}
