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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoPoint;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class DBCommons {

    Configuration jdbcConfig = null;
    private  String dbSchemaStr = "";
    private  String usrStr = "";
    private  String pwdStr = "";
    private  String connString = "" ;
    private  String jdbcdriverClassName = "";


    /**
     * Creates a new instance of DBCommons
     */
    private DBCommons() {
        try {
            //retrieve setting from properties file(s)
            jdbcConfig= new PropertiesConfiguration("jdbc.properties");
            if(jdbcConfig!=null)
            {
                dbSchemaStr = jdbcConfig.getString("app.jdbc.schemaName");
                usrStr= jdbcConfig.getString("app.jdbc.username");
                pwdStr = jdbcConfig.getString("app.jdbc.password");
                connString =jdbcConfig.getString("app.jdbc.url");
                jdbcdriverClassName =jdbcConfig.getString("app.jdbc.driverClassName");
            }
        }
        catch (Exception e)
        {
            jdbcConfig=null;
        }
    }

    private static DBCommons myDBCommons = null;

    /**
     * This is the function the world uses to get the DBCommons
     * It follows the Singleton pattern
     */
    public static DBCommons getDBCommons() {
        if (myDBCommons == null) {
            myDBCommons = new DBCommons();
        }
        return myDBCommons;
    }

    public Vector<DBRegisteredGateway> getRegisteredGatewayEntries()
    {
        Vector<DBRegisteredGateway>  retVect = new  Vector<DBRegisteredGateway>();
        java.sql.Connection conn = null;
        try {
            Class.forName(jdbcdriverClassName).newInstance();
            conn = DriverManager.getConnection(connString, usrStr, pwdStr);
            String echomessage = "";
            if(!conn.isClosed())
            {
                //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = conn.createStatement();
                    if (stmt.execute("SELECT idregisteredgateway, registeredName, friendlyName, friendlyDescription, ip, listeningport, lastadvtimestamp, disabled, FROM_UNIXTIME(lastadvtimestamp, \'%d/%m/%Y %H:%i:%s\') lastdate FROM `"+dbSchemaStr+"`.`registeredgateway` ")) {
                        rs = stmt.getResultSet();
                    }
                    if (rs!= null)
                    {
                        while( rs.next()) {
                            int gateId = rs.getInt("idregisteredgateway");
                            String registeredName = rs.getString("registeredName")== null? "" : rs.getString("registeredName"); // this is the one used in registration messages
                            String friendlyName =  rs.getString("friendlyName")== null? "" : rs.getString("friendlyName");
                            String friendlyDescription =  rs.getString("friendlyDescription")== null? "" : rs.getString("friendlyDescription");
                            String gateIp  = rs.getString("ip")== null? "" : rs.getString("ip");
                            String gatePort  = rs.getString("listeningport")== null? "" : rs.getString("listeningport");
                            int lastadvtimestampInt = rs.getInt("lastadvtimestamp");
                            String lastdate = rs.getString("lastdate") == null? "N/A" : rs.getString("lastdate");
                            Boolean status = rs.getBoolean("disabled");
                            if(!registeredName.isEmpty() && !registeredName.equalsIgnoreCase(""))
                            {
                                DBRegisteredGateway entryRegisterGateway = new DBRegisteredGateway(gateId, registeredName, friendlyName, friendlyDescription, gateIp, gatePort, lastadvtimestampInt, lastdate, status);
                                retVect.addElement(entryRegisterGateway);
                            }
                        }
                    }
                }
                catch (SQLException ex){
                    // handle any errors
                    System.out.println("SQLException: " + ex.getMessage());
                    System.out.println("SQLState: " + ex.getSQLState());
                    System.out.println("VendorError: " + ex.getErrorCode());
                }
                finally {
                    // it is a good idea to release
                    // resources in a finally{} block
                    // in reverse-order of their creation
                    // if they are no-longer needed
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException sqlEx) {
                            System.out.println("SQLException on rs close(): " + sqlEx.getMessage());
                            } // ignore
                        rs = null;
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqlEx) {
                            System.out.println("SQLException on stmt close(): " + sqlEx.getMessage());
                        } // ignore
                        stmt = null;
                    }
                }
            }
            else
            {
                echomessage =  "Error accessing DB server...";
            }
            System.out.println(echomessage);
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        finally {
            try {
                if(conn != null)
                    conn.close();
            }
            catch(SQLException e) {}
        }
        return retVect;
    }

    public Vector<String> getRegisteredGatewayRegNames()
    {
        Vector<String>  retVect = new  Vector<String>();
        java.sql.Connection conn = null;
        try {
            Class.forName(jdbcdriverClassName).newInstance();
            conn = DriverManager.getConnection(connString, usrStr, pwdStr);
            String echomessage = "";
            if(!conn.isClosed())
            {
                //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = conn.createStatement();
                    if (stmt.execute("SELECT registeredName FROM `"+dbSchemaStr+"`.`registeredgateway` ")) {
                        rs = stmt.getResultSet();
                    }
                    if (rs!= null)
                    {
                        while( rs.next()) {
                            String registeredName =rs.getString("registeredName")== null? "" : rs.getString("registeredName");
                            if(!registeredName.isEmpty() && !registeredName.equalsIgnoreCase(""))
                            {
                                retVect.add(registeredName);
                            }
                        }
                    }
                }
                catch (SQLException ex){
                    // handle any errors
                    System.err.println("SQLException2: " + ex.getMessage());
                    System.err.println("SQLState2: " + ex.getSQLState());
                    System.err.println("VendorError2: " + ex.getErrorCode());
                }
                finally {
                    // it is a good idea to release
                    // resources in a finally{} block
                    // in reverse-order of their creation
                    // if they are no-longer needed
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException sqlEx) { } // ignore
                        rs = null;
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqlEx) { } // ignore
                        stmt = null;
                    }
                }
            }
            else
            {
                echomessage =  "Error accessing DB server...";
            }
            System.out.println(echomessage);
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        finally {
            try {
                if(conn != null)
                    conn.close();
            }
            catch(SQLException e) {}
        }
        return retVect;
    }

    public DBRegisteredGateway getRegisteredGateway(String pGwId)
    {
        DBRegisteredGateway  retRegGw = null;
        if(pGwId!=null && !pGwId.isEmpty())
        {
            java.sql.Connection conn = null;
            try {
                Class.forName(jdbcdriverClassName).newInstance();
                conn = DriverManager.getConnection(connString, usrStr, pwdStr);
                String echomessage = "";
                if(!conn.isClosed())
                {
                    //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();
                        if (stmt.execute("SELECT idregisteredgateway, registeredName, friendlyName, friendlyDescription, ip, listeningport, lastadvtimestamp, disabled, FROM_UNIXTIME(lastadvtimestamp, \'%d/%m/%Y %H:%i:%s\') lastdate FROM `"+dbSchemaStr+"`.`registeredgateway` WHERE registeredName=\'"+pGwId + "\'")) {
                            rs = stmt.getResultSet();
                        }
                        if (rs!= null)
                        {
                            while( rs.next()) {
                                int gateId = rs.getInt("idregisteredgateway");
                                String registeredName = rs.getString("registeredName")== null? "" : rs.getString("registeredName"); // this is the one used in registration messages
                                String friendlyName =  rs.getString("friendlyName")== null? "" : rs.getString("friendlyName");
                                String friendlyDescription =  rs.getString("friendlyDescription")== null? "" : rs.getString("friendlyDescription");
                                String gateIp  = rs.getString("ip")== null? "" : rs.getString("ip");
                                String gatePort  = rs.getString("listeningport")== null? "" : rs.getString("listeningport");
                                int lastadvtimestampInt = rs.getInt("lastadvtimestamp");
                                String lastdate = rs.getString("lastdate") == null? "N/A" : rs.getString("lastdate");
                                Boolean status = rs.getBoolean("disabled");
                                if(!registeredName.isEmpty() && !registeredName.equalsIgnoreCase(""))
                                {
                                    retRegGw = new DBRegisteredGateway(gateId, registeredName, friendlyName, friendlyDescription, gateIp, gatePort, lastadvtimestampInt, lastdate, status);
                                }
                                break; // we only need one result, so break here
                            }
                        }
                    }
                    catch (SQLException ex){
                        // handle any errors
                        System.out.println("SQLException: " + ex.getMessage());
                        System.out.println("SQLState: " + ex.getSQLState());
                        System.out.println("VendorError: " + ex.getErrorCode());
                    }
                    finally {
                        // it is a good idea to release
                        // resources in a finally{} block
                        // in reverse-order of their creation
                        // if they are no-longer needed
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException sqlEx) {
                                System.out.println("SQLException on rs close(): " + sqlEx.getMessage());
                            } // ignore
                            rs = null;
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException sqlEx) {
                                System.out.println("SQLException on stmt close(): " + sqlEx.getMessage());
                            } // ignore
                            stmt = null;
                        }
                    }
                }
                else
                {
                    echomessage =  "Error accessing DB server...";
                }
                System.out.println(echomessage);
            }
            catch(Exception e) {
                System.err.println("Exception: " + e.getMessage());
            }
            finally {
                try {
                    if(conn != null)
                        conn.close();
                }
                catch(SQLException e) {}
            }
        }
        return retRegGw;
    }

    synchronized  public void deleteRegisteredGateway(String pGatewayRegisteredName)
    {
        StringBuilder tmpIgnoredOutput = new StringBuilder();
        if(isRegisteredGateway(pGatewayRegisteredName, tmpIgnoredOutput ))
        {
            java.sql.Connection conn = null;
            try {
                Class.forName(jdbcdriverClassName).newInstance();
                conn = DriverManager.getConnection(connString, usrStr, pwdStr);
                String echomessage = "";
                if(!conn.isClosed())
                {
                    //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();

                        if (stmt.execute("DELETE FROM `"+dbSchemaStr+"`.`registeredgateway` WHERE registeredName=\'"+ pGatewayRegisteredName +"\'")) {
                            rs = stmt.getResultSet();  // TODO: this is not needed here...
                        }

                    }
                    catch (SQLException ex){
                        // handle any errors
                        System.err.println("SQLException3: " + ex.getMessage());
                        System.err.println("SQLState3: " + ex.getSQLState());
                        System.err.println("VendorError3: " + ex.getErrorCode());
                    }
                    finally {
                        // it is a good idea to release
                        // resources in a finally{} block
                        // in reverse-order of their creation
                        // if they are no-longer needed
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException sqlEx) { } // ignore
                            rs = null;
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException sqlEx) { } // ignore
                            stmt = null;
                        }
                    }
                }
                else
                {
                    echomessage =  "Error accessing DB server...";
                }
            }
            catch(Exception e) {
                System.err.println("Exception: " + e.getMessage());
            }
            finally {
                try {
                    if(conn != null)
                        conn.close();
                }
                catch(SQLException e) {}
            }
        }
    }

    synchronized public void insertRegisteredGateway(String pGatewayRegisteredName, String pFriendlyName)
    {
        StringBuilder tmpIgnoredOutput = new StringBuilder();
        if(!isRegisteredGateway(pGatewayRegisteredName, tmpIgnoredOutput ))
        {
            if(pFriendlyName == null || pFriendlyName.trim().isEmpty())
            {
                pFriendlyName = "unnamed island";
            }
            java.sql.Connection conn = null;
            try {
                Class.forName(jdbcdriverClassName).newInstance();
                conn = DriverManager.getConnection(connString, usrStr, pwdStr);
                String echomessage = "";
                if(!conn.isClosed())
                {
                    //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        stmt = conn.createStatement();

                        if (stmt.execute("INSERT `"+dbSchemaStr+"`.`registeredgateway`(registeredName, friendlyName) VALUES (\'"+ pGatewayRegisteredName +"\',\'"+pFriendlyName+"\')")) {
                            rs = stmt.getResultSet();  // TODO: this is not needed here...
                        }

                    }
                    catch (SQLException ex){
                        // handle any errors
                        System.err.println("SQLException3: " + ex.getMessage());
                        System.err.println("SQLState3: " + ex.getSQLState());
                        System.err.println("VendorError3: " + ex.getErrorCode());
                    }
                    finally {
                        // it is a good idea to release
                        // resources in a finally{} block
                        // in reverse-order of their creation
                        // if they are no-longer needed
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException sqlEx) { } // ignore
                            rs = null;
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException sqlEx) { } // ignore
                            stmt = null;
                        }
                    }
                }
                else
                {
                    echomessage =  "Error accessing DB server...";
                }
            }
            catch(Exception e) {
                System.err.println("Exception: " + e.getMessage());
            }
            finally {
                try {
                    if(conn != null)
                        conn.close();
                }
                catch(SQLException e) {}
            }
        }
    }

    //TODO: to be moved in a class for DB functions
    synchronized public void updateRcvGatewayAdTimestamp(String pGatewayRegisteredName, boolean removeTimeStampFlag)
    {
        java.sql.Connection conn = null;
        try {
            Class.forName(jdbcdriverClassName).newInstance();
            conn = DriverManager.getConnection(connString, usrStr, pwdStr);
            String echomessage = "";
            if(!conn.isClosed())
            {
                //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = conn.createStatement();
                    if (!removeTimeStampFlag) {
                        if (stmt.execute("UPDATE `"+dbSchemaStr+"`.`registeredgateway` SET lastadvtimestamp = UNIX_TIMESTAMP(now())  WHERE registeredName=\'"+ pGatewayRegisteredName +"\'")) {
                            rs = stmt.getResultSet();  // TODO: this is not needed here...
                        }
                    }
                    else
                    {
                        if (stmt.execute("UPDATE `"+dbSchemaStr+"`.`registeredgateway` SET lastadvtimestamp = 0  WHERE registeredName=\'"+ pGatewayRegisteredName +"\'")) {
                            rs = stmt.getResultSet();  // TODO: this is not needed here...
                        }
                    }
                }
                catch (SQLException ex){
                    // handle any errors
                    System.err.println("SQLException3: " + ex.getMessage());
                    System.err.println("SQLState3: " + ex.getSQLState());
                    System.err.println("VendorError3: " + ex.getErrorCode());
                }
                finally {
                    // it is a good idea to release
                    // resources in a finally{} block
                    // in reverse-order of their creation
                    // if they are no-longer needed
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException sqlEx) { } // ignore
                        rs = null;
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqlEx) { } // ignore
                        stmt = null;
                    }
                }
            }
            else
            {
                echomessage =  "Error accessing DB server...";
            }
            // DEBUG
            //System.out.println(echomessage);
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        finally {
            try {
                if(conn != null)
                    conn.close();
            }
            catch(SQLException e) {}
        }
    }




    synchronized public boolean isRegisteredGateway(String pGatewayRegisteredName, StringBuilder out_descFromDB)
    {
        boolean retBool = false;
        java.sql.Connection conn = null;
        try {
            Class.forName(jdbcdriverClassName).newInstance();
            conn = DriverManager.getConnection(connString, usrStr, pwdStr);
            String echomessage = "";
            if(!conn.isClosed())
            {
                //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = conn.createStatement();
                    if (stmt.execute("SELECT idregisteredgateway, registeredName, friendlyName, friendlyDescription, ip, listeningport, FROM_UNIXTIME(lastadvtimestamp, \"%d/%m/%Y %H:%i:%s\") lastdate FROM `"+dbSchemaStr+"`.`registeredgateway` WHERE registeredName=\'"+ pGatewayRegisteredName +"\'")) {
                        rs = stmt.getResultSet();
                    }
                    if (rs!= null)
                    {
                        while( rs.next()) {
                            int gateId = rs.getInt("idregisteredgateway") ;
                            String registeredName = rs.getString("registeredName")== null? "" : rs.getString("registeredName"); // this is the one used in registration messages
                            String friendlyName =  rs.getString("friendlyName")== null? "" : rs.getString("friendlyName");
                            String friendlyDescription =  rs.getString("friendlyDescription")== null? "" : rs.getString("friendlyDescription");
                            out_descFromDB.append(friendlyName);
                            String gateIp  = rs.getString("ip")== null? "" : rs.getString("ip");
                            String gatePort  = rs.getString("listeningport")== null? "" : rs.getString("listeningport");
                            String lastdate = rs.getString("lastdate") == null? "N/A" :   rs.getString("lastdate");
                            if(registeredName.equalsIgnoreCase(pGatewayRegisteredName))
                            {
                                retBool = true;
                                break;
                            }
                        }
                    }
                }
                catch (SQLException ex){
                    // handle any errors
                    System.err.println("SQLException4: " + ex.getMessage());
                    System.err.println("SQLState4: " + ex.getSQLState());
                    System.err.println("VendorError4: " + ex.getErrorCode());
                }
                finally {
                    // it is a good idea to release
                    // resources in a finally{} block
                    // in reverse-order of their creation
                    // if they are no-longer needed
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException sqlEx) { } // ignore
                        rs = null;
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqlEx) { } // ignore
                        stmt = null;
                    }
                }
            }
            else
            {
                echomessage =  "Error accessing DB server...";
            }
            // DEBUG
            //System.out.println(echomessage);
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        finally {
            try {
                if(conn != null)
                    conn.close();
            }
            catch(SQLException e) {}
        }
        return retBool;
    }

