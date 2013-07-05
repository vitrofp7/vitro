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

package vitro.virtualsensor.utils;


import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author Andres
 */

public class XmlFile extends TextFile {
    
    private DocumentBuilderFactory  domFactory;
    private DocumentBuilder         builder;
    private Document                doc;
    private XPath                   xpath;
    
    /* ---------------------------------------------------------------------- */
    
    
    /* ---------------------------------------------------------------------- */
    public XmlFile (String path) {
        super(path);
        initFromString(this.getText());
    }
    
    /* ---------------------------------------------------------------------- */
    private boolean initFromString (String xml) {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            builder = domFactory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            doc = builder.parse(is);

            xpath = XPathFactory.newInstance().newXPath();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /* ---------------------------------------------------------------------- */
    public NodeList parseXpath (String query) throws XPathExpressionException   {
        XPathExpression expr = xpath.compile(query);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        return nodes;
    }
    
    /* ---------------------------------------------------------------------- */
    public String[] parseXpathValues (String query) throws XPathExpressionException   {
        XPathExpression expr = xpath.compile(query);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        String[] results = new String[nodes.getLength()];
        for (int i = 0; i < nodes.getLength(); i++) {
            results[i] = nodes.item(i).getNodeValue();
        }
        return results;
    }
    
    /* ---------------------------------------------------------------------- */
    public String parseXpathFirst (String query) throws XPathExpressionException   {
        XPathExpression expr = xpath.compile(query);
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        NodeList nodes = (NodeList) result;
        if (nodes.getLength() > 0) return nodes.item(0).getNodeValue();
        else return "";
    }
    
    /* ---------------------------------------------------------------------- */
    
}
