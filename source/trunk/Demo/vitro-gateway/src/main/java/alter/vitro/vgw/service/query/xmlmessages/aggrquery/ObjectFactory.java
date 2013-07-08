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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the alter.vitro.vgw.service.query.xmlmessages.aggrquery package.
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

    private final static QName _MyQueryAggr_QNAME = new QName("", "myQueryAggr");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: alter.vitro.vgw.service.query.xmlmessages.aggrquery
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MyQueryAggrType }
     * 
     */
    public MyQueryAggrType createMyQueryAggrType() {
        return new MyQueryAggrType();
    }

    /**
     * Create an instance of {@link FuncOnSensorType }
     * 
     */
    public FuncOnSensorType createFuncOnSensorType() {
        return new FuncOnSensorType();
    }

    /**
     * Create an instance of {@link ReqFunctionType }
     * 
     */
    public ReqFunctionType createReqFunctionType() {
        return new ReqFunctionType();
    }

    /**
     * Create an instance of {@link FuncOnSensorListType }
     * 
     */
    public FuncOnSensorListType createFuncOnSensorListType() {
        return new FuncOnSensorListType();
    }

    /**
     * Create an instance of {@link ThresholdFieldType }
     * 
     */
    public ThresholdFieldType createThresholdFieldType() {
        return new ThresholdFieldType();
    }

    /**
     * Create an instance of {@link MotesListType }
     * 
     */
    public MotesListType createMotesListType() {
        return new MotesListType();
    }

    /**
     * Create an instance of {@link MoteType }
     * 
     */
    public MoteType createMoteType() {
        return new MoteType();
    }

    /**
     * Create an instance of {@link TimePeriodType }
     * 
     */
    public TimePeriodType createTimePeriodType() {
        return new TimePeriodType();
    }

    /**
     * Create an instance of {@link ReqFunctionsListType }
     * 
     */
    public ReqFunctionsListType createReqFunctionsListType() {
        return new ReqFunctionsListType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link MyQueryAggrType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "myQueryAggr")
    public JAXBElement<MyQueryAggrType> createMyQueryAggr(MyQueryAggrType value) {
        return new JAXBElement<MyQueryAggrType>(_MyQueryAggr_QNAME, MyQueryAggrType.class, null, value);
    }

}
