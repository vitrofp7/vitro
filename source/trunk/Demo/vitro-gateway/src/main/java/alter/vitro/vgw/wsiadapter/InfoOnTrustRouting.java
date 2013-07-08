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

import java.util.HashMap;

/**
 * class to manage the replies from the coap message on trust routing
 */
public class InfoOnTrustRouting {
    public static final String INVALID_SOURCENODEID = "";
    public static final String INVALID_TIMESTAMP = "";
    public static final String DEFAULT_NODE_IPV6_SHORT_PREFIX = "fec0::";
    private String timestamp;
    private String sourceNodeId;
    private HashMap<String, Integer> parentIdsToPFI;
    private static String nodePrefix = DEFAULT_NODE_IPV6_SHORT_PREFIX;

    public InfoOnTrustRouting() {
        setSourceNodeId(INVALID_SOURCENODEID);
        setTimestamp(INVALID_TIMESTAMP);
        setParentIdsToPFI(new HashMap<String, Integer>());
    }

    public static String getNodePrefix() {
        return nodePrefix;
    }

    public static void setNodePrefix(String nodePrefix) {
        InfoOnTrustRouting.nodePrefix = nodePrefix;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSourceNodeId() {
        return sourceNodeId;
    }

    public void setSourceNodeId(String sourceNodeId) {
        this.sourceNodeId = sourceNodeId;
    }

    public HashMap<String, Integer> getParentIdsToPFI() {
        return parentIdsToPFI;
    }

    public void setParentIdsToPFI(HashMap<String, Integer> parentIdsToPFI) {
        this.parentIdsToPFI = parentIdsToPFI;
    }
}
