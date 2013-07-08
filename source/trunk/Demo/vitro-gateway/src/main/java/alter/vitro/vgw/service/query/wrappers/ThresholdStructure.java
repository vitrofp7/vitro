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

import alter.vitro.vgw.service.query.xmlmessages.aggrquery.ThresholdFieldType;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 * Date: 5/20/12
 * Time: 12:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ThresholdStructure {
    private String upperBound;
    boolean upperBoundClosed;
    boolean upperBoundSet;

    private String lowerBound;
    boolean lowerBoundClosed;
    boolean lowerBoundSet;

    public static final String THRESHOLD_EQUAL = "Equal";
    public static final String THRESHOLD_LARGER = "Larger";
    public static final String THRESHOLD_LARGEROREQUAL = "LargerOrEqual";
    public static final String THRESHOLD_LOWER = "Lower";
    public static final String THRESHOLD_LOWEROREQUAL = "LowerOrEqual";
    public static final String THRESHOLD_UNKNOWN = "unknown";


    static final String m_validThresRelations[] = {THRESHOLD_LARGER, THRESHOLD_LARGEROREQUAL, THRESHOLD_LOWER, THRESHOLD_LOWEROREQUAL, THRESHOLD_EQUAL, THRESHOLD_UNKNOWN};


    /**
     * Creates a default empty threshold object
     */
    public ThresholdStructure() {
        upperBoundSet = false;
        lowerBoundSet = false;
        upperBound = "";
        lowerBound = "";
    }

    /**
     * Creates a default empty threshold object
     */
    public ThresholdStructure(List<ThresholdFieldType> tftObj) {
        upperBoundSet = false;
        lowerBoundSet = false;
        upperBound = "";
        lowerBound = "";
        for (ThresholdFieldType tmpField : tftObj)
        {
            if (isValidThresholdRelation(tmpField.getThresholdRelation().trim())) {
                dealWithRelationAndValue(tmpField.getThresholdRelation().trim(),  tmpField.getThresholdValue().trim());
            }
        }

    }

    /**
     * Creates a threshold object from supplied arguments
     *
     * @param threshHM is a HashMap that contains Strings with Threshold Relations mapped to the pertinent values. Each Key String in the Vector
     *                 must be a valid description for a Threshold relation (e.g. Larger, LargerOrEqual, Equal, Lower, LowerOrEqual)
     */
    public ThresholdStructure(HashMap<String, String> threshHM) {
        upperBoundSet = false;
        lowerBoundSet = false;
        upperBound = "";
        lowerBound = "";
        Set<String> tmpset1 = threshHM.keySet();
        Iterator it1 = tmpset1.iterator();
        while (it1.hasNext()) {
            String tmpkey = (String) it1.next();
            if (isValidThresholdRelation(tmpkey)) {
                dealWithRelationAndValue(tmpkey,  threshHM.get(tmpkey));
            }
        }
    }

    private boolean isValidThresholdRelation(String desc) {
        int i;
        for (i = 0; i < m_validThresRelations.length - 1; i++) // -1 because we excluded the "unknown" final entry
        {
            if (desc.equals(m_validThresRelations[i]))
                return true;
        }
        System.out.println("An invalid threshold relation was specified!!!");
        return false;
    }


    private void dealWithRelationAndValue(String relation, String txtValue) {
        if ((relation.equals(ThresholdStructure.THRESHOLD_LARGER) || relation.equals(ThresholdStructure.THRESHOLD_LARGEROREQUAL)) && !isLowerBoundSet()) {
            this.lowerBound = txtValue;
            lowerBoundSet = true;
            lowerBoundClosed = (relation.equals(ThresholdStructure.THRESHOLD_LARGER)) ? false : true;
        } else if ((relation.equals(ThresholdStructure.THRESHOLD_LOWER) || relation.equals(ThresholdStructure.THRESHOLD_LOWEROREQUAL)) && !isUpperBoundSet()) {
            this.upperBound = txtValue;
            upperBoundSet = true;
            upperBoundClosed = (relation.equals(ThresholdStructure.THRESHOLD_LOWER)) ? false : true;
        } else if (relation.equals(ThresholdStructure.THRESHOLD_EQUAL)) {
            upperBoundSet = true;
            upperBoundClosed = true;
            lowerBoundSet = true;
            lowerBoundClosed = true;
            this.upperBound = txtValue;
            this.lowerBound = this.getUpperBound();
        }
    }


    public boolean isUpperBoundSet() {
        return upperBoundSet;
    }

    public boolean isUpperBoundClosed() {
        return upperBoundClosed;
    }

    public boolean isLowerBoundSet() {
        return lowerBoundSet;
    }

    public boolean isLowerBoundClosed() {
        return lowerBoundClosed;
    }

    public boolean isSetThreshold() {
        return (isUpperBoundSet() || isLowerBoundSet());
    }

    // TODO: this could be simplified with storing an arraylist as internal structure instead of member vars (?)
    /**
     * Compares two ThresholdStructure objects
     *
     * @param targetThres the target ThresholdStructure to compare to
     * @return true if objects express the same Threshold, or false otherwise
     */
    public boolean equals(ThresholdStructure targetThres) {
        if (!this.isSetThreshold() && !targetThres.isSetThreshold())
            return true;

        if ((this.isSetThreshold() && !targetThres.isSetThreshold()) ||
                (!this.isSetThreshold() && targetThres.isSetThreshold()) ||
                (this.isSetThreshold() && this.isLowerBoundSet() && !targetThres.isLowerBoundSet()) ||
                (this.isSetThreshold() && !this.isLowerBoundSet() && targetThres.isLowerBoundSet()) ||
                (this.isSetThreshold() && this.isUpperBoundSet() && !targetThres.isUpperBoundSet()) ||
                (this.isSetThreshold() && !this.isUpperBoundSet() && targetThres.isUpperBoundSet())) {
            return false;
        }
        if ((this.isLowerBoundSet() && this.isLowerBoundClosed() && !targetThres.isLowerBoundClosed()) ||
                (this.isLowerBoundSet() && !this.isLowerBoundClosed() && targetThres.isLowerBoundClosed()) ||
                (this.isLowerBoundSet() && this.isUpperBoundClosed() && !targetThres.isUpperBoundClosed()) ||
                (this.isLowerBoundSet() && !this.isUpperBoundClosed() && targetThres.isUpperBoundClosed())) {
            return false;
        }

        if (this.isLowerBoundSet() && !this.getLowerBound().equalsIgnoreCase(targetThres.getLowerBound()) ||
                this.isUpperBoundSet() && !this.getUpperBound().equalsIgnoreCase(targetThres.getUpperBound())) {
            return false;
        }
        // at the end
        return true;
    }

    public String getUpperBound() {
        return upperBound;
    }

    public String getLowerBound() {
        return lowerBound;
    }
}
