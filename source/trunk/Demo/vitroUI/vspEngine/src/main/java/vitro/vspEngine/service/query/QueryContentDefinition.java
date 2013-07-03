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
 * QueryDefContent.java
 *
 */

package vitro.vspEngine.service.query;

import org.codehaus.staxmate.in.SMInputCursor;
import org.codehaus.staxmate.out.SMOutputDocument;
import org.codehaus.staxmate.out.SMOutputElement;
import vitro.vspEngine.service.geo.GeoRegion;

import java.util.*;

/**
 * A query content definition can be set by:
 * <ul>
 * <li> Vector of selected regions (by defining area region coordinates (of a square/rectangular/circular area) </li>
 * <li> Selection of specific motes/smartDevices </li>
 * <li> Specifying the exact coordinates for a sensing task </li>
 * <li> Selecting a gateway</li>
 * </ul>
 * Other parameters include:
 * <ul>
 * <li> The generic capabilities of the network that will be queried. </li>
 * <li> The function(s) to be applied to the retrieved data </li>
 * </ul>
 *
 * <pre>
 * 	&lt;content&gt;
 *		&lt;selectedDevices&gt;		&lt;!-- NEW --&gt;
 *			&lt;selectedAreas&gt;
 *				&lt;selection&gt;	&lt;!-- NEW --&gt;
 *					&lt;id&gt;&lt;/id&gt; &lt;!-- id for this selection --&gt;	&lt;!-- NEW --&gt;
 *					&lt;selectedArea&gt;&lt;/selectedArea&gt;
 *				&lt;/selection&gt;
 *			&lt;/selectedAreas&gt;
 *			&lt;selectedGateways&gt;
 *				&lt;selection&gt;	&lt;!-- NEW --&gt;
 *					&lt;id&gt;&lt;/id&gt; &lt;!-- id for this selection UNIQUE WITHIN THIS QUERY DEFINITION AMONG ALL Devices selections --&gt;	&lt;!-- NEW --&gt;
 *					&lt;Gateway&gt;
 *						&lt;id&gt;vitrogw_cti&lt;/id&gt;
 *						&lt;deviceId&gt;All motes&lt;/deviceId&gt;		&lt;!-- Could later support something like &quot;3 motes&quot;  to require data from at least or exactly 3 motes!! --&gt;
 *					&lt;/Gateway&gt;
 *					&lt;Gateway&gt;
 *						&lt;id&gt;vitrogw_wlab&lt;/id&gt;
 *						&lt;deviceId&gt;All motes&lt;/deviceId&gt;
 *					&lt;/Gateway&gt;
 *				&lt;selection&gt;
 *			&lt;/selectedGateways&gt;
 *		&lt;/selectedDevices&gt;	&lt;!-- NEW --&gt;
 *		&lt;uniqueFunction&gt;
 *			&lt;reqFunction&gt;
 *				&lt;description&gt;Last Value&lt;/description&gt;
 *				&lt;id&gt;1&lt;/id&gt;
 *			&lt;/reqFunction&gt;
 *			&lt;reqFunction&gt;
 *				&lt;description&gt;gwlevel_Average of Values_1&lt;/description&gt;	&lt;!-- Careful here. _#id referes to a valid UniqueFunction ID! --&gt;
 *				&lt;id&gt;2&lt;/id&gt;
 *			&lt;/reqFunction&gt;
 *		&lt;/uniqueFunction&gt;
 *		&lt;selectedCapabilities&gt;
 *			&lt;CapSelection&gt;		&lt;!-- NEW --&gt; &lt;!-- Should make an effort to make the selections unique
 *				&lt;id&gt; &lt;/id&gt;	&lt;!-- Capability Selection ID --&gt;&lt;!-- NEW --&gt; &lt;!-- Should be unique among all capabilities selections --&gt;
 *				&lt;Capability&gt;
 *					&lt;name&gt;urn:x-ogc:def:phenomenon:IDAS:1.0:temperature&lt;/name&gt;
 *					&lt;functionUid&gt;1&lt;/functionUid&gt;
 *					&lt;functionUid&gt;2&lt;/functionUid&gt;
 *				&lt;/Capability&gt;
 *			&lt;/CapSelection&gt;
 *		&lt;/selectedCapabilities&gt;
 *		&lt;SelDevsToSelCaps&gt;	&lt;!-- NEW --&gt;
 *			&lt;entry&gt;			&lt;!-- NEW --&gt;
 *				&lt;CapSelectionID&gt;&lt;/CapSelectionID&gt;	&lt;!-- NEW --&gt;
 *				&lt;DevSelectionID&gt;&lt;/DevSelectionID&gt;	&lt;!-- NEW --&gt;
 *			&lt;/entry&gt;
 *		&lt;/SelDevsToSelCaps&gt;
 * 	&lt;content&gt;
 * </pre>
 * @author antoniou
 */
public class QueryContentDefinition {
//    private Vector<GeoRegion> areasInSelectionVec;
//    private HashMap<String, Vector<String>> gateIdToSelectedMotesHM; // to store selected gateways and the selected motes per gateway. An vector with a single mote entry of QueryProcessor.selAllMotes for a gateway key means "ALL motes in gateway"
//    private HashMap<String, Vector<Integer>> genCapQuerriedTofuncIDsHM; //to store the selected generic capabilities for query and the associated functions per capability
    Vector<ReqFunctionOverData> uniqueFunctionVec;

