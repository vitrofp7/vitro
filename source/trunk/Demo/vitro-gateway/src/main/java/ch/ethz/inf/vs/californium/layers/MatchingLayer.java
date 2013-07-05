package ch.ethz.inf.vs.californium.layers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
/**
 * This class matches the request/response pairs using the token option. It must
 * be below the {@link TransferLayer}, which requires set buddies for each
 * message ({@link Response#getRequest()} and {@link Request#getResponse()}).
 * 
 * @author Matthias Kovatsch
 */
public class MatchingLayer extends UpperLayer {

// Members /////////////////////////////////////////////////////////////////////
	
	private Map<String, RequestResponsePair> pairs = new HashMap<String, RequestResponsePair>();
	
// Nested Classes //////////////////////////////////////////////////////////////
	
	/*
	 * Entity class to keep state of transfers
	 */
	private static class RequestResponsePair {
		public String key;
		public Request request;
	}
	
	// Constructors ////////////////////////////////////////////////////////////
	
	public MatchingLayer() {

	}

	// I/O implementation //////////////////////////////////////////////////////
	
	@Override
	protected void doSendMessage(Message msg) throws IOException { 
		
		if (msg instanceof Request) {
			
			addOpenRequest((Request) msg);
		}
		
		sendMessageOverLowerLayer(msg);
	}	
	
	@Override
	protected void doReceiveMessage(Message msg) {

		if (msg instanceof Response) {

			Response response = (Response) msg;
			
			RequestResponsePair pair = getOpenRequest(msg.sequenceKey());

			// check for missing token
			if (pair == null && response.getToken().length==0) {
				
				LOG.info(String.format("Remote endpoint failed to echo token: %s", msg.key()));
				
				// TODO try to recover from peerAddress
				
				// let timeout handle the problem
				return;
			}
			
			if (pair != null) {
				
				// attach request and response to each other
				response.setRequest(pair.request);
				pair.request.setResponse(response);

				LOG.finer(String.format("Matched open request: %s", response.sequenceKey()));
				
				// TODO: ObservingManager.getInstance().isObserving(msg.exchangeKey());
				if (msg.getFirstOption(OptionNumberRegistry.OBSERVE)==null) {
					removeOpenRequest(response.sequenceKey());
				}
				
			} else {
			
				LOG.info(String.format("Dropping unexpected response: %s", response.sequenceKey()));
				return;
			}
			
		}
		
		deliverMessage(msg);
	}
	
	private RequestResponsePair addOpenRequest(Request request) {
		
		// create new Transaction
		RequestResponsePair exchange = new RequestResponsePair();
		exchange.key = request.sequenceKey();
		exchange.request = request;
		
		LOG.finer(String.format("Storing open request: %s", exchange.key));
		
		// associate token with Transaction
		pairs.put(exchange.key, exchange);
		
		return exchange;
	}
	
	private RequestResponsePair getOpenRequest(String key) {
		return pairs.get(key);
	}
	
	private void removeOpenRequest(String key) {
		
		RequestResponsePair exchange = pairs.remove(key);

		LOG.finer(String.format("Cleared open request: %s", exchange.key));
	}
	
	public String getStats() {
		StringBuilder stats = new StringBuilder();
		
		stats.append("Open requests: ");
		stats.append(pairs.size());
		stats.append('\n');
		stats.append("Messages sent:     ");
		stats.append(numMessagesSent);
		stats.append('\n');
		stats.append("Messages received: ");
		stats.append(numMessagesReceived);
		
		return stats.toString();
	}
}
