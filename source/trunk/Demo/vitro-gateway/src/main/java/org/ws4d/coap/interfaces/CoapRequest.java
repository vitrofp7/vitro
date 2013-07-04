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

import java.util.Vector;

import org.ws4d.coap.messages.CoapMediaType;
import org.ws4d.coap.messages.CoapRequestCode;
/**
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */

public interface CoapRequest extends CoapMessage{

    public void setUriHost(String host);
    public void setUriPort(int port);
    public void setUriPath(String path);
    public void setUriQuery(String query);
    public void setProxyUri(String proxyUri);
    public void setToken(byte[] token);
    
    public void addAccept(CoapMediaType mediaType);
    
    public Vector<CoapMediaType> getAccept(CoapMediaType mediaType);
    public String getUriHost();
    public int getUriPort();
    public String getUriPath();
    public Vector<String> getUriQuery();
    public String getProxyUri();
    
    public void addETag(byte[] etag);
    public Vector<byte[]> getETag();

    public CoapRequestCode getRequestCode();
    public void setRequestCode(CoapRequestCode requestCode);
}
