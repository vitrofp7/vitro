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
/*
 * QueriedMoteAndSensors.java
 *
 */

package vitro.vspEngine.service.query;


import java.util.Vector;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

/**
 * <pre>
 * This class describes the &quot;mote&quot; elememt structure of the PublicQueryAggrMsg.
 *          &lt;mote&gt;
 *               &lt;moteid&gt;1&lt;/moteid&gt;
 *               &lt;funcOnSensorList&gt;
 *                  &lt;funcOnSensor&gt;
 *                      &lt;sensorModelid&gt;4&lt;/sensorModelid&gt;
 *                      &lt;fid&gt;1&lt;/fid&gt;
 *                      &lt;fid&gt;3&lt;/fid&gt;
 *                      &lt;fid&gt;4&lt;/fid&gt;
 *                  &lt;/funcOnSensor&gt;
 *                  &lt;funcOnSensor&gt;
 *                      &lt;sensorModelid&gt;2&lt;/sensorModelid&gt;
 *                      &lt;fid&gt;2&lt;/fid&gt;
 *                      &lt;fid&gt;5&lt;/fid&gt;
 *                  &lt;/funcOnSensor&gt;
 *               &lt;/funcOnSensorList&gt;
 *          &lt;/mote&gt;
 * </pre>
 *
 * @author antoniou
 */
public class QueriedMoteAndSensors {

    private String moteId;
    private Vector<ReqSensorAndFunctions> queriedSensorIdsAndFuncVec;

    private static final String moteIdTag = "moteid";
    private static final String funcOnSensorTag = "funcOnSensor";
    private static final String funcOnSensorListTag = "funcOnSensorList";


    /**
     * Creates a new instance of QueriedMotesAndSensors.
     * Default constructor.
     */
    public QueriedMoteAndSensors() {
        this.moteId = "unknown";
        queriedSensorIdsAndFuncVec = new Vector<ReqSensorAndFunctions>();
    }

    /**
     * Creates a new instance of QueriedMotesAndSensors.
     *
     * @param mId Sets the mote id for this queried mote
     * @param queriedSensorIdsAndFunctionsVec
     *            Sets the Vector of mappings of Sensor models to Function ids.
     */
    public QueriedMoteAndSensors(String mId, Vector<ReqSensorAndFunctions> queriedSensorIdsAndFunctionsVec) {
        this.moteId = mId;
        this.queriedSensorIdsAndFuncVec = queriedSensorIdsAndFunctionsVec;
    }


    /**
     * Handles the "mote" tag and its children elements.
     */
    public QueriedMoteAndSensors(SMInputCursor givenCursor) {
        this.moteId = "unknown";
        String tmpMoteId = "";
        queriedSensorIdsAndFuncVec = new Vector<ReqSensorAndFunctions>();

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(QueriedMoteAndSensors.moteIdTag.toLowerCase() ) && (tmpMoteId.equals("")))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpMoteId = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(QueriedMoteAndSensors.getFuncOnSensorListTag().toLowerCase() ) )
                    {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(!childInElement2.getCurrEvent().hasText())
                            {
                                if (childInElement2.getLocalName().toLowerCase().equals(QueriedMoteAndSensors.getFuncOnSensorTag().toLowerCase())) {
                                    this.queriedSensorIdsAndFuncVec.addElement(new ReqSensorAndFunctions(childInElement2));
                                }
                            }
                        }
                    }
                }
            }
        }  catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }

        if (!tmpMoteId.equals("")) {
            this.moteId = tmpMoteId;
            //System.out.println("registered a mote id ::"+moteId);
        }
    }


    /**
     * Creates XML structured info on this object, under the root Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document.
     * @param parElement the parent element (if not the root) in the given XML document. Null means the root element.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(PublicQueryAggrMsg.getMoteTag());
            }
            else {
                tmpElementOuter =  document.addElement(PublicQueryAggrMsg.getMoteTag());
            }

            tmpElement1 = tmpElementOuter.addElement(QueriedMoteAndSensors.moteIdTag) ;
            tmpElement1.addCharacters( this.moteId);

            if (this.queriedSensorIdsAndFuncVec != null && this.queriedSensorIdsAndFuncVec.size() > 0) {
                tmpElement1 = tmpElementOuter.addElement(QueriedMoteAndSensors.getFuncOnSensorListTag());
                for (int k = 0; k < this.queriedSensorIdsAndFuncVec.size(); k++) {
                    this.queriedSensorIdsAndFuncVec.get(k).createInfoInDocument( document, tmpElement1);
                }
            }

        } catch(Exception e) {
            return;
        }
    }

    /**
     * Returns the FuncOnSensor tag name in the xml structure of the query message
     *
     * @return String with the FuncOnSensor tag name
     */
    public static String getFuncOnSensorTag() {
        return funcOnSensorTag;
    }

    /**
     * Returns the FuncOnSensorList tag name in the xml structure of the query message
     *
     * @return String with the FuncOnSensorList tag name
     */
    public static String getFuncOnSensorListTag() {
        return funcOnSensorListTag;
    }


    /**
     * Returns the mote id of this mote.
     *
     * @return the mote id of this mote
     */
    public String getMoteId() {
        return moteId;
    }

    /**
     * Returns the vector of ReqSensorAndFunctions objects associated with this mote
     *
     * @return the vector of ReqSensorAndFunctions objects associated with this mote.
     */
    public Vector<ReqSensorAndFunctions> getQueriedSensorIdsAndFuncVec() {
        return queriedSensorIdsAndFuncVec;
    }

    /**
     * Sets the mote ID of this queried mote
     *
     * @param moteId The mote id
     */
    public void setMoteId(String moteId) {
        this.moteId = moteId;
    }

    /**
     * Sets the vector that maps Sensor Models to selected unique function ids.
     *
     * @param queriedSensorIdsAndFuncVec a Vector with sensor Models correlated to unique function ids.
     */
    public void setQueriedSensorIdsAndFuncVec(Vector<ReqSensorAndFunctions> queriedSensorIdsAndFuncVec) {
        this.queriedSensorIdsAndFuncVec = queriedSensorIdsAndFuncVec;
    }

}
