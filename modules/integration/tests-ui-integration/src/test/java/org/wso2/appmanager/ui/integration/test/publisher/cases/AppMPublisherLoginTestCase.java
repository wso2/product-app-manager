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

package org.wso2.appmanager.ui.integration.test.publisher.cases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.publisher.pages.WebAppsListPage;
import org.wso2.appmanager.ui.integration.test.publisher.pages.LoginPage;
import org.wso2.appmanager.ui.integration.utils.AppManagerIntegrationTest;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;

public class AppMPublisherLoginTestCase extends AppManagerIntegrationTest {

    private static final String TEST_DESCRIPTION = "Verify login to App Manager Publisher";
    private static final Log log = LogFactory.getLog(AppMPublisherLoginTestCase.class);

    private static final String LOGIN_URI = "/publisher/login";

    private WebDriver driver = null;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        driver = BrowserManager.getWebDriver();
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testPublisherLogin() throws Exception {
        driver.get(appMServer.getContextUrls().getWebAppURLHttps() + LOGIN_URI);
        LoginPage loginPage = new LoginPage(driver);
        WebAppsListPage webAppsListPage = loginPage.loginAs(appMServer.getSuperTenant().getTenantAdmin().getUserName(),
                appMServer.getSuperTenant().getTenantAdmin().getPassword());

    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        if(driver != null){
            driver.close();
            driver.quit();
        }
    }


}