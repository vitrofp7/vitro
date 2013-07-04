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

import vitro.virtualsensor.VirtualSensor;

public class RegisterSensorMessage {

	public static final String input = 
			"\t\t\t\t\t<sml:input name=\"#PHENOMENOM_NAME#\"> \n" +
					"\t\t\t\t\t\t<swe:ObservableProperty definition=\"#PHENOMENOM#\"/> \n" +
					"\t\t\t\t\t</sml:input> \n";
	public static final String output = 
			"\t\t\t\t\t<sml:output name=\"#PHENOMENOM_NAME#\"> \n" +
					"\t\t\t\t\t\t<swe:Quantity definition=\"#PHENOMENOM#\"> \n" +
					"\t\t\t\t\t\t\t<swe:uom code=\"#UOM#\"/> \n" +
					"\t\t\t\t\t\t</swe:Quantity> \n" +
					"\t\t\t\t\t</sml:output> \n";

	public static final String registerSensor = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?> \n" +
                                    "<sos:RegisterSensor service=\"SOS\" version=\"1.0.0\" \n" +
                                    "\txsi:schemaLocation=\"http://www.opengis.net/sos/1.0 sosRegisterSensor.xsd\" \n" +
                                    "\txmlns:swe=\"http://www.opengis.net/swe/1.0.1\" \n" +
                                    "\txmlns:gml=\"http://www.opengis.net/gml\" \n" +
                                    "\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                                    "\txmlns:xlink=\"http://www.w3.org/1999/xlink\" \n" +
                                    "\txmlns:om=\"http://www.opengis.net/om/1.0\" \n" +
                                    "\txmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" \n" +
                                    "\txmlns:sos=\"http://www.opengis.net/sos/1.0\"> \n" +
                                    "\t<sos:SensorDescription> \n" +
                                    "\t\t<sml:System> \n" +
                                    "\t\t\t<sml:identification> \n" +
                                    "\t\t\t\t<sml:IdentifierList> \n" +
                                    "\t\t\t\t\t<sml:identifier> \n" +
                                    "\t\t\t\t\t\t<sml:Term definition=\"urn:x-ogc:def:identifier:IDAS:1.0:localIdentifier\"> \n" +
                                    "\t\t\t\t\t\t\t<sml:value>#SENSOR_NAME#</sml:value> \n" +
                                    "\t\t\t\t\t\t</sml:Term> \n" +
                                    "\t\t\t\t\t</sml:identifier> \n" +
                                    "\t\t\t\t\t<sml:identifier> \n" +
                                    "\t\t\t\t\t\t<sml:Term definition=\"urn:x-ogc:def:identifier:IDAS:1.0:UniversalIdentifierOfLogicalHub\"> \n" +
                                    "\t\t\t\t\t\t\t<sml:value>#GW_NAME#</sml:value> \n" +
                                    "\t\t\t\t\t\t</sml:Term> \n" +
                                    "\t\t\t\t\t</sml:identifier> \n" +
                                    "\t\t\t\t</sml:IdentifierList> \n" +
                                    "\t\t\t</sml:identification> \n" +
                                    "\t\t\t<sml:inputs> \n" +
                                    "\t\t\t\t<sml:InputList> \n" +
                                    "#INPUTS#" +
                                    "\t\t\t\t</sml:InputList> \n" +
                                    "\t\t\t</sml:inputs> \n" +
                                    "\t\t\t<sml:outputs> \n" +
                                    "\t\t\t\t<sml:OutputList> \n" +		                            
                                    "#OUTPUTS#" +		                    
                                    "\t\t\t\t</sml:OutputList> \n" +
                                    "\t\t\t</sml:outputs> \n" +
                                    "\t\t\t\t\t<om:parameter xlink:href=\"urn:x-ogc:def:phenomenon:IDAS:1.0:location\" > \n" +
                                    "\t\t\t\t\t\t<swe:Position> \n" +
                                    "\t\t\t\t\t\t\t<swe:location> \n" +
                                    "\t\t\t\t\t\t\t\t<swe:Vector referenceFrame=\"urn:x-ogc:def:crs:IDAS:1.0:CRS84\"> \n" +
                                    "\t\t\t\t\t\t\t\t\t<swe:coordinate name=\"Latitud\"> \n" +
                                    "\t\t\t\t\t\t\t\t\t\t<swe:Quantity definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:latitude\" > \n" +
                                    "\t\t\t\t\t\t\t\t\t\t<swe:uom code=\"deg\" /> \n" +
                                    "\t\t\t\t\t\t\t\t\t\t<swe:value>#LATITUDE#</swe:value> \n" +
                                    "\t\t\t\t\t\t\t</swe:Quantity> \n" +
                                    "\t\t\t\t\t\t\t\t\t</swe:coordinate> \n" +
                                    "\t\t\t\t\t\t\t\t\t<swe:coordinate name=\"Longitud\"> \n" +
                                    "\t\t\t\t\t\t\t\t\t\t<swe:Quantity definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:longitude\" > \n" +
                                    "\t\t\t\t\t\t\t\t\t\t<swe:uom code=\"deg\" /> \n" +
                                    "\t\t\t\t\t\t\t\t\t\t<swe:value>#LONGITUDE#</swe:value> \n" +
                                    "\t\t\t\t\t\t\t\t</swe:Quantity> \n" +
                                    "\t\t\t\t\t\t\t\t\t</swe:coordinate> \n" +
                                    "\t\t\t\t\t\t\t\t</swe:Vector> \n" +
                                    "\t\t\t\t\t\t\t</swe:location> \n" +
                                    "\t\t\t\t\t\t</swe:Position> \n" +
                                    "\t\t\t\t\t</om:parameter> \n" +
                                    "\t\t</sml:System> \n" +
                                    "\t</sos:SensorDescription> \n" +
                                    "\t<sos:ObservationTemplate> \n" +
                                    "\t\t<om:Observation> \n" +
                                    "\t\t<om:samplingTime /> \n" +
                                    "\t\t<om:procedure /> \n" +
                                    "\t\t<om:observedProperty /> \n" +
                                    "\t\t<om:featureOfInterest /> \n" +
                                    "\t\t<om:result/> \n" +
                                    "\t\t</om:Observation> \n" +
                                    "\t</sos:ObservationTemplate> \n" +
                                    "</sos:RegisterSensor> \n";

