package ch.ethz.inf.vs.californium.examples.resources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * Defines a resource that returns the current time on a GET request.
 * It also Supports observing. 
 *  
 * @author Dominique Im Obersteg, Daniel Pauli, and Matthias Kovatsch
 */
public class TimeResource extends LocalResource {

	// The current time represented as string
	private String time;

	/*
	 * Constructor for a new TimeResource
	 */
	public TimeResource() {
		super("timeResource");
		setTitle("GET the current time");
		setResourceType("CurrentTime");
		isObservable(true);

		// Set timer task scheduling
		Timer timer = new Timer();
		timer.schedule(new TimeTask(), 0, 2000);
	}

	/*
	 * Defines a new timer task to return the current time
	 */
	private class TimeTask extends TimerTask {

		@Override
		public void run() {
			time = getTime();

			// Call changed to notify subscribers
			changed();
		}
	}

	/*
	 * Returns the current time
	 * 
	 * @return The current time
	 */
	private String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("EEEEEEEEE, dd.MM.yyyy, HH:mm:ss");
		Date time = new Date();
		return dateFormat.format(time);
	}

	@Override
	public void performGET(GETRequest request) {

		request.respond(CodeRegistry.RESP_CONTENT, time, MediaTypeRegistry.TEXT_PLAIN);
		
	}
}
