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
 * QueryDefinitions.java
 *
 */

package vitro.vspEngine.service.query;

import org.apache.log4j.Logger;
import vitro.vspEngine.logic.model.Capability;
import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.logic.model.SmartNode;
import vitro.vspEngine.service.geo.GeoRegion;
import vitro.vspEngine.service.query.QueryProcessor;
import vitro.vspEngine.service.engine.UserNode;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.staxmate.SMInputFactory;
import org.codehaus.staxmate.SMOutputFactory;
import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;

import javax.xml.stream.XMLInputFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.Vector;
import java.util.regex.Pattern;


/**
 * This class represents the QueryDefinition objects that construct an IndexOfQueries file.
 * The XML representation should something like this:
 * <pre>
 * 	&lt;query&gt;
 *          &lt;friendlyName&gt;&lt;/friendlyName&gt; 		&lt;---------- Friendly name. Does not have to be unique
 *                                                  
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
 * </pre>
 *
 * @author antoniou
 */
public class QueryDefinition {
    private Logger logger = Logger.getLogger(QueryDefinition.class);
    String m_uQid;
    private String m_friendlyName;
    QueryContentDefinition m_content;
    private long initCreationTS;
    private int desiredPeriod;  // amount of time in seconds for periodic re-issuing of the query
    private int desiredHistory; // the number of result files that should be maintained in "cache" for this query
    private boolean aggregateQueryFlag; // Set the aggregation mode on for queries sent per gateway. This affects ONLY the query for text value, not the ones for binary data (pipes)
    private boolean continuationEnabledFlag;
    private boolean asynchronousFlag;
    private boolean dtnEnabledFlag;
    private boolean securityEnabledFlag;
    private boolean encryptionEnabledFlag;


    private int runningStatus;

    private Vector<NotificationsFromVSNs> notificationsFromVSNsVec;
    private Vector<FinalResultEntryPerDef> filesVec; // the result files for this query definition

    // finals
    private static final int getLatestFile = -1;

    public static final int getNoHistory = 1;  // TODO: why 1 and not 0? was there a specific reason for this?
    public static final int keepAllHistory = -1;
    public static final int noPeriodicSubmission = -1;
    
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_PAUSED = 1;
    
    private static final String idNotSet = "IdNotSet";
    private static final String contentNotSet = "contentNotSet";
    private static final String friendlyNameDefaultPrefix = "";
    private static final String friendlyNameNotSet = "";
    private static final long   initCreationTSNotSet = 0;

    private static final String myQuidTag = "uqid";
    private static final String myFriendlyNameTag = "friendlyName";
    private static final String mycreationTSTag = "creationTS";
    private static final String periodTag = "desiredPeriod";
    private static final String historyTag = "desiredHistory";
    private static final String aggregateTag = "isAggregate";
    private static final String continuationTag ="servContinuation";  // TODO
    private static final String asynchronousTag ="isAsynchronous";  // TODO provide support for VGW engine to send replies asynchronously (this requires also a capability to send a message to VGW to stop the asynch subscription).
    private static final String dtnTag ="useDTN";  // TODO
    private static final String securityTag ="securityAlerts";  // TODO
    private static final String encryptionTag ="useEncryption";  // TODO

    private static final String contentTag = "content";
    private static final String queryResultFileTag = "queryResultFile";


    /**
     * Default constructor
     */
    public QueryDefinition() {
        this.m_uQid = QueryDefinition.idNotSet;
        long now = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ");
        String quidCreationTSStr = df.format(new Date(now));
        this.m_friendlyName = friendlyNameDefaultPrefix + quidCreationTSStr;
        this.initCreationTS = now;
        this.m_content = new QueryContentDefinition();
        this.desiredPeriod = QueryDefinition.noPeriodicSubmission;
        this.desiredHistory = QueryDefinition.getNoHistory;
        this.setAggregateQueryFlag (false);
        this.setAsynchronousFlag(false);
        this.setContinuationEnabledFlag(false);
        this.setDtnEnabledFlag(false);
        this.setEncryptionEnabledFlag(false);
        this.setSecurityEnabledFlag(false);
        this.filesVec = new Vector<FinalResultEntryPerDef>();
        this.notificationsFromVSNsVec= new Vector<NotificationsFromVSNs>();
        this.runningStatus = STATUS_RUNNING;
    }

