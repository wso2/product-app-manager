/*
 *
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.appmanager.integration.ui.Util.service;

import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;

/**
 * This class is use as a client for Authentication admin service
 */
public class AuthenticationAdmin {
    private static final String SERVICE_NAME = "AuthenticationAdmin";

    /**
     * This method is use to get authentication cookie to call services
     *
     * @param endpoint -backend server url
     */
    public static String getCookie(String endpoint) {

        String cookie = null;
        try {
            AuthenticationAdminStub authAdminStub = new AuthenticationAdminStub(endpoint + SERVICE_NAME);
            boolean loginSuccess = authAdminStub.login("admin", "admin", "localhost");

            if (loginSuccess) {
                ServiceContext serviceContext = authAdminStub._getServiceClient().getLastOperationContext().getServiceContext();
                cookie = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cookie;
    }
}
