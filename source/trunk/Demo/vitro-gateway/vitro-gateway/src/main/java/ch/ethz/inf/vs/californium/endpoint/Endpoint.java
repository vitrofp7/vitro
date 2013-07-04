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
package ch.ethz.inf.vs.californium.endpoint;

import java.io.IOException;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Communicator;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.MessageHandler;
import ch.ethz.inf.vs.californium.coap.MessageReceiver;
import ch.ethz.inf.vs.californium.coap.Request;

/**
 * The abstract class Endpoint is the basis for the server-sided
 * {@link LocalEndpoint} and the client-sided {@link RemoteEndpoint} skeleton.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public abstract class Endpoint implements MessageReceiver, MessageHandler {

// Logging /////////////////////////////////////////////////////////////////////
		
	protected static final Logger LOG = Logger.getLogger(Endpoint.class.getName());

// Members /////////////////////////////////////////////////////////////////////
	
	protected Resource rootResource;

// Methods /////////////////////////////////////////////////////////////////////
	
	public abstract void execute(Request request) throws IOException;

	public int resourceCount() {
		return rootResource != null ? rootResource.subResourceCount() + 1 : 0;
	}

//	@Override YOANN COMMENT
	public void receiveMessage(Message msg) {
		msg.handleBy(this);
	}
	
	public int port() {
		return Communicator.getInstance().port();
	}

}
