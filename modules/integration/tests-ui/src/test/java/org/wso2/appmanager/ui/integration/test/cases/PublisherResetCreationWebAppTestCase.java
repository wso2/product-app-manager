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
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherCreateWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;


public class PublisherResetCreationWebAppTestCase extends AppManagerIntegrationTest {

    private static final String TEST_DESCRIPTION = "Reset data that use to create a  Web App";
    private static final Log log = LogFactory.getLog(PublisherCreateWebAppTestCase.class);

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();

        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testPublisherResetCreationWebApp() throws Exception {

        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();


        //create  web app
        createWebAppPage = createWebAppPage.resetAppData(new WebApp("Test1", "Test1", "/test1",
                "1.0", "http://wso2.com", "http"));

        WebDriverWait wait = new WebDriverWait(driver, 90);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_name")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_displayName")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_context")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_version")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_webAppUrl")));


        String assertionError = "Crate web app reset form failed";
        Assert.assertEquals(driver.findElement(By.id("overview_name")).getText().equals(""), true, assertionError);
        Assert.assertEquals(driver.findElement(By.id("overview_displayName")).getText().equals(""), true, assertionError);
        Assert.assertEquals(driver.findElement(By.id("overview_context")).getText().equals(""), true, assertionError);
        Assert.assertEquals(driver.findElement(By.id("overview_version")).getText().equals(""), true, assertionError);
        Assert.assertEquals(driver.findElement(By.id("overview_webAppUrl")).getText().equals(""), true, assertionError);



    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }

}
