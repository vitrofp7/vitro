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
package alter.vitro.vgw.communication;

import java.util.Arrays;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.protobuf.compiler.CommandLineSupport;

import alter.vitro.vgw.service.VitroGatewayService;
import alter.vitro.vgw.service.query.SimpleQueryHandler;
import alter.vitro.vgw.wsiadapter.DbConInfoFactory;
import alter.vitro.vgw.wsiadapter.WsiAdapterCon;
import alter.vitro.vgw.wsiadapter.WsiAdapterConFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: antoniou
 */

public class GWCommandMQMessageConsumerProducer implements MessageListener, ExceptionListener {

    private static Logger logger = LoggerFactory.getLogger(GWCommandMQMessageConsumerProducer.class);
    private Connection connection;
    private MessageProducer producer;
    private MessageConsumer consumer;
    private Session session;
    private int count;
    private long start;
    private Topic topicCommands;
    private Topic topicResponses;
    private String forGatewayId;
    private static SimpleQueryHandler querHandler;

    // URL of the JMS server
    // TODO: ideally this should be set in a Config.txt file, parsed at init
    // TODO: test. Can the other partners connect to this port?
    // TODO: switch to the amethyst one to work globally when the code is ready and tested
    private static String activeMQBrokerUrl = "failover://tcp://localhost:61616";
    //private static String activeMQBrokerUrl = ActiveMQConnection.DEFAULT_BROKER_URL;

    private static GWCommandMQMessageConsumerProducer myGWCommandMQMessageConsumerProducer = null;

    private WsiAdapterCon wsiAdapterCon;
    
    /**
     * Creates a new instance of GWCommandMQMessageConsumerProducer
     */
    private GWCommandMQMessageConsumerProducer(WsiAdapterCon wsiAdapterCon) {
    	this.wsiAdapterCon = wsiAdapterCon;
    }

    /**
     * Get the GWCommandMQMessageConsumerProducer.
     * Singleton pattern
     */
    public static GWCommandMQMessageConsumerProducer getMQManager(WsiAdapterCon wsiAdapterCon) {
        if (myGWCommandMQMessageConsumerProducer == null) {
            myGWCommandMQMessageConsumerProducer = new GWCommandMQMessageConsumerProducer(wsiAdapterCon);
        }
        return myGWCommandMQMessageConsumerProducer;
    }

    /**
     * For testing purposes
     * @param argv the arguments for the main method. Unused.
     * @throws Exception
     */
    public static void main(String[] argv) throws Exception {
        GWCommandMQMessageConsumerProducer l = GWCommandMQMessageConsumerProducer.getMQManager(WsiAdapterConFactory.createMiddleWCon("uberdust", DbConInfoFactory.createConInfo("restHttp")));
        String[] unknown = CommandLineSupport.setOptions(l, argv);
        if (unknown.length > 0) {
            System.out.println("Unknown options: " + Arrays.toString(unknown));
            logger.error("Unknown options: " + Arrays.toString(unknown));
            System.exit(-1);
        }
        l.run("vitrogw_tcs");
    }

    public static String getActiveMQBrokerUrl() {
        return activeMQBrokerUrl;
    }

    public static void setActiveMQBrokerUrl(String activeMQBrokerUrl) {
        GWCommandMQMessageConsumerProducer.activeMQBrokerUrl = activeMQBrokerUrl;
    }

    /***
     * Sets up the channels for listening (adds event listener) and producing data
     *
     * @param pForGatewayId the Gateway id registered by the VITRO DVNS
     * @throws JMSException
     */
    public void run(String pForGatewayId) throws JMSException {
        forGatewayId = pForGatewayId;
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(getActiveMQBrokerUrl());
        connection = factory.createConnection();
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // each gateway registers to its OWN topic, to produce data (observations) towards the end application and another one to receive command from it.
        // There is another special topic, common to all gateways, in order to send registerSensor messages to the end app., but that is handled be a seperate class for now (SOSMQMessageProducer).
        topicCommands = session.createTopic(forGatewayId+".commands");
        topicResponses = session.createTopic(forGatewayId+".responses");

        //
        consumer = session.createConsumer(topicCommands);
        connection.setExceptionListener(this);

        consumer.setMessageListener(this);

        connection.start();

        producer = session.createProducer(topicResponses);
        querHandler = SimpleQueryHandler.getInstance();
        querHandler.initInstance( forGatewayId, forGatewayId, this, wsiAdapterCon);

        logger.debug("Waiting for commands from VSN Manager...");
        System.out.println("Waiting for commands from VSN Manager...");
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

    public void sendText(String s) throws JMSException
    {
        try
        {
            TextMessage message = session.createTextMessage(s);
            // Here we are sending the message!
            producer.send(message);
            // DEBUG
            //System.out.println("Sent response '" + message.getText() + "'");
            logger.debug("Sent response '" + message.getText() + "'");

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean checkText(Message m, String s) {
        try {
            return m instanceof TextMessage && ((TextMessage)m).getText().equals(s);
        } catch (JMSException e) {
            logger.error(e.getMessage());
            e.printStackTrace(System.out);
            return false;
        }
    }

    private static boolean isText(Message m) {
        return m instanceof TextMessage;
    }

    public void onMessage(Message message) {
        if (checkText(message, "SHUTDOWN")) {

            try {
                connection.close();
                // TODO: should it also trigger a VGWservice shutdown?
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace(System.out);
            }

        } else if (checkText(message, "REPORT")) {
            // send a report:
            try {
                //System.out.println("Gateway is sending all registration messages on demand from DVNS");
                //System.out.println("----------------------------------------------------------------");
                logger.debug("Gateway is sending all registration messages on demand from DVNS");
                //long time = System.currentTimeMillis() - start;
                //String msg = "Received " + count + " in " + time + "ms";
                //producer.send(session.createTextMessage(msg));
                //sendText("Gateway: "+ forGatewayId +" reporting back");
                // TODO: we need here for the GatewayService to re-init the process of registration sending "updated" information.
                // TODO: also on the application/DVNS side we need to either merge the new info with the old one, or purge the old one and keep the new (more sensible).
                VitroGatewayService.getVitroGatewayService().registerOnDemand(VitroGatewayService.POST_INIT_FLAG);
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace(System.out);
            }
            count = 0;

        }
        else if (checkText(message, "VSP reporting back")) {
            logger.info("VSP server is alive, and has responded to REPORT request");
        }
        else {

            if (isText(message))
            {
                try
                {
                    // Process QUERY Command message case
                    // DEBUG
//                    System.out.println("Received Message from VSP :" + ((TextMessage)message).getText());
                    logger.debug("Received Message from VSP :" + ((TextMessage)message).getText());
                    // TODO: the gateway id and name are set here to be the same, since we don't have access to the DB AND the SOS registration messages do not yet sent the gateway name...
                    if(querHandler.processQuery(((TextMessage)message).getText()) != 0){
                        logger.error("Processing of incoming query failed!");
                    }

                }
                catch (JMSException e) {
                    logger.error(e.getMessage());
                    e.printStackTrace(System.out);
                }
            }
            else
            {
                logger.debug("Received GENERIC: " + message);
                System.out.println("Received GENERIC: " + message);
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
        logger.error("JMS Exception occurred.  Shutting down client.");
        System.out.println("JMS Exception occurred.  Shutting down client.");
        System.exit(1);
    }

    private MessageProducer myPipeDataProducer;
    private Session myPipeDataSession;

    public MessageProducer getProducer()
    {
       return this.producer;
    }

    public Session getSession()
    {
        return this.session;
    }

    public MessageConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(MessageConsumer consumer) {
        this.consumer = consumer;
    }
}
