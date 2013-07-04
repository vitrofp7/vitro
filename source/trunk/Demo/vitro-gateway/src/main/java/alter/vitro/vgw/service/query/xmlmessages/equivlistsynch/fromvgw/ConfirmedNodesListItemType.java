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
package alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmedNodesListItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmedNodesListItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ofRemoteTimestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmedNodesListItemType", propOrder = {
    "listId",
    "ofRemoteTimestamp"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
public class ConfirmedNodesListItemType {

    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
    protected String listId;
    @XmlElement(required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
    protected String ofRemoteTimestamp;

    /**
     * Gets the value of the listId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
    public String getListId() {
        return listId;
    }

    /**
     * Sets the value of the listId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
    public void setListId(String value) {
        this.listId = value;
    }

    /**
     * Gets the value of the ofRemoteTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
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
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:59+02:00", comments = "JAXB RI v2.2.4-2")
    public void setOfRemoteTimestamp(String value) {
        this.ofRemoteTimestamp = value;
    }

}
