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
/*
 * IndexOfQueries.java
 *
 */

package vitro.vspEngine.service.query;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.abstractservice.*;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;
import vitro.vspEngine.service.geo.GeoRegion;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.vspEngine.service.persistence.DBSelectionOfGateways;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;
import vitro.vspEngine.service.persistence.DBSmartNodeOfGateway;

import java.util.*;

/**
 * This IndexOfQueries class represents the XML structure of the index for Queries
 * issued in the Network, initiated by the current peer.
 * These Queries are defined by the Query content and a unique Id. They are the
 * "definition" for the query messages issued in the network (and in so different from them).
 * <pre>
 * queries_index.xml : File that indexes active queries in the network
 * <p/>
 * Path: ./temp/queries_index.xml
 * <p/>
 * &lt;queries-index&gt;
 * 	&lt;query&gt;
 *          &lt;uqid&gt;&lt;/uqid&gt; 		&lt;---------- Random id for the query definition (query def), unique in the index.
 *                                                  this query can be issued periodically so the id has nothing to do
 *                                                  with the issued queries in the Network per periodic issue of the query def).
 *          &lt;content&gt;(...)&lt;/content&gt; 	       &lt;--- If one query is issued with similar content it will be merged with this one!
 *                                                  (this could be also a primary key (instead of the uqid but let's keep it simple.
 *                                                  Also I may be mistaken in the sense that queries with same content should be merged.)
 *          &lt;desiredPeriod&gt;12&lt;/desiredPeriod&gt; &lt;------ The amount of time in seconds that should pass between re-issuing. (this is just a recommendation to the
 *                                                  program. It could be ignored or surpassed by far)
 *                                                  if `-1` or not present, then the query is not issued periodically.
 *          &lt;desiredHistory&gt;2&lt;/desireHistory&gt; &lt;----- The number of query results do we hold cached from previous query def issued.
 *                                                  This limits the number of the entries in the "queryResultFiles" tag
 *                                                  if `0`, `1` or not present, then only one file is kept.
 *                                                  if `-1`, then only ALL files are kept.
 *          &lt;isAggregate&gt;true&lt;/isAggregate&gt; &lt;----- This flag sets aggregate queries mode (send 1 query per gateway) on or off.
 *          &lt;queryResultFile&gt;
 *              &lt;Filename&gt;&lt;/Filename&gt;   &lt;---------  This should be in a folder named after the "uqid" (see above). This is the final
 *                                                  aggregated file per queryDef issue, and not the intermediate tmp files.
 *              &lt;timestamp&gt;&lt;/timestamp&gt; &lt;---------- This is the date of creation of the FINAL file. Timestamps of the values read
 *                                                  from the network are inside the contents/results of this file
 *          &lt;/queryResultFile&gt;
 *          &lt;queryResultFile&gt;
 * 			.
 * 			.
 *          &lt;/queryResultFile&gt;
 * 	&lt;/query&gt;
 * 	&lt;query&gt;
 * 		.
 * 		.
 * 	&lt;/query&gt;
 * &lt;/queries-index&gt;
 * </pre>
 * <p/>
 * <p/>
 * (To do) Complete the code
 *
 * @author antoniou
 */
public class IndexOfQueries {

    private Logger logger = Logger.getLogger(getClass());
    public static final String queryDefTag = "query";

    public static final String PREDEPLOYED_PREFIX= "mem_only__-__";
    public static final String COMPOSED_DB_PREFIX  = "db_stored__-__";

    HashMap<String, QueryDefinition> queriesDefHM;


    private IndexOfQueries() {

        queriesDefHM = new HashMap<String, QueryDefinition>();
    }

    private static IndexOfQueries myQueryIndex = null;

    /**
     * This is the function the world uses to get the Index Of Queries Definitions'.
     * It follows the Singleton pattern
     */
    public static IndexOfQueries getIndexOfQueries() {

        if (myQueryIndex == null) {
            myQueryIndex = new IndexOfQueries();
        }
        return myQueryIndex;
    }

    /**
     * Creates a new instance of IndexOfQueries read from the corresponding file
     * (Used for persistence)
     */
    public static void parseIndexFromFile(String filenameandPath) {
        // (To do) (++++) read file and update the HashMap

    }

    /**
     * Write the instance of IndexOfQueries back to the corresponding file
     * (Used for persistence)
     * (To do)
     */
    public void writeIndexBackToFile(String filenameandPath) {
        // (To do) (++++) write file from the Vector

    }

    public QueryDefinition getQueryDefinitionById(String uQid) {
        QueryDefinition retVal = null;
        if(queriesDefHM.containsKey(uQid)){
            retVal =  queriesDefHM.get(uQid);
        }
        return retVal;
    }

    synchronized public HashMap<String, QueryDefinition> getAllQueriesDefHashMap() {
        return queriesDefHM;
    }


    synchronized public QueryDefinition addNewQueryDefPreDep(String preDeployedServiceId, UserNode unPeer, Vector<GeoRegion> rangeVec, HashMap<String, Vector<String>> gateIdToSelectedMotesVecHM, HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedfuncVecHM) {
        return addNewQueryDefPreDep(preDeployedServiceId, unPeer,  rangeVec,  gateIdToSelectedMotesVecHM,  genCapQuerriedToSelectedfuncVecHM, null);
    }

    /**
     *  gets a valid ReqFunctionOverData name, from the provided abbreviation stored in the DB
     *
     */
    public static String  getValidFunctionNameFromDBCapability(String dbFuncName){
        String retStr = null;
        if(dbFuncName.equalsIgnoreCase(ReqFunctionOverData.avgFuncAbbr))
        {
            retStr = ReqFunctionOverData.avgFunc;
        } else if(dbFuncName.equalsIgnoreCase(ReqFunctionOverData.maxFuncAbbr)){
            retStr = ReqFunctionOverData.maxFunc;
        }   else if(dbFuncName.equalsIgnoreCase(ReqFunctionOverData.minFuncAbbr)){
            retStr = ReqFunctionOverData.minFunc;
        }   else if(dbFuncName.equalsIgnoreCase(ReqFunctionOverData.lastValFuncAbbr)){
            retStr = ReqFunctionOverData.lastValFunc;
        }   else if(dbFuncName.equalsIgnoreCase(ReqFunctionOverData.histValFuncAbbr)){
            retStr = ReqFunctionOverData.histValFunc;
        }  else if(dbFuncName.equalsIgnoreCase(ReqFunctionOverData.setValFuncAbbr)){
            retStr = ReqFunctionOverData.setValFunc;
        }
        return retStr;
    }

