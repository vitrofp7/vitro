/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis
 * #     Paolo Medagliani
 * #     D. Davide Lamanna
 * #     Panos Trakadas
 * #     Andrea Kropp
 * #     Kiriakos Georgouleas
 * #     Panagiotis Karkazis
 * #     David Ferrer Figueroa
 * #     Francesco Ficarola
 * #     Stefano Puglia
 * #--------------------------------------------------------------------------
 */

package vitro.virtualsensor.communication.sml;

import java.text.SimpleDateFormat;
import java.util.Date;
import vitro.virtualsensor.VirtualSensor;


public class InsertObservationMessage {

	private static SimpleDateFormat dateFormat;

	private static final String insertObservationXML = 
			"<sos:InsertObservation\n" + 
					"\txmlns:sos=\"http://www.opengis.net/sos/1.0\" service=\"SOS\" version=\"1.0.0\"\n" + 
					"\txmlns:gml=\"http://www.opengis.net/gml\"\n" +
					"\txmlns:om=\"http://www.opengis.net/om/1.0\"\n" +
					"\txmlns:paid=\"urn:ogc:def:dictionary:PAID:1.0:paid\"\n" +
					"\txmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\"\n" +
					"\txmlns:swe=\"http://www.opengis.net/swe/1.0.1\"\n" +
					"\txmlns:xlink=\"http://www.w3.org/1999/xlink\"\n" +
					"\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
					"\t<sos:AssignedSensorId>#SENSOR_NAME#</sos:AssignedSensorId>\n" +
					"\t\t<om:Observation> \n" +
					"\t\t<om:samplingTime >\n" +
					"\t\t\t<gml:TimeInstant frame=\"urn:x-ogc:def:trs:IDAS:1.0:ISO8601\">\n" +
					"\t\t\t\t<gml:timePosition frame=\"urn:x-ogc:def:trs:IDAS:1.0:ISO8601\">#TIME#</gml:timePosition>\n" +
					"\t\t\t</gml:TimeInstant>\n" +
					"\t\t</om:samplingTime>\n" +
					"\t\t<om:procedure xlink:href=\"#SENSOR_NAME#\" />\n" +
					"\t\t<om:observedProperty xlink:href=\"#PHENOMENOM#\" />\n" +
					"\t\t<om:featureOfInterest />\n" +
					"\t\t<om:parameter xlink:href=\"urn:x-ogc:def:identifier:IDAS:1.0:UniversalIdentifierOfLogicalHub\" >\n" +
					"\t\t\t<swe:Text >\n" +
					"\t\t\t\t<swe:value>#GW_NAME#</swe:value>\n" +
					"\t\t\t</swe:Text>\n" +
					"\t\t</om:parameter>\n" +
					"\t\t<om:parameter xlink:href=\"urn:x-ogc:def:phenomenon:IDAS:1.0:location\" >\n" +
					"\t\t\t<swe:Position >\n" +
					"\t\t\t\t<swe:location >\n" +
					"\t\t\t\t\t<swe:Vector referenceFrame=\"urn:x-ogc:def:crs:IDAS:1.0:CRS84\">\n" +
					"\t\t\t\t\t\t<swe:coordinate name=\"Latitud\">\n" +
					"\t\t\t\t\t\t\t<swe:Quantity definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:latitude\" >\n" +
					"\t\t\t\t\t\t\t\t<swe:uom code=\"deg\" />\n" +
					"\t\t\t\t\t\t\t\t<swe:value>#LATITUDE#</swe:value>\n" +
					"\t\t\t\t\t\t\t</swe:Quantity>\n" +
					"\t\t\t\t\t\t</swe:coordinate>\n" +
					"\t\t\t\t\t\t<swe:coordinate name=\"Longitud\">\n" +
					"\t\t\t\t\t\t\t<swe:Quantity definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:longitude\" >\n" +
					"\t\t\t\t\t\t\t\t<swe:uom code=\"deg\" />\n" +
					"\t\t\t\t\t\t\t\t<swe:value>#LONGITUDE#</swe:value>\n" +
					"\t\t\t\t\t\t\t</swe:Quantity>\n" +
					"\t\t\t\t\t\t</swe:coordinate>\n" +
					"\t\t\t\t\t</swe:Vector>\n" +
					"\t\t\t\t</swe:location>\n" +
					"\t\t\t</swe:Position>\n" +
					"\t\t</om:parameter>\n\n" +
					"\t\t<om:result xsi:type=\"swe:QuantityPropertyType\">\n" +
					"\t\t\t<swe:Quantity definition=\"#PHENOMENOM#\" >\n" +
					"\t\t\t\t<swe:uom code=\"#UOM#\" />\n" +
					"\t\t\t\t<swe:value>#MEASUREMENT#</swe:value>\n" +
					"\t\t\t</swe:Quantity>\n" +
					"\t\t</om:result>\n" +
					"\t</om:Observation> \n" +
					"</sos:InsertObservation>\n";

	public static String getInsertObservationMessage(VirtualSensor vs){
		String message = "" + insertObservationXML;

		message = message.replaceAll("#GW_NAME#", vs.getSensorInformation().getGateway());
		message = message.replaceAll("#SENSOR_NAME#", vs.getSensorInformation().getId());
		message = message.replace("#LATITUDE#", "" + vs.getSensorInformation().getLatitude());
		message = message.replace("#LONGITUDE#", "" + vs.getSensorInformation().getLongitude());
		message = message.replaceAll("#TIME#",getDate());
		message = message.replaceAll("#PHENOMENOM#", vs.getSensorInformation().getPhenomenom());
		message = message.replaceAll("#UOM#", vs.getSensorInformation().getOutputUOM());
		message = message.replace("#MEASUREMENT#", vs.getSensorInformation().getMeasurements().get(vs.getSensorInformation().getMeasurements().size()-1).toString());
		return message;
	}

	public static String getDate(){
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
		String myString = dateFormat.format(new Date());
		return myString;

		/*
		 * Calendar cal = Calendar.getInstance()
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        String date = sdf.format(cal.getTime())
		 */
	}
}
