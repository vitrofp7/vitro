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
package vitro.vgw.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import net.opengis.gml.v_3_1_1.TimeInstantType;
import net.opengis.om.v_1_0_0.ObservationType;
import net.opengis.sensorml.v_1_0_1.Identification;
import net.opengis.sensorml.v_1_0_1.Identification.IdentifierList.Identifier;
import net.opengis.sensorml.v_1_0_1.IoComponentPropertyType;
import net.opengis.sensorml.v_1_0_1.SystemType;
import net.opengis.sensorml.v_1_0_1.Term;
import net.opengis.sos.v_1_0_0.InsertObservation;
import net.opengis.sos.v_1_0_0.RegisterSensor;
import net.opengis.sos.v_1_0_0.RequestBaseType;
import net.opengis.swe.v_1_0_1.AnyDataPropertyType;
import net.opengis.swe.v_1_0_1.ObservableProperty;
import net.opengis.swe.v_1_0_1.Quantity;
import net.opengis.swe.v_1_0_1.QuantityPropertyType;
import net.opengis.swe.v_1_0_1.UomPropertyType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.model.Node;
import vitro.vgw.model.NodeDescriptor;
import vitro.vgw.model.Observation;
import vitro.vgw.model.Resource;
import vitro.vgw.utils.Utils;

public class SensorMLMessageAdapter {

	private static Logger logger = LoggerFactory.getLogger(SensorMLMessageAdapter.class); 
	
	private static String IDAS_LOCAL_IDENTIFIER_DEFINITION= "urn:x-ogc:def:identifier:IDAS:1.0:localIdentifier";
	private static String IDAS_UNIVERSAL_IDENTIFIER_LOGICAL_HUB_DEFINITION= "urn:x-ogc:def:identifier:IDAS:1.0:UniversalIdentifierOfLogicalHub";
	
	private static String idasPhenomenomPrefix= "urn:x-ogc:def:phenomenon:IDAS:1.0:";
	private static Map<String, String> idasPhenomenomMap = new HashMap<String, String>();
	static{
		idasPhenomenomMap.put(Resource.PHENOMENOM_TEMPERATURE, idasPhenomenomPrefix + "temperature");
		idasPhenomenomMap.put(Resource.PHENOMENOM_HUMIDITY, idasPhenomenomPrefix + "relativeHumidity");
		idasPhenomenomMap.put(Resource.PHENOMENOM_LIGHT, idasPhenomenomPrefix + "luminousIntensity");  //TODO: comment out to support light measurements
		idasPhenomenomMap.put(Resource.PHENOMENOM_WIND_SPEED, idasPhenomenomPrefix + "windSpeed");
		idasPhenomenomMap.put(Resource.PHENOMENOM_CO, idasPhenomenomPrefix + "COConcentration");
		idasPhenomenomMap.put(Resource.PHENOMENOM_CO2, idasPhenomenomPrefix + "CO2Concentration");
		idasPhenomenomMap.put(Resource.PHENOMENOM_PRESSURE, idasPhenomenomPrefix + "pressure");
		idasPhenomenomMap.put(Resource.PHENOMENOM_BAROMETRIC_PRESSURE, idasPhenomenomPrefix + "atmosphericPressure");
	}
	
	private static String idasUomPrefix= "urn:x-ogc:def:uom:IDAS:1.0:";
	private static Map<String, String> idasUOMMap = new HashMap<String, String>();
	static{
		idasUOMMap.put(Resource.UOM_CELSIUS, "celsius");
		idasUOMMap.put(Resource.UOM_KELVIN, "kelvin");
        idasUOMMap.put(Resource.UOM_CANDLE, "candela");
		idasUOMMap.put(Resource.UOM_PERCENT, "percent");
		idasUOMMap.put(Resource.UOM_KMH, "kilometersPerHour");
		idasUOMMap.put(Resource.UOM_PARTS_PER_MILLION, "partsPerMillion");
		idasUOMMap.put(Resource.UOM_PARTS_PER_BILLION, "partsPerBillion");
		idasUOMMap.put(Resource.UOM_PASCAL, "pascal");
		idasUOMMap.put(Resource.UOM_HECTO_PASCAL, "hectoPascal");	
		idasUOMMap.put(Resource.UOM_DIMENSIONLESS, "dimensionless");
	
	}
	
