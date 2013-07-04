<%@page session='false' contentType='text/html' import='java.util.*, vitro.vspEngine.service.engine.UserNode, presentation.webgui.vitroappservlet.Model3dservice.Model3dRoomPolygon'
%>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ page import="java.io.File" %>
<%@ page import="java.io.FilenameFilter" %>
<%@ page import="vitro.vspEngine.logic.model.Gateway" %>
<%@ page import="vitro.vspEngine.logic.model.GatewayWithSmartNodes" %>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Add new model mapping...</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/newModelMappingTasksJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/newModelMappingUploadJS.jsp"></script>
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
                    <b>Step 1: Select KML model and map it to an (unmapped) gateway.</b> <div style='display: none;'>(To do - future work) <a href="#" onclick="return false;">Autodetect</a></div>
                </td>
            </tr>            
            <tr> 
                <td colspan="2">
                    <table border="0" width="100%">
                        <tr bgcolor="#BFDEE3">
                            <td>Select KML model</td><td>Select corresponding WSI</td>
                        </tr>
                        <tr>
                            <td valign="top">
                                <table border="0">
                                    <tr>
                                        <td>                                            
                                            <select id="selectAKml3dFile" onChange="selectFrom3dKMLBox()">
                                                    <option value ="#" selected="true" >No KML model</option>
                                                <%
                                                    // We need to browse the folder of uploaded files and populate the selection dropdown
                                                    String uploadDirName =  application.getRealPath("/") + File.separator+"Models"+File.separator + "Large" + File.separator;
                                                    File dir = new File(uploadDirName);

                                                    File[] allFilesInDir = dir.listFiles(new FilenameFilter() {
                                                    public boolean accept(File dir, String filename)
                                                    { return filename.endsWith(".kml") || filename.endsWith(".KML") ; }
                                                    });
                                                    for (File modelFileTmp : allFilesInDir)
                                                    {
                                                        %>
                                                        <option value ="<%=modelFileTmp.getName() %>" ><%=modelFileTmp.getName() %></option>
                                                        <%
                                                    }

                                                %>
                                            </select>&nbsp;<span id="spanForViewUploaded3dFile"><a href="#" onclick="return false;">View model</a></span>
                                            <script type="text/javascript">resetSelectionBoxFro3dKML();</script>
                                        </td>
                                    </tr>
                                    <tr>    
                                        <td colspan="2">      
                                            <!-- This iframe is used as a place for the post to load -->
                                            <iframe id='targetUpload3dFile' name='targetUpload3dFile' src='' style='display: none'></iframe>
                                            
                                            <form enctype="multipart/form-data" name="form3dFile" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('3dFile','importKML3dFile','status3dFileUpl','submitUploadKML3dFileButton');" target="targetUpload3dFile">
                                                <input type="hidden" id="mode" name="mode" value="3dFile" />
                                                <input type="hidden" id="statusdiv" name="statusdiv" value="status3dFileUpl" />
                                                <input type="hidden" id="buttonid" name="buttonid" value="submitUploadKML3dFileButton" />
                                                Upload 3d model KML file: <input id="importKML3dFile" name="import3dKMLFile" type="file"> <br/>													
                                                <input id="submitUploadKML3dFileButton" type="submit" value="Upload" />
                                            </form>
                                            <!-- This is the upload status area -->
                                            <div id="status3dFileUpl"></div>                                          
                                        </td>
                                    </tr>
                                </table>
                            </td>
                            <td valign="top">
                                <table border="0">
                                    <tr>        
                                        <td>
                                            <select id="selectGwToMap" name="selectGwToMap" onChange="gwSelectionTransact()">
                                            <%
                                                HashMap<String, GatewayWithSmartNodes> infoGWHM = new HashMap<String, GatewayWithSmartNodes>();
                                                try
                                                {
                                                    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
                                                    infoGWHM = ssUN.getGatewaysToSmartDevsHM();

                                                }
                                                catch (Exception e)
                                                {
                                                     e.printStackTrace();
                                                }
                                                %>
                                                    <option value ="#" selected="selected">No gateway</option>
                                             <%

                                                Set<String> keysOfGIds;
                                                try{
                                                        if(infoGWHM != null)
                                                        {
                                                            keysOfGIds = infoGWHM.keySet();
                                                        }
                                                        else
                                                        {
                                                            keysOfGIds = (new HashMap<String, GatewayWithSmartNodes>()).keySet();
                                                        }
                                                    }
                                                    catch (Exception e)
                                                    {
                                                        keysOfGIds = (new HashMap<String, GatewayWithSmartNodes>()).keySet();;
                                                    }
                                                Iterator<String> itgwId = keysOfGIds.iterator();
                                                while(itgwId.hasNext()) {
                                                    String currGwId = itgwId.next();
                                                    Gateway currGw = infoGWHM.get(currGwId);
                                                    String gateId = currGw.getId();
                                                    String gateName = currGw.getName();
                                            %>
                                                <option value ="<%=gateId %>"><%=gateName%></option>
                                            <%
                                                    }
                                            %>    
                                            </select>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr bgcolor="#F3F783">
                <td colspan="2" >
                    <b>Step 2: Define a line of reference for room orientation (alignment)</b>
                </td>
            </tr>
            <tr bgcolor="#BFDEE3">
                    <td colspan="2">
                        Select line endpoints
                    </td>
            </tr>
            <tr>    
                <td valign="top">
                    <table border="0">
                        <tr>
                            <td valign="top">
                                From: <input type=text id="lineRefFrom" name="lineRefFrom" readonly />
                            </td>
                            <td valign="top">
                                To: <input type=text id ="lineRefTo" name="lineRefTo" readonly />
                            </td>
                        </tr>   
                        <tr>    
                            <td colspan="2">      
                                <!-- This iframe is used as a place for the post to load -->
                                <iframe id='targetUploadKMLLinePlaceMarks' name='targetUploadKMLLinePlaceMarks' src='' style='display: none'></iframe>
                                
                                <form enctype="multipart/form-data" name="formLinePlaceMarks" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('LinePlaceMarksFile','importKMLLinePlaceMarksFile','statusKMLLinePlaceMarksUpl','submitUploadKMLLinePlaceMarksButton');" target="targetUploadKMLLinePlaceMarks">
                                    Upload line endpoints' KML file: <input id="importKMLLinePlaceMarksFile" name="importKMLLinePlaceMarksFile" type="file"> <br/>
                                    <input id="submitUploadKMLLinePlaceMarksButton" type="submit" value="Upload" />
                                    <input type="hidden" id="mode" name="mode" value="LinePlaceMarksFile" />
                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusKMLLinePlaceMarksUpl" />
                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadKMLLinePlaceMarksButton" />                                    
                                </form>
                                <!-- This is the upload status area -->
                                <div id="statusKMLLinePlaceMarksUpl"></div>                                          
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
            <tr bgcolor="#F3F783">
                <td colspan="2" >
                    <b>Step 3: Define Rooms in the 3d Model</b> (At least one should be defined)
                </td>                
            </tr>
            <tr bgcolor="#BFDEE3">
                <td colspan="2">Give room center-points</td>
            </tr>
            <tr>
                <td colspan="2">
                    <table border="0">
                        <tr>
                            <td valign="top">
                                <form name="formRoom" onsubmit="return false;">
                                <span  id="enumOfRoomsDiv">
                                <table border="0"  cellspacing="5">
                                    <tr bgcolor="#BFDEE3">
                                        <td>Room Name</td><td>Type</td><td>Size (m)</td><td>Elevation Height (m)</td><td>Room Height (m)</td>
                                    </tr>    
                                    <tr>
                                        <td>
                                            No rooms defined
                                            <input type=hidden NAME="RoomNameBox[]" ID="RoomsNameBox_0"  value ="#" />
                                            <input type=hidden NAME="RoomCoordBox[]" ID="RoomCoordBox_0"  value ="#" />                                            
                                        </td>
                                        <td>
                                            <select NAME="selectARoomTypeBox[]" ID="selectARoomTypeBox_0" disabled="true" >
                                                <option value ="<%= Model3dRoomPolygon.getPolyTypeCube() %>">cubic</option>
                                            </select>                                                                        
                                        </td>
                                        <td>
                                            <input type=text id="RoomsSizeBox[]" name="RoomSizeBox_0" value="4"  maxlength="10" size="8" disabled="true" />
                                        </td>
                                        <td>
                                            <input type=text id="RoomsElevationBox[]" name="RoomElevationBox_0" value="0" maxlength="4" size="4" disabled="true" />
                                        </td>
                                        <td>
                                            <input type=text id="RoomsHeightBox[]" name="RoomHeightBox_0" value="3" maxlength="4" size="4" disabled="true" />
                                        </td>
                                    </tr>
                                </table>
                                </span>
                                </form>                                
                            </td>
                            <td valign="top">
                                <table border="0"  cellspacing="5">
                                    <tr bgcolor="#FFFEE3">
                                        <td>
                                            <a href="#" onclick="submitViewModelSoFarForm('preview'); return false;">View model so far</a>    
                                        </td>                                     
                                    </tr>
                                </table>
                            </td>
                        </tr>   
                        <tr>    
                            <td valign="top">      
                                <!-- This iframe is used as a place for the post to load -->
                                <iframe id='targetUploadKMLRoomCenterPoints' name='targetUploadKMLRoomCenterPoints' src='' style='display: none'></iframe>
                                
                                <form enctype="multipart/form-data" name="formRoomCenterPoints" method="post" action="<%=request.getContextPath()%>/roleVSP/Upload" onsubmit="return startStatusCheck('RoomCenterPointsFile','importKMLRoomCenterPointsFile','statusKMLRoomCenterPointsUpl','submitUploadKMLRoomCenterPointsButton');" target="targetUploadKMLRoomCenterPoints">
                                    Upload room centerpoints' KML file: <input id="importKMLRoomCenterPointsFile" name="importKMLRoomCenterPointsFile" type="file"> <br/>
                                    <input id="submitUploadKMLRoomCenterPointsButton" type="submit" value="Upload" />
                                    <input type="hidden" id="mode" name="mode" value="RoomCenterPointsFile" />
                                    <input type="hidden" id="statusdiv" name="statusdiv" value="statusKMLRoomCenterPointsUpl" />
                                    <input type="hidden" id="buttonid" name="buttonid" value="submitUploadKMLRoomCenterPointsButton" />                                    
                                </form>
                                <!-- This is the upload status area -->
                                <div id="statusKMLRoomCenterPointsUpl"></div>                                          
                            </td>
                            <td valign="top">
                                &nbsp;
                            </td>
                        </tr>
                    </table>                    
                </td>
            </tr>
            <tr bgcolor="#F3F783">
                <td colspan="2" >
                    <b>Step 4: Map motes to rooms</b> (To do - future work) Import placemarks. No motes should be left unmapped(?). <div style='display: none;'>Also: <a href="#" onclick="return false;">Autodetect</a>  </div>
                </td>                
            </tr>
            <tr bgcolor="#BFDEE3">
                <td colspan="2">Select a room and then select (multiple) sensors that should be located inside the room<br>
                                Motes will be placed randomly in the room, but you may edit their position later on (if desired)
                            </td>
            </tr>
            <tr>
                <td colspan="2">
                     <table border="0">
                        <tr>
                            <td valign="top">
                                <form name="formRoomToMotesMap" onsubmit="return false;">
                                <table border="0"  cellspacing="5">
                                    <tr>
                                        <td colspan="3">
                                            <div style="display: inline" id="FixedRoomToMotesMappingDiv"></div>        
                                        </td>
                                    </tr>                                    
                                    <tr bgcolor="#BFDEE3">
                                        <td>Select a Room</td><td>Select Smart Objects to assign in the room(s)</td><td>Action</td>
                                    </tr>
                                    <tr valign="top">
                                        <td>
                                           <div style="display: inline" id="selectARoomToMapDiv">
                                                <select NAME="selectARoomToMap" ID="selectARoomToMap" disabled="true" >
                                                    <option value ="#">No room</option>
                                                 </select>                                            
                                            </div>                                            
                                        </td>
                                        <td>
                                            <div id="checkSDToMapStatusDiv" style="display: inline"></div>
                                            <div class="verbosecolForNewModelDevs">
                                                <div style="display: inline" id="checkSmartDevsToMapDiv" >
                                                    <div id="SmartDevsToMapDiv_0" style="display: inline"><input type=checkbox name="SmartDevsToMap[]" id="SmartDevsToMap_0" value="" disabled="true" />No devices available</div>
                                                </div>
                                            </div>

                                        </td>
                                        <td>
                                            <input type="button" id="submitMapping_0" name="submitMapping_0" value="Add" onclick="addFixedRoomMotesMapping()" />
                                            <br><br>
                                            <input type="button" id="resetMappings_0" name="resetMappings_0" value="Clear" onclick="resetRoomMotesMappings()"  />
                                        </td>
                                    </tr>
                                </table>
                                </form>                                
                            </td>
                            <td valign="top">
                                <table border="0"  cellspacing="5">
                                    <tr bgcolor="#FFFEE3">
                                        <td>
                                            <a href="#" onclick="submitViewModelSoFarForm('preview'); return false;">View model so far</a>    
                                        </td>                                     
                                    </tr>
                                </table>
                            </td>
                        </tr>   
                        <tr>    
                            <td valign="top">      
                                &nbsp;
                            </td>
                            <td valign="top">
                                &nbsp;
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>            
            <tr>
                <td colspan="2">
                    <table border = "0">
                        <tr>
                            <td colspan="2">
                                <table border="0">
                                    <tr> 
                                        <td>
                                            <INPUT  NAME="SUBMIT3" ID="SubmitModelMapFinal" TYPE="BUTTON" VALUE="Submit model mapping" style="cursor:pointer; background-color:#8D8DF6; color:#FCFDBD;  font: 11px tahoma;" onclick="submitViewModelSoFarForm('submit'); return false;" >
                                        </td>
                                        <td>
                                            <div id="progressMsgSubmitFinal" style="display:none;position:absolute;"><img alt="Indicator" src="<%=request.getContextPath()%>/img/indicator.gif" /> Processing...</div>
                                        </td>
                                        <td>
                                            <div id="resultMsgSubmitFinal" style="display:none;position:absolute;">&nbsp;</div>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>        
                    </table>                    
                </td>
            </tr>
    </table>
    <form name="formViewModelSoFar" id="formViewModelSoFar" target="_blank" method="POST" action="<%=request.getContextPath()%>/roleVSP/MakeModel" onsubmit="return false;">
        <div id="vmsfvmsfFormDiv"></div>
    </form>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>