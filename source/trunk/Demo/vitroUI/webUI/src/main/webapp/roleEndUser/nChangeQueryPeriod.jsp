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

<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>
<%
        Logger logger = Logger.getLogger(this.getClass());
        String xmerrordescr="";
        int errno = 0;
        String responseResult = "";
        String pvsnId = "";
        String newPeriodStr = "";
        String pAction = "";
        String reqParamNamesStr ="";
        String reqData = "";
        String reqParamValuesStr = "";
        Enumeration<String> reqDataNames;

        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
        try {
            reqData = request.getQueryString();
            reqDataNames = request.getParameterNames();

            pvsnId = request.getParameter("vsnId");
            newPeriodStr =  request.getParameter("newPeriod");
            pAction =  request.getParameter("psaction");
        }catch(Exception ex) {
            errno = 1;
            xmerrordescr="Error in post data format!";
            reqDataNames = null;

            pvsnId = "";
            newPeriodStr =  "";
            pAction =  "";
        }
        if(reqDataNames !=null )
        {
            while (reqDataNames.hasMoreElements()) {
                String parName =  reqDataNames.nextElement();
                // process element
                reqParamNamesStr += parName + ",";

            }
            //remove trailing comma
            reqParamNamesStr = reqParamNamesStr.replaceAll("\\s*,\\s*$", "");
        }
    if(pAction!=null && pAction.compareToIgnoreCase("changeSamplePeriod") == 0) {
           //get Query from Scheduler and change the defined period
        StringBuilder reqParamValsStrBld = new StringBuilder();
        reqParamValsStrBld.append(pvsnId);
        reqParamValsStrBld.append(",");
        reqParamValsStrBld.append(newPeriodStr);
        reqParamValsStrBld.append(",");
        reqParamValsStrBld.append(pAction);
        reqParamValuesStr=reqParamValsStrBld.toString();

        logger.debug("PARAMS: " + reqParamValuesStr);
        //logger.debug("two");
        StringBuilder resStrBld = new StringBuilder();
        if(pvsnId!=null) {
            if(newPeriodStr!=null) {
                Integer newPeriodInt = Capability.defaultSamplingPeriod;
                try {
                    newPeriodInt = Integer.parseInt(newPeriodStr);
                }catch(Exception ex) {
                    logger.error(ex.getMessage());
                    newPeriodInt =   Capability.defaultSamplingPeriod;
                }
                if(newPeriodInt < Capability.minSamplingPeriod && newPeriodInt!=0) {
                    newPeriodInt = Capability.minSamplingPeriod;
                }
                IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(pvsnId).setDesiredPeriod(newPeriodInt);
            }
            try {
                if(IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(pvsnId)!=null) {
                    int resultedPeriod = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(pvsnId).getDesiredPeriod();
                    if(resultedPeriod == QueryDefinition.noPeriodicSubmission){
                        resultedPeriod = 0;
                    }
                    resStrBld.append(resultedPeriod);
                }   else {
                    resStrBld.append(0);
                    errno = -1;
                    xmerrordescr = "The service is not deployed. Cannot adjust sampling period!";
                }
            }catch (Exception ex1) {
                resStrBld.append(0); // probably the Query is not deployed!
                errno = -1;
                xmerrordescr = "The service is not deployed. Could not adjust sampling period!";
                logger.error("AJAX - The service is not deployed. Cannot adjust sampling period");
            }
        }
        responseResult = resStrBld.toString();
    }
%><Answer>
<error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
<reply value="<%=responseResult %>" vsnId="<%=pvsnId %>" parNames="<%=reqParamNamesStr %>" parValues="<%=reqParamValuesStr %>" qstr="<%=reqData %>" ></reply>
</Answer>