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

package vitro.virtualsensor;

import java.util.Observable;

/**
 * This class provides a message box for the received data, that is redirected to the Virtual Sensors
 * @author David Ferrer Figueroa
 *
 */

public class MsgBox extends Observable{

    private String uom;
    private SensorInformation lastMessage;

    public MsgBox(){
        lastMessage = new SensorInformation();
        uom = "";
    }
    public MsgBox(String uom){
        this.uom = uom;
        lastMessage = new SensorInformation();
    }

    public String getUom() {
        return uom;
    }

    /**
     * This method forwards the message to all the observers
     * @param message is the data sent to the sensor. It is the whole SML message
     */
    public void forward(SensorInformation message) {
        this.lastMessage = message;
        setChanged();
        notifyObservers(message);	
    }

    public SensorInformation getLastMessage(){
        return this.lastMessage;
    }

    /**
     * This method notifies all of its observers that it is not sending any further data.
     * @return true if succeeded, false otherwise
     */
    public boolean removeMsgBox(){
        try {
                forward(new SensorInformation("Close"));
                return true;
        } catch (Exception e) {
                return false;
        }
    }
}
