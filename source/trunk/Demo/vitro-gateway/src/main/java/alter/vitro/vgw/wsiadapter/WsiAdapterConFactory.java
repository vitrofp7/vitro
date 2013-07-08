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
 */
public class WsiAdapterConFactory {
    /**
     * Creates a new instance of WsiAdapterCon. TODO: Both parameters should be parsed from a Config.txt file
     *
     * @param WsnMiddleWType String description of the Middleware or controllers that lie beneath
     * @param dbConInf       Info about the DBMS the middleware will be using (or the connection to a storage/brokering facility), as well as info on any access credentials for it.
     *
     */
    public static  WsiAdapterCon createMiddleWCon(String WsnMiddleWType, DbConInfo dbConInf) {


        if (WsnMiddleWType.equalsIgnoreCase("vitro")) {
            return WsiAdapterConFactory.createVitroCon(dbConInf);
        }
        else if(WsnMiddleWType.equalsIgnoreCase("uberdust")){
            return WsiAdapterConFactory.createUberDustCon(dbConInf);
        }
        else if(WsnMiddleWType.equalsIgnoreCase("wsiadapter")){
            return WsiAdapterConFactory.createWsiAdapter(dbConInf);
        }
        else if(WsnMiddleWType.equalsIgnoreCase("HAIwsiAdapter")){
            return WsiAdapterConFactory.createHAIWsiAdapter(dbConInf);
        }
        else if(WsnMiddleWType.equalsIgnoreCase("TCSWSIAdapter")){
            return WsiAdapterConFactory.createWsiTCSCoapAdapterCon(dbConInf);
        }
        else {
            return WsiAdapterConFactory.createGenericCon(dbConInf);
        }
    }

    private static  WsiAdapterCon createVitroCon(DbConInfo dbConInf) {
        return WsiVITROCon.getWsiAdapterCon(dbConInf);
    }

    private static  WsiAdapterCon createUberDustCon(DbConInfo dbConInf) {
        return WsiUberDustCon.getWsiAdapterCon(dbConInf);
    }
    
    private static  WsiAdapterCon createWsiAdapter(DbConInfo dbConInf) {
        return WsiGeneralAdapterCon.getWsiAdapterCon(dbConInf);
    }
    
    private static  WsiAdapterCon createHAIWsiAdapter(DbConInfo dbConInf) {
        return WsiHAICoapAdapterCon.getWsiAdapterCon(dbConInf);
    }

    private static WsiAdapterCon createWsiTCSCoapAdapterCon(DbConInfo dbConInf){
        return WsiTCSCoapAdapterCon.getWsiTCSCoapAdapterCon(dbConInf);
    }
    //
    // If we don't know the exact middleware, we build a generic  WsiAdapterCon object
    // * Normally we should never get to this point
    //
    private static  WsiAdapterCon createGenericCon(DbConInfo dbConInf) {
        System.out.println("Unspecified MiddleWare Type");
        System.exit(1); //?? maybe a better way to handle generic MiddleWare ??? (or direct connection to DB ?????)
        return null;
    }

}
