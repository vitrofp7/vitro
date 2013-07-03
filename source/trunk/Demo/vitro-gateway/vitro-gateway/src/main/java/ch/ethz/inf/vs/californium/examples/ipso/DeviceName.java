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
package ch.ethz.inf.vs.californium.examples.ipso;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a part of the IPSO profile.
 * 
 * @author Matthias Kovatsch
 */
public class DeviceName extends LocalResource {
	
	private String name = "IPSO Server";

	public DeviceName() {
		super("dev/n");
		setTitle("Name");
		setResourceType("ipso:dev-n");
		setInterfaceDescription("core#p");
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, name, MediaTypeRegistry.TEXT_PLAIN);
	}
	
	@Override
	public void performPUT(PUTRequest request) {

		if (request.getContentType()!=MediaTypeRegistry.TEXT_PLAIN) {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "text/plain only");
			return;
		}
		
		name = request.getPayloadString();

		// complete the request
		request.respond(CodeRegistry.RESP_CHANGED);
	}
}
