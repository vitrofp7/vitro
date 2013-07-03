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
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a test of specification for the
 * ETSI IoT CoAP Plugtests, Paris, France, 24 - 25 March 2012.
 * 
 * @author Matthias Kovatsch
 */
public class LargeUpdate extends LocalResource {

// Members ////////////////////////////////////////////////////////////////

	private byte[] data = null;
	private int dataCt = MediaTypeRegistry.TEXT_PLAIN;

// Constructors ////////////////////////////////////////////////////////////
	
	/*
	 * Default constructor.
	 */
	public LargeUpdate() {
		this("large-update");
	}
	
	/*
	 * Constructs a new storage resource with the given resourceIdentifier.
	 */
	public LargeUpdate(String resourceIdentifier) {
		super(resourceIdentifier);
		setTitle("Large resource that can be updated using PUT method");
		setResourceType("block");
	}

	// REST Operations /////////////////////////////////////////////////////////
	
	/*
	 * GETs the content of this storage resource. 
	 * If the content-type of the request is set to application/link-format 
	 * or if the resource does not store any data, the contained sub-resources
	 * are returned in link format.
	 */
	@Override
	public void performGET(GETRequest request) {
		
		// content negotiation
		ArrayList<Integer> supported = new ArrayList<Integer>();
		supported.add(dataCt);

		int ct = MediaTypeRegistry.IMAGE_PNG;
		if ((ct = MediaTypeRegistry.contentNegotiation(dataCt,  supported, request.getOptions(OptionNumberRegistry.ACCEPT)))==MediaTypeRegistry.UNDEFINED) {
			request.respond(CodeRegistry.RESP_NOT_ACCEPTABLE, "Accept " + MediaTypeRegistry.toString(dataCt));
			return;
		}

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		
		if (data==null) {
			StringBuilder builder = new StringBuilder();
			builder.append("/-------------------------------------------------------------\\\n");
			builder.append("|                 RESOURCE BLOCK NO. 1 OF 5                   |\n");
			builder.append("|               [each line contains 64 bytes]                 |\n");
			builder.append("\\-------------------------------------------------------------/\n");
			builder.append("/-------------------------------------------------------------\\\n");
			builder.append("|                 RESOURCE BLOCK NO. 2 OF 5                   |\n");
			builder.append("|               [each line contains 64 bytes]                 |\n");
			builder.append("\\-------------------------------------------------------------/\n");
			builder.append("/-------------------------------------------------------------\\\n");
			builder.append("|                 RESOURCE BLOCK NO. 3 OF 5                   |\n");
			builder.append("|               [each line contains 64 bytes]                 |\n");
			builder.append("\\-------------------------------------------------------------/\n");
			builder.append("/-------------------------------------------------------------\\\n");
			builder.append("|                 RESOURCE BLOCK NO. 4 OF 5                   |\n");
			builder.append("|               [each line contains 64 bytes]                 |\n");
			builder.append("\\-------------------------------------------------------------/\n");
			builder.append("/-------------------------------------------------------------\\\n");
			builder.append("|                 RESOURCE BLOCK NO. 5 OF 5                   |\n");
			builder.append("|               [each line contains 64 bytes]                 |\n");
			builder.append("\\-------------------------------------------------------------/\n");
			
			request.respond(CodeRegistry.RESP_CONTENT, builder.toString(), ct);
			
		} else {

			// load data into payload
			response.setPayload(data);
	
			// set content type
			response.setContentType(ct);
	
			// complete the request
			request.respond(response);
		}
	}
	
	/*
	 * PUTs content to this resource.
	 */
	@Override
	public void performPUT(PUTRequest request) {

		if (request.getContentType()==MediaTypeRegistry.UNDEFINED) {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "Content-Type not set");
			return;
		}
		
		// store payload
		storeData(request);

		// complete the request
		request.respond(CodeRegistry.RESP_CHANGED);
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
