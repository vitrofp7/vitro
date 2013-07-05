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



import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import vitro.dcaintercom.communication.common.XPathString;

/**
 *
 * @author Andres Picazo Cuesta
 */
public class DCADictionary {

	private HashMap<String, String> phenomSensorML;

	private HashMap<String, String> unitsSensorML;

	// phenomenon, [units]
	private HashMap<String, ArrayList<String>> associatatedUnits;
	private static final String phenomenaPath = "..\\utils\\phenomena.xml";
	private static final String unitPath = "..\\utils\\units.xml";

	private XPathString xPathString;

	private static DCADictionary instance = null;

	public static DCADictionary getInstance () {
		if (instance == null) {
			instance = new DCADictionary();
		}
		return instance;
	}

	private DCADictionary () {

		phenomSensorML = new HashMap<String, String>();

		unitsSensorML = new HashMap<String, String>();

		associatatedUnits = new HashMap<String, ArrayList<String>>();

		try {
			processPhenomena();
			//processUnits();
		} catch (Exception e) {
			System.out.println("Ex: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void processPhenomena() throws Exception {
		File f = new File(phenomenaPath);

		ArrayList<String> eventList = new ArrayList<String>();
		ArrayList<String> eventIden = new ArrayList<String>();

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(f);


		XPath xpath = XPathFactory.newInstance().newXPath();

		//String getNames = "//Dictionary/dictionaryEntry/Phenomenon/name/text()";
		//String getId = "//Dictionary/dictionaryEntry/Phenomenon[@id='mass']/text()";
		//String getId = "//Dictionary/dictionaryEntry/Phenomenon/@id";

		String query;

		/* Get all phenomenon id's */
		query = "//Dictionary/dictionaryEntry/*/@id";
		XPathExpression expr = xpath.compile(query);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			eventList.add(node.getNodeValue());
		}

		query = "//Dictionary/dictionaryEntry/*/identifier/text()";
		expr = xpath.compile(query);
		result = expr.evaluate(doc, XPathConstants.NODESET);
		nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			eventIden.add(node.getNodeValue());
		}

		for (int i = 0; i < eventList.size(); i++) {
			phenomSensorML.put(eventList.get(i), eventIden.get(i));
			associatatedUnits.put(eventIden.get(i), new ArrayList<String>());
		}
	}

	private void processUnits() throws Exception {
		File f = new File(unitPath);

		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(f);

		XPath xpath = XPathFactory.newInstance().newXPath();

		String query;

		/* Get all units */        
		query = "//Dictionary/dictionaryEntry/*/@id";
		XPathExpression expr = xpath.compile(query);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		for (int i = 0; i < nodes.getLength(); i++) {
			//Node node = nodes.item(i);
			String id = nodes.item(i).getNodeValue();
			String[] units = getUnitURN(doc,id);
			StringTokenizer st = new StringTokenizer(units[1], ":");
			while (st.hasMoreTokens()) 
				st.nextToken();
			unitsSensorML.put(id, units[0]);
			// Get phenomenon associated with this measure
			String xq = "//Dictionary/dictionaryEntry/*[@id=\""+id+"\"]/quantityType/text()";
			String [] s = xPathString.parseXpathValues(xq); 
			if (s != null && s.length > 0) {
				ArrayList<String> un = associatatedUnits.get(s[0]);
				if (un!= null) un.add(units[0]);
			} 
		}
	}

	private String[] getUnitURN (Document doc, String id) throws Exception {
		String [] results = new String[2];
		XPath xpath = XPathFactory.newInstance().newXPath();        
		String query;
		query = "//Dictionary/dictionaryEntry/*[@id='" + id + "']/name/text()";
		XPathExpression expr = xpath.compile(query);
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList) result;
		results[0] = nodes.item(0).getNodeValue();
		results[1] = nodes.item(1).getNodeValue();
		return results;
	}

	/* ---------------------------------------------------------------------- */
	public void printPhenomTranslatorMap () {
		System.out.println("");
		Iterator<String> it = phenomSensorML.keySet().iterator();
		String key = "";
		System.out.printf("%-30s   %-8s      %s\n", "Phenomenon ID", "Light", "SensorML");
		System.out.println("------------------------------------------------------------------------------");
		while (it.hasNext()) {
			key = it.next();
			System.out.printf("[%-30s] (%-8s) -> %s\n", key, phenomSensorML.get(key));
		}
	}
	/* ---------------------------------------------------------------------- */
	public void printUnitAssociation () {
		System.out.println("");
		Iterator<String> it = associatatedUnits.keySet().iterator();
		String key = "";
		System.out.printf("%-30s %s\n", "Phenomenon", "Units");
		System.out.println("------------------------------------------------------------------------------");
		while (it.hasNext()) {
			key = it.next();
			ArrayList<String> units = associatatedUnits.get(key);
			System.out.println (key);            
			for (int i = 0; i<units.size(); i++) 
				System.out.println ("\t$s");
		}
	}
	
	public Vector<String> getPhenomTranslatorMap () {
		Iterator<String> it = phenomSensorML.keySet().iterator();
		String key = "";
		Vector<String> result = new Vector<String>();
		while (it.hasNext()) {
			key = it.next();
			result.add(key);
		}
		return result;
	}
	/* ---------------------------------------------------------------------- */
	public void printUnitsTranslatorMap () {
		System.out.println("");
		Iterator<String> it = unitsSensorML.keySet().iterator();
		String key = "";
		System.out.printf("%-30s   %-8s      %s\n", "Unit ID", "Light", "SensorML");
		System.out.println("------------------------------------------------------------------------------");
		while (it.hasNext()) {
			key = it.next();
			System.out.printf("[%-30s] (%-8s) -> %s\n", 
					key, unitsSensorML.get(key));
		}
	}
	
	public Vector<String> getUnitsTranslatorMap(){
		Iterator<String> it = unitsSensorML.keySet().iterator();
		String key = "";
		Vector<String> result = new Vector<String>();
		while (it.hasNext()) {
			key = it.next();
			System.out.println("&"+key);
			result.add(unitsSensorML.get(key));
		}
		return result;
	}
	/* ---------------------------------------------------------------------- */
	public String getPhenomenonFullURN (String phenomenon) {
		return phenomSensorML.get(phenomenon);
	}
	/* ---------------------------------------------------------------------- */

	public String getUnitML (String unit) {
		if (unitsSensorML.containsKey(unit)) {
			return unitsSensorML.get(unit);
		}
		else return "";

	}
	/* ---------------------------------------------------------------------- */
	public String getUnitFullURN (String phenomenon) {
		ArrayList<String>ref = associatatedUnits.get(phenomenon);
		if (ref != null && ref.size() != 0) return ref.get(0);
		else return "";
	}
	/* ---------------------------------------------------------------------- */
	/* ---------------------------------------------------------------------- */
}


