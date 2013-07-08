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
/*
 * QueryScheduler.java
 *
 */

package vitro.vspEngine.service.query;

import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Class responsible for starting and stopping the Query Scheduler Thread.
 * The Query Scheduler schedules query definitions for submission in the p2p network.
 * @author antoniou
 */
public class QueryScheduler extends Thread {

    private boolean runFlag = true;
    private IndexOfQueries myIndexOfQueries = null;
    private QuerySchedulerThread qst = null;

    /**
     * Creates a new instance of QueryScheduler
     */
    private QueryScheduler() {
        myIndexOfQueries = IndexOfQueries.getIndexOfQueries();
    }

    private static QueryScheduler myQueryScheduler = null;

    /**
     * This is the function the world uses to get the QueryScheduler.
     * It follows the Singleton pattern
     */
    public static QueryScheduler getQueryScheduler() {
        if (myQueryScheduler == null) {
            myQueryScheduler = new QueryScheduler();
        }
        return myQueryScheduler;
    }

    public void startScheduler() {
        runFlag = true;
        if(qst==null) {
            qst = new QuerySchedulerThread();
        }
    }

    public void stopScheduler() // this is very graceful, and allows all threads to finish up themselves 
    {
        runFlag = false;
    }

    synchronized public boolean isQueryIdActivelyRunning(String quid)
    {
        boolean retVal = false;
        if(qst!=null && runFlag==true)
        {
            QueryJXThreadInfo tmpQueryInfo = qst.dealWithRunningQueriesHM(QuerySchedulerThread.FIND_IN_HM, quid);
            if ((tmpQueryInfo == null) ||
                    (tmpQueryInfo != null && tmpQueryInfo.getStatus() == QueryJXThreadInfo.STATUS_STARTED))
            {
                retVal = true;
            }
            else if((tmpQueryInfo == null) ||
                    (tmpQueryInfo != null && tmpQueryInfo.getStatus() == QueryJXThreadInfo.STATUS_ENDED))
            {
                retVal = false;
            }
        }
        return retVal;

    }

    /**
     * This is the main Scheduler-for-Queries Thread.
     * Follows a Round Robin policy.
     */
    private class QuerySchedulerThread extends Thread {

        private HashMap<String, QueryJXThreadInfo> regdRunQueries;
        private final int UPDATE_FINISH_HM = 1;
        private final int UPDATE_START_HM = 2;
        public static final int FIND_IN_HM = 3;

        QuerySchedulerThread() {
            regdRunQueries = new HashMap<String, QueryJXThreadInfo>(); // associates a unique query id with a QueryJXThreadInfo object
            start();
        }

        synchronized public QueryJXThreadInfo dealWithRunningQueriesHM(int readWriteFlag, String quid) {

            if (readWriteFlag == FIND_IN_HM) // we read to find a specific quid
            {
                if (regdRunQueries.containsKey(quid)) {
                    return regdRunQueries.get(quid);
                }
                return null;
            } else if (readWriteFlag == UPDATE_START_HM) {
                QueryJXThreadInfo tmpQueryJXThreadInfo = null;
                if (regdRunQueries.containsKey(quid)) {
                    tmpQueryJXThreadInfo = regdRunQueries.get(quid);
                } else {
                    tmpQueryJXThreadInfo = new QueryJXThreadInfo();
                    tmpQueryJXThreadInfo = regdRunQueries.put(quid, tmpQueryJXThreadInfo);
                    tmpQueryJXThreadInfo = regdRunQueries.get(quid);
                }
                GregorianCalendar calendar = new GregorianCalendar();
                long currentTimeInMillis = calendar.getTimeInMillis();
                tmpQueryJXThreadInfo.setStatus(QueryJXThreadInfo.STATUS_STARTED);
                tmpQueryJXThreadInfo.setTimeStarted(new java.sql.Timestamp(currentTimeInMillis));
            } else if (readWriteFlag == UPDATE_FINISH_HM) {
                QueryJXThreadInfo tmpQueryJXThreadInfo = null;
                if (regdRunQueries.containsKey(quid)) {
                    tmpQueryJXThreadInfo = regdRunQueries.get(quid);
                } else {
                    tmpQueryJXThreadInfo = new QueryJXThreadInfo();
                    tmpQueryJXThreadInfo = regdRunQueries.put(quid, tmpQueryJXThreadInfo);
                    tmpQueryJXThreadInfo = regdRunQueries.get(quid);
                }
                tmpQueryJXThreadInfo.setStatus(QueryJXThreadInfo.STATUS_ENDED);
            }
            return null;
        }


