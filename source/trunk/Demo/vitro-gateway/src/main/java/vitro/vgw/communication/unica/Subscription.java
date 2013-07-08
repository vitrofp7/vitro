/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package vitro.vgw.communication.unica;

/**
 * The static class Subscription contains the messages that might be sent to DCA in order to interact with it.
 * The functionalities supported by this class are related the subscription of a Client. 
 * @author David Ferrer
 *
 */

public class Subscription {

	private static final String subscribeXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
					"<SOAP-ENV:Envelope " +
					"xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
					"\n\txmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" " +
					"\n\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
					"\n\txmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
					"\n\txmlns:uct=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\" " +
					"\n\txmlns:enum-1=\"http://tempuri.org/enum-1.xsd\" " +
					"\n\txmlns:enum-2=\"http://tempuri.org/enum-2.xsd\" " +
					"\n\txmlns:uch=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\" " +
					"\n\txmlns:ucf=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults\" " +
					"\n\txmlns:ns1=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\" " +
					"\n\txmlns:m2msubss1=\"http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/subscription/v1/services\" " +
					"\n\txmlns:m2msubss1t=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscription/v1/types\">\n" +
					"<SOAP-ENV:Header>\n" +
					"\t<ns1:transactionInfoHeader>\n" +
					"\t\t<ns1:servId>0</ns1:servId>\n" +
					"\t\t<ns1:appId>#appId#</ns1:appId>\n" +
					"\t\t<ns1:assetId>0</ns1:assetId>\n" +
					"\t\t<ns1:transactionId/>\n" +
					"\t\t<ns1:appProviderId/>\n" +
					"\t</ns1:transactionInfoHeader>\n" +
					"\t<uch:simpleOAuthHeader/>\n" +
					"</SOAP-ENV:Header>\n" +
					"<SOAP-ENV:Body>\n" +
					"\t<m2msubss1t:subscribe>\n" +
					"\t\t<m2msubss1t:responseURI>#response#</m2msubss1t:responseURI>\n" +
					"\t\t<m2msubss1t:subscription>\n" +
					"\t\t\t<m2msubss1t:eventKind>#eventKind#</m2msubss1t:eventKind>\n" +
					"\t\t\t<m2msubss1t:notifyURI>#notify#</m2msubss1t:notifyURI>\n" +
					"\t\t\t<m2msubss1t:serviceLogicalName>#srvLogicalName#</m2msubss1t:serviceLogicalName>\n" +
					"\t\t\t<m2msubss1t:clientAppLogicalName>#clientAppName#</m2msubss1t:clientAppLogicalName>\n" +
					"\t\t\t<m2msubss1t:subscriptionLogicalName>#subscriptionName#</m2msubss1t:subscriptionLogicalName>\n" +
					"\t\t\t<m2msubss1t:xpath>#xpath#</m2msubss1t:xpath>\n" +
					"\t\t\t<m2msubss1t:timed>false</m2msubss1t:timed>\n" +
					"\t\t\t<m2msubss1t:seconds>0</m2msubss1t:seconds>\n" +
					"\t\t\t<m2msubss1t:original>true</m2msubss1t:original>\n" +
					"\t\t\t<m2msubss1t:priority>1</m2msubss1t:priority>\n" +
					"\t\t</m2msubss1t:subscription>\n" +
					"\t</m2msubss1t:subscribe>\n" +
					"</SOAP-ENV:Body>\n" +
					"</SOAP-ENV:Envelope>";

	private static final String unsubscribeXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
					"<soapenv:Envelope" + 
					"\nxmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
					"\nxmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\""+
					"\nxmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\""+ 
					"\nxmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\""+ 
					"\nxmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscription/v1/types\">"+
					"\n<soapenv:Header>"+
					"\n<tran:transactionInfoHeader>"+
					"\n<tran:servId>0</tran:servId>"+
					"\n<tran:appId>#appId#</tran:appId>"+
					"\n<tran:assetId>0</tran:assetId>"+
					"\n<tran:transactionId>0</tran:transactionId>"+
					"\n<tran:appProviderId>0</tran:appProviderId>"+
					"\n</tran:transactionInfoHeader>"+
					"\n<sec:simpleOAuthHeader>"+
					"\n</sec:simpleOAuthHeader>"+
					"\n</soapenv:Header>"+
					"\n<soapenv:Body>"+
					"\n<typ:unsubscribe>"+
					"\n<typ:responseURI>#response#</typ:responseURI>"+
					"\n<typ:subscriptionLogicalName>#logicalName#</typ:subscriptionLogicalName>"+
					"\n<typ:outgoingConnectionId>#Outgoing#</typ:outgoingConnectionId>"+
					"\n</typ:unsubscribe>"+
					"\n</soapenv:Body>"+
					"\n</soapenv:Envelope>";

