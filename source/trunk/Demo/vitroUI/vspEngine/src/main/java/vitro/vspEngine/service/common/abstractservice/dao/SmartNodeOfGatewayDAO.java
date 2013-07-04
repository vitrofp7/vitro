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
import vitro.vspEngine.service.persistence.DBSmartNodeOfGateway;

import javax.persistence.EntityManager;
import java.util.List;

/**

 */
public class SmartNodeOfGatewayDAO {
    private static SmartNodeOfGatewayDAO instance = new SmartNodeOfGatewayDAO();

    private Logger logger = Logger.getLogger(SmartNodeOfGatewayDAO.class);

    private SmartNodeOfGatewayDAO(){
        super();
    }

    public static SmartNodeOfGatewayDAO getInstance(){
        return instance;
    }

    public List<DBSmartNodeOfGateway> getSmartNodeOfGatewayList(EntityManager manager){
        logger.debug("getSmartNodeOfGatewayList() - Start");
        List<DBSmartNodeOfGateway> result =  manager.createQuery("SELECT instance FROM DBSmartNodeOfGateway instance", DBSmartNodeOfGateway.class).getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (DBSmartNodeOfGateway dbSmartNodeOfGatewayTmp : result) {
            dbSmartNodeOfGatewayTmp.getParentGateWay();
        }

        return result;

    }

    public DBSmartNodeOfGateway getSmartNodeOfGateway(EntityManager manager, int smartNodeOfGatewayId){
        logger.debug("getSmartNodeOfGateway() - smartNodeOfGatewayId = " + smartNodeOfGatewayId);
        DBSmartNodeOfGateway dbSmartNodeOfGatewayTmp = manager.find(DBSmartNodeOfGateway.class, smartNodeOfGatewayId);

        //Extract selection data JPA NOT COMMENT THIS LINES
        dbSmartNodeOfGatewayTmp.getParentGateWay();

        return dbSmartNodeOfGatewayTmp;

    }

}
