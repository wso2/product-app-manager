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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * This class is to store a list of JWT security constraints which can be read from JWTSecurity.xml, In a element of
 * that list it will contain a uri pattern and a list of roles which are authorised for that url pattern
 */
@XmlRootElement(name = "JWTSecurityConstraints")
@XmlAccessorType(XmlAccessType.FIELD)
public class JWTSecurityConstraints {

    @XmlElement(name = "SecurityConstraint")
    private List<SecurityConstraint> securityConstraint;


    public JWTSecurityConstraints() {
    }

    /**
     * This method creates JWTSecurityConstraints object by getting a list of securityConstraits
     *
     * @param securityConstraintList a list of security constraints stored in JWTSecurityConstraints.xml
     */
    public JWTSecurityConstraints(List<SecurityConstraint> securityConstraintList) {
        super();
        this.securityConstraint = securityConstraintList;
    }

    /**
     * This method will return a list of security constraints stored in JWTSecuriyConstraints.xml
     * @return returns a list of security constraints.
     */
    public List<SecurityConstraint> getSecurityConstraint() {
        return securityConstraint;
    }
    /**
     *This method asign values to the  securityConstraint.
     * @param securityConstraint list of security constraints which are stored in JWTSecurityConstraints.xml.
     */
    public void setSecurityConstraint(List<SecurityConstraint> securityConstraint) {
        this.securityConstraint = securityConstraint;
    }
}