    /**
     * Creates a new instance of QueryDefinitions
     */
    public QueryDefinition(String uQid, QueryContentDefinition content) {
        this.m_uQid = uQid;
        long now = System.currentTimeMillis();
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZ");
        String quidCreationTSStr = df.format(new Date(now));
        this.m_friendlyName = friendlyNameDefaultPrefix + quidCreationTSStr;
        initCreationTS = now;
        //Timestamp initCreationTSSql = new Timestamp(now);
        
        this.m_content = content;
        this.desiredPeriod = QueryDefinition.noPeriodicSubmission;
        this.desiredHistory = QueryDefinition.getNoHistory;
        this.setAggregateQueryFlag (false);
        this.setAsynchronousFlag(false);
        this.setContinuationEnabledFlag(false);
        this.setDtnEnabledFlag(false);
        this.setEncryptionEnabledFlag(false);
        this.setSecurityEnabledFlag(false);

        this.filesVec = new Vector<FinalResultEntryPerDef>();
        this.notificationsFromVSNsVec= new Vector<NotificationsFromVSNs>();
        this.runningStatus = STATUS_RUNNING;        
    }

    /**
     * Creates a new instance of QueryDefinition
     *
     * @param givenCursor the XML part of a query (As a TextElemet) that describes the Query Definition
     */
    public QueryDefinition(SMInputCursor givenCursor) {
        // (To do)

    }

    public void addQueryResultFile(FinalResultEntryPerDef resfilestruct) {
        //
        // create Final file in filesystem
        //
        //  then update this.filesVec with filepath
        //
        if (resfilestruct.writeBackToFile(this.m_uQid)) {
            resfilestruct.setQueryDefUId(this.m_uQid) ;
            this.filesVec.addElement(resfilestruct);
        }
        return;
    }

    public void addNotificationForVSN(NotificationsFromVSNs notificationStruct) {
        //
        this.notificationsFromVSNsVec.addElement(notificationStruct);
        return;
    }

    synchronized public FinalResultEntryPerDef getLatestQueryResultFile() {
        //
        FinalResultEntryPerDef entryToReturn = null;
        Timestamp latestTime = null;
        for (int i = 0; i < this.getFilesVec().size(); i++) {
            if (i == 0 ||
                    (latestTime != null && latestTime.before(this.getFilesVec().elementAt(i).getCreationDate()))) {
                latestTime = this.getFilesVec().elementAt(i).getCreationDate();
                entryToReturn = this.getFilesVec().elementAt(i);
            }
        }
        return entryToReturn;
    }

    public FinalResultEntryPerDef processQueryAndfindResults(int givenTimeoutPeriod) {
        // TODO: ultimately this should be fixed with inheritence or an interface (new) 
        if ( UserNode.getUserNode() == null)
            return null;

        QueryProcessor qp = new QueryProcessor(this, givenTimeoutPeriod);
        FinalResultEntryPerDef results = qp.analyzeAndFindResults();
        return results;
    }

    /**
     * Does not send any queries. Just returns the "translation" of the query definition to a hashmap of
     * candidate gateways and unique smart devices of theirs (duplicates have been eliminated), that will be possibly sent a query.
     * However, it is not certain that all of these smart devices will be sent a query, because some of them will be eliminated later on,
     * if they don't support the requested capability.
     * For a list of motes who are really-finally queried, you can check the FinalResultEntryPerDef object.
     * This list is used for purposes of showing the translation of the query definition to smart devices.
     */
    public HashMap<Gateway, Vector<SmartNode>> processQueryAndOnlyGetAnalysedCandidateHM() {
        // TODO: ultimately this should be fixed with inheritence or an interface (new)    
        if (UserNode.getUserNode() == null)
            return null;
        QueryProcessor qp = new QueryProcessor(this, -1); // timeoutperiod is irrelevant and set to a dummy value! No queries will be sent!
        HashMap<Gateway, Vector<SmartNode>> resultHM = qp.getGatewayToMotesForWhichQueryWillBeSentHM();
        return resultHM;
    }

