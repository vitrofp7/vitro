package ch.ethz.inf.vs.californium.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.EndpointAddress;
import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.ObservingManager;
import ch.ethz.inf.vs.californium.coap.TokenManager;
import ch.ethz.inf.vs.californium.endpoint.Endpoint;
import ch.ethz.inf.vs.californium.endpoint.Resource;
import ch.ethz.inf.vs.californium.layers.Layer;

/**
 * This class centralizes the configuration of the logging facilities.
 * Californium uses {@link java.util.logging}.
 * 
 * @author Matthias Kovatsch
 */
public class Log {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Level logLevel = Level.ALL;
	
	private static final Formatter printFormatter = new Formatter() {
		@Override
		public String format(LogRecord record) {
			return String.format("%s [%s] %s - %s\r\n",
								 dateFormat.format(new Date(record.getMillis())),
								 record.getSourceClassName().replace("ch.ethz.inf.vs.californium.", ""),
								 record.getLevel(), record.getMessage()
								);
		}
	};
	
	public static void setLevel(Level l) {
		logLevel = l;
	}

	public static void init() {
		
		Logger globalLogger = Logger.getLogger("");
		
		// Remove the default handler
		for (Handler handler : globalLogger.getHandlers()) {
		    globalLogger.removeHandler(handler);
		}
		
		// create custom console handler
		ConsoleHandler cHandler = new ConsoleHandler();
		cHandler.setFormatter(printFormatter);
		// set logging level
		cHandler.setLevel(Level.ALL);
		// add
		globalLogger.addHandler(cHandler);
		
		// create custom file handler
		FileHandler fHandler;
		try {
			fHandler = new FileHandler("Californium-log.%g.txt", true);
			fHandler.setFormatter(printFormatter);
			globalLogger.addHandler(fHandler);
		} catch (Exception e) {
			globalLogger.severe("Cannot add file logger: " + e.getMessage());
		}
		
		// customize levels
		Logger.getLogger(Endpoint.class.getName()).setLevel(logLevel);
		Logger.getLogger(EndpointAddress.class.getName()).setLevel(logLevel);
		Logger.getLogger(Resource.class.getName()).setLevel(logLevel);
		Logger.getLogger(LinkFormat.class.getName()).setLevel(logLevel);
		Logger.getLogger(Message.class.getName()).setLevel(logLevel);
		Logger.getLogger(TokenManager.class.getName()).setLevel(logLevel);
		Logger.getLogger(ObservingManager.class.getName()).setLevel(logLevel);
		Logger.getLogger(Layer.class.getName()).setLevel(logLevel);
		Logger.getLogger(Properties.class.getName()).setLevel(logLevel);
		
		// indicate new start-up
		Logger.getLogger(Log.class.getName()).info("==[ START-UP ]========================================================");
	}
}

