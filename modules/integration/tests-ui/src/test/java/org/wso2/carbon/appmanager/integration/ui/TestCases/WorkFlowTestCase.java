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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;

import javax.activation.DataHandler;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static org.testng.Assert.assertTrue;

/**
 * This test case is not run with default APP manager integration tests. To run this test we assume that BPS server
 * is  running with port offset 2 and all the configurations are done to the BPS as mentioned in the work flow extension
 * doc.APP Manager workflow-extensions.xml is in /resources/artifacts/AM/configFiles/workflowtest/
 * <p/>
 * Flow of the tests
 * 1.Add the workflow-extensions.xml which is in /resources/artifacts/AM/configFiles/workflowtest/ to server
 * 2.Restart the server (if server is not restarted because of some registry caching issue changes in step1
 * will not be applied)
 * 3.run the tests cases
 * 4.revert the workflow-extensions.xml to default one(in destry() method).
 */
public class WorkFlowTestCase extends APPManagerIntegrationTest {
    private String appName;
    private String appId;
    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;
    private ResourceAdminServiceClient resourceAdminServiceStub;
    String parentPath = "/_system/governance/apimgt/applicationdata/";
    String workFlowFileName = "workflow-extensions.xml";
    String newFileName = "workflow-extensions1.xml";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        // rename the default workflow-extensions.xml to workflow-extensions1.xml
        // then add the workflow-extensions.xml from configFiles/workflowtest to registry
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(amServer.getBackEndUrl(), amServer.getSessionCookie());
        String resourcePath = parentPath + workFlowFileName;
        resourceAdminServiceStub.renameResource(parentPath, resourcePath, newFileName);
        DataHandler dh = new DataHandler(new URL("file:///" + ProductConstant.getResourceLocations
                (ProductConstant.AM_SERVER_NAME)
                + File.separator + "configFiles" + File.separator + "workflowtest/" + workFlowFileName));
        resourceAdminServiceStub.addResource(resourcePath, "application/xml", "xml files", dh);
        // restart the server to apply the changes(due to caching)
        ServerConfigurationManager serverConfigurationManager = new
                ServerConfigurationManager(amServer.getBackEndUrl());
        serverConfigurationManager.restartGracefully();
        //since restarted initialise again
        super.init(0);

