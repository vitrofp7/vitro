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
package vitro.dcaintercom.communication.unica;

import java.util.Date;

/**
 * The static class SensorData contains the messages that might be sent to DCA in order to interact with it.
 * The functionalities supported by this class are related to adding, deleting and querying sensors 
 * @author David Ferrer
 *
 */

public class SensorData {

	private static final String getDevicesByServiceXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
					"xmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\" \n" +
					"xmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\" \n" +
					"xmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\" \n" +
					"xmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">\n" +
					"\t<soapenv:Header>\n" +
					"\t\t<tran:transactionInfoHeader>\n" +
					"\t\t\t<tran:servId>0</tran:servId>\n" +
					"\t\t\t<tran:appId>#appId#</tran:appId>\n" +
					"\t\t\t<tran:assetId>0</tran:assetId>\n" +
					"\t\t\t<tran:transactionId>0</tran:transactionId>\n" +
					"\t\t\t<tran:appProviderId>0</tran:appProviderId>\n" +
					"\t\t</tran:transactionInfoHeader>\n" +
					"\t\t<sec:simpleOAuthHeader>\n" +
					"\t\t</sec:simpleOAuthHeader>\n" +
					"\t</soapenv:Header>\n" +
					"\t<soapenv:Body>\n" +
					"\t\t<typ:getDataByService>\n" +
					"\t\t\t<typ:serviceID>\n" +
					"\t\t\t\t<typ:logicalName></typ:logicalName>\n" +
					"\t\t\t</typ:serviceID>\n" +
					"\t\t\t<typ:devices></typ:devices>\n" +
					"\t\t</typ:getDataByService>\n" +
					"\t</soapenv:Body>\n" +
					"</soapenv:Envelope>";

	private static final String createConcentratorXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
					"\n<SOAP-ENV:Envelope "+
					"\nxmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\""+
					"\nxmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\""+
					"\nxmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
					"\nxmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""+
					"\nxmlns:uct=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\""+
					"\nxmlns:enum-1=\"http://tempuri.org/enum-1.xsd\""+
					"\nxmlns:enum-2=\"http://tempuri.org/enum-2.xsd\""+
					"\nxmlns:ns1=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\""+
					"\nxmlns:uch=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\""+
					"\nxmlns:ucf=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults\""+
					"\nxmlns:m2msdatas1=\"http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/sensordata/v1/services\""+
					"\nxmlns:m2msdatas1t=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">"+
					"\n<SOAP-ENV:Header>"+
					"\n<ns1:transactionInfoHeader>"+
					"\n<ns1:servId>0</ns1:servId>"+
					"\n<ns1:appId>#appId#</ns1:appId>"+
					"\n<ns1:assetId>0</ns1:assetId>"+
					"\n<ns1:transactionId/>"+
					"\n<ns1:appProviderId/>"+
					"\n</ns1:transactionInfoHeader>"+
					"\n<uch:simpleOAuthHeader>"+
					"\n</uch:simpleOAuthHeader>"+
					"\n</SOAP-ENV:Header>"+
					"\n<SOAP-ENV:Body>"+
					"\n<m2msdatas1t:addDataByService>"+
					"\n<m2msdatas1t:universalConcentrators>"+
					"\n<m2msdatas1t:universalConcentrator>"+
					"\n<m2msdatas1t:universalConcentrator>#hubId#</m2msdatas1t:universalConcentrator>"+
					"\n</m2msdatas1t:universalConcentrator>"+
					"\n</m2msdatas1t:universalConcentrators>"+
					"\n</m2msdatas1t:addDataByService>"+
					"\n</SOAP-ENV:Body>"+
					"\n</SOAP-ENV:Envelope>";	

