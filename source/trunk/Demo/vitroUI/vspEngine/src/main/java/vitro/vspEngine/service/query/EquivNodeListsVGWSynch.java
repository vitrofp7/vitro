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
import vitro.vspEngine.service.common.abstractservice.AbstractGatewayManager;
import vitro.vspEngine.service.common.abstractservice.AbstractSelectionOfSmartNodesManager;
import vitro.vspEngine.service.common.abstractservice.AbstractSetOfEquivNodesManager;
import vitro.vspEngine.service.common.abstractservice.model.SetOfEquivalentSensorNodes;
import vitro.vspEngine.service.engine.UserNode;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;
import vitro.vspEngine.service.persistence.DBSmartNodeOfGateway;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class that creates the messages to send to a VGW for synching equiv lists
 * Also parses the confirmation responses for the equiv lists
 * Careful here: in the DB the field timestampUpdateLocal (tells the timestamp at the VSp when the request for a list-update-synch was created)
 *                              and the timestampSynchedRemote tells the timestamp that the synch message arrived from the VGW!
 */

public class EquivNodeListsVGWSynch {

    private static Logger logger = Logger.getLogger(EquivNodeListsVGWSynch.class);

    private static final String TAG_ROOT_REQ = "EquivListNodesReq";
    private static final String TAG_MSGTYPE ="message-type" ;
    private static final String TAG_VGW_ID ="vgwId";
    private static final String TAG_TIMESTAMP ="timestamp";
    private static final String TAG_LIST ="equivNodesList";
    private static final String TAG_ITEM ="equivNodesListItem";
    private static final String TAG_LIST_ID ="listId";
    private static final String TAG_REMOTE_TS ="ofRemoteTimestamp";

    private static final String TAG_ROOT_RESP = "EquivListNodesResp";
    private static final String TAG_CONFIRMED_LIST ="confirmedNodesList";
    private static final String TAG_CONFIRMED_ITEM ="confirmedNodesListItem";
    private static final String TAG_NODE_VEC ="nodeVec";
    private static final String TAG_NODE_ID ="nodeId";

    public static final String fromVSPMsgType = "EqvLstSynchReq";
    public static final String fromVGWMsgType = "EqvLstSynchResp";

    //singleton
    private static EquivNodeListsVGWSynch instance = null;


    private EquivNodeListsVGWSynch() {

    }

    public static EquivNodeListsVGWSynch getInstance() {
        if(instance == null){
            instance = new EquivNodeListsVGWSynch();
        }
        return instance;
    }


