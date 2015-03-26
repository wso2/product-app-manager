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

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.appmanager.integration.ui.Util.WireMonitorServer;
import org.wso2.carbon.automation.core.BrowserManager;

import static org.testng.Assert.assertTrue;

public class SAMLtoBackendTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        ApplicationInitializingUtil baseUtil;
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.testApplicationCreation("6");
        baseUtil.testApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        driver = BrowserManager.getWebDriver();
        storeUIClient = new APPMStoreUIClient();

    }

    @Test(groups = {"wso2.appmanager.samlTest"}, description = "SAML to backend Test Case")
    public void testSAMLToBackend() throws Exception {
        int hostPort = 8080;
        WireMonitorServer server = new WireMonitorServer(hostPort);
        server.start();

        storeUIClient.login(driver, ApplicationInitializingUtil.storeURLHttp, username, password);
        storeUIClient.selectApplication(driver, ApplicationInitializingUtil.appId);
        driver.switchTo().alert().accept();
        assertTrue(server.getCapturedMessage().contains("AppMgtSAML2Response"), "SAML to backend pass failed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        driver.close();
    }

}
