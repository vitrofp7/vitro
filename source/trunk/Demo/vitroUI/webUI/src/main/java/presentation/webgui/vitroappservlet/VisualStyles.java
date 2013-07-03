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
 * VisualStyles.java
 *
 */

package presentation.webgui.vitroappservlet;

import presentation.webgui.vitroappservlet.Model3dservice.Model3dStylesEntry;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dStylesList;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author antoniou
 */
public class VisualStyles extends HttpServlet
{
    String uStyleId;
    /** Creates a new instance of VisualResultsModel */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        this.uStyleId = request.getParameter("suid");
        Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
        Model3dStylesEntry currStyleEntry = myStylesIndex.getStyleWithId(this.uStyleId);
        if(currStyleEntry != null) {
            response.setContentType("image/png");
            OutputStream out = response.getOutputStream();
            try {
                JFreeChart chart = StyleCreator.createDatasetAndChart(currStyleEntry);
                
                int chartHeight = 40;
                if( chart.getCategoryPlot().getDataset().getColumnKeys().size()  > 1) {
                    chartHeight = chart.getCategoryPlot().getDataset().getColumnKeys().size() * 22 ;
                }
                ChartUtilities.writeChartAsPNG(out, chart, 300, chartHeight);
            } catch (Exception e) {
                System.err.println(e.toString());
                response.setContentType("text/html");
                PrintWriter outPrintWriter = response.getWriter();
                outPrintWriter.print("<b>Error</b>:" +e.toString());
                outPrintWriter.flush();
                outPrintWriter.close();
            } finally {
                out.close();
            }
            return;
        }
        else
        {            
            response.setContentType("text/html");
            PrintWriter outPrintWriter = response.getWriter();
            outPrintWriter.print("<b>No style defined</b>");
            outPrintWriter.flush();
            outPrintWriter.close();
        }
    }

    
}
