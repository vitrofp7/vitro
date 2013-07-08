<%@ page session='false' contentType="text/html;charset=UTF-8" language="java"  %>
<%@page import='java.util.*, presentation.webgui.vitroappservlet.Common' %>

<%--
  ~ #--------------------------------------------------------------------------
  ~ # Copyright (c) 2013 VITRO FP7 Consortium.
  ~ # All rights reserved. This program and the accompanying materials
  ~ # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
  ~ # http://www.gnu.org/licenses/lgpl-3.0.html
  ~ #
  ~ # Contributors:
  ~ #     Antoniou Thanasis (Research Academic Computer Technology Institute)
  ~ #     Paolo Medagliani (Thales Communications & Security)
  ~ #     D. Davide Lamanna (WLAB SRL)
  ~ #     Alessandro Leoni (WLAB SRL)
  ~ #     Francesco Ficarola (WLAB SRL)
  ~ #     Stefano Puglia (WLAB SRL)
  ~ #     Panos Trakadas (Technological Educational Institute of Chalkida)
  ~ #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
  ~ #     Andrea Kropp (Selex ES)
  ~ #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
  ~ #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
  ~ #
  ~ #--------------------------------------------------------------------------
  --%>

<html>
<head>
    <meta charset="utf-8">	
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/img/favicon2.ico" type="image/x-icon"/>

    <title>Home</title>
	<link href="<%=request.getContextPath()%>/css/bootstrap.css" rel="stylesheet">
	<link href="<%=request.getContextPath()%>/css/vitrodemo.css" rel="stylesheet">

    <script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/bootstrap.js"></script>
	
    <link rel="shortcut icon" href="<%=request.getContextPath()%>/ico/favicon.png">
	<script type="text/javascript">
	$(document).ready(function(){
		$('#dashboardHomeButton').addClass("active");
		$('#myCarousel').carousel({
          interval: 10000
        });
 	});     	
	</script>
</head>
<body>    
    <!-- DDMenu -->
    <%= Common.printDDMenu(application.getRealPath("/"), request) %>
			<%= Common.printDDBody(application.getRealPath("/"), request) %>
	<div class="container" style="height:500px; width:900px;">
		<div class="row-fluid" align="center">
		<div class="well" id="carousel">
			<!-- begin the DDBody -->

			<div class="carousel slide" id="myCarousel">
                <ol class="carousel-indicators">
                  <li class="active" data-slide-to="0" data-target="#myCarousel"></li>
                  <li data-slide-to="1" data-target="#myCarousel" class=""></li>
                  <li data-slide-to="2" data-target="#myCarousel" class=""></li>
                </ol>
                <div class="carousel-inner">
                  <div class="item active">
                    <img alt="" src="img/image1.jpg">
                    <div class="carousel-caption">
                      <h4>Explore the world</h4>
                      <p>Access real-world knowledge via the VITRO platform in a user-friendly manner. Personalize composite services ranging from traffic, product and environmental monitoring over early warning systems and smart energy metering.</p>
                    </div>
                  </div>
                  <div class="item">
                    <img alt="" src="img/image2.jpg" align="middle">
                    <div class="carousel-caption">
                      <h4>Be part of it</h4>
                      <p>VITRO is an open and standards-based platform providing APIs for the development of custom IoT applications as well as the easy installation of each and every commercial hardware platform and software module, hiding heterogeneity from the end-user.</p>
                    </div>
                  </div>
                  <div class="item">
                    <img alt="" src="img/image3.jpg" align="middle">
                    <div class="carousel-caption">
                      <h4>A wealth of novel services</h4>
                      <p>Advanced capabilities in sensor and system level thanks to the technological enhancements leading to the virtualization of smart devices' resources and services.</p>
                    </div>
                  </div>
                </div>
                <a data-slide="prev" href="#myCarousel" class="left carousel-control">‹</a>
                <a data-slide="next" href="#myCarousel" class="right carousel-control">›</a>
              </div>
		</div>
			<!-- begin the footer for the application -->
		</div>
	</div>



    <%= Common.printFooter(request, application) %>
    <!-- end of footer -->
</body>
</html>