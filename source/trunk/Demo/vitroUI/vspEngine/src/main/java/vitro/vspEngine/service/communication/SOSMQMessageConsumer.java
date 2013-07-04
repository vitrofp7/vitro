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
package vitro.vspEngine.service.communication;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */

import vitro.vspEngine.service.communication.dummyXMLParseForSOS.ParseSOSRegisterMsg;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.protobuf.compiler.CommandLineSupport;
import vitro.vspEngine.service.persistence.DBCommons;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.engine.UserNode;

import javax.jms.*;
import java.util.*;

/**
 *  This class is meaningful only for VSP that CONSUMES sensor registration messages from VGWs
 *  TODO: it could be merged with the SOSMQMessageProducer (like we did for the UserNodeCommandMQMessage...)
 */
public class SOSMQMessageConsumer implements MessageListener, ExceptionListener {

    private Connection connection;
    private MessageProducer producer;
    private Session session;
    private int count;
    private long start;
    private Topic topic;
    private Topic control;
    private UserNode ssUserNode;

    // URL of the JMS server
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    // Name of the queue we will receive messages from
    private static String subject = "SOSMESSAGES";

    /**
     * Testing purposes
     * @param argv
     * @throws Exception
     */
    public static void main(String[] argv) throws Exception {
        SOSMQMessageConsumer l = new SOSMQMessageConsumer();
        String[] unknown = CommandLineSupport.setOptions(l, argv);
        if (unknown.length > 0) {
            System.out.println("Unknown options: " + Arrays.toString(unknown));
            System.exit(-1);
        }
        l.run();
    }

    public void init(UserNode pssUN)
    {
        ssUserNode = pssUN;
    }

    // listens and adds event listener!
    public void run() throws JMSException {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topic = session.createTopic(subject+".messages");
        control = session.createTopic(subject+".control");

        MessageConsumer consumer = session.createConsumer(topic);
        connection.setExceptionListener(this);
        consumer.setMessageListener(this);

        connection.start();

        producer = session.createProducer(control);
        System.out.println("Waiting for SOS registration messages...");
    }

    public void stop()  {
        try
        {
            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static boolean checkText(Message m, String s) {
        try {
            return m instanceof TextMessage && ((TextMessage)m).getText().equals(s);
        } catch (JMSException e) {
            e.printStackTrace(System.out);
            return false;
        }
    }

    private static boolean isText(Message m) {
            return m instanceof TextMessage ;
    }

    public void onMessage(Message message) {
        if (checkText(message, "SHUTDOWN")) {

            try {
                connection.close();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }

        } else if (checkText(message, "REPORT")) {
            // send a report:
            try {
                long time = System.currentTimeMillis() - start;
                String msg = "Received " + count + " in " + time + "ms";
                producer.send(session.createTextMessage(msg));
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
            count = 0;

        } else {

            if (isText(message))
            {
                try
                {
                    // SOS message case
                    // DEBUG
                    // System.out.println("Received TEXT: " + ((TextMessage)message).getText());
                    HashMap<String, GatewayWithSmartNodes> infoGWHM = ssUserNode.getGatewaysToSmartDevsHM();
                    HashMap<String,Vector< SensorModel >> ssUNCapHM = ssUserNode.getCapabilitiesTable();
                    //ParseSOSRegisterMsg.parse(((TextMessage) message).getText(), infoGWHM, ssUNCapHM);
                    HashMap<String,  Vector<SensorModel> > advCapsToSensModels = new HashMap<String,  Vector<SensorModel> > ();
                    Vector<SmartNode> advSmDevs = new Vector<SmartNode> ();
                    StringBuilder gwDescFromDB = new StringBuilder ();
                    StringBuilder gwIdfromADV = new StringBuilder ();
                    if(ParseSOSRegisterMsg.parseXmlStrMsg(((TextMessage) message).getText(), advCapsToSensModels, advSmDevs, gwIdfromADV) && DBCommons.getDBCommons().isRegisteredGateway(gwIdfromADV.toString(), gwDescFromDB))
                    {
                        DBCommons.getDBCommons().mergeAdvDataToGateway(infoGWHM, ssUNCapHM, advCapsToSensModels, advSmDevs, gwIdfromADV.toString(), gwDescFromDB.toString(), null, null);
                        DBCommons.getDBCommons().updateRcvGatewayAdTimestamp(gwIdfromADV.toString(), false);
                    }
                    else
                    {
                        System.out.println("Error in XML message (unexpected format or unregistered gateway)!");
                        System.out.println("Wrong message was:" +((TextMessage) message).getText());
                    }
                }
                catch (JMSException e) {
                    e.printStackTrace(System.out);
                }
            }
            else
            {
                System.out.println("Received GENERIC: " + message);
            }
            if (count == 0) {
                start = System.currentTimeMillis();
            }

            if (++count % 1000 == 0) {
                System.out.println("Received " + count + " messages.");
            }
        }

    }

    synchronized public void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
        System.exit(1);
     }




}
