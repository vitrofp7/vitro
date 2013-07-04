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
package vitro.vspEngine.service.geo;

import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class GeoPoint  {
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
     * Default constructor. geo coordinates 0,0,0 are assumed.
     * But a point initiated by the default constructor is always INVALID until
     * manually and explicitly set to valid.
     */
    public GeoPoint() { // Default
        m_latitude = 0.0;
        m_longitude = 0.0;
        m_fromGroundAltitude = 0.0;
        m_absAltitude = GeoPoint.absAltUndefined;
        validPoint = false;
    }

    /**
     * Constructor. Create a GeoPoint from String arguments
     *
     * @param latitude           The latitude of the point.
     * @param longitude          The longitude of the point.
     * @param altitudeFromGround The altitude (from ground) of the point.
     */
    public GeoPoint(String latitude, String longitude, String altitudeFromGround) {
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
        m_absAltitude = GeoPoint.absAltUndefined;
    }

    /**
     * Constructor
     *
     * @param latitude           The latitude of the point.
     * @param longitude          The longitude of the point.
     * @param altitudeFromGround The altitude (from ground) of the point.
     */
    public GeoPoint(double latitude, double longitude, double altitudeFromGround) {
        m_latitude = latitude;
        m_longitude = longitude;
        m_fromGroundAltitude = altitudeFromGround;
        m_absAltitude = GeoPoint.absAltUndefined;
        validPoint = true;
    }

    /**
     * Constructor
     *
     * @param latitude             The latitude of the point.
     * @param longitude            The longitude of the point.
     * @param absAltitudeForPoint  The altitude (from sea level) of the point.
     * @param absAltitudeForGround The altitude (from sea level) of the <b>ground</b> at that point.
     *                             <b>Careful</b>. If absAltitudeForPoint or absAltitudeForGround are given as GeoPoint.absAltUndefined
     *                             then the point will be valid BUT the altitude relative to ground will be set to "0.0").
     */
    public GeoPoint(double latitude, double longitude, double absAltitudeForPoint, double absAltitudeForGround) {
        m_latitude = latitude;
        m_longitude = longitude;
        m_absAltitude = absAltitudeForPoint;
        if (absAltitudeForPoint == GeoPoint.absAltUndefined || absAltitudeForGround == GeoPoint.absAltUndefined) {
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

    //radious in kilometers
    public static Vector<GeoPoint> placePointsOnACircle(GeoPoint circleCenter, double givRadius, int numOfPoints) {
        Vector<GeoPoint> toReturnVec = new Vector<GeoPoint>();
        double refAzimuth = 360 / numOfPoints;
        double startingTilt = 0.0;
        for (int i = 0; i < numOfPoints; i++) {
            toReturnVec.addElement(GeoCalculus.GCDistanceAzimuth(circleCenter, givRadius, GeoCalculus.handleGivenAngleDeg(startingTilt + i * refAzimuth)));
        }
        return toReturnVec;
    }

    /**
     * The string with the coordinates should be separated by the string indicated by parameter separationString
     * Parameter order is defined by tokenOrderMode
     * In case of altitude mode "relative to ground", we only have 3 parameters. Longitude, Latitude, "Relative to ground" Altitude
     * In case of altitude mode "relative to the sea level", we MUST have 4 parameters. Longitude, Latitude, "From sea level" Altitude, "From sea level" Ground Altitude
     */
    public static GeoPoint parseStringGeodesicCoords(String charSeparatedCoords, int tokenOrderMode, String separationString, double elevationOverride) {
        GeoPoint thePointToReturn = null;
        int detectedAltitudeMode = GeoPoint.relToUnknown;
        if (charSeparatedCoords != null && charSeparatedCoords != "") {
            boolean validGivenCoords = true;
            double tmpGivenLong = 0;
            double tmpGivenLat = 0;
            double tmpGivenAlt = 0;
            double tmpGivenAbsAltForGound = GeoPoint.absAltUndefined;
            String tokensOfGivenCoords[] = charSeparatedCoords.split(separationString);
            if (tokensOfGivenCoords != null && tokensOfGivenCoords.length >= 3 && tokensOfGivenCoords.length <= 4) {
                try {
                    if (tokensOfGivenCoords.length == 4) {
                        detectedAltitudeMode = GeoPoint.relToSeaLevelAltitude;
                    } else // the else deals ONLY with the tokensOfGivenCoords.length == 3 case , cause of the parent if clause
                    {
                        detectedAltitudeMode = GeoPoint.relToGroundAltitude;
                    }

                    switch (tokenOrderMode) {
                        case GeoPoint.tokenOrderLatLonAlt: {
                            tmpGivenLat = Double.parseDouble(tokensOfGivenCoords[0]);
                            tmpGivenLong = Double.parseDouble(tokensOfGivenCoords[1]);
                            if (elevationOverride != GeoPoint.noElevationOverride)
                                tmpGivenAlt = elevationOverride;
                            else
                                tmpGivenAlt = Double.parseDouble(tokensOfGivenCoords[2]);

                            if (detectedAltitudeMode == GeoPoint.relToSeaLevelAltitude) {
                                tmpGivenAbsAltForGound = Double.parseDouble(tokensOfGivenCoords[3]);
                            }
                            break;
                        }
                        case GeoPoint.tokenOrderLonLatAlt: {
                            tmpGivenLong = Double.parseDouble(tokensOfGivenCoords[0]);
                            tmpGivenLat = Double.parseDouble(tokensOfGivenCoords[1]);
                            if (elevationOverride != GeoPoint.noElevationOverride)
                                tmpGivenAlt = elevationOverride;
                            else
                                tmpGivenAlt = Double.parseDouble(tokensOfGivenCoords[2]);

                            if (detectedAltitudeMode == GeoPoint.relToSeaLevelAltitude) {
                                tmpGivenAbsAltForGound = Double.parseDouble(tokensOfGivenCoords[3]);
                            }
                            break;
                        }
                        case GeoPoint.tokenOrderUnknown: {
                            validGivenCoords = false;
                        }
                    }
                }
                catch (java.lang.NumberFormatException e1) {
                    validGivenCoords = false;
                }

                if (validGivenCoords &&
                        !Double.isNaN(tmpGivenLat) &&
                        !Double.isNaN(tmpGivenLong) &&
                        !Double.isNaN(tmpGivenAlt) &&
                        !Double.isNaN(tmpGivenAbsAltForGound)) {
                    switch (detectedAltitudeMode) {
                        case GeoPoint.relToGroundAltitude: {
                            thePointToReturn = new GeoPoint(tmpGivenLat, tmpGivenLong, tmpGivenAlt);
                            break;
                        }
                        case GeoPoint.relToSeaLevelAltitude: {
                            thePointToReturn = new GeoPoint(tmpGivenLat, tmpGivenLong, tmpGivenAlt, tmpGivenAbsAltForGound);
                            break;
                        }
                    }
                }
            }
        }
        return thePointToReturn;
    }

    public String toStringGeodesicCoords(int tokenOrderMode, String seperatorString) {
        String theStringToReturn = "";
        if (seperatorString != null && seperatorString != "") {
            switch (tokenOrderMode) {
                case GeoPoint.tokenOrderLatLonAlt: {
                    theStringToReturn = Double.toString(this.getLatitude()) + seperatorString +
                            Double.toString(this.getLongitude()) + seperatorString;
                    if (this.getAbsAltitude() == GeoPoint.absAltUndefined) {
                        theStringToReturn += Double.toString(this.getAltitude());
                    } else {
                        theStringToReturn += Double.toString(this.getAbsAltitude()) + seperatorString +
                                Double.toString(this.getAbsAltitude() - this.getAltitude()); // the abs altitude for ground
                    }
                    break;
                }
                case GeoPoint.tokenOrderLonLatAlt: {
                    theStringToReturn = Double.toString(this.getLongitude()) + seperatorString +
                            Double.toString(this.getLatitude()) + seperatorString;
                    if (this.getAbsAltitude() == GeoPoint.absAltUndefined) {
                        theStringToReturn += Double.toString(this.getAltitude());
                    } else {
                        theStringToReturn += Double.toString(this.getAbsAltitude()) + seperatorString +
                                Double.toString(this.getAbsAltitude() - this.getAltitude()); // the abs altitude for ground
                    }
                    break;
                }
                case GeoPoint.tokenOrderUnknown: {
                    break;
                }
            }
        }
        return theStringToReturn;
    }


    /**
     * Returns true if the points' latitude, longitude, and relative to ground altitude match. Also BOTH points should be valid.
     *
     * @return True if points are equal, and false else.
     */
    public boolean equals(GeoPoint targGeoPoint) {
        return targGeoPoint.isValidPoint() && this.isValidPoint() &&
                this.getLatitude() == targGeoPoint.getLatitude() &&
                this.getLongitude() == targGeoPoint.getLongitude() &&
                this.getAltitude() == targGeoPoint.getAltitude();
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
            result = prime * result + ((strBld.toString() == null || strBld.toString().trim().isEmpty()) ? 0 : strBld.toString().hashCode());
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
        GeoPoint other = (GeoPoint) obj;
        return this.equals(other);
    }

}
