package ch.ethz.inf.vs.californium.endpoint;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.ObservingManager;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;

/**
 * The class LocalResource provides the functionality of a CoAP server resource
 * as a subclass of {@link Resource}. Implementations will inherit this class in order
 * to provide custom resources by overriding some the following methods:
 * <ul>
 * <li>{@link #performGET(GETRequest)}
 * <li>{@link #performPOST(POSTRequest)}
 * <li>{@link #performPUT(PUTRequest)}
 * <li>{@link #performDELETE(DELETERequest)}
 * </ul>
 * These methods are defined by the {@link ch.ethz.inf.vs.californium.coap.RequestHandler} interface and have a default
 * implementation in this class that responds with "4.05 Method Not Allowed."
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class LocalResource extends Resource {

	// Constructors ////////////////////////////////////////////////////////////

	public LocalResource(String resourceIdentifier, boolean hidden) {
		super(resourceIdentifier, hidden);
	}

	public LocalResource(String resourceIdentifier) {
		super(resourceIdentifier, false);
	}

// Observing ///////////////////////////////////////////////////////////////////

	/**
	 * Calling this method will notify all registered observers. Resources that
	 * use this method must also call {@link #isObservable(true)} so that
	 * clients will be registered after a successful GET with Observe option.
	 */
	protected void changed() {
		ObservingManager.getInstance().notifyObservers(this);
	}

// REST Operations /////////////////////////////////////////////////////////////

//	@Override YOANN COMMENT
	public void performGET(GETRequest request) {
		request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
	}

//	@Override YOANN COMMENT
	public void performPUT(PUTRequest request) {
		request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
	}

//	@Override YOANN COMMENT
	public void performPOST(POSTRequest request) {
		request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
	}

//	@Override YOANN COMMENT
	public void performDELETE(DELETERequest request) {
		request.respond(CodeRegistry.RESP_METHOD_NOT_ALLOWED);
	}

	// Sub-resource management /////////////////////////////////////////////////

	/*
	 * Generally forbid the creation of new sub-resources.
	 * Override and define checks to allow creation.
	 */
	@Override
	public void createSubResource(Request request, String newIdentifier) {
		request.respond(CodeRegistry.RESP_FORBIDDEN);
		request.sendResponse();
	}

}
