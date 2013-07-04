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
package ch.ethz.inf.vs.californium.test;

import static org.junit.Assert.*;
import org.junit.Test;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.Message;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.Message.messageType;

public class MessageTest {

	@Test
	public void testMessage() {

		Message msg = new Message();

		msg.setCode(CodeRegistry.METHOD_GET);
		msg.setType(messageType.CON);
		msg.setMID(12345);
		msg.setPayload("some payload".getBytes());

		System.out.println(msg.toString());

		byte[] data = msg.toByteArray();
		Message convMsg = Message.fromByteArray(data);

		assertEquals(msg.getCode(), convMsg.getCode());
		assertEquals(msg.getType(), convMsg.getType());
		assertEquals(msg.getMID(), convMsg.getMID());
		assertEquals(msg.getOptionCount(), convMsg.getOptionCount());
		assertArrayEquals(msg.getPayload(), convMsg.getPayload());
	}

	@Test
	public void testOptionMessage() {
		Message msg = new Message();

		msg.setCode(CodeRegistry.METHOD_GET);
		msg.setType(messageType.CON);
		msg.setMID(12345);
		msg.setPayload("hallo".getBytes());
		msg.addOption(new Option("a".getBytes(), 1));
		msg.addOption(new Option("b".getBytes(), 2));

		byte[] data = msg.toByteArray();
		Message convMsg = Message.fromByteArray(data);

		assertEquals(msg.getCode(), convMsg.getCode());
		assertEquals(msg.getType(), convMsg.getType());
		assertEquals(msg.getMID(), convMsg.getMID());
		assertEquals(msg.getOptionCount(), convMsg.getOptionCount());
		assertArrayEquals(msg.getPayload(), convMsg.getPayload());
	}

	@Test
	public void testExtendedOptionMessage() {
		Message msg = new Message();

		msg.setCode(CodeRegistry.METHOD_GET);
		msg.setType(messageType.CON);
		msg.setMID(12345);

		// msg.addOption(new Option ("a".getBytes(), 1));
		// msg.addOption(new Option ("c".getBytes(), 198));
		msg.addOption(new Option("c".getBytes(), 211));

		// will fail as limit of max 15 options would be exceeded
		// msg.addOption(new Option ("c".getBytes(), 212));

		byte[] data = msg.toByteArray();
		try {
			System.out.printf("DEBUG: %s (%d)\n", getHexString(data),
					data.length);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Message convMsg = Message.fromByteArray(data);

		assertEquals(msg.getCode(), convMsg.getCode());
		assertEquals(msg.getType(), convMsg.getType());
		assertEquals(msg.getMID(), convMsg.getMID());

		assertEquals(msg.getOptionCount(), convMsg.getOptionCount());
		// assertArrayEquals(msg.getPayload(), convMsg.getPayload());
	}

	public static String getHexString(byte[] b) throws Exception {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
