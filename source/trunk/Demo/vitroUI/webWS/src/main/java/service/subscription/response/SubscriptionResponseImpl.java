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

package service.subscription.response;
import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
@WebService(name = "M2MSubscriptionResponseService", targetNamespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/subscriptionresponse/v1/services")
public class SubscriptionResponseImpl implements SubscriptionResponsePort {

    private static final transient org.slf4j.Logger LOG = LoggerFactory.getLogger(SubscriptionResponseImpl.class);

//  @WebMethod
//  public String sayHelloWorldFrom(String from) {
//    String result = "Hello, world, from " + from;
//    System.out.println(result);
//    return result;
//  }

    @WebMethod(action = "urn:subscribeResponse")
    @WebResult(name = "result", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
    @RequestWrapper(localName = "subscribeResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types", className = "service.subscription.response.SubscribeResponse")
    @ResponseWrapper(localName = "subscribeResponseResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types", className = "service.subscription.response.SubscribeResponseResponse")
    public int subscribeResponse(@WebParam(name = "subscriptionLogicalName", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
                                 String subscriptionLogicalName,
                                 @WebParam(name = "outgoingConnectionId", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
                                 int outgoingConnectionId,
                                 @WebParam(name = "errorCode", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
                                 int errorCode,
                                 @WebParam(name = "errorText", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
                                 String errorText) throws ClientException, ServerException {
        if (errorCode == 0) {
            LOG.info("subscribeResponse "+ subscriptionLogicalName + " OK");
            LOG.info("subscribeResponse outgoingConnectionId "+ Integer.toString(outgoingConnectionId)  + " OK");
            //System.out.println("Subscription "+ subscriptionLogicalName + " OK");
            //logger.info("$subscriptionLogicalName: Error:No, ConnID:$outgoingConnectionId $errorText");
        }
        else {
            //System.out.println("Subscription "+ subscriptionLogicalName + " ERROR: " + errorCode);
            LOG.info("subscribeResponse "+ subscriptionLogicalName + " ERROR: " + errorCode);
            //logger.warn("Subscription $subscriptionLogicalName error, code: $errorCode.");
            //logger.error("$subscriptionLogicalName: Error:Yes, $errorText ConnID:$outgoingConnectionId");
            //System.out.println(subscriptionLogicalName + " ERROR: " + outgoingConnectionId);
            LOG.info(subscriptionLogicalName + " ERROR: " + outgoingConnectionId);
            LOG.info("subscribeResponse Error: "+ errorText);
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @WebMethod(action = "urn:unsubscribeResponse")
    @WebResult(name = "result", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
    @RequestWrapper(localName = "unsubscribeResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types", className = "service.subscription.response.UnsubscribeResponse")
    @ResponseWrapper(localName = "unsubscribeResponseResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types", className = "service.subscription.response.UnsubscribeResponseResponse")
    public int unsubscribeResponse(@WebParam(name = "subscriptionLogicalName", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types") String subscriptionLogicalName, @WebParam(name = "errorCode", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types") int errorCode, @WebParam(name = "errorText", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types") String errorText) throws ClientException, ServerException {
        return -1;  //0 success, -1 otherwise
    }

    @WebMethod(action = "urn:disconnectResponse")
    @WebResult(name = "result", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
    @RequestWrapper(localName = "disconnectResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types", className = "service.subscription.response.DisconnectResponse")
    @ResponseWrapper(localName = "disconnectResponseResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types", className = "service.subscription.response.DisconnectResponseResponse")
    public int disconnectResponse(@WebParam(name = "errorCode", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types") int errorCode, @WebParam(name = "errorText", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types") String errorText) throws ClientException, ServerException {
        return -1;  //0 success, -1 otherwise
    }


    public static void main(String[] argv) {
        Object implementor = new SubscriptionResponseImpl ();
        String address = "http://localhost:9000/SubscriptionResponseImpl";
        Endpoint.publish(address, implementor);
    }

}