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
package vitro.vspEngine.service.communication.dummyXMLParseForSOS;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */

import org.w3c.dom.CharacterData;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoPoint;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Vector;

public class ParseSOSRegisterMsg {

    /**
     * @param xmlStr
     * @param out_advCapsToSensModels
     * @param out_advSmDevs
     * @param out_gwIdfromADVStrBuild
     * @return the status of the parse.
     */
    public static boolean parseXmlStrMsg(String xmlStr,
                                         HashMap<String, Vector<SensorModel>> out_advCapsToSensModels,
                                         Vector<SmartNode> out_advSmDevs,
                                         StringBuilder out_gwIdfromADVStrBuild) {
         //TODO: this dummy code SHOULD be replaced by JAXB unmarshaller
        //String str = "<ns2:senderId xmlns=\"netapp.com/fsoCanonical\">NetApp</ns2:senderId>";
        //xmlStr = xmlStr.replaceAll("(</?)[^>:]*:\\s*", "$1");
        xmlStr = xmlStr.replaceAll("(</?)ns[0-9]*:\\s*", "$1");

        //System.out.println(xmlStr);
        boolean successfulParse = false;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource(new StringReader(xmlStr));
            is.setEncoding("UTF-8");
            //InputSource is = new InputSource();
            //is.setCharacterStream(new StringReader(xmlRecords));

            Document doc = db.parse(is);
            Element docElement = (Element) doc.getDocumentElement();
            // DEBUG
            //System.out.println(docElement.getTagName());

            NodeList nodes = doc.getElementsByTagName("sml:identifier");
            if (nodes == null || nodes.getLength() == 0)
                nodes = doc.getElementsByTagName("identifier");

            // iterate the sml:identifier ()

            // String advGatewayIDStr = "";
            String smartDevSerialId = "";
            for (int i = 0; i < nodes.getLength(); i++) {
                Element identifierEl = (Element) nodes.item(i);

                NodeList termList = identifierEl.getElementsByTagName("sml:Term");
                if (termList == null || termList.getLength() == 0)
                    termList = identifierEl.getElementsByTagName("Term");

                for (int k = 0; k < termList.getLength(); k++) {
                    if (((Element) termList.item(k)).getAttribute("definition").equalsIgnoreCase("urn:x-ogc:def:identifier:IDAS:1.0:UniversalIdentifierOfLogicalHub")) {
                        NodeList valuesList = ((Element) termList.item(k)).getElementsByTagName("sml:value");
                        if (valuesList == null || valuesList.getLength() == 0)
                            valuesList = ((Element) termList.item(k)).getElementsByTagName("value");


                        Element line = (Element) valuesList.item(0);
                        out_gwIdfromADVStrBuild.append(getCharacterDataFromElement(line));
                        // DEBUG
                        //System.out.println("gateway id: " + out_gwIdfromADVStrBuild.toString());
                    } else if (((Element) termList.item(k)).getAttribute("definition").equalsIgnoreCase("urn:x-ogc:def:identifier:IDAS:1.0:serialNumber") ||
                            ((Element) termList.item(k)).getAttribute("definition").equalsIgnoreCase("urn:x-ogc:def:identifier:IDAS:1.0:LocalIdentifier")) {
                        NodeList valuesList = ((Element) termList.item(k)).getElementsByTagName("sml:value");
                        if (valuesList == null || valuesList.getLength() == 0)
                            valuesList = ((Element) termList.item(k)).getElementsByTagName("value");

                        Element line = (Element) valuesList.item(0);
                        smartDevSerialId = getCharacterDataFromElement(line);
                        // DEBUG
                        //System.out.println("Serial Number: " + getCharacterDataFromElement(line));
                        // TODO: will this work with nulls for location?


                        // TODO: there could be trouble with the ordering since we use here the GWid.
                        // TODO: if we adopt the components approach this could be easy (but the components could be optional element so it could be complex again)
                        // SensorModel sendModel = new SensorModel(GatewayIDStr, );

                    }
                }
            }
            // after parsing the basic elements (gw id and smartdev id) we proceed to the capabilities and the sensor models (sensors on devices).
            // a smart dev id should be unique within a gw domain
            // a sensor model id should be string and also unique within a gw.
            // TODO: Note. This assumes that VITRO GW sends one SmartDevice (system in sensorml) per message (it could have multiple sensors ofcourse !!
            //  TODO: Support for more smartDevs in one msg should be added (?)
            if (!out_gwIdfromADVStrBuild.toString().equalsIgnoreCase("") && !smartDevSerialId.equalsIgnoreCase("")) {

                //Vector<Integer> allSensModelsIds = new Vector<Integer>();
                Vector<SensorModel> thisNodesSensorModelsVec = new Vector<SensorModel>();
                // we now parse the "Capabilities" from the XML message for the SmartDevice
                // DEBUG
                //System.out.println("Capabilities");
                NodeList inputNodes = doc.getElementsByTagName("sml:input");
                if (inputNodes == null || inputNodes.getLength() == 0)
                    inputNodes = doc.getElementsByTagName("input");

                for (int i = 0; i < inputNodes.getLength(); i++) {
                    Vector<SensorModel> advSensModels = new Vector<SensorModel>();

                    Element inputEl = (Element) inputNodes.item(i);
                   // the name here is the simple capability name:
                    String tmpSensorID = inputEl.getAttribute("name");
                    //System.out.println("Simple name: " +tmpSimpleName );
                    // TODO: IMPORTANT!!! we replace the simple capability (no prefix) with its digest as int!!!!!
                    Integer thedigestInt = tmpSensorID.hashCode();
                    if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);

                    tmpSensorID = thedigestInt.toString(); //todo: eventually we should not use the digest anymore!!!
                    // DEBUG sensor model hash
                    //System.out.println("Simple name to md5 hash: " +tmpSimpleName + " = " +  thedigestInt.toString());


                    NodeList obsPropList = inputEl.getElementsByTagName("swe:ObservableProperty");
                    if (obsPropList == null || obsPropList.getLength() == 0)
                        obsPropList = inputEl.getElementsByTagName("ObservableProperty");

                    String tmpCapDefinition = "";
                    for (int k = 0; k < obsPropList.getLength(); k++) {
                        Element obsPropEl = (Element) obsPropList.item(k);
                        tmpCapDefinition = obsPropEl.getAttribute("definition");
                        // DEBUG
                        //System.out.println("Capability: " +tmpCapDefinition);
                        if (!tmpCapDefinition.equalsIgnoreCase("") && (out_advCapsToSensModels.keySet().isEmpty() || !out_advCapsToSensModels.keySet().contains(tmpCapDefinition))) {
                            out_advCapsToSensModels.put(tmpCapDefinition, advSensModels);
                        } else {
                            advSensModels = out_advCapsToSensModels.get(tmpCapDefinition);
                        }
                        boolean sensorModelFoundInCapsTable = false;
                        if (!advSensModels.isEmpty()) {
                            for (int sv = 0; sv < advSensModels.size(); sv++) {
                                if (advSensModels.elementAt(sv).getGatewayId().equalsIgnoreCase(out_gwIdfromADVStrBuild.toString()) && advSensModels.elementAt(sv).getSmID().equals(tmpSensorID)) {
                                    sensorModelFoundInCapsTable = true;
                                    break;
                                }
                            }
                        }
                        SensorModel tmpSensModelToAdd = new SensorModel(tmpCapDefinition, out_gwIdfromADVStrBuild.toString(), tmpSensorID, SensorModel.numericDataType, null, null);
                        if (!sensorModelFoundInCapsTable) {      //if not found in the total HashMap of Capability to SensorModels of various gateways
                            advSensModels.add(tmpSensModelToAdd);
                        }
                        if ( ! SensorModel.vectorContainsSensorModel(thisNodesSensorModelsVec, out_gwIdfromADVStrBuild.toString(),tmpSensorID )) {
                            //if not found in the vector of SensorModels for this node
                            thisNodesSensorModelsVec.add(tmpSensModelToAdd);
                        }
                    }
                }

                // "vector" of smart devices.
                //out_advSmDevs contains just the one smart device in these VITRO messages (TODO: again, later on, there may be more smart devices in a register message)

                SmartNode smDev = new SmartNode(smartDevSerialId, "Name", "LocationDesc", new GeoPoint());
                smDev.setCapabilitiesVector(thisNodesSensorModelsVec);
                out_advSmDevs.add(smDev);

                successfulParse = true;
            } // end if (there is a valid gateway id in the document
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successfulParse;
    }

    public static void main(String arg[]) {
        String xmlRecords =
                "<data>" +
                        " <employee>" +
                        "   <name>John</name>" +
                        "   <title>Manager</title>" +
                        " </employee>" +
                        " <employee>" +
                        "   <name>Sara</name>" +
                        "   <title>Clerk</title>" +
                        " </employee>" +
                        "</data>";

        String SOSMessageStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>" +
                "" +
                "<sos:RegisterSensor service=\"SOS\" version=\"1.0.0\" xsi:schemaLocation=\"http://schemas.opengis.net/sos/1.0.0/sosRegisterSensor.xsd\" xmlns:swe=\"http://www.opengis.net/swe/1.0.1\" xmlns:gml=\"http://www.opengis.net/gml\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:om=\"http://www.opengis.net/om/1.0\" xmlns:sml=\"http://www.opengis.net/sensorML/1.0.1\" xmlns:sos=\"http://www.opengis.net/sos/1.0\">" +
                "<sos:SensorDescription>" +
                "<sml:System>" +
                "<sml:identification>" +
                "<sml:IdentifierList>" +
                "<sml:identifier>" +
                "<sml:Term definition=\"urn:x-ogc:def:identifier:IDAS:1.0:serialNumber\">" +
                "<sml:value>urn:wisebed:ctitestbed:0x14e6</sml:value>" +
                "</sml:Term>" +
                "</sml:identifier>" +
                "<sml:identifier>" +
                "<sml:Term definition=\"urn:x-ogc:def:identifier:IDAS:1.0:UniversalIdentifierOfLogicalHub\">" +
                "<sml:value>vitrogw_cti</sml:value>" +
                "</sml:Term>" +
                "</sml:identifier>" +
                "</sml:IdentifierList>" +
                "</sml:identification>" +
                "<sml:classification>" +
                "<sml:ClassifierList>" +
                "<sml:classifier>" +
                "<sml:Term definition=\"urn:x-ogc:def:classifier:IDAS:1.0:system\">" +
                "<sml:value>system00001</sml:value>" +
                "</sml:Term>" +
                "</sml:classifier>" +
                "</sml:ClassifierList>" +
                "</sml:classification>" +
                "<sml:inputs>" +
                "<sml:InputList>" +
                "<sml:input name=\"temperature\">" +
                "<swe:ObservableProperty definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:temperature\"/>" +
                "</sml:input>" +
                "<sml:input name=\"light\">" +
                "<swe:ObservableProperty definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:luminousIntensity\"/>" +
                "</sml:input>" +
                "</sml:InputList>" +
                "</sml:inputs>" +
                "<sml:outputs>" +
                "<sml:OutputList>" +
                "<sml:output name=\"temperature\">" +
                "<swe:Quantity definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:temperature\">" +
                "<swe:uom xlink:href=\"urn:x-ogc:def:uom:IDAS:1.0:celsius\"/>" +
                "</swe:Quantity>" +
                "</sml:output>" +
                "<sml:output name=\"light\">" +
                "<swe:Quantity definition=\"urn:x-ogc:def:phenomenon:IDAS:1.0:luminousIntensity\">" +
                "<swe:uom xlink:href=\"urn:x-ogc:def:uom:IDAS:1.0:candle\"/>" +
                "</swe:Quantity>" +
                "</sml:output>" +
                "</sml:OutputList>" +
                "</sml:outputs>" +
                "<sml:parameters>" +
                "<sml:ParameterList>" +
                "<sml:parameter name=\"Command URL\" xlink:arcrole=\"urn:x-ogc:def:property:IDAS:1.0:read\" xlink:href=\"urn:x-ogc:def:property:IDAS:1.0:commandURL\" xlink:role=\"urn:x-ogc:def:property:IDAS:1.0:operationProperty\">" +
                "<swe:Text>" +
                "<swe:value>http://127.0.0.1:8080/CommandParser</swe:value>" +
                "</swe:Text>" +
                "</sml:parameter>" +
                "</sml:ParameterList>" +
                "</sml:parameters>" +
                "</sml:System>" +
                "</sos:SensorDescription>" +
                "<sos:ObservationTemplate>" +
                "<om:Observation>" +
                "<om:samplingTime></om:samplingTime>" +
                "<om:resultTime></om:resultTime>" +
                "<om:procedure/>" +
                "<om:observedProperty/>" +
                "<om:featureOfInterest/>" +
                "<om:result></om:result>" +
                "</om:Observation>" +
                "</sos:ObservationTemplate>" +
                "</sos:RegisterSensor>";


        //ParseSOSRegisterMsg.parse(SOSMessageStr, gwtoDevices, capsHM);

        HashMap<String, Vector<SensorModel>> advCapsToSensModels = new HashMap<String, Vector<SensorModel>>();
        Vector<SmartNode> advSmDevs = new Vector<SmartNode>();
        StringBuilder gwIdfromADV = new StringBuilder();
        if (ParseSOSRegisterMsg.parseXmlStrMsg(SOSMessageStr, advCapsToSensModels, advSmDevs, gwIdfromADV)) {
            // DEBUG
            //System.out.println("Parse Ok!");
        } else {
            System.out.println("Error in Parsing SOS Register XML!");
        }
    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }
}
