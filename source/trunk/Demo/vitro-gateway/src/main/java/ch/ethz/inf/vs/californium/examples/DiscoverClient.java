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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;

import ch.ethz.inf.vs.californium.coap.*;
import ch.ethz.inf.vs.californium.endpoint.RemoteResource;
import ch.ethz.inf.vs.californium.endpoint.Resource;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * This class implements a CoAP client triggered on RPL events.
 * @author Yoann Lopez
 */
public class DiscoverClient {

	// resource URI path used for discovery
	private static final String DISCOVERY_RESOURCE = "/.well-known/core";

	private static final int IDX_ZMQ_RPLSOCKET  = 0; 
	private static final int IDX_ZMQ_PUBSOCKET   = 1;
	private static final int IDX_COAP_PROXY = 2;

	private static final int ERR_BAD_URI         = 2;
	private static final int ERR_REQUEST_FAILED  = 5;
	private static final int ERR_RESPONSE_FAILED = 6;
	private static final int ERR_BAD_LINK_FORMAT = 7;
	private static final int ERR_MISSING_ZMQ_PULLSOCKET = 8;
	private static final int ERR_MISSING_ZMQ_PUBSOCKET = 9;
	private static final int ERR_MISSING_COAP_PROXY = 10;

	private static ArrayList<Request> pendingRequests;

	Context ctx;
	ZMQ.Socket publisher;
	ZMQ.Socket rpllistener;

	private String zmqsubsocket;

	private String zmqpubsocket;

	private String coapproxy;

	private URI proxyUri;

	boolean useCoapProxy = false;

	JSONParser parser;

	/*
	 * Main method of this client.
	 */
	public static void main(String[] args) {

		String zmqRplSubsocket = null;
		String zmqpubsocket = null;
		String coapproxy=null;
		// display help if no parameters specified
		if (args.length == 0) {
			printInfo();
			return;
		}

		Log.setLevel(Level.ALL);
		Log.init();

		// input parameters
		int idx = 0;
		for (String arg : args) {

			switch (idx) {
			case IDX_ZMQ_RPLSOCKET:
				zmqRplSubsocket = arg;
				break;
			case IDX_ZMQ_PUBSOCKET:
				zmqpubsocket = arg;
				break;
			case IDX_COAP_PROXY:
				coapproxy = arg;
				break;
			default:
				System.out.println("Unexpected argument: " + arg);
			}
			++idx;
		}

		if (zmqRplSubsocket == null) {
			System.err.println("ZMQ pull socket not specified");
			System.exit(ERR_MISSING_ZMQ_PULLSOCKET);
		}

		if (zmqpubsocket == null) {
			System.err.println("ZMQ pub socket not specified");
			System.exit(ERR_MISSING_ZMQ_PUBSOCKET);
		}

		if (coapproxy == null) {
			System.out.println("CoAP proxy not specified");
//			System.exit(ERR_MISSING_COAP_PROXY);

		}

		DiscoverClient coapClient = new DiscoverClient(zmqRplSubsocket, zmqpubsocket, coapproxy);

		coapClient.init();
	}


	public DiscoverClient(String zmqsubsocket, String zmqpubsocket, String coapproxy) {
		this.zmqsubsocket = zmqsubsocket;
		this.zmqpubsocket = zmqpubsocket;
		this.coapproxy = coapproxy;
		URI testproxy = null;
		
		if(coapproxy== null) {
			useCoapProxy = false;
		} else {
			try {
				testproxy = new URI(coapproxy);
				proxyUri = testproxy;
			} catch (URISyntaxException e) {
				useCoapProxy = false;
			}
			useCoapProxy = true;
		}

		pendingRequests = new ArrayList<Request>();

		ctx = ZMQ.context (1);

		parser = new JSONParser();

	}

