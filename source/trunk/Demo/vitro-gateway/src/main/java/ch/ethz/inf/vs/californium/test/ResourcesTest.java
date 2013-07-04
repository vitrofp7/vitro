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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.endpoint.RemoteResource;
import ch.ethz.inf.vs.californium.endpoint.Resource;


//import ch.ethz.inf.vs.californium.coap.Resources;

public class ResourcesTest {
	@Test
	public void TwoResourceTest() {

		// note: order of attributes may change

		String resourceInput1 = "</myUri>,</myUri/something>;ct=42;if=\"/someRef/path\";obs;rt=\"MyName\";sz=10";
		String resourceInput2 = "</sensors>,</sensors/temp>;ct=41;obs;rt=\"TemperatureC\"";

		// Build link format string
		String resourceInput = resourceInput1 + "," + resourceInput2;

		// Construct two resources from link format substrings
		// Resource res1 = Resource.fromLinkFormat(resourceInput1);
		// Resource res2 = Resource.fromLinkFormat(resourceInput2);

		// Build resources from assembled link format string
		Resource resource = RemoteResource.newRoot(resourceInput);

		// Check if resources are in hash map
		// assertTrue(resources.hasResource(res1.getResourceName()));
		// assertTrue(resources.hasResource(res2.getResourceName()));

		// Check if link format string equals input
		// String expectedLinkFormat = res1.toLinkFormat() + "," +
		// res2.toLinkFormat();
		// assertEquals(expectedLinkFormat, resources.toLinkFormat());
		assertEquals(resourceInput, LinkFormat.serialize(resource, null, true));
	}

}
