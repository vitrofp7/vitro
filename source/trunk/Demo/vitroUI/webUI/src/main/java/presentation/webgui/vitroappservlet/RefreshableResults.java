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
 * RefreshableResults.java
 *
 */

package presentation.webgui.vitroappservlet;

import vitro.vspEngine.service.query.IndexOfQueries;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author antoniou
 */
public class RefreshableResults extends HttpServlet
{
    private String querUid = "";    
    private String strPeriodOfLinkRefresh = "0";
    private int intPeriodOfLinkRefresh = 0;
    public static final int minPeriodOfLinkRefresh = 10; // 10 seconds minimum
    
    private String myResponse ="";
    /** Creates a new instance of RefreshableResults */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        querUid = request.getParameter("quid");
        strPeriodOfLinkRefresh = request.getParameter("period");
        try{
            if(strPeriodOfLinkRefresh == null || strPeriodOfLinkRefresh.equals("")) {
                intPeriodOfLinkRefresh = minPeriodOfLinkRefresh;
            } else {
                intPeriodOfLinkRefresh = Integer.parseInt(strPeriodOfLinkRefresh);
                if(intPeriodOfLinkRefresh < minPeriodOfLinkRefresh)
                    intPeriodOfLinkRefresh = minPeriodOfLinkRefresh;
            }
        } catch(NumberFormatException e) {
            intPeriodOfLinkRefresh = minPeriodOfLinkRefresh;
        }        
        
        IndexOfQueries myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
        String entireXMLofResultsFiletoType = "";
        if(myIndexOfQueries.getQueryDefinitionById(querUid) == null) {
            response.setContentType("text/html");
            myResponse = "No such query exists in the Index!";
            PrintWriter outPrintWriter = response.getWriter();
            outPrintWriter.print("<b>Error</b>:" +myResponse);
            outPrintWriter.flush();
            outPrintWriter.close();            
        } 
        else 
        {
            response.setContentType("text/kml");
            response.setHeader("Content-disposition","inline; filename=\"refreshableResults.kml\"");
            response.setHeader("Pragma", "public"); //HTTP 1.0 
            response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
            PrintWriter outPrintWriter = response.getWriter();
            outPrintWriter.print("<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"+
                                    "<Document>"+
                                    "<visibility>1</visibility>"+
                                    "<NetworkLink>"+
                                    "<name>Results for "+querUid +"</name>"+
                                    "<Link>"+
                                    "<href>"+"http://"+request.getServerName()+":"+request.getServerPort()+ request.getContextPath()+"/roleEndUser/ViewResults?quid="+querUid+"</href>");
            if(intPeriodOfLinkRefresh > 0)
            {
                outPrintWriter.print("<refreshMode>onInterval</refreshMode>"+
                                      "<refreshInterval>"+Integer.toString(intPeriodOfLinkRefresh)+"</refreshInterval>");
            }
            outPrintWriter.print("</Link>"+
                                "</NetworkLink>"+
                                "</Document>");
            outPrintWriter.flush();
            outPrintWriter.close();
            
        }

    }    
}
