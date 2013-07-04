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
package ch.ethz.inf.vs.californium.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.junit.Test;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;


public class RequestTest {

	class RespondTask extends TimerTask {

		RespondTask(Request request, Response response) {
			this.request = request;
			this.response = response;
		}

		@Override
		public void run() {
			request.respond(response);
		}

		Request request;
		Response response;

	}
	
	Response handledResponse;
	Timer timer = new Timer();

	@Test
	public void testRespond() {

		System.out.println("/b".split("/").length);

		// Client Side /////////////////////////////////////////////////////////

		// create new request with own response handler
		Request request = new GETRequest() {
			@Override
			protected void handleResponse(Response response) {
				// change state of outer object
				handledResponse = response;
			}
		};

		/* (...) send the request to server */

		// Server Side /////////////////////////////////////////////////////////

		/* (...) receive request from client */

		// create new response
		Response response = new Response();

		// respond to the request
		request.respond(response);

		// Validation /////////////////////////////////////////////////////////

		// check if response was handled correctly
		assertSame(response, handledResponse);

	}

	@Test
	public void testReceiveResponse() throws InterruptedException {

		// Client Side /////////////////////////////////////////////////////////

		Request request = new GETRequest();

		// enable response queue in order to perform receiveResponse() calls
		request.enableResponseQueue(true);

		/* (...) send the request to server */

		// Server Side /////////////////////////////////////////////////////////

		/* (...) receive request from client */

		// create new response
		Response response = new Response();

		// schedule delayed response (e.g. take some time for computation etc.)
		timer.schedule(new RespondTask(request, response), 500);

		// Client Side /////////////////////////////////////////////////////////

		// block until response received
		Response receivedResponse = request.receiveResponse();

		// Validation /////////////////////////////////////////////////////////

		// check if response was received correctly
		assertSame(response, receivedResponse);
	}

	@Test
	public void testTokenManager() {

		Set<byte[]> acquiredTokens = new HashSet<byte[]>();
		
		final byte[] emptyToken = new byte[0];
		
		acquiredTokens.add(emptyToken);
		
		System.out.println("Contains: " + acquiredTokens.contains(emptyToken) );
		
		acquiredTokens.remove(emptyToken);
		
		System.out.println("Contains: " + acquiredTokens.contains(emptyToken) );
	}

}