        ApplicationInitializingUtil baseUtil;
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("workflow");
        baseUtil.testWebApplicationPublish();
        appName = ApplicationInitializingUtil.appName;
        appId = ApplicationInitializingUtil.appId;
        baseUrl = getServerURLHttp();
    }

    @Test(groups = {"wso2.appmanager.workflow"}, description = "Test user signup work flow")
    public void userSignupRejectWorkFlowTestCase() throws Exception {
        startNewSession();
        String userName = "usertest";
        String passWord = "user1@test";
        //signup in store
        signupInStore(userName, passWord);
        //reject in workflow-admin
        loginToWorkFlowApp();
        userSignupWorkFlowApproval("reject");
        //login to store
        loginToStore(userName, passWord);
        assertTrue(driver.getPageSource().contains("No Privileges to login"),
                "User signup workflow failed:user can login even though task is rejected");
    }

    @Test(groups = {"wso2.appmanager.workflow"}, description = "Test user signup work flow")
    public void userSignupApproveWorkFlowTestCase() throws Exception {
        startNewSession();
        String userName = "usertest1";
        String passWord = "user1@test";
        //signup in store
        signupInStore(userName, passWord);
        //approve in workflow-admin
        loginToWorkFlowApp();
        userSignupWorkFlowApproval("approve");
        //login to store
        loginToStore(userName, passWord);
        assertTrue(driver.findElement(By.xpath("//a[contains(.,'" + userName + "')]")) != null,
                "User signup workflow failed:user can not login even though task is approved");
    }

    @Test(groups = {"wso2.appmanager.workflow"}, description = "Test subscription  work flow")
    public void subscriptionRejectWorkFlowTestCase() throws Exception {
        startNewSession();
        String userName = "adminuser";
        String passWord = "adminuser";
        loginToStore(userName, passWord);
        subscribeToAPP();
        // go to workflow and reject the subscription
        loginToWorkFlowApp();
        subscriptionWorkFlowApproval("reject");
        //go to store ,select the app and check for unsubscribe button
        //loginToStore(userName, passWord);
        driver.get(baseUrl + "/store");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Web Applications")));
        driver.findElement(By.linkText("Web Applications")).click();
        driver.findElement(By.cssSelector("a[href*='" + appId + "']")).click();
        assertTrue(driver.findElement(By.id("btnSubscribe")) != null, "Subscription workflow failed:user" +
                " is subscribed even though task is rejected");
    }

    private void startNewSession() throws MalformedURLException {
        if (driver != null) {
            driver.close();
        }
        driver = BrowserManager.getWebDriver();
        wait = new WebDriverWait(driver, 30);
    }

    @Test(groups = {"wso2.appmanager.workflow"}, description = "Test subscription  work flow")
    public void subscriptionApproveWorkFlowTestCase() throws Exception {
        startNewSession();
        String userName = "admin";
        String passWord = "admin";
        loginToStore(userName, passWord);
        subscribeToAPP();
        // go to workflow and approve the subscription
        loginToWorkFlowApp();
        subscriptionWorkFlowApproval("approve");
        //go to store ,select the app and check for unsubscribe button
        //loginToStore(userName, passWord);
        driver.get(baseUrl + "/store");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Web Applications")));
        driver.findElement(By.linkText("Web Applications")).click();
        driver.findElement(By.cssSelector("a[href*='" + appId + "']")).click();
        assertTrue(driver.findElement(By.id("btnUnsubscribe")) != null, "Subscription workflow failed");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if (driver != null) driver.quit();
        // revert to original workflow-extensions.xml
        String resourcePath = parentPath + workFlowFileName;
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(amServer.getBackEndUrl(), amServer.getSessionCookie());
        resourceAdminServiceStub.deleteResource(resourcePath);
        resourcePath = parentPath + newFileName;
        resourceAdminServiceStub.renameResource(parentPath, resourcePath, workFlowFileName);
        super.cleanup();
    }

    /**
     * login to store app
     *
     * @param userName User Name
     * @param passWord Password
     */
    private void loginToStore(String userName, String passWord) {
        driver.get(baseUrl + "/store");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Sign in")));
        driver.findElement(By.linkText("Sign in")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(passWord);
        driver.findElement(By.cssSelector("input.btn.btn-primary")).click();
    }

    /**
     * login to work flow admin app as admin
     */
    private void loginToWorkFlowApp() {
        driver.get(baseUrl + "/admin-dashboard");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("pass")).clear();
        driver.findElement(By.id("pass")).sendKeys("admin");
        driver.findElement(By.id("loginButton")).click();
    }

    /**
     * Approve or reject the User sign up work flow task
     *
     * @param action approve or reject
     */
    private void userSignupWorkFlowApproval(String action) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Pending User Creations")));
        driver.findElement(By.linkText("Pending User Creations")).click();
        driver.findElement(By.cssSelector("button.btn.js_startBtn")).click();
        if ("reject".equals(action)) {
            new Select(driver.findElement(By.cssSelector("select.js_stateDropDown.pull-left")))
                    .selectByVisibleText("Reject");
        }
        driver.findElement(By.id("js_completeBtn0")).click();
    }

    /**
     * Sign up in store
     *
     * @param userName User Name
     * @param passWord Password
     */
    private void signupInStore(String userName, String passWord) {
        driver.get(baseUrl + "/store");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-register")));
        driver.findElement(By.id("btn-register")).click();
        driver.findElement(By.id("inp-username-register")).click();
        driver.findElement(By.id("inp-username-register")).clear();
        driver.findElement(By.id("inp-username-register")).sendKeys(userName);
        driver.findElement(By.id("inp-password-register")).clear();
        driver.findElement(By.id("inp-password-register")).sendKeys(passWord);
        driver.findElement(By.id("inp-password-confirm")).clear();
        driver.findElement(By.id("inp-password-confirm")).sendKeys(passWord);
        driver.findElement(By.id("btn-register-submit")).click();
        driver.findElement(By.linkText("OK")).click();
    }

    /**
     * Approve or reject the subscription work flow task
     *
     * @param action
     */
    private void subscriptionWorkFlowApproval(String action) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Pending Subscriptions")));
        driver.findElement(By.linkText("Pending Subscriptions")).click();
        driver.findElement(By.id("js_startBtn0")).click();
        if ("reject".equals(action)) {
            new Select(driver.findElement(By.cssSelector("select.js_stateDropDown.pull-left")))
                    .selectByVisibleText("Reject");
        }
        driver.findElement(By.id("js_completeBtn0")).click();
    }

    /**
     * Subscribe to applicaiton in store
     *
     * @throws InterruptedException
     */
    private void subscribeToAPP() throws InterruptedException {
        //go to store
        driver.get(baseUrl + "/store/");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Web Applications")));
        driver.findElement(By.linkText("Web Applications")).click();
        // select the app and subscribe
        // There is delay  for app to appear in store.So refresh the page and check for the app
        long timeBefore = System.nanoTime();
        long timeElapsed = 0;
        while (true) {
            Thread.sleep(1000);
            if (driver.getPageSource().contains(appId)) {
                driver.findElement(By.cssSelector("a[href*='" + appId + "']")).click();
                driver.findElement(By.id("btnSubscribe")).click();
                driver.findElement(By.cssSelector("#messageModal1 > div.modal-footer > a.btn.btn-other")).click();
                break;
            } else if (timeElapsed >= 6.0e+10) {
                assertTrue(false, "Application " + appName + " is not visible in store");
            }
            timeElapsed = System.nanoTime() - timeBefore;
            driver.navigate().refresh();

        }
    }

}
