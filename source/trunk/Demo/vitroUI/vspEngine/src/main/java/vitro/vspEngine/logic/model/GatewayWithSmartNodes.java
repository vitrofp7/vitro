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
package vitro.vspEngine.logic.model;


import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class GatewayWithSmartNodes extends Gateway {
    //
    private Vector<SmartNode> smartNodesVec;

    public  GatewayWithSmartNodes(Gateway seedGw)
    {
        super(seedGw);
        this.smartNodesVec = null;
    }


    public Vector<SmartNode> getSmartNodesVec() {
        return smartNodesVec;
    }

    public synchronized void setSmartNodesVec(Vector<SmartNode> pSmartNodesVec) {
        this.smartNodesVec = pSmartNodesVec;
    }

}
