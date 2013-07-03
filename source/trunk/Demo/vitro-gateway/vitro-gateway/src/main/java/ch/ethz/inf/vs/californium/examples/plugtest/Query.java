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

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a test of specification for the
 * ETSI IoT CoAP Plugtests, Paris, France, 24 - 25 March 2012.
 * 
 * @author Matthias Kovatsch
 */
public class Query extends LocalResource {

	public Query() {
		super("query");
		setTitle("Resource accepting query parameters");
	}

	@Override
	public void performGET(GETRequest request) {

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		
		StringBuilder payload = new StringBuilder();
		
		payload.append(String.format("Type: %d (%s)\nCode: %d (%s)\nMID: %d",
									 request.getType().ordinal(),
									 request.typeString(),
									 request.getCode(),
									 CodeRegistry.toString(request.getCode()),
									 request.getMID()
									));
		
		for (Option query : request.getOptions(OptionNumberRegistry.URI_QUERY)) {
			String keyValue[] = query.getStringValue().split("=");
			
			payload.append("\nQuery: ");
			payload.append(keyValue[0]);
			if (keyValue.length==2) {
				payload.append(": ");
				payload.append(keyValue[1]);
			}
		}
		
		if (payload.length()>64) {
			payload.delete(62, payload.length());
			payload.append('»');
		}

		// set payload
		response.setPayload(payload.toString());
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
		
		// complete the request
		request.respond(response);
	}
}
