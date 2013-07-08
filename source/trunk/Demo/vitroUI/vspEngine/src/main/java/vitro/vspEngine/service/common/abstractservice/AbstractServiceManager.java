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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import vitro.vspEngine.service.common.abstractservice.dao.GatewayDAO;
import vitro.vspEngine.service.common.abstractservice.dao.ServiceInstanceDAO;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;


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
public class AbstractServiceManager extends JPAManager{
	
	private Logger logger = Logger.getLogger(AbstractServiceManager.class);
	
	private static AbstractServiceManager instance = new AbstractServiceManager();
	
	private AbstractServiceManager(){
		super();
	}

	public static AbstractServiceManager getInstance(){
		return instance;
	}
	
	///////////////////////////////////////
	
	private static String METHOD_REMOVE_SERVICE_INSTANCE = "removeServiceInstance";
	private static String METHOD_INSERT_SERVICE_INSTANCE = "insertServiceInstance";
	private static String METHOD_CREATE_SERVICE_INSTANCE = "createServiceInstance";
    private static String METHOD_CREATE_SERVICE_INSTANCE_RET_ID = "createServiceInstanceReturnId";
    private static String METHOD_GET_SERVICE_INSTANCE_LIST = "getInstanceList";
	private static String METHOD_GET_SERVICE_INSTANCE_BY_ID = "getServiceInstanceById";
	private static String METHOD_UPDATE_SERVICE_INSTANCE_BY_ID = "updateServiceInstance";
	
	//////////// INTERFACE //////////////////////////////////////
	
	public void removeServiceInstance(int instanceId){
		logger.debug("removeServiceInstance - serviceinstance = " + instanceId);
		startVoidMethodInTransaction(METHOD_REMOVE_SERVICE_INSTANCE, instanceId);
	}
	
	public List<ServiceInstance> getInstanceList(){
		logger.debug("getInstanceList - Start");
		return (List<ServiceInstance>)startResultMethodInTransaction(METHOD_GET_SERVICE_INSTANCE_LIST);
	}

    public int createServiceInstanceReturnId(String serviceName, List<String> searchTag, List<String> gatewayList, List<Capability> involvedCaps, boolean encryption, boolean allowDTN, boolean tracking, boolean composition, String slaMessage, boolean subscription, long samplingRate, boolean rulesANDforNotify, boolean continuationFlg){
        logger.debug("createServiceInstanceReturnId - serviceName = " + serviceName + "; searchTag = " + searchTag + "; gatewayList = " + gatewayList + "; involvedCaps = " + involvedCaps+ "; allowDTN = " + allowDTN+ "; rulesANDforNotify = " + rulesANDforNotify+ "; continuation = " + continuationFlg);
        return (Integer)startResultMethodInTransaction(METHOD_CREATE_SERVICE_INSTANCE_RET_ID, serviceName, searchTag, gatewayList, involvedCaps, encryption, allowDTN, tracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuationFlg);
    }


	public void createServiceInstance(String serviceName, List<String> searchTag, List<String> gatewayList, List<Capability> involvedCaps, boolean encryption, boolean allowDTN, boolean tracking, boolean composition, String slaMessage, boolean subscription, long samplingRate, boolean rulesANDforNotify, boolean continuationFlg){
		logger.debug("createServiceInstance - serviceName = " + serviceName + "; searchTag = " + searchTag + "; gatewayList = " + gatewayList + "; involvedCaps = " + involvedCaps+ "; allowDTN = " + allowDTN+ "; rulesANDforNotify = " + rulesANDforNotify+ "; continuation = " + continuationFlg);
		startVoidMethodInTransaction(METHOD_CREATE_SERVICE_INSTANCE, serviceName, searchTag, gatewayList, involvedCaps, encryption, allowDTN, tracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuationFlg);
	}
	