	private static final String getConcentratorsByServiceXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope " +
					"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" \n" +
					"xmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\" \n" +
					"xmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\" \n" +
					"xmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\" \n" +
					"xmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">\n" +
					"\t<soapenv:Header>\n" +
					"\t\t<tran:transactionInfoHeader>\n" +
					"\t\t\t<tran:servId>0</tran:servId>\n" +
					"\t\t\t<tran:appId>#appId#</tran:appId>\n" +
					"\t\t\t<tran:assetId>0</tran:assetId>\n" +
					"\t\t\t<tran:transactionId>0</tran:transactionId>\n" +
					"\t\t\t<tran:appProviderId>0</tran:appProviderId>\n" +
					"\t\t</tran:transactionInfoHeader>\n" +
					"\t\t<sec:simpleOAuthHeader>\n" +
					"\t\t</sec:simpleOAuthHeader>\n" +
					"\t</soapenv:Header>\n" +
					"\t<soapenv:Body>\n" +
					"\t\t<typ:getDataByService>\n" +
					"\t\t\t<typ:serviceID>\n" +
					"\t\t\t\t<typ:logicalName></typ:logicalName>\n" +
					"\t\t\t</typ:serviceID>\n" +
					"\t\t\t<typ:concentrators></typ:concentrators>\n" +
					"\t\t</typ:getDataByService>\n" +
					"\t</soapenv:Body>\n" +
					"</soapenv:Envelope>";

	private static final String deleteConcentratorXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope " +
					"xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
					"\nxmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\"" +
					"\nxmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\"" +
					"\nxmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\"" +
					"\nxmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">" +
					"\n<soapenv:Header>" +
					"<tran:transactionInfoHeader>" +
					"<tran:servId>0</tran:servId>" +
					"<tran:appId>#appId#</tran:appId>" +
					"<tran:assetId>0</tran:assetId>" +
					"<tran:transactionId>0</tran:transactionId>" +
					"<tran:appProviderId>0</tran:appProviderId>" +
					"</tran:transactionInfoHeader>" +
					"<sec:simpleOAuthHeader>" +
					"</sec:simpleOAuthHeader>" +
					"</soapenv:Header>" +
					"<soapenv:Body>" +
					"<typ:deleteDataByService>" +
					"<typ:serviceID>" +
					"<typ:logicalName>#appId#</typ:logicalName>" +
					"</typ:serviceID>" +
					"<typ:universalConcentrators>" +
					"<!--Zero or more repetitions:-->" +
					"<typ:universalConcentrator>" +
					"<typ:universalConcentrator>#UniversalConcentrator#</typ:universalConcentrator>" +
					"</typ:universalConcentrator>" +
					"</typ:universalConcentrators>" +
					"</typ:deleteDataByService>" +
					"</soapenv:Body>" +
					"</soapenv:Envelope>";

	private static final String getLastMeasureByDeviceNameXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"+ 
					"xmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\"\n"+ 
					"xmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\"\n" +
					"xmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\"\n" +
					"xmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">\n"+
					"<soapenv:Header>\n"+
					"<tran:transactionInfoHeader>\n"+
					"<tran:servId>0</tran:servId>\n"+
					"<tran:appId>#appId#</tran:appId>\n"+
					"<tran:assetId>0</tran:assetId>\n"+
					"<tran:transactionId>0</tran:transactionId>\n"+
					"<tran:appProviderId>0</tran:appProviderId>\n"+
					"</tran:transactionInfoHeader>\n"+
					"<sec:simpleOAuthHeader>\n"+
					"</sec:simpleOAuthHeader>\n"+
					"</soapenv:Header>\n"+
					"<soapenv:Body>\n"+
					"<typ:getDeviceData>\n"+
					"<typ:serviceID>\n"+
					"<typ:logicalName></typ:logicalName>\n"+
					"</typ:serviceID>\n"+
					"<typ:device>\n"+
					"<typ:globalIdentifier>#SensorId#</typ:globalIdentifier>\n"+
					"</typ:device>\n"+   
					"<typ:lastMeasure></typ:lastMeasure>\n"+
					"</typ:getDeviceData>\n"+
					"</soapenv:Body>\n"+
					"</soapenv:Envelope>";

