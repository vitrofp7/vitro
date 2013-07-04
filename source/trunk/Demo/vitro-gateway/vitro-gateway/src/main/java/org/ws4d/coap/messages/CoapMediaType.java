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

public enum CoapMediaType {
    text_plain (0),		//text/plain; charset=utf-8
    link_format (40),	//application/link-format
    xml(41),			//application/xml
    octet_stream (42),	//application/octet-stream
    exi(47),			//application/exi
    json(50),			//application/json
    UNKNOWN (-1);
    
    int mediaType;
    private CoapMediaType(int mediaType){
    	this.mediaType = mediaType;
    }
    
    public static CoapMediaType parse(int mediaType){
    	switch(mediaType){
    	case 0: return text_plain; 
    	case 40:return link_format; 
    	case 41:return xml;
    	case 42:return octet_stream;
    	case 47:return exi;
    	case 50:return json;
    		default: return UNKNOWN;
    	}
    }
    
    public int getValue(){
    	return mediaType;
    }
}
