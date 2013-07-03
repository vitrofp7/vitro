/*******************************************************************************
 * Copyright (c) 2013 VITRO FP7 Consortium.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     Antoniou Thanasis
 *     Paolo Medagliani
 *     D. Davide Lamanna
 *     Panos Trakadas
 *     Andrea Kropp
 *     Kiriakos Georgouleas
 *     Panagiotis Karkazis
 *     David Ferrer Figueroa
 *     Francesco Ficarola
 *     Stefano Puglia
 ******************************************************************************/
package vitro.vspEngine.service.query;
/* 
 */
import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.ConfigDetails;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SensorModel;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoPoint;
import vitro.vspEngine.service.geo.GeoRegion;
import vitro.vspEngine.service.engine.UserNode;

import java.util.*;

import java.io.*;

/**
 * The QueryProcessor class is used from the application tier (for example a GUI)
 * to issue queries to the underlying virtual network of WSNs.
 * (To do) Re do the way we deal with binary requests and History Data
 * (To do) Find a better logic for time-out period setting
 */
public class QueryProcessor {
    private Logger logger = Logger.getLogger(FinalResultEntryPerDef.class);

    private UserNode userNodePeer;  // Stores the userNodePeer object that the GUI uses
    private HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM;    // contains mappings from a gateway object to a vector of smart devices of the gateway
    private HashMap<String, Vector<SensorModel>> capHashMap;        // Stores the id/type pairs of the capabilities found on all gateways
    //  data types are stored per sensor model in the SensorModel objects and thus they are
    //              already embedded in the capTable Hashtable

    private QueryDefinition submittedQueryDef;

    private HashMap<Gateway, Vector<SmartNode>> gatewayToMotesForWhichQueryWillBeSentHM;

    private int timeoutPeriod;

    private Vector<QueriedMoteAndSensors> motesSensorsAndFunctionsForQueryVec;

    private HashSet<String> registeredQueryIds;

    private FinalResultEntryPerDef finalResultfile = new FinalResultEntryPerDef();

    /**
     * Constructor Method.
     * Initializes the class variables of the QueryProcessor so that they contain all
     * the useful information contained in the input and selections that the user gave
     * to the GUI.
     *
     * @param submQueryDef Stores the QueryDefinition object for which this processor is called.
     * @param givenTimeoutPeriod Defines how long(in seconds) the peer should wait for a response to all partial queries (that will be issued for the given query definition)
     *
     */
    public QueryProcessor(QueryDefinition submQueryDef, int givenTimeoutPeriod) {

        this.userNodePeer = UserNode.getUserNode();        
        this.gatewaysToSmartDevsHM = new HashMap<String, GatewayWithSmartNodes>(this.userNodePeer.getGatewaysToSmartDevsHM());
        this.capHashMap = this.userNodePeer.getCapabilitiesTable();
                
                
        this.submittedQueryDef = submQueryDef;
        this.finalResultfile.setQueryDefUId(submQueryDef.getuQid());

        // extra initialisations
        this.motesSensorsAndFunctionsForQueryVec = new Vector<QueriedMoteAndSensors>();
        this.registeredQueryIds = new HashSet<String>();
        gatewayToMotesForWhichQueryWillBeSentHM = new HashMap<Gateway, Vector<SmartNode>>();
        // call this method to update the gatewayToMotesForWhichQueryWillBeSentHM hashmap
        justAnalyzeRegions();
        this.timeoutPeriod = givenTimeoutPeriod;
    }


    private void justAnalyzeRegions() {
        //
        // Analyze given regions, return the Hashmap with UNIQUE gateways to UNIQUE motes
        // where the query should be sent.
        //
        //
        // Call the two analysis' methods. Both of them. They will both update/append info to the member
        // HashMap<String, Vector<String>>  gateIdToMotesForWhichQueryWillBeSentHM variable.
        //
        //
        rangeAnalysis();
        regionAnalysis();
    }

