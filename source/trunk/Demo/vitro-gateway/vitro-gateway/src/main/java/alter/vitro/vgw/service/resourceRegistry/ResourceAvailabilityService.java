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
package alter.vitro.vgw.service.resourceRegistry;

import alter.vitro.vgw.model.CSmartDevice;
import alter.vitro.vgw.service.VitroGatewayService;
import alter.vitro.vgw.service.query.UserNodeResponse;
import alter.vitro.vgw.service.query.wrappers.ReqSensorAndFunctions;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw.ConfirmedEnabledNodesListItemType;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw.ConfirmedEnabledNodesListType;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw.EnableNodesRespType;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw.ObjectFactory;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvsp.EnableNodesReqType;
import alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvsp.EnabledNodesListItemType;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vitro.vgw.model.Resource;

import javax.xml.bind.JAXBElement;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Keeps a cache of sensor nodes and whether they are available for VSNs
 * //  TODO every time the GatewayService Rediscovers its resources the enable/disable statuses should be set based on the latest synch update from VSP (except if they are explicitly set by the VGW!)
 *
 */
public class ResourceAvailabilityService {

    public static final int INIT_DISCOVERY = 1;
    public static final int SUBSEQUENT_DISCOVERY = 2;

    private Logger logger = LoggerFactory.getLogger(getClass());
    private long lastRemotelyUpdatedEnableDisableStatusTimeStamp ;

    private HashMap<String, CSmartDevice> cachedDiscoveredDevices;
    private HashMap<String, ResourceProperties> cacheOfLastReceivedEnableDisableMessage;
    private static Map<Integer, Resource> resourceMap;
    private static ResourceAvailabilityService instance = null;

