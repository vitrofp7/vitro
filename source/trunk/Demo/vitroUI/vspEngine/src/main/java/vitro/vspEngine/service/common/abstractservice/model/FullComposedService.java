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

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * A full composed service is comprised from service Instances and is the one that will be eventually deployed
 * For legacy compatibility: it should suffice to have a FullComposedService per Service Instance
 */

@Entity
public class FullComposedService {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    private String name;
    private String friendlyName;

    @Type(type="boolean")
    private boolean predeployed;

    @Type(type="boolean")
    private boolean globalDTNEnableRequest; // applies to all partial services, but it's only enabled at gateways that support it

    @Type(type="boolean")
    private boolean globalContinuationEnableRequest; // applies to all partial services, but it's only enabled at gateways that support it

    @Type(type="boolean")
    private boolean globalEncryptionEnableRequest; // applies to all partial services, but it's only enabled at gateways that support it

    @Type(type="boolean")
    private boolean globalAsynchronousEnableRequest; // applies to all partial services, but it's only enabled at gateways that support it

    private String predeployedId; // aux field. will have values like: pre0, pre1, pre2 etc


    private int samplingFrequency;///in minutes (global for all subservices)
    @ManyToMany
    private List<ServiceInstance> serviceInstanceList;

    // we could add a search tags field merging the search tags of the partial services
    @ElementCollection
    private List<String> searchTagList;

    public FullComposedService(){
        setSearchTagList(new ArrayList<String>());
        setServiceInstanceList(new ArrayList<ServiceInstance>());
        setPredeployed(false);
        setPredeployedId("");
        setGlobalDTNEnableRequest(false);
        setPublicAccess(false);
        setOwnerRoleId(-1); // invalid role
        setOwnerUserId(-1); // invalid userid
        setCreatedTimestamp(new Date());
    }

    private boolean publicAccess; // true if can be accessed by other users.
    private int ownerUserId; // set to the user id that created the service.
    private int ownerRoleId; // group access if admin role (?)

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public List<ServiceInstance> getServiceInstanceList() {
        return serviceInstanceList;
    }

    public void setServiceInstanceList(List<ServiceInstance> serviceInstanceList) {
        this.serviceInstanceList = serviceInstanceList;
    }

    public List<String> getSearchTagList() {
        return searchTagList;
    }

    public void setSearchTagList(List<String> searchTagList) {
        this.searchTagList = searchTagList;
    }

    public int getSamplingFrequency() {
        return samplingFrequency;
    }

    public void setSamplingFrequency(int samplingFrequency) {
        this.samplingFrequency = samplingFrequency;
    }

    public boolean isPredeployed() {
        return predeployed;
    }

    public void setPredeployed(boolean predeployed) {
        this.predeployed = predeployed;
    }

    public String getPredeployedId() {
        return predeployedId;
    }

    public void setPredeployedId(String predeployedId) {
        this.predeployedId = predeployedId;
    }

    public boolean isGlobalDTNEnableRequest() {
        return globalDTNEnableRequest;
    }

    public void setGlobalDTNEnableRequest(boolean globalDTNEnableRequest) {
        this.globalDTNEnableRequest = globalDTNEnableRequest;
    }

    public boolean isGlobalContinuationEnableRequest() {
        return globalContinuationEnableRequest;
    }

    public void setGlobalContinuationEnableRequest(boolean globalContinuationEnableRequest) {
        this.globalContinuationEnableRequest = globalContinuationEnableRequest;
    }

    public boolean isGlobalEncryptionEnableRequest() {
        return globalEncryptionEnableRequest;
    }

    public void setGlobalEncryptionEnableRequest(boolean globalEncryptionEnableRequest) {
        this.globalEncryptionEnableRequest = globalEncryptionEnableRequest;
    }

    public boolean isGlobalAsynchronousEnableRequest() {
        return globalAsynchronousEnableRequest;
    }

    public void setGlobalAsynchronousEnableRequest(boolean globalAsynchronousEnableRequest) {
        this.globalAsynchronousEnableRequest = globalAsynchronousEnableRequest;
    }

    public boolean isPublicAccess() {
        return publicAccess;
    }

    public void setPublicAccess(boolean publicAccess) {
        this.publicAccess = publicAccess;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public int getOwnerRoleId() {
        return ownerRoleId;
    }

    public void setOwnerRoleId(int ownerRoleId) {
        this.ownerRoleId = ownerRoleId;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
