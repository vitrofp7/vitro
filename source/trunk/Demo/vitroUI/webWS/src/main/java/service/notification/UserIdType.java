
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

package service.notification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserIdType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserIdType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="phoneNumber" type="{http://www.telefonica.com/schemas/UNICA/SOAP/common/v1}E164Type"/>
 *         &lt;element name="anyUri" type="{http://www.w3.org/2001/XMLSchema}anyURI"/>
 *         &lt;element name="ipAddress" type="{http://www.telefonica.com/schemas/UNICA/SOAP/common/v1}IpAddressType"/>
 *         &lt;element name="alias" type="{http://www.telefonica.com/schemas/UNICA/SOAP/common/v1}AliasType"/>
 *         &lt;element name="otherId" type="{http://www.telefonica.com/schemas/UNICA/SOAP/common/v1}OtherIdType"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserIdType", propOrder = {
    "phoneNumber",
    "anyUri",
    "ipAddress",
    "alias",
    "otherId"
})
public class UserIdType {

    protected String phoneNumber;
    @XmlSchemaType(name = "anyURI")
    protected String anyUri;
    protected IpAddressType ipAddress;
    protected String alias;
    protected OtherIdType otherId;

    /**
     * Gets the value of the phoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the value of the phoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhoneNumber(String value) {
        this.phoneNumber = value;
    }

    /**
     * Gets the value of the anyUri property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnyUri() {
        return anyUri;
    }

    /**
     * Sets the value of the anyUri property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnyUri(String value) {
        this.anyUri = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link IpAddressType }
     *     
     */
    public IpAddressType getIpAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link IpAddressType }
     *     
     */
    public void setIpAddress(IpAddressType value) {
        this.ipAddress = value;
    }

    /**
     * Gets the value of the alias property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the value of the alias property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlias(String value) {
        this.alias = value;
    }

    /**
     * Gets the value of the otherId property.
     * 
     * @return
     *     possible object is
     *     {@link OtherIdType }
     *     
     */
    public OtherIdType getOtherId() {
        return otherId;
    }

    /**
     * Sets the value of the otherId property.
     * 
     * @param value
     *     allowed object is
     *     {@link OtherIdType }
     *     
     */
    public void setOtherId(OtherIdType value) {
        this.otherId = value;
    }

}
