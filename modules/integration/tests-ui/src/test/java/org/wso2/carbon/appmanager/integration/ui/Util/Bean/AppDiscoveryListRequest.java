/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.Util.Bean;

/**
 * Application discovery request for querying the list of applications
 */
public class AppDiscoveryListRequest {
    private String serverType;
    private String serverUrl;
    private String serverUserName;
    private String serverPassword;
    private String appStatus;
    private String appNameStartsWith;
    private String discoveryAction;

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword(String serverPassword) {
        this.serverPassword = serverPassword;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getAppNameStartsWith() {
        return appNameStartsWith;
    }

    public void setAppNameStartsWith(String appNameStartsWith) {
        this.appNameStartsWith = appNameStartsWith;
    }

    public String getServerUserName() {
        return serverUserName;
    }

    public void setServerUserName(String serverUserName) {
        this.serverUserName = serverUserName;
    }

    public String getDiscoveryAction() {
        return discoveryAction;
    }

    public void setDiscoveryAction(String discoveryAction) {
        this.discoveryAction = discoveryAction;
    }

    public String generatePostData() {
        StringBuilder sb = new StringBuilder();
        sb.append("serverType=").append(serverType).append("&")
                .append("serverUrl=").append(serverUrl).append("&")
                .append("serverUserName=").append(serverUserName).append("&")
                .append("serverPassword=").append(serverPassword).append("&")
                .append("appNameStartsWith=").append(appNameStartsWith).append("&")
                .append("appStatus=").append(appStatus).append("&")
                .append("discoveryAction=").append(discoveryAction);
        return sb.toString();
    }
}
