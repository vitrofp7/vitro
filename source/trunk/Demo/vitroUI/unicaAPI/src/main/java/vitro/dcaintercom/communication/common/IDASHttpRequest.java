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
package vitro.dcaintercom.communication.common;

import java.io.InputStream;
import java.io.StringReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
public class IDASHttpRequest {
    private static IDASHttpRequest _IDASHttpRequest = null;
    private Logger LOG;

    private IDASHttpRequest()
    {
        LOG= Logger.getLogger(this.getClass());
    }

    public synchronized static IDASHttpRequest getIDASHttpRequest()
    {
        if(_IDASHttpRequest == null)
        {
            _IDASHttpRequest = new IDASHttpRequest();
        }
        return _IDASHttpRequest;
    }

    public String sendPOSTToIdasXML(String requestStr, String serviceEndPoint)
    {
        String retStr = "";

        LOG.info("Trying to HTTP connect to IDAS...");
        //System.out.println("Trying to HTTP connect to IDAS...");
//            sendPOSTToIdasSubscription();
        HttpClient httpClient = new DefaultHttpClient();
        try {

            HttpPost httpIDASPost = new HttpPost(serviceEndPoint);
            LOG.info("executing request " + httpIDASPost.getURI());
            //System.out.println("executing request " + httpIDASPost.getURI());

            HttpEntity xmlStringEntity = new StringEntity(requestStr, "text/xml" ,"UTF-8");
            httpIDASPost.setEntity(xmlStringEntity);

            HttpResponse responseIDAS = httpClient.execute(httpIDASPost);
            //
            // HANDLE THE IDAS RESPONSE  FOR REGISTER SENSOR
            //
            int responseIDASStatusCode = responseIDAS.getStatusLine().getStatusCode();
            HttpEntity responseIDASEntity = responseIDAS.getEntity();

            if (responseIDASEntity != null) {
                //InputStream responseIDASBody = responseIDASEntity.getContent() ;
                String responseIDASBodyStr = EntityUtils.toString(responseIDASEntity);

                StringReader sr1 = new StringReader(responseIDASBodyStr);

                if (responseIDASStatusCode != 200) {
                    // responseBody will have the error response
                    LOG.info("--------ERROR Response: "+ responseIDASStatusCode+"------------------------------");
//                    System.out.println("--------ERROR Response: "+ responseIDASStatusCode+"------------------------------");
//                    LOG.info(responseIDASBodyStr);
//                    System.out.println(responseIDASBodyStr);
                    // TODO: Add handler for response from IDAS interfaces
//                    LOG.info("----------------------------------------");
//                    System.out.println("----------------------------------------");
                }
                else
                {
                    // DEBUG
                    // TODO: Add handler for response from IDAS interfaces code (responses package)
                    LOG.info("--------OK Response: "+ responseIDASStatusCode+"------------------------------");
                    //System.out.println("--------OK Response: "+ responseIDASStatusCode+"------------------------------");
//                    System.out.println(responseIDASBodyStr);
                }
                retStr = responseIDASBodyStr;
                //###
                //### END OF POST TO THE IDAS PLATFORM (REGISTER SENSOR MESSAGE)
                //###
            }
            else{
                LOG.info("No response received from POST to DCA");
                //System.out.println("--------NO Response! ----- ");
            }

        }
        catch (Exception e)
        {
            LOG.info(e.getMessage());
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        finally {
            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate de-allocation of all system resources
            // System.out.println("--------Shutting down connection! ----- ");
            httpClient.getConnectionManager().shutdown();
        }
        return retStr;
    }


    public String sendPOSTToIdasXML(String requestStr)
    {

        return sendPOSTToIdasXML(requestStr, Config.getConfig().getSensorDataUrl());
    }



}
