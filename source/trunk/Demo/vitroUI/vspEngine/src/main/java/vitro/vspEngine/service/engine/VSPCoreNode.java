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

import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 */
public interface VSPCoreNode {

    /**
    * To be called when starting the runtime
    */
    public void startEngine();

    /**
     * To be called before terminating the runtime. Terminate active connections and do general clean up.
     */
    public void stopEngine();

    /**
     * Sends a direct command message to a VGW
     * TODO: we could use a multicast version of this too (send to a list of gateways)
     * TODO: optional compression could be applied here
     * @param GWId the VGW ID (registered ID)
     * @param CommandContent
     */
    public void sendDirectCommand(String GWId, String CommandContent);

    /**
     *
     * @param GWId
     * @return
     */
    public List<Capability> getCapabilitiesForGW(String GWId);

    /**
     *
     * @param GWId
     * @return
     */
    public List<SmartNode> getSmartNodesForGW(String GWId);

    /**
     *
     * @return
     */
    public List<Capability> getAllCapabilitiesOverall();

    /**
     *
     * @return
     */
    public HashMap<String, GatewayWithSmartNodes> getGatewaysToSmartDevsHM();

    /**
     *
     * @return
     */
    public HashMap<String, Vector<SensorModel>> getCapabilitiesTable();

    /**
     *
     * @param reqCapabilities
     * @return
     */
    public HashMap<String, Vector<SensorModel>> getSensorModelsForCapabilities(List<Capability> reqCapabilities);

    /**
     *
     * @param arrayOfGWIds
     */
    public void requestStatusUpdateFromGWs(List<String> arrayOfGWIds);

    /**
     *
     * @param arrayOfGWIds
     */
    public void purgeResourcesFromGWs(List<String> arrayOfGWIds);

    /**
     *
     * @param ServiceDefinition
     */
    public void sendSubscriptionForCompositeService(String ServiceDefinition);

    /**
     * TODO: Should not return void!
     * @param userId
     */
    public void getActiveVSNsForUser(String userId);

    /**
     * TODO: could probably need more credentials here
     * Should add to DCA and local database.
     * TODO: gateways locally registered should be synched with DCA registered! (bi-directionally and at engine start)
     * @param VGW
     */

    public void registerVGW(String VGW);

    /**
     * After registration negotiation should take place before enabling a VGW to participate in a VSN
     * @param VGWID
     */
    public void negotiateVGW(String VGWID);

    /**
     * Disables or enables a VGW. A disabled VGW cannot be used for VSNs. A VGW that is disabled while being used in VSNs, will depart from those VSNs.
     * @param VGWID
     * @param enableFlag
     */
    public void enableVGW(String VGWID, boolean enableFlag);

    /**
     * Returns enabled status of a VGW
     * @param VGWID
     */
    public boolean isVGWEnabled(String VGWID);


    /**
     * TODO: should check if active VSNs that need the VGW,
     *                      if it can "break" those (re-init resources feasibility eval)
     *                      if any active connections/topics should be removed etc.
     *                      if the VGW should be informed about this deletion (if still active)
     * @param VGW
     */
    public void deleteVGW(String VGW);

    /**
     *
     * @param VSPsCredentials   is an array with a list of the credentials for the VSPs to collaborate with (each entry is the full credentials per VSP)
     */
    public void requestCollaborationWithOtherVSPs(List<String> VSPsCredentials);

    /**
     *
     * @param VSPsCredentials   is an array with a list of the credentials for the VSPs to end collaboration with (each entry is the full credentials per VSP)
     */
    public void endCollaborationWithOtherVSPs(List<String> VSPsCredentials);


    /**
     * TODO: should have meaningful parameters
     */
    public void addBillingPolicy();

    /**
     * TODO: should not return void
     */
    public void getBillingPolicy();

    /**
     * TODO: should not return void
     */
    public void updateBillingPolicy();

    /**
     * TODO: should not return void
     */
    public void removeBillingPolicy();

    /**
     * TODO: Feasibility status and pricing class instead of void
     */
    public void negotiateVSN();



}
