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
/**
 * Test case which verifies the ability of subscribers rating apps.
 */
public class RatingTestCase {
    private static final String TEST_DESCRIPTION = "Test web application ratings";
    private APPMPublisherRestClient appmPublisherRestClient;
    private APPMStoreRestClient appmStoreRestClient;
    private AutomationContext appMServer;
    private String appName = "RatingTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String trackingCode = "AM_" + appName;
    private User adminUser;
    private String userName;
    private String password;
    private String backEndUrl;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);

        adminUser = appMServer.getSuperTenant().getTenantAdmin();
        userName = adminUser.getUserName();
        password = adminUser.getPassword();

        appmPublisherRestClient.login(userName, password);
        appmStoreRestClient = new APPMStoreRestClient(backEndUrl);
    }

    @Test(description = TEST_DESCRIPTION)
    public void testWebAppRating() throws Exception {
        HttpResponse response = appmPublisherRestClient.webAppCreate(appName, context, appVersion, trackingCode,
                                                                     userName);
        JSONObject responseData = new JSONObject(response.getData());
        String uuid = responseData.getString(AppmTestConstants.ID);
        appmPublisherRestClient.publishWebApp(uuid);

        final int ratingValue1 = 3;

        // Subscribe and rate to app from admin user
        HttpResponse adminUserResponse = subscribeAndRateApplication(adminUser, appName, userName, appVersion,
                                                                     uuid, ratingValue1);
        JSONObject responseData1 = new JSONObject(adminUserResponse.getData());
        Assert.assertEquals(adminUserResponse.getResponseCode(), 200, "Response code mismatch");
        Assert.assertEquals(responseData1.getDouble(AppmTestConstants.USER), 3.0,
                            "Error while rating the web application as user : " + userName);
        Assert.assertEquals(responseData1.getDouble(AppmTestConstants.AVERAGE), 3.0,
                            "Error while retrieving the average rating for user : " + userName);


        User testUser = appMServer.getSuperTenant().getTenantUser("testuser1");
        final int ratingValue2 = 5;

        // Subscribe and rate to app from testuser1
        HttpResponse testUserResponse = subscribeAndRateApplication(testUser, appName, userName, appVersion,
                                                                    uuid, ratingValue2);
        Assert.assertEquals(testUserResponse.getResponseCode(), 200, "Response code mismatch");
        JSONObject responseData2 = new JSONObject(testUserResponse.getData());
        Assert.assertEquals(responseData2.getDouble("user"), 5.0,
                            "Error while rating the web application as user : " + testUser.getUserName());
        Assert.assertEquals(responseData2.getDouble("average"), 4.0,
                            "Error while retrieving the average rating for user : " + testUser.getUserName());
    }

    private HttpResponse subscribeAndRateApplication(User loginUser, String appName, String userName,String appVersion,
                                                     String uuid, int ratingValue) throws Exception {
        //Login to store
        appmStoreRestClient.login(loginUser.getUserName(), loginUser.getPassword());
        SubscriptionRequest adminSubscriptionRequest = new SubscriptionRequest(appName, userName, appVersion);
        //Send Subscription request.
        appmStoreRestClient.subscribeForApplication(adminSubscriptionRequest);

        //Rate web application
        HttpResponse response = appmStoreRestClient.rateApplication(uuid, AppmTestConstants.WEB_APP, ratingValue);
        //Logout from store
        appmStoreRestClient.logout();
        return response;
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        appmPublisherRestClient.logout();
    }
}
