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
/*
 * GeodesicCircularRegion.java
 */

package vitro.vspEngine.service.geo;

import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.service.query.QueryContentDefinition;

import java.util.Vector;


/**
 * @author antoniou
 */
public class GeoCircularRegion extends GeoRegion {

    GeoPoint circlularCenter;
    double m_radious; // in kms

    private static final String circRadiusTag = "radius";
    private static final String circCenterPointTag = "centerPoint";

    private static final int invalidRadious = -1;

    /**
     * Creates a new instance of GeoCircularRegion
     */
    public GeoCircularRegion(GeoPoint givenCirclularCenter, double givenRadious) {
        super();
        this.setTypeMode(GeoRegion.typeCircularRegion);
        this.circlularCenter = givenCirclularCenter;
        this.m_radious = givenRadious;
    }

    /**
     * Creates a new instance of GeoCircularRegion
     *
     * @param givenCursor the XML part of a query (As a TextElement) that describes the Geodesic Circular Region
     */
    public GeoCircularRegion(SMInputCursor givenCursor) {
        super();
        this.setTypeMode(GeoRegion.typeUnknownRegion);
        this.circlularCenter = new GeoPoint();
        this.m_radious = GeoCircularRegion.invalidRadious;

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(GeoCircularRegion.circCenterPointTag.toLowerCase() ))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.circlularCenter = GeoPoint.parseStringGeodesicCoords(childInElement2.getText(), GeoPoint.tokenOrderLatLonAlt, ",", GeoPoint.noElevationOverride);
                                break;
                            }
                        }
                    }  else if(childInElement.getLocalName().toLowerCase().equals(GeoCircularRegion.circRadiusTag.toLowerCase() )) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.m_radious = Double.parseDouble(childInElement2.getText());
                                break;
                            }
                        }
                    }

                }
            }
        } catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }
        if (this.m_radious != GeoCircularRegion.invalidRadious && this.circlularCenter.isValidPoint()) {
            this.setTypeMode(GeoRegion.typeCircularRegion);
        }

    }

    /**
     * Returns true if the current region contains the given point
     */
    public boolean containsPoint(GeoPoint targPoint) {
        double centerpointDistance = GeoCalculus.ellipsoidDistance(this.getCirclularCenter(), targPoint);
        if (centerpointDistance <= this.getRadious()) {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the current region overlaps somewhere with the target region.
     * <b>Careful</b> In case of a circle VA rectangle very loose approximation is used. (we check for collision with the
     * circumscribed square)
     */
    public boolean overlapsWithRegion(GeoRegion targ2) {
        boolean areOverlapping = false;
        switch (targ2.getTypeMode()) {
            case GeoRegion.typeCircularRegion: {
                GeoCircularRegion circTarg2 = (GeoCircularRegion) targ2;
                double centerpointsDistance = GeoCalculus.ellipsoidDistance(this.getCirclularCenter(), circTarg2.getCirclularCenter());
                if (centerpointsDistance < (this.getRadious() + circTarg2.getRadious())) {
                    areOverlapping = true;
                }
                break;
            }
            case GeoRegion.typeSimpleRectRegion: {
                /**
                 *
                 * Set areOverlapping flag if the circumscribed square overlaps with the target rectangle
                 */
                GeoSimpleRectRegion simpleRectTarg2 = (GeoSimpleRectRegion) targ2;
                double sideSizeForSquare = 2 * this.getRadious(); // in kms
                areOverlapping = simpleRectTarg2.overlapsWithRegion(new GeoSimpleRectRegion(this.getCirclularCenter(), sideSizeForSquare, sideSizeForSquare, simpleRectTarg2.getLengthSideTilt()));
                break;
            }
            case GeoRegion.typeUnknownRegion: {
                ;
            }
        }
        return areOverlapping;
    }

    /**
     * Returns true if the current region contains the target region entirely
     */
    public boolean containsEntireRegion(GeoRegion targ2) {
        boolean containsFlag = false;
        switch (targ2.getTypeMode()) {
            case GeoRegion.typeCircularRegion: {
                GeoCircularRegion circTarg2 = (GeoCircularRegion) targ2;
                double centerpointsDistance = GeoCalculus.ellipsoidDistance(this.getCirclularCenter(), circTarg2.getCirclularCenter());
                if (this.getRadious() > circTarg2.getRadious() + centerpointsDistance) {
                    containsFlag = true;
                }
                break;
            }
            case GeoRegion.typeSimpleRectRegion: {
                /**
                 * All of rectangles angle vertices should be inside the circle.
                 */
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
            case GeoRegion.typeUnknownRegion: {
                ;
            }
        }
        return containsFlag;
    }

    /**
     * Returns a Vector of Geodesic Points on the circumference of the inscribed circle (minus some 0.6 meters from the real radius
     * to avoid collisions with "room walls")
     * (since the region is circular the inscribed circle is identical with the region circle)
     * <p/>
     * (To do) Add code here
     */
    public Vector<GeoPoint> placePointsInInscribedCircle(int numOfPoints) {
        double inscRadius = 0.6 / 1000;
        if (this.getRadious() - (0.6 / 1000) > 0) {
            inscRadius = this.getRadious() - (0.6 / 1000);
        }
        Vector<GeoPoint> toReturnVec = GeoPoint.placePointsOnACircle(this.getCirclularCenter(), inscRadius, numOfPoints);
        return toReturnVec;

    }


    /**
     * Returns true if two regions are of the same type and exactly the same. (Same center and radious)
     */
    public boolean equals(GeoRegion targReg) {
        if (targReg.getTypeMode() == GeoRegion.typeCircularRegion) {
            GeoCircularRegion circTargReg = (GeoCircularRegion) targReg;
            if (circTargReg.getCirclularCenter().equals(this.getCirclularCenter()) &&
                    circTargReg.getRadious() == this.getRadious()) {
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

            tmpElement1 =  tmpElementOuter.addElement(GeoCircularRegion.circRadiusTag);
            tmpElement1.addCharacters(Double.toString(this.getRadious()));

            tmpElement1 =  tmpElementOuter.addElement(GeoCircularRegion.circCenterPointTag);
            tmpElement1.addCharacters(this.circlularCenter.toStringGeodesicCoords(GeoPoint.tokenOrderLatLonAlt, ","));

        }  catch(Exception e) {
            return;
        }
    }

    public String printInfo() {
        return "Type: Circular\nCenter(lat/lon/alt): " + this.getCirclularCenter().toStringGeodesicCoords(GeoPoint.tokenOrderLatLonAlt, "/") + "\nRadius: " + Double.toString(this.getRadious());
    }

    public GeoPoint getCirclularCenter() {
        return circlularCenter;
    }

    public double getRadious() {
        return m_radious;
    }

}
