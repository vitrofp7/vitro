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
package ch.ethz.inf.vs.californium.layers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.MessageReceiver;

/**
 * An abstract Layer class that enforced a uniform interface for building a
 * layered communications stack.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public abstract class Layer implements MessageReceiver {

// Logging /////////////////////////////////////////////////////////////////////
	
	protected static final Logger LOG = Logger.getLogger(Layer.class.getName());

// Members /////////////////////////////////////////////////////////////////////

	private List<MessageReceiver> receivers;
	protected int numMessagesSent;
	protected int numMessagesReceived;

// Methods /////////////////////////////////////////////////////////////////////
	
	public void sendMessage(Message msg) throws IOException {

		if (msg != null) {
			doSendMessage(msg);
			++numMessagesSent;
		}
	}

//	@Override YOANN COMMENT
	public void receiveMessage(Message msg) {

		if (msg != null) {
			++numMessagesReceived;
			doReceiveMessage(msg);
		}
	}

	protected abstract void doSendMessage(Message msg) throws IOException;

	protected abstract void doReceiveMessage(Message msg);

	protected void deliverMessage(Message msg) {

		// pass message to registered receivers
		if (receivers != null) {
			for (MessageReceiver receiver : receivers) {
				receiver.receiveMessage(msg);
			}
		}
	}

	public void registerReceiver(MessageReceiver receiver) {

		// check for valid receiver
		if (receiver != null && receiver != this) {

			// lazy creation of receiver list
			if (receivers == null) {
				receivers = new ArrayList<MessageReceiver>();
			}

			// add receiver to list
			receivers.add(receiver);
		}
	}

	public void unregisterReceiver(MessageReceiver receiver) {

		// remove receiver from list
		if (receivers != null) {
			receivers.remove(receiver);
		}
	}

	public int getNumMessagesSent() {
		return numMessagesSent;
	}

	public int getNumMessagesReceived() {
		return numMessagesReceived;
	}
}
