package ch.ethz.inf.vs.californium.examples.ipso;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a part of the IPSO profile.
 * 
 * @author Matthias Kovatsch
 */
public class PowerDimmer extends LocalResource {
	
	private static int percent = 100;
	
	public static int getDimmer() {
		return percent;
	}

	public PowerDimmer() {
		super("pwr/dim");
		setTitle("Load Dimmer");
		setResourceType("ipso:pwr-dim");
		setInterfaceDescription("core#a");
		isObservable(true);
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, Integer.toString(percent), MediaTypeRegistry.TEXT_PLAIN);
	}
	
	@Override
	public void performPUT(PUTRequest request) {

		if (request.getContentType()!=MediaTypeRegistry.TEXT_PLAIN) {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "text/plain only");
			return;
		}
		
		int pl = Integer.parseInt(request.getPayloadString());
		if (pl>=0 && pl<=100) {
			if (percent==pl) return;
			
			percent = pl;
			request.respond(CodeRegistry.RESP_CHANGED);
			
			changed();
		} else {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "use 0-100");
		}
	}
}
