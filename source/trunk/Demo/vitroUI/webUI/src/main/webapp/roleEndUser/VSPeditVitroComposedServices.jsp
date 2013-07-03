<!DOCTYPE html>
<%@ page session='false' contentType='text/html' import='java.util.*, presentation.webgui.vitroappservlet.*,vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'%>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="vitro.vspEngine.service.persistence.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- #superceded -->
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Service Management</title>
    <link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
    <link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/compositeServiceCasualTransactJS.jsp"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/gatewaysAndQuerysViewTasksJS.jsp"></script>


    <script type="text/javascript">
        $(document).ready(function(){
            $('#dashboardSettingsButton').addClass("active");
            $('#srv-list').addClass("active");
        });
    </script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <!--    <script type="text/javascript" src="<%=request.getContextPath()%>/js/actb.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/js/table.js"></script>-->
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.uitablefilter.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css" />
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
    <script Language=JavaScript>
        function checkAll()
        {

            var allRows = document.getElementsByName("selectRow");
            for (var i=0; i<allRows.length;i++)
            {
                if(allRows[i].type =="checkbox")
                {
                    allRows[i].checked=true;
                }
            }
        }
    </script>




    <script Language=JavaScript>
        function uncheckAll()
        {

            var allRows = document.getElementsByName("selectRow");
            for (var i=0; i<allRows.length;i++)
            {
                if(allRows[i].type =="checkbox")
                {
                    allRows[i].checked=false;
                }
            }
        }

        function checkThis(uniqId) {
            var allRows = document.getElementsByName("selectRow");
            for (var i=0; i<allRows.length;i++)
            {
                if(allRows[i].type =="checkbox" && allRows[i].value == uniqId )
                {
                    allRows[i].checked=true;
                    break;
                }
            }
        }
    </script>
    <script language="JavaScript">   /*
        function createSelectedList()
        {

            var allRows = document.getElementsByName("selectRow");

            var ListOfSelected=new Array();
            var Listedvalues ="";
            var j=1;

            for (var i=0;i<allRows.length;i++)
            {
                if (allRows[i].checked==true)
                {
                    Listedvalues = Listedvalues+"ListOfSelected="+allRows[i].value+"?";
                    ListOfSelected[j-1]=allRows[i].value;
                    j++;
                }
            }
            Listedvalues = Listedvalues+"ListOfSelected="+allRows[allRows.length-1].value;
            return ListOfSelected;
            //return Listedvalues;
        }
        */
    </script>
    <script type="text/javascript">
        /*
        $(function(){

            $("#pippo").click(function(){var SelList = createSelectedList();
                var url= new String("<%=request.getContextPath()%>/roleVSP/RemoveListOfSelectedAction?");

                for (var i=0;i<SelList.length-1;i++)
                {
                    url = url+"ListOfSelected="+ SelList[i] + "&";
                }
                url =  url+"ListOfSelected="+ SelList[SelList.length-1];

                window.location.replace(url);

            });
        });
        */
    </script>

    <script>
        $(function() {
            var theTable = $('table.servicelist')

            theTable.find("tbody > tr").find("td:eq(1)").mousedown(function(){
                $(this).prev().find(":checkbox").click()
            });

            $("#filter").keyup(function() {
                $.uiTableFilter( theTable, this.value );
            })

            $('#filter-form').submit(function(){
                theTable.find("tbody > tr:visible > td:eq(1)").mousedown();
                return false;
            }).focus(); //Give focus to input field
        });
    </script>

</head>
<body><%-- For the menu --%>






