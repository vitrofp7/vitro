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

import javax.persistence.EntityManager;

import vitro.vspEngine.logic.model.Gateway;
import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;
import vitro.vspEngine.service.persistence.DBRegisteredGateway;

import java.util.List;

public class GatewayDAO {

    private static GatewayDAO instance = new GatewayDAO();
	
	private GatewayDAO(){
		super();
	}

	public static GatewayDAO getInstance(){
		return instance;
	}

    /**
     *
     * @param manager
     * @param incId the incremental auto id in the db
     * @return
     */
    public DBRegisteredGateway getGatewayByIncId(EntityManager manager, int incId){

        DBRegisteredGateway dbRegisteredGateway = manager.find(DBRegisteredGateway.class, incId);
        return dbRegisteredGateway;
    }

    /**
     *
     * @param manager
     * @param name the gateway (registered) name
     * @return
     */
	public DBRegisteredGateway getGateway(EntityManager manager, String name){
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT g ");
		sb.append("FROM DBRegisteredGateway g ");
		sb.append("WHERE g.registeredName = :gname ");
		
		DBRegisteredGateway result = manager.createQuery(sb.toString(), DBRegisteredGateway.class).
		setParameter("gname", name).
		getSingleResult();
		
		return result;
	}

    public List<DBRegisteredGateway> getInstanceList(EntityManager manager){
        List<DBRegisteredGateway> result =  manager.createQuery("SELECT instance FROM DBRegisteredGateway instance", DBRegisteredGateway.class).getResultList();

        return result;

    }
	
	
}
