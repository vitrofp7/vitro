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

import groovy.lang.GroovyShell;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.MatchResult;

/**
 * This class contains all the power of the Composition of Services, allowing the user to mix different inputs in order to return different outputs
 * @author David Ferrer Figueroa
 *
 * The format for the conditional expression will be the same as used in a if message
 * The instructions allowed are max, min, avg, last
 */
public class ProcessorModule {
    
    public ProcessorModule(){
    }
	/**
	 * This method evaluates the expression and obtains the result for the sensor
	 */
    public String processExpression(String expression){
            GroovyShell shell = new GroovyShell();
            return shell.evaluate(expression).toString(); 
    }

    public boolean trigger() {
            return true;		
    }

    public String parseExpression(String expression, ArrayList<SensorInformation> inputSensors){
        try {
            Scanner sc;
            String x = "" + expression; // get a copy
            String pattern;
            MatchResult result;

            // replace averages
            sc = new Scanner(x);
            pattern = "avg\\((\\w+)\\)";
            while (sc.findWithinHorizon(pattern,0) != null) {
                    result = sc.match();
                    for (int i=1; i<=result.groupCount(); i++) {
                            String phenom = "urn:x-ogc:def:phenomenon:IDAS:1.0:" + result.group(i); 
                            x = x.replaceAll(pattern, avg(phenom, inputSensors));
                    }
            }
            sc.close();

            // replace maximums
            sc = new Scanner(x);
            pattern = "max\\((\\w+)\\)";
            while (sc.findWithinHorizon(pattern,0) != null) {
                    result = sc.match();
                    for (int i=1; i<=result.groupCount(); i++) {
                            x = x.replaceAll(pattern, max(result.group(i), inputSensors));
                    }
            }
            sc.close();

            // replace minimums
            sc = new Scanner(x);
            pattern = "min\\((\\w+)\\)";
            while (sc.findWithinHorizon(pattern,0) != null) {
                    result = sc.match();
                    for (int i=1; i<=result.groupCount(); i++) {
                            x = x.replaceAll(pattern, min(result.group(i), inputSensors));
                    }
            }
            sc.close();

            // replace lastMeasurement
            sc = new Scanner(x);
            pattern = "last\\((\\w+)\\)";
            while (sc.findWithinHorizon(pattern,0) != null) {
                    result = sc.match();
                    for (int i=1; i<=result.groupCount(); i++) {
                            x = x.replaceAll(pattern, last(result.group(i), inputSensors));
                    }
            }
            sc.close();
            return x;
        } catch (Exception e) {
                return "";
        }
    }


/*	private String avg (String phenomenon, ArrayList<ArrayList<Double>> measurements, ArrayList<String> inputUnits) {
            int index = inputUnits.indexOf(phenomenon);
            return "" + avg(measurements.get(index))+ "";
    }*/

    private String avg(String phenomenon, ArrayList<SensorInformation> inputSensors) {
        ArrayList<Double> values = new ArrayList<Double>();
        for(SensorInformation s : inputSensors) {
            if(s.getPhenomenom().equals(phenomenon)) {
                values.addAll(s.getMeasurements());
            }
        }
        return "" + avg(values) + "";
    }

    private String max(String phenomenon, ArrayList<SensorInformation> inputSensors) {
        ArrayList<Double> values = new ArrayList<Double>();
        for(SensorInformation s : inputSensors) {
            if(s.getPhenomenom().equals(phenomenon)) {
                values.addAll(s.getMeasurements());
            }
        }
        return "" + max(values)+ "";
    }


    private String min (String phenomenon, ArrayList<SensorInformation> inputSensors){
        ArrayList<Double> values = new ArrayList<Double>();
        for(SensorInformation s : inputSensors){
            if(s.getPhenomenom().equals(phenomenon)){
                values.addAll(s.getMeasurements());
            }
        }           
        return "" + min(values)+ "";
    }

    //TODO no funciona correctamente. Valido solo para un sensor por UOM
    private String last (String phenomenon, ArrayList<SensorInformation> inputSensors) {
        ArrayList<Double> values = new ArrayList<Double>();
        for(SensorInformation s : inputSensors){
            if(s.getPhenomenom().equals(phenomenon)){
                values.addAll(s.getMeasurements());
            }
        }
        return "" + last(values)+ "";
    }

    private double avg (ArrayList<Double> list) {
        // Parse values to double
        double [] values = parseValues(list);        
        if (values == null || values.length == 0) {
            return 0.0;
        }
        else {
            double res = 0.0;
            for (double val : values) {
                res += val;
            }
            try {
                res = res / (new Double(values.length));
            } catch (Exception e2) {
                return 0.0;
            }
            return res;
        }
    }

    private double max (ArrayList<Double> list) {
        // Parse values to double
        double [] values = parseValues(list);        
        if (values == null || values.length == 0){
            return 0.0;
        }
        else {
            double res = Double.MIN_VALUE;
            for (double val : values) {
                    if (val > res) {
                    res = val;
                }
            }
            return res;
        }
    }

    private double min (ArrayList<Double> list) {
        // Parse values to double
        double [] values = parseValues(list);        
        if (values == null || values.length == 0){
            return 0.0;
        }
        else {
            double res = Double.MAX_VALUE;
            for (double val : values) {
                    if (val < res) {
                    res = val;
                }
            }
            return res;
        }
    }

    private double last (ArrayList<Double> list) {
        double [] values = parseValues(list);
        if (values == null || values.length == 0){
            return 0.0;
        }
        else {
            return values[(values.length) -1];
        }
    }

    private double [] parseValues (ArrayList<Double> list) {
        boolean error = false;
        int i = 0;
        double [] values = new double[list.size()];
        while (!error && i < list.size()) {
            Double data = list.get(i);
            try {
                values[i++] = data.doubleValue();
            } catch (Exception e) {
                error = true;
            }
        }
        if (error){
            return null;
        }
        else{
            return values;
        }
    }

    public static void testEmbeddedGroovy () {
        GroovyShell shell = new GroovyShell();
        String cmd = "if (1 > 2) return \"ONE\"; else return \"TWO\"";
        String res = shell.evaluate(cmd).toString();
        System.out.println("Res: " + res);
    }
}

/*private String max (String phenomenon, ArrayList<ArrayList<Double>> measurements, ArrayList<String> inputUnits) {
int index = inputUnits.indexOf(phenomenon);
return "" + max(measurements.get(index)) + "";
}

private String min (String phenomenon, ArrayList<ArrayList<Double>> measurements, ArrayList<String> inputUnits) {
int index = inputUnits.indexOf(phenomenon);
return "" + min(measurements.get(index)) + "";
}
private String last (String phenomenon, ArrayList<ArrayList<Double>> measurements, ArrayList<String> inputUnits) {
    int index = inputUnits.indexOf(phenomenon);
    return "" + last(measurements.get(index)) + "";
}*/
