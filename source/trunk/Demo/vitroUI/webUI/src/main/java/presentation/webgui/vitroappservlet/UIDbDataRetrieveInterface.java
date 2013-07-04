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

/**
 * TODO this is placeholder for now, but will implement fully on server side the UI functions included in nTransactDbDataRetrieveInteface (most of the code there should migrate here)
 */
public class UIDbDataRetrieveInterface {

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param serviceID: composed service ID (integer id from DB)
     * @return "gateway_id,sensor_id,flag\n" records where flag (0 or 1) indicates if the sensor is still "active" (included in the most recent resource update of the VGW)
     */
    public static void  getServiceSensorListForComposedService(int serviceID) {

    }

    /**
     *
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param instanceID  partial service ID (in db)
     *
     */
    public static void  getServiceSensorListForPartialService(int instanceID) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param serviceID  composed service ID	(int)
     * @param gatewayID  registered gw name (eg vitrogw_cti)
     * @param sensorID   registered sensor name (eg 000:111:222)
     * @return   "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,sensor_is_active_flag,sensor_supports_this_capability_flag,capability_is_supported_at_all_flag\n"
     *     where:
            partServ_id indicates the partial service within the requested composed service,
            cap_id_in_db is the capability id in the DB
            cap_name is the full capability name (eg 'urn:x-ogc:def:phenomenon:IDAS:1.0:luminousIntensity')
            func_name is the functions name (eg min, max, last)
            trigger_flag is 0 or 1 indicating whether there is an alert connected to this capability definition
            trigger_comparison_sign is "gt" by default meaning a value has to be "greater than" to trigger an alert
            trigger_value is a number (the threshold for the trigger)
            sensor_is_active_flag is 0 or 1 indicating whether the sensor is active (included in the most recent resource update of the VGW)
            sensor_supports_this_capability_flag is 0 or 1 indicating whether the sensor supports the capability (because the user can order a sensor to measure temperature from the map, but the sensor may not support this)
            capability_is_supported_at_all_flag is 0 or 1 indicating if this capability is at all supported in VITRO (because all the resources supporting it at one time, may have failed or been removed)

     */
    public static void  getSensorCapabilityListForComposedService(int serviceID, String gatewayID, String sensorID ) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param instanceID   partial service ID	(int)
     * @param gatewayID   registered gw name (eg vitrogw_cti)
     * @param sensorID  registered sensor name (eg 000:111:222)
     * @return   "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,sensor_is_active_flag,sensor_supports_this_capability_flag,capability_is_supported_at_all_flag\n"
     */
    public static void  getSensorCapabilityListForPartialService(int instanceID, String gatewayID, String sensorID ) {

    }

    /**
     *
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param capabilityID    capability ID	(int)
     * @param gatewayID     registered gw name (eg vitrogw_cti)
     * @param sensorID    registered sensor name (eg 000:111:222)
     * @param serviceID     composed service ID	(int)
     * @return   "date,value\n" records where date is the timestamp of the measurement and value is its value (float).
    Note that for CTI multiple measurements may have identical timestamps due to uberdust updating its measurement with low frequency or not at all (for some sensors).
    This could be overriden/fixed if we instead show the timestamp for the time the value was sent from the VGW or the time it was received at the VSP.
     */
    public static void  getDataCapabilityForComposedService(int capabilityID, String gatewayID, String sensorID, int serviceID) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param capabilityID    capability ID	(int)
     * @param gatewayID     registered gw name (eg vitrogw_cti)
     * @param sensorID    registered sensor name (eg 000:111:222)
     * @param instanceID  partial service ID	(int)
     * @return   "date,value\n" records where date is the timestamp of the measurement and value is its value (float).
     */
    public static void  getDataCapabilityForPartialService(int capabilityID, String gatewayID, String sensorID, int instanceID) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * Get most recent observation value for capability of composite service
     * @param capabilityID    capability ID	(int)
     * @param gatewayID     registered gw name (eg vitrogw_cti)
     * @param sensorID    registered sensor name (eg 000:111:222)
     * @param serviceID     composed service ID	(int)
     * @return   "date,value\n" records where date is the timestamp of the measurement and value is its value (float).
     */
    public static void getMostRecentDataCapabilityForComposedService(int capabilityID, String gatewayID, String sensorID, int serviceID) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * Get most recent observation value for capability of partial service
     * @param capabilityID    capability ID	(int)
     * @param gatewayID     registered gw name (eg vitrogw_cti)
     * @param sensorID    registered sensor name (eg 000:111:222)
     * @param instanceID     partial service ID	(int)
     * @return   "date,value\n" records where date is the timestamp of the measurement and value is its value (float).
     */
    public static void getMostRecentDataCapabilityForPartialService(int capabilityID, String gatewayID, String sensorID, int instanceID) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param serviceID: composed service ID (integer id from DB)
     * @return "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,cap_supported_flag\n"
     *  partServ_id indicates the partial service within the requested composed service,
        cap_id_in_db is the capability id in the DB
        cap_name is the full capability name (eg 'urn:x-ogc:def:phenomenon:IDAS:1.0:luminousIntensity')
        func_name is the functions name (eg min, max, last)
        trigger_flag is 0 or 1 indicating whether there is an alert connected to this capability definition
        trigger_comparison_sign is "gt" by default meaning a value has to be "greater than" to trigger an alert.
        trigger_value is a number (the threshold for the trigger)
        cap_supported_flag is 0 or 1 indicating whether the capability is (still) supported by the framework
     */
    public static void  getComposedServiceCapabilityList(int serviceID) {

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param instanceID  partial service id in DB
     * @return "partServ_id,cap_id_in_db,cap_name,func_name,trigger_flag,trigger_comparison_sign,trigger_value,cap_supported_flag\n" records
     */
    public static void  getPartialServiceCapabilityList(int instanceID){

    }

    /**
     * TODO: will not return void when implemented  (nor CSV values. It should return objects, or lists of objects etc
     * @param serviceID composed service ID	(int)
     * @return "partialServiceId1,..., partialServiceId2" : CSV of all the ids of partial services
     */
    public static void  getPartialServicesForComposedServiceID(int serviceID)  {

    }

}
