/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
package org.ws4d.coap.connection;

import java.net.InetAddress;

public class ChannelKey {
	public InetAddress inetAddr;
	public int port;

	public ChannelKey(InetAddress inetAddr, int port) {
		this.inetAddr = inetAddr;
		this.port = port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((inetAddr == null) ? 0 : inetAddr.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChannelKey other = (ChannelKey) obj;
		if (inetAddr == null) {
			if (other.inetAddr != null)
				return false;
		} else if (!inetAddr.equals(other.inetAddr))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
}