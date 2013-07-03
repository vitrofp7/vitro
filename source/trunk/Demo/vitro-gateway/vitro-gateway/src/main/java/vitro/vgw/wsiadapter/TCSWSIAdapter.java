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
package vitro.vgw.wsiadapter;

import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.CoapChannelManager;
import org.ws4d.coap.interfaces.CoapClient;
import org.ws4d.coap.interfaces.CoapClientChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.interfaces.CoapResponse;
import org.ws4d.coap.messages.CoapEmptyMessage;
import org.ws4d.coap.messages.CoapRequestCode;
import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.exception.WSIAdapterException;
import vitro.vgw.model.Node;
import vitro.vgw.model.NodeDescriptor;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.wsiadapter.coap.model.MoteResource;
import vitro.vgw.wsiadapter.coap.model.Network;
import vitro.vgw.wsiadapter.coap.util.Constants;
import vitro.vgw.wsiadapter.coap.util.Functions;
import ch.ethz.inf.vs.californium.coap.*;
import ch.ethz.inf.vs.californium.endpoint.RemoteResource;
import ch.ethz.inf.vs.californium.util.Log;
//import ch.ethz.inf.vs.californium.endpoint.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thalesgroup.tai.sensors.*;

public class TCSWSIAdapter implements WSIAdapter, CoapClient {
    private static final int UNDEFINED_COAP_MESSAGE_ID = -1;
    static ArrayList<Integer> timedOutCoapMessageIDsList = new ArrayList<Integer>();
    static ArrayList<Integer> timedOut_DTN_CoapMessageIDsList = new ArrayList<Integer>(); //stores packet Ids
    //15/04
    static HashMap<String, CoapClientChannel> proxyAddrToClientChannelResourcesHM = new HashMap<String, CoapClientChannel>();
    static HashMap<String, CoapClientChannel>  proxyAddrToClientChannelObservationsHM = new HashMap<String, CoapClientChannel>();


    private JSONParser parser;
	private List<NodeDescriptor> nodeList;
	private List<Node> listofnode;
	private List<Resource> resourceList;
	private Logger logger = LoggerFactory.getLogger(getClass());
	private CountDownLatch signal;
	private CoapChannelManager channelManager;
	//private CoapClientChannel clientChannelObservations;
    //private CoapClientChannel clientChannelResources;
    private String resourceValue;
	private String exceptionError;
	// uri of coap proxy
	private URI proxyUri;
    private boolean isDtnEnabled;
    private boolean trustCoapMessagingActivated;
	private final int PORT = Constants.COAP_DEFAULT_PORT;
	private List<Request> pendingRequests;

	private static final String DISCOVERY_RESOURCE = "/.well-known/core";

	private static final int IDX_ZMQ_RPLSOCKET  = 0; 
	private static final int IDX_ZMQ_PUBSOCKET   = 1;
	private static final int IDX_COAP_PROXY = 2;
	private static final int IDX_SOS_ENDPOINT = 3;

	private static final int ERR_BAD_URI         = 2;
	private static final int ERR_REQUEST_FAILED  = 5;
	private static final int ERR_RESPONSE_FAILED = 6;
	private static final int ERR_BAD_LINK_FORMAT = 7;
	private static final int ERR_MISSING_ZMQ_PULLSOCKET = 8;
	private static final int ERR_MISSING_ZMQ_PUBSOCKET = 9;
	private static final int ERR_MISSING_COAP_PROXY = 10;
	private static final int ERR_MISSING_SOS_ENDPOINT = 11;


	// if we use a coap proxy. Note: 03/05/2012, could not use jcoap as CoAP-CoAP
	// proxy. interoperability problem.
	private boolean useCoapProxy = false;


	public TCSWSIAdapter () {
		signal = null;
		channelManager = null;
        //clientChannelObservations = null;
        //clientChannelResources = null;
		nodeList = new ArrayList<NodeDescriptor>();
		listofnode = new ArrayList<Node>();
		resourceList = new ArrayList<Resource>();
		resourceValue = null;
		exceptionError = "";
		parser = new JSONParser();
		channelManager = BasicCoapChannelManager.getInstance();
        isDtnEnabled = false;
        trustCoapMessagingActivated = false;
		pendingRequests = new ArrayList<Request>();
	}