    /**
     * creates a node level function referring to the unique id function given in the second argument (if it is a max function, the second argument shows over what function the max is applied (eg. last value, min value (it could be another gateway level function) )
     * @param pFuncName  a valid requested function name (the valid ones are defined in the class ReqFunctionOverData
     * @param refUniqueFunctId   the unique id of the function referenced by the gateway level function name in the first argument
     * @return a calculated name with separators showing the gateway level function and the referenced function's unique id.
     *
     */
    /*
    private String makeNodeFuncNameThatRefersTo(String pFuncName, int refUniqueFunctId){

        StringBuilder retStrBld = new StringBuilder();
        retStrBld.append(ReqFunctionOverData.NODE_LEVEL_PREFIX);
        retStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        retStrBld.append(pFuncName);
        retStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        retStrBld.append(refUniqueFunctId);
        return retStrBld.toString();
    } */

    /**
     * creates a gateway level functions referring to the unique id function given in the second argument (if it is a max function, the second argument shows over what function the max is applied (eg. last value, min value (it could be another gateway level function) )
     * @param pFuncName  a valid requested function name (the valid ones are defined in the class ReqFunctionOverData
     * @param refUniqueFunctId   the unique id of the function referenced by the gateway level function name in the first argument
     * @return a calculated name with separators showing the gateway level function and the referenced function's unique id.
     *
     */
    private String makeGwFuncNameThatRefersTo(String pFuncName, int refUniqueFunctId){

        StringBuilder retStrBld = new StringBuilder();
        retStrBld.append(ReqFunctionOverData.GW_LEVEL_PREFIX);
        retStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        retStrBld.append(pFuncName);
        retStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        retStrBld.append(refUniqueFunctId);

        return retStrBld.toString();
    }

