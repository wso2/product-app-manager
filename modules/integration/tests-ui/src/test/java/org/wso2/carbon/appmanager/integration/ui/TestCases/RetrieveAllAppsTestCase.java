/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
*KIND, either express or implied. See the License for the
*specific language governing permissions and limitations
*under the License.
*/
package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;

import static org.testng.Assert.assertTrue;

public class RetrieveAllAppsTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private String appName;
    private String appVersion;
    private String appType;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appMStore;
    private ApplicationInitializingUtil baseUtil;
    private static String appPrefix = "22";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.testApplicationCreation(appPrefix);
        baseUtil.testApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appName = appProp.getAppName() + appPrefix;
        appVersion = appProp.getVersion();
        appType = "webapp";
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient(
                ApplicationInitializingUtil.storeURLHttp);
    }

    // TODO : This test case should be re-written, ApplicationInitializingUtil only holds the information of the last created app.
    @Test(groups = {"wso2.appmanager.allappsretrieval"}, description = "Retrieve All Apps test")
    public void testAllAppsRetrieval() throws Exception {
        appMPublisher.login(username, password);
        appMStore.login(username, password);

        JSONArray subscriptionJsonArray = appMStore.getAllWebAppsProperties(username, password, 1,1);
        JSONObject subscribedUser;
        for (int index = 0; index < subscriptionJsonArray.length(); index++) {
            subscribedUser = subscriptionJsonArray.getJSONObject(index);

            String appId = (String) subscribedUser.get("id");
            assertTrue((appId.equals(ApplicationInitializingUtil.appId) == true), "Unable to Retrieve application property.");

            // Check App Type
            String type = (String) subscribedUser.get("type");
            assertTrue((type.equals(appType) == true), "Unable to Retrieve application property.");

            // Check lifecycleState
            String lifecycleState = (String) subscribedUser.get("lifecycleState");
            assertTrue((lifecycleState.equalsIgnoreCase("Published") == true), "Unable to Retrieve application property.");

            // Check Path attribute
            String appPropertyString = username + "/" + appName + "/" + appVersion + "/" + appType;
            String path = (String) subscribedUser.get("path");
            assertTrue(path.endsWith(appPropertyString) , "Unable to Retrieve application property.");
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        appMStore.logout();
        super.cleanup();
        baseUtil.destroy();
    }
}
