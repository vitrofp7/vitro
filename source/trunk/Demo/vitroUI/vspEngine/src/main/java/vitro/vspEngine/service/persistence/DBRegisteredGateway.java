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

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 */
@Entity
@Table(name="registeredgateway")
public class DBRegisteredGateway {
    @Id
    private int idregisteredGateway; //  auto incremented field. (TODO: it's not defined here as auto-inc. is it in the schema?)
    private String registeredName;          // this is the "ID" against which the gateway is admitted in the framework.  e.g. "vitro_cti"
    private String friendlyName;            // a short friendly field for the gateway name
    private String friendlyDescription;     // a more verbose field describing the gateway
    private String ip;
    private String listeningport;
    private int lastadvtimestamp;
    @Transient
    private String lastDate;
    @Column(nullable = false, columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean  disabled;      // this is the flag to monitor if a gw is disabled

    public DBRegisteredGateway(){
        this.idregisteredGateway = -1;
        this.registeredName = "";
        this.friendlyName = "";
        this.friendlyDescription = "";
        this.ip = "";
        this.listeningport = "";
        this.lastadvtimestamp = 0;
        this.lastDate = "";
        this.disabled = false;
    }

    public DBRegisteredGateway(int pIdRegisteredGateway, String pRegisteredName, String pFriendlyName, String pFriendlyDescription, String pIp, String pListeningport, int pLastadvtimestamp, String pLastDate, Boolean pdisabled)
    {
        idregisteredGateway = pIdRegisteredGateway ;
        registeredName = pRegisteredName;
        friendlyName = pFriendlyName;
        friendlyDescription=pFriendlyDescription;
        ip = pIp;
        listeningport = pListeningport;
        lastadvtimestamp = pLastadvtimestamp;
        lastDate = pLastDate;
        disabled = pdisabled;
    }


    public int getIdregisteredGateway() {
        return idregisteredGateway;
    }

    public String getRegisteredName() {
        return registeredName;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public String getFriendlyDescription() {
        return friendlyDescription;
    }

    public String getIp() {
        return ip;
    }

    public String getListeningport() {
        return listeningport;
    }

    public long getLastadvtimestamp() {
        return lastadvtimestamp;
    }

    public String getLastDate() {
        return lastDate;
    }

    public Boolean getStatus() {
        return disabled;
    }

    public void setStatus(boolean status)
    {
        if (status == true)
            this.disabled = false;
        else this.disabled =true;

    }
}
