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
package alter.vitro.vgw.service.query.xmlmessages.aggrquery;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for myQueryAggrType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="myQueryAggrType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="message-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="queryDefID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="query-count" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="motesList" type="{}motesListType"/>
 *         &lt;element name="reqFunctionsList" type="{}reqFunctionsListType"/>
 *         &lt;element name="isHistory" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="asynch" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="encrypt" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="dtn" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="security" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="continuation" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "myQueryAggrType", propOrder = {
    "messageType",
    "queryDefID",
    "queryCount",
    "motesList",
    "reqFunctionsList",
    "isHistory",
     "asynch",
     "encrypt",
     "dtn",
     "security",
     "continuation"
})
public class MyQueryAggrType {

    @XmlElement(name = "message-type", required = true)
    protected String messageType;
    @XmlElement(required = true)
    protected String queryDefID;
    @XmlElement(name = "query-count", required = true)
    protected int queryCount;
    @XmlElement(required = true)
    protected MotesListType motesList;
    @XmlElement(required = true)
    protected ReqFunctionsListType reqFunctionsList;
    protected boolean isHistory;
    private boolean asynch;
    private boolean encrypt;
    private boolean dtn;
    private boolean security;
    private boolean continuation;

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
     *     {@link int }
     *     
     */
    public int getQueryCount() {
        return queryCount;
    }

    /**
     * Sets the value of the queryCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link int }
     *     
     */
    public void setQueryCount(int value) {
        this.queryCount = value;
    }

    /**
     * Gets the value of the motesList property.
     * 
     * @return
     *     possible object is
     *     {@link MotesListType }
     *     
     */
    public MotesListType getMotesList() {
        return motesList;
    }

    /**
     * Sets the value of the motesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MotesListType }
     *     
     */
    public void setMotesList(MotesListType value) {
        this.motesList = value;
    }

    /**
     * Gets the value of the reqFunctionsList property.
     * 
     * @return
     *     possible object is
     *     {@link ReqFunctionsListType }
     *     
     */
    public ReqFunctionsListType getReqFunctionsList() {
        return reqFunctionsList;
    }

    /**
     * Sets the value of the reqFunctionsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReqFunctionsListType }
     *     
     */
    public void setReqFunctionsList(ReqFunctionsListType value) {
        this.reqFunctionsList = value;
    }

    /**
     * Gets the value of the isHistory property.
     * 
     */
    public boolean isIsHistory() {
        return isHistory;
    }

    /**
     * Sets the value of the isHistory property.
     * 
     */
    public void setIsHistory(boolean value) {
        this.isHistory = value;
    }


    public boolean isAsynch() {
        return asynch;
    }

    public void setAsynch(boolean asynch) {
        this.asynch = asynch;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    public boolean isDtn() {
        return dtn;
    }

    public void setDtn(boolean dtn) {
        this.dtn = dtn;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public boolean isContinuation() {
        return continuation;
    }

    public void setContinuation(boolean continuation) {
        this.continuation = continuation;
    }
}
