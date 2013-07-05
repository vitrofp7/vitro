package ch.ethz.inf.vs.californium.examples.plugtest;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a test of specification for the
 * ETSI IoT CoAP Plugtests, Paris, France, 24 - 25 March 2012.
 * 
 * @author Matthias Kovatsch
 */
public class LongPath extends LocalResource {

	public LongPath() {
		super("seg1/seg2/seg3");
		setTitle("Long path resource");
	}

	@Override
	public void performGET(GETRequest request) {

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		
		String payload = String.format("Type: %d (%s)\nCode: %d (%s)\nMID: %d",
									   request.getType().ordinal(),
									   request.typeString(),
									   request.getCode(),
									   CodeRegistry.toString(request.getCode()),
									   request.getMID()
									  );
		
		// set payload
		response.setPayload(payload);
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);
		
		// complete the request
		request.respond(response);
	}
}
