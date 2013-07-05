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

package vitro.virtualsensor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import vitro.dcaintercom.communication.common.Config;
import vitro.dcaintercom.communication.common.XPathString;
import vitro.virtualsensor.communication.SensorConnector;
import vitro.virtualsensor.communication.unica.DCAClient;
import vitro.virtualsensor.communication.unica.UNICAConnection;
import vitro.virtualsensor.communication.webservices.NotificationHandler;
import vitro.virtualsensor.communication.webservices.SubscriptionHandler;

public class NodeController {

    private HashMap<String, MsgBox> uom; //<uom, msgbox>
    private HashMap<String, VirtualSensor> vs; //<vsName, VS>
    private HashMap<String, DCAClient> clientsSubscriptions; //<subscriptionLogicalName, DCAClient> 
    //TODO Modify it. It shouldn't be addressed by the subscription name
    private HashMap<String, VirtualSensor> subscriptions; //<subscriptionLogicalName, VirtualSensor>

    public static NodeController nodeController = null;

    private String queryXpathSensorName = "/InsertObservation/AssignedSensorId/text()";
    private String queryXpathGWName = "/InsertObservation/Observation/parameter/Text/value/text()";
    private String queryXpathPhenomenom = "/InsertObservation/Observation/result/Quantity/@definition";
    private String queryXpathUOM = "/InsertObservation/Observation/result/Quantity/uom/@code";
    private String queryXpathObservation = "/InsertObservation/Observation/result/Quantity/value/text()";
    private String queryXpathLocation = "/InsertObservation/Observation/parameter/Position/location/Vector/coordinate/Quantity/value/text()";

    private int sensorId;
    private int counter = 0;
    private String gatewayId;
    private String clientName = "VITRO";
    private String subsName = "subscription";
    private String queryUOM;
    private String customText = "custom";

    private org.apache.cxf.endpoint.Server subscriptionResponseServer;
    private org.apache.cxf.endpoint.Server notificationResponseServer;
    private SubscriptionHandler subscriptionHandler;
    private NotificationHandler notificationHandler;
    private String hostIP;
    private String notificationsResponseURL;
    private String subscriptionsResponseURL;
    org.apache.log4j.Logger log;

    private NodeController() {
        log = org.apache.log4j.Logger.getLogger(getClass());

        this.uom = new HashMap<String, MsgBox>();
        this.vs = new HashMap<String, VirtualSensor>();
        this.clientsSubscriptions = new HashMap<String, DCAClient>();
        this.subscriptions = new HashMap<String, VirtualSensor>();
        this.sensorId = 0;
        this.gatewayId = "VirtualGateway";
        this.hostIP = "http://"+getProbableExternalIpAddress();  // the getProbableExternalIpAddress() does not return the http:// prefix
        this.notificationsResponseURL = this.hostIP.concat(":80/notificationsResponse");
        this.subscriptionsResponseURL = this.hostIP.concat(":80/subscriptionsResponse");
        initWebServices();

        /* this.hostIP = "http://10.95.197.181"; 
      this.notificationsResponseURL = this.hostIP.concat(":80/notificationsResponse");
      this.subscriptionsResponseURL = this.hostIP.concat(":80/subscriptionsResponse");*/
    }

    public static NodeController getInstance() {
        if (nodeController == null) {
            nodeController = new NodeController();
        }
        return nodeController;
    }