    private HashMap<Integer, Vector<GeoRegion>> areasSelectionHM;       // to store the various areasSelected in entries of Vectors of areas. Has an ID key for each selection. Unique among all defined selections (areas, gateways, and motes)
    private HashMap<Integer, HashMap<String, Vector<String> > >  devicesSelectionHM;     // to store the various devices selected in entries of (HashMap<String, Vector<String>) gateids to motes in gateways.  Has an ID key for each selection. Unique among all defined selections (areas, gatewyas, and motes)

    private HashMap<Integer, HashMap<String, Vector<Integer>> > capabilitiesSelectionHM ; //to store the groupings (sets) of capabilities-to-Functions. Has ID which is unique among these capabilities-to-Functions sets.
    private HashMap<Integer, Integer> SelDevsToSelCaps ;                // to store mapping from selections (of areas or devices) to Capabilities.

    public static final String selUndefined = "No selection";
    public static final String selAllMotes = "All motes";

    private static final String uniqFunctTag = "uniqueFunction";

    private static final String selAreasTag = "selectedAreas";
    public static final String selAreaTag = "selectedArea";
    private static final String selgatewaysTag = "selectedGateways";
    private static final String selgatewayToDevsTag = "gateway";
    private static final String selgatewayIdTag = "id";
    private static final String selSmartDevTag = "deviceId";
    private static final String selectedCapabilitiesListTag = "selectedCapabilities";
    private static final String selCapToFunctionsTag = "capability";
    private static final String selCapDescTag = "name";
    private static final String selFunctIdTag = "functionUid";

    private static final String selectedDevicesListTag = "selectedDevices";
    private static final String selectionTag = "selection";
    private static final String selectionIDTag = "id";
    private static final String capabilitiesSelectionTag = "capSelection";
    private static final String devSelectionToCapabilitiesSelectionMappingTag = "selDevsToSelCaps";
    private static final String devSelectionToCapabilitiesSelectionMappingEntryTag = "entry";
    private static final String devSelToCapSel_CapSelIdTag = "capSelectionID";
    private static final String devSelToCapSel_DevSelIdTag = "devSelectionID";

    /**
     * Default constructor for content definition
     */
    public QueryContentDefinition() {
        //this.areasInSelectionVec = new Vector<GeoRegion>();
        //this.gateIdToSelectedMotesHM = new HashMap<String, Vector<String>>();
        //this.genCapQuerriedTofuncIDsHM = new HashMap<String, Vector<Integer>>();
        this.uniqueFunctionVec = new Vector<ReqFunctionOverData>();
        this.setAreasSelectionHM(new HashMap<Integer, Vector<GeoRegion>>());
        this.setDevicesSelectionHM(new HashMap<Integer, HashMap<String, Vector<String> > > ());
        this.setCapabilitiesSelectionHM(new HashMap<Integer, HashMap<String, Vector<Integer>> >());
        this.setSelDevsToSelCaps(new HashMap<Integer, Integer>());
    }


    /**
     * Constructor for content definition
     *
     * @param pAreasSelectionHM         A hashmap that stores the various areasSelected in entries of Vectors of areas. Has an ID key for each selection  unique among all node selections (region, gateway or specific nodes)
     * @param pDevicesSelectionHM       A hashmap that stores to store the various devices selected in entries of (HashMap<String, Vector<String>) gateids to motes in gateways.  Has an ID key for each selection. Unique among all defined selections (areas, gatewyas, and motes). In a selection, a vector with a single mote entry of QueryProcessor.selAllMotes for a gateway key means "ALL motes in gateway"
     * @param pCapabilitiesSelectionHM  A Hashmap that stores the groupings (sets) of capabilities-to-Functions.
     * @param pSelDevsToSelCaps      The mapping between selections of regions or devices And Capabilities Selections.
     * @param uniqfunctionVec       A vector of ReqFunctionOverData containing all the unique functions requested.
     */
    public QueryContentDefinition(HashMap<Integer, Vector<GeoRegion>> pAreasSelectionHM,
                                  HashMap<Integer, HashMap<String, Vector<String> > > pDevicesSelectionHM,
                                  HashMap<Integer, HashMap<String, Vector<Integer>> > pCapabilitiesSelectionHM,
                                  HashMap<Integer, Integer> pSelDevsToSelCaps,
                                  Vector<ReqFunctionOverData> uniqfunctionVec
                                  )
    {

        this.uniqueFunctionVec = uniqfunctionVec;
        this.setAreasSelectionHM(pAreasSelectionHM);
        this.setDevicesSelectionHM(pDevicesSelectionHM);
        this.setCapabilitiesSelectionHM(pCapabilitiesSelectionHM);
        this.setSelDevsToSelCaps(pSelDevsToSelCaps);
    }



