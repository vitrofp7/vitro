<%@page session='false' contentType='text/html' import="presentation.webgui.vitroappservlet.Common"
        %>
<html>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon" />
    <title>Create a new VITRO high level service (VSP view)</title>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/prototype.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/scriptaculous/scriptaculous.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/menudrop.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/genericStyle.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
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
<table>
    <tbody>
    <tr>
        <td>
        <form action="#" name="serviceCompositionFrm"><br>
        <table style="text-align: left; width: 692px; height: 60px;"
               border="1" cellpadding="2" cellspacing="2">
            <tbody>
            <tr  bgcolor="#F3F783"><td colspan="2"><span style="font-weight: bold;">Composite Service Template Definition (High level VITRO service)</span></td></tr>
            <tr>
                <td
                        style="vertical-align: top; width: 128px; height: 32px; text-align: right;">Service
                    Name:</td>
                <td style="vertical-align: top; width: 544px; height: 32px;"><input
                        name="serviceNameTxtBx"></td>
            </tr>
            <tr>
                <td
                        style="vertical-align: top; width: 128px; height: 30px; text-align: right;">Search
                    tags: <br>
                </td>
                <td style="vertical-align: top; width: 544px; height: 30px;"><input
                        name="tagsCSVtxtbx">&nbsp; (comma separated string values)</td>
            </tr>
            </tbody>
        </table>
        <br>
        <table style="text-align: left; height: 179px; width: 658px;"
               border="1" cellpadding="2" cellspacing="2">
            <tbody>
            <tr>
                <td style="vertical-align: top; width: 31px;"><input
                        name="requireDataEncryptCxBx" id="requireDataEncryptCxBx" value="OFF" type="checkbox"></td>
                <td style="vertical-align: top; width: 258px;"><label for="requireDataEncryptCxBx">Requires Data
                    Encryption (privacy)</label> </td>
                <td style="vertical-align: top; width: 344px;"><br>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top; width: 31px;"><input
                        name="allowDTNCxBx" id="allowDTNCxBx" value="OFF" type="checkbox"></td>
                <td style="vertical-align: top; width: 258px;"><label for="allowDTNCxBx">Allow DTN</label></td>
                <td
                        style="vertical-align: top; width: 344px; text-align: left;">Default
                    Max
                    Response Delay: <input name="dtndelaysecondsTxBx" size="5"> seconds<br>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top;"><input name="isOverridableCxBx" id="isOverridableCxBx"
                                                        value="OFF" type="checkbox"></td>
                <td style="vertical-align: top; width: 258px;"><label for="isOverridableCxBx">Is Overridable</label><br>
                </td>
                <td style="vertical-align: top; width: 344px;"><br>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top;"><input name="isTrackingCxBx"  id="isTrackingCxBx"
                                                        value="OFF" type="checkbox"></td>
                <td style="vertical-align: top; width: 258px;"><label for="isTrackingCxBx">Is a tracking
                    application</label> </td>
                <td style="vertical-align: top; width: 344px;">(should expect a
                    list of Object RFIDs to track)<br>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top;"><input name="usesMobileNodesCxBx" id="usesMobileNodesCxBx"
                                                        value="OFF" type="checkbox"></td>
                <td style="vertical-align: top; width: 258px;"><label for="usesMobileNodesCxBx">Use Mobile
                    Nodes</label> </td>
                <td style="vertical-align: top; width: 344px;"><br>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top;"><br>
                </td>
                <td
                        style="vertical-align: top; width: 258px; text-align: right;"> Set
                    Default
                    Desired Priority:</td>
                <td style="vertical-align: top; width: 344px;">
                    <select name="defaultPriorityLst">
                        <option>Very High</option>
                        <option>High</option>
                        <option>Medium</option>
                        <option>Low</option>
                        <option>Very Low</option>
                    </select>
                    <br>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top;"><br>
                </td>
                <td
                        style="vertical-align: top; text-align: right; width: 258px;">Pricing
                    Types (shown to the EU): <br>
                </td>
                <td style="vertical-align: top; width: 344px;">
                    <select name="pricingTypesLst">
                        <option>One time charge</option>
                        <option>Charge per Hour</option>
                        <option>Charge based on B/W</option>
                        <option>Monthly Subscription Charge</option>
                        <option>Annual Subscription Charge</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top;"><br>
                </td>
                <td
                        style="vertical-align: top; text-align: right; width: 258px;">Service
                    Level Agreement (SLA): <span style="text-decoration: underline;"><br>
</span></td>
                <td style="vertical-align: top; width: 344px;"><a
                        href="#">Edit</a><span
                        style="text-decoration: underline;"><br>
