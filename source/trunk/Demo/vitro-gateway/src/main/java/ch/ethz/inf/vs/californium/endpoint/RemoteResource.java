package ch.ethz.inf.vs.californium.endpoint;

import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;

/**
 * The class RemoteResource is currently an unimplemented skeleton for a
 * client stub to access a {@link LocalResource} at the server.
 * So far, it can be used as a discovery cache.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class RemoteResource extends Resource {
	
	public RemoteResource(String resourceIdentifier) {
		super(resourceIdentifier);
	}

	public static RemoteResource newRoot(String linkFormat) {
		return LinkFormat.parse(linkFormat);
	}

	@Override
	public void createSubResource(Request request, String newIdentifier) {
		// TODO Auto-generated method stub

	}

//	@Override YOANN COMMENT
	public void performDELETE(DELETERequest request) {
		// TODO Auto-generated method stub

	}

//	@Override YOANN COMMENT
	public void performGET(GETRequest request) {
		// TODO Auto-generated method stub

	}

//	@Override YOANN COMMENT
	public void performPOST(POSTRequest request) {
		// TODO Auto-generated method stub

	}

//	@Override YOANN COMMENT
	public void performPUT(PUTRequest request) {
		// TODO Auto-generated method stub

	}

}
