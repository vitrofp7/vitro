package ch.ethz.inf.vs.californium.examples.ipso;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This resource implements a part of the IPSO profile.
 * 
 * @author Matthias Kovatsch
 */
public class PowerInstantaneous extends LocalResource {
	
	private double power = 0d;

	public PowerInstantaneous() {
		super("pwr/w");
		setTitle("Instantaneous Power");
		setResourceType("ipso:pwr-w");
		// second rt not supported by current SensiNode RD demo
		//setResourceType("ucum:W");
		setInterfaceDescription("core#s");
		
		isObservable(true);

		// Set timer task scheduling
		Timer timer = new Timer();
		timer.schedule(new TimeTask(), 0, 1000);
	}

	private class TimeTask extends TimerTask {

		@Override
		public void run() {
			if (PowerRelay.getRelay()) {
				power = Math.round(1500d*Math.random()*(PowerDimmer.getDimmer()/100d));
			} else {
				// skip changed() update if nothing changed
				if (power==0d) {
					return;
				}
				
				power = 0d;
			}

			// Call changed to notify subscribers
			changed();
		}
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, Double.toString(power), MediaTypeRegistry.TEXT_PLAIN);
	}
}
