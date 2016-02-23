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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * This class is to store a List of role names which can be read from JWTSecurity.xml
 */
@XmlType(name = "authConstraint")
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthConstraint {
    @XmlElement(name = "roleName")
    private List<RoleName> roleName;

    /**
     *
     * @return Returns a list of rolenames stored in JWTSecurityConstraints.xml
     */
    public List<RoleName> getRoleName() {

        return roleName;
    }

    /**
     * Set values to the rolenames
     * @param roleName list of rolenames stored in JWTSecurityConstraints.xml
     */
    public void setRoleName(List<RoleName> roleName) {

        this.roleName = roleName;
    }
}
