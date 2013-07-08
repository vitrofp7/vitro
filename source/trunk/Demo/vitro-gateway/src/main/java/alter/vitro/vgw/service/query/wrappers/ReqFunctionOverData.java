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

import alter.vitro.vgw.service.query.xmlmessages.aggrquery.ReqFunctionType;
import org.apache.log4j.Logger;

import java.math.BigInteger;

/**
 * User: antoniou
 */
public class ReqFunctionOverData extends ReqFunctionType {
    private static Logger logger = Logger.getLogger(ReqFunctionOverData.class);
    public static final int unknownFuncId = -1;
    public static final String unknownFunc = "unknown";
    public static final String avgFunc = "Average of Values";
    public static final String maxFunc = "Maximum Value";
    public static final String minFunc = "Minimum Value";
    public static final String lastValFunc = "Last Value";
    public static final String histValFunc = "History of Values";
    public static final String setValFunc = "Set Value";

    //public static final String NODE_LEVEL_PREFIX  = "NL";
    public static final String GW_LEVEL_PREFIX = "gwlevel";
    public static final String GW_LEVEL_SEPARATOR = "_";
    public static final String ruleRuleBinaryAndFunc = "Rule Binary AND";
    public static final String ruleRuleIfThenFunc = "Rule IF THEN";

    //valid functions FOR NODE LEVEL operations
    static final String m_validFunctions[] = {ReqFunctionOverData.avgFunc,
            ReqFunctionOverData.maxFunc,
            ReqFunctionOverData.minFunc,
            ReqFunctionOverData.lastValFunc,
            ReqFunctionOverData.histValFunc,
            ReqFunctionOverData.setValFunc,
            ReqFunctionOverData.unknownFunc
            };
    //valid functions FOR GATEWAY LEVEL operations
    static final String m_gwLevelValidFunctions[] = {ReqFunctionOverData.avgFunc,
            ReqFunctionOverData.maxFunc,
            ReqFunctionOverData.minFunc,
            ReqFunctionOverData.ruleRuleBinaryAndFunc,
            ReqFunctionOverData.ruleRuleIfThenFunc
    };

    private TimeIntervalStructure requiredTimeInterval;
    private ThresholdStructure requiredThresholds;

    /**
     *     valid functions FOR Node LEVEL operations
     */
    private boolean isValidReqFunct(String funcName) {
        int i;
        for (i = 0; i < m_validFunctions.length - 1; i++) // -1 because we excluded the "unknown" final entry
        {
            if (funcName.equalsIgnoreCase(m_validFunctions[i]))
                return true;
            /*else{     // extra check for case, we have encapsulated it and appended a suffix to differentiate from identical function of the same name
                String[] descriptionTokens = funcName.split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                // eg example NL_Last Value_2
                //logger.debug("descriptionTokens[0] !!!" + descriptionTokens[0]);
                if( descriptionTokens !=null && descriptionTokens.length > 1
                        && ((descriptionTokens[0].equalsIgnoreCase(ReqFunctionOverData.NODE_LEVEL_PREFIX ) && descriptionTokens[1].equalsIgnoreCase(m_validFunctions[i]) ) ))
                {
                    return true;
                }
            } */
        }
        logger.error("An invalid node level function was specified!!!");
        return false;
    }

    /**
     *     valid functions FOR GATEWAY LEVEL operations
     */
    public static boolean isValidGatewayReqFunct(String funcName) {
        int i;
        int lenOfValidPrefix = 0;
        for (i = 0; i < m_gwLevelValidFunctions.length; i++) //
        {
            StringBuilder tmpValidPrefixBuild = new StringBuilder();
            tmpValidPrefixBuild.append(ReqFunctionOverData.GW_LEVEL_PREFIX);
            tmpValidPrefixBuild.append( ReqFunctionOverData.GW_LEVEL_SEPARATOR);
            tmpValidPrefixBuild.append(m_gwLevelValidFunctions[i]);

            lenOfValidPrefix = tmpValidPrefixBuild.toString().length();
            if (funcName.length() >= lenOfValidPrefix && funcName.substring(0, lenOfValidPrefix).equals(tmpValidPrefixBuild.toString()))
                return true;
        }
        logger.debug("An invalid gateway level function was specified!!!");
        return false;
    }

    public  ReqFunctionOverData(ReqFunctionType rftObj)
    {
        super();
        super.setId(rftObj.getId());

        String tmpFuncDesc = rftObj.getDescription().trim();
        if (tmpFuncDesc.equals("") || (!isValidReqFunct(tmpFuncDesc) && !isValidGatewayReqFunct(tmpFuncDesc))) {
            super.setDescription(ReqFunctionOverData.unknownFunc);
        }
        else
            super.setDescription(tmpFuncDesc);


        if (rftObj.getTimePeriod() == null ) {
            this.requiredTimeInterval = new TimeIntervalStructure();
        } else {
            this.requiredTimeInterval = new TimeIntervalStructure(rftObj.getTimePeriod());
        }
        if (rftObj.getThresholdField() == null || rftObj.getThresholdField().isEmpty()) {
            this.requiredThresholds = new ThresholdStructure();
        } else {
            this.requiredThresholds = new ThresholdStructure(rftObj.getThresholdField());
        }
        super.getThresholdField().addAll(rftObj.getThresholdField());
    }

    /**
     * Returns the function description in a String
     *
     * @return The function description
     */
    public String getfuncName() {
        /*String[] descriptionTokens = this.getDescription().split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        // Extra check ONLY for Node Level functions!
        // eg example NL_Last Value_2 or Last Value_2
        //logger.debug("descriptionTokens[0] !!!" + descriptionTokens[0]);
        if( descriptionTokens !=null && descriptionTokens.length > 1
                && ( (descriptionTokens[0].equalsIgnoreCase(ReqFunctionOverData.NODE_LEVEL_PREFIX ) && !descriptionTokens[1].isEmpty() ) ))
        {
            return descriptionTokens[1];
        }
        else*/
            return this.getDescription();
    }

    /**
     * Returns the function unique Id within this query definition
     *
     * @return The function unique id
     */
    public int getfuncId() {
        return this.getId().intValue();
    }

    /**
     * Sets the function unique Id within this query definition
     *
     * @param funcId The function unique id
     */
    public void setfuncId(int funcId) {
//        this.m_funcId = funcId;
        this.setId(BigInteger.valueOf(funcId));
    }

    /**
     * Compares two ReqFunctionOverData objects. The ids are not compared, since we are only interested in the function's functionality/parameters
     *
     * @param targetFunc the target ReqFunctionOverData to compare to
     * @return true if objects express the same Requested Function, or false otherwise
     */
    public boolean equals(ReqFunctionOverData targetFunc) {
        if (!this.getfuncName().equals(targetFunc.getfuncName())) {
            return false;
        }
        if ((this.requiredTimeInterval == null && targetFunc.requiredTimeInterval != null) ||
                (this.requiredTimeInterval != null && targetFunc.requiredTimeInterval == null) ||
                (!this.requiredTimeInterval.equals(targetFunc.requiredTimeInterval))) {
            return false;
        }
        if ((this.requiredThresholds == null && targetFunc.requiredThresholds != null) ||
                (this.requiredThresholds != null && targetFunc.requiredThresholds == null) ||
                (!this.requiredThresholds.equals(targetFunc.requiredThresholds))) {
            return false;
        }
        // at the end
        return true;
    }

}
