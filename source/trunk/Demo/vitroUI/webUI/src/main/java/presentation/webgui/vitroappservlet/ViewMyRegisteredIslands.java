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
package presentation.webgui.vitroappservlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import vitro.dcaintercom.communication.common.Config;
import vitro.dcaintercom.communication.common.IDASHttpRequest;
import vitro.dcaintercom.communication.common.XPathString;
import vitro.dcaintercom.communication.unica.SensorData;
import vitro.virtualsensor.communication.HttpForwarder;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.persistence.DBCommons;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;

/**
 * This class in in charge of sending the information of the devices and
 * concentrators related to the island
 *
 * @author Lucía Plaza Sacarrera
 * @author David Ferrer Figueroa
 *
 */
public class ViewMyRegisteredIslands {

    private static ViewMyRegisteredIslands _viewMyRegisteredIslands = null;
    private Logger LOG;

    private String responseReceivedDev; //Body of the response message.
    private String responseReceivedCon; //Body of the response message.
    // internal dataDevicesTable table has the first entry (0) a table of strings for registered devices
    //                                          and second entry (1) a table of strings for devices creation time
    //                                          and a third entry(2) a table of strings for devices registration time
    //                                          and a fourth entry; a table of strings for devices status (TODO: check whether this is update-able)
    //  WARNING: the element where each table is stored is determined by the order of calls to 4 different methods (TODO: fix this)
    private Vector<String[]> dataDevicesTable = new Vector<String[]>();

    // internal dataConcentratorsTable table has the first entry (0) a table of strings for registered VGWs
    //                                          and second entry (1) a table of string for corresponding ipv4 ips for the VGWs
    //                                          and a third entry(2) a table of lat,long strings for VGWs locations
    //  WARNING: the element where each table is stored is determined by the order of calls to 3 different methods (TODO: fix this)
    private Vector<String[]> dataConcentratorsTable = new Vector<String[]>();
    // TODO: unused. To be removed?
    private Vector<String> phenomena;

    public ViewMyRegisteredIslands() {
        LOG = Logger.getLogger(this.getClass());
        // dictionary = DCADictionary.getInstance();
    }

    // TODO: unused. Is this to be removed?
    public Vector<String> getPhenomena() {
        //phenomena = dictionary.getPhenomTranslatorMap();
        return phenomena;
    }

    public synchronized static ViewMyRegisteredIslands getViewMyRegisteredIslands() {
        if (_viewMyRegisteredIslands == null) {
            _viewMyRegisteredIslands = new ViewMyRegisteredIslands();
        }
        return _viewMyRegisteredIslands;
    }

    public String getResponseReceivedDev() {
        if (responseReceivedDev == null) {
            return "";
        } else {
            return responseReceivedDev;
        }
    }//End getResponseReceivedDev

    public String getResponseReceivedCon() {
        if (responseReceivedCon == null) {
            return "";
        } else {
            return responseReceivedCon;
        }
    }//End getResponseReceivedCon

    public Vector<String[]> getDataDevicesTable() {

        return dataDevicesTable;

    }//End getDataDevicesTable

    public Vector<String[]> getConcentratorsTable() {

        return dataConcentratorsTable;

    }//End getConcentratorsTable

    /**
     * This method retrieves the information about the devices connected to the
     * service and send it to the front page.
     *
     * @return the XML that was sent
     */
    public String sendResponseDevicesList() {
        String retDev = "";

        try {
            HttpForwarder http = new HttpForwarder();
            retDev = SensorData.getDevicesList(Config.getConfig().getServiceIDVITRO());
            http.post(Config.getConfig().getSensorDataUrl(), retDev);
            //responseReceivedDev=IDASHttpRequest.getIDASHttpRequest().sendPOSTToIdasXML(retDev);
            responseReceivedDev = http.getResponse();
            System.out.println(responseReceivedDev);
        } catch (Exception e) {
            LOG.info(e.getMessage());
            e.printStackTrace();
            //System.out.println(e.getMessage());
        }
        /* finally {
         // When HttpClient instance is no longer needed,
         // shut down the connection manager to ensure
         // immediate de-allocation of all system resources
         System.out.println("--------Shutting down connection! ----- ");
         httpClient.getConnectionManager().shutdown();
         }
         */
        return retDev;

    }//End sendResponseDevicesList

