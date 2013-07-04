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
package vitro.vspEngine.logic.model;

import vitro.vspEngine.logic.exception.VspEngineException;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class SensorModel extends Capability {

    public static final String invalidId = "unknown id";
    public static final String invalidType = "unknown";
    public static final String commonType = "common"; // to use when we don't care about differentiating sensormodels for capabilities
    public static final String defaultDataType = "String";
    public static final String numericDataType = "double";
    public static final String stringDataType = "String";
    public static final String binaryDataType = "binary";
    public static final String defaultAccuracy = "0.01";
    public static final String defaultUnits = Capability.UOM_DIMENSIONLESS;
    private static final String[] validDataTypes = {SensorModel.stringDataType, SensorModel.binaryDataType, SensorModel.numericDataType};

    // uniqueness is guaranteed if the id has 1-1 correspondence with the sensor's model name (or S/N) .
    private String smID;        // The id of a sensor model must be unique in a testbed ( gateway skope) but not globally   (todo: do we even need this restriction anymore?)
    private String gatewayId; // Putting the gateway id here, we essentially make the sensor model id unique.
    private String dataType;    // the type of data that this sensor provides (e.g. Integer, Double,...)
    private String accuracy;    // the expected error in the readings of the sensor
    private String units;       // the units for the reading.


    public SensorModel(String theCapabilityName, String theGwId)
    {
        super(theCapabilityName);
        if (theGwId != null && !theGwId.trim().isEmpty())
            this.gatewayId = theGwId;
        else
            this.gatewayId = Gateway.invalidGwID;

        this.smID = SensorModel.commonType;
        setDataType(defaultDataType);
        this.accuracy = SensorModel.defaultAccuracy;
        try {
            this.units = getDefaultUnitsOfMeasure();
        }
        catch(VspEngineException ex)
        {
            this.units = SensorModel.defaultUnits;
        }
    }

    /**
     * Creates a new instance of CSensorModel
     */
    public SensorModel(String theCapabilityName, String theGwId, String theSmId, String theDataType, String theAccuracy, String theUnits) {

        super(theCapabilityName);
        if (theGwId != null && !theGwId.trim().isEmpty())
            this.gatewayId = theGwId;
        else
            this.gatewayId = Gateway.invalidGwID;
        this.smID = theSmId;

        if (theSmId != null && !theSmId.trim().isEmpty())
            this.smID = theSmId;
        else
            this.smID = SensorModel.invalidType;

        setDataType(theDataType);

        if (theAccuracy != null)
            this.accuracy = theAccuracy;
        else
            this.accuracy = SensorModel.defaultAccuracy;

        if (theUnits != null)
            this.units = theUnits;
        else
            this.units = SensorModel.defaultUnits;

    }


    /**
     * Checks if a given Datatype is supported
     * @param theDataType The datatype of a sensor model currently (numeric, binary or string)
     * @return true if it is one of the supported types (currently: double, String and binary) or false otherwise
     */
    boolean isValidDataType(String theDataType)
    {
        for(int i = 0 ; i < SensorModel.validDataTypes.length; i++)
        {
            if(theDataType.compareToIgnoreCase(SensorModel.validDataTypes[i]) == 0)
                return true;
        }
        return false;
    }

    // getters
    public String getAccuracy() {
        return accuracy;
    }

    public String getDataType() {
        return dataType;
    }

    /**
     *
     * @return the name of the generic capability of this sensor model
     */
    public String getName() {
        return name;
    }

    public String getSmID() {
        return smID;
    }

    public String getUnits() {
        return units;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    //setters
    public void setDataType(String theDataType) {
        if (theDataType != null && isValidDataType(theDataType))
            this.dataType = theDataType;
        else
            this.dataType = SensorModel.defaultDataType;
    }

    @Override
    public int hashCode()  {
        final int prime = 31;
        int result = 1;
        StringBuilder strBld = new StringBuilder();
        strBld.append(smID);
        result = prime * result + ((strBld.toString() == null || strBld.toString().trim().isEmpty() ) ? 0 : strBld.toString().hashCode());
        return result;
    }

    // match on id and gatewayId merged!
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SensorModel other = (SensorModel) obj;
        return (getGatewayId().compareToIgnoreCase(other.getGatewayId()) ==0  && getSmID().compareToIgnoreCase(other.getSmID()) == 0 );
    }


    public static boolean vectorContainsSensorModel(Vector<SensorModel> currVec, String sensorGW, String sensorID )
    {
        boolean retVal = false;
        if(!currVec.isEmpty())
        {
            for(SensorModel tmpSens:currVec)
            {
                if(tmpSens.getGatewayId().compareToIgnoreCase(sensorGW) == 0 && tmpSens.getSmID().compareToIgnoreCase(sensorID) == 0)       //TODO: It is expected that the first clause is always true, since the node belongs to one gateway.
                {
                    retVal= true;
                    break;
                }
            }
        }
        return retVal;
    }

}
