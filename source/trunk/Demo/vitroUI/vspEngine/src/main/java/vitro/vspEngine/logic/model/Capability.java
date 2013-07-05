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
package vitro.vspEngine.logic.model;

import vitro.vspEngine.logic.exception.VspEngineException;
import vitro.vspEngine.service.engine.UserNode;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class Capability {
    public static final String dcaPrefix = "urn:x-ogc:def:phenomenon:IDAS:1.0:";

    public static String PHENOMENOM_TEMPERATURE = "temperature";
    public static String PHENOMENOM_HUMIDITY = "relativeHumidity";
    public static String PHENOMENOM_LIGHT = "luminousIntensity";
    public static String PHENOMENOM_WIND_SPEED = "windspeed";
    public static String PHENOMENOM_CO = "co";
    public static String PHENOMENOM_CO2 = "CO2Concentration";
    public static String PHENOMENOM_PRESSURE = "pressure";
    public static String PHENOMENOM_BAROMETRIC_PRESSURE = "atmosphericPressure";
    public static String PHENOMENOM_PARKING = "parking";
    public static String PHENOMENOM_NOISE = "noise";
    public static String PHENOMENOM_BATTERY = "battery";
    public static String PHENOMENOM_SWITCH_LIGHT1 = "switchlight1";
    public static String PHENOMENOM_SWITCH_LIGHT2 = "switchlight2";
    public static String PHENOMENOM_SWITCH_LIGHT3 = "switchlight3";
    public static String PHENOMENOM_SWITCH_LIGHT4 = "switchlight4";
    public static String PHENOMENOM_UNKNOWN = "unknown";

    public static Capability RES_TEMPERATURE = new Capability(PHENOMENOM_TEMPERATURE);
    public static Capability RES_HUMIDITY = new Capability(PHENOMENOM_HUMIDITY);
    public static Capability RES_LIGHT = new Capability(PHENOMENOM_LIGHT);
    public static Capability RES_WIND_SPEED = new Capability(PHENOMENOM_WIND_SPEED);
    public static Capability RES_CO = new Capability(PHENOMENOM_CO);
    public static Capability RES_CO2 = new Capability(PHENOMENOM_CO2);
    public static Capability RES_PRESSURE = new Capability(PHENOMENOM_PRESSURE);
    public static Capability RES_BAROMETRIC_PRESSURE = new Capability(PHENOMENOM_BAROMETRIC_PRESSURE);
    public static Capability RES_PARKING = new Capability(PHENOMENOM_PARKING);
    public static Capability RES_NOISE = new Capability(PHENOMENOM_NOISE);
    public static Capability RES_BATTERY = new Capability(PHENOMENOM_BATTERY);
    public static Capability RES_SWITCH_LIGHT1 = new Capability(PHENOMENOM_SWITCH_LIGHT1);
    public static Capability RES_SWITCH_LIGHT2 = new Capability(PHENOMENOM_SWITCH_LIGHT2);
    public static Capability RES_SWITCH_LIGHT3 = new Capability(PHENOMENOM_SWITCH_LIGHT3);
    public static Capability RES_SWITCH_LIGHT4 = new Capability(PHENOMENOM_SWITCH_LIGHT4);
    public static Capability RES_UNKNOWN = new Capability(PHENOMENOM_UNKNOWN);


    public static String UOM_CELSIUS = "celsius";
    public static String UOM_KELVIN = "kelvin";
    public static String UOM_CANDLE = "candela";
    public static String UOM_LUX = "lux";
    public static String UOM_PERCENT = "percent";
    public static String UOM_KMH = "kmH";
    public static String UOM_PARTS_PER_MILLION = "ppm";
    public static String UOM_PARTS_PER_BILLION = "ppb";
    public static String UOM_PASCAL = "pascal";
    public static String UOM_HECTO_PASCAL = "hectoPascal";
    public static String UOM_DIMENSIONLESS = "dimensionless";

    // default mapping of Phenomena to UoMs. This can be overriden by specific sensorModels
    private static Map<String, String> defaultUomMap = new HashMap<String, String>();
    static{
        defaultUomMap.put(Capability.PHENOMENOM_TEMPERATURE, UOM_KELVIN);
        defaultUomMap.put(Capability.PHENOMENOM_LIGHT, UOM_LUX);
        defaultUomMap.put(Capability.PHENOMENOM_HUMIDITY, UOM_PERCENT);
        defaultUomMap.put(Capability.PHENOMENOM_WIND_SPEED, UOM_KMH);
        defaultUomMap.put(Capability.PHENOMENOM_CO, UOM_PARTS_PER_MILLION);
        defaultUomMap.put(Capability.PHENOMENOM_CO2, UOM_PARTS_PER_MILLION);
        defaultUomMap.put(Capability.PHENOMENOM_PRESSURE, UOM_PASCAL);
        defaultUomMap.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, UOM_HECTO_PASCAL);
        defaultUomMap.put(Capability.PHENOMENOM_PARKING, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_NOISE, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_BATTERY, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_SWITCH_LIGHT1, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_SWITCH_LIGHT2, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_SWITCH_LIGHT3, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_SWITCH_LIGHT4, UOM_DIMENSIONLESS);
        defaultUomMap.put(Capability.PHENOMENOM_UNKNOWN, UOM_DIMENSIONLESS);
    }

    private static Map<String, Capability> capabilityMap = new HashMap<String, Capability>();
    static{
        capabilityMap.put(Capability.PHENOMENOM_TEMPERATURE, RES_TEMPERATURE);
        capabilityMap.put(Capability.PHENOMENOM_LIGHT, RES_LIGHT);
        capabilityMap.put(Capability.PHENOMENOM_HUMIDITY, RES_HUMIDITY);
        capabilityMap.put(Capability.PHENOMENOM_WIND_SPEED, RES_WIND_SPEED);
        capabilityMap.put(Capability.PHENOMENOM_CO, RES_CO);
        capabilityMap.put(Capability.PHENOMENOM_CO2, RES_CO2);
        capabilityMap.put(Capability.PHENOMENOM_PRESSURE, RES_PRESSURE);
        capabilityMap.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, RES_BAROMETRIC_PRESSURE);
        capabilityMap.put(Capability.PHENOMENOM_PARKING, RES_PARKING);
        capabilityMap.put(Capability.PHENOMENOM_NOISE, RES_NOISE);
        capabilityMap.put(Capability.PHENOMENOM_BATTERY, RES_BATTERY);
        capabilityMap.put(Capability.PHENOMENOM_SWITCH_LIGHT1, RES_SWITCH_LIGHT1);
        capabilityMap.put(Capability.PHENOMENOM_SWITCH_LIGHT2, RES_SWITCH_LIGHT2);
        capabilityMap.put(Capability.PHENOMENOM_SWITCH_LIGHT3, RES_SWITCH_LIGHT3);
        capabilityMap.put(Capability.PHENOMENOM_SWITCH_LIGHT4, RES_SWITCH_LIGHT4);
        capabilityMap.put(Capability.PHENOMENOM_UNKNOWN, RES_UNKNOWN);

    }

    //new friendly names for capability (ONLY FOR display purposes)
    private static Map<String, String> simpleNameCapabilityToFriendlyNameUIMap = new HashMap<String, String>();
    static{
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_TEMPERATURE, "Temperature");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_LIGHT, "Light sensor");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_HUMIDITY, "Humidity");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_WIND_SPEED, "Wind Speed");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_CO, "CO levels");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_CO2, "CO2 levels");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_PRESSURE, "Pressure");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, "Barometric Pressure");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_PARKING, "Parking Spot");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_NOISE, "Noise levels");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_BATTERY, "Battery levels");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_SWITCH_LIGHT1, "Switch lamp zone 1");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_SWITCH_LIGHT2, "Switch lamp zone 2");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_SWITCH_LIGHT3, "Switch lamp zone 3");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_SWITCH_LIGHT4, "Switch lamp zone 4");
        simpleNameCapabilityToFriendlyNameUIMap.put(Capability.PHENOMENOM_UNKNOWN, "Unknown");

    }


    static HashMap<String, String> simpleNameToSemanticDescriptionMap = new HashMap<String, String>();
    static {
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_TEMPERATURE, "http://dbpedia.org/resource/Temperature");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_LIGHT, "http://dbpedia.org/resource/Luminance");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_HUMIDITY, "http://dbpedia.org/resource/Humidity");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_WIND_SPEED, "http://dbpedia.org/resource/Wind_speed");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_CO, "http://dbpedia.org/resource/Carbon_monoxide");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_CO2, "http://dbpedia.org/resource/Carbon_dioxide");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_PRESSURE, "http://dbpedia.org/resource/Pressure");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, "http://dbpedia.org/resource/Barometric_pressure");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_PARKING, "http://dbpedia.org/resource/Parking_space");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_NOISE, "http://dbpedia.org/resource/Sound_intensity");     // ???
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_BATTERY, "http://dbpedia.org/resource/Battery_(electricity)");
                                                                // ??? http://dbpedia.org/resource/Energy_consumption_measurement ???
                                                                // ??? http://dbpedia.org/resource/Energy_level ???
        /// /simpleNameToSemanticDescriptionMap.put("ir", "");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_SWITCH_LIGHT1,"http://dbpedia.org/resource/LightSwitchZone1");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_SWITCH_LIGHT2, "http://dbpedia.org/resource/LightSwitchZone1");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_SWITCH_LIGHT3, "http://dbpedia.org/resource/LightSwitchZone1");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_SWITCH_LIGHT4, "http://dbpedia.org/resource/LightSwitchZone1");
        simpleNameToSemanticDescriptionMap.put(Capability.PHENOMENOM_UNKNOWN, "http://dbpedia.org/resource/unknown");
    }

    static HashMap<String, String> simpleNameToIcon = new HashMap<String, String>();
    static {
        simpleNameToIcon.put(Capability.PHENOMENOM_TEMPERATURE, "thermometer-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_LIGHT, "sunny-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_HUMIDITY, "water-droplet-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_WIND_SPEED, "question-mark-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_CO, "co-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_CO2, "co-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_PRESSURE, "question-mark-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, "baropresssm32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_PARKING, "parking-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_NOISE, "noise-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_BATTERY, "battery-icon32.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT1,"lighbulboffsm.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT2, "lighbulboffsm.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT3, "lighbulboffsm.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT4, "lighbulboffsm.png");
        simpleNameToIcon.put(Capability.PHENOMENOM_UNKNOWN, "question-mark-icon32.png");
    }

    static HashMap<String, String> simpleNameToSelectedIcon = new HashMap<String, String>();
    static {
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_TEMPERATURE, "thermometerSel-icon32.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_LIGHT, "sunnySel-icon32.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_HUMIDITY, "water-dropletSel-icon32.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_WIND_SPEED, "question-markSel-icon32.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_CO, "co-icon32Selected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_CO2, "co-icon32Selected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_PRESSURE, "question-markSel-icon32.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, "baropresssm32Selected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_PARKING, "parking-icon32Selected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_NOISE, "noise-icon32Selected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_BATTERY, "battery-icon32Selected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT1,"lighbulboffsmSelected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT2, "lighbulboffsmSelected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT3, "lighbulboffsmSelected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT4, "lighbulboffsmSelected.png");
        simpleNameToSelectedIcon.put(Capability.PHENOMENOM_UNKNOWN, "question-markSel-icon32.png");
    }

    static HashMap<String, String> simpleNameToDisabledIcon = new HashMap<String, String>();
    static {
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_TEMPERATURE, "thermometerDsbld-icon32.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_LIGHT, "sunnyDsbld-icon32.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_HUMIDITY, "water-dropletDsbld-icon32.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_WIND_SPEED, "question-markDsbld-icon32.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_CO, "co-icon32Disabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_CO2, "co-icon32Disabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_PRESSURE, "question-markDsbld-icon32.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_BAROMETRIC_PRESSURE, "baropresssm32Disabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_PARKING, "parking-icon32Disabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_NOISE, "noise-icon32Disabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_BATTERY, "battery-icon32Disabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT1,"lighbulboffsmDisabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT2, "lighbulboffsmDisabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT3, "lighbulboffsmDisabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_SWITCH_LIGHT4, "lighbulboffsmDisabled.png");
        simpleNameToDisabledIcon.put(Capability.PHENOMENOM_UNKNOWN, "question-markDsbld-icon32.png");
    }

    String name;

    public static Capability getCapability(String capabilityName) throws VspEngineException {
        Capability result = null;
        if(capabilityMap.containsKey(capabilityName)) {
            result = capabilityMap.get(capabilityName);
        }
        if(result == null){
            throw new VspEngineException("No capability mapping for " + capabilityName);
        }
        return result;
    }

    public static List<String> getSupportedCapabilitiesIncludingUnknown() {
        List resultList = new ArrayList();
        for(String keyOfMap : capabilityMap.keySet()) {
            resultList.add(keyOfMap);
        }
        return resultList;
    }

    protected Capability(String name) {
        super();
        this.name = name;
    }

    public String getDefaultUnitsOfMeasure() throws VspEngineException {
        String uomToReturn = null;
        if(defaultUomMap.containsKey(name))
        {
            uomToReturn = defaultUomMap.get(name);
        }
        if(uomToReturn == null)
        {
            throw new VspEngineException("No default uom mapping for " + name);
        }
        return uomToReturn;

    }

    public static String getDefaultIcon(String pNname)  {
        String iconToReturn = null;
        if(simpleNameToIcon.containsKey(pNname))
        {
            iconToReturn = simpleNameToIcon.get(pNname);
        }
        if(iconToReturn == null)
        {
            iconToReturn = simpleNameToIcon.get(Capability.PHENOMENOM_UNKNOWN);
        }
        return iconToReturn;
    }

    public static Set<String> validSimpleCapNames() {
        return  simpleNameCapabilityToFriendlyNameUIMap.keySet();
    }


    public static String getFriendlyUIName(String simpleCapName) {
        String friendlyNameToRet = null;
        if(simpleNameToIcon.containsKey(simpleCapName))
        {
            friendlyNameToRet = simpleNameCapabilityToFriendlyNameUIMap.get(simpleCapName);
        }
        if(friendlyNameToRet == null)
        {
            friendlyNameToRet = simpleNameCapabilityToFriendlyNameUIMap.get(Capability.PHENOMENOM_UNKNOWN);
        }
        return friendlyNameToRet;

    }

    public static String getDefaultSelectedIcon(String pNname) {
        String iconToReturn = null;
        if(simpleNameToSelectedIcon.containsKey(pNname))
        {
            iconToReturn = simpleNameToSelectedIcon.get(pNname);
        }
        if(iconToReturn == null)
        {
            iconToReturn = simpleNameToSelectedIcon.get(Capability.PHENOMENOM_UNKNOWN);
        }
        return iconToReturn;
    }

    public static String getDefaultDisabledIcon(String pNname) {
        String iconToReturn = null;
        if(simpleNameToDisabledIcon.containsKey(pNname))
        {
            iconToReturn = simpleNameToDisabledIcon.get(pNname);
        }
        if(iconToReturn == null)
        {
            iconToReturn = simpleNameToDisabledIcon.get(Capability.PHENOMENOM_UNKNOWN);
        }
        return iconToReturn;
    }

    public String getName(){
        return name;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Capability other = (Capability) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (name.compareToIgnoreCase(other.name)!=0)
            return false;
        return true;
    }

    // TODO: mainly for demo purposes because it may refer to capability URNs not officially supported in DCA
    public static boolean isActuatingCapability(String capUrn)
    {
        boolean retVal = false;
        Matcher m = Pattern.compile("\\d+$").matcher(capUrn);
        //int numberInTheEnd = -1;  // TODO: for now, we discover an actuator by searching for a trailing number in their name
        if(m.find() && !capUrn.matches(Pattern.quote(Capability.PHENOMENOM_CO2)+"$") && !capUrn.matches(Pattern.quote("ch4")+"$")  ) {
            // except from those ending to PHENOMENOM_CO2 and "ch4" (methane gas)
            retVal= true;
        }
        return retVal;
    }

    /**
     * Returns a sensor model (it's not a unique value) that belongs to this capability!
     * @param fullCapName
     * @return
     */
    public static String getASensorModelFromName(String fullCapName) {
        UserNode ssUN = UserNode.getUserNode();
        boolean aSensorModelFound = false;
        String aSensorModelStr = null;
        //String capName = sensorModelId;
        if(ssUN!=null)
        {
            HashMap<String, Vector< SensorModel>> tmpHM = ssUN.getCapabilitiesTable();
            if(tmpHM.containsKey(fullCapName))
            {
                Vector<SensorModel> tmpSMVec = tmpHM.get(fullCapName);
                if(tmpSMVec!= null && tmpSMVec.size() > 0)
                {
                    //return the model of the first entry
                    aSensorModelStr = tmpSMVec.elementAt(0).getSmID();
                }
            }
        }
        return aSensorModelStr;
    }


    // TODO: mainly for demo purposes
    public static String getNameFromSensorModel(String sensorModelId){
        String capName = sensorModelId;

        UserNode ssUN = UserNode.getUserNode();
        boolean capNameFound = false;
        //String capName = sensorModelId;
        if(ssUN!=null)
        {
            HashMap<String, Vector< SensorModel>> tmpHM = ssUN.getCapabilitiesTable();
            Set<String> keysCaps = tmpHM.keySet();
            Iterator<String> onCapsIt = keysCaps.iterator();
            while(onCapsIt.hasNext() && !capNameFound)
            {
                String tmpCapName = onCapsIt.next();
                Vector<SensorModel> tmpSMVec = tmpHM.get(tmpCapName);
                if(tmpSMVec!= null && tmpSMVec.size() > 0)
                {
                    for (int ki=0 ; ki<tmpSMVec.size(); ki++)
                    {
                        if(tmpSMVec.elementAt(ki).getSmID().equalsIgnoreCase(sensorModelId))
                        {
                            capNameFound= true;
                            //capName = tmpCapName;
                            // remove prefix
                            capName = tmpCapName.replaceAll(Pattern.quote(Capability.dcaPrefix), "");
                            break;
                        }
                    }
                }
            }
        }
        return capName;
    }
}
