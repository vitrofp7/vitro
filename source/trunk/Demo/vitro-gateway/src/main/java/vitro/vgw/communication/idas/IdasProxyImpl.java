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
package vitro.vgw.communication.idas;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import net.opengis.ows.v_1_1_0.ExceptionReport;
import net.opengis.sos.v_1_0_0.InsertObservation;
import net.opengis.sos.v_1_0_0.InsertObservationResponse;
import net.opengis.sos.v_1_0_0.RegisterSensor;
import net.opengis.sos.v_1_0_0.RegisterSensorResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vitro.vgw.exception.VitroGatewayException;
import vitro.vgw.utils.Utils;

public class IdasProxyImpl implements IdasProxy{

	private Logger logger = LoggerFactory.getLogger(IdasProxyImpl.class);
	
	private HttpClient httpclient;

	private String endPoint;

	public IdasProxyImpl(String endPoint) {
		super();
		this.httpclient = new DefaultHttpClient();
		this.endPoint = endPoint;
		
	}

	public InsertObservationResponse insertObservation(InsertObservation request) throws VitroGatewayException {

		Utils.showElement(request);
		
		Object idasResponse = sendRequest(request);
		if(!(idasResponse instanceof InsertObservationResponse)){
			throw new VitroGatewayException("IDAS response is not of type InsertObservationResponse");
		}

		return (InsertObservationResponse)idasResponse;
	}
	
	public RegisterSensorResponse registerSensor(RegisterSensor request) throws VitroGatewayException {

		Utils.showElement(request);
		
		Object idasResponse = sendRequest(request);
		if(!(idasResponse instanceof RegisterSensorResponse)){
			throw new VitroGatewayException("IDAS response is not of type RegisterSensorResponse");
		}


		return (RegisterSensorResponse)idasResponse;

			
	}
	
	public RegisterSensorResponse registerSensor(String request) throws VitroGatewayException {

		Object idasResponse = sendRequest(request);
		if(!(idasResponse instanceof RegisterSensorResponse)){
			throw new VitroGatewayException("IDAS response is not of type RegisterSensorResponse");
		}


		return (RegisterSensorResponse)idasResponse;

			
	}
	
	private Object sendRequest(String request) throws VitroGatewayException{
		
		Object result = null;
		
		InputStream instream = null;
		
		try{
			HttpPost httpPost = new HttpPost(endPoint);

			StringEntity entityPar = new StringEntity(request, "application/xml", HTTP.UTF_8);

			httpPost.setEntity(entityPar);

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				
				instream = entity.getContent();
				
				Unmarshaller unmarshaller = Utils.getJAXBContext().createUnmarshaller();
				Object idasResponse = unmarshaller.unmarshal(instream);
				
				if (idasResponse instanceof ExceptionReport) {
					throw Utils.parseException((ExceptionReport)idasResponse);
				} 
				
				result = idasResponse;

			} else{
				throw new VitroGatewayException("Server response does not contain any body");
			}
		} catch(VitroGatewayException e){
			throw e;
		} catch(Exception e){
			throw new VitroGatewayException(e);
		} finally{
			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					logger.error("Error while closing server response stream", e);
				}
			}
		}
		
		return result;
		
	}
	
	private Object sendRequest(Object request) throws VitroGatewayException{
		
		Object result = null;
		
		InputStream instream = null;
		
		try{
			HttpPost httpPost = new HttpPost(endPoint);

			JAXBContext jaxbContext = Utils.getJAXBContext();
			
			Marshaller mar = jaxbContext.createMarshaller();
			mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			mar.marshal(request, baos);

			String requestXML = baos.toString(HTTP.UTF_8);

			StringEntity entityPar = new StringEntity(requestXML, "application/xml", HTTP.UTF_8);

			httpPost.setEntity(entityPar);

			HttpResponse response = httpclient.execute(httpPost);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				
				instream = entity.getContent();
				
				Unmarshaller unmarshaller = Utils.getJAXBContext().createUnmarshaller();
				Object idasResponse = unmarshaller.unmarshal(instream);
				
				if (idasResponse instanceof ExceptionReport) {
					throw Utils.parseException((ExceptionReport)idasResponse);
				} 
				
				result = idasResponse;

			} else{
				throw new VitroGatewayException("Server response does not contain any body");
			}
		} catch(VitroGatewayException e){
			throw e;
		} catch(Exception e){
			throw new VitroGatewayException(e);
		} finally{
			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					logger.error("Error while closing server response stream", e);
				}
			}
		}
		
		return result;
		
	}

}
