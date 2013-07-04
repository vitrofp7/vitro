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
package org.ws4d.coap.rest;

import org.ws4d.coap.interfaces.CoapChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.messages.CoapMediaType;

/**
 * @author Nico Laum <nico.laum@uni-rostock.de>
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */
public interface CoapResource extends Resource {
	/* returns the CoAP Media Type */
    public CoapMediaType getCoapMediaType();

    /* called by the application, when the resource state changed -> used for observation */
    public void changed(); 
	
	/* called by the server to register a new observer, returns false if resource is not observable */
	public boolean addObserver(CoapRequest request);
	
	/* removes an observer from the list */
	public void  removeObserver(CoapChannel channel);
	
	/* returns if the resource is observable */
	public boolean  isObservable();
	
	/* returns if the resource is observable */
	public int getObserveSequenceNumber();
	
	/* returns the unix time when resource expires, -1 for never */
	public long expires();
	public boolean isExpired();
}
