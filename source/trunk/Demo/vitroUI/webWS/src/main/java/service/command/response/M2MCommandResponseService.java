
package service.command.response;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "M2MCommandResponseService", targetNamespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/commandresponse/v1/services", wsdlLocation = "file:/C:/workspace/allProjects/VitroMiddlewareNew/vitroUI/webWS/src/main/resources/UNICA_API_SOAP_m2m_commandresponse_services_v1_1.wsdl")
public class M2MCommandResponseService
    extends Service
{

    private final static URL M2MCOMMANDRESPONSESERVICE_WSDL_LOCATION;
    private final static WebServiceException M2MCOMMANDRESPONSESERVICE_EXCEPTION;
    private final static QName M2MCOMMANDRESPONSESERVICE_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/commandresponse/v1/services", "M2MCommandResponseService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("file:/C:/workspace/allProjects/VitroMiddlewareNew/vitroUI/webWS/src/main/resources/UNICA_API_SOAP_m2m_commandresponse_services_v1_1.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        M2MCOMMANDRESPONSESERVICE_WSDL_LOCATION = url;
        M2MCOMMANDRESPONSESERVICE_EXCEPTION = e;
    }

    public M2MCommandResponseService() {
        super(__getWsdlLocation(), M2MCOMMANDRESPONSESERVICE_QNAME);
    }

    public M2MCommandResponseService(WebServiceFeature... features) {
        super(__getWsdlLocation(), M2MCOMMANDRESPONSESERVICE_QNAME, features);
    }

    public M2MCommandResponseService(URL wsdlLocation) {
        super(wsdlLocation, M2MCOMMANDRESPONSESERVICE_QNAME);
    }

    public M2MCommandResponseService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, M2MCOMMANDRESPONSESERVICE_QNAME, features);
    }

    public M2MCommandResponseService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public M2MCommandResponseService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns CommandResponsePort
     */
    @WebEndpoint(name = "CommandResponse")
    public CommandResponsePort getCommandResponse() {
        return super.getPort(new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/commandresponse/v1/services", "CommandResponse"), CommandResponsePort.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns CommandResponsePort
     */
    @WebEndpoint(name = "CommandResponse")
    public CommandResponsePort getCommandResponse(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/commandresponse/v1/services", "CommandResponse"), CommandResponsePort.class, features);
    }

    private static URL __getWsdlLocation() {
        if (M2MCOMMANDRESPONSESERVICE_EXCEPTION!= null) {
            throw M2MCOMMANDRESPONSESERVICE_EXCEPTION;
        }
        return M2MCOMMANDRESPONSESERVICE_WSDL_LOCATION;
    }

}