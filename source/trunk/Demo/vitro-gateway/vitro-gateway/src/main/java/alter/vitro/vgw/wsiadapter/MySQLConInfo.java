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
 * Time: 9:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class MySQLConInfo extends DbConInfo {

        public MySQLConInfo() {
            super(null, null, null, null, "mysql", null);
            boolean driverIsRegged = false;
            driverIsRegged = checkDriverReg();
            if (driverIsRegged == false) {
                ;// +++++ throw Some Exception ?????
            }
        }

        public MySQLConInfo(String mysDBIp, String mysDbName, String mysDBusername, String mysDBpasswd, String mysExtraInfo) {
            super(mysDBIp, mysDbName, mysDBusername, mysDBpasswd, "mysql", mysExtraInfo);
            boolean driverIsRegged = false;
            driverIsRegged = checkDriverReg();
            if (driverIsRegged == false) {
                ;// +++++ throw Some Exception ?????
            }
        }

        private boolean checkDriverReg() {
            //
            // In case of mySql DB
            // Check if necessary driver is registered
            //
            System.out.println("Checking if Driver is registered with DriverManager.");
            try {
                Class.forName("com.mysql.jdbc.Driver");
            }
            catch (ClassNotFoundException cnfe) {
                System.out.println("Couldn't find the mySql driver!");
                System.out.println("Let's print a stack trace");
                cnfe.printStackTrace();
                return false;
            }
            System.out.println("Registered the driver ok.");
            return true;
        }
}
