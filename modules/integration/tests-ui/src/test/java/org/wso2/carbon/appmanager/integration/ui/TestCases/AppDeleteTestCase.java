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


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import static org.testng.Assert.assertTrue;


public class AppDeleteTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appMStore;
    private ApplicationInitializingUtil baseUtil;
    private static String appPrefix = "1";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser(appPrefix);
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);

    }

    @Test(groups = {"wso2.appmanager.delete"}, description = "Application delete test")
    public void testApplicationDeletion() throws Exception {

        appMPublisher.login(username, password);
        appMStore.login(username, password);
        appMPublisher.deleteApp(ApplicationInitializingUtil.appId);
        HttpResponse appAvailResponse = HttpRequestUtil.doGet(getGatewayServerURLHttp() +
                appProp.getAppName() + appPrefix + "/" + appProp.getVersion()+ "/", null);
        assertTrue(appAvailResponse.getResponseCode() != 200 , "Application " + appProp.getAppName() +
                " is available. Delete failed.");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        appMStore.logout();
        super.cleanup();
        baseUtil.destroy();
    }

}
