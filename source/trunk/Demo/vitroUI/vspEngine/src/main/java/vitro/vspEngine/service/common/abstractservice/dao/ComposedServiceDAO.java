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
import vitro.vspEngine.service.common.abstractservice.model.FullComposedService;

import javax.persistence.EntityManager;
import java.util.List;

/**
 *
 */
public class ComposedServiceDAO {

    private static ComposedServiceDAO instance = new ComposedServiceDAO();

    private Logger logger = Logger.getLogger(ComposedServiceDAO.class);

    private ComposedServiceDAO(){
        super();
    }

    public static ComposedServiceDAO getInstance(){
        return instance;
    }

    public List<FullComposedService> getComposedServiceList(EntityManager manager){
        logger.debug("getComposedServiceList() - Start");
        List<FullComposedService> result =  manager.createQuery("SELECT instance FROM FullComposedService instance", FullComposedService.class).getResultList();

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        for (FullComposedService fullComposedService : result) {
            // todo get Services List?
            //fullComposedService.getGatewayList().size();
            //fullComposedService.getObservedCapabilities().size();
            fullComposedService.getServiceInstanceList().size();
            fullComposedService.getSearchTagList().size();
        }

        return result;

    }

    public FullComposedService getComposedService(EntityManager manager, int instanceId){
        logger.debug("getComposedService() - ComposedServiceId = " + instanceId);
        FullComposedService fullComposedService = manager.find(FullComposedService.class, instanceId);

        //Extract gateway data from db via JPA NOT COMMENT THIS LINES
        //fullComposedService.getGatewayList().size();
        //fullComposedService.getObservedCapabilities().size();
        fullComposedService.getServiceInstanceList().size();
        fullComposedService.getSearchTagList().size();


        return fullComposedService;

    }


}
