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

import alter.vitro.vgw.service.query.xmlmessages.aggrquery.TimePeriodType;

import java.sql.Timestamp;

/**
 * User: antoniou
 */
public class TimeIntervalStructure extends TimePeriodType {

    /**
     * Creates a default empty TimeInterval object
     */
    public TimeIntervalStructure() {
        super();
    }

    /**
     * Creates a TimeInterval object from the TimePeriodType of the unmarshalled object
     */
    public TimeIntervalStructure(TimePeriodType tptObj) {
        super();
        if(tptObj.getFrom() != null)
        {
            super.setFrom(tptObj.getFrom().trim() );

        }
        if(tptObj.getTo() != null)
        {
            super.setTo(tptObj.getTo().trim());
        }
    }


    /**
     * Constructs a TimeInterval object from the given parameters
     * If both parameters are null, then it assumed that we want all values returned.
     * If both parameters have the same value, then  the Interval is essentially a specific point in time.
     *
     * @param tsfrom a timestamp that defines the starting point in time (it can be null, so no limit is set as a starting point)
     * @param tsto   a timestamp that defines the finishing point in time (it can be null, so no limit is set as an ending point - practical limit it current time)
     */
    public TimeIntervalStructure(Timestamp tsfrom, Timestamp tsto) {
        if (tsfrom != null) {
            super.setFrom(tsfrom.toString() );
        }

        if (tsto != null) {
            super.setTo(tsto.toString());
        }
    }

    /**
     * Creates a string (text) for this TimeInterval object,
     *
     * @return the string representation of this object
     */
    public String createInfoInText() {
        String toReturnStr = "";
        if (this.isAnyTimestampDefined()) {
            if (this.isTimestampFromDefined() && this.isTimestampToDefined() && (this.getToTimestamp().compareTo(this.getFromTimestamp()) == 0)) {
                toReturnStr = " at:" + this.getToTimestamp().toString();
            } else {
                toReturnStr = " at interval";
                if (this.isTimestampFromDefined()) {
                    toReturnStr = " from:" + this.getFromTimestamp().toString();
                }
                if (this.isTimestampToDefined()) {
                    toReturnStr += " till:" + this.getToTimestamp().toString();
                }
            }
        } else toReturnStr = "(undefined)";

        return toReturnStr;

    }

    public boolean isTimestampFromDefined() {
        return (super.getFrom()!=null && ! super.getFrom().trim().equals(""));
    }

    public boolean isTimestampToDefined() {
        return (super.getTo()!=null && ! super.getTo().trim().equals(""));
    }

    public boolean isAnyTimestampDefined() {
        return (isTimestampToDefined() || isTimestampFromDefined());
    }

    public Timestamp getToTimestamp() {
        Timestamp tmpTo = null;
        if(isTimestampToDefined())
            tmpTo = Timestamp.valueOf(super.getTo().trim());
        return tmpTo;

    }

    public Timestamp getFromTimestamp() {
        Timestamp tmpFrom = null;
        if(isTimestampFromDefined())
            tmpFrom = Timestamp.valueOf(super.getFrom().trim());
        return tmpFrom;
    }

    /**
     * Compares two TimeIntervalStructure objects
     *
     * @param targetTis the target TimeIntervalStructure to compare to
     * @return true if objects express the same TimeInterval, or false otherwise
     */
    public boolean equals(TimeIntervalStructure targetTis) {
        if (!this.isAnyTimestampDefined() && !targetTis.isAnyTimestampDefined())
            return true;

        if ((this.isAnyTimestampDefined() && !targetTis.isAnyTimestampDefined()) ||
                (!this.isAnyTimestampDefined() && targetTis.isAnyTimestampDefined()) ||
                (this.isAnyTimestampDefined() && this.isTimestampToDefined() && !targetTis.isTimestampToDefined()) ||
                (this.isAnyTimestampDefined() && !this.isTimestampToDefined() && targetTis.isTimestampToDefined()) ||
                (this.isAnyTimestampDefined() && this.isTimestampFromDefined() && !targetTis.isTimestampFromDefined()) ||
                (this.isAnyTimestampDefined() && !this.isTimestampFromDefined() && targetTis.isTimestampFromDefined())) {
            return false;
        }

        if ((this.isTimestampToDefined()) &&
                !(this.getToTimestamp().equals(targetTis.getToTimestamp()))) {
            return false;
        }

        if ((this.isTimestampFromDefined()) &&
                !(this.getFromTimestamp().equals(targetTis.getFromTimestamp()))) {
            return false;
        }

        // at the end
        return true;
    }
}
