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
package vitro.dcaintercom.communication.common;

/**
 */
public class Config {
    private static Config _config = null;
    // TODO: this could be moved in an external dca.config file
    private int serviceIDVITRO = 1001;
    private String serviceName = "VITRO";
    private String ipDCAhost = "http://195.235.93.106";
    private String commandsUrl = ipDCAhost + ":6501/UNICA_SDP/M2M/Command";  //commands
    private String sensorDataUrl =  ipDCAhost + ":6503/UNICA_SDP/M2M/Sensordata";  //Queries & Provisión
    private String customersUrl =  ipDCAhost + ":6507/UNICA_SDP/M2M/Customer"; // Customers provision
    private String subscriptionsUrl =  ipDCAhost + ":6504/UNICA_SDP/M2M/Subscription"; // subscriptions
    private String queriesRestUrl =  ipDCAhost + ":6508"; //  Queries Rest: (Under development)
    private String notificationURL        = ipDCAhost + ":8002/idas/2.0";
    
    private Config()
    {

    }

    public synchronized static Config getConfig()
    {
        if(_config == null)
        {
            _config = new Config();
        }
        return _config;
    }

    public String getNotificationsUrl(){
        return this.notificationURL;
    }
    public String getCommandsUrl() {
        return commandsUrl;
    }

    public void setCommandsUrl(String commandsUrl) {
        this.commandsUrl = commandsUrl;
    }

    public String getSensorDataUrl() {
        return sensorDataUrl;
    }

    public void setSensorDataUrl(String sensorDataUrl) {
        this.sensorDataUrl = sensorDataUrl;
    }

    public String getCustomersUrl() {
        return customersUrl;
    }

    public void setCustomersUrl(String customersUrl) {
        this.customersUrl = customersUrl;
    }

    public String getSubscriptionsUrl() {
        return subscriptionsUrl;
    }

    public void setSubscriptionsUrl(String subscriptionsUrl) {
        this.subscriptionsUrl = subscriptionsUrl;
    }

    public String getQueriesRestUrl() {
        return queriesRestUrl;
    }

    public void setQueriesRestUrl(String queriesRestUrl) {
        this.queriesRestUrl = queriesRestUrl;
    }

    public int getServiceIDVITRO() {
        return serviceIDVITRO;
    }

    public void setServiceIDVITRO(int serviceIDVITRO) {
        this.serviceIDVITRO = serviceIDVITRO;
    }
    
    public String getServiceNameVITRO(){
    	return this.serviceName;
    }
}