	/*
	 * This method returns the list of all Nodes managed by VGW.
	 *
	 * If a discovery process is not available on the underlying WSNs, a configuration file could be used
	 *
	 * Thales :
	 * Step 1 : Get the addresses of the json.
	 * Step 2 : Sort this information inside the right objects.
	 */

	public List<Node> getAvailableNodeList() throws WSIAdapterException {

		String raw_data = doHttpGet("http://[aaaa::212:7401:1:101]");

		if(!raw_data.equals("")) {
			Object raw_obj = null;

			try{

				raw_obj = parser.parse(raw_data);
			}
			catch(ParseException pe){

				System.out.println("position: " + pe.getPosition());
				System.out.println(pe);

			}





			System.out.println(raw_obj);

			JSONObject obj2 = (JSONObject) raw_obj;
			JSONArray raw_arr = (JSONArray) obj2.get("route"); 

			System.out.println("Routes are: " +raw_arr);


			//

			assert obj2 != null;
			//   JSONArray raw_arr = (JSONArray) obj2.get("address");

			//   System.out.println("Nodes are: " + raw_arr);
			Object[] objs = raw_arr.toArray();
			for (Object o:objs) {
				JSONObject id = (JSONObject) o;
				String ipaddr = ((String) id.get("address")).split("/")[0];
				Node n = new NodeDescriptor();

				Node m = new Node();

				n.setId(ipaddr);
				if(nodeList == null) {
					System.err.println("m vale " + m) ; return null;
				}   
				nodeList.add((NodeDescriptor) n);

				m.setId(ipaddr);
				listofnode.add(m);

			}
		}
		return listofnode;
	}

