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
 * StyleCreator.java
 *
 *
 */

package presentation.webgui.vitroappservlet;

import presentation.webgui.vitroappservlet.Model3dservice.Model3dStyleNumericCase;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dStyleSpecialCase;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dStylesEntry;
import presentation.webgui.vitroappservlet.Model3dservice.Model3dStylesList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleInsets;
import vitro.vspEngine.logic.model.Capability;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Vector;

/**
 *
 * @author antoniou
 */
public class StyleCreator extends HttpServlet {
    private static final String modePreview = "preview";
    private static final String modeSubmitStyle = "submit";
    
    private String mode;
    
    private String givenGenCapability;
    private String givenDefaultColor;
    private String givenDefaultIconfile;
    private String givenDefaultPrefabfile;
    
    private String[] givenSpecialValuesBox;
    private String[] givenSpecialValueColor;
    private String[] givenSpecialValueIconfile;
    private String[] givenSpecialValuePrefabfile;
    
    private String[] givenRangeFromBox;
    private String[] givenRangeToBox;
    private String[] givenRangeColor;
    private String[] givenRangeIconfile;
    private String[] givenRangePrefabfile;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request,response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
        this.mode = request.getParameter("nssfMode");
        
        this.givenGenCapability = request.getParameter("nssfCapability");
        this.givenDefaultColor = request.getParameter("nssfGlobalColor");
        this.givenDefaultIconfile = request.getParameter("nssfDefaultIconfile");
        this.givenDefaultPrefabfile = request.getParameter("nssfDefaultPrefabfile");
        
        this.givenSpecialValuesBox = request.getParameterValues("nssfSpecialValuesBox[]");
        this.givenSpecialValueColor = request.getParameterValues("nssfSpecialValueColor[]");
        this.givenSpecialValueIconfile = request.getParameterValues("nssfSpecialValueIconfile[]");
        this.givenSpecialValuePrefabfile = request.getParameterValues("nssfSpecialValuePrefabfile[]");
        
        this.givenRangeFromBox = request.getParameterValues("nssfRangeFromBox[]");
        this.givenRangeToBox = request.getParameterValues("nssfRangeToBox[]");
        this.givenRangeColor = request.getParameterValues("nssfRangeColor[]");
        this.givenRangeIconfile = request.getParameterValues("nssfRangeIconfile[]");
        this.givenRangePrefabfile = request.getParameterValues("nssfRangePrefabfile[]");
        
