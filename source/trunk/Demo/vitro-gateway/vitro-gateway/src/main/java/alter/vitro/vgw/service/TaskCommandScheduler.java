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
package alter.vitro.vgw.service;

/**
 * Will coordinate the processing of incoming queries and commands, as well as internal processes (like the trust-routing service)
 */
public class TaskCommandScheduler {
    // highest priority is highest number
    private final int UNKNOWN_PRIORITY = -1;
    private final int VSN_QUERY = 1;
    private final int TRUST_ROUTING_SERVICE = 2;
    private final int REPORT_RESOURCES = 4;
    private final int EQUIV_LIST_UPDT = 5;
    private final int ENABLE_NODE_UPDT = 6;

    private final int TIME_BETWEEN_QUERIES_OF_SAME_VSN_SECs = 30;//space them apart to not flood the VGW/WSI

    //
    // the task scheduler runs through a cyclical queue
    // it considers the period of repeating tasks (so that they won't execute before-hand)
    // - TODO but also, so that they will execute close to their period
    // TODO: Queries for the same VSN id, will not execute
    // TODO: Standard queries are executed once and they are removed from schedule queue (later they could run in their defined period, instead of getting polled from the VSP)
    // TODO: Tasks with high priority are executed first.
    // TODO: After executing a task with high priority, we move to the task with next highest (if any) and continue all the way to priority 1.
    //          so as not having tasks of high priority monopolizing the VGW.
    // TODO: The scheduler for the periodic trust routing poll service should be moved/merged with the this scheduler!

}
