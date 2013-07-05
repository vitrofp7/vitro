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
import vitro.vspEngine.logic.model.GatewayWithSmartNodes;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.common.abstractservice.AbstractGatewayManager;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

/**
 * Class that creates the messages to send to a VGW for synching enabled/disabled node lists
 * Also parses the confirmation responses for the enabled/disabled lists
 * <EnableNodesReq>
 *      <message-type>string</message-type>
 *      <enabledNodesList>
 *          <!--Zero or more repetitions:-->
 *          <enabledNodesListItem>
 *              <nodeId>string</nodeId>
 *              <status>string</status>
 *              <ofRemoteTimestamp>string</ofRemoteTimestamp>
 *          </enabledNodesListItem>
 *      </enabledNodesList>
 *      <timestamp>string</timestamp>
 * </EnableNodesReq>

 -------
 (VGW RESPONSE)
 * <EnableNodesResp>
 *  <message-type>string</message-type>
 *  <vgwId>string</vgwId>
 *      <confirmedEnabledNodesList>
 *          <!--Zero or more repetitions:-->
 *          <EnabledNodesListItem>
 *              <nodeId>string</nodeId>
 *              <status>string</status>
 *              <ofRemoteTimestamp>string</ofRemoteTimestamp>
 *              <gwInitFlag>string</gwInitFlag>
 *          </EnabledNodesListItem>
 *      </confirmedEnabledNodesList>
 * <timestamp>string</timestamp>
 * </EnableNodesResp>
 */
public class DisabledNodesVGWSynch {
    private static Logger logger = Logger.getLogger(DisabledNodesVGWSynch.class);

    private static HashMap<String, Vector<EnabledNodesListItem>> cacheOfEnabledNodeItems; // used for persistence when the VGW resources are purged or requested anew (thus possibly/probably overwriting the existing nodes)
    private static HashMap<String, Long> cacheOfTimestampsForMessagesFromVGW;

    private static final String TAG_ROOT_REQ = "EnableNodesReq";
    private static final String TAG_MSGTYPE ="message-type" ;
    private static final String TAG_VGW_ID ="vgwId";
    private static final String TAG_TIMESTAMP ="timestamp";
    private static final String TAG_LIST ="enabledNodesList";
    private static final String TAG_ITEM ="enabledNodesListItem";
    private static final String TAG_NODE_ID ="nodeId";
    private static final String TAG_NODE_STATUS = "status";
    private static final String TAG_REMOTE_TS ="ofRemoteTimestamp";

    private static final String TAG_ROOT_RESP = "EnableNodesResp";
    private static final String TAG_CONFIRMED_LIST ="confirmedEnabledNodesList";
    private static final String TAG_CONFIRMED_ITEM ="confirmedEnabledNodesListItem";
    private static final String TAG_GW_INITIATED ="gwInitFlag";

    public static final String fromVSPMsgType = "EnableNodesReq";
    public static final String fromVGWMsgType = "EnableNodesResp";

    //singleton
    private static DisabledNodesVGWSynch instance = null;


    private DisabledNodesVGWSynch() {
        setCacheOfEnabledNodeItems(new HashMap<String, Vector<EnabledNodesListItem>>());
        setCacheOfTimestampsForMessagesFromVGW(new HashMap<String, Long>());
    }

    public static DisabledNodesVGWSynch getInstance() {
        if(instance == null){
            instance = new DisabledNodesVGWSynch();
        }
        return instance;
    }

    public static HashMap<String, Vector<EnabledNodesListItem>> getCacheOfEnabledNodeItems() {
        return cacheOfEnabledNodeItems;
    }

    public static void setCacheOfEnabledNodeItems(HashMap<String, Vector<EnabledNodesListItem>> cacheOfEnabledNodeItems) {
        DisabledNodesVGWSynch.cacheOfEnabledNodeItems = cacheOfEnabledNodeItems;
    }

    public static HashMap<String, Long> getCacheOfTimestampsForMessagesFromVGW() {
        return cacheOfTimestampsForMessagesFromVGW;
    }

