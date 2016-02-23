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


import org.wso2.JWTSecurity.Exceptions.JWTSecurityException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;

/**
 * This calss reads data (unmarshal) data from JWTSecurity.xml file using jaxb
 */
public class JWTSecurityConstraintsReader {
    /**
     *
     * @param inputStream this is the stream of JWTSecurity.xml file
     * @return JWTSecurityConstraints which contains all the authentication and authorisation details
     * @throws JWTSecurityException
     */
    public JWTSecurityConstraints getCustomData(InputStream inputStream) throws JWTSecurityException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JWTSecurityConstraints.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            JWTSecurityConstraints que = (JWTSecurityConstraints) jaxbUnmarshaller.unmarshal(inputStream);
            return que;
        } catch (JAXBException e) {
            throw new JWTSecurityException(
                    "JWTSecurityConstraints.xml reading error", e);
        }
    }
}