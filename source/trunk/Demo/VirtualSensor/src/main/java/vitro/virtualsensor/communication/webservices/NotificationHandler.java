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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vitro.virtualsensor.communication.webservices;

/**
 *
 * @author David
 */
import javax.jws.WebMethod;
import javax.jws.WebService;
import vitro.dcaintercom.communication.interfaces.NotificationsInterface;
import vitro.virtualsensor.NodeController;
import vitro.virtualsensor.SensorInformation;

@WebService
public class NotificationHandler implements NotificationsInterface {
    
    @WebMethod
    @Override
    public int notify (String subsLogicalName, String eventKind, String XMLfile){
        try {
            if (subsLogicalName == null || subsLogicalName.isEmpty()) {
                //logger.warn("Received subscriptionLogicalName empty or null");
                System.out.println("Received subscriptionLogicalName empty or null");
                return -1;
            }
            if (eventKind == null || eventKind.isEmpty()) {
                //logger.warn("Received eventKind empty or null");
                System.out.println("Received eventKind empty or null");
                return -1;
            }
            if (eventKind.equalsIgnoreCase("Register")) {
                //logger.info("Received registry info for: $subscriptionLogicalName");
                //logger.info("Payload:\n$xmlRegister");
                System.out.println("Received registry info for:" +subsLogicalName);
                System.out.println("Payload:\n" + XMLfile);
            }   
            else if (eventKind.equalsIgnoreCase("observation")) {
                NodeController n = NodeController.getInstance();
                SensorInformation receivedSensor = new SensorInformation();
              
                receivedSensor.setId(n.parseValueXML(XMLfile, n.getQueryXpathSensorName()));
                receivedSensor.setGateway(n.parseValueXML(XMLfile, n.getQueryXpathGWName()));
                receivedSensor.setPhenomenom(n.parseValueXML(XMLfile, n.getQueryXpathPhenomenom()));
                receivedSensor.setOutputUOM(n.parseValueXML(XMLfile, n.getQueryXpathUOM()));
                receivedSensor.setCoordinates(n.parseXML(XMLfile, n.getQueryXpathLocation()));
                receivedSensor.addMeasurement(new Double(n.parseValueXML(XMLfile, n.getQueryXpathObservation())).doubleValue());
                
                if(n.getUom().containsKey(receivedSensor.getOutputUOM())) {
                    n.getUom().get(receivedSensor.getOutputUOM()).forward(receivedSensor);
                }
                else{
                    System.out.println("Received observation for an unknown Virtual Sensor");
                }
            }
            else {
                //logger.warn("Unknown event kind. Received: $eventKind");
                System.out.println("Unknown event kind. Received: " + eventKind);
            }
            return 0;
        }catch (Exception e) {
            System.out.println("Error forwarding the message to the Virtual Sensors");
            return -1;
        }
    }
}
