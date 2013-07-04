package service.notification;
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
@WebService(name = "M2MNotificationService", targetNamespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/m2m/notification/v1/services")
public class NotificationImpl implements NotificationPort {

    private static final transient org.slf4j.Logger LOG = LoggerFactory.getLogger(NotificationImpl.class);

    @WebMethod(action = "urn:notify")
    @WebResult(name = "result", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")
    @RequestWrapper(localName = "notify", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types", className = "service.notification.Notify")
    @ResponseWrapper(localName = "notifyResponse", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types", className = "service.notification.NotifyResponse")
    public int notify(@WebParam(name = "subscriptionLogicalName", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")
                      String subscriptionLogicalName,
                      @WebParam(name = "eventKind", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")
                      EventKindType eventKind,
                      @WebParam(name = "xmlRegister", targetNamespace = "http://www.telefonica.com/schemas/UNICA/SOAP/m2m/notification/v1/types")
                      String xmlRegister)
            throws ClientException, ServerException {
        LOG.info("Notify for LogicalName "+ subscriptionLogicalName + " OK");
        LOG.info("Notify EventKind Type "+ eventKind.name() + "::" + eventKind.value()+" OK");
        LOG.info("Notify xmlRegister "+ xmlRegister  + " OK");
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public static void main(String[] argv) {
        Object implementor = new NotificationImpl ();
        String address = "http://localhost:9000/NotificationImpl";
        Endpoint.publish(address, implementor);
    }


}