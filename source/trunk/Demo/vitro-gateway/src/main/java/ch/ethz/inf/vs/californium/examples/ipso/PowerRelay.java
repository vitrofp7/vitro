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
public class PowerRelay extends LocalResource {
	
	private static boolean on = true;
	
	public static boolean getRelay() {
		return on;
	}

	public PowerRelay() {
		super("pwr/rel");
		setTitle("Load Relay");
		setResourceType("ipso:pwr-rel");
		setInterfaceDescription("core#a");
		isObservable(true);
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, on?"1":"0", MediaTypeRegistry.TEXT_PLAIN);
	}
	
	@Override
	public void performPUT(PUTRequest request) {

		if (request.getContentType()!=MediaTypeRegistry.TEXT_PLAIN) {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "text/plain only");
			return;
		}
		
		String pl = request.getPayloadString();
		if (pl.equals("true") || pl.equals("1")) {
			if (on==true) return;
			on = true;
		} else if (pl.equals("false") || pl.equals("0")) {
			if (on==false) return;
			on = false;
		} else {
			request.respond(CodeRegistry.RESP_BAD_REQUEST, "use true/false or 1/0");
			return;
		}

		// complete the request
		request.respond(CodeRegistry.RESP_CHANGED);
		
		changed();
	}
}
