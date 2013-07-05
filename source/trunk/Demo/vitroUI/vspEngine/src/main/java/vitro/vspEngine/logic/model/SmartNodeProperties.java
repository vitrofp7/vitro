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
package vitro.vspEngine.logic.model;

import java.util.HashMap;
import java.util.List;

/**
 * Similar to ResourceProperties from VGW
 */
public class SmartNodeProperties {
    private String nodeId;
    private boolean enabled;
    private long timeStampEnabledStatusRemotelySynch; //the timestamp contained on the req VSP message
    private long timeStampEnabledStatusSynch;   // the time a enable/disable command was received at the VGW for this node
    private boolean enabledStatusWasInitiatedByVGW;
    //private HashMap<String, List<SensorModel>> vsnIdToEngagedCapabilities;

    public SmartNodeProperties(String pNodeId) {
        this.nodeId = pNodeId;
        setEnabled(true);
        setTimeStampEnabledStatusRemotelySynch(0);
        setEnabledStatusWasInitiatedByVGW(false);
        //vsnIdToEngagedCapabilities = new HashMap<String, List<CSensorModel>>();
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


    public boolean isEnabledStatusWasInitiatedByVGW() {
        return enabledStatusWasInitiatedByVGW;
    }

    public void setEnabledStatusWasInitiatedByVGW(boolean enabledStatusWasInitiatedByVGW) {
        this.enabledStatusWasInitiatedByVGW = enabledStatusWasInitiatedByVGW;
    }
}