        // default mode is preview mode!
        if(this.mode == null || this.mode.equals("")) 
        {
            this.mode = StyleCreator.modePreview; // by default we assume preview mode
        }
        
        
        if(this.mode.equals(StyleCreator.modePreview)) 
        {
            previewStyleLegend(response);
        } 
        else if(this.mode.equals(StyleCreator.modeSubmitStyle)) {
            submitStyle(response);
        } else {
            response.setContentType("text/html");
            PrintWriter outPrintWriter = response.getWriter();
            outPrintWriter.print("<b>Error</b>: no valid mode was specified! ("+this.mode+")");
            outPrintWriter.flush();
            outPrintWriter.close();
        }
    }
    
    private void submitStyle(HttpServletResponse response) throws IOException {
      Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();

      // fill in the SpecialCases vector with valid Model3dStyleSpecialCase objects
      Vector<Model3dStyleSpecialCase> givSpecialCasesVec = retrieveSpecialCasesVector();

      // fill in the SpecialCases vector with valid Model3dStyleSpecialCase objects
      Vector<Model3dStyleNumericCase> givNumericCasesVec = retrieveNumericCasesVector();

      if(myStylesIndex.addNewStyleEntry(null, 
                                        this.givenGenCapability, 
                                        this.givenDefaultColor, 
                                        this.givenDefaultIconfile, 
                                        this.givenDefaultPrefabfile, 
                                        givSpecialCasesVec,   
                                        givNumericCasesVec ) == true)
      {      
           response.setContentType("text/xml; charset=UTF-8");
           PrintWriter outPrintWriter = response.getWriter();
           outPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
           outPrintWriter.println("<Answer>");
           outPrintWriter.println("<error errno=\"0\" errdesc=\"Done!\"></error>");
           outPrintWriter.println("</Answer>");
           outPrintWriter.flush();
           outPrintWriter.close();
      }
      else
      {
           response.setContentType("text/xml; charset=UTF-8");
           PrintWriter outPrintWriter = response.getWriter();
           outPrintWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
           outPrintWriter.println("<Answer>");
           outPrintWriter.println("<error errno=\"1\" errdesc=\"Something went wrong!\"></error>");
           outPrintWriter.println("</Answer>");
           outPrintWriter.flush();
           outPrintWriter.close();
      }
    }
    
    private void previewStyleLegend(HttpServletResponse response) throws IOException {
        // png creation using jfreechart.
        /*response.setContentType("text/html");
        PrintWriter outPrintWriter = response.getWriter();
        outPrintWriter.print("<b>Error</b>: no valid mode was specified! ("+this.mode+")");
        outPrintWriter.flush();
        outPrintWriter.close();
        */
         Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();

        // fill in the SpecialCases vector with valid Model3dStyleSpecialCase objects
        Vector<Model3dStyleSpecialCase> givSpecialCasesVec = retrieveSpecialCasesVector();

        // fill in the SpecialCases vector with valid Model3dStyleSpecialCase objects
        Vector<Model3dStyleNumericCase> givNumericCasesVec = retrieveNumericCasesVector();
        Model3dStylesEntry tmpDummyEntry = new Model3dStylesEntry(null, 
                                                                    this.givenGenCapability, 
                                                                    this.givenDefaultColor, 
                                                                    this.givenDefaultIconfile, 
                                                                    this.givenDefaultPrefabfile, 
                                                                    givSpecialCasesVec, 
                                                                    givNumericCasesVec );
        response.setContentType("image/png");
        OutputStream out = response.getOutputStream();
        try {
                                
            
            JFreeChart chart = createDatasetAndChart(tmpDummyEntry);
            
            int chartHeight = 40;
            if( chart.getCategoryPlot().getDataset().getColumnKeys().size()  > 1)
            {
                chartHeight = chart.getCategoryPlot().getDataset().getColumnKeys().size() * 22 ;
            }
            ChartUtilities.writeChartAsPNG(out, chart, 300, chartHeight);
        }
        catch (Exception e) {
            System.err.println(e.toString());
            response.setContentType("text/html");
            PrintWriter outPrintWriter = response.getWriter();
            outPrintWriter.print("<b>Error</b>:" +e.toString());
            outPrintWriter.flush();
            outPrintWriter.close();
        }
        finally {
            out.close();
        }
        return;
    }
    
    
    
    
  private Vector<Model3dStyleSpecialCase> retrieveSpecialCasesVector()
  {
      Vector<Model3dStyleSpecialCase> givSpecialCasesVec = new Vector<Model3dStyleSpecialCase>();
       // insert Style Special Cases
       if(this.givenSpecialValuesBox!=null && this.givenSpecialValuesBox.length > 0
                && this.givenSpecialValueColor!=null && this.givenSpecialValueColor.length > 0
                && this.givenSpecialValueIconfile!=null && this.givenSpecialValueIconfile.length > 0
                && this.givenSpecialValuePrefabfile!=null && this.givenSpecialValuePrefabfile.length > 0
                && this.givenSpecialValuesBox.length == this.givenSpecialValueColor.length 
                && this.givenSpecialValuesBox.length == this.givenSpecialValueIconfile.length
                && this.givenSpecialValuesBox.length == this.givenSpecialValuePrefabfile.length)
       {
           for(int i = 0; i < this.givenSpecialValuesBox.length; i++) 
           {
               Model3dStyleSpecialCase tmpCase = new Model3dStyleSpecialCase(this.givenSpecialValuesBox[i], this.givenSpecialValueColor[i], this.givenSpecialValueIconfile[i], this.givenSpecialValuePrefabfile[i]);
               givSpecialCasesVec.add(tmpCase) ;
           }
       }
       return givSpecialCasesVec;
  }
    
  private Vector<Model3dStyleNumericCase> retrieveNumericCasesVector()
  {
       Vector<Model3dStyleNumericCase> givNumericCasesVec = new Vector<Model3dStyleNumericCase>();
       // insert Style Numeric Cases
       if(this.givenRangeFromBox!=null && this.givenRangeFromBox.length > 0
                && this.givenRangeToBox!=null && this.givenRangeToBox.length > 0
                && this.givenRangeColor!=null && this.givenRangeColor.length > 0
                && this.givenRangeIconfile!=null && this.givenRangeIconfile.length > 0
                && this.givenRangePrefabfile!=null && this.givenRangePrefabfile.length > 0
                && this.givenRangeFromBox.length == this.givenRangeToBox.length 
                && this.givenRangeFromBox.length == this.givenRangeColor.length
                && this.givenRangeFromBox.length == this.givenRangeIconfile.length
                && this.givenRangeFromBox.length == this.givenRangePrefabfile.length)
       {
           for(int i = 0; i < this.givenRangeFromBox.length; i++) 
           {
               Model3dStyleNumericCase tmpCase = new Model3dStyleNumericCase(this.givenRangeFromBox[i], this.givenRangeToBox[i], this.givenRangeColor[i], this.givenRangeIconfile[i], this.givenRangePrefabfile[i]);
               givNumericCasesVec.add(tmpCase) ;
           }
       }
       return givNumericCasesVec;
  }
    
    
    public static JFreeChart createDatasetAndChart(Model3dStylesEntry givStyleEntry) {
        
        Vector<String> tmpCategLabels = new Vector<String>();
        Vector<String> tmpCategColors = new Vector<String>();
        
        tmpCategLabels.addElement("Default");
        tmpCategColors.addElement(givStyleEntry.getGlobalColor());
        
            for(int i = 0; i < givStyleEntry.getSpecialCasesVec().size(); i++) 
            {
                String tmpLabel = givStyleEntry.getSpecialCasesVec().elementAt(i).getSpecialValue();
                tmpCategLabels.addElement(tmpLabel);
                if(givStyleEntry.getSpecialCasesVec().elementAt(i).getColor1().equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                {
                    tmpCategColors.addElement(givStyleEntry.getGlobalColor()); // get the global default set
                }
                else
                    tmpCategColors.addElement(givStyleEntry.getSpecialCasesVec().elementAt(i).getColor1());
            }

           for(int i = 0; i < givStyleEntry.getNumericCasesVec().size(); i++) 
           {
               String tmpLabel = "";
               if(givStyleEntry.getNumericCasesVec().elementAt(i).getFromValue().equals(""))
               {
                   tmpLabel = "(-inf, ";
               }           
               else
                   tmpLabel = "["+givStyleEntry.getNumericCasesVec().elementAt(i).getFromValue()+", ";
               
               if(givStyleEntry.getNumericCasesVec().elementAt(i).getToValue().equals(""))
               {
                   tmpLabel += "+inf)";
               }
               else
                   tmpLabel += givStyleEntry.getNumericCasesVec().elementAt(i).getToValue()+")";
               
               tmpCategLabels.addElement(tmpLabel);
               
               if(givStyleEntry.getNumericCasesVec().elementAt(i).getColor1().equals(Model3dStyleNumericCase.UNDEFINEDCOLOR1))
                {
                    tmpCategColors.addElement(givStyleEntry.getGlobalColor()); // get the global default set
                }
                else
                    tmpCategColors.addElement(givStyleEntry.getNumericCasesVec().elementAt(i).getColor1());                              
           }

        int sizeOfDataColumn = 15;  //indicates the "length" of the bars in a per cent scale.
        double[][] data = {{sizeOfDataColumn}} ;
        String[] myCategories = {"error"};
        if(tmpCategLabels.size() > 0)
        {            
            data = new double[1][tmpCategLabels.size()];
            myCategories = new String[tmpCategLabels.size()];
            for(int i = 0; i < tmpCategLabels.size(); i++)
            {
                data[0][i] = sizeOfDataColumn;
                myCategories[i] = tmpCategLabels.elementAt(i);
            }
        }
        final String[] mySeries = {""};
        
        CategoryDataset dataset = DatasetUtilities.createCategoryDataset(
            mySeries,
            myCategories,
            data
        );
        
        JFreeChart chart = createChart(dataset, tmpCategColors, givStyleEntry);
        return chart;        
    }
    
    private static JFreeChart createChart(CategoryDataset dataset,  Vector<String> givCategColors, Model3dStylesEntry givStyleEntry) {
        String capSimpleName = givStyleEntry.getCorrCapability();
        capSimpleName = capSimpleName.replaceAll(Capability.dcaPrefix, "");
        JFreeChart chart = ChartFactory.createBarChart(
            "Style Legend for "+capSimpleName,  // chart title
            null,             // domain axis label
            null,            // range axis label
            dataset,                // data
            PlotOrientation.HORIZONTAL,
            false,                  // include legend
            true,
            false
        );

        chart.getTitle().setFont( new Font("SansSerif", Font.BOLD, 14));
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        chart.setBackgroundPaint(Color.white);  // seen
        CategoryPlot plot = chart.getCategoryPlot();
        chart.setPadding(new RectangleInsets(0, 0, 0, 0)); //new

        plot.setNoDataMessage("NO DATA!");

        Paint[] tmpPaintCategories = {Color.white};
        if(givCategColors.size() > 0)
        {
            tmpPaintCategories = new Paint[givCategColors.size()] ;
            for(int i = 0; i < givCategColors.size(); i++)
            {
               tmpPaintCategories[i] =  Color.decode(givCategColors.elementAt(i));
            }
        }
        
        CategoryItemRenderer renderer = new CustomRenderer(tmpPaintCategories);

        renderer.setSeriesPaint(0, new Color(255, 204, 51)); //new


        plot.setRenderer(renderer);

        plot.setAxisOffset(new RectangleInsets(0, 0, 0, 0)); //new
        plot.setForegroundAlpha(1f);       //new
        plot.setBackgroundAlpha(1f); //new
        plot.setInsets(new RectangleInsets(5, 0, 5, 0));  //new was 5,0,5,0
        plot.setRangeGridlinesVisible(false); //new was true
        plot.setBackgroundPaint(Color.white);//new: was (Color.lightGray);
        plot.setOutlinePaint(Color.white);

        //plot.setOrientation(PlotOrientation.HORIZONTAL);
        
        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setLowerMargin(0.04);
        domainAxis.setUpperMargin(0.04);
        domainAxis.setVisible(true);
        domainAxis.setLabelAngle(Math.PI/2);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, 100.0); // new: was 100
        rangeAxis.setVisible(false);
        // OPTIONAL CUSTOMISATION COMPLETED.
        return chart;
    }
        
    /**
     * A custom renderer that returns a different color for each item in a single series.
     */
    static class CustomRenderer extends BarRenderer {

        /** The colors. */
        private Paint[] colors;

        /**
         * Creates a new renderer.
         * @param colors  the colors.
         */
        public CustomRenderer(Paint[] colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item.  Overrides the default behaviour inherited from
         * AbstractSeriesRenderer.
         * @param row  the series.
         * @param column  the category.
         * @return The item color.
         */
        public Paint getItemPaint(int row, int column) {
            return this.colors[column % this.colors.length];
        }
    }    
    
}


