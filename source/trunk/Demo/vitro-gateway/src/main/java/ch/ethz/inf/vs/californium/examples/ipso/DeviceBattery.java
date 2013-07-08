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
public class DeviceBattery extends LocalResource {
	
	private double power = 3.6d;

	public DeviceBattery() {
		super("dev/bat");
		setTitle("Battery");
		setResourceType("ipso:dev-bat");
		// second rt not supported by current SensiNode RD demo
		//setResourceType("ucum:V");
		setInterfaceDescription("core#s");
		
		isObservable(true);

		// Set timer task scheduling
		Timer timer = new Timer();
		timer.schedule(new TimeTask(), 0, 1000);
	}

	private class TimeTask extends TimerTask {

		@Override
		public void run() {
			power -= 0.001d*Math.random();

			// Call changed to notify subscribers
			changed();
		}
	}

	@Override
	public void performGET(GETRequest request) {
		
		// complete the request
		request.respond(CodeRegistry.RESP_CONTENT, Double.toString(Math.round(power*1000d)/1000d), MediaTypeRegistry.TEXT_PLAIN);
	}
}
