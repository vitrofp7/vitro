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
 * FinalResultEntryPerDef.java
 *
 */

package vitro.vspEngine.service.query;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.apache.log4j.Logger;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.service.common.ConfigDetails;
import vitro.vspEngine.service.common.abstractservice.AbstractCapabilityManager;
import vitro.vspEngine.service.common.abstractservice.AbstractComposedServiceManager;
import vitro.vspEngine.service.common.abstractservice.AbstractObservationManager;
import vitro.vspEngine.service.common.abstractservice.AbstractServiceManager;
import vitro.vspEngine.service.common.abstractservice.model.Capability;
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This class is for objects that represent the final result per issue of a Query Definition in the p2p Network
 * It essentially merges into one "record" or optionally one file (for persistence), all the partial replies
 * received from the network for a particular issue (session) of a Query definition.
 * <p/>
 * The early implementation of this class is roughly equal to a HashMap of GatewayPeerIds to a Vector of ReqResultsOverData
 * <p/>
 * (To do) (++++) The code with the double hM (temporary and real) should be fixed to be optimal (keep only one hashmap).
 * for now this will remain as it is. The HMs are cleared after their use (at specific points) so as not to burden the heap when they are not needed any more.
 *
 * @author Antoniou
 */
public class FinalResultEntryPerDef {
    private static Logger logger = Logger.getLogger(FinalResultEntryPerDef.class);

    /**
     * Creates a new instance of FinalResultEntryPerDef
     */
    private HashMap<String, Vector<ReqResultOverData>> temporary_gatewaysToTheirResultsHM; // accessed by the application code. 
    private HashMap<String, Vector<ReqResultOverData>> real_gatewaysToTheirResultsHM; // (accesed by the backend code) it is used in Add to Result file and write back to file.

    private Timestamp creationDate;
    private String filePath;

    private String queryDefUId;

    public FinalResultEntryPerDef() {
        setQueryDefUId(""); // new: to connect with query (VSN) definitions
        temporary_gatewaysToTheirResultsHM = new HashMap<String, Vector<ReqResultOverData>>();
        real_gatewaysToTheirResultsHM = new HashMap<String, Vector<ReqResultOverData>>();
        filePath = "";
        GregorianCalendar calendar = new GregorianCalendar();
        long currentTime = calendar.getTimeInMillis();
        this.creationDate = new java.sql.Timestamp(currentTime);
    }


    /**
     * (To do) (++++) Here we have also skipped the DataTypeAdapter part. In the future it should find its way back to the code!!!
     */
    synchronized public void addPartialResult(PublicResponseAggrMsg partialResp) {
        //
        // Get the GateWayId from the partialResp. Check if there is an entry for this gateway in the HashMap
        // If there is not create one, then create a Vector of ReqResultOverData for it and put the Vector<ReqResultOverData>
        // part of the PublicResponseAggrMsg in there.
        //
        String prGatewayId = partialResp.getResponderPeerID();
        if (!this.real_gatewaysToTheirResultsHM.containsKey(prGatewayId)) {
            // If there is not a previous entry create one, then create a Vector of ReqResultOverData for it and put the Vector<ReqResultOverData>
            // part of the PublicResponseAggrMsg in there.
            // System.out.println("No match was found for "+ prGatewayId);
            Vector<ReqResultOverData> tmpReqResuVec = partialResp.getAllValuesVec();
            Vector<RespServContinuationReplacementStruct> continuationInfoVec = partialResp.getServiceContinuationList();
            this.real_gatewaysToTheirResultsHM.put(prGatewayId, tmpReqResuVec);

            writeObservationsToDB(prGatewayId, tmpReqResuVec, continuationInfoVec);

        } else {
            //
            // if there is a previous entry for the gateway in the HashMap, then go to each of the entries in the mapped Vector<ReqResultOverData>.
            //
            // System.out.println("Yes! Match was found for "+ prGatewayId);
            Vector<ReqResultOverData> prReqResuVec = partialResp.getAllValuesVec();
            Vector<RespServContinuationReplacementStruct> continuationInfoVec = partialResp.getServiceContinuationList();
            Vector<ReqResultOverData> mytmpReqResuVec2 = this.real_gatewaysToTheirResultsHM.get(prGatewayId);
            for (int i = 0; i < prReqResuVec.size(); i++) {
                /*
                * This compares the ids of functions
                */
                int prFuncId = prReqResuVec.elementAt(i).getFid();
                boolean matchisfound = false;
                for (int j = 0; j < mytmpReqResuVec2.size(); j++) {
                    int myTmpFuncId = mytmpReqResuVec2.elementAt(j).getFid();
                    if (prFuncId == myTmpFuncId) {
                        // System.out.println("Yes! Match was found for function "+ myTmpFuncDesc);                        
                        //
                        // If in the mapped Vector<ReqResultOverData> there already exists an entry for a function in the partialResp' Vector<ReqResultOverData>
                        // then get the associated  Vector<ResultAggrStruct> and search for matching mid/sids for each mid/sid in the partialResp. 
                        // (To do) Careful here with the matches. If wildcard -1 is implemented for moteids !!!!
                        //        
                        //System.out.println("Merging with other ReqResultOverData");
                        mytmpReqResuVec2.elementAt(j).mergeWith(prReqResuVec.elementAt(i));
                        matchisfound = true;
                        break; // the inner loop, and continue searching for other function matches.
                    }
                }
                //
                // If we reach here with matchisfound false we have the following case scenario:
                // If in the FinalResultEntry's Vector<ReqResultOverData> there is no entry for a function in the partialResp'  Vector<ReqResultOverData>, then create an entry and put
                // the corresponding entry of the partialResp' Vector<ReqResultOverData> there.
                //
                if (matchisfound == false) {
                    // System.out.println("No match was found for function "+ prReqResuVec.elementAt(i).funct_desc);
                    mytmpReqResuVec2.addElement(prReqResuVec.elementAt(i));
                }
            }
            if(prReqResuVec!=null && prReqResuVec.size()>0 )
            {
                // in any case, we write all the observations to the DB
                writeObservationsToDB(prGatewayId, prReqResuVec, continuationInfoVec);
            }
        }

        // Update creation Date with the current timestamp 
        GregorianCalendar calendar = new GregorianCalendar();
        long currentTime = calendar.getTimeInMillis();
        this.creationDate = new java.sql.Timestamp(currentTime);

    }


