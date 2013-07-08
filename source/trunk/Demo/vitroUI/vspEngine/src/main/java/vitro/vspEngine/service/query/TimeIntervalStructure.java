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
 * TimeIntervalStructure.java
 *
 */

package vitro.vspEngine.service.query;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;


/**
 * Holds a TimeInterval object that will be used with the Required Function to be applied on the data returned from the query
 *
 * @author antoniou
 */
public class TimeIntervalStructure {
    private Logger logger = Logger.getLogger(TimeIntervalStructure.class);
    private static final String timePeriodRootTag = "timePeriod";
    private static final String fromTag = "from";
    private static final String toTag = "to";

    Timestamp timeperiod_from;
    Timestamp timeperiod_to;
    boolean timestampFromDefined;
    boolean timestampToDefined;

    /**
     * Creates a default empty TimeInterval object
     */
    public TimeIntervalStructure() {
        timeperiod_from = null;
        timeperiod_to = null;
        timestampFromDefined = false;
        timestampToDefined = false;
    }

    /**
     * Constructs a TimeInterval object from the given parametes
     * If both parameters are null, then it assumed that we want all values returned.
     * If both parameters have the same value, then  the Interval is essentially a specific point in time.
     *
     * @param tsfrom a timestamp that defines the starting point in time (it can be null, so no limit is set as a starting point)
     * @param tsto   a timestamp that defines the finishing point in time (it can be null, so no limit is set as an ending point - practical limit it current time)
     */
    public TimeIntervalStructure(Timestamp tsfrom, Timestamp tsto) {
        timeperiod_from = null;
        timeperiod_to = null;
        timestampFromDefined = false;
        timestampToDefined = false;
        if (tsfrom != null) {
            timeperiod_from = tsfrom;
            timestampFromDefined = true;
        }

        if (tsto != null) {
            timeperiod_to = tsto;
            timestampToDefined = true;
        }
    }

    public static String getTimePeriodRootTag() {
        return timePeriodRootTag;
    }

    public static String getFromTag() {
        return fromTag;
    }

    public static String getToTag() {
        return toTag;
    }


    /**
     * Update time interval from XML element.
     * @param givenCursor
     */
    void parseTimeInterval(SMInputCursor givenCursor) {

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().toLowerCase().equals(TimeIntervalStructure.getFromTag().toLowerCase()) && !isTimestampFromDefined() )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.timeperiod_from = Timestamp.valueOf(childInElement2.getText());
                                timestampFromDefined = true;
                                break;
                            }
                        }
                    }
                    else if(childInElement.getLocalName().toLowerCase().equals(TimeIntervalStructure.getToTag().toLowerCase()) && !isTimestampToDefined() )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                this.timeperiod_to = Timestamp.valueOf(childInElement2.getText());
                                timestampToDefined = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }
    }

    /**
     *
     * Creates XML structured info on this TimeInterval object, under the specified Element, in the specified StructuredDocument
     *
     * @param parElement the parent element in the given XML document.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (this.isAnyTimestampDefined()) {
                if (parElement != null) {
                    tmpElementOuter = parElement.addElement(TimeIntervalStructure.getTimePeriodRootTag());
                }
                else {
                    tmpElementOuter = document.addElement(TimeIntervalStructure.getTimePeriodRootTag());
                }


                if (this.isTimestampFromDefined()) {
                    tmpElement1 =  tmpElementOuter.addElement(TimeIntervalStructure.getFromTag() );
                    tmpElement1.addCharacters( this.timeperiod_from.toString());

                }
                if (this.isTimestampToDefined()) {
                    tmpElement1 =  tmpElementOuter.addElement(TimeIntervalStructure.getToTag() );
                    tmpElement1.addCharacters( this.timeperiod_to.toString());
                }
            }

        } catch(Exception e) {
            return;
        }
    }


    /**
     * Creates a string (text) for this TimeInterval object,
     *
     * @return the string representation of this object
     */
    public String createInfoInText() {
        String toReturnStr = "";
        if (this.isAnyTimestampDefined()) {
            if (this.isTimestampFromDefined() && this.isTimestampToDefined() && (this.timeperiod_to.compareTo(this.timeperiod_from) == 0)) {
                toReturnStr = " at:" + this.timeperiod_to.toString();
            } else {
                toReturnStr = " at interval";
                if (this.isTimestampFromDefined()) {
                    toReturnStr = " from:" + this.timeperiod_from.toString();
                }
                if (this.isTimestampToDefined()) {
                    toReturnStr += " till:" + this.timeperiod_to.toString();
                }
            }
        } else toReturnStr = "(undefined)";

        return toReturnStr;

    }

    public boolean isTimestampFromDefined() {
        return timestampFromDefined;
    }

    public boolean isTimestampToDefined() {
        return timestampToDefined;
    }

    public boolean isAnyTimestampDefined() {
        return (isTimestampToDefined() || isTimestampFromDefined());
    }

    /**
     * Compares two TimeIntervalStructure objects
     *
     * @param targetTis the target TimeIntervalStructure to compare to
     * @return true if objects express the same TimeInterval, or false otherwise
     */
    public boolean equals(TimeIntervalStructure targetTis) {
        if (!this.isAnyTimestampDefined() && !targetTis.isAnyTimestampDefined())
            return true;

        if ((this.isAnyTimestampDefined() && !targetTis.isAnyTimestampDefined()) ||
                (!this.isAnyTimestampDefined() && targetTis.isAnyTimestampDefined()) ||
                (this.isAnyTimestampDefined() && this.isTimestampToDefined() && !targetTis.isTimestampToDefined()) ||
                (this.isAnyTimestampDefined() && !this.isTimestampToDefined() && targetTis.isTimestampToDefined()) ||
                (this.isAnyTimestampDefined() && this.isTimestampFromDefined() && !targetTis.isTimestampFromDefined()) ||
                (this.isAnyTimestampDefined() && !this.isTimestampFromDefined() && targetTis.isTimestampFromDefined())) {
            return false;
        }

        if ((this.isTimestampToDefined()) &&
                !(this.timeperiod_to.equals(targetTis.timeperiod_to))) {
            return false;
        }

        if ((this.isTimestampFromDefined()) &&
                !(this.timeperiod_from.equals(targetTis.timeperiod_from))) {
            return false;
        }

        // at the end
        return true;
    }

    public String toString() {
        StringWriter outStringWriter = new StringWriter();
        WstxOutputFactory fout = new WstxOutputFactory();
        fout.configureForXmlConformance();
        SMOutputDocument doc = null;
        SMOutputElement outputRootEl = null;
        try{
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            createInfoInDocument(doc, null);
            doc.closeRoot();
        } catch(Exception e) {
            return e.getMessage();
        }
        String retString = "";
        try{
            retString = outStringWriter.toString();
            outStringWriter.close();
        } catch(Exception e) {
            logger.error("Errors encountered while attempting to print this XML document!");
            e.printStackTrace();
        }
        return retString;

    }


    /**
     * Testing purposes
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        try{
            TimeIntervalStructure tsStruct = new TimeIntervalStructure(new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
            System.out.println("output: \n" + tsStruct.toString());

        }catch(Exception e)
        {
            System.out.println(e.getMessage());
        }

    }
}
