/*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */
package alter.vitro.vgw.service;

import alter.vitro.vgw.wsiadapter.InfoOnTrustRouting;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 */
public class TrustRoutingQueryService {

    public static final int DEFAULT_PERIOD = 1*60; // in seconds  // TODO: change to two minutes or more
    public static final int NO_PERIOD = -1; // in seconds  // for one shot try

    public static final int DEFAULT_PERIOD_FOR_INTERMEDIATE_COAP_MSGs = 8; //in seconds

    private static int periodBetweenCoapMsgToAnotherNode = DEFAULT_PERIOD_FOR_INTERMEDIATE_COAP_MSGs;

    private static int periodQueryInterval = DEFAULT_PERIOD;
    private HashMap<String, InfoOnTrustRouting> cachedDirectoryOfTrustRoutingInfo;
    private boolean runFlag = true;
    private QueryTrustRoutingThread qst = null;


    private static TrustRoutingQueryService instance = null;

    private TrustRoutingQueryService() {
        setCachedDirectoryOfTrustRoutingInfo(new  HashMap<String, InfoOnTrustRouting>());
    }

    public static TrustRoutingQueryService getInstance() {
        if(instance == null) {
            instance = new TrustRoutingQueryService();
        }
        return instance;
    }

    public static int getPeriodQueryInterval() {
        return periodQueryInterval;
    }

    public static void setPeriodQueryInterval(int periodQueryInterval) {
        TrustRoutingQueryService.periodQueryInterval = periodQueryInterval;
    }

    public static int getPeriodBetweenCoapMsgToAnotherNode() {
        return periodBetweenCoapMsgToAnotherNode;
    }

    public static void setPeriodBetweenCoapMsgToAnotherNode(int periodBetweenCoapMsgToAnotherNode) {
        TrustRoutingQueryService.periodBetweenCoapMsgToAnotherNode = periodBetweenCoapMsgToAnotherNode;
    }

    /**
     * initialize a Thread that will periodically query the nodes of the gateway for trust routing info
     */
    public void startScheduler() {
        runFlag = true;
        if(qst == null) {                   // new condition
            qst = new QueryTrustRoutingThread();
        }
    }

    public void stopScheduler() // this is very graceful, and allows all threads to finish up themselves
    {
        runFlag = false;
    }

    synchronized public boolean isQueryActivelyRunning(/*String quid*/)
    {
        boolean retVal = false;
        if(qst!=null && runFlag==true)
        {
            QueryTRThreadInfo tmpQueryInfo = qst.dealWithRunningQueriesHM(QueryTrustRoutingThread.FIND_IN_HM);
            if ((tmpQueryInfo == null) ||
                    (tmpQueryInfo != null && tmpQueryInfo.getStatus() == QueryTRThreadInfo.STATUS_STARTED))
            {
                retVal = true;
            }
            else if((tmpQueryInfo == null) ||
                    (tmpQueryInfo != null && tmpQueryInfo.getStatus() == QueryTRThreadInfo.STATUS_ENDED))
            {
                retVal = false;
            }
        }
        return retVal;
    }

    public HashMap<String, InfoOnTrustRouting> getCachedDirectoryOfTrustRoutingInfo() {
        return cachedDirectoryOfTrustRoutingInfo;
    }

    public void setCachedDirectoryOfTrustRoutingInfo(HashMap<String, InfoOnTrustRouting> cachedDirectoryOfTrustRoutingInfo) {
        this.cachedDirectoryOfTrustRoutingInfo = cachedDirectoryOfTrustRoutingInfo;
    }


    /**
     * This is the main Scheduler-for-Queries Thread.
     * Follows a Round Robin policy.
     */
    private class QueryTrustRoutingThread extends Thread {

        private HashMap<String, QueryTRThreadInfo> regdRunQueries;
        private final int UPDATE_FINISH_HM = 1;
        private final int UPDATE_START_HM = 2;
        public static final int FIND_IN_HM = 3;
        public static final String UNIQUE_QUERY_ID_FOR_TRUST_ROUTING= "1";


        QueryTrustRoutingThread() {
            // associates a unique query id with a QueryTRThreadInfo object
            // effectively we only need one so the unique Query ID is UNIQUE_QUERY_ID_FOR_TRUST_ROUTING

            regdRunQueries = new HashMap<String, QueryTRThreadInfo>();

            start();
        }

