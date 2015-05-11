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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherUIClient;
import org.wso2.carbon.automation.core.BrowserManager;

import java.util.concurrent.TimeUnit;

import static org.wso2.carbon.appmanager.integration.ui.Util.ExtendedAsserts.assertContains;

/**
 * Tests the discovery UI on application discovery
 *
 */
public class AppDiscoveryUITestCase extends APPManagerIntegrationTest {

    private static final String APP_SERVER_URL = "https://localhost:9443/services/";
    private static final String INFO_SUCCESS_DISCOVERY_START_WITH = "Successfully Queried the server";
    private String username;
    private String password;
    private APPMPublisherUIClient appmPublisherUIClient;
    private WebDriver webDriverForPublisher;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        webDriverForPublisher = BrowserManager.getWebDriver();
        appmPublisherUIClient = new APPMPublisherUIClient();
    }

    @Test
    public void testDiscoverListing() throws Exception {
        performInitialDiscovery();

        String status = webDriverForPublisher.findElement(By.className("info-div"))
                .findElement(By.tagName("span")).getText();
        assertContains(status, INFO_SUCCESS_DISCOVERY_START_WITH,
                "The status in info box should be \"" + INFO_SUCCESS_DISCOVERY_START_WITH + "\"");
    }

    public void testPaging() throws Exception {
        performInitialDiscovery();

    }

    /**
     * Performs the initial discovery with the UI.
     *
     */
    private void performInitialDiscovery() {
        appmPublisherUIClient
                .login(webDriverForPublisher, "https://localhost:9443", username, password);
        // find element username
        webDriverForPublisher.get("https://localhost:9443/publisher" + "/assets/discover/webapp/");
        WebElement usernameEle = webDriverForPublisher.findElement(By.id("server-url"));
        usernameEle.sendKeys(APP_SERVER_URL);
        webDriverForPublisher.findElement(By.id("server-username")).sendKeys("admin");
        webDriverForPublisher.findElement(By.id("server-password")).sendKeys("admin");
        webDriverForPublisher.findElement(By.id("btn-discover-asset")).sendKeys("admin");

        webDriverForPublisher.findElement(By.id("btn-discover-asset")).click();
        webDriverForPublisher.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        webDriverForPublisher.close();
        webDriverForPublisher.quit();
    }

}
