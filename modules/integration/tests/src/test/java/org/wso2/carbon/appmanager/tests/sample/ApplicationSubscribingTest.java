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

package org.wso2.carbon.appmanager.tests.sample;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.tests.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.tests.util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.tests.util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.tests.util.bean.AppCreateRequest;
import org.wso2.carbon.appmanager.tests.util.bean.SubscriptionRequest;
import org.wso2.carbon.automation.api.clients.authenticators.AuthenticatorClient;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.api.clients.user.mgt.UserManagementClient;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;


public class ApplicationSubscribingTest extends APPManagerIntegrationTest {
    
    private APPMStoreRestClient appStore;
    private APPMPublisherRestClient appmPublisher;
    //move to base class
    private String storeURLHttp;
    private String publisherURLHttp;
    private String applicationID;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        if (isBuilderEnabled()) {
            storeURLHttp = getServerURLHttp();
            publisherURLHttp = getServerURLHttp();

        } else {
            storeURLHttp = getStoreServerURLHttp();
            publisherURLHttp = getPublisherServerURLHttp();
        }
        
        appStore = new APPMStoreRestClient(storeURLHttp);
        appmPublisher = new APPMPublisherRestClient(publisherURLHttp);

    }

    @Test(groups = {"wso2.appmanager"}, description = "Application Subscription/Unsubscription Test Case")
    public void testApplicationSubscriptionTestCase() throws Exception {

        String appName = "test123";
        String version = "1";
        String context ="/test";
        String applicationURL = "https://sample.com";

        //login to publisher
        appmPublisher.login(userInfo.getUserName(), userInfo.getPassword());
        AppCreateRequest appCreateRequest = new AppCreateRequest();
        appCreateRequest.setOverview_name(appName);
        appCreateRequest.setOverview_context(context);
        appCreateRequest.setOverview_version(version);
        appCreateRequest.setOverview_webAppUrl(applicationURL);
        appCreateRequest.setOverview_provider(userInfo.getUserName());

        //Send application create request
        HttpResponse appCreateResponse = appmPublisher.createApp(appCreateRequest);
        Assert.assertEquals(appCreateResponse.getResponseCode(), 200, "Response code mismatch");
        JSONObject appCreateResponseJsonObject = new JSONObject(appCreateResponse.getData());
        applicationID = (String) appCreateResponseJsonObject.get("id");

        //Send application publishing request
        HttpResponse appPublishResponse = appmPublisher.publishApp(applicationID);
        Assert.assertEquals(appPublishResponse.getResponseCode(), 200, "Response code mismatch");

        //login to store
    	appStore.login(userInfo.getUserName(), userInfo.getPassword());
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(appName,userInfo.getUserName(),version);

        //Send Subscription Request
        HttpResponse subscriptionResponse = appStore.subscribeForApplication(subscriptionRequest);
        JSONObject subscriptionJsonObject = new JSONObject(subscriptionResponse.getData());
        Assert.assertTrue(!(Boolean) subscriptionJsonObject.get("error"), "Error while updating tier permission");
        Assert.assertTrue((Boolean) subscriptionJsonObject.get("status"), "Application is already subscribed");

        //Send Unsubscription Request
        HttpResponse unSubscriptionResponse = appStore.unsubscribeForApplication(subscriptionRequest);
        JSONObject unSubscriptionJsonObject = new JSONObject(unSubscriptionResponse.getData());
        Assert.assertTrue(!(Boolean) unSubscriptionJsonObject.get("error"), "Error while updating tier permission");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