synchronized public void updateStatus(String pGatewayRegisteredName)
	{
	java.sql.Connection conn = null;
    try {
    	String echomessage = "";
        Class.forName(jdbcdriverClassName).newInstance();
        conn = DriverManager.getConnection(connString, usrStr, pwdStr);
        if(!conn.isClosed())
        {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                if (stmt.execute("SELECT idregisteredgateway, registeredName, friendlyName, friendlyDescription, ip, listeningport, lastadvtimestamp, disabled, FROM_UNIXTIME(lastadvtimestamp, \'%d/%m/%Y %H:%i:%s\') lastdate FROM `"+dbSchemaStr+"`.`registeredgateway` ")) {
                    rs = stmt.getResultSet();
                }
                if (rs!= null)
                {
                    while( rs.next()) {
                        int gateId = rs.getInt("idregisteredgateway") ;
                        String registeredName = rs.getString("registeredName")== null? "" : rs.getString("registeredName"); // this is the one used in registration messages
                        String friendlyName =  rs.getString("friendlyName")== null? "" : rs.getString("friendlyName");
                        String friendlyDescription =  rs.getString("friendlyDescription")== null? "" : rs.getString("friendlyDescription");
                        String gateIp  = rs.getString("ip")== null? "" : rs.getString("ip");
                        String gatePort  = rs.getString("listeningport")== null? "" : rs.getString("listeningport");
                        int lastadvtimestampInt = rs.getInt("lastadvtimestamp");
                        String lastdate = rs.getString("lastdate") == null? "N/A" : rs.getString("lastdate");
                        Boolean status = rs.getBoolean("disabled");
                        if(registeredName.equalsIgnoreCase(pGatewayRegisteredName))
                        {
                        	if (status == false) {
                                if (stmt.execute("UPDATE `"+dbSchemaStr+"`.`registeredgateway` SET disabled = 1  WHERE registeredName=\'"+ pGatewayRegisteredName +"\'")) {
                                    rs = stmt.getResultSet();  // TODO: this is not needed here...
                                }
                            }
                            else
                            {
                                if (stmt.execute("UPDATE `"+dbSchemaStr+"`.`registeredgateway` SET disabled = 0  WHERE registeredName=\'"+ pGatewayRegisteredName +"\'")) {
                                    rs = stmt.getResultSet();  // TODO: this is not needed here...
                                }
                            }
                        break;
                        }
                    }
                }
                
            }
            catch (SQLException ex){
                // handle any errors
                System.err.println("SQLException3: " + ex.getMessage());
                System.err.println("SQLState3: " + ex.getSQLState());
                System.err.println("VendorError3: " + ex.getErrorCode());
            }
            finally {
                // it is a good idea to release
                // resources in a finally{} block
                // in reverse-order of their creation
                // if they are no-longer needed
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) { } // ignore
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) { } // ignore
                    stmt = null;
                }
            }
        }
        else
        {
            echomessage =  "Error accessing DB server...";
        }
        // DEBUG
        //System.out.println(echomessage);
    }
    catch(Exception e) {
        System.err.println("Exception: " + e.getMessage());
    }
    finally {
        try {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e) {}
    }
	}

