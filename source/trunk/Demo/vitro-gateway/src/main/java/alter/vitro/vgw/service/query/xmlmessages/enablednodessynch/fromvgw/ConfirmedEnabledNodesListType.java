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

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for confirmedEnabledNodesListType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="confirmedEnabledNodesListType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="confirmedEnabledNodesListItem" type="{}confirmedEnabledNodesListItemType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "confirmedEnabledNodesListType", propOrder = {
    "confirmedEnabledNodesListItem"
})
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
public class ConfirmedEnabledNodesListType {

    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    protected List<ConfirmedEnabledNodesListItemType> confirmedEnabledNodesListItem;

    /**
     * Gets the value of the confirmedEnabledNodesListItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the confirmedEnabledNodesListItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getConfirmedEnabledNodesListItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ConfirmedEnabledNodesListItemType }
     * 
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2013-04-16T11:53:10+02:00", comments = "JAXB RI v2.2.4-2")
    public List<ConfirmedEnabledNodesListItemType> getConfirmedEnabledNodesListItem() {
        if (confirmedEnabledNodesListItem == null) {
            confirmedEnabledNodesListItem = new ArrayList<ConfirmedEnabledNodesListItemType>();
        }
        return this.confirmedEnabledNodesListItem;
    }

}
