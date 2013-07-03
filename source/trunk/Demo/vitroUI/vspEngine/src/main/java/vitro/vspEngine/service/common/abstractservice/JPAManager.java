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
package vitro.vspEngine.service.common.abstractservice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;

public abstract class JPAManager {

	private Logger logger = Logger.getLogger(getClass());
	
	protected JPAManager(){
		emf = Persistence.createEntityManagerFactory("vspPU");
	}
	
	//////////////////////////
	
	private EntityManagerFactory emf;
	
	
	///////////// INFRASTRUCTURE ////////////////////////////
	
	
	final protected void startVoidMethodInTransaction(String methodName, Object... params){
		EntityManager manager = null;
		EntityTransaction trans = null;
		
		try{
			manager = emf.createEntityManager();
			
			
			trans = manager.getTransaction();
			//BOT
			trans.begin();
			logger.debug("BOT");
			
			callVoidMethod(manager, methodName, params);
			
			//EOT
			trans.commit();
			logger.debug("EOT");
			
		} catch(Throwable th){
			th.printStackTrace();
			if(trans != null && trans.isActive()){
				trans.rollback();
				logger.debug("Rollback");
			}
		} finally{
			if(manager != null){
				manager.close();
			}
		}
	}
	
	
	
	final protected Object startResultMethodInTransaction(String methodName, Object... params){
		EntityManager manager = null;
		EntityTransaction trans = null;
		
		Object result = null;
		
		try{
			manager = emf.createEntityManager();
			
			
			trans = manager.getTransaction();
			//BOT
			trans.begin();
			logger.debug("BOT");
			
			result = callResultMethod(manager, methodName, params);
			
			//EOT
			trans.commit();
			logger.debug("EOT");
		} catch(Throwable th){
			th.printStackTrace();
			if(trans != null && trans.isActive()){
				trans.rollback();
				logger.debug("Rollback");
			}
		} finally{
			if(manager != null){
				manager.close();
			}
		}
		
		return result;
	}
	
	
	protected abstract void callVoidMethod(EntityManager manager, String methodName, Object... params);
	
	protected abstract Object callResultMethod(EntityManager manager, String methodName, Object... params);

	
}