	private static final String getMeasurementsWithinAnIntervalXML  = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope "+
					"\nxmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""+ 
					"\nxmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\""+ 
					"\nxmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\""+
					"\nxmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\""+ 
					"\nxmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">"+
					"\n<soapenv:Header>" +
					"\n<tran:transactionInfoHeader>" +
					"\n<tran:servId>0</tran:servId>" +
					"\n<tran:appId>#appId#</tran:appId>" +
					"\n<tran:assetId>0</tran:assetId>" +
					"\n<tran:transactionId>0</tran:transactionId>" +
					"\n<tran:appProviderId>0</tran:appProviderId>" +
					"\n</tran:transactionInfoHeader>" +
					"\n<sec:simpleOAuthHeader/>" +
					"\n</soapenv:Header>" +
					"\n<soapenv:Body>" +
					"\n<typ:getDeviceData>" +
					"\n<typ:serviceID>" +
					"\n<typ:logicalName>#LogicalName#</typ:logicalName>" +
					"\n</typ:serviceID>" +
					"\n<typ:device>" +
					"\n<typ:globalIdentifier>#SensorId#</typ:globalIdentifier>" +
					"\n</typ:device>" +
					"\n<typ:measures>" +
					"\n<typ:filter>" +
					"\n<typ:from>#from#</typ:from>" +
					"\n<typ:to>#to#</typ:to>" +
					"\n</typ:filter>" +
					"\n<typ:attributes>" +
					"\n<typ:name>#phenomenom#</typ:name>" +
					"\n</typ:attributes>" +
					"\n</typ:measures>" +
					"\n</typ:getDeviceData>" +
					"\n</soapenv:Body>" +
					"\n</soapenv:Envelope>";

	private static final String getConcentratorDataXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope " +
					"\nxmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"" +
					"\nxmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\"" + 
					"\nxmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\"" +
					"\nxmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\"" +
					"\nxmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">" +
					"\n<soapenv:Header>" +
					"\n<tran:transactionInfoHeader>" +
					"\n<tran:servId>0</tran:servId>" +
					"\n<tran:appId>#appId#</tran:appId>" +
					"\n<tran:assetId>0</tran:assetId>" +
					"\n<tran:transactionId>0</tran:transactionId>" +
					"\n<tran:appProviderId>0</tran:appProviderId>" +
					"\n</tran:transactionInfoHeader>" +
					"\n<sec:simpleOAuthHeader>" +
					"\n</sec:simpleOAuthHeader>" +
					"\n</soapenv:Header>" +
					"\n<soapenv:Body>" +
					"\n<typ:getConcentratorData>" +
					"\n<!--Optional:-->" +
					"\n<typ:serviceID>" +
					"\n<typ:logicalName></typ:logicalName>" +
					"\n</typ:serviceID>" +
					"\n<typ:concentratorID>" +
					"\n<typ:logicalName>#GW#</typ:logicalName>" +
					"\n</typ:concentratorID>" +
					"\n</typ:getConcentratorData>" +
					"\n</soapenv:Body>" +
					"\n</soapenv:Envelope>";

	
	private static final String getDeviceDataSMLXML = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n"+
					"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"+ 
					"xmlns:tran=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header\"\n"+ 
					"xmlns:sec=\"http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers\"\n" +
					"xmlns:v1=\"http://www.telefonica.com/schemas/UNICA/SOAP/common/v1\"\n" +
					"xmlns:typ=\"http://www.telefonica.com/schemas/UNICA/SOAP/m2m/sensordata/v1/types\">\n"+
					"<soapenv:Header>\n"+
					"<tran:transactionInfoHeader>\n"+
					"<tran:servId>0</tran:servId>\n"+
					"<tran:appId>#appId#</tran:appId>\n"+
					"<tran:assetId>0</tran:assetId>\n"+
					"<tran:transactionId>0</tran:transactionId>\n"+
					"<tran:appProviderId>0</tran:appProviderId>\n"+
					"</tran:transactionInfoHeader>\n"+
					"<sec:simpleOAuthHeader>\n"+
					"</sec:simpleOAuthHeader>\n"+
					"</soapenv:Header>\n"+
					"<soapenv:Body>\n"+
					"<typ:getDeviceData>\n"+
					"<typ:serviceID>\n"+
					"<typ:logicalName></typ:logicalName>\n"+
					"</typ:serviceID>\n"+
					"<typ:device>\n"+
					"<typ:globalIdentifier>#SensorId#</typ:globalIdentifier>\n"+
					"</typ:device>\n"+   
					"<typ:sensorML></typ:sensorML> \n" +
					"</typ:getDeviceData>\n"+
					"</soapenv:Body>\n"+
					"</soapenv:Envelope>";
					
