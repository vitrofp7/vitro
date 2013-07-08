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
package alter.vitro.vgw.wsiadapter;

import alter.vitro.vgw.model.CGateway;
import alter.vitro.vgw.model.CGatewayWithSmartDevices;
import alter.vitro.vgw.model.CSensorModel;
import alter.vitro.vgw.model.CSmartDevice;
import alter.vitro.vgw.service.geodesics.GeodesicPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vitro.vgw.exception.WSIAdapterException;
import vitro.vgw.model.Node;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.service.SensorMLMessageAdapter;
import vitro.vgw.wsiadapter.WSIAdapter;
import vitro.vgw.wsiadapter.TCSWSIAdapter;
import vitro.vgw.wsiadapter.WSIAdapterCoap;

import java.sql.Timestamp;
import java.util.*;

import alter.vitro.vgw.service.query.wrappers.QueriedMoteAndSensors;
import alter.vitro.vgw.service.query.wrappers.ReqFunctionOverData;
import alter.vitro.vgw.service.query.wrappers.ReqResultOverData;
import alter.vitro.vgw.service.query.wrappers.ReqSensorAndFunctions;
import alter.vitro.vgw.service.query.wrappers.ResultAggrStruct;
import alter.vitro.vgw.service.query.wrappers.TimeIntervalStructure;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class WsiTCSCoapAdapterCon extends WsiAdapterCon {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private WSIAdapter delegate = new TCSWSIAdapter();

    private static WsiTCSCoapAdapterCon myCon = null;

    private Map<String, CSmartDevice> smartDeviceMap;
    private Map<Integer, Resource> resourceMap;

    /**
     * Creates a new instance of WsiUberDustCon
     */
    private WsiTCSCoapAdapterCon() {
        //
        //
        super();
        smartDeviceMap = new HashMap<String, CSmartDevice>();
        resourceMap = new HashMap<Integer, Resource>();

        resourceMap.put(getResourceCode(Resource.RES_TEMPERATURE), Resource.RES_TEMPERATURE);
        resourceMap.put(getResourceCode(Resource.RES_LIGHT), Resource.RES_LIGHT);
        resourceMap.put(getResourceCode(Resource.RES_HUMIDITY), Resource.RES_HUMIDITY);
        resourceMap.put(getResourceCode(Resource.RES_WIND_SPEED), Resource.RES_WIND_SPEED);
        resourceMap.put(getResourceCode(Resource.RES_CO), Resource.RES_CO);
        resourceMap.put(getResourceCode(Resource.RES_CO2), Resource.RES_CO2);
        resourceMap.put(getResourceCode(Resource.RES_PRESSURE), Resource.RES_PRESSURE);
        resourceMap.put(getResourceCode(Resource.RES_BAROMETRIC_PRESSURE), Resource.RES_BAROMETRIC_PRESSURE);

        resourceMap.put(getResourceCode(Resource.RES_TRUST_ROUTING), Resource.RES_TRUST_ROUTING);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT1), Resource.RES_SWITCH_LIGHT1);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT2), Resource.RES_SWITCH_LIGHT2);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT3), Resource.RES_SWITCH_LIGHT3);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT4), Resource.RES_SWITCH_LIGHT4);


    }

    private int getResourceCode(Resource resource){

        int hash = resource.getName().hashCode();
        return hash < 0 ? hash * -1 : hash;

    }





    /**
     * This is the function the world uses to get the Connection to the Data from the WSN.
     * It follows the Singleton pattern
     */
    public static WsiTCSCoapAdapterCon getWsiTCSCoapAdapterCon(DbConInfo databaseConnInfo) {
        if (myCon == null) {
            myCon = new WsiTCSCoapAdapterCon();

        }
        return myCon;
    }

    @Override
    public CGatewayWithSmartDevices createWSIDescr(CGateway givGatewayInfo) {

        Vector<CSmartDevice> currSmartDevicesVec = new Vector<CSmartDevice>();
        CGatewayWithSmartDevices myGatewayForSmartDevs = new CGatewayWithSmartDevices(givGatewayInfo, currSmartDevicesVec);

        // An auxiliary structure that maps Unique Generic Capability Descriptions to Lists of SensorTypes ids.
        HashMap<String, Vector<Integer>> myAllCapabilitiesToSensorModelIds = new HashMap<String, Vector<Integer>>();

        //Clear my sensor map
        smartDeviceMap.clear();


        try{
            List<Node> nodeList = delegate.getAvailableNodeList();
            for (Node node : nodeList) {

                /* For each node I create a SmartDevice */
                Vector<Integer> sensorModels_IDs_OfSmartDevVector = new Vector<Integer> () ;// todo: fix this redundancy!
                Vector<CSensorModel> sensorModelsOfSmartDevVector = new Vector<CSensorModel>();
                CSmartDevice tmpSmartDev = new CSmartDevice(node.getId(),
                        "",/* smart device type name */
                        "",/* location description e.g. room1*/
                        new GeodesicPoint(), /*  */
                        sensorModels_IDs_OfSmartDevVector);

                //Node interrogation about supported features
                List<Resource> resourceList = delegate.getResources(node);


                for (Resource resource : resourceList) {

                    String tmpGenericCapabilityForSensor = resource.getName();
                    Integer thedigestInt = (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()).hashCode();
                    if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

                    CSensorModel tmpSensorModel = new CSensorModel(givGatewayInfo.getId(), /*Gateway Id*/
                            thedigestInt, /*Sensor Model Id */
                            (tmpGenericCapabilityForSensor + "-" + tmpSmartDev.getName()), /* Sensor Model name */
                            CSensorModel.numericDataType, /* Data type*/  // TODO: later on this should be adjustable!!!
                            CSensorModel.defaultAccuracy, /* Accuracy */
                            CSensorModel.defaultUnits) /* Units */;  // TODO: this should be set when it is known!!!
//                                                if(!tmpGenericCapabilityForSensor.equalsIgnoreCase("UnknownPhenomenon" ))
//                                                {
                    sensorModelsOfSmartDevVector.add(tmpSensorModel);
                    sensorModels_IDs_OfSmartDevVector.add(tmpSensorModel.getSmid());

                    if (!myAllCapabilitiesToSensorModelIds.containsKey(tmpGenericCapabilityForSensor)) {
                        myAllCapabilitiesToSensorModelIds.put(tmpGenericCapabilityForSensor, new Vector<Integer>());
                        givGatewayInfo.getAllGwGenericCapabilities().put(tmpGenericCapabilityForSensor, new Vector<CSensorModel>());
                    }
                    // When we reach this part, we already have a key that corresponds to a unique sensor capability description
                    if (!myAllCapabilitiesToSensorModelIds.get(tmpGenericCapabilityForSensor).contains(Integer.valueOf(tmpSensorModel.getSmid()))) {
                        myAllCapabilitiesToSensorModelIds.get(tmpGenericCapabilityForSensor).addElement(tmpSensorModel.getSmid());
                        givGatewayInfo.getAllGwGenericCapabilities().get(tmpGenericCapabilityForSensor).addElement(tmpSensorModel);
                    }

                }

                if (!sensorModelsOfSmartDevVector.isEmpty()) {
                    // TODO: FILTER OUT UNSUPPORTED OR COMPLEX NODES!!!!
                    currSmartDevicesVec.addElement(tmpSmartDev);
                    smartDeviceMap.put(tmpSmartDev.getId(), tmpSmartDev);
                    //#####################################
                }


            }
        } catch (WSIAdapterException e) {
            logger.error("Error while retrieving WSI description", e);
        }

        return myGatewayForSmartDevs;
    }

    @Override
    public void setUpdateDescrInterval() {
        // TODO Auto-generated method stub

    }

    @Override
    public Vector<ReqResultOverData> translateAggrQuery(Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFunctsVec, Vector<ReqFunctionOverData> reqFunctionVec) {
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
        //    we can assume that we want the MAX over the values we took within that specific timeperiod (from all desired motes and all readings of their corresponding sensor)
        //    e.g.  In the period (Mon to Wed) mote-1 can have 20 light values from its light sensor, mote-2 can have 34 values etc. We calculate the max over all these.
        //    However if no timeperiod tag is set, we assume that we get the MAX over only the Latest Value per mote.
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

        Vector<ReqResultOverData> retVecofResults = new Vector<ReqResultOverData>();

        for (int i = 0; i < motesAndTheirSensorAndFunctsVec.size(); i++) {

            QueriedMoteAndSensors currentMote = motesAndTheirSensorAndFunctsVec.elementAt(i);

            String fullMoteId = currentMote.getMoteid();

            List<ReqSensorAndFunctions> tmpVecSmAndFuncList = currentMote.getQueriedSensorIdsAndFuncVec();

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

                //System.out.println("Size of tmpVecSmAndFuncList::" + Integer.toString(tmpVecSmAndFuncList.size())  );
                for (ReqSensorAndFunctions currentSensorModel : tmpVecSmAndFuncList) {

                    int smid = currentSensorModel.getSensorModelIdInt();
                    int countValuesOfthisSensorModel = 0;


                    //System.out.println("For mote "+fullMoteId +" and sensor "+Integer.toString(smid) + " function vector size is "+reqFunctionVec.size());
                    for (ReqFunctionOverData currentRequestedFunction : reqFunctionVec) {
                        vOfSensorValues = new Vector<ResultAggrStruct>(); // <--this was moved here, otherwise it created duplicate entries!

                        if (currentRequestedFunction.getfuncId() == ReqFunctionOverData.unknownFuncId) {
                            vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId, smid, "No Result", 1, null));
                            countValuesOfthisSensorModel += 1;
                        } else if (currentSensorModel.getFunctionsOverSensorModelVec().contains(currentRequestedFunction.getfuncId())) { // this loop (and this condition) allows to retrieve the valid "functions" to be performed on values of this sensor

                            Vector<WsiUberDustCon.VerySimpleSensorMeasurement> mySensorReadings = new Vector<WsiUberDustCon.VerySimpleSensorMeasurement>();  // bugfix: this is now moved inside the functions loop
                            // for each different "function" on the sensor values, we may need to gather completely different values. (e.g. a function could request a history of measurements, or only measurements that are above a threshold)

                            CSmartDevice currentSmartDevice = smartDeviceMap.get(fullMoteId);

                            Resource requestedResource = resourceMap.get(smid);

                            if(delegate == null) {
                                logger.error("Delegate object was null. Could not request Node Observation");

                            } else if(currentSmartDevice == null || currentSmartDevice.getId() == null) {
                                logger.error("currentSmartDevice object or it's id was null. Could not request Node Observation");
                            }
                            else if (requestedResource == null) {
                                logger.error("requestedResource object was null. Could not request Node Observation");
                            }
                            else
                            {
                                Observation obs = delegate.getNodeObservation(new Node(currentSmartDevice.getId()), requestedResource);
                                if(obs != null) {
                                    WsiUberDustCon.VerySimpleSensorMeasurement simpleSensorMeasurement = new WsiUberDustCon.VerySimpleSensorMeasurement(
                                            SensorMLMessageAdapter.getIdasPhenomenom(requestedResource),
                                            SensorMLMessageAdapter.getIdasUOMDefinition(obs.getUom()),
                                            new String[]{obs.getValue()},
                                            SensorMLMessageAdapter.getIdasTimestamp(obs.getTimestamp()),
                                            obs.getTimestamp()
                                    );

                                    mySensorReadings.add(simpleSensorMeasurement);

                                    for (int o = 0; o < mySensorReadings.size(); o++) {
                                        /* TODO: (++++) this could be optimized further (not write the entire data in the vector) / first process it
                                         * according to the function.
                                         * TODO: Somewhere around here we should handle the History function (not likely for uberdust)
                                         */
                                        // TODO: here we handle the actuation capabilities for lights as well, if a set value function was requested on them
                                        // TODO: if a last value reading was requested we can handle that too by sending their state (as reported)
                                        //                            if(mySensorReadings.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS +  (dictionaryNameToIDASPhenomenon.get("switchlight1")).toString() )
                                        //                                    ||mySensorReadings.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS +  (dictionaryNameToIDASPhenomenon.get("switchlight2")).toString() )
                                        //                                    ||mySensorReadings.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS +  (dictionaryNameToIDASPhenomenon.get("switchlight3")).toString() )
                                        //                                    ||mySensorReadings.elementAt(o).getObservPropertyDef().equalsIgnoreCase(staticprefixPhenomenonIDAS +  (dictionaryNameToIDASPhenomenon.get("switchlight4")).toString() )
                                        //                                    )
                                        //                            {
                                        //                                 System.out.println("------------------------------------------ HANDLE ACTUATION NOW! "+ mySensorReadings.elementAt(o).getObservPropertyDef());
                                        //
                                        //                                 System.out.println("Function: "+ reqFunctionVec.get(k).getfuncName());
                                        //                                // TODO: for now we use the threshold field to set the actuation value! Later this could be a separate parameter field
                                        //                                    if(reqFunctionVec.get(k).getThresholdField()!= null && !reqFunctionVec.get(k).getThresholdField().isEmpty())
                                        //                                    {
                                        //                                        ThresholdStructure requiredThresholds = new ThresholdStructure(reqFunctionVec.get(k).getThresholdField());
                                        //                                        if (requiredThresholds.getLowerBound()!=null && !requiredThresholds.getLowerBound().isEmpty())
                                        //                                        {
                                        //                                            System.out.println("Actuation parameter: " + requiredThresholds.getLowerBound().trim());
                                        //                                        }
                                        //                                    }
                                        //
                                        //                            }
                                        //                            else
                                        //                            {

                                        long valueToAdd;
                                        try {
                                            double d = Double.parseDouble(mySensorReadings.elementAt(o).getObservOutputMeasurementData()[0]);
                                            valueToAdd = (long) d;
                                        } catch (Exception e) {
                                            //System.out.println("*** *** *** OOOOO it's an exception for  ************ "+ mySensorReadings.elementAt(o).getObservOutputMeasurementData()[0]);
                                            valueToAdd = -1;
                                        }

                                        long timestampOfReading = mySensorReadings.elementAt(o).getObservPropertyTSLong();
                                        Timestamp timestampOfReadingSql = new Timestamp(timestampOfReading);
                                        vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId,
                                                smid,
                                                Long.toString(valueToAdd),
                                                1,
                                                new TimeIntervalStructure(timestampOfReadingSql,
                                                        timestampOfReadingSql))
                                        );
                                        countValuesOfthisSensorModel += 1;
                                    }
                                    if (countValuesOfthisSensorModel == 0) {
                                        vOfSensorValues.addElement(new ResultAggrStruct(fullMoteId, smid, ReqResultOverData.specialValueNoReading, 1, null));
                                        countValuesOfthisSensorModel += 1;
                                    } else {
                                        logger.debug("Counted Values of this sensor " + Integer.toString(countValuesOfthisSensorModel));
                                    }
                                }
                                else {//if obs is null
                                    logger.error("Returned Obs value was Null! Failed to retrieve observation!");
                                }
                            }
                        }
                        // this condition checks that at least one value was retrieved from the sensor and used in the function (even if that value was "no result" for an unknown function)
                        if (countValuesOfthisSensorModel > 0) // we should make sure that this is always true.
                        {
                            retVecofResults.addElement(new ReqResultOverData(currentRequestedFunction.getfuncId(), vOfSensorValues));
                        }
                    } // ends the block where we gather values of a sensor for a specific function

                } // ends the loop over the requested sensor Models (capabilities) of the current requested Smart Device
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }// end of for loop over all requested Smart Devices in the request vector

        return retVecofResults;
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
            // this AdapterCon uses a delegate WsiAdapter delegate, so that should handle the activation
            delegate.setDtnPolicy(value);
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
            // this AdapterCon uses a delegate WsiAdapter delegate, so that should handle the activation
            delegate.setTrustCoapMessagingActive(value);
        }
        else
            trustCoapMessagingActivated = false;
    }

    /**
     * Will initiate a query (CoAP) to all sensors in the parameter list for trust routing info (pfi of their parents) and will return the result
     * @param nodeIdsToQuery a list of node ids to query
     * @return a vector of InfoOnTrustRouting objects
     */
    public Vector<InfoOnTrustRouting> findRealTimeTrustInfoOnNodes(Vector<String> nodeIdsToQuery, int secondBetweenIssuingToAnotherNode){
        Vector<InfoOnTrustRouting> retVec = new Vector<InfoOnTrustRouting>();
        if(isTrustCoapMessagingModeSupported() && isTrustCoapMessagingActive())     {
            //   implement querying sensors for trust routing info (if supported)
            if(nodeIdsToQuery!=null)
            {
                Iterator<String> nodeIdsIter = nodeIdsToQuery.iterator();
                while(nodeIdsIter.hasNext()) {
                    String currNodeId = nodeIdsIter.next();
                    // in the following function we could use directly the resource (instead of going through the mapping. Kept it as is for consistency)
                    try {
                        InfoOnTrustRouting currTrustRoutingInfo = delegate.getNodeTrustRoutingInfo(new Node(currNodeId), resourceMap.get(getResourceCode(Resource.RES_TRUST_ROUTING)));
                        if(currTrustRoutingInfo!=null) {
                            retVec.addElement(currTrustRoutingInfo);
                        }
                        // do not send immediate the next message so as to not flood the network
                        try{
                            Thread.sleep(secondBetweenIssuingToAnotherNode*1000);
                        }
                        catch(InterruptedException exxSleep) {
                            logger.error("Error or interruption while sleeping between coap messages", exxSleep);
                        }
                    } catch (WSIAdapterException e) {
                        logger.error("Error while retrieving trust routing info", e);
                    }

                }
            }
        }
        return retVec;
    }

}
