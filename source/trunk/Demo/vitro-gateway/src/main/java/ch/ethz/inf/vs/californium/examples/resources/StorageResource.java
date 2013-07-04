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
package ch.ethz.inf.vs.californium.examples.resources;

import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/*
 * This class implements a 'storage' resource for demonstration purposes.
 * 
 * Defines a resource that stores POSTed data and that creates new
 * sub-resources on PUT request where the Uri-Path doesn't yet point to an
 * existing resource.
 *  
 * @author Dominique Im Obersteg & Daniel Pauli
 * @version 0.1
 * 
 */
public class StorageResource extends LocalResource {

	// Constructors ////////////////////////////////////////////////////////////
	
	/*
	 * Default constructor.
	 */
	public StorageResource() {
		this("storage");
	}
	
	/*
	 * Constructs a new storage resource with the given resourceIdentifier.
	 */
	public StorageResource(String resourceIdentifier) {
		super(resourceIdentifier);
		setTitle("PUT your data here or POST new resources!");
		setResourceType("Storage");
		isObservable(true);
	}

	// REST Operations /////////////////////////////////////////////////////////
	
	/*
	 * GETs the content of this storage resource. 
	 * If the content-type of the request is set to application/link-format 
	 * or if the resource does not store any data, the contained sub-resources
	 * are returned in link format.
	 */
	@Override
	public void performGET(GETRequest request) {

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);

		// check if link format requested
		if (request.getContentType()==MediaTypeRegistry.APPLICATION_LINK_FORMAT || data == null) {

			// respond with list of sub-resources in link format
			response.setPayload(LinkFormat.serialize(this, request.getOptions(OptionNumberRegistry.URI_QUERY), true), MediaTypeRegistry.APPLICATION_LINK_FORMAT);

		} else {

			// load data into payload
			response.setPayload(data);

			// set content type
			if (getContentTypeCode().size()>0) {
				response.setContentType(getContentTypeCode().get(0));
			}
		}

		// complete the request
		request.respond(response);
	}
	
	/*
	 * PUTs content to this resource.
	 */
	@Override
	public void performPUT(PUTRequest request) {

		// store payload
		storeData(request);

		// complete the request
		request.respond(CodeRegistry.RESP_CHANGED);
	}

	/*
	 * POSTs a new sub-resource to this resource.
	 * The name of the new sub-resource is retrieved from the request
	 * payload.
	 */
	@Override
	public void performPOST(POSTRequest request) {

		// get request payload as a string
		String payload = request.getPayloadString();
		
		// check if valid Uri-Path specified
		if (payload != null && !payload.isEmpty()) {

			createSubResource(request, payload);

		} else {

			// complete the request
			request.respond(CodeRegistry.RESP_BAD_REQUEST,
				"Payload must contain Uri-Path for new sub-resource.");
		}
	}

	/*
	 * Creates a new sub-resource with the given identifier in this resource.
	 * Added checks for resource creation.
	 */
	@Override
	public void createSubResource(Request request, String newIdentifier) {
		
		if (request instanceof PUTRequest) {
			request.respond(CodeRegistry.RESP_FORBIDDEN, "PUT restricted to exiting resources");
			return;
		}
		
		// omit leading and trailing slashes
		if (newIdentifier.startsWith("/")) {
			newIdentifier = newIdentifier.substring(1);
		}
		if (newIdentifier.endsWith("/")) {
			newIdentifier = newIdentifier.substring(0, newIdentifier.length()-1);
		}
		
		// truncate from special chars onwards 
		if (newIdentifier.indexOf("/")!=-1) {
			newIdentifier = newIdentifier.substring(0,newIdentifier.indexOf("/"));
		}
		if (newIdentifier.indexOf("?")!=-1) {
			newIdentifier = newIdentifier.substring(0,newIdentifier.indexOf("?"));
		}
		if (newIdentifier.indexOf("\r")!=-1) {
			newIdentifier = newIdentifier.substring(0,newIdentifier.indexOf("\r"));
		}
		if (newIdentifier.indexOf("\n")!=-1) {
			newIdentifier = newIdentifier.substring(0,newIdentifier.indexOf("\n"));
		}
		
		// special restriction
		if (newIdentifier.length()>32) {
			request.respond(CodeRegistry.RESP_FORBIDDEN, "Resource segments limited to 32 chars");
			return;
		}
		
		// rt by query
		String newRtAttribute = null;
		for (Option query : request.getOptions(OptionNumberRegistry.URI_QUERY)) {
			String keyValue[] = query.getStringValue().split("=");
			
			if (keyValue[0].equals("rt") && keyValue.length==2) {
				newRtAttribute = keyValue[1];
				continue;
			}
		}

		// create new sub-resource
		if (getResource(newIdentifier, false)==null) {
			
			StorageResource resource = new StorageResource(newIdentifier);
			if (newRtAttribute!=null) {
				resource.setResourceType(newRtAttribute);
			}
			
			addSubResource(resource);
	
			// store payload
			resource.storeData(request);
	
			// create new response
			Response response = new Response(CodeRegistry.RESP_CREATED);
	
			// inform client about the location of the new resource
			response.setLocationPath(resource.getPath());
	
			// complete the request
			request.respond(response);
			
		} else {
			// defensive programming if someone incorrectly calls createSubResource()
			request.respond(CodeRegistry.RESP_INTERNAL_SERVER_ERROR, "Trying to create existing resource");
			Logger.getAnonymousLogger().severe(String.format("Cannot create sub resource: %s/[%s] already exists", this.getPath(), newIdentifier));
		}
	}
	
	/*
	 * DELETEs this storage resource, if it is not root.
	 */
	@Override
	public void performDELETE(DELETERequest request) {

		// disallow to remove the root "storage" resource
		if (parent instanceof StorageResource) {

			// remove this resource
			remove();

			request.respond(CodeRegistry.RESP_DELETED);
		} else {
			request.respond(CodeRegistry.RESP_FORBIDDEN,
				"Root storage resource cannot be deleted");
		}
	}

	// Internal ////////////////////////////////////////////////////////////////
	
	/*
	 * Convenience function to store data contained in a 
	 * PUT/POST-Request. Notifies observing endpoints about
	 * the change of its contents.
	 */
	private void storeData(Request request) {

		// set payload and content type
		data = request.getPayload();
		clearAttribute(LinkFormat.CONTENT_TYPE);
		setContentTypeCode(request.getContentType());

		// signal that resource state changed
		changed();
	}

	private byte[] data; 
}
