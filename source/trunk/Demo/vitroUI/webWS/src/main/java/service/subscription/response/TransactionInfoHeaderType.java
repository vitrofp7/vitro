
package service.subscription.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Includes the Service Identifier, the Application Identifier, the Asset Identifier and a Transaction Identifier
 * 
 * <p>Java class for TransactionInfoHeaderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionInfoHeaderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="servId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="appId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="assetId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="transactionId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="appProviderId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionInfoHeaderType", namespace = "http://www.telefonica.com/wsdl/UNICA/SOAP/common/v2/transaction_info_header", propOrder = {
    "servId",
    "appId",
    "assetId",
    "transactionId",
    "appProviderId"
})
public class TransactionInfoHeaderType {

    protected int servId;
    protected int appId;
    protected int assetId;
    @XmlElement(required = true)
    protected String transactionId;
    @XmlElement(required = true)
    protected String appProviderId;

    /**
     * Gets the value of the servId property.
     * 
     */
    public int getServId() {
        return servId;
    }

    /**
     * Sets the value of the servId property.
     * 
     */
    public void setServId(int value) {
        this.servId = value;
    }

    /**
     * Gets the value of the appId property.
     * 
     */
    public int getAppId() {
        return appId;
    }

    /**
     * Sets the value of the appId property.
     * 
     */
    public void setAppId(int value) {
        this.appId = value;
    }

    /**
     * Gets the value of the assetId property.
     * 
     */
    public int getAssetId() {
        return assetId;
    }

    /**
     * Sets the value of the assetId property.
     * 
     */
    public void setAssetId(int value) {
        this.assetId = value;
    }

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the appProviderId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppProviderId() {
        return appProviderId;
    }

    /**
     * Sets the value of the appProviderId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppProviderId(String value) {
        this.appProviderId = value;
    }

}
