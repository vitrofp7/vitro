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
package vitro.vgw.wsiadapter;


import java.util.List;

import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;
import vitro.vgw.exception.WSIAdapterException;
import vitro.vgw.model.Node;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;


/*
 * Interface describing the underlying component to interact with the sensor networks.
 * 
 * Its implementation is the actual interaction protocol with the networks (e.g serial, http, ....)
 * 
 * */
public interface WSIAdapter {

	/*
	 * This method returns the list of all Nodes managed by VGW.
	 * 
	 * If a discovery process is not available on the underlying WSNs, a configuration file could be used 
	 * 
	 * */
	List<Node> getAvailableNodeList() throws WSIAdapterException;
	
	/*
	 * Used to retrieve a sensor node's managed resources
	 * */
	List<Resource> getResources(Node node) throws WSIAdapterException;
	
	/*
	 * Used to get an observation of a resource from a node
	 * */
	Observation getNodeObservation(Node node, Resource resource) throws WSIAdapterException;

    boolean getDtnPolicy();
    /**
     * is supposed to start the DTN (switch on the DTN mode)
     */
    void setDtnPolicy(boolean value);


    boolean isTrustCoapMessagingActive();
    /**
     * is supposed to start the Trust Coap Messaging (switch on the Trust Coap Messaging mode)
     */
    void setTrustCoapMessagingActive(boolean value);

    /**
     * Used to get the coap response message for the trust routing resource from a node.  (the Resource parameter will always be Resource.RES_TRUST_ROUTING
     * @param node the  node queried
     * @param resource the resource queried (always Resource.RES_TRUST_ROUTING)
     * @return InfoOnTrustRouting object with the response
     */
    InfoOnTrustRouting getNodeTrustRoutingInfo(Node node, Resource resource) throws WSIAdapterException;

}
