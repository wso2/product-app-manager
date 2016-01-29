/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.ui.integration.test.utils;

public class AppmUiTestConstants {
    public static final String APP_MANAGER = "App Manager";

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

    public static final String APP_CREATOR = "AppCreator";
    public static final String ADMIN = "AdminUser";
    public static final String APP_PUBLISHER = "AppPublisher";
    public static final String SAMPLE_DEPLOYED_WEB_APP_NAME = "travel-booking-1.0";
    public static final String SAMPLE_DEPLOYED_WEB_APP_BLOCKED_RESOURCE = "index.jsp";
    public static final String SAMPLE_DEPLOYED_WEB_APP_ANONYMOUS_RESOURCE = "loging.jsp";
    public static final String UNAUTHORIZED_LOGIN_REDIRECT_PAGE = "authenticationendpoint";
}
