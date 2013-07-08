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
