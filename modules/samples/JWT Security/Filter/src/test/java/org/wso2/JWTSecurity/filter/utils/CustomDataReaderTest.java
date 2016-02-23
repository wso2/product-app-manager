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

import java.io.InputStream;

/**
 * Created by visitha on 1/27/16.
 */
public class CustomDataReaderTest extends TestCase {

    public void testGetCustomData()
            throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();


        InputStream inputStream = classLoader.getResourceAsStream("JWTSecurityConstraints.xml");
        JWTSecurityConstraintsReader JWTSecurityConstraintsReader = new JWTSecurityConstraintsReader();
        JWTSecurityConstraints JWTSecurityConstraints = JWTSecurityConstraintsReader.getCustomData(inputStream);
        assertNotNull(JWTSecurityConstraints);
       assertNotNull(JWTSecurityConstraints.getSecurityConstraint());
        assertNotNull((JWTSecurityConstraints.getSecurityConstraint().get(0)));
        assertNotNull((JWTSecurityConstraints.getSecurityConstraint().get(0).getAuthConstraint()));
        assertNotNull((JWTSecurityConstraints.getSecurityConstraint().get(0).getUrlPattern()));
        assertNotNull((JWTSecurityConstraints.getSecurityConstraint().get(0).getAuthConstraint().getRoleName()));
        assertNotNull((JWTSecurityConstraints.getSecurityConstraint().get(0).getAuthConstraint().getRoleName().get(0)));
        assertEquals(2,(JWTSecurityConstraints.getSecurityConstraint().get(0).getAuthConstraint().getRoleName().size()));
        assertNotNull((JWTSecurityConstraints.getSecurityConstraint().get(0).getAuthConstraint().getRoleName().get(0).getRoleName()));
 }
}