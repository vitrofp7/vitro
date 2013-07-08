package ch.ethz.inf.vs.californium.endpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import ch.ethz.inf.vs.californium.coap.Communicator;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

/**
 * The class RemoteEndpoint is currently an unimplemented skeleton for a
 * client stub to access a {@link LocalEndpoint} at the server.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class RemoteEndpoint extends Endpoint {

	public static Endpoint fromURI(String uri) {
		try {
			return new RemoteEndpoint(new URI(uri));
		} catch (URISyntaxException e) {
			System.out.printf(
					"[%s] Failed to create RemoteEndpoint from URI: %s\n",
					"JCoAP", e.getMessage());
			return null;
		}
	}

	public RemoteEndpoint(URI uri) {
		
		// initialize communicator
		Communicator.setupDeamon(true);
		Communicator.getInstance().registerReceiver(this);

		this.uri = uri;
	}

	@Override
	public void execute(Request request) throws IOException {

		if (request != null) {

			request.setURI(this.uri);

			// execute the request
			request.execute();
		}

	}

	protected URI uri;

//	@Override  YOANN COMMENT
	public void handleRequest(Request request) {
		// TODO Auto-generated method stub

	}

//	@Override YOANN COMMENT
	public void handleResponse(Response response) {
		// response.handle();
	}
}