    /**
     * Constructor for content definition
     *
     * @param givRangesVec                Stores the choices of regions
     * @param gateIdToSelectedMotesVecHM  A hashmap that stores the regions' gatewayPeerIDs  selected mapped to a vector of motes of each gateway that will be queried or a vector of a single QueryProcessor.selAllMotes entry, that means that all motes should be querried.
     * @param genCapQuerriedTofuncIDVecHM A Hashmap that stores the generic capabilities descriptions that will be queried mapped to the vector of unique functions ids for each Generic Capability
     * @param uniqfunctionVec             A vector of ReqFunctionOverData containing all the unique functions requested.
     */
    public QueryContentDefinition(Vector<GeoRegion> givRangesVec, HashMap<String, Vector<String>> gateIdToSelectedMotesVecHM, HashMap<String, Vector<Integer>> genCapQuerriedTofuncIDVecHM, Vector<ReqFunctionOverData> uniqfunctionVec) {
        //this.areasInSelectionVec = givRangesVec;
        //this.gateIdToSelectedMotesHM = gateIdToSelectedMotesVecHM;
        //this.genCapQuerriedTofuncIDsHM = genCapQuerriedTofuncIDVecHM;
        this.uniqueFunctionVec = uniqfunctionVec;

        // legacy support if we get ony these arguments, then we need to build the new schema
        Integer uniqIndexOfSelectedDevs = 1; //start from 1
        Integer uniqIndexOfSelectedCaps = 1; //start from 1
        this.setAreasSelectionHM(new HashMap<Integer, Vector<GeoRegion>>());
        if(givRangesVec !=null && !givRangesVec.isEmpty()) {
            this.getAreasSelectionHM().put(uniqIndexOfSelectedDevs++, givRangesVec);
        }
        this.setDevicesSelectionHM(new HashMap<Integer, HashMap<String, Vector<String> > >());
        if(gateIdToSelectedMotesVecHM !=null && !gateIdToSelectedMotesVecHM.isEmpty())
        {
            this.getDevicesSelectionHM().put(uniqIndexOfSelectedDevs++, gateIdToSelectedMotesVecHM);
        }

        this.setCapabilitiesSelectionHM(new HashMap<Integer, HashMap<String, Vector<Integer>> >());
        if(genCapQuerriedTofuncIDVecHM !=null && !genCapQuerriedTofuncIDVecHM.isEmpty())
        {
            this.getCapabilitiesSelectionHM().put(uniqIndexOfSelectedCaps++, genCapQuerriedTofuncIDVecHM);
        }

        this.setSelDevsToSelCaps(new HashMap<Integer, Integer>());
        if( ( !this.getAreasSelectionHM().isEmpty() || !this.getDevicesSelectionHM().isEmpty()) &&  !this.getCapabilitiesSelectionHM().isEmpty()  )
        {
            for(Integer deviceSelectionKey= 1; deviceSelectionKey < uniqIndexOfSelectedDevs; deviceSelectionKey++)
            {
                for (Integer capabilitiesSelectionKey : getCapabilitiesSelectionHM().keySet()) {
                    this.getSelDevsToSelCaps().put(deviceSelectionKey, capabilitiesSelectionKey);
                }
            }
        }
    }

    /**
     * Creates a new instance of QueryContentDefinition
     *
     * @param givenCursor the XML part of a query (As a TextElement) that describes the Query Content Definition
     */
    public QueryContentDefinition(SMInputCursor givenCursor) {
        // (TODO)

    }

