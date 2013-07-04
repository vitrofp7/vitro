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
package alter.vitro.vgw.service.geodesics;

//import java.util.Vector;

/**
 */
public class GeodesicPoint {
    public static final int NoHeightSet = -1;
    public static final int tokenOrderLatLonAlt = 0;
    public static final int tokenOrderLonLatAlt = 1;

    public static final int tokenOrderUnknown = 2;

    public static final int relToSeaLevelAltitude = 0;
    public static final int relToGroundAltitude = 1;
    public static final int relToUnknown = -1;

    public static final int absAltUndefined = -1;

    public static final double noElevationOverride = -1;

    private double m_latitude;
    private double m_longitude;
    private double m_fromGroundAltitude; // this is what we mainly use
    private double m_absAltitude;           // this is for informational purposes mostly
    private boolean validPoint;

    /**
     * Default constructor. Geodesic coordinates 0,0,0 are assumed.
     * But a point initiated by the default constructor is always INVALID until
     * manually and explicitly set to valid.
     */
    public GeodesicPoint() { // Default
        m_latitude = 0.0;
        m_longitude = 0.0;
        m_fromGroundAltitude = 0.0;
        m_absAltitude = GeodesicPoint.absAltUndefined;
        validPoint = false;
    }

    /**
     * Constructor. Create a GeodesicPoint from String arguments
     *
     * @param latitude           The latitude of the point.
     * @param longitude          The longitude of the point.
     * @param altitudeFromGround The altitude (from ground) of the point.
     */
    public GeodesicPoint(String latitude, String longitude, String altitudeFromGround) {
        m_latitude = 0.0;
        m_longitude = 0.0;
        m_fromGroundAltitude = 0.0;
        validPoint = true;
        try {
            m_latitude = Double.valueOf(latitude);
        } catch (NumberFormatException e) {
            validPoint = false;
        }
        try {
            m_longitude = Double.valueOf(longitude);
        } catch (NumberFormatException e) {
            validPoint = false;
        }
        try {
            m_fromGroundAltitude = Double.valueOf(altitudeFromGround);
        } catch (NumberFormatException e) {
            m_fromGroundAltitude = 0.0;
        }
        m_absAltitude = GeodesicPoint.absAltUndefined;
    }

    /**
     * Constructor
     *
     * @param latitude           The latitude of the point.
     * @param longitude          The longitude of the point.
     * @param altitudeFromGround The altitude (from ground) of the point.
     */
    public GeodesicPoint(double latitude, double longitude, double altitudeFromGround) {
        m_latitude = latitude;
        m_longitude = longitude;
        m_fromGroundAltitude = altitudeFromGround;
        m_absAltitude = GeodesicPoint.absAltUndefined;
        validPoint = true;
    }

    /**
     * Constructor
     *
     * @param latitude             The latitude of the point.
     * @param longitude            The longitude of the point.
     * @param absAltitudeForPoint  The altitude (from sea level) of the point.
     * @param absAltitudeForGround The altitude (from sea level) of the <b>ground</b> at that point.
     *                             <b>Careful</b>. If absAltitudeForPoint or absAltitudeForGround are given as GeodesicPoint.absAltUndefined
     *                             then the point will be valid BUT the altitude relative to ground will be set to "0.0").
     */
    public GeodesicPoint(double latitude, double longitude, double absAltitudeForPoint, double absAltitudeForGround) {
        m_latitude = latitude;
        m_longitude = longitude;
        m_absAltitude = absAltitudeForPoint;
        if (absAltitudeForPoint == GeodesicPoint.absAltUndefined || absAltitudeForGround == GeodesicPoint.absAltUndefined) {
            m_fromGroundAltitude = 0.0;
        } else {
            m_fromGroundAltitude = absAltitudeForPoint - absAltitudeForGround;
        }
        validPoint = true;
    }

    public boolean isValidPoint() {
        return validPoint;
    }

    public void setValidPoint(boolean validPoint) {
        this.validPoint = validPoint;
    }

    /**
     * Returns a point's longitude
     *
     * @return The point's longitude
     */
    public double getLongitude() {
        return m_longitude;
    }

    /**
     * Returns a point's latitude
     *
     * @return The point's latitude
     */
    public double getLatitude() {
        return m_latitude;
    }

    /**
     * Returns a point's Altitude
     *
     * @return The point's Altitude
     */
    public double getAltitude() {
        return m_fromGroundAltitude;
    }

    public double getAbsAltitude() {
        return m_absAltitude;
    }



    /**
     * Returns true if the points' latitude, longitude, and relative to ground altitude match. Also BOTH points should be valid.
     *
     * @return True if points are equal, and false else.
     */
    public boolean equals(GeodesicPoint targGeodesicPoint) {
        return targGeodesicPoint.isValidPoint() && this.isValidPoint() &&
                this.getLatitude() == targGeodesicPoint.getLatitude() &&
                this.getLongitude() == targGeodesicPoint.getLongitude() &&
                this.getAltitude() == targGeodesicPoint.getAltitude();
    }

    @Override
    public int hashCode()  {
        final int prime = 31;
        int result = 1;
        if (isValidPoint())
        {
            StringBuilder strBld = new StringBuilder();
            strBld.append(m_latitude);
            strBld.append(m_longitude);
            strBld.append(m_fromGroundAltitude);
            strBld.append(m_absAltitude);
            result = prime * result + ((strBld.toString() == null || strBld.toString().equals("")) ? 0 : strBld.toString().hashCode());
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GeodesicPoint other = (GeodesicPoint) obj;
        return this.equals(other);
    }
}
