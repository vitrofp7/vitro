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

package vitro.virtualsensor;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import vitro.dcaintercom.communication.common.Config;
import vitro.virtualsensor.communication.SensorConnector;

/**
 * 
 * @author David Ferrer Figueroa
 * 
 */

public class VirtualSensor implements Observer {

    //TODO establecer timeout
    private static final int timeout = 100;
    private String operation;
    private SensorConnector gw;
    private ArrayList<SensorInformation> inputSensors; //Separados por UOM
    private SensorInformation sensorInfo;

    private SensorInformation lastMeasurement;

    private ProcessorModule processor;
    boolean newData = false;
    private Timer timer;

    public VirtualSensor(){
        operation = "";
        gw = new SensorConnector(Config.getConfig().getNotificationsUrl());
        inputSensors = new ArrayList<SensorInformation>();
        sensorInfo = new SensorInformation();
        lastMeasurement = new SensorInformation();
        processor = new ProcessorModule();
        timer = new Timer();
    }

    public VirtualSensor(String sensorName, String gwName, ArrayList<String> inputGateways) {
        sensorInfo = new SensorInformation(sensorName);
        this.gw = new SensorConnector(Config.getConfig().getNotificationsUrl());
        sensorInfo.setId(sensorName+"");
        sensorInfo.setGateway(gwName);
        this.operation = "";
        for (int i = 0; i< inputGateways.size(); i++){
            this.inputSensors.add(generateSensorInformation(inputGateways.get(i)));
        }
        this.processor = new ProcessorModule();
    }

    public VirtualSensor(String sensorName, String gw) {
        this(sensorName, gw, new ArrayList<String>());
    }

    public VirtualSensor(ArrayList<SensorInformation> sI, String sensorName, String gwName) {
        sensorInfo = new SensorInformation();
        this.gw = new SensorConnector(Config.getConfig().getNotificationsUrl());
        inputSensors = sI;
        sensorInfo.setId(sensorName+"");
        sensorInfo.setGateway(gwName);
        this.operation = "";
    }

    public SensorInformation generateSensorInformation(String inputGateway){
        SensorInformation s = new SensorInformation(inputGateway);
        return s;
    }

    public ArrayList<SensorInformation> getInputSensors() {
        return inputSensors;
    }

    public void setInputSensors(ArrayList<SensorInformation> inputSensors) {
        this.inputSensors = inputSensors;
    }

    public String getGw() {
        return sensorInfo.getGateway();
    }

    public ArrayList<String> getInputUOM() {
        return sensorInfo.getInputUOM();
    }

    public void setInputUOM(ArrayList<String> inputUOM) {
        this.sensorInfo.setInputUOM(inputUOM);
    }

    public SensorInformation getSensorInformation() {
        return sensorInfo;
    }

    public ArrayList<Double> getOutputMeasurement(){
        return this.sensorInfo.getMeasurements();
    }

    public int addInputSensor(String sensorName, String gwName, String phenomenom, String uom) {
        int result = findInputSensor(sensorName, gwName, phenomenom, uom);
        if(result == -1) {
            SensorInformation s = new SensorInformation();
            s.setId(sensorName);
            s.setGateway(gwName);
            s.setOutputUOM(uom);
            s.setPhenomenom(phenomenom);
            inputSensors.add(s);
            result = inputSensors.size()-1;
        }			
        return result;
    }

    public int addInputSensor(SensorInformation s) {
        if(findInputSensor(s)==-1) {
            inputSensors.add(s);
        }
        return addInputSensor(s.getId(),s.getGateway(),s.getPhenomenom(),s.getOutputUOM());
    }

    public int findInputSensor(String sensorName, String gwName, String phenomenom, String uom) {
        int result = -1;
        for (int i = 0; i<this.inputSensors.size(); i++) {
            if (this.inputSensors.get(i).getId().equals(sensorName) && this.inputSensors.get(i).getOutputUOM().equals(uom)  && this.inputSensors.get(i).getPhenomenom().equals(phenomenom)){
                result = i;
            }
        }
        return result;
    }
//TODO Comprobar que filtro siempre por sensores o por concentradores!! vaya batiburrillo
    public int findInputSensor(SensorInformation s) {
        int result = -1;
        int i = 0;
        for (SensorInformation si : inputSensors){
            if (si.getOutputUOM().equals(s.getOutputUOM())  && si.getPhenomenom().equals(s.getPhenomenom())){
                result = i;
            }
            i++;
        }
        return result;
    }

    public void changeOperation(String newOperation) {
        this.operation = newOperation;
    }

    /*
     * A sensor cannot be deleted from DCA. Therefore, we leave this method like this, expecting that maybe, someday, it will be possible	
     */
    public boolean removeVS() {
        return true;
    }

    /** 
     * This method is called whenever the observer object is changed
     */
    @Override
    public void update (Observable o, Object msg) {
        String uom = ((MsgBox)o).getUom();
        SensorInformation newData = (SensorInformation) msg;
        if((newData.getId()).equals(this.sensorInfo.getId()) && (newData.getGateway()).equals(this.sensorInfo.getGateway())){
            System.out.println("Ignoring my own notification");
        }
        else {
            int index = sensorInfo.getInputUOM().indexOf(uom);
           //Esta línea no pertenece al código original
            // inputSensors.get(index).addMeasurement(newData.getMeasurements().get(0));
            //
            //if(!this.inputSensors.contains(newData)) {
              //  System.out.println("Error filtering UOM");
           // }
           // else {
                this.lastMeasurement = newData;
                this.newData = true;
                evaluate();
            //}
        }
    }

    public void startSensor() {
        System.out.println("Sensor started");
       // runTimer();
    }

    public void runTimer(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run(){
                evaluate();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, timeout*1000);
    }

    public void evaluate() {
        int index;
        if(this.newData){ //If the sensor receives new data
            if((index = findInputSensor(lastMeasurement))==-1){        //If the message is not stored in the inputSensors list, it is discarded
                System.out.println("Message with UOM " + this.lastMeasurement.getOutputUOM() + " filtered");
            }
            else{
                try {
                    for(Double d : lastMeasurement.getMeasurements()){
                        inputSensors.get(index).addMeasurement(d.doubleValue());
                    }
                    Double result = evaluateExpression();
                    sensorInfo.addMeasurement(result.doubleValue());
                    gw.sendObservationMessage(this);
                } catch (Exception e) {
                    System.out.println("Error parsing the data");
                }
            }
        }
        this.newData = false;
    }

    public int findIndex(String uom) {
        for(int i = 0; i<inputSensors.size();i++){
            if(uom.equals(inputSensors.get(i).getOutputUOM())){
                return i;
            }
        }
        return -1;
    }

    public Double evaluateExpression() {
        try{
            String expression = "" + operation;
            if(expression != null && !expression.isEmpty()){
                processor = new ProcessorModule();
                String parsedExpression = processor.parseExpression(expression, inputSensors);
                return new Double(processor.processExpression(parsedExpression));
                //return Double.valueOf(processor.processExpression(parsedExpression));
            }
            else{
                return new Double(-1);
            }            
        }catch(Exception e){
            System.out.println("Error processing the expression");
            return null;
        }        
    }

    public String getOutputPhenomenom() {
        return this.sensorInfo.getPhenomenom();
    }

    public ArrayList<String> getInputPhenomena() {
        ArrayList<String> inputPhenomena = new ArrayList<String>();
        for(SensorInformation s : inputSensors) {
            inputPhenomena.add(s.getPhenomenom());
        }
        return inputPhenomena;
    }
}