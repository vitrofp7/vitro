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

package vitro.virtualsensor.communication.soap;

import java.util.HashMap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import vitro.virtualsensor.NodeController;

@WebService
public class SubscriptionHandler implements SubscriptionsInterface {

	private static HashMap<String, String> subscriptions; //<subscriptionLogicalName, outgoingConnectionId>
	
	@WebMethod
	public void subscribeResponse(String subscriptionLogicalName, int outgoingConnectionId, int errorCode, String errorText) {
		if (errorCode == 0) {
			//logger.info ("Subscription $subscriptionLogicalName OK.");
			System.out.println("Subscription "+ subscriptionLogicalName + " OK");
			//logger.info("$subscriptionLogicalName: Error:No, ConnID:$outgoingConnectionId $errorText");
			NodeController.getInstance();
			if (!NodeController.nodeController.findClient(subscriptionLogicalName)) {
				//logger.error("Model for $subscriptionLogicalName missing from sensorDepot");
				System.out.println("Model for subscription "+ subscriptionLogicalName + " is missing");
			}
			else {
				NodeController.getInstance().getClient(subscriptionLogicalName).setOutgoingConnectionId(outgoingConnectionId);
                /// note: (antoniou) changed from start() to startSensor() in order to compile. The start() method did not exist
				NodeController.getInstance().getSensor(subscriptionLogicalName).startSensor();
				//logger.info("Instancing model for $subscriptionLogicalName at sensorDepot");
			}
		}
		else {
			System.out.println("Subscription "+ subscriptionLogicalName + " ERROR: " + errorCode);
			//logger.warn("Subscription $subscriptionLogicalName error, code: $errorCode.");
			//logger.error("$subscriptionLogicalName: Error:Yes, $errorText ConnID:$outgoingConnectionId");
			System.out.println(subscriptionLogicalName + " ERROR: " + outgoingConnectionId);
		}
		System.out.println("Error: "+ errorText);
		//logger.info("Error Text: $errorText");
	}
}