    public String getProbableExternalIpAddress(){
        //first try to retrieve the IP from a site (TODO: we can host this on amethyst as well)
        String ipRet = null;
        try{
            URL whatismyip = new URL("http://amethyst.cti.gr/vitroui/vwhatsmyip.jsp");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            while( (ipRet = in.readLine() ) != null)
            {
                if(! ipRet.trim().isEmpty())
                {
                    break;
                }
                //retrieve your external IP as a String . Useful especially on NAT configurations
            }
            log.info("from external web IP=" + ipRet);
            if(ipRet != null && (ipRet.trim().compareToIgnoreCase ("")== 0 || ipRet.toLowerCase().startsWith("192.") ||
                    ipRet.toLowerCase().startsWith("127.")) )
            {
                ipRet = null;
            }
        }
        catch (Exception exMalUrl)
        {
            log.error( exMalUrl.toString());
        }

        if(ipRet == null)
        {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); ipRet==null && enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            if(!inetAddress.getHostAddress().toLowerCase().contains(":")
                                    && ! ( inetAddress.getHostAddress().toLowerCase().startsWith("192.") ||
                                    inetAddress.getHostAddress().toLowerCase().startsWith("127.")  ) )
                            {
                                ipRet = inetAddress.getHostAddress();
                                log.info("from local config IP=" + ipRet);
                            }
                        }
                    }
                }
            } catch (SocketException ex) {
                log.error( ex.toString());
            }
        }
        return ipRet.trim();
    }
    /**
     * Adds a new MsgBox listener for a specific UOM
     *
     * @param newUom
     * @return
     */
    public MsgBox addMsgBox(String newUom) {
        try {
            if (!(findMsgBox(newUom))) {
                MsgBox obs = new MsgBox(newUom);
                this.uom.put(newUom, obs);
                return obs;
            } else {
                return this.uom.get(newUom);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public boolean findClient(String subscriptionLogicalName) {
        return clientsSubscriptions.containsKey(subscriptionLogicalName);
    }

    /**
     * This method creates a new VirtualSensor. It also refreshes the list of
     * MsgBox and the devices linked to each one If its name is set to "", then,
     * an automatic identifier is assigned to it. The same happens with the
     * gateway
     *
     * @param sensorName
     * @param inputUom
     * @param outputUom
     * @return
     */
    public VirtualSensor addVirtualSensor(String sensorName, String gwName, ArrayList<String> inputSensors, ArrayList<String> inputUom, String outputUom, String operation) {
        try {
            VirtualSensor virtualSensor;
            if (sensorName.equals("")) {
                sensorName = "VirtualSensor" + String.valueOf(sensorId++);
            }
            if (gwName.equals("")) {
                gwName = this.gatewayId;
            }
            if (!this.vs.containsKey(sensorName)) {//Not found in the map
                virtualSensor = new VirtualSensor(sensorName, gwName, inputSensors);
            } else { //Found in the map
                virtualSensor = replaceVirtualSensor(sensorName, gwName, inputUom, outputUom);
            }
            virtualSensor.getSensorInformation().setOutputUOM(outputUom);
            this.vs.put(sensorName, virtualSensor);
            virtualSensor.changeOperation(operation);

            for (int i = 0; i < inputUom.size(); i++) {
                this.addMsgBox(inputUom.get(i)).addObserver(virtualSensor);
            }
            SensorConnector sc = new SensorConnector(Config.getConfig().getNotificationsUrl());
            sc.sendRegistrationMessage(virtualSensor);
            return virtualSensor;
        } catch (Exception e) {
            return null;
        }
    }

    public VirtualSensor addVirtualSensor(String sensorName, String gwName, String outputPhenom, String outputUom, String operation, ArrayList<SensorInformation> sI, String subscriptionName) {
        try {
            VirtualSensor virtualSensor;
            if (sensorName.equals("")) {
                sensorName = "VirtualSensor" + String.valueOf(sensorId++);
            }
            if (gwName.equals("")) {
                gwName = this.gatewayId;
            }
            if (!this.vs.containsKey(sensorName)) { //Not found in the map
                virtualSensor = new VirtualSensor(sI, sensorName, gwName);
            }
            else {
                return null;
            }
            virtualSensor.getSensorInformation().setOutputUOM(outputUom);
            virtualSensor.getSensorInformation().setPhenomenom("urn:x-ogc:def:phenomenon:IDAS:1.0:" + outputPhenom);
            virtualSensor.changeOperation(operation);

            for (int i = 0; i < sI.size(); i++) {
                String outUom = sI.get(i).getOutputUOM();
                virtualSensor.getInputUOM().add(outUom);
            }

            this.vs.put(sensorName, virtualSensor);
            this.subscriptions.put(subscriptionName, virtualSensor);

            for (int i = 0; i < sI.size(); i++) { //Establish the mesage boxes for the input UOM
                String inputUom = sI.get(i).getOutputUOM();
                this.addMsgBox(inputUom).addObserver(virtualSensor);
            }

            SensorConnector sc = new SensorConnector(Config.getConfig().getNotificationsUrl());
            sc.sendRegistrationMessage(virtualSensor);
            return virtualSensor;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param appId
     * @param response
     * @param eventKind
     * @param notify
     * @param srvLogicalName
     * @param clientAppName if left empty, it is automatically set to an auto
     * generated value
     * @param subscriptionName
     * @return
     */
    public DCAClient addClient(String name, int appId, String response, String eventKind, String notify, String srvLogicalName, String clientAppName, String subscriptionName, String xpath) {
        DCAClient client = new DCAClient(name);
        try {
            if (clientAppName.equals("")) {
                clientAppName = clientName;
            }
            if (subscriptionName.equals("")) {
                subscriptionName = subsName.concat("" + counter);
            }
            if (!(clientsSubscriptions.containsKey(subscriptionName))) {
                client.setConnection(new UNICAConnection(appId, response, eventKind, notify, srvLogicalName, clientAppName, subscriptionName, xpath));
                this.clientsSubscriptions.put(subscriptionName, client);
            }
        } catch (Exception e) {
        }
        return client;
    }

    public boolean subscribeClient(String name, int appId, String response, String eventKind, String notify, String srvLogicalName, String clientAppName, String subscriptionName, String xpath) {
        DCAClient client = addClient(name, appId, response, eventKind, notify, srvLogicalName, clientAppName, subscriptionName, xpath);
        boolean subsClient = client.subscribe();
        return subsClient;
    }

    /**
     * This method replaces an existing VirtualSensor I assume that the
     * connection details are set apart from this method
     *
     * @param sensorName
     * @param gwName
     * @param inputUom
     * @param outputUom
     * @return
     */
    public VirtualSensor replaceVirtualSensor(String sensorName, String gwName, ArrayList<String> inputUom, String outputUom) {
        VirtualSensor virtualSensor = new VirtualSensor(sensorName, gwName);
        virtualSensor.setInputUOM(inputUom);
        virtualSensor.getSensorInformation().setOutputUOM(outputUom);
        return virtualSensor;
    }

    public void setGatewayId(String id) {
        this.gatewayId = id;
    }

    /**
     * This method gets the UOM of the received message in order to forward it
     * to the message boxes
     *
     * @param uom is the received XML from DCA
     * @return an ArrayList containing the UOM of the message.
     */
    public boolean findMsgBox(String uom) {
        return this.uom.containsKey(uom);
    }

    //TODO FALTA CERRAR LAS CONEXIONES CORRESPONDIENTES A ESA UNIDAD DE MEDIDA
    public boolean deleteMsgBox(String uom) {
        MsgBox delete = this.uom.remove(uom);
        return delete.removeMsgBox();
    }

    //TODO FALTA CERRAR LAS CONEXIONES CORRESPONDIENTES A ESA UNIDAD DE MEDIDA
    public boolean deleteVS(String sensorId) {
        VirtualSensor vs = this.vs.remove(sensorId);
        return vs.removeVS();
    }

    public boolean deleteClient(String subscriptionLogicalName, int operation) {
        DCAClient client = this.clientsSubscriptions.remove(subscriptionLogicalName);
        switch (operation) {
            //TODO Definir que es lo que tiene que ir como parametros
            case 1:
                return client.disconnect("");
            case 2:
                return client.unsubscribe("", "");
        }
        return false;
    }

    public String getQueryUOM() {
        return queryUOM;
    }

    public void setQueryUOM(String queryUOM) {
        this.queryUOM = queryUOM;
    }

    public String parseValueXML(String message, String queryXpath) {
        String results = new String();

        try {
            XPathString xPathString = new XPathString(message);
            results = xPathString.parseXpathFirst(queryXpath);
        } catch (Exception e) {
            log.error("Error parsing the data");
        }
        return results;
    }

    public String[] parseXML(String message, String queryXpath) {
        String[] results = new String[10];
        try {
            XPathString xPathString = new XPathString(message);
            results = xPathString.parseXpathValues(queryXpath);

        } catch (Exception e) {
            log.error("Error parsing the data");
        }
        return results;
    }

    public DCAClient getClient(String subscriptionLogicalName) {
        return clientsSubscriptions.get(subscriptionLogicalName);
    }

    public VirtualSensor getSensor(String subscriptionLogicalName) {
        return subscriptions.get(subscriptionLogicalName);
    }

    private void initWebServices() {
        try{
            org.apache.cxf.jaxws.JaxWsServerFactoryBean sfMng1 = new org.apache.cxf.jaxws.JaxWsServerFactoryBean();
            setNotificationHandler(new NotificationHandler());
            sfMng1.setServiceBean(getNotificationHandler());
            //sfMng1.setAddress(this.notificationsResponseURL);
            sfMng1.setAddress(this.notificationsResponseURL);
            setNotificationResponseServer(sfMng1.create());    // TODO: This seems to be called twice in the code (the second time thows an exception that the java.lang.RuntimeException: Soap 1.1 endpoint already registered on address ...
            log.info("A new Web Service has been initiated at " +  notificationsResponseURL);

            org.apache.cxf.jaxws.JaxWsServerFactoryBean sfMng2 = new org.apache.cxf.jaxws.JaxWsServerFactoryBean();
            setSubscriptionHandler(new SubscriptionHandler());
            sfMng2.setServiceBean(getSubscriptionHandler());
            sfMng2.setAddress(subscriptionsResponseURL);
            setSubscriptiontionResponseServer(sfMng2.create());
            log.info("A new Web Service has been initiated at " +  subscriptionsResponseURL);
        }
        catch(Exception e){
            log.error(e);
            e.printStackTrace();
        }
    }

    public HashMap<String, MsgBox> getUom() {
        return uom;
    }

    public void setUom(HashMap<String, MsgBox> uom) {
        this.uom = uom;
    }

    public HashMap<String, VirtualSensor> getVs() {
        return vs;
    }

    public void setVs(HashMap<String, VirtualSensor> vs) {
        this.vs = vs;
    }

    public HashMap<String, DCAClient> getClients() {
        return clientsSubscriptions;
    }

    public void setClients(HashMap<String, DCAClient> clients) {
        this.clientsSubscriptions = clients;
    }

    public HashMap<String, VirtualSensor> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(HashMap<String, VirtualSensor> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public String getQueryXpathSensorName() {
        return queryXpathSensorName;
    }

    public void setQueryXpathSensorName(String queryXpathSensorName) {
        this.queryXpathSensorName = queryXpathSensorName;
    }

    public String getQueryXpathGWName() {
        return queryXpathGWName;
    }

    public void setQueryXpathGWName(String queryXpathGWName) {
        this.queryXpathGWName = queryXpathGWName;
    }

    public String getQueryXpathPhenomenom() {
        return queryXpathPhenomenom;
    }

    public void setQueryXpathPhenomenom(String queryXpathPhenomenom) {
        this.queryXpathPhenomenom = queryXpathPhenomenom;
    }

    public String getQueryXpathUOM() {
        return queryXpathUOM;
    }

    public void setQueryXpathUOM(String queryXpathUOM) {
        this.queryXpathUOM = queryXpathUOM;
    }

    public String getQueryXpathObservation() {
        return queryXpathObservation;
    }

    public void setQueryXpathObservation(String queryXpathObservation) {
        this.queryXpathObservation = queryXpathObservation;
    }

    public String getQueryXpathLocation() {
        return queryXpathLocation;
    }

    public void setQueryXpathLocation(String queryXpathLocation) {
        this.queryXpathLocation = queryXpathLocation;
    }

    public int getSensorId() {
        return sensorId;
    }

    public void setSensorId(int sensorId) {
        this.sensorId = sensorId;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getSubsName() {
        return subsName;
    }

    public void setSubsName(String subsName) {
        this.subsName = subsName;
    }

    public void setNotificationResponseServer(org.apache.cxf.endpoint.Server server){
        this.notificationResponseServer = server;
    }

    public org.apache.cxf.endpoint.Server getNotificationResponseServer(){
        return this.notificationResponseServer;
    }

    public void setSubscriptiontionResponseServer(org.apache.cxf.endpoint.Server server){
        this.subscriptionResponseServer = server;
    }

    public org.apache.cxf.endpoint.Server getSubscriptionResponseServer(){
        return this.subscriptionResponseServer;
    }
    public void setNotificationHandler(NotificationHandler s){
        this.notificationHandler = s;
    }

    public NotificationHandler getNotificationHandler() {
        return this.notificationHandler;
    }

    public void setSubscriptionHandler(SubscriptionHandler s){
        this.subscriptionHandler = s;
    }

    public SubscriptionHandler getSubscriptionHandler() {
        return this.subscriptionHandler;
    }

    public String getNotificationResponseURL(){
        return this.notificationsResponseURL;
    }

    public String getSubscriptionResponseURL(){
        return this.subscriptionsResponseURL;
    }

    public boolean findSubscription(String subscriptionLN){
        if(subscriptions.containsKey(subscriptionLN)) {
            return true;
        }
        else {
            return false;
        }
    }

    public org.apache.log4j.Logger getLogger(){
        return log;
    }

    /**
     * for debuggin
     */

    public String getCustomText()
    {
        return  customText;
    }

    public void setCustomText(String pCustomTxt)
    {
        customText = pCustomTxt;
    }
}
