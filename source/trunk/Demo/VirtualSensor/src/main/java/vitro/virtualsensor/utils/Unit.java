/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis
 * #     Paolo Medagliani
 * #     D. Davide Lamanna
 * #     Panos Trakadas
 * #     Andrea Kropp
 * #     Kiriakos Georgouleas
 * #     Panagiotis Karkazis
 * #     David Ferrer Figueroa
 * #     Francesco Ficarola
 * #     Stefano Puglia
 * #--------------------------------------------------------------------------
 */

package vitro.virtualsensor.utils;

public class Unit {
    public String id = "";
    public String uom = "";
    public String lightCode = "";
    public String associatedPhenomena = "";
    
    @Override public String toString () {
        String res = "Unit: " + id;
        res += "\n\tUOM                 : " + uom;
        res += "\n\tLight Code          : " + lightCode;
        res += "\n\tAssociated phenomena: " + associatedPhenomena;
        return res;
    }
}