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
package vitro.vgw.model;

import java.util.HashMap;
import java.util.Map;

import vitro.vgw.exception.VitroGatewayException;


/*
 * Available physical parameters on sensor nodes, e.g temperature, humidity, pressure, light,.... 
 * */
public class Resource {

	public static String PHENOMENOM_TEMPERATURE = "temperature";
	public static String PHENOMENOM_HUMIDITY = "humidity";
	public static String PHENOMENOM_LIGHT = "light";
	public static String PHENOMENOM_WIND_SPEED = "windspeed";
	public static String PHENOMENOM_CO = "co";
	public static String PHENOMENOM_CO2 = "co2";
	public static String PHENOMENOM_PRESSURE = "pressure";
	public static String PHENOMENOM_BAROMETRIC_PRESSURE = "barometricpressure";
    public static String PHENOMENOM_TRUST_ROUTING = "trustrouting";

    // TODO: note, I am adding these phenomena in order to support some extra actuation capabilities for the demos. We can remove it later (thanasis)
    public static String PHENOMENOM_SWITCH_LIGHT1 = "switchlight1";
    public static String PHENOMENOM_SWITCH_LIGHT2 = "switchlight2";
    public static String PHENOMENOM_SWITCH_LIGHT3 = "switchlight3";
    public static String PHENOMENOM_SWITCH_LIGHT4 = "switchlight4";
	
    public static Resource RES_TEMPERATURE = new Resource(PHENOMENOM_TEMPERATURE);
    public static Resource RES_HUMIDITY = new Resource(PHENOMENOM_HUMIDITY);
    public static Resource RES_LIGHT = new Resource(PHENOMENOM_LIGHT);
    public static Resource RES_WIND_SPEED = new Resource(PHENOMENOM_WIND_SPEED);
    public static Resource RES_CO = new Resource(PHENOMENOM_CO);
    public static Resource RES_CO2 = new Resource(PHENOMENOM_CO2);
    public static Resource RES_PRESSURE = new Resource(PHENOMENOM_PRESSURE);
    public static Resource RES_BAROMETRIC_PRESSURE = new Resource(PHENOMENOM_BAROMETRIC_PRESSURE);
    public static Resource RES_TRUST_ROUTING = new Resource(PHENOMENOM_TRUST_ROUTING );
    // TODO: note, I am adding these phenomena in order to support some extra actuation capabilities for the demos. We can remove it later (thanasis)
    public static Resource RES_SWITCH_LIGHT1 = new Resource(PHENOMENOM_SWITCH_LIGHT1);
    public static Resource RES_SWITCH_LIGHT2 = new Resource(PHENOMENOM_SWITCH_LIGHT2);
    public static Resource RES_SWITCH_LIGHT3 = new Resource(PHENOMENOM_SWITCH_LIGHT3);
    public static Resource RES_SWITCH_LIGHT4 = new Resource(PHENOMENOM_SWITCH_LIGHT4);

    public static String UOM_CELSIUS = "celsius";
	public static String UOM_KELVIN = "kelvin";
    public static String UOM_CANDLE = "candela";
	public static String UOM_PERCENT = "percent";
	public static String UOM_KMH = "kmH";
	public static String UOM_PARTS_PER_MILLION = "ppm";
	public static String UOM_PARTS_PER_BILLION = "ppb";
	public static String UOM_PASCAL = "pascal";
	public static String UOM_HECTO_PASCAL = "hectoPascal";
	public static String UOM_DIMENSIONLESS = "dimensionless";


	private static Map<String, String> defaultUomMap = new HashMap<String, String>();
	static{
		defaultUomMap.put(Resource.PHENOMENOM_TEMPERATURE, UOM_KELVIN);
        defaultUomMap.put(Resource.PHENOMENOM_LIGHT, UOM_CANDLE);
        defaultUomMap.put(Resource.PHENOMENOM_HUMIDITY, UOM_PERCENT);
		defaultUomMap.put(Resource.PHENOMENOM_WIND_SPEED, UOM_KMH);
		defaultUomMap.put(Resource.PHENOMENOM_CO, UOM_PARTS_PER_MILLION);
		defaultUomMap.put(Resource.PHENOMENOM_CO2, UOM_PARTS_PER_MILLION);
		defaultUomMap.put(Resource.PHENOMENOM_PRESSURE, UOM_PASCAL);
		defaultUomMap.put(Resource.PHENOMENOM_BAROMETRIC_PRESSURE, UOM_HECTO_PASCAL);
        defaultUomMap.put(Resource.PHENOMENOM_TRUST_ROUTING, UOM_DIMENSIONLESS);
        // TODO: note, I am adding these phenomena in order to support some extra actuation capabilities for the demos. We can remove it later (thanasis)
        defaultUomMap.put(Resource.PHENOMENOM_SWITCH_LIGHT1, UOM_DIMENSIONLESS);
        defaultUomMap.put(Resource.PHENOMENOM_SWITCH_LIGHT2, UOM_DIMENSIONLESS);
        defaultUomMap.put(Resource.PHENOMENOM_SWITCH_LIGHT3, UOM_DIMENSIONLESS);
        defaultUomMap.put(Resource.PHENOMENOM_SWITCH_LIGHT4, UOM_DIMENSIONLESS);
	}

    private static Map<String, Resource> resourceMap = new HashMap<String, Resource>();
	static{
		resourceMap.put(Resource.PHENOMENOM_TEMPERATURE, RES_TEMPERATURE);
		resourceMap.put(Resource.PHENOMENOM_HUMIDITY, RES_HUMIDITY);
        resourceMap.put(Resource.PHENOMENOM_LIGHT, RES_LIGHT);  // TODO: uncomment to support light
        resourceMap.put(Resource.PHENOMENOM_WIND_SPEED, RES_WIND_SPEED);
		resourceMap.put(Resource.PHENOMENOM_CO, RES_CO);
		resourceMap.put(Resource.PHENOMENOM_CO2, RES_CO2);
		resourceMap.put(Resource.PHENOMENOM_PRESSURE, RES_PRESSURE);
		resourceMap.put(Resource.PHENOMENOM_BAROMETRIC_PRESSURE, RES_BAROMETRIC_PRESSURE);
        resourceMap.put(Resource.PHENOMENOM_TRUST_ROUTING, RES_TRUST_ROUTING);
        // TODO: note, I am adding these phenomena in order to support some extra actuation capabilities for the demos. We can remove it later (thanasis)
        resourceMap.put(Resource.PHENOMENOM_SWITCH_LIGHT1, RES_SWITCH_LIGHT1);
        resourceMap.put(Resource.PHENOMENOM_SWITCH_LIGHT2, RES_SWITCH_LIGHT2);
        resourceMap.put(Resource.PHENOMENOM_SWITCH_LIGHT3, RES_SWITCH_LIGHT3);
        resourceMap.put(Resource.PHENOMENOM_SWITCH_LIGHT4, RES_SWITCH_LIGHT4);
	}
	
	String name;

	public static Resource getResource(String resourceName) throws VitroGatewayException{
		Resource result = resourceMap.get(resourceName);
		if(result == null){
			throw new VitroGatewayException("No resource mapping for " + resourceName);
		}
		return result;
	}
	
	
	private Resource(String name) {
		super();
		this.name = name;
	}
	
	public String getUnityOfMeasure(){
		return defaultUomMap.get(name);
	}
	
	public String getName(){
		return name;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Resource other = (Resource) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
	
	
}
