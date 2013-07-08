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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import vitro.dcaintercom.communication.common.IDASHttpRequest;
import vitro.dcaintercom.communication.common.XPathString;
import vitro.dcaintercom.communication.unica.SensorData;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.persistence.DBCommons;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.dcaintercom.communication.common.Config;
import vitro.vspEngine.service.query.QueriedMoteAndSensors;
import vitro.vspEngine.service.query.ReqFunctionOverData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 * TODO: for more effieciency this should better integrated in UserNode (or a factory that chooses communication mode for the UserNode)
 */
public class DummyDCACommUtils {

    private static  DummyDCACommUtils __dummyDCACommUtils = null;
    private Logger LOG;

   private DummyDCACommUtils() {
       LOG = Logger.getLogger(this.getClass());
   }

    /**
     * singleton pattern
     * @return the single instance of __dummyDCACommUtils
     */
    public static DummyDCACommUtils getDummyDCACommUtils() {
        if(__dummyDCACommUtils == null)
        {
            __dummyDCACommUtils = new DummyDCACommUtils();
        }
        return __dummyDCACommUtils;
    }


    /**
     * TODO: perhaps we could remove this eventually. This servlet will remain but only for browsing/managing registered islands for a WSI...
     * TODO: the requests per smartdev capabilities should be threaded!
     */
    public void startDCAEngine(UserNode refUserNode)
    {
        // global in-memory. TODO: add persistency!
        HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM = refUserNode.getGatewaysToSmartDevsHM(); // new HashMap<String, GatewayWithSmartNodes>();    // will contain maps from a gateway id to GatewayWithSmartNodes  objects
        HashMap<String, Vector<SensorModel>> capHMap = refUserNode.getCapabilitiesTable(); // new HashMap<String, Vector<SensorModel>> ();

        Vector<DCAConcentrator> dcaRegisteredVGWs = getDCAConcentratorsList();
        Vector<DCADataDevice> dcaRegisteredDevices = get_ALL_DCADataDevicesList();
        // make the connection between Reg VGWs and their Devices;
        // THIS IS IMPORTANT (it could be integrated in the get_ALL_DCADataDevices though (TODO)
        connectDataDevicesToConcentrators(dcaRegisteredVGWs, dcaRegisteredDevices);

        // for each gateway THAT IS ALSO stored as REGISTERED IN THE LOCAL DB, populate the  gatewaysToSmartDevsHM and the capHMap hashmaps.
        if(dcaRegisteredVGWs!=null && dcaRegisteredDevices!=null)
        {
            for(int i = 0; i< dcaRegisteredVGWs.size(); i++)
            {
                DCAConcentrator tmpCurrDCAConcentrator = dcaRegisteredVGWs.elementAt(i);
                DBRegisteredGateway dbRegGw = DBCommons.getDBCommons().getRegisteredGateway(tmpCurrDCAConcentrator.id);
                if(dbRegGw!=null)     // only for registered VGWs (in DCA) that are also in the local DB
                {
                    // for each registered Smart Node in this gateway!
                    if (tmpCurrDCAConcentrator.dataDevicesVec!=null && tmpCurrDCAConcentrator.dataDevicesVec.size() > 0)
                    {
                        // we now parse the "Capabilities" from the XML message for the SmartDevice
                        for (int k=0;k<tmpCurrDCAConcentrator.dataDevicesVec.size();k++){
                            // "local" versions of the global corresponding hashmaps, for dealing with each separate smart device. Later merged with globals
                            HashMap<String, Vector<SensorModel>> ps_advCapsToSensModels = new HashMap<String, Vector<SensorModel>>();
                            Vector<SmartNode> ps_advSmDevs = new Vector<SmartNode>(); // will only contain one device

                            DCADataDevice tmpCurrDCADataDev = tmpCurrDCAConcentrator.dataDevicesVec.elementAt(k);

                            // We use the name here to remove the additional VGW prefix
                            String tmpDevName = "Name";
                            tmpDevName = tmpCurrDCADataDev.id.replaceAll(Pattern.quote(tmpCurrDCAConcentrator.id + "."), "");
                            // TODO: set device coordinates IF they are available in the SensorML for its registration
                            SmartNode smDev = new SmartNode(tmpCurrDCADataDev.id, tmpDevName, "LocationDesc", new GeoPoint(), tmpCurrDCADataDev.creationTime, tmpCurrDCADataDev.registrationTime, tmpCurrDCADataDev.status);
                            Vector<SensorModel> thisNodesSensorModelsVec = retrievePhenomenaByDevice(tmpCurrDCAConcentrator.id, tmpCurrDCADataDev.id, ps_advCapsToSensModels);
                            if(!thisNodesSensorModelsVec.isEmpty())
                            {
                                smDev.setCapabilitiesVector(thisNodesSensorModelsVec);
                                ps_advSmDevs.add(smDev);
                                DBCommons.getDBCommons().mergeAdvDataToGateway(gatewaysToSmartDevsHM, capHMap, ps_advCapsToSensModels, ps_advSmDevs, tmpCurrDCAConcentrator.id, dbRegGw.getFriendlyName(), tmpCurrDCAConcentrator.ipv4, tmpCurrDCAConcentrator.locationStr);
                            }
                        }
                        DBCommons.getDBCommons().updateRcvGatewayAdTimestamp(tmpCurrDCAConcentrator.id, false);
                    }
                }
            }
        }
    }


