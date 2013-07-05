package ch.ethz.inf.vs.californium.examples.resources;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.vs.californium.coap.CodeRegistry;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.OptionNumberRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.endpoint.LocalResource;

/**
 * This class implements an "/image" resource for demonstration purposes.
 * 
 * Provides different representations of an image through supports content
 * negotiation.
 * The required files are provided in the "run" directory for the .jar version.
 * Make sure to fix the location when running elsewhere.
 *  
 * @author Matthias Kovatsch
 */
public class ImageResource extends LocalResource {
	
	private List<Integer> supported = new ArrayList<Integer>();

	// Constructors ////////////////////////////////////////////////////////////
	
	/*
	 * Default constructor.
	 */
	public ImageResource() {
		this("image");
	}
	
	/*
	 * Constructs a new storage resource with the given resourceIdentifier.
	 */
	public ImageResource(String resourceIdentifier) {
		super(resourceIdentifier);
		setTitle("GET an image with different content-types");
		setResourceType("Image");
		
		supported.add(MediaTypeRegistry.IMAGE_PNG);
		supported.add(MediaTypeRegistry.IMAGE_JPEG);
		supported.add(MediaTypeRegistry.IMAGE_GIF);
		supported.add(MediaTypeRegistry.IMAGE_TIFF);
		
		for (int ct : supported) {
			setContentTypeCode(ct);
		}
		
		setMaximumSizeEstimate(18029);
		isObservable(false);
	}

	// REST Operations /////////////////////////////////////////////////////////
	
	@Override
	public void performGET(GETRequest request) {
		
		String filename = "data/image/";
		int ct = MediaTypeRegistry.IMAGE_PNG;
		
		// content negotiation
		if ((ct = MediaTypeRegistry.contentNegotiation(ct,  supported, request.getOptions(OptionNumberRegistry.ACCEPT)))==MediaTypeRegistry.UNDEFINED) {
			request.respond(CodeRegistry.RESP_NOT_ACCEPTABLE, "Accept GIF, JPEG, PNG, or TIFF");
			return;
		}
		
		filename += "image." + MediaTypeRegistry.toFileExtension(ct);

		//load representation from file
		File file = new File(filename);
		
		if (!file.exists()) {
			request.respond(CodeRegistry.RESP_INTERNAL_SERVER_ERROR, "Representation not found");
			return;
		}
		
		//get length of file
		int fileLength = (int)file.length();
		
		FileInputStream fileIn = null;
		byte[] fileData = new byte[fileLength];
		
		try
		{
		  //open input stream from file
		  fileIn = new FileInputStream(file);
		  //read file into byte array
		  fileIn.read(fileData);
		  fileIn.close();
		} catch (Exception e) {
			request.respond(CodeRegistry.RESP_INTERNAL_SERVER_ERROR, "IO error");
			System.err.println("/image IO error: " +e.getMessage());
			return;
		}
		
		// create response
		Response response = new Response(CodeRegistry.RESP_CONTENT);
		response.setPayload(fileData);

		// set content type
		response.setContentType(ct);

		// complete the request
		request.respond(response);
	}
}
