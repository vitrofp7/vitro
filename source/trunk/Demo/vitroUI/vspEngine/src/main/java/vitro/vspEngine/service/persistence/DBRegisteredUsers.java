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

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 */
//@Entity
//@Table(name="users")
public class DBRegisteredUsers {
//    @Id
    private int idusers; //  auto incremented field.
    private String loginName;          // this is the "ID" against which the gateway is admitted in the framework.  e.g. "vitro_cti"
    private String passwd;            // a short friendly field for the gateway name
    private String email;     // a more verbose field describing the gateway
    private int role;
    private String role_name;
    private int lastadvtimestamp;
//    @Transient
    private String lastDate;
//    @Column(nullable = false, columnDefinition = "TINYINT")
//    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean  disabled;      // this is the flag to monitor if a gw is disabled

    public DBRegisteredUsers(){
        this.idusers = -1;
        this.loginName = "";
        this.passwd = "";
        this.email = "";
        this.role = -1;
        this.role_name = "";
        this.lastadvtimestamp = 0;
        this.lastDate = "";
        this.disabled = false;
    }

    public DBRegisteredUsers(int pIdUser, String ploginName, String ppasswd, String pemail, int prole, String prole_name, int pLastadvtimestamp, String pLastDate, Boolean pdisabled)
    {
        idusers = pIdUser ;
        loginName = ploginName;
        passwd = ppasswd;
        email=pemail;
        role = prole;
        role_name = prole_name;
        lastadvtimestamp = pLastadvtimestamp;
        lastDate = pLastDate;
        disabled = pdisabled;
    }


    public int getIdUsers() {
        return idusers;
    }

    public String getloginName() {
        return loginName;
    }

    public String getrole_name() {
        return role_name;
    }

    public String getemailAddress() {
        return email;
    }
    public int getIdRole() {
        return role;
    }

    public void setPassword(String ppasswd) {
    	this.passwd = ppasswd;
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
