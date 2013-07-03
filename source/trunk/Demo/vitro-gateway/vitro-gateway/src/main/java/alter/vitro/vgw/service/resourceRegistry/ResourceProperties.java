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
package alter.vitro.vgw.service.resourceRegistry;

import alter.vitro.vgw.model.CSensorModel;

import java.util.HashMap;
import java.util.List;

/**
 * TODO: this class could be used to define availability of a node's resources (and release them when a VSN is done)
 * For now it is used to declare a node completely available or unavailable (used by the continuation service).
 */
public class ResourceProperties {
    private String nodeId;
    private boolean enabled;
    private long timeStampEnabledStatusRemotelySynch; //the timestamp contained on the VSP message
    private long timeStampEnabledStatusSynch;   // the time a enable/disable command was received at the VGW for this node
    private boolean statusWasDecidedByThisVGW;  //true would be only when explicitly the VGW disables a device, or re-enables it AFTER having disabled it

    private HashMap<String, List<CSensorModel>>  vsnIdToEngagedCapabilities;

    public ResourceProperties(String pNodeId) {
        setNodeId(pNodeId);
        setEnabled(true);
        vsnIdToEngagedCapabilities = new HashMap<String, List<CSensorModel>>();
        setStatusWasDecidedByThisVGW(false);   //true would be only when explicitly the VGW disables a device, or re-enables it AFTER having disabled it
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getTimeStampEnabledStatusSynch() {
        return timeStampEnabledStatusSynch;
    }

    public void setTimeStampEnabledStatusSynch(long timeStampEnabledStatusSynch) {
        this.timeStampEnabledStatusSynch = timeStampEnabledStatusSynch;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public long getTimeStampEnabledStatusRemotelySynch() {
        return timeStampEnabledStatusRemotelySynch;
    }

    public void setTimeStampEnabledStatusRemotelySynch(long timeStampEnabledStatusRemotelySynch) {
        this.timeStampEnabledStatusRemotelySynch = timeStampEnabledStatusRemotelySynch;
    }

    public boolean isStatusWasDecidedByThisVGW() {
        return statusWasDecidedByThisVGW;
    }

    public void setStatusWasDecidedByThisVGW(boolean statusWasDecidedByThisVGW) {
        this.statusWasDecidedByThisVGW = statusWasDecidedByThisVGW;
    }
}