    /**
     * Creates XML structured info on this QueryContentDefinition object, under the parent Element, in the specified StructuredDocument
     *
     * @param document   the provided XML document. (e.g. a query)
     * @param parElement the parent element in the given XML document
     */
    public void createInfoInDocument(SMOutputDocument document, SMOutputElement parElement) {

        SMOutputElement tmpElementOuter = null;
        SMOutputElement tmpElement1;
        SMOutputElement tmpElement2;
        SMOutputElement tmpElement3;
        SMOutputElement tmpElement4;
        try{
            if (parElement != null) {
                tmpElementOuter = parElement.addElement(QueryDefinition.getContentTag());
            }
            else {
                tmpElementOuter =  document.addElement(QueryDefinition.getContentTag());
            }

            SMOutputElement devSelectionEl = null;
            if( !this.getAreasSelectionHM().isEmpty() || !this.getDevicesSelectionHM().isEmpty()) {
                devSelectionEl = tmpElementOuter.addElement(QueryContentDefinition.selectedDevicesListTag);
                // should check for null in the parElement argument!
                // Coordinates were defined

                if (!this.getAreasSelectionHM().isEmpty()) {
                    tmpElement1 = devSelectionEl.addElement(QueryContentDefinition.selAreasTag);

                    for (Map.Entry<Integer, Vector<GeoRegion>> areasSelectionHMEntry : this.getAreasSelectionHM().entrySet()) {
                        tmpElement2 = tmpElement1.addElement(QueryContentDefinition.selectionTag);
                        tmpElement3 = tmpElement2.addElement(QueryContentDefinition.selectionIDTag);
                        tmpElement3.addCharacters(  areasSelectionHMEntry.getKey().toString());
                        for (int i = 0; i < areasSelectionHMEntry.getValue().size(); i++) {
                            // code that dumps region specific info in the document
                            tmpElement3 = tmpElement2.addElement(QueryContentDefinition.selAreaTag);
                            areasSelectionHMEntry.getValue().get(i).createInfoInDocument(document, tmpElement3);
                        }
                    }
                }
                if (!this.getDevicesSelectionHM().isEmpty()) {
                    //
                    // gateways and/or specific mote selections defined.
                    //
                    tmpElement1 = devSelectionEl.addElement(QueryContentDefinition.selgatewaysTag) ;
                    for (Map.Entry<Integer, HashMap<String, Vector<String>>> devicesSelectionHMEntry : this.getDevicesSelectionHM().entrySet()) {
                        tmpElement2 = tmpElement1.addElement(QueryContentDefinition.selectionTag);
                        tmpElement3 = tmpElement2.addElement(QueryContentDefinition.selectionIDTag);
                        tmpElement3.addCharacters(  devicesSelectionHMEntry.getKey().toString());

                        for (Map.Entry<String,  Vector<String>> gwTodevicesEntry : devicesSelectionHMEntry.getValue().entrySet()) {
                            String gateId = gwTodevicesEntry.getKey();

                            tmpElement3 = tmpElement2.addElement(QueryContentDefinition.selgatewayToDevsTag);
                            tmpElement4 = tmpElement3.addElement(QueryContentDefinition.selgatewayIdTag) ;
                            tmpElement4.addCharacters(  gateId);

                            Vector<String> tmpSmDevVec =gwTodevicesEntry.getValue();
                            for (int i = 0; i < tmpSmDevVec.size(); i++) {
                                tmpElement4 = tmpElement3.addElement(QueryContentDefinition.selSmartDevTag) ;
                                tmpElement4.addCharacters(tmpSmDevVec.elementAt(i));
                            }
                        }
                    }
                }
            }
            //
            // unique functions defined
            //
            tmpElement1 = tmpElementOuter.addElement(QueryContentDefinition.uniqFunctTag) ;
            for (int i = 0; i < this.getUniqueFunctionVec().size(); i++) {
                this.getUniqueFunctionVec().elementAt(i).createInfoInDocument(document, tmpElement1);
            }
            //
            // capabilities and functions defined
            //
            if(!this.getCapabilitiesSelectionHM().isEmpty()) {
                tmpElement1 = tmpElementOuter.addElement(QueryContentDefinition.selectedCapabilitiesListTag) ;
                for (Map.Entry<Integer, HashMap<String, Vector<Integer>> > capabilitiesSelectionHMEntry : this.getCapabilitiesSelectionHM().entrySet()) {
                    tmpElement2 = tmpElement1.addElement(QueryContentDefinition.capabilitiesSelectionTag);
                    tmpElement3 = tmpElement2.addElement(QueryContentDefinition.selectionIDTag);
                    tmpElement3.addCharacters(  capabilitiesSelectionHMEntry.getKey().toString());


                    for (Map.Entry<String,  Vector<Integer>> capToFunctionsEntry : capabilitiesSelectionHMEntry.getValue().entrySet()) {
                        String cap = capToFunctionsEntry.getKey();
                        tmpElement3 = tmpElement2.addElement(QueryContentDefinition.selCapToFunctionsTag);
                        tmpElement4 = tmpElement3.addElement(QueryContentDefinition.selCapDescTag);
                        tmpElement4.addCharacters(cap);

                        Vector<Integer> tmpFidVec = capToFunctionsEntry.getValue();
                        for (int i = 0; i < tmpFidVec.size(); i++) {
                            tmpElement4 = tmpElement3.addElement(QueryContentDefinition.selFunctIdTag);
                            tmpElement4.addCharacters(tmpFidVec.elementAt(i).toString());
                        }
                    }
                }
            }
            //
            // Mapping of selection sets of devices -TO- sets of capabilities (*rules*)
            //
            if( !this.getSelDevsToSelCaps().isEmpty() )
            {
                tmpElement1 = tmpElementOuter.addElement(QueryContentDefinition.devSelectionToCapabilitiesSelectionMappingTag) ;
                for (Map.Entry<Integer, Integer> selDevsToSelCapsEntry : this.getSelDevsToSelCaps().entrySet()) {
                    tmpElement2 = tmpElement1.addElement(QueryContentDefinition.devSelectionToCapabilitiesSelectionMappingEntryTag);
                    tmpElement3 = tmpElement2.addElement(QueryContentDefinition.devSelToCapSel_DevSelIdTag);
                    tmpElement3.addCharacters(selDevsToSelCapsEntry.getKey().toString());
                    tmpElement3 = tmpElement2.addElement(QueryContentDefinition.devSelToCapSel_CapSelIdTag);
                    tmpElement3.addCharacters(selDevsToSelCapsEntry.getValue().toString());
                }
            }

        } catch(Exception e) {
            return;
        }
    }


    /**
     * Checks if the given Query Content Definition object is empty.
     * Supports the new query content definition schema
     * @return true if the given Query Content Definition is a dummy empty object (probably awaiting yet to be filled by valid data).
     */
    public boolean isEmptyQueryContent() {
        if (this.uniqueFunctionVec == null ||
                this.uniqueFunctionVec.isEmpty() ||
                (
                    (this.getAreasSelectionHM() == null || this.getAreasSelectionHM().isEmpty()) &&
                            (this.getDevicesSelectionHM() == null || this.getDevicesSelectionHM().isEmpty() )
                ) ||
                (
                    this.getCapabilitiesSelectionHM() == null ||
                            this.getCapabilitiesSelectionHM().isEmpty()
                ) ||
                (
                    this.getSelDevsToSelCaps() == null ||
                    this.getSelDevsToSelCaps().isEmpty()
                )
            ) {
            return true;
        } else
            return false;
    }


