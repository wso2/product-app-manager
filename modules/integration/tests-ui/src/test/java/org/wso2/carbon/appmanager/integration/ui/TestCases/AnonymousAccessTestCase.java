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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import static org.testng.Assert.assertEquals;
/**
 * This class is use to test anonymous access for a web app
 */
public class AnonymousAccessTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appMStore;
    private ApplicationInitializingUtil baseUtil;
    private static String appPrefix = "anonymousAccessWebApp";
    private static String appId;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithAnonymousAccess(appPrefix, "true");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        appId = baseUtil.appId;
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient(
                ApplicationInitializingUtil.storeURLHttp);

    }
    /**
     * Tests access web app which creates with allow anonymous access
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.AnonymouseAccess"}, description = "Allow Anonymous Access to web app")
    public void testApplicationDeletion() throws Exception {

        appMPublisher.login(username, password);
        appMStore.login(username, password);
        appMPublisher.deleteApp(ApplicationInitializingUtil.appId);
        HttpResponse appAvailResponse = HttpRequestUtil.doGet(
                "http://localhost:9763/store/assets/webapp/" + appId, null);
        Document html = Jsoup.parse(appAvailResponse.getData().toString());
        String title = html.title();
        assertEquals(title, "Store | webapp", "Anonymous Access Web application Publishing failed");

    }
}
