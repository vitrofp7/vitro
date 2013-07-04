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
/*
 * GeodesicsCalculus.java
 *
 */

package vitro.vspEngine.service.geo;

/**
 * @author antoniou
 */
public class GeoCalculus {

    /**
     * Creates a new instance of GeoCalculus
     * We won't be using it though, since ALL methods will be static
     */
    private GeoCalculus() {
    }

    //
    // Code taken from: http://www.codeguru.com/Cpp/Cpp/algorithms/article.php/c5115/
    //

    final static double PI = Math.PI;
    final static double DE2RA = 0.017453292519943295; // pi/180 
    final static double RA2DE = 57.295779513082320885; // 180/pi 
    final static double ERAD = 6378.137;    // km
    final static double ERADM = 6378137.0;  // km
    final static double AVG_ERAD = 6371.0;  // km
    final static double FLATTENING = 1.000000 / 298.257223563;// Earth flattening (WGS84)
    final static double EPS = 0.00000000000005;

    /**
     * Code taken from: http://www.codeguru.com/Cpp/Cpp/algorithms/article.php/c5115/
     * result is in  km
     */
    public static double ellipsoidDistance(double lat1, double lon1, double lat2, double lon2) {
        double distance = 0.0;
        double faz, baz;
        double r = 1.0 - GeoCalculus.FLATTENING;
        double tu1, tu2, cu1, su1, cu2, x, sx, cx, sy, cy, y, sa, c2a, cz, e, c, d;
        double cosy1, cosy2;
        distance = 0.0;

        if ((lon1 == lon2) && (lat1 == lat2)) return distance;
        lon1 *= GeoCalculus.DE2RA;
        lon2 *= GeoCalculus.DE2RA;
        lat1 *= GeoCalculus.DE2RA;
        lat2 *= GeoCalculus.DE2RA;

        cosy1 = Math.cos(lat1);
        cosy2 = Math.cos(lat2);

        if (cosy1 == 0.0) cosy1 = 0.0000000001;
        if (cosy2 == 0.0) cosy2 = 0.0000000001;

        tu1 = r * Math.sin(lat1) / cosy1;
        tu2 = r * Math.sin(lat2) / cosy2;
        cu1 = 1.0 / Math.sqrt(tu1 * tu1 + 1.0);
        su1 = cu1 * tu1;
        cu2 = 1.0 / Math.sqrt(tu2 * tu2 + 1.0);
        x = lon2 - lon1;

        distance = cu1 * cu2;
        baz = distance * tu2;
        faz = baz * tu1;

        do {
            sx = Math.sin(x);
            cx = Math.cos(x);
            tu1 = cu2 * sx;
            tu2 = baz - su1 * cu2 * cx;
            sy = Math.sqrt(tu1 * tu1 + tu2 * tu2);
            cy = distance * cx + faz;
            y = Math.atan2(sy, cy);
            sa = distance * sx / sy;
            c2a = -sa * sa + 1.0;
            cz = faz + faz;
            if (c2a > 0.0) cz = -cz / c2a + cy;
            e = cz * cz * 2. - 1.0;
            c = ((-3.0 * c2a + 4.0) * GeoCalculus.FLATTENING + 4.0) * c2a * GeoCalculus.FLATTENING / 16.0;
            d = x;
            x = ((e * cy * c + cz) * sy * c + y) * sa;
            x = (1.0 - c) * x * GeoCalculus.FLATTENING + lon2 - lon1;
        } while (Math.abs(d - x) > GeoCalculus.EPS);

        x = Math.sqrt((1.0 / r / r - 1.0) * c2a + 1.0) + 1.0;
        x = (x - 2.0) / x;
        c = 1.0 - x;
        c = (x * x / 4.0 + 1.0) / c;
        d = (0.375 * x * x - 1.0) * x;
        x = e * cy;
        distance = 1.0 - e - e;
        distance = ((((sy * sy * 4.0 - 3.0) *
                distance * cz * d / 6.0 - x) * d / 4.0 + cz) * sy * d + y) * c * GeoCalculus.ERAD * r;

        return distance;
    }

    public static double ellipsoidDistance(GeoPoint startPoint, GeoPoint endPoint) {
        return ellipsoidDistance(startPoint.getLatitude(), startPoint.getLongitude(), endPoint.getLatitude(), endPoint.getLongitude());
    }

