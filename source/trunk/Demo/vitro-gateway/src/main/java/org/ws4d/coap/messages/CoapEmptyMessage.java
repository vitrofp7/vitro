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
package org.ws4d.coap.messages;

/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

public class CoapEmptyMessage extends AbstractCoapMessage {

	public CoapEmptyMessage(byte[] bytes, int length){
		this(bytes, length, 0);
	}
	
	public CoapEmptyMessage(byte[] bytes, int length, int offset){
		serialize(bytes, length, offset);
		/* check if response code is valid, this function throws an error in case of an invalid argument */
		if (this.messageCodeValue != 0){
			throw new IllegalArgumentException("Not an empty CoAP message.");
		}
		
		if (length != HEADER_LENGTH){
			throw new IllegalArgumentException("Invalid length of an empty message");
		}
	}

	public CoapEmptyMessage(CoapPacketType packetType, int messageId) {
		this.version = 1;
		this.packetType = packetType;
		this.messageCodeValue = 0;
		this.messageId = messageId;		
	}

	
	public boolean isRequest() {
		return false;
	}

	
	public boolean isResponse() {
		return false;
	}

	
	public boolean isEmpty() {
		return true;
	}

}
