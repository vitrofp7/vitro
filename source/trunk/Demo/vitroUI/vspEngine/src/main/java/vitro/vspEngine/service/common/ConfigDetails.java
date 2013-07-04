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
package vitro.vspEngine.service.common;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 */
public class ConfigDetails {

    private org.apache.log4j.Logger LOG;
    static private String pathToRootFolder = ".";
    static private String configtxtPathName = "Config.txt";
    private HashMap<String, Object> ht; // contains keys that correspond to tags in the config.txt file and maps them to their values

    /**
     * Creates a new instance of ConfigDetails
     */
    private ConfigDetails() {
        ht = null;
        ht = new HashMap<String, Object>();
        LOG = org.apache.log4j.Logger.getLogger(getClass());
        parseConfigXML();
    }

    private static ConfigDetails myConfig = null;

    /**
     * Singleton pattern
     *
     * @return an object of ConfigDetails filled with data from a Config.txt (TODO)
     */
    synchronized public static ConfigDetails getConfigDetails() {
        if (myConfig == null) {
            myConfig = new ConfigDetails();
        }
        return myConfig;
    }


    /**
     * Singleton pattern
     *
     * @return an object of ConfigDetails filled with data from the Config.txt
     */
    synchronized public static ConfigDetails getConfigDetails(String aPathToRootFolder) {
        ConfigDetails.setPathToRootFolder(aPathToRootFolder);
        if (myConfig == null) {
            myConfig = new ConfigDetails();
        }
        return myConfig;
    }

    /**
     * @param aPathToRootFolder the pathToRootFolder to set
     */
    private static void setPathToRootFolder(String aPathToRootFolder) {
        pathToRootFolder = aPathToRootFolder;
    }

    /**
     * Reads from Config.txt and updates the ConfigDetails object fields
     *
     * @return nothing
     */
    private void parseConfigXML() {
        setDefaultValuesForSimpleSetup();
        /*
        // TODO: Fill in
         */
    }


    private void setDefaultValuesForSimpleSetup() {
        ht.clear();
        ht.put("pathtopeer", pathToRootFolder );
        ht.put("peertype", "userpeer");
    }

    public String getPathToPeer() {
        return (String) (ht.get("pathtopeer"));
    }

    public String toString() {
        return "toString() method not implemented for ConfigDetails"; // TODO: Not implemented yet
    }

    public String getProbableExternalIpAddress(){
        //first try to retrieve the IP from a site (TODO: we can host this on amethyst as well)
        String ipRet = null;
        try{
            URL whatismyip = new URL("http://amethyst.cti.gr/vitroui/vwhatsmyip.jsp");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    whatismyip.openStream()));

            while( (ipRet = in.readLine() ) != null)
            {
                if(! ipRet.trim().isEmpty())
                {
                   break;
                }
                //retrieve your external IP as a String . Useful especially on NAT configurations
            }
            LOG.info("from external web IP=" + ipRet);
            if(ipRet != null && (ipRet.trim().compareToIgnoreCase ("")== 0 || ipRet.toLowerCase().startsWith("192.") ||
                    ipRet.toLowerCase().startsWith("127.")) )
            {
                ipRet = null;
            }
        }
        catch (Exception exMalUrl)
        {
            LOG.error( exMalUrl.toString());
        }

        if(ipRet == null)
        {
            try {
                for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                    NetworkInterface intf = en.nextElement();
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); ipRet==null && enumIpAddr.hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            if(!inetAddress.getHostAddress().toLowerCase().contains(":")
                                    && ! ( inetAddress.getHostAddress().toLowerCase().startsWith("192.") ||
                                         inetAddress.getHostAddress().toLowerCase().startsWith("127.")  ) )
                            {
                                ipRet = inetAddress.getHostAddress();
                                LOG.info("from local config IP=" + ipRet);
                            }
                        }
                    }
                }
            } catch (SocketException ex) {
                LOG.error( ex.toString());
            }
        }
        return ipRet.trim();
    }

}
