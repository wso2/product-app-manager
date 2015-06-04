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

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherUIClient;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.wso2.carbon.appmanager.integration.ui.Util.ExtendedAsserts.assertContains;

/**
 * Tests the discovery UI on application discovery
 *
 */
public class AppDiscoveryUITestCase extends APPManagerIntegrationTest {

    private static final String INFO_SUCCESS_DISCOVERY_START_WITH = "Successfully Queried the server";
    private String username;
    private String appServerServiceUrl = null; // e.g. "https://localhost:9443/services/";
    private String appMServerUrl = null; //e.g "https://localhost:9443/";
    private String password;
    private APPMPublisherUIClient appmPublisherUIClient;
    private WebDriver webDriverForPublisher;

    @BeforeMethod(groups = { "wso2.appmanager.discovery" }, alwaysRun = true)
    public void initBrowser() throws Exception {
        webDriverForPublisher = BrowserManager.getWebDriver();
    }

    @BeforeSuite(groups = { "wso2.appmanager.discovery" }, alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appmPublisherUIClient = new APPMPublisherUIClient();
        appServerServiceUrl = getServerBackendUrlHttps() + "/services/";
        appMServerUrl = getServerBackendUrlHttps();
        File srcFile = new File(
                ProductConstant.getSystemResourceLocation() + ".." + File.separator + ".."
                        + File.separator + ".." + File.separator + ".." + File.separator
                        + "mock-web-service" + File.separator + "target" + File.separator
                        + "MockAdminService.aar");
        File targetDir = new File(
                System.getProperty(ServerConstants.CARBON_HOME) + File.separator + "repository"
                        + File.separator + "deployment" + File.separator + "server" + File.separator
                        + "axis2services");
        FileUtils.copyFileToDirectory(srcFile, targetDir);
        waitForHttpGet(appServerServiceUrl + "WebappAdmin?wsdl", 200, 30000L);
    }

    /**
     * Tests the discovery application listing
     * @throws Exception
     */
    @Test(groups = { "wso2.appmanager.discovery" }, description = "Tests Discovery app listing",
            sequential = true, priority = 4)
    public void testDiscoverListing() throws Exception {
        performInitialDiscovery();

        String status = webDriverForPublisher.findElement(By.className("info-div"))
                .findElement(By.tagName("span")).getText();
        assertContains(status, INFO_SUCCESS_DISCOVERY_START_WITH,
                "The status in info box should be \"" + INFO_SUCCESS_DISCOVERY_START_WITH + "\"");
    }

    /**
     * Tests the discovery application listing, with the app server URL not ending with "\"
     * @throws Exception
     */
    @Test(groups = { "wso2.appmanager.discovery" }, description = "Tests Discovery app listing",
            sequential = true, priority = 4)
    public void testDiscoverListing_AsUrlNotEndingWithSlash() throws Exception {
        //Remove the trailing "/" from the URL
        appServerServiceUrl = appServerServiceUrl.substring(0, appServerServiceUrl.length() - 1);
        performInitialDiscovery();

        String status = webDriverForPublisher.findElement(By.className("info-div"))
                .findElement(By.tagName("span")).getText();
        assertContains(status, INFO_SUCCESS_DISCOVERY_START_WITH,
                "The status in info box should be \"" + INFO_SUCCESS_DISCOVERY_START_WITH + "\"");
    }

    /**
     * Tests the pagination on the app discovery listing
     * @throws Exception
     */
    @Test(groups = {
            "wso2.appmanager.discovery" }, description = "Tests pagination on Discovery app listing",
            sequential = true, priority = 4)
    public void testPaging() throws Exception {
        performInitialDiscovery();
        webDriverForPublisher.get(getDiscoverWebappUrl() + "?page=2");
        String status = webDriverForPublisher.findElement(By.className("info-div"))
                .findElement(By.tagName("span")).getText();
        assertContains(status, INFO_SUCCESS_DISCOVERY_START_WITH,
                "The status in info box should be \"" + INFO_SUCCESS_DISCOVERY_START_WITH + "\"");
        webDriverForPublisher.get(getDiscoverWebappUrl() + "?page=1");
        status = webDriverForPublisher.findElement(By.className("info-div"))
                .findElement(By.tagName("span")).getText();
        assertContains(status, INFO_SUCCESS_DISCOVERY_START_WITH,
                "The status in info box should be \"" + INFO_SUCCESS_DISCOVERY_START_WITH + "\"");
    }

    /**
     * Tests the pagination on the app discovery listing
     * @throws Exception
     */
    @Test(groups = {
            "wso2.appmanager.discovery" }, description = "Tests creation of discovered app",
            sequential = true, priority = 7)
    public void testAppCreate() throws Exception {
        performInitialDiscovery();
        //Find the button(s) named
        webDriverForPublisher.findElement(By.xpath("(//input[@name='createAsset'])[10]")).click();
        WebDriverWait wait = new WebDriverWait(webDriverForPublisher, 30);

        wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.id("discover-create-asset-status")));

        String status = webDriverForPublisher.findElement(By.id("statusText")).getText();
        assertContains(status, "is successfully created with proxy context",
                "The proxy application created message should appear");

        //TODO: Check with the list. with loop timeout
    }

    /**
     * Performs the initial discovery with the UI.
     *
     */
    private void performInitialDiscovery() {
        appmPublisherUIClient.login(webDriverForPublisher, appMServerUrl, username, password);
        // find element username
        webDriverForPublisher.get(getDiscoverWebappUrl());
        WebElement usernameEle = webDriverForPublisher.findElement(By.id("server-url"));
        usernameEle.sendKeys(appServerServiceUrl);
        webDriverForPublisher.findElement(By.id("server-username")).sendKeys("admin");
        webDriverForPublisher.findElement(By.id("server-password")).sendKeys("admin");
        webDriverForPublisher.findElement(By.id("btn-discover-asset")).sendKeys("admin");

        webDriverForPublisher.findElement(By.id("btn-discover-asset")).click();
        webDriverForPublisher.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterMethod(groups = { "wso2.appmanager.discovery" }, alwaysRun = true)
    public void destroy() throws Exception {
        webDriverForPublisher.quit();
    }

    protected String getPublisherUrl() {
        return appMServerUrl + "/publisher";
    }

    protected String getDiscoverWebappUrl() {
        return getPublisherUrl() + "/assets/discover/webapp/";
    }

    /**
     * Waits for the HTTP status to return from the given URL
     * @param urlStr
     * @param httpResponseCode
     * @param timeout
     * @throws Exception
     */
    private void waitForHttpGet(String urlStr, int httpResponseCode, long timeout)
            throws Exception {

        long start = System.currentTimeMillis();
        Exception lastException = null;
        do {
            HttpGet httpGet = new HttpGet(urlStr);
            HttpClient client = new DefaultHttpClient();
            try {
                HttpResponse response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == httpResponseCode) {
                    return;
                }
            } catch (ClientProtocolException e) {
                lastException = e;
                //Ignore
            } catch (IOException e) {
                lastException = e;
                //Ignore
            }

            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                //Ignore
            }
        } while (start + timeout > System.currentTimeMillis());

        if (lastException != null) {
            throw new IOException("Could not connect to URL:" + urlStr, lastException);
        }
        //fail("Could not verify that the [MockWebappAdminService] is deployed. It may not have been copied to test-pack properly.");
    }
}