<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/XulMenu.js"></script>--%>
<%--<script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdownMenu/parseMenuHtml.jsp"></script>--%>
<%= Common.printDDMenu(application.getRealPath("/"), request) %>
<div class="container-fluid">
    <div class="row-fluid">
        <div class="span2">
            <%= Common.printSideMenu(application.getRealPath("/"), request) %>
        </div>
        <div class="span10">
            <div class="well">
                <table class="table" border="1">
                    <tbody>
                    <tr>
                        <td>
                            <table id="allVSNsTbl" class="table table-striped servicelist reborder">
                                <thead>
                                <tr style="background-color: #BFDEE3!important;">
                                    <td colspan="2">
                                        <h4>VITRO Services:</h4>
                                    </td>
                                    <td colspan="4">
                                        <div class="input-append" style="text-align: right;">
                                            <form id="filter-form" style="margin: 0!important;">
                                                <span class="add-on">Filter:</span>
                                                <input name="filter" id="filter" value="" maxlength="30" type="text">
                                            </form>
                                        </div>
                                    </td>
                                </tr>

                                <tr bgcolor="#f3c683">
                                    <td align="left" colspan = "6">
                                        <div class="row-fluid">
                                            <div class="btn-group">
                                                <input class="btn" type="button" name="selectall" value="Select All" onclick="checkAll()" />
                                                <input class="btn" type="button" name="unselectall" value="Unselect All" onclick="uncheckAll()" />
                                                <input class="btn" type="button" value="Remove Selected" name="pippo" id="pippo" onclick="actOnSelectedCompositeServices('removeBatch'); " />
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                                <tr bgcolor="#f3f783">
                                    <td style="vertical-align: top; width: 58px; text-align: left; font-weight: bold;">Selection<br>
                                    </td>
                                    <td style="vertical-align: top; width: 271px; text-align: left; font-weight: bold;">Service<br>
                                    </td>
                                    <td style="vertical-align: top; width: 163px; text-align: left; font-weight: bold;">Tags<br>
                                    </td>
                                    <!--            <td style="vertical-align: top; width: 175px; text-align: center; font-weight: bold;">Popularity<br>
                                   </td>-->
                                   <!-- <td style="vertical-align: top; width: 170px; text-align: left; font-weight: bold;">Edit<br>
                                   </td> -->
                                    <td style="vertical-align: top; width: 170px; text-align: left; font-weight: bold;">Sampling (secs)<br>
                                    </td>
                                    <td style="vertical-align: top; width: 160px; text-align: left; font-weight: bold;" >Remove<br>
                                    </td>
                                    <td style="vertical-align: top; width: 80px; text-align: left; font-weight: bold;" >Action<br>
                                    </td>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach items="${composedServiceList}"  var="uiComposedService">
                                    <tr id="tr_${uiComposedService.getUniqueQid()}">
                                        <td style="vertical-align: top; width: 58px; text-align: left;"><input name="selectRow" type="checkbox" value="${uiComposedService.getUniqueQid()}"><br>
                                        </td>
                                        <td style="vertical-align: top; width: 271px;">${uiComposedService.getComposedService().getFriendlyName()}<br></td>
                                        <td style="vertical-align: top; width: 163px;">${uiComposedService.searchTagsString}</td>
                                        <!--<td style="vertical-align: top; width: 80px; text-align: left;">
                                            <form action="<%=request.getContextPath()%>/roleEndUser/EditComposedServiceAction" style="margin: 0!important;">
                                                <input type="hidden" name="compservId" value="${uiComposedService.getComposedService().id}" />
                                                <input class="btn" type="submit" value="Edit"/>
                                            </form>
                                        </td> -->
                                        <td style="vertical-align: top; width: 80px; text-align: left;">
                                            <c:choose>
                                                <c:when test="${uiComposedService.isDeployed()}">
                                                    <input type="text" id="samplePer_${uiComposedService.getUniqueQid()}" value="${uiComposedService.getCurrentSamplingPeriod()}" maxlength="4" size="40px" onchange="javascript:modifiedSamplePeriod('${uiComposedService.getUniqueQid()}');"/>
                                                </c:when>
                                                <c:otherwise>
                                                      &nbsp;
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="vertical-align: top; width: 80px; text-align: left;">
                                            <input type="hidden" name="compservId" value="${uiComposedService.getComposedService().id}" />
                                            <input class="btn" type="submit" value="Remove" onclick="uncheckAll(); checkThis('${uiComposedService.getUniqueQid()}');actOnSelectedCompositeServices('removeBatch'); " />
                                       </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${uiComposedService.isDeployed()}">
                                                    <a href="#" onClick="deployCompositeService('${uiComposedService.getComposedService().name}','${uiComposedService.getComposedService().id}');return false;"><span style="display: none;" id="DeployCommandTextDivNew_${uiComposedService.getComposedService().id}"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                                                    <a href="#" onClick="changeQueryRunningStatus('${uiComposedService.getUniqueQid()}');return false;"><span style="display:none" id="queryStatusActionTextDiv_${uiComposedService.getUniqueQid()}">${uiComposedService.getStatus() }</span><span id="queryStatusActionImgDiv_${uiComposedService.getUniqueQid()}">${uiComposedService.getStatusImgHtml() }</span></a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a href="#" onClick="deployCompositeService('${uiComposedService.getComposedService().name}','${uiComposedService.getComposedService().id}');return false;"><span  id="DeployCommandTextDivNew_${uiComposedService.getComposedService().id}"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                                                    <a href="#" onClick="changeQueryRunningStatus('${uiComposedService.getUniqueQid()}');return false;"><span style="display:none" id="queryStatusActionTextDiv_${uiComposedService.getUniqueQid()}">${uiComposedService.getStatus() }</span><span id="queryStatusActionImgDiv_${uiComposedService.getUniqueQid()}">${uiComposedService.getStatusImgHtml() }</span></a>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>


                                </tbody>
                            </table>


                            <!--   <a href="#">Previous page</a>&nbsp; <a href="#">Next page</a><br>
                            -->

                            <!--  </form>-->
                        </td></tr></tbody></table>
                <!--
                <pre><code>
                 <script language="javascript" type="text/javascript">
                //<![CDATA[
                    var table2_Props = 	{
                                    col_0: "none",
                                    col_1: "none",
                                    col_2: "select",
                                    col_3: "none",
                                    col_4: "none",
                                    col_5: "none",
                                    col_6: "none",
                                    display_all_text: " [ Show all ] ",
                                    sort_select: true
                                };
                    setFilterGrid( "table2",table2_Props );
                    var table1_Props = 	{
                            grid: false,
                        };
                setFilterGrid( "table1",table1_Props,-1 );
                //]]>
                </script>
                </code></pre>-->
            </div>
        </div>
    </div>
</div>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>
