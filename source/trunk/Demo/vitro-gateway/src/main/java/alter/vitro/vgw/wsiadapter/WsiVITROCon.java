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
import alter.vitro.vgw.service.query.wrappers.QueriedMoteAndSensors;
import alter.vitro.vgw.service.query.wrappers.ReqFunctionOverData;
import alter.vitro.vgw.service.query.wrappers.ReqResultOverData;

import java.util.Vector;

/**
 */
public class WsiVITROCon extends WsiAdapterCon{
    /**
     * Creates a new instance of WsiUberDustCon
     */
    private WsiVITROCon() {
        //
        //
        super();
    }

    private static WsiVITROCon myCon = null;

    /**
     * This is the function the world uses to get the Connection to the Data from the WSN.
     * It follows the Singleton pattern
     */
    public static WsiVITROCon getWsiAdapterCon(DbConInfo databaseConnInfo) {
        if (myCon == null) {
            myCon = new WsiVITROCon();
            myCon.setDbConInfo(databaseConnInfo);
        }
        return myCon;
    }


    /**
     * TODO: IMPORTANT !!!
     *
     * Updates the list with the controlled WSI's capabilities
     */
    public synchronized CGatewayWithSmartDevices createWSIDescr(CGateway givGatewayInfo) {
        //
        //
        //

        return null;
    }

    /**
     * TODO: Should set the interval in which the Gateway should re-send its description.
     * Adds provision for periodic updates
     */
    public void setUpdateDescrInterval() {
        //
        //
        //
    }

    /**
     * Should translate the aggregated query to the appropriate type according to the middleware underneath
     * and return appropriate readings/values. The type of values should be stored elsewhere (at the external middleware (IDAS, backup app?))
     *
     * @param motesAndTheirSensorAndFunctsVec
     * @param reqFunctionVec        Vector with Functions to be applied to query data
     * @return a Vector of the Results as ReqResultOverData structures (XML)
     */
    // TODO: Important
    public synchronized Vector<ReqResultOverData> translateAggrQuery(Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFunctsVec, Vector<ReqFunctionOverData> reqFunctionVec) {
        return null;
        // TODO: or throw an unimplemented Exception!
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
     * Will initiate a query (CoAP) to all sensors in the parameter list for trust routing info (pfi of their parents) and will return the result
     * @param nodeIdsToQuery a list of node ids to query
     * @return a vector of InfoOnTrustRouting objects
     */
    public Vector<InfoOnTrustRouting> findRealTimeTrustInfoOnNodes(Vector<String> nodeIdsToQuery, int secondBetweenIssuingToAnotherNode){
        Vector<InfoOnTrustRouting> retVec = new Vector<InfoOnTrustRouting>();
        if(isTrustCoapMessagingModeSupported() && isTrustCoapMessagingActive())     {
            //   TODO: implement querying sensors for trust routing info (if supported)
            ;
        }
        return retVec;
    }

    /**
     * TODO: implement if DTN is support. Should specify what happens when DTN is activated
     */
    private void   handleDTNActivation(boolean value) {
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
