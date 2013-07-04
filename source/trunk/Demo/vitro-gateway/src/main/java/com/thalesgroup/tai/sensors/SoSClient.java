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
