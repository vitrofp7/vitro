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

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 * Date: 5/18/12
 * Time: 9:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbConInfoFactory {

    public static DbConInfo createConInfo(String dbmsType) {

        if (dbmsType.equalsIgnoreCase("mysql")) {
            return DbConInfoFactory.createMySQLConInfo();
        }
        else if (dbmsType.equalsIgnoreCase("restHttp")) {
            return DbConInfoFactory.createRestHttpConInfo();
        } else {
            return DbConInfoFactory.createGenericConInfo();
        }
    }

    private static DbConInfo createMySQLConInfo() {
        return new MySQLConInfo();
    }

    private static DbConInfo createRestHttpConInfo() {
        return new RestHttpConInfo();
    }

    private static DbConInfo createGenericConInfo() {
        DbConInfo tmpDbConInfo = new DbConInfo(null, null, null, null, null, null);
        tmpDbConInfo.setConnProtocol("generic"); // Normally we should never come to this (when do we???)
        return tmpDbConInfo;
    }
}