	public static String doHttpGet(String endpoint)
	{
		String output;
		String temp = "";
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
				temp = output;

			}
            br.close();
			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			System.err.println("ClientProtocolException\n");
			return "";

		} catch (IOException e) {
			System.err.println("IOException\n");
			return "";
		}
		
		return temp;
	}

	private int coapResourcesRequest(Node node) throws UnknownHostException, WSIAdapterException {
        int messageIDToReturn = UNDEFINED_COAP_MESSAGE_ID;
        String proxyAddress = getProxyAddress(node);

		if(proxyAddress != null) {
            // 15/04
            CoapClientChannel clientChannelResources = null;
            try {
                // *** Alternative approach
                if(!proxyAddrToClientChannelResourcesHM.isEmpty()
                        &&proxyAddrToClientChannelResourcesHM.containsKey(proxyAddress)
                        && proxyAddrToClientChannelResourcesHM.get(proxyAddress)!= null)
                {
                    //re use it.
                    clientChannelResources= proxyAddrToClientChannelResourcesHM.get(proxyAddress);

                } else {
                    clientChannelResources = channelManager.connect((CoapClient)this, InetAddress.getByName(proxyAddress), PORT);
                    if(clientChannelResources!=null)
                    {
                        proxyAddrToClientChannelResourcesHM.put(proxyAddress, clientChannelResources);
                    }
                    else {
                        logger.error("Impossible to setup a coap channel for resources!");
                        return UNDEFINED_COAP_MESSAGE_ID;
                    }
                }
                // *** end of alternative approach

                // == Fix that was still causing too many open files:
                //if(clientChannelResources!= null) {
                //    clientChannelResources.close();
                //}
                //clientChannelResources = channelManager.connect((CoapClient)this, InetAddress.getByName(proxyAddress), PORT);
                // == end of fix
                //			clientChannelResources = channelManager.connect(this, InetAddress.getByName("localhost"), PORT);
                CoapRequest coapRequest = clientChannelResources.createRequest(true, CoapRequestCode.GET);
                //coapRequest.setContentType(CoapMediaType.octet_stream);
                //coapRequest.setProxyUri("coap://[" + node.getId() + "]:5683/" + MoteResource.RESOURCE_DISCOVERY);
                System.out.println("Il nodo da contattare è "+node.getId());
                coapRequest.setUriHost(node.getId());
                coapRequest.setUriPort(5683);
                coapRequest.setUriPath(MoteResource.RESOURCE_DISCOVERY);
                clientChannelResources.sendMessage(coapRequest);
                messageIDToReturn = coapRequest.getMessageID();
                logger.info("Sent Request: {} for node {}", coapRequest.toString(), node.getId());
                //clientChannelResources.close();
            }
            catch(Exception ex) {
                if(clientChannelResources!=null)
                {
                //    clientChannelResources.close();
                }
                throw new WSIAdapterException("Unable to send coap resource req to " + node.getId());
            }
            finally {
                if(clientChannelResources !=null)
                {
                //    clientChannelResources.close();
                }
            }
		} else {
			logger.warn("No available proxy for Node " + node.getId() + " is found");
			throw new WSIAdapterException("No available proxy for Node " + node.getId() + " is found");
		}
        return messageIDToReturn;
	}


	public List<Resource> getResources(Node node) throws WSIAdapterException {
/*		List<Resource> resourceList = new ArrayList<Resource>();

		String ipaddr = node.getId();
		
		//discoverCoapEndpoint("coap://[" + ipaddr + "]:5683");
		Resource resource = null;
		try {
			resource = MoteResource.getResource("st");
			resourceList.add(resource);
			resource = MoteResource.getResource("co2");
			resourceList.add(resource);
		} catch (VitroGatewayException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return resourceList;
		}
		*/
		signal = new CountDownLatch(1);
		List<Resource> resourceList = new ArrayList<Resource>();
        int requestMessageID = UNDEFINED_COAP_MESSAGE_ID;
        try {
            boolean requestWasRespondedWithinTimelimit = false;
            if(isDtnEnabled) {
                //requestMessageID = dtnResourcesRequest(node);
                requestWasRespondedWithinTimelimit = signal.await(Constants.DTN_REQUEST_TIMEOUT, TimeUnit.MINUTES);
            } else {
                requestMessageID = coapResourcesRequest(node);
                // TODO: the signal await function will return FALSE if the timeout has expired.
                // This allows to manage the timeout case, and possibly the re-sending of messages that the coap gateway will do (until receiving a response)
                // The plan is that if this request times out, the ID of the request is logged in a static/global table and we further ignore messages for this ID (we don't handle them anymore).
                requestWasRespondedWithinTimelimit = signal.await(Constants.SIMPLE_COAP_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }

            if(!requestWasRespondedWithinTimelimit) {  //if we had timeout
                if(isDtnEnabled) //for DTN mode, iwe add it to a separate list (different callback function and messages )
                {
                    // TODO: we need to make something similar for DTN too!
                    if(requestMessageID !=  UNDEFINED_COAP_MESSAGE_ID && !timedOut_DTN_CoapMessageIDsList.contains(Integer.valueOf(requestMessageID))) {
                        timedOut_DTN_CoapMessageIDsList.add(Integer.valueOf(requestMessageID));
                    }

                } else {
                    //add the messageId to the list of IDs to ignore in the future (the check will be done in the callback function
                    if(requestMessageID !=  UNDEFINED_COAP_MESSAGE_ID && !timedOutCoapMessageIDsList.contains(Integer.valueOf(requestMessageID))) {
                        timedOutCoapMessageIDsList.add(Integer.valueOf(requestMessageID));
                    }
                }
            }
            else {//if no time-out occurred
                if(this.resourceList.size() > 0) {

                    resourceList.addAll(this.resourceList);
                    this.resourceList.clear();

                } else {

                    String error = "";
                    if(!exceptionError.equals("")) {
                        error = exceptionError;
                        exceptionError = "";
                    } else {
                        error = "No available resources for Node " + node.getId();
                    }

                    throw new WSIAdapterException(error);
                }
            }

        } catch (InterruptedException e) {
            throw new WSIAdapterException(e.getMessage(), e);
        } catch (UnknownHostException e) {
            throw new WSIAdapterException(e.getMessage(), e);
        }
		return resourceList;
	
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
		System.out.println("Ici???s");

		ResponseHandler respHandler = new ResponseHandler() {

			public void handleResponse(Response response) {
				System.out.println("On arrive ici");
				doHandleResponse(response);
			}
		};
		request.registerResponseHandler(respHandler);
		System.out.println("response handler registered");
		// enable response queue in order to use blocking I/O
		//	request.enableResponseQueue(true);

		//
		request.prettyPrint();

		pendingRequests.add(request);
		System.out.println("request stored");
		// execute request
		try {
			request.execute();
			System.out.println("request executed");
		}
		catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
			System.exit(ERR_REQUEST_FAILED);
		} catch (IOException e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(ERR_REQUEST_FAILED);
		}
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

	

	private String getProxyAddress(Node node) {
		String nodeIP = node.getId();
		logger.debug("node getid returns "+nodeIP);
		

		return nodeIP;
	}


    private String formatResourceValue(String resourceValue, Resource resource) {


        String resultRet  = "";
        StringBuilder resultBld = new StringBuilder();
        resultBld.append(resourceValue);
        resultRet = resultBld.toString();

        int resourceValueLength = resultRet.length();
        System.out.println("La stringa ricevuta è "+resultRet);

        String integerPart = resultRet.substring(0, resourceValueLength - 2);
        String decimalPart = resultRet.substring(resourceValueLength - 2, resourceValueLength);

        if(resource.getName().equals("temperature")
            //||
            //             resource.getName().equals(Resource.PHENOMENOM_LIGHT)
                ){
            //result = resultRet;//integerPart + "." + decimalPart;
            logger.debug("Result is "+resultRet);
        }
        else if(resource.getName().equals("co2")
            //||
            //             resource.getName().equals(Resource.PHENOMENOM_LIGHT)
                ){

            //result = resultRet;//integerPart + "." + decimalPart;
            logger.debug("Result co2 is "+resultRet);
        }

        return resultRet;

    }

	private int coapObservationRequest(Node node, Resource resource) throws UnknownHostException, VitroGatewayException {
        int messageIDToReturn = UNDEFINED_COAP_MESSAGE_ID;
        String proxyAddress = getProxyAddress(node);
		//proxyAddress = node.getId();
		if(proxyAddress != null) {
			String moteUriResource = "";
			if(MoteResource.containsValue(resource)) {
				//moteUriResource += "temperature";//MoteResource.getMoteUriResource(resource);
                String theResourceName = MoteResource.getMoteUriResource(resource);
                if(theResourceName == null) {
                    logger.error("unsupported resource");
                    return UNDEFINED_COAP_MESSAGE_ID;
                }
                // FOR TCS adapter, we prefer the TEMPERATURE_TCS
                // FOR WLAB and HAI we prefer the TEMPERATURE_ALT
                // we do this check because the getMoteUriResource is making a reverse lookup in the hashmap (where two keys point to the same resource)
                if( theResourceName.compareToIgnoreCase(MoteResource.TEMPERATURE_ALT ) == 0 ){
                    theResourceName =  MoteResource.TEMPERATURE_TCS;
                }
				moteUriResource += theResourceName;
				int PORT = Constants.COAP_DEFAULT_PORT;
                // 15/04
                CoapClientChannel clientChannelObservations = null;
                try {
                    // *** Alternative approach
                    if(!proxyAddrToClientChannelObservationsHM.isEmpty()
                            &&proxyAddrToClientChannelObservationsHM.containsKey(proxyAddress)
                            && proxyAddrToClientChannelObservationsHM.get(proxyAddress)!= null)
                    {
                        //re use it.
                        clientChannelObservations= proxyAddrToClientChannelObservationsHM.get(proxyAddress);

                    } else {
                        clientChannelObservations = channelManager.connect((CoapClient)this, InetAddress.getByName(proxyAddress), PORT);
                        if(clientChannelObservations!=null)
                        {
                            proxyAddrToClientChannelObservationsHM.put(proxyAddress, clientChannelObservations);
                        }
                        else {
                            logger.error("Impossible to setup a coap channel for observations!");
                            return UNDEFINED_COAP_MESSAGE_ID;
                        }
                    }
                    // *** end of alternative approach

                    // == Fix that was still causing too many open files:
                    //if(clientChannelObservations!= null) {
                    //    clientChannelObservations.close();
                    //}
                    //clientChannelObservations = channelManager.connect((CoapClient) this, InetAddress.getByName(proxyAddress), PORT);
                    // == end of fix

                    //				clientChannelObservations = channelManager.connect(this, InetAddress.getByName("localhost"), PORT);
                    CoapRequest coapRequest = clientChannelObservations.createRequest(true, CoapRequestCode.GET);
                    //coapRequest.setProxyUri("coap://[" + node.getId() + "]:5683/" + moteUriResource);
                    coapRequest.setUriHost(node.getId());
                    System.out.println("l'host è "+ node.getId());
                    coapRequest.setUriPort(PORT);
                    System.out.println("la porta è "+ PORT);
                    coapRequest.setUriPath(moteUriResource);
                    System.out.println("l'URI è "+ coapRequest.getUriPath());
                    clientChannelObservations.sendMessage(coapRequest);
                    messageIDToReturn = coapRequest.getMessageID();
                    logger.info("Sent Request: " + coapRequest.toString());
                    //clientChannelObservations.close();
                }
                catch(Exception ex) {
                    logger.error("Could not setup a coap channel or send the coapObservationRequest message!", ex);
                    if(clientChannelObservations !=null)
                    {
                        //clientChannelObservations.close();
                    }
                    throw new WSIAdapterException("Unable to send coap observe req to " + node.getId() + " and Resource " + resource.getName());
                }
                finally {
                    if(clientChannelObservations !=null)
                    {
                        //clientChannelObservations.close();
                    }
                }
			} else {
				logger.warn("No resource mapping for Node " + node.getId() + " and Resource " + resource.getName());
				throw new WSIAdapterException("No resource mapping for Node " + node.getId() + " and Resource " + resource.getName());
			}
			//
		} else {
			logger.warn("No available proxy for Node " + node.getId() + " is found");
			throw new WSIAdapterException("No available proxy for Node " + node.getId() + " is found");
		}
        return messageIDToReturn;
	}

	public Observation getNodeObservation(Node node, Resource resource) throws WSIAdapterException {
		signal = new CountDownLatch(1);
		Observation observation = new Observation();

		try {
            logger.debug("arrivo qui");

            int requestMessageID = UNDEFINED_COAP_MESSAGE_ID;
            boolean requestWasRespondedWithinTimelimit = false;
            if(isDtnEnabled) {
               // requestMessageID = dtnObservationRequest(node, resource);
                requestWasRespondedWithinTimelimit = signal.await(Constants.DTN_REQUEST_TIMEOUT, TimeUnit.MINUTES);
            } else {
                requestMessageID = coapObservationRequest(node, resource);
                // TODO: the signal await function will return FALSE if the timeout has expired.
                // This allows to manage the timeout case, and possibly the re-sending of messages that the coap gateway will do (until receiving a response)
                // The plan is that if this request times out, the ID of the request is logged in a static/global table and we further ignore messages for this ID (we don't handle them anymore).
                requestWasRespondedWithinTimelimit = signal.await(Constants.SIMPLE_COAP_REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
            if(!requestWasRespondedWithinTimelimit) { //if we had timeout
                if(isDtnEnabled) //for DTN mode, we add it to a separate list (different callback function and messages )
                {
                    if(requestMessageID !=  UNDEFINED_COAP_MESSAGE_ID && !timedOut_DTN_CoapMessageIDsList.contains(Integer.valueOf(requestMessageID))) {
                        timedOut_DTN_CoapMessageIDsList.add(Integer.valueOf(requestMessageID));
                    }
                } else {
                    //add the messageId to the list of IDs to ignore in the future (the check will be done in the callback function
                    if(requestMessageID !=  UNDEFINED_COAP_MESSAGE_ID && !timedOutCoapMessageIDsList.contains(Integer.valueOf(requestMessageID))) {
                        timedOutCoapMessageIDsList.add(Integer.valueOf(requestMessageID));
                    }
                }
            }
            else {
                if(resourceValue != null) {

                    observation.setNode(node);
                    observation.setResource(resource);
                    observation.setValue(formatResourceValue(resourceValue, resource));
                    observation.setTimestamp(System.currentTimeMillis());
                    resourceValue = null;

                } else {

                    String error;
                    if(!exceptionError.equals("")) {
                        error = exceptionError;
                        exceptionError = "";
                    } else {
                        error = "No available resources for Node " + node.getId() + " and Resource " + resource.getName();
                    }

                    throw new WSIAdapterException(error);
                }
            }

		} catch (UnknownHostException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		} catch (InterruptedException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		} catch (VitroGatewayException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		}

		return observation;
	}


	public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
		if(notReachable) {
			logger.warn("Connection failed: server is not reachable");
			exceptionError = "Connection failed: server is not reachable";
		} else {
			logger.warn("Connection failed");
			exceptionError = "Connection failed";
		}
        // signal.countDown(); //commented out because the message will timeout.
	}

    public void onResponse(CoapClientChannel channel, CoapResponse response) {
        boolean countDownTheSignal = false;
        try {
            if( !timedOutCoapMessageIDsList.contains(Integer.valueOf(response.getMessageID())) && response.getMessageID() != UNDEFINED_COAP_MESSAGE_ID ) {
                manageResponse(response);
                countDownTheSignal = true;
            }
            // TODO: if it is contained, do we remove it to clean up the list? (also the messageID for the coapMessages could be rotating, so we would need clean-up !!)
            //                          or do we expect further responses for a timeout message (due to the re-transmissions?)
        } catch (VitroGatewayException e) {
            logger.error(e.getMessage());
        }
        if (countDownTheSignal) {
            signal.countDown();
        }
    }


	public void onSeparateResponse(CoapClientChannel channel, CoapResponse message) {
		logger.info("Received Separate Response");
		//TODO: no implementation in TinyOS
        // signal.countDown(); //commented out because the message will timeout.
	}


	private void manageTextPlain(byte[] payloadBytes) throws VitroGatewayException {
		String payloadString = new String(payloadBytes);
		System.out.println("The resource is " + payloadString );
		resourceValue = payloadString;
	}


	synchronized void doHandleResponse(Response response) {
		System.out.println("Receiving COAP response...");
		boolean success = true;

		if (response != null) {
			response.prettyPrint();
			System.out.println(response.getMID());
			System.out.println("Time elapsed (ms): " + response.getRTT());

			// check of response contains resources
			String data="";
			if (response.getContentType()==MediaTypeRegistry.APPLICATION_LINK_FORMAT) {

				data = response.getPayloadString();
				System.out.println("\npayload string: "+data);

				// create resource tree from link format
				ch.ethz.inf.vs.californium.endpoint.Resource root = ch.ethz.inf.vs.californium.endpoint.RemoteResource.newRoot(data);
				if (root != null) {

					// output discovered resources
					System.out.println("\nDiscovered resources:");
					root.prettyPrint();
					for(ch.ethz.inf.vs.californium.endpoint.Resource resource: root.getSubResources()){
						
						vitro.vgw.model.Resource r = null;
						try {
							String proposition = resource.getPath().replace("/", "");
							System.out.println("=> Proposition : " + proposition);
							if ( proposition.equals(Resource.PHENOMENOM_TEMPERATURE)){
								r = vitro.vgw.model.Resource.getResource(proposition);
								resourceList.add(r);
							}
							
						} catch (VitroGatewayException e) {
							System.out.println("Tu fais un amalgame entre la coqueterie et la classe");
							e.printStackTrace();
						}

					}
					System.out.println("Liste finale =>" + resourceList.toString());
					

					//					for (ch.ethz.inf.vs.californium.endpoint.Resource resource:root.getSubResources()) {
					//						// automatically subscribe to observable resources
					//						if(resource.isObservable()) {
					//							System.out.println("\nSubscribing to observable resource:"+response.getPeerAddress()+ resource.getPath());
					//							doRequest("OBSERVE", "coap://" + response.getPeerAddress()+ resource.getPath());
					//						}
					//
					//						String id="randomstuff"+response.getPeerAddress()+resource.getPath();
					//
					//
					//					}


				} else {
					System.err.println("Failed to parse link format");
					System.exit(ERR_BAD_LINK_FORMAT);
				}
			} else {
				System.out.println("\nReceived response from "+ response.getPeerAddress());
				System.out.println("\n   content type "+MediaTypeRegistry.toString(response.getContentType()));
			}

			if (!response.hasOption(OptionNumberRegistry.OBSERVE)) {
				Request _req = response.getRequest();
				boolean removed = pendingRequests.remove(_req);
				if(removed) {
					System.out.println("\nPending request removed");
				}
			}

			// TODO FIXME we need to keep a registry of the id in the link-format and the IP address of 
			// the node in here somewhere. Because, when we receive a response and we don't keep the id 
			// discovered for the node, we have no way to get it.
			String id="randomstuff"+response.getPeerAddress()+response.getUriPath();
			//sosClient.doAddObservation(id, MediaTypeRegistry.toString(response.getContentType()), response.getPayload());

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


	private void manageOctetStream(byte[] payloadBytes) {
		if(payloadBytes.length == 2) {
			short payloadShort = Functions.byteArraytoShort(payloadBytes);
			resourceValue = String.valueOf(payloadShort);
		} else {
            // CHANGED THIS TO REFLECT THE EXACT BYTES ARRAY AND BE ABLE TO RECONSTRUCT IT
            //resourceValue = new String(payloadBytes);
            resourceValue = Base64.encodeBase64String(payloadBytes);
        }
	}



	private void manageResponse(CoapResponse response) throws VitroGatewayException {
		logger.info("Received response code: " + response.getResponseCode());
		logger.info("CoAP Message ID: " + response.getMessageID());
		logger.info("Content Type: " + response.getContentType());
		logger.info("CoAP Server address: " + response.getChannel().getRemoteAddress());

		if(response.getPayload() == null) {
			return;
		}

		byte[] payloadBytes = response.getPayload();
		if(payloadBytes.length > 0) {
			switch(response.getContentType().getValue()) {
			case 0: {
				manageTextPlain(payloadBytes); //textplain
				break;
			}
			case 40: {
				manageLinkFormat(payloadBytes); //linkformat --> /.well-known/core
				break;
			}
			case 41: //TODO: xml (required???)
				break;
			case 42: {
				manageOctetStream(payloadBytes); //octetstream --> resource outcomes
				break;
			}
			case 47: //TODO: exi (required???)
				break;
			case 50: //TODO: json (required???)
			default: logger.warn("Unknown Content Type");
			}
		} else {
			logger.warn("The payload is empty.");
		}
	}

	private void manageLinkFormat(byte[] payloadBytes) throws VitroGatewayException {

		String payloadString = new String(payloadBytes);

		/** 
		 * Manage /.well-known/core resource 
		 */

		// Pattern example: </st>;ct=42,</sh>;ct=42
		Pattern p = Pattern.compile("(</\\w+>);?(ct=\\d+)?", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(payloadString);

		while(m.find()) {
			logger.debug("Regex Result: " + m.group());

			/** Resource Name */
			if(m.group(1) != null) {
				String resourceName = m.group(1).replaceAll("<|/|>", "");
				if(MoteResource.containsKey(resourceName)) {
					logger.debug("Resource name: " + resourceName);
					Resource resource = MoteResource.getResource(resourceName);
					resourceList.add(resource);
				}
			}

			/** Resource Content Type 
			if(m.group(0) != null) {
				String resourceName = m.group(0).replaceAll("<|/|>", "");
				if(MoteResource.containsKey(resourceName)) {
					logger.debug("Resource name: " + resourceName);
					Resource resource = MoteResource.getResource(resourceName);
					resourceList.add(resource);
				}
			}*/
		}
	}


	public void onSeparateResponseAck(CoapClientChannel channel, CoapEmptyMessage message) {
		logger.info("Received Ack of Separate Response");
		//TODO: no implementation in TinyOS
        // signal.countDown(); //commented out because the message will timeout.
	}

	
	
	
	synchronized List<Resource> doHandleResponse1(Response response) {
		System.out.println("Receiving COAP response...");
		boolean success = true;

		if (response != null) {
			response.prettyPrint();
			System.out.println(response.getMID());
			System.out.println("Time elapsed (ms): " + response.getRTT());

			// check of response contains resources
			String data="";
			if (response.getContentType()==MediaTypeRegistry.APPLICATION_LINK_FORMAT) {

				data = response.getPayloadString();
				System.out.println("\npayload string: "+data);

				// create resource tree from link format
				ch.ethz.inf.vs.californium.endpoint.Resource root = ch.ethz.inf.vs.californium.endpoint.RemoteResource.newRoot(data);
				if (root != null) {

					// output discovered resources
					System.out.println("\nDiscovered resources:");
					root.prettyPrint();
					for(ch.ethz.inf.vs.californium.endpoint.Resource resource: root.getSubResources()){
						
						vitro.vgw.model.Resource r = null;
						try {
							String proposition = resource.getPath().replace("/", "");
							System.out.println("=> Proposition : " + proposition);
							if ( proposition.equals(Resource.PHENOMENOM_TEMPERATURE)){
								r = vitro.vgw.model.Resource.getResource(proposition);
								resourceList.add(r);
							}
							
						} catch (VitroGatewayException e) {
							System.out.println("Tu fais un amalgame entre la coqueterie et la classe");
							e.printStackTrace();
						}

					}
					System.out.println("Liste finale =>" + resourceList.toString());
					

					//					for (ch.ethz.inf.vs.californium.endpoint.Resource resource:root.getSubResources()) {
					//						// automatically subscribe to observable resources
					//						if(resource.isObservable()) {
					//							System.out.println("\nSubscribing to observable resource:"+response.getPeerAddress()+ resource.getPath());
					//							doRequest("OBSERVE", "coap://" + response.getPeerAddress()+ resource.getPath());
					//						}
					//
					//						String id="randomstuff"+response.getPeerAddress()+resource.getPath();
					//
					//
					//					}


				} else {
					System.err.println("Failed to parse link format");
					System.exit(ERR_BAD_LINK_FORMAT);
				}
			} else {
				System.out.println("\nReceived response from "+ response.getPeerAddress());
				System.out.println("\n   content type "+MediaTypeRegistry.toString(response.getContentType()));
			}

			if (!response.hasOption(OptionNumberRegistry.OBSERVE)) {
				Request _req = response.getRequest();
				boolean removed = pendingRequests.remove(_req);
				if(removed) {
					System.out.println("\nPending request removed");
				}
			}

			// TODO FIXME we need to keep a registry of the id in the link-format and the IP address of 
			// the node in here somewhere. Because, when we receive a response and we don't keep the id 
			// discovered for the node, we have no way to get it.
			String id="randomstuff"+response.getPeerAddress()+response.getUriPath();
			//sosClient.doAddObservation(id, MediaTypeRegistry.toString(response.getContentType()), response.getPayload());

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
		return resourceList;
	}



    public boolean getDtnPolicy() {
        return isDtnEnabled;
    }

    /**
     * is supposed to start the DTN (switch on the DTN mode)
     */
    public void setDtnPolicy(boolean value) {
        isDtnEnabled = value;
    }

    // ------------------ trust coap messaging
    public boolean isTrustCoapMessagingActive(){
        return  trustCoapMessagingActivated;
    }
    /**
     * is supposed to start the Trust Coap Messaging (switch on the Trust Coap Messaging mode)
     */
    public void setTrustCoapMessagingActive(boolean value){
        trustCoapMessagingActivated = value;
    }


    /**
     * Used to get the coap response message for the trust routing resource from a node.  (the Resource parameter will always be Resource.RES_TRUST_ROUTING
     * @param node the  node queried
     * @param resource the resource queried (always Resource.RES_TRUST_ROUTING)
     * @return InfoOnTrustRouting object with the response
     */
    public InfoOnTrustRouting getNodeTrustRoutingInfo(Node node, Resource resource) throws WSIAdapterException {
        // TODO: an implementation is needed if this action is supported
        return null;
    }

}
