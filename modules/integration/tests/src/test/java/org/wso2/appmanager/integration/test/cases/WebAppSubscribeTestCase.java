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

package org.wso2.appmanager.integration.test.cases;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.APPMStoreRestClient;
import org.wso2.appmanager.integration.utils.bean.AppCreateRequest;
import org.wso2.appmanager.integration.utils.bean.SubscriptionRequest;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import static org.testng.Assert.assertEquals;

public class WebAppSubscribeTestCase {

    private static final String TEST_DESCRIPTION = "Verify Subscribing to a Web App";
    private APPMPublisherRestClient appmPublisherRestClient;
    private APPMStoreRestClient appmStoreRestClient;
    private String appName = "WebAppSubscribeTestCase";
    private String appVersion = "1.0.0";
    private String context = "/WebAppSubscribeTestCase";
    private String trackingCode = "WebAppSubscribeTestCase";
    private String userName = "admin";
    private String password = "admin";
    private String backEndUrl;


    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        AutomationContext appMServer = new AutomationContext("App Manager",
                                                             TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        appmPublisherRestClient.login(userName, password);
        appmStoreRestClient = new APPMStoreRestClient(backEndUrl);
        appmStoreRestClient.login(userName, password);
    }

    @Test(description = TEST_DESCRIPTION)
    public void testPublisherCreateWebApp() throws Exception {
        AppCreateRequest appRequest = new AppCreateRequest(appName, context, appVersion, trackingCode);
        HttpResponse response = appmPublisherRestClient.webAppCreate(appRequest);

        JSONObject responseData = new JSONObject(response.getData());
        String uuid = responseData.getString("id");
        appmPublisherRestClient.publishWebApp(uuid);

        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(appName, userName, appVersion);
        HttpResponse subscriptionResponse= appmStoreRestClient.subscribeForApplication(subscriptionRequest);
        JSONObject subscribedData = new JSONObject(subscriptionResponse.getData());
        assertEquals(subscribedData.get("status"), "true", "User hasn't subscribed successfully" );
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {

    }
}
