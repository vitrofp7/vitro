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
package vitro.vspEngine.service.query;

import vitro.vspEngine.logic.model.Capability;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 */
public class NotificationsFromVSNs {
    public static final String ALERT_PREFIX = "ALERT";
    public static final String alertDelimiter = "_##_";
    public static final int DEPLOY_STATUS_TYPE = 5;
    public static final int ALL_CONDITIONS_MET_TYPE = 4;
    public static final int CRITICAL_TYPE = 3;
    public static final int INFO_TYPE = 2;
    public static final int SECURITY_TYPE = 1;
    public static final int UNDEFINED_TYPE = -1;
    public static final int GATEWAY_LEVEL = 1;
    public static final int VSP_LEVEL = 2;
    public static final int UNDEF_LEVEL = -1;


    private String queryDefId;
    private String message;
    private String timestamp;
    private String vgwID;
    private int type;
    private String moteID;
    private String replacmntID;
    private long value;
    private long boundValue;
    private String refFunctName;
    private String refFunctTriggerSign;
    private String capabilityCode;
    private String valueTimestamp;
    private int level;
    private int refFunctId;

    public String getQueryDefId() {
        return queryDefId;
    }

    public NotificationsFromVSNs(){
        queryDefId = "";
        message = "";
        timestamp = Long.toString(System.currentTimeMillis());
        vgwID = "";
        moteID = "";
        setType(UNDEFINED_TYPE);
        setMoteID("");
        setReplacmntID("");
        setValue(0);
        setBoundValue(0);
        setRefFunctName(ReqFunctionOverData.unknownFunc);
        setRefFunctTriggerSign("");
        setCapabilityCode("");
        setValueTimestamp(Long.toString(System.currentTimeMillis()));
        setLevel(NotificationsFromVSNs.UNDEF_LEVEL);
        setRefFunctId(ReqFunctionOverData.unknownFuncId);
    }

    public NotificationsFromVSNs(String fromAlertDelimitedString) {
        String[] tokensInAlertArr = fromAlertDelimitedString.split(Pattern.quote(alertDelimiter));
        if(tokensInAlertArr.length >= 5)
        {
            queryDefId = tokensInAlertArr[1];
            vgwID = tokensInAlertArr[2];
            timestamp = tokensInAlertArr[3];
            message = tokensInAlertArr[4];
            if(tokensInAlertArr.length >= 15) //support for recent extension to alert messages
            {
                setType(Integer.parseInt(tokensInAlertArr[5]));
                setMoteID(tokensInAlertArr[6]);
                setValue(Long.parseLong(tokensInAlertArr[7]));
                setBoundValue(Long.parseLong(tokensInAlertArr[8]));
                setRefFunctName(tokensInAlertArr[9]);
                setRefFunctTriggerSign(tokensInAlertArr[10]);
                setCapabilityCode(tokensInAlertArr[11]);
                setValueTimestamp(tokensInAlertArr[12]);
                setLevel(Integer.parseInt(tokensInAlertArr[13]));
                setRefFunctId(Integer.parseInt(tokensInAlertArr[14]));
                if(tokensInAlertArr.length >= 16) //support for recent extension to alert messages for replacement node
                {
                    setReplacmntID(tokensInAlertArr[15]);
                }
            }
        }
        else
        {
            queryDefId = "";
            vgwID = "Unknown VGW";
            timestamp = Long.toString(System.currentTimeMillis());
            message = "Generic Alert Message";
            setType(UNDEFINED_TYPE);
            setMoteID("");
            setReplacmntID("");
            setValue(0);
            setBoundValue(0);
            setRefFunctName(ReqFunctionOverData.unknownFunc);
            setRefFunctTriggerSign("");
            setCapabilityCode("");
            setValueTimestamp(Long.toString(System.currentTimeMillis()));
            setLevel(NotificationsFromVSNs.UNDEF_LEVEL);
            setRefFunctId(ReqFunctionOverData.unknownFuncId);
        }
    }

