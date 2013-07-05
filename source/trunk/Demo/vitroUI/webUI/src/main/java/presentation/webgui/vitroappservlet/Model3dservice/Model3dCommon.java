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

/*
 * Model3dCommon.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

/**
 *
 * @author antoniou
 */
public class Model3dCommon {
    
    public static boolean isValidColorString(String testColorStr)
    {
        boolean blnResult = true;
        String strValidChars = "0123456789ABCDEFabcdef";
        if (testColorStr == null || testColorStr.length() < 7 || testColorStr.length() > 7) return false;
        //  test strString consists of valid characters listed above
        if(testColorStr.charAt(0) != '#') {
            blnResult = false;
        } else {
            for (int i = 1; i < testColorStr.length() && blnResult == true; i++) {
                char strChar = testColorStr.charAt(i);
                if (strValidChars.indexOf(strChar) == -1) {
                    blnResult = false;
                }
            }
        }
        return blnResult;
    }
}
