package ch.ethz.inf.vs.californium.examples.plugtest;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a test of specification for the
 * ETSI IoT CoAP Plugtests, Paris, France, 24 - 25 March 2012.
 * 
 * @author Matthias Kovatsch
 */
public class Large extends LocalResource {

	public Large() {
		super("large");
		setTitle("Large resource");
		setResourceType("block");
	}

	@Override
	public void performGET(GETRequest request) {
		
		StringBuilder builder = new StringBuilder();
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 1 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 2 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 3 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 4 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 5 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		
		request.respond(CodeRegistry.RESP_CONTENT, builder.toString(), MediaTypeRegistry.TEXT_PLAIN);
	}

}
