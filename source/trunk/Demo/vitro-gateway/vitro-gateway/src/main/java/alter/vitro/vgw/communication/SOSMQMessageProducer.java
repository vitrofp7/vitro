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
package alter.vitro.vgw.communication;

import java.util.Arrays;

/**
 * Author: antoniou
 */
import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.protobuf.compiler.CommandLineSupport;

public class SOSMQMessageProducer {
    // URL of the JMS server. DEFAULT_BROKER_URL will just mean
    // that JMS server is on localhost
    private Connection connection;
    private MessageProducer producer;
    private Session session;
    private int count;
    private long start;
    private Topic destinationTopic;
    private Topic destinationControl;

    // TODO:switch to the amethyst one to work globally when the code is ready and tested
    private static String activeMQBrokerUrl = "failover://tcp://localhost:61616";
    //private static String activeMQBrokerUrl = ActiveMQConnection.DEFAULT_BROKER_URL;

    // Name of the queue we will be sending messages to
    private static String subject = "SOSMESSAGES";


    private static SOSMQMessageProducer mySOSMQMessageProducer = null;

    /**
     * Creates a new instance of SOSMQMessageProducer
     */
    private SOSMQMessageProducer() {
    }

    /**
     * Get the SOSMQMessageProducer.
     * Singleton pattern
     */
    public static SOSMQMessageProducer getRegisterSOSMQManager() {
        if (mySOSMQMessageProducer == null) {
            mySOSMQMessageProducer = new SOSMQMessageProducer();
        }
        return mySOSMQMessageProducer;
    }

    public static String getActiveMQBrokerUrl() {
        return activeMQBrokerUrl;
    }

    public static void setActiveMQBrokerUrl(String activeMQBrokerUrl) {
        SOSMQMessageProducer.activeMQBrokerUrl = activeMQBrokerUrl;
    }


    public void run() throws JMSException {

        // Getting JMS connection from the server and starting it
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(getActiveMQBrokerUrl());
        connection = connectionFactory.createConnection();
        connection.start();

        // JMS messages are sent and received using a Session. We will
        // create here a non-transactional session object. If you want
        // to use transactions you should set the first parameter to 'true'
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Destination represents here our queue 'SOSMESSAGES' on the
        // JMS server. You don't have to do anything special on the
        // server to create it, it will be created automatically.

        //Destination destination = session.createQueue(subject);
        destinationTopic = session.createTopic(subject+".messages");
        destinationControl = session.createTopic(subject+".control");

        // MessageProducer is used for sending messages (as opposed
        // to MessageConsumer which is used for receiving them)
        producer = session.createProducer(destinationTopic);
    }

    public void sendText(String s)   throws JMSException
    {
        try
        {
            TextMessage message = session.createTextMessage(s);
            // Here we are sending the message!
            producer.send(message);
            // DEBUG
            //System.out.println("Sent message '" + message.getText() + "'");

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop()  {
        try
        {
            connection.stop();
            connection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] argv) throws JMSException {
        SOSMQMessageProducer l = new SOSMQMessageProducer();
        String[] unknown = CommandLineSupport.setOptions(l, argv);
        if (unknown.length > 0) {
            System.out.println("Unknown options: " + Arrays.toString(unknown));
            System.exit(-1);
        }
        l.run();
    }
}