    public FinalResultEntryPerDef justFindResults() {
        if (this.gatewayToMotesForWhichQueryWillBeSentHM == null || gatewayToMotesForWhichQueryWillBeSentHM.isEmpty()) {
            logger.error("No matching target for query was found!");
            return finalResultfile;
        }
        // Call the method to delete any temporary files existing because of previous timeouts
        // (To do) This function needs serious updating. But priority is low.
        clearTempFiles();

        // Call one of the two analysis' methods, according to the location choice

        int queryCount = 0;    // queryCount will store the number of the queries made and sent.
        queryCount = sendQueriesToSmartDevs(); // according to the gateIdToMotesForWhichQueryWillBeSentHM variable
        // Read the results from the temporary files created by the PublicQueryHandler
        // and merge them in a final Result file.
        if (queryCount == 0) {
            logger.debug("No queries were sent!");
            return null;
        } else {
            final int allQueriesCount = queryCount;
            try {
                new Thread() {
                    public void run() {
                        readResults(allQueriesCount);
                        submittedQueryDef.addQueryResultFile(finalResultfile);
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return finalResultfile;
    }


    /**
     * This is the second public "interface" method of the QueryProcessor class.
     * Normally, this method will be called by the GUI after the call to retrieveSubmittedQueryDef.
     * Calls the rest of the class methods (some of them recursively)
     * in order to check the data, to make queries, forward them
     * and organize/return the results.
     *
     * @return FinalResultEntryPerDef object with the results.
     */
    public FinalResultEntryPerDef analyzeAndFindResults() {
//            results = new Vector();
        if (this.submittedQueryDef == null) {
            logger.error("You should first obtain the current Query Definition object!");
            return null;
        }
        //justAnalyzeRegions();
        return justFindResults();
    }

    /**
     * As it can be understood by the study of the temporary files mechanism that is
     * being used, if any timeout occurs, there may be temporary files left intact in
     * our system (created after the call of the method that is supposed to delete them).
     * This method deletes them all, so that we can proceed to new queries-responses handling.
     * (To do) This function needs serious updating. But priority is low.
     */
    private void clearTempFiles() {

        File f;
        FileReader fr;
        boolean hasMoreFiles = true;
        int i = 0;

        while (hasMoreFiles) {

            try {
                f = new File(ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "temp" + Integer.toString(i));
                fr = new FileReader(ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "temp" + Integer.toString(i));
                fr.close();
                if (f.delete()) {
                    logger.debug("Deleted a file");
                }
                i++;
            } catch (FileNotFoundException fnfe) {
                hasMoreFiles = false;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * Function that updates the local hashmap of motes for which the query will be sent
     * for a certain gateway, eliminating the case of duplicate entries.
     * (again this is needed especially if regions(gateways) and ranges (coverage areas) can
     * both be set from the GUI, so as to brutally eliminate the "overlapping" cases).
     */
    private void updateHashMapofMotesToQuery(Gateway givGatew, SmartNode smartDevForQuery) {
        Vector<SmartNode> previousInsertedMotesOfGatewayVec = null;
        Set<Gateway> keysOfGatewaysToMotes = this.gatewayToMotesForWhichQueryWillBeSentHM.keySet();

        previousInsertedMotesOfGatewayVec = this.gatewayToMotesForWhichQueryWillBeSentHM.get(givGatew);

        if (previousInsertedMotesOfGatewayVec == null) {
            previousInsertedMotesOfGatewayVec = new Vector<SmartNode>();
        }
        boolean thisSmartDevExistsAlready = false;
        // if the previousQueriedMotesOfGatewayVec contains the moteId of the motesForQueryVec.elementAt(i)
        // then skip that element.
        for (int k = 0; k < previousInsertedMotesOfGatewayVec.size(); k++) {
            if (previousInsertedMotesOfGatewayVec.elementAt(k).getId().equals(smartDevForQuery.getId())) {
                thisSmartDevExistsAlready = true;
                break; // because the first match is enough. We don't have to go through the entire previousInsertedMotesOfGatewayVec in each loop (for each mote in the vector motesSensAndFunctForQueryVec)
            }
        }
        if (!thisSmartDevExistsAlready) {
            previousInsertedMotesOfGatewayVec.addElement(smartDevForQuery);
            this.gatewayToMotesForWhichQueryWillBeSentHM.put(givGatew, previousInsertedMotesOfGatewayVec);
        }
    }


    /**
     * This method analyzes the input data and creates/sends the queries in the case
     * that the user has chosen to define a range
     * (To do) Should we change from GeoPoint to Coordinate ?
     *
     */
    private void rangeAnalysis() {

        boolean isHist = false;// (++++) to change)

        if( submittedQueryDef.getQContent().getAreasSelectionHM()!=null &&  !submittedQueryDef.getQContent().getAreasSelectionHM().isEmpty()) {
            for (Map.Entry<Integer, Vector<GeoRegion>> areasSelectionHMEntry : submittedQueryDef.getQContent().getAreasSelectionHM().entrySet()) {
                Vector<GeoRegion> selRegionsVec = areasSelectionHMEntry.getValue();
                if(selRegionsVec != null && selRegionsVec.isEmpty())
                {
                    // For each known gateway...
                    Set<String> keysOfGIds = this.gatewaysToSmartDevsHM.keySet();
                    Iterator<String> itgwId = keysOfGIds.iterator();
                    while (itgwId.hasNext()) {
                        String currGwId = itgwId.next();
                        Gateway currGw = this.gatewaysToSmartDevsHM.get(currGwId);
                        if(currGw!= null && this.gatewaysToSmartDevsHM.get(currGwId).getSmartNodesVec() !=null){

                            Vector<SmartNode> tmpSmartDevVec = this.gatewaysToSmartDevsHM.get(currGwId).getSmartNodesVec();
                            for(int i = 0 ; i< selRegionsVec.size(); i++) {
                                if(currGw.getCoverageArea() != null && selRegionsVec.elementAt(i).containsEntireRegion(currGw.getCoverageArea())) {
                                    //
                                    // if the gateway coverage area IS defined AND is contained Entirely inside a selected region then
                                    // add all of its SmartNodes to the SmartNodes to be queried.
                                    // then BREAK the search for the current gateway. (since we already included all its SmartNodes)
                                    // For each sensor (i.e. MOTE) of this gateway...
                                    SmartNode tempMote;
                                    GeoPoint moteGPlocation;
                                    for (int j = 0; j < tmpSmartDevVec.size(); j++) {
                                        tempMote = tmpSmartDevVec.elementAt(j);
                                        updateHashMapofMotesToQuery(currGw, tempMote);
                                    }
                                    break;
                                }
                                else if( (currGw.getCoverageArea() != null && selRegionsVec.elementAt(i).overlapsWithRegion(currGw.getCoverageArea()) ) ||
                                        (currGw.getCoverageArea() == null ) ) {
                                    //
                                    // if the Gateway coverage (member var) area IS NOT set OR it IS set but only collides with a selected region then
                                    // loop through all the SmartNodes and choose those whose points ARE SET (valid) and are contained in the selected region.
                                    // don't break the search though.
                                    // For each SmartNodes (i.e. MOTE) of this gateway...
                                    SmartNode tempMote;
                                    GeoPoint moteGPlocation;
                                    for (int j = 0; j < tmpSmartDevVec.size(); j++) {

                                        tempMote = tmpSmartDevVec.elementAt(j);
                                        moteGPlocation = tempMote.getLocation();
                                        // If the SmartNode is within the co-ordinates given by the user
                                        //
                                        if (moteGPlocation.isValidPoint() && selRegionsVec.elementAt(i).containsPoint(moteGPlocation)){
                                            //
                                            // We have a match:
                                            // put in Vector of motes that will be (possibly sent a query = they won't if they don;t have the required sensor though)
                                            //
                                            //logger.debug("Found match for range:"+ currGw.getName() +"::" + tempMote.getId());
                                            updateHashMapofMotesToQuery(currGw, tempMote);
                                        }
                                    }
                                }

                            }//end for loop
                        }
                    }
                }
            }
        }
    }

    /**
     * This method analyzes the input data and creates/sends the queries in the case
     * that the user has chosen to specify a region (i.e. specific gateways and/or motes)
     * (To do) We should handle the case of QueryProcessor.selAllMotes being defined for a gateway.
     * This case is actually different from a query definition that selects each mote of the gateway one by one,
     * because if the former definition is to be issued again in a future point, it will include new motes added (if any) to the gateway
     * in the meantime, when the later will just query only the selected motes again (Exculding the new ones added if any).
     *
     */
    private void regionAnalysis() {

        boolean isHist = false;// (++++) to change)

        if( submittedQueryDef.getQContent().getDevicesSelectionHM()!=null &&  !submittedQueryDef.getQContent().getDevicesSelectionHM().isEmpty()) {
            for (Map.Entry<Integer, HashMap<String, Vector<String>>> devicesSelectionHMEntry : submittedQueryDef.getQContent().getDevicesSelectionHM().entrySet()) {
                // we get the requested hashmap of gateways and motes first (defined in the QContent of the submitted Query definition)
                for (Map.Entry<String,  Vector<String>> gwTodevicesEntry : devicesSelectionHMEntry.getValue().entrySet()) {
                    // loop over all SELECTED Gateways
                    String tmpGateId = gwTodevicesEntry.getKey();

                    // we get a fixed copy so as not to spoil the moteIdsVec of QueryContent
                    Vector<String> selectedMoteIdsVec = new Vector<String>(gwTodevicesEntry.getValue());
                    //
                    // HANDLE WILDCARD case, where all motes of a gateway were selected
                    //
                    boolean wildcardMoteFlagSet = false;
                    if (selectedMoteIdsVec.size() == 1 && selectedMoteIdsVec.elementAt(0).equals(QueryContentDefinition.selAllMotes)) {
//                        logger.debug("WILDCARD SET!!!!!!");
                        wildcardMoteFlagSet = true;
                    }
                    //
                    // Here we have a list of specific motes to scan through (or a wildcard that in the first pass will cause the selectedMoteIdsVec to be filled with all mote ids from the specific Gateway...
                    //
                    //  logger.debug("Count of selected motes::" + Integer.toString(selectedMoteIdsVec.size()) );
                    for (int i = 0; i < selectedMoteIdsVec.size(); i++) {
                        // For each gateway of all those registered in the userpeer...
                        Set<String> keysOfGIds = this.gatewaysToSmartDevsHM.keySet();
                        Iterator<String> itgwId = keysOfGIds.iterator();
                        while (itgwId.hasNext()) {
                            String currGwId = itgwId.next();
                            Gateway currGw = this.gatewaysToSmartDevsHM.get(currGwId);
                            if(currGw!= null && this.gatewaysToSmartDevsHM.get(currGwId).getSmartNodesVec() !=null){
                                Vector<SmartNode> tmpSmartDevVec = this.gatewaysToSmartDevsHM.get(currGwId).getSmartNodesVec();

                                SmartNode tempMote;
                                GeoPoint gplocation;
                                // if the gateway Id matches with the one defined at the selected mote entry
                                if (currGw.getId().equals(tmpGateId)) {
                                    if ((i == 0) && (wildcardMoteFlagSet == true)) // Execute only in the first pass
                                    {
    //                                    logger.debug("CODE FOR WILDCARD SET!!!!!!");

                                        // remove "wildcard" and add all motes Ids from this gateway in the selectedMoteIdsVec
                                        selectedMoteIdsVec.removeElementAt(0);
                                        for (int k = 0; k < tmpSmartDevVec.size(); k++) {
                                            tempMote = tmpSmartDevVec.elementAt(k);
                                            selectedMoteIdsVec.addElement(tempMote.getId());
                                        }
                                    }
                                    // For each SmartNode of this gateway
                                    for (int k = 0; k < tmpSmartDevVec.size(); k++) {
                                        tempMote = tmpSmartDevVec.elementAt(k);
                                        // If it matches with one of the selected motes
                                        if (tempMote.getId().equals(selectedMoteIdsVec.elementAt(i))) {
                                            //
                                            // We have a match:
                                            // put in Vector of motes that will be (possibly sent a query = they won't if they don;t have the required sensor though)
                                            //
                                            updateHashMapofMotesToQuery(currGw, tempMote);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private int sendQueriesToSmartDevs() {
        int qCount = 0;    // counter of the queries

        boolean isHist = false;// (++++) to change)

        Set<Gateway> keysOfGatewayToMotes = this.gatewayToMotesForWhichQueryWillBeSentHM.keySet();
        Iterator<Gateway> itOnGws = keysOfGatewayToMotes.iterator();
        while (itOnGws.hasNext()) {
            // for this gateway Id. Get smart devices id vector to sent queries. Check if they match the required capabilities
            // and sent (either directly per smart device OR per Gateway -aggregated)
            Gateway currGw = itOnGws.next();
            String currGateName = QueryContentDefinition.selUndefined;
            Vector<SmartNode> selectedSmartDevsVec = this.gatewayToMotesForWhichQueryWillBeSentHM.get(currGw);

            motesSensorsAndFunctionsForQueryVec = new Vector<QueriedMoteAndSensors>();
            // For each SmartNode of this gateway
            for (int i = 0; i < selectedSmartDevsVec.size(); i++) {
                //
                // send queries if mote matches the selected capabilities
                // no += required here for qCount, because qCount gets Incremented inside the method. (note that it is also an argument of the method)
                qCount = matchCapAndDoAction(currGw.getId(), currGw.getName(), selectedSmartDevsVec.elementAt(i), isHist, qCount);
            }
            if (submittedQueryDef.isAggregateQueryFlag() && motesSensorsAndFunctionsForQueryVec.size() > 0) {
                Vector<ReqFunctionOverData> refFunctionsVec = submittedQueryDef.getQContent().getUniqueRefFunctionVecForMotes(motesSensorsAndFunctionsForQueryVec);
                if (!motesSensorsAndFunctionsForQueryVec.isEmpty()) {
                    // Careful here, we should use the  currGw.getId() and currGw.getName() !!!
                    int tmpQueryId = this.userNodePeer.sendAnAggrQuery(submittedQueryDef.getuQid(), currGw.getId(), motesSensorsAndFunctionsForQueryVec, isHist, refFunctionsVec, qCount,
                            submittedQueryDef.isContinuationEnabledFlag(),
                            submittedQueryDef.isAsynchronousFlag(),
                            submittedQueryDef.isDtnEnabledFlag(),
                            submittedQueryDef.isSecurityEnabledFlag(),
                            submittedQueryDef.isEncryptionEnabledFlag());
                    
                    // reserve a default Timed-Out entry in the finalResultFile
                    finalResultfile.addPartialResult(new PublicResponseAggrMsg(submittedQueryDef.getuQid(), currGw.getId(), currGw.getName(), motesSensorsAndFunctionsForQueryVec, refFunctionsVec, qCount));
                    registeredQueryIds.add(Integer.toString(tmpQueryId) + "__" + Integer.toString(qCount));
                    qCount += 1;
                }
            }
        }
        if (qCount == 0) {
            logger.error("Error: No valid entry was found for the specified Region's Gateway(s)");
            return qCount;
        }
        return qCount;
    }

    /**
     * Sends a query if the SmartNode has the selected generic capabilities.
     * Typically it scans through the list of sensor model ids in the specific mote (tmpmote)
     * for a match with each given Generic Capability Description.
     * Caution: queries are sent immediately within this method, if the aggregation mode is disabled.
     * If the aggregation mode is enabled then the queries will be sent in the calling method. In the
     * latter case this method just gathers all motes and sensors to be queried in a HashMap that will be
     * used for the creation of the final aggregated query to be sent.
     *
     * @param gateId          The gateway Peer Id where the query will be sent
     * @param gateName        The name of the gateway Peer where the query will be sent
     * @param tmpMote         The SmartNode that will be checked to verify if it has sensors that are included in the specified generic capability in the query
     * @param isHist          (To do) (Change this) This indicates if the query has history mode on.
     * @param qCount          The number of queries till the call of this method for the current running query definition.
     * @return The number of queries sent (or to be sent)
     * TODO: IMPORTANT!
     * TODO: needs further adapting to the new query definition schema that allows specific sets of devices to be queried about different capabilities!
     * TODO: Possible solution: Get all involved sensors to be querried (as is now)
     *                          Also get the sets of sensors (even if there is redundancy)
     *                          Then go through all involved sensors and find through the sets it belongs to, and the mapping of sets to capabiities, which capabilities should be querried for that sensor!
     */
    private int matchCapAndDoAction(String gateId, String gateName, SmartNode tmpMote, boolean isHist, int qCount) {
        String cap;
        Vector<SensorModel> vecCapSensorModels;
        // For each Generic capability the user asked for...

        Vector<ReqSensorAndFunctions> tmpVecOfAllQuerSens = new Vector<ReqSensorAndFunctions>();
        QueriedMoteAndSensors moteAndVecOfAllQuerSens = new QueriedMoteAndSensors(tmpMote.getId(), tmpVecOfAllQuerSens);

        for (Map.Entry<Integer, HashMap<String, Vector<Integer>> > capabilitiesSelectionHMEntry : submittedQueryDef.getQContent().getCapabilitiesSelectionHM().entrySet()) {

            for (Map.Entry<String,  Vector<Integer>> capToFunctionsEntry : capabilitiesSelectionHMEntry.getValue().entrySet()) {
                cap = capToFunctionsEntry.getKey();
                String sensModelId = SensorModel.invalidId;
                String sensModelDataType = "";

                vecCapSensorModels = capHashMap.get(cap);
                //
                // Send query if the mote has this capability
                //
                // New:: Scan through the sensor model ids for this Generic Capability Description
                // check if any of them appears inside the list of sensor model ids in the specific mote we have (tmpMote)
                //
                for (int i1 = 0; i1 < vecCapSensorModels.size(); i1++) {
                    if (!(vecCapSensorModels.elementAt(i1).getGatewayId().equals(gateId)))// means that the sensor model is not inside the current gateway we deal with, and should not be considered in this call
                    {
                        continue;
                    }
                    sensModelId = vecCapSensorModels.elementAt(i1).getSmID();

                    sensModelDataType = vecCapSensorModels.elementAt(i1).getDataType();
                    // CAREFUL: TO DO +++ when binary type handling is RE-ENABLED properly then the following commented && clause should be uncommented!!!!
                    if ( SensorModel.vectorContainsSensorModel(tmpMote.getCapabilitiesVector(), gateId,  sensModelId)) {/*&& !(sensModelDataType.equalsIgnoreCase(SensorModel.binaryDataType))*/
                        //logger.debug("The SmartNode " + tmpMote.getId()+" contains sensor::" +sensModelId);
                        if (submittedQueryDef.isAggregateQueryFlag()) // one query will be sent per GateWay (so the sendQuery won't be in this method)
                        {
                            tmpVecOfAllQuerSens.addElement(new ReqSensorAndFunctions(sensModelId, capToFunctionsEntry.getValue()));
                        } else // if no aggregation mode, then send immediately
                        {
                            // However we use again the aggrTypeOfMessage only with single entries for the arguments (representing a specific SmartNode and Sensor Model
                            Vector<ReqSensorAndFunctions> tmpVecOfSingleQuerSens = new Vector<ReqSensorAndFunctions>();

                            tmpVecOfSingleQuerSens.addElement(new ReqSensorAndFunctions(sensModelId, capToFunctionsEntry.getValue()));

                            moteAndVecOfAllQuerSens.setQueriedSensorIdsAndFuncVec(tmpVecOfSingleQuerSens);

                            Vector<QueriedMoteAndSensors> tmpquerMotAndSensVec = new Vector<QueriedMoteAndSensors>();
                            tmpquerMotAndSensVec.addElement(moteAndVecOfAllQuerSens);

                            Vector<ReqFunctionOverData> tmpfunctionVec = submittedQueryDef.getQContent().getUniqueRefFunctionVecForMotes(tmpquerMotAndSensVec);
                            if (!tmpquerMotAndSensVec.isEmpty()) {
                                int tmpQueryId = this.userNodePeer.sendAnAggrQuery(submittedQueryDef.getuQid(), gateId, tmpquerMotAndSensVec, isHist, tmpfunctionVec, qCount,
                                        submittedQueryDef.isContinuationEnabledFlag(),
                                        submittedQueryDef.isAsynchronousFlag(),
                                        submittedQueryDef.isDtnEnabledFlag(),
                                        submittedQueryDef.isSecurityEnabledFlag(),
                                        submittedQueryDef.isEncryptionEnabledFlag());
                                // reserve a default Timed-Out entry in the finalResultFile
                                finalResultfile.addPartialResult(new PublicResponseAggrMsg(submittedQueryDef.getuQid(), gateId, gateName, tmpquerMotAndSensVec, tmpfunctionVec, qCount));

                                registeredQueryIds.add(Integer.toString(tmpQueryId) + "__" + Integer.toString(qCount));
                                qCount += 1;
                            }
                        }
                    }
                   //     TODO: handling of binary data
                    else if (! SensorModel.vectorContainsSensorModel(tmpMote.getCapabilitiesVector(), gateId,  sensModelId))  {
                        logger.info("Info (ignore)::" + tmpMote.getName() + " - " + tmpMote.getId() + " does not support sensing of " + cap + "::" + sensModelId);
                    }
                }
            }
        }
        if (submittedQueryDef.isAggregateQueryFlag() && tmpVecOfAllQuerSens.size() > 0) {
            this.motesSensorsAndFunctionsForQueryVec.addElement(moteAndVecOfAllQuerSens);
        }
        return qCount;
    }

    /**
     * Method readResults:
     * <p/>
     * The handler of the responses used by this application writes the values
     * of the responses in temporary files named in a way that this processor can expect
     * them, read them and then delete the temporary files. This method is called after
     * all queries are sent, in order to do this work. To wait for the expected temporary
     * files to appear, read from them the expected values and delete them. It needs to know
     * the amount of queries sent, in order to know how many (and what) temporary files it
     * must expect to read.
     *
     * @param qCount The amount of queries sent
     */
    private void readResults(int qCount) {

        Iterator<String> it2 = registeredQueryIds.iterator();
        int candidateqCount2 = -1;
        String tmpFileNameAppendixStr2 = "";

        while (it2.hasNext()) {
            tmpFileNameAppendixStr2 = it2.next();
            String[] tmpTokens = tmpFileNameAppendixStr2.split("__");
            logger.debug("I have the queryID of:" + tmpTokens[0]);
        }

        logger.debug("My query sent count is " + Integer.toString(qCount));

        boolean fileFound = false;
        String valueStr;
        FileReader rfile = null;
        BufferedReader rBuff = null;
        int i;
        boolean hasTimeout = true;
        long startTime, currentTime;
        GregorianCalendar calendar = new GregorianCalendar();

        startTime = calendar.getTimeInMillis();
        currentTime = startTime;
        Vector<Boolean> fileFoundVec = new Vector<Boolean>();
        for (i = 0; i < qCount; i++) {
            fileFoundVec.addElement(new Boolean(false));
        }
        int filesRemainToBeRead = qCount;
        i = 0;
        while ((filesRemainToBeRead > 0) && ((hasTimeout) && (currentTime < (startTime + this.timeoutPeriod * 1000)))) {
            while (fileFoundVec.get(i).booleanValue())      // skip files that were already been found and been read
            {
                i += 1; // circular movement between files
                if (i == qCount) {
                    i = 0;
                }
            }
            try {
                /**
                 * (To do) For the moment we skip entirely the old "binary" section. We will worry about this later!
                 *
                 */
                Iterator<String> it1 = registeredQueryIds.iterator();
                int candidateqCount = -1;
                String tmpFileNameAppendixStr = "";

                while (it1.hasNext()) {
                    tmpFileNameAppendixStr = it1.next();
                    String[] tmpTokens = tmpFileNameAppendixStr.split("__");

                    candidateqCount = Integer.valueOf(tmpTokens[1]).intValue();
                    if (candidateqCount == i) {
                        break;
                    }

                }

                FileInputStream fis = new FileInputStream(ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "temp" + tmpFileNameAppendixStr);
                // rfile = new FileReader(ConfigDetails.getConfigDetails().getPathToPeer()+File.separatorChar+"Temp"+File.separatorChar+"temp"+tmpFileNameAppendixStr); // ++++ elegxos periptwsh pou den yparxei to Config.txt
                try {
                    logger.debug("Start -Processing aggrResponse from TMP File: " + ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "temp" + tmpFileNameAppendixStr);
                    PublicResponseAggrMsg partialResponse = new PublicResponseAggrMsg(fis);
                    logger.debug("End -Processing aggrResponse from TMP File: " + ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "temp" + tmpFileNameAppendixStr);
                    finalResultfile.addPartialResult(partialResponse);
                }
                catch (IOException e) {
                    fis.close();
                    throw new FileNotFoundException();               // pass a loop
                }
                catch (IllegalArgumentException e) {
                    fis.close();
                    throw new FileNotFoundException();               // pass a loop
                }
                //StringWriter out = new StringWriter();
                //doc.sendToWriter(out);
                //logger.debug(out.toString());
                fileFoundVec.set(i, new Boolean(true));
                filesRemainToBeRead -= 1;
                fis.close();
            }

            catch (FileNotFoundException fnfe) {
                // check if waiting for a response has been timed out
                // Timeout = 9 seconds
                try {
                    Thread.sleep(2 * 100); // wait 200 milliseconds
                }
                catch (Exception e) {
                }

            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
            // At the end go to the next file in line
            i += 1; // circular movement between files
            if (i == qCount) {
                i = 0;
            }
            calendar = new GregorianCalendar();
            currentTime = calendar.getTimeInMillis();
        }
    }

    // getters
    /**
     * This method indicated which gateways and SmartNodes will be queried for a given submitted query definition, after it has been processed by  this QueryProcessor object)
     * @return The hashmap (Gateway to vector of SmartNodes) for the refined (after the QueryProcessor has processed the query definition)
     *         set of gateways and SmartNodes that will be queried.
     *
     */
    public HashMap<Gateway, Vector<SmartNode>> getGatewayToMotesForWhichQueryWillBeSentHM() {
        return gatewayToMotesForWhichQueryWillBeSentHM;
    }

}