    /**
     * Should parse a QueryDefinition and get the results from the corresponding gateway. Also apply any global functions if requested!
     */
    public void executeAggrQuery(String uQDefID, String gateID, Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFuncVec, boolean isHistory, Vector<ReqFunctionOverData> functionVec, int thisQueryOrderNum) {
        String tmpXMLtoSend;
        String tmpXMLreceived;
        try {
            // TODO: ++++ CHECK RESPONSES. CHECK INVALID RESPONSES AND ERRORS TOO?
            Iterator<QueriedMoteAndSensors> itVec =  motesAndTheirSensorAndFuncVec.iterator();
            while(itVec.hasNext())
            {
                QueriedMoteAndSensors tmp = itVec.next();
                String dcaDevId = /*gateID+ "." +*/ tmp.getMoteId(); // we already have the gateID prefix in DCA comm mode.
                tmpXMLtoSend= SensorData.getLastMeasurement(Config.getConfig().getServiceIDVITRO(), dcaDevId);
                tmpXMLreceived= IDASHttpRequest.getIDASHttpRequest().sendPOSTToIdasXML(tmpXMLtoSend);
                //LOG.debug("RECEIVED LAST MEASUREMENT FROM "+ dcaDevId + ": "+tmpXMLreceived + "\n--------------------------\n--------------------------\n\n");
                String[] possibleFaultCode;

                XPathString xpathStr = new  XPathString(tmpXMLreceived);
                try{
                    possibleFaultCode = xpathStr.parseXpathValues("//Envelope/Body/Fault/faultcode/text()");
                    if(possibleFaultCode != null && possibleFaultCode.length>0 && possibleFaultCode[0] !=null && !possibleFaultCode[0].trim().isEmpty())
                    {
                        String[] possibleFaultText;
                        String[] possibleFaultDetailCode;
                        possibleFaultText = xpathStr.parseXpathValues("//Envelope/Body/Fault/faultstring/text()");
                        possibleFaultDetailCode = xpathStr.parseXpathValues("//Envelope/Body/Fault/detail/errorCode/text()");
                        if(possibleFaultText != null && possibleFaultText.length>0    &&
                                possibleFaultDetailCode != null && possibleFaultDetailCode.length>0  &&
                                possibleFaultText[0] !=null &&  possibleFaultDetailCode[0] !=null)
                            LOG.error("Error while requesting measurements from " + dcaDevId + ". Error Code: "+ possibleFaultCode[0] + " ("+ possibleFaultDetailCode[0] +"). Error desc: " + possibleFaultText[0]);
                        else
                            LOG.error("Error while requesting measurements from " + dcaDevId + ". Error Code: "+ possibleFaultCode[0]+".");
                    }
                    else
                    {
                        String[] capabilityArrStr;
                        String[] measurementArrStr;
                        String[] dateTSArrStr;
                        capabilityArrStr = xpathStr.parseXpathValues("//Envelope/Body/getDeviceDataResponse/measure/data/attribute/name/text()");
                        measurementArrStr =  xpathStr.parseXpathValues("//Envelope/Body/getDeviceDataResponse/measure/data/attribute/value/text()");
                        dateTSArrStr = xpathStr.parseXpathValues("//Envelope/Body/getDeviceDataResponse/measure/date/text()");
                        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");

                        if(capabilityArrStr!=null && measurementArrStr!=null && dateTSArrStr!= null &&
                                capabilityArrStr.length>0 && measurementArrStr.length>0 && dateTSArrStr.length>0 &&
                                capabilityArrStr[0]!=null && measurementArrStr[0]!=null && dateTSArrStr[0]!=null )
                        {
                            //Date timestampDate = df.parse(dateTSArrStr[0]);
                            Date timestampDate = new Date();
                            Calendar calendar = javax.xml.bind.DatatypeConverter.parseDateTime(dateTSArrStr[0]);
                            timestampDate = calendar.getTime();
                            LOG.info("RECEIVED LAST MEASUREMENT FROM "+ dcaDevId + ": "+ capabilityArrStr[0] + ":: "+measurementArrStr[0]+ " :: at:: "+timestampDate.toString()+"  \n--------------------------\n--------------------------\n\n");
                        }
                        else
                        {
                            LOG.error("Unexpected error while reading measurement received from " + dcaDevId + ".");
                        }
                    }
                    //dirtySensorMLForDevice=xpathStr.parseXpathValues("//Envelope/Body/Fault/faultcode/text()");
                    //parse the right values (xpath), omit wrong/error answers, and produce an aggrResponseMessage+file for response!
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception e){
            LOG.info(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }



    /**
     *  @return    the vector of sensorModels for this device. Also the out_advCapsToSensModels is set
     */
    private Vector<SensorModel> retrievePhenomenaByDevice(String gwID,
                                                          String devId,
                                                          HashMap<String, Vector<SensorModel>> out_advCapsToSensModels) {
        Vector<SensorModel> thisNodesSensorModelsVec = new Vector<SensorModel>();

        String [] tmpPhenomenaInputIDAS = null;
        String [] tmpPhenomenaModelName = null;
        String tmpXMLtoSend;
        String tmpXMLreceived;
        try {
            tmpXMLtoSend= SensorData.getDeviceDataSML(Config.getConfig().getServiceIDVITRO(), devId);
            tmpXMLreceived= IDASHttpRequest.getIDASHttpRequest().sendPOSTToIdasXML(tmpXMLtoSend);

            String[] dirtySensorMLForDevice;
            XPathString xpathStr = new  XPathString(tmpXMLreceived);

            try{
                dirtySensorMLForDevice=xpathStr.parseXpathValues("//Envelope/Body/getDeviceDataResponse/sensorML/text()");
            }catch (Exception ex){
                ex.printStackTrace();
                dirtySensorMLForDevice = new String[1];
            }
            String cleanSensorMLForDevice = StringEscapeUtils.unescapeXml(dirtySensorMLForDevice[0]).replaceAll("[^\\x20-\\x7e]", "");
            // TODO: there must be a prettier way to remove the Buffer content{...} wrapping
            cleanSensorMLForDevice = cleanSensorMLForDevice.replaceAll(Pattern.quote("Buffer content: {"), "");
            cleanSensorMLForDevice = cleanSensorMLForDevice.replaceAll(Pattern.quote("}"), "");
            xpathStr = new  XPathString(cleanSensorMLForDevice);
            // TODO: should we consider the inputs (sensed) or the outputs? (returned values) here?
            tmpPhenomenaInputIDAS=xpathStr.parseXpathValues("//RegisterSensor/SensorDescription/System/inputs/InputList/input/ObservableProperty/@definition");
            tmpPhenomenaModelName=xpathStr.parseXpathValues("//RegisterSensor/SensorDescription/System/inputs/InputList/input/@name");

            String tmpCapDefinition = "";
            if(tmpPhenomenaInputIDAS!=null && tmpPhenomenaModelName!=null && tmpPhenomenaInputIDAS.length == tmpPhenomenaModelName.length && tmpPhenomenaInputIDAS.length>0)
            {
                for (int i=0;i<tmpPhenomenaInputIDAS.length;i++){
                    Vector<SensorModel> advSensModels = new Vector<SensorModel>();

                    tmpCapDefinition = tmpPhenomenaInputIDAS[i];
                    String tmpSensorID = tmpPhenomenaModelName[i];
                    Integer thedigestInt = tmpSensorID.hashCode();
                    if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);
                    tmpSensorID = thedigestInt.toString(); //todo: eventually we should not use the digest anymore!!!

                    if (!tmpCapDefinition.equalsIgnoreCase("") && (out_advCapsToSensModels.keySet().isEmpty() || !out_advCapsToSensModels.keySet().contains(tmpCapDefinition))) {
                        out_advCapsToSensModels.put(tmpCapDefinition, advSensModels);
                    } else {
                        advSensModels = out_advCapsToSensModels.get(tmpCapDefinition);
                    }
                    boolean sensorModelFoundInCapsTable = false;
                    if (!advSensModels.isEmpty()) {
                        for (int sv = 0; sv < advSensModels.size(); sv++) {
                            if (advSensModels.elementAt(sv).getGatewayId().equalsIgnoreCase(gwID) && advSensModels.elementAt(sv).getSmID().equals(tmpSensorID)) {
                                sensorModelFoundInCapsTable = true;
                                break;
                            }
                        }
                    }
                    SensorModel tmpSensModelToAdd = new SensorModel(tmpCapDefinition, gwID, tmpSensorID, SensorModel.numericDataType, null, null);
                    if (!sensorModelFoundInCapsTable) {      //if not found in the total HashMap of Capability to SensorModels of various gateways
                        advSensModels.add(tmpSensModelToAdd);
                    }
                    if ( ! SensorModel.vectorContainsSensorModel(thisNodesSensorModelsVec, gwID,tmpSensorID )) {
                        //if not found in the vector of SensorModels for this node
                        thisNodesSensorModelsVec.add(tmpSensModelToAdd);
                    }
                }
            }
        }
        catch (Exception e){
            LOG.info(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return thisNodesSensorModelsVec;
    }//End retrievePhenomenaByDevice


    /***************************************************
     *
     */
    /**
     * This method retrieves the information about the devices connected to the
     * service and send it to the front page.
     *
     * @return the List of DCA Concetrators in the response XML from DCA.
     */
    private Vector<DCAConcentrator> getDCAConcentratorsList() {
        Vector<DCAConcentrator> vecToRet = new Vector<DCAConcentrator>();
        String postCon = "";
        String responseReceivedCon = "";
        try {
            postCon = SensorData.getConcentratorsList(Config.getConfig().getServiceIDVITRO());
            responseReceivedCon=IDASHttpRequest.getIDASHttpRequest().sendPOSTToIdasXML(postCon);
        } catch (Exception e) {
            LOG.info(e.getMessage());
            e.printStackTrace();
        }
        if(responseReceivedCon != null && !responseReceivedCon.trim().isEmpty())
        {
            // get Concentrators Ids
            String[] concentratorsIds;
            XPathString xpathStr = new XPathString(responseReceivedCon);
            try {
                concentratorsIds = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/concentrators/concentrator/logicalName/text()");
            } catch (Exception ex) {
                ex.printStackTrace();
                concentratorsIds = new String[1];
            }

            // get Concentrators IPs
            String[] iPaddress;
            xpathStr = new XPathString(responseReceivedCon);
            try {
                iPaddress = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/concentrators/concentrator/ipAddress/ipv4/text()");
            } catch (Exception ex) {
                ex.printStackTrace();
                iPaddress = new String[1];
            }

            // get Concentrators Locations (Lat, long)
            String[] latitude;
            String[] longitude;
            String[] location;
            xpathStr = new XPathString(responseReceivedCon);

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

            // form the Concentrator objects
            if(concentratorsIds != null && iPaddress!=null && location!=null &&
                    concentratorsIds.length > 0 &&
                    concentratorsIds.length == iPaddress.length &&
                    concentratorsIds.length == location.length)
            {
                for (int i = 0 ; i< concentratorsIds.length; i++)
                {
                    DCAConcentrator tmpDCAConc = new DCAConcentrator();
                    tmpDCAConc.id = concentratorsIds[i];
                    tmpDCAConc.ipv4 = iPaddress[i];
                    tmpDCAConc.locationStr = location[i];
                    vecToRet.add(tmpDCAConc);
                }
            }
        }
        return vecToRet;

    }//End getDCAConcentratorsList


    private Vector<DCADataDevice> get_ALL_DCADataDevicesList() {
        Vector<DCADataDevice> vecToRet = new Vector<DCADataDevice>();
        String postCon = "";
        String responseReceivedDev = "";
        try {
            postCon = SensorData.getDevicesList(Config.getConfig().getServiceIDVITRO());
            responseReceivedDev=IDASHttpRequest.getIDASHttpRequest().sendPOSTToIdasXML(postCon);
        } catch (Exception e) {
            LOG.info(e.getMessage());
            e.printStackTrace();
        }
        if(responseReceivedDev != null && !responseReceivedDev.trim().isEmpty())
        {
           // get Devices Ids
            String[] dataDevicesIds;
            XPathString xpathStr = new XPathString(responseReceivedDev);

            try {
                dataDevicesIds = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/deviceID/globalIdentifier/text()");
            } catch (Exception ex) {
                dataDevicesIds = new String[1];
            }
           // get Devices  registration time
            String[] dataDevicesRegTm;
            xpathStr = new XPathString(responseReceivedDev);

            try {
                dataDevicesRegTm = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/registrationTime/text()");
            } catch (Exception ex) {
                dataDevicesRegTm = new String[1];
            }

            // get Devices creation Time
            String[] dataDevicesCreatTime;
            xpathStr = new XPathString(responseReceivedDev);

            try {
                dataDevicesCreatTime = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/creationTime/text()");
            } catch (Exception ex) {
                dataDevicesCreatTime = new String[1];
            }
           // get Devices Status
            String[] dataDevicesStatus;
            xpathStr = new XPathString(responseReceivedDev);

            try {
                dataDevicesStatus = xpathStr.parseXpathValues("//Envelope/Body/getDataByServiceResponse/devices/device/status/text()");
            } catch (Exception ex) {
                dataDevicesStatus = new String[1];
            }
            // form the DCA DataDevice objects
            if(dataDevicesIds != null && dataDevicesRegTm!=null && dataDevicesCreatTime!=null &&  dataDevicesStatus!=null &&
                    dataDevicesIds.length > 0 &&
                    dataDevicesIds.length == dataDevicesRegTm.length &&
                    dataDevicesIds.length == dataDevicesCreatTime.length &&
                    dataDevicesIds.length == dataDevicesStatus.length)
            {
                for (int i = 0 ; i< dataDevicesIds.length; i++)
                {
                    DCADataDevice tmpDCADev = new DCADataDevice();
                    tmpDCADev.id = dataDevicesIds[i];
                    tmpDCADev.registrationTime = dataDevicesRegTm[i];
                    tmpDCADev.creationTime = dataDevicesCreatTime[i];
                    tmpDCADev.status = dataDevicesStatus[i];
                    vecToRet.add(tmpDCADev);
                }
            }
        }
        return vecToRet;

    }//End get_ALL_DCADataDevicesList

    /**
     * Associates devices to the corresponding concentrators
     */
    private void connectDataDevicesToConcentrators(Vector<DCAConcentrator> allConcs, Vector<DCADataDevice> allDevs) {
        if(allConcs != null && allDevs != null)
        {
            for (int j=0; j<allConcs.size(); j++) {
                DCAConcentrator tmpCurrDCAConc = allConcs.elementAt(j);
                for (int k=0; k<allDevs.size(); k++) {
                    if(allDevs.elementAt(k).id.startsWith(tmpCurrDCAConc.id+"."))  {
                        tmpCurrDCAConc.dataDevicesVec.add(allDevs.elementAt(k));
                    }
                }
            }
        }
    }//End connectDataDevicesToConcentrators

    /****************************************************
    *
    */

    /**
     * Auxilliary class for DCA comm
     */
    private class DCAConcentrator {
        public String id;
        public String ipv4;
        public String locationStr;
        public Vector<DCADataDevice> dataDevicesVec;

        DCAConcentrator() {
            id = "";
            ipv4 = "";
            locationStr = "";
            dataDevicesVec = new Vector<DCADataDevice>();
        }
    }

    /**
     * Auxiliary class for DCA comm . DataDevice is a SmartDevice
     */
    private class DCADataDevice {
        public String id;
        public String creationTime;
        public String registrationTime;
        public String status;

        DCADataDevice() {
            id="";
            creationTime = "";
            registrationTime ="";
            status = "";
        }
    }

}
