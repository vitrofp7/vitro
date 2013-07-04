<?xml version="1.0" encoding="UTF-8" standalone="yes" ?>
<%@page session='false' contentType="text/xml" import='java.util.*, presentation.webgui.vitroappservlet.*, vitro.vspEngine.service.query.*, vitro.vspEngine.service.geo.*' %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.FullComposedService" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="java.util.regex.Pattern" %>
<%@ page import="vitro.vspEngine.service.common.abstractservice.model.Capability" %>
<%
    Logger logger = Logger.getLogger(getClass());
    boolean stopBatch = false;
    boolean removeBatch = false;
    int numOfInvolvedVSNs = 0;
    ArrayList<String> qidList = new ArrayList<String>();
        //form field parameters for Batch VSN action
    String cmpMode = request.getParameter("cmp");
    if(cmpMode == null) {
        cmpMode = "0";
    }
    String psaction = request.getParameter("psaction");
    String[] allAvailVSNs;
    String[] guiSelectedVSNs;
    if( psaction.compareToIgnoreCase("singleStartStop")==0
            || psaction.compareToIgnoreCase("singleRemove")==0 )
    {
        numOfInvolvedVSNs = 1;
        String quid = "";
        quid = request.getParameter("quid");
        qidList.add(quid);
    }
    else if(psaction.compareToIgnoreCase("stopBatch")==0
            || psaction.compareToIgnoreCase("startBatch")==0
            || psaction.compareToIgnoreCase("removeBatch")==0)
    {
        allAvailVSNs = request.getParameterValues("allvsn[]");
        guiSelectedVSNs = request.getParameterValues("selvsn[]");
        numOfInvolvedVSNs = guiSelectedVSNs.length;

        for(int i= 0; guiSelectedVSNs!=null && i < guiSelectedVSNs.length; i++)
        {
            String quid = allAvailVSNs[Integer.parseInt(guiSelectedVSNs[i])];
            qidList.add(quid);
        }
        if(psaction.compareToIgnoreCase("stopBatch")==0)
        {
            stopBatch=true;
        }
        else if(psaction.compareToIgnoreCase("startBatch")==0)
        {
            stopBatch=false;
        }
        else if(psaction.compareToIgnoreCase("removeBatch") == 0)
        {
            removeBatch=true;
        }
    }