    /**
     * To be called after all the prefixes and suffixes are added
     * @param pFuncName
     * @param funcNameToNumberOfInstancesUsed
     * @return the string with an extra (incremented) suffix (eg _2) if the function is used more than one times
     */
    private String makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(String pFuncName, HashMap<String, Integer> funcNameToNumberOfInstancesUsed) {
        StringBuilder retStrBld = new StringBuilder();
        retStrBld.append(pFuncName);
        String pFuncNameEssential = "";
        String[] descriptionTokens = pFuncName.split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
        if(descriptionTokens !=null && descriptionTokens.length > 2 && !descriptionTokens[1].isEmpty()) {
            pFuncNameEssential = descriptionTokens[1];
        }  else {
            pFuncNameEssential = pFuncName;
        }
        if(funcNameToNumberOfInstancesUsed.containsKey(pFuncNameEssential)) {
            Integer noOfInstances = funcNameToNumberOfInstancesUsed.get(pFuncNameEssential);
            noOfInstances ++;
            funcNameToNumberOfInstancesUsed.put(pFuncNameEssential, noOfInstances);
            retStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
            retStrBld.append(noOfInstances);
        } else {
            funcNameToNumberOfInstancesUsed.put(pFuncNameEssential, 1);
        }
        return retStrBld.toString();

    }
    /**
     * Adds a new Query Definition to the IndexOfQueries. To be used when deploying a VSN (Composed Service).
     * @param composedServiceIDinDB
     * @return null if no service definition was found, or if the creation failed (due to insufficient data, or badly stored data about the service)
     */
    synchronized public QueryDefinition addNewQueryDef(UserNode unPeer, int composedServiceIDinDB)
    {
        HashMap<String, Integer> funcNameToNumberOfInstancesUsed = new HashMap<String, Integer>();
        logger.debug("QueryDefinition - add new composite Query () - Start");
        QueryDefinition tmpQDef = null;

        boolean pAsynchFlagSet=false;
        boolean pContFlagSet=false;
        boolean pDtnFlagSet=false;
        boolean pEncryptFlagSet=false;
        boolean pSecurityFlagSet=false;

        // legacy support if we get ony these arguments, then we need to build the new schema
        Integer uniqIndexOfSelectedDevs = 1; //start from 1
        Integer uniqIndexOfSelectedCaps = 1; //start from 1
        Integer uniqueFunctionIdAssign = 1; // the unique ids for the functions begin from 1

        final Integer LASTVAL_UID = 1;
        final Integer AVG_UID = 2;
        final Integer MIN_UID = 3;
        final Integer MAX_UID = 4;

        boolean haveConsideredALatestValFunctionOnce = false;    //todo: for use later (which functions to ignore)

        String candidateQueryDefID = "0022"; // will be replaced
        // here we have the uId and we create the new QueryDefinition
        HashMap<Integer, Vector<GeoRegion>> pAreasSelectionHM = new HashMap<Integer, Vector<GeoRegion>> ();
        HashMap<Integer, HashMap<String, Vector<String> > > pDevicesSelectionHM = new HashMap<Integer, HashMap<String, Vector<String> > >();
        HashMap<Integer, HashMap<String, Vector<Integer>> > pCapabilitiesSelectionHM = new HashMap<Integer, HashMap<String, Vector<Integer>> >();
        HashMap<Integer, Integer> pSelDevsToSelCaps = new HashMap<Integer, Integer>();
        //
        Vector<ReqFunctionOverData> uniqueFunctionVec = new Vector<ReqFunctionOverData>();
        // HashMap<String, Boolean> genCapQuerriedToHasLastValueFuncHM = new HashMap<String, Boolean>(); //todo: for use later (which functions to ignore)

        HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedfuncVecHM = new HashMap<String, Vector<ReqFunctionOverData>>();
        HashMap<String, Vector<Integer>> genCapQuerriedTolistOfUniqueFuncIds = new HashMap<String, Vector<Integer>>();

        //HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedGwLevelfuncVecHM = new HashMap<String, Vector<ReqFunctionOverData>>();

        // A function to be used (and reused is the last value function) so we set it always as constant in the vector of unique functions with id 1
        // TODO: later we can go through all functions and if none wants explicitly the last value for any capability, we could add an additional field to command the gateway to not return the last value of the sensor back.
        // TODO: Same stands for every aggregate function over last values (so gwLevel, referencing the first function).
        //
        String nameOfFunc = "";
        /*
        nameOfFunc = makeNodeFuncNameThatRefersTo(ReqFunctionOverData.lastValFunc, LASTVAL_UID);
        nameOfFunc =  makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(nameOfFunc, funcNameToNumberOfInstancesUsed);
        */
        nameOfFunc = ReqFunctionOverData.lastValFunc;
        uniqueFunctionVec.addElement(new ReqFunctionOverData(nameOfFunc,LASTVAL_UID, null, null));
        nameOfFunc = makeGwFuncNameThatRefersTo(ReqFunctionOverData.avgFunc, LASTVAL_UID);
        nameOfFunc = makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(nameOfFunc, funcNameToNumberOfInstancesUsed);
        uniqueFunctionVec.addElement(new ReqFunctionOverData(nameOfFunc,AVG_UID, null, null));
        nameOfFunc = makeGwFuncNameThatRefersTo(ReqFunctionOverData.minFunc, LASTVAL_UID);
        nameOfFunc = makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(nameOfFunc, funcNameToNumberOfInstancesUsed);
        uniqueFunctionVec.addElement(new ReqFunctionOverData(nameOfFunc,MIN_UID, null, null));
        nameOfFunc = makeGwFuncNameThatRefersTo(ReqFunctionOverData.maxFunc, LASTVAL_UID);
        nameOfFunc = makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(nameOfFunc, funcNameToNumberOfInstancesUsed);
        uniqueFunctionVec.addElement(new ReqFunctionOverData(nameOfFunc,MAX_UID, null, null));
        uniqueFunctionIdAssign = MAX_UID;
        uniqueFunctionIdAssign+=1;


        //logger.debug("QueryDefinition - add new composite Query () - Start 01");

        FullComposedService theStoredComposedService = null;
        try {
            AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
        theStoredComposedService = manager.getComposedService(composedServiceIDinDB)    ;
        }catch(Exception ex1)
        {
            theStoredComposedService = null;
        }

        if(theStoredComposedService != null)
        {
            pAsynchFlagSet=theStoredComposedService.isGlobalAsynchronousEnableRequest();
            pContFlagSet=theStoredComposedService.isGlobalContinuationEnableRequest();
            pDtnFlagSet=theStoredComposedService.isGlobalDTNEnableRequest();
            pEncryptFlagSet=theStoredComposedService.isGlobalEncryptionEnableRequest();

            List<ServiceInstance> partialServicesList = theStoredComposedService.getServiceInstanceList();

            if(partialServicesList != null)
            {
                //
                // Check if any valid queries can be made with the given user selections.
                // If not, avoid useless processing, returning null
                //

                // iterate through all Partial Services and configure a hashmap (or two) of capabilities to vector of functions (maybe unique?)
                AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                for (ServiceInstance partialServiceTmpIter : partialServicesList)
                {
                    ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(partialServiceTmpIter.getId());
                    // this capability is the stored one
                    logger.debug("QueryDefinition - add new composite Query () - BEFORE get OBSERVED CAPS!");
                    List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                    try{
                        storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                    }catch(Exception ex002)
                    {
                        storedCapsAndRulesList =null;
                    }
                    logger.debug("QueryDefinition - add new composite Query () - AFTER get OBSERVED CAPS!");
                    if(storedCapsAndRulesList != null)
                    {
                        AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();

                        for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                        {
                            Capability storedCapAndRules = abstractCapabilityManager.getCapability(storedCapAndRulesIterTmp.getId());
                            String currCapName = storedCapAndRules.getName();
                            if(!genCapQuerriedToSelectedfuncVecHM.containsKey(currCapName))  {
                                genCapQuerriedToSelectedfuncVecHM.put(currCapName, new Vector<ReqFunctionOverData>());
                            }
                            if(!genCapQuerriedTolistOfUniqueFuncIds.containsKey(currCapName))  {
                                genCapQuerriedTolistOfUniqueFuncIds.put(currCapName, new Vector<Integer>());
                            }

                            Vector<ReqFunctionOverData> tmpVecOfFuncs = new Vector<ReqFunctionOverData>(); //;genCapQuerriedToSelectedfuncVecHM.get(currCapName);
                            Vector<Integer> listOfUniqueFuncIds = new Vector<Integer>();// we don't get the existing, because a capability could have different functions for different sets of nodes, (not globally same) genCapQuerriedTolistOfUniqueFuncIds.get(currCapName);
                            // The tmpVecOfFuncs should be updated with the functions of this capability within the current partial service
                            String validFunctionName = getValidFunctionNameFromDBCapability(storedCapAndRules.getFunction());
                            if(validFunctionName != null) {

                                if(validFunctionName.equalsIgnoreCase(ReqFunctionOverData.avgFunc)
                                        || validFunctionName.equalsIgnoreCase(ReqFunctionOverData.maxFunc)
                                        || validFunctionName.equalsIgnoreCase(ReqFunctionOverData.minFunc))
                                {
                                    // if an aggregate function has no trigger  then it just references the lastVal function (which is fixed as having id: 1)
                                    int referencedAggrFunction = 0;
                                    if(ReqFunctionOverData.avgFunc.equalsIgnoreCase(validFunctionName))
                                    {
                                        referencedAggrFunction = AVG_UID;
                                    }else if(ReqFunctionOverData.minFunc.equalsIgnoreCase(validFunctionName))
                                    {
                                        referencedAggrFunction = MIN_UID;

                                    }else if(ReqFunctionOverData.maxFunc.equalsIgnoreCase(validFunctionName)){

                                        referencedAggrFunction = MAX_UID;
                                    }
                                    listOfUniqueFuncIds.add(referencedAggrFunction);

                                    if(storedCapAndRules.getHasTrigger().equalsIgnoreCase("yes")){
                                        // if an aggregate function has a trigger, then it needs a ifthen function  over the aggregate function (which in turn is over the lastval function)
                                        if(storedCapAndRules.getTriggerConditionSign()!=null && storedCapAndRules.getTriggerConditionSign().compareToIgnoreCase("gt")==0 &&
                                                storedCapAndRules.getTriggerConditionValue()!=null)
                                        {
                                            HashMap<String, String> threshHM = new HashMap<String, String>();
                                            threshHM.put( ThresholdStructure.THRESHOLD_LARGEROREQUAL, storedCapAndRules.getTriggerConditionValue());
                                            ThresholdStructure threshTmp = new ThresholdStructure(threshHM);

                                            String theNewIfThenName = makeGwFuncNameThatRefersTo(ReqFunctionOverData.ruleRuleIfThenFunc,referencedAggrFunction);
                                            theNewIfThenName = makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(theNewIfThenName,funcNameToNumberOfInstancesUsed);
                                            tmpVecOfFuncs.addElement(new ReqFunctionOverData(theNewIfThenName, ReqFunctionOverData.unknownFuncId, null,threshTmp));
                                        }
                                    }// no else needed since the max, min,avg value function are already in the request by default (they won't be actually calculated though if no node references them)!
                                }
                                else if (validFunctionName.equalsIgnoreCase(ReqFunctionOverData.lastValFunc) )
                                {
                                    if(storedCapAndRules.getHasTrigger().equalsIgnoreCase("yes")){
                                        if(storedCapAndRules.getTriggerConditionSign()!=null && storedCapAndRules.getTriggerConditionSign().compareToIgnoreCase("gt")==0 &&
                                                storedCapAndRules.getTriggerConditionValue()!=null)
                                        {
                                            HashMap<String, String> threshHM = new HashMap<String, String>();
                                            threshHM.put( ThresholdStructure.THRESHOLD_LARGEROREQUAL, storedCapAndRules.getTriggerConditionValue());
                                            ThresholdStructure threshTmp = new ThresholdStructure(threshHM);
                                            String theNewIfThenName = makeGwFuncNameThatRefersTo(ReqFunctionOverData.ruleRuleIfThenFunc,1);
                                            theNewIfThenName = makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(theNewIfThenName,funcNameToNumberOfInstancesUsed);
                                            tmpVecOfFuncs.addElement(new ReqFunctionOverData(theNewIfThenName, ReqFunctionOverData.unknownFuncId, null,threshTmp));
                                        }
                                    }
                                    // no else needed since the last value function is already requested by default!
                                }

                                // update vector of unique function Ids for this capability
                                boolean foundmatch;
                                int previousFuncInVectUid = 0;
                                for (int i = 0; i < tmpVecOfFuncs.size(); i++) {

                                    //pre-process for gwLevel functions that have 0 id as their referenced function!
                                    // This is a special case (for allowing gwLevel function to reference other gw Levle functions, or
                                    // not having min/max/avg functions by default in the definition (if we go this more dynamic route, we
                                    // would need to double check for duplicates of these functions)
                                    // TODO: we need to double check for duplicates either way!!
                                    if(previousFuncInVectUid!=0 && ReqFunctionOverData.isValidGatewayReqFunct( tmpVecOfFuncs.elementAt(i).getfuncName()))
                                    {
                                        //
                                        //get the referenced id and if zero replace with the previousFuncInVectUid
                                        String[] descriptionTokens = tmpVecOfFuncs.elementAt(i).getfuncName().split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                                        // if avg, min, max, then set the id to the first id of the uniqueFunctionVec
                                        //  descriptionTokens[0] -> gw_level_prefix
                                        //  descriptionTokens[1] -> name of function
                                        //  descriptionTokens[2] -> the ref unique id (or 0: special value)
                                        //
                                        if(descriptionTokens !=null && descriptionTokens.length > 2 && descriptionTokens[2].equalsIgnoreCase("0"))
                                        {    // this could produce an even higher suffix number (for multiple instances of same function name -erasing the previous suffix, but should be harmless)
                                            String theNewName = makeGwFuncNameThatRefersTo(descriptionTokens[1],previousFuncInVectUid );
                                            theNewName = makeFunctionNameUniqueAfterNormalPrefixAndSuffixesWereAdded(theNewName,funcNameToNumberOfInstancesUsed);
                                            tmpVecOfFuncs.elementAt(i).setfuncName(theNewName);
                                        }
                                    }
                                    // check for match inside the "global" uniqueFunctionVec, and update it if necessary
                                    foundmatch = false;
                                    for (int j = 0; j < uniqueFunctionVec.size() && (foundmatch == false); j++) {
                                        if (uniqueFunctionVec.elementAt(j).equals(tmpVecOfFuncs.elementAt(i))) {   // function object comparison
                                            foundmatch = true;
                                            int tmpFid = uniqueFunctionVec.elementAt(j).getfuncId();
                                            //check for match in the local (for this capability listOfUniqueFuncIds)
                                            if (!(listOfUniqueFuncIds.contains(Integer.valueOf(tmpFid)))) {
                                                listOfUniqueFuncIds.add(Integer.valueOf(tmpFid));
                                            }
                                            previousFuncInVectUid = tmpFid;
                                        }
                                    }
                                    if (!foundmatch) {     // if a function in the tmpVexOfFunctions for this capability is not found in the Vector of unique functions (globally), then add it there, and assign it with a unique id
                                        tmpVecOfFuncs.elementAt(i).setfuncId(uniqueFunctionIdAssign);
                                        uniqueFunctionVec.add(tmpVecOfFuncs.elementAt(i));
                                        listOfUniqueFuncIds.add(Integer.valueOf(uniqueFunctionIdAssign));
                                        previousFuncInVectUid = uniqueFunctionIdAssign;
                                        uniqueFunctionIdAssign += 1;
                                    }
                                } // end of for loop over vector of functions for this cap to filter out duplicates.
                            }  //end if (if we found a valid function name for this capability)
                            // WE add to all capabilities the 1rst (last val) function!
                            if(!listOfUniqueFuncIds.contains(1)){
                                listOfUniqueFuncIds.add(1);
                            }

                            // todo: this code could use some compression/elimination of redundancy
                            HashMap<String, Vector<Integer>> currCapToUniqFunctIDsHM = new HashMap<String, Vector<Integer>>();
                            currCapToUniqFunctIDsHM.put(storedCapAndRules.getName(), listOfUniqueFuncIds);
                            pCapabilitiesSelectionHM.put(storedCapAndRules.getId(), currCapToUniqFunctIDsHM);

                            // for now we only allow sets of nodes from the map, so we check this list (not the regions list)
                            List<DBSelectionOfSmartNodes>  dbSelectionOfSmartNodeses = storedCapAndRules.getDBSelectionOfSmartNodesList();
                            if(dbSelectionOfSmartNodeses!=null)
                            {
                                AbstractSelectionOfSmartNodesManager selectionOfSmartNodesManager =  AbstractSelectionOfSmartNodesManager.getInstance();
                                for (DBSelectionOfSmartNodes  dbSelectionOfNodesTmpIter: dbSelectionOfSmartNodeses)
                                {
                                    DBSelectionOfSmartNodes dbSelectionOfNodes = selectionOfSmartNodesManager.getSelectionOfSmartNodes(dbSelectionOfNodesTmpIter.getId());

                                    List<DBSmartNodeOfGateway> dbSmartNodeOfGatewayList = dbSelectionOfNodes.getDBSmartNodeOfGatewayList();
                                    if(dbSmartNodeOfGatewayList!=null)
                                    {
                                        pDevicesSelectionHM.put(uniqIndexOfSelectedDevs, new HashMap<String, Vector<String>>());
                                        HashMap<String, Vector<String>> gwToSmDevListHM= pDevicesSelectionHM.get(uniqIndexOfSelectedDevs);

                                        AbstractSmartNodeOfGatewayManager abstractSmartNodeOfGatewayManager =  AbstractSmartNodeOfGatewayManager.getInstance();
                                        for (DBSmartNodeOfGateway dbSmartNodeTmpIter: dbSmartNodeOfGatewayList)
                                        {
                                            DBSmartNodeOfGateway dbSmartNode =  abstractSmartNodeOfGatewayManager.getSmartNodeOfGateway(dbSmartNodeTmpIter.getId());
                                            String currgwName = dbSmartNode.getParentGateWay().getRegisteredName();
                                            String nodeId = dbSmartNode.getIdWithinGateway();
                                            if(!gwToSmDevListHM.containsKey(currgwName)){
                                                gwToSmDevListHM.put(currgwName, new Vector<String>());
                                            }
                                            Vector<String> smDevVec = gwToSmDevListHM.get(currgwName);
                                            smDevVec.addElement(nodeId);
                                        }

                                        pSelDevsToSelCaps.put(uniqIndexOfSelectedDevs, storedCapAndRules.getId());

                                        uniqIndexOfSelectedDevs+=1;
                                    }
                                }
                            }
                            List<DBSelectionOfGateways>  dbSelectionOfGatewayseses = storedCapAndRules.getDBSelectionOfGatewaysList();
                            //get gateways as unique selections too. For now, we don't check for overlaps and identical sets
                            if(dbSelectionOfGatewayseses!=null){
                                AbstractSelectionOfGatewaysManager selectionOfGatewaysManager =  AbstractSelectionOfGatewaysManager.getInstance();
                                for (DBSelectionOfGateways  dbSelectionOfGatewaysTmpIter: dbSelectionOfGatewayseses)
                                {
                                    DBSelectionOfGateways dbSelectionOfGateways = selectionOfGatewaysManager.getSelectionOfGateways(dbSelectionOfGatewaysTmpIter.getId());

                                    List<DBRegisteredGateway> dbRegisteredGatewayList = dbSelectionOfGateways.getDBRegisteredGatewayList();
                                    if(dbRegisteredGatewayList!=null)
                                    {
                                        pDevicesSelectionHM.put(uniqIndexOfSelectedDevs, new HashMap<String, Vector<String>>());
                                        HashMap<String, Vector<String>> gwToSmDevListHM= pDevicesSelectionHM.get(uniqIndexOfSelectedDevs);

                                        AbstractGatewayManager abstractGatewayManager =  AbstractGatewayManager.getInstance();
                                        for (DBRegisteredGateway dbRegGatewayTmpIter: dbRegisteredGatewayList)
                                        {
                                            DBRegisteredGateway dRegGateway =  abstractGatewayManager.getDBRegisteredGatewayByIncId(dbRegGatewayTmpIter.getIdregisteredGateway());
                                            if(dRegGateway!=null) {
                                            String currgwName = dRegGateway.getRegisteredName();
                                                String nodeId = QueryContentDefinition.selAllMotes;//special value, shows to ALL motes of gateway
                                                if(!gwToSmDevListHM.containsKey(currgwName)){
                                                    gwToSmDevListHM.put(currgwName, new Vector<String>());
                                                }
                                                Vector<String> smDevVec = gwToSmDevListHM.get(currgwName);
                                                smDevVec.addElement(nodeId);
                                            }
                                            else{
                                                logger.error("Could not acquire DB Registered name from DB! - Cannot create a valid query!");
                                                return null;

                                            }
                                        }
                                        pSelDevsToSelCaps.put(uniqIndexOfSelectedDevs, storedCapAndRules.getId());
                                        uniqIndexOfSelectedDevs+=1;
                                    }

                                }
                            }

                        }   // end for loop over capabilities for this partial service
                    } //if capabilities for this partial service are <> null
                    logger.debug("QueryDefinition - add new composite Query () - LOOP PARTIAL");
                }   //end for loop over all partial services


            }
            logger.debug("QueryDefinition - add new composite Query () - Start 02");

            // TODO: fix the selection hashmaps and selections sets before creating a new query content definition.
            // TODO: fix the names of capabilities to be the full (dca) urns. ...
            //  pAreasSelectionHM (nothing to do)
            //  pDevicesSelectionHM (HM: selection ID to gateways (gateway name to Vector [QueryContentDefinition.selAllMotes ] or specific nodes  HashMap<String, Vector<String> >  gatewayname -> vector of sensor IDs.
            //                              selection ID should get the associated selections from the capabilities entry.
            //                              and the hashmap will be gateway name to Vector of sensor Ids
            //  pCapabilitiesSelectionHM (HM: Capabilities selection ID to HashMap<String, Vector<Integer>> (CapName to Vector of Unique function Ids)
            //                              for each capability of partial service we get its ID (the one stored in the DB could do) for the integer key (selection ID -singular)
            //                                  the HM would have only one key, and the Vector of unique Funct Ids for that capability
            //
            //  pSelDevsToSelCaps  (HM: selection ID to capabilities selection ID). In the DB, each capability can be connected to set of sensors, set of areas and set of gateways (currently sensors).
            //
            QueryContentDefinition qContent = new QueryContentDefinition(pAreasSelectionHM, pDevicesSelectionHM, pCapabilitiesSelectionHM,pSelDevsToSelCaps,uniqueFunctionVec);

            if ((tmpQDef = checkforGivenQueryContent(qContent)) != null) {
                // TODO: add to logger (debug)
                System.out.println("This query has already been submitted!");
                // (To do) should throw some kind of exception here!!!
                return tmpQDef;
            }

            StringBuilder candidateQueryIdStrBld = new StringBuilder();
            candidateQueryIdStrBld.append(COMPOSED_DB_PREFIX);
            candidateQueryIdStrBld.append(composedServiceIDinDB);
            candidateQueryDefID = candidateQueryIdStrBld.toString();

            logger.debug("QueryDefinition - add new composite Query () - Start 03");

            tmpQDef = new QueryDefinition(candidateQueryDefID, qContent);
            tmpQDef.setAsynchronousFlag(pAsynchFlagSet);
            tmpQDef.setContinuationEnabledFlag(pContFlagSet);
            tmpQDef.setDtnEnabledFlag(pDtnFlagSet);
            tmpQDef.setEncryptionEnabledFlag(pEncryptFlagSet);
            tmpQDef.setSecurityEnabledFlag(pSecurityFlagSet);
            this.queriesDefHM.put(candidateQueryDefID, tmpQDef);
            logger.debug("QueryDefinition - add new composite Query () - End");

        }

        return tmpQDef;
    }
    
