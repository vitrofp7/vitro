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
 * GeodesicSimpleRectRegion.java
 *
 */

package vitro.vspEngine.service.geo;

import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.service.query.QueryContentDefinition;

import java.util.Enumeration;
import java.util.Vector;
/**
 * @author antoniou
 */
public class GeoSimpleRectRegion extends GeoRegion {
    double length; // in kms
    double width; // the smaller of the two sizes in kms
    GeoPoint centerPoint;
    double lengthSideTilt; // the azimuth of the length side relative to the North. Tilt should be larger than 0 and smaller than 360

    /**
     * Creates a new instance of GeoSimpleRectRegion
     * <p/>
     * <p/>
     * givenLength and givenWidth should be in kilometers.
     */
    public GeoSimpleRectRegion(GeoPoint givenCenterPoint, double givenLength, double givenWidth, double givenTilt) {
        super();
        this.setTypeMode(GeoRegion.typeSimpleRectRegion);
        length = givenLength;
        width = givenWidth;
        centerPoint = givenCenterPoint;
        lengthSideTilt = GeoCalculus.handleGivenAngleDeg(givenTilt);
    }

    private static final String rectTiltTag = "tilt";
    private static final String rectCenterTag = "centerPoint";
    private static final String rectLengthTag = "length";
    private static final String rectWidthTag = "width";

    private static final int invalidTilt = -1;
    private static final int invalidLength = -1;
    private static final int invalidWidth = -1;

    /**
     * Get a SimpleRectangular area from a startPoint and end point and a tilt that define this area
     */
    public GeoSimpleRectRegion(GeoPoint startPoint, GeoPoint endPoint, double givenTilt) {
        super();
        this.setTypeMode(GeoRegion.typeSimpleRectRegion);
        //
        // we should calculate the center, length and width
        //
        double azimuthOfConnectingDiagonal = GeoCalculus.approxGCAzimuth(startPoint, endPoint);
        double diagonalLength = GeoCalculus.ellipsoidDistance(startPoint, endPoint); // in kms
        this.centerPoint = GeoCalculus.GCDistanceAzimuth(startPoint, 0.5 * diagonalLength, azimuthOfConnectingDiagonal);
        lengthSideTilt = GeoCalculus.handleGivenAngleDeg(givenTilt);
        double refAzimuthDeg = GeoCalculus.handleGivenAngleDeg(azimuthOfConnectingDiagonal - givenTilt);
        double refAzimuthRad = Math.toRadians(refAzimuthDeg);
        this.length = diagonalLength * Math.cos(refAzimuthRad);
        this.width = diagonalLength * Math.sin(refAzimuthRad);
    }

