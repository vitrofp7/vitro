/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis
 * #     Paolo Medagliani
 * #     D. Davide Lamanna
 * #     Panos Trakadas
 * #     Andrea Kropp
 * #     Kiriakos Georgouleas
 * #     Panagiotis Karkazis
 * #     David Ferrer Figueroa
 * #     Francesco Ficarola
 * #     Stefano Puglia
 * #--------------------------------------------------------------------------
 */

package vitro.virtualsensor.communication.unica;

import vitro.virtualsensor.communication.HttpForwarder;
import vitro.dcaintercom.communication.common.Config;
import vitro.dcaintercom.communication.unica.Subscription;

/**
 * This class provides a Client that redirects all the information that collects
 * to the message boxes
 * 
 * @author David Ferrer Figueroa
 * 
 */
public class UNICAConnection {

	// Connection details
	private HttpForwarder http;
	private String subscriptionEndpoint = Config.getConfig().getSubscriptionsUrl();
	
	private int appId;
	private String response;
	private String eventKind;
	private String notify;
	private String srvLogicalName;
	private String clientAppName;
	private String subscriptionName;
	private String xpath;

	public UNICAConnection() {
		setConnectionParameters(1001,"","","","","","","");
		http = new HttpForwarder();
	}

	public UNICAConnection(int appId, String response, String eventKind,
			String notify, String srvLogicalName, String clientAppName,
			String subscriptionName, String xpath) {
		setConnectionParameters(appId, response, eventKind, notify,
				srvLogicalName, clientAppName, subscriptionName, xpath);
		http = new HttpForwarder();
	}

	public String getClientId() {
		return clientAppName;
	}

	public void setConnectionParameters(int appId, String response,
			String eventKind, String notify, String srvLogicalName,
			String clientAppName, String subscriptionName, String xpath) {
		this.appId = appId;
		this.response = response;
		this.eventKind = eventKind;
		this.notify = notify;
		this.srvLogicalName = srvLogicalName;
		this.clientAppName = clientAppName;
		this.subscriptionName = subscriptionName;
		this.xpath = xpath;
	}

	// TODO recibir el mensaje y extraer los campos relevantes. Buscar los
	// metodos creados por Thanasis relevantes
	public String receiveNotification() {
		return "";
	}

	/**
	 * This method creates a subscription for a client
	 * 
	 * @param xpath
	 *            contains information regarding the subscription.
	 * @return true if the subscription was successfully performed. False
	 *         otherwise.
	 */
	public boolean subscribeClient(String xpath) {
		try {
			String XML = Subscription.subscribe(this.appId, this.response,
			this.eventKind, this.notify, this.srvLogicalName,
			this.clientAppName, this.subscriptionName, xpath);
			http.post(subscriptionEndpoint, XML);
			System.out.println("Message sent");

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	public boolean subscribe() {
		try {
			String XML = Subscription.subscribe(this.appId, this.response,
					this.eventKind, this.notify, this.srvLogicalName,
					this.clientAppName, this.subscriptionName, this.xpath);
			System.out.println(XML);
			//IDASHttpRequestgetIDASHttpRequest.().sendPOSTToIdasXML(retCon);
			http.post(subscriptionEndpoint, XML);
                        if (http.getResponseCode() == 200) {
                            System.out.println("Received code: 200");
                            System.out.println("Response: " + http.getResponse());
                        } 
                        else {
                            System.out.println("Received code " + http.getResponseCode());
                            System.out.println("Response: " + http.getErrorResponse());
                        }
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param logicalName
	 * @param outgoing
	 * @return TODO Cerrar la conexi�n del observable con DCA y notificar a los
	 *         observer de que la conexion termina. Podr�a hacerse enviando un
	 *         par�metro espec�fico: NULL - NULL? "endConnection", etc.
	 * 
	 */
	public boolean unsubscribe(String logicalName, String outgoing) {
		try {
			String XML = Subscription.unsubscribe(this.appId, this.response,
			logicalName, outgoing);
			http.post(subscriptionEndpoint, XML);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * TODO Cerrar la conexion del observable con DCA y notificar a los observer
	 * de que la conexion termina. Podria hacerse enviando un par�metro
	 * especifico: NULL - NULL? "endConnection", etc.
	 * 
	 * @param outgoing
	 * @return
	 */
	public boolean disconnect(String outgoing) {
		try {
			String XML = Subscription.disconnect(this.appId, this.response,
			outgoing);
			http.post(subscriptionEndpoint, XML);

			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String getXpath() {
		return xpath;
	}

	public void setXpath(String xpath) {
		this.xpath = xpath;
	}

	public void show() {
		System.out.println("Client AppID " +this.appId);
		System.out.println("Client AppName" + this.clientAppName);
		System.out.println("Subscription Endpoint " + this.subscriptionEndpoint);
		System.out.println("Event kind " + this.eventKind);
		System.out.println("Notify " + this.notify);
		System.out.println("Response" + this.response);
		System.out.println("Service Logical Name " + this.srvLogicalName);
		System.out.println("SubscriptionName " + this.subscriptionName);
		System.out.println("Xpath " + this.xpath);
	}
}
