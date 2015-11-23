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

package org.wso2.appmanager.integration.utils.bean;

/**
 * This class is used to generate the payload for user subscription.
 */
public class SubscriptionRequest extends AbstractRequest {

    private String name;
    private String provider;
    private String version;
    private String applicationName = "DefaultApplication";
    private String tier = "Unlimited";

    public SubscriptionRequest(String apiName, String provider, String version) {
        this.name = apiName;
        this.provider = provider;
        this.version = version;
    }

    @Override
    public void setAction() {
        setAction("addAPISubscription");
    }

    @Override
    public void init() {
        addParameter("apiName", name);
        addParameter("apiProvider", provider);
        addParameter("apiVersion", version);
        addParameter("appName", applicationName);
        addParameter("apiTier", tier);

    }

    /**
     * Get Api Name.
     * @return Api Name String.
     */
    public String getName() {
        return name;
    }

    /**
     * Get App Provider.
     * @return provider String.
     */
    public String getProvider() {
        return provider;
    }

    /**
     * Get App Version.
     * @return version String.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set App Version.
     * @param version String.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get Application Name.
     * @return applicationName String.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Set Application Name.
     * @param applicationName String.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Get Tier.
     * @return tier String.
     */
    public String getTier() {
        return tier;
    }

    /**
     * Set Tier.
     * @param tier String.
     */
    public void setTier(String tier) {
        this.tier = tier;
    }
}
