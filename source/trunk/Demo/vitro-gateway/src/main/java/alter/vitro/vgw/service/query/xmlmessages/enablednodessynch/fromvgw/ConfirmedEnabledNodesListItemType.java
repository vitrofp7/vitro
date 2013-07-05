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
 * <p>Java class for confirmedEnabledNodesListItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmedEnabledNodesListItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nodeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ofRemoteTimestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="gwInitFlag" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmedEnabledNodesListItemType", propOrder = {
    "nodeId",
    "status",
    "ofRemoteTimestamp",
    "gwInitFlag"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
public class ConfirmedEnabledNodesListItemType {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String nodeId;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String status;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String ofRemoteTimestamp;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected String gwInitFlag;

    /**
     * Gets the value of the nodeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getNodeId() {
        return nodeId;
    }

    /**
     * Sets the value of the nodeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setNodeId(String value) {
        this.nodeId = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the ofRemoteTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getOfRemoteTimestamp() {
        return ofRemoteTimestamp;
    }

    /**
     * Sets the value of the ofRemoteTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setOfRemoteTimestamp(String value) {
        this.ofRemoteTimestamp = value;
    }

    /**
     * Gets the value of the gwInitFlag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public String getGwInitFlag() {
        return gwInitFlag;
    }

    /**
     * Sets the value of the gwInitFlag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public void setGwInitFlag(String value) {
        this.gwInitFlag = value;
    }

}
