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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import vitro.vspEngine.service.persistence.DBRegionSelection;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.vspEngine.service.persistence.DBSmartNodeOfGateway;


@Entity
public class ServiceInstance {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	
	//Used for optimistic locking
	@Version
	private int version;
	
	private String name;
	
	@Type(type="boolean")
	private boolean encryption;
	
	@Type(type="boolean")
	private boolean allowDTN;
	
	@Type(type="boolean")
	private boolean rfidTracking;
	
	@Type(type="boolean")
	private boolean composition;

    @Type(type="boolean")
    private boolean rulesANDforNotify;

	@ElementCollection
	private List<String> searchTagList;
	
	@OneToMany(cascade = { CascadeType.MERGE})
	private List<Capability> observedCapabilities;

	/*
	@ManyToMany
	private List<DBRegisteredGateway> gatewayList;

    @ManyToMany
    private List<DBSmartNodeOfGateway> sensorNodeList;

    @ManyToMany
    private List<DBRegionSelection> selectedRegionList;
    */

    /*
    @ManyToMany
	private List<Observation> observationList;     */
	
	@Lob
	private String slaMessage;
	
	@Type(type="boolean")
	private boolean subscriptionEnabled;

    @Type(type="boolean")
    private boolean continuation;
	
	private long samplingRate;
	
	
	public ServiceInstance(){
		searchTagList = new ArrayList<String>();
		observedCapabilities = new ArrayList<Capability>();
		//gatewayList = new ArrayList<DBRegisteredGateway>();  // should mean full islands. If a gateway is listed here, this includes ALL of its sensors, regardless if the sensor selection has a subset of them.
        //setSensorNodeList(new ArrayList<DBSmartNodeOfGateway>());
        //setSelectedRegionList(new ArrayList<DBRegionSelection>());  // should remain as region. Should be resolved at runtime, continuously (and be udpated with new sensors added within, or failed).
		//observationList = new ArrayList<Observation>();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getSearchTagList() {
		return searchTagList;
	}

	public void setSearchTagList(List<String> searchTagList) {
		this.searchTagList = searchTagList;
	}

	public List<Capability> getObservedCapabilities() {
		return observedCapabilities;
	}

	public void setObservedCapabilities(List<Capability> observedCapabilities) {
		this.observedCapabilities = observedCapabilities;
	}

    /*
	public List<DBRegisteredGateway> getGatewayList() {
		return gatewayList;
	}

	public void setGatewayList(List<DBRegisteredGateway> gatewayList) {
		this.gatewayList = gatewayList;
	}
	*/
       /*
	public List<Observation> getObservationList() {
		return observationList;
	}

	public void setObservationList(List<Observation> observationList) {
		this.observationList = observationList;
	}
         */

	public boolean isEncryption() {
		return encryption;
	}

	public void setEncryption(boolean encryption) {
		this.encryption = encryption;
	}

	public boolean isAllowDTN() {
		return allowDTN;
	}

	public void setAllowDTN(boolean allowDTN) {
		this.allowDTN = allowDTN;
	}

	public boolean isRfidTracking() {
		return rfidTracking;
	}

	public void setRfidTracking(boolean rfidTracking) {
		this.rfidTracking = rfidTracking;
	}

	public boolean isComposition() {
		return composition;
	}

	public void setComposition(boolean composition) {
		this.composition = composition;
	}

	public String getSlaMessage() {
		return slaMessage;
	}

	public void setSlaMessage(String slaMessage) {
		this.slaMessage = slaMessage;
	}

	public boolean isSubscriptionEnabled() {
		return subscriptionEnabled;
	}

	public void setSubscriptionEnabled(boolean subscriptionEnabled) {
		this.subscriptionEnabled = subscriptionEnabled;
	}

	public long getSamplingRate() {
		return samplingRate;
	}

	public void setSamplingRate(long samplingRate) {
		this.samplingRate = samplingRate;
	}


    public boolean isRulesANDforNotify() {
        return rulesANDforNotify;
    }

    public void setRulesANDforNotify(boolean rulesANDforNotify) {
        this.rulesANDforNotify = rulesANDforNotify;
    }
    /*
    public List<DBSmartNodeOfGateway> getSensorNodeList() {
        return sensorNodeList;
    }

    public void setSensorNodeList(List<DBSmartNodeOfGateway> sensorNodeList) {
        this.sensorNodeList = sensorNodeList;
    }

    public List<DBRegionSelection> getSelectedRegionList() {
        return selectedRegionList;
    }

    public void setSelectedRegionList(List<DBRegionSelection> selectedRegionList) {
        this.selectedRegionList = selectedRegionList;
    }
    */

    public boolean isContinuation() {
        return continuation;
    }

    public void setContinuation(boolean continuation) {
        this.continuation = continuation;
    }
}
