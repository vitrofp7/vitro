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

/**
 *
 * @author antoniou
 */
import org.apache.log4j.Logger;
import vitro.vspEngine.service.query.SimpleQueryHandler;
import java.util.Arrays;
import javax.jms.*;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.protobuf.compiler.CommandLineSupport;

public class UserNodeCommandMQMessageConsumerProducer  implements MessageListener, ExceptionListener {
    private Logger logger = Logger.getLogger(UserNodeCommandMQMessageConsumerProducer.class);
    private Connection connection;
    private MessageConsumer consumer;
    private MessageProducer producer;
    private MessageProducer producerOfLocalAlertsFromGW;
    private Session session;
    private int count;
    private long start;
    private Topic topicCommands;
    private Topic topicResponses;
    private String forGatewayId;

    // URL of the JMS server
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    // Name of the queue we will receive messages from


//    private String url = "tcp://localhost:61616";

    /**
     * For testing purposes!
     * @param argv
     * @throws Exception
     */
    public static void main(String[] argv) throws Exception {
        UserNodeCommandMQMessageConsumerProducer l = new UserNodeCommandMQMessageConsumerProducer();
        String[] unknown = CommandLineSupport.setOptions(l, argv);
        if (unknown.length > 0) {
            System.out.println("Unknown options: " + Arrays.toString(unknown));
            System.exit(-1);
        }
        l.run("vitrogw_cti");
    }

/*    public void init(UserNode pssUN)
    {
        ssUserNode = pssUN;
    }*/

    // listens and adds event listener!
    public void run(String pForGatewayId) throws JMSException {
        forGatewayId = pForGatewayId;
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        topicCommands = session.createTopic(forGatewayId+".commands");
        topicResponses = session.createTopic(forGatewayId+".responses");
        producerOfLocalAlertsFromGW = session.createProducer(topicResponses);

        setConsumer(session.createConsumer(topicResponses)); // new : made member
        connection.setExceptionListener(this);
        getConsumer().setMessageListener(this);

        connection.start();
        //TODO: Careful, the roles should be reversed for GW and Providers
        producer = session.createProducer(topicCommands);
        logger.info("Waiting for responses from..." + forGatewayId);
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

    public void sendText(String s) throws JMSException
    {
        try
        {
            TextMessage message = session.createTextMessage(s);
            // Here we are sending the message!
            producer.send(message);
            // DEBUG
            //System.out.println("Sent to : " +forGatewayId+ " a command: '" + message.getText() + "'");

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendSelfAlertText(String s) throws JMSException
    {
        try
        {
            TextMessage message = session.createTextMessage(s);
            // Here we are sending the message!
            producerOfLocalAlertsFromGW.send(message);
            // DEBUG
            //System.out.println("Sent to : " +forGatewayId+ " a command: '" + message.getText() + "'");

        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
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
                logger.error("Error while shutting down MQ pipe");
                e.printStackTrace(System.out);
            }

        } else if (checkText(message, "REPORT")) {
            // send a report:
            try {
                long time = System.currentTimeMillis() - start;
                //sendText("VSP reporting back");
                SimpleQueryHandler responsHandler = new SimpleQueryHandler();
                responsHandler.processResponse(((TextMessage) message).getText(), forGatewayId);
            } catch (Exception e) {
                logger.error("Error while sending report back msg");
                e.printStackTrace(System.out);
            }
            count = 0;

        } else {

            if (isText(message))
            {
                try
                {
                    // PROCESS QUERY COMMAND RESPONSE message case
                    // DEBUG
                    //System.out.println("Received Response:" + ((TextMessage)message).getText());
                    logger.debug(((TextMessage)message).getText());
                    SimpleQueryHandler responsHandler = new SimpleQueryHandler();
                    responsHandler.processResponse(((TextMessage)message).getText());                    
                }
                catch (JMSException e) {
                    logger.error("Error while received text msg from VGW");
                    e.printStackTrace(System.out);
                }
            }
            else
            {
                logger.debug("Received GENERIC: " + message);
                //System.out.println("Received GENERIC: " + message);
            }
            if (count == 0) {
                start = System.currentTimeMillis();
            }

            if (++count % 1000 == 0) {
                logger.debug("Received " + count + " messages.");
                System.out.println("Received " + count + " messages.");
            }
        }

    }


    synchronized public void onException(JMSException ex) {
        System.out.println("JMS Exception occurred.  Shutting down client.");
        logger.error("JMS Exception occurred.  Shutting down client.");
        System.exit(1);
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }
}