	/** 
	 * Creates a GW to a specific Service ID
	 * @param appId is the service Identifier
	 * @param hubId is the name of the new hub/concentrator that it is wanted to add
	 * @return the XML ready to be sent
	 */
	public static String createConcentrator(int appId, String hubId) {
		String message = ""+createConcentratorXML;
		message = message.replace("#appId#",String.valueOf(appId));
		message = message.replace("#hubId#", hubId);
		return message;
	}

	/**
	 * Deletes a Gateway
	 * @param appId is the service Identifier
	 * @param UniversalConcentrator is the concentrator that we want to delete
	 * @return the XML ready to be sent
	 */
	public static String deleteConcentrator(int appId, String UniversalConcentrator) {
		String message = ""+deleteConcentratorXML;
		message = message.replace("#appId#",String.valueOf(appId));
		message = message.replace("#UniversalConcentrator#", UniversalConcentrator);
		return message;

	}

	/**
	 * Provides specific information of a Concentrator
	 * @param appId is the Service ID
	 * @param Concentrator is the GW that we want to obtain info about
	 * @return
	 */
	public static String getConcentratorData(int appId, String Concentrator) {
		String message = ""+getConcentratorDataXML;
		message = message.replace("#appId#",String.valueOf(appId));
		message = message.replace("#GW#", Concentrator);
		return message;
	}

	public static String getDeviceDataSML(int appId, String deviceId) {
		String message = "" + getDeviceDataSMLXML;
		message = message.replace("#appId#",String.valueOf(appId));
		message = message.replace("#SensorId#", deviceId);
		return message;
	}

	/**
	 * Gives a list of devices matching a Service ID
	 * @param appId is the service Identifier
	 * @return the XML ready to send
	 */
	public static String getDevicesList(int appId) {
		String message =""+ getDevicesByServiceXML;
        message = message.replace("#appId#", Integer.toString(appId));
		return message;
	}

	/**
	 * Provides a list of GW matching a specific Service ID
	 * @param appId is the Service Identifier
	 * @return
	 */
	public static String getConcentratorsList(int appId) {
		String message =""+ getConcentratorsByServiceXML;
		message = message.replace("#appId#",String.valueOf(appId));
		return message;
	}

	/**
	 * Provides Historical information of a sensor within a period of time
	 * @param appId is the Service ID of the sensor
	 * @param LogicalName is the Logical Name assigned to the GW of the sensor
	 * @param SensorId is the sensor name, including the GW name
	 * @param phenomenom to be requested
	 * @param from initial date (either a String or a Date)
	 * @param to final date (either a String or a Date)
	 * @return the XML ready to be forwarded
	 */
	public static String getHistoricData(int appId, String LogicalName, String SensorId, String phenomenom, Object from, Object to) {
		String message, fromDate, toDate;
		message = ""+getMeasurementsWithinAnIntervalXML;

		if(from instanceof Date && to instanceof Date) {
			fromDate = from.toString();
			toDate = to.toString();
		}
		else if (from instanceof String && to instanceof String) {
			fromDate = (String) from;
			toDate = (String) to;
		}
		else { //Add new formats if necessary
			fromDate = "";
			toDate = "";
		}
		message = message.replace("#appId#", String.valueOf(appId));
		message = message.replace("#LogicalName#", LogicalName).replace("#SensorId#", SensorId);
		message = message.replace("#phenomenom#", phenomenom);
		message = message.replace("#from#", fromDate).replace("#to#", toDate);

		return message;
	}

	/**
	 * Provides the last measurement taken by a Sensor. Can be used as a single request for information
	 * @param appId is the Service Id
	 * @param SensorId is the name of the Sensor 
	 * @return the XML text, ready to forward
	 */
	public static String getLastMeasurement (int appId, String SensorId) {
		String message = ""+getLastMeasureByDeviceNameXML;
		message = message.replace("#appId#",String.valueOf(appId));
		message = message.replace("#SensorId#", SensorId);
		return message;
	}
}
