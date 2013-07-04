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
package alter.vitro.vgw.model;

/**
 * Author: antoniou
 */

import java.util.List;

public class CGatewayWithSmartDevices {

    CGateway mGateway;
    List<CSmartDevice> mSmartDevVec;

    /**
     * Creates a new instance of GatewayWithSmartDevices
     */
    public CGatewayWithSmartDevices(CGateway givGateway, List<CSmartDevice> givSmartDevVec) {
        mGateway = givGateway;
        mSmartDevVec = givSmartDevVec;
    }

    public CGateway getGateway() {
        return mGateway;
    }

    public List<CSmartDevice> getSmartDevVec() {
        return mSmartDevVec;
    }

    public synchronized void setGateway(CGateway mGateway) {
        this.mGateway = mGateway;
    }

    public synchronized void setSmartDevVec(List<CSmartDevice> mSmartDevVec) {
        this.mSmartDevVec = mSmartDevVec;
    }
}
