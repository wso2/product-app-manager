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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import static org.testng.Assert.assertEquals;

/**
 * This class is use to test create and publish features in web application
 */
public class AppCreateAndPublishTestCase extends APPManagerIntegrationTest {
    private ApplicationInitializingUtil baseUtil;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appmStore;
    private String username;
    private String password;
    private String appId;
    private String mobileAppId;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        appmStore = new APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
        username = userInfo.getUserName();
        password = userInfo.getPassword();
    }

    /**
     * Tests create web application
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.webappcreation"}, description = "Web Application Create test")
    public void testCreateWebapp() throws Exception {
        appMPublisher.login(username, password);
        HttpResponse appCreateResponse = baseUtil.createWebApplicationWithExistingUser("CreateWebApplication");
        JSONObject jsonObject = new JSONObject(appCreateResponse.getData());
        appId = jsonObject.get("id").toString();
        assertEquals(appCreateResponse.getResponseCode(), 200, "Web application creation failed");
        JSONObject stateResponse = new JSONObject(appMPublisher.getCurrentState(appId, "webapp").getData().toString());
        assertEquals(stateResponse.get("state"), "Created", "Web application creation failed");
    }

    /**
     * Tests create mobile application
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.mobileappcreation"}, description = "Mobile application Create test"
            , dependsOnMethods = {"testCreateWebapp"})
    public void testCreateMobileApp() throws Exception {
        String response = baseUtil.createMobileApplication();
        JSONObject responseObject = new JSONObject(response);
        mobileAppId = responseObject.get("id").toString();
        assertEquals(mobileAppId != null, true, "Mobile application creation failed");
        JSONObject stateResponse = new JSONObject(appMPublisher.getCurrentState(mobileAppId, "mobileapp").getData());
        assertEquals(stateResponse.get("state"), "Created", "Mobile application creation failed");
    }


    /**
     * Tests publish web application
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.webapppublish"}, description = "Web Application publish test",
            dependsOnMethods = {"testCreateWebapp"})
    public void testPublishWebapp() throws Exception {
        HttpResponse appPublishResponse = appMPublisher.publishApp(appId, "webapp");
        assertEquals(appPublishResponse.getResponseCode(), 200, "Web application publishing failed");
        JSONObject stateResponse = new JSONObject(appMPublisher.getCurrentState(appId, "webapp").getData().toString());
        assertEquals(stateResponse.get("state"), "Published", "Web application Publishing failed");
    }

    /**
     * Tests publish mobile application
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.mobieappublish"}, description = "Mobile Application publish test",
            dependsOnMethods = {"testCreateMobileApp"})
    public void testPublishMobileApp() throws Exception {
        HttpResponse appPublishResponse = appMPublisher.publishApp(mobileAppId, "mobileapp");
        assertEquals(appPublishResponse.getResponseCode(), 200, "Mobile application publishing failed");
        JSONObject stateResponse = new JSONObject(appMPublisher.getCurrentState(mobileAppId, "mobileapp").getData()
                .toString());
        assertEquals(stateResponse.get("state"), "Published", "Mobile application Publishing failed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        baseUtil.destroy();
        appmStore.logout();
    }

}
