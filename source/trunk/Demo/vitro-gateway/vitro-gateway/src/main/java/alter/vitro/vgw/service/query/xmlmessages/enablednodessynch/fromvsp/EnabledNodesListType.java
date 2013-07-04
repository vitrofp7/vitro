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
package alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvsp;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for enabledNodesListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="enabledNodesListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="enabledNodesListItem" type="{}enabledNodesListItemType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "enabledNodesListType", propOrder = {
    "enabledNodesListItem"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T09:37:22+02:00", comments = "JAXB RI v2.2.4-2")
public class EnabledNodesListType {

    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T09:37:22+02:00", comments = "JAXB RI v2.2.4-2")
    protected List<EnabledNodesListItemType> enabledNodesListItem;

    /**
     * Gets the value of the enabledNodesListItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the enabledNodesListItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnabledNodesListItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EnabledNodesListItemType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T09:37:22+02:00", comments = "JAXB RI v2.2.4-2")
    public List<EnabledNodesListItemType> getEnabledNodesListItem() {
        if (enabledNodesListItem == null) {
            enabledNodesListItem = new ArrayList<EnabledNodesListItemType>();
        }
        return this.enabledNodesListItem;
    }

}
