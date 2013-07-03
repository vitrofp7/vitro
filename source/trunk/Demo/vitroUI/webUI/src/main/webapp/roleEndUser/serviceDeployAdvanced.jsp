<!DOCTYPE html>
<%@page session='false' contentType='text/html'
        import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.geo.*, vitro.vspEngine.service.query.*'
        %>
<%@ page import="presentation.webgui.vitroappservlet.Common" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>
    <title>Deploy a VITRO service </title>

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>	

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/getXMLRequest.js"></script>
    <script type="text/javascript" language="JavaScript" src="<%=request.getContextPath()%>/js/compositeServiceCasualTransactJS.jsp"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/proDropdownMenu1/stuHover.js" ></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/upload.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/proDropdownMenu1/proDropdownMenu1.css" />

 	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">
	
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardSettingsButton').addClass("active");
		$('#srv-new').addClass("active");
 	});    
	</script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.uitablefilter.js"></script>

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
    </script>
    <script language="JavaScript">
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
    </script>
    <script type="text/javascript">

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

    <script>
        $(function() {
            var theTable = $('table.composedservicelist')

            theTable.find("tbody > tr").find("td:eq(1)").mousedown(function(){
                $(this).prev().find(":checkbox").click()
            });

            $("#filtercomposed").keyup(function() {
                $.uiTableFilter( theTable, this.value );
            })

            $('#filter-form-composed').submit(function(){
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
<!-- this comment gets all the way to the browser -->
<%-- this comment gets discarded when the JSP is translated into a Servlet --%>
<table border="1" class="table"><tbody><tr><td>
    <form action="#" name="findServices">
        <table style="text-align: left;" class="table table-striped  servicelist" cellpadding="2" cellspacing="2">
            <thead>
            <tr>
				<td style="background-color: #f3f783!important;">
					<h4>VITRO Services:</h4>
				</td>
				<td style="text-align: right; background-color: #f3f783!important;">

					<div class="input-append">
						<form id="filter-form">
                                                <span class="add-on">Filter:</span>
                                                <input name="filter" id="filter" value="" maxlength="30" type="text">
                                            </form>
<!--<span class="add-on">Keywords
                    Search:</span>
					<input id="appendedInputButtons" type="text" maxlength="255" tabindex="1" name="searchkeysTxtBx">
					<input value="Search..." name="searchfiltersbtn" type="button" class="btn">-->
					</div>
                </td>
			</tr>
            <tr bgcolor="#BFDEE3">
  
                <td style="vertical-align: top; width: 471px; text-align: left; font-weight: bold;"><strong>Service</strong>
                </td>
 <!--                 <td style="vertical-align: top; width: 163px; text-align: center; font-weight: bold;"><strong>Details</strong>
                </td>
                <td style="vertical-align: top; width: 175px; text-align: center; font-weight: bold;"><strong>Popularity</strong>
                </td>
                <td style="vertical-align: top; width: 170px; text-align: center; font-weight: bold;"><strong>User Rating</strong>
                </td>
                <td style="vertical-align: top; width: 730px; text-align: center; font-weight: bold;"><strong>Review/Edit
                    Configuration</strong>
                </td> -->
                <td style="vertical-align: top; width: 60px; text-align: right; font-weight: bold;"><strong>Deploy</strong>
                </td>
            </tr>
	</thead>
	<tbody>
            <tr>
                <td style="vertical-align: top; width: 471px;">Monitor
                    Temperature in Patras and Rome<br>
                </td>
  <!--                <td style="vertical-align: top; width: 163px;"><a href="#">More...</a><br>
                </td>
                <td style="vertical-align: top; width: 175px; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                <td style="vertical-align: top; width: 170px; text-align: right;">90%<br>
                </td>
                <td style="vertical-align: top; width: 730px; text-align: center;"><a href="#">Default...</a><br>
                </td> -->
                <td style="text-align: right;">
                    <a href="#" onClick="deployCompositeService('Monitor Temperature in Patras and Rome','pre0');return false;"><span id="DeployCommandTextDiv_0"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                </td>
            </tr>
            <tr>
               <%-- <td style="vertical-align: top; width: 58px; text-align: center;"><input name="selectRow" type="checkbox"><br>
                </td>--%>
                <td style="vertical-align: top; width: 471px;">Monitor Temperature in Colombes France<br>
                </td>
  <!--                <td style="vertical-align: top; width: 163px;"><a href="#">More...</a><br>
                </td>
                <td style="vertical-align: top; width: 175px; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                <td style="vertical-align: top; width: 170px; text-align: right;">90%<br>
                </td>
                <td style="vertical-align: top; width: 60px; text-align: center;"><a href="#">Default...</a><br>
                </td> -->
                <td style="text-align: right;">
                    <a href="#" onClick="deployCompositeService('Monitor Temperature in Colombes France', 'pre3');return false;"><span id="DeployCommandTextDiv_3"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                </td>
            </tr>
            <tr>
                <%--<td style="vertical-align: top; text-align: center;"><input name="selectRow" type="checkbox" /></td>
                --%><td style="vertical-align: top;">Fire Early Warning in CTI offices<br>
                </td>
   <!--               <td style="vertical-align: top;"><a href="#">More...</a></td>
                <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                <td style="vertical-align: top; text-align: right;">95%<br>
                </td>
                <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
                </td> -->
                <td style="text-align: right;">
                    <a href="#" onClick="deployCompositeService('Fire Early Warning in CTI offices','pre1');return false;"><span id="DeployCommandTextDiv_1"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                </td>
            </tr>
            <tr>
                <%--<td style="vertical-align: top; text-align: center;"><input name="selectRow" type="checkbox" /></td>
                --%><td style="vertical-align: top;">Fire Early Warning in Thales building<br>
                </td>
 <!--                 <td style="vertical-align: top;"><a href="#">More...</a></td>
                <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                <td style="vertical-align: top; text-align: right;">95%<br>
                </td>
                <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
                </td> -->
                <td style="text-align: right;">
                    <a href="#" onClick="deployCompositeService('Fire Early Warning in Thales building','pre4');return false;"><span id="DeployCommandTextDiv_4"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                </td>
            </tr>
            <tr style="display: none;">
                <%--<td style="vertical-align: top; text-align: center;"><input name="selectRow" type="checkbox"></td>
                --%><td style="vertical-align: top;">Monitor temperature from exactly 3 sensors<br>
                </td>
  <!--                <td style="vertical-align: top;"><a href="#">More...</a></td>
                <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                <td style="vertical-align: top; text-align: right;">85%<br>
                </td>
                <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
                </td> -->
                <td style="text-align: right;">
                    <a href="#" onClick="deployCompositeService('Monitor temperature from exactly 3 sensors','pre2');return false;"><span id="DeployCommandTextDiv_2"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                </td>
            </tr>
            <c:forEach items="${composedServiceList}"  var="uiComposedService">
                <tr>
                    <%--<td style="vertical-align: top; width: 58px; text-align: center;"><input name="selectRow" type="checkbox" value="${uiServiceInstance.serviceInstance.id}"><br>
                    </td>--%>
                    <td style="vertical-align: top; ">${uiComposedService.getComposedService().name}<br></td>
 <!--                     <td style="vertical-align: top; "><a href="#">More...</a></td>
                    <td style="vertical-align: top; text-align: right;"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="o" src="<%=request.getContextPath()%>/img/popularstar.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"><img style="width: 16px; height: 16px;" alt="-" src="<%=request.getContextPath()%>/img/popularstarblank.png"></td>
                    <td style="vertical-align: top; text-align: right;">90%<br>
                    </td>
                        <td style="vertical-align: top; text-align: center;"><a href="#">Custom...</a><br>
                        </td> -->
                        <td style="text-align: right;">
                            <a href="#" onClick="deployCompositeService('${uiComposedService.getComposedService().name}','${uiComposedService.getComposedService().id}');return false;"><span id="DeployCommandTextDivNew_${uiComposedService.getComposedService().id}"><img title="Deploy service" src="<%=request.getContextPath()%>/img/deployVSN52h.png" style="height: 32px;width: 32px;" /></span></a>
                        </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <!-- <a href="#">Previous page</a>&nbsp; <a href="#">Next page</a> -->

        <!-- <input value="Negotiate Selected" name="StartNegotiate" type="button">&nbsp; <input value="Clear Selection" name="clearselectionbtn" type="button">&nbsp; <input value="Show VITRO sample services" name="loadsampleservicesbtn" type="button" style="display: none;"> -->
    </form>
</td></tr></tbody></table>

                    </div>
                    </div>
                    </div>
<!-- begin the footer for the application -->
<%= Common.printFooter(request, application) %>
<!-- end of footer -->
</body>
</html>
