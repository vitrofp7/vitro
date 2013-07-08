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
import vitro.vspEngine.service.common.abstractservice.dao.ComposedServiceDAO;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/*
 * This class uses JPA to persist data
 *
 * It is similar to the AbstractServiceManager class but it refers to Composed Services (From serviceInstances)
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
public class AbstractComposedServiceManager extends JPAManager{

    private Logger logger = Logger.getLogger(AbstractComposedServiceManager.class);

    private static AbstractComposedServiceManager instance = new AbstractComposedServiceManager();

    private AbstractComposedServiceManager(){
        super();
    }

    public static AbstractComposedServiceManager getInstance(){
        return instance;
    }

    ///////////////////////////////////////

    private static String METHOD_REMOVE_COMPOSED_SERVICE = "removeComposedService";
    private static String METHOD_INSERT_COMPOSED_SERVICE = "insertComposedService";
    private static String METHOD_CREATE_COMPOSED_SERVICE = "createComposedService";
    private static String METHOD_CREATE_COMPOSED_SERVICE_RET_ID = "createComposedServiceReturnId";
    private static String METHOD_GET_COMPOSED_SERVICE_LIST = "getComposedServiceList";
    private static String METHOD_GET_COMPOSED_SERVICE_BY_ID = "getComposedServiceById";
    private static String METHOD_UPDATE_COMPOSED_SERVICE_BY_ID = "updateComposedService";

    //////////// INTERFACE //////////////////////////////////////

    public void removeComposedService(int composedServiceId){
        logger.debug("removeComposedService - ComposedService = " + composedServiceId);
        startVoidMethodInTransaction(METHOD_REMOVE_COMPOSED_SERVICE, composedServiceId);
    }

    public List<FullComposedService> getComposedServiceList(){
        logger.debug("getComposedServiceList - Start");
        return (List<FullComposedService>)startResultMethodInTransaction(METHOD_GET_COMPOSED_SERVICE_LIST);
    }

    public int createComposedServiceReturnId(String serviceName, String serviceFriendlyName,  List<String> searchTag, List<ServiceInstance> listOfPartialServices, boolean pGlobalDTNEnableRequest, boolean pPreDeployedFlag, String pPreDeployedId, boolean pGlobalContintuation, boolean pGlobalEncryption, boolean pGlobalAsynchronous){
        logger.debug("createComposedServiceReturnId - serviceName = " + serviceName + "; serviceFriendlyName = " + serviceFriendlyName +  "; searchTag = " + searchTag + "; partialServicesList = " + listOfPartialServices );
        return (Integer)startResultMethodInTransaction(METHOD_CREATE_COMPOSED_SERVICE_RET_ID, serviceName, serviceFriendlyName, searchTag, listOfPartialServices,  pGlobalDTNEnableRequest, pPreDeployedFlag,  pPreDeployedId,  pGlobalContintuation,  pGlobalEncryption,  pGlobalAsynchronous);
    }

    public void createComposedService(String serviceName, String serviceFriendlyName,  List<String> searchTag, List<ServiceInstance> listOfPartialServices, boolean pGlobalDTNEnableRequest, boolean pPreDeployedFlag, String pPreDeployedId, boolean pGlobalContintuation, boolean pGlobalEncryption, boolean pGlobalAsynchronous){
        logger.debug("createComposedService - serviceName = " + serviceName + "; serviceFriendlyName = " + serviceFriendlyName +  "; searchTag = " + searchTag + "; partialServicesList = " + listOfPartialServices );
        startVoidMethodInTransaction(METHOD_CREATE_COMPOSED_SERVICE, serviceName, serviceFriendlyName, searchTag, listOfPartialServices, pGlobalDTNEnableRequest, pPreDeployedFlag,  pPreDeployedId,  pGlobalContintuation,  pGlobalEncryption,  pGlobalAsynchronous);
    }

    public void updateComposedService(int instanceId, String serviceName, String serviceFriendlyName,  List<String> searchTag, List<ServiceInstance> listOfPartialServices, boolean pGlobalDTNEnableRequest ){
        logger.debug("updateComposedService - serviceName = " + serviceName + "; serviceFriendlyName = " + serviceFriendlyName + "; searchTag = " + searchTag + "; partialServicesList = " + listOfPartialServices );
        startVoidMethodInTransaction(METHOD_UPDATE_COMPOSED_SERVICE_BY_ID, serviceName, serviceFriendlyName, searchTag,  listOfPartialServices, pGlobalDTNEnableRequest);
    }

    public void insertComposedService(FullComposedService instance){
        startVoidMethodInTransaction(METHOD_INSERT_COMPOSED_SERVICE, instance);
    }

    public FullComposedService getComposedService(int ComposedServiceId){
        logger.debug("getComposedService - ComposedServiceId = " + ComposedServiceId);
        return (FullComposedService)startResultMethodInTransaction(METHOD_GET_COMPOSED_SERVICE_BY_ID, ComposedServiceId);
    }

    //////////////  IMPLEMENTATIONS ///////////////////
    private FullComposedService getComposedServiceById(EntityManager manager, Object... params ){
        FullComposedService result = null;

        int instanceId = (Integer)params[0];

        ComposedServiceDAO composedServiceDAO = ComposedServiceDAO.getInstance();
        result = composedServiceDAO.getComposedService(manager, instanceId);

        return result;
    }

    private void insertComposedServiceImpl(EntityManager manager, Object... params ){
        FullComposedService composedService = (FullComposedService) params[0];
        manager.merge(composedService);
    }

    private int  createComposedServiceReturnId(EntityManager manager, Object... params ){
        String composedServiceName = (String) params[0];
        String composedServiceFriendlyName = (String) params[1];
        //logger.debug(" createComposedServiceReturnId - midway 001 ");
        List<String> composedSearchTag = (List<String>) params[2];
        //logger.debug(" createComposedServiceReturnId - midway 002 ");
        List<ServiceInstance> listOfPartialServices = (List<ServiceInstance>) params[3];
        //logger.debug(" createComposedServiceReturnId - midway 003 ");
        boolean pGlobalDTNEnableRequest = (Boolean) params[4]  ;
        boolean pPreDeployedFlag = (Boolean)  params[5];
        String pPreDeployedId = (String)  params[6];
        boolean pGlobalContintuation = (Boolean)  params[7];
        boolean pGlobalEncryption  = (Boolean)  params[8];
        boolean pGlobalAsynchronous = (Boolean)  params[9];
        //logger.debug(" createComposedServiceReturnId - after parsing parameters ");

//
        FullComposedService fullComposedServiceInstance = new FullComposedService();
        fullComposedServiceInstance.setName(composedServiceName);
        fullComposedServiceInstance.setFriendlyName(composedServiceFriendlyName);
        fullComposedServiceInstance.setSearchTagList(composedSearchTag);
        fullComposedServiceInstance.setServiceInstanceList(listOfPartialServices);
        fullComposedServiceInstance.setGlobalDTNEnableRequest(pGlobalDTNEnableRequest);
        fullComposedServiceInstance.setPredeployed(pPreDeployedFlag);
        fullComposedServiceInstance.setPredeployedId(pPreDeployedId);
        fullComposedServiceInstance.setGlobalContinuationEnableRequest(pGlobalContintuation);
        fullComposedServiceInstance.setGlobalEncryptionEnableRequest(pGlobalEncryption);
        fullComposedServiceInstance.setGlobalAsynchronousEnableRequest(pGlobalAsynchronous);
        //logger.debug("createComposedServiceReturnId -Set all parameters !" );
//
//          TODO: Ideally we want to lookup existing partial services to compose this composed service, and create the ones that don't exist. (???)
//              for now, we create all of our required partial services for this composed service.
//        ServiceInstanceDAO serviceInstanceDAO = ServiceInstanceDAO.getInstance();
//
//        if(listOfPartialServices != null){
//            for(ServiceInstance onePartialService: listOfPartialServices){
//                  // todo: we need a way to match with existing stored services
//                ServiceInstance storedPartialService = serviceInstanceDAO.getServiceInstance(manager, onePartialService.getId());
//                if(storedPartialService !=null) {
//                   fullComposedServiceInstance.getServiceInstanceList().add(storedPartialService);
//                }
//            }
//        }
//
//
        FullComposedService copyOffullComposedServiceInstance = manager.merge(fullComposedServiceInstance);
        //logger.debug(" createComposedServiceReturnId - composed service new id? : " + Integer.toString(copyOffullComposedServiceInstance.getId()));
        return copyOffullComposedServiceInstance.getId();
    }


    private void createComposedService(EntityManager manager, Object... params ){
        createComposedServiceReturnId(manager, params );

    }


    private void updateComposedService(EntityManager manager, Object... params ){
        logger.info("updateComposedService ");
        //TODO:
        String composedServiceName = (String) params[0];
        String composedServiceFriendlyName = (String) params[1];
        List<String> composedSearchTag = (List<String>) params[2];
        List<ServiceInstance> listOfPartialServices = (List<ServiceInstance>) params[3];
        Integer instanceId = (Integer) params[4];

//
        FullComposedService fullComposedServiceInstance = manager.find(FullComposedService.class, instanceId);
        fullComposedServiceInstance.setName(composedServiceName);
        fullComposedServiceInstance.setFriendlyName(composedServiceFriendlyName);
        fullComposedServiceInstance.setSearchTagList(composedSearchTag);
        fullComposedServiceInstance.setServiceInstanceList(listOfPartialServices);
//
//
        List<ServiceInstance> toSavePartialServices = new ArrayList<ServiceInstance>(listOfPartialServices.size());
        for (ServiceInstance serviceInstance : listOfPartialServices) {
            toSavePartialServices.add(manager.merge(serviceInstance));
            logger.info("merged Service Instance = " + serviceInstance);
        }
//
//        TODO: Again, ideally we want to lookup existing partial services to compose this composed service, and create the ones that don't exist. (???)
//              for now, we create all of our required partial services for this composed service.
//        ServiceInstanceDAO serviceInstanceDAO = ServiceInstanceDAO.getInstance();
//
//        if(listOfPartialServices != null){
//            for(ServiceInstance onePartialService: listOfPartialServices){
//                  // todo: we need a way to match with existing stored services
//                ServiceInstance storedPartialService = serviceInstanceDAO.getServiceInstance(manager, onePartialService.getId());
//                if(storedPartialService !=null) {
//                   fullComposedServiceInstance.getServiceInstanceList().add(storedPartialService);
//                }
//            }
//        }


    }

    private void removeComposedService(EntityManager manager, Object... params ){

        Integer composedServiceId = (Integer) params[0];

        FullComposedService composedService= manager.find(FullComposedService.class, composedServiceId);
        manager.remove(composedService);

    }

    private List<FullComposedService> getComposedServiceList(EntityManager manager){

        ComposedServiceDAO serviceInstanceDAO = ComposedServiceDAO.getInstance();

        List<FullComposedService> result = serviceInstanceDAO.getComposedServiceList(manager);

        return result;
    }


    ///////////// MAPPING METHODS ////////////////////////////

    protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
        if(methodName.equals(METHOD_INSERT_COMPOSED_SERVICE)){
            insertComposedServiceImpl(manager, params);
        } else if(methodName.equals(METHOD_CREATE_COMPOSED_SERVICE)){
            createComposedService(manager, params);
        } else if(methodName.equals(METHOD_UPDATE_COMPOSED_SERVICE_BY_ID)){
            updateComposedService(manager, params);
        } else if(methodName.equals(METHOD_REMOVE_COMPOSED_SERVICE)){
            removeComposedService(manager, params);
        }
    }

    protected Object callResultMethod(EntityManager manager, String methodName, Object... params){

        Object result = null;

        if(methodName.equals(METHOD_GET_COMPOSED_SERVICE_LIST)){
            result = getComposedServiceList(manager);
        } else if(methodName.equals(METHOD_GET_COMPOSED_SERVICE_BY_ID)){
            result = getComposedServiceById(manager, params);
        } else if(methodName.equals(METHOD_CREATE_COMPOSED_SERVICE_RET_ID)) {
            result = createComposedServiceReturnId(manager, params);
        }

        return result;
    }

}