    public static String getAlertDelimitedString(NotificationsFromVSNs pSampleNotify) {
        StringBuilder bldToReturn = new StringBuilder();
        bldToReturn.append(ALERT_PREFIX);
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getQueryDefId()); // first queryDefId
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getVgwID()); // second VGW
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getTimestamp());    // 3rd timestamp
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getMessage());  // 4rth message
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getType());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getMoteID());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getValue());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getBoundValue());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getRefFunctName());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getRefFunctTriggerSign());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getCapabilityCode());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getValueTimestamp());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getLevel());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getRefFunctId());
        bldToReturn.append(alertDelimiter);
        bldToReturn.append(pSampleNotify.getReplacmntID());
        return bldToReturn.toString();
    }

    public void setQueryDefId(String queryDefId) {
        this.queryDefId = queryDefId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getVgwID() {
        return vgwID;
    }

    public void setVgwID(String vgwID) {
        this.vgwID = vgwID;
    }

    // -- printing details

    private String toBold(String targStr, boolean isHtml)
    {
        String boldDelimiterStart = "*";
        String boldDelimiterEnd = "*";
        if (isHtml)
        {
            boldDelimiterStart = "<b>";
            boldDelimiterEnd = "</b>";

        }
        return boldDelimiterStart+targStr+boldDelimiterEnd;
    }

    public String getDetailsInHtml()
    {
        return getDetailsInText(true);
    }

    public String getDetailsInText(boolean isHtml)
    {
        String lineDelimiter = "\n";
        if (isHtml)
        {
            lineDelimiter = "<br />";
        }
        StringBuilder buildTheString = new StringBuilder();
        buildTheString.append(toBold("Notification: ", isHtml));
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
        String notificationDateStr = df.format(new Date(Long.parseLong(this.getTimestamp()) ));
        buildTheString.append(notificationDateStr);
        buildTheString.append(" ");
        String mainMessage = this.getMessage();
        String mainMessageCapability = "";
        String[] messageTokens = mainMessage.split(Pattern.quote("__"));
        if(messageTokens!=null && messageTokens.length >= 2)
        {
            mainMessage = messageTokens[0];
            mainMessageCapability = Capability.getNameFromSensorModel(messageTokens[1]);
        }
        buildTheString.append(mainMessage);
        buildTheString.append(" ");
        buildTheString.append(mainMessageCapability);
        buildTheString.append(" from: ");
        buildTheString.append(this.getVgwID());
        return buildTheString.toString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMoteID() {
        return moteID;
    }

    public void setMoteID(String moteID) {
        this.moteID = moteID;
    }

    public long getValue() {
        return value;
    }

    public void setValue(long value) {
        this.value = value;
    }

    public long getBoundValue() {
        return boundValue;
    }

    public void setBoundValue(long boundValue) {
        this.boundValue = boundValue;
    }

    public String getRefFunctName() {
        return refFunctName;
    }

    public void setRefFunctName(String refFunctName) {
        this.refFunctName = refFunctName;
    }

    public String getRefFunctTriggerSign() {
        return refFunctTriggerSign;
    }

    public void setRefFunctTriggerSign(String refFunctTriggerSign) {
        this.refFunctTriggerSign = refFunctTriggerSign;
    }

    public String getCapabilityCode() {
        return capabilityCode;
    }

    public void setCapabilityCode(String capabilityCode) {
        this.capabilityCode = capabilityCode;
    }

    public String getValueTimestamp() {
        return valueTimestamp;
    }

    public void setValueTimestamp(String valueTimestamp) {
        this.valueTimestamp = valueTimestamp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRefFunctId() {
        return refFunctId;
    }

    public void setRefFunctId(int refFunctId) {
        this.refFunctId = refFunctId;
    }

    public String getReplacmntID() {
        return replacmntID;
    }

    public void setReplacmntID(String replacmntID) {
        this.replacmntID = replacmntID;
    }
}
