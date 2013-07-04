package it.wlab.vitro.vgw;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.BeforeClass;
import org.junit.Test;

import vitro.vgw.communication.response.VgwResponse;

public class HelloWorldIT {
	private static String endpointUrl;
	
	@BeforeClass
	public static void beforeClass() {
		endpointUrl = System.getProperty("service.url");
	}
	
	@Test
	public void testPing() throws Exception {
		WebClient client = WebClient.create(endpointUrl + "/hello/echo/SierraTangoNevada");
		Response r = client.accept("text/plain").get();
		assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());
		String value = IOUtils.toString((InputStream)r.getEntity());
		assertEquals("SierraTangoNevada", value);
	}

    @Test
    public void testXMLRoundtrip() throws Exception {
        BufferedReader br = null;
        
        StringBuffer sb = new StringBuffer();
        
        try{
        	br = new BufferedReader(new InputStreamReader(HelloWorldIT.class.getResourceAsStream("/tests/test-request")));
            String line = null;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            
        } finally{
            if(br != null){
                br.close();    
            }
        }
        
        
        
        WebClient client = WebClient.create(endpointUrl + "/vgw/invokeWSIService");
        Response r = client.accept("application/xml")
                .type("application/xml")
                .post(sb.toString());
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());

        JAXBContext jc = JAXBContext.newInstance("it.wlab.vitro.communication");

        Unmarshaller unmarshaller = jc.createUnmarshaller();

        VgwResponse response  = (VgwResponse) unmarshaller.unmarshal((InputStream)r.getEntity());

        
        if(response != null){
        	 System.out.println("response = " + response.isSuccess());
        }
        
        assertEquals(true, response.isSuccess());
    }
}
