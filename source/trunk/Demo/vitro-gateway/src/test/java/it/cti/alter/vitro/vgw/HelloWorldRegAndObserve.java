package it.cti.alter.vitro.vgw;

import alter.vitro.vgw.service.SensorMLMessageAdapter;
import alter.vitro.vgw.service.VitroGatewayService;
import org.apache.activemq.ActiveMQConnection;
import org.apache.log4j.Logger;
import vitro.vgw.communication.request.ObservationType;
import vitro.vgw.communication.request.VgwRequestObservation;
import vitro.vgw.communication.response.VgwResponse;
/*import vitro.vgw.model.Resource;   */

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * User: antoniou
 */
public class HelloWorldRegAndObserve {
    private Logger logger = Logger.getLogger(getClass());

    private static HelloWorldRegAndObserve mymainapp;

    private HelloWorldRegAndObserve() {

    }

    public static HelloWorldRegAndObserve getHelloWorldRegAndObserve() {
        if(mymainapp == null)
        {
            mymainapp = new HelloWorldRegAndObserve();
        }
        return mymainapp;
    }

    /**
     * Note: This test uses the alter package for the demo app purposes
     *
     * Testing purposes
     *
     * @param args  arguments for the main function. Unused.
     */
    public static void main(String[] args) throws IOException {
        VitroGatewayService vgs = VitroGatewayService.getVitroGatewayService();
        //
        //  Set the name to the registered one with VITRO DVNS:
        //
        vgs.setAssignedGatewayUniqueIdFromReg("vitrogw_cti");
        System.out.println("Starting up:" + vgs.getAssignedGatewayUniqueIdFromReg());
        vgs.setDVNSUrl("http://195.235.93.106:8002/idas/sml");  // new IDAS url
//      vgs.setDVNSUrl("http://195.235.93.25:9002/idas/sml");    // old IDAS url -- Do not use anymore
        //
        // Set Use Idas to false to connect to the backup testing framework
        // Set it to true to connect to IDAS, using the DVNS Url.
        //
        vgs.setUseIdas(false);
        // CAREFUL:: this is for testing purposes with a localhost ActiveMQ setup. Use the setting bellow for connection with the ActiveMQ of the deployed application on amethyst.cti.gr)
        // If trying to connect to the backup testing framework, change this to the  "failover://tcp://amethyst.cti.gr:61616"
        vgs.setActiveMQBrokerUrl(ActiveMQConnection.DEFAULT_BROKER_URL);
        //vgs.setActiveMQBrokerUrl("failover://tcp://150.140.5.98:61616");
        //
        // Set the adapter name that you are using for the connection to your WSI
        //
        vgs.setWsiAdapterName("uberdust");
        vgs.setWsiDTNSupport(false); //new: Switch to true if DTN is supported in the controlled WSI by this VGW
        vgs.setWsiTrustCoapMessagingSupport(true); // by default it would be false.

        //
        // Set any connection details you might be using in your adapter to your WSI (e.g. DB credentials and schema name, http REST connection details
        //
        vgs.setWsiDbConInfoName("restHttp");

        try{
            SensorMLMessageAdapter smlAdapter = new SensorMLMessageAdapter();
            smlAdapter.init();
            vgs.setSensorMLMessageAdapter(smlAdapter);
            vgs.init();
            // Activation of Coap Messaging must be done after initialization
            vgs.setTrustRoutingCoapQueryPeriod(3*60); //in seconds. TrustRoutingQueryService.NO_PERIOD value means one-shot.
            vgs.setTrustRoutingCoapQueryPerNodeInterPeriod(10); //in seconds. Default value is TrustRoutingQueryService.DEFAULT_PERIOD_FOR_INTERMEDIATE_COAP_MSGs
            vgs.activateTrustRoutingCoapMessaging(true);
            //
            // this will only do something if IDAS mode is enabled:
            if(vgs.getUseIdas()) {
                HelloWorldRegAndObserve.getHelloWorldRegAndObserve().logger.info("Sending new Observations: temperature");
                VgwRequestObservation theCapabilityRequest =new VgwRequestObservation();
                //
                // As a test select to send temperature to IDAS. Latest value from all nodes
               // theCapabilityRequest.setObsType(ObservationType.TEMPERATURE);
                // This calls the WSI Service of collecting measurements for the requested resource and sending them to IDAS
                //VgwResponse response  =  vgs.invokeWSIService(theCapabilityRequest);
                //assertEquals(true, response.isSuccess());

                HelloWorldRegAndObserve.getHelloWorldRegAndObserve().logger.info("Sending new Observations: Barometric");
                theCapabilityRequest.setObsType(ObservationType.BAROMETRICPRESSURE);
                // This calls the WSI Service of collecting measurements for the requested resource and sending them to IDAS
                VgwResponse response  =  vgs.invokeWSIService(theCapabilityRequest);
                assertEquals(true, response.isSuccess());

                //
                // Also send
                HelloWorldRegAndObserve.getHelloWorldRegAndObserve().logger.info("Sending new Observations: Light");
                theCapabilityRequest.setObsType(ObservationType.LIGHT);
                // This calls the WSI Service of collecting measurements for the requested resource and sending them to IDAS
                response  =  vgs.invokeWSIService(theCapabilityRequest);
                assertEquals(true, response.isSuccess());


            }
        }
        catch(Exception vgEx)
        {
            System.out.println(vgEx.getMessage());
        }
        //vgs.shutdown() ;
    }
}
