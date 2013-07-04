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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EquivListNodesResp_QNAME = new QName("", "EquivListNodesResp");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EquivListNodesRespType }
     * 
     */
    public EquivListNodesRespType createEquivListNodesRespType() {
        return new EquivListNodesRespType();
    }

    /**
     * Create an instance of {@link ConfirmedNodesListItemType }
     * 
     */
    public ConfirmedNodesListItemType createConfirmedNodesListItemType() {
        return new ConfirmedNodesListItemType();
    }

    /**
     * Create an instance of {@link ConfirmedNodesListType }
     * 
     */
    public ConfirmedNodesListType createConfirmedNodesListType() {
        return new ConfirmedNodesListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EquivListNodesRespType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "EquivListNodesResp")
    public JAXBElement<EquivListNodesRespType> createEquivListNodesResp(EquivListNodesRespType value) {
        return new JAXBElement<EquivListNodesRespType>(_EquivListNodesResp_QNAME, EquivListNodesRespType.class, null, value);
    }

}