    public static void setCacheOfTimestampsForMessagesFromVGW(HashMap<String, Long> cacheOfTimestampsForMessagesFromVGW) {
        DisabledNodesVGWSynch.cacheOfTimestampsForMessagesFromVGW = cacheOfTimestampsForMessagesFromVGW;
    }

    /**
     * To be called before sending for a report from a VGW
     * @param vgwId
     */
    public void invalidateCacheForVGW(String vgwId) {
        logger.debug("Invalidating resource enable/disable cache for gw: " + vgwId);
        if(cacheOfEnabledNodeItems.containsKey(vgwId) && cacheOfEnabledNodeItems.get(vgwId)!=null) {
            cacheOfEnabledNodeItems.get(vgwId).clear();
        }
        if(UserNode.getUserNode() !=null ) {
            HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM = UserNode.getUserNode().getGatewaysToSmartDevsHM();
            if(gatewaysToSmartDevsHM.containsKey(vgwId)) {
                GatewayWithSmartNodes tmpGWWithNodes = gatewaysToSmartDevsHM.get(vgwId);
                if(tmpGWWithNodes!= null && tmpGWWithNodes.getSmartNodesVec()!=null){
                    Iterator<SmartNode> smVecIt = tmpGWWithNodes.getSmartNodesVec().iterator();
                    while(smVecIt.hasNext()) {
                        SmartNode tmpSmart =  smVecIt.next();
                        tmpSmart.getRegistryProperties().setEnabled(true);
                        tmpSmart.getRegistryProperties().setEnabledStatusWasInitiatedByVGW(false);
                        tmpSmart.getRegistryProperties().setTimeStampEnabledStatusRemotelySynch(0);
                        tmpSmart.getRegistryProperties().setTimeStampEnabledStatusSynch(0);
                    }
                }
            }
        }

    }

    public String createMessageForVGWFromCache(String vgwId) {
        String retMsgStr = "";
        DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(vgwId);

        if(cacheOfEnabledNodeItems.containsKey(vgwId)
                && cacheOfEnabledNodeItems.get(vgwId)!=null && !cacheOfEnabledNodeItems.get(vgwId).isEmpty() && tmpDbRGw != null) {


            StringWriter outStringWriter = new StringWriter();
            WstxOutputFactory fout = new WstxOutputFactory();
            fout.configureForXmlConformance();
            SMOutputDocument doc = null;
            try{
                // output
                XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
                doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
                doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
                createInfoInDocument(doc, null, cacheOfEnabledNodeItems.get(vgwId));
                doc.closeRoot();
                retMsgStr = outStringWriter.toString();
                outStringWriter.close();
            } catch(Exception e) {
                logger.error("Errors encountered while attempting to print this message!");
                retMsgStr = "";
                e.printStackTrace();
            }
            logger.debug("Produced en/dis message to send to VGWid: " +vgwId+" :: " + retMsgStr);
            if(retMsgStr!=null && !retMsgStr.isEmpty()) {
                StringBuilder toAddHeaderBld = new StringBuilder();
                // based on a UserNodeResponse structure we should have a queryId (which here is the messageType again, as src which should be the VSPCore, and a body)
                toAddHeaderBld.append(DisabledNodesVGWSynch.fromVSPMsgType);
                toAddHeaderBld.append(UserNodeQuery.headerSpliter);
                if(UserNode.getUserNode() !=null ){
                    toAddHeaderBld.append(UserNode.getUserNode().getPeerID());
                }else {
                    toAddHeaderBld.append("unknownID");
                }

                toAddHeaderBld.append(UserNodeQuery.headerSpliter);
                toAddHeaderBld.append(retMsgStr);
                retMsgStr = toAddHeaderBld.toString();
            }
        }
        else {
            logger.error("Cache empty or target VGW to get Enabled lists is not a registered gateway!");
        }

        return retMsgStr ;
    }

