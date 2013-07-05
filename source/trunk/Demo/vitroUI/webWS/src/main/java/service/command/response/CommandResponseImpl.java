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

package service.command.response;

import org.slf4j.LoggerFactory;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * User: antoniou
 */
@WebService(name = "M2MCommandResponseService", targetNamespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/commandresponse/v1/services")
public class CommandResponseImpl implements CommandResponsePort {

    private static final transient org.slf4j.Logger LOG = LoggerFactory.getLogger(CommandResponseImpl.class);

    @WebMethod(action = "urn:commandSensorResult")
    @WebResult(name = "result", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types")
    @RequestWrapper(localName = "commandSensorResult", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types", className = "service.command.response.CommandSensorResult")
    @ResponseWrapper(localName = "commandSensorResultResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types", className = "service.command.response.CommandSensorResultResponse")
    public int commandSensorResult(@WebParam(name = "correlator", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types")
                                   String correlator,
                                   @WebParam(name = "commandResult", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types")
                                   CommandSensorResultType commandResult,
                                   @WebParam(name = "errorCode", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types")
                                   int errorCode, @WebParam(name = "errorText", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/commandresponse/v1/types")
                                   String errorText) throws ClientException, ServerException {
        if (errorCode == 0) {
            LOG.info("CommandResponse for correlator "+ correlator + " OK");
            LOG.info("CommandResponse commandResult ::"+ commandResult.getCommandResultML() + ":: OK");
        }
        else {
            LOG.info("CommandResponse "+ correlator + " ERROR: " + Integer.toString(errorCode));
            LOG.info("CommandResponse error "+ errorText  + " OK");
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] argv) {
        Object implementor = new CommandResponseImpl ();
        String address = "http://localhost:9000/CommandResponseImpl";
        Endpoint.publish(address, implementor);
    }
}
