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

import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.util.List;

/**
 * User: antoniou
 */
abstract public class GeoRegion {
    public static final int typeCircularRegion = 0;
    public static final int typeSimpleRectRegion = 1;
    public static final int typeUnknownRegion = -1;

    public static final String regionTypeTag = "type";

    int typeMode;

    /**
     * Creates a new instance of GeoRegion
     */
    protected GeoRegion() {
        typeMode = typeUnknownRegion;
    }

    abstract public boolean containsPoint(GeoPoint p1);

    abstract public boolean overlapsWithRegion(GeoRegion targ2);

    abstract public boolean containsEntireRegion(GeoRegion targ2);

    abstract public boolean equals(GeoRegion targReg);

    abstract public String printInfo();

    public void setTypeMode(int typeMode) {
        this.typeMode = typeMode;
    }

    public int getTypeMode() {
        return typeMode;
    }

    /**
     * Creates XML structured info on this GeoRegion object, under the parent Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document
     */
    abstract public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement);

    abstract public List<GeoPoint> placePointsInInscribedCircle(int numOfPoints);
}
