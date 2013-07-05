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

/**
 */

import alter.vitro.vgw.service.geodesics.GeodesicPoint;
import alter.vitro.vgw.service.geodesics.GeodesicRegion;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 */
public class CGateway {
    // TODO: Assess if needed
    private String id;
    private String name;
    private String description;
    private GeodesicRegion coverageArea;
    private GeodesicPoint gwLocationPoint;
    private HashMap<String, Vector<CSensorModel>> allGwGenericCapabilities;

    public static final String invalidGwID = "";
    public static final String invalidGwName = "";
    private static final String unknownDescription = "unknown";

    public CGateway(){
        this.id = CGateway.invalidGwID;
        this.name = CGateway.invalidGwName;
        this.description = CGateway.unknownDescription;
        this.gwLocationPoint = new GeodesicPoint(); // a dummy INVALID point
        this.setAllGwGenericCapabilities(new  HashMap<String, Vector<CSensorModel>>());

    }

    public CGateway(String givId, String givName, String givDescription, GeodesicRegion givCoverageArea, GeodesicPoint givGwLocationPoint) {
        if (givId != null && !givId.equals(""))
            this.id = givId;
        else
            this.id = CGateway.invalidGwID;

        if (givName != null)
            this.name = givName;
        else
            this.name = CGateway.invalidGwName;

        if (givDescription != null && !givDescription.equals(""))
            this.description = givDescription;
        else
            this.description = CGateway.unknownDescription;

        this.coverageArea = givCoverageArea; // Careful: can be null if not set

        if (givGwLocationPoint != null)
            this.gwLocationPoint = givGwLocationPoint;
        else
            this.gwLocationPoint = new GeodesicPoint(); // a dummy INVALID point

        this.setAllGwGenericCapabilities(new  HashMap<String, Vector<CSensorModel>>());
    }


    public String getId() {

        return id;
    }


    public String getName() {

        return name;
    }

    public String getDescription() {

        return this.description;
    }

    public GeodesicRegion getCoverageArea() {
        return coverageArea;
    }

    public GeodesicPoint getGwLocationPoint() {
        return gwLocationPoint;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCoverageArea(GeodesicRegion coverageArea) {
        this.coverageArea = coverageArea;
    }

    public void setGwLocationPoint(GeodesicPoint gwLocationPoint) {
        this.gwLocationPoint = gwLocationPoint;
    }

    public HashMap<String, Vector<CSensorModel>> getAllGwGenericCapabilities() {
        return allGwGenericCapabilities;
    }

    public void setAllGwGenericCapabilities(HashMap<String, Vector<CSensorModel>> allGwGenericCapabilities) {
        this.allGwGenericCapabilities = allGwGenericCapabilities;
    }


    /**
     * We don't compare the names. Just the ids.
     * TODO: what else should be compared?
     */
    public boolean equals(CGateway targGateway) {

        return this.getId().equals(targGateway.getId());
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
        CGateway other = (CGateway) obj;
        return this.equals(other);
    }
}
