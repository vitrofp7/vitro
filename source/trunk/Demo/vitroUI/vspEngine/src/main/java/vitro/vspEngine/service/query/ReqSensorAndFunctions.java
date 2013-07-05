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
/*
 * ReqSensorAndFunctions.java
 *
 */

package vitro.vspEngine.service.query;

import java.util.Vector;

import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.logic.model.SensorModel;

/**
 * <pre>
 * This class describes the &quot;funcOnSensor&quot; elememt structure of the PublicQueryAggrMsg.*
 * <p/>
 *               &lt;funcOnSensor&gt;
 *                  &lt;sensorModelid&gt;4&lt;/sensorModelid&gt;
 *                  &lt;fid&gt;1&lt;/fid&gt;
 *                  &lt;fid&gt;3&lt;/fid&gt;
 *                  &lt;fid&gt;4&lt;/fid&gt;
 *               &lt;/funcOnSensor&gt;
 * </pre>
 *
 * @author antoniou
 */
public class ReqSensorAndFunctions {

    private String sensorModelId;
    private Vector<Integer> functionsOverSensorModelVec;

    private static final String sensorModelidTag = "sensorModelid";
    private static final String functionIdTag = "fid";

    /**
     * Creates a new instance of ReqSensorAndFunctions
     */
    public ReqSensorAndFunctions() {
        sensorModelId =  SensorModel.invalidId;
        functionsOverSensorModelVec = new Vector<Integer>();
    }

    /**
     * Creates a new instance of ReqSensorAndFunctions
     *
     * @param smid              Sets the sensor model id for this queries sensor model
     * @param funcOverSModelVec Sets the vector of ids of unique functions to be applied to this sensor model.
     */
    public ReqSensorAndFunctions(String smid, Vector<Integer> funcOverSModelVec) {
        sensorModelId = smid;
        functionsOverSensorModelVec = funcOverSModelVec;
    }

    /**
     * Handles the "funcOnSensor" tag and its children elements.
     */
    public ReqSensorAndFunctions(SMInputCursor givenCursor) {
        this.sensorModelId =   SensorModel.invalidId;
        String tmpSmID = SensorModel.invalidId;
        functionsOverSensorModelVec = new Vector<Integer>();

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(ReqSensorAndFunctions.sensorModelidTag.toLowerCase() ) && (tmpSmID.equals( SensorModel.invalidId)))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpSmID = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(ReqSensorAndFunctions.functionIdTag.toLowerCase() ) )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.functionsOverSensorModelVec.addElement(Integer.valueOf(childInElement2.getText()));
                                break;
                            }
                        }
                    }
                }
            }
        }  catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }

    }

    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(QueriedMoteAndSensors.getFuncOnSensorTag());
            }
            else {
                tmpElementOuter =  document.addElement(QueriedMoteAndSensors.getFuncOnSensorTag());
            }

            tmpElement1 = tmpElementOuter.addElement(ReqSensorAndFunctions.sensorModelidTag) ;
            tmpElement1.addCharacters( this.sensorModelId);

            if (this.functionsOverSensorModelVec != null) {
                for (int k = 0; k < this.functionsOverSensorModelVec.size(); k++) {
                    Integer tmpFid = this.functionsOverSensorModelVec.get(k);
                    tmpElement1 = tmpElementOuter.addElement(ReqSensorAndFunctions.functionIdTag) ;
                    tmpElement1.addCharacters(tmpFid.toString());
                }
            }

        } catch(Exception e) {
            return;
        }
    }

    /**
     * Retrieves the vector of unique function Ids that are referenced in this sensor model.
     *
     * @return The vector of unique function Ids that are referenced in this sensor model.
     */
    public Vector<Integer> getFunctionsOverSensorModelVec() {
        return functionsOverSensorModelVec;
    }

    /**
     * Retrieves the sensor model id of this object.
     *
     * @return the sensor model id of this object.
     */
    public String getSensorModelId() {
        return sensorModelId;
    }


}