	private static final String disconnectXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
					"<soapenv:Envelope " +
					"\nxmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""+ 
					"\nxmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\""+ 
					"\nxmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\""+ 
					"\nxmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\"" +
					"\nxmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscription/v1/types\">"  +
					"\n<soapenv:Header>" +
					"\n<tran:transactionInfoHeader>" +
					"\n<tran:servId>0</tran:servId>" +
					"\n<tran:appId>#appId#</tran:appId>" +
					"\n<tran:assetId>0</tran:assetId>" +
					"\n<tran:transactionId>0</tran:transactionId>" +
					"\n<tran:appProviderId>0</tran:appProviderId>" +
					"\n</tran:transactionInfoHeader>" +
					"\n<sec:simpleOAuthHeader>" +
					"\n</sec:simpleOAuthHeader>" +
					"\n</soapenv:Header>" +
					"\n<soapenv:Body>" +
					"\n<typ:disconnect>" +
					"\n<typ:responseURI>#response#</typ:responseURI>" +
					"\n<typ:outgoingConnectionId>#outgoing#</typ:outgoingConnectionId>" +
					"\n</typ:disconnect>" +
					"\n</soapenv:Body>" +
					"\n</soapenv:Envelope>";

	/**
	 * This method makes a subscription of a Client to DCA
	 * @param appId is the Service Identifier 
	 * @param response is the URI to which the server must respond with the result of the subscription process.
	 * @param eventKind (Register or Observation) is the kind of event to which the subscription relates 
	 * @param notify  is the URI where the server will notify the messages
	 * @param srvLogicalName is the Logical name of the M2M service to which the client wants to subscribe
	 * @param clientAppName is the logical name of the client app
	 * @param subscriptionName is a name generated by the client that must be unique. 
	 * @param xpath is the criteria for the subscription
	 * @return the XML file ready to send
	 */
	public static String subscribe(int appId, String response, String eventKind, String notify,	String srvLogicalName, String clientAppName, String subscriptionName, String xpath) {
		String message = subscribeXML.replace("#appId#", String.valueOf(appId)).replace("#response#",response)
				.replace("#eventKind#", eventKind).replace("#notify#", notify).replace("#srvLogicalName#", srvLogicalName)
				.replace("#clientAppName#", clientAppName).replace("#subscriptionName#", subscriptionName).replace("#xpath#", xpath);
		return message;
	}

	/**
	 * This method unsubscribes a client from a subscription
	 * @param appId is the Service ID
	 * @param responseis the URI to which the server must respond with the result of the unsubscription process.
	 * @param logicalName Indicates the reference to the subscritpion that must be deleted
	 * @param Outgoing A connection ID issued by the server that identifies (in the server side) the client-side notification URI associated to this subscription
	 * @return the XML file ready to send
	 */
	public static String unsubscribe(int appId, String response, String logicalName, String Outgoing) {
		String message = unsubscribeXML.replace("#appId#",String.valueOf(appId)).replace("#response#", response)
				.replace("#logicalName#", logicalName).replace("#Outgoing#",Outgoing);
		return message;
	}

	/**
	 * This method disconnects a client from a subscription
	 * @param appId is the Service ID
	 * @param responseis the URI to which the server must respond with the result of the unsubscription process.
	 * @param Outgoing A connection ID issued by the server that identifies (in the server side) the client-side notification URI associated to this subscription
	 * @return the XML file ready to send
	 */
	public static String disconnect(int appId, String response, String Outgoing){
		String message = disconnectXML.replace("#appId#",String.valueOf(appId)).replace("#response#", response).replace("#outgoing#",Outgoing);
		return message;
	}
}
