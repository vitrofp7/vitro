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

package vitro.virtualsensor.utils;

public class UNICAData {
	private static final String [] phenomena = {"event", "temperature", "mass", "relativeHumidity", "direction", "windDirection", 
			"velocity", "windSpeed", "pressure", "atmosphericPressure", "rainfall", "concentration", "time", 
			"NOConcentration", "O3Concentration", "CO2Concentration", "COConcentration", "UVRadiation", 
			"solarRadiation", "acceleration", "Xacceleration", "Yacceleration", "Zacceleration", "sound", 
			"electricPotential", "electricCurrent", "length", "location", "longitude", "latitude", "altitude", 
			"connectivity", "turbidity", "volume", "power", "averagePower", "minimumPower", "maximumPower", 
			"energy", "currency", "cost", "energyCost", "energyCO2", "frequency", "pulseOximetry", 
			"GlucoseConcentration", "BloodPressure", "HeartRate", "presence", "batteryCharge", "gasConcentration", 
			"luminousFlux", "unknown"};
	public static String [] getPhenomena(){
		return phenomena;
	}

}
