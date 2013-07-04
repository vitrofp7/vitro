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
package alter.vitro.vgw.wsiadapter;

import alter.vitro.vgw.service.VitroGatewayService;
import alter.vitro.vgw.service.geodesics.GeodesicPoint;
import alter.vitro.vgw.model.CGateway;
import alter.vitro.vgw.model.CGatewayWithSmartDevices;
import alter.vitro.vgw.model.CSensorModel;
import alter.vitro.vgw.model.CSmartDevice;
import alter.vitro.vgw.service.query.wrappers.*;
/*import alter.vitro.vgw.service.query.xmlmessages.aggrquery.ThresholdFieldType;*/
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ws4d.coap.Constants;
import org.ws4d.coap.connection.BasicCoapChannelManager;
import org.ws4d.coap.interfaces.*;
import org.ws4d.coap.messages.CoapEmptyMessage;
import org.ws4d.coap.messages.CoapRequestCode;
import vitro.vgw.wsiadapter.coap.util.Functions;
/*import scala.collection.mutable.HashTable;   */

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class WsiUberDustCon extends WsiAdapterCon{
    private Logger logger = LoggerFactory.getLogger(getClass());

    //todo: to be moved into a config.txt or something
    public static final String uberdustNodesGetRestUri = "http://uberdust.cti.gr/rest/testbed/1/node/raw";
    public static final String uberdustNodes_Status_GetRestUri = "http://uberdust.cti.gr/rest/testbed/1/status/raw";

    public static final String coapUrnUberDustForSettingActuators ="uberdust.cti.gr";  // urn of node/urn of capability;
    public static final String coapUrnUberDustForGettingActuatorStatus ="uberdust.cti.gr";  // + just the simple name of the capability;
    public static final int coapUberdustPort = 5683;


    public static HashMap<String, String>  coapRequestMessageIdToUrnNode = new HashMap<String, String>();
    public static HashMap<String, String>  urnNodeToCoapReply = new HashMap<String, String>();

    private static String staticprefixUberdustCapability ="urn:wisebed:node:capability:";

    private static String staticprefixNode = "urn:wisebed:ctitestbed:";

    // DEBUG OFFLINE STUFF
    private static boolean DEBUG_OFFLINE_MODE = true;
    private static String DEBUG_OFFLINE_STR_NODE_GETRESTSTATUS=  "" +
            "urn:wisebed:ctitestbed:0x14d4\n" +
            "urn:wisebed:ctitestbed:0x14d9\n" +
            "urn:wisebed:ctitestbed:0x14e6\n" +
            "urn:wisebed:ctitestbed:0x14ea\n" +
            "urn:wisebed:ctitestbed:0x152f\n" +
            "urn:wisebed:ctitestbed:0x1538\n" +
            "urn:wisebed:ctitestbed:0x786a\n" +
            "urn:wisebed:ctitestbed:0x295\n" +
            "urn:wisebed:ctitestbed:0x42f"; // nodes list

    // nodes list with capabilities
    private static String DEBUG_OFFLINE_STR_GETRESTSTATUS_RAW =  "" +
            "urn:wisebed:ctitestbed:0x14d4\t"+WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t32\n" +
            "urn:wisebed:ctitestbed:0x14d4\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x14d4\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x14d9\t" +WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t28\n" +
            "urn:wisebed:ctitestbed:0x14d9\t" +WsiUberDustCon.staticprefixUberdustCapability+"light\t1366167339000\t25\n" +
            "urn:wisebed:ctitestbed:0x14d9\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x14d9\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x14e6\t" +WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t24\n" +
            "urn:wisebed:ctitestbed:0x14e6\t" +WsiUberDustCon.staticprefixUberdustCapability+"light\t1366167339000\t13\n" +
            "urn:wisebed:ctitestbed:0x14e6\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x14e6\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x14ea\t" +WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t28\n" +
            "urn:wisebed:ctitestbed:0x14ea\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x14ea\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x152f\t" +WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t23\n" +
            "urn:wisebed:ctitestbed:0x152f\t" + "room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x152f\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x1538\t" + WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t26\n"+
            "urn:wisebed:ctitestbed:0x1538\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x1538\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x786a\t" + WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t30\n"+
            "urn:wisebed:ctitestbed:0x786a\t" + WsiUberDustCon.staticprefixUberdustCapability+"light\t1366167339000\t19\n"+
            "urn:wisebed:ctitestbed:0x786a\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x786a\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x295\t" + WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t25\n"+
            "urn:wisebed:ctitestbed:0x295\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x295\t" +"nodetype\t0\tisense\n" +
            "urn:wisebed:ctitestbed:0x42f\t" + WsiUberDustCon.staticprefixUberdustCapability+"temperature\t1366167339000\t31\n"+
            "urn:wisebed:ctitestbed:0x42f\t" + WsiUberDustCon.staticprefixUberdustCapability+"light\t1366167339000\t23\n"+
            "urn:wisebed:ctitestbed:0x42f\t" +"room\t0\t0.I.5\n" +
            "urn:wisebed:ctitestbed:0x42f\t" +"nodetype\t0\tisense\n" ;

    private static String DEBUG_OFFLINE_STR_BODY_ADMINSTATUS = "" ; // the faulty nodes
    // END OF DEBUG OFFLINE STUFF

    static Map<String, String> dictionaryUberdustUrnToSimpleCapabilityName;
    static
    {
        dictionaryUberdustUrnToSimpleCapabilityName = new HashMap<String, String>();
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"temperature", "temperature");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"humidity", "humidity");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"light", "light");             // TODO: light is unsupported for now, but will be appended (IDAS mentioned that it is named: luminousIntensity)
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"windspeed", "windspeed");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"co", "co");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"co2", "co2");
        //dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"ir", "ir");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"pressure", "pressure");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"barometricpressure", "barometricpressure");
        // TODO: This actuation just for the demo. Adapt/Improve later
        //
        // Changed light1, 2, 3, 4 to lz to reflect changes in uberdust naming
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"lz1", "switchlight1");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"lz2", "switchlight2");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"lz3", "switchlight3");
        dictionaryUberdustUrnToSimpleCapabilityName.put(WsiUberDustCon.staticprefixUberdustCapability+"lz4", "switchlight4");
    }

    private String getSimpleCapForUberdustUrn(String pName){
        String retVal = "UnknownPhenomenon";
        try{
            if(dictionaryUberdustUrnToSimpleCapabilityName.containsKey(pName) )
            {
                retVal = dictionaryUberdustUrnToSimpleCapabilityName.get( pName);
            }
            else
            {
                logger.debug("Error: Unsupported simple capability for uberdust capability: " + pName);
            }
        }
        catch(Exception e)
        {
            logger.debug("Error: Unsupported simple capability for uberdust capability: " + pName);

        }
        return retVal;
    }

    private String getUberdustUrnForIDASCapName(String pName){
        String retVal = "UnknownPhenomenon";
        try{
            Set<String> tmpKeySet = dictionaryUberdustUrnToSimpleCapabilityName.keySet();
            for (String candidateUberDustUrn : tmpKeySet) {
                if ( dictionaryUberdustUrnToSimpleCapabilityName.containsKey(candidateUberDustUrn)   &&  (staticprefixPhenomenonIDAS + dictionaryUberdustUrnToSimpleCapabilityName.get(candidateUberDustUrn)).equals(pName)) {
                    retVal = candidateUberDustUrn;
                    break;
                }
            }
        }
        catch(Exception e)
        {
//            logger.debug("Error: Unsupported simple capability for uberdust capability: " + pName);

        }
        return retVal;

    }

    /**
     * Creates a new instance of WsiUberDustCon
     */
    private WsiUberDustCon() {
        //
        //
        super();
    }

    private static WsiUberDustCon myCon = null;

    /**
     * This is the function the world uses to get the Connection to the Data from the WSN.
     * It follows the Singleton pattern
     */
    public static WsiUberDustCon getWsiAdapterCon(DbConInfo databaseConnInfo) {
        if (myCon == null) {
            myCon = new WsiUberDustCon();
            WsiAdapterCon.setDbConInfo(databaseConnInfo);
            databaseConnInfo.setDatabaseIp(uberdustNodesGetRestUri);      // reusing this field for a different purpose.
            databaseConnInfo.setDBextraInfo(uberdustNodes_Status_GetRestUri);      // reusing this field for a different purpose.

            // TODO: attempt to connect to rest to test availabity?
        }
        return myCon;
    }
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //                  START OF OFFLINE FUNCTIONS
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################

    /**
     * Test function in order to debug resource discovery even when uberdust is not accessible
     * @param givGatewayInfo
     * @return
     */
    private CGatewayWithSmartDevices  DEBUG_offline_createWSIDescr(CGateway givGatewayInfo)  {
        Vector<CSmartDevice> currSmartDevicesVec = new Vector<CSmartDevice>();
        CGatewayWithSmartDevices myGatewayForSmartDevs = new CGatewayWithSmartDevices(givGatewayInfo, currSmartDevicesVec);

        // An auxiliary structure that maps Unique Generic Capability Descriptions to Lists of SensorTypes ids.
        HashMap<String, Vector<Integer>> myAllCapabilitiesToSensorModelIds = new HashMap<String, Vector<Integer>>();


        String responseBodyFromHttpNodesGetStr = WsiUberDustCon.DEBUG_OFFLINE_STR_NODE_GETRESTSTATUS;
        String responseBodyFromHttpNodes_STATUS_Get = WsiUberDustCon.DEBUG_OFFLINE_STR_GETRESTSTATUS_RAW;
        String responseBodyFromHttpNodes_ADMINSTATUS_Get = WsiUberDustCon.DEBUG_OFFLINE_STR_BODY_ADMINSTATUS;
        try{
            //
            String[] nodeUrnsInUberdust = responseBodyFromHttpNodesGetStr.split("\\r?\\n");
            int totalNodeUrnsInUberdust = nodeUrnsInUberdust.length;


            String[] nodeAndLastCapReadingsUrnsInUberdust = responseBodyFromHttpNodes_STATUS_Get.split("\\r?\\n");
            int totalNodeWithCapsInUberdust= nodeAndLastCapReadingsUrnsInUberdust.length;

            //TODO: test this:
            Vector<String> allFaultyNodesUrns = getFaultyNodes();


            // LOOP OVER EVERY NODE (smart device), and for each node, get its capabilities from the second response (responseBody_STATUS_Str)

            logger.debug("Total nodes:" + String.valueOf(totalNodeUrnsInUberdust));
            for (String aNodeUrnsInUberdust : nodeUrnsInUberdust) {
                if(allFaultyNodesUrns.contains(aNodeUrnsInUberdust))
                {
                    logger.debug("Skiipping node: "+aNodeUrnsInUberdust);
                    continue; //skip faulty nodes!
                }
                logger.debug("Discovering resources of node: " +aNodeUrnsInUberdust );

                Vector<Integer> sensorModels_IDs_OfSmartDevVector = new Vector<Integer>();// todo: fix this redundancy!
                Vector<CSensorModel> sensorModelsOfSmartDevVector = new Vector<CSensorModel>();
                CSmartDevice tmpSmartDev = new CSmartDevice(aNodeUrnsInUberdust,
                        "",/* smart device type name */
                        "",/* location description e.g. room1*/
                        new GeodesicPoint(), /*  */
                        sensorModels_IDs_OfSmartDevVector);


                // TODO: Add an extra early for loop to update the fields for the attributes of the SmartDevice such as:
                //      Eventually if the SmartDev has NO other valid sensors (e.g. observation sensors or actuators) then it won't be added !
                String tmp_longitude = "";
                String tmp_latitude = "";
                String tmp_altitude = "";

                for (String aNodeAndLastCapReadingsUrnsInUberdust1 : nodeAndLastCapReadingsUrnsInUberdust) {
                    //to update the device attributes!
                    String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust1.split("\\t");
                    if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)       //we are at the capabilities of the current smartdevice
                    {
                        logger.debug(" node id: " +nodeCapAndReadingRowItems[0] );
                        logger.debug(" capability: " +nodeCapAndReadingRowItems[1] != null? nodeCapAndReadingRowItems[1] : "" );
                        logger.debug(" timestamp: " +nodeCapAndReadingRowItems[2] != null? nodeCapAndReadingRowItems[2] : "");
                        logger.debug(" measurement: " +nodeCapAndReadingRowItems[3]!= null? nodeCapAndReadingRowItems[3]: "" );
                        // [0] is mote (smart device) id
                        // [1] is capability
                        // [2] is timestamp
                        // [3] is measurement value
                        if ((nodeCapAndReadingRowItems[1] != null) && !(nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase(""))) {
                            if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("room") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                {
                                    tmpSmartDev.setLocationDesc(nodeCapAndReadingRowItems[3].trim());
                                }
                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("nodetype") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                tmpSmartDev.setName(nodeCapAndReadingRowItems[3].trim());
                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("description") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                //TODO: do we need this?

                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("x") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                //TODO: we need the function to derive a valid longitude from the uberdust value (pending)
                                tmp_longitude = nodeCapAndReadingRowItems[3].trim();
                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("y") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                //TODO: we need the function to derive a valid latitude)
                                tmp_latitude = nodeCapAndReadingRowItems[3].trim();
                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("z") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                //altitude is in meters (assumption)
                                tmp_altitude = nodeCapAndReadingRowItems[3].trim();

                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("phi") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                //TODO: do we need this?
                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("theta") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                //TODO: do we need this?
                            }
                        }

                    }
                }    // end of first round of for loop for attributes
                if (!tmp_latitude.equalsIgnoreCase("") && !tmp_longitude.equalsIgnoreCase("") && !tmp_altitude.equalsIgnoreCase("")) {
                    tmpSmartDev.setGplocation(new GeodesicPoint(tmp_latitude, tmp_longitude, tmp_altitude));
                }

                //
                // Again same loop for measurement and actuation capabilities!
                //
                for (String aNodeAndLastCapReadingsUrnsInUberdust : nodeAndLastCapReadingsUrnsInUberdust) {
                    String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust.split("\\t");
                    if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)           //we are at the capabilities of the current smartdevice
                    {
                        // [0] is mote (smart device) id
                        // [1] is capability
                        // [2] is measurement value
                        // [3] is timestamp
//                                        logger.debug(nodeCapAndReadingRowItems[1]);
                        // TODO: FILTER OUT UNSUPPORTED OR COMPLEX CAPABILITIES!!!!
                        // Since uberdust does not distinguish currenlty between sensing/actuating capabilities and properties, we need to filter out manually
                        // everything we don't consider a sensing/actuating capability.
                        // Another filtering out is done at a later stage with the SensorMLMessageAdapter, which will filter out the capabilities not supported by IDAS
                        // TODO: it could be nice to have this filtering unified.
                        if ((nodeCapAndReadingRowItems[1] != null) && (nodeCapAndReadingRowItems[1].trim().compareTo("") != 0)
                                && !getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("UnknownPhenomenon")) {

                            //todo: this is just to support actuation during the demo. The code should be improved later on:
                            // todo: replace with regex
                            //if(getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight1")
                            //        || getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight2")
                            //        ||  getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight3")
                            //        ||getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight4") )
                            //{

                            //}
                            // else
                            // {
                            //TODO: don't get light measurements from arduinos even if they advertise light as a capability

                            // The model id is set as the hashcode of the capability name appended with the model type of the device.
                            // Perhaps this should be changed to something MORE specific
                            // TODO: the units should be set here as we know them. Create a small dictionary to set them!
                            // TODO: the non-observation sensors/ non-actuation should be filtered here!! the Name for the others should be "UnknownPhenomenon"

                            String tmpGenericCapabilityForSensor = getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim());
                            Integer thedigestInt = (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()).hashCode();
                            if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

                            CSensorModel tmpSensorModel = new CSensorModel(givGatewayInfo.getId(), /*Gateway Id*/
                                    thedigestInt, /*Sensor Model Id */
                                    (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()), /* Sensor Model name */
                                    CSensorModel.numericDataType, /* Data type*/  // TODO: later on this should be adjustable!!!
                                    CSensorModel.defaultAccuracy, /* Accuracy */
                                    CSensorModel.defaultUnits) /* Units */;  // TODO: this should be set when it is known!!!
                            //                                            if(!tmpGenericCapabilityForSensor.equalsIgnoreCase("UnknownPhenomenon" ))
                            //                                            {
                            sensorModelsOfSmartDevVector.add(tmpSensorModel);
                            sensorModels_IDs_OfSmartDevVector.add(tmpSensorModel.getSmid());
                            //logger.debug("HER HE R HER : Adding id: "+ Integer.toString(thedigestInt)+ " for cap: " + tmpGenericCapabilityForSensor);
                            //Integer thedigestIntAlt = (tmpGenericCapabilityForSensor).hashCode();
                            //if (thedigestIntAlt < 0) thedigestIntAlt = thedigestIntAlt * (-1);
                            //logger.debug("HER HE R HER : WHEREAS EXPERIMENT id: "+ Integer.toString(thedigestIntAlt)+ " for cap: " + tmpGenericCapabilityForSensor);

                            //                                            }

                            if (!myAllCapabilitiesToSensorModelIds.containsKey(tmpGenericCapabilityForSensor)) {
                                myAllCapabilitiesToSensorModelIds.put(tmpGenericCapabilityForSensor, new Vector<Integer>());
                                givGatewayInfo.getAllGwGenericCapabilities().put(tmpGenericCapabilityForSensor, new Vector<CSensorModel>());
                            }
                            // When we reach this part, we already have a key that corresponds to a unique sensor capability description
                            if (!myAllCapabilitiesToSensorModelIds.get(tmpGenericCapabilityForSensor).contains(Integer.valueOf(tmpSensorModel.getSmid()))) {
                                myAllCapabilitiesToSensorModelIds.get(tmpGenericCapabilityForSensor).addElement(tmpSensorModel.getSmid());
                                givGatewayInfo.getAllGwGenericCapabilities().get(tmpGenericCapabilityForSensor).addElement(tmpSensorModel);
                            }
                            // }
                        }
                    }
                }
                if (!sensorModelsOfSmartDevVector.isEmpty()) {
                    // TODO: FILTER OUT UNSUPPORTED OR COMPLEX NODES!!!!
                    // For demo purposes let's keep only the first floor and iSense devices
                    String isensePrefixTag = "isense";
                    String arduinoTag = "arduino";
                    String telosBTag ="telosb";
                    String roomsOnZeroFloor_PartI_PrefixTag = "0.I.";
                    String roomsOnZeroFloor_PartII_PrefixTag = "0.II.";

                    if(!VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                    {
                        if ((!tmpSmartDev.getLocationDesc().isEmpty()) &&
                                ((tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartI_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartI_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartI_PrefixTag))
                                        || (tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartII_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartII_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartII_PrefixTag)))
                                &&
                                (!tmpSmartDev.getName().isEmpty()) &&
                                ((tmpSmartDev.getName().length() >= isensePrefixTag.length() && tmpSmartDev.getName().substring(0, isensePrefixTag.length()).equalsIgnoreCase(isensePrefixTag)) || (tmpSmartDev.getName().length() >= arduinoTag.length() && tmpSmartDev.getName().substring(0, arduinoTag.length()).equalsIgnoreCase(arduinoTag)))) {
                            currSmartDevicesVec.addElement(tmpSmartDev);
                        }
                    }
                    else if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                    {
                        //logger.debug("I am hai");
                        if ((!tmpSmartDev.getLocationDesc().isEmpty()) &&
                                ((tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartI_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartI_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartI_PrefixTag))
                                        || (tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartII_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartII_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartII_PrefixTag)))
                                &&
                                (!tmpSmartDev.getName().isEmpty()) &&
                                ((tmpSmartDev.getName().length() >= telosBTag.length() && tmpSmartDev.getName().substring(0, telosBTag.length()).equalsIgnoreCase(telosBTag)) )) {
                            String myoldid = tmpSmartDev.getId();
                            tmpSmartDev.setId(dictionaryUberdustUrnToHaiUrnName.get(myoldid));
                            currSmartDevicesVec.addElement(tmpSmartDev);
                        }
                    }

                    //#####################################
                }
            }      // ends for loop over all smartdevices discovered!

        }catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        return myGatewayForSmartDevs;

    }

    Vector<ReqResultOverData> DEBUG_offline_translateAggrQuery(Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFunctsVec, Vector<ReqFunctionOverData> reqFunctionVec) {
        //logger.debug("IN UBERDUST QUERY");
        /*for(QueriedMoteAndSensors qms: motesAndTheirSensorAndFunctsVec) {
            logger.debug("Mote: " + qms.getMoteid());
           for(ReqSensorAndFunctions rsf : qms.getQueriedSensorIdsAndFuncVec()){
               logger.debug("Capability: " +rsf.getSensorModelid());
               for( Integer fidd: rsf.getFunctionsOverSensorModelVec()) {
                   logger.debug("Function: " +fidd);
               }

           }

        }*/
        String responseBodyFromHttpNodesGetStr = WsiUberDustCon.DEBUG_OFFLINE_STR_NODE_GETRESTSTATUS;
        String responseBodyFromHttpNodes_STATUS_Get = WsiUberDustCon.DEBUG_OFFLINE_STR_GETRESTSTATUS_RAW;
        String responseBodyFromHttpNodes_ADMINSTATUS_Get = WsiUberDustCon.DEBUG_OFFLINE_STR_BODY_ADMINSTATUS;

        boolean useTheStandardWayToGetStateForActuatorReading = false; // false allows to get explicitly the latest reading instead of the standard one (in the general status page) updated every few minutes
        // TODO: tmp structure to be replaced when this is tested and works
        Vector<VerySimpleSensorEntity> allSensorsWithCapsAndMeasures = new Vector<VerySimpleSensorEntity>();
        // Maps Smart Device ids to Room names, where the room names are available.
        HashMap<String, String>  smartDevIdsToRooms = new  HashMap<String, String>();
        //
        // ##########################################################################################################################################
        //
        /*
         * TODO: optimize the status/resource retrieval process for uberdust!
         * TODO: Take into account the mote status before ADDING it to the gateway description list (++++ LATER)
         *         For now we assume that the queries to the actual WSN are handled by the middleware.
         *         We search the uberdust "database" for data. (but we can still perform actions to affect the WSI!)
         *
         *         The plan is for a future service
         *         where a peer could submit queries for submission in the actual WSNs, and subsequently gather the data
         *         of the results. (e.g. administration service>reprogramming service)
         */
        try {

                //logger.debug("--------OK Response: "+ httpUberdustNodesGetResponseStatusCode+"------------------------------");
                //
                String[] nodeUrnsInUberdust = responseBodyFromHttpNodesGetStr.split("\\r?\\n");
                int totalNodeUrnsInUberdust = nodeUrnsInUberdust.length;

                String[] nodeAndLastCapReadingsUrnsInUberdust = responseBodyFromHttpNodes_STATUS_Get.split("\\r?\\n");
                int totalNodeWithCapsInUberdust= nodeAndLastCapReadingsUrnsInUberdust.length;

                // LOOP OVER EVERY NODE (smart device), and for each node, get its capabilities from the second response (responseBody_STATUS_Str)
                Vector<String> allFaultyNodesUrns = getFaultyNodes();

                for (String aNodeUrnsInUberdust : nodeUrnsInUberdust) {
                    if(allFaultyNodesUrns.contains(aNodeUrnsInUberdust))
                    {
                        logger.debug("Skiipping node: "+aNodeUrnsInUberdust);
                        continue;
                    }
                    Vector<VerySimpleObservationCapabilities> sensObsCapsVector = new Vector<VerySimpleObservationCapabilities>();
                    Vector<VerySimpleSensorMeasurement> sensObsMeasurementVector = new Vector<VerySimpleSensorMeasurement>();


                    if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                    {
                        aNodeUrnsInUberdust = dictionaryUberdustUrnToHaiUrnName.get(aNodeUrnsInUberdust);
                        if(aNodeUrnsInUberdust == null) continue;
                    }

//                                logger.debug("Iteration " + String.valueOf(k+1) + " of " + String.valueOf(totalNodeUrnsInUberdust));
//                                logger.debug(nodeUrnsInUberdust[k]);

                    Vector<Integer> sensorModels_IDs_OfSmartDevVector = new Vector<Integer>();
                    CSmartDevice tmpSmartDev = new CSmartDevice(aNodeUrnsInUberdust,
                            "",/* smart device type name */
                            "",/* location description e.g. room1*/
                            new GeodesicPoint(), /*  */
                            sensorModels_IDs_OfSmartDevVector);


                    // TODO: Add an extra early for loop to update the fields for the attributes of the SmartDevice such as:
                    //      Eventually if the SmartDev has NO other valid sensors (e.g. observation sensors or actuators) then it won't be added !
                    String tmp_longitude = "";
                    String tmp_latitude = "";
                    String tmp_altitude = "";

                    for (String aNodeAndLastCapReadingsUrnsInUberdust1 : nodeAndLastCapReadingsUrnsInUberdust) {
                        //to update the device attributes!
                        String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust1.split("\\t");
                        if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                        {
                            nodeCapAndReadingRowItems[0] = dictionaryUberdustUrnToHaiUrnName.get(nodeCapAndReadingRowItems[0]);
                            if(nodeCapAndReadingRowItems[0] == null) continue;
                        }


                       if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)       //we are at the capabilities of the current smartdevice
                        {
                            // [0] is mote (smart device) id
                            // [1] is capability
                            // [2] is timestamp
                            // [3] is measurement value
                            if ((nodeCapAndReadingRowItems[1] != null) && !(nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase(""))) {
                                if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("room") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    tmpSmartDev.setLocationDesc(nodeCapAndReadingRowItems[3].trim());
                                    smartDevIdsToRooms.put(tmpSmartDev.getId(), tmpSmartDev.getLocationDesc());
                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("nodetype") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    tmpSmartDev.setName(nodeCapAndReadingRowItems[3].trim());
                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("description") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    //TODO: do we need this?

                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("x") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    //TODO: we need the function to derive a valid longitude from the uberdust value (pending)
                                    tmp_longitude = nodeCapAndReadingRowItems[3].trim();
                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("y") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    //TODO: we need the function to derive a valid latitude)
                                    tmp_latitude = nodeCapAndReadingRowItems[3].trim();
                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("z") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    //altitude is in meters (assumption)
                                    tmp_altitude = nodeCapAndReadingRowItems[3].trim();

                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("phi") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    //TODO: do we need this?
                                } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("theta") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                    //TODO: do we need this?
                                }
                            }
                        }
                    }    // end of first round of for loop for attributes
                    if (!tmp_latitude.equalsIgnoreCase("") && !tmp_longitude.equalsIgnoreCase("") && !tmp_altitude.equalsIgnoreCase("")) {
                        tmpSmartDev.setGplocation(new GeodesicPoint(tmp_latitude, tmp_longitude, tmp_altitude));
                    }

                    //
                    // Again same loop for measurement and actuation capabilities!
                    //
                    for (String aNodeAndLastCapReadingsUrnsInUberdust : nodeAndLastCapReadingsUrnsInUberdust) {
                        String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust.split("\\t");
                        if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                        {
                            nodeCapAndReadingRowItems[0] = dictionaryUberdustUrnToHaiUrnName.get(nodeCapAndReadingRowItems[0]);
                            if(nodeCapAndReadingRowItems[0] == null) continue;
                        }
                        if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)           //we are at the capabilities of the current smartdevice
                        {
                            // [0] is mote (smart device) id
                            // [1] is capability
                            // [2] is measurement value
                            // [3] is timestamp
//                                        logger.debug(nodeCapAndReadingRowItems[1]);
                            // TODO: FILTER OUT UNSUPPORTED OR COMPLEX CAPABILITIES!!!!
                            // Since uberdust does not distinguish currenlty between sensing/actuating capabilities and properties, we need to filter out manually
                            // everything we don't consider a sensing/actuating capability.
                            // Another filtering out is done at a later stage with the SensorMLMessageAdapter, which will filter out the capabilities not supported by IDAS
                            // TODO: it could be nice to have this filtering unified.
                            if ((nodeCapAndReadingRowItems[1] != null) && (nodeCapAndReadingRowItems[1].trim().compareTo("") != 0)
                                    && !getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("UnknownPhenomenon")) {
                                // The model id is set as the hashcode of the capability name appended with the model type of the device.
                                // Perhaps this should be changed to something MORE specific
                                // TODO: the units should be set here as we know them for Uberdust. Create a small dictionary to set them!
                                // TODO: the non-observation sensors/ non-actuation should be filtered here!! the Name for the others should be "UnknownPhenomenon"

                                String tmpGenericCapabilityForSensor = getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim());
                                Integer thedigestInt = (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()).hashCode();
                                if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

//                                            /*
//                                            CSensorModel tmpSensorModel = new CSensorModel(givGatewayInfo.getId(), /*Gateway Id*/
//                                                    thedigestInt, /*Sensor Model Id */
//                                                    (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()), /* Sensor Model name */
//                                                    CSensorModel.numericDataType, /* Data type*/  // TODO: later on this should be adjustable!!!
//                                                    CSensorModel.defaultAccuracy, /* Accuracy */
//                                                    CSensorModel.defaultUnits) /* Units */;  // TODO: this should be set when it is known!!!

                                // GET THE OBSERVATION
                                VerySimpleObservationCapabilities tmpObsCap = new VerySimpleObservationCapabilities(nodeCapAndReadingRowItems[1], true);
                                if ((tmpObsCap.getSimpleName() != null) && !(tmpObsCap.getSimpleName().equalsIgnoreCase("UnknownPhenomenon"))) {
                                    sensObsCapsVector.add(tmpObsCap);

                                    // ts of measurement in place [2]
                                    // value of measurement in place [3]
                                    // logger.debug(nodeCapAndReadingRowItems[2]+'\t'+nodeCapAndReadingRowItems[3]);
                                    long theTimeStamp = Long.parseLong(nodeCapAndReadingRowItems[2]);
                                    String theValue = nodeCapAndReadingRowItems[3];
                                    if(theValue.contains(" "))
                                        theValue = theValue.split(" ")[0];    // if it contains the UOM as a suffix,then just keep the first part
                                    String observPropertyDef = tmpObsCap.getPhenomenonIDASUrn();
                                    String observOutputUOMCode = tmpObsCap.getUomIDASUrn();// tmpObsCap.getUomIDASCode();
                                    // just one (last) value
                                    String[] observOutputMeasurementData = new String[1];
                                    // Dummy measurement value
                                    if (tmpObsCap.getSimpleName().equalsIgnoreCase("temperature"))
                                    {
                                        //since we assume kelvin to be the default UOM, until fixed, wew set oiur ceslious to Kelvin here:
                                        //K = C+273 . TODO. Later on this normalization should be done at the VSP!
                                        double d = Double.parseDouble(theValue);
                                        double convertedKelvinValue =  d + 273.0;
                                        String  convertedKelvinValueStr = Long.toString((long)convertedKelvinValue);

                                        observOutputMeasurementData[0] = convertedKelvinValueStr; //to kelvin
                                    }
                                    else
                                    {
                                        observOutputMeasurementData[0] = theValue;
                                    }
                                    // TODO: Check if timezone is correct!
                                    // FOR UBERDUST: override sensors timestamp with reply from uberdust timestamp (now)
                                    Date dateNow = new Date();
                                    theTimeStamp =  dateNow.getTime();
                                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                                    String observPropertyTSStr = df.format(new Date(theTimeStamp));
                                    //logger.debug("---- " + observPropertyTSStr + " ---- " + theValue + " ----------------------------");
                                    sensObsMeasurementVector.add(new VerySimpleSensorMeasurement(observPropertyDef, observOutputUOMCode, observOutputMeasurementData, observPropertyTSStr, theTimeStamp));
                                }

                                sensorModels_IDs_OfSmartDevVector.add(thedigestInt);

                            }
                        }
                    }
                    if (!sensorModels_IDs_OfSmartDevVector.isEmpty()) {
                        // TODO: FILTER OUT UNSUPPORTED OR COMPLEX NODES!!!!
                        VerySimpleSensorEntity sens01 = new VerySimpleSensorEntity(aNodeUrnsInUberdust, sensObsCapsVector, sensObsMeasurementVector);
                        allSensorsWithCapsAndMeasures.add(sens01);


                        // TODO: MAYBE HERE WE CAN CHECK IF THESE MEASUREMENTS are for the results??
                        //if (!sensObsMeasurementVector.isEmpty())
                        //{
                        //    Iterator<VerySimpleSensorMeasurement> it1 = sensObsMeasurementVector.iterator();
                        //    while( it1.hasNext())
                        //    {
                        //        VerySimpleSensorMeasurement sensorMeasurement = (VerySimpleSensorMeasurement)it1.next();
                        //
                        //    }
                        //}
                        //#####################################
                    }
                }      // ends for loop over all smartdevices discovered!
        }
        catch(Exception e)
        {
            logger.error("error::" + e.getMessage());

        }

        //
        // TILL HERE WE HAVE A VECTOR WITH ALL Devices and Capabilities and Measurements: allSensorsWithCapsAndMeasures
        //
        //

        Vector<ResultAggrStruct> vOfSensorValues;

        Vector<ReqResultOverData> retVecofResults;
        retVecofResults = new Vector<ReqResultOverData>();

        //logger.debug("Size of motesAndTheirSensorAndFunctsVec::" + Integer.toString(motesAndTheirSensorAndFunctsVec.size())  );

        for (int i = 0; i < motesAndTheirSensorAndFunctsVec.size(); i++) {

            String fullMoteId = motesAndTheirSensorAndFunctsVec.elementAt(i).getMoteid();

            // for each entry, get the vector of queried sensor types and the functions to be applied to the measurements.
            List<ReqSensorAndFunctions> tmpVecSmAndFuncList = motesAndTheirSensorAndFunctsVec.elementAt(i).getQueriedSensorIdsAndFuncVec();
//            Vector<Integer> tmpVecSmIds =  motesAndTheirSensorHM.get(fullMoteId);
            /**
             *
             *  TODO: So far we assume all of the data types in measurements to be Long! This should be fixed!!!
             *
             */
            try {

                //
                // We have the readings from all sensors.
                // we must select only the readings from the specific sensors of interest (those inside the  tmpVecSmAndFuncList vector) .
                //


                //logger.debug("Size of tmpVecSmAndFuncList::" + Integer.toString(tmpVecSmAndFuncList.size())  );
                for (ReqSensorAndFunctions aTmpVecSmAndFuncList : tmpVecSmAndFuncList) {
                    int smid = aTmpVecSmAndFuncList.getSensorModelIdInt();
                    int countValuesOfthisSensorModel = 0;

                    // TODO : fix to other connectors ->moved vOfSensorValues in the for loop!

                    //logger.debug("For mote "+fullMoteId +" and sensor "+Integer.toString(smid) + " function vector size is "+reqFunctionVec.size());
                    for (ReqFunctionOverData currentRequestedFunction : reqFunctionVec) {
                        vOfSensorValues = new Vector<ResultAggrStruct>();

                        if (currentRequestedFunction.getfuncId() == ReqFunctionOverData.unknownFuncId) {
                            vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId, smid, "No Result", 1, null));
                            countValuesOfthisSensorModel += 1;
                        } else if (aTmpVecSmAndFuncList.getFunctionsOverSensorModelVec().contains(currentRequestedFunction.getfuncId())) {    // this loop (and this condition) allows to retrieve the valid "functions" to be performed on values of this sensor
                            Vector<VerySimpleSensorMeasurement> mySensorReadingsRelatedToCurrentFunction = new Vector<VerySimpleSensorMeasurement>();  // bugfix: this is now moved inside the functions loop
                            // for each different "function" on the sensor values, we may need to gather completely different values. (e.g. a function could request a history of measurements, or only measurements that are above a threshold)
                            // TODO: Separate cases for binary values (e.g. image or webcam stream) and numeric values  (and string values?)

                            // TODO: for uberdust, loop through all nodes in (http get status vector): allSensorsWithCapsAndMeasures
                            //          and keep the readings, apply functions (FOR NOW WE ALWAYS APPLY LAST READING NO MATTER WHAT)
                            // TODO: Fix -> for now we always apply last reading no matter what the actual function was (since we have no history).
                            // TODO: fix serial numbers for sensor models. They should not just be the hash on the capability simple name...
                            for (VerySimpleSensorEntity tmpSmartDev : allSensorsWithCapsAndMeasures) {
                                if (tmpSmartDev.getSerialID().equalsIgnoreCase(fullMoteId))     // first match based on the requested smart device ID
                                {
                                    for (VerySimpleSensorMeasurement tmpMeasurement : tmpSmartDev.getMeasurementsVector()) {
                                        String obsPropertyIDASUrn = tmpMeasurement.getObservPropertyDef();
                                        String obsPropertySimple = "lalala";
                                        Iterator<String> itDict = dictionaryNameToIDASPhenomenon.keySet().iterator();
                                        String tmpSimpleName;
                                        // initial loop to get the "real" simple name for the search key capability (at this poing this is not yet a valid requested sensor)
                                        // we need the simple name because we used it to calculate the sensor model id (with the hashCode() )
                                        // so we get simple names, then calc their hashCodes (turn it into a positive number if it was negative) and then compare it with the requested hashcode (smid)  (assumed positive, because the DVNS will make sure of that)
                                        // logger.debug("^^^^^^^^^^OV: "+ obsPropertyIDASUrn);
                                        while (itDict.hasNext()) {
                                            tmpSimpleName = itDict.next();
                                            //logger.debug("^^^^^^^^^^VS: "+ (dictionaryNameToIDASPhenomenon.get(tmpSimpleName)).toString());

                                            if ((staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get(tmpSimpleName))).equalsIgnoreCase(obsPropertyIDASUrn)) {
                                                //logger.debug("*** *** *** Found matching capability in dictionary:" + tmpSimpleName);
                                                obsPropertySimple = tmpSimpleName;
                                                break;
                                            }
                                        }

                                        int projectedSmIdforPropertyDef = obsPropertySimple.hashCode();
                                        if (projectedSmIdforPropertyDef < 0) {
                                            projectedSmIdforPropertyDef = projectedSmIdforPropertyDef * (-1);
                                        }

                                        if (smid == projectedSmIdforPropertyDef) {
                                            // debug:
//                                            if((tmpSimpleName.equalsIgnoreCase("switchlight1")
//                                                    ||tmpSimpleName.equalsIgnoreCase("switchlight2")
//                                                    ||tmpSimpleName.equalsIgnoreCase("switchlight3")
//                                                    ||tmpSimpleName.equalsIgnoreCase("switchlight4") )
//                                                &&
//                                            ((smartDevIdsToRooms.get(fullMoteId)!=null) && smartDevIdsToRooms.get(fullMoteId).equalsIgnoreCase("0.I.3")));
//                                                    {
//                                                logger.debug("*** *** *** ADDING A MEASUREMENT FOR: "+ tmpSimpleName + " Mote:" +fullMoteId + "Room: " + smartDevIdsToRooms.get(fullMoteId));
//                                            }
                                            mySensorReadingsRelatedToCurrentFunction.add(tmpMeasurement);

                                            break; // TODO: break since a smartdevice will not have two of the same sensor models. Can it though? in general?
                                        }
                                        //else
                                        //{
                                        //    logger.debug("*** *** *** BUT DOES NOT MATCH A requested sensor: "+ tmpSimpleName);
                                        //}
                                    }
                                    break; //since we processed  the sensor dev that we wanted.
                                }
                            }

                            //logger.debug("READINGS LENGTH:" + Integer.toString(mySensorReadingsRelatedToCurrentFunction.length) );

                            for (int o = 0; o < mySensorReadingsRelatedToCurrentFunction.size(); o++) {
                                /* TODO: (++++) this could be optimized further (not write the entire data in the vector) / first process it
                                 * according to the function.
                                 * TODO: Somewhere around here we should handle the History function (not likely for uberdust)
                                 */
                                //SensorTypes tmpSensor = jWebTypesManager.selectSensorType(smid);
                                long valueToAdd=-1;
                                //if(tmpSensor.getIsComplex() == false)
                                //{
                                // TODO: here we handle the actuation capabilities for lights as well, if a set value function was requested on them
                                // TODO: if a last value reading was requested we can handle that too by sending their state (as reported)
                                if (mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight1")))
                                        || mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight2")))
                                        || mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight3")))
                                        || mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight4")))
                                        ) {
                                    logger.debug("Function: " + currentRequestedFunction.getfuncName());
                                    // TODO: for now we use the threshold field to set the actuation value! Later this could be a separate parameter field
                                    if (currentRequestedFunction.getfuncName().equalsIgnoreCase(ReqFunctionOverData.setValFunc) && currentRequestedFunction.getThresholdField() != null && !currentRequestedFunction.getThresholdField().isEmpty()) {
                                        logger.debug("-------- HANDLING ACTUATION NOW! " + mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef() + " room: " + smartDevIdsToRooms.get(fullMoteId) + " mote: " + fullMoteId + " val: " + mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                        ThresholdStructure requiredThresholds = new ThresholdStructure(currentRequestedFunction.getThresholdField());
                                        if (requiredThresholds.getLowerBound() != null && !requiredThresholds.getLowerBound().isEmpty()) {
                                            logger.debug("Actuation parameter: " + requiredThresholds.getLowerBound().trim());
                                            // attempt to set the light to the desired value!
                                            // TODO: check if a valid value (0 or 1)
                                            try {
                                                String valStr =  actuateSmartDevToValue(fullMoteId, smartDevIdsToRooms.get(fullMoteId), getUberdustUrnForIDASCapName(mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef()), requiredThresholds.getLowerBound().trim());
                                                double d = Double.parseDouble(valStr);
                                                valueToAdd = (long) d;
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                valueToAdd = -1;
                                            }

                                        }
                                    }
                                    else {

                                        if(useTheStandardWayToGetStateForActuatorReading)
                                        {
                                            try {
                                                String valStr =  mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0];
                                                double d = Double.parseDouble(valStr);
                                                valueToAdd = (long) d;
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                valueToAdd = -1;
                                            }
                                        }
                                        else
                                        {
                                            String UberdustUrnForCap = getUberdustUrnForIDASCapName(mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef());
                                            String justtheCapName = UberdustUrnForCap.substring(staticprefixUberdustCapability.length());
                                            //TODO: this masking is just for the demo!
                                            //mask light4 capability as light5 in order to show it in the demo: (light4 is not visible from the camera's viewpoint)
                                            // Changed light4 to lz4 to reflect naming change in uberdust
                                            if(justtheCapName.equalsIgnoreCase("lz4"))
                                                justtheCapName = "lz5";
                                            String lineOfStateReading = getLatestReadingTabSepLineForVirtualNode(fullMoteId, justtheCapName);
                                            String[] lineTokens = lineOfStateReading.split("\\t");
                                            // [0] has the timestamp
                                            // [1] has the value
                                            long valueOfReturnedState;
                                            String observPropertyTSStr;
                                            long theTimeStamp = 0;
                                            try {
                                                double d = Double.parseDouble(lineTokens[1]);
                                                valueOfReturnedState = (long) d;
                                                theTimeStamp = Long.parseLong(lineTokens[0]);
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                                                observPropertyTSStr = df.format(new Date(theTimeStamp));
                                                logger.debug("Actuator state was: " + lineTokens[1] + " at: " + observPropertyTSStr);
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                                                observPropertyTSStr = df.format(new Date(theTimeStamp));
                                                valueOfReturnedState = -1;

                                            }
                                            mySensorReadingsRelatedToCurrentFunction.elementAt(o).setObservPropertyTSLong(theTimeStamp);
                                            mySensorReadingsRelatedToCurrentFunction.elementAt(o).setObservPropertyTSStr(observPropertyTSStr);
                                            mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0] = Long.toString(valueOfReturnedState);
                                            // todo: move code repetition
                                            try {
                                                String valStr =  mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0];
                                                double d = Double.parseDouble(valStr);
                                                valueToAdd = (long) d;
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                valueToAdd = -1;
                                            }
                                        }
                                    }

                                } else {
                                    try {
                                        String valStr =  mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0];
                                        double d = Double.parseDouble(valStr);
                                        valueToAdd = (long) d;
                                    } catch (Exception e) {
                                        //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                        valueToAdd = -1;
                                    }
                                }

                                long timestampOfReading = mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyTSLong();
                                Timestamp timestampOfReadingSql = new Timestamp(timestampOfReading);
                                vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId,
                                        smid,
                                        Long.toString(valueToAdd),
                                        1,
                                        new TimeIntervalStructure(timestampOfReadingSql,
                                                timestampOfReadingSql))
                                );
                                //}
                                //                                else// put blob value as a String (FOR NOW this is just a URL to the binary file so this is ok) (++++)
                                //                                    // TODO: later handling of binary data will change and we should send the real binary files over pipes to the client
                                //                                {
                                //                                    vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId,
                                //                                        smid,
                                //                                        new String(myMotesSensorsReadings[o].getComplexRawData()),
                                //                                        1,
                                //                                        new TimeIntervalStructure(myMotesSensorsReadings[o].getDate(),
                                //                                                myMotesSensorsReadings[o].getDate()))
                                //                                    );
                                //                                }

                                countValuesOfthisSensorModel += 1;
                            }
                            if (countValuesOfthisSensorModel == 0) {
                                vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId, smid, ReqResultOverData.specialValueNoReading, 1, null));
                                countValuesOfthisSensorModel += 1;
                            } else
                                logger.debug("Counted Values of this sensor: " + fullMoteId + " "+  Integer.toString(countValuesOfthisSensorModel));
                        }
                        // this condition checks that at least one value was retrieved from the sensor and used in the function (even if that value was "no result")
                        if (countValuesOfthisSensorModel > 0) // we should make sure that this is always true.
                        {
                            retVecofResults.addElement(new ReqResultOverData(currentRequestedFunction.getfuncId(), vOfSensorValues));
                        }
                    }  // ends the block where we gather values of a sensor for a specific function
                }   // ends the loop over the requested sensor Models (capabilities) of the current requested Smart Device
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }// end of for loop over all requested Smart Devices in the request vector

        // here we have a Vector filled with ResultAggrStruct

        // END OF LEGACY driver code

        return retVecofResults;
    }

    private Vector<String> DEBUG_offline_getFaultyNodes(){

        String responseBodyFromHttpNodesGetStr = WsiUberDustCon.DEBUG_OFFLINE_STR_NODE_GETRESTSTATUS;
        String responseBodyFromHttpNodes_STATUS_Get = WsiUberDustCon.DEBUG_OFFLINE_STR_GETRESTSTATUS_RAW;
        String responseBodyFromHttpNodes_ADMINSTATUS_Get = WsiUberDustCon.DEBUG_OFFLINE_STR_BODY_ADMINSTATUS;

        Vector<String> retVec  = new Vector<String>();
        // direct for test http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x42f/payload/7f,69,70,1,$1,1
        try {


                    String[] faultyNodeUrnsLinesInUberdust = responseBodyFromHttpNodes_ADMINSTATUS_Get.split("\\r?\\n");
                    int totalfaultyNodeUrnsLinesInUberdust = faultyNodeUrnsLinesInUberdust.length;
                    for (String afaultyNodeUrnLineInUberdust : faultyNodeUrnsLinesInUberdust) {
                        String[] afaultyNodeUrnTokens = afaultyNodeUrnLineInUberdust.split("\\t");
                        // [0] has the urn
                        if(afaultyNodeUrnTokens!=null && afaultyNodeUrnTokens.length>0 && !afaultyNodeUrnTokens[0].isEmpty())
                        {
                            retVec.addElement(afaultyNodeUrnTokens[0]);
                            //      logger.debug("Adding faulty node:: "+afaultyNodeUrnTokens[0]);
                        }

                    }
        }
        catch (Exception e)
        {
            logger.debug(e.getMessage());
        }
        return retVec;
    }


    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //                  END OF OFFLINE FUNCTIONS
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    //###################################################################################################
    /**
     * TODO: IMPORTANT !!!
     *
     * Updates the list with the controlled WSI's capabilities
     */
    public synchronized CGatewayWithSmartDevices createWSIDescr(CGateway givGatewayInfo)  {
        //
        //
        //
        if(DEBUG_OFFLINE_MODE) {
            return DEBUG_offline_createWSIDescr(givGatewayInfo);
        }
        //
        Vector<CSmartDevice> currSmartDevicesVec = new Vector<CSmartDevice>();
        CGatewayWithSmartDevices myGatewayForSmartDevs = new CGatewayWithSmartDevices(givGatewayInfo, currSmartDevicesVec);

        // An auxiliary structure that maps Unique Generic Capability Descriptions to Lists of SensorTypes ids.
        HashMap<String, Vector<Integer>> myAllCapabilitiesToSensorModelIds = new HashMap<String, Vector<Integer>>();

        //
        // ##########################################################################################################################################
        // ##########################################################################################################################################
        //
        /*
         * TODO: optimize the status/resource retrieval process for uberdust!
         * TODO: Take into account the mote status before ADDING it to the gateway description list (++++ LATER)
         *         For now we assume that the queries to the actual WSN are handled by the middleware.
         *         We search the uberdust "database" for data. (but we can still perform actions to affect the WSI!)
         *
         *         The plan is for a future service
         *         where a peer could submit queries for submission in the actual WSNs, and subsequently gather the data
         *         of the results. (e.g. administration service>reprogramming service)
         */
        HttpClient httpclient = new DefaultHttpClient();
        try {
            //
            //
            // TODO: x, y, z can be used with wisedb Coordinate.java (look code) to produce GoogleEarth Coordinates (what ISO is that? Can it be advertised in SensorML for IDAS ?)
            // TODO: make use of Description and Type and Room Fields when available ?
            // TODO: Make a summary, how many valid from those found in uberdust? How many were registered successfully? How many measurements were registered successfully?
            //
            //
            boolean gotResponseFromHttpNodesGet = false;
            boolean gotResponseFromHttpNodes_STATUS_Get = false;
            boolean gotResponseFromHttpNodes_ADMINSTATUS_Get = false;

            String responseBodyStr = "";

            HttpGet httpUberdustNodesGet = new HttpGet(uberdustNodesGetRestUri);
            HttpResponse httpUberdustNodesGetResponse = httpclient.execute(httpUberdustNodesGet);

            int httpUberdustNodesGetResponseStatusCode = httpUberdustNodesGetResponse.getStatusLine().getStatusCode();
            HttpEntity httpUberdustNodesGetResponseEntity = httpUberdustNodesGetResponse.getEntity();
            if(httpUberdustNodesGetResponseEntity != null)
            {

                responseBodyStr = EntityUtils.toString(httpUberdustNodesGetResponseEntity);
                if (httpUberdustNodesGetResponseStatusCode != 200) {
                    // responseBody will have the error response
                    logger.debug("--------ERROR Response: "+ httpUberdustNodesGetResponseStatusCode+"------------------------------");
                    logger.debug(responseBodyStr);
                    logger.debug("----------------------------------------");
                }
                else
                {
                    //logger.debug("--------OK Response: "+ httpUberdustNodesGetResponseStatusCode+"------------------------------");
                    //
                    String[] nodeUrnsInUberdust = responseBodyStr.split("\\r?\\n");
                    int totalNodeUrnsInUberdust = nodeUrnsInUberdust.length;

                    HttpGet httpUberdustNodes_STATUS_Get = new HttpGet(uberdustNodes_Status_GetRestUri);
                    HttpResponse  httpUberdustNodes_STATUS_GetResponse = httpclient.execute(httpUberdustNodes_STATUS_Get);

                    int httpUberdustNodes_STATUS_GetResponseStatusCode = httpUberdustNodes_STATUS_GetResponse.getStatusLine().getStatusCode();
                    HttpEntity httpUberdustNodes_STATUS_GetResponseEntity = httpUberdustNodes_STATUS_GetResponse.getEntity();
                    if(httpUberdustNodes_STATUS_GetResponseEntity != null)
                    {
                        String responseBody_STATUS_Str = EntityUtils.toString(httpUberdustNodes_STATUS_GetResponseEntity);
                        if (httpUberdustNodes_STATUS_GetResponseStatusCode != 200) {
                            // responseBody_STATUS_Str will have the error response
                            logger.debug("--------ERROR Response: "+ httpUberdustNodes_STATUS_GetResponseStatusCode+"------------------------------");
                            logger.debug(responseBody_STATUS_Str);
                            logger.debug("----------------------------------------");
                        }
                        else
                        {
                            //logger.debug("--------OK Response: "+ httpUberdustNodes_STATUS_GetResponseStatusCode+"------------------------------");

                            String[] nodeAndLastCapReadingsUrnsInUberdust = responseBody_STATUS_Str.split("\\r?\\n");
                            int totalNodeWithCapsInUberdust= nodeAndLastCapReadingsUrnsInUberdust.length;

                            //TODO: test this:
                            Vector<String> allFaultyNodesUrns = getFaultyNodes();


                            // LOOP OVER EVERY NODE (smart device), and for each node, get its capabilities from the second response (responseBody_STATUS_Str)
                            for (String aNodeUrnsInUberdust : nodeUrnsInUberdust) {
                                if(allFaultyNodesUrns.contains(aNodeUrnsInUberdust))
                                {
                                    logger.debug("Skiipping node: "+aNodeUrnsInUberdust);
                                    continue; //skip faulty nodes!
                                }

//                                logger.debug("Iteration " + String.valueOf(k+1) + " of " + String.valueOf(totalNodeUrnsInUberdust));
//                                logger.debug(nodeUrnsInUberdust[k]);

                                Vector<Integer> sensorModels_IDs_OfSmartDevVector = new Vector<Integer>();// todo: fix this redundancy!
                                Vector<CSensorModel> sensorModelsOfSmartDevVector = new Vector<CSensorModel>();
                                CSmartDevice tmpSmartDev = new CSmartDevice(aNodeUrnsInUberdust,
                                        "",/* smart device type name */
                                        "",/* location description e.g. room1*/
                                        new GeodesicPoint(), /*  */
                                        sensorModels_IDs_OfSmartDevVector);


                                // TODO: Add an extra early for loop to update the fields for the attributes of the SmartDevice such as:
                                //      Eventually if the SmartDev has NO other valid sensors (e.g. observation sensors or actuators) then it won't be added !
                                String tmp_longitude = "";
                                String tmp_latitude = "";
                                String tmp_altitude = "";

                                for (String aNodeAndLastCapReadingsUrnsInUberdust1 : nodeAndLastCapReadingsUrnsInUberdust) {
                                    //to update the device attributes!
                                    String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust1.split("\\t");
                                    if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0) {

                                        // [0] is mote (smart device) id
                                        // [1] is capability
                                        // [2] is timestamp
                                        // [3] is measurement value
                                        if ((nodeCapAndReadingRowItems[1] != null) && !(nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase(""))) {
                                            if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("room") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                {
                                                    tmpSmartDev.setLocationDesc(nodeCapAndReadingRowItems[3].trim());
                                                }
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("nodetype") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                tmpSmartDev.setName(nodeCapAndReadingRowItems[3].trim());
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("description") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: do we need this?

                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("x") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: we need the function to derive a valid longitude from the uberdust value (pending)
                                                tmp_longitude = nodeCapAndReadingRowItems[3].trim();
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("y") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: we need the function to derive a valid latitude)
                                                tmp_latitude = nodeCapAndReadingRowItems[3].trim();
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("z") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //altitude is in meters (assumption)
                                                tmp_altitude = nodeCapAndReadingRowItems[3].trim();

                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("phi") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: do we need this?
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("theta") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: do we need this?
                                            }
                                        }

                                    }
                                }    // end of first round of for loop for attributes
                                if (!tmp_latitude.equalsIgnoreCase("") && !tmp_longitude.equalsIgnoreCase("") && !tmp_altitude.equalsIgnoreCase("")) {
                                    tmpSmartDev.setGplocation(new GeodesicPoint(tmp_latitude, tmp_longitude, tmp_altitude));
                                }

                                //
                                // Again same loop for measurement and actuation capabilities!
                                //
                                for (String aNodeAndLastCapReadingsUrnsInUberdust : nodeAndLastCapReadingsUrnsInUberdust) {
                                    String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust.split("\\t");
                                    if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)           //we are at the capabilities of the current smartdevice
                                    {
                                        // [0] is mote (smart device) id
                                        // [1] is capability
                                        // [2] is measurement value
                                        // [3] is timestamp
//                                        logger.debug(nodeCapAndReadingRowItems[1]);
                                        // TODO: FILTER OUT UNSUPPORTED OR COMPLEX CAPABILITIES!!!!
                                        // Since uberdust does not distinguish currenlty between sensing/actuating capabilities and properties, we need to filter out manually
                                        // everything we don't consider a sensing/actuating capability.
                                        // Another filtering out is done at a later stage with the SensorMLMessageAdapter, which will filter out the capabilities not supported by IDAS
                                        // TODO: it could be nice to have this filtering unified.
                                        if ((nodeCapAndReadingRowItems[1] != null) && (nodeCapAndReadingRowItems[1].trim().compareTo("") != 0)
                                                && !getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("UnknownPhenomenon")) {

                                            //todo: this is just to support actuation during the demo. The code should be improved later on:
                                            // todo: replace with regex
                                            //if(getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight1")
                                            //        || getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight2")
                                            //        ||  getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight3")
                                            //        ||getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("switchlight4") )
                                            //{

                                            //}
                                            // else
                                            // {
                                            //TODO: don't get light measurements from arduinos even if they advertise light as a capability

                                            // The model id is set as the hashcode of the capability name appended with the model type of the device.
                                            // Perhaps this should be changed to something MORE specific
                                            // TODO: the units should be set here as we know them. Create a small dictionary to set them!
                                            // TODO: the non-observation sensors/ non-actuation should be filtered here!! the Name for the others should be "UnknownPhenomenon"

                                            String tmpGenericCapabilityForSensor = getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim());
                                            Integer thedigestInt = (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()).hashCode();
                                            if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

                                            CSensorModel tmpSensorModel = new CSensorModel(givGatewayInfo.getId(), /*Gateway Id*/
                                                    thedigestInt, /*Sensor Model Id */
                                                    (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()), /* Sensor Model name */
                                                    CSensorModel.numericDataType, /* Data type*/  // TODO: later on this should be adjustable!!!
                                                    CSensorModel.defaultAccuracy, /* Accuracy */
                                                    CSensorModel.defaultUnits) /* Units */;  // TODO: this should be set when it is known!!!
                                            //                                            if(!tmpGenericCapabilityForSensor.equalsIgnoreCase("UnknownPhenomenon" ))
                                            //                                            {
                                            sensorModelsOfSmartDevVector.add(tmpSensorModel);
                                            sensorModels_IDs_OfSmartDevVector.add(tmpSensorModel.getSmid());
                                            //                                            }

                                            if (!myAllCapabilitiesToSensorModelIds.containsKey(tmpGenericCapabilityForSensor)) {
                                                myAllCapabilitiesToSensorModelIds.put(tmpGenericCapabilityForSensor, new Vector<Integer>());
                                                givGatewayInfo.getAllGwGenericCapabilities().put(tmpGenericCapabilityForSensor, new Vector<CSensorModel>());
                                            }
                                            // When we reach this part, we already have a key that corresponds to a unique sensor capability description
                                            if (!myAllCapabilitiesToSensorModelIds.get(tmpGenericCapabilityForSensor).contains(Integer.valueOf(tmpSensorModel.getSmid()))) {
                                                myAllCapabilitiesToSensorModelIds.get(tmpGenericCapabilityForSensor).addElement(tmpSensorModel.getSmid());
                                                givGatewayInfo.getAllGwGenericCapabilities().get(tmpGenericCapabilityForSensor).addElement(tmpSensorModel);
                                            }
                                            // }
                                        }
                                    }
                                }
                                if (!sensorModelsOfSmartDevVector.isEmpty()) {
                                    // TODO: FILTER OUT UNSUPPORTED OR COMPLEX NODES!!!!
                                    // For demo purposes let's keep only the first floor and iSense devices
                                    String isensePrefixTag = "isense";
                                    String arduinoTag = "arduino";
                                    String telosBTag ="telosb";
                                    String roomsOnZeroFloor_PartI_PrefixTag = "0.I.";
                                    String roomsOnZeroFloor_PartII_PrefixTag = "0.II.";

                                    if(!VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                                    {
                                        if ((!tmpSmartDev.getLocationDesc().isEmpty()) &&
                                                ((tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartI_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartI_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartI_PrefixTag))
                                                        || (tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartII_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartII_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartII_PrefixTag)))
                                                &&
                                                (!tmpSmartDev.getName().isEmpty()) &&
                                                ((tmpSmartDev.getName().length() >= isensePrefixTag.length() && tmpSmartDev.getName().substring(0, isensePrefixTag.length()).equalsIgnoreCase(isensePrefixTag)) || (tmpSmartDev.getName().length() >= arduinoTag.length() && tmpSmartDev.getName().substring(0, arduinoTag.length()).equalsIgnoreCase(arduinoTag)))) {
                                            currSmartDevicesVec.addElement(tmpSmartDev);
                                        }
                                    }
                                    else if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                                    {
                                        //logger.debug("I am hai");
                                        if ((!tmpSmartDev.getLocationDesc().isEmpty()) &&
                                                ((tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartI_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartI_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartI_PrefixTag))
                                                        || (tmpSmartDev.getLocationDesc().length() >= roomsOnZeroFloor_PartII_PrefixTag.length() && tmpSmartDev.getLocationDesc().substring(0, roomsOnZeroFloor_PartII_PrefixTag.length()).equalsIgnoreCase(roomsOnZeroFloor_PartII_PrefixTag)))
                                                &&
                                                (!tmpSmartDev.getName().isEmpty()) &&
                                                ((tmpSmartDev.getName().length() >= telosBTag.length() && tmpSmartDev.getName().substring(0, telosBTag.length()).equalsIgnoreCase(telosBTag)) )) {
                                            String myoldid = tmpSmartDev.getId();
                                            tmpSmartDev.setId(dictionaryUberdustUrnToHaiUrnName.get(myoldid));
                                            currSmartDevicesVec.addElement(tmpSmartDev);
                                        }
                                    }

                                    //#####################################
                                }
                            }      // ends for loop over all smartdevices discovered!
                        }  //if GET STATUS response code is OK!
                    }  // if GET STATUS response entity is NOT null
                } //if get list of nodes replied validly
            } //if get list of nodes response entity is NOT null
        }
        catch(Exception e)
        {
            logger.debug("error::" + e.getMessage());

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate de-allocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        // DEBUG: for debugging
        //GatewayDescriptionAdvertisement myGwAdDesc = new GatewayDescriptionAdvertisement(givGatewayInfo,
        //        currSmartDevicesVec,
        //        myAllCapabilitiesToSensorModels);
        //
        return myGatewayForSmartDevs;
    }

    /**
     * TODO: Should set the interval in which the Gateway should re-send its description. (asynchronously -i.e. not on demand from the Vitro Service Provider - Framework)
     *
     */
    public void setUpdateDescrInterval() {
        //
        //
        //
    }

    /**
     * Should translate the aggregated query to the appropriate type according to the middleware underneath
     * and return appropriate readings/values. The type of values should be stored elsewhere (at the VITRO Service Provider (VSP) !
     * TODO: NOTICE: for now the SensorModelId here is a hashcode of the simple name of the capability (resource name) .
     * TODO: ^^^^^^^ It is also always positive so multiply by *(-1) if it is not.
     * TODO: ^^^^^^^ This is inconsistent with what we sent for uberdust in the registration
     * TODO: ^^^^^^^ because for now the framework will ignore this setting on the RegisterSensor messages, and will just calculate it on its own from the hashcode of the resource names.
     *
     *
     *
     * @param motesAndTheirSensorAndFunctsVec Vector of moteIds mapped to their sensors (those of them that are involved in the query) and the requested function ids
     * @param reqFunctionVec        Vector with Functions to be applied to query data
     * @return a Vector of the Results as ReqResultOverData structures (XML)
     */
    // TODO: Important
    public synchronized Vector<ReqResultOverData> translateAggrQuery(Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFunctsVec, Vector<ReqFunctionOverData> reqFunctionVec) {


        if(DEBUG_OFFLINE_MODE) {
            return DEBUG_offline_translateAggrQuery(motesAndTheirSensorAndFunctsVec,reqFunctionVec);
        }


        boolean useTheStandardWayToGetStateForActuatorReading = false; // false allows to get explicitly the latest reading instead of the standard one (in the general status page) updated every few minutes
        // TODO: tmp structure to be replaced when this is tested and works
        Vector<VerySimpleSensorEntity> allSensorsWithCapsAndMeasures = new Vector<VerySimpleSensorEntity>();
        // Maps Smart Device ids to Room names, where the room names are available.
        HashMap<String, String>  smartDevIdsToRooms = new  HashMap<String, String>();
        //
        // ##########################################################################################################################################
        //
        /*
         * TODO: optimize the status/resource retrieval process for uberdust!
         * TODO: Take into account the mote status before ADDING it to the gateway description list (++++ LATER)
         *         For now we assume that the queries to the actual WSN are handled by the middleware.
         *         We search the uberdust "database" for data. (but we can still perform actions to affect the WSI!)
         *
         *         The plan is for a future service
         *         where a peer could submit queries for submission in the actual WSNs, and subsequently gather the data
         *         of the results. (e.g. administration service>reprogramming service)
         */
        HttpClient httpclient = new DefaultHttpClient();
        try {
            //
            //
            // TODO: x, y, z can be used with wisedb Coordinate.java (look code) to produce GoogleEarth Coordinates (what ISO is that? Can it be advertised in SensorML for IDAS ?)
            // TODO: make use of Description and Type and Room Fields when available ?
            // TODO: Make a summary, how many valid from those found in uberdust? How many were registered successfully? How many measurements were registered successfully?
            //
            //
            HttpGet httpUberdustNodesGet = new HttpGet(uberdustNodesGetRestUri);
            HttpResponse httpUberdustNodesGetResponse = httpclient.execute(httpUberdustNodesGet);

            int httpUberdustNodesGetResponseStatusCode = httpUberdustNodesGetResponse.getStatusLine().getStatusCode();
            HttpEntity httpUberdustNodesGetResponseEntity = httpUberdustNodesGetResponse.getEntity();
            if(httpUberdustNodesGetResponseEntity != null)
            {

                String responseBodyStr = EntityUtils.toString(httpUberdustNodesGetResponseEntity);
                if (httpUberdustNodesGetResponseStatusCode != 200) {
                    // responseBody will have the error response
                    logger.debug("--------ERROR Response: "+ httpUberdustNodesGetResponseStatusCode+"------------------------------");
                    logger.debug(responseBodyStr);
                    logger.debug("----------------------------------------");
                }
                else
                {
                    //logger.debug("--------OK Response: "+ httpUberdustNodesGetResponseStatusCode+"------------------------------");
                    //
                    String[] nodeUrnsInUberdust = responseBodyStr.split("\\r?\\n");
                    int totalNodeUrnsInUberdust = nodeUrnsInUberdust.length;

                    HttpGet httpUberdustNodes_STATUS_Get = new HttpGet(uberdustNodes_Status_GetRestUri);
                    HttpResponse  httpUberdustNodes_STATUS_GetResponse = httpclient.execute(httpUberdustNodes_STATUS_Get);

                    int httpUberdustNodes_STATUS_GetResponseStatusCode = httpUberdustNodes_STATUS_GetResponse.getStatusLine().getStatusCode();
                    HttpEntity httpUberdustNodes_STATUS_GetResponseEntity = httpUberdustNodes_STATUS_GetResponse.getEntity();
                    if(httpUberdustNodes_STATUS_GetResponseEntity != null)
                    {
                        String responseBody_STATUS_Str = EntityUtils.toString(httpUberdustNodes_STATUS_GetResponseEntity);
                        if (httpUberdustNodes_STATUS_GetResponseStatusCode != 200) {
                            // responseBody_STATUS_Str will have the error response
                            logger.debug("--------ERROR Response: "+ httpUberdustNodes_STATUS_GetResponseStatusCode+"------------------------------");
                            logger.debug(responseBody_STATUS_Str);
                            logger.debug("----------------------------------------");
                        }
                        else
                        {
                            //logger.debug("--------OK Response: "+ httpUberdustNodes_STATUS_GetResponseStatusCode+"------------------------------");

                            String[] nodeAndLastCapReadingsUrnsInUberdust = responseBody_STATUS_Str.split("\\r?\\n");
                            int totalNodeWithCapsInUberdust= nodeAndLastCapReadingsUrnsInUberdust.length;

                            // LOOP OVER EVERY NODE (smart device), and for each node, get its capabilities from the second response (responseBody_STATUS_Str)
                            Vector<String> allFaultyNodesUrns = getFaultyNodes();

                            for (String aNodeUrnsInUberdust : nodeUrnsInUberdust) {
                                if(allFaultyNodesUrns.contains(aNodeUrnsInUberdust))
                                {
                                    logger.debug("Skiipping node: "+aNodeUrnsInUberdust);
                                    continue;
                                }
                                Vector<VerySimpleObservationCapabilities> sensObsCapsVector = new Vector<VerySimpleObservationCapabilities>();
                                Vector<VerySimpleSensorMeasurement> sensObsMeasurementVector = new Vector<VerySimpleSensorMeasurement>();


                                if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                                {
                                    aNodeUrnsInUberdust = dictionaryUberdustUrnToHaiUrnName.get(aNodeUrnsInUberdust);
                                    if(aNodeUrnsInUberdust == null) continue;
                                }

//                                logger.debug("Iteration " + String.valueOf(k+1) + " of " + String.valueOf(totalNodeUrnsInUberdust));
//                                logger.debug(nodeUrnsInUberdust[k]);

                                Vector<Integer> sensorModels_IDs_OfSmartDevVector = new Vector<Integer>();
                                CSmartDevice tmpSmartDev = new CSmartDevice(aNodeUrnsInUberdust,
                                        "",/* smart device type name */
                                        "",/* location description e.g. room1*/
                                        new GeodesicPoint(), /*  */
                                        sensorModels_IDs_OfSmartDevVector);


                                // TODO: Add an extra early for loop to update the fields for the attributes of the SmartDevice such as:
                                //      Eventually if the SmartDev has NO other valid sensors (e.g. observation sensors or actuators) then it won't be added !
                                String tmp_longitude = "";
                                String tmp_latitude = "";
                                String tmp_altitude = "";

                                for (String aNodeAndLastCapReadingsUrnsInUberdust1 : nodeAndLastCapReadingsUrnsInUberdust) {
                                    //to update the device attributes!
                                    String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust1.split("\\t");
                                    if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                                    {
                                        nodeCapAndReadingRowItems[0] = dictionaryUberdustUrnToHaiUrnName.get(nodeCapAndReadingRowItems[0]);
                                        if(nodeCapAndReadingRowItems[0] == null) continue;
                                    }


                                   if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)       //we are at the capabilities of the current smartdevice
                                    {
                                        // [0] is mote (smart device) id
                                        // [1] is capability
                                        // [2] is timestamp
                                        // [3] is measurement value
                                        if ((nodeCapAndReadingRowItems[1] != null) && !(nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase(""))) {
                                            if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("room") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                tmpSmartDev.setLocationDesc(nodeCapAndReadingRowItems[3].trim());
                                                smartDevIdsToRooms.put(tmpSmartDev.getId(), tmpSmartDev.getLocationDesc());
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("nodetype") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                tmpSmartDev.setName(nodeCapAndReadingRowItems[3].trim());
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("description") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: do we need this?

                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("x") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: we need the function to derive a valid longitude from the uberdust value (pending)
                                                tmp_longitude = nodeCapAndReadingRowItems[3].trim();
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("y") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: we need the function to derive a valid latitude)
                                                tmp_latitude = nodeCapAndReadingRowItems[3].trim();
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("z") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //altitude is in meters (assumption)
                                                tmp_altitude = nodeCapAndReadingRowItems[3].trim();

                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("phi") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: do we need this?
                                            } else if (nodeCapAndReadingRowItems[1].trim().equalsIgnoreCase("theta") && nodeCapAndReadingRowItems[3] != null && !nodeCapAndReadingRowItems[3].trim().equalsIgnoreCase("")) {
                                                //TODO: do we need this?
                                            }
                                        }
                                    }
                                }    // end of first round of for loop for attributes
                                if (!tmp_latitude.equalsIgnoreCase("") && !tmp_longitude.equalsIgnoreCase("") && !tmp_altitude.equalsIgnoreCase("")) {
                                    tmpSmartDev.setGplocation(new GeodesicPoint(tmp_latitude, tmp_longitude, tmp_altitude));
                                }

                                //
                                // Again same loop for measurement and actuation capabilities!
                                //
                                for (String aNodeAndLastCapReadingsUrnsInUberdust : nodeAndLastCapReadingsUrnsInUberdust) {
                                    String[] nodeCapAndReadingRowItems = aNodeAndLastCapReadingsUrnsInUberdust.split("\\t");
                                    if(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg().equalsIgnoreCase("vitrogw_hai"))
                                    {
                                        nodeCapAndReadingRowItems[0] = dictionaryUberdustUrnToHaiUrnName.get(nodeCapAndReadingRowItems[0]);
                                        if(nodeCapAndReadingRowItems[0] == null) continue;
                                    }
                                    if (nodeCapAndReadingRowItems.length >3 && nodeCapAndReadingRowItems[0].compareToIgnoreCase(aNodeUrnsInUberdust) == 0)           //we are at the capabilities of the current smartdevice
                                    {
                                        // [0] is mote (smart device) id
                                        // [1] is capability
                                        // [2] is measurement value
                                        // [3] is timestamp
//                                        logger.debug(nodeCapAndReadingRowItems[1]);
                                        // TODO: FILTER OUT UNSUPPORTED OR COMPLEX CAPABILITIES!!!!
                                        // Since uberdust does not distinguish currenlty between sensing/actuating capabilities and properties, we need to filter out manually
                                        // everything we don't consider a sensing/actuating capability.
                                        // Another filtering out is done at a later stage with the SensorMLMessageAdapter, which will filter out the capabilities not supported by IDAS
                                        // TODO: it could be nice to have this filtering unified.
                                        if ((nodeCapAndReadingRowItems[1] != null) && (nodeCapAndReadingRowItems[1].trim().compareTo("") != 0)
                                                && !getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim()).equalsIgnoreCase("UnknownPhenomenon")) {
                                            // The model id is set as the hashcode of the capability name appended with the model type of the device.
                                            // Perhaps this should be changed to something MORE specific
                                            // TODO: the units should be set here as we know them for Uberdust. Create a small dictionary to set them!
                                            // TODO: the non-observation sensors/ non-actuation should be filtered here!! the Name for the others should be "UnknownPhenomenon"

                                            String tmpGenericCapabilityForSensor = getSimpleCapForUberdustUrn(nodeCapAndReadingRowItems[1].trim());
                                            Integer thedigestInt = (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()).hashCode();
                                            if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

//                                            /*
//                                            CSensorModel tmpSensorModel = new CSensorModel(givGatewayInfo.getId(), /*Gateway Id*/
//                                                    thedigestInt, /*Sensor Model Id */
//                                                    (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()), /* Sensor Model name */
//                                                    CSensorModel.numericDataType, /* Data type*/  // TODO: later on this should be adjustable!!!
//                                                    CSensorModel.defaultAccuracy, /* Accuracy */
//                                                    CSensorModel.defaultUnits) /* Units */;  // TODO: this should be set when it is known!!!

                                            // GET THE OBSERVATION
                                            VerySimpleObservationCapabilities tmpObsCap = new VerySimpleObservationCapabilities(nodeCapAndReadingRowItems[1], true);
                                            if ((tmpObsCap.getSimpleName() != null) && !(tmpObsCap.getSimpleName().equalsIgnoreCase("UnknownPhenomenon"))) {
                                                sensObsCapsVector.add(tmpObsCap);

                                                // ts of measurement in place [2]
                                                // value of measurement in place [3]
                                                // logger.debug(nodeCapAndReadingRowItems[2]+'\t'+nodeCapAndReadingRowItems[3]);
                                                long theTimeStamp = Long.parseLong(nodeCapAndReadingRowItems[2]);
                                                String theValue = nodeCapAndReadingRowItems[3];
                                                if(theValue.contains(" "))
                                                    theValue = theValue.split(" ")[0];    // if it contains the UOM as a suffix,then just keep the first part
                                                String observPropertyDef = tmpObsCap.getPhenomenonIDASUrn();
                                                String observOutputUOMCode = tmpObsCap.getUomIDASUrn();// tmpObsCap.getUomIDASCode();
                                                // just one (last) value
                                                String[] observOutputMeasurementData = new String[1];
                                                // Dummy measurement value
                                                if (tmpObsCap.getSimpleName().equalsIgnoreCase("temperature"))
                                                {
                                                    //since we assume kelvin to be the default UOM, until fixed, wew set oiur ceslious to Kelvin here:
                                                    //K = C+273 . TODO. Later on this normalization should be done at the VSP!
                                                    double d = Double.parseDouble(theValue);
                                                    double convertedKelvinValue =  d + 273.0;
                                                    String  convertedKelvinValueStr = Long.toString((long)convertedKelvinValue);

                                                    observOutputMeasurementData[0] = convertedKelvinValueStr; //to kelvin
                                                }
                                                else
                                                {
                                                    observOutputMeasurementData[0] = theValue;
                                                }
                                                // TODO: Check if timezone is correct!
                                                // FOR UBERDUST: override sensors timestamp with reply from uberdust timestamp (now)
                                                Date dateNow = new Date();
                                                theTimeStamp =  dateNow.getTime();
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                                                String observPropertyTSStr = df.format(new Date(theTimeStamp));
                                                //logger.debug("---- " + observPropertyTSStr + " ---- " + theValue + " ----------------------------");
                                                sensObsMeasurementVector.add(new VerySimpleSensorMeasurement(observPropertyDef, observOutputUOMCode, observOutputMeasurementData, observPropertyTSStr, theTimeStamp));
                                            }

                                            sensorModels_IDs_OfSmartDevVector.add(thedigestInt);

                                        }
                                    }
                                }
                                if (!sensorModels_IDs_OfSmartDevVector.isEmpty()) {
                                    // TODO: FILTER OUT UNSUPPORTED OR COMPLEX NODES!!!!
                                    VerySimpleSensorEntity sens01 = new VerySimpleSensorEntity(aNodeUrnsInUberdust, sensObsCapsVector, sensObsMeasurementVector);
                                    allSensorsWithCapsAndMeasures.add(sens01);


                                    // TODO: MAYBE HERE WE CAN CHECK IF THESE MEASUREMENTS are for the results??
                                    //if (!sensObsMeasurementVector.isEmpty())
                                    //{
                                    //    Iterator<VerySimpleSensorMeasurement> it1 = sensObsMeasurementVector.iterator();
                                    //    while( it1.hasNext())
                                    //    {
                                    //        VerySimpleSensorMeasurement sensorMeasurement = (VerySimpleSensorMeasurement)it1.next();
                                    //
                                    //    }
                                    //}
                                    //#####################################
                                }
                            }      // ends for loop over all smartdevices discovered!
                        }  //if GET STATUS response code is OK!
                    }  // if GET STATUS response entity is NOT null
                } //if get list of nodes replied validly
            } //if get list of nodes response entity is NOT null
        }
        catch(Exception e)
        {
            logger.debug("error::" + e.getMessage());

        } finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate de-allocation of all system resources
            httpclient.getConnectionManager().shutdown();
        }

        //
        // TILL HERE WE HAVE A VECTOR WITH ALL Devices and Capabilities and Measurements: allSensorsWithCapsAndMeasures
        //
        //
        // LEGACY CODE
        //
        //
        //  TODO: This vector works now, but if we integrate components for sensors, we will need the real sensormodelIds to be registered by IDAS (DVNS),
        // (for now, only the hashcode of simple capabilities are considered to be the sensormodelids,(and this "hashcode" is set to always be a positive value, so if it is negative multiply it by (-1))
        //          thus forcing all sensor models that measure the same phenomenon to be considered identical .
        //
        // Q: a MAX function means maximum among readings from a sensor of a specific mote (aggregation)
        // or among readings from ALL motes (aggregation in a higher level)
        // A: We should actually interpret it as both. Since we have a timeperiod tag in the Function XML
        //    we can assume that we want the MAX over the values we took within that specified timeperiod (from all desired motes and all readings of their corresponding sensor)
        //    e.g.  In the period (Mon to Wed) mote-1 can have 20 light values from its light sensor, mote-2 can have 34 values etc. We calculate the max over all these.
        //    However if no timeperiod tag is set, we should assume that we get the MAX over only the Latest Value per mote, and probably in relatively current timewindow (e.g. from now() until a few hours ago)
        //    e.g. mote-1's latest light value is x1, mote-2 latest light value is x2 etc., and we calculate the max over just these values.
        //
        // We have a Vectors of functions to be applied and
        // a HashMap of SmartDevices (motes) mapped to those of their sensors that should be "queries"
        //
        // The steps are::
        // 1. For each mote ID:
        //        get each sensor ID and get all readings of mote.
        // 2. Fill in a Vector of ResultAggrStucts and send them with the Vector of Functions to executeFuctOverData for each function.
        //     This array is used because we want a generic format to send data to the function
        //     and that is the XML structure of the ResultAggrStruct.
        //     Each entry in the array has a mid, sid, (datatype), value AND timeperiod (from=to=timestamp) from database).
        // 3. Return a Vector of ReqResultOverData
        //
        // Note: Levels of aggregations should be:
        //       1. Values of motes specific sensor model (from light sensor 3006) (within a period of time, or all readings from the beginning of time!!)
        //       2. Values of all motes with the same sensor model   (aggragate over the results of step 1, over the involved motes)
        //       3. Values of all motes with sensor model that belongs to the same generic capability (e.g. light sensor 2000 and light sensor 3006 ( aggregate over the results of step 2)
        //       4. Values among gateways (this can be done only at the user peer).
        //
        // !! Note: No Longer Needed: --> Parse the moteID and extract WSN_ID and SMARTDEVICE_ID info (the format is WSN_ID::SMARTDEVICE_ID)
        //
        Vector<ResultAggrStruct> vOfSensorValues;

        Vector<ReqResultOverData> retVecofResults;
        retVecofResults = new Vector<ReqResultOverData>();

        //logger.debug("Size of motesAndTheirSensorAndFunctsVec::" + Integer.toString(motesAndTheirSensorAndFunctsVec.size())  );

        for (int i = 0; i < motesAndTheirSensorAndFunctsVec.size(); i++) {

            String fullMoteId = motesAndTheirSensorAndFunctsVec.elementAt(i).getMoteid();

            // for each entry, get the vector of queried sensor types and the functions to be applied to the measurements.
            List<ReqSensorAndFunctions> tmpVecSmAndFuncList = motesAndTheirSensorAndFunctsVec.elementAt(i).getQueriedSensorIdsAndFuncVec();
//            Vector<Integer> tmpVecSmIds =  motesAndTheirSensorHM.get(fullMoteId);
            /**
             *
             *  TODO: So far we assume all of the data types in measurements to be Long! This should be fixed!!!
             *
             */
            try {

                //
                // We have the readings from all sensors.
                // we must select only the readings from the specific sensors of interest (those inside the  tmpVecSmAndFuncList vector) .
                //


                //logger.debug("Size of tmpVecSmAndFuncList::" + Integer.toString(tmpVecSmAndFuncList.size())  );
                for (ReqSensorAndFunctions currentSensorModel : tmpVecSmAndFuncList) {
                    int smid = currentSensorModel.getSensorModelIdInt();
                    int countValuesOfthisSensorModel = 0;

                    // TODO : fix to other connectors ->moved vOfSensorValues in the for loop!

                    //logger.debug("For mote "+fullMoteId +" and sensor "+Integer.toString(smid) + " function vector size is "+reqFunctionVec.size());
                    for (ReqFunctionOverData currentRequestedFunction : reqFunctionVec) {
                        vOfSensorValues = new Vector<ResultAggrStruct>();

                        if (currentRequestedFunction.getfuncId() == ReqFunctionOverData.unknownFuncId) {
                            vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId, smid, "No Result", 1, null));
                            countValuesOfthisSensorModel += 1;
                        } else if (currentSensorModel.getFunctionsOverSensorModelVec().contains(currentRequestedFunction.getfuncId())) {    // this loop (and this condition) allows to retrieve the valid "functions" to be performed on values of this sensor
                            Vector<VerySimpleSensorMeasurement> mySensorReadingsRelatedToCurrentFunction = new Vector<VerySimpleSensorMeasurement>();  // bugfix: this is now moved inside the functions loop
                            // for each different "function" on the sensor values, we may need to gather completely different values. (e.g. a function could request a history of measurements, or only measurements that are above a threshold)
                            // TODO: Separate cases for binary values (e.g. image or webcam stream) and numeric values  (and string values?)

                            // TODO: for uberdust, loop through all nodes in (http get status vector): allSensorsWithCapsAndMeasures
                            //          and keep the readings, apply functions (FOR NOW WE ALWAYS APPLY LAST READING NO MATTER WHAT)
                            // TODO: Fix -> for now we always apply last reading no matter what the actual function was (since we have no history).
                            // TODO: fix serial numbers for sensor models. They should not just be the hash on the capability simple name...
                            for (VerySimpleSensorEntity tmpSmartDev : allSensorsWithCapsAndMeasures) {
                                if (tmpSmartDev.getSerialID().equalsIgnoreCase(fullMoteId))     // first match based on the requested smart device ID
                                {
                                    for (VerySimpleSensorMeasurement tmpMeasurement : tmpSmartDev.getMeasurementsVector()) {
                                        String obsPropertyIDASUrn = tmpMeasurement.getObservPropertyDef();
                                        String obsPropertySimple = "lalala";
                                        Iterator<String> itDict = dictionaryNameToIDASPhenomenon.keySet().iterator();
                                        String tmpSimpleName;
                                        // initial loop to get the "real" simple name for the search key capability (at this poing this is not yet a valid requested sensor)
                                        // we need the simple name because we used it to calculate the sensor model id (with the hashCode() )
                                        // so we get simple names, then calc their hashCodes (turn it into a positive number if it was negative) and then compare it with the requested hashcode (smid)  (assumed positive, because the DVNS will make sure of that)
                                        // logger.debug("^^^^^^^^^^OV: "+ obsPropertyIDASUrn);
                                        while (itDict.hasNext()) {
                                            tmpSimpleName = itDict.next();
                                            //logger.debug("^^^^^^^^^^VS: "+ (dictionaryNameToIDASPhenomenon.get(tmpSimpleName)).toString());

                                            if ((staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get(tmpSimpleName))).equalsIgnoreCase(obsPropertyIDASUrn)) {
                                                //logger.debug("*** *** *** Found matching capability in dictionary:" + tmpSimpleName);
                                                obsPropertySimple = tmpSimpleName;
                                                break;
                                            }
                                        }

                                        int projectedSmIdforPropertyDef = obsPropertySimple.hashCode();
                                        if (projectedSmIdforPropertyDef < 0) {
                                            projectedSmIdforPropertyDef = projectedSmIdforPropertyDef * (-1);
                                        }

                                        if (smid == projectedSmIdforPropertyDef) {
                                            // debug:
//                                            if((tmpSimpleName.equalsIgnoreCase("switchlight1")
//                                                    ||tmpSimpleName.equalsIgnoreCase("switchlight2")
//                                                    ||tmpSimpleName.equalsIgnoreCase("switchlight3")
//                                                    ||tmpSimpleName.equalsIgnoreCase("switchlight4") )
//                                                &&
//                                            ((smartDevIdsToRooms.get(fullMoteId)!=null) && smartDevIdsToRooms.get(fullMoteId).equalsIgnoreCase("0.I.3")));
//                                                    {
//                                                logger.debug("*** *** *** ADDING A MEASUREMENT FOR: "+ tmpSimpleName + " Mote:" +fullMoteId + "Room: " + smartDevIdsToRooms.get(fullMoteId));
//                                            }
                                            mySensorReadingsRelatedToCurrentFunction.add(tmpMeasurement);

                                            break; // TODO: break since a smartdevice will not have two of the same sensor models. Can it though? in general?
                                        }
                                        //else
                                        //{
                                        //    logger.debug("*** *** *** BUT DOES NOT MATCH A requested sensor: "+ tmpSimpleName);
                                        //}
                                    }
                                    break; //since we processed  the sensor dev that we wanted.
                                }
                            }

                            //logger.debug("READINGS LENGTH:" + Integer.toString(mySensorReadingsRelatedToCurrentFunction.length) );

                            for (int o = 0; o < mySensorReadingsRelatedToCurrentFunction.size(); o++) {
                                /* TODO: (++++) this could be optimized further (not write the entire data in the vector) / first process it
                                 * according to the function.
                                 * TODO: Somewhere around here we should handle the History function (not likely for uberdust)
                                 */
                                //SensorTypes tmpSensor = jWebTypesManager.selectSensorType(smid);
                                long valueToAdd=-1;
                                //if(tmpSensor.getIsComplex() == false)
                                //{
                                // TODO: here we handle the actuation capabilities for lights as well, if a set value function was requested on them
                                // TODO: if a last value reading was requested we can handle that too by sending their state (as reported)
                                if (mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight1")))
                                        || mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight2")))
                                        || mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight3")))
                                        || mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS + (dictionaryNameToIDASPhenomenon.get("switchlight4")))
                                        ) {
                                    logger.debug("Function: " + currentRequestedFunction.getfuncName());
                                    // TODO: for now we use the threshold field to set the actuation value! Later this could be a separate parameter field
                                    if (currentRequestedFunction.getfuncName().equalsIgnoreCase(ReqFunctionOverData.setValFunc) && currentRequestedFunction.getThresholdField() != null && !currentRequestedFunction.getThresholdField().isEmpty()) {
                                        logger.debug("-------- HANDLING ACTUATION NOW! " + mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef() + " room: " + smartDevIdsToRooms.get(fullMoteId) + " mote: " + fullMoteId + " val: " + mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                        ThresholdStructure requiredThresholds = new ThresholdStructure(currentRequestedFunction.getThresholdField());
                                        if (requiredThresholds.getLowerBound() != null && !requiredThresholds.getLowerBound().isEmpty()) {
                                            logger.debug("Actuation parameter: " + requiredThresholds.getLowerBound().trim());
                                            // attempt to set the light to the desired value!
                                            // TODO: check if a valid value (0 or 1)
                                            try {
                                                String valStr =  actuateSmartDevToValue(fullMoteId, smartDevIdsToRooms.get(fullMoteId), getUberdustUrnForIDASCapName(mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef()), requiredThresholds.getLowerBound().trim());
                                                double d = Double.parseDouble(valStr);
                                                valueToAdd = (long) d;
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                valueToAdd = -1;
                                            }

                                        }
                                    }
                                    else {

                                        if(useTheStandardWayToGetStateForActuatorReading)
                                        {
                                            try {
                                                String valStr =  mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0];
                                                double d = Double.parseDouble(valStr);
                                                valueToAdd = (long) d;
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                valueToAdd = -1;
                                            }
                                        }
                                        else
                                        {
                                            String UberdustUrnForCap = getUberdustUrnForIDASCapName(mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyDef());
                                            String justtheCapName = UberdustUrnForCap.substring(staticprefixUberdustCapability.length());
                                            //TODO: this masking is just for the demo!
                                            //mask light4 capability as light5 in order to show it in the demo: (light4 is not visible from the camera's viewpoint)
                                            // Changed light4 to lz4 to reflect naming change in uberdust
                                            if(justtheCapName.equalsIgnoreCase("lz4"))
                                                justtheCapName = "lz5";
                                            String lineOfStateReading = getLatestReadingTabSepLineForVirtualNode(fullMoteId, justtheCapName);
                                            String[] lineTokens = lineOfStateReading.split("\\t");
                                            // [0] has the timestamp
                                            // [1] has the value
                                            long valueOfReturnedState;
                                            String observPropertyTSStr;
                                            long theTimeStamp = 0;
                                            try {
                                                double d = Double.parseDouble(lineTokens[1]);
                                                valueOfReturnedState = (long) d;
                                                theTimeStamp = Long.parseLong(lineTokens[0]);
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                                                observPropertyTSStr = df.format(new Date(theTimeStamp));
                                                logger.debug("Actuator state was: " + lineTokens[1] + " at: " + observPropertyTSStr);
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                                                observPropertyTSStr = df.format(new Date(theTimeStamp));
                                                valueOfReturnedState = -1;

                                            }
                                            mySensorReadingsRelatedToCurrentFunction.elementAt(o).setObservPropertyTSLong(theTimeStamp);
                                            mySensorReadingsRelatedToCurrentFunction.elementAt(o).setObservPropertyTSStr(observPropertyTSStr);
                                            mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0] = Long.toString(valueOfReturnedState);
                                            // todo: move code repetition
                                            try {
                                                String valStr =  mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0];
                                                double d = Double.parseDouble(valStr);
                                                valueToAdd = (long) d;
                                            } catch (Exception e) {
                                                //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                                valueToAdd = -1;
                                            }
                                        }
                                    }

                                } else {
                                    try {
                                        String valStr =  mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0];
                                        double d = Double.parseDouble(valStr);
                                        valueToAdd = (long) d;
                                    } catch (Exception e) {
                                        //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                                        valueToAdd = -1;
                                    }
                                }

                                long timestampOfReading = mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservPropertyTSLong();
                                Timestamp timestampOfReadingSql = new Timestamp(timestampOfReading);
                                vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId,
                                        smid,
                                        Long.toString(valueToAdd),
                                        1,
                                        new TimeIntervalStructure(timestampOfReadingSql,
                                                timestampOfReadingSql))
                                );
                                    //}
                                    //                                else// put blob value as a String (FOR NOW this is just a URL to the binary file so this is ok) (++++)
                                    //                                    // TODO: later handling of binary data will change and we should send the real binary files over pipes to the client
                                    //                                {
                                    //                                    vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId,
                                    //                                        smid,
                                    //                                        new String(myMotesSensorsReadings[o].getComplexRawData()),
                                    //                                        1,
                                    //                                        new TimeIntervalStructure(myMotesSensorsReadings[o].getDate(),
                                    //                                                myMotesSensorsReadings[o].getDate()))
                                    //                                    );
                                    //                                }

                                countValuesOfthisSensorModel += 1;
                            }
                            if (countValuesOfthisSensorModel == 0) {
                                vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId, smid, ReqResultOverData.specialValueNoReading, 1, null));
                                countValuesOfthisSensorModel += 1;
                            } else
                                logger.debug("Counted Values of this sensor: " + fullMoteId + " "+  Integer.toString(countValuesOfthisSensorModel));
                        }
                        // this condition checks that at least one value was retrieved from the sensor and used in the function (even if that value was "no result")
                        if (countValuesOfthisSensorModel > 0) // we should make sure that this is always true.
                        {
                            retVecofResults.addElement(new ReqResultOverData(currentRequestedFunction.getfuncId(), vOfSensorValues));
                        }
                    }  // ends the block where we gather values of a sensor for a specific function
                }   // ends the loop over the requested sensor Models (capabilities) of the current requested Smart Device
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }// end of for loop over all requested Smart Devices in the request vector

        // here we have a Vector filled with ResultAggrStruct

        // END OF LEGACY driver code

        return retVecofResults;
    }

    private Vector<String> getFaultyNodes(){
        if(DEBUG_OFFLINE_MODE) {
            return DEBUG_offline_getFaultyNodes();
        }

        Vector<String> retVec  = new Vector<String>();
        // direct for test http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x42f/payload/7f,69,70,1,$1,1
        HttpClient httpClient = new DefaultHttpClient();
        try {

                //logger.debug("Accessing Admin Status for faulty nodes: http://uberdust.cti.gr/rest/testbed/1/adminstatus");
                HttpGet httpUberdustFaultyNodesGet = new HttpGet("http://uberdust.cti.gr/rest/testbed/1/adminstatus");

                HttpResponse httpUberdustFaultyNodesGet_Response = httpClient.execute(httpUberdustFaultyNodesGet);
                int httpUberdustFaultyNodesGetResponse_StatusCode = httpUberdustFaultyNodesGet_Response.getStatusLine().getStatusCode();
                HttpEntity httpUberdustFaultyNodesGetResponse_ResponseEntity = httpUberdustFaultyNodesGet_Response.getEntity();
                if(httpUberdustFaultyNodesGetResponse_ResponseEntity != null)
                {

                    String responseBodyStr = EntityUtils.toString(httpUberdustFaultyNodesGetResponse_ResponseEntity);
                    if (httpUberdustFaultyNodesGetResponse_StatusCode != 200) {
                        // responseBody will have the error response
                        logger.debug("--------ERROR Response: "+ httpUberdustFaultyNodesGetResponse_StatusCode+"------------------------------");
                        logger.debug(responseBodyStr);
                        logger.debug("----------------------------------------");
                    }
                    else
                    {
                        String[] faultyNodeUrnsLinesInUberdust = responseBodyStr.split("\\r?\\n");
                        int totalfaultyNodeUrnsLinesInUberdust = faultyNodeUrnsLinesInUberdust.length;
                        for (String afaultyNodeUrnLineInUberdust : faultyNodeUrnsLinesInUberdust) {
                            String[] afaultyNodeUrnTokens = afaultyNodeUrnLineInUberdust.split("\\t");
                            // [0] has the urn
                            if(afaultyNodeUrnTokens!=null && afaultyNodeUrnTokens.length>0 && !afaultyNodeUrnTokens[0].isEmpty())
                            {
                                retVec.addElement(afaultyNodeUrnTokens[0]);
                          //      logger.debug("Adding faulty node:: "+afaultyNodeUrnTokens[0]);
                            }

                        }

                    }
                }
        }
        catch (Exception e)
        {
            logger.debug(e.getMessage());
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate de-allocation of all system resources
            httpClient.getConnectionManager().shutdown();
        }
        return retVec;
    }


    private String getLatestReadingTabSepLineForVirtualNode(String moteId, String justtheCapName)
    {
        String retValStr ="timestamp\t-1\t0";
        int retryInterval = 1;
        //HttpClient httpClient = new DefaultHttpClient();
        try {
            //Arduino state reading: http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:0x42f/capability/urn:wisebed:node:capability:light1/latestreading
            // response is Timestamp\tvalue (e.g. 1337847892000	0.0)
                //logger.debug("Getting Actuator Latest Reading with Coap!");
                // retry at least 3 times until you get a response!
                if(justtheCapName.equalsIgnoreCase("lz4"))
                {
                    justtheCapName = "lz5";
                }
                BasicCoapClient client = new BasicCoapClient();
                client.channelManager = BasicCoapChannelManager.getInstance();
                int readStatusRetries = 3;
                boolean gotReadStatusReply = false;
                String keyForMsgsCheck = moteId+"::"+justtheCapName;
                while(readStatusRetries > 0  && !gotReadStatusReply)
                {
                    client.coapGetActuatorStatus(moteId, justtheCapName);
                    try{
                        Thread.currentThread().sleep(retryInterval*1000);//sleep for predefined number of seconds before retrying
                    }
                    catch(InterruptedException ie){
                        //If this thread was interrupted by another thread
                    }
                    if(urnNodeToCoapReply.containsKey(keyForMsgsCheck)&& urnNodeToCoapReply.get(keyForMsgsCheck)!=null && !urnNodeToCoapReply.get(keyForMsgsCheck).isEmpty())
                    {
                        gotReadStatusReply = true;
                        String repliedStatus = urnNodeToCoapReply.get(keyForMsgsCheck);
                        //logger.debug("READ JCOAP LAST VALUE STATUS::" + repliedStatus);
                        // TODO handle read Status case.
                        retValStr = repliedStatus.trim();
                        retValStr = Long.toString(System.currentTimeMillis())+"\t"+retValStr+"\t"+retValStr;
                        urnNodeToCoapReply.remove(keyForMsgsCheck);
                    }

                    readStatusRetries--;
                }
                if(!gotReadStatusReply)
                {
                    // TODO handle could not read status after retries
                    retValStr = Long.toString(System.currentTimeMillis())+"\t-1\t0";
                }

            /*
            logger.debug("Getting: http://uberdust.cti.gr/rest/testbed/1/node/"+moteId+"/capability/"+staticprefixUberdustCapability+justtheCapName+"/latestreading");
            HttpGet httpUberdustActuatorStateGet = new HttpGet("http://uberdust.cti.gr/rest/testbed/1/node/"+moteId+"/capability/"+staticprefixUberdustCapability+justtheCapName+"/latestreading");

            HttpResponse httpUberdustActuatorStateGet_Response = httpClient.execute(httpUberdustActuatorStateGet);

            int httpUberdustActuatorStateGet_Response_StatusCode = httpUberdustActuatorStateGet_Response.getStatusLine().getStatusCode();
            HttpEntity httpUberdustActuatorStateGet_ResponseEntity = httpUberdustActuatorStateGet_Response.getEntity();
            if(httpUberdustActuatorStateGet_ResponseEntity != null)
            {

                String actState_responseBodyStr = EntityUtils.toString(httpUberdustActuatorStateGet_ResponseEntity);
                if (httpUberdustActuatorStateGet_Response_StatusCode != 200) {
                    // responseBody will have the error response
                    logger.debug("--------ERROR Response: "+ httpUberdustActuatorStateGet_Response_StatusCode+"------------------------------");
                    logger.debug(actState_responseBodyStr);
                    logger.debug("----------------------------------------");
                }
                else
                {
//                                    logger.debug("--------OK Response: "+ httpUberdustActuatorStateGet_Response_StatusCode+"------------------------------");
                    String[] responseLines = actState_responseBodyStr.split("\\r?\\n");
                    if (responseLines.length > 0 )
                    {
                        retValStr = responseLines[0];
                    }
                }
            }
            */
        }
        catch (Exception e)
        {
            logger.debug(e.getMessage());
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate de-allocation of all system resources
            //httpClient.getConnectionManager().shutdown();
        }
        return retValStr;
    }

    private String actuateSmartDevToValue(String moteId, String roomName, String UberdustUrnForCap, String setVal)
    {
        String retValStr ="-1";
        final int totalRetries = 4;
        int numOfRetries = 0;
        int retryInterval= 2; //in seconds

        boolean valueWasSet = false;

        //example (careful with these, these actually work)
        //Arduino state reading: http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:0x42f/capability/urn:wisebed:node:capability:light1/latestreading
        //SetState by virtual node: http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:0.I.3/capability/light1/insert/timestamp/1/reading/1/

        // direct for test http://uberdust.cti.gr/rest/sendCommand/destination/urn:wisebed:ctitestbed:0x42f/payload/7f,69,70,1,$1,1
        //HttpClient httpClient = new DefaultHttpClient();
        try {

            // TODO: infer the virtual node and simple capability name!
            String justtheCapName = UberdustUrnForCap.substring(staticprefixUberdustCapability.length());

            //TODO: this masking is just for the demo!
            //mask light4 capability as light5 in order to show it in the demo: (light4 is not visible from the camera's viewpoint)
            // Changed light4 to lz4 to reflect naming change in uberdust
            if(justtheCapName.equalsIgnoreCase("lz4"))
                justtheCapName = "lz5";

            while(numOfRetries < totalRetries && !valueWasSet)
            {
                //logger.debug("Actuation Attempt no: " + Long.toString(numOfRetries +1));
                //logger.debug("Inserting: http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:"+roomName+"/capability/"+justtheCapName+"/insert/timestamp/1/reading/"+setVal+"/");
                //HttpGet httpUberdustActuatorInsertGet = new HttpGet("http://uberdust.cti.gr/rest/testbed/1/node/urn:wisebed:ctitestbed:virtual:"+roomName+"/capability/"+justtheCapName+"/insert/timestamp/1/reading/"+setVal+"/");
                // Sending direct command:
                //logger.debug("Start CoAP Client");
                BasicCoapClient client = new BasicCoapClient();
                client.channelManager = BasicCoapChannelManager.getInstance();
                int readStatusRetries = 3;
                boolean gotReadStatusReply = false;
                String keyForMsgsCheck = moteId+"::"+justtheCapName;
                while(readStatusRetries > 0  && !gotReadStatusReply)
                {
                    client.coapGetActuatorStatus(moteId, justtheCapName);
                    try{
                        Thread.currentThread().sleep(retryInterval*1000);//sleep for predefined number of seconds before retrying
                    }
                    catch(InterruptedException ie){
                        //If this thread was interrupted by another thread
                    }
                    if(urnNodeToCoapReply.containsKey(keyForMsgsCheck)&& urnNodeToCoapReply.get(keyForMsgsCheck)!=null && !urnNodeToCoapReply.get(keyForMsgsCheck).isEmpty())
                    {
                        gotReadStatusReply = true;
                        String repliedStatus = urnNodeToCoapReply.get(keyForMsgsCheck);
                        //logger.debug("GOT JCOAP REPLY STATUS::" + repliedStatus);
                        // TODO handle read Status case.
                        retValStr = repliedStatus.trim();
                        if(repliedStatus.trim().equalsIgnoreCase(setVal))
                        {
                            //logger.debug("THE VALUE WAS SET!" + repliedStatus);
                            valueWasSet = true;
                        }
                        urnNodeToCoapReply.remove(keyForMsgsCheck);
                    }

                    readStatusRetries--;
                }
                if(!gotReadStatusReply)
                {
                    // TODO handle could not read status after retries
                }
                //
                //System.out.print("Setting actuation value!");
                // TODO : check with previous status if able to read it
                if(!valueWasSet)
                {
                    client.coapSetActuatorValue(Integer.parseInt(setVal),moteId,justtheCapName);

                    numOfRetries +=1;
                    try{
                        //introduce some delay because the virtual actuator node cannot handle messages being sent so fast (if there are messages for the other actuators it controls)
                        // TODO: we could use some logic to make it wait only if there are other actuators of the same smartdevice to be tasked to do something
                        // TODO alternatively this is going to be masked by the whole wait and then check for the actuator's status to confirm that the action was performed.
                        Thread.currentThread().sleep(retryInterval*1000);//sleep for predefined number of seconds before retrying
                    }
                    catch(InterruptedException ie){
                        //If this thread was interrupted by another thread
                    }
                }
                /*
             Matcher m = Pattern.compile("\\d+$").matcher(justtheCapName);
             int whichLightNum = -1;
             while(m.find()) {
                 whichLightNum = Integer.parseInt(m.group());
             }
             logger.debug("Directly Inserting: http://uberdust.cti.gr/rest/sendCommand/destination/"+moteId+"/payload/7f,69,70,1,"+Integer.toString(whichLightNum)+","+setVal);
             HttpGet httpUberdustActuatorInsertGet = new HttpGet("http://uberdust.cti.gr/rest/sendCommand/destination/"+moteId+"/payload/7f,69,70,1,"+Integer.toString(whichLightNum)+","+setVal);

             HttpResponse httpUberdustActuatorInsertGet_Response = httpClient.execute(httpUberdustActuatorInsertGet);
             int httpUberdustActuatorInsertGetResponse_StatusCode = httpUberdustActuatorInsertGet_Response.getStatusLine().getStatusCode();
             HttpEntity httpUberdustActuatorInsertGetResponse_ResponseEntity = httpUberdustActuatorInsertGet_Response.getEntity();
             if(httpUberdustActuatorInsertGetResponse_ResponseEntity != null)
             {

                 String responseBodyStr = EntityUtils.toString(httpUberdustActuatorInsertGetResponse_ResponseEntity);
                 if (httpUberdustActuatorInsertGetResponse_StatusCode != 200) {
                     // responseBody will have the error response
                     logger.debug("--------ERROR Response: "+ httpUberdustActuatorInsertGetResponse_StatusCode+"------------------------------");
                     logger.debug(responseBodyStr);
                     logger.debug("----------------------------------------");
                 }
                 else
                 {
//                  logger.debug("--------OK Response: "+ httpUberdustActuatorInsertGetResponse_StatusCode+"------------------------------");
                     String lineOfStateReading = getLatestReadingTabSepLineForVirtualNode(moteId, justtheCapName);

                     String[] lineTokens = lineOfStateReading.split("\\t");
                     // [0] has the timestamp
                     // [1] has the value
                     long valueOfReturnedState;
                     long valueToSet;
                     try {
                         double d = Double.parseDouble(lineTokens[1]);
                         valueOfReturnedState = (long) d;
                         valueToSet = Long.parseLong(setVal);
                         long theTimeStamp = Long.parseLong(lineTokens[0]);
                         DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSZ");
                         String observPropertyTSStr = df.format(new Date(theTimeStamp));
                         logger.debug("Actuator state was: " + lineTokens[1] + " at: " + observPropertyTSStr);
                         // set or update the return value
                         retValStr = lineTokens[1];

                     } catch (Exception e) {
                         //logger.debug("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadingsRelatedToCurrentFunction.elementAt(o).getObservOutputMeasurementData()[0]);
                         valueOfReturnedState = -1;
                         valueToSet = -1; // but should not be considered equal to the awry valueOfState

                     }
                     if(valueOfReturnedState == valueToSet && valueOfReturnedState!=-1 && valueToSet!=-1)
                     {
                         numOfRetries = totalRetries; // but do not break. Honor the wait!
                     }

                     numOfRetries +=1;
                     try{
                         //introduce some delay because the virtual actuator node cannot handle messages being sent so fast (if there are messages for the other actuators it controls)
                         // TODO: we could use some logic to make it wait only if there are other actuators of the same smartdevice to be tasked to do something
                         // TODO alternatively this is going to be masked by the whole wait and then check for the actuator's status to confirm that the action was performed.
                         Thread.currentThread().sleep(retryInterval*1000);//sleep for predefined number of seconds before retrying
                     }
                     catch(InterruptedException ie){
                         //If this thread was interrupted by another thread
                     }
                 }
             }   */
            } // end of while  (num of retries loop)

        }
        catch (Exception e)
        {
                logger.debug(e.getMessage());
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate de-allocation of all system resources
           // httpClient.getConnectionManager().shutdown();
        }
        return retValStr;
    }

    //
    // TODO: THIS SHOULD LEAVE FROM HERE!!
    //
    public class VerySimpleSensorEntity {

        private String serialID;
        private String[] inputNames;
        private String[] inputDefs;
        private String[] outputNames;
        private String[] outputDefs;
        private String[] outputUOMsHref;
        private String[] outputUOMCode;
        private String[] outputQuantityDef;
        private Vector<VerySimpleSensorMeasurement> myMeasurements;

        public VerySimpleSensorEntity(String pSerialID, Vector<VerySimpleObservationCapabilities> pObsCapsVector, Vector<VerySimpleSensorMeasurement> pMeasurementsVector) {
            this.setSerialID(pSerialID);
            if (pObsCapsVector != null && pObsCapsVector.size() > 0) {
                int vecSize = pObsCapsVector.size();
                this.setInputNames(new String[vecSize]);
                this.setInputDefs(new String[vecSize]);
                this.setOutputNames(new String[vecSize]);
                this.setOutputDefs(new String[vecSize]);
                this.setOutputUOMsHref(new String[vecSize]);
                this.setOutputQuantityDef(new String[vecSize]);
                this.setOutputUOMCode(new String[vecSize]);
                for (int i = 0; i < pObsCapsVector.size(); i++) {
                    String simpleCapName = pObsCapsVector.get(i).getSimpleName().toLowerCase();
                    String simpleUomName = pObsCapsVector.get(i).getUomSimpleName().toLowerCase();
                    this.getInputNames()[i] = simpleCapName;
                    this.getInputDefs()[i] = pObsCapsVector.get(i).getPhenomenonIDASUrn();
                    this.getOutputNames()[i] = simpleCapName;
                    this.getOutputDefs()[i] = pObsCapsVector.get(i).getPhenomenonIDASUrn();
                    this.getOutputUOMsHref()[i] = pObsCapsVector.get(i).getUomIDASUrn();
                    this.getOutputQuantityDef()[i] = pObsCapsVector.get(i).getPhenomenonIDASUrn();
                    this.getOutputUOMCode()[i] = pObsCapsVector.get(i).getUomIDASUrn(); //getUomIDASCode();
                }
            }
            this.myMeasurements = pMeasurementsVector;
        }

        public Vector<VerySimpleSensorMeasurement> getMeasurementsVector() {
            return this.myMeasurements ;
        }

        public String getSerialID() {
            return serialID;
        }

        public void setSerialID(String serialID) {
            this.serialID = serialID;
        }

        public String[] getInputNames() {
            return inputNames;
        }

        public void setInputNames(String[] inputNames) {
            this.inputNames = inputNames;
        }

        public String[] getInputDefs() {
            return inputDefs;
        }

        public void setInputDefs(String[] inputDefs) {
            this.inputDefs = inputDefs;
        }

        public String[] getOutputNames() {
            return outputNames;
        }

        public void setOutputNames(String[] outputNames) {
            this.outputNames = outputNames;
        }

        public String[] getOutputDefs() {
            return outputDefs;
        }

        public void setOutputDefs(String[] outputDefs) {
            this.outputDefs = outputDefs;
        }

        public String[] getOutputUOMsHref() {
            return outputUOMsHref;
        }

        public void setOutputUOMsHref(String[] outputUOMsHref) {
            this.outputUOMsHref = outputUOMsHref;
        }

        public String[] getOutputQuantityDef() {
            return outputQuantityDef;
        }

        public void setOutputQuantityDef(String[] outputQuantityDef) {
            this.outputQuantityDef = outputQuantityDef;
        }

        public String[] getOutputUOMCode() {
            return outputUOMCode;
        }

        public void setOutputUOMCode(String[] outputUOMCode) {
            this.outputUOMCode = outputUOMCode;
        }
    }

    /**
     * private class for storing measurements from the get request
     */
    public static class VerySimpleSensorMeasurement {

        private String observOutputUOMCode;
        private String[] observOutputMeasurementData;
        private String observPropertyDef;
        // Should be in ISO8601 e.g 2011-06-29T18:40:06.000000Z
        private String observPropertyTSStr;
        private long observPropertyTSLong;

        public VerySimpleSensorMeasurement(String pObservPropertyDef, String pObservOutputUOMCode, String[] pObservOutputMeasurementData, String pObservPropertyTSStr, long pObservPropertyTSLong) {
            this.setObservPropertyDef(pObservPropertyDef);
            this.setObservOutputUOMCode(pObservOutputUOMCode);
            this.setObservOutputMeasurementData(pObservOutputMeasurementData);
            this.setObservPropertyTSStr(pObservPropertyTSStr);
            this.setObservPropertyTSLong(pObservPropertyTSLong);
        }


        public String getObservOutputUOMCode() {
            return staticprefixUOMIDAS + observOutputUOMCode;
        }

        public void setObservOutputUOMCode(String observOutputUOMCode) {
            this.observOutputUOMCode = observOutputUOMCode;
        }

        public String[] getObservOutputMeasurementData() {
            return observOutputMeasurementData;
        }

        public void setObservOutputMeasurementData(String[] observOutputMeasurementData) {
            this.observOutputMeasurementData = observOutputMeasurementData;
        }

        public String getObservPropertyDef() {
            return observPropertyDef;
        }

        public void setObservPropertyDef(String observPropertyDef) {
            this.observPropertyDef = observPropertyDef;
        }

        public String getObservPropertyTSStr() {
            return observPropertyTSStr;
        }

        public void setObservPropertyTSStr(String observPropertyTSStr) {
            this.observPropertyTSStr = observPropertyTSStr;
        }


        public long getObservPropertyTSLong() {
            return observPropertyTSLong;
        }

        public void setObservPropertyTSLong(long pObservPropertTSLong) {
            this.observPropertyTSLong = pObservPropertTSLong;
        }

    }

    public class VerySimpleObservationCapabilities {
        //Capability Semantic Description
        // e.g. http://dbpedia.org/resource/Luminance

        private String simpleName;
        private String uomSimpleName;

        public VerySimpleObservationCapabilities(String pSimpleName, String pUomSimpleName) {
            this.setSimpleName(pSimpleName);
            this.setUomSimpleName(pUomSimpleName);
        }

        // TODO: to be put in inherited class!
        public VerySimpleObservationCapabilities(String pName, boolean isUberdustFormat) {
            if (isUberdustFormat) {
                this.setSimpleName(getSimpleCapForUberdustUrn(pName));
            } else {
                this.setSimpleName(pName);
            }
            this.setUomSimpleName(this.getSimpleUomForCap());
        }


        public String getSimpleName() {
            return simpleName;
        }

        public void setSimpleName(String simpleName) {
            this.simpleName = simpleName;
        }

        public String getUomSimpleName() {
            return uomSimpleName;
        }

        public void setUomSimpleName(String uomSimpleName) {
            this.uomSimpleName = uomSimpleName;
        }



        // custom creation of URN For IDAS phenomena
        public String getPhenomenonIDASUrn() {
            //
            //
            //
            String retVal = "UnknownPhenomenon";
            try{
                if(dictionaryNameToIDASPhenomenon.containsKey(this.getSimpleName()) )
                {
                    retVal = staticprefixPhenomenonIDAS +  dictionaryNameToIDASPhenomenon.get(this.getSimpleName());
                }
                else
                {
                    logger.debug("Error: Unsupported Phenomenon: " + this.getSimpleName());
                }
            }
            catch(Exception e)
            {
                logger.debug("Error: Unsupported Phenomenon: " + this.getSimpleName());

            }
            return retVal;
        }

        // custom creation of URN For IDAS UOMs
        // TODO: since we removed the code field this returns the full id for the
        public String getUomIDASUrn() {
            String retVal = "UnknownUomUrn";
            try {
                if(dictionaryUomToIDASUom.containsKey(this.getUomSimpleName()) )
                {
                    retVal = staticprefixUOMIDAS +  dictionaryUomToIDASUom.get(this.getUomSimpleName());
                }
            } catch (Exception e) {
                //logger.debug("Error: Unsupported Uom: " + this.getUomSimpleName());
            }
            return retVal;
        }

        public String getSimpleUomForCap() {
            String retVal = "UnknownUom";
            try {
                if(dictionarySimplePhenomenonToUom.containsKey(this.getUomSimpleName()) )
                {
                    retVal =  dictionarySimplePhenomenonToUom.get(this.getUomSimpleName());
                }
            } catch (Exception e) {
                // logger.debug("Error: Unsupported Simple mapping to Uom for capability: " + this.getSimpleName());
            }
            return retVal;
        }


    }

    //
    // SOME NEEDED static dictionaries and functions
    //
    private static String staticprefixPhenomenonIDAS = "urn:x-ogc:def:phenomenon:IDAS:1.0:";
    private static String staticprefixUOMIDAS = "urn:x-ogc:def:uom:IDAS:1.0:";
