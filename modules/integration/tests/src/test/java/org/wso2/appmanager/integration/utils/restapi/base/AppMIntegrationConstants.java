/*
*Copyright (c) 2016​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.appmanager.integration.utils.restapi.base;

/**
 * define constants that are used in AppM integration tests
 */
public class AppMIntegrationConstants {

    //instance names
    public static final String APPM_PRODUCT_GROUP_NAME = "App Manager";
    public static final String APPM_STORE_INSTANCE = "store";
    public static final String APPM_PUBLISHER_INSTANCE = "publisher";
    public static final String APPM_GATEWAY_MGT_INSTANCE = "gateway-mgt";
    public static final String APPM_GATEWAY_WRK_INSTANCE = "gateway-wrk";
    public static final String APPM_KEY_MANAGER_INSTANCE = "keyManager";
    public static final String BACKEND_SERVER_INSTANCE = "backend-server";

    //Response element names
    public static final String API_RESPONSE_ELEMENT_NAME_ERROR = "error";
    public static final String API_RESPONSE_ELEMENT_NAME_SUBSCRIPTION = "subscriptions";
    public static final String API_RESPONSE_ELEMENT_NAME_APPLICATIONS = "applications";
    public static final String API_RESPONSE_ELEMENT_NAME_API_NAME = "name";
    public static final String API_RESPONSE_ELEMENT_NAME_API_VERSION = "version";
    public static final String API_RESPONSE_ELEMENT_NAME_API_PROVIDER = "provider";
    public static final String API_RESPONSE_ELEMENT_NAME_APIS = "apis";
    public static final String API_RESPONSE_ELEMENT_NAME_ID = "id";


    public static final String STORE_APPLICATION_REST_URL = "store/site/pages/applications.jag";

    public class PubliserRestApis {
        public static final String LOGIN_URL = "publisher/api/authenticate?action=login";
        public static final String LOGOUT_URL = "publisher/api/authenticate?action=logout";


    }

    public class StoreRestApis {
        public static final String LOGIN_URL = "store/apis/user/login";
        public static final String LOGOUT_URL = "store/apis/user/logout";
    }

}