    /**
     *
     * Creates XML structured info under the parent Element, in the specified StructuredDocument
     *  get all entries for a gateway from the memory
     *  return the message to be sent
     */
    public String createMessageForVGW(String vgwId) {
        String retMsgStr = "";
        DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(vgwId);
        if(tmpDbRGw != null) {
            Vector<EnabledNodesListItem> nodesInfoToSendVec = new Vector<EnabledNodesListItem>();
            if(UserNode.getUserNode() !=null ) {
                HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM = UserNode.getUserNode().getGatewaysToSmartDevsHM();
                if(gatewaysToSmartDevsHM.containsKey(vgwId)) {
                    GatewayWithSmartNodes tmpGWWithNodes = gatewaysToSmartDevsHM.get(vgwId);
                    if(tmpGWWithNodes!= null && tmpGWWithNodes.getSmartNodesVec()!=null){
                        Iterator<SmartNode> smVecIt = tmpGWWithNodes.getSmartNodesVec().iterator();
                        while(smVecIt.hasNext()) {
                            SmartNode nodeInMemTmp = smVecIt.next() ;
                            EnabledNodesListItem newNodeInfo = new EnabledNodesListItem();
                            newNodeInfo.setNodeId(nodeInMemTmp.getId());
                            newNodeInfo.setGwInitFlag(nodeInMemTmp.getRegistryProperties().isEnabledStatusWasInitiatedByVGW());
                            newNodeInfo.setOfRemoteTimestamp(nodeInMemTmp.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch());
                            newNodeInfo.setStatus(nodeInMemTmp.getRegistryProperties().isEnabled());
                            if(newNodeInfo.isValid()) {
                                nodesInfoToSendVec.addElement(newNodeInfo);
                            }
                        }
                    }
                }
            }

            StringWriter outStringWriter = new StringWriter();
            WstxOutputFactory fout = new WstxOutputFactory();
            fout.configureForXmlConformance();
            SMOutputDocument doc = null;
            try{
                // output
                XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
                doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
                doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
                createInfoInDocument(doc, null, nodesInfoToSendVec);
                doc.closeRoot();
                retMsgStr = outStringWriter.toString();
                outStringWriter.close();
            } catch(Exception e) {
                logger.error("Errors encountered while attempting to print this message!");
                retMsgStr = "";
                e.printStackTrace();
            }
            logger.debug("Produced en/dis message to send to VGWid: " +vgwId+" :: " + retMsgStr);
            if(retMsgStr!=null && !retMsgStr.isEmpty()) {
                StringBuilder toAddHeaderBld = new StringBuilder();
                // based on a UserNodeResponse structure we should have a queryId (which here is the messageType again, as src which should be the VSPCore, and a body)
                toAddHeaderBld.append(DisabledNodesVGWSynch.fromVSPMsgType);
                toAddHeaderBld.append(UserNodeQuery.headerSpliter);
                if(UserNode.getUserNode() !=null ){
                    toAddHeaderBld.append(UserNode.getUserNode().getPeerID());
                }else {
                    toAddHeaderBld.append("unknownID");
                }

                toAddHeaderBld.append(UserNodeQuery.headerSpliter);
                toAddHeaderBld.append(retMsgStr);
                retMsgStr = toAddHeaderBld.toString();
            }
        }
        else {
            logger.error("Target VGW to get Enabled lists is not a registered gateway!");
        }
        return retMsgStr;
    }

