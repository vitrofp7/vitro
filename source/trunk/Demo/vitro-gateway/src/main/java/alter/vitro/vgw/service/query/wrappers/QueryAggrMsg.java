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


import alter.vitro.vgw.service.query.xmlmessages.aggrquery.MoteType;
import alter.vitro.vgw.service.query.xmlmessages.aggrquery.MyQueryAggrType;
import alter.vitro.vgw.service.query.xmlmessages.aggrquery.ReqFunctionType;

import java.util.List;
import java.util.Vector;

public class QueryAggrMsg {

    private static final String thisMsgType = "aggregatedQuery";

    private MyQueryAggrType query;

    /**
     * Returns a string that declares the type of this message. This is put for extensibility purposes.
     *
     * @return String with the query message type.
     */
    public static String getThisMsgType() {
        return thisMsgType;
    }

    public QueryAggrMsg(MyQueryAggrType pQuery)
    {
        setQuery(pQuery);
    }

    public MyQueryAggrType getQuery() {
        return query;
    }

    public void setQuery(MyQueryAggrType query) {
        this.query = query;
    }

    public Vector<ReqFunctionOverData> getReqFunctionVector() {
        Vector<ReqFunctionOverData> resVec = new Vector<ReqFunctionOverData>();
        List<ReqFunctionType> rftList =  this.getQuery().getReqFunctionsList().getReqFunction();
        if(rftList!= null && rftList.size() >0 )
        {
            for(int i = 0 ; i< rftList.size(); i++)
            {
                resVec.addElement(new ReqFunctionOverData(rftList.get(i)));
            }
        }
        return resVec;
    }

    public Vector<QueriedMoteAndSensors> getMotesAndTheirSensorAndFunctVec()
    {
        Vector<QueriedMoteAndSensors> resVec = new Vector<QueriedMoteAndSensors>();
        List<MoteType> mlfList =  this.getQuery().getMotesList().getMote();
        if(mlfList!= null && mlfList.size() >0 )
        {
            for(int i = 0 ; i< mlfList.size(); i++)
            {
                resVec.addElement(new QueriedMoteAndSensors(mlfList.get(i)));
            }
        }
        return resVec;
    }

}