    /**
     * Returns the vector of unique (distinct) functions defined for this query definition.
     * Each function has a unique id.
     *
     * @return the vector of unique (distinct) functions for this query definition.
     */
    public Vector<ReqFunctionOverData> getUniqueFunctionVec() {
        return uniqueFunctionVec;
    }

    /**
     * Returns the vector of unique (distinct) functions referenced only inside the given Vector of QueriedMoteAndSensors objects
     *  TODO: Adapt to the new schema (seems ok)
     * @return the vector of unique (distinct) functions referenced only inside the given Vector.
     */
    public Vector<ReqFunctionOverData> getUniqueRefFunctionVecForMotes(Vector<QueriedMoteAndSensors> givenMoteVec) {
        Vector<ReqFunctionOverData> resultUniqueFunctionVec = new Vector<ReqFunctionOverData>();
        boolean matchWithFunctionInResultVecFound = false;
        //for each mote in the givenMoteVec
        for (int q = 0; q < givenMoteVec.size(); q++) {
            Vector<ReqSensorAndFunctions> givenSensorVec = givenMoteVec.elementAt(q).getQueriedSensorIdsAndFuncVec();
            // for each sensor in the vector, get the associated Vector of functions IDs
            for (int i = 0; i < givenSensorVec.size(); i++) {
                Vector<Integer> tmpFuncIdsVec = givenSensorVec.elementAt(i).getFunctionsOverSensorModelVec();
                // for each Function ID in the Vector of functions find the corresponding ReqFunctionOverData from the UniqueFunctionVec
                for (int j = 0; j < tmpFuncIdsVec.size(); j++) {
                    ReqFunctionOverData refFunction = getUniqueFunctionById(tmpFuncIdsVec.elementAt(j));
                    if (refFunction != null) {
                        // check if this ReqFunctionOverData has already been appended to the resultUniqueFunctionVec
                        matchWithFunctionInResultVecFound = false;
                        for (int k = 0; k < resultUniqueFunctionVec.size() && (matchWithFunctionInResultVecFound == false); k++)
                        {
                            if (resultUniqueFunctionVec.elementAt(k).getfuncId() == refFunction.getfuncId()) {
                                matchWithFunctionInResultVecFound = true;
                            }
                        }
                        if (matchWithFunctionInResultVecFound == false) {
                            resultUniqueFunctionVec.addElement(refFunction);
                        }
                    }
                }
            }
        }
        return resultUniqueFunctionVec;
    }


    /**
     * Returns a ReqFunctionOverData that corresponds to the given id in the UniqueFunction Vector.
     *
     * @param givenId the id of the requested function
     * @return A ReqFunctionOverData that corresponds to the given id in the UniqueFunction Vector
     */
    public ReqFunctionOverData getUniqueFunctionById(int givenId) {
        for (int i = 0; i < getUniqueFunctionVec().size(); i++) {
            if (getUniqueFunctionVec().elementAt(i).getfuncId() == givenId) {
                return getUniqueFunctionVec().elementAt(i);
            }
        }
        return null;
    }



