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


import java.util.List;

import vitro.vgw.exception.RSControllerException;
import vitro.vgw.model.Node;
import vitro.vgw.model.NodeDescriptor;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;

public interface RSController {
	
	/*
	 * Used to initialize the component. It will return all managed nodes and related resources
	 * */
	public List<NodeDescriptor> init() throws RSControllerException;
	
	/*
	 * Used to get an observation of a resource from a node in a synchronous way
	 * */
	public Observation getWSIData(Node node, Resource resource) throws RSControllerException;
	
	/*
	 * Used to get observations of a specified resource from all nodes handling it in a synchronous way
	 * */
	public List<Observation> getWSIData(Resource resource) throws RSControllerException;

}
