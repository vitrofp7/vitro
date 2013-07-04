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
package vitro.vspEngine.service.persistence;

import javax.persistence.*;

/**
 * Region Selection supercedes gateway selection and specific sensor selection (it is a super-set).
 * This class stores region selection for service definition.
 * If a region is selected for a service, it should be resolved dynamically on runtime (every time or with a more intelligent resolution mechanism)
 * in order to be updated of new or dead devices and WSIs within its borders.
 * A region has a type.
 * For now, only rectangular regions are supported (so if the user shows a free hand area in the map it can't be supported. We could keep the top-left most and bottom-right most
 * points and calculate a rectangular "approximation".
 * Circular regions could also be supported.
 */
@Entity
public class DBRegionSelection {

    // for rectangle, the firstPointLat, firstPointLong, firstPointAlt, is for the top-left point
    //                 and the secondPointLat  ...   is the bottom-right point
    // for circle the irstPointLat, firstPointLong, firstPointAlt, is the circle center. The secondPoints are identical to the first and the extraVar is the radius
    //

    public enum RegionType {
        RECTANGULAR, CIRCULAR;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;

    @Column(name="regionType")
    @Enumerated(EnumType.ORDINAL)
    private RegionType regionType;

    private double  firstPointLat;
    private double  firstPointLong;
    private double  firstPointAlt;

    private double  secondPointLat;
    private double  secondPointLong;
    private double  secondPointAlt;

    private double extraVar;

    public DBRegionSelection(){
        setRegionType(RegionType.RECTANGULAR);
        setFirstPointLat(0);
        setFirstPointLong(0);
        setFirstPointAlt(0);
        setSecondPointLat(0);
        setSecondPointLong(0);
        setSecondPointAlt(0);
        setExtraVar(0);
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public void setRegionType(RegionType regionType) {
        this.regionType = regionType;
    }

    public double getFirstPointLat() {
        return firstPointLat;
    }

    public void setFirstPointLat(double firstPointLat) {
        this.firstPointLat = firstPointLat;
    }

    public double getFirstPointLong() {
        return firstPointLong;
    }

    public void setFirstPointLong(double firstPointLong) {
        this.firstPointLong = firstPointLong;
    }

    public double getFirstPointAlt() {
        return firstPointAlt;
    }

    public void setFirstPointAlt(double firstPointAlt) {
        this.firstPointAlt = firstPointAlt;
    }

    public double getSecondPointLat() {
        return secondPointLat;
    }

    public void setSecondPointLat(double secondPointLat) {
        this.secondPointLat = secondPointLat;
    }

    public double getSecondPointLong() {
        return secondPointLong;
    }

    public void setSecondPointLong(double secondPointLong) {
        this.secondPointLong = secondPointLong;
    }

    public double getSecondPointAlt() {
        return secondPointAlt;
    }

    public void setSecondPointAlt(double secondPointAlt) {
        this.secondPointAlt = secondPointAlt;
    }

    public double getExtraVar() {
        return extraVar;
    }

    public void setExtraVar(double extraVar) {
        this.extraVar = extraVar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
