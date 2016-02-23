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
package org.wso2.tomcat.authenticator;

import org.apache.catalina.realm.GenericPrincipal;

import java.security.Principal;
import java.util.List;

/**
 * Generic principal to store user details read from the JWT token
 */
public class JWTGenericPrincipal extends GenericPrincipal {

    private String username;
    private List<String> roleList;

    public JWTGenericPrincipal(String name, String password, List<String> roles) {
        super(name, password, roles);
        this.username = name;
        this.roleList = roles;
    }

    /**
     * Return the username of the generic principal.
     *
     * @return string user name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the set of roles in the JWT token as a list.
     *
     * @return List<String>  roles list.
     */
    public List<String> getRoleList() {
        return roleList;
    }
}
