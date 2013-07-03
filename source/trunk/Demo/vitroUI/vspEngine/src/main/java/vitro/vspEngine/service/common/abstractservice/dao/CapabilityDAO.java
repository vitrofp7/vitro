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
import vitro.vspEngine.service.common.abstractservice.model.Capability;

import javax.persistence.EntityManager;
import java.util.List;

/**
 */
public class CapabilityDAO {

    private static CapabilityDAO instance = new CapabilityDAO();

    private Logger logger = Logger.getLogger(CapabilityDAO.class);

    private CapabilityDAO(){
        super();
    }

    public static CapabilityDAO getInstance(){
        return instance;
    }

    public List<Capability> getCapabilityList(EntityManager manager){
        logger.debug("getCapabilityList() - Start");
        List<Capability> result =  manager.createQuery("SELECT instance FROM Capability instance", Capability.class).getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (Capability capabilityTmp : result) {
            capabilityTmp.getDBSelectionOfGatewaysList().size();
            capabilityTmp.getDBSelectionOfSmartNodesList().size();
            capabilityTmp.getDBSelectionOfRegionsList().size();
        }

        return result;

    }

    public Capability getCapability(EntityManager manager, int capabilityId){
        logger.debug("getCapability() - capabilityId = " + capabilityId);
        Capability capability = manager.find(Capability.class, capabilityId);

        //Extract selection data JPA NOT COMMENT THIS LINES
        capability.getDBSelectionOfGatewaysList().size();
        capability.getDBSelectionOfSmartNodesList().size();
        capability.getDBSelectionOfRegionsList().size();

        return capability;

    }

}