	private static final String TEMPLATE_REGISTER_SENSOR = "registerSensor";
	private static final String TEMPLATE_INSERT_OBSERVATION = "insertObservation";
	
	private Map<String, RequestBaseType> sensorMLMessageTemplateMap = new HashMap<String, RequestBaseType>();
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
	
	public static String getIdasPhenomenom(Resource resource){
		return idasPhenomenomMap.get(resource.getName());
	}
	
	public static String getIdasUOMCode(String uom){
		return idasUOMMap.get(uom);
	}
	
	public static String getIdasTimestamp(long timestamp){
		return dateFormat.format(new Date(timestamp));
	}
	
	public static String getIdasUOMDefinition(String uom){
		String idasUomCode = idasUOMMap.get(uom);
		return idasUomCode == null ? null : idasUomPrefix + idasUomCode;
	}
	
	public void init() throws Exception{
		
		InputStream registerSensorTemplateInputStream = SensorMLMessageAdapter.class.getResourceAsStream("/templates/message_working.xml");
		InputStream insertObservationTemplateInputStream = SensorMLMessageAdapter.class.getResourceAsStream("/templates/insert_observation_template.xml");
		try{
			RegisterSensor registerSensorTemplate = (RegisterSensor) (Utils.getIDASProtcolUnmarshaller().unmarshal(registerSensorTemplateInputStream));
			InsertObservation insertObservationTemplate = (InsertObservation) (Utils.getIDASProtcolUnmarshaller().unmarshal(insertObservationTemplateInputStream));
			
			sensorMLMessageTemplateMap.put(TEMPLATE_REGISTER_SENSOR, registerSensorTemplate);
			sensorMLMessageTemplateMap.put(TEMPLATE_INSERT_OBSERVATION, insertObservationTemplate);
			
		} finally{
			if(registerSensorTemplateInputStream != null){
				registerSensorTemplateInputStream.close();
			}
			if(insertObservationTemplateInputStream != null){
				insertObservationTemplateInputStream.close();
			}
		}
    }
	
	private RequestBaseType getMessageTemplate(String messageId){
		RequestBaseType result = sensorMLMessageTemplateMap.get(messageId);
		return (RequestBaseType)result.clone();
	}
	
	@SuppressWarnings({ "restriction", "unchecked" })
	public RegisterSensor getRegisterSensorMessage(String gatewayLogicalName, NodeDescriptor nodeDescriptor) throws VitroGatewayException{
		
		RegisterSensor registerSensor = null;
		
		try{
			
			registerSensor = (RegisterSensor)getMessageTemplate(TEMPLATE_REGISTER_SENSOR);
			
//			logger.debug("cachedMessage = " + (RegisterSensor)sensorMLMessageTemplateMap.get(TEMPLATE_REGISTER_SENSOR));
//			logger.debug("clonedMessage = " + (RegisterSensor)sensorMLMessageTemplateMap.get(TEMPLATE_REGISTER_SENSOR));
			
			SystemType system = ((JAXBElement<SystemType>)registerSensor.getSensorDescription().getAny()).getValue();
			
			List<Identification> identificationList = system.getIdentification();
			
			Identification identification = identificationList.get(0);
			List<Identifier> identifierList = identification.getIdentifierList().getIdentifier();
			
			for (Identifier identifier : identifierList) {
				
				Term identifierTerm = identifier.getTerm(); 
				
				if(identifierTerm.getDefinition().equals(IDAS_LOCAL_IDENTIFIER_DEFINITION)){
					identifierTerm.setValue(nodeDescriptor.getId());
				}
				
				if(identifierTerm.getDefinition().equals(IDAS_UNIVERSAL_IDENTIFIER_LOGICAL_HUB_DEFINITION)){
					identifierTerm.setValue(gatewayLogicalName);
				}
			}
			
			List<Resource> resourceList = nodeDescriptor.getResourcelist();
			
			List<IoComponentPropertyType> inputList = system.getInputs().getInputList().getInput();
			inputList.clear();
			
			List<IoComponentPropertyType> outputList = system.getOutputs().getOutputList().getOutput();
			outputList.clear();
			
			
			for (Resource resource : resourceList) {
				
				inputList.add(getInputComponent(resource));
				outputList.add(getOutputComponent(resource));
			}
			
			
		} catch(Exception e){
			throw new VitroGatewayException("Error during registerSensor message producing", e);
		}
		
		return registerSensor;
	}
	
