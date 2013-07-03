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

package vitro.virtualsensor.utils;



import java.util.*;

import javax.xml.xpath.XPathExpressionException;

/**
 *
 * @author Andres Picazo Cuesta
 */
public class GwDictionary {
    
    private HashMap<String,Phenomena> phenoms;
    private HashMap<String,Unit> units;
    
    private ArrayList<Phenomena> sortedPhenomena;
    private ArrayList<Unit> sortedUnit;

    public GwDictionary() throws XPathExpressionException{
    	this("phenomena.xml", "units.xml");
    }
    
    public GwDictionary (String phenomenaFile, String unitsFile) throws XPathExpressionException {
        
        phenoms = new HashMap<String,Phenomena>();
        units = new HashMap<String,Unit>();
        sortedPhenomena = null;
        sortedUnit = null;
        
        String query;
        // Extract phenomena ---------------------------------------------------
        XmlFile pf = new XmlFile(phenomenaFile);
        query = "/Dictionary/dictionaryEntry/*/@id";
        String [] ids = pf.parseXpathValues(query);
        query = "/Dictionary/dictionaryEntry/*/name/text()";
        String [] light = pf.parseXpathValues(query);
        query = "/Dictionary/dictionaryEntry/*/identifier/text()";
        String [] uri = pf.parseXpathValues(query);
        int k = 0;
        for (int i = 0; i < ids.length; i++) {
            Phenomena phenomena = new Phenomena();
            phenomena.id = ids[i];
            phenomena.urn = uri[i];
            
            String lightUrn; // = light[1+(i*2)];
            while (!(lightUrn = light[k++]).contains("urn:x-ogc:")) {}
            StringTokenizer st = new StringTokenizer(lightUrn,":");
            while(st.hasMoreTokens()) phenomena.lightCode = st.nextToken();
            phenoms.put(phenomena.id, phenomena);
//            System.out.println(phenomena.toString());
        }
        // Extract units -------------------------------------------------------
        XmlFile uf = new XmlFile(unitsFile);
        query = "/Dictionary/dictionaryEntry/*/@id";
        ids = uf.parseXpathValues(query);
        query = "/Dictionary/dictionaryEntry/*/name/text()";
        light = uf.parseXpathValues(query);
//        query = "/Dictionary/dictionaryEntry/*/quantityType/text()";
//        uri = uf.parseXpathValues(query);
        k = 0;
        for (int i = 0; i < ids.length; i++) {
            Unit unit = new Unit();
            unit.id = ids[i];
            unit.uom = light[k++];
            StringTokenizer st = new StringTokenizer(light[k++],":");
            while(st.hasMoreTokens()) unit.lightCode = st.nextToken();
//            st = new StringTokenizer(uri[i],":");
//            while(st.hasMoreTokens())  unit.associatedPhenomena = st.nextToken();
            units.put(unit.id, unit);
//            System.out.println(unit.toString());
        }
//        System.out.println(uf.getText());
    }
    
    public boolean existsPhenomena (String phenomena) {
        return phenoms.containsKey(phenomena);
    }
    
    public boolean existsUnit (String unit) {
        return units.containsKey(unit);
    }
    
    public ArrayList<Phenomena> getPhenomena () {
        ArrayList<Phenomena> phList = new ArrayList<Phenomena>();
        Iterator<String>it = phenoms.keySet().iterator();
        while (it.hasNext()) {
            Phenomena ph = phenoms.get(it.next());
            phList.add(ph);
        }
        return phList;
    }
    
    public ArrayList<Unit> getUnits () {
        ArrayList<Unit> uList = new ArrayList<Unit>();
        Iterator<String>it = units.keySet().iterator();
        while (it.hasNext()) {
            Unit ph = units.get(it.next());
            uList.add(ph);
        }
        return uList;
    }
    
    public ArrayList<Phenomena> getSortedPhenomena () {
        if (sortedPhenomena == null) {
            HashMap<Integer,Phenomena> phMap = new HashMap<Integer,Phenomena>();
            Iterator<String>it = phenoms.keySet().iterator();
            while (it.hasNext()) {
                Phenomena p = phenoms.get(it.next());
                phMap.put(Integer.parseInt(p.lightCode), p);
            }
            Integer[]codes = phMap.keySet().toArray(new Integer[phenoms.keySet().size()]);
            Arrays.sort(codes);
            sortedPhenomena = new ArrayList<Phenomena>();
            for (Integer i : codes) {
                sortedPhenomena.add(phMap.get(i));
            }
        }
        return sortedPhenomena;
    }
    public ArrayList<Unit> getSortedUnits () {
        if (sortedUnit == null) {
            HashMap<Integer,Unit> utMap = new HashMap<Integer,Unit>();
            Iterator<String>it = units.keySet().iterator();
            while (it.hasNext()) {
                Unit u = units.get(it.next());
                utMap.put(Integer.parseInt(u.lightCode), u);
            }
            Integer[]codes = utMap.keySet().toArray(new Integer[units.keySet().size()]);
            Arrays.sort(codes);
            sortedUnit = new ArrayList<Unit>();
            for (Integer i : codes) {
                sortedUnit.add(utMap.get(i));
            }
        }
        return sortedUnit;
    }
    
    public ArrayList<String> getPhenomenaArrayList(){
		ArrayList<Phenomena> p = getSortedPhenomena();
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < p.size(); i ++)
			result.add(p.get(i).id);
		return result;
	}
	
	public ArrayList<String> getUnitsArrayList(){
		ArrayList<Unit> u = getSortedUnits();
		ArrayList<String> result = new ArrayList<String>();
		for (int i = 0; i < u.size(); i ++)
			result.add(u.get(i).id);
		return result;
	}
 }
