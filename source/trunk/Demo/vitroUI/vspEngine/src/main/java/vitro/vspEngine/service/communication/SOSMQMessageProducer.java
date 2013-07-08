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
package vitro.vspEngine.service.communication;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.protobuf.compiler.CommandLineSupport;

import javax.jms.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */

/**
 *  This class is meaningful only for a gateway that PRODUCES sensor registration messages towards the VSP
 *  TODO: it could be merged with the SOSMQMessageConsumer (like we did for the UserNodeCommandMQMessage...)
 */
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

    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    // Name of the queue we will be sending messages to
    private static String subject = "SOSMESSAGES";

    public void run() throws JMSException {

        // Getting JMS connection from the server and starting it
        ConnectionFactory connectionFactory =
                new ActiveMQConnectionFactory(url);
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
            System.out.println("Sent message '" + message.getText() + "'");

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