	public void updateServiceInstance(int instanceId, String serviceName, List<String> searchTag, List<String> gatewayList, List<Capability> involvedCaps, boolean encryption, boolean allowDTN, boolean tracking, boolean composition, String slaMessage, boolean subscription, long samplingRate, boolean rulesANDforNotify, boolean continuationFlg){
		logger.debug("updateServiceInstance - serviceName = " + serviceName + "; searchTag = " + searchTag + "; gatewayList = " + gatewayList + "; involvedCaps = " + involvedCaps+ "; allowDTN = " + allowDTN+ "; rulesANDforNotify = " + rulesANDforNotify+ "; continuation = " + continuationFlg);
		startVoidMethodInTransaction(METHOD_UPDATE_SERVICE_INSTANCE_BY_ID, serviceName, searchTag, gatewayList, involvedCaps, instanceId, encryption, allowDTN, tracking, composition, slaMessage, subscription, samplingRate, rulesANDforNotify, continuationFlg);
	}
	
	public void insertServiceInstance(ServiceInstance instance){
		startVoidMethodInTransaction(METHOD_INSERT_SERVICE_INSTANCE, instance);
	}
	
	public ServiceInstance getServiceInstance(int serviceInstanceId){
		logger.debug("getServiceInstance - serviceInstanceId = " + serviceInstanceId);
		return (ServiceInstance)startResultMethodInTransaction(METHOD_GET_SERVICE_INSTANCE_BY_ID, serviceInstanceId);
	}
	
	
	////////// IMPLEMENTATIONS ///////////////////////////////////
	
	private ServiceInstance getServiceInstanceById(EntityManager manager, Object... params ){
		ServiceInstance result = null;
		
		int instanceId = (Integer)params[0];
		
		ServiceInstanceDAO serviceInstanceDAO = ServiceInstanceDAO.getInstance();
		result = serviceInstanceDAO.getServiceInstance(manager, instanceId);
		
		return result;
	}
	
	private void insertServiceInstanceImpl(EntityManager manager, Object... params ){
		ServiceInstance serviceInstance = (ServiceInstance) params[0];
		manager.merge(serviceInstance);
	}

    private int  createServiceInstanceReturnId(EntityManager manager, Object... params ){
        String serviceName = (String) params[0];
        List<String> searchTag = (List<String>) params[1];
        List<String> gatewayList = (List<String>) params[2];
        List<Capability> involvedCaps = (List<Capability>) params[3];
        Boolean encryption = (Boolean) params[4];
        Boolean allowDTN = (Boolean) params[5];
        Boolean tracking = (Boolean) params[6];
        Boolean composition = (Boolean) params[7];
        String slaMessage = (String)params[8];
        Boolean subscription = (Boolean)params[9];
        Long samplingRate = (Long)params[10];
        Boolean rulesANDforNotify = (Boolean) params[11];
        Boolean continuation = (Boolean) params[12];

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setName(serviceName);
        serviceInstance.setSearchTagList(searchTag);
        serviceInstance.setObservedCapabilities(involvedCaps);

        serviceInstance.setAllowDTN(allowDTN);
        serviceInstance.setEncryption(encryption);
        serviceInstance.setRfidTracking(tracking);
        serviceInstance.setComposition(composition);
        serviceInstance.setSlaMessage(slaMessage);
        serviceInstance.setSubscriptionEnabled(subscription);
        serviceInstance.setSamplingRate(samplingRate);
        serviceInstance.setRulesANDforNotify(rulesANDforNotify);
        serviceInstance.setContinuation(continuation);

        /*
        GatewayDAO gatewayDAO = GatewayDAO.getInstance();
        if(gatewayList != null){
            for(String gatewayName: gatewayList){

                DBRegisteredGateway currentGateway = gatewayDAO.getGateway(manager, gatewayName);
                serviceInstance.getGatewayList().add(currentGateway);
            }
        }
        */

        ServiceInstance copyOfServiceInstance = manager.merge(serviceInstance);
        logger.debug("serviceInstance new id? : " + Integer.toString(copyOfServiceInstance.getId()));
        return copyOfServiceInstance.getId();
    }

	private void createServiceInstance(EntityManager manager, Object... params ){
        createServiceInstanceReturnId(manager,params );

		
	}
	
