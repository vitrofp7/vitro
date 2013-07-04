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
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This class implements a 'hello world' resource for demonstration purposes.
 * Defines a resource that returns text with special characters on GET.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class HelloWorldResource extends LocalResource {

	public HelloWorldResource(String custom, String title, String rt) {
		super(custom);
		setTitle(title);
		setResourceType(rt);
	}
	
	public HelloWorldResource() {
		this("helloWorld", "GET a friendly greeting!", "HelloWorldDisplayer");
	}

	@Override
	public void performGET(GETRequest request) {

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);

		// set payload
		response.setPayload("Hello World! Some umlauts: äöü\n\nZalgo: C͓̦̭̹̭͎͖̗̗̊Ȱ̬̥͚͚̏͛ͩ͆̎̿̈͝A̵̴̡̩̞͇̱͓͎̾P͎ͤͦ͆̍͋͒̽̂ͮ͠ͅ ̧̯̟̑ͫ͑͑͢͡R͈̜͍̄͌̄ͣͅU̥̭͓͉̟̳͗̈́̂L͎̘̪͓̟̩͌ͮͧ͞Ẽ̴̖̳̘̌̉ͯ͋̽̔Z̠̣̩̫͚͇̬̲͛ͮ̓ͧͨ̕");
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);

		// complete the request
		request.respond(response);
	}
}
