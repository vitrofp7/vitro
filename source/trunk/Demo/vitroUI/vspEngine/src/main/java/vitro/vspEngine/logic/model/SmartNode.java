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
package vitro.vspEngine.logic.model;

import vitro.vspEngine.service.geo.Coordinate;
import vitro.vspEngine.service.geo.GeoPoint;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class SmartNode {

    public static final String invalidId = "-1"; // TODO: Assuming a smart node ID can never be "-1"
    private String id;
    private String name;
    private String locationDesc;
    private Coordinate coordLocation;
    private GeoPoint gpPoint; // todo: this will have to be merged eventually with the Coordinate member.
    private String creationTime;
    private String registrationTime;
    private String dcaStatus;  // is a status used by DCA. It's not connected to enabled/disabled status of a node (for VITRO the platform (as of yet)
    private SmartNodeProperties registryProperties; // this class stores the enabled/disabled status of the node (among other possible extended properties)

    //Resources available on a given node
    private Vector<SensorModel> capabilitiesVector;


    public SmartNode() {
        super();
        setCoordLocation(new Coordinate());
        setLocation(new GeoPoint());
        capabilitiesVector = new Vector<SensorModel>() {};
        setRegistrationTime("2012-05-23T08:12:58Z");     //dummy
        setCreationTime("2012-05-23T08:12:58Z");         //dummy
        setDcaStatus("");                                 // default ""
        setRegistryProperties(new SmartNodeProperties(this.id));
    }



    public SmartNode(String pId, String pName, String pLocationDesc, Coordinate pGplocation) {
        super();
        this.id = pId;
        this.setName(pName);
        this.setLocationDesc(pLocationDesc);
        this.setCoordLocation(pGplocation);
        this.setLocation(new GeoPoint(pGplocation.getY(), pGplocation.getX(), pGplocation.getZ()));
        setRegistrationTime("2012-05-23T08:12:58Z");     //dummy
        setCreationTime("2012-05-23T08:12:58Z");         //dummy
        setDcaStatus("");                                 // default ""
        setRegistryProperties(new SmartNodeProperties(this.id));
    }

    public SmartNode(String pId, String pName, String pLocationDesc, GeoPoint pGpGeoPoint) {
        super();
        this.id = pId;
        this.setName(pName);
        this.setLocationDesc(pLocationDesc);
        this.setCoordLocation(new Coordinate(pGpGeoPoint.getLongitude(), pGpGeoPoint.getLatitude(), pGpGeoPoint.getAltitude()));
        this.setLocation(pGpGeoPoint);
        setRegistrationTime("2012-05-23T08:12:58Z");     //dummy
        setCreationTime("2012-05-23T08:12:58Z");         //dummy
        setDcaStatus("");                                 // default ""
        setRegistryProperties(new SmartNodeProperties(this.id));
    }

    public SmartNode(String pId, String pName, String pLocationDesc, GeoPoint pGpGeoPoint, String pCreationTime, String pRegistrationTime, String pStatus)
    {
        this(pId, pName, pLocationDesc, pGpGeoPoint);
        setRegistrationTime(pCreationTime);
        setCreationTime(pRegistrationTime);
        setDcaStatus(pStatus);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.getRegistryProperties().setNodeId(this.id);
    }

    public Vector<SensorModel> getCapabilitiesVector() {
        return capabilitiesVector;
    }

    public void setCapabilitiesVector(Vector<SensorModel> capabilitiesVector) {
        this.capabilitiesVector = capabilitiesVector;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    // TODO: should we also check for exactly identical capabilitiesList?
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SmartNode other = (SmartNode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (id.compareToIgnoreCase(other.id)!=0)
            return false;
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationDesc() {
        return locationDesc;
    }

    public void setLocationDesc(String locationDesc) {
        this.locationDesc = locationDesc;
    }

    public Coordinate getCoordLocation() {
        return coordLocation;
    }

    public void setCoordLocation(Coordinate coordLocation) {
        this.coordLocation = coordLocation;
    }

    public GeoPoint getLocation() {
        return gpPoint;
    }

    public void setLocation(GeoPoint gpPoint) {
        this.gpPoint = gpPoint;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(String registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getDcaStatus() {
        return dcaStatus;
    }

    public void setDcaStatus(String dcaStatus) {
        this.dcaStatus = dcaStatus;
    }

    public SmartNodeProperties getRegistryProperties() {
        return registryProperties;
    }

    public void setRegistryProperties(SmartNodeProperties registryProperties) {
        this.registryProperties = registryProperties;
    }
}
