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
package vitro.vspEngine.service.common.abstractservice.dao;

import org.apache.log4j.Logger;
import vitro.vspEngine.service.persistence.DBSelectionOfSmartNodes;

import javax.persistence.EntityManager;
import java.util.List;

/**
 */
public class SelectionOfSmartNodesDAO {

    private static SelectionOfSmartNodesDAO instance = new SelectionOfSmartNodesDAO();

    private Logger logger = Logger.getLogger(SelectionOfSmartNodesDAO.class);

    private SelectionOfSmartNodesDAO(){
        super();
    }

    public static SelectionOfSmartNodesDAO getInstance(){
        return instance;
    }

    public List<DBSelectionOfSmartNodes> getSelectionOfSmartNodesList(EntityManager manager){
        logger.debug("getSelectionOfSmartNodesList() - Start");
        List<DBSelectionOfSmartNodes> result =  manager.createQuery("SELECT instance FROM DBSelectionOfSmartNodes instance", DBSelectionOfSmartNodes.class).getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (DBSelectionOfSmartNodes dbSelectionOfSmartNodesTmp : result) {
            dbSelectionOfSmartNodesTmp.getDBSmartNodeOfGatewayList().size();
        }

        return result;

    }

    public DBSelectionOfSmartNodes getSelectionOfSmartNodes(EntityManager manager, int selectionOfSmartNodesListId){
        logger.debug("getSelectionOfSmartNodes() - selectionOfSmartNodesListId = " + selectionOfSmartNodesListId);
        DBSelectionOfSmartNodes dbSelectionOfSmartNodesTmp = manager.find(DBSelectionOfSmartNodes.class, selectionOfSmartNodesListId);

        //Extract selection data JPA NOT COMMENT THIS LINES
        dbSelectionOfSmartNodesTmp.getDBSmartNodeOfGatewayList().size();

        return dbSelectionOfSmartNodesTmp;

    }

}