        synchronized public QueryTRThreadInfo dealWithRunningQueriesHM(int readWriteFlag/*, String quid*/) {
            // effectively we only need one so the unique Query ID is UNIQUE_QUERY_ID_FOR_TRUST_ROUTING
            String quid = UNIQUE_QUERY_ID_FOR_TRUST_ROUTING ;
            if (readWriteFlag == FIND_IN_HM) // we read to find a specific quid
            {
                if (regdRunQueries.containsKey(quid)) {
                    return regdRunQueries.get(quid);
                }
                return null;
            } else if (readWriteFlag == UPDATE_START_HM) {
                QueryTRThreadInfo tmpQueryTRThreadInfo = null;
                if (regdRunQueries.containsKey(quid)) {
                    tmpQueryTRThreadInfo = regdRunQueries.get(quid);
                } else {
                    tmpQueryTRThreadInfo = new QueryTRThreadInfo();
                    tmpQueryTRThreadInfo = regdRunQueries.put(quid, tmpQueryTRThreadInfo);
                    tmpQueryTRThreadInfo = regdRunQueries.get(quid);
                }
                GregorianCalendar calendar = new GregorianCalendar();
                long currentTimeInMillis = calendar.getTimeInMillis();
                tmpQueryTRThreadInfo.setStatus(QueryTRThreadInfo.STATUS_STARTED);
                tmpQueryTRThreadInfo.setTimeStarted(new java.sql.Timestamp(currentTimeInMillis));
            } else if (readWriteFlag == UPDATE_FINISH_HM) {
                QueryTRThreadInfo tmpQueryTRThreadInfo = null;
                if (regdRunQueries.containsKey(quid)) {
                    tmpQueryTRThreadInfo = regdRunQueries.get(quid);
                } else {
                    tmpQueryTRThreadInfo = new QueryTRThreadInfo();
                    tmpQueryTRThreadInfo = regdRunQueries.put(quid, tmpQueryTRThreadInfo);
                    tmpQueryTRThreadInfo = regdRunQueries.get(quid);
                }
                tmpQueryTRThreadInfo.setStatus(QueryTRThreadInfo.STATUS_ENDED);
            }
            return null;
        }


        public void run() {
            int nextQueryToCheck = 0;
            while (runFlag) {
                //
                // Check with Round Robin each and every entry (only one in this case)
                int sizeOfQueuedQueries = 1;
                if (nextQueryToCheck >= sizeOfQueuedQueries)
                    nextQueryToCheck = 0; // reset nextQueryToCheck to provide round robin
                for (int counter = 0; counter < sizeOfQueuedQueries; counter++) {
                    if (counter == nextQueryToCheck) {
                        //final String tmpqDefuId = st0.elementAt(counter);
                        // Main check:
                        // if the query is not found in the stillRunningQueries HM,
                        // OR it is found AND has ended AND its period has passed
                        // then --> launch it
                        GregorianCalendar calendar = new GregorianCalendar();
                        long currentTimeInMillis = calendar.getTimeInMillis();
                        //todo: shouldn't this also be using a copy constructor as above?
                        //final QueryDefinition tmpDef = myIndexOfQueries.getAllQueriesDefHashMap().get(tmpqDefuId);
                        if(VitroGatewayService.getVitroGatewayService() != null) //new
                        {
                            long queryDefPeriodSet = TrustRoutingQueryService.getPeriodQueryInterval();
                            long queryDefPeriodInMillis = queryDefPeriodSet * 1000;
                            boolean queryDefRequestedStatus = VitroGatewayService.getVitroGatewayService().isTrustRoutingCoapMessagingActive();

                            QueryTRThreadInfo tmpQueryInfo = dealWithRunningQueriesHM(FIND_IN_HM);
                            // (To do) for stopping queries  we have an extra member runningStatus in the queryDefinition which we check in the following clause appropriately.
                            //                         (THAT runningStatus only has 2 states: running (default) and stopped. And a user is able to control it from the interface.
                            if ((tmpQueryInfo == null) ||     // if null then it's the first time it is issued.
                                    (tmpQueryInfo != null &&
                                            tmpQueryInfo.getStatus() == QueryTRThreadInfo.STATUS_ENDED &&
                                            queryDefRequestedStatus  &&
                                            (queryDefPeriodSet != NO_PERIOD && (currentTimeInMillis - tmpQueryInfo.getTimeStarted().getTime()) >= queryDefPeriodInMillis))) {
                                try {
                                    new Thread() {
                                        public void run() {
                                            dealWithRunningQueriesHM(UPDATE_START_HM);
                                            //launch thread for the query

                                            Vector<InfoOnTrustRouting> result = VitroGatewayService.getVitroGatewayService().sendTrustRoutingCoapInquiryToAllNodes();
                                            if(result!=null && !result.isEmpty()){
                                                Iterator<InfoOnTrustRouting> resultIter = result.iterator();
                                                while(resultIter.hasNext()) {
                                                    InfoOnTrustRouting tmpInfoOnTrust =resultIter.next();
                                                    getCachedDirectoryOfTrustRoutingInfo().put(tmpInfoOnTrust.getSourceNodeId(), tmpInfoOnTrust);
                                                }
                                            }
                                            dealWithRunningQueriesHM(UPDATE_FINISH_HM);
                                        }
                                    }.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                    }
                }
                nextQueryToCheck++;
                try {
                    Thread.currentThread().sleep(500);
                }
                catch (InterruptedException e) {
                }
            }
        }
    }
}

class QueryTRThreadInfo {
    private Timestamp timeStarted = null;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_ENDED = 2;
    private int status = STATUS_ENDED;

    public QueryTRThreadInfo() {
        this.status = STATUS_ENDED;
        GregorianCalendar calendar = new GregorianCalendar();
        long currentTimeInMillis = calendar.getTimeInMillis();
        this.timeStarted = new java.sql.Timestamp(currentTimeInMillis);
    }

    public QueryTRThreadInfo(Timestamp givTimeStarted, int givStatus) {
        this.status = givStatus;
        this.timeStarted = givTimeStarted;
    }

    public int getStatus() {
        return status;
    }

    public Timestamp getTimeStarted() {
        return timeStarted;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTimeStarted(Timestamp timeStarted) {
        this.timeStarted = timeStarted;
    }
}