//    private static String staticprefixUberdustCapability = "urn:wisebed:node:capability:";
    static HashMap<String, String> dictionaryNameToIDASPhenomenon;

    static {
        // TODO: for now remove the class PhenomenonIDAS and keep only the corresponding name
        dictionaryNameToIDASPhenomenon = new HashMap<String, String>();   // new HashMap<String, PhenomenonIDAS>();
        dictionaryNameToIDASPhenomenon.put("temperature", "temperature" ); // new PhenomenonIDAS("temperature", 1, "Temperatura"));
        dictionaryNameToIDASPhenomenon.put("humidity", "relativeHumidity" ); // new PhenomenonIDAS("relativeHumidity", 3, "Humedad relativa"));
        //dictionaryNameToIDASPhenomenon.put("light", "solarRadiation" ); // new PhenomenonIDAS("solarRadiation", 18, "Radiación solar" ) ); // TODO: is this correct ? solarRadiation == light ?
        dictionaryNameToIDASPhenomenon.put("light", "luminousIntensity" ); // new PhenomenonIDAS("luminousIntensity", -1, "light"));
        dictionaryNameToIDASPhenomenon.put("windspeed", "windSpeed" ); // new PhenomenonIDAS("windSpeed", 7, "Concentración de CO2"));
        dictionaryNameToIDASPhenomenon.put("co", "COConcentration" ); // new PhenomenonIDAS("COConcentration", 16, "Concentración de CO"));
        dictionaryNameToIDASPhenomenon.put("co2", "CO2Concentration" ); // new PhenomenonIDAS("CO2Concentration", 151, "Velocidad del viento"));
        //dictionaryNameToIDASPhenomenon.put("ir", "" ); // "");
        dictionaryNameToIDASPhenomenon.put("pressure", "pressure" ); // new PhenomenonIDAS("pressure", 8, "Pressure"));
        dictionaryNameToIDASPhenomenon.put("barometricpressure", "atmosphericPressure" ); // new PhenomenonIDAS("atmosphericPressure", 9, "Presión atmosférica"));
        // todo remove after demos. These are not supported by IDAS
        dictionaryNameToIDASPhenomenon.put("switchlight1", "switchlight1" ); // ;
        dictionaryNameToIDASPhenomenon.put("switchlight2", "switchlight2" ); // ;
        dictionaryNameToIDASPhenomenon.put("switchlight3", "switchlight3" ); // ;
        dictionaryNameToIDASPhenomenon.put("switchlight4", "switchlight4" ); // ;
    }

    static HashMap<String, String> dictionaryUomToIDASUom;
    static {
        // TODO: for now remove the class UomIDAS and keep only the corresponding name
        dictionaryUomToIDASUom = new HashMap<String, String>(); // new HashMap<String, UomIDAS>();
        dictionaryUomToIDASUom.put("kelvin", "kelvin" ); //new UomIDAS("celsius", 12, "Cel", "Grados Centígrados"));
        dictionaryUomToIDASUom.put("celsius", "celsius" ); //new UomIDAS("celsius", 12, "Cel", "Grados Centígrados"));
        dictionaryUomToIDASUom.put("percent", "percent" ); // new UomIDAS("percent", 11, "%", "Humedad relativa"));
        dictionaryUomToIDASUom.put("candela", "candela" ); // new UomIDAS("candle", 10, "cd", "Candela (intensidad lumínica)"));
        dictionaryUomToIDASUom.put("pascal", "pascal" ); // new UomIDAS("pascal", 16, "Pa", "Pressure"));
        dictionaryUomToIDASUom.put("hectoPascal", "hectoPascal" ); // new UomIDAS("hectoPascal", 17, "hPa", "Presión"));
        dictionaryUomToIDASUom.put("poundPerSquareInch", "poundPerSquareInch" ); // new UomIDAS("poundPerSquareInch", 33, "[psi]", "Pressure"));
        dictionaryUomToIDASUom.put("millimetersPerSquareMeter", "millimetersPerSquareMeter" ); // new UomIDAS("millimetersPerSquareMeter", 18, "mm/m2", "Precipitación, pluviosidad"));
        dictionaryUomToIDASUom.put("millimeters", "millimeters" ); // new UomIDAS("millimeters", 19, "mm", "Milímetros"));
        dictionaryUomToIDASUom.put("partsPerMillion", "partsPerMillion" ); // new UomIDAS("partsPerMillion", 20, "[ppm]", "Concentration"));
        dictionaryUomToIDASUom.put("partsPerBillion", "partsPerBillion" ); // new UomIDAS("partsPerBillion", 21, "[ppb]", "Concentración"));
        dictionaryUomToIDASUom.put("dimensionless", "dimensionless" ); // new UomIDAS("dimensionless", 32, "", "No unidad"));
    }

    static HashMap<String, String> dictionaryNameToSemanticDescription;
    static {
        dictionaryNameToSemanticDescription = new HashMap<String, String>();
        dictionaryNameToSemanticDescription.put("temperature", "http://dbpedia.org/resource/Temperature");
        dictionaryNameToSemanticDescription.put("humidity", "http://dbpedia.org/resource/Humidity");
        //dictionaryNameToSemanticDescription.put("light", "http://dbpedia.org/resource/Luminance");
        //dictionaryNameToSemanticDescription.put("windspeed", "");
        //dictionaryNameToSemanticDescription.put("co", "");
        //dictionaryNameToSemanticDescription.put("co2", "http://dbpedia.org/resource/CO2");
        //dictionaryNameToSemanticDescription.put("ir", "");
        //dictionaryNameToSemanticDescription.put("windspeed", "");
        //dictionaryNameToSemanticDescription.put("pressure", "");
        //dictionaryNameToSemanticDescription.put("barometricpressure", "");
    }

    static HashMap<String, String> dictionarySimplePhenomenonToUom;
    static {
        dictionarySimplePhenomenonToUom = new HashMap<String, String>();
        dictionarySimplePhenomenonToUom.put("temperature", "kelvin");
        dictionarySimplePhenomenonToUom.put("humidity", "percent");
        dictionarySimplePhenomenonToUom.put("light", "candela");
        dictionarySimplePhenomenonToUom.put("windspeed", "dimensionless");    // TODO: set as dimensionless for now    ( dimensionless 32 No unidad)
        dictionarySimplePhenomenonToUom.put("co", "dimensionless");
        dictionarySimplePhenomenonToUom.put("co2", "dimensionless");
        //dictionarySimplePhenomenonToUom.put("ir", "dimensionless");
        dictionarySimplePhenomenonToUom.put("pressure", "dimensionless");
        dictionarySimplePhenomenonToUom.put("barometricpressure", "dimensionless");
        dictionarySimplePhenomenonToUom.put("switchlight1", "dimensionless");
        dictionarySimplePhenomenonToUom.put("switchlight2", "dimensionless");
        dictionarySimplePhenomenonToUom.put("switchlight3", "dimensionless");
        dictionarySimplePhenomenonToUom.put("switchlight4", "dimensionless");
    }


    public static String getStaticprefixPhenomenonIDAS() {
        return staticprefixPhenomenonIDAS;
    }

    public static void setStaticprefixPhenomenonIDAS(String pPrefixPhenomenonIDAS) {
        staticprefixPhenomenonIDAS = pPrefixPhenomenonIDAS;
    }

    public static String getStaticprefixUOMIDAS() {
        return staticprefixUOMIDAS;
    }

    public static void setStaticprefixUOMIDAS(String pPrefixUOMIDAS) {
        staticprefixUOMIDAS = pPrefixUOMIDAS;
    }

    public static String getStaticprefixUberdustCapability() {
        return staticprefixUberdustCapability;
    }

    public static void setStaticprefixUberdustCapability(String pPrefixUberdustCapability) {
        staticprefixUberdustCapability = pPrefixUberdustCapability;
    }


    static Map<String, String> dictionaryUberdustUrnToHaiUrnName;
    static
    {
        dictionaryUberdustUrnToHaiUrnName = new HashMap<String, String>();
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x1bee", "HAI:::01");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x1c62", "HAI:::02");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x4f22", "HAI:::03");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x68d1", "HAI:::04");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x6f58", "HAI:::05");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x712", "HAI:::06");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0x9112", "HAI:::07");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0xe852", "HAI:::08");
        dictionaryUberdustUrnToHaiUrnName.put(WsiUberDustCon.staticprefixNode+"0xf042", "HAI:::09");
    }

    static Map<String, String> dictionaryHaiUrnToUberdustUrnName;
    static
    {
        dictionaryHaiUrnToUberdustUrnName = new HashMap<String, String>();
        dictionaryHaiUrnToUberdustUrnName.put("HAI:::01" , WsiUberDustCon.staticprefixNode+"0x1bee");
        dictionaryHaiUrnToUberdustUrnName.put("HAI:::02" ,WsiUberDustCon.staticprefixNode+"0x1c62");
        dictionaryHaiUrnToUberdustUrnName.put("HAI:::03" , WsiUberDustCon.staticprefixNode+"0x4f22");
        dictionaryHaiUrnToUberdustUrnName.put("HAI:::04" , WsiUberDustCon.staticprefixNode+"0x68d1");
        dictionaryHaiUrnToUberdustUrnName.put("HAI:::05" , WsiUberDustCon.staticprefixNode+"0x6f58");
        dictionaryUberdustUrnToHaiUrnName.put("HAI:::06", WsiUberDustCon.staticprefixNode+"0x712");
        dictionaryUberdustUrnToHaiUrnName.put("HAI:::07", WsiUberDustCon.staticprefixNode+"0x9112");
        dictionaryUberdustUrnToHaiUrnName.put("HAI:::08", WsiUberDustCon.staticprefixNode+"0xe852");
        dictionaryUberdustUrnToHaiUrnName.put("HAI:::09", WsiUberDustCon.staticprefixNode+"0xf042");
    }


    private class  BasicCoapClient implements CoapClient {
        private static final String SERVER_ADDRESS_STATUS = WsiUberDustCon.coapUrnUberDustForGettingActuatorStatus ;// "localhost";
        private static final String SERVER_ADDRESS_POST = WsiUberDustCon.coapUrnUberDustForSettingActuators;// "localhost";
        private static final int PORT = WsiUberDustCon.coapUberdustPort;// Constants.COAP_DEFAULT_PORT;
        int counter = 0;
        CoapChannelManager channelManager = null;
        CoapClientChannel clientChannel = null;


//        public static void main(String[] args) {
//            logger.debug("Start CoAP Client");
//            BasicCoapClient client = new BasicCoapClient();
//            client.channelManager = BasicCoapChannelManager.getInstance();
//            client.runTestClient();
//        }
        // TODO: do we have time outs for these Coap requests?!
        public void coapGetActuatorStatus(String nodeurn, String justtheCapName){
            try {
                // 1. get hex addr for nodeurn
                // 2. get simple capabilityUrn
                String hexNodeAddr = nodeurn.substring(staticprefixNode.length());
                hexNodeAddr = hexNodeAddr.substring("0x".length());

                clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS_STATUS), PORT);
                CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
        //                      coapRequest.setContentType(CoapMediaType.octet_stream);
        //                      coapRequest.setToken("ABCD".getBytes());
                              coapRequest.setUriHost(hexNodeAddr);
        //                      coapRequest.setUriPort(1234);
                              coapRequest.setUriPath("/"+justtheCapName);
        //                      coapRequest.setUriQuery("a=1&b=2&c=3");
        //                      coapRequest.setProxyUri("http://proxy.org:1234/proxytest");
                clientChannel.sendMessage(coapRequest);
                //logger.debug("uriHost:" +coapRequest.getUriHost() ) ;
                //logger.debug("uriPath:" +coapRequest.getUriPath() ) ;
                //logger.debug("clientChannel:" +clientChannel.getRemoteAddress().getHostAddress() +"::"+ Integer.toString(clientChannel.getRemotePort())) ;
                //logger.debug("Sent Request. MessageId: " + Integer.toString(coapRequest.getMessageID()) );
                coapRequestMessageIdToUrnNode.put(Integer.toString(coapRequest.getMessageID()), nodeurn+"::"+justtheCapName);
                //urnNodeToCoapReply.put(nodeurn+"status", "");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        public void coapSetActuatorValue(int onOff, String nodeurn, String justtheCapName) {
            try {
                clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS_POST), PORT);
                String hexNodeAddr = nodeurn.substring(staticprefixNode.length());
                hexNodeAddr = hexNodeAddr.substring("0x".length());

                CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.POST);
                coapRequest.setPayload(Integer.toString(onOff));
                //coapRequest.setUriPath("/"+nodeurn + "/"+ fullUrnUberdustCapName);  //no longer used.
                coapRequest.setUriHost(hexNodeAddr);
                coapRequest.setUriPath("/"+justtheCapName);
                clientChannel.sendMessage(coapRequest);
                //logger.debug("Sent Request for setting value of " +nodeurn+ " cap: "+justtheCapName+ " to: "+Integer.toString(onOff)  + " MessageId: " + Integer.toString(coapRequest.getMessageID()) );
                // TODO: the server will send back a response with the changed value here! We could use it as well (?)
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

