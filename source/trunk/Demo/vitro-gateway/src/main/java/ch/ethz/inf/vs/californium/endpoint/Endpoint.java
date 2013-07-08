package ch.ethz.inf.vs.californium.endpoint;

import java.io.IOException;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Communicator;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.MessageHandler;
import ch.ethz.inf.vs.californium.coap.MessageReceiver;
import ch.ethz.inf.vs.californium.coap.Request;

/**
 * The abstract class Endpoint is the basis for the server-sided
 * {@link LocalEndpoint} and the client-sided {@link RemoteEndpoint} skeleton.
 * 
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public abstract class Endpoint implements MessageReceiver, MessageHandler {

// Logging /////////////////////////////////////////////////////////////////////
		
	protected static final Logger LOG = Logger.getLogger(Endpoint.class.getName());

// Members /////////////////////////////////////////////////////////////////////
	
	protected Resource rootResource;

// Methods /////////////////////////////////////////////////////////////////////
	
	public abstract void execute(Request request) throws IOException;

	public int resourceCount() {
		return rootResource != null ? rootResource.subResourceCount() + 1 : 0;
	}

//	@Override YOANN COMMENT
	public void receiveMessage(Message msg) {
		msg.handleBy(this);
	}
	
	public int port() {
		return Communicator.getInstance().port();
	}

}
