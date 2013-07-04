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

import java.net.InetAddress;
/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

public interface CoapSocketHandler {
    // public void registerResponseListener(CoapResponseListener
    // responseListener);
    // public void unregisterResponseListener(CoapResponseListener
    // responseListener);
    // public int sendRequest(CoapMessage request);
    // public void sendResponse(CoapResponse response);
    // public void establish(DatagramSocket socket);
    // public void testConfirmation(int msgID);
    //
    // public boolean isOpen();
    /* TODO */
    public CoapClientChannel connect(CoapClient client, InetAddress remoteAddress, int remotePort);

    public void close();

    public void sendMessage(CoapMessage msg);

    public CoapChannelManager getChannelManager();

	int getLocalPort();

	void removeClientChannel(CoapClientChannel channel);

	void removeServerChannel(CoapServerChannel channel);
}
