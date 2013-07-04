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
package ch.ethz.inf.vs.californium.examples.plugtest;

import java.util.ArrayList;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a test of specification for the
 * ETSI IoT CoAP Plugtests, Paris, France, 24 - 25 March 2012.
 * 
 * @author Matthias Kovatsch
 */
public class LargeCreate extends LocalResource {

// Members ////////////////////////////////////////////////////////////////

	private byte[] data = null;
	private int dataCt = -1;

// Constructors ////////////////////////////////////////////////////////////
	
	/*
	 * Default constructor.
	 */
	public LargeCreate() {
		this("large-create");
	}
	
	/*
	 * Constructs a new storage resource with the given resourceIdentifier.
	 */
	public LargeCreate(String resourceIdentifier) {
		super(resourceIdentifier, false);
		setTitle("Large resource that can be created using POST method");
		setResourceType("block");
	}

	// REST Operations /////////////////////////////////////////////////////////
	
	@Override
	public void performGET(GETRequest request) {

		Response response = null;
		
		if (data==null) {
			
			response = new Response(CodeRegistry.RESP_CONTENT);
			response.setPayload("Nothing POSTed yet", MediaTypeRegistry.TEXT_PLAIN);
			
		} else {
			
			// content negotiation
			ArrayList<Integer> supported = new ArrayList<Integer>();
			supported.add(dataCt);

			int ct = dataCt;
			if ((ct = MediaTypeRegistry.contentNegotiation(dataCt,  supported, request.getOptions(OptionNumberRegistry.ACCEPT)))==MediaTypeRegistry.UNDEFINED) {
				request.respond(CodeRegistry.RESP_NOT_ACCEPTABLE, "Accept " + MediaTypeRegistry.toString(dataCt));
				return;
			}
			
			response = new Response(CodeRegistry.RESP_CONTENT);

			// load data into payload
			response.setPayload(data);
	
			// set content type
			response.setContentType(ct);
	
		}
		
		// complete the request
		request.respond(response);
	}
	
	/*
	 * POST content to create this resource.
	 */
	@Override
	public void performPOST(POSTRequest request) {

		if (request.getContentType()==MediaTypeRegistry.UNDEFINED) {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "Content-Type not set");
			return;
		}
		
		// store payload
		storeData(request);

		// create new response
		Response response = new Response(CodeRegistry.RESP_CREATED);

		// inform client about the location of the new resource
		response.setLocationPath("/nirvana");

		// complete the request
		request.respond(response);
	}
	
	/*
	 * DELETE the data and act as resouce was deleted.
	 */
	@Override
	public void performDELETE(DELETERequest request) {

		// delete
		data = null;

		// complete the request
		request.respond(new Response(CodeRegistry.RESP_DELETED));
	}

	// Internal ////////////////////////////////////////////////////////////////
	
	/*
	 * Convenience function to store data contained in a 
	 * PUT/POST-Request. Notifies observing endpoints about
	 * the change of its contents.
	 */
	private synchronized void storeData(Request request) {

		// set payload and content type
		data = request.getPayload();
		dataCt = request.getContentType();
		clearAttribute(LinkFormat.CONTENT_TYPE);
		setContentTypeCode(dataCt);

		// signal that resource state changed
		changed();
	}
}
