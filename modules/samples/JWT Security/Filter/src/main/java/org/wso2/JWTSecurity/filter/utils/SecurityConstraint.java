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

@XmlType(name = "SecurityConstraint")
@XmlAccessorType(XmlAccessType.FIELD)
/**
 * This class is to store url pattern and authConstraint which contains list of roles which are allowed to access the
 * given url
 */
public class SecurityConstraint {

    @XmlElement(name = "urlPattern")
    private String urlPattern;
    @XmlElement(name = "authConstraint")
    private AuthConstraint authConstraint;

    /**
     * This methode returns an AuthConstraint object.
     * @return a Auth constraint Object.
     */
    public AuthConstraint getAuthConstraint() {
        return authConstraint;
    }

    /**
     * This method asing values to auth authConstraint
     * @param authConstraint AuthConstraint object
     */
    public void setAuthConstraint(AuthConstraint authConstraint) {
        this.authConstraint = authConstraint;
    }

    /**
     * This method returns a URL Pattern
     * @return This is String which contains URL Pattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * This methode is to set url pattern
     * @param urlPattern parse a string as url pattern.
     */
    public void setUrlPattern(String urlPattern) {

        this.urlPattern = urlPattern;
    }

}
