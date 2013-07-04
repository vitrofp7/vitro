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
package alter.vitro.vgw.service;

import alter.vitro.vgw.service.query.UserNodeQuery;
import alter.vitro.vgw.service.query.UserNodeResponse;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw.ConfirmedNodesListItemType;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw.ConfirmedNodesListType;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw.EquivListNodesRespType;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw.ObjectFactory;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp.EquivListNodesReqType;
import alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvsp.EquivNodesListItemType;
import alter.vitro.vgw.service.resourceRegistry.ResourceAvailabilityService;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * This class deals with continuation of service issues
 * If a  gateway is aware of equivalent nodes, then it keeps them in a cache of stored sets of equivalent nodes
 * These sets need to be updated consistently (eg. when adding a new node to a set, then we should check if two sets have now become equivalent (if another set already contained that node).
 * Also, in a set, there are no duplicates for a node. And two sets cannot have common nodes (if they do they should be merged).
 * Updates for these sets are coming as commands from the VSP.
 * TODO: also VGW could auto-detect possible node equivalencies, but it should always require WSI Enabler or VSP confirmation to add them to sets.
 * The sets are also kept (and persisted) at VSP level.
 * When a VGW is launched it could update the sets locally and at VSP (exchanging the right messages) to allow for purging of non-existing nodes.
 * So when launched the VGW should at least require the list of these sets from the VSP.
 * Also when the VSP is re-launched, it should sent the appropriate such lists to the corresponding gateways.
 *
 * Finally the nodes in a set should have the same capabilities (VITRO-queri-able capabilities) (this check is only done in the UI though to allow for flexibility).
 */
public class ContinuationOfProvisionService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /* Singleton pattern*/
    private long lastUpdatedTimeStamp ;
    // Key is the ID of the the EQUIV LIST in the VSP database, List<String> is a list of the sensor Ids in an equivalency set
    private HashMap<Integer, List<String>> cachedSetsOfEquivalency;
    // vsn-id to  array. Array[0] the replaced node id, array[1] the replacement.
    // (or we could skip the vsn-id entirely.
    // todo: String[] has [0] the source node, [1] the replacement node and (todo) [3] the capability resource
    // todo: so the entries will not be unique per node, but unique per source node+capability.
    private HashMap<String, List<String[]>> cachedReplacedResources;   // TODO: do we need such a cache (Yes. probably to refer to upon subsequent queries)?


    private static ContinuationOfProvisionService instance = null;

    private ContinuationOfProvisionService() {
        lastUpdatedTimeStamp = 0;
        setCachedSetsOfEquivalency(new HashMap<Integer, List<String>>());
        setCachedReplacedResources(new HashMap<String, List<String[]>>());
    }

    public static ContinuationOfProvisionService getInstance() {
        if(instance == null) {
            instance = new ContinuationOfProvisionService();
        }
        return instance;
    }

    public HashMap<Integer, List<String>> getCachedSetsOfEquivalency() {
        return cachedSetsOfEquivalency;
    }

    public void setCachedSetsOfEquivalency(HashMap<Integer, List<String>> cachedSetsOfEquivalency) {
        this.cachedSetsOfEquivalency = cachedSetsOfEquivalency;
    }

    public void addToCachedSetsOfEquivalency() {

    }

    public void removeFromCachedSetsOfEquivalency() {

    }

    public HashMap<String, List<String[]>> getCachedReplacedResources() {
        return cachedReplacedResources;
    }

    public void setCachedReplacedResources(HashMap<String, List<String[]>> cachedReplacedResources) {
        this.cachedReplacedResources = cachedReplacedResources;
    }

    /**
     * Sends back to the VSP, which sensor is replaced by which replacement (and, for which VSN id and capability)
     */
   // public void sendtoVSPCachedReplacedResources() {
   ////
    //}

    /**
     * Processes synch updates to the sets of equivalency received from the VSP.
     */
    public void handleVSPUpdateForSetsOfEquivalency(EquivNodesListItemType reqItem) {
        if(reqItem!=null && reqItem.getListId() != null && !reqItem.getListId().isEmpty()
                && reqItem.getNodeVec()!=null
                && reqItem.getNodeVec().getNodeId()!=null
                && !reqItem.getNodeVec().getNodeId().isEmpty())
        {
            Integer listCandId = 0;
            try {
                listCandId = Integer.valueOf(reqItem.getListId());
            } catch(Exception ex) {
                listCandId = 0;
                logger.error("Could not convert list id to integer!");
                return;
            }
            List<Integer> listsToBeMerged = new ArrayList<Integer>();
            for(String nodeCandId : reqItem.getNodeVec().getNodeId()) {

                for (Integer cachedListId : getCachedSetsOfEquivalency().keySet()){
                    if(getCachedSetsOfEquivalency().get(cachedListId)!=null)
                    {
                        for(String cachedNodeInList: getCachedSetsOfEquivalency().get(cachedListId)){
                            if(cachedNodeInList.compareToIgnoreCase(nodeCandId) == 0) {
                                if(!listsToBeMerged.contains(cachedListId))
                                {

                                    listsToBeMerged.add(cachedListId);
                                }
                            }
                        }
                    }

                }
            }
            // merge all the listsToBeMerged. Be careful not to add duplicates of node names.
            List<String> resultMergedList = new ArrayList<String>();
            if(listsToBeMerged.size() > 0 ) {
                listCandId = listsToBeMerged.get(0); //as long as it's something
            }  //else it's the id retrieved from the query/request (which is unique in the VSP, and VGW gets all ids from VSP anyway
            for(int i = 0 ; i < listsToBeMerged.size(); i++) {
                Integer idOfListToBeMerged = listsToBeMerged.get(i);
                // safe-double-check
                if(getCachedSetsOfEquivalency().containsKey(idOfListToBeMerged)
                        && getCachedSetsOfEquivalency().get(idOfListToBeMerged) != null) {
                    for(String nodeInAList: getCachedSetsOfEquivalency().get(idOfListToBeMerged)){
                        if(!resultMergedList.contains(nodeInAList))  {
                            resultMergedList.add(nodeInAList);
                        }
                    }
                    getCachedSetsOfEquivalency().remove(idOfListToBeMerged);
                }
            }
            // and after the merge, merge also the nodes in the request list (still checking for duplicates)
            for(String nodeCandId : reqItem.getNodeVec().getNodeId()) {
                if(!resultMergedList.contains(nodeCandId))  {
                    resultMergedList.add(nodeCandId);
                }
            }
            getCachedSetsOfEquivalency().put(listCandId, resultMergedList);
        }
    }

    /**
     * Processes synch updates to the sets of equivalency received from the VSP.
     */
    public String createSynchConfirmationForVSP(EquivListNodesReqType forRequest) {
        String retStr = "";
        // todo check also the timestamp of the Request with the lastUpdatedTimeStamp
        // and update the lastUpdatedTimeStamp if needed.
        if(forRequest!=null ) {
            long receivedTimestamp = 0;
            try {
                receivedTimestamp = Long.valueOf(forRequest.getTimestamp());
            }catch (Exception enmfrmtx) {
                logger.error("Exception while converting timestamp of synch message");
            }
            if(receivedTimestamp < lastUpdatedTimeStamp) {
                //ignore the message
                logger.info("Ignoring synch message with old timestamp");
                return null;
            } else {
                lastUpdatedTimeStamp = receivedTimestamp;
                // clean / purge existing cache of equivLists   and replacements
                getCachedSetsOfEquivalency().clear();
                getCachedReplacedResources().clear();
            }
        }


        try {
            EquivListNodesRespType response;

            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw");
            // create an object to marshal
            ObjectFactory theFactory = new ObjectFactory();
            response = theFactory.createEquivListNodesRespType();

            if(response != null && forRequest!=null) {

                response.setVgwId(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg());
                response.setMessageType(UserNodeResponse.COMMAND_TYPE_EQUIV_LIST_SYNCH_RESP);

                response.setTimestamp(Long.toString(new Date().getTime()));
                if( forRequest.getEquivNodesList() !=null ) {

                    if (response.getConfirmedNodesList() == null ) {
                        ConfirmedNodesListType theConfirmListType = new ConfirmedNodesListType();
                        response.setConfirmedNodesList(theConfirmListType);
                    }
                        //
                    if(forRequest.getEquivNodesList().getEquivNodesListItem() != null
                         && !forRequest.getEquivNodesList().getEquivNodesListItem().isEmpty()) {
                     // loop though items and confirm each one
                        for( EquivNodesListItemType reqItem  : forRequest.getEquivNodesList().getEquivNodesListItem())
                        {
                            ConfirmedNodesListItemType confirmedItemTmp = new ConfirmedNodesListItemType();
                            confirmedItemTmp.setListId(reqItem.getListId());
                            confirmedItemTmp.setOfRemoteTimestamp(reqItem.getOfRemoteTimestamp());
                            // process this addition to local cache
                            handleVSPUpdateForSetsOfEquivalency(reqItem);

                            response.getConfirmedNodesList().getConfirmedNodesListItem().add(confirmedItemTmp);
                        }
                    }
                }


                //javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.equivlistsynch.fromvgw");
                //ObjectFactory theFactory = new ObjectFactory();
                javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                JAXBElement<EquivListNodesRespType> myResponseMsgEl = theFactory.createEquivListNodesResp(response);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                marshaller.marshal(myResponseMsgEl, baos);
                retStr = baos.toString(HTTP.UTF_8);
            }

        } catch (javax.xml.bind.JAXBException je) {
            je.printStackTrace();
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        return  retStr;
    }

    // should return the cached equivalent if it is enabled
    // it could also consider for which VSN and which capability this equivalency is requested for
    // TODO: also check again against the current version of the Cached Equiv Lists. Equivalencies that did not exist, might be there, and vice versa old equivalencies might have been reverted!
    // TODO: however for safety when we update an equivalency list, we remove all the cachedReplacement records, to re-calculate them anew!
    public String[] findNextEquivalentNode(String vsnId, List<String> nodeIdsInThisVSN, String sourceNode, String capabilityId) {
        String[] retNodeReplacingInfo = null;
        boolean foundCachedReplacement = false;
        boolean foundAReplacement = false;
        // first search in the replacement cache for this VSNid
        if(!cachedReplacedResources.isEmpty() && cachedReplacedResources.containsKey(vsnId)){
            List<String[]> nodeReplaceEntries =  cachedReplacedResources.get(vsnId);
            for (String[] entryTmp: nodeReplaceEntries){
                if(entryTmp.length == 3
                        && entryTmp[0] !=null
                        && !entryTmp[0].isEmpty()
                        && entryTmp[0].compareToIgnoreCase(sourceNode) == 0
                        && nodeIdsInThisVSN !=null
                        && !nodeIdsInThisVSN.contains(entryTmp[1])
                        && entryTmp[1]!=null
                        && !entryTmp[1].isEmpty()
                        && entryTmp[1].compareToIgnoreCase(sourceNode) != 0
                        && entryTmp[2]!=null
                        && !entryTmp[2].isEmpty()
                        && entryTmp[2].compareToIgnoreCase(capabilityId) == 0 )
                {
                    // check if this node is enabled (or also disabled)
                    if(ResourceAvailabilityService.getInstance().isNodeResourceAvailable(vsnId, entryTmp[1], capabilityId )
                            && !replacesOtherNodeInVSN(vsnId, sourceNode, entryTmp[1], capabilityId )){
                    // DONE: check if this node is enabled (or also disabled)

                        retNodeReplacingInfo = entryTmp;
                        foundCachedReplacement = true;
                    }
                    if(!foundCachedReplacement){
                        // TODO: if this entry is stale or the node is disabled we should also remove it!
                        removeReplacementVectorForNodeOfVSN(vsnId, sourceNode, capabilityId);
                    }
                    break;
                }
            }
        }
         // if not found a replacement in cache, then search based on stored equiv lists and also if found a match, then
        //                  add to the replacement cache and return it.
        if (!foundCachedReplacement && !cachedSetsOfEquivalency.isEmpty())
        {
            for(Integer setIdTmp : cachedSetsOfEquivalency.keySet()) {
                if(foundAReplacement)
                {
                    break;
                }
                List<String> setOfEquivEntry = cachedSetsOfEquivalency.get(setIdTmp);
                if(setOfEquivEntry != null && !setOfEquivEntry.isEmpty()){
                    for(String itemNode: setOfEquivEntry)   {
                        if(itemNode.compareToIgnoreCase(sourceNode) == 0 )
                        {
                            // we iterate the set one more time
                            for(String itemReplaceCandidateNode: setOfEquivEntry)   {
                                if(!foundAReplacement
                                        && itemReplaceCandidateNode.compareToIgnoreCase(sourceNode)!= 0
                                        && nodeIdsInThisVSN !=null
                                        && !nodeIdsInThisVSN.contains(itemReplaceCandidateNode)
                                        && ResourceAvailabilityService.getInstance().isNodeResourceAvailable(vsnId, itemReplaceCandidateNode, capabilityId)
                                        && !replacesOtherNodeInVSN(vsnId, sourceNode, itemReplaceCandidateNode, capabilityId )){

                                    // DONE: check if this node is enabled (or also disabled)\
                                    // TODO: check if this node actually exists in the VGW currently (or is it stale)!
                                    // TODO: if capability is set as an argument, we should check that if the (alive) replacement has also this capability
                                    // DONE: add node in the cache of replacements!
                                    retNodeReplacingInfo = updateReplacementVectorForNodeOfVSN(vsnId, sourceNode,itemReplaceCandidateNode, capabilityId);

                                    foundAReplacement = true;
                                    break;
                                }

                            }
                            break;
                        }
                    } // end of loop over nodes of an equiv set
                }

          } // an of loop over equiv sets of the cache
        }
        return retNodeReplacingInfo;
    }

    /**
     * removes and entry from the replacements cache for this node and vsnId
     * @param sourceNode
     * @param vsnId
     */
     synchronized private void removeReplacementVectorForNodeOfVSN( String vsnId, String sourceNode, String pCapabilityId) {
         List<String[]> replacementsListForVSN = null;
         if(cachedReplacedResources.containsKey(vsnId)) {

             replacementsListForVSN = cachedReplacedResources.get(vsnId);
             int initSizeOfList = replacementsListForVSN.size();
             for(int idx = initSizeOfList; idx >= 0; idx --) {
                 if(replacementsListForVSN.get(idx) != null &&
                         replacementsListForVSN.get(idx).length == 3 &&
                         replacementsListForVSN.get(idx)[0].compareToIgnoreCase(sourceNode) ==0 &&
                         replacementsListForVSN.get(idx)[2].compareToIgnoreCase(pCapabilityId) ==0 ) {
                      replacementsListForVSN.remove(idx);
                 }
             }
         }

     }
     //
    //

    /**
     * This is the only method updating the cache of replacements for vsnId. (other than the purging one)
     * @param vsnId
     * @param sourceNode
     * @param targetNode
     * @param pCapabilityId
     * @return
     */
    synchronized String[] updateReplacementVectorForNodeOfVSN(String vsnId, String sourceNode, String targetNode, String pCapabilityId) {
        String[] replacementInfoItem = null;
        if(vsnId!=null
                && !vsnId.isEmpty()
                && sourceNode!=null
                && !sourceNode.isEmpty()
                && targetNode!=null
                && !targetNode.isEmpty()
                && pCapabilityId!=null
                && !pCapabilityId.isEmpty())
        {
            replacementInfoItem = new String[] {sourceNode, targetNode, pCapabilityId};
            List<String[]> replacementsListForVSN = null;
            if(cachedReplacedResources.containsKey(vsnId) && cachedReplacedResources.get(vsnId)!=null) {
                replacementsListForVSN = cachedReplacedResources.get(vsnId);
            }
            else {
                replacementsListForVSN = new ArrayList<String[]>();
            }
            replacementsListForVSN.add(replacementInfoItem);
            cachedReplacedResources.put(vsnId, replacementsListForVSN);
        }
        return replacementInfoItem;
    }

    public boolean replacesOtherNodeInVSN(String pVSNId,String origMoteId, String replcPMoteId, String pCapabilityId) {
        // A replacement replcPMoteId node should not replace other nodes for the same VSN;
        boolean replacesOtherNodeInVSNFlg = false;
        if(pVSNId!=null
                && !pVSNId.isEmpty()
                && origMoteId!=null
                && !origMoteId.isEmpty()
                && replcPMoteId!=null
                && !replcPMoteId.isEmpty()
                && pCapabilityId!=null
                && !pCapabilityId.isEmpty())
        {
              if(getCachedReplacedResources().containsKey(pVSNId)) {
                  List<String[]> tokenList = getCachedReplacedResources().get(pVSNId);
                   for(String [] tokenIt: tokenList) {
                       if(tokenIt[0].compareToIgnoreCase(origMoteId)!=0 &&
                               tokenIt[1].compareToIgnoreCase(replcPMoteId)==0 &&
                               tokenIt[2].compareToIgnoreCase(pCapabilityId)==0) {
                           replacesOtherNodeInVSNFlg = true;
                           break;
                       }
                   }

              }
        }
        return replacesOtherNodeInVSNFlg;

    }

    /**
     * for debugging (verbose list of lists)
     * @return
     */
    public String printEquivListsAndReplacementLists() {
        String retStr = "";
        StringBuilder retStrBld = new StringBuilder();
        retStrBld.append("Nodes Equivalency List: \n");
        for(Integer equivSetId : getCachedSetsOfEquivalency().keySet()) {
            retStrBld.append("Set id: ");
            retStrBld.append(equivSetId);
            List<String> equivSetNodesList = getCachedSetsOfEquivalency().get(equivSetId);

            if(equivSetNodesList!=null && !equivSetNodesList.isEmpty())
            {
                retStrBld.append("\nNodes:");
                for(int i = 0; i <equivSetNodesList.size(); i++){
                    retStrBld.append(equivSetNodesList.get(i));
                    if(i+1 < equivSetNodesList.size()) {
                        retStrBld.append(",");
                    }
                }
            }
            retStrBld.append("\n");
        }
        retStrBld.append("Replaced Resources List: \n");
        for(String vsnId : getCachedReplacedResources().keySet()) {
            retStrBld.append("VSN id: ");
            retStrBld.append(vsnId);

            List<String[]> smDevListsTmp = getCachedReplacedResources().get(vsnId);
            if(smDevListsTmp!=null && !smDevListsTmp.isEmpty())
            {
                retStrBld.append("Replacement lists: ");

                for(int i = 0; i <smDevListsTmp.size(); i++){
                    String[] smDevReplcInfo =  smDevListsTmp.get(i);
                    if(smDevReplcInfo!=null && smDevReplcInfo.length == 3) {
                    retStrBld.append("Device: ");
                    retStrBld.append(smDevReplcInfo[0]);
                    retStrBld.append(" Replaced By: ");
                    retStrBld.append(smDevReplcInfo[1]);
                    retStrBld.append(" For Capability: ");
                    retStrBld.append(smDevReplcInfo[2]);
                    retStrBld.append("\n");
                    }
                }
            }
            retStrBld.append("\n");
        }
        retStr = retStrBld.toString();
        return retStr;
    }


}
