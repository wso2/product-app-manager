/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.AppDiscoveryListRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import static org.testng.Assert.assertEquals;
import static org.wso2.carbon.appmanager.integration.ui.Util.ExtendedAsserts.assertContains;

/**
 * Tests the application discovery API integration
 */
public class AppDiscoveryTestCase extends APPManagerIntegrationTest {

    private static final String APP_SERVER_URL = "https://localhost:9443/services/";
    private static final String INFO_SUCCESS_DISCOVERY_START_WITH = "Successfully Queried the server";
    private String username;
    private String password;
    private APPMPublisherRestClient appMPublisher;
    private ApplicationInitializingUtil baseUtil;
    private static String appPrefix = "1";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
    }

    /**
     * Tests whether the application discovery listing works
     * @throws Exception
     */
    @Test(groups = { "wso2.appmanager.discovery" }, description = "Application Discovery List test")
    public void testApplicationDiscoveryList() throws Exception {

        appMPublisher.login(username, password);
        AppDiscoveryListRequest appDiscoveryListRequest = new AppDiscoveryListRequest();
        appDiscoveryListRequest.setServerUrl(APP_SERVER_URL);
        appDiscoveryListRequest.setDiscoveryAction("discoverAssets");

        HttpResponse appDiscoveryResponse = appMPublisher
                .getDiscoverableApplications(appDiscoveryListRequest);
        assertEquals(appDiscoveryResponse.getResponseCode(), 200);
        assertContains(appDiscoveryResponse.getData(), "proxy-context-Test1WarByAdminOn",
                "There should be one application listed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();

    }

}
