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

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.SubscriptionRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;

import org.wso2.carbon.automation.core.utils.HttpResponse;
import static org.testng.Assert.assertTrue;

public class AppSubscriptionTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private String appName;
    private String version;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appMStore;
    private ApplicationInitializingUtil baseUtil;
    private static String appPrefix = "18";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.testApplicationCreation(appPrefix);
        baseUtil.testApplicationPublish();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appName = appProp.getAppName() + appPrefix;
        version = appProp.getVersion();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient(
                ApplicationInitializingUtil.storeURLHttp);
    }

    @Test(groups = {"wso2.appmanager.appsubscription"}, description = "Application subscription test")
    public void testAppSubscription() throws Exception {
        appMPublisher.login(username, password);

        //login to store
        appMStore.login(username, password);
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(appName,username,version);

        //Send Subscription Request
        HttpResponse subscriptionResponse = appMStore.subscribeForApplication(subscriptionRequest);
        JSONObject subscriptionJsonObject = new JSONObject(subscriptionResponse.getData());
        Boolean subscriptionStatus = (Boolean) subscriptionJsonObject.get("status");
        assertTrue(subscriptionStatus.equals(true) , username + " Unable to subscribe to application : "
                + appProp.getAppName() + ".");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        appMStore.logout();
        super.cleanup();
        baseUtil.destroy();
    }
}

