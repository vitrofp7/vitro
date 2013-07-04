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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;
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
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.wsiadapter.coap.model.MoteResource;
import vitro.vgw.wsiadapter.coap.model.Network;
import vitro.vgw.wsiadapter.coap.util.Constants;
import vitro.vgw.wsiadapter.coap.util.Functions;
import org.apache.commons.codec.binary.Base64;


public class WSIAdapterCoapHAI implements WSIAdapter, CoapClient, Observer {
	private static final int UNDEFINED_COAP_MESSAGE_ID = -1;
    static ArrayList<Integer> timedOutCoapMessageIDsList = new ArrayList<Integer>();
    static ArrayList<Integer> timedOut_DTN_CoapMessageIDsList = new ArrayList<Integer>(); //stores packet Ids

    //15/04
    static HashMap<String, CoapClientChannel>  proxyAddrToClientChannelResourcesHM = new HashMap<String, CoapClientChannel>();
    static HashMap<String, CoapClientChannel>  proxyAddrToClientChannelObservationsHM = new HashMap<String, CoapClientChannel>();


    /**
	 * @author Francesco Ficarola (ficarola<at>dis.uniroma1<dot>it)
	 * @author Kyriakos Georgouleas
	 */
	
	private Logger logger = LoggerFactory.getLogger(WSIAdapterCoap.class);
	private CountDownLatch signal;
	
	private final int COAP_PORT = Constants.COAP_DEFAULT_PORT;
	
	private CoapChannelManager channelManager;

	private CoapClientChannel clientChannel;

    private List<Resource> resourceList;
    private String resourceValue;
    
    private DTNResponder dtnServer;
    private Thread threadDTN;
    private Random random;
    private boolean isDtnEnabled;
    private boolean trustCoapMessagingActivated;
    private final String RESOURCE_REQ = "1";
    
    private String exceptionError;
    
    List<String> nodeIdList; //private List<Node> nodesList;
	
	
	public WSIAdapterCoapHAI() {
		signal = null;
		channelManager = null;
		clientChannel = null;
		//clientChannelObservations = null;
        //clientChannelResources = null;
		resourceList = new ArrayList<Resource>();
		resourceValue = null;
		exceptionError = "";
		random = new Random();
		dtnServer = new DTNResponder(Constants.DTN_VGW_PORT, this);
		threadDTN = new Thread(dtnServer);
		isDtnEnabled = false;
        trustCoapMessagingActivated = false;
		channelManager = BasicCoapChannelManager.getInstance();
		
		nodeIdList = new ArrayList<String>(); //nodesList = new LinkedList<Node>();
	}

	
	/**
	 * WSIAdapter interface
	 */

/*	public List<Node> getAvailableNodeList() throws WSIAdapterException {
		
		logger.info("Getting available nodes...");
		
		nodesList = new ArrayList<Node>();
		
		List<String> wsnProxyList = new LinkedList<String>();
		wsnProxyList.add(Network.WLAB_OFFICE_PROXY_ADDRESS);
		wsnProxyList.add(Network.WLAB_LAB_PROXY_ADDRESS);
		
		DatagramSocket serverSocket = null;
		DatagramSocket clientSocket = null;
		String cmdString = "route";
		
		for(int i=0; i < wsnProxyList.size(); i++) {
			try {
				serverSocket = new DatagramSocket(Constants.UDPSHELL_VGW_PORT);
			
				String hostProxyString = wsnProxyList.get(i);
				InetAddress hostProxy = InetAddress.getByName(hostProxyString);
								
				clientSocket = new DatagramSocket();
				byte[] bufCmd = new byte[10];
				bufCmd = cmdString.getBytes();
				DatagramPacket outcomingPacket = new DatagramPacket(bufCmd, bufCmd.length, hostProxy, Constants.PROXY_UDPFORWARDER_PORT);
				clientSocket.send(outcomingPacket);
				
				boolean otherPackets = false;
				
				serverSocket.setSoTimeout(Constants.PROXY_RESPONSE_TIMEOUT);
				logger.info("Quering " + hostProxyString);
				try {
					byte[] bufAck = new byte[10];
					DatagramPacket ackPacket = new DatagramPacket(bufAck, bufAck.length);
					serverSocket.receive(ackPacket);
					String ackString = new String(ackPacket.getData()).trim();
					if(ackString.equals("ack")) {
						otherPackets = true;
					}
				} catch (SocketTimeoutException e) {
					logger.warn(e.getMessage());
				}
				
				serverSocket.setSoTimeout(0);
				
		        while(otherPackets) {
		        	try {
		        		byte[] bufIncoming = new byte[1000];
		        		DatagramPacket incomingPacket = new DatagramPacket(bufIncoming, bufIncoming.length);
		        		serverSocket.receive(incomingPacket);
		        		String currentNodeIP = new String(incomingPacket.getData()).trim();
		        		if(!currentNodeIP.equals("end")) {
			        		logger.info("Node: " + currentNodeIP);
		        			nodesList.add(new Node(currentNodeIP));
		        		} else {
		        			otherPackets = false;
		        			logger.info("No other nodes from " + hostProxyString);
		        		}
		        	} catch (IOException e) {
		        		logger.error(e.getMessage());
					}
		        }
			
			
			} catch (UnknownHostException e) {
				logger.warn(e.getMessage() + " is not reachable.");
			} catch (SocketException e) {
				logger.error(e.getMessage());
			} catch (IOException e) {
				logger.error(e.getMessage());
			} finally {
				if(serverSocket != null) {
					serverSocket.close();
				}
				if(clientSocket != null) {
					clientSocket.close();
				}
			}
		}
		
		return nodesList;
	}
*/
	public List<Node> getAvailableNodeList() throws WSIAdapterException {
		if(nodeIdList.isEmpty()){
			return getDefaultNodeList();
			//return getNodeListFromPppRouter();
		} else{
			List<Node> result = new ArrayList<Node>();
			
			for(String nodeId: nodeIdList){
				Node node = new Node(nodeId);
				result.add(node);
			}
			
			return result;
		}
	}
	
