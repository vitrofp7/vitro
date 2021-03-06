package ch.ethz.inf.vs.californium.examples.plugtest;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;


/*
 * This class implements a 'separate' resource for demonstration purposes.
 * 
 * Defines a resource that returns a response in a separate CoAP Message
 *  
 * @author Dominique Im Obersteg & Daniel Pauli
 * @version 0.1
 * 
 */
public class Separate extends LocalResource {

	public Separate() {
		super("separate");
		setTitle("Resource which cannot be served immediately and which cannot be acknowledged in a piggy-backed way");
	}

	@Override
	public void performGET(GETRequest request) {

		// we know this stuff may take longer...
		// promise the client that this request will be acted upon
		// by sending an Acknowledgement
		request.accept();

		// do the time-consuming computation
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);

		// set payload
		response.setPayload(String.format("Type: %d (%s)\nCode: %d (%s)\nMID: %d",
				  request.getType().ordinal(),
				  request.typeString(),
				  request.getCode(),
				  CodeRegistry.toString(request.getCode()),
				  request.getMID()
				 ));
		response.setContentType(MediaTypeRegistry.TEXT_PLAIN);

		// complete the request
		request.respond(response);
	}
}
