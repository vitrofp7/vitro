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
import alter.vitro.vgw.service.query.xmlmessages.response.TimePeriodType;

/**
 * User: antoniou
 */
public class ResultAggrStruct extends OutType{
    //TimeIntervalStructure tis;
    public static final String MidSpecialForAggregateMultipleValues = "-1";
    /**
     * Creates a new instance of ResultAggrStruct
     *
     * @param mid             A string with the mote id
     * @param sid             The sensor model id
     * @param val             the value (aggregated or not) of a reading/ of multiple readings.
     * @param numofAggrValues
     * @param tis             A timeIntervalStructure that shows the interval in which this motes' sensors value(s) were read.
     */
    public ResultAggrStruct(String mid, int sid, String val, int numofAggrValues, TimeIntervalStructure tis) {

        super.setMid(mid);
        super.setSid(Integer.toString(sid));
        super.setVal(val);
        super.setNumOfAggrVal(Integer.toString(numofAggrValues));
        if(tis == null) {
            super.setTimePeriod(new TimePeriodType());
        }
        else
        {
            TimePeriodType myTimePeriod = new TimePeriodType();
            if(tis.isTimestampFromDefined())
                myTimePeriod.setFrom(tis.getFrom());
            if(tis.isTimestampToDefined())
                myTimePeriod.setTo(tis.getTo());
            super.setTimePeriod(myTimePeriod);
        }
    }

    /**
     * Copy constructor.
     */
    public ResultAggrStruct(OutType srcOt) {
        super.setMid(srcOt.getMid().trim());
        super.setSid(srcOt.getSid().trim());
        super.setVal(srcOt.getVal().trim());
        super.setNumOfAggrVal(srcOt.getNumOfAggrVal().trim());
        super.setTimePeriod(srcOt.getTimePeriod());
    }

    public int getNumOfAggrValues() {
        return Integer.parseInt(super.getNumOfAggrVal().trim());
    }

    public TimeIntervalStructure getTis() {
        TimeIntervalStructure retTis = new TimeIntervalStructure();
        if (super.getTimePeriod() != null)
        {
            if(super.getTimePeriod().getFrom() != null &&  !super.getTimePeriod().getFrom().trim().equals(""))
            {
                retTis.setFrom(super.getTimePeriod().getFrom().trim());
            }
            if(super.getTimePeriod().getTo() != null && !super.getTimePeriod().getTo().trim().equals(""))
            {
                retTis.setTo(super.getTimePeriod().getTo().trim());
            }
        }
        return retTis;
    }

}
