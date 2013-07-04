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

import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;
import ch.ethz.inf.vs.californium.examples.resources.CarelessResource;
import ch.ethz.inf.vs.californium.examples.resources.HelloWorldResource;
import ch.ethz.inf.vs.californium.examples.resources.ImageResource;
import ch.ethz.inf.vs.californium.examples.resources.LargeResource;
import ch.ethz.inf.vs.californium.examples.resources.SeparateResource;
import ch.ethz.inf.vs.californium.examples.resources.StorageResource;
import ch.ethz.inf.vs.californium.examples.resources.TimeResource;
import ch.ethz.inf.vs.californium.examples.resources.ToUpperResource;
import ch.ethz.inf.vs.californium.examples.resources.ZurichWeatherResource;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * The class ExampleServer shows how to implement a server by extending 
 * {@link LocalEndpoint}. In the implementation class, use
 * {@link LocalEndpoint#addResource(ch.ethz.inf.vs.californium.endpoint.LocalResource)}
 * to add custom resources extending {@link LocalResource}.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class ExampleServer extends LocalEndpoint {

	// exit codes for runtime errors
	public static final int ERR_INIT_FAILED = 1;
	
	/**
	 * Constructor for a new ExampleServer. Call {@code super(...)} to configure
	 * the port, etc. according to the {@link LocalEndpoint} constructors.
	 * <p>
	 * Add all initial {@link LocalResource}s here.
	 */
	public ExampleServer() throws SocketException {
		
		// add resources to the server
		addResource(new HelloWorldResource());
		addResource(new ToUpperResource());
		addResource(new StorageResource());
		addResource(new SeparateResource());
		addResource(new LargeResource());
		addResource(new TimeResource());
		addResource(new ZurichWeatherResource());
		addResource(new ImageResource());
		addResource(new CarelessResource());
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
		
		Log.init();
		
		// create server
		try {
			
			Endpoint server = new ExampleServer();
			
			
			System.out.printf("ExampleServer listening on port %d.\n", server.port());
			
		} catch (SocketException e) {

			System.err.printf("Failed to create SampleServer: %s\n", e.getMessage());
			System.exit(ERR_INIT_FAILED);
		}
		
	}

}
