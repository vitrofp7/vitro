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

import alter.vitro.vgw.service.query.xmlmessages.aggrquery.FuncOnSensorListType;
import alter.vitro.vgw.service.query.xmlmessages.aggrquery.FuncOnSensorType;
import alter.vitro.vgw.service.query.xmlmessages.aggrquery.MoteType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class QueriedMoteAndSensors extends MoteType{
//    private String moteId;
//    private Vector<ReqSensorAndFunctions> queriedSensorIdsAndFuncVec;


    /**
     * Creates a new instance of QueriedMotesAndSensors.
     * Default constructor.
     */
    public QueriedMoteAndSensors() {
        super();
        setMoteid("unknown");

        FuncOnSensorListType fosListType = new FuncOnSensorListType();
        setFuncOnSensorList(fosListType);
//        queriedSensorIdsAndFuncVec = new Vector<ReqSensorAndFunctions>();
    }

    /**
     * Creates a new instance of QueriedMotesAndSensors.
     *
     * @param mId Sets the mote id for this queried mote
     * @param queriedSensorIdsAndFunctionsVec
     *            Sets the Vector of mappings of Sensor models to Function ids.
     */
    public QueriedMoteAndSensors(String mId, Vector<ReqSensorAndFunctions> queriedSensorIdsAndFunctionsVec) {
        super();
        setMoteid(mId);
        setQueriedSensorIdsAndFuncVec(queriedSensorIdsAndFunctionsVec);
    }


    /**
     * Creates a new instance of QueriedMotesAndSensors.
     *
     * @param pMt The moteType from an unmarshalled message
     *            Sets the Vector of mappings of Sensor models to Function ids.
     */
    public QueriedMoteAndSensors(MoteType pMt) {
        super();
        setMoteid(pMt.getMoteid().trim());
        // cleanup id fields!
        if (pMt.getFuncOnSensorList()!= null && pMt.getFuncOnSensorList().getFuncOnSensor() !=null)
        {
            Iterator<FuncOnSensorType> fosIter = pMt.getFuncOnSensorList().getFuncOnSensor().iterator();
            while(fosIter.hasNext())
            {
                FuncOnSensorType fosT = fosIter.next();
                String trimmedVal = fosT.getSensorModelid().trim();
                fosT.setSensorModelid(trimmedVal);
            }
        }
        setFuncOnSensorList(pMt.getFuncOnSensorList());
    }


    /**
     * Returns the vector of ReqSensorAndFunctions objects associated with this mote
     *
     * @return the vector of ReqSensorAndFunctions objects associated with this mote.
     */
    public List<ReqSensorAndFunctions> getQueriedSensorIdsAndFuncVec() {
        List<ReqSensorAndFunctions> resLst = new ArrayList<ReqSensorAndFunctions>();
        if( getFuncOnSensorList().getFuncOnSensor() != null)
        {
            Iterator<FuncOnSensorType> fosIter = getFuncOnSensorList().getFuncOnSensor().iterator();
            while(fosIter.hasNext())
            {
                FuncOnSensorType fosT = fosIter.next();
                resLst.add(new ReqSensorAndFunctions(fosT)) ;
            }
        }
        return resLst;
    }


    /**
     * Sets the vector that maps Sensor Models to selected unique function ids.
     *
     * @param queriedSensorIdsAndFuncVec a Vector with sensor Models correlated to unique function ids.
     */
    public void setQueriedSensorIdsAndFuncVec(Vector<ReqSensorAndFunctions> queriedSensorIdsAndFuncVec) {
        Iterator<ReqSensorAndFunctions> listIter = queriedSensorIdsAndFuncVec.iterator();
        FuncOnSensorListType myFuncOnSensorList = new FuncOnSensorListType() ;
        while (listIter.hasNext())
        {
            myFuncOnSensorList.getFuncOnSensor().add(listIter.next());

        }
        setFuncOnSensorList(myFuncOnSensorList);
    }

}
