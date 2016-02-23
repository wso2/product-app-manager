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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * This class is used to store rolenames
 */

@XmlType(name = "RoleName")
@XmlAccessorType(XmlAccessType.FIELD)
public class RoleName {
    @XmlValue
    private String roleName;

    /**
     * This method is to get a role name, it returns a string
     * @return Returns a String as a role name
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * This method is to set role name
     * @param roleName String from JWTSeccurityConstrints.xml
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