    /**
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document. it could also be null.
     * @param nodesInfoToSendVec
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement, Vector<EnabledNodesListItem> nodesInfoToSendVec) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(DisabledNodesVGWSynch.TAG_ROOT_REQ);
            }
            else {
                tmpElementOuter =  document.addElement(DisabledNodesVGWSynch.TAG_ROOT_REQ);
            }

            tmpElement1 =  tmpElementOuter.addElement(DisabledNodesVGWSynch.TAG_MSGTYPE);
            tmpElement1.addCharacters(  DisabledNodesVGWSynch.fromVSPMsgType);

            // get node properties!
            if (nodesInfoToSendVec.size() > 0) {
                tmpElement1 =  tmpElementOuter.addElement(DisabledNodesVGWSynch.TAG_LIST);
                for (int k = 0; k < nodesInfoToSendVec.size(); k++) {
                    nodesInfoToSendVec.get(k).createInfoInDocument( document, tmpElement1);
                }
            }

            //timestamp tag
            tmpElement1 = tmpElementOuter.addElement(DisabledNodesVGWSynch.TAG_TIMESTAMP);
            long nowDateTS = new Date().getTime();
            tmpElement1.addCharacters(Long.toString(nowDateTS));

        } catch(Exception e) {
            logger.error(e.getMessage());
        }
        //
        //

    }


    /**
     *
     * parse the incoming message for nodes list confirmation
     * update the in-mem table when needed!
     * @param stream the InputStream source of the message from the VGW
     */
    public void parseVGWMsg(InputStream stream) { //throws IOException {
        String pVgwId = "";
        Vector<EnabledNodesListItem> confirmedEnabledNodesVec = new Vector<EnabledNodesListItem>();
        long timestamp = 0;
        long currentTimestamp = (new Date()).getTime();

        XMLStreamReader2 sr = null;
        try{
            WstxInputFactory f = null;

            SMInputCursor inputRootElement = null;

            f = new WstxInputFactory();
            f.configureForConvenience();
            // Let's configure factory 'optimally'...
            f.setProperty(XMLInputFactory.IS_COALESCING, Boolean.FALSE);
            f.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);

            sr = (XMLStreamReader2)f.createXMLStreamReader(stream);
            inputRootElement = SMInputFactory.rootElementCursor(sr);
            // If we needed to store some information about preceding siblings,
            // we should enable tracking. (we need it for  mygetElementValueStaxMultiple method)
            inputRootElement.setElementTracking(SMInputCursor.Tracking.PARENTS);

            inputRootElement.getNext();
            SMInputCursor childInElement = inputRootElement.childCursor();
            while (childInElement.getNext() != null) {
                if(!childInElement.getCurrEvent().hasText() ) {
                    if( childInElement.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_MSGTYPE) == 0 ) {

                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        String tmpMessageTypeValue = "";
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpMessageTypeValue = childInElement2.getText();
                                logger.debug(tmpMessageTypeValue);
                                break;
                            }
                        }
                        if(tmpMessageTypeValue.compareToIgnoreCase(DisabledNodesVGWSynch.fromVGWMsgType) != 0 )
                        {
                            logger.error("This is not the expected type of message (DisabledNodesVGWSynch Response)");
                            //throw new IOException(); // (++++) maybe throw some other kind of exception
                            return;
                        }
                        //Model3dStylesList.getListofStyleEntriesVec().add(new Model3dStylesEntry(childInElement));
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_VGW_ID) == 0 )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                pVgwId = childInElement2.getText();
                                DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pVgwId);
                                if(tmpDbRGw == null) {
                                    logger.error("Equiv list en/dis message received from invalid GW id");
                                    // TODO: deal with this
                                }
                                logger.debug(pVgwId);
                                break;
                            }
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_TIMESTAMP) == 0  ) {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                try {
                                    timestamp = Long.valueOf( childInElement2.getText());
                                }
                                catch (Exception efrmt) {
                                    logger.error("timestamp format exception");
                                    timestamp = 0;
                                }
                                logger.debug(Long.toString(timestamp));
                                break;
                            }
                        }
                    }
                    else if (childInElement.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_CONFIRMED_LIST ) == 0) {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null) {
                            if(!childInElement2.getCurrEvent().hasText() && childInElement2.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_CONFIRMED_ITEM) ==0 ) {

                                EnabledNodesListItem candNode = new EnabledNodesListItem();
                                SMInputCursor childInElement3 = childInElement2.childCursor();
                                while(childInElement3.getNext() != null) {

                                    if(!childInElement3.getCurrEvent().hasText() ) {
                                        if( childInElement3.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_NODE_ID) == 0 ) {

                                            SMInputCursor childInElement4 = childInElement3.childMixedCursor();
                                            while (childInElement4.getNext() != null)
                                            {
                                                if(childInElement4.getCurrEvent().hasText())
                                                {
                                                    candNode.setNodeId(childInElement4.getText());
                                                    break;
                                                }
                                            }
                                        } else if(childInElement3.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_NODE_STATUS) == 0 ) {

                                            SMInputCursor childInElement4 = childInElement3.childMixedCursor();
                                            while (childInElement4.getNext() != null)
                                            {
                                                if(childInElement4.getCurrEvent().hasText())
                                                {
                                                    if (childInElement4.getText().trim().compareToIgnoreCase("enabled") == 0) {
                                                        candNode.setStatus(true);
                                                    }  else if(childInElement4.getText().trim().compareToIgnoreCase("disabled") == 0) {
                                                        candNode.setStatus(false);
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if(childInElement3.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_REMOTE_TS) == 0 ) {

                                            SMInputCursor childInElement4 = childInElement3.childMixedCursor();
                                            while (childInElement4.getNext() != null)
                                            {
                                                if(childInElement4.getCurrEvent().hasText())
                                                {
                                                    long candTs = 0;   // the VGW will send messages with 0 remoteTimestamp when initiated (and also probably when something went wrong)
                                                    try {
                                                        candTs = Long.valueOf( childInElement4.getText());
                                                    }
                                                    catch (Exception efrmt) {
                                                        logger.error("timestamp format exception");
                                                    }
                                                    if(candTs == 0) {
                                                        candTs = currentTimestamp;
                                                    }
                                                    candNode.setOfRemoteTimestamp(candTs);
                                                    break;
                                                }
                                            }
                                        }   else if(childInElement3.getLocalName().compareToIgnoreCase(DisabledNodesVGWSynch.TAG_GW_INITIATED) == 0 ) {

                                            SMInputCursor childInElement4 = childInElement3.childMixedCursor();
                                            while (childInElement4.getNext() != null)
                                            {
                                                if(childInElement4.getCurrEvent().hasText())
                                                {
                                                    boolean gwInitiatedTheStatus = false;

                                                    if(childInElement4.getText().compareToIgnoreCase("1") == 0) {
                                                        gwInitiatedTheStatus = true;
                                                    }
                                                    candNode.setGwInitFlag(gwInitiatedTheStatus);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }

                                if(candNode.isValid() ) {
                                    confirmedEnabledNodesVec.addElement(candNode);
                                }
                            }
                        }
                    }

                }
            }
            logger.debug("Parsed en/dis message from VGW: " +pVgwId+" successfully!!");




            if(!cacheOfTimestampsForMessagesFromVGW.containsKey(pVgwId)) {
                cacheOfTimestampsForMessagesFromVGW.put(pVgwId, currentTimestamp);
            }
            Vector<EnabledNodesListItem>  confirmed_AND_FOUND_EnabledNodesVec = new Vector<EnabledNodesListItem>();
            if(!confirmedEnabledNodesVec.isEmpty() && UserNode.getUserNode() !=null ) {
                HashMap<String, GatewayWithSmartNodes> gatewaysToSmartDevsHM = UserNode.getUserNode().getGatewaysToSmartDevsHM();
                if(gatewaysToSmartDevsHM.containsKey(pVgwId)) {
                    GatewayWithSmartNodes tmpGWWithNodes = gatewaysToSmartDevsHM.get(pVgwId);
                    if(tmpGWWithNodes!= null && tmpGWWithNodes.getSmartNodesVec()!=null){
                        Iterator<SmartNode> smVecIt = tmpGWWithNodes.getSmartNodesVec().iterator();
                        while(smVecIt.hasNext()) {
                            SmartNode nodeInMemTmp = smVecIt.next() ;
                            EnabledNodesListItem nodeItMatched = null;
                            boolean foundAndHandledMatch = false;
                            for(EnabledNodesListItem nodeIt: confirmedEnabledNodesVec) {
                                //check if it exists in memory and change its properties!

                                if(nodeIt.getNodeId().compareToIgnoreCase(nodeInMemTmp.getId()) == 0) {
                                    //match with timestamp for confirmation
                                    logger.debug("Match for node: " + nodeInMemTmp.getId() + " with timestamp: "+ nodeInMemTmp.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch());
                                    if(nodeInMemTmp.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch() == nodeIt.getOfRemoteTimestamp() ||
                                         (nodeInMemTmp.getRegistryProperties().getTimeStampEnabledStatusRemotelySynch() == 0 )    ){
                                        // sets the confirmation date to the current local time of the VSP  (time of message reception)
                                        nodeInMemTmp.getRegistryProperties().setTimeStampEnabledStatusSynch(currentTimestamp);
                                        nodeInMemTmp.getRegistryProperties().setEnabled(nodeIt.getStatus());
                                        // TODO: if the status was initiated by the VGW we could manage it here to set the status anew!
                                        // BUT SET THE STATUS ANYWAY!
                                        // TODO: if something special is done here (for initiated by VGW), the 2nd level cache should also be updated
                                        // TODO: so update the confirmedEnabledNodesVec item (nodeIt) as well
                                        nodeInMemTmp.getRegistryProperties().setEnabledStatusWasInitiatedByVGW(nodeIt.isGwInitFlag()); // this just logs the update from vgw, but TODO:  it should need more code to handle this case
                                        nodeItMatched = nodeIt;
                                        foundAndHandledMatch = true;
                                    }
                                    // +++++++++++++++++++++
                                    break;
                                }
                            }
                            if(foundAndHandledMatch) {
                                confirmed_AND_FOUND_EnabledNodesVec.addElement(nodeItMatched);
                            }
                        }
                    }
                }
            }
            //update the 2nd level cache
            // TODO: check if here we should keep only the found nodes (in mem) or all...
            cacheOfEnabledNodeItems.put(pVgwId, confirmed_AND_FOUND_EnabledNodesVec);

            logger.debug("Updated memory based on  en/dis message from VGW: " +pVgwId+" successfully!!");

        }
        catch (Exception ex)
        {
            logger.error("Error parsing the en/dis message from vgw");
            ex.printStackTrace();
        }
        finally {
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


    private class EnabledNodesListItem {
        private String nodeId;
        private boolean status;
        private long ofRemoteTimestamp;
        private boolean gwInitFlag;

        EnabledNodesListItem() {
            setNodeId("");
            setStatus(false);
            setOfRemoteTimestamp(0);
            setGwInitFlag(false);
        }

        boolean isValid() {
            if(getNodeId() != null && getNodeId().trim().compareToIgnoreCase("")!= 0
                &&   getOfRemoteTimestamp() > 0)   //TODO: this extra field condition (on the remote timestamp) allows to compact the messages send to vgws
            {
                return true;
            }
            return false;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public boolean getStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        public long getOfRemoteTimestamp() {
            return ofRemoteTimestamp;
        }

        public void setOfRemoteTimestamp(long ofRemoteTimestamp) {
            this.ofRemoteTimestamp = ofRemoteTimestamp;
        }

        public boolean isGwInitFlag() {
            return gwInitFlag;
        }

        public void setGwInitFlag(boolean gwInitFlag) {
            this.gwInitFlag = gwInitFlag;
        }

        public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {
            SMOutputElement tmpElementOuter = null;
            SMOutputElement tmpElement1;

            try{
                if (parElement != null) {
                    tmpElementOuter = parElement.addElement(DisabledNodesVGWSynch.TAG_ITEM);
                }
                else {
                    tmpElementOuter =  document.addElement(DisabledNodesVGWSynch.TAG_ITEM);
                }

                tmpElement1 =  tmpElementOuter.addElement(DisabledNodesVGWSynch.TAG_NODE_ID);
                tmpElement1.addCharacters( nodeId);


                tmpElement1 =  tmpElementOuter.addElement(DisabledNodesVGWSynch.TAG_NODE_STATUS);
                String myStatus = getStatus() ? "enabled" : "disabled";
                tmpElement1.addCharacters( myStatus);

                tmpElement1 =  tmpElementOuter.addElement(DisabledNodesVGWSynch.TAG_REMOTE_TS);
                String strRemoteTS = Long.toString(ofRemoteTimestamp);
                tmpElement1.addCharacters( strRemoteTS);


            }   catch(Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
