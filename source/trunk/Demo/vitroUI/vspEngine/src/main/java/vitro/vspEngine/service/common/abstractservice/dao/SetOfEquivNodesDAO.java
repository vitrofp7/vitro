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
package vitro.vspEngine.service.common.abstractservice.dao;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.common.abstractservice.model.SetOfEquivalentSensorNodes;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 */
public class SetOfEquivNodesDAO {


    private static SetOfEquivNodesDAO instance = new SetOfEquivNodesDAO();

    private Logger logger = Logger.getLogger(SetOfEquivNodesDAO.class);

    private SetOfEquivNodesDAO(){
        super();
    }

    public static SetOfEquivNodesDAO getInstance(){
        return instance;
    }

    public List<SetOfEquivalentSensorNodes> getSetOfEquivNodesList(EntityManager manager){
        logger.debug("getSetOfEquivNodesList() - Start");
        List<SetOfEquivalentSensorNodes> result =  manager.createQuery("SELECT instance FROM SetOfEquivalentSensorNodes instance ORDER BY instance.timestampUpdateLocal ASC", SetOfEquivalentSensorNodes.class).getResultList();

        //Extract  data from db via JPA NOT COMMENT THIS LINES
        for (SetOfEquivalentSensorNodes setOfEquivalentSensorNodesTmp : result) {
            setOfEquivalentSensorNodesTmp.getInterchngblNodes();
        }

        return result;

    }

    public List<SetOfEquivalentSensorNodes> getSetOfEquivNodesListForVgwId(EntityManager manager, String gatewayID){
        logger.debug("getSetOfEquivNodesListForVgwId() - Start");
        List<SetOfEquivalentSensorNodes> result = null;
        StringBuilder sqlQueryBld = new StringBuilder();
        sqlQueryBld.append("SELECT instance FROM SetOfEquivalentSensorNodes instance WHERE ");
        if(gatewayID!= null && gatewayID.trim().compareTo("")!=0){
            sqlQueryBld.append(" instance.vgwId LIKE :pVgwId");
        }
        sqlQueryBld.append(" ORDER BY instance.timestampUpdateLocal ASC");
        String theFinalQueryStr = sqlQueryBld.toString();

        TypedQuery sqlQuerySetOfEquivalentSensorNodes =  manager.createQuery(theFinalQueryStr, SetOfEquivalentSensorNodes.class);
        if(gatewayID!= null && gatewayID.trim().compareTo("")!=0){
            sqlQuerySetOfEquivalentSensorNodes.setParameter("pVgwId", gatewayID);
        }


        result =  sqlQuerySetOfEquivalentSensorNodes.getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (SetOfEquivalentSensorNodes setOfEquivalentSensorNodesTmp : result) {
            setOfEquivalentSensorNodesTmp.getInterchngblNodes();
        }

        return result;
    }


    public SetOfEquivalentSensorNodes getSetOfEquivNodes(EntityManager manager, int setId){
        logger.debug("getSetOfEquivNodes() - setId = " + setId);
        SetOfEquivalentSensorNodes setOfEquivalentSensorNodes = manager.find(SetOfEquivalentSensorNodes.class, setId);

        //Extract  data JPA NOT COMMENT THIS LINES
        setOfEquivalentSensorNodes.getInterchngblNodes();

        return setOfEquivalentSensorNodes;

    }
}
