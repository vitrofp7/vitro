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

/**
 * Should create instances of UserNode (the core engine for a VSP service) that would use the IDAS middleware (native)
 * or the direct connectivity (alternative mechanism). (backup).
 * TODO: Also, switching between the two engines could also be supported
 */
public class VSPCoreNodeFactory {
    public static VSPCoreNode createVSPCoreNode(String engineType) {

        if (engineType.equalsIgnoreCase("native")) {
            return VSPCoreNodeFactory.createVSPCoreNodeNative();
        }
        else if (engineType.equalsIgnoreCase("directComm")) {
            return VSPCoreNodeFactory.createVSPCoreNodeDirectComm();
        } else {
            return VSPCoreNodeFactory.createVSPCoreNodeDirectComm();
        }
    }

    private static VSPCoreNode createVSPCoreNodeDirectComm() {
        return UserNode.getUserNode();
    }

    private static VSPCoreNode createVSPCoreNodeNative() {
        return UserNodeNative.getUserNode();
    }

}