    /**
     * This method retrieves the information about the devices connected to the
     * service and send it to the front page.
     *
     * @return the XML that was sent
     */
    public String sendResponseConcentratorsList() {
        String retCon = "";

        try {
            HttpForwarder http = new HttpForwarder();
            retCon = SensorData.getConcentratorsList(Config.getConfig().getServiceIDVITRO());
            http.post(Config.getConfig().getSensorDataUrl(), retCon);
            //responseReceivedCon=IDASHttpRequest.getIDASHttpRequest().sendPOSTToIdasXML(retCon);
            responseReceivedCon = String.valueOf(http.getResponse());
            System.out.println(responseReceivedCon);
        } catch (Exception e) {
            LOG.info(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        /* finally {
         // When HttpClient instance is no longer needed,
         // shut down the connection manager to ensure
         // immediate de-allocation of all system resources
         System.out.println("--------Shutting down connection! ----- ");
         httpClient.getConnectionManager().shutdown();
         }
         */
        return retCon;

    }//End sendResponseConcentratorsList

    /**
     * This method processes the response received,sending a query, to obtain
     * the Id of the devices.
     *
     * @return the list of the names
     */
    public String[] processingDataDevicesId() {
        String[] dataDevices;
        XPathString xpathStr = new XPathString(responseReceivedDev);

        try {
            dataDevices = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/deviceID/globalIdentifier/text()");
        } catch (Exception ex) {
            dataDevices = new String[1];
        }

        dataDevicesTable.add(dataDevices);
        return dataDevices;

    }//End processingDataDevicesId

    /**
     * This method processes the response received to return the creation time
     * of the devices
     *
     * @return list with the creation times
     */
    public String[] processingDataDevicesCreationTime() {
        String[] dataDevices;
        XPathString xpathStr = new XPathString(responseReceivedDev);

        try {
            dataDevices = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/creationTime/text()");
        } catch (Exception ex) {
            dataDevices = new String[1];
        }

        dataDevicesTable.add(dataDevices);
        return dataDevices;

    }//End processingDataDevicesCreationTime

    /**
     * This method processes the response received of the devices to return the
     * registration time of them
     *
     * @return a list with the times
     */
    public String[] processingDataDevicesRegistrationTime() {
        String[] dataDevices;
        XPathString xpathStr = new XPathString(responseReceivedDev);

        try {
            dataDevices = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/registrationTime/text()");
        } catch (Exception ex) {
            dataDevices = new String[1];
        }

        dataDevicesTable.add(dataDevices);
        return dataDevices;

    }//End processingDataDevicesRegistrationTime

    /**
     * This method processes the response received of the devices to return the
     * status
     *
     * @return a list with the status
     */
    public String[] processingDataDevicesStatus() {
        String[] dataDevices;
        XPathString xpathStr = new XPathString(responseReceivedDev);

        try {
            dataDevices = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/status/text()");
        } catch (Exception ex) {
            dataDevices = new String[1];
        }

        dataDevicesTable.add(dataDevices);
        return dataDevices;

    }//End processingDataDevicesStatus

    /**
     * This method sends the query to obtain the name of the concentrators.
     *
     * @return a string array with the list of the concentrators.
     */
    public String[] processingDataConcentratorsId() {
        String[] dataConcentrators;
        XPathString xpathStr = new XPathString(responseReceivedCon);

        try {
            dataConcentrators = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/concentrators/concentrator/logicalName/text()");
        } catch (Exception ex) {
            ex.printStackTrace();
            dataConcentrators = new String[1];
        }

        dataConcentratorsTable.add(dataConcentrators);
        return dataConcentrators;

    }//End processingDataConcentrators

    /**
     * This method sends a query to obtain the IP address of the concentrators
     *
     * @return a list with the IP addresses
     */
    public String[] processingDataConcentratorsIPadd() {
        String[] iPaddress;
        XPathString xpathStr = new XPathString(responseReceivedCon);

        try {
            iPaddress = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/concentrators/concentrator/ipAddress/ipv4/text()");
        } catch (Exception ex) {
            ex.printStackTrace();
            iPaddress = new String[1];
        }

        dataConcentratorsTable.add(iPaddress);
        return iPaddress;

    }// End processingDataConcentratorsIPadd

    /**
     * This method queries to obtain the location of the concentrators
     *
     * @return a list composed with the latitude and the longitude of the
     * concentrators
     */
    public String[] processingDataConcentratorsLocation() {
        String[] latitude;
        String[] longitude;
        String[] location;
        XPathString xpathStr = new XPathString(responseReceivedCon);

        try {
            latitude = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/concentrators/concentrator/location/latitude/text()");
            longitude = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/concentrators/concentrator/location/longitude/text()");
            location = latitude;
            for (int i = 0; i < location.length; i++) {
                location[i] = latitude[i] + "," + longitude[i];
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            location = new String[1];
        }

        dataConcentratorsTable.add(location);
        return location;
    }//End processingDataConcentratorsLocation

    /**
     * This method returns the indexes of the devices in the dataDevicestable
     * that are related to a concentrator.
     *
     * @param concentrator: name of the concentrator from which the devices are
     * to be located
     * @return the indexes of the elements
     */
    public Integer[] relationConcentratorsDevicesIndexes(String concentrator) {
        ArrayList<String> devicesConcentrator = new ArrayList<String>();
        String[] devId = dataDevicesTable.elementAt(0);
        ArrayList<Integer> concentratorIndexes = new ArrayList<Integer>();

        for (int i = 0; i < devId.length; i++) {
            String aux = devId[i];
            // if the device Id starts with the VGWs name then it is assigned to that concetrator.
            // TODO: WARNING!!!! possible bug here: two VGW could begin with the same string (on being a substring of the other, so device assignment could be wrongly made here)
            // FIX: added a +"." to preclude matching of substrs of the correct VGW
            if(aux.startsWith(concentrator+".")){
                devicesConcentrator.add(aux);
                concentratorIndexes.add(i);
            }
        }

        String devCon[] = new String[devicesConcentrator.size()];
        Integer conInd[] = new Integer[concentratorIndexes.size()];
        devicesConcentrator.toArray(devCon);
        concentratorIndexes.toArray(conInd);
        return conInd;

    }//End relationConcentratorsDevicesIndexes


    public String getQueryXPath(ArrayList<String> gws, ArrayList<String> phenomena) {
        String singleQuery = "/sos:InsertObservation/om:Observation/om:parameter[@xlink:href=\"urn:x-ogc:def:identifier:IDAS:1.0:UniversalIdentifierOfLogicalHub\"]/swe:Text/swe:value[text()=#GATEWAY#]/../../../om:result/swe:Quantity[@definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:#PHENOMENOM#\"]/../../..";
        String finalQuery = "";

        if (gws.size() != phenomena.size()) {
            LOG.info("Mismatch size of gateways and phenomena in the xpath query");
            return null;
        }
        for (int i = 0; i < gws.size(); i++) {
            if (!finalQuery.equals("")) {
                finalQuery.concat(" | ");
            }
            finalQuery = finalQuery.concat(singleQuery);
            finalQuery = finalQuery.replace("#GATEWAY#", "\"" + gws.get(i) + "\"");
            finalQuery = finalQuery.replace("#PHENOMENOM#", phenomena.get(i));
        }
        LOG.info("New XPath query: " + finalQuery);
        return finalQuery;
    }

    public String registerGW(String gw, int option) {
        if (option != 1 && option != 0) {
            return "";
        }
        HttpForwarder http = new HttpForwarder();
        String xML;

        if (option == 0) { //Register GW
            xML = SensorData.createConcentrator(Config.getConfig().getServiceIDVITRO(), gw);
        } else { // Delete GW
            xML = SensorData.deleteConcentrator(Config.getConfig().getServiceIDVITRO(), gw);
        }

        http.post(Config.getConfig().getSensorDataUrl(), xML);
        XPathString xpathStr = new XPathString(http.getResponse());

        String[] errors;//Array of strings that stores the possible errors
        String[] errorValue;//When there is an error, the error that is.

        try {
            if (option == 0) { //Register GW
                errors = xpathStr.parseXpathValues("//Envelope/Body/addDataByServiceResponse/errorText/text()");
                if (errors.length != 0) {
                    if (errors[0].equals("OK")) {
                        return errors[0];
                    }

                } else {
                    try {
                        errorValue = xpathStr.parseXpathValues("//Envelope/Body/Fault/faultstring/text()");
                    } catch (Exception ex) {
                        errorValue = new String[1];
                    }
                    return errorValue[0];
                }
            } else if (option == 1) {//Delete GW
                errors = xpathStr.parseXpathValues("//Envelope/Body/deleteDataByServiceResponse/errorText/text()");
                if (errors.length != 0) {
                    if (errors[0].equals("OK")) {
                        return errors[0];
                    }

                } else {
                    try {
                        errorValue = xpathStr.parseXpathValues("//Envelope/Body/Fault/faultstring/text()");
                    } catch (Exception ex) {
                        errorValue = new String[1];
                    }
                    return errorValue[0];
                }
            }
        } catch (Exception ex) {
            errors = new String[1];
        }
        return "";
    }

    public ArrayList<ArrayList<String>> splitQuery(String query) {
        try {
            String[] splitted = query.split(" ");
            ArrayList<String> gws = new ArrayList<String>();
            ArrayList<String> phenomena1 = new ArrayList<String>();
            ArrayList<String> uom = new ArrayList<String>();
            for (int i = 0; i < splitted.length; i++) {
                String[] aux = splitted[i].split(":");
                if (aux.length != 3) {
                    return null;
                }
                gws.add(aux[0]);
                phenomena1.add(aux[1]);
                uom.add(aux[2]);
            }
            ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
            result.add(gws);
            result.add(phenomena1);
            result.add(uom);

            return result;
        } catch (Exception e) {
            System.out.println("Error splitting query " + e);
            return null;
        }
    }

    public ArrayList<String> splitStrings(String data) {
        try {
            ArrayList<String> aux = new ArrayList<String>();
            String[] substrings = data.split(":");
            for (int i = 0; i < substrings.length; i++) {
                aux.add(i, substrings[i]);
            }
            return aux;
        } catch (Exception e) {
            System.out.println("Error splitting String: " + e);
            return null;
        }
    }
}//End class ViewMyRegisteredIslands

