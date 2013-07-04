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

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;
import ch.ethz.inf.vs.californium.examples.ipso.*;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * The class IpsoServer provides an example of the IPSO Profile specification.
 * The server registers its resources at the SensiNode Resource Directory.
 * 
 * @author Matthias Kovatsch
 */
public class IpsoServer extends LocalEndpoint {

	// exit codes for runtime errors
	public static final int ERR_INIT_FAILED = 1;
	
	/**
	 * Constructor for a new PlugtestServer. Call {@code super(...)} to configure
	 * the port, etc. according to the {@link LocalEndpoint} constructors.
	 * <p>
	 * Add all initial {@link LocalResource}s here.
	 */
	public IpsoServer() throws SocketException {
		
		// add resources to the server
		addResource(new DeviceName());
		addResource(new DeviceManufacturer());
		addResource(new DeviceModel());
		addResource(new DeviceSerial());
		addResource(new DeviceBattery());

		addResource(new PowerInstantaneous());
		addResource(new PowerCumulative());
		addResource(new PowerRelay());
		addResource(new PowerDimmer());
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
			
			LocalEndpoint server = new IpsoServer();
			
			System.out.printf(IpsoServer.class.getSimpleName()+" listening on port %d.\n", server.port());
			
			Request register = new POSTRequest() {
					@Override
			        protected void handleResponse(Response response) {
			            // specific handling for this request
			            // here: response received, output a pretty-print
						System.out.println("Successfully regeistered");
			            response.prettyPrint();
			        }
				};
				
			// RD location
			String rd = "coap://interop.ams.sensinode.com:5683/rd";
			if (args.length>0 && args[0].startsWith("coap://")) {
				rd = args[0];
			} else {
				System.out.println("Hint: You can give the RD URI as first argument.");
				System.out.println("Fallback to SensiNode RD");
			}
				
			// Individual hostname
			String hostname = Double.toString(Math.round(Math.random()*1000));
			if (args.length>1 && args[1].matches("[A-Za-z0-9-_]+")) {
				hostname = args[1];
			} else {
				System.out.println("Hint: You can give an alphanumeric (plus '-' and '_') string as second argument to specify a custom hostname.");
				System.out.println("Fallback to hostname");
				try {
					hostname = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e1) {
					System.out.println("Unable to retrieve hostname for registration");
					System.out.println("Fallback to random");
				}
			}
			
			register.setURI(rd+"?h=Cf-"+hostname);
			register.setPayload(LinkFormat.serialize(server.getRootResource(), null, true), MediaTypeRegistry.APPLICATION_LINK_FORMAT);

			// execute the request
			try {
				System.out.println("Registering at "+rd+" as Cf-"+hostname);
				register.execute();
			} catch (Exception e) {
				System.err.println("Failed to execute request: " + e.getMessage());
				System.exit(ERR_INIT_FAILED);
			}
			
		} catch (SocketException e) {

			System.err.printf("Failed to create "+IpsoServer.class.getSimpleName()+": %s\n", e.getMessage());
			System.exit(ERR_INIT_FAILED);
		}
		
	}

}
