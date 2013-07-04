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

import ch.ethz.inf.vs.californium.coap.Message;


public abstract class UpperLayer extends Layer {

	public void sendMessageOverLowerLayer(Message msg) throws IOException {

		// check if lower layer assigned
		if (lowerLayer != null) {

			lowerLayer.sendMessage(msg);
		} else {
			System.out.printf("[%s] ERROR: No lower layer present", getClass()
					.getName());
		}
	}

	public void setLowerLayer(Layer layer) {

		// unsubscribe from old lower layer
		if (lowerLayer != null) {
			lowerLayer.unregisterReceiver(this);
		}

		// set new lower layer
		lowerLayer = layer;

		// subscribe to new lower layer
		if (lowerLayer != null) {
			lowerLayer.registerReceiver(this);
		}
	}

	public Layer getLowerLayer() {
		return lowerLayer;
	}

	private Layer lowerLayer;
}
