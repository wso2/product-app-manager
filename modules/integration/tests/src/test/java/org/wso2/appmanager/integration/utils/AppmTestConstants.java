/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.integration.utils;

public class AppmTestConstants {
    public static final String APP_MANAGER = "App Manager";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String COOKIE = "Cookie";

    public static final String DATA = "data";

    public static final String ERROR = "error";

    public static final String ID = "id";

    public static final String LIFE_CYCLE_STATE = "lifecycleState";

    public static final String MESSAGE = "message";

    public static final String PUBLISHED = "Published";

    public static final String RESPONSE = "response";

    public static final String SET_COOKIE = "Set-Cookie";

    public static final String STATUS = "status";

    public static final String TYPE = "type";

    public static final String WEB_APP = "webapp";

    public class PubliserRestApis {
        public static final String ADD_NEW_TAGS = "/publisher/api/tag/webapp/";
        public static final String ADD_POLICY_GROUP = "/publisher/api/entitlement/policy/partial/policyGroup/save";
        public static final String ADD_SSO_PROVIDER = "/publisher/api/sso/addConfig";
        public static final String GET_WEB_APP_PROPERTY = "/publisher/api/asset/webapp/";
        public static final String CREATE_APP = "/publisher/asset/webapp";
        public static final String LOGIN_URL = "/publisher/api/authenticate?action=login";
        public static final String LOGOUT_URL = "/publisher/api/authenticate?action=logout";


    }

    public class StoreRestApis {
        public static final String LOGIN_URL = "/store/apis/user/login";
        public static final String LOGOUT_URL = "/store/logout";
        public static final String SUBSCRIBE_FOR_APPS = "/store/resources/webapp/v1/subscription/app";
        public static final String UNSUBSCRIBE_FOR_APPS = "/store/resources/webapp/v1/unsubscription/app";
    }
}
