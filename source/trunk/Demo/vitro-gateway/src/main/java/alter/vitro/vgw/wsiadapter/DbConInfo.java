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
package alter.vitro.vgw.wsiadapter;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 * Date: 5/18/12
 * Time: 9:04 AM
 * To change this template use File | Settings | File Templates.
 */
public class DbConInfo {
    private String m_sDBIp;
    private String m_sDBusername;
    private String m_sDBpassword;
    private String m_sDatabaseName;
    private String m_sExtraInformation;
    private String m_connProtocol;

    DbConInfo(String mysDBIp, String mysDbName, String mysDBusername, String mysDBpasswd, String myConnProtocol, String mysExtraInfo) {
        m_sDBIp = mysDBIp;
        m_sDBusername = mysDBusername;
        m_sDBpassword = mysDBpasswd;
        m_sDatabaseName = mysDbName;
        m_connProtocol = myConnProtocol;
        m_sExtraInformation = mysExtraInfo;
    }

    //
    // Set-ers
    //
    public void setConnProtocol(String pConnProtocol) {
        this.m_connProtocol = pConnProtocol;
    }

    public void setDatabaseIp(String databaseIp) {
        this.m_sDBIp = databaseIp;
    }

    public void setDatabaseName(String databaseName) {
        this.m_sDatabaseName = databaseName;
    }

    public void setDBusername(String dBusername) {
        this.m_sDBusername = dBusername;
    }

    public void setDBpassword(String dBpassword) {
        this.m_sDBpassword = dBpassword;
    }

    public void setDBextraInfo(String dBextraInfo) {
        this.m_sExtraInformation = dBextraInfo;
    }

    //
    // Get-ers
    //
    public String getConnProtocol() {
        return m_connProtocol;
    }

    public String getDBusername() {
        return m_sDBusername;
    }

    public String getDBpassword() {
        return m_sDBpassword;
    }

    public String getDatabaseName() {
        return m_sDatabaseName;
    }

    public String getDatabaseIp() {
        return m_sDBIp;
    }

    public String getDatabaseExtraInfo() {
        return m_sExtraInformation;
    }


    //prefix protocol can be e.g. "jdbc:"
    public String getConnectionString(String prefixProtocol) {
        if(prefixProtocol!=null && prefixProtocol.compareTo("") != 0)
            return prefixProtocol + getConnProtocol() + "://" + getDatabaseIp() + "/" + getDatabaseName();
        else
            return getConnProtocol() + "://" + getDatabaseIp() + "/" + getDatabaseName();

    }

    public String toString() {
        return getConnectionString("") + "##" + getDBusername() + "##" + getDBpassword();
    }
}
