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
 * ResultAggrStruct.java
 *
 */

package vitro.vspEngine.service.query;

import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.service.engine.UserNode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;


/**
 * Describes the "out" tag structure in the following schema for results to aggregate queries
 * <p/>
 * (To do) An Aggregate public Response Message can have multiple result fields (if multiple functions where specified in the query).
 * (To do) Add an extra data-type field. We need it (and we can get it from the gateway) to aggregate locally the data. Of course the User Peer
 * has this info, but it would be optimal to aggregate as much as we can locally, before sending data!
 * <pre>
 * Structure follows an XML Format:
 * 		&lt;reqFunction&gt;
 *                  &lt;fid&gt;3&lt;/fid&gt;
 *                  &lt;outList&gt;
 *                      &lt;<b>out</b>&gt;
 *                          &lt;mid&gt;2&lt;/mid&gt; (or -1 if aggregate function)
 *                          &lt;sid&gt;1&lt;/sid&gt;
 *                          &lt;val&gt;1112&lt;/val&gt; (will be an entire file of CSV (comma separated) values if History Of Values Function)
 *                          &lt;timePeriod&gt;
 *                              &lt;from&gt;timestamp1&lt;/from&gt;
 *                              &lt;to&gt;timestamp2&lt;/to&gt;
 *                          &lt;/timePeriod&gt;
 *                          &lt;NumOfAggrVal&gt;4&lt;/ NumOfAggrVal&gt; (Has meaning when mid = -1 or even when the function is History)
 *                      &lt;<b>/out</b>&gt;
 *                      &lt;out&gt;
 *                          .
 *                          .
 *                      &lt;/out&gt;
 *                  &lt;/outList&gt;
 *              &lt;/reqFunction&gt;
 *  </pre>
 *
 * @author antoniou
 */
public class ResultAggrStruct {

    private static final String outRootTag = "out";
    private static final String moteIdTag = "mid";
    private static final String sensorModelIdTag = "sid";
    private static final String valueTag = "val";
    private static final String numofAggrValuesTag = "NumOfAggrVal";
    private static final String timeIntervalTag = "timePeriod";

    public static final String MidSpecialForAggregateMultipleValues = "-1";

    String mid;
    String sid;
    String val;
    int numofAggrValues;
    TimeIntervalStructure tis;

    /**
     * Creates a new instance of ResultAggrStruct
     *
     * @param mid             A string with the mote id
     * @param sid             The sensor model id
     * @param val             the value (aggregated or not) of a reading/ of multiple readings.
     * @param numofAggrValues
     * @param tis             A timeIntervalStructure that shows the interval in which this motes' sensors value(s) were read.
     */
    public ResultAggrStruct(String mid, String sid, String val, int numofAggrValues, TimeIntervalStructure tis) {
        this.mid = mid;
        this.sid = sid;
        this.val = val;
        this.numofAggrValues = numofAggrValues;
        if (tis == null) {
            this.tis = new TimeIntervalStructure();
        } else {
            this.tis = tis;
        }
    }

    /**
     * Copy constructor.
     */
    public ResultAggrStruct(ResultAggrStruct srcRas) {
        this(srcRas.mid, srcRas.sid, srcRas.val, srcRas.numofAggrValues, srcRas.tis);
    }

    /**
     * Creates a new instance of  ResultAggrStruct from the corresponding part of an XML file that describes the given Result per mote+sensor
     *
     * @param givenCursor
     */
    public ResultAggrStruct(SMInputCursor givenCursor) {
        this.mid = "";
        this.sid =  SensorModel.invalidId;
        this.val = "";
        this.numofAggrValues = -1;
        this.tis = new TimeIntervalStructure();

        String tmpFuncDesc = "";

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(ResultAggrStruct.getMoteIdTag().toLowerCase() ))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.mid = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(ResultAggrStruct.getSensorModelIdTag().toLowerCase() ))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.sid = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(ResultAggrStruct.getNumofAggrValuesTag().toLowerCase() ))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.numofAggrValues = Integer.parseInt(childInElement2.getText());
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(ResultAggrStruct.getValueTag().toLowerCase() ))
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.val = childInElement2.getText();
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(ResultAggrStruct.getTimeIntervalTag().toLowerCase() ))
                    {
                        this.tis.parseTimeInterval(childInElement);
                    }
                }
            }
        } catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }

    }

    public static String getOutRootTag() {
        return outRootTag;
    }

    public static String getMoteIdTag() {
        return moteIdTag;
    }

    public static String getSensorModelIdTag() {
        return sensorModelIdTag;
    }

    public static String getValueTag() {
        return valueTag;
    }

    public static String getNumofAggrValuesTag() {
        return numofAggrValuesTag;
    }

    public static String getTimeIntervalTag() {
        return timeIntervalTag;
    }

    public String getMid() {
        return mid;
    }

    public String getSid() {
        return sid;
    }

    public int getNumofAggrValues() {
        return numofAggrValues;
    }

    public TimeIntervalStructure getTis() {
        return tis;
    }

    public String getVal() {
        return val;
    }


    /**
     *
     * @param document
     * @param parElement  the parent element (if not the root) in the given XML document.
     * @param tempFlag  for visualization don't show sensor model SN but the capability name!
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement, boolean tempFlag) {
        try{

            SMOutputElement tmpElementOuter;
            SMOutputElement tmpElement1;

            if (parElement != null) {
                tmpElementOuter = parElement.addElement(ResultAggrStruct.getOutRootTag());
            }
            else {
                tmpElementOuter =  document.addElement(ResultAggrStruct.getOutRootTag());
            }

            tmpElement1 =  tmpElementOuter.addElement(ResultAggrStruct.getMoteIdTag());
            tmpElement1.addCharacters(this.getMid());

            tmpElement1 =  tmpElementOuter.addElement(ResultAggrStruct.getSensorModelIdTag());
            if(!tempFlag)
                tmpElement1.addCharacters(this.getSid());
             else
            {
                String capName = Capability.getNameFromSensorModel(this.getSid());
                tmpElement1.addCharacters(capName);
            }

            tmpElement1 =  tmpElementOuter.addElement(ResultAggrStruct.getNumofAggrValuesTag());
            tmpElement1.addCharacters(Integer.toString(this.getNumofAggrValues()) );

            tmpElement1 =  tmpElementOuter.addElement(ResultAggrStruct.getValueTag());
            tmpElement1.addCharacters(this.getVal());

            if (this.tis != null) {
                //tmpElement1 =  tmpElementOuter.addElement(ResultAggrStruct.getTimeIntervalTag());
                //this.getTis().createInfoInDocument(tmpElement1);
                this.getTis().createInfoInDocument(document, tmpElementOuter);
            }

        } catch(Exception e) {
            return;
        }
    }

}
