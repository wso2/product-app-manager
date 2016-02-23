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
package org.wso2.JWTSecurity.filter.utils;

import java.util.List;

/**
 * This class is user authenticator class which provides facility to authenticate users. It takes URL pattern, User Role
 * list from jwt Token, security constraint list from JWTSecurityConstraints.xml then compare the values and
 * authenticate the user.
 */
public class UserAuthenticator {

    private List<SecurityConstraint> securityConstraintList;

    /**
     * This is the class constructor of the class it takes a list of security constraint as method argument.
     *
     * @param securityConstraintList a list of securityConstraintList.
     */

    public UserAuthenticator(List<SecurityConstraint> securityConstraintList) {
        this.securityConstraintList = securityConstraintList;

    }

    /**
     * This method takes a list of roles from JWT token and the url pattern, if URL pattern do not match return true
     * (for un secured applications), if URL pattern matches then look in to the role list, if user role matches it
     * returns true else return false.
     *
     * @param rolesList JWT Token user roles list
     * @param uri       uri of the application
     * @return returns true or false
     */
    public boolean isUserAuthenticated(List<String> rolesList, String uri) {
        SecurityConstraint securityConstraint = findSecurityConstraintForUri(uri);

        if (securityConstraint != null) {
            List<RoleName> roleNames = securityConstraint.getAuthConstraint().getRoleName();
            return isMatchingRoleExists(roleNames, rolesList);
        }
        //It returns true when it found a URI patterns which are not defined in the JWTSecurityCostraints.xml
        return true;
    }

    /**
     * This method takes uri as argument and if uri matches with any url pattern defined in JWTSecurityconstraints.xml
     * and if uri matches with it it returns a securityConstraint object else return null.
     *
     * @param uri
     * @return
     */
    private SecurityConstraint findSecurityConstraintForUri(String uri) {
        if (uri == null) {
            return null;
        }
        for (SecurityConstraint securityConstraint : securityConstraintList) {
            String urlPattern = securityConstraint.getUrlPattern();
            if (uri.contains(urlPattern)) {
                return securityConstraint;
            }//TODO use regex to match uri patterns
        }
        return null;
    }

    /**
     * This method takes two role list, one is from jwt token and the other one is from JWTSecurityConstraints.xml. if
     * user roles intersects in those two list return true, else return false.
     *
     * @param roleName List of roles from JWT token.
     * @param rolesList List of roles from xml.
     * @return boolean.
     */
    private boolean isMatchingRoleExists(List<RoleName> roleName, List<String> rolesList) {
        for (RoleName aRoleName : roleName) {
            String authenticatedRole = aRoleName.getRoleName();
            if (rolesList.contains(authenticatedRole)) {
                return true;
            }
        }
        return false;
    }
    public boolean isURISecured(String uri){
        for (SecurityConstraint securityConstraint : securityConstraintList) {
            String urlPattern = securityConstraint.getUrlPattern();
            if (uri.contains(urlPattern)) {
                return true;
            }//TODO use regex to match uri patterns
        }
        return false;
    }

}
