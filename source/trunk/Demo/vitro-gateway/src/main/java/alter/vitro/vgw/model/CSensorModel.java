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
package alter.vitro.vgw.model;

/**
 * Author: antoniou
 */

public class CSensorModel {
    public static final int invalidId = -1;
    public static final String invalidType = "unknown";
    public static final String defaultDataType = "String";
    public static final String numericDataType = "double";
    public static final String stringDataType = "String";
    public static final String binaryDataType = "binary";
    public static final String defaultAccuracy = "0.01";
    public static final String defaultUnits = "unknown";
    private static final String[] validDataTypes = {CSensorModel.stringDataType, CSensorModel.binaryDataType, CSensorModel.numericDataType};

  // uniqueness is guaranteed if the id has 1-1 correspondence with the sensor's model name (or S/N) .
    private int smid;           // The id of a sensor model must be unique in the gateway but not globally // TODO: make it a string
    private String gatewayId;   // Putting the gateway id here, we essentially make the sensor model id unique. // TODO:probably redundant

    private String name;        // model name
    private String dataType;    // the type of data that this sensor provides (e.g. Integer, Double,...)
    private String accuracy;    // the expected error in the readings of the sensor
    private String units;       // the units for the reading.


    /**
     * Creates a new instance of CSensorModel
     */
    public CSensorModel(String gwId, int thesmid, String theName, String theDataType, String theAccurac, String theUnits) {

        if (gwId != null && !gwId.equals(""))
            this.gatewayId = gwId;
        else
            this.gatewayId = CGateway.invalidGwID;

        this.smid = thesmid;

        if (theName != null)
            this.name = theName;
        else
            this.name = CSensorModel.invalidType;

        setDataType(theDataType);

        if (theAccurac != null)
            this.accuracy = theAccurac;
        else
            this.accuracy = CSensorModel.defaultAccuracy;

        if (theUnits != null)
            this.units = theUnits;
        else
            this.units = CSensorModel.defaultUnits;

    }


    /**
     * Checks if a given Datatype is supported
     * @param theDataType The datatype of a sensor model currently (numeric, binary or string)
     * @return true if it is one of the supported types (currently: double, String and binary) or false otherwise
     */
    boolean isValidDataType(String theDataType)
    {
        for(int i = 0 ; i < CSensorModel.validDataTypes.length; i++)
        {
            if(theDataType.toLowerCase().equals(CSensorModel.validDataTypes[i].toLowerCase()))
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

    public String getName() {
        return name;
    }

    public int getSmid() {
        return smid;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public String getUnits() {
        return units;
    }

    //setters
    public void setDataType(String theDataType) {
        if (theDataType != null && isValidDataType(theDataType))
            this.dataType = theDataType;
        else
            this.dataType = CSensorModel.defaultDataType;
    }

    @Override
    public int hashCode()  {
        final int prime = 31;
        int result = 1;
        StringBuilder strBld = new StringBuilder();
        strBld.append(gatewayId);
        strBld.append("::");
        strBld.append(smid);
        result = prime * result + ((strBld.toString() == null || strBld.toString().equals("") ) ? 0 : strBld.toString().hashCode());
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
        CSensorModel other = (CSensorModel) obj;
        StringBuilder strBldThisId = new StringBuilder();
        strBldThisId.append(gatewayId);
        strBldThisId.append("::");
        strBldThisId.append(smid);

        StringBuilder strBldOtherId = new StringBuilder();
        strBldOtherId.append(other.getGatewayId());
        strBldOtherId.append("::");
        strBldOtherId.append(other.getSmid());
        return strBldThisId.toString().equals(strBldOtherId.toString());
    }

}
