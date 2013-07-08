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
package alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnableNodesRespType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnableNodesRespType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vgwId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="confirmedEnabledNodesList" type="{}confirmedEnabledNodesListType"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EnableNodesRespType", propOrder = {
    "messageType",
    "vgwId",
    "confirmedEnabledNodesList",
    "timestamp"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
public class EnableNodesRespType {

    @XmlElement(name = "message-type", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String messageType;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String vgwId;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected ConfirmedEnabledNodesListType confirmedEnabledNodesList;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String timestamp;

    /**
     * Gets the value of the messageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setMessageType(String value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the vgwId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getVgwId() {
        return vgwId;
    }

    /**
     * Sets the value of the vgwId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setVgwId(String value) {
        this.vgwId = value;
    }

    /**
     * Gets the value of the confirmedEnabledNodesList property.
     * 
     * @return
     *     possible object is
     *     {@link ConfirmedEnabledNodesListType }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public ConfirmedEnabledNodesListType getConfirmedEnabledNodesList() {
        return confirmedEnabledNodesList;
    }

    /**
     * Sets the value of the confirmedEnabledNodesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfirmedEnabledNodesListType }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setConfirmedEnabledNodesList(ConfirmedEnabledNodesListType value) {
        this.confirmedEnabledNodesList = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

}
