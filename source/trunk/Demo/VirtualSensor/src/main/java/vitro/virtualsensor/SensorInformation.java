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

package vitro.virtualsensor;

import java.util.ArrayList;

public class SensorInformation{
    
    private String              sensorID;
    private String              gateway;
    private String              phenomenom;
    private ArrayList<String>   inputUOM;
    private String              outputUOM;
    private ArrayList<Double>   measurements;
    private double              latitude;
    private double              longitude;

    public SensorInformation(){
        sensorID = "";
        gateway = "";
        outputUOM = "";
        phenomenom = "";
        measurements = new ArrayList<Double>();	
        inputUOM = new ArrayList<String>();
        latitude = 0.0;
        longitude = 0.0;
    }

    public SensorInformation(String sensorId){
        this.sensorID = sensorId;  
        gateway = "";
        outputUOM = "";
        phenomenom = "";
        measurements = new ArrayList<Double>();	
        inputUOM = new ArrayList<String>();
        latitude = 0.0;
        longitude = 0.0;    
    }
    
    public String getId() {
        return sensorID;
    }
    
    public void setId(String identifier) {
        this.sensorID = identifier;
    }
    public void setInputUOM(ArrayList<String> input){
        inputUOM = input;
    }
    public String getGateway() {
        return gateway;
    }
    public void setGateway(String gateway) {
        this.gateway = gateway;
    }
    public String getPhenomenom() {
        return phenomenom;
    }
    public ArrayList<String> getInputUOM() {
        return inputUOM;
    }
    public void addInputUOM(String inputUOM) {
        this.inputUOM.add(inputUOM);
    }
    public String getOutputUOM() {
        return outputUOM;
    }
    public void setOutputUOM(String outputUOM) {
        this.outputUOM = outputUOM;
    }
    public void setPhenomenom(String phenomenom) {
        this.phenomenom = phenomenom;
    }
    public ArrayList<Double> getMeasurements() {
        return measurements;
    }
    public void addMeasurement(double measurements) {
        this.measurements.add(new Double(measurements));
    }
    public String getIdentifier() {
        return sensorID;
    }
    public void setIdentifier(String identifier) {
        this.sensorID = identifier;
    }
    public double getLatitude() {
        return latitude;
    }
    public void setCoordinates(String [] coordinates) {
        this.latitude = new Double(coordinates[0]);
        this.longitude = new Double(coordinates[1]);
    }
    public double getLongitude() {
        return longitude;
    }
    public void setMeasurements(ArrayList<Double> measurements) {
        this.measurements = measurements;
    }
}
