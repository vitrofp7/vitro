package ch.ethz.inf.vs.californium.coap;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.util.Properties;

/**
 * The class EndpointAddress stores IP address and port. It is mainly used to handle {@link Message}s.
 * 
 * @author Matthias Kovatsch
 */
public class EndpointAddress {

// Logging /////////////////////////////////////////////////////////////////////
	
	protected static final Logger LOG = Logger.getLogger(EndpointAddress.class.getName());

// Members /////////////////////////////////////////////////////////////////////
	
	/** The address. */
	private InetAddress address = null;
	
	/** The port. */
	private int port = Properties.std.getInt("DEFAULT_PORT");

// Constructors ////////////////////////////////////////////////////////////////
	
	/**
	 * Instantiates a new endpoint address using the default port.
	 *
	 * @param address the IP address
	 */
	public EndpointAddress(InetAddress address) {
		this.address = address;
	}
	
	/**
	 * Instantiates a new endpoint address, setting both, IP and port.
	 *
	 * @param address the IP address
	 * @param port the custom port
	 */
	public EndpointAddress(InetAddress address, int port) {
		this.address = address;
		this.port = port;
	}
	
	/**
	 * A convenience constructor that takes the address information from a URI object.
	 *
	 * @param uri the URI
	 */
	public EndpointAddress(URI uri) {
		// Allow for correction later, as host might be unknown at initialization time.
		try {
			this.address = InetAddress.getByName(uri.getHost());
		} catch (UnknownHostException e) {
			LOG.warning(String.format("Cannot fully initialize: %s", e.getMessage()));
		}
		if (uri.getPort()!=-1) this.port = uri.getPort();
	}

// Methods /////////////////////////////////////////////////////////////////////
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.address instanceof Inet6Address) {
			return String.format("[%s]:%d", this.address.getHostAddress(), this.port);
		} else {
			return String.format("%s:%d", this.address.getHostAddress(), this.port);
		}
	}
	
	/**
	 * Returns the IP address.
	 *
	 * @return the address
	 */
	public InetAddress getAddress() {
		return this.address;
	}
	
	/**
	 * Returns the port number.
	 *
	 * @return the port
	 */
	public int getPort() {
		return this.port;
	}
}
