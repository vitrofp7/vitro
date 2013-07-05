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
package vitro.vgw.rscontroller;


import java.util.ArrayList;
import java.util.List;

import vitro.vgw.exception.RSControllerException;
import vitro.vgw.exception.WSIAdapterException;
import vitro.vgw.model.Node;
import vitro.vgw.model.NodeDescriptor;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.wsiadapter.WSIAdapter;

public class RSControllerStub implements RSController{

	private WSIAdapter wsiAdapter;
	
	private List<NodeDescriptor> nodeDescList;
	
	public WSIAdapter getWsiAdapter() {
		return wsiAdapter;
	}

	public void setWsiAdapter(WSIAdapter wsiAdapter) {
		this.wsiAdapter = wsiAdapter;
	}

	public List<NodeDescriptor> init() throws RSControllerException {
		List<NodeDescriptor> result = null;
		
		try{
			result = new ArrayList<NodeDescriptor>();
			
			List<Node> nodeList = wsiAdapter.getAvailableNodeList();
			if(nodeList != null){
				for (Node node : nodeList) {
					List<Resource> resourceList = wsiAdapter.getResources(node);
					
					NodeDescriptor currentDescriptor = new NodeDescriptor();
					currentDescriptor.setId(node.getId());
					currentDescriptor.setResourcelist(resourceList);
					
					result.add(currentDescriptor);
				}
			}
		} catch(WSIAdapterException wsaEx){
			throw new RSControllerException(wsaEx);
		}
		
		return nodeDescList = result;
	

	}

	public Observation getWSIData(Node node, Resource resource) throws RSControllerException {
		try{
			return wsiAdapter.getNodeObservation(node, resource);
		} catch(WSIAdapterException wsaEx){
			throw new RSControllerException(wsaEx);
		}
		
	}

	
	public List<Observation> getWSIData(Resource resource) throws RSControllerException {
		
		List<Observation> observationList = null;
		
		try{
			observationList = new ArrayList<Observation>();
			
			
			for (NodeDescriptor nodeDescriptor : nodeDescList) {
				//Filter on nodes providing the requested resource
				if(nodeDescriptor.getResourcelist().contains(resource)){
					Observation currentObservation = wsiAdapter.getNodeObservation(nodeDescriptor, resource);
					observationList.add(currentObservation);
				}
			}
		} catch(WSIAdapterException wsaEx){
			throw new RSControllerException(wsaEx);
		}
		
		return observationList;
	}
	
	//TODO: To test run-time node registration 
	public NodeDescriptor registerSensor(Node node) throws RSControllerException {
		NodeDescriptor currentDescriptor = null;
		
		try{
			List<Resource> resourceList = wsiAdapter.getResources(node);
			
			currentDescriptor = new NodeDescriptor();
			currentDescriptor.setId(node.getId());
			currentDescriptor.setResourcelist(resourceList);
		} catch(WSIAdapterException ex){
			throw new RSControllerException(ex.getMessage(), ex);
		}
		
		if(!nodeDescList.contains(currentDescriptor)){
			nodeDescList.add(currentDescriptor);
		}
		
		return currentDescriptor;
	}
	
	

}
