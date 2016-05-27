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
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.APPMStoreRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.bean.SubscriptionRequest;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

public class WebAppSubscribeTestCase {

    private static final String TEST_DESCRIPTION = "Verify Subscribing to a Web App";
    private APPMPublisherRestClient appmPublisherRestClient;
    private APPMStoreRestClient appmStoreRestClient;
    private String appName = "WebAppSubscribeTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String trackingCode = "AM_" + appName;
    private String appCreatedUser;


    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER,
                                                             TestUserMode.SUPER_TENANT_ADMIN);
        String backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        User appCreator = appMServer.getSuperTenant().getTenantUser("AdminUser");
        appCreatedUser = appCreator.getUserName();
        appmPublisherRestClient.login(appCreatedUser, appCreator.getPassword());
        HttpResponse appCreateResponse = appmPublisherRestClient.webAppCreate(appName, context,
                                                        appVersion, trackingCode , appCreatedUser);
        JSONObject appCreateResponseData = new JSONObject(appCreateResponse.getData());
        String uuid = appCreateResponseData.getString(AppmTestConstants.ID);
        appmPublisherRestClient.publishWebApp(uuid);
        User subscriber = appMServer.getSuperTenant().getTenantUser("Subscriber");
        appmStoreRestClient = new APPMStoreRestClient(backEndUrl);
        appmStoreRestClient.login(subscriber.getUserName(), subscriber.getPassword());

    }

    @Test(description = TEST_DESCRIPTION)
    public void testWebAppSubscriptionAndUnSubscription() throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(appName, appCreatedUser,
                                                                          appVersion);


        //Send Subscription request.
        HttpResponse subscriptionResponse = appmStoreRestClient.subscribeForApplication(
                subscriptionRequest);
        JSONObject subscriptionJsonObject = new JSONObject(subscriptionResponse.getData());
        Assert.assertTrue(!(Boolean) subscriptionJsonObject.get("error"),
                          "Error while updating tier permission.");
        Assert.assertTrue((Boolean) subscriptionJsonObject.get(AppmTestConstants.STATUS),
                          "Application is already subscribed.");

        //Send Unsubscription Request
       HttpResponse unSubscriptionResponse = appmStoreRestClient.unsubscribeForApplication(
                subscriptionRequest);
        JSONObject unSubscriptionJsonObject = new JSONObject(unSubscriptionResponse.getData());
        Assert.assertTrue(!(Boolean) unSubscriptionJsonObject.get("error"),
                          "Error while updating tier permission.");
        Assert.assertTrue((Boolean) subscriptionJsonObject.get(AppmTestConstants.STATUS),
                          "Application is already unsubscribed.");
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        appmPublisherRestClient.logout();
        appmStoreRestClient.logout();
    }
}
