package ch.ethz.inf.vs.californium.examples.ipso;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a part of the IPSO profile.
 * 
 * @author Matthias Kovatsch
 */
public class DeviceModel extends LocalResource {

	public DeviceModel() {
		super("dev/mdl");
		setTitle("Model");
		setResourceType("ipso:dev-mdl");
		setInterfaceDescription("core#rp");
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, "Californium", MediaTypeRegistry.TEXT_PLAIN);
	}
}
