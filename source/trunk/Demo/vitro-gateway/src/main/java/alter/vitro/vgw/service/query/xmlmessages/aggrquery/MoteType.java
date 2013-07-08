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
 * <p>Java class for moteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="moteType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="moteid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="funcOnSensorList" type="{}funcOnSensorListType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "moteType", propOrder = {
    "moteid",
    "funcOnSensorList"
})
public class MoteType {

    @XmlElement(required = true)
    protected String moteid;
    @XmlElement(required = true)
    protected FuncOnSensorListType funcOnSensorList;

    /**
     * Gets the value of the moteid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoteid() {
        return moteid;
    }

    /**
     * Sets the value of the moteid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoteid(String value) {
        this.moteid = value;
    }

    /**
     * Gets the value of the funcOnSensorList property.
     * 
     * @return
     *     possible object is
     *     {@link FuncOnSensorListType }
     *     
     */
    public FuncOnSensorListType getFuncOnSensorList() {
        return funcOnSensorList;
    }

    /**
     * Sets the value of the funcOnSensorList property.
     * 
     * @param value
     *     allowed object is
     *     {@link FuncOnSensorListType }
     *     
     */
    public void setFuncOnSensorList(FuncOnSensorListType value) {
        this.funcOnSensorList = value;
    }

}