	public List<Node> getDefaultNodeList() throws WSIAdapterException {
		
		List<Node> result = new ArrayList<Node>();
		
		Node node = new Node("fec0::2");
		result.add(node);
		
		node = new Node("fec0::3");
		result.add(node);
		
		node = new Node("fec0::5");
		result.add(node);
		
		node = new Node("fec0::7");
		result.add(node);
		
		node = new Node("fec0::8");
		result.add(node);
		
		node = new Node("fec0::9");
		result.add(node);
		
		node = new Node("fec0::b");
		result.add(node);
		
		node = new Node("fec0::d");
		result.add(node);
		
		node = new Node("fec0::f");
		result.add(node);
		
		node = new Node("fec0::11");
		result.add(node);
		
		node = new Node("fec0::13");
		result.add(node);
		
		node = new Node("fec0::15");
		result.add(node);
		
		return result;
	}
	
/*	public List<Resource> getResources(Node node) throws WSIAdapterException {
		
		signal = new CountDownLatch(1);
		List<Resource> resourceList = new ArrayList<Resource>();
        int requestMessageID = UNDEFINED_COAP_MESSAGE_ID;
		try {
            boolean requestWasRespondedWithinTimelimit = false;
            if(isDtnEnabled) {
                requestMessageID = dtnResourcesRequest(node);
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
            else {
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
		} catch (IOException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		}
		
		return resourceList;
	}
*/
	public List<Resource> getResources(Node node) throws WSIAdapterException {
		
		signal = new CountDownLatch(1);
		List<Resource> resourceList = new ArrayList<Resource>();

		try {
			coapResourcesRequest(node);
			signal.await();

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
			
		} catch (InterruptedException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		} catch (UnknownHostException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		}
		
		return resourceList;
	}

	
/*	public Observation getNodeObservation(Node node, Resource resource) throws WSIAdapterException {
		
		signal = new CountDownLatch(1);
		Observation observation = new Observation();
		
		try {
            int requestMessageID = UNDEFINED_COAP_MESSAGE_ID;
            boolean requestWasRespondedWithinTimelimit = false;
			if(isDtnEnabled) {
                requestMessageID = dtnObservationRequest(node, resource);
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

                    String error = "";
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
		} catch (IOException e) {
			throw new WSIAdapterException(e.getMessage(), e);
		}
		
		return observation;
	}
*/
	public Observation getNodeObservation(Node node, Resource resource) throws WSIAdapterException {
		
		signal = new CountDownLatch(1);
		Observation observation = new Observation();
		
		try {
			
			coapObservationRequest(node, resource);
			signal.await();
			
			if(resourceValue != null) {
				
				observation.setNode(node);
				observation.setResource(resource);
				observation.setValue(formatResourceValue(resourceValue, resource));
				observation.setTimestamp(System.currentTimeMillis());
				resourceValue = null;
				
			} else {
				
				String error = "";
				if(!exceptionError.equals("")) {
					error = exceptionError;
					exceptionError = "";
				} else {
					error = "No available resources for Node " + node.getId() + " and Resource " + resource.getName();
				}
				
				throw new WSIAdapterException(error);
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
	

	/**
	 * DTN methods
	 */
	
	public void update(Observable obs, Object obj) {
		String dtnMsg = (String)obj;
		try {
			onDtnResponse(dtnMsg);
		} catch (VitroGatewayException e) {
			logger.error(e.getMessage());
		}
	}
	
	private void onDtnResponse(String dtnMsg) throws VitroGatewayException {
        boolean countDownTheSignal = false;

		List<String> msgElements = new LinkedList<String>();
		StringTokenizer st = new StringTokenizer(dtnMsg, "#");
		while(st.hasMoreTokens()) {
			msgElements.add(st.nextToken().trim());
		}
		
		if(msgElements.size() != 4) {
			logger.error("Malformed DTN message: " + dtnMsg);
			//return;  // and no countdown is performed
		}
		else {
            String packetID = msgElements.get(0);
            String serverID = msgElements.get(1);
            String packetType = msgElements.get(2);
            String packetBody = msgElements.get(3);

            if(packetID != null && !packetID.trim().isEmpty())
            {
                Integer packetIDInt = Integer.valueOf(UNDEFINED_COAP_MESSAGE_ID);
                try{
                    packetIDInt = Integer.valueOf(packetID);
                }catch (NumberFormatException nfe1)
                {
                    logger.debug("NUMBER FORMAT EXCEPTION FOR DTN Packet ID: " + packetID);
                }
                if(packetIDInt == Integer.valueOf(UNDEFINED_COAP_MESSAGE_ID) || timedOut_DTN_CoapMessageIDsList.contains(Integer.valueOf(packetID))){
                    countDownTheSignal = false;
                    logger.debug("IGNORING TIMED OUT DTN Packet ID: " + packetID);
                    // TODO: if it is contained, do we remove it to clean up the list? (also the messageID for the coapMessages could be rotating, so we would need clean-up !!)
                    //                          or do we expect further responses for a timeout message (due to the re-transmissions?)
                }
                else {
                    logger.info("DTN Packet ID: " + packetID);
                    logger.info("DTN Server ID: " + serverID);
                    logger.info("DTN Packet Type: " + packetType);
                    logger.info("DTN Packet Body: " + packetBody);

                    List<String> bodyElements = new LinkedList<String>();
                    st = new StringTokenizer(packetBody, "*");
                    while(st.hasMoreTokens()) {
                        bodyElements.add(st.nextToken().trim());
                    }

                    if(bodyElements.get(0).equals("wkc")) {
                        for(int i = 1; i < bodyElements.size(); i++) {
                            String resourceName = bodyElements.get(i);
                            if(MoteResource.containsKey(resourceName)) {
                                logger.debug("Resource name: " + resourceName);
                                Resource resource = MoteResource.getResource(resourceName);
                                resourceList.add(resource);
                            }
                        }
                    } else
                    if(bodyElements.get(0).equals("st")) {
                        Integer readingValue = Integer.parseInt(bodyElements.get(1));
                        int rawValue = 23355 + readingValue - 200;
                        resourceValue = String.valueOf(rawValue);
                    } else
                    if(bodyElements.get(0).equals("sh")) {
                        Integer readingValue = Integer.parseInt(bodyElements.get(1));
                        int rawvalue = -204 + readingValue*4 - (readingValue*readingValue)*100/628931;
                        resourceValue = String.valueOf(rawvalue);
                    }
                    countDownTheSignal= true;
                }
            }
            else {
                logger.debug("EMPTY OR NULL DTN Packet ID found!");
            }
        }
        if (countDownTheSignal) {
            signal.countDown();
        }
	}
	
	public boolean getDtnPolicy() {
		return isDtnEnabled;
	}
	
	public void setDtnPolicy(boolean value) {
		isDtnEnabled = value;
		
		if(isDtnEnabled) {
			if(!threadDTN.isAlive()) {
				threadDTN.start();
			}
		}
	}
	
	private int dtnResourcesRequest(Node node) throws WSIAdapterException, IOException {
        int requestMsgId = UNDEFINED_COAP_MESSAGE_ID;     // TODO: ??? we will use the packetID as a message ID to return.
		String proxyAddress = getProxyAddress(node);
		if(proxyAddress != null) {
			int packetID =UNDEFINED_COAP_MESSAGE_ID;
            do { //while loop to avoid a packetID that exists in the array that is to be ignored!
                 packetID = random.nextInt(65535) + 1;
            } while(timedOut_DTN_CoapMessageIDsList.contains(Integer.valueOf(packetID)) || packetID==UNDEFINED_COAP_MESSAGE_ID );

			String msgString = Integer.toString(packetID) + "#" + node.getId() + "#" + RESOURCE_REQ + "#wkc";
			byte[] msgBytes = new byte[Constants.DTN_MESSAGE_SIZE];
			msgBytes = msgString.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getByName(proxyAddress), Constants.PROXY_UDPFORWARDER_PORT);
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.send(sendPacket);
			clientSocket.close();
            requestMsgId = packetID;
			logger.info("Sent Request: " + msgString);
		} else {
			logger.warn("No available proxy for Node " + node.getId() + " is found");
			throw new WSIAdapterException("No available proxy for Node " + node.getId() + " is found");
		}
        return requestMsgId;
	}
	
	private int dtnObservationRequest(Node node, Resource resource) throws VitroGatewayException, IOException {
        int requestMsgId = UNDEFINED_COAP_MESSAGE_ID;     // TODO: ??? we will use the packetID as a message ID to return.
        String proxyAddress = getProxyAddress(node);
		if(proxyAddress != null) {
			String moteUriResource = "";
			if(MoteResource.containsValue(resource)) {
				//moteUriResource += MoteResource.getMoteUriResource(resource);
                String theResourceName = MoteResource.getMoteUriResource(resource);
                if(theResourceName == null) {
                    logger.error("unsupported resource");
                    return UNDEFINED_COAP_MESSAGE_ID;
                }
                // FOR TCS adapter, we prefer the TEMPERATURE_TCS
                // FOR WLAB and HAI we prefer the TEMPERATURE_ALT
                // we do this check because the getMoteUriResource is making a reverse lookup in the hashmap (where two keys point to the same resource)
                if( theResourceName.compareToIgnoreCase(MoteResource.TEMPERATURE_TCS ) == 0 ){
                    theResourceName =  MoteResource.TEMPERATURE_ALT;
                }
                moteUriResource += theResourceName;
                int packetID =UNDEFINED_COAP_MESSAGE_ID;
                do { //while loop to avoid a packetID that exists in the array that is to be ignored!
                    packetID = random.nextInt(65535) + 1;
                } while(timedOut_DTN_CoapMessageIDsList.contains(Integer.valueOf(packetID)) || packetID == UNDEFINED_COAP_MESSAGE_ID);

				String msgString = packetID + "#" + node.getId() + "#" + RESOURCE_REQ + "#" + moteUriResource;
				byte[] msgBytes = new byte[Constants.DTN_MESSAGE_SIZE];
				msgBytes = msgString.getBytes();
				DatagramPacket sendPacket = new DatagramPacket(msgBytes, msgBytes.length, InetAddress.getByName(proxyAddress), Constants.PROXY_UDPFORWARDER_PORT);
				DatagramSocket clientSocket = new DatagramSocket();
				clientSocket.send(sendPacket);
				clientSocket.close();
                requestMsgId = packetID;
				logger.info("Sent Request: " + msgString);
			} else {
				logger.warn("No resource mapping for Node " + node.getId() + " and Resource " + resource.getName());
				throw new WSIAdapterException("No resource mapping for Node " + node.getId() + " and Resource " + resource.getName());
			}
		} else {
			logger.warn("No available proxy for Node " + node.getId() + " is found");
			throw new WSIAdapterException("No available proxy for Node " + node.getId() + " is found");
		}
        return requestMsgId;
	}

	
	
	/**
	 * CoapClient interface
	 */
	
	
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

	
	public void onSeparateResponseAck(CoapClientChannel channel, CoapEmptyMessage message) {
		logger.info("Received Ack of Separate Response");
		//TODO: no implementation in TinyOS
        // signal.countDown(); //commented out because the message will timeout.
	}
	
	public void connect(Node node)  throws UnknownHostException {
		String proxyAddress = getProxyAddress(node);
		if(proxyAddress != null) {
			clientChannel = channelManager.connect(this, InetAddress.getByName(proxyAddress), COAP_PORT);
		}

	}
	
	/**
	 * Private CoAP methods
	 */

/*	private int coapResourcesRequest(Node node) throws UnknownHostException, WSIAdapterException {
        int messageIDToReturn = UNDEFINED_COAP_MESSAGE_ID;
		String proxyAddress = getProxyAddress(node);
		if(proxyAddress != null) {
            // 15/04
            CoapClientChannel clientChannelResources = null;
            try{
                // *** Alternative approach
                if(!proxyAddrToClientChannelResourcesHM.isEmpty()
                        &&proxyAddrToClientChannelResourcesHM.containsKey(proxyAddress)
                        && proxyAddrToClientChannelResourcesHM.get(proxyAddress)!= null)
                {
                    //re use it.
                    clientChannelResources= proxyAddrToClientChannelResourcesHM.get(proxyAddress);

                } else {
                    clientChannelResources = channelManager.connect((CoapClient)this, InetAddress.getByName(proxyAddress), COAP_PORT);
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
                //clientChannelResources = channelManager.connect(this, InetAddress.getByName(proxyAddress), COAP_PORT);
                // == end of fix

    //			clientChannelResources = channelManager.connect(this, InetAddress.getByName("localhost"), PORT);
                CoapRequest coapRequest = clientChannelResources.createRequest(true, CoapRequestCode.GET);
                coapRequest.setProxyUri("coap://[" + node.getId() + "]:61616/" + MoteResource.RESOURCE_DISCOVERY);
    //			coapRequest.setUriPath("/" + MoteResource.RESOURCE_DISCOVERY);
                clientChannelResources.sendMessage(coapRequest);
                messageIDToReturn = coapRequest.getMessageID();
                logger.info("Sent Request: {} for node {}", coapRequest.toString(), node.getId());
                //clientChannelResources.close();
            }
            catch(Exception ex) {
                logger.error("Could not setup a coap channel or send the coapResourcesRequest message!", ex);
                if(clientChannelResources !=null)
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
*/
	private void coapResourcesRequest(Node node) throws UnknownHostException, WSIAdapterException {
		//String proxyAddress = getProxyAddress(node);
		//if(proxyAddress != null) {
		if(clientChannel != null) {	
			//clientChannel = channelManager.connect(this, InetAddress.getByName(proxyAddress), PORT);
//			clientChannel = channelManager.connect(this, InetAddress.getByName("localhost"), PORT);
			CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
			//coapRequest.setUriPath("/" + MoteResource.RESOURCE_DISCOVERY);
			//coapRequest.setProxyUri("coap://[" + node.getId() + "]:61616/" + MoteResource.TEMPERATURE);
			coapRequest.setProxyUri("coap://[" + node.getId() + "]:61616/" + MoteResource.RESOURCE_DISCOVERY);
			clientChannel.sendMessage(coapRequest);
			logger.info("Sent Request: {} for node {}", coapRequest.toString(), node.getId());
		} else {
			logger.warn("No available proxy for Node " + node.getId() + " is found");
			throw new WSIAdapterException("No available proxy for Node " + node.getId() + " is found");
		}
	}
	
/*	private int coapObservationRequest(Node node, Resource resource) throws UnknownHostException, VitroGatewayException {
        int messageIDToReturn = UNDEFINED_COAP_MESSAGE_ID;
        String proxyAddress = getProxyAddress(node);
		
		if(proxyAddress != null) {

			String moteUriResource = "";
			if(MoteResource.containsValue(resource)) {
				//moteUriResource += MoteResource.getMoteUriResource(resource);
                String theResourceName = MoteResource.getMoteUriResource(resource);
                if(theResourceName == null) {
                    logger.error("unsupported resource");
                    return UNDEFINED_COAP_MESSAGE_ID;
                }
                // FOR TCS adapter, we prefer the TEMPERATURE_TCS
                // FOR WLAB and HAI we prefer the TEMPERATURE_ALT
                // we do this check because the getMoteUriResource is making a reverse lookup in the hashmap (where two keys point to the same resource)
                if( theResourceName.compareToIgnoreCase(MoteResource.TEMPERATURE_TCS ) == 0 ){
                    theResourceName =  MoteResource.TEMPERATURE_ALT;
                }
                moteUriResource += theResourceName;
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
                        clientChannelObservations = channelManager.connect((CoapClient)this, InetAddress.getByName(proxyAddress), COAP_PORT);
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
                    //clientChannelObservations = channelManager.connect(this, InetAddress.getByName(proxyAddress), COAP_PORT);
                    // == end of fix
    //				clientChannelObservations = channelManager.connect(this, InetAddress.getByName("localhost"), PORT);
                    CoapRequest coapRequest = clientChannelObservations.createRequest(true, CoapRequestCode.GET);
                    coapRequest.setProxyUri("coap://[" + node.getId() + "]:61616/" + moteUriResource);
    //				coapRequest.setUriPath(moteUriResource);
                        clientChannelObservations.sendMessage(coapRequest);
                    messageIDToReturn = coapRequest.getMessageID();
                    logger.info("Sent Request: " + coapRequest.toString());
                        //clientChannelObservations.close();
                }
                catch(Exception ex) {
                    logger.error("Could not setup a coap channel or send the coapObservationRequest message!", ex);
                    if(clientChannelObservations !=null)
                    {
                        // clientChannelObservations.close();
                    }
                    throw new WSIAdapterException("Unable to send coap observe req to " + node.getId() + " and Resource " + resource.getName());
                }
                finally {
                    if(clientChannelObservations !=null)
                    {
                        // clientChannelObservations.close();
                    }
                }
            }else {
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
*/
	private void coapObservationRequest(Node node, Resource resource) throws UnknownHostException, VitroGatewayException {
		//String proxyAddress = getProxyAddress(node);
		
		//if(proxyAddress != null) {
		if(clientChannel != null) {	
			String moteUriResource = "";
			if(MoteResource.containsValue(resource)) {
				moteUriResource += MoteResource.getMoteUriResource(resource);
				//clientChannel = channelManager.connect(this, InetAddress.getByName(proxyAddress), PORT);
//				clientChannel = channelManager.connect(this, InetAddress.getByName("localhost"), PORT);
				CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
				coapRequest.setProxyUri("coap://[" + node.getId() + "]:61616/" + moteUriResource);
//				coapRequest.setUriPath(moteUriResource);
				clientChannel.sendMessage(coapRequest);
				logger.info("Sent Request: " + coapRequest.toString());
			} else {
				logger.warn("No resource mapping for Node " + node.getId() + " and Resource " + resource.getName());
				throw new WSIAdapterException("No resource mapping for Node " + node.getId() + " and Resource " + resource.getName());
			}
//			
		} else {
			logger.warn("No available proxy for Node " + node.getId() + " is found");
			throw new WSIAdapterException("No available proxy for Node " + node.getId() + " is found");
		}
	}
	
	private String getProxyAddress(Node node) {
		/*String nodeIP = node.getId();
		
		if(nodeIP.contains(Network.WLAB_OFFICE_IPV6_PREFIX) || nodeIP.contains(Network.WLAB_OFFICE_IPV6_PREFIX_SHORT)) {
			return Network.WLAB_OFFICE_PROXY_ADDRESS;
		} else
		if(nodeIP.contains(Network.WLAB_LAB_IPV6_PREFIX) || nodeIP.contains(Network.WLAB_LAB_IPV6_PREFIX_SHORT)) {
			return Network.WLAB_LAB_PROXY_ADDRESS;
		}
		
		return null;*/
		
		String localaddress = "localhost";
		return localaddress;
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
            logger.info("Content Type Val : " + Integer.toString(response.getContentType().getValue()));

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
			
			/** Resource Content Type */
			if(m.group(2) != null) {
				// Do Nothing: not necessary
			}
		}
	}


	private void manageTextPlain(byte[] payloadBytes) {
		String payloadString = new String(payloadBytes);
		resourceValue = payloadString;
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


    private String formatResourceValue(String resourceValue, Resource resource) {

        StringBuilder resultBld = new StringBuilder();
        String resultRet  = "";
        resultBld.append(resourceValue);
        resultRet = resultBld.toString();

        int resourceValueLength = resultRet.length();
        String integerPart = resultRet.substring(0, resourceValueLength - 2);
        String decimalPart = resultRet.substring(resourceValueLength - 2, resourceValueLength);

        if(resource.getName().equals(Resource.PHENOMENOM_TEMPERATURE) ||
                resource.getName().equals(Resource.PHENOMENOM_HUMIDITY)) {
            logger.debug(" Temperature or Humidity Value detected!");
            resultBld = new StringBuilder();
            resultBld.append(integerPart);
            resultBld.append(".");
            resultBld.append(decimalPart);

        }
        resultRet  = resultBld.toString();
        logger.debug("Resource formatted value: " + resultRet);
        return resultRet;
    }

    // ------------------ Trust Coap messaging
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
        //    code reuses the getNodeObservation() method
        // this is experimental. The value that Coap trust-routing messages return has variable size - it is a list of 32bit entries
        //
        InfoOnTrustRouting retInfo = null;
        if(node != null && resource == null) {
            Observation theNodeTrustInfoObservation = null;
            try{
                theNodeTrustInfoObservation = getNodeObservation(node, resource);
            }catch (WSIAdapterException wsiex) {
                theNodeTrustInfoObservation = null;
                logger.error("Error while running getNodeObservation for Coap trust routing", wsiex);
            }
            if(theNodeTrustInfoObservation != null) {
                retInfo = new InfoOnTrustRouting();
                String tmpStr = theNodeTrustInfoObservation.getValue();
                byte[] wholePayloadBytes =  Base64.decodeBase64(tmpStr);
                logger.debug("Trust Routing retrieval for node: " + node.getId() + " returned bytes of length: " + wholePayloadBytes.length);

                retInfo.setSourceNodeId(node.getId());
                retInfo.setTimestamp(Long.toString(theNodeTrustInfoObservation.getTimestamp()));

                if(wholePayloadBytes!=null && wholePayloadBytes.length >=4  ) {
                    //String tmpNodeIdSubStr;
                    //String tmpPFIvalSubStr;
                    for(int i =0; i<wholePayloadBytes.length && ( i+3 < wholePayloadBytes.length); i+=4) {
                        //tmpNodeIdSubStr = wholePayload.substring(i, i+2);
                        //tmpPFIvalSubStr = wholePayload.substring(i+2, i+4);
                        //logger.debug("TrustRoutingCoap par node: " +tmpNodeIdSubStr + " pfi val: " + tmpPFIvalSubStr);
                        byte[] nodeIdBytes = new byte[] {wholePayloadBytes[i], wholePayloadBytes[i+1]}; //tmpNodeIdSubStr.getBytes();  //javax.xml.bind.DatatypeConverter.parseHexBinary(tmpNodeIdSubStr);
                        byte[] pfiValueHexBytes = new byte[] {wholePayloadBytes[i+2], wholePayloadBytes[i+3]}; // tmpPFIvalSubStr.getBytes(); //javax.xml.bind.DatatypeConverter.parseHexBinary(tmpPFIvalSubStr);
                        short tmpNodeIdValue = Functions.byteArraytoShort(nodeIdBytes);
                        // TODO we could set the prefix later
                        String parentNodeId = (new StringBuilder()).append(InfoOnTrustRouting.getNodePrefix()).append(tmpNodeIdValue).toString();
                        int tmpPFIvalue = Functions.byteArraytoSecShort(pfiValueHexBytes);
                        logger.debug("TrustRoutingCoap par node Sec CONVERTED: " +parentNodeId + " pfi val: " + Integer.valueOf(tmpPFIvalue).toString());
                        retInfo.getParentIdsToPFI().put(parentNodeId, Integer.valueOf(tmpPFIvalue));
                    }
                }
                if(retInfo.getSourceNodeId().compareTo(InfoOnTrustRouting.INVALID_SOURCENODEID) == 0 ||
                        retInfo.getParentIdsToPFI().isEmpty())
                {
                    retInfo = null;
                }
            }
            else {
                logger.error("Null Observation was returned to method getNodeTrustRoutingInfo()");
            }
        } else {
            logger.error("Null node or resource was provided to method getNodeTrustRoutingInfo()");
        }
        return retInfo;
    }

}
