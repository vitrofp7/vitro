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
package vitro.vgw.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.opengis.sos.v_1_0_0.InsertObservation;
import net.opengis.sos.v_1_0_0.InsertObservationResponse;
import net.opengis.sos.v_1_0_0.RegisterSensor;
import net.opengis.sos.v_1_0_0.RegisterSensorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vitro.vgw.communication.idas.IdasProxy;
import vitro.vgw.communication.request.VgwActivationRequest;
import vitro.vgw.communication.request.VgwRequestObservation;
import vitro.vgw.communication.response.NodeRegistrationResultType;
import vitro.vgw.communication.response.VgwActivationResponse;
import vitro.vgw.communication.response.VgwResponse;
import vitro.vgw.exception.RSControllerException;
import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.model.Node;
import vitro.vgw.model.NodeDescriptor;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.rscontroller.RSController;
import vitro.vgw.rscontroller.RSControllerStub;

/*
 * This implementation register to IDAS each WSN node.
 * 
 * If the gateway has to register a virtual systema abstracting all underlying nodes, this implementation is not correct.
 * */
@Path("/vgw")
public class VitroGatewayService {

	private Logger logger = LoggerFactory.getLogger(getClass()); 
	
	private RSController rsController;
	private IdasProxy idas;
	private SensorMLMessageAdapter sensorMLMessageAdapter;
	
	private String gatewayLogicalName;
	
	private Map<Node, String> idasNodeMapping;
	
	
	public VitroGatewayService(){
		idasNodeMapping = new HashMap<Node, String>();
	}
	
    public RSController getRsController() {
		return rsController;
	}

	public void setRsController(RSController rsController) {
		this.rsController = rsController;
	}
	
	public IdasProxy getIdas() {
		return idas;
	}

	public void setIdas(IdasProxy idas) {
		this.idas = idas;
	}

	public SensorMLMessageAdapter getSensorMLMessageAdapter() {
		return sensorMLMessageAdapter;
	}

	public void setSensorMLMessageAdapter(
			SensorMLMessageAdapter sensorMLMessageAdapter) {
		this.sensorMLMessageAdapter = sensorMLMessageAdapter;
	}

	public String getGatewayLogicalName() {
		return gatewayLogicalName;
	}

	public void setGatewayLogicalName(String gatewayLogicalName) {
		this.gatewayLogicalName = gatewayLogicalName;
	}

	public void init() throws VitroGatewayException{
		logger.debug("init VitroGatewayService");
		//TBD WSI & Sensor Node configurator initialization??
		
		//Scan controlled WSI and associated resources
		List<NodeDescriptor> nodeDescriptorList = rsController.init();
		
		for (NodeDescriptor nodeDescriptor : nodeDescriptorList) {
			RegisterSensor registerSensor = sensorMLMessageAdapter.getRegisterSensorMessage(getGatewayLogicalName(), nodeDescriptor);
			RegisterSensorResponse registerSensorResponse = idas.registerSensor(registerSensor);
			
			String assignedSensorId = registerSensorResponse.getAssignedSensorId();
			
			//Register assigned sensor id to associate subsequent obseravation to the correct node
			//TODO: understand if persistence is needed
			idasNodeMapping.put(nodeDescriptor, assignedSensorId);
		}
    }

	@GET
    @Path("/echo/{input}")
    @Produces("text/plain")
    public String ping(@PathParam("input") String input) {

        return input;
    }

    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("/invokeWSIService")
    public VgwResponse invokeWSIService(VgwRequestObservation request) throws VitroGatewayException {

    	logger.debug("request = {}", request);
        
    	//Get requested resource 
    	String resourceName = request.getObsType().value();
    	Resource requestedResource = Resource.getResource(resourceName);
    	if(requestedResource == null){
    		throw new VitroGatewayException(resourceName + " not managed by VITRO gateway");
    	}
    	
    	List<Observation> observationList = rsController.getWSIData(requestedResource);
    	if(observationList != null && !observationList.isEmpty()){
    		for (Observation observation : observationList) {
    			
    			Node node = observation.getNode();
    			String assignedSensorId = idasNodeMapping.get(node);
    			
    			logger.debug("Node {} -> assignedSensotId {}", node.getId(), assignedSensorId);
    			
    			InsertObservation insertObservation = sensorMLMessageAdapter.getInsertObservationMessage(gatewayLogicalName, assignedSensorId, observation);
    			InsertObservationResponse insertObservationResponse = idas.insertObservation(insertObservation);
    			
    			logger.debug("insertObservationResponse = {}", insertObservationResponse);
			}
    	}
    	
        VgwResponse vgwResponse = new VgwResponse();
        vgwResponse.setSuccess(true);
        
        return vgwResponse;
    }
    
