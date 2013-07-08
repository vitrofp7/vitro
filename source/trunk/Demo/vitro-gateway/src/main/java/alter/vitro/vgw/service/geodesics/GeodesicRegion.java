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
package alter.vitro.vgw.service.geodesics;

import java.util.List;

/**
 * User: antoniou
 */
abstract public class GeodesicRegion {
    public static final int typeCircularRegion = 0;
    public static final int typeSimpleRectRegion = 1;
    public static final int typeUnknownRegion = -1;

    public static final String regionTypeTag = "type";

    int typeMode;

    /**
     * Creates a new instance of GeodesicRegion
     */
    protected GeodesicRegion() {
        typeMode = typeUnknownRegion;
    }

    abstract public boolean containsPoint(GeodesicPoint p1);

    abstract public boolean overlapsWithRegion(GeodesicRegion targ2);

    abstract public boolean containsEntireRegion(GeodesicRegion targ2);

    abstract public boolean equals(GeodesicRegion targReg);

    abstract public String printInfo();

    public void setTypeMode(int typeMode) {
        this.typeMode = typeMode;
    }

    public int getTypeMode() {
        return typeMode;
    }

    abstract public List<GeodesicPoint> placePointsInInscribedCircle(int numOfPoints);
}
