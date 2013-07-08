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
