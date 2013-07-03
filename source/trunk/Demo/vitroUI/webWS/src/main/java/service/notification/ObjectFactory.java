
package service.notification;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the service.notification package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _SimpleOAuthHeaderTypeXoauthRequestorId_QNAME = new QName("http://www.telefonica.com/schemas/UNICA/SOAP/common/v1", "xoauth_requestor_id");
    private final static QName _TransactionInfoHeader_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header", "transactionInfoHeader");
    private final static QName _SimpleOAuthHeader_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers", "simpleOAuthHeader");
    private final static QName _SessionToken_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers", "sessionToken");
    private final static QName _ServerException_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults", "ServerException");
    private final static QName _ClientException_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults", "ClientException");
    private final static QName _UplinkTransactionInfoHeader_QNAME = new QName("http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header", "uplinkTransactionInfoHeader");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: service.notification
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ServerExceptionType }
     * 
     */
    public ServerExceptionType createServerExceptionType() {
        return new ServerExceptionType();
    }

    /**
     * Create an instance of {@link ClientExceptionType }
     * 
     */
    public ClientExceptionType createClientExceptionType() {
        return new ClientExceptionType();
    }

    /**
     * Create an instance of {@link NameValuePairType }
     * 
     */
    public NameValuePairType createNameValuePairType() {
        return new NameValuePairType();
    }

    /**
     * Create an instance of {@link SimpleReferenceType }
     * 
     */
    public SimpleReferenceType createSimpleReferenceType() {
        return new SimpleReferenceType();
    }

    /**
     * Create an instance of {@link IpAddressType }
     * 
     */
    public IpAddressType createIpAddressType() {
        return new IpAddressType();
    }

    /**
     * Create an instance of {@link FilterParamsType }
     * 
     */
    public FilterParamsType createFilterParamsType() {
        return new FilterParamsType();
    }

    /**
     * Create an instance of {@link NameType }
     * 
     */
    public NameType createNameType() {
        return new NameType();
    }

    /**
     * Create an instance of {@link SearchParamsType }
     * 
     */
    public SearchParamsType createSearchParamsType() {
        return new SearchParamsType();
    }

    /**
     * Create an instance of {@link SessionTokenType }
     * 
     */
    public SessionTokenType createSessionTokenType() {
        return new SessionTokenType();
    }

    /**
     * Create an instance of {@link UserIdType }
     * 
     */
    public UserIdType createUserIdType() {
        return new UserIdType();
    }

    /**
     * Create an instance of {@link OtherIdType }
     * 
     */
    public OtherIdType createOtherIdType() {
        return new OtherIdType();
    }

    /**
     * Create an instance of {@link ExtensionType }
     * 
     */
    public ExtensionType createExtensionType() {
        return new ExtensionType();
    }

    /**
     * Create an instance of {@link SimpleOAuthHeaderType }
     * 
     */
    public SimpleOAuthHeaderType createSimpleOAuthHeaderType() {
        return new SimpleOAuthHeaderType();
    }

    /**
     * Create an instance of {@link AddressType }
     * 
     */
    public AddressType createAddressType() {
        return new AddressType();
    }

    /**
     * Create an instance of {@link Notify }
     * 
     */
    public Notify createNotify() {
        return new Notify();
    }

    /**
     * Create an instance of {@link NotifyResponse }
     * 
     */
    public NotifyResponse createNotifyResponse() {
        return new NotifyResponse();
    }

    /**
     * Create an instance of {@link UplinkTransactionInfoHeaderType }
     * 
     */
    public UplinkTransactionInfoHeaderType createUplinkTransactionInfoHeaderType() {
        return new UplinkTransactionInfoHeaderType();
    }

    /**
     * Create an instance of {@link TransactionInfoHeaderType }
     * 
     */
    public TransactionInfoHeaderType createTransactionInfoHeaderType() {
        return new TransactionInfoHeaderType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserIdType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/schemas/UNICA/SOAP/common/v1", name = "xoauth_requestor_id", scope = SimpleOAuthHeaderType.class)
    public JAXBElement<UserIdType> createSimpleOAuthHeaderTypeXoauthRequestorId(UserIdType value) {
        return new JAXBElement<UserIdType>(_SimpleOAuthHeaderTypeXoauthRequestorId_QNAME, UserIdType.class, SimpleOAuthHeaderType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TransactionInfoHeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header", name = "transactionInfoHeader")
    public JAXBElement<TransactionInfoHeaderType> createTransactionInfoHeader(TransactionInfoHeaderType value) {
        return new JAXBElement<TransactionInfoHeaderType>(_TransactionInfoHeader_QNAME, TransactionInfoHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SimpleOAuthHeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers", name = "simpleOAuthHeader")
    public JAXBElement<SimpleOAuthHeaderType> createSimpleOAuthHeader(SimpleOAuthHeaderType value) {
        return new JAXBElement<SimpleOAuthHeaderType>(_SimpleOAuthHeader_QNAME, SimpleOAuthHeaderType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SessionTokenType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/security_headers", name = "sessionToken")
    public JAXBElement<SessionTokenType> createSessionToken(SessionTokenType value) {
        return new JAXBElement<SessionTokenType>(_SessionToken_QNAME, SessionTokenType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServerExceptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults", name = "ServerException")
    public JAXBElement<ServerExceptionType> createServerException(ServerExceptionType value) {
        return new JAXBElement<ServerExceptionType>(_ServerException_QNAME, ServerExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ClientExceptionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v1/faults", name = "ClientException")
    public JAXBElement<ClientExceptionType> createClientException(ClientExceptionType value) {
        return new JAXBElement<ClientExceptionType>(_ClientException_QNAME, ClientExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UplinkTransactionInfoHeaderType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header", name = "uplinkTransactionInfoHeader")
    public JAXBElement<UplinkTransactionInfoHeaderType> createUplinkTransactionInfoHeader(UplinkTransactionInfoHeaderType value) {
        return new JAXBElement<UplinkTransactionInfoHeaderType>(_UplinkTransactionInfoHeader_QNAME, UplinkTransactionInfoHeaderType.class, null, value);
    }

}
