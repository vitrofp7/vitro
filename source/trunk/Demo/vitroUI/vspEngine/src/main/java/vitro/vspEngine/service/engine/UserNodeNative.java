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
package vitro.vspEngine.service.engine;

import org.apache.log4j.Logger;
import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.communication.UserNodeCommandMQMessageConsumerProducer;
import vitro.vspEngine.service.query.IndexOfQueries;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * TEMP class. Will be removed eventually since we don't need separate "engines". Just separate ways to communicate with VGWs
 */
public class UserNodeNative implements VSPCoreNode {
    private Logger LOG;
    private String logLevel = "info";

    private static UserNodeNative myUserNode = null;
    private String myName;
    private String peerID;
    private HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM;    // will contain maps from a gateway id to GatewayWithSmartNodes  objects
    private HashMap<String, Vector<SensorModel>> capHMap;    // will store the "Generic capability id"/"SensorModel id" pairs of the capabilities found on all gateways

    private boolean channelDCAUsed = false;      // At this point we keep both communication channels open in parallel,
    // but in the end, only one should be kept and it should be possible to switch
    // via some Factory pattern

    /**
     * Constructor.
     * Creates a new instance of UserNodeNative
     */
    private UserNodeNative() {
        LOG = Logger.getLogger(this.getClass());
        System.out.println("Instantiating a User Node Native!");
        gatewaysToSmartDevsHM = new HashMap<String, GatewayWithSmartNodes>();
        //capHMap = (Map)Collections.synchronizedMap(new HashMap<String, Vector<SensorModel>>());
        capHMap = new HashMap<String, Vector<SensorModel>>();
    }


    /**
     * This is the function the world uses to get the UserNode engine object.
     * It follows the Singleton pattern
     */
    public static UserNodeNative getUserNode() {
        if (myUserNode == null) {
            myUserNode = new UserNodeNative();
            // TODO: should not set this as fixed!!!!
            myUserNode.myName = "Test VITRO app node";
            myUserNode.peerID = "TestAppIDUnique";

        }
        return myUserNode;
    }

    @Override
    public void startEngine() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void stopEngine() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendDirectCommand(String gateID, String CommandContent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Capability> getCapabilitiesForGW(String GWId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SmartNode> getSmartNodesForGW(String GWId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashMap<String, GatewayWithSmartNodes> getGatewaysToSmartDevsHM() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashMap<String, Vector<SensorModel>> getCapabilitiesTable() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Capability> getAllCapabilitiesOverall() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public HashMap<String, Vector<SensorModel>> getSensorModelsForCapabilities(List<Capability> reqCapabilities) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void requestStatusUpdateFromGWs(List<String> arrayOfGWIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void purgeResourcesFromGWs(List<String> arrayOfGWIds) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void sendSubscriptionForCompositeService(String ServiceDefinition) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getActiveVSNsForUser(String userId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void registerVGW(String VGW) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void negotiateVGW(String VGWID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void enableVGW(String VGWID, boolean enableFlag) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isVGWEnabled(String VGWID) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteVGW(String VGW) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void requestCollaborationWithOtherVSPs(List<String> VSPsCredentials) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void endCollaborationWithOtherVSPs(List<String> VSPsCredentials) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void updateBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeBillingPolicy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void negotiateVSN() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