    // getters
    public Vector<FinalResultEntryPerDef> getFilesVec() {
        return filesVec;
    }

    public Vector<NotificationsFromVSNs> getNotificationsFromVSNsVec() {
        return notificationsFromVSNsVec;
    }


    public int getDesiredPeriod() {
        return desiredPeriod;
    }

    public QueryContentDefinition getQContent() {
        return m_content;
    }

    public String getuQid() {
        return m_uQid;
    }

    public int getDesiredHistory() {
        return desiredHistory;
    }

    public boolean isAggregateQueryFlag() {
        return aggregateQueryFlag;
    }

    public static String getQueryResultFileTag() {
        return queryResultFileTag;
    }

    public static String getContentTag() {
        return contentTag;
    }

    public int getRunningStatus() {
        return runningStatus;
    }

    // setters
    public void setDesiredHistory(int desiredHistory) {
        this.desiredHistory = desiredHistory;
    }

    public void setDesiredPeriod(int givDesiredPeriod) {
        if (givDesiredPeriod <= 0)
            this.desiredPeriod = QueryDefinition.noPeriodicSubmission;
        else
            this.desiredPeriod = givDesiredPeriod;
    }

    public void setAggregateQueryFlag(boolean aggregateQueryFlag) {
        this.aggregateQueryFlag = aggregateQueryFlag;
    }


    public void setFilesVec(Vector<FinalResultEntryPerDef> filesVec) {
        this.filesVec = filesVec;
    }

    public void setNotificationsFromVSNsVec(Vector<NotificationsFromVSNs> pnotificationsFromVSNsVec) {
        this.notificationsFromVSNsVec = pnotificationsFromVSNsVec;
    }

    /**
     * Sets the running status of a query.
     * if an invalid running status is given as a parameter, then the status will be set to the default which is "running".
     * @param givRunningStatus    Can be STATUS_PAUSED or STATUS_RUNNING
     */
    public void setRunningStatus(int givRunningStatus) {
        if(givRunningStatus!= this.STATUS_PAUSED && givRunningStatus!= this.STATUS_RUNNING)
            this.runningStatus = this.STATUS_RUNNING;
        else            
            this.runningStatus = givRunningStatus;
    }
    
    /**
     * Method createFunctionInfoInDocument:
     *
     * @param document    the desired MIME type representation for the query.
     * @param parElement  the parent element in the given XML document
     * @param verboseflag sets whether we want to list the results info inside the XML or not at all.
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement, boolean verboseflag) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;


        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement( IndexOfQueries.getQueryDefTag());
            }
            else {
                tmpElementOuter =  document.addElement( IndexOfQueries.getQueryDefTag()); //special case for PublicResponseAggrMsg creation
            }

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.myQuidTag );
            tmpElement1.addCharacters(  this.getuQid());

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.myFriendlyNameTag );
            tmpElement1.addCharacters(  this.getFriendlyName());

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.mycreationTSTag );
            tmpElement1.addCharacters(  Long.toString(this.getInitCreationTS()));


            if (this.getQContent() != null && !this.getQContent().isEmptyQueryContent()) {
                this.getQContent().createInfoInDocument(document, tmpElementOuter);
            }

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.periodTag );
            tmpElement1.addCharacters(  Integer.toString(this.getDesiredPeriod()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.historyTag );
            tmpElement1.addCharacters(  Integer.toString(this.getDesiredHistory()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.aggregateTag );
            tmpElement1.addCharacters(  Boolean.toString(this.isAggregateQueryFlag()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.continuationTag );
            tmpElement1.addCharacters(  Boolean.toString(this.isContinuationEnabledFlag()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.asynchronousTag );
            tmpElement1.addCharacters(  Boolean.toString(this.isAsynchronousFlag()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.dtnTag );
            tmpElement1.addCharacters(  Boolean.toString(this.isDtnEnabledFlag()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.securityTag );
            tmpElement1.addCharacters(  Boolean.toString(this.isSecurityEnabledFlag()));

            tmpElement1 =  tmpElementOuter.addElement(QueryDefinition.encryptionTag );
            tmpElement1.addCharacters(  Boolean.toString(this.isEncryptionEnabledFlag()));

            if (verboseflag && this.getFilesVec() != null && this.getFilesVec().size() > 0) {
                for (int i = 0; i < this.getFilesVec().size(); i++) {
                    this.getFilesVec().elementAt(i).createInfoInDocument(document, tmpElementOuter, false, false);
                }
            }

        } catch(Exception e) {
            return;
        }

        return;
    }

    /**
     * Method toString:
     * <p/>
     * no parameters
     *
     * @return the XML String representing this requested Query Definition XML fields
     */
    public String toString() {
        return this.toMyString(true);
    }

