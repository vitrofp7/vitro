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
package alter.vitro.vgw.model;

import alter.vitro.vgw.service.geodesics.GeodesicPoint;
import alter.vitro.vgw.service.resourceRegistry.ResourceProperties;
import vitro.vgw.model.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * User: antoniou
 */
public class CSmartDevice {
    private String id;
    private String name;
    private String locationDesc;
    private GeodesicPoint gplocation;
    private List<Integer> specificSensorModelIdsVec; // assumed unique only inside a Gateway (and not globally overall gateways)
    private ResourceProperties registryProperties;


    public CSmartDevice(String givId, String givName, String givLocationDesc, GeodesicPoint givGeoLocation, Vector<Integer> givSpecificSensorIdsVec) {

        this.id = givId;
        this.name = givName;
        this.locationDesc = givLocationDesc;
        this.gplocation = givGeoLocation;
        if (givSpecificSensorIdsVec != null)
            this.specificSensorModelIdsVec = givSpecificSensorIdsVec;
        else
            this.specificSensorModelIdsVec = new Vector<Integer>();   // TODO: this could also be a list of Strings, for more freedom.

        setRegistryProperties(new ResourceProperties(givId));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        getRegistryProperties().setNodeId(this.id);
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

    public GeodesicPoint getGplocation() {
        return gplocation;
    }

    public void setGplocation(GeodesicPoint gplocation) {
        this.gplocation = gplocation;
    }

    public List<Integer> getSpecificSensorModelIdsVec() {
        return specificSensorModelIdsVec;
    }

    public void setSpecificSensorModelIdsVec(List<Integer> specificSensorModelIdsVec) {
        this.specificSensorModelIdsVec = specificSensorModelIdsVec;
    }

    @Override
    public int hashCode()  {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    // match on id and only
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CSmartDevice other = (CSmartDevice) obj;
        return id.equals(other.getId());
    }

    public ResourceProperties getRegistryProperties() {
        return registryProperties;
    }

    public void setRegistryProperties(ResourceProperties registryProperties) {
        this.registryProperties = registryProperties;
    }
}
