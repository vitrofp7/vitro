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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vitro.vspEngine.service.query;

import java.io.IOException;

/**
 *
 * @author antoniou
 */
public class UserNodeQuery {
    private int queryId;
    private String src; //the sender (user node) not really needed
    private String content; // the query content
    public static final String headerSpliter = "__00__";

    
    public UserNodeQuery(){
        queryId = 0;
        src = "";
        content = "";
    }
            
    // TODO: complete this
    // format is like:
    // <Query>
    //  <QueryId>17577470</QueryId>
    //  <src>TestAppIDCTI</src>
    //  <content>
    //  <?xml version="1.0"?>
    //  <!DOCTYPE myQueryAggr>
    //  ...
    //  </content>
    // </Query>
    //
    public UserNodeQuery(String fromXMLExchMessage){
        
        String pQueryId = "0";
        String pSrc = "";
        String pContent = "";
        
        String[] fromXMLExchMessageBasicElements = fromXMLExchMessage.split(headerSpliter);        
        
        if(fromXMLExchMessageBasicElements.length == 3)
        {
            queryId = Integer.parseInt(fromXMLExchMessageBasicElements[0]);
            src = fromXMLExchMessageBasicElements[1];
            content = fromXMLExchMessageBasicElements[2];
        }
        // DEBUG message
        //System.out.println("ID = "+ queryId + "\nsource node = "+ src +"\nCONTENT = "+content);
    }
    
    /**
     * @return the QueryId
     */
    public int getQueryId() {
        return queryId;
    }

    /**
     * @param pQueryId the QueryId to set
     */
    public void setQueryId(int pQueryId) {
        this.queryId = pQueryId;
    }

    /**
     * @return the src
     */
    public String getSrc() {
        return src;
    }

    /**
     * @param src the src to set
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * @return the Query
     */
    public String getQuery() {
        return content;
    }

    /**
     * @param pQuery the Query to set
     */
    public void setQuery(String pQuery) {
        this.content = pQuery;
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        // TODO: Simple enough, so we won't use any XMLWriter:
        sb.append(Integer.toString(queryId));
        sb.append(headerSpliter);
        sb.append(src);
        sb.append(headerSpliter);
        sb.append(content);
        return sb.toString();
    }
    
    // tester
    public static void main(String[] args) throws IOException {
        
        String input2XMLStr = "62499119__00__TestAppIDCTI__00__<?xml version=\"1.0\"?>" +
        "<!DOCTYPE myQueryAggr>" +
        "<myQueryAggr>" +
        "<message-type>" +
        "aggregatedQuery" +
        "</message-type>" +
        "<queryDefID>" +
        "ufk3g3uj9796" +
        "</queryDefID>" +
        "<query-count>" +
        "0" +
        "</query-count>" +
        "<motesList>" +
        "<mote>" +
        "<moteid>" +
        "urn:wisebed:ctitestbed:0x14e6" +
        "</moteid>" +
        "<funcOnSensorList>" +
        "<funcOnSensor>" +
        "<sensorModelid>" +
        "321701236" +
        "</sensorModelid>" +
        "<fid>" +
        "1" +
        "</fid>" +
        "</funcOnSensor>" +
        "</funcOnSensorList>" +
        "</mote>" +
        "<mote>" +
        "<moteid>" +
        "urn:wisebed:ctitestbed:0x153d" +
        "</moteid>" +
        "<funcOnSensorList>" +
        "<funcOnSensor>" +
        "<sensorModelid>" +
        "321701236" +
        "</sensorModelid>" +
        "<fid>" +
        "1" +
        "</fid>" +
        "</funcOnSensor>" +
        "</funcOnSensorList>" +
        "</mote>" +
        "<mote>" +
        "<moteid>" +
        "urn:wisebed:ctitestbed:0x181" +
        "</moteid>" +
        "<funcOnSensorList>" +
        "<funcOnSensor>" +
        "<sensorModelid>" +
        "321701236" +
        "</sensorModelid>" +
        "<fid>" +
        "1" +
        "</fid>" +
        "</funcOnSensor>" +
        "</funcOnSensorList>" +
        "</mote>" +
        "</motesList>" +
        "<reqFunctionsList>" +
        "<reqFunction>" +
        "<description>" +
        "Last Value" +
        "</description>" +
        "<id>" +
        "1" +
        "</id>" +
        "</reqFunction>" +
        "</reqFunctionsList>" +
        "<isHistory>" +
        "false" +
        "</isHistory>" +
        "</myQueryAggr>";
        
       
        UserNodeQuery q1 = new UserNodeQuery(input2XMLStr);
        System.out.println(q1.toString());
    }
    
}
