<%@ page import="vitro.vspEngine.service.query.IndexOfQueries" %>
<%@ page import="vitro.vspEngine.service.query.QueryDefinition" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
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

<%

    String xmerrordescr="";
    int errno = 0;

    String serviceId = "-1";
    serviceId = request.getParameter("pid");

    int PeriodSlctd;
    int HistNumSlctd;
    boolean AggrSlctd;
    // get Composed Service from DB (is such exists with this service ID

    // attempt to create a new query definition and deploy it based on the data stored in the DB
    UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));
    if(ssUN != null)
    {
        int intServiceId = -1;
        try {
            intServiceId = Integer.parseInt(serviceId);
        }
        catch (Exception ex003)
        {
            intServiceId = -1;
        }
        IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
        QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDef(ssUN, intServiceId);
        if(qdef == null)
        {
            xmerrordescr ="Unable to create new Query!";
            errno = 1;
        }
        else
        {
            PeriodSlctd = Capability.defaultSamplingPeriod;
            HistNumSlctd = 0;
            AggrSlctd = true;
            //qdef.setFriendlyName(friendName);
            qdef.setDesiredPeriod(PeriodSlctd);
            qdef.setDesiredHistory(HistNumSlctd);
            qdef.setAggregateQueryFlag(AggrSlctd);
            errno = 0;
        }
    }
    else {
        xmerrordescr ="No valid VSP object found!";
        errno = 1;
    }


%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <QueryDefId></QueryDefId>
    <KMLJSPFileUrl></KMLJSPFileUrl>
</Answer>