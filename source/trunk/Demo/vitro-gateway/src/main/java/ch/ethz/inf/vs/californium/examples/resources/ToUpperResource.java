package ch.ethz.inf.vs.californium.examples.resources;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This class implements a 'toUpper' resource for demonstration purposes.
 * Defines a resource that returns a POSTed string in upper-case letters.
 *  
 * @author Matthias Kovtsch
 * 
 */
public class ToUpperResource extends LocalResource {

	public ToUpperResource() {
		super("toUpper");
		setTitle("POST text here to convert it to uppercase");
		setResourceType("UppercaseConverter");
	}

	@Override
	public void performPOST(POSTRequest request) {
		
		if (request.getContentType()!=MediaTypeRegistry.TEXT_PLAIN) {
			request.respond(CodeRegistry.RESP_UNSUPPORTED_MEDIA_TYPE, "Use text/plain");
			return;
		}

		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, request.getPayloadString().toUpperCase(), MediaTypeRegistry.TEXT_PLAIN);
	}
}
