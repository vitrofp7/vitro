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
package vitro.vspEngine.service.common.abstractservice.model;

import org.hibernate.annotations.Type;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;

import javax.persistence.*;
import java.util.Date;

/**
 * This class stores sets of smart nodes that are considered equivalent and therefore interchangeable for service continuation; whene one sensor in the set fails, any other in that set can take its place.
 * This set of sensors is updated from the UI.
 * The equivalency is independent of specific services or capabilities. (the framework should detect, however, if a node set as equivalent with another node, can't measure temperature (for example), whereas the other one can.
 * The equivalency is a "suggestion" of inter-changeability but the gateway
 * When a service uses the "service continuation" flag, the the VGW should receive in the incoming query, any vectors of equivalency related to the gateway (OR more efficiently) only those vectors of equivalency that include nodes participating in the query/service
 * TODO Adding a new set of equivalent nodes should expand an existing entry if a sensor of the new set already belongs to an equivalency entry. (This could also be done in post-processing, albeit the DB will be cluttered!
 * TODO (CONSIDER) the VGW could also have a way to auto-detect equivalent sensors, but this practically can't work efficiently.
 */
@Entity
public class SetOfEquivalentSensorNodes {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Version
    private int version;

    private String vgwId; // added for easy recovery (todo - ideally we could also link to the Reg Vitro gateways, or retrieve the Id from the selection of nodes!

    // todo: SHOULD WE REMOVE THE TYPE MERGE? THEN WE WOULD HAVE TO SAVE THE DB-NODE-SELECTION FIRST BEFORE STORING THE SET_OF_EQUIV_NODES!
    @OneToOne(cascade = { CascadeType.MERGE})
    private
    DBSelectionOfSmartNodes interchngblNodes;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestampUpdateLocal;      // when the insertion/update was made to the DB and the request was sent to the VGW.

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestampSynchedRemotely;   // when the confirmation was received from the VGW

    @Type(type="boolean")
    private boolean markedTobeDeleted;     //marked to be deleted. Will be deleted on next synch confirm from VGW!


    public SetOfEquivalentSensorNodes() {
        interchngblNodes = new DBSelectionOfSmartNodes();
        timestampUpdateLocal = new Date();
        timestampSynchedRemotely = null;
        setMarkedTobeDeleted(false);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public DBSelectionOfSmartNodes getInterchngblNodes() {
        return interchngblNodes;
    }

    public void setInterchngblNodes(DBSelectionOfSmartNodes interchngblNodes) {
        this.interchngblNodes = interchngblNodes;
    }

    public Date getTimestampUpdateLocal() {
        return timestampUpdateLocal;
    }

    public void setTimestampUpdateLocal(Date timestampUpdateLocal) {
        this.timestampUpdateLocal = timestampUpdateLocal;
    }

    public Date getTimestampSynchedRemotely() {
        return timestampSynchedRemotely;
    }

    public void setTimestampSynchedRemotely(Date timestampSynchedRemotely) {
        this.timestampSynchedRemotely = timestampSynchedRemotely;
    }

    public String getVgwId() {
        return vgwId;
    }

    public void setVgwId(String vgwId) {
        this.vgwId = vgwId;
    }

    public boolean isMarkedTobeDeleted() {
        return markedTobeDeleted;
    }

    public void setMarkedTobeDeleted(boolean markedTobeDeleted) {
        this.markedTobeDeleted = markedTobeDeleted;
    }
}
