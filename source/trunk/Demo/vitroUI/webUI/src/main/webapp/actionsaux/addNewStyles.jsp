<%@page session='false' contentType='text/html' import='java.util.*,presentation.webgui.vitroappservlet.Model3dservice.Model3dStyleSpecialCase'
%>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FilenameFilter" %>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Add new styles...</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/ColorPicker2Combined.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/newStyleTasksJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/newStyleUploadJS.jsp"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
</head>
<body><%-- For the menu --%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
    <%= Common.printDDMenu(application.getRealPath("/"), request) %>
<!-- this comment gets all the way to the browser -->
<%-- this comment gets discarded when the JSP is translated into a Servlet --%>
<p></p>
<table border="1" width="800">
    <tr  bgcolor="#F3F783">
        <td colspan="2">
            <b>Step 1: Select the capability for which this new style should be applied.</b> 
        </td>
    </tr>            
    <tr> 
        <td colspan="2">
            <table border="0" width="100%">
                <tr bgcolor="#BFDEE3">
                    <td colspan="2">Select a capability (<b>Definition of ranges is only allowed for numeric capabilities!</b>)</td>
                </tr>
                <tr>
                    <td valign="top">
                        <table border="0" cellpadding="4">    
                            <tr>
                                <td valign="top" >
                                    <table border="0" cellpadding="3">                            
                                        <tr>
                                            <td valign="top" colspan="3">                                            
                                                <select id="selectGenericCapability" >
                                                    <option value ="#" >No Capability</option>
                                                    <%
                                                        // capabilities LIST
                                                        Set<String> capsSet = (new HashMap<String, String>()).keySet();
                                                        try
                                                        {
                                                            UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
                                                            capsSet = ssUN.getCapabilitiesTable().keySet();
                                                        }
                                                        catch (Exception e)
                                                        {
                                                             e.printStackTrace();
                                                        }


                                                        Iterator<String> capsIt = capsSet.iterator();
                                                        int i=0;
                                                        String currentCap;
                                                        while(capsIt.hasNext()) {
                                                            currentCap = capsIt.next();
                                                     %>
                                                     <option value ="<%=currentCap%>" ><%=currentCap%></option>
                                                     <%
                                                        i++;
                                                        }
                                                     %>
                                                    <%
                                                        // We need to browse the folder of uploaded files and populate the selection dropdown
                                                        String uploadDirName =  application.getRealPath("/") + File.separator+"Models"+ File.separator + "Media" + File.separator;
                                                        File dir = new File(uploadDirName);

                                                        File[] allImagesInDir = dir.listFiles(new FilenameFilter() {
                                                            public boolean accept(File dir, String filename)
                                                            { return filename.endsWith(".kml") || filename.endsWith(".KML")
                                                                    || filename.endsWith(".PNG") || filename.endsWith(".png")
                                                                    || filename.endsWith(".JPG") || filename.endsWith(".jpg")
                                                                    || filename.endsWith(".ICO") || filename.endsWith(".ico")
                                                                    || filename.endsWith(".GIF") || filename.endsWith(".gif")
                                                                    || filename.endsWith(".BMP") || filename.endsWith(".bmp"); }
                                                            });
                                                        File[] allPrefabsInDir = dir.listFiles(new FilenameFilter() {
                                                            public boolean accept(File dir, String filename)
                                                            { return filename.endsWith(".DAE") || filename.endsWith(".dae"); }
                                                        });
                                                    %>

                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3">
                                                Select a default color:
                                                <!-- flooble.com Color Picker start -->
                                                <a href="javascript:pickColorColorPicker('pick1188464214');" id="pick1188464214" style="border: 1px solid #000000; font-family:Verdana; font-size:10px; text-decoration: none;">&nbsp;&nbsp;&nbsp;</a>
                                                <input id="pick1188464214field" size="7"  onChange="relateColorColorPicker('pick1188464214', this.value);" name="defaultColorSelected" value="#FFFFFF">
                                                <script language="javascript">relateColorColorPicker('pick1188464214', getObjColorPicker('pick1188464214field').value);</script>
                                                <noscript><a href="http://www.flooble.com/scripts/colorpicker.php">javascript color picker by flooble</a> | <a href="http://www.blackjack-primer.com">Blackjack Guide</a></noscript>
                                                <!-- flooble Color Picker end -->
                                            </td>
                                        </tr>                      
                                        <tr>
                                            <td valign="top">   
                                                <!-- This iframe is used as a place for the post to load -->
                                                <iframe id='targetUploadDefaultIconfile' name='targetUploadDefaultIconfile' src='' style='display: none'></iframe>
                                                
                                                <form enctype="multipart/form-data" name="formDefaultIconfile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('DefaultIconfile','importDefaultIconfile','statusDefaultIconfileUpl','submitUploadDefaultIconfileButton');" target="targetUploadDefaultIconfile">
                                                    Upload default icon file: <input id="importDefaultIconfile" name="importDefaultIconfile" type="file"> <br/>
                                                    <input id="submitUploadDefaultIconfileButton" type="submit" value="Upload" />
                                                    <input type="hidden" id="mode" name="mode" value="DefaultIconfile" />
                                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusDefaultIconfileUpl" />
                                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadDefaultIconfileButton" />                                    
                                                </form>
                                                <!-- This is the upload status area -->
                                                <div id="statusDefaultIconfileUpl"></div>                                          
                                            </td>
                                            <td valign="top">      
                                                &nbsp;&nbsp;or select&nbsp;&nbsp;
                                            </td>
                                            <td valign="top">                                            
                                                <select id="selectDefaultIconfile" >
                                                    <option value ="#" >No Icon</option>
                                                    <%    for (File modelFileTmp : allImagesInDir)
                                                    {
                                                    %>
                                                    <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                                <script type="text/javascript">resetSelectionBox('selectDefaultIconfile');</script>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top">      
                                                <!-- This iframe is used as a place for the post to load -->
                                                <iframe id='targetUploadDefaultPrefabfile' name='targetUploadDefaultPrefabfile' src='' style='display: none'></iframe>
                                                
                                                <form enctype="multipart/form-data" name="formDefaultPrefabfile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('DefaultPrefabfile','importDefaultPrefabfile','statusDefaultPrefabfileUpl','submitUploadDefaultPrefabfileButton');" target="targetUploadDefaultPrefabfile">
                                                    Upload default prefab file: <input id="importDefaultPrefabfile" name="importDefaultPrefabfile" type="file"> <br/>
                                                    <input id="submitUploadDefaultPrefabfileButton" type="submit" value="Upload" />
                                                    <input type="hidden" id="mode" name="mode" value="DefaultPrefabfile" />
                                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusDefaultPrefabfileUpl" />
                                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadDefaultPrefabfileButton" />                                    
                                                </form>
                                                <!-- This is the upload status area -->
                                                <div id="statusDefaultPrefabfileUpl"></div>                                          
                                            </td>
                                            <td valign="top">      
                                                &nbsp;&nbsp;or select&nbsp;&nbsp;
                                            </td>
                                            <td valign="top">                                            
                                                <select id="selectDefaultPrefabfile" >
                                                    <option value ="#" >No Prefab</option>
                                                    <%    for (File modelFileTmp : allPrefabsInDir)
                                                    {
                                                    %>
                                                    <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                                <script type="text/javascript">resetSelectionBox('selectDefaultPrefabfile');</script>
                                            </td>   
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr  bgcolor="#F3F783">
        <td colspan="2">
            <b>Step 2: Assign a specific color, icon and/or prefab to the following special value cases.</b> 
        </td>
    </tr>            
    <tr> 
        <td colspan="2">
            <table border="0" width="100%">
                <tr bgcolor="#BFDEE3">
                    <td>Assign attributes to each special value </td>
                </tr>
                <tr>
                    <td valign="top">
                        <table border="0" cellpadding="4">
                            <tr bgcolor="#BFDEE3" >
                                <td style="border-right: solid 1px #000080; border-top: solid 1px #000080; border-left: solid 1px #000080;">Special value and attributes</td>
                                <td style="border-right: solid 1px #000080; border-top: solid 1px #000080; border-left: solid 1px #000080; "> Action </td>
                            </tr>
                            <tr>
                                <td valign="top" style="border-right: solid 1px #000080; border-bottom: solid 1px #000080; border-left: solid 1px #000080;">
                                    <table border="0" cellpadding="3">
                                            <tr>
                                                <td>
                                                    <form name="formSpecialValueCases" onsubmit="return false;" >
                                                        <div style="display: inline" id="ListSpecialValueCasesDiv"></div>
                                                    </form>
                                                </td>                                            
                                            </tr>                                        
                                            <tr>
                                                <td valign="top" colspan="3">
                                                    <div style="display: inline" id="selectSpecialValueCasesDiv">
                                                        <select NAME="selectSpecialValueCase" id="selectSpecialValueCase" >
                                                            <%
                                                            String[] allValidSpVal = Model3dStyleSpecialCase.getValidSpecialValues();
                                                            for(int j = 0 ; j < allValidSpVal.length - 1; j++) // length -1 to leave out the "undefined special value" case
                                                            {%>
                                                            <option value ="<%= allValidSpVal[j]%>" ><%=allValidSpVal[j]%></option>
                                                            <%}
                                                            %>
                                                        </select>
                                                        <script type="text/javascript">resetSelectionBox('selectSpecialValueCase');</script>
                                                    </div>
                                                </td>
                                            </tr>
                                        <tr>
                                            <td valign="top" colspan="3">
                                                Select a color:                                                  <!-- flooble.com Color Picker start -->
                                                <a href="javascript:pickColorColorPicker('pick1188464300');" id="pick1188464300" style="border: 1px solid #000000; font-family:Verdana; font-size:10px; text-decoration: none;">&nbsp;&nbsp;&nbsp;</a>
                                                <input id="pick1188464300field" size="7"  onChange="relateColorColorPicker('pick1188464300', this.value);" name="SpecialValueColorSelected" >
                                                <script language="javascript">relateColorColorPicker('pick1188464300', getObjColorPicker('pick1188464300field').value);</script>
                                                <noscript><a href="http://www.flooble.com/scripts/colorpicker.php">javascript color picker by flooble</a> | <a href="http://www.blackjack-primer.com">Blackjack Guide</a></noscript>
                                                <!-- flooble Color Picker end -->
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top">  
                                                <!-- This iframe is used as a place for the post to load -->
                                                <iframe id='targetUploadSpecialValueIconfile' name='targetUploadSpecialValueIconfile' src='' style='display: none'></iframe>
                                                
                                                <form enctype="multipart/form-data" name="formSpecialValueIconfile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('SpecialValueIconfile','importSpecialValueIconfile','statusSpecialValueIconfileUpl','submitUploadSpecialValueIconfileButton');" target="targetUploadSpecialValueIconfile">
                                                    Upload icon file: <input id="importSpecialValueIconfile" name="importSpecialValueIconfile" type="file"> <br/>
                                                    <input id="submitUploadSpecialValueIconfileButton" type="submit" value="Upload" />
                                                    <input type="hidden" id="mode" name="mode" value="SpecialValueIconfile" />
                                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusSpecialValueIconfileUpl" />
                                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadSpecialValueIconfileButton" />                                    
                                                </form>
                                                <!-- This is the upload status area -->
                                                <div id="statusSpecialValueIconfileUpl"></div>                                          
                                            </td>
                                            <td valign="top">      
                                                &nbsp;&nbsp;or select&nbsp;&nbsp;
                                            </td>
                                            <td valign="top">                                            
                                                <select id="selectSpecialValueIconfile" >
                                                    <option value ="#" >No Icon</option>
                                            <%    for (File modelFileTmp : allImagesInDir)
                                                    {
                                                    %>
                                                    <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                                <script type="text/javascript">resetSelectionBox('selectSpecialValueIconfile');</script>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top">      
                                                <!-- This iframe is used as a place for the post to load -->
                                                <iframe id='targetUploadSpecialValuePrefabfile' name='targetUploadSpecialValuePrefabfile' src='' style='display: none'></iframe>
                                                
                                                <form enctype="multipart/form-data" name="formSpecialValuePrefabfile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('SpecialValuePrefabfile','importSpecialValuePrefabfile','statusSpecialValuePrefabfileUpl','submitUploadSpecialValuePrefabfileButton');" target="targetUploadSpecialValuePrefabfile">
                                                    Upload prefab file: <input id="importSpecialValuePrefabfile" name="importSpecialValuePrefabfile" type="file"> <br/>
                                                    <input id="submitUploadSpecialValuePrefabfileButton" type="submit" value="Upload" />
                                                    <input type="hidden" id="mode" name="mode" value="SpecialValuePrefabfile" />
                                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusSpecialValuePrefabfileUpl" />
                                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadSpecialValuePrefabfileButton" />                                    
                                                </form>
                                                <!-- This is the upload status area -->
                                                <div id="statusSpecialValuePrefabfileUpl"></div>                                          
                                            </td>
                                            <td valign="top">      
                                                &nbsp;&nbsp;or select&nbsp;&nbsp;
                                            </td>
                                            <td valign="top">
                                                <select id="selectSpecialValuePrefabfile" >
                                                    <option value ="#" >No Prefab</option>
                                                <%    for (File modelFileTmp : allPrefabsInDir)
                                                    {
                                                %>
                                                    <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                <%
                                                    }
                                                %>
                                                </select>
                                                <script type="text/javascript">resetSelectionBox('selectSpecialValuePrefabfile');</script>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td valign="top" style="border-right: solid 1px #000080; border-bottom: solid 1px #000080; border-left: solid 1px #000080;">
                                    <table border="0" cellpadding="3">
                                        <tr>
                                            <td>
                                                <input type="button" id="submitSpecialValueCase_0" name="submitSpecialValueCase_0" value="Add this" onclick="addSpecialValueCase()" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type="button" id="resetSpecialValueCases_0" name="resetSpecialValueCases_0" value="Clear all" onclick="resetSpecialValueCases()"  />
                                            </td>                                
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr  bgcolor="#F3F783">
        <td colspan="2">
            <b>Step 3: Define a numeric value range and assign a specific color, icon and/or prefab to it.</b> 
        </td>
    </tr>            
    <tr> 
        <td colspan="2">
            <table border="0" width="100%">
                <tr bgcolor="#BFDEE3">
                    <td>Define ranges <b>[From, to)</b> and assign attributes</td>
                </tr>
                <tr>
                    <td valign="top">
                        <table border="0" cellpadding="4">
                            <tr bgcolor="#BFDEE3" >
                                <td style="border-right: solid 1px #000080; border-top: solid 1px #000080; border-left: solid 1px #000080;">Value range and attributes</td>
                                <td style="border-right: solid 1px #000080; border-top: solid 1px #000080; border-left: solid 1px #000080; "> Action </td>
                            </tr>
                            <tr>
                                <td valign="top" style="border-right: solid 1px #000080; border-bottom: solid 1px #000080; border-left: solid 1px #000080;">
                                    <table border="0" cellpadding="3">
                                        <tr>
                                            <td>
                                                <form name="formNumericRangeCases" onsubmit="return false;" >                                                    
                                                    <div style="display: inline" id="ListNumericRangeCasesDiv"></div>
                                                </form>
                                            </td>                                            
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3">
                                                From: <input type=text id="rangeValFrom" name="rangeValFrom" />
                                                &nbsp;&nbsp;&nbsp;&nbsp;
                                                To: <input type=text id ="rangeValTo" name="rangeValTo" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top" colspan="3">
                                                Select a color:<a href="javascript:pickColorColorPicker('pick1188464400');" id="pick1188464400" style="border: 1px solid #000000; font-family:Verdana; font-size:10px; text-decoration: none;">&nbsp;&nbsp;&nbsp;</a>
                                                <input id="pick1188464400field" size="7"  onChange="relateColorColorPicker('pick1188464400', this.value);" name="NumericRangeColorSelected" >
                                                <script language="javascript">relateColorColorPicker('pick1188464400', getObjColorPicker('pick1188464400field').value);</script>
                                                <noscript><a href="http://www.flooble.com/scripts/colorpicker.php">javascript color picker by flooble</a> | <a href="http://www.blackjack-primer.com">Blackjack Guide</a></noscript>
                                                <!-- flooble Color Picker end -->
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top">      
                                                <!-- This iframe is used as a place for the post to load -->
                                                <iframe id='targetUploadNumericRangeIconfile' name='targetUploadNumericRangeIconfile' src='' style='display: none'></iframe>
                                                
                                                <form enctype="multipart/form-data" name="formNumericRangeIconfile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('NumericRangeIconfile','importNumericRangeIconfile','statusNumericRangeIconfileUpl','submitUploadNumericRangeIconfileButton');" target="targetUploadNumericRangeIconfile">
                                                    Upload icon file: <input id="importNumericRangeIconfile" name="importNumericRangeIconfile" type="file"> <br/>
                                                    <input id="submitUploadNumericRangeIconfileButton" type="submit" value="Upload" />
                                                    <input type="hidden" id="mode" name="mode" value="NumericRangeIconfile" />
                                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusNumericRangeIconfileUpl" />
                                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadNumericRangeIconfileButton" />                                    
                                                </form>
                                                <!-- This is the upload status area -->
                                                <div id="statusNumericRangeIconfileUpl"></div>                                          
                                            </td>
                                            <td valign="top">      
                                                &nbsp;&nbsp;or select&nbsp;&nbsp;
                                            </td>
                                            <td valign="top">                                            
                                                <select id="selectNumericRangeIconfile" >
                                                    <option value ="#" >No Icon</option>
                                                    <%    for (File modelFileTmp : allImagesInDir)
                                                    {
                                                    %>
                                                    <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                                <script type="text/javascript">resetSelectionBox('selectNumericRangeIconfile');</script>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="top">      
                                                <!-- This iframe is used as a place for the post to load -->
                                                <iframe id='targetUploadNumericRangePrefabfile' name='targetUploadNumericRangePrefabfile' src='' style='display: none'></iframe>
                                                
                                                <form enctype="multipart/form-data" name="formNumericRangePrefabfile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('NumericRangePrefabfile','importNumericRangePrefabfile','statusNumericRangePrefabfileUpl','submitUploadNumericRangePrefabfileButton');" target="targetUploadNumericRangePrefabfile">
                                                    Upload prefab file: <input id="importNumericRangePrefabfile" name="importNumericRangePrefabfile" type="file"> <br/>
                                                    <input id="submitUploadNumericRangePrefabfileButton" type="submit" value="Upload" />
                                                    <input type="hidden" id="mode" name="mode" value="NumericRangePrefabfile" />
                                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusNumericRangePrefabfileUpl" />
                                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadNumericRangePrefabfileButton" />                                    
                                                </form>
                                                <!-- This is the upload status area -->
                                                <div id="statusNumericRangePrefabfileUpl"></div>                                          
                                            </td>
                                            <td valign="top">      
                                                &nbsp;&nbsp;or select&nbsp;&nbsp;
                                            </td>
                                            <td valign="top">                                            
                                                <select id="selectNumericRangePrefabfile" >
                                                    <option value ="#" >No Prefab</option>
                                                    <%    for (File modelFileTmp : allPrefabsInDir)
                                                    {
                                                    %>
                                                    <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                                <script type="text/javascript">resetSelectionBox('selectNumericRangePrefabfile');</script>
                                            </td>                                
                                        </tr>
                                    </table>
                                </td>
                                <td valign="top" style="border-right: solid 1px #000080; border-bottom: solid 1px #000080; border-left: solid 1px #000080;">
                                    <table border="0" cellpadding="3">
                                        <tr>
                                            <td>
                                                <input type="button" id="submitNumericRangeCase_0" name="submitNumericRangeCase_0" value="Add this" onclick="addNumericRangeCase()" />
                                            </td>
                                        </tr>
                                        <tr>
                                            <td>
                                                <input type="button" id="resetNumericRangeCases_0" name="resetNumericRangeCases_0" value="Clear all" onclick="resetNumericRangeCases()"  />
                                            </td>                                
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr  bgcolor="#F3F783">
        <td colspan="2">
            <b>Step 4: Preview and submit new style.</b> 
        </td>
    </tr>            
    <tr>         
        <td colspan="2">
            <table border="0" width="100%">
                <tr bgcolor="#BFDEE3">
                    <td colspan="2">Preview legend picture and submit</td>
                </tr>
                <tr>
                    <td colspan="2">
                        <table border="0">
                            <tr bgcolor="#FFFEE3">
                                <td>
                                    <a href="#" onclick="submitNewStyleSoFarForm('preview'); return false;">View style legend so far</a>    
                                </td>                                     
                            </tr>
                            <tr> 
                                <td>
                                    <INPUT  NAME="SUBMIT4" ID="SubmitStyleFinal" TYPE="BUTTON" VALUE="Submit new style" style="cursor:pointer; background-color:#8D8DF6; color:#FCFDBD;  font: 11px tahoma;" onclick="submitNewStyleSoFarForm('submit'); return false;" >
                                </td>
                                <td>
                                    <div id="progressMsgStyleSubmitFinal" style="display:none;position:absolute;"><img alt="Indicator" src="<%=request.getContextPath()%>/img/indicator.gif" /> Processing...</div>
                                </td>
                                <td>
                                    <div id="resultMsgStyleSubmitFinal" style="display:none;position:absolute;">&nbsp;</div>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>        
            </table>                    
        </td>
    </tr>
</table>
<form name="formNewStyleSoFar" id="formNewStyleSoFar" target="_blank" method="POST" action="<%=request.getContextPath()%>/roleVSP/CreateStyle" onsubmit="return false;">
        <div id="nssfnsfFormDiv"></div>
</form>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>