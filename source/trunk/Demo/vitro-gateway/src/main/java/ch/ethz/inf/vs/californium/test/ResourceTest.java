package ch.ethz.inf.vs.californium.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.ethz.inf.vs.californium.coap.LinkFormat;
import ch.ethz.inf.vs.californium.coap.Option;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.endpoint.RemoteResource;
import ch.ethz.inf.vs.californium.endpoint.Resource;


public class ResourceTest {

	@Test
	public void simpleTest() {
		String input = "</sensors/temp>;ct=41;rt=\"TemperatureC\"";
		Resource root = RemoteResource.newRoot(input);

		Resource res = root.getResource("/sensors/temp");
		assertNotNull(res);

		assertEquals("temp", res.getName());
		assertEquals(41, res.getContentTypeCode());
		assertEquals("TemperatureC", res.getResourceType());
	}

	@Test
	public void extendedTest() {
		String input = "</myPäth>;rt=\"MyName\";if=\"/someRef/path\";ct=42;obs;sz=10";
		Resource root = RemoteResource.newRoot(input);
		root.prettyPrint();

		Resource res = root.getResource("/myPäth");
		
		res.prettyPrint();
		
		assertNotNull(res);


		assertEquals("myPäth", res.getName());
		assertEquals("/myPäth", res.getPath());
		assertEquals("MyName", res.getResourceType().get(0));
		assertEquals("/someRef/path", res.getInterfaceDescription().get(0));
		assertEquals(42, res.getContentTypeCode().get(0).intValue());
		assertEquals(10, res.getMaximumSizeEstimate());
		assertTrue(res.isObservable());
	
	}

	@Test
	public void conversionTest() {
		String link1 = "</myUri/something>;ct=42;if=\"/someRef/path\";obs;rt=\"MyName\";sz=10";
		String link2 = "</myUri>";
		String link3 = "</a>";
		String format = link1 + "," + link2 + "," + link3;
		Resource res = RemoteResource.newRoot(format);
		res.prettyPrint();
		String result = LinkFormat.serialize(res, null, true);
		assertEquals(link3 + "," + link2 + "," + link1, result);
	}
	
	@Test
	public void concreteTest() {
		String link = "</careless>;rt=\"SepararateResponseTester\";title=\"This resource will ACK anything, but never send a separate response\",</feedback>;rt=\"FeedbackMailSender\";title=\"POST feedback using mail\",</helloWorld>;rt=\"HelloWorldDisplayer\";title=\"GET a friendly greeting!\",</image>;ct=21;ct=22;ct=23;ct=24;rt=\"Image\";sz=18029;title=\"GET an image with different content-types\",</large>;rt=\"block\";title=\"Large resource\",</large_update>;rt=\"block\";rt=\"observe\";title=\"Large resource that can be updated using PUT method\",</mirror>;rt=\"RequestMirroring\";title=\"POST request to receive it back as echo\",</obs>;obs;rt=\"observe\";title=\"Observable resource which changes every 5 seconds\",</query>;title=\"Resource accepting query parameters\",</seg1/seg2/seg3>;title=\"Long path resource\",</separate>;title=\"Resource which cannot be served immediately and which cannot be acknowledged in a piggy-backed way\",</storage>;obs;rt=\"Storage\";title=\"PUT your data here or POST new resources!\",</test>;title=\"Default test resource\",</timeResource>;rt=\"CurrentTime\";title=\"GET the current time\",</toUpper>;rt=\"UppercaseConverter\";title=\"POST text here to convert it to uppercase\",</weatherResource>;rt=\"ZurichWeather\";title=\"GET the current weather in zurich\"";
		Resource res = RemoteResource.newRoot(link);
		res.prettyPrint();
		String result = LinkFormat.serialize(res, null, true);
		assertEquals(link, result);
	}

	@Test
	public void matchTest() {
		String link1 = "</myUri/something>;ct=42;if=\"/someRef/path\";obs;rt=\"MyName\";sz=10";
		String link2 = "</myUri>;ct=50;rt=\"MyName\"";
		String link3 = "</a>;sz=10;rt=\"MyNope\"";
		String format = link1 + "," + link2 + "," + link3;
		Resource res = RemoteResource.newRoot(format);
		res.prettyPrint();
		
		List<Option> query = new ArrayList<Option>();
		query.add(new Option("ct=530", OptionNumberRegistry.URI_QUERY));
		
		//System.out.println(LinkFormat.matches(res.getResource("myUri/something"), query));
		
		String queried = LinkFormat.serialize(res, query, true);
		
		System.out.println(queried);
		
		assertEquals(link2+","+link1, queried);
	}
}
