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
package vitro.vspEngine.service.common.abstractservice.model;

import org.hibernate.annotations.Type;
import vitro.vspEngine.service.query.NotificationsFromVSNs;

import javax.persistence.*;
import java.util.Date;

/**
 */
@Entity
public class Notification {
    public static final int  TYPE_DEPLOY_STATUS = NotificationsFromVSNs.DEPLOY_STATUS_TYPE;
    public static final int  TYPE_SECURITY = NotificationsFromVSNs.SECURITY_TYPE;
    public static final int  TYPE_INFO = NotificationsFromVSNs.INFO_TYPE;
    public static final int  TYPE_CRITICAL = NotificationsFromVSNs.CRITICAL_TYPE;
    public static final int  TYPE_UNDEFINED = NotificationsFromVSNs.UNDEFINED_TYPE;
    public static final int  TYPE_ALL_CONDITIONS_MET = NotificationsFromVSNs.ALL_CONDITIONS_MET_TYPE;
    public static final int LEVEL_GATEWAY =  NotificationsFromVSNs.GATEWAY_LEVEL ;
    public static final int LEVEL_VSP = NotificationsFromVSNs.VSP_LEVEL ;
    public static final int LEVEL_UNDEFINED = NotificationsFromVSNs.UNDEF_LEVEL;


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Version
    private int version;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;                    // when the measurement was taken according to the sensor or the gateway

    @Temporal(TemporalType.TIMESTAMP)
    private Date receivedTimestamp;            // when the measurement was received at the VSP

    //TODO we need a connection to a measurement (but a notification may not be connected to a measurement (but to a processing of or a condition on a measurement or set of measurements)
    //TODO  So no FK to measurements. We can have a column to show the key of a measurement if such exists (otherwise null)
    //TODO an Entity is needed for it?!
    private String sensorName;
    private Double sensorCoordLat; //new
    private Double sensorCoordLong; //new

    private String replacmntSensorName; //new
    private Double replacmntsensorCoordLat; //new
    private Double replacmntsensorCoordLong; //new

    @Type(type="boolean")
    private boolean gatewayLevel;     //under consideration (should show that the value is a result of a gateway-level aggregate function (max, min etc within the same island)

    @Type(type="boolean")
    private boolean crossGatewayLevel;// under consideration (should show that the value is a result of cross-gateway-level aggregate function (eg. max over two islands)

    private int aggreagatedSensorsNum; // how many sensors were considered if the function was aggregate (if gatewayLevel or  crossGatewayLevel were set).

    private String gatewayRegName;

    private String resource;        // this should match the name of the corresponding capability for the measurement (TODO do we need this?)

    private int capabilityID;       // this should match the capabilityID (part of partial service) in the DB (TODO we could implement this as a FK too 1-1 connection)

    private int partialServiceID;   //this references the partialServiceID for this measurement (TODO we could implement this as a FK too 1-1 connection)

    //private int compositeServiceID; //references the composite service ID for this measurement (TODO, we probably don't really need this here, if the measurements are all connected to sub-services!)

    private String uom;

    private float value;

    private String notificationText;
    private int notificationType;

    private long boundValue;
    private String refFunctName;
    private String refFunctTriggerSign;
    private int level;
    private int refFunctId;

    public Notification(){
        super();
        notificationText = "";
        setNotificationType(Notification.TYPE_UNDEFINED);
        setLevel(Notification.LEVEL_UNDEFINED);
        setSensorCoordLat(Double.valueOf(0));
        setSensorCoordLong(Double.valueOf(0));
        setReplacmntSensorName("");
        setReplacmntsensorCoordLat(Double.valueOf(0));
        setReplacmntsensorCoordLong(Double.valueOf(0));

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(Date receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public boolean isGatewayLevel() {
        return gatewayLevel;
    }

    public void setGatewayLevel(boolean gatewayLevel) {
        this.gatewayLevel = gatewayLevel;
    }

    public boolean isCrossGatewayLevel() {
        return crossGatewayLevel;
    }

    public void setCrossGatewayLevel(boolean crossGatewayLevel) {
        this.crossGatewayLevel = crossGatewayLevel;
    }

    public int getAggreagatedSensorsNum() {
        return aggreagatedSensorsNum;
    }

    public void setAggreagatedSensorsNum(int aggreagatedSensorsNum) {
        this.aggreagatedSensorsNum = aggreagatedSensorsNum;
    }

    public String getGatewayRegName() {
        return gatewayRegName;
    }

    public void setGatewayRegName(String gatewayRegName) {
        this.gatewayRegName = gatewayRegName;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public int getCapabilityID() {
        return capabilityID;
    }

    public void setCapabilityID(int capabilityID) {
        this.capabilityID = capabilityID;
    }

    public int getPartialServiceID() {
        return partialServiceID;
    }

    public void setPartialServiceID(int partialServiceID) {
        this.partialServiceID = partialServiceID;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public int getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(int notificationType) {
        this.notificationType = notificationType;
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

    public Double getSensorCoordLat() {
        return sensorCoordLat;
    }

    public void setSensorCoordLat(Double sensorCoordLat) {
        this.sensorCoordLat = sensorCoordLat;
    }

    public Double getSensorCoordLong() {
        return sensorCoordLong;
    }

    public void setSensorCoordLong(Double sensorCoordLong) {
        this.sensorCoordLong = sensorCoordLong;
    }

    public String getReplacmntSensorName() {
        return replacmntSensorName;
    }

    public void setReplacmntSensorName(String replacmntSensorName) {
        this.replacmntSensorName = replacmntSensorName;
    }

    public Double getReplacmntsensorCoordLat() {
        return replacmntsensorCoordLat;
    }

    public void setReplacmntsensorCoordLat(Double replacmntsensorCoordLat) {
        this.replacmntsensorCoordLat = replacmntsensorCoordLat;
    }

    public Double getReplacmntsensorCoordLong() {
        return replacmntsensorCoordLong;
    }

    public void setReplacmntsensorCoordLong(Double replacmntsensorCoordLong) {
        this.replacmntsensorCoordLong = replacmntsensorCoordLong;
    }
}