public Vector<DBRegisteredUsers> getRegisteredUsersEntries()
{
    Vector<DBRegisteredUsers>  retVect = new  Vector<DBRegisteredUsers>();
    java.sql.Connection conn = null;
    try {
        Class.forName(jdbcdriverClassName).newInstance();
        conn = DriverManager.getConnection(connString, usrStr, pwdStr);
        String echomessage = "";
        if(!conn.isClosed())
        {
            //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                if (stmt.execute("SELECT idusers, passwd, login, email, idrole, role_name, lastadvtimestamp, disabled, FROM_UNIXTIME(lastadvtimestamp, \'%d/%m/%Y %H:%i:%s\') lastdate FROM vitrofrontenddb.roles AS r JOIN (vitrofrontenddb.userinrolesmr as ur JOIN vitrofrontenddb.users AS u ON u.idusers = ur.iduser) ON r.idroles=ur.idrole ")) {
                    rs = stmt.getResultSet();
                }
                if (rs!= null)
                {
                    while( rs.next()) {
                        int userId = rs.getInt("idusers");
                        String passwd = rs.getString("passwd")== null? "" : rs.getString("passwd"); // this is the one used in registration messages
                        String loginName =  rs.getString("login")== null? "" : rs.getString("login");
                        String emailAddress =  rs.getString("email")== null? "" : rs.getString("email");
                        String role_name  = rs.getString("role_name")== null? "" : rs.getString("role_name");
                        int role  = rs.getInt("idrole");
                        int lastadvtimestampInt = rs.getInt("lastadvtimestamp");
                        String lastdate = rs.getString("lastdate") == null? "N/A" : rs.getString("lastdate");
                        Boolean status = rs.getBoolean("disabled");
                        if(!loginName.isEmpty() && !loginName.equalsIgnoreCase(""))
                        {
                            DBRegisteredUsers entryRegisterUsers = new DBRegisteredUsers(userId, loginName, passwd, emailAddress, role, role_name, lastadvtimestampInt, lastdate, status);
                            retVect.addElement(entryRegisterUsers);
                        }
                    }
                }
            }
            catch (SQLException ex){
                // handle any errors
                System.out.println("SQLException: " + ex.getMessage());
                System.out.println("SQLState: " + ex.getSQLState());
                System.out.println("VendorError: " + ex.getErrorCode());
            }
            finally {
                // it is a good idea to release
                // resources in a finally{} block
                // in reverse-order of their creation
                // if they are no-longer needed
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) {
                        System.out.println("SQLException on rs close(): " + sqlEx.getMessage());
                        } // ignore
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) {
                        System.out.println("SQLException on stmt close(): " + sqlEx.getMessage());
                    } // ignore
                    stmt = null;
                }
            }
        }
        else
        {
            echomessage =  "Error accessing DB server...";
        }
        System.out.println(echomessage);
    }
    catch(Exception e) {
        System.err.println("Exception: " + e.getMessage());
    }
    finally {
        try {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e) {}
    }
    return retVect;
}