    /**
     *
     * @param pQueryDefUId  the unique id for this query definition
     * @param prFuncId the unique id (code) for the requested function
     * @return          null if not found. The string of the code otherwise
     */
    public static String findSampleCapabilityHashByFunctId(String pQueryDefUId, int prFuncId){
        String retCapHash = null;
        QueryDefinition thisQueryDef = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(pQueryDefUId);
        HashMap<Integer, HashMap<String, Vector<Integer>>> capIdToHashMapCapStringToFuidsHM =  thisQueryDef.getQContent().getCapabilitiesSelectionHM();

        boolean matchFound = false;
        Set<Integer> stKeysOfCapsHM = capIdToHashMapCapStringToFuidsHM.keySet();
        Iterator<Integer> itOnKeysOfCapsHM = stKeysOfCapsHM.iterator();
        String tmpCapName = "";
        int tmpCapDBId;
        while (itOnKeysOfCapsHM.hasNext()   && !matchFound ) {
            tmpCapDBId =   itOnKeysOfCapsHM.next();

            HashMap<String, Vector<Integer>> capStringToFuidsHM =  capIdToHashMapCapStringToFuidsHM.get(tmpCapDBId);
            Set<String> stKeysOfCapsToFuidHM = capStringToFuidsHM.keySet();

            Iterator<String> itOnKeysOfCapsToFuidHM = stKeysOfCapsToFuidHM.iterator();
            while (itOnKeysOfCapsToFuidHM.hasNext()  && !matchFound  ) {
                tmpCapName = itOnKeysOfCapsToFuidHM.next();

                // Check if the given function id belongs (applies) to this capability
                if((capStringToFuidsHM.get(tmpCapName).contains(prFuncId)))
                {
                    retCapHash = vitro.vspEngine.logic.model.Capability.getASensorModelFromName(tmpCapName);
                    matchFound = true;
                    break;
                }
            }
        }

        return retCapHash;
    }
    /**
     *
     * @param pQueryDefUId  the unique id for this query definition
     * @param prFuncId the unique id for the requested function
     * @param pHashOfCap the hash digest of the capability (the "sensor serial id")
     * @return          -1 if not found. >0 otherwise
     */

    public static int findCapabilityIDInQueryDefByFunctIdAndHashOfCap(String pQueryDefUId, int prFuncId, String pHashOfCap) {

        QueryDefinition thisQueryDef = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(pQueryDefUId);
        HashMap<Integer, HashMap<String, Vector<Integer>>> capIdToHashMapCapStringToFuidsHM =  thisQueryDef.getQContent().getCapabilitiesSelectionHM();

        int tmpCapDBId = -1;
        String tmpCapName = "";
        boolean matchFound = false;
        Set<Integer> stKeysOfCapsHM = capIdToHashMapCapStringToFuidsHM.keySet();
        Iterator<Integer> itOnKeysOfCapsHM = stKeysOfCapsHM.iterator();
        while (itOnKeysOfCapsHM.hasNext() && !matchFound) {
            tmpCapDBId =   itOnKeysOfCapsHM.next();
            tmpCapName = "";

            HashMap<String, Vector<Integer>> capStringToFuidsHM =  capIdToHashMapCapStringToFuidsHM.get(tmpCapDBId);
            Set<String> stKeysOfCapsToFuidHM = capStringToFuidsHM.keySet();

            Iterator<String> itOnKeysOfCapsToFuidHM = stKeysOfCapsToFuidHM.iterator();
            while (itOnKeysOfCapsToFuidHM.hasNext() && !matchFound) {
                tmpCapName = itOnKeysOfCapsToFuidHM.next();
                // Check if the capname is the same for the given hash !!!
                String tmpCapNameNoPrefix = tmpCapName.replaceAll(Pattern.quote(vitro.vspEngine.logic.model.Capability.dcaPrefix),"" );
                String capNameForSid = vitro.vspEngine.logic.model.Capability.getNameFromSensorModel(pHashOfCap);

                //Integer thedigestInt = tmpCapNameNoPrefix.hashCode();
                //if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);
                //String tmpSensorID = thedigestInt.toString();

                // Check also if the given function id belongs (applies) to this capability
                if(tmpCapNameNoPrefix.equalsIgnoreCase(capNameForSid) && (capStringToFuidsHM.get(tmpCapName).contains(prFuncId)))
                {
                    matchFound = true;
                    break;
                }
            }
        }
        return tmpCapDBId;
    }

