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
package ch.ethz.inf.vs.californium.examples.resources;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This class implements a 'toUpper' resource for demonstration purposes.
 * Defines a resource that returns a POSTed string in upper-case letters.
 *  
 * @author Matthias Kovtsch
 * 
 */
public class ToUpperResource extends LocalResource {

	public ToUpperResource() {
		super("toUpper");
		setTitle("POST text here to convert it to uppercase");
		setResourceType("UppercaseConverter");
	}

	@Override
	public void performPOST(POSTRequest request) {
		
		if (request.getContentType()!=MediaTypeRegistry.TEXT_PLAIN) {
			request.respond(CodeRegistry.RESP_UNSUPPORTED_MEDIA_TYPE, "Use text/plain");
			return;
		}

		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, request.getPayloadString().toUpperCase(), MediaTypeRegistry.TEXT_PLAIN);
	}
}
