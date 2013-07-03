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

package vitro.virtualsensor.communication.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService (targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")
public interface NotificationsInterface {

		@WebMethod
		public  @WebResult(name="result") int notify (
				@WebParam(name="subscriptionLogicalName", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")String subsLogicalName,
				@WebParam(name="eventKind", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")String eventKind,
				@WebParam(name="xmlRegister", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")String XMLfile);
}
