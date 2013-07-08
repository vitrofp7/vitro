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
 * KMLTranslate3dStyle.java
 *
 */

package presentation.webgui.vitroappservlet.KMLPresentationService;

import presentation.webgui.vitroappservlet.Model3dservice.*;
import org.codehaus.staxmate.out.SMOutputElement;

/**
 *
 * @author antoniou
 */
public class KMLTranslate3dStyle {
    
    public static final String roomPlaceMarkStyleUrlId = "room_placemark";
    
    private static String getAlphaBGRcolor(String givRGBcolor)
    {
        String toReturnStr = "FF000000";
        if(Model3dCommon.isValidColorString(givRGBcolor))
        {
            String tmpR=givRGBcolor.substring(1,3);
            String tmpG=givRGBcolor.substring(3,5);
            String tmpB=givRGBcolor.substring(5,7);
            toReturnStr = "FF"+tmpB+tmpG+tmpR;
        }
        return toReturnStr;
    }
    
    /**
     * Should create a similar to the following structure for each capability selected (and listing all the defined special values/value ranges)
     * (to do) where will the small prefabs (i.e. 3d models for the sensors) be used in this structure?
     * (to do) for now we also skip the IconStyle
     * Be Careful here, the colors in GoogleEarth the color scheme is bgr(blue green red) instead of rgb(red green blue)
     * <pre>
     *  &lt;Style id= capability+"styleid"+"_"+ValueOrRangeIndicatorSomething&gt;
     *          &lt;IconStyle&gt; (if any (to do for now we skip this)
     *              &lt;Icon&gt;        
     *                  &lt;href&gt;http://em1server.cti.gr/~mylonasg/thermo-icon.png&lt;/href&gt;
     *              &lt;/Icon&gt;
     *          &lt;/IconStyle&gt;
     *          &lt;LineStyle&gt;
     *              &lt;color&gt;ff000000&lt;/color&gt;
     *          &lt;/LineStyle&gt;
     *          &lt;PolyStyle&gt;
     *              &lt;color&gt;ff1c6100&lt;/color&gt;
     *              &lt;outline&gt;0&lt;/outline&gt;
     *          &lt;/PolyStyle&gt;
     * &lt;/Style&gt;
     * </pre>
     */    
    public static void appendStyleForValuesOfCapability(SMOutputElement parentElement, String selectedStyleId, String contextPath)
     throws javax.xml.stream.XMLStreamException 
    {
        Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
        Model3dStylesEntry selectedStylesForCurrCap = myStylesIndex.getStyleWithId(selectedStyleId);
        if(selectedStylesForCurrCap == null)
            return;
        String currCap = selectedStylesForCurrCap.getCorrCapability();
        // with the entry  selectedStylesForCurrCap, find all colors defined and append them as seperate styles in the KML
        /*
        //
        // for placeMarks
        //
        // Globals/Defaults
        {
            SMOutputElement  theStyleEl = parentElement.addElement("Style");
            theStyleEl.addAttribute("id", currCap+"_Global_Placemark");        
        
            if(! selectedStylesForCurrCap.getGlobalIconFile().equals(Model3dStylesEntry.undefinedIconFilename))
            {
                SMOutputElement  theIconStyleEl = theStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+selectedStylesForCurrCap.getGlobalIconFile());// (To do: fill it in with the url to the uploaded icon!!!)
            }        
        }
        // (To do+++) for global prefab ???
        // ...
        // ...        
        // for special values
        for(int i = 0; i < selectedStylesForCurrCap.getSpecialCasesVec().size(); i++)
        {            
            Model3dStyleSpecialCase currStyleSpecial = selectedStylesForCurrCap.getSpecialCasesVec().elementAt(i);
            SMOutputElement  theSpecialStyleEl = parentElement.addElement("Style");
            theSpecialStyleEl.addAttribute("id", currCap+"_Special_Placemark_"+Integer.toString(i));
            
            if(! currStyleSpecial.getIconFilename().equals(Model3dStylesEntry.undefinedIconFilename)) {
                SMOutputElement  theIconStyleEl = theSpecialStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+currStyleSpecial.getIconFilename()) ; // (To do: fill it in with the url to the uploaded icon!!!)
            }
            else if(! selectedStylesForCurrCap.getGlobalIconFile().equals(Model3dStylesEntry.undefinedIconFilename))
            { // put default icon if exists
                SMOutputElement  theIconStyleEl = theSpecialStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+selectedStylesForCurrCap.getGlobalIconFile());// (To do: fill it in with the url to the uploaded icon!!!)
            }
            // (To do+++) for special value prefab ???
            // ...
            // ...            
        }        
        // for numeric ranges        
        for(int i = 0; i < selectedStylesForCurrCap.getNumericCasesVec().size(); i++)
        {
            Model3dStyleNumericCase currStyleNumeric = selectedStylesForCurrCap.getNumericCasesVec().elementAt(i);
            SMOutputElement  theNumericStyleEl = parentElement.addElement("Style");
            theNumericStyleEl.addAttribute("id", currCap+"_Numeric_Placemark_"+Integer.toString(i));
            
            if(! currStyleNumeric.getIconFilename().equals(Model3dStylesEntry.undefinedIconFilename)) {
                SMOutputElement  theIconStyleEl = theNumericStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+currStyleNumeric.getIconFilename()) ; // (To do: fill it in with the url to the uploaded icon!!!)
            }
            else if(! selectedStylesForCurrCap.getGlobalIconFile().equals(Model3dStylesEntry.undefinedIconFilename))
            { // put default icon if exists
                SMOutputElement  theIconStyleEl = theNumericStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+selectedStylesForCurrCap.getGlobalIconFile());// (To do: fill it in with the url to the uploaded icon!!!)
            }
            // (To do+++) for numeric range prefab ???
            // ...
            // ...            
        }
        */
        // -----------------------------------------------------------
        //
        // for room coloring and placemark icons (we use the same style: for placemarks only the icons matter, for rooms only the coloring)
        //
        // Globals/Defaults
        {        
            SMOutputElement  theStyleEl = parentElement.addElement("Style");
            theStyleEl.addAttribute("id", currCap+"_Global");
            
            if(! selectedStylesForCurrCap.getGlobalIconFile().equals(Model3dStylesEntry.undefinedIconFilename)) {
                SMOutputElement  theIconStyleEl = theStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+selectedStylesForCurrCap.getGlobalIconFile());// (To do: fill it in with the url to the uploaded icon!!!)
            }
            SMOutputElement  theLineStyleEl = theStyleEl.addElement("LineStyle");
            SMOutputElement  theLineStyleColorEl = theLineStyleEl.addElement("color");
            theLineStyleColorEl.addCharacters(KMLTranslate3dStyle.getAlphaBGRcolor(selectedStylesForCurrCap.getGlobalColor()) );
            
            SMOutputElement  thePolyStyleEl = theStyleEl.addElement("PolyStyle");
            SMOutputElement  thePolyStyleColorEl = thePolyStyleEl.addElement("color");
            thePolyStyleColorEl.addCharacters(KMLTranslate3dStyle.getAlphaBGRcolor(selectedStylesForCurrCap.getGlobalColor()) );
            SMOutputElement  thePolyStyleOutlineEl = thePolyStyleEl.addElement("outline");
            thePolyStyleOutlineEl.addCharacters("0");
            // (To do+++) for global prefab ???
            // ...
            // ...
        }
        // for special values
        for(int i = 0; i < selectedStylesForCurrCap.getSpecialCasesVec().size(); i++)
        {            
            Model3dStyleSpecialCase currStyleSpecial = selectedStylesForCurrCap.getSpecialCasesVec().elementAt(i);
            SMOutputElement  theSpecialStyleEl = parentElement.addElement("Style");
            theSpecialStyleEl.addAttribute("id", currCap+"_Special_"+Integer.toString(i));
            
            if(! currStyleSpecial.getIconFilename().equals(Model3dStylesEntry.undefinedIconFilename)) {
                SMOutputElement  theIconStyleEl = theSpecialStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+currStyleSpecial.getIconFilename()) ; // (To do: fill it in with the url to the uploaded icon!!!)
            }
            else if(! selectedStylesForCurrCap.getGlobalIconFile().equals(Model3dStylesEntry.undefinedIconFilename))
            { // put default icon if exists
                SMOutputElement  theIconStyleEl = theSpecialStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+selectedStylesForCurrCap.getGlobalIconFile());// (To do: fill it in with the url to the uploaded icon!!!)
            }
            SMOutputElement  theSpecialLineStyleEl = theSpecialStyleEl.addElement("LineStyle");
            SMOutputElement  theSpecialLineStyleColorEl = theSpecialLineStyleEl.addElement("color");
            String designatedColor = currStyleSpecial.getColor1();
            if(designatedColor.equals(Model3dStyleSpecialCase.UNDEFINEDCOLOR1))
                designatedColor = selectedStylesForCurrCap.getGlobalColor(); // get the global default set
            theSpecialLineStyleColorEl.addCharacters(KMLTranslate3dStyle.getAlphaBGRcolor(designatedColor) );
            
            SMOutputElement  theSpecialPolyStyleEl = theSpecialStyleEl.addElement("PolyStyle");
            SMOutputElement  theSpecialPolyStyleColorEl = theSpecialPolyStyleEl.addElement("color");
            theSpecialPolyStyleColorEl.addCharacters(KMLTranslate3dStyle.getAlphaBGRcolor(designatedColor) );
            SMOutputElement  theSpecialPolyStyleOutlineEl = theSpecialPolyStyleEl.addElement("outline");
            theSpecialPolyStyleOutlineEl.addCharacters("0");
            // (To do+++) for special value prefab ???
            // ...
            // ...            
        }        
        // for numeric ranges        
        for(int i = 0; i < selectedStylesForCurrCap.getNumericCasesVec().size(); i++)
        {
            Model3dStyleNumericCase currStyleNumeric = selectedStylesForCurrCap.getNumericCasesVec().elementAt(i);
            SMOutputElement  theNumericStyleEl = parentElement.addElement("Style");
            theNumericStyleEl.addAttribute("id", currCap+"_Numeric_"+Integer.toString(i));
            
            if(! currStyleNumeric.getIconFilename().equals(Model3dStylesEntry.undefinedIconFilename)) {
                SMOutputElement  theIconStyleEl = theNumericStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+currStyleNumeric.getIconFilename()) ; // (To do: fill it in with the url to the uploaded icon!!!)
            }
            else if(! selectedStylesForCurrCap.getGlobalIconFile().equals(Model3dStylesEntry.undefinedIconFilename))
            { // put default icon if exists
                SMOutputElement  theIconStyleEl = theNumericStyleEl.addElement("IconStyle");
                SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
                SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
                theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+selectedStylesForCurrCap.getGlobalIconFile());// (To do: fill it in with the url to the uploaded icon!!!)
            }
            SMOutputElement  theNumericLineStyleEl = theNumericStyleEl.addElement("LineStyle");
            SMOutputElement  theNumericLineStyleColorEl = theNumericLineStyleEl.addElement("color");
            String designatedColor = currStyleNumeric.getColor1();
            if(designatedColor.equals(Model3dStyleNumericCase.UNDEFINEDCOLOR1))
                designatedColor = selectedStylesForCurrCap.getGlobalColor(); // get the global default set            
            theNumericLineStyleColorEl.addCharacters(KMLTranslate3dStyle.getAlphaBGRcolor(designatedColor) );
            
            SMOutputElement  theNumericPolyStyleEl = theNumericStyleEl.addElement("PolyStyle");
            SMOutputElement  theNumericPolyStyleColorEl = theNumericPolyStyleEl.addElement("color");
            theNumericPolyStyleColorEl.addCharacters(KMLTranslate3dStyle.getAlphaBGRcolor(designatedColor) );
            SMOutputElement  theNumericPolyStyleOutlineEl = theNumericPolyStyleEl.addElement("outline");
            theNumericPolyStyleOutlineEl.addCharacters("0");
            // (To do+++) for numeric range prefab ???
            // ...
            // ...            
        }

    }
        
    /**
     * Should create a similar to the following structure for the legend placement
     * <pre>
     *  &lt;ScreenOverlay id="TemperatureLegend"&gt;
     *       &lt;name&gt;Temperature Legend&lt;/name&gt;
     *       &lt;visibility&gt;0&lt;/visibility&gt;
     *       &lt;Icon&gt;
     *          &lt;href&gt;http://em1server.cti.gr/~mylonasg/legend.png&lt;/href&gt;
     *       &lt;/Icon&gt;
     *       &lt;overlayXY x="0" y="1" xunits="fraction" yunits="fraction"/&gt;
     *       &lt;screenXY x="0" y="1" xunits="fraction" yunits="fraction"/&gt;
     *       &lt;rotationXY x="0.5" y="0.5" xunits="fraction" yunits="fraction"/&gt;
     *       &lt;size x="200" y="121" xunits="pixels" yunits="pixels"/&gt;
     *   &lt;/ScreenOverlay&gt;
     * </pre>
     */
    public static void showStyleLegend(SMOutputElement parentElement, String selectedStyleId, String contextPath)
     throws javax.xml.stream.XMLStreamException {
        
        Model3dStylesList myStylesIndex = Model3dStylesList.getModel3dStylesList();
        Model3dStylesEntry selectedStylesForCurrCap = myStylesIndex.getStyleWithId(selectedStyleId);
        if(selectedStylesForCurrCap == null)
            return;
        
        SMOutputElement  screenOverlayEl = parentElement.addElement("ScreenOverlay");
        screenOverlayEl.addAttribute("id", selectedStylesForCurrCap.getCorrCapability()+"Legend");
        
        SMOutputElement overlayNameEl =  screenOverlayEl.addElement("name");
        overlayNameEl.addCharacters(selectedStylesForCurrCap.getCorrCapability()+" Legend");
            
        //SMOutputElement visibilityEl = screenOverlayEl.addElement("visibility");
        //visibilityEl.addCharacters("0");
        
        SMOutputElement iconEl = screenOverlayEl.addElement("Icon");
        SMOutputElement iconHrefEl = iconEl.addElement("href");
        iconHrefEl.addCharacters(contextPath+"/roleEndUser/ViewStyle?suid="+selectedStylesForCurrCap.getStyleId());
        
        
        SMOutputElement overlayXYEl = screenOverlayEl.addElement("overlayXY");
        overlayXYEl.addAttribute("x","0");
        overlayXYEl.addAttribute("y","1");
        overlayXYEl.addAttribute("xunits","fraction");
        overlayXYEl.addAttribute("yunits","fraction");
        
        SMOutputElement screenXYEl = screenOverlayEl.addElement("screenXY");
        screenXYEl.addAttribute("x","0");
        screenXYEl.addAttribute("y","1");
        screenXYEl.addAttribute("xunits","fraction");
        screenXYEl.addAttribute("yunits","fraction");
        
        SMOutputElement rotationXYEl = screenOverlayEl.addElement("rotationXY");
        rotationXYEl.addAttribute("x","0.5");
        rotationXYEl.addAttribute("y","0.5");
        rotationXYEl.addAttribute("xunits","fraction");
        rotationXYEl.addAttribute("yunits","fraction");
        
    }
    
    /**
     * Should create a similar to the following (common style) structure for the balloons per sensor 
     * <pre>
     *  &lt;Style id="sensorballoonstyle"&gt;
     *      &lt;BalloonStyle&gt;
     *          &lt;!-- a background color for the balloon. Probably common and NOT the style color --&gt;
     *          &lt;bgColor&gt;ffffffbb&lt;/bgColor&gt;
     *          &lt;!-- styling of the balloon text --&gt;
     *           &lt;text&gt;&lt;![CDATA[
     *           &lt;b&gt;&lt;font color="#CC0000" size="+3"&gt;$[name]&lt;/font&gt;&lt;/b&gt;
     *          &lt;br/&gt;&lt;br/&gt;
     *          &lt;font face="Courier"&gt;$[description]&lt;/font&gt;
     *          &lt;br/&gt;&lt;br/&gt;
     *          Extra text that will appear in the description balloon
     *          &lt;br/&gt;&lt;br/&gt;
     *          ]]&gt;&lt;/text&gt;
     *      &lt;/BalloonStyle&gt;
     *  &lt;/Style&gt;
     * </pre>
     */
    public static void appendStyleForSensorBalloon(SMOutputElement parentElement)
     throws javax.xml.stream.XMLStreamException {
        SMOutputElement  theStyleEl = parentElement.addElement("Style");
        theStyleEl.addAttribute("id", "SensorBalloonStyle");

        SMOutputElement  theBalloonStyleEl = theStyleEl.addElement("BalloonStyle");
       // theBalloonStyleEl.addCharacters("<!-- a background color for the balloon. Probably common and NOT the style color -->");
        
        SMOutputElement  thebgColorEl = theBalloonStyleEl.addElement("bgColor");
        thebgColorEl.addCharacters("ffffffbb");  // a common color
        
        SMOutputElement  theTextEl = theBalloonStyleEl.addElement("text");
        theTextEl.addCharacters("<b><font color=\"#CC0000\" size=\"+3\">$[name]</font></b>"+
                                        "<br/><br/>"+
                                        "<font face=\"Courier\">$[description]</font>"+
                                        "<br/><br/>"+
                                        "Have a nice day!<br/><br/>"); 
        
    }    
    
    /**
     * for room PlaceMarks
     * <pre>
     *  &lt;Style id=""Room_Placemark"&gt;
     *      &lt;IconStyle&gt;
     *           &lt;scale&gt;0.9&lt;/scale&gt;
     *           &lt;Icon&gt;
     *              &lt;href&gt;blahblahblah&lt;/href&gt;
     *           &lt;/Icon&gt;
     *      &lt;/IconStyle&gt;
     *  &lt;/Style&gt;
     * </pre>
     */
    public static void appendStyleForRoomPlaceMark(SMOutputElement parentElement, String contextPath)
    throws javax.xml.stream.XMLStreamException {
        SMOutputElement  theStyleEl = parentElement.addElement("Style");
        theStyleEl.addAttribute("id", KMLTranslate3dStyle.roomPlaceMarkStyleUrlId);
        SMOutputElement  theIconStyleEl = theStyleEl.addElement("IconStyle");
        SMOutputElement  scaleEl = theIconStyleEl.addElement("scale");
        scaleEl.addCharacters("0.9");
        SMOutputElement  theIconStyleIconEl = theIconStyleEl.addElement("Icon");
        SMOutputElement  theIconhrefEl = theIconStyleIconEl.addElement("href");
        theIconhrefEl.addCharacters(contextPath+"/Models/Media/"+Model3dStylesEntry.roomPlacemarkIconFilename);
    }
        
}