</span></td>
            </tr>
            </tbody>
        </table>
<br />
        <table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
            <tbody>
            <tr bgcolor="#BFDEE3">
                <td colspan="3"><span style="text-decoration: underline; font-weight: bold;">Involved Capabilities</span></td>
            </tr>
            <tr>
                <td style="vertical-align: top; width: 203px;">Available: </td>
                <td style="vertical-align: top; width: 35px;"><br>
                </td>
                <td style="vertical-align: top; width: 180px;">Selected: </td>
            </tr>
            <tr>
                <td style="vertical-align: top; width: 203px; "
                    rowspan="2">
                    <select size="15" name="InvolvedCaps" multiple="multiple">
                        <optgroup label="Sensing">
                            <option>Temperature</option>
                            <option>Luminance</option>
                            <option>Humidity</option>
                            <option>CO Concentration</option>
                            <option>CO2 Concentration</option>
                            <option>Wind Speed</option>
                            <option>Barometric Pressure</option>
                            <option>Sound Detection</option>
                            <option>Presence Detection</option>
                        </optgroup> <optgroup label="Actuating">
                        <option>Light Switch</option>
                        <option>Web Camera (Photo)</option>
                        <option>Web Camera (Video Feed)</option>
                        <option>Windows</option>
                        <option>Blinds</option>
                        <option>AirConditioner</option>
                    </optgroup>
                    </select>
                </td>
                <td style="vertical-align: top; width: 35px; height: 58px;"><input
                        name="AddCapBtn" value="&gt;" type="button"><br>
                    <input name="RemoveCapBtn" value="&lt;" type="button"> </td>
                <td style="vertical-align: top; width: 180px; height: 58px;"
                    rowspan="2">
                    <select size="15" name="InvolvedCaps" multiple="multiple">
                        <optgroup label="Sensing">
                            <option>Temperature</option>
                            <option>Luminance</option>
                            <option>Humidity</option>
                        </optgroup> <optgroup label="Actuating">
                        <option>Light Switch</option>
                        <option>Web Camera (photo)</option>
                    </optgroup>
                    </select>
                </td>
            </tr>
            <tr>
                <td style="vertical-align: top; width: 35px;"><br>
                </td>
            </tr>
            </tbody>
        </table>
        <br>
        <table style="text-align: left; width: 444px;" border="1"
               cellpadding="2" cellspacing="2">
            <tbody>
            <tr bgcolor="#BFDEE3">
                <td colspan="2"><span style="text-decoration: underline;font-weight: bold;">All Actions</span>&nbsp;(T) means Triggered only, (S) means Static only</td>
            </tr>
            <tr>
                <td>
                    <select size="8" name="AllActionsLst" multiple="multiple">
                        <option>01: GET [aggrFunctionList][explicit] *SensingCapability*|*ActuationState* FOR_SPECIFIC
                            [nodesList]/[LocationsList]/[GWList]</option>
                        <option>02: GET [aggrFunctionList][explicit] *SensingCapability*|*ActuationState* FOR_AT_LEAST *number*
                            [device][location][GW] FROM [nodesList]/[LocationsList]/[GWList]</option>
                        <option>03: GET [aggrFunctionList][explicit] *SensingCapability*|*ActuationState* FOR_EXACTLY *number*
                            [device][location][GW] FROM [nodesList]/[LocationsList]/[GWList]</option>
                        <option>04: ACTUATE *ActuatingCapability* FOR_SPECIFIC
                            [nodesList]/[LocationsList]/[GWList] SET_VALUE = *value*</option>
                        <option>05: ACTUATE *ActuatingCapability* FOR_AT_LEAST *number*
                            [device][location][GW] FROM [nodesList]/[LocationsList]/[GWList] SET_VALUE = *value* </option>
                        <option>06: ACTUATE *ActuatingCapability* FOR_EXACTLY *number*
                            [device][location][GW] FROM [nodesList]/[LocationsList]/[GWList] SET_VALUE = *value* </option>
                        <option>07:(S) GET_HISTORY [aggrFunctionList][explicit] *SensingCapability* FROM [specific]
                            [nodesList]/[LocationsList]/[GWList] HISTORY_PERIOD
                            [from/until][NumOfLastValues]</option>
                        <option>08:(T) STOP_STATIC_RULE [StaticRuleIDList]</option>
                        <option>09:(T) START_STATIC_RULE [ActionIDList]</option>
                        <option>10:(T) SEND e-mail_notification</option>
                        <option>11:(T) TERMINATE_SERVICE (*)</option>
                        <option>12:(T) NOTIFY AND TERMINATE_SERVICE (*)</option>
                        <option>13:(T) NOTIFY AND CONTINUE_SERVICE (*)</option>
                        <option>14:(T) Search For New Resources Then Begin Renegotiation Else Notify And Stop (*)</option>
                    </select>
                </td>
                <td style="vertical-align: top; width: 55px; height: 58px;"><input
                        name="AddStaticRuleBtn" value="Create New Action" type="button"><br>
                    <input name="RemoveActionBtn" value="Remove Selected" type="button"> </td>
                </td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr bgcolor="#BFDEE3">
                <td colspan="2"><span style="text-decoration: underline;font-weight: bold;">Conditions</span></td>
            </tr>
            <tr>
                <td>
                    <select size="8" name="ConditionsLst" multiple="multiple">
                        <option>01: ACTION01 HAS_NUMERIC_VALUE
                            [comparisonOperatorsList] *numeric_value* [anytime][at specific
                            time][from/until][for at least duration] *datetime*|*seconds*</option>
                        <option>02: ACTION02 HAS_BOOLEAN_VALUE [TRUE][FALSE] [anytime][at specific
                            time[from/until][for at least duration] *datetime*|*seconds* </option>
                        <option>03: ACTION03 HAS_NUMERIC_VALUE [comparisonOperatorsList] *numeric_value* [anytime][at specific
                            time][from/until][for at least duration] *datetime*|*seconds* </option>
                        <option>04: Override_Request_Received (*)</option>
                        <option>05: Conflict_Detected (*)</option>
                        <option>06: Partially_Feasible (*)</option>
                        <option>07: No_Longer_Feasible (*)</option>
                    </select>
                </td>
                <td style="vertical-align: top; width: 55px; height: 58px;"><input
                        name="AddConditionBtn" value="Create New Condition" type="button"><br>
                    <input name="RemoveConditionBtn" value="Remove Selected" type="button"> </td>
                </td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr bgcolor="#BFDEE3">
                <td colspan="2"><span style="text-decoration: underline;font-weight: bold;">Static Rules</span>&nbsp;(will
                    execute no mater what, after the service is deployed)</td>
            </tr>
            <tr>
                <td>
                    <select size="8" name="StaticRulesLst" multiple="multiple">
                        <option>EXECUTE ACTION01 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                        <option>EXECUTE ACTION02 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                        <option>EXECUTE ACTION04 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                        <option>EXECUTE ACTION07 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                    </select>
                </td>
                <td style="vertical-align: top; width: 55px; height: 58px;"><input
                        name="AddStaticRuleBtn" value="Create New Static Rule" type="button"><br>
                    <input name="RemoveStaticRuleBtn" value="Remove Selected" type="button"> </td>
                </td>
            </tr>
            <tr>
                <td colspan="2">&nbsp;</td>
            </tr>
            <tr  bgcolor="#BFDEE3">
                <td colspan="2"><span style="text-decoration: underline;font-weight: bold;">Trigger Rules</span>&nbsp;Combine Conditions with Actions (Nested Conditions Support) (order-defined priority
        among conflicting rules)</td>
            </tr>
            <tr>
                <td>
                    <select size="8" name="TriggerRulesLst" multiple="multiple">
                        <option>IF CONDITION01 AND CONDITION02 THEN ACTION01 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                        <option>IF CONDITION01 OR (CONDITION02 AND CONDITION03) THEN ACTION02 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                        <option>IF CONDITION04* THEN ACTION03 SCHEDULE [immediately][at][every][from/until][for at least duration]</option>
                        <option>IF CONDITION05* THEN ACTION08</option>
                        <option>IF CONDITION06* THEN
                            [ACTION12]|[ACTION13]|[ACTION14]</option>
                        <option>IF CONDITION07* THEN
                            [ACTION12]|[ACTION14]</option>
                    </select>
                </td>
                <td style="vertical-align: top; width: 55px; height: 58px;"><input
                        name="AddTriggerRuleBtn" value="New Trigger Rule" type="button"><br>
                    <input name="RemoveTriggerRuleBtn" value="Remove Selected" type="button"> </td>
                </td>
            </tr>
            </tbody>
        </table>
        <br/>
        <a href="#">
            Preview auto-generated Reports Template</a>&nbsp; &nbsp; &nbsp; <a
                href="#">Add
            Reports Templates</a><br>
        <br>
        <input name="submitComposedServiceBtn" value="Submit Composed Service"
               type="button">&nbsp;&nbsp;&nbsp;<input
                name="clearComposedServiceFieldsBtn" value="Clear Fields" type="button"><br>
        <br>
        </form>
        </td>
    </tr>
    </tbody>
</table>
<!-- begin the footer for the application -->
    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
</body>
</html>