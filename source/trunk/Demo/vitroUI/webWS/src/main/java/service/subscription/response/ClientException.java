
package service.subscription.response;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebFault(name = "ClientException", targetNamespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults")
public class ClientException
    extends Exception
{

    /**
     * Java type that goes as soapenv:Fault detail element.
     * 
     */
    private ClientExceptionType faultInfo;

    /**
     * 
     * @param message
     * @param faultInfo
     */
    public ClientException(String message, ClientExceptionType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ClientException(String message, ClientExceptionType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * 
     * @return
     *     returns fault bean: service.subscription.response.ClientExceptionType
     */
    public ClientExceptionType getFaultInfo() {
        return faultInfo;
    }

}
