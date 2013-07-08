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
package org.apache.shiro.authc.credential;

import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;

/**
 * Created with IntelliJ IDEA.
 * User: antoniou
 */
/**
 * Filter that allows access if the current user has the roles specified by the mapped value, or denies access
 * if the user does not have all of the roles specified.
 */
public class RolesAnyAuthorizationFilter extends AuthorizationFilter {
        @SuppressWarnings({"unchecked"})
        public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws IOException {
            boolean retGrantAccessFlg = false;
            Subject subject = getSubject(request, response);
            String[] rolesArray = (String[]) mappedValue;

            if (rolesArray == null || rolesArray.length == 0) {
                //no roles specified, so nothing to check - allow access.
                retGrantAccessFlg = true;
            }
            else {
                Set<String> roles = CollectionUtils.asSet(rolesArray);
                Iterator<String> it0 = roles.iterator();
                String tmpRoleStr = "";
                while (it0.hasNext()) {
                    tmpRoleStr = it0.next();
                    if(subject.hasRole(tmpRoleStr))
                    {
                        retGrantAccessFlg = true;
                        break;
                    }
                }
            }
            return retGrantAccessFlg;
//            return subject.hasAllRoles(roles);
        }

}
