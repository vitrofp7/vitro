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
package alter.vitro.vgw.service.query.wrappers;

import alter.vitro.vgw.service.query.xmlmessages.response.ServContReplcItemType;

/**
 */
public class RespServContinuationReplacementStruct extends ServContReplcItemType {

    public RespServContinuationReplacementStruct(String pSourceId, String pReplacementId, String pCapId) {
        super();
        super.setNodeSourceId(pSourceId);
        super.setNodeReplmntId(pReplacementId);
        super.setCapabilityId(pCapId);
    }

    public RespServContinuationReplacementStruct(ServContReplcItemType rftObj)
    {
        super();
        super.setNodeSourceId(rftObj.getNodeSourceId());
        super.setNodeReplmntId(rftObj.getNodeReplmntId());
        super.setCapabilityId(rftObj.getCapabilityId());
    }

    public String getNodeSourceId() {
        return nodeSourceId;
    }

    public void setNodeSourceId(String pNodeSourceId) {
        super.setNodeSourceId(pNodeSourceId);
    }

    public String getNodeReplmntId() {
        return nodeReplmntId;
    }

    public void setNodeReplmntId(String pNodeReplmntId) {
        super.setNodeReplmntId(pNodeReplmntId);
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String pCapabilityId) {
        super.setCapabilityId(pCapabilityId);
    }

    /**
     * Compares two RespServContinuationReplacementStruct objects.
     * @param targetStruct the target RespServContinuationReplacementStruct to compare to
     * @return true if objects express the same RespServContinuationReplacementStruct, or false otherwise
     */
    public boolean equals(RespServContinuationReplacementStruct targetStruct) {
        if ( (this.getNodeSourceId().compareToIgnoreCase(targetStruct.getNodeSourceId()) != 0) ||
                (this.getNodeReplmntId().compareToIgnoreCase(targetStruct.getNodeReplmntId()) != 0 ) ||
                (this.getCapabilityId().compareToIgnoreCase(targetStruct.getCapabilityId()) != 0 ) )
        {
            return false;
        }
        // at the end
        return true;
    }


}
