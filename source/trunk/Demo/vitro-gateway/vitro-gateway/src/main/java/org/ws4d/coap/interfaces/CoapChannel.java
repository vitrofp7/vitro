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
package org.ws4d.coap.interfaces;
/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

import java.net.InetAddress;

import org.ws4d.coap.messages.CoapBlockOption.CoapBlockSize;

public interface CoapChannel {

	public void sendMessage(CoapMessage msg);

	/*TODO: close when finished, & abort()*/
    public void close();
    
    public InetAddress getRemoteAddress();

    public int getRemotePort();
    
    /* handles an incomming message */
    public void handleMessage(CoapMessage message);
    
    /*TODO: implement Error Type*/
	public void lostConnection(boolean notReachable, boolean resetByServer);
	
	
	public CoapBlockSize getMaxReceiveBlocksize();

	public void setMaxReceiveBlocksize(CoapBlockSize maxReceiveBlocksize);

	public CoapBlockSize getMaxSendBlocksize();

	public void setMaxSendBlocksize(CoapBlockSize maxSendBlocksize);
}
