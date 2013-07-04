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
 * <p>Java class for QueryResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QueryResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="queryDefID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="query-count" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responderPeerID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="responderName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="reqFunctionsList" type="{}respFunctionsListType"/>
 *         &lt;element name="servContList" type="{}servContinuationListType"/>
 *         &lt;element name="serviceDeployStatus" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryResponseType", propOrder = {
    "messageType",
    "queryDefID",
    "queryCount",
    "responderPeerID",
    "responderName",
    "reqFunctionsList",
    "servContList",
    "serviceDeployStatus"
})
public class QueryResponseType {

    @XmlElement(name = "message-type", required = true)
    protected String messageType;
    @XmlElement(required = true)
    protected String queryDefID;
    @XmlElement(name = "query-count", required = true)
    protected String queryCount;
    @XmlElement(required = true)
    protected String responderPeerID;
    @XmlElement(required = true)
    protected String responderName;
    @XmlElement(required = true)
    protected RespFunctionsListType reqFunctionsList;
    @XmlElement(required = true)
    protected ServContinuationListType servContList;
    @XmlElement(required = true)
    protected String serviceDeployStatus;

    /**
     * Gets the value of the messageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
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
    public void setMessageType(String value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the queryDefID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryDefID() {
        return queryDefID;
    }

    /**
     * Sets the value of the queryDefID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryDefID(String value) {
        this.queryDefID = value;
    }

    /**
     * Gets the value of the queryCount property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryCount() {
        return queryCount;
    }

    /**
     * Sets the value of the queryCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryCount(String value) {
        this.queryCount = value;
    }

    /**
     * Gets the value of the responderPeerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponderPeerID() {
        return responderPeerID;
    }

    /**
     * Sets the value of the responderPeerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponderPeerID(String value) {
        this.responderPeerID = value;
    }

    /**
     * Gets the value of the responderName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponderName() {
        return responderName;
    }

    /**
     * Sets the value of the responderName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponderName(String value) {
        this.responderName = value;
    }

    /**
     * Gets the value of the reqFunctionsList property.
     * 
     * @return
     *     possible object is
     *     {@link RespFunctionsListType }
     *     
     */
    public RespFunctionsListType getReqFunctionsList() {
        return reqFunctionsList;
    }

    /**
     * Sets the value of the reqFunctionsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespFunctionsListType }
     *     
     */
    public void setReqFunctionsList(RespFunctionsListType value) {
        this.reqFunctionsList = value;
    }

    /**
     * Gets the value of the servContList property.
     * 
     * @return
     *     possible object is
     *     {@link ServContinuationListType }
     *     
     */
    public ServContinuationListType getServContList() {
        return servContList;
    }

    /**
     * Sets the value of the servContList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServContinuationListType }
     *     
     */
    public void setServContList(ServContinuationListType value) {
        this.servContList = value;
    }

    /**
     * Gets the value of the serviceDeployStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceDeployStatus() {
        return serviceDeployStatus;
    }

    /**
     * Sets the value of the serviceDeployStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceDeployStatus(String value) {
        this.serviceDeployStatus = value;
    }

}
