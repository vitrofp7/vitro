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

import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.ws4d.coap.interfaces.CoapChannel;
import org.ws4d.coap.interfaces.CoapRequest;
import org.ws4d.coap.messages.CoapMediaType;

/**
 * Well-Known CoRE support (draft-ietf-core-link-format-05)
 * 
 * @author Nico Laum <nico.laum@uni-rostock.de>
 * @author Christian Lerche <christian.lerche@uni-rostock.de>
 */
public class CoreResource implements CoapResource {
	/* use the logger of the resource server */
	private final static Logger logger = Logger.getLogger(CoapResourceServer.class); 
    private final static String uriPath = "/.well-known/core";
    private HashMap<Resource, String> coreStrings = new HashMap<Resource, String>();
    ResourceServer serverListener = null; 
    CoapResourceServer server = null;
    
    public CoreResource (CoapResourceServer server){
    	this.server = server;
    }
    
    
    /*Hide*/
    @SuppressWarnings("unused")
	private CoreResource (){
    }
    
   
    public String getMimeType() {
        return null;
    }

    
    public String getPath() {
        return uriPath;
    }

  
    public String getShortName() {
        return getPath();
    }

    
    public byte[] getValue() {
        return buildCoreString(null).getBytes();
    }

    public void registerResource(Resource resource) {
        if (resource != null) {
            StringBuilder coreLine = new StringBuilder();
            coreLine.append("<");
            coreLine.append(resource.getPath());
            coreLine.append(">");
            // coreLine.append(";ct=???");
            coreLine.append(";rt=\"" + resource.getResourceType() + "\"");
            // coreLine.append(";if=\"observations\"");
            coreStrings.put(resource, coreLine.toString());
        }
    }
    
    private String buildCoreString(String resourceType) {
    	/* TODO: implement filtering also with ct and if*/
    	HashMap<String, Resource> resources = server.getResources();
    	StringBuilder returnString = new StringBuilder();
    	for (Resource resource : resources.values()){
    		if (resourceType == null || resource.getResourceType() == resourceType) {
    			returnString.append("<");
    			returnString.append(resource.getPath());
    			returnString.append(">");
                // coreLine.append(";ct=???");
    			if (resource.getResourceType() != null) {
    				returnString.append(";rt=\"" + resource.getResourceType() + "\"");
    			}
                // coreLine.append(";if=\"observations\"");
    			returnString.append(",");
    		}
    		
    	}
        return returnString.toString();
    }

   
    public byte[] getValue(Vector<String> queries) {
	for (String query : queries) {
	    if (query.startsWith("rt=")) return buildCoreString(query.substring(3)).getBytes();
	}
	return getValue();
    }

	
	public String getResourceType() {
		// TODO implement
		return null;
	}

	
	public CoapMediaType getCoapMediaType() {
		return CoapMediaType.link_format;
	}

	
	public void post(byte[] data) {
		/* nothing happens in case of a post */
		return;		
	}

	
	public void changed() {
		
	}


	public void registerServerListener(ResourceServer server) {
		this.serverListener = server;
	}


	public void unregisterServerListener(ResourceServer server) {
		this.serverListener = null;
	}

	
	public boolean addObserver(CoapRequest request) {
		// TODO: implement. Is this resource observeable? (should)
		return false;
	}



	public void removeObserver(CoapChannel channel) {
		// TODO: implement. Is this resource observeable? (should)
	}



	public boolean isObservable() {
		return false;
	}
	
	public int getObserveSequenceNumber(){
		return 0;
	}

	
	public long expires() {
		/* expires never */
		return -1;
	}


	public boolean isExpired() {
		return false;
	}
}