    /**
     * Compares two QueryContentDefinition objects
     * TODO: Adapt to new schema
     * @param targetContent the target QueryContentDefinition to compare to
     * @return true if objects express the same content for their definition, or false otherwise
     */
    public boolean equals(QueryContentDefinition targetContent) {

        HashMap<Integer,Integer>  mapOfSelectionKeysOrigToTarget = new   HashMap<Integer,Integer>();
        HashMap<Integer,Integer>  mapOfCapabilitySelKeysOrigToTarget = new   HashMap<Integer,Integer>();

        if (this.getAreasSelectionHM() != null && targetContent.getAreasSelectionHM() != null) {
            if (this.getAreasSelectionHM().size() != targetContent.getAreasSelectionHM().size()) {
                return false;
            } else {
                int totalEntriesToMatch =  this.getAreasSelectionHM().size();
                int matchedSoFar = 0;
                // create a temp copy of the targetContent areasInSelectionVector, and use that for the comparison
                HashMap <Integer, Vector<GeoRegion>>  copyTargetAreasSelectionHM = new HashMap <Integer, Vector<GeoRegion>>(targetContent.getAreasSelectionHM());
                for (Map.Entry<Integer, Vector<GeoRegion>> thisAreasSelectionHMEntry : this.getAreasSelectionHM().entrySet()) {
                    Vector<GeoRegion> thisAreasInSelectionVec = thisAreasSelectionHMEntry.getValue();
                    Integer origSelKey = thisAreasSelectionHMEntry.getKey();
                    for (Map.Entry<Integer, Vector<GeoRegion>> copyTargetAreasSelectionHMEntry : copyTargetAreasSelectionHM.entrySet()) {
                        Vector<GeoRegion> tmpTargetAreasInSelectionVec = new Vector<GeoRegion>(copyTargetAreasSelectionHMEntry.getValue());
                        Integer targSelKey = copyTargetAreasSelectionHMEntry.getKey();
                        int originalVectorSizeOfTargetAreasInSelectionVec = tmpTargetAreasInSelectionVec.size();
                        int i = 0;
                        for (i = 0; i < thisAreasInSelectionVec.size(); i++) {
                            for (int j = 0; tmpTargetAreasInSelectionVec.size() > 0 && j < tmpTargetAreasInSelectionVec.size(); j++)
                            {
                                if (thisAreasInSelectionVec.get(i).equals(tmpTargetAreasInSelectionVec.get(j))) {
                                    tmpTargetAreasInSelectionVec.removeElementAt(j);
                                    j -= 1;
                                }
                            }
                        }
                        if ((i != originalVectorSizeOfTargetAreasInSelectionVec ) || (tmpTargetAreasInSelectionVec.size() != 0)) {
                            ;  //no match, continue searching (in the loop).
                        }
                        else {
                            matchedSoFar +=1;
                            copyTargetAreasSelectionHMEntry.getValue().clear(); //clear the vector so that we won't have it match anything else again.
                            // TODO we could also store the mapping of keys of target to keys of "this" in order to check for equality with the SelDevsToSelCaps hashmap!
                            mapOfSelectionKeysOrigToTarget.put(origSelKey, targSelKey);
                            break; //found a match for this vector entry. so go to the next source entry.
                        }
                    }
                    //if we reached here, then (since we break on match) we have not found a match for the current entry, then definitely we won't have a match for all entries
                    return false;
                }
                if(matchedSoFar != totalEntriesToMatch) {       //maybe redundant
                    return false;
                }
            }
        } else if (
                    ((this.getAreasSelectionHM() == null || this.getAreasSelectionHM().isEmpty()) && (targetContent.getAreasSelectionHM() != null && !targetContent.getAreasSelectionHM().isEmpty()) ) ||
                            ((this.getAreasSelectionHM() != null && !this.getAreasSelectionHM().isEmpty()) && (targetContent.getAreasSelectionHM() == null || targetContent.getAreasSelectionHM().isEmpty()) )
                ){
            return false;
        }

        if (this.getDevicesSelectionHM() != null && targetContent.getDevicesSelectionHM() != null) {
            if (this.getDevicesSelectionHM().size() != targetContent.getDevicesSelectionHM().size()) {
                return false;
            } else {            //if they have the same size (same number of selection sets)
                int totalEntriesToMatch =  this.getDevicesSelectionHM().size();
                int matchedSoFar = 0;
                //
                // for each selection set:
                // for each gateway in this gateIdToSelectedMotesHM
                // First check if the gateways included in the hashmap are exactly identical.
                // Then for each gateway get the vector of motes.
                // if the "this" vector of motes does not containsAll of target vector of motes OR
                // target vector does not contains all of "this" then return false
                //
                for (Map.Entry<Integer, HashMap<String, Vector<String> >> thisDevicesSelectionHMEntry : this.getDevicesSelectionHM().entrySet()) {
                    HashMap<String, Vector<String> > thisDevicesInSelectionSetHM = thisDevicesSelectionHMEntry.getValue();
                    Integer origSelKey = thisDevicesSelectionHMEntry.getKey();
                    for (Map.Entry<Integer, HashMap<String, Vector<String> >> targetDevicesSelectionHMEntry : targetContent.getDevicesSelectionHM().entrySet()) {
                        HashMap<String, Vector<String> > targetDevicesSelectionSetHM = targetDevicesSelectionHMEntry.getValue();
                        Integer targSelKey = targetDevicesSelectionHMEntry.getKey();

                        Set<String> tmpset00target = targetDevicesSelectionSetHM.keySet();
                        Set<String> tmpset00this = thisDevicesInSelectionSetHM.keySet();


                        boolean possibleMatchInThisSet = true;
                        if ((!tmpset00this.containsAll(tmpset00target)) || (!tmpset00target.containsAll(tmpset00this))) {
                            possibleMatchInThisSet = false;
                        }
                        else {
                            Iterator<String> itonset00this = tmpset00this.iterator();
                            while (itonset00this.hasNext()) {
                                String tmpGateID = itonset00this.next();
                                Vector<String> tmpMoteVecThis = thisDevicesInSelectionSetHM.get(tmpGateID);
                                Vector<String> tmpMoteVecTarget = targetDevicesSelectionSetHM.get(tmpGateID);
                                if (!tmpMoteVecThis.containsAll(tmpMoteVecTarget) ||
                                        !(tmpMoteVecTarget.containsAll(tmpMoteVecThis))) {

                                    possibleMatchInThisSet = false;    //no possible match within this set
                                    break; // no use to keep searching in this selection set
                                }
                            }
                        }
                        if(possibleMatchInThisSet)  //if here we still have this set to true, then we have matching sets.
                        {
                            matchedSoFar+=1;
                            mapOfSelectionKeysOrigToTarget.put(origSelKey, targSelKey);
                            break;
                        }
                    }
                    //if we reached here, then (since we break on matching sets) we have not found a match for the current entry, then definitely we won't have a match for all entries
                    return false;
                }
                if(matchedSoFar != totalEntriesToMatch) {       //maybe redundant
                    return false;
                }
            }

        } else if ( ((this.getDevicesSelectionHM() == null || this.getDevicesSelectionHM().isEmpty()) && (targetContent.getDevicesSelectionHM() != null && !targetContent.getDevicesSelectionHM().isEmpty()) ) ||
                            ((this.getDevicesSelectionHM() != null && !this.getDevicesSelectionHM().isEmpty()) && (targetContent.getDevicesSelectionHM() == null || targetContent.getDevicesSelectionHM().isEmpty()) )
            ){
            return false;
        }
        //
        // If we reach here then we have to also check if the selected capabilities are the same too
        //
        if (this.getCapabilitiesSelectionHM() != null && targetContent.getCapabilitiesSelectionHM() != null) {
            if (this.getCapabilitiesSelectionHM().size() != targetContent.getCapabilitiesSelectionHM().size()) {
                return false;
            } else {            //if they have the same size (same number of selection sets)
                int totalEntriesToMatch =  this.getCapabilitiesSelectionHM().size();
                int matchedSoFar = 0;

                for (Map.Entry<Integer, HashMap<String, Vector<Integer> >> thisCapabilitiesSelectionHMEntry : this.getCapabilitiesSelectionHM().entrySet()) {
                    HashMap<String, Vector<Integer> > thisCapabilitiesInSelectionSetHM = thisCapabilitiesSelectionHMEntry.getValue();
                    Integer origCapSelKey = thisCapabilitiesSelectionHMEntry.getKey();
                    for (Map.Entry<Integer, HashMap<String, Vector<Integer> >> targetCapabilitiesSelectionHMEntry : targetContent.getCapabilitiesSelectionHM().entrySet()) {
                        HashMap<String, Vector<Integer> > targetCapabilitiesSelectionSetHM = targetCapabilitiesSelectionHMEntry.getValue();
                        Integer targCapSelKey = targetCapabilitiesSelectionHMEntry.getKey();


                        Set<String> tmpset00target = targetCapabilitiesSelectionSetHM.keySet();
                        Set<String> tmpset00this = thisCapabilitiesInSelectionSetHM.keySet();

                        boolean possibleMatchInThisSet = true;
                        if ((!tmpset00this.containsAll(tmpset00target)) || (!tmpset00target.containsAll(tmpset00this))) {
                            possibleMatchInThisSet = false;
                        }
                        else {
                            Iterator<String> itonset00this = tmpset00this.iterator();
                            while (itonset00this.hasNext()) {
                                String tmpCapName = itonset00this.next();
                                Vector<Integer> tmpFunctVecThis = thisCapabilitiesInSelectionSetHM.get(tmpCapName);
                                Vector<Integer> tmpFunctVecTarget = targetCapabilitiesSelectionSetHM.get(tmpCapName);
                                if (!tmpFunctVecThis.containsAll(tmpFunctVecTarget) ||
                                        !(tmpFunctVecTarget.containsAll(tmpFunctVecThis))) {           // we also check the unique function IDs to match! This is very strict. TODO we also need a separate CapsToFunctsIDsMapping for source and target to compare if the correspondance is correct (yet allow different unique Function Ids) (???)

                                    possibleMatchInThisSet = false;    //no possible match within this set
                                    break; // no use to keep searching in this selection set
                                }
                            }
                        }
                        if(possibleMatchInThisSet)  //if here we still have this set to true, then we have matching sets.
                        {
                            matchedSoFar+=1;
                            mapOfCapabilitySelKeysOrigToTarget.put(origCapSelKey, targCapSelKey);
                            break;
                        }
                    }
                    //if we reached here, then (since we break on matching sets) we have not found a match for the current entry, then definitely we won't have a match for all entries
                    return false;
                }
                if(matchedSoFar != totalEntriesToMatch) {       //maybe redundant
                    return false;
                }
            }
        }   else if ( ((this.getCapabilitiesSelectionHM() == null || this.getCapabilitiesSelectionHM().isEmpty()) && (targetContent.getCapabilitiesSelectionHM() != null && !targetContent.getCapabilitiesSelectionHM().isEmpty()) ) ||
                ((this.getCapabilitiesSelectionHM() != null && !this.getCapabilitiesSelectionHM().isEmpty()) && (targetContent.getCapabilitiesSelectionHM() == null || targetContent.getCapabilitiesSelectionHM().isEmpty()) )
                ){
            return false;
        }


        //
        // If we reach here then we have to also check if the selected functions are the same too
        //  We compare with a copy of the target function Vector.
        //
        Vector<ReqFunctionOverData> targetFunctVec = targetContent.uniqueFunctionVec;
        Vector<ReqFunctionOverData> tmpComparisonVec = new Vector<ReqFunctionOverData>(targetFunctVec);
        int i = 0;
        for (i = 0; i < this.uniqueFunctionVec.size(); i++) {

            for (int j = 0; tmpComparisonVec.size() > 0 && j < tmpComparisonVec.size(); j++) {
                if (this.uniqueFunctionVec.elementAt(i).equals(tmpComparisonVec.elementAt(j))) {
                    tmpComparisonVec.removeElementAt(j);
                    j -= 1;
                }
            }
        }
        // if at the end, the iterator of the source Vector of function does not contain the size of the target vector (in other words they are not of equal size)
        // or the clone of the target Vector of function has still some unmatched elements then they are not equal
        if ((i != targetFunctVec.size()) || (tmpComparisonVec.size() != 0)) {
            //System.out.println("PROBLEM IN SELECTED FUNCTIONS!!!!");
            return false;
        }
        // if we reach here, then we have to evaluate SelDevsToSelCaps equality
        if (this.getSelDevsToSelCaps() != null && targetContent.getSelDevsToSelCaps() != null) {
            if (this.getSelDevsToSelCaps().size() != targetContent.getSelDevsToSelCaps().size()) {
                return false;
            } else {            //if they have the same size (same number of selection mappings)
                int totalEntriesToMatch =  this.getSelDevsToSelCaps().size();
                int matchedSoFar = 0;
                for (Map.Entry<Integer, Integer> thisSelDevsToSelCapsHMEntry : this.getSelDevsToSelCaps().entrySet()) {
                    Integer thisSelDevsToSelCapsHMEntryValue = thisSelDevsToSelCapsHMEntry.getValue();
                    Integer thisSelDevsToSelCapsHMEntryKey = thisSelDevsToSelCapsHMEntry.getKey();
                    for (Map.Entry<Integer, Integer> targetSelDevsToSelCapsHMEntry : targetContent.getSelDevsToSelCaps().entrySet()) {
                        Integer targetSelDevsToSelCapsHMEntryValue = targetSelDevsToSelCapsHMEntry.getValue();
                        Integer targetSelDevsToSelCapsHMEntryKey = targetSelDevsToSelCapsHMEntry.getKey();

                        if(mapOfSelectionKeysOrigToTarget.containsKey(thisSelDevsToSelCapsHMEntryKey) && targetSelDevsToSelCapsHMEntryKey == mapOfSelectionKeysOrigToTarget.get(thisSelDevsToSelCapsHMEntryKey)   &&
                                mapOfCapabilitySelKeysOrigToTarget.containsKey(thisSelDevsToSelCapsHMEntryValue)    && targetSelDevsToSelCapsHMEntryValue == mapOfCapabilitySelKeysOrigToTarget.get(thisSelDevsToSelCapsHMEntryValue) )
                        {
                            matchedSoFar +=1;
                            break;
                        }
                    }
                    // if we didn't match (amtch means break, so we don't reach here in that case) with an original entry then we dont have similar matchings
                    return false;
                }
                if(matchedSoFar != totalEntriesToMatch) {       //maybe redundant
                    return false;
                }

            }
        } else if ( ((this.getSelDevsToSelCaps() == null || this.getSelDevsToSelCaps().isEmpty()) && (targetContent.getSelDevsToSelCaps() != null && !targetContent.getSelDevsToSelCaps().isEmpty()) ) ||
                ((this.getSelDevsToSelCaps() != null && !this.getSelDevsToSelCaps().isEmpty()) && (targetContent.getSelDevsToSelCaps() == null || targetContent.getSelDevsToSelCaps().isEmpty()) )
                ){
            return false;
        }


        //
        // If we reach here, then we have identical query content definitions
        //
        return true;
    }


