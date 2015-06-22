/*
 *Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.appmanager.integration.ui.Util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationProperties {

    private String appURL;
    private String transports;
    private String version;
    private String appName;
    private String appDisplayName;
    private String role;
    private String tier;
    private Properties appProp;
    private String tags;
    private String path;
    private String policyPath;
    private String appSample;
    private String anonymousAccessToUrlPattern;
    private String policyGroupName;
    private String throttlingTier;
    private String objPartialMappings;
    private String policyGroupDesc;
    private String mobileAppName;
    private String mobileAppDescription;


    public ApplicationProperties() throws IOException {
        appProp = new Properties();
        String propFileName = "appconfig.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        appProp.load(inputStream);

        if (inputStream == null) {
            throw new FileNotFoundException("Property file '" + propFileName + "' is not found in the classpath");
        }

        setAppURL(appProp.getProperty("appURL"));
        setTransports(appProp.getProperty("appTransport"));
        setVersion(appProp.getProperty("appVersion"));
        setTier(appProp.getProperty("tier"));
        setAppName(appProp.getProperty("appName"));
        setAppDisplayName(appProp.getProperty("appDisplayName"));
        setTags(appProp.getProperty("tags"));
        setRole(appProp.getProperty("role"));
        setPath(appProp.getProperty("path"));
        setPolicyPath(appProp.getProperty("policyFile"));
        setAppSample(appProp.getProperty("appSample"));
        setAnonymousAccessToUrlPattern(appProp.getProperty("anonymousAccessToUrlPattern"));
        setPolicyGroupName(appProp.getProperty("policyGroupName"));
        setThrottlingTier(appProp.getProperty("throttlingTier"));
        setObjPartialMappings(appProp.getProperty("objPartialMappings"));
        setPolicyGroupDesc(appProp.getProperty("policyGroupDesc"));
        setMobileAppName(appProp.getProperty("mobileAppName"));
        setMobileAppDescription(appProp.getProperty("mobileAppDescription"));
    }

    public String getAppURL() {
        return appURL;
    }

    private void setAppURL(String appURL) {
        this.appURL = appURL;
    }

    public String getTransports() {
        return transports;
    }

    private void setTransports(String transports) {
        this.transports = transports;
    }

    public String getVersion() {
        return version;
    }

    private void setVersion(String version) {
        this.version = version;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppDisplayName() {
        return appDisplayName;
    }

    public void setAppDisplayName(String appDisplayName) {
        this.appDisplayName = appDisplayName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPolicyPath() {
        return policyPath;
    }

    public void setPolicyPath(String policyPath) {
        this.policyPath = policyPath;
    }

    public String getAppSample() {
        return appSample;
    }

    public void setAppSample(String appSample) {
        this.appSample = appSample;
    }

    public String getAnonymousAccessToUrlPattern() {
        return anonymousAccessToUrlPattern;
    }

    public void setAnonymousAccessToUrlPattern(String anonymousAccessToUrlPattern) {
        this.anonymousAccessToUrlPattern = anonymousAccessToUrlPattern;
    }

    public String getPolicyGroupName() {
        return policyGroupName;
    }

    public void setPolicyGroupName(String policyGroupName) {
        this.policyGroupName = policyGroupName;
    }

    public String getThrottlingTier() {
        return throttlingTier;
    }

    public void setThrottlingTier(String throttlingTier) {
        this.throttlingTier = throttlingTier;
    }

    public String getObjPartialMappings() {
        return objPartialMappings;
    }

    public void setObjPartialMappings(String objPartialMappings) {
        this.objPartialMappings = objPartialMappings;
    }

    public String getPolicyGroupDesc() {
        return policyGroupDesc;
    }

    public void setPolicyGroupDesc(String policyGroupDesc) {
        this.policyGroupDesc = policyGroupDesc;
    }


    public String getMobileAppName() {
        return mobileAppName;
    }

    public void setMobileAppName(String mobileAppName) {
        this.mobileAppName = mobileAppName;
    }


    public String getMobileAppDescription() {
        return mobileAppDescription;
    }

    public void setMobileAppDescription(String mobileAppDescription) {
        this.mobileAppDescription = mobileAppDescription;
    }
}