	private void updateServiceInstance(EntityManager manager, Object... params ){
		logger.info("updateServiceInstance ");
        Integer instanceId = (Integer) params[1];
        String serviceName = (String) params[2];
		List<String> searchTag = (List<String>) params[3];
		List<String> gatewayList = (List<String>) params[4];
		List<Capability> involvedCaps = (List<Capability>) params[5];
		Boolean encryption = (Boolean) params[6];
		Boolean allowDTN = (Boolean) params[7];
		Boolean tracking = (Boolean) params[8];
		Boolean composition = (Boolean) params[9];
		String slaMessage = (String) params[10];
		Boolean subscription = (Boolean)params[11];
		Long samplingRate = (Long)params[12];
        Boolean rulesANDforNotify = (Boolean) params[13];
        Boolean continuation = (Boolean) params[14];
		
		ServiceInstance serviceInstance = manager.find(ServiceInstance.class, instanceId);
		serviceInstance.setName(serviceName);
		serviceInstance.setSearchTagList(searchTag);
		
		
		List<Capability> toSaveCaps = new ArrayList<Capability>(involvedCaps.size());
		for (Capability capability : involvedCaps) {
			toSaveCaps.add(manager.merge(capability));
			logger.info("merged cap = " + capability);
		}
		
		serviceInstance.setObservedCapabilities(toSaveCaps);
		serviceInstance.setAllowDTN(allowDTN);
		serviceInstance.setEncryption(encryption);
		serviceInstance.setRfidTracking(tracking);
		serviceInstance.setComposition(composition);
		serviceInstance.setSlaMessage(slaMessage);
		serviceInstance.setSubscriptionEnabled(subscription);
		serviceInstance.setSamplingRate(samplingRate);
        serviceInstance.setRulesANDforNotify(rulesANDforNotify);
        serviceInstance.setContinuation(continuation);

        /*
		GatewayDAO gatewayDAO = GatewayDAO.getInstance();
		
		if(gatewayList != null){
			for(String gatewayName: gatewayList){

	            DBRegisteredGateway currentGateway = gatewayDAO.getGateway(manager, gatewayName);
				serviceInstance.getGatewayList().add(currentGateway);
			}
		}
       */
				
	}
	
	private void removeServiceInstance(EntityManager manager, Object... params ){
		
		Integer instanceId = (Integer) params[0];
		
		ServiceInstance serviceInstance = manager.find(ServiceInstance.class, instanceId);
		manager.remove(serviceInstance);
/*		serviceInstance.remove(serviceName);
		serviceInstance.remove(searchTag);
		serviceInstance.remove(gatewayList);
		serviceInstance.remove(involvedCaps);

		//serviceInstance.remove(serviceInstance.getInstance());
*/
				
	}
	
	private List<ServiceInstance> getInstanceList(EntityManager manager){
		
		ServiceInstanceDAO serviceInstanceDAO = ServiceInstanceDAO.getInstance();
		
		List<ServiceInstance> result = serviceInstanceDAO.getInstanceList(manager);
		
		return result;
	}
	
	
	///////////// MAPPING METHODS ////////////////////////////
	
	protected void callVoidMethod(EntityManager manager, String methodName, Object... params){
		if(methodName.equals(METHOD_INSERT_SERVICE_INSTANCE)){
			insertServiceInstanceImpl(manager, params);
		} else if(methodName.equals(METHOD_CREATE_SERVICE_INSTANCE)){
			createServiceInstance(manager, params);
		} else if(methodName.equals(METHOD_UPDATE_SERVICE_INSTANCE_BY_ID)){
			updateServiceInstance(manager, params);
		} else if(methodName.equals(METHOD_REMOVE_SERVICE_INSTANCE)){
			removeServiceInstance(manager, params);
		} 
	}
	
	protected Object callResultMethod(EntityManager manager, String methodName, Object... params){
		
		Object result = null;
		
		if(methodName.equals(METHOD_GET_SERVICE_INSTANCE_LIST)){
			result = getInstanceList(manager);
		} else if(methodName.equals(METHOD_GET_SERVICE_INSTANCE_BY_ID)){
			result = getServiceInstanceById(manager, params);
		} else if(methodName.equals(METHOD_CREATE_SERVICE_INSTANCE_RET_ID))
        {
            result =createServiceInstanceReturnId(manager, params);
        }
		
		return result;
	}

	
	
}
