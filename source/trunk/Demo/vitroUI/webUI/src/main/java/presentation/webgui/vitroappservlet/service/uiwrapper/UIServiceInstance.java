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
package presentation.webgui.vitroappservlet.service.uiwrapper;

import java.util.List;

import vitro.vspEngine.service.common.abstractservice.model.ServiceInstance;

/**
 * #superceded?
 */
public class UIServiceInstance {
	
	ServiceInstance serviceInstance;
	
	public UIServiceInstance(ServiceInstance serviceInstance){
		this.serviceInstance = serviceInstance;
	}

	public ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
	
	public String getSearchTagsString(){
		StringBuffer sb = new StringBuffer();
		
		List<String> sarchTagList = serviceInstance.getSearchTagList();
		
		for (int i = 0; i < sarchTagList.size(); i++) {
			if(i != 0){
				sb.append(", ");
			}
			sb.append(sarchTagList.get(i).trim());
		}
		
		return sb.toString();
	}
	
	

}
