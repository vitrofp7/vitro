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
 * Model3dMetaGatewayEntry.java
 *
 */

package presentation.webgui.vitroappservlet.Model3dservice;

import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import java.io.StringWriter;

/**
 * <pre>
 * &lt;gateway&gt;
 * 	&lt;id&gt;q&lt;/gatewayId&gt; &lt;-------------	This is the peer id for the Gateway. Many KML files can have the same	gatewayId.
 * 									They will be merged if necessary.
 *									(Supposedly filled in by the Gateway that sent the file).
 *									(HOWEVER bear in mind that multiple Gateways could be inside a building)
 *	&lt;name&gt;Bobos&lt;/gatewayName&gt; &lt;-----	This is the peer name for the Gateway. If we can require it to be unique it can
 *									assist us in "searching" appropriate KML files.
 *									(Supposedly filled in by the Gateway that sent the file).
 *	&lt;defaultInterface&gt;1&lt;/defaultInterface&gt; &lt;------------------------	The default interface for this KML and this gateway.
 * &lt;/gateway&gt;
 * </pre>
 * @author antoniou
 */
public class Model3dMetaGatewayEntry {
    private Logger logger = Logger.getLogger(Model3dMetaGatewayEntry.class);
    private static final String nameTag = "name";
    private static final String idTag = "id";
    private static final String defaultInterfaceTag = "defaultInterface";
    
    private String gwid;
    private String gwname;
    private long defaultInterface;
    static private long noDefaultInterfaceDefined = -1;
    /**
     * Default constructor.
     * Creates a new instance of Model3dMetaGatewayEntry. This instance is invalid though, until
     * the fields of the entry are set manually, so this constructor should be used with this in mind.
     */
    public Model3dMetaGatewayEntry() {
        this.gwid = "";
        this.gwname = "";
        this.defaultInterface =  Model3dMetaGatewayEntry.getNoDefaultInterfaceDefined();
    }
    
    public Model3dMetaGatewayEntry(String gatewid, String gatewname, long gatedefaultInterface) {
        this.gwid = gatewid;
        this.gwname = gatewname;
        this.defaultInterface =  gatedefaultInterface;
    }
    
    /**
     * Creates a new instance of Model3dMetaGatewayEntry
     * @param givenCursor the XML part of the meta file that describes a gateway entry
     */
    public Model3dMetaGatewayEntry(SMInputCursor givenCursor) {
        this.gwid = "";
        this.gwname = "";
        this.defaultInterface =  Model3dMetaGatewayEntry.getNoDefaultInterfaceDefined();

        try{
            SMInputCursor childInElement = givenCursor.childCursor();
            String myText="";
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getIdTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.gwid = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getNameTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.gwname = childInElement2.getText();
                            break;
                        }
                    }
                }
                else if(!childInElement.getCurrEvent().hasText() &&
                        childInElement.getLocalName().toLowerCase().equals(this.getDefaultInterfaceTag().toLowerCase() ) ) 
                {
                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                    while (childInElement2.getNext() != null)
                    {
                        if(childInElement2.getCurrEvent().hasText())
                        {
                            this.defaultInterface = Long.parseLong(childInElement2.getText());
                            break;
                        }
                    }
                }
            }
        } catch(Exception e) {
            return; // the default (though invalid) values are already set.
        }        
    }
    
    
    /**
     *
     * @param parElement the parent element (if not the root) in the given XML document.
     *
     */
    public void createInfoInDocument(SMOutputElement parElement) {
        try{
            SMOutputElement tmpElement1;
            tmpElement1 =  parElement.addElement(this.getIdTag());
            tmpElement1.addCharacters(this.getGwid());
            
            tmpElement1 =  parElement.addElement(this.getNameTag());
            tmpElement1.addCharacters(this.getGwname());
            
            tmpElement1 =  parElement.addElement(this.getDefaultInterfaceTag());
            tmpElement1.addCharacters(Long.toString(this.getDefaultInterface()) );
        } catch(Exception e) {
            return;
        }
    }
    
    /**
     * Compares two Model3dMetaGatewayEntry objects.
     * @param targGwEntry the target Model3dMetaGatewayEntry to compare to
     * @return true if objects express the same gateway entry, or false otherwise
     */
    public boolean equals(Model3dMetaGatewayEntry targGwEntry) {
        if(this.getGwid().equals(targGwEntry.getGwid()) &&
                this.getGwname().equals(targGwEntry.getGwname()) &&
                this.getDefaultInterface() == targGwEntry.getDefaultInterface()) {
            return true;
        } else
            return false;
    }    
    
    /**
     * Method toString:
     * no parameters
     * @return  the XML String representing this gateway entry's XML fields
     */
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
            outputRootEl = doc.addElement(Model3dIndexEntry.getGwEntryTag());
            createInfoInDocument(outputRootEl);
            doc.closeRoot();
        } catch(Exception e) {
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
    
    public static long getNoDefaultInterfaceDefined() {
        return noDefaultInterfaceDefined;
    }
    
    public static String getIdTag() {
        return idTag;
    }
    
    public static String getNameTag() {
        return nameTag;
    }
    
    public static String getDefaultInterfaceTag() {
        return defaultInterfaceTag;
    }
    
    public long getDefaultInterface() {
        return defaultInterface;
    }
    
    public String getGwid() {
        return gwid;
    }
    
    public String getGwname() {
        return gwname;
    }

    public void setDefaultInterface(long defaultInterface) {
        this.defaultInterface = defaultInterface;
    }        
}
