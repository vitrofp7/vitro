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
package com.thalesgroup.tai.sensors;

public class SoSClient {

    private String serverEndpoint = "";

    public SoSClient(String serverEndpoint) {
        this.serverEndpoint = serverEndpoint;
    }

    public void doRegisterSensor(String id, String resourceName, boolean isObservable ) {
        System.out.println("\nSOS: Registering sensor. Paolo !!!");
        System.out.println("\n   id="+id);
        System.out.println("\n   name="+resourceName);
        //		System.out.println("\n   contenttype="+contentType);
        System.out.println("isobservable=" + isObservable);

    }

    public void doAddObservation(String id, String contentType, byte[] payload) {
        System.out.println("\nSOS: Adding observation. Paolo !!!");
        System.out.println("\n   id="+id);
        System.out.println("\n   contenttype="+contentType);
        System.out.println("\n   payloadsize="+payload.length);
    }
}
