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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class UserAuthenticatorTest extends TestCase {

    public void testMultipleUsersInJWTMultipleUsersInXml() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleName1.setRoleName("adminad");
        roleName2.setRoleName("visitha");
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        authRoles.add("visitha");
        authRoles.add("Minura");
        authRoles.add("Ravindu");
        authRoles.add("Chandupa");
        authRoles.add("WIjesinghe");
        authRoles.add("Manel");
        String uri = "first/greeting";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(true,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }

    public void testNoUserInJWTMultipleUsersInXml() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleName1.setRoleName("adminad");
        roleName2.setRoleName("visitha");
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        String uri = "first/greeting";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(false,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }

    public void testOneUserInJWTMultipleUsersInXml() throws Exception {
        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleName1.setRoleName("admin");
        roleName2.setRoleName("visitha");
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        String uri = "first/greeting";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(true,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }
//
    public void testMultipleUserInJWTNoUserInXml() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        authRoles.add("visitha");
        authRoles.add("Minura");
        authRoles.add("Ravindu");
        authRoles.add("Chandupa");
        authRoles.add("WIjesinghe");
        authRoles.add("Manel");
        String uri = "first/greeting";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(false,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }
//
//
    public void testOneUserInJWTOneUserInXml() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleName1.setRoleName("admin");
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        String uri = "first/greeting";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(true,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }
//
    public void testNoUserInJWTNoUserInXml() throws Exception {
        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        String uri = "first/greeting";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(false,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }
    public void testWithoutUri() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleName1.setRoleName("adminad");
        roleName2.setRoleName("visitha");
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        authRoles.add("visitha");
        authRoles.add("Minura");
        authRoles.add("Ravindu");
        authRoles.add("Chandupa");
        authRoles.add("WIjesinghe");
        authRoles.add("Manel");
        String uri = null;
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(true,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }
    public void testWithUnMatchingUri() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        roleName1.setRoleName("adminad");
        roleName2.setRoleName("visitha");
        roleNames.add(roleName1);
        roleNames.add(roleName2);
        AuthConstraint authConstraint = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        SecurityConstraint securityConstraints = new SecurityConstraint();
        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraintsList.add(securityConstraints);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        authRoles.add("visitha");
        authRoles.add("Minura");
        authRoles.add("Ravindu");
        authRoles.add("Chandupa");
        authRoles.add("WIjesinghe");
        authRoles.add("Manel");
        String uri = "Unknown";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(true,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }

    public void testMultipleSecurityConstraints() throws Exception {

        RoleName roleName1 = new RoleName();
        RoleName roleName2 = new RoleName();
        List<RoleName> roleNames = new ArrayList<RoleName>();
        List<RoleName> roleNames2 = new ArrayList<RoleName>();
        roleName1.setRoleName("admin");
        roleName2.setRoleName("visitha");
        roleNames.add(roleName1);
        roleNames2.add(roleName2);

        AuthConstraint authConstraint = new AuthConstraint();
        AuthConstraint authConstraint2 = new AuthConstraint();
        authConstraint.setRoleName(roleNames);
        authConstraint2.setRoleName(roleNames2);

        SecurityConstraint securityConstraints = new SecurityConstraint();
        SecurityConstraint securityConstraints2 = new SecurityConstraint();

        List<SecurityConstraint> securityConstraintsList = new ArrayList<SecurityConstraint>();
        securityConstraints.setUrlPattern("/greeting");
        securityConstraints2.setUrlPattern("/welcome");

        securityConstraints.setAuthConstraint(authConstraint);
        securityConstraints2.setAuthConstraint(authConstraint2);

        securityConstraintsList.add(securityConstraints);
        securityConstraintsList.add(securityConstraints2);
        List<String> authRoles = new ArrayList<String>();
        authRoles.add("admin");
        authRoles.add("visitha");
        authRoles.add("Minura");
        authRoles.add("Ravindu");
        authRoles.add("Chandupa");
        authRoles.add("WIjesinghe");
        authRoles.add("Manel");
        String uri = "/welcome";
        UserAuthenticator userAuthenticator = new UserAuthenticator(securityConstraintsList);
        userAuthenticator.isUserAuthenticated(authRoles, uri);
        assertEquals(true,userAuthenticator.isUserAuthenticated(authRoles, uri) );
    }

}