    /**
     * Superseded, this Hashmap will eventually be removed!
     * @return
     */
//    private Vector<GeoRegion> getRangeCoordsSelected() {
//        return this.areasInSelectionVec;
//    }

    /**
     * Superseded, this Hashmap will eventually be removed!
     * @return
     */
//    private HashMap<String, Vector<String>> getGateIdToSelectedMotesHM() {
//        return gateIdToSelectedMotesHM;
//    }

    /**
     * Superseded
     * Returns the Hashmap, where generic capabilities selected to be queried are mapped to
     * specific unique function ids.
     *
     * @return A HashMap mapping generic capabilities to specific unique function ids.
     */
//    private HashMap<String, Vector<Integer>> getGenCapQuerriedTofuncIDsHM() {
//        return genCapQuerriedTofuncIDsHM;
//    }


    public HashMap<Integer, Vector<GeoRegion>> getAreasSelectionHM() {
        return areasSelectionHM;
    }

    public void setAreasSelectionHM(HashMap<Integer, Vector<GeoRegion>> areasSelectionHM) {
        this.areasSelectionHM = areasSelectionHM;
    }

    public HashMap<Integer, HashMap<String, Vector<String>>> getDevicesSelectionHM() {
        return devicesSelectionHM;
    }

    public void setDevicesSelectionHM(HashMap<Integer, HashMap<String, Vector<String>>> devicesSelectionHM) {
        this.devicesSelectionHM = devicesSelectionHM;
    }

    public HashMap<Integer, HashMap<String, Vector<Integer>>> getCapabilitiesSelectionHM() {
        return capabilitiesSelectionHM;
    }

    public void setCapabilitiesSelectionHM(HashMap<Integer, HashMap<String, Vector<Integer>>> capabilitiesSelectionHM) {
        this.capabilitiesSelectionHM = capabilitiesSelectionHM;
    }

    public HashMap<Integer, Integer> getSelDevsToSelCaps() {
        return SelDevsToSelCaps;
    }

    public void setSelDevsToSelCaps(HashMap<Integer, Integer> selDevsToSelCaps) {
        SelDevsToSelCaps = selDevsToSelCaps;
    }
}
