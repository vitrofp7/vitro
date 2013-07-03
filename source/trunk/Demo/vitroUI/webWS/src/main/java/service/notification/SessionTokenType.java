
package service.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * For UNICA APIs access with Login
 * 
 * <p>Java class for SessionTokenType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SessionTokenType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="session_token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="consumer_key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="requestor_id" type="{http://www.telefonica.com/schemas/UNICA/SOAP/common/v1}UserIdType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SessionTokenType", propOrder = {
    "sessionToken",
    "consumerKey",
    "requestorId"
})
public class SessionTokenType {

    @XmlElement(name = "session_token", required = true)
    protected String sessionToken;
    @XmlElement(name = "consumer_key", required = true)
    protected String consumerKey;
    @XmlElement(name = "requestor_id")
    protected UserIdType requestorId;

    /**
     * Gets the value of the sessionToken property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionToken() {
        return sessionToken;
    }

    /**
     * Sets the value of the sessionToken property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionToken(String value) {
        this.sessionToken = value;
    }

    /**
     * Gets the value of the consumerKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConsumerKey() {
        return consumerKey;
    }

    /**
     * Sets the value of the consumerKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConsumerKey(String value) {
        this.consumerKey = value;
    }

    /**
     * Gets the value of the requestorId property.
     * 
     * @return
     *     possible object is
     *     {@link UserIdType }
     *     
     */
    public UserIdType getRequestorId() {
        return requestorId;
    }

    /**
     * Sets the value of the requestorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserIdType }
     *     
     */
    public void setRequestorId(UserIdType value) {
        this.requestorId = value;
    }

}
