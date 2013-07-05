/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package vitro.vgw.wsiadapter.coap.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.model.Resource;
import vitro.vgw.wsiadapter.coap.util.Functions;


public class MoteResource {
	
	private static Logger logger = LoggerFactory.getLogger(MoteResource.class);
    //new: added to support querying the trust_routing resource (coap) when supported.
    public static String TRUST_ROUTING = "tr";
	public static String TEMPERATURE_TCS = "temperature";
    public static String TEMPERATURE_ALT = "st";
    // TODO: [CONSIDER] could we add another key for the same resource ? (to resolve the issue of different names for same resources)
    // eg for temperature we could keep the TEMPERATURE = "temperature" key and
    //                              add a TEMPERATURE_ALT = "st" key.
    // and both would map to the Temperature resource  eg.:
    // moteResourceMap.put(MoteResource.TEMPERATURE, Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
    // moteResourceMap.put(MoteResource.TEMPERATURE_ALT, Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
	public static String HUMIDITY = "sh";
	public static String LIGHT = "sl";
	public static String WIND_SPEED = "sw";
	public static String CO = "co";
	public static String CO2 = "co2";
	public static String PRESSURE = "sp";
	public static String BAROMETRIC_PRESSURE = "sbp";
	public static String RESOURCE_DISCOVERY = ".well-known/core";
	
	private static Map<String, Resource> moteResourceMap = new HashMap<String, Resource>();
	static {
		try {
			moteResourceMap.put(MoteResource.TEMPERATURE_TCS, Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
            moteResourceMap.put(MoteResource.TEMPERATURE_ALT, Resource.getResource(Resource.PHENOMENOM_TEMPERATURE));
			moteResourceMap.put(MoteResource.HUMIDITY, Resource.getResource(Resource.PHENOMENOM_HUMIDITY));
			moteResourceMap.put(MoteResource.LIGHT, Resource.getResource(Resource.PHENOMENOM_LIGHT));
			moteResourceMap.put(MoteResource.WIND_SPEED, Resource.getResource(Resource.PHENOMENOM_WIND_SPEED));
			moteResourceMap.put(MoteResource.CO, Resource.getResource(Resource.PHENOMENOM_CO));
			moteResourceMap.put(MoteResource.CO2, Resource.getResource(Resource.PHENOMENOM_CO2));
			moteResourceMap.put(MoteResource.PRESSURE, Resource.getResource(Resource.PHENOMENOM_PRESSURE));
			moteResourceMap.put(MoteResource.BAROMETRIC_PRESSURE, Resource.getResource(Resource.PHENOMENOM_BAROMETRIC_PRESSURE));
            moteResourceMap.put(MoteResource.TRUST_ROUTING, Resource.getResource(Resource.PHENOMENOM_TRUST_ROUTING));
		} catch (VitroGatewayException e) {
			logger.error("", e);
		}
	}
	
	public static Resource getResource(String moteResourceName) throws VitroGatewayException {
		Resource result = moteResourceMap.get(moteResourceName);
		if(result == null){
			throw new VitroGatewayException("No mote resource mapping for " + moteResourceName);
		}
		return result;
	}
	
	public static String getMoteUriResource(Resource resource) throws VitroGatewayException {
		String result = Functions.getKeyByValue(moteResourceMap, resource);
		if(result == null) {
			throw new VitroGatewayException("No mote resource mapping for " + resource.getName());
		}
		return result;
	}
	
	public static boolean containsKey(String resourceName) {
		boolean isPresent = false;
		if(moteResourceMap.containsKey(resourceName)) {
			isPresent = true;
		}
		return isPresent;
	}
	
	public static boolean containsValue(Resource resource) {
		boolean isPresent = false;
		if(moteResourceMap.containsValue(resource)) {
			isPresent = true;
		}
		return isPresent;
	}
}
