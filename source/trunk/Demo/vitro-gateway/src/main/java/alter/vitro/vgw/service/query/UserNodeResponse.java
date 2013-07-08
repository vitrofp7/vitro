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
package alter.vitro.vgw.service.query;

/**
 * Author: antoniou
 */
public class UserNodeResponse {
    public static String COMMAND_TYPE_ENABLENODES_RESP = "EnableNodesResp";
    public static String COMMAND_TYPE_EQUIV_LIST_SYNCH_RESP = "EqvLstSynchResp";

    // TODO: check compatibility issues for response generation and toString///

    private int queryId;
    private String src; //the sender (user node) not really needed
    private String content; // the query content
    public static final String headerSpliter = "__00__";


    public UserNodeResponse(){
        queryId = 0;
        src = "";
        content = "";
    }

    // TODO: complete this
    public UserNodeResponse(String fromXMLExchMessage){

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
     * @return the query
     */
    public String getResponse() {
        return content;
    }

    /**
     * @param pQuery the query to set
     */
    public void setResponse(String pQuery) {
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
}