    /*
     * To register sensor in IDAS at runtime
     * */
    @POST
    @Produces("application/xml")
    @Consumes("application/xml")
    @Path("/activateSensor")
    public VgwActivationResponse activateSensor(VgwActivationRequest request) throws VitroGatewayException {

    	VgwActivationResponse vgwResponse = new VgwActivationResponse();
    	vgwResponse.setSuccess(true);
    	
    	logger.debug("request = {}", request);
    	List<String> nodeIdList = request.getNodeId();
    	
    	
    	for (String nodeId : nodeIdList) {
    		
    		/*
    		 * Create an xml element for the node activation result
    		 * */
    		vitro.vgw.communication.response.ObjectFactory factory = new vitro.vgw.communication.response.ObjectFactory();
    		NodeRegistrationResultType nodeRegistrationResult = factory.createNodeRegistrationResultType();
    		vgwResponse.getNodeRegistationResult().add(nodeRegistrationResult);
    		nodeRegistrationResult.setNodeId(nodeId);
    		
    		try{
    			NodeDescriptor currentNode = getNodeDescription(nodeId);
        		if(currentNode != null){
        			
        			RegisterSensor registerSensor = sensorMLMessageAdapter.getRegisterSensorMessage(getGatewayLogicalName(), currentNode);
        			RegisterSensorResponse registerSensorResponse = idas.registerSensor(registerSensor);
        			
        			String assignedSensorId = registerSensorResponse.getAssignedSensorId();
        			
        			//Register assigned sensor id to associate subsequent observation to the correct node
        			//TODO: understand if persistence is needed
        			idasNodeMapping.put(currentNode, assignedSensorId);
        			nodeRegistrationResult.setSuccess(true);
        		}
    		} catch(RSControllerException ex){
    			logger.error("Error while registering node {}", nodeId, ex);
    			nodeRegistrationResult.setSuccess(false);
    			nodeRegistrationResult.setErrorMessage(ex.getMessage());
    			
    			vgwResponse.setSuccess(false);
    		}
    		
		}
    	
    	if(!vgwResponse.isSuccess()){
    		
    		List<NodeRegistrationResultType> nodeResultList = vgwResponse.getNodeRegistationResult();
    		
    		StringBuffer sb = new StringBuffer();
    		
    		sb.append("Error while registering nodes: ");
    		
    		for (NodeRegistrationResultType nodeRegistrationResultType : nodeResultList) {
				if(!nodeRegistrationResultType.isSuccess()){
					sb.append(nodeRegistrationResultType.getNodeId() + ", ");
				}
			}
    		
    		vgwResponse.setErrorMessage(sb.toString());
    		
    	} 
         
         
         return vgwResponse;
    }
    
    /**
     * Only for testing purposes to support IDAS to register sensors at runtime
     * @throws RSControllerException 
     * */
    private NodeDescriptor getNodeDescription(String nodeId) throws RSControllerException{
    	
    	/*
    	 * This function should not be present in future versions, so I do not change the RSControllerStub interface
    	 * 
    	 * 
    	 * */
    	RSControllerStub rsControllerStub = (RSControllerStub)rsController;
    	
    	NodeDescriptor descriptor = rsControllerStub.registerSensor(new Node(nodeId));
    	
    	return descriptor;
    }
}