	public static String getRegisterSensorMessage(VirtualSensor vs){
            String message = "" + registerSensor;
            String inputList = "";
            String outputList = "";

            for(int i = 0; i < vs.getInputSensors().size(); i++) {
                String phenomName = vs.getInputPhenomena().get(i).replaceFirst("urn:x-ogc:def:phenomenon:IDAS:1.0:", "");
                inputList = inputList.concat(input);
                inputList = inputList.replaceAll("#PHENOMENOM_NAME#", phenomName);
                inputList = inputList.replaceAll("#PHENOMENOM#", vs.getInputPhenomena().get(i));
            }

            outputList = outputList.concat(output);
            String phenomName = vs.getOutputPhenomenom().replaceFirst("urn:x-ogc:def:phenomenon:IDAS:1.0:", "");
            outputList = outputList.replaceAll("#PHENOMENOM_NAME#", phenomName);
            outputList = outputList.replaceAll("#PHENOMENOM#", vs.getOutputPhenomenom());
            outputList = outputList.replace("#UOM#", vs.getSensorInformation().getOutputUOM());

            message = message.replace("#GW_NAME#", vs.getGw());
            message = message.replace("#SENSOR_NAME#", vs.getSensorInformation().getId());
            message = message.replace("#LATITUDE#", "" + vs.getSensorInformation().getLatitude());
            message = message.replace("#LONGITUDE#", "" + vs.getSensorInformation().getLongitude());
            message = message.replace("#INPUTS#", inputList);
            message = message.replace("#OUTPUTS#", outputList);
            return message;
	}
}
