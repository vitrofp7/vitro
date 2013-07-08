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
 * An abstract class that defines an "interface" for the connection with the available middleware controller (native VITRO r&s and node configurator or other middleware such as uberdust)
 * This is supposed to provide extensibility to the code, if we chose to interface with many of the
 * contemporary middleware APIs.
 */
abstract public class WsiAdapterCon {
    /**
     * Creates a new instance of WsiAdapterCon
     */
    protected WsiAdapterCon() {
        //
        //
        m_DatabaseConnInfo = null;
        setDTNModeSupported(false); // should be explicitly stated as true
        setTrustCoapMessagingModeSupported(false);  // should be explicitly stated as true
        setDtnPolicy(false);
        setTrustCoapMessagingActive(false);

    }

    //
    // For the initialization of the WsiAdapterCon object.
    // contains info about the database type/username/password etc
    //
    protected static void setDbConInfo(DbConInfo databaseConnInfo) {
        //
        //
        //
        m_DatabaseConnInfo = databaseConnInfo;

    }

    protected static DbConInfo getDbConInfo() {
        //
        //
        //
        return m_DatabaseConnInfo;

    }

    abstract public boolean getDtnPolicy();
    /**
     * is supposed to start the DTN (switch on the DTN mode)
     */
    abstract public void setDtnPolicy(boolean value);

    public  boolean isDTNModeSupported() {
        return DTNModeSupported;
    }

    public  void setDTNModeSupported(boolean DTNModeSupported) {
        this.DTNModeSupported = DTNModeSupported;
    }

    public boolean isTrustCoapMessagingModeSupported() {
        return trustCoapMessagingModeSupported;
    }

    public void setTrustCoapMessagingModeSupported(boolean trustCoapMessagingModeSupported) {
        this.trustCoapMessagingModeSupported = trustCoapMessagingModeSupported;
    }

    abstract public boolean isTrustCoapMessagingActive();
    abstract public void setTrustCoapMessagingActive(boolean value);

    /**
     * Prepares an updated version of the structure required to send registration messages about the controlled WSI's capabilities
     */
    abstract public CGatewayWithSmartDevices createWSIDescr(CGateway givGatewayInfo) ;

    /**
     * Should set the interval in which the Gateway should resent its registration messages
     * TODO: This is not implemented yet.
     */
    abstract public void setUpdateDescrInterval();

    /**
     * Translates the query to an appropriate set of commands that use the selected middleware
     * and returns appropriate readings/values.
     *
     * @param motesAndTheirSensorAndFunctsVec A HashMap that maps the selected mote-ids to be queried, to those of their sensors that will be queried and the functions to be performed on their data
     * @param reqFunctionVec        A vector of the functions that will be applied to the data in the Gateway DB.
     * @return A Vector of ReqResultOverData objects that encapsulate the results to be sent back to the querying peer
     *         grouped by Function description.
     */
    abstract public Vector<ReqResultOverData> translateAggrQuery(Vector<QueriedMoteAndSensors> motesAndTheirSensorAndFunctsVec, Vector<ReqFunctionOverData> reqFunctionVec);

    /**
     * Will initiate a query (CoAP) to all sensors in the parameter list for trust routing info (pfi of their parents) and will return the result
     * @param nodeIdsToQuery a list of node ids to query
     * @return a vector of InfoOnTrustRouting objects
     */
    abstract public Vector<InfoOnTrustRouting> findRealTimeTrustInfoOnNodes(Vector<String> nodeIdsToQuery, int secondBetweenIssuingToAnotherNode);

    // Data members
    private static DbConInfo m_DatabaseConnInfo;
    protected boolean isDtnEnabled;
    private  boolean DTNModeSupported;
    private boolean trustCoapMessagingModeSupported;
    protected boolean trustCoapMessagingActivated;


}