	@SuppressWarnings({ "restriction", "unchecked" })
	public InsertObservation getInsertObservationMessage(String gatewayLogicalName, String assignedSensorId, Observation obs) throws VitroGatewayException{
		
		Node node = obs.getNode();
		
		/*
		* 
		* A.L. -> So in the "AssignedSensorId" element of InsertObservation message I have not to set the "AssignedSensorId" specified in RegisterSensorResponse, but the identifier of the system used in RegisterSensor message.
		*		
		* D.F -> That AssignedSensorID is the name of your sensor.
		*
		* A.L. ->  I noticed that "id" attribute  of the "Observation" element can be set to an arbirary value.
		*
		* D.F -> You can choose any id, and the response would be positive, but you should follow the notation showed in the example “IDAS_CODE_provided_with_the_Register_Sensor-GW_Name-Sensor_NAME, as IDAS internally requires this codification
		* 
		* */
		
		InsertObservation insertObservation = (InsertObservation)getMessageTemplate(TEMPLATE_INSERT_OBSERVATION);
		
		//That AssignedSensorID is the name of your sensor.
		insertObservation.setAssignedSensorId(node.getId());
		ObservationType observation = insertObservation.getObservation();
		
		//IDAS_CODE_provided_with_the_Register_Sensor-GW_Name-Sensor_NAME
		observation.setId(gatewayLogicalName + "." + node.getId());
		
		///////// TIME ///////////
		List<String> temp = ((TimeInstantType)observation.getSamplingTime().getTimeObject().getValue()).getTimePosition().getValue();
		temp.clear();
		temp.add(dateFormat.format(new Date(obs.getTimestamp())));
		
		/////// OBSERVED PROPERTY //////////////
		logger.debug("IDAS resource = {}", getIdasPhenomenom(obs.getResource()));
		observation.getObservedProperty().setHref(getIdasPhenomenom(obs.getResource()));
		
		////// PROCEDURE //////////////
		observation.getProcedure().setHref(node.getId());
		
		////// PARAMETER LIST //////////////
		List<AnyDataPropertyType> parameterList = observation.getParameter();
		
		for (AnyDataPropertyType anyDataPropertyType : parameterList) {
			if(anyDataPropertyType.getHref().equals(IDAS_UNIVERSAL_IDENTIFIER_LOGICAL_HUB_DEFINITION)){
				anyDataPropertyType.getText().setValue(gatewayLogicalName);
			}
        }
		
		////// QUANTITY //////////////
		Quantity quantity = ((QuantityPropertyType)observation.getResult()).getQuantity();
		
		quantity.setDefinition(getIdasPhenomenom(obs.getResource()));
		quantity.setValue(Double.parseDouble(obs.getValue()));
		quantity.getUom().setCode(getIdasUOMCode(obs.getUom()));
		
		return insertObservation;
	}
	
	private IoComponentPropertyType getInputComponent(Resource resource){
		IoComponentPropertyType inputElement = new IoComponentPropertyType();
		
		inputElement.setName(resource.getName());
		ObservableProperty observableProperty = new ObservableProperty();
		inputElement.setObservableProperty(observableProperty);
		observableProperty.setDefinition(getIdasPhenomenom(resource));
		return inputElement;
	}
	
	private IoComponentPropertyType getOutputComponent(Resource resource){
		IoComponentPropertyType outputElement = new IoComponentPropertyType();
		
		outputElement.setName(resource.getName());
		
		Quantity quantity = new Quantity();
		outputElement.setQuantity(quantity);
		
		quantity.setDefinition(getIdasPhenomenom(resource));
		UomPropertyType uom = new UomPropertyType();
		uom.setHref(getIdasUOMDefinition(resource.getUnityOfMeasure()));
		quantity.setUom(uom);
		
		return outputElement;
	}
	
}