	public String doHttpGet(String endpoint) {
		String output = "";  
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet getRequest = new HttpGet(endpoint);
			getRequest.addHeader("accept", "application/json");

			HttpResponse response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));

			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			System.err.println("ClientProtocolException\n");
			return "";

		} catch (IOException e) {
			System.err.println("IOException\n");
			return "";
		}
		return output;
	}

	public void init() {
		// prepare socket to publish events
		createPubSocket();
		
		// use HTTP inital request to get routing state
		doInitialRplStateRequest();
		
		// listen to RPL events
		handleRplEvents();
		
	}

	public void discoverCoapEndpoint(String addr) {
		// create request according to specified method
		String method = "DISCOVER";
		URI uri = null;
		try {
			uri = new URI(addr);
		} catch (URISyntaxException e) {
			System.err.println("Failed to parse URI: " + e.getMessage());
			return;
		}

		doRequest(method, uri);
	}

	public void createPubSocket() {
		publisher = ctx.socket (ZMQ.PUB);
		publisher.bind(zmqpubsocket); 
		publisher.setHWM(2); // We store 2 messages max

	}
	
	public void doInitialRplStateRequest() {
		// TODO FIXME: find a way to discover RPL border router address
		// Also, multiple border router handling would be great
		String rxMsg = doHttpGet("http://[aaaa::212:7401:1:101]:8080");

		// Overridden
		rxMsg = "{ \"addresses\":[ " +
		"      \"aaaa::212:7401:1:101\"," +
		"      \"fe80::212:7401:1:101\"" +
		"   ]," +
		"   \"neighbors\":[" +
		"      {" +
		"         \"address\":\"fe80::212:7403:3:303\"," +
		"         \"state\":\"REACHABLE\"" +
		"      }," +
		"      {" +
		"         \"address\":\"fe80::212:7402:2:202\"," +
		"         \"state\":\"REACHABLE\"" +
		"      }" +
		"   ]," +
		"   \"route\":[" +
		"      {" +
		"         \"address\":\"aaaa::212:7403:3:303/128\"," +
		"         \"nexthop\":\"fe80::212:7403:3:303\"," +
		"         \"lifetime\":\"16711376\"" +
		"      }," +
		"      {" +
		"         \"address\":\"aaaa::212:7402:2:202/128\"," +
		"         \"nexthop\":\"fe80::212:7402:2:202\"," +
		"         \"lifetime\":\"16711425\"" +
		"						}" +
		"	]" +
		"	}";

		if(!rxMsg.equals("")) {
			Object obj = null;
			try{
				obj = parser.parse(rxMsg);
			}
			catch(ParseException pe){
				System.out.println("position: " + pe.getPosition());
				System.out.println(pe);
			
				return;
			}

			System.out.println(obj);
			JSONObject obj2=(JSONObject)obj;
			JSONArray arr = (JSONArray) obj2.get("route"); 

			System.out.println("Routes are: " +arr);
			Object[] objs = arr.toArray();
			for (Object o:objs) {
				JSONObject route = (JSONObject) o;
				System.out.println(" route is: " +route);

				String ipaddr = ((String) route.get("address")).split("/")[0]; // remove /128
				discoverCoapEndpoint("coap://["+ipaddr+"]:5683");
			}
		}
	}
	
	public void handleRplEvents() {
		// Subscriber tells us when it's ready here
		rpllistener = ctx.socket(ZMQ.PULL);
		rpllistener.bind(zmqsubsocket);
		
		byte[] pulldata;
		String coapAddr = "";
		
		while(true) {
			System.out.println("Blocking for new RPL event");
			pulldata = rpllistener.recv(0);
			/* TODO FIXME: define received format
			   {
			    "type": "dao",
			    "lifetime": 42,
			    "prefix_length": 42,
			    "prefix": "cafe:babe"
			}*/
			System.out.println(" received: "+ new String(pulldata));
			coapAddr = "coap://[aaaa::0212:7402:0002:0202]:5683";

			discoverCoapEndpoint(coapAddr);
			
		}
		// TODO FIXME this is not reached
//		publisher.close();
//		rpllistener.close();
	
	}
	
	synchronized void doHandleResponse(Response response) {
		System.out.println("Receiving response...");
		boolean success = true;

		if (response != null) {
			response.prettyPrint();
			System.out.println("Time elapsed (ms): " + response.getRTT());


			// check of response contains resources
			String data="";
			if (response.getContentType()==MediaTypeRegistry.APPLICATION_LINK_FORMAT) {

				data = response.getPayloadString();
				System.out.println("\npayload string: "+data);

				// create resource three from link format
				Resource root = RemoteResource.newRoot(data);
				if (root != null) {

					// output discovered resources
					System.out.println("\nDiscovered resources:");
					root.prettyPrint();


					System.out.println("Publishing response on ZMQ socket "+publisher.toString());

					//			pubSocket.send(MediaTypeRegistry.toString(response.getContentType()).getBytes(), ZMQ.SNDMORE);
					//			publisher.send(response.getPeerAddress().toString().getBytes(), ZMQ.SNDMORE);
					publisher.send(response.getPayload(), 0);

					for (Resource resource:root.getSubResources()) {
						if(resource.isObservable()) {
							System.out.println("\nSubscribing to observable resource:"+response.getPeerAddress()+ resource.getPath());
							//							doRequest("OBSERVE", "coap://" + response.getPeerAddress()+ resource.getPath());
						}
					}


				} else {
					System.err.println("Failed to parse link format");
					System.exit(ERR_BAD_LINK_FORMAT);
				}
			} else {
				System.out.println("\nReceived response with content type "+MediaTypeRegistry.toString(response.getContentType()));
				System.out.println("Publishing response on ZMQ socket "+publisher.toString());

				//			pubSocket.send(MediaTypeRegistry.toString(response.getContentType()).getBytes(), ZMQ.SNDMORE);
				//			publisher.send(response.getPeerAddress().toString().getBytes(), ZMQ.SNDMORE);
				publisher.send(response.getPayload(), 0);

			}

			if (!response.hasOption(OptionNumberRegistry.OBSERVE)) {
				Request _req = response.getRequest();
				boolean removed = pendingRequests.remove(_req);
				if(removed) {
					System.out.println("\nPending request removed");
				}
			}

		} else {
			// TODO FIXME: need to find a way to receive timeout and clean the pending requests
			System.out.println("Handling request timeout");
//			boolean removed = pendingRequests.remove(response.getRequest());
//			if(removed) {
//				System.out.println("\nPending request removed");
//			} else {
//				System.out.println("\nNo pending request found");
//			}
		}
	}

	void doRequest(String method, String strUri) {
		URI uri;
		try {
			uri = new URI(strUri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		doRequest(method, uri);
	}

	void doRequest(String method, URI uri) {

		Request request = newRequest(method);
		URI reqUri = uri;

		//		if(useCoapProxy) {
		//			System.out.println("Setting proxy for request");
		//			request.setOption(new Option(uri.toString(), OptionNumberRegistry.PROXY_URI));
		//		}

		if (method.equals("OBSERVE")) {
			request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));
		}

		// set request URI
		if (method.equals("DISCOVER") && (uri.getPath() == null || uri.getPath().isEmpty() || uri.getPath().equals("/"))) {
			// add discovery resource path to URI
			try {
				reqUri = new URI(uri.getScheme(), uri.getAuthority(), DISCOVERY_RESOURCE, uri.getQuery());

			} catch (URISyntaxException e) {
				System.err.println("Failed to parse URI: " + e.getMessage());
				System.exit(ERR_BAD_URI);
			}

		}

		if(useCoapProxy) {
			System.out.println("Setting proxy for request");
			request.setOption(new Option(reqUri.toString(), OptionNumberRegistry.PROXY_URI));
//			request.setURI(reqUri);
			// override peer address with proxy address
			EndpointAddress a = new EndpointAddress(proxyUri);
			request.setPeerAddress(a);
		} else {
			System.out.println("No proxy for request");
			request.setURI(reqUri);
		}
		request.setPayload("");
		request.setToken( TokenManager.getInstance().acquireToken() );


		ResponseHandler respHandler = new ResponseHandler() {

//			@Override
			public void handleResponse(Response response) {
				doHandleResponse(response);
			}
		};
		request.registerResponseHandler(respHandler);

		// enable response queue in order to use blocking I/O
		//	request.enableResponseQueue(true);

		//
		request.prettyPrint();

		pendingRequests.add(request);

		// execute request
		try {
			request.execute();
		}
		catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
			System.exit(ERR_REQUEST_FAILED);
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(ERR_REQUEST_FAILED);
		}
	}

	/*
	 * Outputs user guide of this program.
	 */
	public static void printInfo() {
		System.out.println("Discovery client");
		System.out.println("(c) 2012, Thales communications and security, Colombes.");
		System.out.println();
		System.out.println("Usage: " + DiscoverClient.class.getSimpleName() + " RplEventSubZmQueue outPubZmQueue <coapProxy>");
		System.out.println("  RplEventSubZmQueue  : The ZMQ queue to receive RPL events");
		System.out.println("  outPubZmQueue       : The ZMQ queue to publish events");
		System.out.println("  coapProxy           : The CoAP proxy to use (optional)");
		System.out.println();
		System.out.println("Examples:");
		System.out.println("  DiscoverClient tcp://127.0.0.1:5555 tcp://*:5557");
		System.out.println("  DiscoverClient tcp://127.0.0.1:5555 tcp://*:5557 coap://[::1]:5683");
	}

	/*
	 * Instantiates a new request based on a string describing a method.
	 * 
	 * @return A new request object, or null if method not recognized
	 */
	private static Request newRequest(String method) {
		if (method.equals("GET")) {
			return new GETRequest();
		} else if (method.equals("POST")) {
			return new POSTRequest();
		} else if (method.equals("PUT")) {
			return new PUTRequest();
		} else if (method.equals("DELETE")) {
			return new DELETERequest();
		} else if (method.equals("DISCOVER")) {
			return new GETRequest();
		} else if (method.equals("OBSERVE")) {
			return new GETRequest();
		} else {
			return null;
		}
	}

}
