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

package org.wso2.appmanager.ui.integration.test.cases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherCreateWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.utils.AppManagerIntegrationTest;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;

public class PublisherCreateWebAppTestCase extends AppManagerIntegrationTest {

    private static final String TEST_DESCRIPTION = "Verify Creating a Web App";
    private static final Log log = LogFactory.getLog(PublisherCreateWebAppTestCase.class);

    private LoginPage loginPage;
    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    private WebDriver driver = null;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();

        loginPage = LoginPage.getPage(driver, appMServer, LoginPage.LoginTo.PUBLISHER);
        webAppsListPage = (PublisherWebAppsListPage) loginPage.login(appMServer.getSuperTenant().getTenantAdmin().getUserName(),
                appMServer.getSuperTenant().getTenantAdmin().getPassword(), LoginPage.LoginTo.PUBLISHER);
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testPublisherCreateWebApp() throws Exception {

        createWebAppPage = webAppsListPage.gotoCreateWebAppPage(appMServer);
        createWebAppPage.createWebApp(new WebApp("Test1", "Test1", "/test1",
                            "1.0", "http://wso2.com", "http"));
        Thread.sleep(12000);

        createWebAppPage = webAppsListPage.gotoCreateWebAppPage(appMServer);
        webAppsListPage = createWebAppPage.createWebApp(new WebApp("Test2", "Test2", "/test2",
                "2.0", "http://wso2.org", "http"));
        Thread.sleep(12000);
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        if(driver != null){
            driver.close();
            driver.quit();
        }
    }
}
