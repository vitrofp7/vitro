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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.util.Log;


public class RTTClient {

	static String uriString = "";
	static int n = 1000;
	static int sent = 0;
	static int received = 0;
	static double total = 0d;
	static double min = Double.MAX_VALUE;
	static double max = 0d;

	/*
	 * Main method of this client.
	 */
	public static void main(String[] args) {
		
		URI uri = null;
		
		Log.setLevel(Level.WARNING);
		Log.init();
		
		if (args.length > 0) {
			// input URI from command line arguments
			try {
				uri = new URI(args[0]);
				uriString = args[0];
			} catch (URISyntaxException e) {
				System.err.println("Invalid URI: " + e.getMessage());
				System.exit(-1);
			}
			
			if (args.length > 1) {
				try {
					n = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid number: " + e.getMessage());
					System.exit(-1);
				}
			}
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() {
			    	
			    	System.out.printf("\nRTT statistics for %s:\n    Packets: Sent = %d, Received = %d, Lost = %d (%d%% loss),\nApproximate round trip times in milli-seconds:\n    Minimum = %fms, Maximum = %fms, Average = %fms\n",
			    			uriString,
			    			sent, received, sent-received, (sent-received)/sent,
			    			min, max, total/received);
			    }
			 });
		
			for (int i = 0; i < n; i++) {
			
				Request request = new GETRequest();
				request.enableResponseQueue(true);
				request.setURI(uri);
				
				try {
					request.execute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(-1);
				}
				try {
					Response response = request.receiveResponse();
					++sent;
					if (response != null) {
						++received;
						
						if (response.getRTT() > max) {
							max = response.getRTT();
						}
						
						if (response.getRTT() < min) {
							min= response.getRTT();
						}
						
						if (response.getRTT() < 0) {
							System.out.println("ERROR: Response untimed, time=" + response.getRTT());
						} else if (request.getRetransmissioned()>0) {
							System.out.println("WARNING: Response after retransmission, time=" + response.getRTT());
						} else {
							System.out.println("time=" + response.getRTT() + "ms");
						}
						total += response.getRTT();
					} else {
						System.out.println("No response received");
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			

		} else {
			// display help
			System.out.println("Californium (Cf) RTT Client");
			System.out.println("(c) 2012, Institute for Pervasive Computing, ETH Zurich");
			System.out.println();
			System.out.println("Usage: " + RTTClient.class.getSimpleName() + " URI");
			System.out.println("  URI: The CoAP URI of the remote resource to measure");
		}
	}
	
}
