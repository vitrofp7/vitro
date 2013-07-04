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
package ch.ethz.inf.vs.californium.examples;

import java.net.SocketException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;
import ch.ethz.inf.vs.californium.examples.plugtest.*;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * The class PlugtestServer implements the test specification for the
 * ETSI IoT CoAP Plugtests, Paris, France, 24 - 25 March 2012.
 * 
 * @author Matthias Kovatsch
 */
public class PlugtestServer extends LocalEndpoint {

	// exit codes for runtime errors
	public static final int ERR_INIT_FAILED = 1;
	
	/**
	 * Constructor for a new PlugtestServer. Call {@code super(...)} to configure
	 * the port, etc. according to the {@link LocalEndpoint} constructors.
	 * <p>
	 * Add all initial {@link LocalResource}s here.
	 */
	public PlugtestServer() throws SocketException {
		
		// add resources to the server
		addResource(new DefaultTest());
		addResource(new LongPath());
		addResource(new Query());
		addResource(new Separate());
		addResource(new Large());
		addResource(new LargeUpdate());
		addResource(new LargeCreate());
		addResource(new Observe());
	}

	// Logging /////////////////////////////////////////////////////////////////
	
	@Override
	public void handleRequest(Request request) {
		
		// Add additional handling like special logging here.
		request.prettyPrint();
		
		// dispatch to requested resource
		super.handleRequest(request);
	}

	
	// Application entry point /////////////////////////////////////////////////
	
	public static void main(String[] args) {

		Log.setLevel(Level.INFO);
		Log.init();
		
		// create server
		try {
			
			Endpoint server = new PlugtestServer();
			
			System.out.printf(PlugtestServer.class.getSimpleName()+" listening on port %d.\n", server.port());
			
		} catch (SocketException e) {

			System.err.printf("Failed to create "+PlugtestServer.class.getSimpleName()+": %s\n", e.getMessage());
			System.exit(ERR_INIT_FAILED);
		}
		
	}

}
