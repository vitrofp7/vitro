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
package alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp package. 
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

    private final static QName _EquivListNodesReq_QNAME = new QName("", "EquivListNodesReq");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EquivListNodesReqType }
     * 
     */
    public EquivListNodesReqType createEquivListNodesReqType() {
        return new EquivListNodesReqType();
    }

    /**
     * Create an instance of {@link EquivNodesListItemType }
     * 
     */
    public EquivNodesListItemType createEquivNodesListItemType() {
        return new EquivNodesListItemType();
    }

    /**
     * Create an instance of {@link NodeVecType }
     * 
     */
    public NodeVecType createNodeVecType() {
        return new NodeVecType();
    }

    /**
     * Create an instance of {@link EquivNodesListType }
     * 
     */
    public EquivNodesListType createEquivNodesListType() {
        return new EquivNodesListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EquivListNodesReqType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "EquivListNodesReq")
    public JAXBElement<EquivListNodesReqType> createEquivListNodesReq(EquivListNodesReqType value) {
        return new JAXBElement<EquivListNodesReqType>(_EquivListNodesReq_QNAME, EquivListNodesReqType.class, null, value);
    }

}