    //
    // Code taken from: http://www.codeguru.com/Cpp/Cpp/algorithms/article.php/c5115/
    // 
    // ellipsoid Azimuth did not exist, so I used the 	GCAzimuth instead
    // check what rad2de is (rad to degrees??)
    // return values 0 to 360 (?) (so direction matters)
    public static double approxGCAzimuth(double lat1, double lon1, double lat2, double lon2) {
        double result = 0.0;

        int ilat1 = (int) (0.50 + lat1 * 360000.0);
        int ilat2 = (int) (0.50 + lat2 * 360000.0);
        int ilon1 = (int) (0.50 + lon1 * 360000.0);
        int ilon2 = (int) (0.50 + lon2 * 360000.0);

        lat1 *= GeoCalculus.DE2RA;
        lon1 *= GeoCalculus.DE2RA;
        lat2 *= GeoCalculus.DE2RA;
        lon2 *= GeoCalculus.DE2RA;

        if ((ilat1 == ilat2) && (ilon1 == ilon2)) {
            return result;
        } else if (ilon1 == ilon2) {
            if (ilat1 > ilat2)
                result = 180.0;
        } else {
            double c = Math.acos(Math.sin(lat2) * Math.sin(lat1) + Math.cos(lat2) * Math.cos(lat1) * Math.cos((lon2 - lon1)));
            double A = Math.asin(Math.cos(lat2) * Math.sin((lon2 - lon1)) / Math.sin(c));
            result = (A * GeoCalculus.RA2DE);

            if ((ilat2 > ilat1) && (ilon2 > ilon1)) {
            } else if ((ilat2 < ilat1) && (ilon2 < ilon1)) {
                result = 180.0 - result;
            } else if ((ilat2 < ilat1) && (ilon2 > ilon1)) {
                result = 180.0 - result;
            } else if ((ilat2 > ilat1) && (ilon2 < ilon1)) {
                result += 360.0;
            }
        }

        return result;
    }

    public static double approxGCAzimuth(GeoPoint startPoint, GeoPoint endPoint) {
        return approxGCAzimuth(startPoint.getLatitude(), startPoint.getLongitude(), endPoint.getLatitude(), endPoint.getLongitude());
    }

    /**
     * calculate point co-ordinates, given the azimuth (angle from north) and a starting point (latitude1, longitude2)
     * dist should be in kilometers
     */
    public static GeoPoint GCDistanceAzimuth(double lat1, double lon1, double dist, double az, double explicitPointHeight) {
        double b = dist / GeoCalculus.AVG_ERAD;
        double sinb = Math.sin(b);
        double cosb = Math.cos(b);
        double sinc = Math.sin(GeoCalculus.DE2RA * (90.0 - lat1));
        double cosc = Math.cos(GeoCalculus.DE2RA * (90.0 - lat1));
        double azrad = GeoCalculus.DE2RA * az;

        double a = Math.acos(cosb * cosc + sinc * sinb * Math.cos(azrad));
        double B = Math.asin(sinb * Math.sin(azrad) / Math.sin(a));

        double olat2 = GeoCalculus.RA2DE * ((GeoCalculus.PI / 2.0) - a);
        double olon2 = GeoCalculus.RA2DE * B + lon1;
        return new GeoPoint(olat2, olon2, explicitPointHeight);
    }

    /**
     * dist should be in kilometers
     */
    public static GeoPoint GCDistanceAzimuth(GeoPoint startPoint, double dist, double az) {
        return GCDistanceAzimuth(startPoint.getLatitude(), startPoint.getLongitude(), dist, az, startPoint.getAltitude());
    }


    /**
     * to check if it works
     */
    public static double handleGivenAngleDeg(double givenAngleDegrees) {
        if (givenAngleDegrees >= 0 && givenAngleDegrees >= 360) {
            int n = (int) givenAngleDegrees / 360;
            givenAngleDegrees = givenAngleDegrees - n * 360;
        } else if (givenAngleDegrees < 0) {
            int n = (int) ((-1) * givenAngleDegrees) / 360;
            givenAngleDegrees = 360 + (givenAngleDegrees - n * 360);
        }
        return givenAngleDegrees;
    }

}
