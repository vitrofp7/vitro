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
package alter.vitro.vgw.service.query.wrappers;

import alter.vitro.vgw.service.query.xmlmessages.aggrquery.FuncOnSensorType;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * User: antoniou
 */
public class ReqSensorAndFunctions extends FuncOnSensorType {
    public static final int invalidSensModelId = -1;

    /**
     * Creates a new instance of ReqSensorAndFunctions
     */
    public ReqSensorAndFunctions() {
        super();
        setSensorModelid(Integer.toString(ReqSensorAndFunctions.invalidSensModelId));
    }

    /**
     * Creates a new instance of ReqSensorAndFunctions
     *
     * @param smid              Sets the sensor model id for this queries sensor model
     * @param funcOverSModelLst Sets the vector of ids of unique functions to be applied to this sensor model.
     */
    public ReqSensorAndFunctions(int smid, List<Integer> funcOverSModelLst) {
        super();
        super.setSensorModelid(Integer.toString(smid));
        Iterator<Integer> fidIter = funcOverSModelLst.iterator();
        while (fidIter.hasNext())
        {
            super.getFid().add(BigInteger.valueOf(fidIter.next()));
        }
    }
    /**
     * Creates a new instance of ReqSensorAndFunctions
     *
     * @param smid              Sets the sensor model id for this queries sensor model
     * @param funcOverSModelVector Sets the vector of ids of unique functions to be applied to this sensor model.
     */
    public ReqSensorAndFunctions(int smid, Vector<Integer> funcOverSModelVector) {
        super();
        super.setSensorModelid(Integer.toString(smid));
        Iterator<Integer> fidIter = funcOverSModelVector.iterator();
        while (fidIter.hasNext())
        {
            super.getFid().add(BigInteger.valueOf(fidIter.next()));
        }
    }

    /**
     * Creates a new instance of ReqSensorAndFunctions
     *
     * @param fosT  from the XML unmarshaller
     */
    public ReqSensorAndFunctions(FuncOnSensorType fosT) {
        super();
        super.setSensorModelid(fosT.getSensorModelid().trim());
        Iterator<BigInteger> fidIter = fosT.getFid().iterator();
        while (fidIter.hasNext())
        {
            super.getFid().add(fidIter.next());
        }
    }

    /**
     * Retrieves the vector of unique function Ids that are referenced in this sensor model.
     *
     * @return The vector of unique function Ids that are referenced in this sensor model.
     */
    public Vector<Integer> getFunctionsOverSensorModelVec() {
        Vector<Integer> fidsVec = new Vector<Integer>();
        Iterator<BigInteger> fidIter = getFid().iterator();
        while (fidIter.hasNext())
        {
            fidsVec.addElement(fidIter.next().intValue());
        }
        return  fidsVec;
    }

    /**
     * Retrieves the sensor model id of this object.
     *
     * @return the sensor model id of this object.
     */
    public int getSensorModelIdInt() {
        return Integer.parseInt(getSensorModelid().trim());
    }

}
