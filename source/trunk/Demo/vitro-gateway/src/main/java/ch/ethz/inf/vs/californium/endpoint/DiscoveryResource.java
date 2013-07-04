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
package ch.ethz.inf.vs.californium.endpoint;

import java.util.List;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Response;

/**
 * This class implements the CoAP /.well-known/core resource.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class DiscoveryResource extends LocalResource {

// Constants ///////////////////////////////////////////////////////////////////

	/** The default resource identifier for resource discovery. */
	public static final String DEFAULT_IDENTIFIER = ".well-known/core";

// Attributes //////////////////////////////////////////////////////////////////

	/** The root resource of the endpoint used for recursive Link-Format generation. */
	private Resource root;

// Constructors ////////////////////////////////////////////////////////////////

	/**
	 * Constructor for a new DiscoveryResource
	 * 
	 * @param rootResource The entry point used for the local discovery
	 */
	public DiscoveryResource(Resource rootResource) {
		super(DEFAULT_IDENTIFIER, true); // hidden
		setContentTypeCode(MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		
		this.root = rootResource;
	}

// REST Operations /////////////////////////////////////////////////////////////

	@Override
	public void performGET(GETRequest request) {

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		
		// get filter query
		List<Option> query = request.getOptions(OptionNumberRegistry.URI_QUERY);

		// return resources in link-format
		response.setPayload(LinkFormat.serialize(root, query, true), MediaTypeRegistry.APPLICATION_LINK_FORMAT);

		// complete the request
		request.respond(response);
	}
}
