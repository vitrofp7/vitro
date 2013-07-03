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
package vitro.vspEngine.service.query;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.StringWriter;
import java.util.Vector;

/**
 * Aux class for service continuation part of a PublicResponseAggrMsg
 */
public class RespServContinuationReplacementStruct {

    private static Logger logger = Logger.getLogger(PublicResponseAggrMsg.class);
    private String nodeSourceId;
    private String nodeReplmntId;
    private String capabilityId;

    private static final String sourceIdTag = "nodeSourceId";
    private static final String replacemntIdTag = "nodeReplmntId";
    private static final String capabilityIdTag = "capabilityId";

    public RespServContinuationReplacementStruct(String pSourceId, String pReplacementId, String pCapId) {
        setNodeSourceId(pSourceId);
        setNodeReplmntId(pReplacementId);
        setCapabilityId(pCapId);
    }

    /**
     * Creates a new instance of RespServContinuationReplacementStruct
     *
     * @param givenCursor the XML part of a query  that describes this struct item
     */
    public RespServContinuationReplacementStruct(SMInputCursor givenCursor) {
        setNodeSourceId("");
        setNodeReplmntId("");
        setCapabilityId("");

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText())
                {
                    if(childInElement.getLocalName().compareToIgnoreCase(RespServContinuationReplacementStruct.sourceIdTag )==0  )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                setNodeSourceId(childInElement2.getText());
                                break;
                            }
                        }
                    }else if( childInElement.getLocalName().compareToIgnoreCase(RespServContinuationReplacementStruct.replacemntIdTag) ==0 ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                setNodeReplmntId(childInElement2.getText());
                                break;
                            }
                        }
                    }else if( childInElement.getLocalName().compareToIgnoreCase(RespServContinuationReplacementStruct.capabilityIdTag ) ==0 ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                setCapabilityId(childInElement2.getText());
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

    public String getNodeSourceId() {
        return nodeSourceId;
    }

    public void setNodeSourceId(String pNodeSourceId) {
        nodeSourceId = pNodeSourceId;
    }

    public String getNodeReplmntId() {
        return nodeReplmntId;
    }

    public void setNodeReplmntId(String pNodeReplmntId) {
        nodeReplmntId =pNodeReplmntId;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public void setCapabilityId(String pCapabilityId) {
        capabilityId = pCapabilityId;
    }

    /**
     * Compares two RespServContinuationReplacementStruct objects.
     * @param targetStruct the target RespServContinuationReplacementStruct to compare to
     * @return true if objects express the same RespServContinuationReplacementStruct, or false otherwise
     */
    public boolean equals(RespServContinuationReplacementStruct targetStruct) {
        if ( (this.getNodeSourceId().compareToIgnoreCase(targetStruct.getNodeSourceId()) != 0) ||
                (this.getNodeReplmntId().compareToIgnoreCase(targetStruct.getNodeReplmntId()) != 0 ) ||
                (this.getCapabilityId().compareToIgnoreCase(targetStruct.getCapabilityId()) != 0 ) )
        {
            return false;
        }
        // at the end
        return true;
    }


    /**
     * Method createFunctionInfoInDocument:
     *
     * @param document the desired MIME type representation for the query.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement, boolean tempFlag) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(PublicResponseAggrMsg.getServContListItemTag());
            }
            else {
                tmpElementOuter =  document.addElement(PublicResponseAggrMsg.getServContListItemTag());
            }
            tmpElement1 = tmpElementOuter.addElement(RespServContinuationReplacementStruct.sourceIdTag) ;
            tmpElement1.addCharacters(  getNodeSourceId());

            tmpElement1 = tmpElementOuter.addElement(RespServContinuationReplacementStruct.replacemntIdTag) ;
            tmpElement1.addCharacters(  getNodeReplmntId());

            tmpElement1 = tmpElementOuter.addElement(RespServContinuationReplacementStruct.capabilityIdTag) ;
            if(!tempFlag)    {
                tmpElement1.addCharacters(  getCapabilityId());
            }
            else
            {
                String tmpCapabilityName = getCapabilityId();
                tmpElement1.addCharacters( tmpCapabilityName);            //todo: we could add a method to access the capability friendly name -not the id which is obscure
            }
        } catch(Exception e) {
            return;
        }



    }

    /**
     * Method toString:
     * <p/>
     * no parameters
     *
     * @return the XML String representing this requested Function XML fields
     */
    public String toString() {
        StringWriter outStringWriter = new StringWriter();
        WstxOutputFactory fout = new WstxOutputFactory();
        fout.configureForXmlConformance();
        SMOutputDocument doc = null;
        try{
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            createInfoInDocument(doc, null, false);
            doc.closeRoot();
        } catch(Exception e) {
            e.printStackTrace();
            return "";
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



}
