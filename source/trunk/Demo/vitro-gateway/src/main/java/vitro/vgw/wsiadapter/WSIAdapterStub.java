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
package vitro.vgw.wsiadapter;


import java.util.ArrayList;
import java.util.List;

import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.exception.WSIAdapterException;
import vitro.vgw.model.Node;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;

public class WSIAdapterStub implements WSIAdapter{

	private static final String NODE_1_ID = "fec0:0:0:3::1";
	private static final String NODE_2_ID = "fec0:0:0:3::2";
	private static final String NODE_3_ID = "fec0:0:0:3::3";
	private static final String NODE_4_ID = "fec0:0:0:3::4";

    private boolean isDtnEnabled;
    private boolean trustCoapMessagingActivated;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public List<Node> getAvailableNodeList() {
		List<Node> nodeList = new ArrayList<Node>(2);
		
		Node node = new Node();
		node.setId(NODE_1_ID);
		nodeList.add(node);
		
		node = new Node();
		node.setId(NODE_2_ID);
		nodeList.add(node);
		
		node = new Node();
		node.setId(NODE_3_ID);
		nodeList.add(node);
		
		node = new Node();
		node.setId(NODE_4_ID);
		nodeList.add(node);
		
		return nodeList;
	}

	
	public List<Resource> getResources(Node node) {
		
		List<Resource> resourceList = new ArrayList<Resource>();
		
		try{
			if(node.getId().equals(NODE_1_ID)){
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
			} else if(node.getId().equals(NODE_2_ID)){
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_HUMIDITY));
			}  else if(node.getId().equals(NODE_3_ID)){
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_CO));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_CO2));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_PRESSURE));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_WIND_SPEED));
			} else if(node.getId().equals(NODE_4_ID)){
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_CO));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_CO2));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_PRESSURE));
				resourceList.add(Resource.getResource(Resource.PHENOMENOM_WIND_SPEED));
			}
		} catch(Exception ex){
			logger.error("Error while requesting resource capabilities to node " + node.getId());
		}
		

		
		return resourceList;
	}

	
	public Observation getNodeObservation(Node node, Resource resource) throws WSIAdapterException {
		if(node.getId().equals(NODE_1_ID)){
			Observation obs = new Observation();
			obs.setNode(node);
			try {
				obs.setResource(Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
			} catch (VitroGatewayException e) {
				logger.error("Error getting resource", e);
			}
			obs.setValue("32");
			obs.setTimestamp(System.currentTimeMillis());
			
			return obs;
		}
		if(node.getId().equals(NODE_2_ID)){
			Observation obs = new Observation();
			obs.setNode(node);
			try {
				obs.setResource(Resource.getResource(Resource.PHENOMENOM_HUMIDITY));
			} catch (VitroGatewayException e) {
				logger.error("Error getting resource", e);
			}
			obs.setValue("50");
			obs.setTimestamp(System.currentTimeMillis());
			
			return obs;
		}
		if(node.getId().equals(NODE_3_ID) || node.getId().equals(NODE_4_ID)){
			try{
				if(resource.equals(Resource.getResource(Resource.PHENOMENOM_TEMPERATURE))){
					Observation obs = new Observation();
					obs.setNode(node);
					
					obs.setResource(Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
					
					obs.setValue("23");
					obs.setUom(Resource.UOM_CELSIUS);
					obs.setTimestamp(System.currentTimeMillis());
					
					return obs;
				} else if(resource.equals(Resource.getResource(Resource.PHENOMENOM_CO))){
					Observation obs = new Observation();
					obs.setNode(node);
					
						obs.setResource(Resource.getResource(Resource.PHENOMENOM_CO));
					
					obs.setValue("56");
					obs.setTimestamp(System.currentTimeMillis());
					
					return obs;
				} else if(resource.equals(Resource.getResource(Resource.PHENOMENOM_CO2))){
					Observation obs = new Observation();
					obs.setNode(node);
					
						obs.setResource(Resource.getResource(Resource.PHENOMENOM_CO2));
					
					obs.setValue("67");
					obs.setTimestamp(System.currentTimeMillis());
					
					return obs;
				}  else if(resource.equals(Resource.getResource(Resource.PHENOMENOM_PRESSURE))){
					Observation obs = new Observation();
					obs.setNode(node);
					
						obs.setResource(Resource.getResource(Resource.PHENOMENOM_PRESSURE));
					
					obs.setValue("100");
					obs.setTimestamp(System.currentTimeMillis());
					
					return obs;
				}  else if(resource.equals(Resource.getResource(Resource.PHENOMENOM_WIND_SPEED))){
					Observation obs = new Observation();
					obs.setNode(node);
					
						obs.setResource(Resource.getResource(Resource.PHENOMENOM_WIND_SPEED));
					
					obs.setValue("40");
					obs.setTimestamp(System.currentTimeMillis());
					
					return obs;
				}
			} catch(VitroGatewayException e){
				logger.error("Error getting resource", e);
			}
		}
		
		throw new WSIAdapterException("the " + resource.getName() + " resource is not active on node " + node.getId());
		
	}

    public boolean getDtnPolicy() {
        return isDtnEnabled;
    }

    public void setDtnPolicy(boolean value) {
        isDtnEnabled = value;
    }


    public boolean isTrustCoapMessagingActive(){
        return  trustCoapMessagingActivated;
    }
    /**
     * is supposed to start the Trust Coap Messaging (switch on the Trust Coap Messaging mode)
     */
    public void setTrustCoapMessagingActive(boolean value){
        trustCoapMessagingActivated = value;
    }

    public InfoOnTrustRouting getNodeTrustRoutingInfo(Node node, Resource resource) throws WSIAdapterException {
        // TODO: an implementation is needed if this action is supported
        return null;
    }

}