        public void run() {
            int nextQueryToCheck = 0;
            while (runFlag) {
                //
                // Check with Round Robin each and every entry in IndexOfQueries, reIssue if necessary (and if set period has passed).
                // we launch a new THREAD for each issue of a query Definition. We should take care
                // that no two (or more) threads of the same query definition are running at the same time!
                //
                // we repeatedly get and FIX (using the copy constructor) this because it can change from time to time (if user adds more queries)
                Vector<String> st0 = new Vector<String>(myIndexOfQueries.getAllQueriesDefHashMap().keySet());
                if (nextQueryToCheck >= st0.size())
                    nextQueryToCheck = 0; // reset nextQueryToCheck to provide round robin
                //System.out.println("Size Of scheduled Vector:"+st0.size() +" Next Query to check:" +Integer.toString(nextQueryToCheck));
                for (int counter = 0; counter < st0.size(); counter++) {
                    if (counter == nextQueryToCheck) {
                        final String tmpqDefuId = st0.elementAt(counter);
                        // Main check:
                        // if the query is not found in the stillRunningQueries HM,
                        // OR it is found AND has ended AND its period has passed
                        // then --> launch it
                        GregorianCalendar calendar = new GregorianCalendar();
                        long currentTimeInMillis = calendar.getTimeInMillis();
                        //todo: shouldn't this also be using a copy constructor as above?
                        final QueryDefinition tmpDef = myIndexOfQueries.getAllQueriesDefHashMap().get(tmpqDefuId);
                        if(tmpDef != null) //new
                        {
                            long queryDefPeriodSet = tmpDef.getDesiredPeriod();
                            long queryDefPeriodInMillis = queryDefPeriodSet * 1000;
                            int queryDefRequestedStatus = tmpDef.getRunningStatus();

                            QueryJXThreadInfo tmpQueryInfo = dealWithRunningQueriesHM(FIND_IN_HM, tmpqDefuId);
                            // (To do) for stopping queries  we have an extra member runningStatus in the queryDefinition which we check in the following clause appropriately.
                            //                         (THAT runningStatus only has 2 states: running (default) and stopped. And a user is able to control it from the interface.
                            if ((tmpQueryInfo == null) ||     // if null then it's the first time it is issued.
                                    (tmpQueryInfo != null &&
                                            tmpQueryInfo.getStatus() == QueryJXThreadInfo.STATUS_ENDED &&
                                            queryDefRequestedStatus == QueryDefinition.STATUS_RUNNING &&
                                            (queryDefPeriodSet != QueryDefinition.noPeriodicSubmission && (currentTimeInMillis - tmpQueryInfo.getTimeStarted().getTime()) >= queryDefPeriodInMillis))) {
                                try {
                                    new Thread() {
                                        public void run() {
                                            dealWithRunningQueriesHM(UPDATE_START_HM, tmpqDefuId);
                                            //launch thread for the query
                                            int selTimeoutPeriod = 11*30; //in seconds // 5 and a half minutes. It should be more than 5 minutes (limit of VGW with DTN response!) TODO: use a constant?! like VGWs PROXY_RESPONSE_TIMEOUT = 5000 or DTN_REQUEST_TIMEOUT = 5;
                                            FinalResultEntryPerDef results = tmpDef.processQueryAndfindResults(selTimeoutPeriod);
                                            dealWithRunningQueriesHM(UPDATE_FINISH_HM, tmpqDefuId);
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

class QueryJXThreadInfo {
    private Timestamp timeStarted = null;
    public static final int STATUS_STARTED = 1;
    public static final int STATUS_ENDED = 2;
    private int status = STATUS_ENDED;

    public QueryJXThreadInfo() {
        this.status = STATUS_ENDED;
        GregorianCalendar calendar = new GregorianCalendar();
        long currentTimeInMillis = calendar.getTimeInMillis();
        this.timeStarted = new java.sql.Timestamp(currentTimeInMillis);
    }

    public QueryJXThreadInfo(Timestamp givTimeStarted, int givStatus) {
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
