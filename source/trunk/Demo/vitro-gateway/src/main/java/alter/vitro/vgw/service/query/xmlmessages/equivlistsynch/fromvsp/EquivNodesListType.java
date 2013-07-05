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
package alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for equivNodesListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="equivNodesListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="equivNodesListItem" type="{}equivNodesListItemType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "equivNodesListType", propOrder = {
    "equivNodesListItem"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:34:32+02:00", comments = "JAXB RI v2.2.4-2")
public class EquivNodesListType {

    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:34:32+02:00", comments = "JAXB RI v2.2.4-2")
    protected List<EquivNodesListItemType> equivNodesListItem;

    /**
     * Gets the value of the equivNodesListItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the equivNodesListItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEquivNodesListItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EquivNodesListItemType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:34:32+02:00", comments = "JAXB RI v2.2.4-2")
    public List<EquivNodesListItemType> getEquivNodesListItem() {
        if (equivNodesListItem == null) {
            equivNodesListItem = new ArrayList<EquivNodesListItemType>();
        }
        return this.equivNodesListItem;
    }

}
