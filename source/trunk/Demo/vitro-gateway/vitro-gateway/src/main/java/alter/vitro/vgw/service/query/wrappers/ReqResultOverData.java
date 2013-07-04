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
package alter.vitro.vgw.service.query.wrappers;

import alter.vitro.vgw.service.query.xmlmessages.response.OutType;
import alter.vitro.vgw.service.query.xmlmessages.response.RespFunctionType;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * User: antoniou
 */
public class ReqResultOverData extends RespFunctionType {
    //int fid;
    //Vector<ResultAggrStruct> allResultsforFunct;

    public static int modeFillWithTimeouts = 1;

    public static final String specialValuePending = "Pending response";
    public static final String specialValueNotSupported = "Unsupported";
    public static final String specialValueBinary = "Binary value";
    public static final String specialValueNoReading = "No reading";
    public static final String specialValueTimedOut = "Timed out";

    /**
     * Creates a new instance of RespResultOverData with timed-out values for all defined sensors.
     *
     * @param givenfid the unique function id for this function.
     * @param motesSensorsAndFunctionsForQueryVec
     *                 the Vector that defines the queried sensors for each mote and the pertinent functions for each sensor
     * @param mode     For this call it is set to  ReqResultOverData.modeFillWithTimeouts . This parameter is used only to differentiate this constructor's definition from the other one (there was some ambiguity with the vector arguments)
     */
    public ReqResultOverData(int givenfid, Vector<QueriedMoteAndSensors> motesSensorsAndFunctionsForQueryVec, int mode) {

        super();
        super.setFid(Integer.toString(givenfid));
        List<OutType> outTypeList = super.getOut();

        for (int i = 0; i < motesSensorsAndFunctionsForQueryVec.size(); i++) {
            QueriedMoteAndSensors tmpMoteAndItsSensors = motesSensorsAndFunctionsForQueryVec.elementAt(i);
            String moteId = tmpMoteAndItsSensors.getMoteid();
            List<ReqSensorAndFunctions> tmpSensorsAndItsFunctionsVec = tmpMoteAndItsSensors.getQueriedSensorIdsAndFuncVec();
            for (int j = 0; j < tmpSensorsAndItsFunctionsVec.size(); j++) {
                ReqSensorAndFunctions tmpSensorAndItsFunctions = tmpSensorsAndItsFunctionsVec.get(j);
                int sid = tmpSensorAndItsFunctions.getSensorModelIdInt();
                if (tmpSensorAndItsFunctions.getFunctionsOverSensorModelVec().contains(Integer.valueOf(givenfid))) {
                    outTypeList.add(new ResultAggrStruct(moteId, sid, ReqResultOverData.specialValueTimedOut, 0, null));
                }
            }
        }
    }

    /**
     * Creates a new instance of RespResultOverData
     *
     * @param givenfid           the unique id for the function for which the results are contained in this object
     * @param allResultsforFunct a Vector with the results per mid/sid key.
     */
    public ReqResultOverData(int givenfid, Vector<ResultAggrStruct> allResultsforFunct) {
        super();
        super.setFid(Integer.toString(givenfid));
        List<OutType> outTypeList = super.getOut();
        Iterator<ResultAggrStruct> resultStructIter =  allResultsforFunct.iterator();
        while(resultStructIter.hasNext())
        {
            outTypeList.add(resultStructIter.next());
        }
    }


    /**
     * Method mergeWith. Merges two ReqResultOverData objects.
     *
     * @param targetReqRes the target ReqResultOverData to be merged with this object
     */
    public void mergeWith(ReqResultOverData targetReqRes) {
        //
        // Get the associated  Vector<ResultAggrStruct> and search for matching mid/sids for each mid/sid in the targetReqRes.
        //
        //Vector<ResultAggrStruct> targRes = targetReqRes.allResultsforFunct;
        //Vector<ResultAggrStruct> myTmpRes = this.allResultsforFunct;

        List<OutType> targOutTypeList = targetReqRes.getOut();
        List<OutType> myOutTypeList = super.getOut();

        for (int i = 0; i < targOutTypeList.size(); i++) {
            String targ_mid = targOutTypeList.get(i).getMid().trim();
            int targ_sid = Integer.parseInt(targOutTypeList.get(i).getSid().trim());
            boolean matchisfound = false;
            for (int j = 0; j < myOutTypeList.size(); j++) {
                String myTmp_mid = myOutTypeList.get(j).getMid().trim();
                int myTmp_sid = Integer.parseInt(myOutTypeList.get(j).getSid().trim());
//                System.out.println("Comparing " + targ_mid + " with " + myTmp_mid  +" and " + Integer.toString(myTmp_sid) +" with "+ Integer.toString(targ_sid));
                if (myTmp_mid.equals(targ_mid) && (myTmp_sid == targ_sid)) {
//                    System.out.println("Match on " + targ_mid + "::" + Integer.toString(targ_sid));
                    //match found
                    //
                    // If there is a match then (probably a case of overwriting a default set timed-out field) overwrite its data
                    // with those from the targetReqRes
                    //
                    // System.out.println("Merging with other ResultAggrStruct");
                    myOutTypeList.set(j, new ResultAggrStruct(targOutTypeList.get(i)));
                    matchisfound = true;
                    break;
                }
            }
            //
            // If there is no match then append this ResultAggrStruct to this object's Vector<ResultAggrStruct>.
            //
            if (!matchisfound ) {
                //              System.out.println("NO match for " + targ_mid +"::" + Integer.toString(targ_sid));
                myOutTypeList.add(targOutTypeList.get(i));
            }
        }
    }

    /**
     * Returns the id for the specific function of this ReqResultOverData object.
     *
     * @return the id for this specific function.
     */
    public int getFidInt() {
        return Integer.parseInt(super.getFid());
    }

    /**
     * Returns the vector with results for the specific function of this ReqResultOverData object.
     *
     * @return the vector with results for the specific function of this ReqResultOverData object.
     */
    public Vector<ResultAggrStruct> getAllResultsforFunct() {
        Vector<ResultAggrStruct> resVec = new Vector<ResultAggrStruct>();
        List<OutType> outTypeList = super.getOut();
        Iterator<OutType> resultStructIter =  outTypeList.iterator();
        while(resultStructIter.hasNext())
        {
            resVec.add(new ResultAggrStruct(resultStructIter.next()));
        }
        return resVec;
    }

    public void setAllResultsforFunct(Vector<ResultAggrStruct> pVec) {
        List<OutType> outTypeList = super.getOut();
        outTypeList.clear();
        if(pVec == null || pVec.isEmpty()) {
               return;
        }
        else {
            for(ResultAggrStruct rasItem : pVec ) {
                OutType newItem = new OutType();
                newItem.setMid(rasItem.getMid());
                newItem.setNumOfAggrVal(rasItem.getNumOfAggrVal());
                newItem.setSid(rasItem.getSid());
                newItem.setTimePeriod(rasItem.getTimePeriod());
                newItem.setVal(rasItem.getVal());
                outTypeList.add(newItem);
            }
        };
    }

}
