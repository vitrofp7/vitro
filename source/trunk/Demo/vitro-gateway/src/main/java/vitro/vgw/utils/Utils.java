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
package vitro.vgw.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import net.opengis.ows.v_1_1_0.ExceptionReport;
import net.opengis.ows.v_1_1_0.ExceptionType;
import net.opengis.sensorml.v_1_0_1.ProcessChainType;
import net.opengis.sos.v_1_0_0.RegisterSensor;
import net.opengis.sos.v_1_0_0.filter.v_1_1_0.ComparisonOpsType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vitro.vgw.exception.IdasException;
import vitro.vgw.exception.VitroGatewayException;


public class Utils {

	private static Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static Unmarshaller getIDASProtcolUnmarshaller() throws Exception{
			Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
	        return unmarshaller;
	} 
	
	public static Marshaller getIDASProtcolMarshaller() throws Exception{
		Marshaller marshaller = getJAXBContext().createMarshaller();
		return marshaller;
	} 
	
	public static JAXBContext getJAXBContext() throws Exception{
		return  JAXBContext.newInstance(
				RegisterSensor.class,
				ComparisonOpsType.class, 
				ProcessChainType.class);
	}
	
	public static void showElement(Object element) throws VitroGatewayException{
		
		ByteArrayOutputStream baos = null;
		
		try{
			Marshaller marshaller = getJAXBContext().createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			baos = new ByteArrayOutputStream();
			marshaller.marshal(element, baos);
			
			logger.debug(baos.toString());
		} catch(Exception e){
			throw new VitroGatewayException(e);
		} finally{
			if(baos != null){
				try {
					baos.close();
				} catch (IOException e) {
					logger.error("Error while closing baos", e);
				}
			}
		}
		
	}
	
	public static IdasException parseException(ExceptionReport exceptionReport) throws VitroGatewayException{
	
		showElement(exceptionReport);
		
		
		ExceptionType exceptionType = exceptionReport.getException().get(0);

		String code = exceptionType.getExceptionCode();
		String locator = exceptionType.getLocator();
		return new IdasException(exceptionType.getExceptionText().get(0), code, locator);
		
	}
	
}
