/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis
 * #     Paolo Medagliani
 * #     D. Davide Lamanna
 * #     Panos Trakadas
 * #     Andrea Kropp
 * #     Kiriakos Georgouleas
 * #     Panagiotis Karkazis
 * #     David Ferrer Figueroa
 * #     Francesco Ficarola
 * #     Stefano Puglia
 * #--------------------------------------------------------------------------
 */

package vitro.virtualsensor.communication;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.*;

/**
 *
 * @author Andres Picazo Cuesta
 */
public class HttpForwarder {
    
    private String response = "";
    private String errorResponse = "";
    private String error = "";
    private int responseCode = -1;
    
    public static final String POST = "POST";
    public static final String GET  = "GET";
    
    public String getResponse () {
        return response;
    }
    
    public String getErrorResponse() {
        return errorResponse;
    }
    
    public String getError () {
        return error;
    }
    
    public int getResponseCode () {
        return responseCode;
    }
    
    public boolean post (String endpoint, String msg) {
        return forward(endpoint, msg, POST);
    }
    
    public boolean get (String endpoint, String msg) {
        return forward(endpoint, msg, GET);
    }
    
    public boolean forward (String endpoint, String type) {
        return forward(endpoint, "", type);
    }
    
    public boolean forward (String endpoint, String msg, String type) {
        
        boolean ok;
        
        responseCode = -1;
        
        // Opens connection
        HttpURLConnection conn = openConnection(endpoint, type);
        if (conn == null) return false;
        
        // If msg not empty, write it
        if (!msg.isEmpty() && !sendMsg(msg,conn)) return false;
        
        ok = getResponse(conn);
        getErrorResponse(conn);
        
        try {
            responseCode = conn.getResponseCode();
        } catch (Exception e) {
            responseCode = -1;
        }
                        
        return ok;
    }
    
    private HttpURLConnection openConnection (String endpoint, String type) {
        HttpURLConnection connection;
        try {
            URL url = new URL(endpoint);
            URLConnection uc = url.openConnection();
            connection = (HttpURLConnection) uc;
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod(type); 
            return connection;
        } catch (Exception e) {
            error = "Can't open connection to: " + endpoint + " Cause:" + e.getMessage();
            return null;
        }
    }
    
    private boolean sendMsg (String msg, HttpURLConnection connection) {
        try {
            Writer wout = new OutputStreamWriter(connection.getOutputStream());
            wout.write(msg);
            wout.flush();
            wout.close();
            return true;
        } catch (Exception e) {
            error = "Can't write msg. Cause:" + e.getMessage();
            return false;
        }
    }
    
    private boolean getResponse (HttpURLConnection connection) {
        try {
            InputStream answer = connection.getInputStream();
            response = "";
            int c;
            while ((c = answer.read()) != -1) {
                response += (char)c;
            }
            response = String.valueOf(response);
            answer.close();
            return true;
        } catch (Exception e) {
            error = "Can't read response. Cause:" + e.getMessage();
            return false;
        }
    }
    
    private boolean getErrorResponse (HttpURLConnection connection) {
        try {
            InputStream err = connection.getErrorStream();
            errorResponse = "";
            int c;
            while ((c = err.read()) != -1) {
                errorResponse += (char)c;
            }
            err.close();
            return false;
        } catch (Exception e) {
            error = "Can't read error response. Cause:" + e.getMessage();
            return false;
        }
    }
    
    @Override
    public String toString() {
        return responseCode + " - Response: " + response + "\nErrorResponse: " + errorResponse + "\nError:" + error;
    }
}