    /**
     * Method toMyString:
     *
     * @param verboseflag sets whether we want to list the results info inside the XML or not at all.
     * @return the XML String representing this requested Query Definition XML fields
     */
    public String toMyString(boolean verboseflag) {
        StringWriter outStringWriter = new StringWriter();
        WstxOutputFactory fout = new WstxOutputFactory();
        fout.configureForXmlConformance();
        SMOutputDocument doc = null;
        try{
            // output
            XMLStreamWriter2 sw = (XMLStreamWriter2)fout.createXMLStreamWriter(outStringWriter);
            doc = SMOutputFactory.createOutputDocument(sw, "1.0", "UTF-8", true);
            doc.setIndentation("\r\n\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t", 2, 1);
            createInfoInDocument(doc, null, verboseflag);
            doc.closeRoot();
        } catch(Exception e) {
            e.printStackTrace();
            return "Errors encountered while attempting to print this Query Definition!";
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

    /**
     * Prints the values of the class variables of the calling QueryProcessor object
     */
    public void printInput() {
        System.out.println(getDetailsInText(false));        
    }

    public String getDetailsInHtml()
    {
        return getDetailsInText(true);
    }
    
    private String toBold(String targStr, boolean isHtml)
    {
        String boldDelimiterStart = "*";
        String boldDelimiterEnd = "*";
        if (isHtml)
        {
            boldDelimiterStart = "<b>";
            boldDelimiterEnd = "</b>";
            
        }
        return boldDelimiterStart+targStr+boldDelimiterEnd;
    }

    /**
     * TODO: this returns gateway Ids explicitly mentioned in the query definition.
     * TODO: A more correct version would also evaluate any defined region sets in the query definition to add the gateways included there!
     *
     *
     * @return   a set of the explicitly mentioned gateways in the query definition!
     */
    public Set<String> getInvolvedGatewayIds()
    {
        Set<String> retSet = new HashSet<String>();   //since this is a hashset, adding the same value twice won't create duplicates in the set!
        for (Map.Entry<Integer, HashMap<String, Vector<String>>> devicesSelectionHMEntry : this.getQContent().getDevicesSelectionHM().entrySet()) {
            for (Map.Entry<String,  Vector<String>> gwTodevicesEntry : devicesSelectionHMEntry.getValue().entrySet()) {
                String tmpGateId = gwTodevicesEntry.getKey();
                retSet.add(tmpGateId);
            }
        }
        return  retSet;
    }

    /**
     *  TODO: Adapt to new query Definition Schema and extra settings!
     * @param isHtml
     * @return A user-friendly string descriptive of the specific VSN/Service details
     */
    public String getDetailsInText(boolean isHtml)                       
    {
        String lineDelimiter = "\n";
        if (isHtml)
        {
            lineDelimiter = "<br />";
        }
        StringBuilder buildTheString = new StringBuilder();
        buildTheString.append(toBold("VSN Details", isHtml));
        buildTheString.append(lineDelimiter);
        if(isDtnEnabledFlag()) {
            buildTheString.append("DTN requested: ");
            buildTheString.append(isDtnEnabledFlag());
            buildTheString.append(lineDelimiter);
        }

        if(isContinuationEnabledFlag()) {
            buildTheString.append("Continuation requested: ");
            buildTheString.append(isContinuationEnabledFlag());
            buildTheString.append(lineDelimiter);
        }
//        buildTheString.append("-------------------------");
        buildTheString.append(lineDelimiter);      
        buildTheString.append("[Area(s) Selection(s)]");
        buildTheString.append(lineDelimiter);

        for (Map.Entry<Integer, Vector<GeoRegion>> areasSelectionHMEntry : this.getQContent().getAreasSelectionHM().entrySet()) {
            buildTheString.append("Selection ID: ");
            buildTheString.append(areasSelectionHMEntry.getKey());
            buildTheString.append(lineDelimiter);
            for (int i = 0; i < areasSelectionHMEntry.getValue().size(); i++) {
                buildTheString.append(areasSelectionHMEntry.getValue().get(i).printInfo());
                buildTheString.append(lineDelimiter);
            }
        }
//        buildTheString.append("Motes Selected: ");
//        buildTheString.append(lineDelimiter);       
        for (Map.Entry<Integer, HashMap<String, Vector<String>>> devicesSelectionHMEntry : this.getQContent().getDevicesSelectionHM().entrySet()) {
            buildTheString.append("Selection ID: ");
            buildTheString.append(devicesSelectionHMEntry.getKey());
            buildTheString.append(lineDelimiter);
            for (Map.Entry<String,  Vector<String>> gwTodevicesEntry : devicesSelectionHMEntry.getValue().entrySet()) {
                String tmpGateId = gwTodevicesEntry.getKey();
                buildTheString.append("Smart Devices from gateway: " + tmpGateId);
                buildTheString.append(lineDelimiter);
                for (int j = 0; j < gwTodevicesEntry.getValue().size(); j++) {
                    buildTheString.append(gwTodevicesEntry.getValue().elementAt(j));
                    buildTheString.append(lineDelimiter);
                }
            }
        }

//      buildTheString.append("---------");
        buildTheString.append(lineDelimiter);           
        buildTheString.append("[Requested Capabilities and Functions]");
        buildTheString.append(lineDelimiter);

        try{

            for (Map.Entry<Integer, HashMap<String, Vector<Integer>> > capabilitiesSelectionHMEntry : this.getQContent().getCapabilitiesSelectionHM().entrySet()) {
                buildTheString.append("Capability Selection ID: ");
                buildTheString.append(capabilitiesSelectionHMEntry.getKey());
                buildTheString.append(lineDelimiter);

                for (Map.Entry<String,  Vector<Integer>> capToFunctionsEntry : capabilitiesSelectionHMEntry.getValue().entrySet()) {
                    String tmpgCapName = capToFunctionsEntry.getKey();
                    // TODO: this renaming to simple value should be done elsewhere eventually. Prefix should not be explicit here
                    //
                    String tmpgCapNameSimple = tmpgCapName.replaceAll(Pattern.quote(Capability.dcaPrefix), "");
                    buildTheString.append("For Capability: ");
                    buildTheString.append(tmpgCapNameSimple);
                    buildTheString.append(lineDelimiter);
                    //            buildTheString.append("*************************");
                    //            buildTheString.append(lineDelimiter);

                    Vector<Integer> functionVec = capToFunctionsEntry.getValue();
                    for (int j = 0; j < functionVec.size(); j++) {
                        buildTheString.append("Function name: " );
                        if(ReqFunctionOverData.isValidGatewayReqFunct(this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getfuncName()))
                        {
                            String[] descriptionTokens = this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getfuncName().split("_");
                            if(descriptionTokens!=null && descriptionTokens.length>1)
                            {
                                buildTheString.append(descriptionTokens[1] + " over the selected nodes");
                            }
                            else
                                buildTheString.append(this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getfuncName());
                        }
                        else
                        {
                            buildTheString.append(this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getfuncName());
                        }
                        //thresholds handling
                        if(this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getfuncName().equalsIgnoreCase(ReqFunctionOverData.setValFunc))
                        {
                            ThresholdStructure tmpThresh = this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getRequiredThresholds();
                            if(tmpThresh != null)
                            {
                                buildTheString.append(": ");
                                buildTheString.append(tmpThresh.getLowerBound()); //it is "equals" so it won't matter which of the two it actually is (lower or upper)
                            }
                        }
                        else
                        {
                            ThresholdStructure tmpThresh = this.getQContent().getUniqueFunctionById(functionVec.elementAt(j)).getRequiredThresholds();
                            if(tmpThresh != null && tmpThresh.isLowerBoundSet())
                            {
                                buildTheString.append(": greater than ");
                                buildTheString.append(tmpThresh.getLowerBound());
                            }
                            if(tmpThresh != null && tmpThresh.isUpperBoundSet())
                            {
                                buildTheString.append(": lower than ");
                                buildTheString.append(tmpThresh.getUpperBound());
                            }
                        }
                        buildTheString.append(lineDelimiter);
                    }

                }
            }
            // TODO: make this more user friendly eventually!
            buildTheString.append(lineDelimiter);
            buildTheString.append("[Mapping Selections to Requested Capabilities]");
            buildTheString.append(lineDelimiter);
            for (Map.Entry<Integer, Integer> selDevsToSelCapsEntry : this.getQContent().getSelDevsToSelCaps().entrySet()) {
                buildTheString.append("Selection ID: ");
                buildTheString.append(selDevsToSelCapsEntry.getKey());
                buildTheString.append(" to Capability Selection ID: ");
                buildTheString.append(selDevsToSelCapsEntry.getValue());
                buildTheString.append(lineDelimiter);
            }
        }
        catch(Exception e) 
        {
            System.out.println(e.getMessage());
        }
            
        
        return buildTheString.toString();
    }

    /**
     *
     * @param quid
     * @return "Start" if the service is stopped, "Stop" if the service is started and "" if the service is not among the deployed ones.
     */
    public static String displayQueryStatusAction(String quid) {
        String actionToReturn = "Stop";
        if(quid == null || IndexOfQueries.getIndexOfQueries() == null || IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(quid) == null){
            actionToReturn = "";
        }
        else {
            int currStatus = IndexOfQueries.getIndexOfQueries().getQueryDefinitionById(quid).getRunningStatus();
            if (currStatus == QueryDefinition.STATUS_PAUSED)
                actionToReturn = "Start";
            else if (currStatus == QueryDefinition.STATUS_RUNNING)
                actionToReturn = "Stop";
        }
        return actionToReturn;
    }

    /**
     * @return the m_friendlyName
     */
    public String getFriendlyName() {
        return m_friendlyName;
    }

    /**
     * @param m_friendlyName the m_friendlyName to set
     */
    public void setFriendlyName(String m_friendlyName) {
        this.m_friendlyName = m_friendlyName;
    }

    /**
     * @return the initCreationTS
     */
    public long getInitCreationTS() {
        return initCreationTS;
    }

    /**
     * @param initCreationTS the initCreationTS to set
     */
    public void setInitCreationTS(long initCreationTS) {
        this.initCreationTS = initCreationTS;
    }


    public boolean isContinuationEnabledFlag() {
        return continuationEnabledFlag;
    }

    public void setContinuationEnabledFlag(boolean continuationEnabledFlag) {
        this.continuationEnabledFlag = continuationEnabledFlag;
    }

    public boolean isAsynchronousFlag() {
        return asynchronousFlag;
    }

    public void setAsynchronousFlag(boolean asynchronousFlag) {
        this.asynchronousFlag = asynchronousFlag;
    }

    public boolean isDtnEnabledFlag() {
        return dtnEnabledFlag;
    }

    public void setDtnEnabledFlag(boolean dtnEnabledFlag) {
        this.dtnEnabledFlag = dtnEnabledFlag;
    }

    public boolean isSecurityEnabledFlag() {
        return securityEnabledFlag;
    }

    public void setSecurityEnabledFlag(boolean securityEnabledFlag) {
        this.securityEnabledFlag = securityEnabledFlag;
    }

    public boolean isEncryptionEnabledFlag() {
        return encryptionEnabledFlag;
    }

    public void setEncryptionEnabledFlag(boolean encryptionEnabledFlag) {
        this.encryptionEnabledFlag = encryptionEnabledFlag;
    }
}
