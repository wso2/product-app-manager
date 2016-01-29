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
import org.testng.annotations.AfterClass;
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
 * This Test class verifies the ability of appCreator, appPublisher and admin users to delete web apps in 'Unpublished' state
 */
public class DeleteWebAppInUnpublishedStateTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Delete web application in Unpublished state.";
    private static final String TEST_APP_NAME_SUFFIX = "DeleteWebAppInUnpublishedStateTestCase";
    private static final String creatorDeleteAppTest = AppmUiTestConstants.APP_CREATOR + TEST_APP_NAME_SUFFIX;
    private static final String publisherDeleteAppTest = AppmUiTestConstants.APP_PUBLISHER + TEST_APP_NAME_SUFFIX;
    private static final String adminDeleteAppTest = AppmUiTestConstants.ADMIN + TEST_APP_NAME_SUFFIX;

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;
    private String appVersion = "1.0.0";
    private String contextPrefix = "/";
    private String appProvider;
    private User appCreator;
    private User admin;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        appCreator = appMServer.getSuperTenant().getTenantUser(AppmUiTestConstants.APP_CREATOR);
        admin = appMServer.getSuperTenant().getTenantUser(AppmUiTestConstants.ADMIN);
        appProvider = appCreator.getUserName();

        //Login to Publisher as AppCreator
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER,
                appCreator.getUserName(), appCreator.getPassword());
        //Create webapps for each test scenario
        createWebApp(creatorDeleteAppTest);
        createWebApp(publisherDeleteAppTest);
        createWebApp(adminDeleteAppTest);
        closeDriver(driver);

        WebDriver driver = BrowserManager.getWebDriver();
        //Login to Publisher as admin user
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER,
                admin.getUserName(), admin.getPassword());
        //Unpublish web apps in each test scenario
        changeLifeCycleStateIntoUnpublished(creatorDeleteAppTest, driver);
        changeLifeCycleStateIntoUnpublished(publisherDeleteAppTest, driver);
        changeLifeCycleStateIntoUnpublished(adminDeleteAppTest, driver);
        closeDriver(driver);
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testUnpublishedWebAppDeleteWithValidUsers(String username, String password, String appName)
            throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, username, password);
        boolean isDeleted = webAppsListPage.deleteApp(appName, appProvider, appVersion, driver);
        closeDriver(driver);
        Assert.assertTrue(isDeleted, "Delete option is not available to user:" + username +
                " who has sufficient privileges to delete.");
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testUnpublishedWebAppDeleteWithInValidUsers(String username, String password, String appName)
            throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, username, password);
        boolean isDeleteButtonAvailable = webAppsListPage.isDeleteButtonAvailable(appName, appProvider, appVersion);
        closeDriver(driver);
        Assert.assertTrue(!isDeleteButtonAvailable, "Delete option is available to user:" + username +
                " who has insufficient privileges to delete.");
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

    private void changeLifeCycleStateIntoUnpublished(String appName, WebDriver driver) throws Exception {
        //Promote the web app life cycle state into In Review state
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.SUBMIT_FOR_REVIEW, driver);
        //Promote the web app life cycle state into Approved state
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.APPROVE, driver);
        //Promote the web app life cycle state into Published state
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.PUBLISH, driver);
        //Promote the web app life cycle state into Deprecated state
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.UNPUBLISH, driver);
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, admin.getUserName(),
                admin.getPassword());
        webAppsListPage.deleteApp(publisherDeleteAppTest, appProvider, appVersion, driver);
        closeDriver(driver);
    }
}
