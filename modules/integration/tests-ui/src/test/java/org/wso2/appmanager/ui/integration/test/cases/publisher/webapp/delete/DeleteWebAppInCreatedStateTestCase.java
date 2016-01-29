/*
 *
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * /
 */

package org.wso2.appmanager.ui.integration.test.cases.publisher.webapp.delete;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherCreateWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;
import org.wso2.appmanager.ui.integration.test.utils.AppmUiTestConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;

/**
 * This Test class verifies the ability of appCreator, appPublisher and admin users to delete web apps in 'Created' state
 */
public class DeleteWebAppInCreatedStateTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Delete web application in Created state.";
    private static final String TEST_APP_NAME_SUFFIX = "DeleteWebAppInCreatedStateTestCase";
    private static final String creatorDeleteAppTest = AppmUiTestConstants.APP_CREATOR + TEST_APP_NAME_SUFFIX;
    private static final String publisherDeleteAppTest = AppmUiTestConstants.APP_PUBLISHER + TEST_APP_NAME_SUFFIX;
    private static final String adminDeleteAppTest = AppmUiTestConstants.ADMIN + TEST_APP_NAME_SUFFIX;

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;
    private String appVersion = "1.0.0";
    private String contextPrefix = "/";
    private String appProvider;
    private User appCreator;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        appCreator = appMServer.getSuperTenant().getTenantUser(AppmUiTestConstants.APP_CREATOR);
        appProvider = appCreator.getUserName();

        //Login to Publisher as AppCreator
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER,
                appCreator.getUserName(), appCreator.getPassword());

        //Create webapps for each test scenario
        createWebApp(creatorDeleteAppTest);
        createWebApp(publisherDeleteAppTest);
        createWebApp(adminDeleteAppTest);
        closeDriver(driver);
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testCreatedWebAppDeleteWithValidUsers(String username, String password, String appName)
            throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, username, password);


        boolean isDeleted = webAppsListPage.deleteApp(appName, appProvider, appVersion, driver);

        closeDriver(driver);
        Assert.assertTrue(isDeleted, "Delete option is available to user:" + username +
                " who has sufficient privileges to delete.");
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testCreatedWebAppDeleteWithInValidUsers(String username, String password, String appName)
            throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, username, password);
        String id = appName + "-" + appProvider + "-" + appVersion;
        boolean status = isElementExist(driver,By.id(id));
        // overviewWebAppPage.logout();
        closeDriver(driver);
        Assert.assertTrue(!status, "Delete option is  available to user:" + username +
                " who has insufficient privileges to edit.");
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmUiTestConstants.APP_MANAGER,
                TestUserMode.SUPER_TENANT_ADMIN);
        User appCreator = appMServer.getSuperTenant().getTenantUser(AppmUiTestConstants.APP_CREATOR);
        User adminUser = appMServer.getSuperTenant().getTenantUser(AppmUiTestConstants.ADMIN);
        return new Object[][]{
                new Object[]{appCreator.getUserName(), appCreator.getPassword(), creatorDeleteAppTest},
                new Object[]{adminUser.getUserName(), adminUser.getPassword(), adminDeleteAppTest}
        };
    }

    @DataProvider
    public static Object[][] inValidUserModeDataProvider() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmUiTestConstants.APP_MANAGER,
                TestUserMode.SUPER_TENANT_ADMIN);
        User appPublisher = appMServer.getSuperTenant().getTenantUser(AppmUiTestConstants.APP_PUBLISHER);
        return new Object[][]{
                new Object[]{appPublisher.getUserName(), appPublisher.getPassword(), publisherDeleteAppTest}
        };
    }

    public void createWebApp(String appName) throws Exception {
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();
        //create webapp
        createWebAppPage.createWebApp(new WebApp(appName, appName, contextPrefix + appName,
                appVersion, "http://wso2.com", "http"));
        new WebDriverWait(driver, 120).until(ExpectedConditions.visibilityOfElementLocated(By.linkText(appName)));
    }

}
