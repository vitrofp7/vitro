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
public class DeviceManufacturer extends LocalResource {

	public DeviceManufacturer() {
		super("dev/mfg");
		setTitle("Manufacturer");
		setResourceType("ipso:dev-mfg");
		setInterfaceDescription("core#rp");
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, "ETH Zurich", MediaTypeRegistry.TEXT_PLAIN);
	}
}