    //get all entries for a gateway from the DB table,
    //return the message to be sent
    /**
     *
     * Creates XML structured info under the parent Element, in the specified StructuredDocument
     *  get all entries for a gateway from the memory
     *  return the message to be sent
     */
    public String createMessageForVGW(String vgwId) {
        String retMsgStr = "";
        DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(vgwId);
        HashMap<Integer,SetOfEquivalentSensorNodes > cacheOfEquivListHM = new HashMap<Integer,SetOfEquivalentSensorNodes >();
        HashMap<Integer,List<DBSmartNodeOfGateway>> cacheOfNodeSelectionWithEquivLists = new  HashMap<Integer,List<DBSmartNodeOfGateway>>();
        if(tmpDbRGw!=null)
        {
            AbstractSetOfEquivNodesManager abstractSetOfEquivNodesManager = AbstractSetOfEquivNodesManager.getInstance();
            List<SetOfEquivalentSensorNodes> allSetOfEquivalentSensorNodesList = abstractSetOfEquivNodesManager.getSetOfEquivNodesListForGwId(vgwId);
            for (SetOfEquivalentSensorNodes setEqTmpIter : allSetOfEquivalentSensorNodesList)
            {
                if(!cacheOfEquivListHM.containsKey(setEqTmpIter.getId())) {
                    cacheOfEquivListHM.put(setEqTmpIter.getId(), setEqTmpIter);

                    DBSelectionOfSmartNodes curSelectionSMNds = setEqTmpIter.getInterchngblNodes();
                    if(curSelectionSMNds!=null) {
                        AbstractSelectionOfSmartNodesManager abstractSelectionOfSmartNodesManager = AbstractSelectionOfSmartNodesManager.getInstance();
                        curSelectionSMNds = abstractSelectionOfSmartNodesManager.getSelectionOfSmartNodes(curSelectionSMNds.getId());
                        List<DBSmartNodeOfGateway> theNodesList = curSelectionSMNds.getDBSmartNodeOfGatewayList();
                        if(theNodesList!=null) {
                            cacheOfNodeSelectionWithEquivLists.put(setEqTmpIter.getId(), theNodesList);

                        }  else {
                            cacheOfNodeSelectionWithEquivLists.put(setEqTmpIter.getId(), new ArrayList<DBSmartNodeOfGateway>());
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
                createInfoInDocument(doc, null, cacheOfEquivListHM, cacheOfNodeSelectionWithEquivLists);
                doc.closeRoot();
                retMsgStr = outStringWriter.toString();
                outStringWriter.close();
            } catch(Exception e) {
                logger.error("Errors encountered while attempting to print this message!");
                retMsgStr = "";
                e.printStackTrace();
            }
            logger.debug("Produced equiv list message to send to VGWid: " +vgwId+" :: " + retMsgStr);
            if(retMsgStr!=null && !retMsgStr.isEmpty()) {
                StringBuilder toAddHeaderBld = new StringBuilder();
                // based on a UserNodeResponse structure we should have a queryId (which here is the messageType again, as src which should be the VSPCore, and a body)
                toAddHeaderBld.append(EquivNodeListsVGWSynch.fromVSPMsgType);
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
            logger.error("Target VGW to get Equiv Lists was not a registered gateway!");
        }
        return retMsgStr;
    }

    /**
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document. it could also be null.
     * @param cacheOfEquivListHM
     * @param cacheOfNodeSelectionWithEquivLists
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement,
                                     HashMap<Integer,SetOfEquivalentSensorNodes > cacheOfEquivListHM,
                                     HashMap<Integer,List<DBSmartNodeOfGateway>> cacheOfNodeSelectionWithEquivLists) {
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(EquivNodeListsVGWSynch.TAG_ROOT_REQ);
            }
            else {
                tmpElementOuter =  document.addElement(EquivNodeListsVGWSynch.TAG_ROOT_REQ);
            }

            tmpElement1 =  tmpElementOuter.addElement(EquivNodeListsVGWSynch.TAG_MSGTYPE);
            tmpElement1.addCharacters(EquivNodeListsVGWSynch.fromVSPMsgType);

            // get all lists
            tmpElement1 =  tmpElementOuter.addElement(EquivNodeListsVGWSynch.TAG_LIST); // need to have this tag, even if no lists exist
            if (!cacheOfEquivListHM.isEmpty() && !cacheOfNodeSelectionWithEquivLists.isEmpty()) {

                for ( Integer equivListId : cacheOfEquivListHM.keySet()) {
                    if(cacheOfNodeSelectionWithEquivLists.containsKey(equivListId) && cacheOfEquivListHM.get(equivListId)!= null) {
                        createInfoInDocumentForEquivList( document, tmpElement1, cacheOfEquivListHM.get(equivListId), cacheOfNodeSelectionWithEquivLists.get(equivListId));
                    }
                }
            }

            //timestamp tag
            tmpElement1 = tmpElementOuter.addElement(EquivNodeListsVGWSynch.TAG_TIMESTAMP);
            long nowDateTS = new Date().getTime();
            tmpElement1.addCharacters(Long.toString(nowDateTS));

        } catch(Exception e) {
            logger.error(e.getMessage());
        }
        //
        //

    }

    // aux functions for sub- XML elements
    private void createInfoInDocumentForEquivList(SMOutputDocument document, SMOutputElement parElement,SetOfEquivalentSensorNodes equivList, List<DBSmartNodeOfGateway> listOfSmNodes){
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(EquivNodeListsVGWSynch.TAG_ITEM);
            }
            else {
                tmpElementOuter =  document.addElement(EquivNodeListsVGWSynch.TAG_ITEM);
            }

            tmpElement1 =  tmpElementOuter.addElement(EquivNodeListsVGWSynch.TAG_LIST_ID);
            tmpElement1.addCharacters(Integer.toString(equivList.getId()));

            Date tsLocalFromDB = equivList.getTimestampUpdateLocal();
            String tsLocalFromDBLongStr = "";
            if(tsLocalFromDB!=null) {
                tsLocalFromDBLongStr= Long.toString(tsLocalFromDB.getTime());
            }
            //Date tsRemoteFromDB = equivList.getTimestampSynchedRemotely();
            //String tsRemoteFromDBLongStr = "";
            //if(tsRemoteFromDB!=null) {
            //    tsRemoteFromDBLongStr= Long.toString(tsRemoteFromDB.getTime());
            //}

            tmpElement1 =  tmpElementOuter.addElement(EquivNodeListsVGWSynch.TAG_REMOTE_TS);
            tmpElement1.addCharacters(tsLocalFromDBLongStr);   // correct because local for VSP , is remote for VGW

            tmpElement1 =  tmpElementOuter.addElement(EquivNodeListsVGWSynch.TAG_NODE_VEC); //need to have this tag even if no nodes!
            // if marked to be deleted, also don't send nodes. (the id has to be send for synching the deletion.
            if (!listOfSmNodes.isEmpty() && !equivList.isMarkedTobeDeleted()) {
                for ( DBSmartNodeOfGateway nodeInlist : listOfSmNodes) {
                    createInfoInDocumentForSmartNodeOfGw(document, tmpElement1, nodeInlist);
                }
            }

        }   catch(Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void createInfoInDocumentForSmartNodeOfGw(SMOutputDocument document, SMOutputElement parElement,DBSmartNodeOfGateway smNode){
        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;

        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(EquivNodeListsVGWSynch.TAG_NODE_ID);
            }
            else {
                tmpElementOuter =  document.addElement(EquivNodeListsVGWSynch.TAG_NODE_ID);
            }
            tmpElementOuter.addCharacters(smNode.getIdWithinGateway());

        }   catch(Exception e) {
            logger.error(e.getMessage());
        }
    }

    // end of AUX Functions  for sub- XML elements

    /**
     *
     * parse the incoming message for equiv lists confirmation
     * update the DB table when needed!
     * @param stream the InputStream source of the message from the VGW
     */
    public void parseVGWMsg(InputStream stream) { //throws IOException {
        String pVgwId = "";

        long timestamp = 0;
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
                    if( childInElement.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_MSGTYPE) == 0 ) {

                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        String tmpMessageTypeValue = "";
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                tmpMessageTypeValue = childInElement2.getText();
                                break;
                            }
                        }
                        if(tmpMessageTypeValue.compareToIgnoreCase(EquivNodeListsVGWSynch.fromVGWMsgType) != 0 )
                        {
                            logger.error("This is not the expected type of message (EquivNodeListsVGWSynch Response)");
                            //throw new IOException(); // (++++) maybe throw some other kind of exception
                            return;
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_VGW_ID) == 0 )
                    {
                        SMInputCursor childInElement2 = childInElement.childMixedCursor();
                        while (childInElement2.getNext() != null)
                        {
                            if(childInElement2.getCurrEvent().hasText())
                            {
                                pVgwId = childInElement2.getText();
                                DBRegisteredGateway tmpDbRGw = AbstractGatewayManager.getInstance().getDBRegisteredGatewayByName(pVgwId);
                                if(tmpDbRGw == null) {
                                    logger.error("Equiv list synch message received from invalid GW id");
                                    // TODO: deal with this
                                }

                                break;
                            }
                        }
                    }
                    else if( childInElement.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_TIMESTAMP) == 0  ) {
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
                                break;
                            }
                        }
                    } else if (childInElement.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_CONFIRMED_LIST ) == 0) {
                        SMInputCursor childInElement2 = childInElement.childCursor();
                        while (childInElement2.getNext() != null) {
                            if(!childInElement2.getCurrEvent().hasText() && childInElement2.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_CONFIRMED_ITEM) ==0 ) {

                                String listIdTmp = "";
                                int listIdTmpInt = -1;
                                long remoteOriginalTS = 0; // VSP's timestamp (which is the localTS column in the DB!!)
                                SMInputCursor childInElement3 = childInElement2.childCursor();
                                while(childInElement3.getNext() != null) {

                                    if(!childInElement3.getCurrEvent().hasText() ) {
                                        if( childInElement3.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_LIST_ID) == 0 ) {

                                            SMInputCursor childInElement4 = childInElement3.childMixedCursor();
                                            while (childInElement4.getNext() != null)
                                            {
                                                if(childInElement4.getCurrEvent().hasText())
                                                {
                                                    listIdTmp = childInElement4.getText();
                                                    try {
                                                        listIdTmpInt = Integer.valueOf(listIdTmp);
                                                    } catch (Exception efrmt) {
                                                        logger.error("Equiv list id format error!");
                                                        listIdTmpInt = -1;
                                                    }
                                                    break;
                                                }
                                            }
                                        } else if(childInElement3.getLocalName().compareToIgnoreCase(EquivNodeListsVGWSynch.TAG_REMOTE_TS) == 0 ) {

                                            SMInputCursor childInElement4 = childInElement3.childMixedCursor();
                                            while (childInElement4.getNext() != null)
                                            {
                                                if(childInElement4.getCurrEvent().hasText())
                                                {
                                                    long candTs = 0;
                                                    try {
                                                        candTs = Long.valueOf( childInElement4.getText());
                                                    }
                                                    catch (Exception efrmt) {
                                                        logger.error("timestamp format exception");
                                                    }
                                                    remoteOriginalTS = candTs;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if(listIdTmpInt > 0 && listIdTmp.trim().compareToIgnoreCase("")!= 0
                                        &&   remoteOriginalTS > 0) {
                                 // THEN UPDATE THE CORRECT TIMESTAMP COLUMN IN THE DB (The RemoteTS will be set to this messages timestamp or better to the Date Now() because the VGW timestamp could be way off / un-synched etc)
                                 // WE STILL SHOULD MATCH AGAINST  remoteOriginalTS to see if we should update or not!
                                    long nowDtTSLong =new Date().getTime();
                                    AbstractSetOfEquivNodesManager abstractSetOfEquivNodesManager = AbstractSetOfEquivNodesManager.getInstance();
                                    SetOfEquivalentSensorNodes setOfEquivalentSensorNodes = abstractSetOfEquivNodesManager.getSetOfEquivNodes(listIdTmpInt);
                                    if(setOfEquivalentSensorNodes != null ) {
                                        long storedLocalTS = setOfEquivalentSensorNodes.getTimestampUpdateLocal().getTime();
                                        if(storedLocalTS == remoteOriginalTS) {
                                            logger.debug("Match for updating equiv list");
                                            if(setOfEquivalentSensorNodes.isMarkedTobeDeleted()) {
                                                logger.debug("Handling marked to be deleted equiv list");
                                                abstractSetOfEquivNodesManager.removeSetOfEquivNodes(listIdTmpInt);
                                            } else {
                                                abstractSetOfEquivNodesManager.updateSetOfEquivNodes(listIdTmpInt, nowDtTSLong);
                                            }
                                            logger.debug("UPDATED EQUIV LIST SUCCESSFULLY =-------------------------------------");
                                        }else {
                                            logger.debug("Mismatch for updating equiv list. Could not update!");
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("Error parsing the equiv lists synch message from vgw");
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
}
