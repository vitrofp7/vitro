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
package vitro.dcaintercom.communication.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService (targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")
public interface SubscriptionsInterface {

    @WebMethod
    public void subscribeResponse (
            @WebParam(name="subscriptionLogicalName", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")String subscriptionLogicalName,
            @WebParam(name="outgoingConnectionId",targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")int outgoingConnectionId,
            @WebParam(name="errorCode",targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")int errorCode,
            @WebParam(name="errorText",targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/subscriptionresponse/v1/types")String errorText);    
}
