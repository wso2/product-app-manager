/*
*  Copyright (c) 2005-2011, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.JWTSecurity.servlets;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.security.Principal;
import java.util.List;

/**
 * This class is to wrap the oridginal HttpServeletRequest with roles and users names coming from JWT token
 */
/*package private*/
class UserRoleRequestWrapper extends HttpServletRequestWrapper {
    private Principal userPrincipal;
    private List<String> roles;
    private HttpServletRequest realRequest;

    /**
     * @param user    user name from decoded JWT Token
     * @param roles   List of roles of the user from decoded JWT token
     * @param request Original request
     */
    public UserRoleRequestWrapper(final String user, List<String> roles, HttpServletRequest request) {
        super(request);
        this.roles = roles;
        this.realRequest = request;

        if (user == null) {
            this.userPrincipal = realRequest.getUserPrincipal();
        } else {
            this.userPrincipal = new Principal() {
                @Override
                public String getName() {
                    return user;
                }
            };
        }

    }

    /**
     * @param role role name to compare with the roles in the request
     * @return If the role list comming from JWT token is null it looks in to the roles in the original request and
     * return true or false.
     */
    @Override
    public boolean isUserInRole(String role) {
        if (roles == null) {
            return this.realRequest.isUserInRole(role);
        }
        return roles.contains(role);
    }

    /**
     *
     * @return Returns the principal
     */
    @Override
    public Principal getUserPrincipal() {
        return userPrincipal;
    }
}