    //
    // retrieve CapabilityID by query definition id, Function UId (in the content definition) and hash of capability) and hasOfCap (which is the sid of the query result!)
    //          the capabilityId is retrieved from the query Content where it is explicitly stored when creating the content definition from the DB.
    //
    public static Capability  findCapabilityDBByFunctIdAndHashOfCap(String pQueryDefUId, int prFuncId, String pHashOfCap, String gwId, String sensorId, boolean isNotification, List<String> refFunctNameLst, List<Boolean> refIsDefinitionFunctList){
        logger.debug("Searching Cap id db for " + pQueryDefUId + "::"+prFuncId + "::"+pHashOfCap);
        String refFunctNameFound = "";
        String pFuncNameEssential = "";
        QueryDefinition thisQueryDef = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(pQueryDefUId);
        int tmpCapDBId = -1;
        boolean matchFound = false;
        if(thisQueryDef!=null) {
            HashMap<Integer, HashMap<String, Vector<Integer>>> capIdToHashMapCapStringToFuidsHM =  thisQueryDef.getQContent().getCapabilitiesSelectionHM();


            String tmpCapName = "";
            refFunctNameFound = thisQueryDef.getQContent().getUniqueFunctionById(prFuncId).getfuncName();

            String[] descriptionTokens = refFunctNameFound.split(ReqFunctionOverData.GW_LEVEL_SEPARATOR);
            if(descriptionTokens !=null && descriptionTokens.length > 2 && !descriptionTokens[1].isEmpty()) {
                pFuncNameEssential = descriptionTokens[1];
            }  else {
                pFuncNameEssential = refFunctNameFound;
            }

            Set<Integer> stKeysOfCapsHM = capIdToHashMapCapStringToFuidsHM.keySet();
            Iterator<Integer> itOnKeysOfCapsHM = stKeysOfCapsHM.iterator();
            while (itOnKeysOfCapsHM.hasNext()   && !matchFound ) {
                tmpCapDBId =   itOnKeysOfCapsHM.next();
                tmpCapName = "";

                HashMap<String, Vector<Integer>> capStringToFuidsHM =  capIdToHashMapCapStringToFuidsHM.get(tmpCapDBId);
                Set<String> stKeysOfCapsToFuidHM = capStringToFuidsHM.keySet();

                Iterator<String> itOnKeysOfCapsToFuidHM = stKeysOfCapsToFuidHM.iterator();
                while (itOnKeysOfCapsToFuidHM.hasNext()  && !matchFound  ) {
                    tmpCapName = itOnKeysOfCapsToFuidHM.next();
                    // Check if the capname is the same for the given hash !!!


                    String tmpCapNameNoPrefix = tmpCapName.replaceAll(Pattern.quote(vitro.vspEngine.logic.model.Capability.dcaPrefix),"" );

                    //logger.debug("Cap DB id, Cap Name, Cap name no prefix :: " +Integer.toString(tmpCapDBId)+ "__" +tmpCapName + "__" +tmpCapNameNoPrefix) ;
                    String capNameForSid = vitro.vspEngine.logic.model.Capability.getNameFromSensorModel(pHashOfCap);
                    //Integer thedigestInt = tmpCapNameNoPrefix.hashCode();
                    //if (thedigestInt < 0) thedigestInt = thedigestInt * (-1);
                    //String tmpSensorID = thedigestInt.toString();

                    // Check also if the given function id belongs (applies) to this capability
                    //logger.debug("tmpSensorID ==  pHashOfCap -- hashCapName " + tmpSensorID + "::"+ pHashOfCap + "--" + vitro.vspEngine.logic.model.Capability.getNameFromSensorModel(pHashOfCap));
                    if(tmpCapNameNoPrefix.equalsIgnoreCase(capNameForSid) && ( (capStringToFuidsHM.get(tmpCapName).contains(prFuncId) ) || isNotification == true) )
                    {

                        //if(isNotification) {
                        //    logger.debug("NOTIFICATION!!!");
                        //}
                        HashMap<Integer, HashMap<String, Vector<String>>> selIdToGwToMoteIds = thisQueryDef.getQContent().getDevicesSelectionHM();
                        HashMap<Integer, Integer> SelDevsToSelCaps = thisQueryDef.getQContent().getSelDevsToSelCaps();
                        for(Integer keyMoteSelId : SelDevsToSelCaps.keySet()  ) {
                            if(!matchFound)   {
                                if(SelDevsToSelCaps.get(keyMoteSelId) == tmpCapDBId) {
                                    //then search in that vector to find the gw and node
                                    logger.debug("Searching for selection ID: " + Integer.toString(keyMoteSelId));
                                    if(selIdToGwToMoteIds.containsKey(keyMoteSelId)) {
                                        HashMap<String, Vector<String>> tmpgGwToMoteIds =  selIdToGwToMoteIds.get(keyMoteSelId);
    //                                    //debug
    //                                    for (String gwKeyName : tmpgGwToMoteIds.keySet()) {
    //                                        StringBuilder nodeNameTmpTmpBld = new StringBuilder();
    //
    //                                        for (String nodeNameTmpTmp : tmpgGwToMoteIds.get(gwKeyName) ) {
    //                                            nodeNameTmpTmpBld.append(nodeNameTmpTmp);
    //                                            nodeNameTmpTmpBld.append(",");
    //                                        }
    //                                        logger.debug("Gw KEY:::::: " + gwKeyName + " Nodes: " + nodeNameTmpTmpBld);
    //                                    }
    //                                    //end of debug

                                        if ((tmpgGwToMoteIds.containsKey(gwId) && tmpgGwToMoteIds.get(gwId).contains(sensorId)) ||
                                            ( tmpgGwToMoteIds.containsKey(gwId) && tmpgGwToMoteIds.get(gwId).contains(QueryContentDefinition.selAllMotes))
                                                )
                                        {
                                            matchFound = true;
                                            logger.debug("MATCH FOUND!!!");
                                        }
                                        else if(tmpgGwToMoteIds.containsKey(gwId) && ( isNotification && (sensorId==null || sensorId.trim().compareToIgnoreCase("") == 0) ) ){
                                            matchFound = true;
                                            logger.debug("MATCH FOUND!!! Notification case");
                                        }
                                    }
                                    else {
                                        logger.error("Error: (Nodes or Gateways) Selection ID does not exist in query definition");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(matchFound){
            Capability toReturnCap = AbstractCapabilityManager.getInstance().getCapability(tmpCapDBId);
            if(toReturnCap!=null && refIsDefinitionFunctList!=null && refIsDefinitionFunctList.size()>0 && !refFunctNameFound.isEmpty() && !pFuncNameEssential.isEmpty())
            {
                refFunctNameLst.clear();
                refFunctNameLst.add(0, refFunctNameFound);
                refFunctNameLst.add(1, pFuncNameEssential);
                if(pFuncNameEssential.compareToIgnoreCase(IndexOfQueries.getValidFunctionNameFromDBCapability(toReturnCap.getFunction())) == 0){
                    refIsDefinitionFunctList.clear();
                    refIsDefinitionFunctList.add(0, true);
                }
            }
            return toReturnCap;
        }
        return null;

    }

    //
    // retrieve a Service Instance by the capability ID (the actual id in the query definition is the id in the DB for the capability) and composite service ID
    //
    public static ServiceInstance  findPartialServiceDBByCapId(int pCompositeServiceId, int pCapId){
        //
        //get the service Instances belonging to this CompositeService.
        //
        boolean matchFound = false;
        int resultServiceInstanceId =0;
        FullComposedService theStoredComposedService = null;
        try {
            AbstractComposedServiceManager manager = AbstractComposedServiceManager.getInstance();
            theStoredComposedService = manager.getComposedService(pCompositeServiceId)    ;
        }catch(Exception ex1)
        {
            theStoredComposedService = null;
        }

        if(theStoredComposedService != null)
        {
            List<ServiceInstance> partialServicesList = theStoredComposedService.getServiceInstanceList();

            if(partialServicesList != null)
            {
                // iterate through all Partial Services and configure a hashmap (or two) of capabilities to vector of functions (maybe unique?)
                AbstractServiceManager partialSrvcManager =  AbstractServiceManager.getInstance();
                for (ServiceInstance partialServiceTmpIter : partialServicesList)
                {
                    if ( !matchFound ) {
                        resultServiceInstanceId = partialServiceTmpIter.getId();
                        //logger.debug("Partial Service id::" + Integer.toString(resultServiceInstanceId) + "to match capId::" + Integer.toString(pCapId));
                        ServiceInstance partialServiceTmp = partialSrvcManager.getServiceInstance(resultServiceInstanceId);
                        // this capability is the stored one
                        List<vitro.vspEngine.service.common.abstractservice.model.Capability> storedCapsAndRulesList = null;
                        try{
                            storedCapsAndRulesList =  partialServiceTmp.getObservedCapabilities() ;
                        }catch(Exception ex002)
                        {
                            storedCapsAndRulesList =null;
                        }
                        if(storedCapsAndRulesList != null)
                        {
                            AbstractCapabilityManager abstractCapabilityManager =  AbstractCapabilityManager.getInstance();

                            for (vitro.vspEngine.service.common.abstractservice.model.Capability storedCapAndRulesIterTmp: storedCapsAndRulesList)
                            {
                                //logger.debug("pCapId == storedCapId " + Integer.toString(pCapId) + ":==:" + Integer.toString(storedCapAndRulesIterTmp.getId()));
                                if ( !matchFound && pCapId == storedCapAndRulesIterTmp.getId())
                                {
                                    //logger.debug("FOUND MATCH::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                                    matchFound = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // get the service Instance (s) which have this capId associated with them
        // todo could it be a vector?

        if(matchFound)
        {
            return AbstractServiceManager.getInstance().getServiceInstance(resultServiceInstanceId);
        }
        else {
            return null;
        }
    }

    /**
     * Will wire every observation that is found in the Vector of results for this query definition, back to db (will detect Composite Service Id, Partial Service Id, and Capability Id
     */
    private void writeObservationsToDB(String prGatewayId, Vector<ReqResultOverData> tmpReqResuVec, Vector<RespServContinuationReplacementStruct> tmpContinuationInfoVec) {
        QueryDefinition thisQueryDef = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(this.getQueryDefUId());
        int pCompositeServiceId = -1;
        boolean predeployedFlag = false;
        if(thisQueryDef!=null)
        {
            // we need to match with CapabilityId (sid) and with ufid
            QueryContentDefinition tmpThisQContent = thisQueryDef.getQContent();
            if(tmpThisQContent!=null && !tmpThisQContent.isEmptyQueryContent())
            {

                for (int i = 0; i < tmpReqResuVec.size(); i++) {
                    int prFuncId = tmpReqResuVec.elementAt(i).getFid();
                    Boolean pGatewayLevelFunct = false;
                    Boolean pCrossGatewayLevelFunct = false;   //TODO: implement a way to detect these?
                    ReqFunctionOverData tmpCurrFunct = tmpThisQContent.getUniqueFunctionById(prFuncId);
                    if(tmpCurrFunct!=null)
                    {

                        pGatewayLevelFunct = ReqFunctionOverData.isValidGatewayReqFunct(tmpCurrFunct.getfuncName());
                        // Add the corresponding entries in the DB for persistence:
                        String pGatewayName =  prGatewayId;

                        Vector<ResultAggrStruct> tmpResStructVec = tmpReqResuVec.elementAt(i).getAllResultsforFunct();
                        //(WRONG NOTE, WONTDO) THIS FOR LOOP BRINGS ALSO OLD RESULTS? WHY DO WE ADD THE OBSERVATIONS HERE, WON'T THIS CREATE DUPLICATE ENTRIES IN THE DB?
                        for(int j=0; j< tmpResStructVec.size(); j++)
                        {
                            ResultAggrStruct tmpResultStruct = tmpResStructVec.elementAt(j);
                            if(tmpResultStruct!=null)
                            {

                                int pPartialServiceId=-1;
                                int pCapabilityId=-1;
                                String pResource = "";
                                String currentqeuryDefNoPrefix =  this.getQueryDefUId().replaceAll(Pattern.quote(IndexOfQueries.COMPOSED_DB_PREFIX), "");
                                currentqeuryDefNoPrefix =  currentqeuryDefNoPrefix.replaceAll(Pattern.quote(IndexOfQueries.PREDEPLOYED_PREFIX), "");

                                try {
                                    pCompositeServiceId = Integer.parseInt(currentqeuryDefNoPrefix);
                                }catch (Exception e22) {

                                }

                                String pSensorId = tmpResultStruct.getMid() ; //could be a wildcard (ResultAggrStruct.MidSpecialForAggregateMultipleValues ie "-1") if aggregate function for gateway
                                String pHashOfCap = tmpResultStruct.getSid();
                                String pReplacementId = "";
                                //if(!predeployedFlag ) {
                                // TODO: could this be a Vector of capabilities ? (in a many-to-many relationship)
                                // in the following method the parameters refFunctNameLst will be changed and contain the FunctionName for this observation (String), and the refIsDefinitionFunctList will contain the flag showing if this function is the one in the Capability definition (boolean).
                                List<String> refFunctNameLst = new  ArrayList<String>();
                                refFunctNameLst.add(0,""); //the funct name,  gateway level prefix and referencs in query definition
                                refFunctNameLst.add(1,""); //only the funct name
                                List<Boolean> refIsDefinitionFunctList = new  ArrayList<Boolean>();
                                refIsDefinitionFunctList.add(false);
                                Capability dbAssocCap = findCapabilityDBByFunctIdAndHashOfCap(this.getQueryDefUId(), prFuncId, pHashOfCap, pGatewayName, pSensorId, false, refFunctNameLst, refIsDefinitionFunctList );
                                if(dbAssocCap!=null) {
                                    pCapabilityId = dbAssocCap.getId();
                                    pResource = dbAssocCap.getName();
                                }

                                // TODO: could this be a Vector of Service Instances ? (in a many-to-many relationship)
                                ServiceInstance siTmp = findPartialServiceDBByCapId(pCompositeServiceId, pCapabilityId);
                                if(siTmp!=null) {
                                    pPartialServiceId = siTmp.getId();
                                }
                                //} else {
                                //    pCapabilityId =  findCapabilityIDInQueryDefByFunctIdAndHashOfCap(this.getQueryDefUId(), prFuncId, pHashOfCap);
                                //    pPartialServiceId =pCompositeServiceId;
                                //    pResource =    "placeholder"; // todo we could reverse the digest of the sid to get the description (as we do when we friendly print the results xml)
                                //}
                                int aggregatedSensorsNum = 1 ;
                                aggregatedSensorsNum = tmpResultStruct.getNumofAggrValues();

                                Date pTimestamp = new Date();
                                String pValueStr =tmpResultStruct.getVal() ;
                                float pValue = 0;
                                String pUom = "";

                                try {
                                    pValue=  Float.parseFloat(pValueStr);
                                }   catch (Exception e11)
                                {
                                    logger.error("Could not format observation value to Float: " + pValueStr);
                                }

                                if(tmpResultStruct.getTis()!=null && tmpResultStruct.getTis().timeperiod_from!=null )
                                {
                                    pTimestamp = new java.util.Date(tmpResultStruct.getTis().timeperiod_from.getTime());
                                }
                                else  {
                                    pTimestamp = new java.util.Date();
                                }

                                Date pReceivedTimestamp = new java.util.Date();
                                //
                                // check with continuation info:
                                // TODO: do we handle the aggregate results too? the continuation info is per sensor, so when -1 is stated, does this mean that a replaced sensor is included (or could it refer to another set <=probably the latter)
                                if(tmpContinuationInfoVec!=null && !tmpContinuationInfoVec.isEmpty()) {
                                    //
                                    logger.debug("CONTINUTATION SERVICE CHECK START::::::::::::::::::::::::::::::::::::::");

                                    logger.debug("CHECKING INTO CONT VECTOR FOR REPLACEMENT INFO");
                                    for(RespServContinuationReplacementStruct contInfoEntry: tmpContinuationInfoVec) {
                                        if( (contInfoEntry.getNodeSourceId().compareToIgnoreCase(pSensorId) == 0)
                                            && (contInfoEntry.getCapabilityId().compareToIgnoreCase(pHashOfCap) ==0) ) {
                                            logger.debug("FOUND ONE REPLACEMENT INFO");
                                            pReplacementId = contInfoEntry.getNodeReplmntId();
                                        }
                                    }
                                    logger.debug("CONTINUTATION SERVICE CHECK DONE::::::::::::::::::::::::::::::::::::::");
                                }
                                // Inside the loop here over all the results and also the corresponding IDs of capabilities, and partial Services
                                // we add the values to the Observations table in the DB.
                                String  pRefFunctionName;
                                Boolean pIsTheDefinitionFunction;
                                if(dbAssocCap!=null && siTmp!=null) {
                                    AbstractObservationManager.getInstance().createObservation(pPartialServiceId,
                                        pCapabilityId,
                                        pGatewayName,
                                        pSensorId,
                                        pReplacementId,
                                        pGatewayLevelFunct,
                                        pCrossGatewayLevelFunct,
                                        aggregatedSensorsNum,
                                        pResource,
                                        pTimestamp,
                                        pReceivedTimestamp,
                                        pValue,
                                        pUom,
                                        refFunctNameLst.get(0),
                                        refFunctNameLst.get(1),
                                        prFuncId,
                                        refIsDefinitionFunctList.get(0)
                                        );
                                }
                                else {
                                    logger.error("An invalid observation was found, with no associated capability or partial service!");
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * @return The filename of the Final result File
     */
    synchronized public boolean writeBackToFile(String uQid) {
        logger.debug("Setting query id: "  + uQid);
        this.setQueryDefUId(uQid);
        boolean allOk = true;
        String currentTimeStr;
        GregorianCalendar calendar;
        try {
            //  Date today = new Date();
            calendar = new GregorianCalendar();
            currentTimeStr = Long.toString(calendar.getTimeInMillis());

            this.filePath = ConfigDetails.getConfigDetails().getPathToPeer() + File.separatorChar + "Temp" + File.separatorChar + "Final" + this.getQueryDefUId() + "__" + currentTimeStr;
            FileWriter fw = new FileWriter(this.filePath);
            //for(int i=0; i<values.size(); i++)
            //{
            //    fw.write(values.get(i).toString());
            //}
            fw.write(this.toString());
            fw.close();
            // CAREFUL. here we clear the GatewaysToTheirResults so as not to carry this info anymore (it is now written in a file)
            this.real_gatewaysToTheirResultsHM.clear();
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            allOk = false;
            real_gatewaysToTheirResultsHM.clear(); // CAREFUL. we clear it anyways...(=even if errors occurred).
        }
        return allOk;
    }

    synchronized public HashMap<String, Vector<ReqResultOverData>> parseGatewaysToTheirResults() {
        this.parseFromFinalResultFile();
        HashMap<String, Vector<ReqResultOverData>> tmpCOPYtoReturn = new HashMap<String, Vector<ReqResultOverData>>(this.temporary_gatewaysToTheirResultsHM);
        this.temporary_gatewaysToTheirResultsHM.clear();
        return tmpCOPYtoReturn;
    }

    synchronized public Timestamp getCreationDate() {
        return creationDate;
    }

    synchronized public String getFilePath() {
        return filePath;
    }


    synchronized public Vector<ResultAggrStruct> findResultBy(String givGwId, ReqFunctionOverData functStruct, String moteId, String sensorModelIdStr) {
        //(To do)
        // open pertinent result file. parse it into the hashmap
        // if file does not exist, then simply return (empty vector)
        // if file exists, then check by given data.
        // gwid, and functStruct should always be given.
        //
        Vector<ResultAggrStruct> toReturnVec = new Vector<ResultAggrStruct>();
        if (givGwId == null || givGwId.equals("") || givGwId.equals("unknown") || functStruct == null)
            return toReturnVec;

        Vector<ReqResultOverData> tmpReqResVec = parseGatewaysToTheirResults().get(givGwId);
        for (int k = 0; k < tmpReqResVec.size(); k++) {
            /*
             * (To do) somehow to seperate function results
             *
             * The following loop is for results of a specific function
             */
            if (tmpReqResVec.elementAt(k).getFid() == functStruct.getfuncId()) {
                Vector<ResultAggrStruct> resVec = tmpReqResVec.elementAt(k).getAllResultsforFunct();

                for (int j1 = 0; j1 < resVec.size(); j1++) {
                    if (moteId.equals(resVec.get(j1).getMid()) && sensorModelIdStr.equals(resVec.get(j1).getSid())) {
                        toReturnVec.addElement(resVec.get(j1));
                    }
                }
            }
        }
        return toReturnVec;
    }

    /**
     * Updates the instance of FinalResultEntryPerDef object with data read from the corresponding file
     */
    synchronized private void parseFromFinalResultFile() {
        File inFile;
        if (this.getFilePath() != null && !this.getFilePath().equals("")) {
            inFile = new File(this.getFilePath());
            if (inFile.exists()) {
                FileReader fr = null;
                SMInputCursor inputRootElement = null;
                XMLStreamReader2 sr = null;
                try {

                    while (true) {
                        try {
                            fr = new FileReader(this.getFilePath());

                            WstxInputFactory fin = new WstxInputFactory();
                            fin.configureForConvenience();
                            fin.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE); // <-- NEEDED TO GET ATTRIBUTES!
                            // input

                            try{
                                sr = (XMLStreamReader2)fin.createXMLStreamReader(fr);
                                inputRootElement = SMInputFactory.rootElementCursor(sr);
                                inputRootElement.getNext();
                                //docum1 = (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(new MimeMediaType("text/xml"), fr);
                                break;
                            }  catch (IllegalArgumentException e) {
                                if(fr!=null)  {
                                    try {
                                        fr.close();
                                    }
                                    catch (IOException ex2)
                                    {
                                        logger.error("Error while trying to close up File partial entry");
                                    }
                                }
                                if(sr != null){
                                    try {
                                        sr.closeCompletely();
                                    }catch (XMLStreamException ex2)
                                    {
                                        logger.error("Error while trying to close up XML reader");
                                    }
                                }
                                throw new FileNotFoundException();               // pass a loop
                            } catch (javax.xml.stream.XMLStreamException e)
                            {
                                if(fr!=null)  {
                                    try {
                                        fr.close();
                                    }
                                    catch (IOException ex2)
                                    {
                                        logger.error("Error while trying to close up File partial entry");
                                    }
                                }
                                if(sr != null){
                                    try {
                                        sr.closeCompletely();
                                    }catch (XMLStreamException ex2)
                                    {
                                        logger.error("Error while trying to close up XML reader");
                                    }
                                }
                                throw new FileNotFoundException();      // pass a loop
                            }

                        } catch (FileNotFoundException fnfe) {
                            // check if waiting for a response has been timed out
                            // Timeout = 9 seconds
                            try {
                                Thread.sleep(2 * 100); // wait 200 milliseconds
                            } catch (Exception e) {
                            }

                        } catch (IOException ioe) {
                            ioe.printStackTrace();
                        }
                    }
                    if(inputRootElement!= null)
                    {
                        SMInputCursor childInElement = inputRootElement.childCursor();
                        while (childInElement.getNext() != null) {
                            if(!childInElement.getCurrEvent().hasText() ) {
                                if( childInElement.getLocalName().toLowerCase().equals("gatewayslist" ) ) {
                                    SMInputCursor childInElement2 = childInElement.childCursor();
                                    while (childInElement2.getNext() != null)
                                    {
                                        if(!childInElement2.getCurrEvent().hasText())
                                        {
                                            if(childInElement2.getLocalName().toLowerCase().equals("gateway"))
                                            {
                                                SMInputCursor childInElement3 = childInElement2.childCursor();
                                                String tmpKeyId = "";
                                                Vector<ReqResultOverData> tmpReqResVec = new Vector<ReqResultOverData>();
                                                while (childInElement3.getNext() != null)
                                                {
                                                    if(!childInElement3.getCurrEvent().hasText())
                                                    {
                                                        if(childInElement3.getLocalName().toLowerCase().equals("id"))
                                                        {
                                                            SMInputCursor childInElement5 = childInElement3.childMixedCursor();
                                                            while (childInElement5.getNext() != null)
                                                            {
                                                                if(childInElement5.getCurrEvent().hasText())
                                                                {
                                                                    tmpKeyId = childInElement5.getText();
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                        else if(childInElement3.getLocalName().toLowerCase().equals(PublicResponseAggrMsg.getRequestedFunctionsListTag().toLowerCase()))
                                                        {
                                                            SMInputCursor childInElement4 = childInElement3.childCursor();
                                                            while (childInElement4.getNext() != null)
                                                            {
                                                                if(!childInElement4.getCurrEvent().hasText() && childInElement4.getLocalName().toLowerCase().equals(PublicResponseAggrMsg.getRequestedFunctionTag().toLowerCase()))
                                                                {
                                                                    tmpReqResVec.addElement(new ReqResultOverData(childInElement4));
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (!tmpKeyId.equals("")) {
                                                    this.temporary_gatewaysToTheirResultsHM.put(tmpKeyId, tmpReqResVec); // ONLY THE TEMPORARY HASHMAP IS AFFECTED!
                                                }

                                            }
                                        }
                                    }
                                }
                                else if(childInElement.getLocalName().toLowerCase().equals("filename"))
                                {
                                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                                    while (childInElement2.getNext() != null)
                                    {
                                        if(childInElement2.getCurrEvent().hasText())
                                        {
                                            this.filePath = childInElement2.getText();
                                            break;
                                        }
                                    }
                                }
                                else if(childInElement.getLocalName().toLowerCase().equals("timestamp"))
                                {
                                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                                    while (childInElement2.getNext() != null)
                                    {
                                        if(childInElement2.getCurrEvent().hasText())
                                        {
                                            this.creationDate = Timestamp.valueOf(childInElement2.getText());
                                            break;
                                        }
                                    }
                                }
                                else if(childInElement.getLocalName().toLowerCase().equals("qdefUid"))
                                {
                                    SMInputCursor childInElement2 = childInElement.childMixedCursor();
                                    while (childInElement2.getNext() != null)
                                    {
                                        if(childInElement2.getCurrEvent().hasText())
                                        {
                                            this.setQueryDefUId(childInElement2.getText());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(fr!=null)  {
                        try {
                            fr.close();
                        }
                        catch (IOException ex2)
                        {
                            logger.error("Error while trying to close up File partial entry");
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    if(fr!=null)  {
                        try {
                            fr.close();
                        }
                        catch (IOException ex2)
                        {
                            logger.error("Error while trying to close up File partial entry");
                        }
                    }
                    if(sr != null){
                        try {
                            sr.closeCompletely();
                        }catch (XMLStreamException ex2)
                        {
                            logger.error("Error while trying to close up XML reader");
                        }
                    }
                }
            }
        }
    }


    /**
     * Method createInfoInDocument:
     * Creates XML structured info on this FinalResultEntryPerDef object, under the parent Element, in the specified StructuredDocument
     *
     * @param document    the desired MIME type representation for the query.
     * @param parElement  the parent element in the given XML document
     * @param verboseflag sets whether we want to dump the results inside the XML or just the filename containing the results.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement, boolean verboseflag, boolean tempflag) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;
        SMOutputElement tmpElement2;
        SMOutputElement tmpElement3;
        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(QueryDefinition.getQueryResultFileTag());
            }
            else {
                tmpElementOuter =  document.addElement(QueryDefinition.getQueryResultFileTag());
            }

            if (verboseflag) {
                HashMap<String, Vector<ReqResultOverData>> myGatewaysToTheirResultsHM;
                if (tempflag) {
                    myGatewaysToTheirResultsHM = this.temporary_gatewaysToTheirResultsHM;
                } else
                    myGatewaysToTheirResultsHM = this.real_gatewaysToTheirResultsHM;
                Set<String> st = myGatewaysToTheirResultsHM.keySet();
                Iterator<String> it = st.iterator();
                if (st.size() > 0) {
                    tmpElement1 = tmpElementOuter.addElement("gatewayslist") ;

                    while (it.hasNext()) {
                        String tmpGateWayId = it.next();
                        tmpElement2 = tmpElement1.addElement("gateway") ;
                        tmpElement3 = tmpElement2.addElement("id") ;
                        tmpElement3.addCharacters(tmpGateWayId);

                        Vector<ReqResultOverData> tmpReqResVec = myGatewaysToTheirResultsHM.get(tmpGateWayId);
                        if (tmpReqResVec.size() > 0) {
                            tmpElement3 =  tmpElement2.addElement(PublicResponseAggrMsg.getRequestedFunctionsListTag()) ;
                            for (int i = 0; i < tmpReqResVec.size(); i++) {
                                tmpReqResVec.get(i).createInfoInDocument(document, tmpElement3, tempflag);
                            }
                        }
                    }
                }
            }
            tmpElement1 = tmpElementOuter.addElement("filename") ;
            tmpElement1.addCharacters(this.filePath);

            tmpElement1 = tmpElementOuter.addElement("timestamp") ;
            tmpElement1.addCharacters(this.creationDate.toString());

            tmpElement1 = tmpElementOuter.addElement("qdefUid") ;
            tmpElement1.addCharacters(this.getQueryDefUId());

        }
        catch(Exception e) {
            return;
        }
    }


    /**
     * Method toString:
     * <p/>
     * no parameters
     *
     * @return the XML String representing this requested Final Results File XML fields
     */
    public String toString() {
        return this.toMyString(false);
    }

    public String toTmpString() {
        return this.toMyString(true);
    }

    private String toMyString(boolean showTempHMflag){
        StringWriter outStringWriter = new StringWriter();
        WstxOutputFactory fout = new WstxOutputFactory();
        fout.configureForXmlConformance();
        SMOutputDocument doc = null;
        try{
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);

            if (showTempHMflag)
                this.parseFromFinalResultFile();
            createInfoInDocument(doc, null, true, showTempHMflag);

            doc.closeRoot();
            this.temporary_gatewaysToTheirResultsHM.clear();
        } catch(Exception e) {
            e.printStackTrace();
            this.temporary_gatewaysToTheirResultsHM.clear();
            return "Errors encountered while attempting to print this FilesResultEntry!";
        }

        String retString = "";
        try{
            retString = outStringWriter.toString();
            outStringWriter.close();
        } catch(Exception e) {
            logger.error("Errors encountered while attempting to print this XML document!");
            e.printStackTrace();
        }
        return retString;
        //           StructuredTextDocument document = (StructuredTextDocument) StructuredDocumentFactory.newStructuredDocument(new MimeMediaType("text/xml"), IndexOfQueries.getQueryDefTag());
    }


    public String getQueryDefUId() {
        return queryDefUId;
    }

    public void setQueryDefUId(String pQueryDefUId) {
        this.queryDefUId = pQueryDefUId;
    }
}
