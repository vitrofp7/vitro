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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Defines a set of DBRegionSelections   participating in a serviceInstance
 */
@Entity
public class DBSelectionOfRegions {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;


    @OneToMany(cascade = { CascadeType.MERGE})
    private List<DBRegionSelection> DBRegionSelectionList;

    public DBSelectionOfRegions(){
        setDBRegionSelectionList(new ArrayList<DBRegionSelection>());

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DBRegionSelection> getDBRegionSelectionList() {
        return DBRegionSelectionList;
    }

    public void setDBRegionSelectionList(List<DBRegionSelection> DBRegionSelectionList) {
        this.DBRegionSelectionList = DBRegionSelectionList;
    }
}
