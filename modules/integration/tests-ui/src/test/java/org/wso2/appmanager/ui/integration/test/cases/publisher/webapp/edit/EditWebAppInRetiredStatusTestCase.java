/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.ui.integration.test.cases.publisher.webapp.edit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherCreateWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherOverviewWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;
import org.wso2.appmanager.ui.integration.test.utils.AppmUiTestConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;


public class EditWebAppInRetiredStatusTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Edit web app in retired status";
    private String appName = "EditWebAppInRetiredStatusTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String appProvider;

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        appProvider = appCreator.getUserName();
        createWebApp(appCreator);
        changeLifeCycleStatus();
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testEditWebAppWithValidUsers(String userName, String password) throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        PublisherWebAppsListPage webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, userName, password);
        PublisherOverviewWebAppPage overviewWebAppPage = webAppsListPage.goToOverviewPage(appName, appProvider, appVersion);
        new WebDriverWait(driver, 90).until(ExpectedConditions.visibilityOfElementLocated(By.id("overview")));
        boolean status = overviewWebAppPage.isEditLinkAvailable();
        // overviewWebAppPage.logout();
        closeDriver(driver);
        Assert.assertTrue(status, "Edit option is not available to user:" + userName +
                " who has sufficient privileges to edit.");
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testEditWebAppWithInValidUsers(String userName, String password) throws Exception {
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        WebDriver driver = BrowserManager.getWebDriver();
        //login to publisher
        PublisherWebAppsListPage webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER, userName, password);
        if (userName.equals(appCreator.getUserName())) {
            PublisherOverviewWebAppPage overviewWebAppPage = webAppsListPage.goToOverviewPage(appName, appProvider, appVersion);
            new WebDriverWait(driver, 90).until(ExpectedConditions.visibilityOfElementLocated(By.id("overview")));
            boolean status = overviewWebAppPage.isEditLinkAvailable();
            closeDriver(driver);
            Assert.assertTrue(!status, "Edit option is  available to user:" + userName +
                    " who has insufficient privileges to edit.");
        } else { //publisher
            String id = appName + "-" + appProvider + "-" + appVersion;
            boolean status = isElementExist(driver, By.id(id));
            closeDriver(driver);
            Assert.assertTrue(!status, "Edit option is  available to user:" + userName +
                    " who has insufficient privileges to edit.");
        }

    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        //Delete webapp using creator user
        webAppsListPage.deleteApp(appName, appProvider, appVersion, driver);
        closeDriver(driver);
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmUiTestConstants.APP_MANAGER,
                TestUserMode.SUPER_TENANT_ADMIN);
        User adminUser = appMServer.getSuperTenant().getTenantUser("AdminUser");
        return new Object[][]{
                new Object[]{adminUser.getUserName(), adminUser.getPassword()}
        };
    }

    @DataProvider
    public static Object[][] inValidUserModeDataProvider() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmUiTestConstants.APP_MANAGER,
                TestUserMode.SUPER_TENANT_ADMIN);
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        User appPublisher = appMServer.getSuperTenant().getTenantUser("AppPublisher");
        return new Object[][]{
                new Object[]{appCreator.getUserName(), appCreator.getPassword()},
                new Object[]{appPublisher.getUserName(), appPublisher.getPassword()}
        };
    }

    private void createWebApp(User appCreator) throws Exception {
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER,
                appCreator.getUserName(), appCreator.getPassword());
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();
        createWebAppPage.createWebApp(new org.wso2.appmanager.ui.integration.test.dto.WebApp(appName, appName, context,
                appVersion, "http://wso2.com", "http"));
        String id = appName + "-" + appProvider + "-" + appVersion;
        new WebDriverWait(driver, 90).until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
    }

    private void changeLifeCycleStatus() throws Exception {
        WebDriver driver = BrowserManager.getWebDriver();
        PublisherWebAppsListPage webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER,
                appMServer.getSuperTenant().getTenantAdmin().getUserName(),
                appMServer.getSuperTenant().getTenantAdmin().getPassword());
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.SUBMIT_FOR_REVIEW, driver);
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.APPROVE, driver);
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.PUBLISH, driver);
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.DEPRECATE, driver);
        webAppsListPage.changeLifeCycleState(appName, appProvider, appVersion,
                AppmUiTestConstants.LifeCycleStatus.RETIRE, driver);
        closeDriver(driver);
    }
}
