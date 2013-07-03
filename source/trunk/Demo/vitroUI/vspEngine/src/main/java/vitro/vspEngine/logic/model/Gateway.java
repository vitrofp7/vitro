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

import java.util.HashMap;
import java.util.Vector;

import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.geo.GeoRegion;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class Gateway {
    // TODO: Assess if needed
    private String id;                  // this is the formal and unique registeredId for a gateway. It is a string   . E.g. "vitro_cti"
    private String name;                // this is a short friendly name for the gateway. E.g. "CTI ISLAND"
    private String description;         // this is an optional verbose description field for the gateway.
    private String ipV4address;
    private String ipV6address;
    private GeoRegion coverageArea;
    private GeoPoint gwLocationPoint;
    private HashMap<String, Vector<SensorModel>> allGwGenericCapabilities;

    public static final String invalidGwID = "";
    public static final String invalidGwName = "";
    private static final String unknownDescription = "unknown";

    public Gateway(){
        this.id = Gateway.invalidGwID;
        this.name = Gateway.invalidGwName;
        this.description = Gateway.unknownDescription;
        this.gwLocationPoint = new GeoPoint(); // a dummy INVALID point
        this.coverageArea = null;
        this.setAllGwGenericCapabilities(new  HashMap<String, Vector<SensorModel>>());

    }

    public Gateway(Gateway pGateway)
    {
        this.id = pGateway.getId();
        this.name = pGateway.getName();
        this.setIpV4address(pGateway.getIpV4address());
        this.setIpV6address(pGateway.getIpV6address());
        this.description = pGateway.getDescription();
        this.gwLocationPoint = pGateway.getGwLocationPoint();
        this.coverageArea = pGateway.getCoverageArea();
        this.setAllGwGenericCapabilities(pGateway.getAllGwGenericCapabilities());
    }

    /**
     *
     * @param givId
     * @param givName
     * @param givDescription
     * @param givCoverageArea
     * @param givGwLocationPoint
     */
    public Gateway(String givId, String givName, String givDescription, GeoRegion givCoverageArea, GeoPoint givGwLocationPoint) {
        if (givId != null && !givId.trim().isEmpty())
            this.id = givId;
        else
            this.id = Gateway.invalidGwID;

        if (givName != null)
            this.name = givName;
        else
            this.name = Gateway.invalidGwName;

        if (givDescription != null && !givDescription.trim().isEmpty())
            this.description = givDescription;
        else
            this.description = Gateway.unknownDescription;

        this.coverageArea = givCoverageArea; // Careful: can be null if not set

        if (givGwLocationPoint != null)
            this.gwLocationPoint = givGwLocationPoint;
        else
            this.gwLocationPoint = new GeoPoint(); // a dummy INVALID point

        this.setAllGwGenericCapabilities(new  HashMap<String, Vector<SensorModel>>());
    }

    /**
     * Secondary constructor with additional info about ipv4 and ipv6 addresses!
     * @param givId
     * @param givName
     * @param givDescription
     * @param givCoverageArea
     * @param givGwLocationPoint
     * @param givIpV4addr
     * @param givIPV6Addr
     */
    public Gateway(String givId, String givName, String givDescription, GeoRegion givCoverageArea, GeoPoint givGwLocationPoint, String givIpV4addr, String givIPV6Addr) {

        this(givId,givName,givDescription,givCoverageArea,givGwLocationPoint);

        if(givIpV4addr != null)
            this.setIpV4address(givIpV4addr);
        else
            this.setIpV4address("127.0.0.1"); // a dummy invalid address (we put localhost here)

        if(givIPV6Addr != null)
            this.setIpV6address(givIPV6Addr);
        else
            this.setIpV6address("::1"); // a dummy invalid address (we put localhost here). it could also be the unspecified ipv6 addr ("::")
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

    public GeoRegion getCoverageArea() {
        return coverageArea;
    }

    public GeoPoint getGwLocationPoint() {
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

    public void setCoverageArea(GeoRegion coverageArea) {
        this.coverageArea = coverageArea;
    }

    public void setGwLocationPoint(GeoPoint gwLocationPoint) {
        this.gwLocationPoint = gwLocationPoint;
    }

    public HashMap<String, Vector<SensorModel>> getAllGwGenericCapabilities() {
        return allGwGenericCapabilities;
    }

    public void setAllGwGenericCapabilities(HashMap<String, Vector<SensorModel>> allGwGenericCapabilities) {
        this.allGwGenericCapabilities = allGwGenericCapabilities;
    }

    public String getIpV4address() {
        return ipV4address;
    }

    public void setIpV4address(String ipV4address) {
        this.ipV4address = ipV4address;
    }

    public String getIpV6address() {
        return ipV6address;
    }

    public void setIpV6address(String ipV6address) {
        this.ipV6address = ipV6address;
    }

    /**
     *
     * @return a string form containing a CSV of latitude and longitude (TODO: altitude is omitted!)
     */
    public String getGateLocationStr() {

        return this.getGwLocationPoint().getLatitude()+","+this.getGwLocationPoint().getLongitude();
    }


    /**
     * We don't compare the names. Just the ids.
     * TODO: what else should be compared?
     */
    public boolean equals(Gateway targGateway) {

        return (this.getId().compareToIgnoreCase(targGateway.getId()) == 0);
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
        Gateway other = (Gateway) obj;
        return this.equals(other);
    }


}
