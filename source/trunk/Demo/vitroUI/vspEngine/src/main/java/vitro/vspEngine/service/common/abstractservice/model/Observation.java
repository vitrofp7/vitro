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
package vitro.vspEngine.service.common.abstractservice.model;

import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.Type;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.vspEngine.service.query.ReqFunctionOverData;

@Entity
public class Observation {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	@Version
	private int version;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;                    // when the measurement was taken according to the sensor or the gateway

    @Temporal(TemporalType.TIMESTAMP)
    private Date receivedTimestamp;            // when the measurement was received at the VSP

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

    private String refFunctName;
    private String refFunctNameEssential;
    private int refFunctIdInQueryDef;
    @Type(type="boolean")
    private boolean theDefinitionFunction; // indicates that this function is the one that corresponds to the formal definition of the capabiity (entry in the capability table with capabilityId).
                                            // and not an auxiliary function (eg. sometimes "last value" values are sent too for a capability, even though the capability requests only an aggregate (eg AVG or MAX)

    private String uom;
	
	private float value;

    public Observation(){
        super();
        setSensorCoordLat(Double.valueOf(0));
        setSensorCoordLong(Double.valueOf(0));
        setReplacmntSensorName("");
        setReplacmntsensorCoordLat(Double.valueOf(0));
        setReplacmntsensorCoordLong(Double.valueOf(0));
        setRefFunctName("");
        setRefFunctNameEssential("");
        setTheDefinitionFunction(false);
        setRefFunctIdInQueryDef(ReqFunctionOverData.unknownFuncId);
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

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
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

    /*
    public int getCompositeServiceID() {
        return compositeServiceID;
    }

    public void setCompositeServiceID(int compositeServiceID) {
        this.compositeServiceID = compositeServiceID;
    }
    */

    public Date getReceivedTimestamp() {
        return receivedTimestamp;
    }

    public void setReceivedTimestamp(Date receivedTimestamp) {
        this.receivedTimestamp = receivedTimestamp;
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

    public String getRefFunctName() {
        return refFunctName;
    }

    public void setRefFunctName(String refFunctName) {
        this.refFunctName = refFunctName;
    }

    public boolean isTheDefinitionFunction() {
        return theDefinitionFunction;
    }

    public void setTheDefinitionFunction(boolean theDefinitionFunction) {
        this.theDefinitionFunction = theDefinitionFunction;
    }

    public String getRefFunctNameEssential() {
        return refFunctNameEssential;
    }

    public void setRefFunctNameEssential(String pRefFunctNameEssential) {
        refFunctNameEssential = pRefFunctNameEssential;
    }

    public int getRefFunctIdInQueryDef() {
        return refFunctIdInQueryDef;
    }

    public void setRefFunctIdInQueryDef(int refFunctIdInQueryDef) {
        this.refFunctIdInQueryDef = refFunctIdInQueryDef;
    }
}
