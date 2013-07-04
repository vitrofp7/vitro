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
package org.apache.shiro.authc.credential;

/**
 * User: antoniou
 * TMP fix needed for shiro to work with jdbc salted passwords
 */
import org.apache.shiro.authc.AuthenticationInfo;

/**
 * Applies a temporary fix to the PasswordMatcher since it does not take character arrays into account.
 */
public class TempFixPasswordMatcher extends PasswordMatcher {

    @Override
    protected Object getStoredPassword(AuthenticationInfo storedAccountInfo) {
        Object stored = super.getStoredPassword(storedAccountInfo);
        if (stored instanceof char[]) {
            return new String((char[])stored);
        }
        return stored;
    }
}
