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
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.SubscriptionRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;


public class LifeCycleTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private WebDriver driver;
    private APPMStoreRestClient appMStore;
    private APPMPublisherRestClient appMPublisher;
    private ApplicationInitializingUtil baseUtil;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("3");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        driver = BrowserManager.getWebDriver();
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.
                APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
    }

    /*
     * Testing the application is invokable
     * No need to subscribe since app is subscribe in ApplicationCreation Test Case
     */

    @Test(groups = {"wso2.appmanager.lifecycleTest"},
            description = "Lifecycle Test Case With Un Publishing the application")
    public void testApplicationSubscribe() throws Exception {

        appMPublisher.login(username, password);
        appMStore.login(username, password);

        //Un subscribe the Application
        SubscriptionRequest appUnSubscriptionRequest = new SubscriptionRequest(ApplicationInitializingUtil.appName,
                username, ApplicationInitializingUtil.version);
        HttpResponse unSubscriptionResponse = appMStore.unsubscribeForApplication(appUnSubscriptionRequest,"webapp");
        JSONObject unSubscriptionJsonObject = new JSONObject(unSubscriptionResponse.getData());
        assertFalse((Boolean) unSubscriptionJsonObject.get("error"), "Error while updating tier permission");
        assertTrue((Boolean) unSubscriptionJsonObject.get("status"),
                "Application " + appProp.getAppName() + " is already un subscribed");

        //Un publish the app
        HttpResponse appUnPublishResponse = appMPublisher.unPublishApp(ApplicationInitializingUtil.appId,"webapp");
        assertEquals(appUnPublishResponse.getResponseCode(), 200, "Response code mismatch");

        //Test app is subscribe
        SubscriptionRequest appSubscriptionRequest = new SubscriptionRequest(ApplicationInitializingUtil.appName,
                username, ApplicationInitializingUtil.version);
        HttpResponse subscriptionResponseUnpublished = appMStore.subscribeForApplication(appSubscriptionRequest);
        JSONObject subscriptionJsonObjectUnPublished = new JSONObject(subscriptionResponseUnpublished.getData());
        assertFalse((Boolean) subscriptionJsonObjectUnPublished.get("error"),
                "Error while updating tier permission");
        assertTrue((Boolean) subscriptionJsonObjectUnPublished.get("status"),
                "Application " + appProp.getAppName() + " is already subscribed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        String parentWindow = driver.getWindowHandle();
        Set<String> handles =  driver.getWindowHandles();
        for(String windowHandle  : handles) {
            if(!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }
        driver.switchTo().window(parentWindow);
        driver.close();
        baseUtil.destroy();
    }
}