synchronized public void updateStatusUser(String pUserRegisteredName)
{
java.sql.Connection conn = null;
try {
	String echomessage = "";
    Class.forName(jdbcdriverClassName).newInstance();
    conn = DriverManager.getConnection(connString, usrStr, pwdStr);
    if(!conn.isClosed())
    {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (stmt.execute("SELECT idusers, passwd, login, email, idrole, role_name, lastadvtimestamp, disabled, FROM_UNIXTIME(lastadvtimestamp, \'%d/%m/%Y %H:%i:%s\') lastdate FROM vitrofrontenddb.roles AS r JOIN (vitrofrontenddb.userinrolesmr as ur JOIN vitrofrontenddb.users AS u ON u.idusers = ur.iduser) ON r.idroles=ur.idrole ")) {
                rs = stmt.getResultSet();
            }
            if (rs!= null)
            {
                while( rs.next()) {
                    int userId = rs.getInt("idusers");
                    String passwd = rs.getString("passwd")== null? "" : rs.getString("passwd"); // this is the one used in registration messages
                    String loginName =  rs.getString("login")== null? "" : rs.getString("login");
                    String emailAddress =  rs.getString("email")== null? "" : rs.getString("email");
                    String role_name  = rs.getString("role_name")== null? "" : rs.getString("role_name");
                    int role  = rs.getInt("idrole");
                    int lastadvtimestampInt = rs.getInt("lastadvtimestamp");
                    String lastdate = rs.getString("lastdate") == null? "N/A" : rs.getString("lastdate");
                    Boolean status = rs.getBoolean("disabled");
                    if(loginName.equalsIgnoreCase(pUserRegisteredName))
                    {
                    	if (status == false) {
                            if (stmt.execute("UPDATE `"+dbSchemaStr+"`.`users` SET disabled = 1  WHERE login=\'"+ pUserRegisteredName +"\'")) {
                                rs = stmt.getResultSet();  // TODO: this is not needed here...
                            }
                        }
                        else
                        {
                            if (stmt.execute("UPDATE `"+dbSchemaStr+"`.`users` SET disabled = 0  WHERE login=\'"+ pUserRegisteredName +"\'")) {
                                rs = stmt.getResultSet();  // TODO: this is not needed here...
                            }
                        }
                    break;
                    }
                }
            }
            
        }
        catch (SQLException ex){
            // handle any errors
            System.err.println("SQLException3: " + ex.getMessage());
            System.err.println("SQLState3: " + ex.getSQLState());
            System.err.println("VendorError3: " + ex.getErrorCode());
        }
        finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) { } // ignore
                rs = null;
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) { } // ignore
                stmt = null;
            }
        }
    }
    else
    {
        echomessage =  "Error accessing DB server...";
    }
    // DEBUG
    //System.out.println(echomessage);
}
catch(Exception e) {
    System.err.println("Exception: " + e.getMessage());
}
finally {
    try {
        if(conn != null)
            conn.close();
    }
    catch(SQLException e) {}
}
}

    /**
     *
     * @param ssUNinfoGWHM
     * @param ssUNCapHM
     * @param in_advCapsToSensModelsToMerge
     * @param in_advSmDevs
     * @param in_advGatewayIDStr
     * @param in_regGatewayDescStr
     * @param in_advGatewayIPv4
     * @param in_advGatewayLocStr
     */
    synchronized public void mergeAdvDataToGateway(HashMap<String, GatewayWithSmartNodes> ssUNinfoGWHM,
                                                    HashMap<String,Vector<SensorModel>> ssUNCapHM,
                                                    HashMap<String,  Vector<SensorModel> > in_advCapsToSensModelsToMerge,
                                                    Vector<SmartNode> in_advSmDevs,
                                                    String in_advGatewayIDStr,
                                                    String in_regGatewayDescStr,
                                                    String in_advGatewayIPv4,
                                                    String in_advGatewayLocStr) {
        //
        // For each generic capability found in the GatewayDescriptionAdvertisement (even when went per smart device separately)
        // Check if the UserPeer's capHMap contains it
        //  (if not add it, with its full vector of sensormodels).
        //  (if it does, then add only those sensormodels that don't exist in the existing vector of sensormodels (of the UserPeer's capHMap)
        //  TODO: support String for id for sensorModels ...(?)
        Set<String> advCapsSet = in_advCapsToSensModelsToMerge.keySet();
        Iterator<String> advCapsIt = advCapsSet.iterator();  // from the received GW ADV
        String currentAdvCap;
        // place one check-box for each Generic Capability Description
        while (advCapsIt.hasNext()) {
            currentAdvCap = advCapsIt.next();
            Vector<SensorModel> listOfSensToAdd = in_advCapsToSensModelsToMerge.get(currentAdvCap);
            if (ssUNCapHM.containsKey(currentAdvCap)) {
                Vector<SensorModel> previousListOfSensModel = ssUNCapHM.get(currentAdvCap);
                // concatenate with previous List (skip if the results are from an existing gateway)
                // so this does not PRECLUDE the same sensor model being added for the generic capability. (TODO: this can only be fixed in the gateways retroactively make a shared hastable for unique ids for specifi sensormodels)
                // but each model is uniquely indexed within its gateway (and maintains the gateway id info in its class type). This is good and similar to the SensorML specs
                // SensorML additionally allows for multiple output formats for a "sensor model" (component) and unlimited nesting in components though....
                for (int k1 = 0; k1 < previousListOfSensModel.size(); k1++) {
                    for (int m1 = 0; m1 < listOfSensToAdd.size(); m1++) {
                        if ((previousListOfSensModel.get(k1).getGatewayId().equals(listOfSensToAdd.get(m1).getGatewayId())) &&
                                (previousListOfSensModel.get(k1).getSmID().equals(listOfSensToAdd.get(m1).getSmID()) )) { // we should add only the newly added models...if any
                            listOfSensToAdd.removeElementAt(m1);
                            m1 -= 1;
                        }
                    }
                }
                previousListOfSensModel.addAll(listOfSensToAdd);
            } else if (listOfSensToAdd.size() > 0) {
                ssUNCapHM.put(currentAdvCap, listOfSensToAdd);
            }
        }

        // Find an existing entry for the gateway
        if (ssUNinfoGWHM.containsKey(in_advGatewayIDStr)) {
            // update existing mapped gateway with new possible values for each field
            GatewayWithSmartNodes tmpToUpd = ssUNinfoGWHM.get(in_advGatewayIDStr);
            Vector<SmartNode> existingSmDevs = tmpToUpd.getSmartNodesVec();

            tmpToUpd.setName(in_regGatewayDescStr); //renew description
            tmpToUpd.setDescription("GwDesc");      //????

            boolean foundDev = false;
            for (int j = 0; j < in_advSmDevs.size(); j++)
            {
                foundDev = false;
                for(int o = 0; o < existingSmDevs.size(); o++)
                {
                    if(in_advSmDevs.elementAt(j).getId().equalsIgnoreCase(existingSmDevs.elementAt(o).getId()))
                    {
                        foundDev = true;
                        existingSmDevs.setElementAt(in_advSmDevs.elementAt(j),o);    //replace (update)

                    }
                }
                if (foundDev == false)
                {
                    existingSmDevs.add(in_advSmDevs.elementAt(j));
                }
            }
        } else {
            GatewayWithSmartNodes tmpToInsert = null;
            if(in_regGatewayDescStr!=null && !(in_regGatewayDescStr.trim().isEmpty())){

                tmpToInsert = new GatewayWithSmartNodes(new Gateway(in_advGatewayIDStr, in_regGatewayDescStr, "GwDesc", null, null, in_advGatewayIPv4, in_advGatewayLocStr));
                tmpToInsert.setSmartNodesVec(in_advSmDevs);
            }
            else {
                GeoPoint gwGP = null;
                if(in_advGatewayLocStr!=null)
                {
                    String[] locTokensLatLong = in_advGatewayLocStr.split(",");
                    if(locTokensLatLong!=null && locTokensLatLong.length==2)
                    {
                        gwGP = new GeoPoint( locTokensLatLong[0].trim(), locTokensLatLong[1].trim(), "0");
                    }
                }
                tmpToInsert = new GatewayWithSmartNodes(new Gateway(in_advGatewayIDStr, "GwName", "GwDesc", null, gwGP, in_advGatewayIPv4, null));
                tmpToInsert.setSmartNodesVec(in_advSmDevs);
            }
            ssUNinfoGWHM.put(in_advGatewayIDStr, tmpToInsert);
        }
    }

    public Vector<String> getRegisteredUserRegNames()
    {
        Vector<String>  retVect = new  Vector<String>();
        java.sql.Connection conn = null;
        try {
            Class.forName(jdbcdriverClassName).newInstance();
            conn = DriverManager.getConnection(connString, usrStr, pwdStr);
            String echomessage = "";
            if(!conn.isClosed())
            {
                //echomessage =  "Successfully connected to "+ "MySQL server using TCP/IP...";
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = conn.createStatement();
                    if (stmt.execute("SELECT login FROM `"+dbSchemaStr+"`.`users` ")) {
                        rs = stmt.getResultSet();
                    }
                    if (rs!= null)
                    {
                        while( rs.next()) {
                            String registeredLogin =rs.getString("login")== null? "" : rs.getString("login");
                            if(!registeredLogin.isEmpty() && !registeredLogin.equalsIgnoreCase(""))
                            {
                                retVect.add(registeredLogin);
                            }
                        }
                    }
                }
                catch (SQLException ex){
                    // handle any errors
                    System.err.println("SQLException2: " + ex.getMessage());
                    System.err.println("SQLState2: " + ex.getSQLState());
                    System.err.println("VendorError2: " + ex.getErrorCode());
                }
                finally {
                    // it is a good idea to release
                    // resources in a finally{} block
                    // in reverse-order of their creation
                    // if they are no-longer needed
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException sqlEx) { } // ignore
                        rs = null;
                    }
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqlEx) { } // ignore
                        stmt = null;
                    }
                }
            }
            else
            {
                echomessage =  "Error accessing DB server...";
            }
            System.out.println(echomessage);
        }
        catch(Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        finally {
            try {
                if(conn != null)
                    conn.close();
            }
            catch(SQLException e) {}
        }
        return retVect;

           
        }

    synchronized public void insertUser(String ploginName, String pemailAddress, String ppasswd, String proleName)
    {
    java.sql.Connection conn = null;
    try {
    	String echomessage = "";
        Class.forName(jdbcdriverClassName).newInstance();
        conn = DriverManager.getConnection(connString, usrStr, pwdStr);
        if(!conn.isClosed())
        {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                if (stmt.execute("SELECT * FROM `"+dbSchemaStr+"`.`users` where `login`=\'"+ploginName+"\'")) {
                    rs = stmt.getResultSet();
                }
                if (rs== null)
                {	
                	int refRoleId=-1;
                	int refUserId=-1;
                	
                	 if (stmt.execute("START TRANSACTION")) {
                         rs = stmt.getResultSet();  // TODO: this is not needed here...
                 		}
                     if (stmt.execute("INSERT INTO `"+dbSchemaStr+"`.`users` (`login`, `email`, `passwd`, `lastadvtimestamp`, `disabled`) VALUES (\'"+ ploginName +"\',\'"+pemailAddress+"\',\'"+ppasswd+"\',0,0)")) {
                                    rs = stmt.getResultSet();  // TODO: this is not needed here...
                     }               
                     if (stmt.execute("SELECT `idusers` from `"+dbSchemaStr+"`.`users` where `login`= \'"+ ploginName +"\'")) {
                                        rs = stmt.getResultSet();
                        refUserId=rs.getInt("idusers");                
                     }
                     if (stmt.execute("SELECT `idroles` from `"+dbSchemaStr+"`.`roles` where `role_name`= \'"+ proleName +"\'")) {
                         rs = stmt.getResultSet();
                         refRoleId=rs.getInt("idroles");                
                     }
                     if (stmt.execute("INSERT INTO `"+dbSchemaStr+"`.`userinrolesmr` (`idrole`, `iduser`) VALUES (\'" + refRoleId +"\',\'"+refUserId+"\')")) {
                         rs = stmt.getResultSet();  // TODO: this is not needed here...
                     }  
                     if (stmt.execute("COMMIT")) {
                         rs = stmt.getResultSet();  // TODO: this is not needed here...
                 		}
                 	
                }
                else
                	System.err.println("The inserted value already exists");
                
            }
            catch (SQLException ex){
                // handle any errors
                System.err.println("SQLException3: " + ex.getMessage());
                System.err.println("SQLState3: " + ex.getSQLState());
                System.err.println("VendorError3: " + ex.getErrorCode());
            }
            finally {
                // it is a good idea to release
                // resources in a finally{} block
                // in reverse-order of their creation
                // if they are no-longer needed
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) { } // ignore
                    rs = null;
                }
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) { } // ignore
                    stmt = null;
                }
            }
        }
        else
        {
            echomessage =  "Error accessing DB server...";
        }
        // DEBUG
        //System.out.println(echomessage);
    }
    catch(Exception e) {
        System.err.println("Exception: " + e.getMessage());
    }
    finally {
        try {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e) {}
    }
    }

    
    
    
}
