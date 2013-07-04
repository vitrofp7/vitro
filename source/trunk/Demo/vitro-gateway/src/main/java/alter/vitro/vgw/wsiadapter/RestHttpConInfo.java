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
package alter.vitro.vgw.wsiadapter;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 * Date: 5/18/12
 * Time: 9:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class RestHttpConInfo extends DbConInfo {

        public RestHttpConInfo() {
            super(null, null, null, null, "http", null);
        }

        public RestHttpConInfo(String mysDBIp, String mysDbName, String mysDBusername, String mysDBpasswd, String mysExtraInfo) {
            // set the protocol to http
            super(mysDBIp, mysDbName, mysDBusername, mysDBpasswd, "http", mysExtraInfo);
        }

}
