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

import java.io.File;

public class AppmTestConstants {
    public static final String APP_MANAGER = "App Manager";

    public static final String AVERAGE = "average";

    public static final String BUSINESS_OWNER_ID = "businessOwnerId";

    public static final String BUSINESS_OWNER_NAME = "businessOwnerName";

    public static final String BUSINESS_OWNER_EMAIL= "businessOwnerEmail";

    public static final String BUSINESS_OWNER_DESCRIPTION = "businessOwnerDescription";

    public static final String BUSINESS_OWNER_SITE = "businessOwnerSite";

    public static final String CONTENT_TYPE = "Content-Type";

    public static final String COOKIE = "Cookie";

    public static final String DATA = "data";

    public static final String ERROR = "error";

    public static final String ID = "id";

    public static final String LIFE_CYCLE_STATE = "lifecycleState";

    public static final String MESSAGE = "message";

    public static final String OK = "ok";

    public static final String PUBLISHED = "Published";

    public static final String RESPONSE = "response";

    public static final String SET_COOKIE = "Set-Cookie";

    public static final String STATUS = "status";

    public static final String SUCCESS = "success";

    public static final String TYPE = "type";

    public static final String USER = "user";

    public static final String WEB_APP = "webapp";

    public static final String LOG4J_PROPERTY_FILE_PATH = File.separator + "repository" + File.separator + "conf" +
            File.separator + "log4j.properties";

    public static final String AUDIT_LOGS_FILE_PATH = File.separator + "repository" + File.separator + "logs" +
            File.separator + "audit.log";

    public class PubliserRestApis {
        public static final String ADD_NEW_TAGS = "/publisher/api/tag/webapp/";
        public static final String ADD_POLICY_GROUP = "/publisher/api/entitlement/policy/partial/policyGroup/save";
        public static final String ADD_SSO_PROVIDER = "/publisher/api/sso/addConfig";
        public static final String EDIT_SSO_PROVIDER = "/publisher/api/sso/editConfig";
        public static final String GET_WEB_APP_PROPERTY = "/publisher/api/asset/webapp/";
        public static final String CREATE_APP = "/publisher/asset/webapp";
        public static final String EDIT_WEB_APP = "/publisher/api/asset/webapp/";
        public static final String DELETE_WEB_APP = "/publisher/api/asset/delete/webapp/";
        public static final String ADD_ROLES = "/publisher/asset/webapp/id/";
        public static final String LOGIN_URL = "/publisher/api/authenticate?action=login";
        public static final String LOGOUT_URL = "/publisher/api/authenticate?action=logout";


    }

    public class StoreRestApis {
        public static final String LOGIN_URL = "/store/apis/user/login";
        public static final String LOGOUT_URL = "/store/apis/user/logout";
        public static final String SUBSCRIBE_FOR_APPS = "/store/resources/webapp/v1/subscription/app";
        public static final String UNSUBSCRIBE_FOR_APPS = "/store/resources/webapp/v1/unsubscription/app";

        public static final String RATING_FOR_APPS = "/store/apis/rate?id={id}&type={appType}&value={ratingValue}";
    }

    public class AdminDashBoardApis {
        public static final String LOGIN_URL = "/admin-dashboard/site/blocks/user/login/ajax/login.jag";
        public static final String BUSINESS_OWNER_URL = "/admin-dashboard/apis/businessowners";
        public static final String UPDATE_BUSINESS_OWNER_URL = "/admin-dashboard/apis/businessowners/update";
    }

    public class VariableTemplates {
        public static final String ID = "{id}";
        public static final String APP_TYPE = "{appType}";
        public static final String RATING_VALUE = "{ratingValue}";

    }

    public class LifeCycleStatus {
        public static final String SUBMIT_FOR_REVIEW = "Submit for Review";
        public static final String APPROVE = "Approve";
        public static final String PUBLISH = "Publish";
        public static final String UNPUBLISH = "Unpublish";
        public static final String DEPRECATE = "Deprecate";
        public static final String RETIRE = "Retire";
        public static final String RE_PUBLISH = "Re-Publish";
        public static final String RECYCLE = "Recycle";
        public static final String REJECT = "Reject";
    }

    public class TestUsers {
        public static final String APP_CREATOR = "AppCreator";
        public static final String ADMIN = "AdminUser";
        public static final String APP_PUBLISHER = "AppPublisher";
    }

    public class AuditLogActions{
        public static final String USER_LOGGED_IN = "UserLoggedIn";
        public static final String NEW_ASSET_ADDED = "NewAssetAdded";
        public static final String ASSET_UPDATED = "AssetUpdated";
        public static final String LIFE_CYCLE_ACTION_PERFORMED = "LifecycleActionPerformed-";
        public static final String ASSET_DELETED = "AssetDeleted";
        public static final String USER_LOGGED_OUT = "UserLoggedOut";
    }
}
