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
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*,  vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="vitro.vspEngine.service.engine.UserNode" %>
<%
        
	String xmerrordescr="";
        int errno = 0;
        
        // form field parameters
        String[] allAvailGateways;
        String[] guiSelectedGateways;
        String[] guiSelectedSmartDevs;
        String[] guiIndxGatewayToSelectedSmartDevs;
        String[] guiCapsSelected;
        String[] guiFunctsSelected;
        int PeriodSlctd;
        int HistNumSlctd;
        boolean AggrSlctd;
        String actuationValueSetStr;
        
        allAvailGateways = request.getParameterValues("allgw[]");
        guiSelectedGateways = request.getParameterValues("selgw[]");
        guiSelectedSmartDevs = request.getParameterValues("smdvs[]");
        guiIndxGatewayToSelectedSmartDevs = request.getParameterValues("idxGwtoSD[]");
        guiCapsSelected = request.getParameterValues("gcaps[]");
        guiFunctsSelected = request.getParameterValues("functs[]");
        PeriodSlctd = Integer.parseInt(request.getParameter("period"));
        HistNumSlctd = Integer.parseInt(request.getParameter("hist"));
        AggrSlctd = request.getParameter("aggr").equals("0")? false: true;
        actuationValueSetStr = request.getParameter("act");
        
	String psaction = request.getParameter("psaction");
        
	if( psaction.equals("newQuery") )
	{
            errno = 0;
            // will do stuff here
            // Insert new Query definition in the IndexOfQueries
            //
        UserNode ssUN = (UserNode)(application.getAttribute("ssUN"));

            // get location choice mode. This will change later. For the verbose interface this
            // is always QueryContentDefinition.selRegionAnalysis
            //int locChoice = QueryContentDefinition.selRegionAnalysis;
            
            // here since we don't care for range coordinates in the verbose interface we don't put anything in the vector
            Vector<GeoRegion> areasSelectedVec = new Vector<GeoRegion>();
            
            // create the gateIdToSelectedMotesVecHM
            HashMap<String, Vector<String>>  gateIdToSelectedMotesVecHM = new HashMap<String, Vector<String>>(); // to store selected gateways and the selected motes per gateway. A vector with a single mote entry of QueryProcessor.selAllMotes for a gateway key means "ALL motes in gateway"
            for(int i=0; guiSelectedGateways!=null && i < guiSelectedGateways.length; i++)
            {
                String gateId = allAvailGateways[Integer.parseInt(guiSelectedGateways[i])];
                Vector<String> selectedMoteVec = new Vector<String>();
                selectedMoteVec.add(QueryContentDefinition.selAllMotes);
                gateIdToSelectedMotesVecHM.put(gateId, selectedMoteVec);
            }
            // since the gateway selection covers ALL motes inside a gateway, we now check for
            // and add only those smartdevices that don't belong in explicitly (via the GUI) selected entire gateways.
            //            
            for(int i=0; guiSelectedSmartDevs!=null && i < guiSelectedSmartDevs.length; i++)
            {
                boolean foundmatch = false;
                for(int j =0; guiSelectedGateways!=null && j < guiSelectedGateways.length; j++)
                {    
                    if(guiIndxGatewayToSelectedSmartDevs[i].equals(guiSelectedGateways[j])  )
                    {
                        foundmatch = true;
                        break;
                    }    
                }
                if(!foundmatch)
                {
                    String gateId = allAvailGateways[Integer.parseInt(guiIndxGatewayToSelectedSmartDevs[i])];
                    if(gateIdToSelectedMotesVecHM.get(gateId) == null)
                    {
                        Vector<String> tmpSelectedMoteVec = new Vector<String>();
                        tmpSelectedMoteVec.add(guiSelectedSmartDevs[i]);
                        gateIdToSelectedMotesVecHM.put(gateId, tmpSelectedMoteVec);
                    }
                    else
                    {
                        Vector<String> originalSelectedMoteVec = gateIdToSelectedMotesVecHM.get(gateId);
                        originalSelectedMoteVec.add(guiSelectedSmartDevs[i]);
                        // the following is probably redundant
                        // (let's test without it and then if problem's occur we'll test with it).
                        //gateIdToSelectedMotesVecHM.put(gateId, originalSelectedMoteVec);
                    }
                }
            }
            if(gateIdToSelectedMotesVecHM.isEmpty()) // this normally should never happen (due to GUI javascript checks)
            {
                String gateId = QueryContentDefinition.selUndefined;
                Vector<String> selectedMoteVec = new Vector<String>();
                selectedMoteVec.add(QueryContentDefinition.selUndefined);
                gateIdToSelectedMotesVecHM.put(gateId, selectedMoteVec);
            }     
            
            // create the genCapQuerriedToSelectedfuncVecHM
            HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedfuncVecHM = new  HashMap<String, Vector<ReqFunctionOverData>>(); //to store the selected generic capabilities for query and the associated functions per capability
            HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedGwLevelfuncVecHM  = new  HashMap<String, Vector<ReqFunctionOverData>>(); // similarly for gateway level functions
            // for the verbose interface ALL selected functions will be applied to ALL selected capabilities.
            // first build the vector of selected functions. (no ids are set at this point, also functions here don;t have to be unique. Both of these steps will be done by backend ).
            Vector<ReqFunctionOverData> funcVec = new Vector<ReqFunctionOverData>();
            Vector<ReqFunctionOverData> gwLevelFunctVector = new Vector<ReqFunctionOverData>();
            boolean haveConsideredALatestValFunctionOnce = false;
            for (int i = 0; guiFunctsSelected!=null && i < guiFunctsSelected.length; i++) 
            {
                String function = guiFunctsSelected[i];
                if(function.equalsIgnoreCase(ReqFunctionOverData.setValFunc) && !actuationValueSetStr.isEmpty() &&!actuationValueSetStr.equals(""))
                {
                    HashMap<String, String> threshHM = new HashMap<String, String>();
                    threshHM.put( ThresholdStructure.THRESHOLD_EQUAL, actuationValueSetStr);
                    ThresholdStructure threshTmp = new ThresholdStructure(threshHM);
                    funcVec.addElement(new ReqFunctionOverData(function, ReqFunctionOverData.unknownFuncId, null, threshTmp ));
                    //funcVec.addElement(new ReqFunctionOverData(function, ReqFunctionOverData.unknownFuncId, null, null ));
                }
                else
                {

                    //todo: early gwlevel aggregate function handling: check for min, max or avg. It should be applied to specific node-level function ids, but at this point we are ...
                    //todo: .. applying to the first node-level function (and all of those are treated as latest values).
                    // todo: a single function should decide if a function is eligible for gwlevel function (instead of checking caes here one by one)  e.g. isGWLevelFunction()
                    if(function.equalsIgnoreCase(ReqFunctionOverData.avgFunc) || function.equalsIgnoreCase(ReqFunctionOverData.maxFunc) || function.equalsIgnoreCase(ReqFunctionOverData.minFunc))
                    {
                        gwLevelFunctVector.addElement(new ReqFunctionOverData(ReqFunctionOverData.GW_LEVEL_PREFIX+ReqFunctionOverData.GW_LEVEL_SEPARATOR+function+ReqFunctionOverData.GW_LEVEL_SEPARATOR+ReqFunctionOverData.unknownFuncId,ReqFunctionOverData.unknownFuncId , null,null));
                        //bring also the latest values to confirm...
                        if(!haveConsideredALatestValFunctionOnce)
                        {
                            funcVec.addElement(new ReqFunctionOverData(ReqFunctionOverData.lastValFunc, ReqFunctionOverData.unknownFuncId, null, null ));
                            haveConsideredALatestValFunctionOnce = true;
                        }
                    }
                    else if(function.equalsIgnoreCase(ReqFunctionOverData.lastValFunc))//actually now that we handle the gateway level function, then just treat any other selection of function in the simple menu as "bring the latest values"
                    {   // an in that regard merge them all in one function
                        if(!haveConsideredALatestValFunctionOnce)
                        {
                            funcVec.addElement(new ReqFunctionOverData(ReqFunctionOverData.lastValFunc, ReqFunctionOverData.unknownFuncId, null, null ));
                            haveConsideredALatestValFunctionOnce = true;
                        }
                    }
                }

            }
            if(funcVec.isEmpty())
            {
                funcVec.addElement(new ReqFunctionOverData(QueryContentDefinition.selUndefined, ReqFunctionOverData.unknownFuncId, null, null ));
            }
            // then create the hashmap
            for (int i = 0; guiCapsSelected!=null && i < guiCapsSelected.length; i++) 
            {
                genCapQuerriedToSelectedfuncVecHM.put(guiCapsSelected[i], funcVec);
                genCapQuerriedToSelectedGwLevelfuncVecHM.put(guiCapsSelected[i], gwLevelFunctVector);
            }
            
            IndexOfQueries IndexOfQueryDefs = IndexOfQueries.getIndexOfQueries();
            QueryDefinition qdef = IndexOfQueryDefs.addNewQueryDefPreDep("", ssUN, areasSelectedVec,
                                                                    gateIdToSelectedMotesVecHM,
                                                                    genCapQuerriedToSelectedfuncVecHM, genCapQuerriedToSelectedGwLevelfuncVecHM);
            if(qdef == null)
            {
                xmerrordescr ="Unable to create new Query!";
                errno = 1; 
            }
            else
            {      
                if(PeriodSlctd <= 0)
                {
                    PeriodSlctd = QueryDefinition.noPeriodicSubmission;
                }
                qdef.setDesiredPeriod(PeriodSlctd);
                qdef.setDesiredHistory(HistNumSlctd);
                qdef.setAggregateQueryFlag(AggrSlctd);
            
            //
            // (To do) (Let the scheduler (in the servlet for now) deal with its issuing in the network)
            // (To do) If the scheduler works well, it may be integrated in the API (and removed from the GUI).
            //         with the necessary modifications. For legacy compatibility the
            //          scheduler (singleton, and probably a static member of IndexOfQueries) should be optional (activated on demand from the GUI)
            //
            // (To do) Return a link to the XML of the Query Definition (only the Uqid is needed to be sent as Get String to a .jsp)
            // 
            // (To do) Dynamic JSP-KML creation according to the selections in the GUI
            // Return a link to the JSP file (dummy for the time being)
            // 
            // (To do) Create an "edit" link to edit query parameters like Period, History and Aggregated mode (The definition
            // cannot change. (if the user wants to change the definition he should re-issue a new Query -for the time being)
            //
            // (To do) At a later phase, perhaps the edit could affect the query definition by means of actually inserting a new (Additional) query
            // and give the user the choise to keep or discard the original.
            // 
            //   
            }
            
        }
        
        if(errno == 0)
        {
            xmerrordescr = "OK";
        }
      
%>
<Answer>
    <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>"></error>
    <QueryDefId></QueryDefId>
    <KMLJSPFileUrl></KMLJSPFileUrl>    
</Answer>