%>
<Answer>
    <vsnList>
        <%
            for(int i= 0; i< qidList.size(); i++)
            {
                QueryDefinition currQueryDef = null;
                String xmerrordescr="";
                int errno = 0;
                // form field parameters for SINGLE VSN action

                int currAssumedStatus = QueryDefinition.STATUS_RUNNING;     //has meaning only when we have a "singleStartStop" query stop/start action
                int currActualStatus = QueryDefinition.STATUS_RUNNING;
                int finalStatus = QueryDefinition.STATUS_RUNNING;

                if(psaction.equals("singleStartStop"))
                {
                    currAssumedStatus = Integer.parseInt(request.getParameter("currassumedstatus"));
                }

                String quid = qidList.get(i);

                if(quid!=null && !quid.trim().equals(""))
                {
                    IndexOfQueries myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
                    currQueryDef = myIndexOfQueries.getQueryDefinitionById(quid);
                    if(currQueryDef  != null)
                    {
                        currActualStatus = currQueryDef.getRunningStatus();
                        if(psaction.equals("singleStartStop")  && (currActualStatus != currAssumedStatus))
                        { // someone has changed the status from another user-session
                            finalStatus = currActualStatus;
                            errno = 0;
                            xmerrordescr = "VSN status was already changed!";
                        }
                        else if(psaction.equals("singleStartStop") || (!removeBatch))
                        {
                            if(psaction.equals("singleStartStop"))//do a switch
                            {
                                if(currActualStatus == QueryDefinition.STATUS_RUNNING)
                                    currQueryDef.setRunningStatus(QueryDefinition.STATUS_PAUSED);
                                else if (currActualStatus == QueryDefinition.STATUS_PAUSED)
                                    currQueryDef.setRunningStatus(QueryDefinition.STATUS_RUNNING);
                            }
                            else if(stopBatch &&!removeBatch && currActualStatus == QueryDefinition.STATUS_RUNNING) //stop it
                            {
                                currQueryDef.setRunningStatus(QueryDefinition.STATUS_PAUSED);
                            }
                            else if(!stopBatch &&!removeBatch && currActualStatus == QueryDefinition.STATUS_PAUSED) // start it
                            {
                                currQueryDef.setRunningStatus(QueryDefinition.STATUS_RUNNING);
                            }
                            finalStatus = currQueryDef.getRunningStatus();
                            errno = 0;
                            xmerrordescr = "OK";
                        }
                        else //case: remove
                        {
                            if (psaction.equals("singleRemove") || removeBatch) //remove the VSN
                            {
                                if(IndexOfQueries.getIndexOfQueries().removeQueryDef(quid))
                                {
                                    finalStatus = QueryDefinition.STATUS_PAUSED;
                                    errno = 0;
                                    xmerrordescr = "REMOVED";
                                }
                                else
                                {
                                    finalStatus = currQueryDef.getRunningStatus();
                                    errno = 4;
                                    xmerrordescr = "Could not remove a specified VSN: "+quid;
                                }
                                if (errno == 0 && cmpMode.compareToIgnoreCase("1") == 0) {
                                    int composedServiceId = -1;
                                    String currentqeuryDefNoPrefix =  quid.replaceAll(Pattern.quote(IndexOfQueries.COMPOSED_DB_PREFIX), "");
                                    currentqeuryDefNoPrefix =  currentqeuryDefNoPrefix.replaceAll(Pattern.quote(IndexOfQueries.PREDEPLOYED_PREFIX), "");

                                    try {
                                        composedServiceId = Integer.parseInt(currentqeuryDefNoPrefix);
                                    }catch (Exception e22) {
                                        composedServiceId = -1;
                                    }
                                    if(composedServiceId!= -1) {
                                        logger.debug(" AJAX - Removing composed service "+composedServiceId);

                                        AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
                                        FullComposedService composedService = manager.getComposedService(composedServiceId);


                                        AbstractComposedServiceManager.getInstance().removeComposedService(composedServiceId);


                                        logger.debug(" AJAX -  Removed Service");
                                    } else
                                    {
                                        logger.debug(" AJAX -- Could not remove composite service ");
                                        errno = 4;
                                        xmerrordescr = "Could not remove composite service: "+quid;
                                    }
                                }
                            }
                        }
                    }
                    else
                    {// the service is undeployed but if we are in the remove composite service, we can remove it from here.
                       if ( (psaction.equals("singleRemove") || removeBatch) &&  cmpMode.compareToIgnoreCase("1") == 0)//remove the VSN
                        {
                            int composedServiceId = -1;
                            String currentqeuryDefNoPrefix =  quid.replaceAll(Pattern.quote(IndexOfQueries.COMPOSED_DB_PREFIX), "");
                            currentqeuryDefNoPrefix =  currentqeuryDefNoPrefix.replaceAll(Pattern.quote(IndexOfQueries.PREDEPLOYED_PREFIX), "");

                            try {
                                composedServiceId = Integer.parseInt(currentqeuryDefNoPrefix);
                            }catch (Exception e22) {
                                composedServiceId = -1;
                            }
                            if(composedServiceId!= -1) {
                                logger.debug(" AJAX - Removing composed service "+composedServiceId);

                                AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
                                FullComposedService composedService = manager.getComposedService(composedServiceId);

                                AbstractComposedServiceManager.getInstance().removeComposedService(composedServiceId);

                                logger.debug(" AJAX -  Removed Service");
                                errno = 0;
                                xmerrordescr = "REMOVED";
                            } else
                            {
                                logger.debug(" AJAX -- Could not remove composite service ");
                                errno = 4;
                                xmerrordescr = "Could not remove composite service: "+quid;
                            }
                        }else {
                            errno = 1;
                            xmerrordescr = "No VSN with id " + quid +" exists in the Index!";
                        }
                    }
                }
                else
                {
                    errno = 2;
                    xmerrordescr = "No VSN was specified!";
                }

        %>
        <vsnEntry>
            <quid><%=qidList.get(i) %></quid>
            <newQueryStatus><%=Integer.toString(finalStatus) %></newQueryStatus>
            <error errno="<%=Integer.toString(errno) %>" errdesc="<%=xmerrordescr %>" />
        </vsnEntry>
        <%
            }
        %>
    </vsnList>
</Answer>

