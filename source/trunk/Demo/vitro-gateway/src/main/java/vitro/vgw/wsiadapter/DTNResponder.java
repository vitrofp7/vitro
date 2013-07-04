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
package vitro.vgw.wsiadapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import vitro.vgw.wsiadapter.coap.util.Constants;

public class DTNResponder extends Observable implements Runnable {
	private Logger logger = Logger.getLogger(getClass());
	
	private int port;
	private DatagramSocket serverSocket;
	
	private Observer adapterListener;
	
	public DTNResponder(int port, Observer adapterListener) {
		this.port = port;
		this.adapterListener = adapterListener;
	}
	
	public void run() {
		
		try {
			
			this.addObserver(adapterListener);
			
			serverSocket = new DatagramSocket(port);
			while(true) {
				byte[] receiveData = new byte[Constants.DTN_MESSAGE_SIZE];
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				String msgString = new String(receivePacket.getData()).trim();
				logger.info("DTN response: " + msgString);
				
				/** Notify received messages to the Observer */
				setChanged();
				notifyObservers(msgString);
			}
			
		} catch (IOException e) {
			
			logger.error(e.getMessage());
			
		} finally {
			
			if(serverSocket != null) {
				serverSocket.close();
			}
			
			this.deleteObservers();
		}
	}

}