    // the final function vector is for gateway level functions
    synchronized public QueryDefinition addNewQueryDefPreDep(String predeployedServiceId, UserNode unPeer, Vector<GeoRegion> rangeVec, HashMap<String, Vector<String>> gateIdToSelectedMotesVecHM, HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedfuncVecHM, HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedGwLevelfuncVecHM) {

        //
        // Check if any valid queries can be made with the given user selections.
        // If not, avoid useless processing, returning null        
        //
        if (!checkInputValid(unPeer, rangeVec, gateIdToSelectedMotesVecHM, genCapQuerriedToSelectedfuncVecHM)) {
            return null;
        }
        //
        // Check if Query exists with the same content. If it does then
        // return the existing Definition to the calling method. 
        // (To do) (++++) However the calling method should check if this is set to be periodically issued 
        // and if so then it won't do nothing. It will expect that the original instance of this query is already being issued 
        // periodically! The original instances of the queries should be issued/initiated once at the UserPeer startup (when
        // it is ready to submit queries!!!
        // What could also be done is to delete the current running periodic query and reissue it (or gracefully kill the pertinent Thread).
        //          

        //
        // Analyze the hashmap of generic capabilities to Functions and get the FunctionVector (with only the distinct functions (identical functions are merged) and with a unique id per function),
        //         and also a hashmap of generic capabilities to Function unique Ids.
        //
        HashMap<String, Vector<Integer>> genCapQuerriedTofuncIDVecHM = new HashMap<String, Vector<Integer>>();
        Vector<ReqFunctionOverData> uniqueFunctionVec = new Vector<ReqFunctionOverData>();

        Set<String> stKeysOfCapsHM = genCapQuerriedToSelectedfuncVecHM.keySet();
        Iterator<String> itOnKeysOfCapsHM = stKeysOfCapsHM.iterator();
        int uniqueFunctionIdAssign = 1; // the unique ids for the functions begin from 1
        boolean foundmatch = false;
        while (itOnKeysOfCapsHM.hasNext()) {
            String tmpGenCapName = itOnKeysOfCapsHM.next();
            Vector<ReqFunctionOverData> tmpFuncVec = genCapQuerriedToSelectedfuncVecHM.get(tmpGenCapName);
            Vector<Integer> listOfUniqueFuncIds = new Vector<Integer>();
            // for each element in this temporary vector, if it exists inside the uniqueFunctionVec then
            // don't do anything with it. If it does not then append it in the uniqueFunctionVec vector.
            //
            for (int i = 0; i < tmpFuncVec.size(); i++) {
                // check for match inside the "global" uniqueFunctionVec, and update it if necessary
                foundmatch = false;
                for (int j = 0; j < uniqueFunctionVec.size() && (foundmatch == false); j++) {
                    if (uniqueFunctionVec.elementAt(j).equals(tmpFuncVec.elementAt(i))) {
                        foundmatch = true;
                        int tmpFid = uniqueFunctionVec.elementAt(j).getfuncId();
                        //check for match in the local (for this capability listOfUniqueFuncIds) 
                        if (!(listOfUniqueFuncIds.contains(Integer.valueOf(tmpFid)))) {
                            listOfUniqueFuncIds.add(Integer.valueOf(tmpFid));
                        }
                    }
                }
                if (!foundmatch) {
                    tmpFuncVec.elementAt(i).setfuncId(uniqueFunctionIdAssign);
                    uniqueFunctionVec.add(tmpFuncVec.elementAt(i));
                    listOfUniqueFuncIds.add(Integer.valueOf(uniqueFunctionIdAssign));
                    uniqueFunctionIdAssign += 1;
                }
            }
            genCapQuerriedTofuncIDVecHM.put(tmpGenCapName, listOfUniqueFuncIds);
        }
        //TODO: probably more checks should be done for the gwLevelFunctVector, but for now, just pass it to the contentDefinition...
        //TODO: probably the gwLevelFunctVector, should be also considered in the query similarity check (Existing Query check -> checkforGivenQueryContent)
        // assign ids to gw level functions AFTER all the node level functions!
        int uniqueVectorSizeOfNodeLevelFunctions = uniqueFunctionVec.size();
        if(genCapQuerriedToSelectedGwLevelfuncVecHM!=null)
        {
            Set<String> stKeysOfCapsHM_forGWLevel = genCapQuerriedToSelectedGwLevelfuncVecHM.keySet();
            Iterator<String> itOnKeysOfCapsHM_forGWLevel = stKeysOfCapsHM_forGWLevel.iterator();
            foundmatch = false;
            // loop on capabilities
            while (itOnKeysOfCapsHM_forGWLevel.hasNext()) {
                String tmpGenCapName = itOnKeysOfCapsHM_forGWLevel.next();
                Vector<ReqFunctionOverData> tmpFuncVec = genCapQuerriedToSelectedGwLevelfuncVecHM.get(tmpGenCapName);
                Vector<Integer> listOfUniqueFuncIds = genCapQuerriedTofuncIDVecHM.get(tmpGenCapName);
                // for each element in this temporary vector, if it exists inside the uniqueFunctionVec then
                // don't do anything with it. If it does not then append it in the uniqueFunctionVec vector.
                //
                //loop on the functions of Gateway Level for this capability
                for (int i = 0; i < tmpFuncVec.size(); i++) {
                    // check for match inside the "global" uniqueFunctionVec, and update it if necessary
                    foundmatch = false;
                    for (int j = 0; j <  uniqueFunctionVec.size() && (foundmatch == false) ; j++) { //not the fixed uniqueVectorSizeOfNodeLevelFunctions
                        if (uniqueFunctionVec.elementAt(j).equals(tmpFuncVec.elementAt(i))) {
                            foundmatch = true;
                            int tmpFid = uniqueFunctionVec.elementAt(j).getfuncId();
                            //check for match in the local (for this capability listOfUniqueFuncIds) 
                            if (!(listOfUniqueFuncIds.contains(Integer.valueOf(tmpFid)))) {
                                listOfUniqueFuncIds.add(Integer.valueOf(tmpFid));
                            }
                        }
                    }
                    if (!foundmatch) {
                        ReqFunctionOverData tmpGwFunct = tmpFuncVec.elementAt(i);
                        tmpGwFunct.setfuncId(uniqueFunctionIdAssign);
                        String[] descriptionTokens = tmpGwFunct.getfuncName().split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                        // if avg, min, max, then set the id to the first id of the uniqueFunctionVec
                        //  descriptionTokens[0] -> gw_level_prefix
                        //  descriptionTokens[1] -> name of function
                        //  descriptionTokens[2] ->   a random id (it does not matter),           both this and the next should be enough differentiate the functions if we need them to be different.
                        //  descriptionTokens[3] (if exists) ->   a random id (it does not matter)

                        // TODO fix this
                        if(descriptionTokens !=null && descriptionTokens.length > 2 && uniqueVectorSizeOfNodeLevelFunctions > 0 &&
                                ( descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.maxFunc) || 
                                descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.minFunc)   || 
                                descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.avgFunc) ))
                        {
                            StringBuilder tmpStrBld = new StringBuilder();
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_PREFIX);
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                            tmpStrBld.append(descriptionTokens[1]);  //name
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                            //tmpStrBld.append(uniqueFunctionVec.elementAt(0).getfuncId()); // the unique id to be applied on
                            tmpStrBld.append(listOfUniqueFuncIds.elementAt(0)); // the first unique id of THIS capability (not globally) to be applied on //TODO: NEW CHANGED!!!
                                                                                // this would normally take the simple function for the last value (or with a threshold and/or time span).
                            tmpGwFunct.setfuncName(tmpStrBld.toString());

                        }
                        //else if rule, then set the first id to first function and the second to second function.
                        else if(descriptionTokens !=null && descriptionTokens.length > 2 && uniqueVectorSizeOfNodeLevelFunctions > 1 &&
                                (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.ruleRuleBinaryAndFunc)) )
                        {
                            StringBuilder tmpStrBld = new StringBuilder();
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_PREFIX);
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                            tmpStrBld.append(descriptionTokens[1]);  //name
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                            //tmpStrBld.append(uniqueFunctionVec.elementAt(0).getfuncId()); // the unique id to be applied on
                            tmpStrBld.append(listOfUniqueFuncIds.elementAt(0)); // the first unique id of THIS capability (not globally) to be applied on //TODO: NEW CHANGED!!!
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                            //tmpStrBld.append(uniqueFunctionVec.elementAt(1).getfuncId()); //  the second unique id to be applied on
                            tmpStrBld.append(listOfUniqueFuncIds.elementAt(1)); // the second unique id of THIS capability (not globally) to be applied on //TODO: NEW CHANGED!!!
                            tmpGwFunct.setfuncName(tmpStrBld.toString());
                        }
                        else if(descriptionTokens !=null && descriptionTokens.length > 2 && uniqueVectorSizeOfNodeLevelFunctions > 1 &&
                                (descriptionTokens[1].equalsIgnoreCase(ReqFunctionOverData.ruleRuleIfThenFunc)) )
                        {
                            StringBuilder tmpStrBld = new StringBuilder();
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_PREFIX);
                            tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                            tmpStrBld.append(descriptionTokens[1]);  //name
                            // TODO: actually the gatewayFunc should get as referenced the global unique function that matches the first local unique function for this capability  )
                            // TODO: in the end, it should be more configurable (like retrieved from the split of the gw evel function name, which is now overrided)
                            // NOTE: the THEN clause is considered to always be "send a notification back to the VSP (so that the VSN Manager will decide what needs to happen, especially if it's a cross -gateway VSN application
                            //      or the VSN has some VSP side configuration parameters that should be checked also (eg. check auth/permissions to override an action or to command an action).
                            // TODO: possible optimization would be to detect if an action can be decided solely on the VGW.
                            //hackomatic todo: to delete later...
                            // if (uniqueFunctionVec.elementAt(0).getfuncName().equalsIgnoreCase(ReqFunctionOverData.setValFunc))
                            // NOTE:: we search to find the name of the function that is references with the 1rst unique function id for this capability
                            String tmpFuncNameToCompare = "";      //TODO: NEW CHANGED!!!
                            for(int ik=0; ik <uniqueFunctionVec.size(); ik++) //TODO: NEW CHANGED!!!
                            {
                                if(uniqueFunctionVec.elementAt(ik).getfuncId() == listOfUniqueFuncIds.elementAt(0))
                                {
                                    tmpFuncNameToCompare= uniqueFunctionVec.elementAt(0).getfuncName();
                                    break;
                                }

                            }
                            //
                            if(tmpFuncNameToCompare.equalsIgnoreCase(ReqFunctionOverData.setValFunc))  //TODO: NEW CHANGED!!!
                            {    //TODO this part needs changes. Can we have a set Value function referenced in the IF CLAUSE?
                                tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                                //tmpStrBld.append(uniqueFunctionVec.elementAt(1).getfuncId()); // the unique id for the if condition
                                tmpStrBld.append(listOfUniqueFuncIds.elementAt(0)); // should be 1, but we don't know is if has [1] index the unique id for the if condition //TODO: NEW CHANGED!!!
                                tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                                //tmpStrBld.append(uniqueFunctionVec.elementAt(0).getfuncId()); // the second unique id for the result when the condition is met!
                                tmpStrBld.append(listOfUniqueFuncIds.elementAt(0)); // the unique id for the if condition //TODO: NEW CHANGED!!!
                                
                            }
                            else
                            {
                                tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);                
                                //tmpStrBld.append(uniqueFunctionVec.elementAt(0).getfuncId()); // the unique id for the if condition
                                tmpStrBld.append(listOfUniqueFuncIds.elementAt(0)); // the unique id for the if condition //TODO: NEW CHANGED!!!
                                tmpStrBld.append(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
                                //tmpStrBld.append(uniqueFunctionVec.elementAt(1).getfuncId()); // // should be 1, but we don't know if it has [1] index the second unique id for the result when the condition is met!
                                tmpStrBld.append(listOfUniqueFuncIds.elementAt(0)); // the unique id for the if condition //TODO: NEW CHANGED!!!
                            }
                            
                            
                            tmpGwFunct.setfuncName(tmpStrBld.toString());
                        }
                        uniqueFunctionVec.add(tmpGwFunct);
                        listOfUniqueFuncIds.add(Integer.valueOf(uniqueFunctionIdAssign));
                        uniqueFunctionIdAssign += 1;
                    }
                }
                //genCapQuerriedTofuncIDVecHM.put(tmpGenCapName, listOfUniqueFuncIds); //not needed!
            }
        }
        
        
        QueryContentDefinition qContent = new QueryContentDefinition(rangeVec, gateIdToSelectedMotesVecHM, genCapQuerriedTofuncIDVecHM, uniqueFunctionVec);

        QueryDefinition tmpQDef;

        if ((tmpQDef = checkforGivenQueryContent(qContent)) != null) {
            System.out.println("This query has already been submitted!");
            // (To do) should throw some kind of exception here!!!
            return tmpQDef;
        }

        //
        // Create uId 
        // TODO: add a prefix to distinguish from services Stored in the DB
        String candidateQueryDefID = "0012";
        if(predeployedServiceId== null || predeployedServiceId.trim().isEmpty())
        {
            do {
                Random r = new Random();
                candidateQueryDefID = Long.toString(Math.abs(r.nextLong()), 36);
                candidateQueryDefID = PREDEPLOYED_PREFIX+  candidateQueryDefID;
            }
            while ((!queriesDefHM.isEmpty()) && (queriesDefHM.containsKey(candidateQueryDefID)));
        }
        else{
            candidateQueryDefID = PREDEPLOYED_PREFIX + predeployedServiceId;
        }
        // here we have the uId and we create the new QueryDefinition

        tmpQDef = new QueryDefinition(candidateQueryDefID, qContent);

        this.queriesDefHM.put(candidateQueryDefID, tmpQDef);

        return tmpQDef;
    }


    /**
     *
     * @param pQueryDefID
     * @return true if succeeded. Always check the return value for success or not.
     */
    synchronized public boolean removeQueryDef(String pQueryDefID)
    {
        boolean retVal = false;
        QueryDefinition tmpQDef;
        //first pause the query
        tmpQDef = this.queriesDefHM.get(pQueryDefID);
        if(tmpQDef != null)
        {
            tmpQDef.setRunningStatus(QueryDefinition.STATUS_PAUSED);
            //then remove it from the Hashmap
            // TODO: do we really want to avoid removing it while still running? Or does this not matter?
            while(true)
            {
                if(!QueryScheduler.getQueryScheduler().isQueryIdActivelyRunning(pQueryDefID))
                {
                    this.queriesDefHM.remove(pQueryDefID);
                    //TODO: do we handle the related results files somehow?
                    //TODO: we should probably call writeIndexBackToFile() when implemented!
                    retVal = true;
                    break;
                }
                else
                {
                    try{
                        Thread.currentThread().sleep(300);//sleep for predefined number of seconds before retrying
                    }
                    catch(InterruptedException ie){
                        //If this thread was interrupted by another thread
                        System.out.println(ie.getMessage());
                        break;
                    }
                }
            }
        }
        return retVal;
    }

    private QueryDefinition checkforGivenQueryContent(QueryContentDefinition qContent) {
        //
        // Scan through all keys of HashMap. If you find the same content return.
        // 
        Vector<String> st = new Vector<String>(this.queriesDefHM.keySet());
        QueryDefinition tmpDefinition = null;
        for (int i = 0; i < st.size(); i++) {
            tmpDefinition = this.queriesDefHM.get(st.elementAt(i));
            //System.out.println("Checking with "+Integer.toString(i));
            QueryContentDefinition tmpContent = tmpDefinition.getQContent();
            if (qContent.equals(tmpContent)) {
                //System.out.println("FOUND MATCH!"+Integer.toString(i));
                return tmpDefinition;
            }
        }
        return null;
    }
    
        /**
     * Checks if all the necessary selections are made (e.g. at least one button in each group)
     * and, if there are any (according to the selection), all the obligatory fields completed.
     * If not, returns false.
     *
     * @return true if the check is succesful, false otherwise
     */
    private boolean checkInputValid(UserNode unPeer, Vector<GeoRegion> areasVec, HashMap<String, Vector<String>> gateIdToSelectedMotesVecHM, HashMap<String, Vector<ReqFunctionOverData>> genCapQuerriedToSelectedfuncVecHM) {
        boolean selectedSomeAreas = false;
        boolean selectedSomeGateways = false;
        // Check for invalid input at the selection made on the big panel ("location" panel)
        if (areasVec == null) {
            selectedSomeAreas = false;
        } else {
            for (int i = 0; i < areasVec.size(); i++) {
                GeoRegion tmpArea = areasVec.get(i);
                if (tmpArea == null || tmpArea.getTypeMode() == GeoRegion.typeUnknownRegion) {
                    areasVec.removeElementAt(i); // remove invalid area definition
                    i--;
                }
            }
            if (areasVec.size() == 0)
                selectedSomeAreas = false;
        }

        if (gateIdToSelectedMotesVecHM == null ||
                (gateIdToSelectedMotesVecHM.containsKey(QueryContentDefinition.selUndefined) && gateIdToSelectedMotesVecHM.get(QueryContentDefinition.selUndefined).elementAt(0) == QueryContentDefinition.selUndefined)) {
            System.out.println("No gateway/motes selected.");
            selectedSomeGateways = false;
        } else {
            selectedSomeGateways = true;
        }

        if (!selectedSomeGateways && !selectedSomeAreas) {
            System.out.println("No valid area/gateway selection was made!");
            return false;
        }

        // Check if any sensor-types ("capabilities") are selected by the user
        if (genCapQuerriedToSelectedfuncVecHM == null ||
                genCapQuerriedToSelectedfuncVecHM.size() == 0 ||
                unPeer.getGatewaysToSmartDevsHM().size() == 0) {
            System.out.println("No generic Capability selected or no sensors available.");
            return false;
        }

        // Check if any function (i.e. last, max, etc.) is selected by the user per Capability
        Set<String> keysOfgCapToFunctions = genCapQuerriedToSelectedfuncVecHM.keySet();
        Iterator<String> it002 = keysOfgCapToFunctions.iterator();
        while (it002.hasNext()) {
            String tmpgCapName = it002.next();
            Vector<ReqFunctionOverData> functionVec = genCapQuerriedToSelectedfuncVecHM.get(tmpgCapName);
            if (functionVec == null ||
                    functionVec.elementAt(0).getfuncName().equals(QueryContentDefinition.selUndefined) ||
                    functionVec.elementAt(0).getfuncName().equals(ReqFunctionOverData.unknownFunc)) {
                System.out.println("No function selected for capability: " + tmpgCapName);
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the tag name for the Query Definition element of the XML representation of th Index of Queries
     *
     * @return the tag name for the Query Definition element of the XML.
     */
    public static String getQueryDefTag() {
        return queryDefTag;
    }
    

    
}