//        public void runTestClient(){
//            try {
//                clientChannel = channelManager.connect(this, InetAddress.getByName(SERVER_ADDRESS), PORT);
//                CoapRequest coapRequest = clientChannel.createRequest(true, CoapRequestCode.GET);
////                      coapRequest.setContentType(CoapMediaType.octet_stream);
////                      coapRequest.setToken("ABCD".getBytes());
////                      coapRequest.setUriHost("123.123.123.123");
////                      coapRequest.setUriPort(1234);
////                      coapRequest.setUriPath("/sub1/sub2/sub3/");
////                      coapRequest.setUriQuery("a=1&b=2&c=3");
////                      coapRequest.setProxyUri("http://proxy.org:1234/proxytest");
//                clientChannel.sendMessage(coapRequest);
//                logger.debug("Sent Request");
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//            }
//        }

        public void onConnectionFailed(CoapClientChannel channel, boolean notReachable, boolean resetByServer) {
            logger.debug("Uberdust Coap Connection Failed");
        }

        public void onSeparateResponseAck(CoapClientChannel coapClientChannel, CoapEmptyMessage coapEmptyMessage) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void onSeparateResponse(CoapClientChannel coapClientChannel, CoapResponse coapResponse) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        public void onResponse(CoapClientChannel channel, CoapResponse response) {
            String responseStr = new String(response.getPayload(), 0, response.getPayloadLength());
            String fromHashMapUrn = "";
            if(coapRequestMessageIdToUrnNode.containsKey(Integer.toString(response.getMessageID()) ))
            {
                fromHashMapUrn = coapRequestMessageIdToUrnNode.get(Integer.toString(response.getMessageID()));
                urnNodeToCoapReply.put(fromHashMapUrn, responseStr);
                //logger.debug("found matching request! Node Urn: " + fromHashMapUrn );
                coapRequestMessageIdToUrnNode.remove(Integer.toString(response.getMessageID() ));
            }
            //logger.debug("Received response: "+ responseStr + ". Message ID: "+ Integer.toString(response.getMessageID()));
            //logger.debug("2. Received response: "+  urnNodeToCoapReply.get(fromHashMapUrn) + ". Message ID: "+ Integer.toString(response.getMessageID()));
        }
    }

    public boolean getDtnPolicy(){
        return isDtnEnabled;
    }
    /**
     * is supposed to start the DTN (switch on the DTN mode)
     */
    public void setDtnPolicy(boolean value){
        if(isDTNModeSupported())     {
            isDtnEnabled = value;
            handleDTNActivation(value);
        }
        else
            isDtnEnabled = false;
    }

    public boolean isTrustCoapMessagingActive(){
        return  trustCoapMessagingActivated;
    }

    /**
     * is supposed to start the Trust Coap Messaging (switch on the Trust Coap Messaging mode)
     */
    public void setTrustCoapMessagingActive(boolean value){
        if(isTrustCoapMessagingModeSupported())     {
            trustCoapMessagingActivated = value;
            handleTrustCoapMessagingActivation(value);
        }
        else
            trustCoapMessagingActivated = false;
    }

    /**
     * TODO: to remove the sample code from CTI gateway
     *
     * Will initiate a query (CoAP) to all sensors in the parameter list for trust routing info (pfi of their parents) and will return the result
     * @param nodeIdsToQuery a list of node ids to query
     * @return a vector of InfoOnTrustRouting objects
     */
    public Vector<InfoOnTrustRouting> findRealTimeTrustInfoOnNodes(Vector<String> nodeIdsToQuery, int secondBetweenIssuingToAnotherNode){
        Vector<InfoOnTrustRouting> retVec = new Vector<InfoOnTrustRouting>();
        if(isTrustCoapMessagingModeSupported() && isTrustCoapMessagingActive())     {
            //   TODO: implement querying sensors for trust routing info (if supported)
            // Sample test code for conversion debugging... Do we have a special order for the bytes (little endian ?)
            InfoOnTrustRouting retInfo = new InfoOnTrustRouting();
            byte[] payloadBytes =new byte[]{1,0,(byte)0xE8,(byte)0x03};
            String tmpStr = Base64.encodeBase64String(payloadBytes);
            byte[] wholePayloadBytes =  Base64.decodeBase64(tmpStr);

            String payLoadValStr = new String(payloadBytes);
            String sampleNodeId = "urn:wisebed:ctitestbed:0x153d";
            logger.debug("Trust Routing retrieval for node: " + sampleNodeId + " returned: " + payLoadValStr +" + length: " + payLoadValStr.length() + " bytes length: "+ payLoadValStr.getBytes().length + " orig bytes length: " + payloadBytes.length + " reformed byte[] length: "+ wholePayloadBytes.length);
            retInfo.setSourceNodeId(sampleNodeId);
            retInfo.setTimestamp(Long.toString(new Date().getTime()));
            //String wholePayload =  payLoadValStr;

            //byte[] wholePayloadBytes = payLoadValStr.getBytes();
            if(wholePayloadBytes!=null /*&& !wholePayload.isEmpty() && wholePayloadBytes.length> 0 */ && wholePayloadBytes.length >=4 ) { //&& wholePayloadBytes.length %4==0 )
                // {
                //String tmpNodeIdSubStr;
                //String tmpPFIvalSubStr;
                for(int i =0; i<wholePayloadBytes.length && ( i+3 < wholePayloadBytes.length); i+=4) {
                    //tmpNodeIdSubStr = wholePayload.substring(i, i+2);
                    //tmpPFIvalSubStr = wholePayload.substring(i+2, i+4);
                    //logger.debug("TrustRoutingCoap parent node: " +tmpNodeIdSubStr + " pfi val: " + tmpPFIvalSubStr);
                    byte[] nodeIdBytes = new byte[]{wholePayloadBytes[i], wholePayloadBytes[i+1]};   //tmpNodeIdSubStr.getBytes();  //javax.xml.bind.DatatypeConverter.parseHexBinary(tmpNodeIdSubStr);
                    byte[] pfiValueHexBytes = new byte[]{wholePayloadBytes[i+2], wholePayloadBytes[i+3]};//tmpPFIvalSubStr.getBytes(); //javax.xml.bind.DatatypeConverter.parseHexBinary(tmpPFIvalSubStr);
                    short tmpNodeIdValue = Functions.byteArraytoShort(nodeIdBytes);
                    // TODO (temp solution): is this the correct value of a node ID??? We need to add the prefix as a parameter to the function (or even set it later?)
                    String parentNodeId = (new StringBuilder()).append(InfoOnTrustRouting.getNodePrefix()).append(tmpNodeIdValue).toString();
                    //short tmpPFIvalue = Functions.byteArraytoShort(pfiValueHexBytes);
                    short tmpPFIvalue = Functions.byteArraytoSecShort(pfiValueHexBytes);

                    logger.debug("TrustRoutingCoap par node int sec CONVERTED: " +parentNodeId + " pfi val: " + Integer.valueOf(tmpPFIvalue).toString());
                    retInfo.getParentIdsToPFI().put(parentNodeId, Integer.valueOf(tmpPFIvalue));
                }
            }
            if(retInfo.getSourceNodeId().compareTo(InfoOnTrustRouting.INVALID_SOURCENODEID) == 0 ||
                    retInfo.getParentIdsToPFI().isEmpty())
            {
                retInfo = null;
            }
            else {
                retVec.addElement(retInfo);
            }
        }
        return retVec;
    }

    /**
     * TODO: implement if DTN is supported. Should specify what happens when DTN is activated/deactivated
     */
    private void handleDTNActivation(boolean value) {
        return;
    }

    /**
     * Implement  if Coap Messaging for trust aware routing is supported.
     * @param value
     */
    private void handleTrustCoapMessagingActivation(boolean value) {
        return;
    }
}