    /**
     * Creates a new instance of GeoSimpleRectRegion
     *
     * @param givenCursor the XML part of a query (As a TextElement) that describes the Geodesic Simple Rectangular Region(
     */
    public GeoSimpleRectRegion(SMInputCursor givenCursor) {
        super();
        this.setTypeMode(GeoRegion.typeUnknownRegion);
        this.centerPoint = new GeoPoint();
        this.length = GeoSimpleRectRegion.invalidLength;
        this.lengthSideTilt = GeoSimpleRectRegion.invalidTilt;
        this.width = GeoSimpleRectRegion.invalidWidth;

         try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(GeoSimpleRectRegion.rectCenterTag.toLowerCase() ))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.centerPoint = GeoPoint.parseStringGeodesicCoords(childInElement2.getText(), GeoPoint.tokenOrderLatLonAlt, ",", GeoPoint.noElevationOverride);
                                break;
                            }
                        }
                    }else if (childInElement.getLocalName().toLowerCase().equals(GeoSimpleRectRegion.rectLengthTag.toLowerCase())) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.length = Double.parseDouble(childInElement2.getText());
                                break;
                            }
                        }
                    } else if (childInElement.getLocalName().toLowerCase().equals(GeoSimpleRectRegion.rectTiltTag.toLowerCase())) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.lengthSideTilt = Double.parseDouble(childInElement2.getText());
                                break;
                            }
                        }

                    } else if (childInElement.getLocalName().toLowerCase().equals(GeoSimpleRectRegion.rectWidthTag.toLowerCase())) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.width = Double.parseDouble(childInElement2.getText());
                                break;
                            }
                        }
                    }
                }
            }
             if (this.length != GeoSimpleRectRegion.invalidLength &&
                     this.lengthSideTilt != GeoSimpleRectRegion.invalidTilt &&
                     this.width != GeoSimpleRectRegion.invalidWidth &&
                     this.centerPoint.isValidPoint()) {
                 this.setTypeMode(GeoRegion.typeSimpleRectRegion);
             }
         }
         catch(Exception e) {
             return; // the default (though invalid) values are already set.
         }
    }

    /**
     * Returns the four vertex points for the rectangle, in clockwise order.
     */
    public GeoPoint[] getVertexPointArray() {
        GeoPoint[] arrayToReturn = new GeoPoint[4];
        double halfRectDiagonalLength = 0.5 * Math.sqrt(Math.pow(this.getLength(), 2) + Math.pow(this.getWidth(), 2));
        double refAzimuth = 0.0; // in degrees
        double refAzimuthCos = this.getLength() / Math.sqrt(Math.pow(this.getLength(), 2) + Math.pow(this.getWidth(), 2));
        // we know that refAzimuth is between 0 and PI/2
        double refAzimuth_ACos = Math.acos(refAzimuthCos);// acos always returns arc inside 0 through  pi
        refAzimuth = Math.toDegrees(refAzimuth_ACos);

        for (int i = 0; i < 4; i++) {
            arrayToReturn[i] = GeoCalculus.GCDistanceAzimuth(this.getCenterPoint(), halfRectDiagonalLength, GeoCalculus.handleGivenAngleDeg(this.getLengthSideTilt() + (i < 2 ? 0 : 180) + (Math.IEEEremainder(i, 2) * 180 + (Math.pow(-1, Math.IEEEremainder(i, 2)) * refAzimuth))));
        }
        return arrayToReturn;
    }

    /**
     * Returns true if a point belongs inside the rectangular area.
     */
    public boolean containsPoint(GeoPoint targetPoint) {
        // A point belongs inside the rectangle if the corners defined by the point and the vectors toward each of the 
        // edges, all calculated clockwise (or all counterclockwise), are never larger than PI. If a corner is found larger than PI then
        // the point is outside the rectangle.
        GeoPoint[] tmpVertexArray = this.getVertexPointArray();
        double refAngleDeg = 0.0;
        for (int i = 0; i < 4; i++) {
            refAngleDeg = GeoCalculus.approxGCAzimuth(targetPoint, tmpVertexArray[i]);
            double newAngleDiffDeg = GeoCalculus.handleGivenAngleDeg((refAngleDeg - GeoCalculus.approxGCAzimuth(targetPoint, tmpVertexArray[(int) Math.IEEEremainder(i + 1, 4)])));
            if (newAngleDiffDeg > 180) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     */
    public boolean overlapsWithRegion(GeoRegion targ2) {
        boolean overlapsFlag = false;
        switch (targ2.getTypeMode()) {
            case GeoRegion.typeCircularRegion: {
                /**
                 * For circular VS rectangle collisions we will ONLY MAKE an approximation
                 * we calculate the Circumscribed Square of the circle (the circle is escribed in the square)
                 * we tilt the square to be parallel to the defined region and then check if the square and the rectangle collide.
                 */
                GeoCircularRegion circTarg2 = (GeoCircularRegion) targ2;
                double sideSizeForSquare = 2 * circTarg2.getRadious(); // in kms
                overlapsFlag = this.overlapsWithRegion(new GeoSimpleRectRegion(circTarg2.getCirclularCenter(), sideSizeForSquare, sideSizeForSquare, this.getLengthSideTilt()));
                break;
            }
            case GeoRegion.typeSimpleRectRegion: {
                /**
                 * For rectangle VS rectangle collision is detected IF the source rectangle contains at least a vertex of the target rectangle
                 * or vice versa.
                 * OR we have a case of "CROSS" overlapping (2 cases actually), where they overlap, but no vertices are contained by any of the two rectangles.
                 * OR we have a case of "STAR" overlapping. Again here no vertices are contained by any of the two rectangles.
                 * For the CROSS and STAR cases, to simplify things we rotate both ractangles by x, so as the source (this) rectangle has 0 tilt (its edges are aligned with parallels and meridian)
                 */
                GeoSimpleRectRegion simpleRectTarg2 = (GeoSimpleRectRegion) targ2;
                GeoPoint[] targAllRctVertices = simpleRectTarg2.getVertexPointArray();
                GeoPoint[] myAllRctVertices = this.getVertexPointArray();
                overlapsFlag = false;
                for (int i = 0; i < 4; i++) {
                    if (this.containsPoint(targAllRctVertices[i])) {
                        overlapsFlag = true;
                        break;
                    }
                    if (simpleRectTarg2.containsPoint(myAllRctVertices[i])) {
                        overlapsFlag = true;
                        break;
                    }
                }
                //
                // if still false prepare to check for CROSS and STAR cases. Rotate rectangles so that the (this)
                // rectangle has 0 tilt.
                //
                GeoSimpleRectRegion tiltedThisRect;
                GeoSimpleRectRegion tiltedTargRect;
                if (!overlapsFlag) {
                    double rotateByAngle = GeoCalculus.handleGivenAngleDeg(360 - this.getLengthSideTilt()); // tilt is already smaller than 360 so no handleGivenAngleDeg is needed. but nonetheless...
                    tiltedThisRect = new GeoSimpleRectRegion(this.getCenterPoint(), this.getLength(), this.getWidth(), rotateByAngle);
                    tiltedTargRect = new GeoSimpleRectRegion(simpleRectTarg2.getCenterPoint(), simpleRectTarg2.getLength(), simpleRectTarg2.getWidth(), rotateByAngle);
                    // find top-left point and opposite bottom-right point for tiltedThisRect. We will use them as reference in the folowing cases:
                    GeoPoint[] refVertices = tiltedThisRect.getVertexPointArray();
                    GeoPoint[] targVertices = tiltedTargRect.getVertexPointArray();
                    double refTopLeftLat = 0.0; // should be the highest found
                    double refTopLeftLon = 0.0; // should be the lowest found
                    double refBottomRightLat = 0.0; // should be the lowest found
                    double refBottomRightLon = 0.0; // should be the highest found
                    for (int i = 0; i < 4; i++) {
                        if (i == 0) { // initialisation
                            refTopLeftLat = refVertices[i].getLatitude();
                            refTopLeftLon = refVertices[i].getLongitude();
                            refBottomRightLat = refVertices[i].getLatitude();
                            refBottomRightLon = refVertices[i].getLongitude();
                        } else {
                            refTopLeftLat = refTopLeftLat >= refVertices[i].getLatitude() ? refTopLeftLat : refVertices[i].getLatitude();
                            refTopLeftLon = refTopLeftLon <= refVertices[i].getLongitude() ? refTopLeftLon : refVertices[i].getLongitude();
                            refBottomRightLat = refBottomRightLat <= refVertices[i].getLatitude() ? refBottomRightLat : refVertices[i].getLatitude();
                            refBottomRightLon = refBottomRightLon >= refVertices[i].getLongitude() ? refBottomRightLon : refVertices[i].getLongitude();
                        }
                    }
                    //
                    // Check for CROSS case 1:
                    // 1. All targRect vertices should have latitude inside the reference latitudes.
                    // 2. And 2 consecutive vertices should have longitude smaller than the topLeftLon
                    //    and the 2 remaining should have longitude larger than the bottomRightLon
                    //
                    for (int i = 0; i < 4; i++) {
                        int iplusOne = (int) Math.IEEEremainder(i + 1, 4);
                        int iplusTwo = (int) Math.IEEEremainder(i + 2, 4);
                        int iplusThree = (int) Math.IEEEremainder(i + 3, 4);
                        if (targVertices[i].getLatitude() <= refTopLeftLat && targVertices[i].getLatitude() >= refBottomRightLat &&
                                targVertices[iplusOne].getLatitude() <= refTopLeftLat && targVertices[iplusOne].getLatitude() >= refBottomRightLat &&
                                targVertices[iplusTwo].getLatitude() <= refTopLeftLat && targVertices[iplusTwo].getLatitude() >= refBottomRightLat &&
                                targVertices[iplusThree].getLatitude() <= refTopLeftLat && targVertices[iplusThree].getLatitude() >= refBottomRightLat &&
                                targVertices[i].getLongitude() <= refTopLeftLon && targVertices[iplusOne].getLongitude() <= refTopLeftLon &&
                                targVertices[iplusTwo].getLongitude() >= refBottomRightLon && targVertices[iplusThree].getLongitude() >= refBottomRightLon) {
                            overlapsFlag = true;
                            break;
                        }
                    }
                    //
                    // if still false then check for CROSS case 2:
                    // 1. all targRect vertices should have longitude inside the reference longitudes.
                    // 2. And 2 consecutive vertices should have latitude larger than the topLeftLat
                    //    and the 2 remaining should have latitude smaller than the bottomRightLat
                    if (!overlapsFlag) {
                        for (int i = 0; i < 4; i++) {
                            int iplusOne = (int) Math.IEEEremainder(i + 1, 4);
                            int iplusTwo = (int) Math.IEEEremainder(i + 2, 4);
                            int iplusThree = (int) Math.IEEEremainder(i + 3, 4);
                            if (targVertices[i].getLongitude() >= refTopLeftLon && targVertices[i].getLongitude() <= refBottomRightLon &&
                                    targVertices[iplusOne].getLongitude() >= refTopLeftLon && targVertices[iplusOne].getLongitude() <= refBottomRightLon &&
                                    targVertices[iplusTwo].getLongitude() >= refTopLeftLon && targVertices[iplusTwo].getLongitude() <= refBottomRightLon &&
                                    targVertices[iplusThree].getLongitude() >= refTopLeftLon && targVertices[iplusThree].getLongitude() <= refBottomRightLon &&
                                    targVertices[i].getLatitude() >= refTopLeftLat && targVertices[iplusOne].getLatitude() >= refTopLeftLat &&
                                    targVertices[iplusTwo].getLatitude() <= refBottomRightLat && targVertices[iplusThree].getLatitude() <= refBottomRightLat) {
                                overlapsFlag = true;
                                break;
                            }
                        }
                        //
                        // if still false then check for STAR case 1:
                        // (there should be a sequence of all four vertices where:
                        // vertex 0(in row): should have latitude between ref latitudes and longitude smaller than topLeftLon
                        // vertex 1(in row): should have longitude between ref longitudes and latitude larger than topLeftLat
                        // vertex 2(in row): should have latitude between ref latitudes and longitude larger than bottomRightLon
                        // vertex 3(in row): should have longitude between ref longitudes and latitude  smaller than bottomRightLat
                        if (!overlapsFlag) {
                            for (int i = 0; i < 4; i++) {
                                int iplusOne = (int) Math.IEEEremainder(i + 1, 4);
                                int iplusTwo = (int) Math.IEEEremainder(i + 2, 4);
                                int iplusThree = (int) Math.IEEEremainder(i + 3, 4);
                                if (targVertices[i].getLatitude() <= refTopLeftLat && targVertices[i].getLatitude() >= refBottomRightLat &&
                                        targVertices[i].getLongitude() <= refTopLeftLon &&
                                        targVertices[iplusTwo].getLatitude() <= refTopLeftLat && targVertices[iplusTwo].getLatitude() >= refBottomRightLat &&
                                        targVertices[iplusTwo].getLongitude() >= refBottomRightLon &&
                                        targVertices[iplusOne].getLongitude() >= refTopLeftLon && targVertices[iplusOne].getLongitude() <= refBottomRightLon &&
                                        targVertices[iplusOne].getLatitude() >= refTopLeftLat &&
                                        targVertices[iplusThree].getLongitude() >= refTopLeftLon && targVertices[iplusThree].getLongitude() <= refBottomRightLon &&
                                        targVertices[iplusThree].getLatitude() <= refBottomRightLat) {
                                    overlapsFlag = true;
                                    break;
                                }
                            }
                            //
                            // if still false then check for STAR case 2 (same as one but going counterclockwise just in case):
                            // (there should be a sequence of all four vertices where:
                            // vertex 1(in row): should have latitude between ref latitudes and longitude smaller than topLeftLong
                            // vertex 2(in row): should have longitude between ref longitudes and latitude smaller than bottomRightLat
                            // vertex 3(in row): should have latitude between ref latitudes and longitude larger than bottomRightLon
                            // vertex 4(in row): should have longitude between ref longitudes and latitude larger than topLeftLat
                            if (!overlapsFlag) {
                                for (int i = 0; i < 4; i++) {
                                    int iplusOne = (int) Math.IEEEremainder(i + 1, 4);
                                    int iplusTwo = (int) Math.IEEEremainder(i + 2, 4);
                                    int iplusThree = (int) Math.IEEEremainder(i + 3, 4);
                                    if (targVertices[i].getLatitude() <= refTopLeftLat && targVertices[i].getLatitude() >= refBottomRightLat &&
                                            targVertices[i].getLongitude() <= refTopLeftLon &&
                                            targVertices[iplusTwo].getLatitude() <= refTopLeftLat && targVertices[iplusTwo].getLatitude() >= refBottomRightLat &&
                                            targVertices[iplusTwo].getLongitude() >= refBottomRightLon &&
                                            targVertices[iplusOne].getLongitude() >= refTopLeftLon && targVertices[iplusOne].getLongitude() <= refBottomRightLon &&
                                            targVertices[iplusOne].getLatitude() <= refBottomRightLat &&
                                            targVertices[iplusThree].getLongitude() >= refTopLeftLon && targVertices[iplusThree].getLongitude() <= refBottomRightLon &&
                                            targVertices[iplusThree].getLatitude() >= refTopLeftLat) {
                                        overlapsFlag = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            }
        }
        return overlapsFlag;
    }

    /**
     * Returns true if the current region contains the target region entirely
     */
    public boolean containsEntireRegion(GeoRegion targ2) {
        boolean containsFlag = false;
        switch (targ2.getTypeMode()) {
            case GeoRegion.typeCircularRegion: {
                //
                // for circle the rectangle should contain the entire tilted Circumscribed Square (i.e. for which the circle is escribed) (tilt is used so as the square's sides are parallel to the rectangle's sides)
                //
                GeoCircularRegion circTarg2 = (GeoCircularRegion) targ2;
                double sideSizeForSquare = 2 * circTarg2.getRadious(); // in kms
                containsFlag = this.containsEntireRegion(new GeoSimpleRectRegion(circTarg2.getCirclularCenter(), sideSizeForSquare, sideSizeForSquare, this.getLengthSideTilt()));
                break;
            }
            case GeoRegion.typeSimpleRectRegion: {
                //
                // for rectangle the rectangle should contain all four end points of the target rectangle
                //
                GeoSimpleRectRegion simpleRectTarg2 = (GeoSimpleRectRegion) targ2;
                GeoPoint[] tmpAllRctVertices = simpleRectTarg2.getVertexPointArray();
                containsFlag = true;
                for (int i = 0; i < 4; i++) {
                    if (!this.containsPoint(tmpAllRctVertices[i])) {
                        containsFlag = false;
                        break;
                    }
                }
                break;
            }
        }
        return containsFlag;
    }

    /**
     * Returns a Vector of Geodesic Points on the circumference of the inscribed circle (minus some 0.6 meters from the real radius
     * to avoid collisions with "room walls")
     * (To do) Add code here
     */
    public Vector<GeoPoint> placePointsInInscribedCircle(int numOfPoints) {
        //
        // the insribed circle will have radius : width/2 - 0.6 (if w/2 is larger than 0.6, else just 0.6)
        // Someone should make should that the rooms are never smaller than 0.6 meters wide (lol).
        //
        double inscRadius = 0.6 / 1000;
        if ((this.width / 2) - (0.6 / 1000) > 0) {
            inscRadius = (this.width / 2) - (0.6 / 1000);
        }
        Vector<GeoPoint> toReturnVec = GeoPoint.placePointsOnACircle(this.getCenterPoint(), inscRadius, numOfPoints);
        return toReturnVec;
    }


    /**
     * Returns true if two regions are of the same type and exactly the same.
     * (To do ) add code for leaving an error margin in equality (becuase a rectangle can be defined also
     * and maybe more often by giving two points (start and end point) from which the length, width and center
     * are calculated (probably with some distibuted error in the calculations). So just put in "loose equalities"
     * using some fixed accepted error value. This should happen only in rectangular equality.
     */
    public boolean equals(GeoRegion targReg) {
        if (targReg.getTypeMode() == GeoRegion.typeSimpleRectRegion) {
            // regions should be exactly equal if they have the same type, center point, tilt, width and length.
            GeoSimpleRectRegion simpleRectTarg2 = (GeoSimpleRectRegion) targReg;
            if (this.getCenterPoint().equals(simpleRectTarg2.getCenterPoint()) &&
                    this.getLength() == simpleRectTarg2.getLength() &&
                    this.getWidth() == simpleRectTarg2.getWidth() &&
                    this.getLengthSideTilt() == simpleRectTarg2.getLengthSideTilt()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates XML structured info on this GeoRegion object, under the parent Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(QueryContentDefinition.selAreaTag);
            }
            else {
                tmpElementOuter =  document.addElement(QueryContentDefinition.selAreaTag);
            }

            tmpElement1 =  tmpElementOuter.addElement(GeoRegion.regionTypeTag);
            tmpElement1.addCharacters(Integer.toString(this.getTypeMode()));

            tmpElement1 =  tmpElementOuter.addElement(GeoSimpleRectRegion.rectTiltTag);
            tmpElement1.addCharacters(Double.toString(this.getLengthSideTilt()));

            tmpElement1 =  tmpElementOuter.addElement(GeoSimpleRectRegion.rectCenterTag);
            tmpElement1.addCharacters(this.getCenterPoint().toStringGeodesicCoords(GeoPoint.tokenOrderLatLonAlt, ","));

            tmpElement1 =  tmpElementOuter.addElement(GeoSimpleRectRegion.rectLengthTag);
            tmpElement1.addCharacters(Double.toString(this.getLength()));

            tmpElement1 =  tmpElementOuter.addElement(GeoSimpleRectRegion.rectWidthTag);
            tmpElement1.addCharacters(Double.toString(this.getWidth()));

        }  catch(Exception e) {
            return;
        }
    }

    public String printInfo() { // (to add more info to the output)
        return "Type: Rectangular\n" +
                "Center(lat/lon/alt): " + this.getCenterPoint().toStringGeodesicCoords(GeoPoint.tokenOrderLatLonAlt, "/") + "\n" +
                "Width: " + Double.toString(this.getWidth()) + "\n" +
                "Length: " + Double.toString(this.getLength()) + "\n" +
                "Tilt: " + Double.toString(this.getLengthSideTilt()) + "\n";
    }


    public GeoPoint getCenterPoint() {
        return centerPoint;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getLengthSideTilt() {
        return lengthSideTilt;
    }


}