    private ResourceAvailabilityService() {
        resourceMap = new HashMap<Integer, Resource>();
        resourceMap.put(getResourceCode(Resource.RES_TEMPERATURE), Resource.RES_TEMPERATURE);
        resourceMap.put(getResourceCode(Resource.RES_LIGHT), Resource.RES_LIGHT);
        resourceMap.put(getResourceCode(Resource.RES_HUMIDITY), Resource.RES_HUMIDITY);
        resourceMap.put(getResourceCode(Resource.RES_WIND_SPEED), Resource.RES_WIND_SPEED);
        resourceMap.put(getResourceCode(Resource.RES_CO), Resource.RES_CO);
        resourceMap.put(getResourceCode(Resource.RES_CO2), Resource.RES_CO2);
        resourceMap.put(getResourceCode(Resource.RES_PRESSURE), Resource.RES_PRESSURE);
        resourceMap.put(getResourceCode(Resource.RES_BAROMETRIC_PRESSURE), Resource.RES_BAROMETRIC_PRESSURE);

        resourceMap.put(getResourceCode(Resource.RES_TRUST_ROUTING), Resource.RES_TRUST_ROUTING);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT1), Resource.RES_SWITCH_LIGHT1);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT2), Resource.RES_SWITCH_LIGHT2);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT3), Resource.RES_SWITCH_LIGHT3);
        resourceMap.put(getResourceCode(Resource.RES_SWITCH_LIGHT4), Resource.RES_SWITCH_LIGHT4);

        setCacheOfLastReceivedEnableDisableMessage(new HashMap<String, ResourceProperties>());
        setCachedDiscoveredDevices(new HashMap<String, CSmartDevice>() );
        lastRemotelyUpdatedEnableDisableStatusTimeStamp = 0;
    }

    private int getResourceCode(Resource resource){

        int hash = resource.getName().hashCode();
        return hash < 0 ? hash * -1 : hash;

    }

    private int getStringMixedResourceCode(String pToken ){

        int hash = pToken.hashCode();
        return hash < 0 ? hash * -1 : hash;

    }

    public static ResourceAvailabilityService getInstance() {
        if(instance == null) {
            instance = new ResourceAvailabilityService();
        }
        return instance;
    }

    public HashMap<String, ResourceProperties> getCacheOfLastReceivedEnableDisableMessage() {
        return cacheOfLastReceivedEnableDisableMessage;
    }


    public void setCacheOfLastReceivedEnableDisableMessage(HashMap<String, ResourceProperties> pCacheOfLastReceivedEnableDisableMessages) {
        this.cacheOfLastReceivedEnableDisableMessage = pCacheOfLastReceivedEnableDisableMessages;
    }

    public void handleVSPEnableDisableRequest() {

    }

    public boolean isSmartNodeEnabled(String nodeId) {
        boolean retVal = false;
        if(cachedDiscoveredDevices.containsKey(nodeId) && cachedDiscoveredDevices.get(nodeId)!=null ) {
            logger.debug("Device is in cache");
            ResourceProperties nodeProps = cachedDiscoveredDevices.get(nodeId).getRegistryProperties();
            if(nodeProps.isEnabled()) {
                logger.debug("Device is enabled");
            } else {
                logger.debug("Device is disabled");
            }
            retVal = nodeProps.isEnabled();
        }
        return retVal;
    }

    public boolean smartNodeSupportsCapability(String nodeId, String capabilityId) {
        boolean retVal = false;
        CSmartDevice corrDev = null;
        if(cachedDiscoveredDevices.containsKey(nodeId) && cachedDiscoveredDevices.get(nodeId)!=null ) {
            corrDev = cachedDiscoveredDevices.get(nodeId);
            Integer extSimpleCapabilityIdInt = -1;
            try{
                extSimpleCapabilityIdInt= Integer.valueOf(capabilityId);
            } catch (Exception exftm) {
                logger.error("Could not convert capability id to integer to check for avail: "+capabilityId);
            }
            Integer composedMixedCapabilityId = ReqSensorAndFunctions.invalidSensModelId;
            logger.debug("Device check for capability " + capabilityId);
            if(!resourceMap.containsKey(extSimpleCapabilityIdInt)) {
                logger.debug("VGW does not support this capability: " +capabilityId);
                //logger.debug("VGW Supports: ");
                //for(Integer idSuppd : resourceMap.keySet()) {
                //    logger.debug(Integer.toString(idSuppd) + ": " + resourceMap.get(idSuppd).getName());
                //}

            }
            else {
                String capName = resourceMap.get(extSimpleCapabilityIdInt).getName();
                // For some reason (valid reason) the smID stored in the VGW for nodes is mixing the node name (type) with capability simple name however
                // the checking works in the internal functions, but for comparison here
                // we need to acquire something similar for the incomming externalCap id
                logger.debug("VGW supports this cap :" + capName);

                composedMixedCapabilityId = getStringMixedResourceCode(capName + "-" + corrDev.getName());
                //logger.debug("Mixed capId is :" + composedMixedCapabilityId);

                logger.debug("Device capabilities: ");
                for(Integer cpId :cachedDiscoveredDevices.get(nodeId).getSpecificSensorModelIdsVec() ) {
                    logger.debug(Integer.toString(cpId));
                }
                if(cachedDiscoveredDevices.containsKey(nodeId)
                        && cachedDiscoveredDevices.get(nodeId)!=null
                        && cachedDiscoveredDevices.get(nodeId).getSpecificSensorModelIdsVec()!=null
                        && !cachedDiscoveredDevices.get(nodeId).getSpecificSensorModelIdsVec().isEmpty()
                        && cachedDiscoveredDevices.get(nodeId).getSpecificSensorModelIdsVec().contains(composedMixedCapabilityId)) {
                    retVal = true;
                }
            }
        }

        if(retVal) {
            logger.debug("Device supports this capability");
        } else {
            logger.debug("Device deos not support this capability");
        }
        return retVal;
    }
    /**
     *
     * @param pVSNId
     * @param pMoteId
     * @param pCapabilityId
     * @return
     */
    public boolean isNodeResourceAvailable(String pVSNId,String pMoteId, String pCapabilityId) {
        // A disabled node is disabled for all capabilities
        // Also, TODO there should be a RECORD of capabilities assigned to VSNid
        // Now: The decision is made based on whether the node is enabled AND supports the requested capability at all
        boolean enabledAndSupportsCap = (isSmartNodeEnabled(pMoteId) && smartNodeSupportsCapability(pMoteId, pCapabilityId));
        return enabledAndSupportsCap;

    }


    /**
     * Processes synch updates to the sets of enabled/disabled received from the VSP.
     * and create a message to confirm them
     */
    public String createSynchConfirmationForVSP(EnableNodesReqType forRequest) {
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
            if(receivedTimestamp < lastRemotelyUpdatedEnableDisableStatusTimeStamp) {
                //ignore the message
                logger.info("Ignoring synch message with old timestamp");
                return null;
            } else {
                lastRemotelyUpdatedEnableDisableStatusTimeStamp = receivedTimestamp;
            }
        }
        // clear the cache of last request:
        this.getCacheOfLastReceivedEnableDisableMessage().clear();

        try {
            EnableNodesRespType response;

            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw");
            // create an object to marshal
            ObjectFactory theFactory = new ObjectFactory();
            response = theFactory.createEnableNodesRespType();

            if(response != null && forRequest!=null) {

                response.setVgwId(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg());
                response.setMessageType(UserNodeResponse.COMMAND_TYPE_ENABLENODES_RESP);

                response.setTimestamp(Long.toString(new Date().getTime()));
                if( forRequest.getEnabledNodesList() !=null ) {
                    if (response.getConfirmedEnabledNodesList() == null ) {
                        ConfirmedEnabledNodesListType theConfirmListType = new ConfirmedEnabledNodesListType();
                        response.setConfirmedEnabledNodesList(theConfirmListType);
                        //
                        if(forRequest.getEnabledNodesList().getEnabledNodesListItem() != null &&
                                !forRequest.getEnabledNodesList().getEnabledNodesListItem().isEmpty() ) {
                            // loop though items and confirm each one
                            for( EnabledNodesListItemType reqItem  : forRequest.getEnabledNodesList().getEnabledNodesListItem())
                            {
                                boolean reqStatus = (reqItem.getStatus().compareToIgnoreCase("enabled") ==0 )? true: false;
                                boolean updatedByVGW = false;
                                long reqRemoteTimestamp = 0;
                                try {
                                    reqRemoteTimestamp = Long.valueOf(reqItem.getOfRemoteTimestamp());
                                } catch(Exception exftme1) {
                                    logger.error("Could not convert timestamp of remote synch enable request");
                                    reqRemoteTimestamp = 0;
                                }
                                long localTimestampSynch = (new Date()).getTime(); //it does not matter ?
                                // ALSO HANDLE EACH ONE OF THE REQUESTS (TODO: CAN WE CONFIRM ALL, BUT NOT CONFROM TO ALL REQUESTs ???)
                                //
                                //
                                if(getCachedDiscoveredDevices()!= null && !getCachedDiscoveredDevices().isEmpty() && getCachedDiscoveredDevices().containsKey(reqItem.getNodeId()) ) {
                                    CSmartDevice tmpSmDev =  getCachedDiscoveredDevices().get(reqItem.getNodeId());
                                    if(!tmpSmDev.getRegistryProperties().isEnabled() && tmpSmDev.getRegistryProperties().isStatusWasDecidedByThisVGW()) {
                                        // if it was disabled by the VGW, then ignore any requests/cached or not by the VSP to change its status.
                                        reqStatus = tmpSmDev.getRegistryProperties().isEnabled();
                                        updatedByVGW = true;
                                    }
                                    else {
                                        tmpSmDev.getRegistryProperties().setEnabled(reqStatus);
                                        tmpSmDev.getRegistryProperties().setTimeStampEnabledStatusRemotelySynch(reqRemoteTimestamp);
                                        tmpSmDev.getRegistryProperties().setTimeStampEnabledStatusSynch(localTimestampSynch);//is this used?
                                    }
                                }
                                //
                                //
                                // AND STORE THE REQUEST TO THE CACHE OF LAST REQUEST, INDEPENDENTLY OF WHETHER WE CONFORMED
                                // BUT IF WE DID NOT CONFORM, WE SEND BACK OUR (VGW) VALUES
                                //
                                ResourceProperties reqResProps = new ResourceProperties(reqItem.getNodeId());
                                reqResProps.setEnabled(reqStatus);
                                reqResProps.setTimeStampEnabledStatusRemotelySynch(reqRemoteTimestamp);
                                reqResProps.setTimeStampEnabledStatusSynch(localTimestampSynch); //it does not matter ??
                                reqResProps.setStatusWasDecidedByThisVGW(updatedByVGW);

                                this.getCacheOfLastReceivedEnableDisableMessage().put(reqItem.getNodeId(),reqResProps) ;

                                // AND CONSTRUCT THE CONFIRMATION MESSAGE!
                                ConfirmedEnabledNodesListItemType confirmedItemTmp = new ConfirmedEnabledNodesListItemType();
                                confirmedItemTmp.setNodeId(reqItem.getNodeId());

                                String updatedByVGWStr = updatedByVGW? "1" : "0";
                                String reqStatusStr = reqStatus? "enabled" : "disabled";
                                confirmedItemTmp.setStatus(reqStatusStr);
                                confirmedItemTmp.setGwInitFlag(updatedByVGWStr);
                                confirmedItemTmp.setOfRemoteTimestamp(reqItem.getOfRemoteTimestamp());
                                theConfirmListType.getConfirmedEnabledNodesListItem().add(confirmedItemTmp);
                             }
                        }
                    }
                }
                javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                JAXBElement<EnableNodesRespType> myResponseMsgEl = theFactory.createEnableNodesResp(response);

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

    public String createSynchConfirmationForVSPFromCurrentStatus_VGWInitiated() {
        String retStr = "";

        try {
            EnableNodesRespType response;

            javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance("alter.vitro.vgw.service.query.xmlmessages.enablednodessynch.fromvgw");
            // create an object to marshal
            ObjectFactory theFactory = new ObjectFactory();
            response = theFactory.createEnableNodesRespType();

            if(response != null) {
                response.setVgwId(VitroGatewayService.getVitroGatewayService().getAssignedGatewayUniqueIdFromReg());
                response.setMessageType(UserNodeResponse.COMMAND_TYPE_ENABLENODES_RESP);

                response.setTimestamp(Long.toString(new Date().getTime()));
                if (response.getConfirmedEnabledNodesList() == null ) {
                    ConfirmedEnabledNodesListType theConfirmListType = new ConfirmedEnabledNodesListType();
                    response.setConfirmedEnabledNodesList(theConfirmListType);


                    for(String devId : getCachedDiscoveredDevices().keySet()) {
                        CSmartDevice smDevTmp = getCachedDiscoveredDevices().get(devId);
                        // AND CONSTRUCT THE CONFIRMATION MESSAGE!
                        ConfirmedEnabledNodesListItemType confirmedItemTmp = new ConfirmedEnabledNodesListItemType();
                        confirmedItemTmp.setNodeId(smDevTmp.getId());
                        boolean updatedByVGW = smDevTmp.getRegistryProperties().isStatusWasDecidedByThisVGW();
                        boolean currStatus = smDevTmp.getRegistryProperties().isEnabled();
                        String updatedByVGWStr = updatedByVGW? "1" : "0";
                        String currStatusStr = currStatus? "enabled" : "disabled";
                        confirmedItemTmp.setStatus(currStatusStr);
                        confirmedItemTmp.setGwInitFlag(updatedByVGWStr);
                        confirmedItemTmp.setOfRemoteTimestamp(Long.toString(smDevTmp.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch()));
                        theConfirmListType.getConfirmedEnabledNodesListItem().add(confirmedItemTmp);
                    }
                }
                javax.xml.bind.Marshaller marshaller = jaxbContext.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                JAXBElement<EnableNodesRespType> myResponseMsgEl = theFactory.createEnableNodesResp(response);

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
        logger.debug("Sending UPDATE FROM VGW (enable-disable): " + retStr);
        return  retStr;
    }


    public /*synchronized*/ void updateCachedNodeList(List<CSmartDevice> newList, int discoveryFlag) {
        if(newList!= null) {
            Iterator<CSmartDevice> smDevIt = newList.iterator();
            while(smDevIt.hasNext()) {
                CSmartDevice tmpSmDev = smDevIt.next();

                getCachedDiscoveredDevices().put(tmpSmDev.getId(), tmpSmDev);
                //
                // Check with cached synch message from VSP
                //
                if(getCacheOfLastReceivedEnableDisableMessage() != null && !getCacheOfLastReceivedEnableDisableMessage().isEmpty() &&
                        getCacheOfLastReceivedEnableDisableMessage().containsKey(tmpSmDev.getId())) {
                    ResourceProperties tmpResProps = getCacheOfLastReceivedEnableDisableMessage().get(tmpSmDev.getId());
                    if(discoveryFlag == INIT_DISCOVERY) {
                        tmpSmDev.getRegistryProperties().setEnabled(tmpResProps.isEnabled());
                        tmpSmDev.getRegistryProperties().setTimeStampEnabledStatusRemotelySynch(tmpResProps.getTimeStampEnabledStatusRemotelySynch()); ;
                        tmpSmDev.getRegistryProperties().setTimeStampEnabledStatusSynch((new Date()).getTime()); //is this used?
                        if(this.lastRemotelyUpdatedEnableDisableStatusTimeStamp < tmpResProps.getTimeStampEnabledStatusRemotelySynch())
                        {
                            this.lastRemotelyUpdatedEnableDisableStatusTimeStamp = tmpResProps.getTimeStampEnabledStatusRemotelySynch();
                        }
                    } else if(discoveryFlag == SUBSEQUENT_DISCOVERY) {
                        if(!tmpSmDev.getRegistryProperties().isEnabled() && tmpSmDev.getRegistryProperties().isStatusWasDecidedByThisVGW()) {
                             // if it was disabled by the VGW, then ignore any requests/cached or not by the VSP to change its status.

                        }
                        else {
                            tmpSmDev.getRegistryProperties().setEnabled(tmpResProps.isEnabled());
                            tmpSmDev.getRegistryProperties().setTimeStampEnabledStatusRemotelySynch(tmpResProps.getTimeStampEnabledStatusRemotelySynch()); ;
                            tmpSmDev.getRegistryProperties().setTimeStampEnabledStatusSynch((new Date()).getTime());//is this used?
                            if(this.lastRemotelyUpdatedEnableDisableStatusTimeStamp < tmpResProps.getTimeStampEnabledStatusRemotelySynch())
                            {
                                this.lastRemotelyUpdatedEnableDisableStatusTimeStamp = tmpResProps.getTimeStampEnabledStatusRemotelySynch();
                            }
                        }
                    }
                }
            }
        } else {
            getCachedDiscoveredDevices().clear();
        }
        logger.debug(ResourceAvailabilityService.getInstance().printDevicesAndEnabledStatus());

    }

    public void setCachedDiscoveredDevices(HashMap<String, CSmartDevice> cachedDiscoveredDevices) {
        this.cachedDiscoveredDevices = cachedDiscoveredDevices;
    }

    public HashMap<String, CSmartDevice> getCachedDiscoveredDevices() {
        return cachedDiscoveredDevices;
    }

    public String printDevicesAndEnabledStatus() {
        String retStr = "";
        StringBuilder retStrBld = new StringBuilder();
        retStrBld.append("Devices and Enabled Status: \n");
        for(String devId : getCachedDiscoveredDevices().keySet()) {
            CSmartDevice smDevTmp = getCachedDiscoveredDevices().get(devId);
            retStrBld.append("Smart Device: ");
            retStrBld.append(devId);
            retStrBld.append(" ::Enabled: ");
            retStrBld.append(smDevTmp.getRegistryProperties().isEnabled());
            retStrBld.append("\n");
        }
        retStr = retStrBld.toString();
        return retStr;
    }
}
