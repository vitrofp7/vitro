/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
package alter.vitro.vgw.service.query.xmlmessages.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for servContReplcItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="servContReplcItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nodeSourceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nodeReplmntId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="capabilityId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "servContReplcItemType", propOrder = {
    "nodeSourceId",
    "nodeReplmntId",
    "capabilityId"
})
public class ServContReplcItemType {

    @XmlElement(required = true)
    protected String nodeSourceId;
    @XmlElement(required = true)
    protected String nodeReplmntId;
    @XmlElement(required = true)
    protected String capabilityId;

    /**
     * Gets the value of the nodeSourceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeSourceId() {
        return nodeSourceId;
    }

    /**
     * Sets the value of the nodeSourceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeSourceId(String value) {
        this.nodeSourceId = value;
    }

    /**
     * Gets the value of the nodeReplmntId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNodeReplmntId() {
        return nodeReplmntId;
    }

    /**
     * Sets the value of the nodeReplmntId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNodeReplmntId(String value) {
        this.nodeReplmntId = value;
    }

    /**
     * Gets the value of the capabilityId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCapabilityId() {
        return capabilityId;
    }

    /**
     * Sets the value of the capabilityId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCapabilityId(String value) {
        this.capabilityId = value;
    }

}
