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

package vitro.virtualsensor.communication.webservices;

//import java.util.HashMap;
import javax.jws.WebMethod;
import javax.jws.WebService;
import vitro.dcaintercom.communication.interfaces.SubscriptionsInterface;
import vitro.virtualsensor.NodeController;

@WebService
public class SubscriptionHandler implements SubscriptionsInterface {

	
    /**
     *
     * @param subscriptionLogicalName
     * @param outgoingConnectionId
     * @param errorCode
     * @param errorText
     */
    @WebMethod
    @Override
    public void subscribeResponse(String subscriptionLogicalName, int outgoingConnectionId, int errorCode, String errorText) {
        if (errorCode == 0) {
            //logger.info ("Subscription $subscriptionLogicalName OK.");
            System.out.println("Subscription "+ subscriptionLogicalName + " OK");
            //logger.info("$subscriptionLogicalName: Error:No, ConnID:$outgoingConnectionId $errorText");
            NodeController n = NodeController.getInstance();
            if (!n.findClient(subscriptionLogicalName)) {
                //logger.error("Model for $subscriptionLogicalName missing from sensorDepot");
                System.out.println("Model for subscription " + subscriptionLogicalName + " is missing");
            }
            else {
                n.getClient(subscriptionLogicalName).setOutgoingConnectionId(outgoingConnectionId);
                n.getSensor(subscriptionLogicalName).startSensor();
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
