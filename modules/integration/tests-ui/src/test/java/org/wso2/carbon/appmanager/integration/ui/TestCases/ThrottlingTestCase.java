/*
 * Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.apache.catalina.startup.Tomcat;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.*;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertTrue;

public class ThrottlingTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private WebDriver driverOne, driverTwo;
    private Map<String, String> requestHeaders = new HashMap<String, String>();
    private APPMStoreUIClient storeUIClient;
    Tomcat tomcat;
    private ApplicationInitializingUtil baseUtil;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("8");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        driverOne = BrowserManager.getWebDriver();
        if (requestHeaders.get("Content-Type") == null) {
            this.requestHeaders.put("Content-Type", "text/html");
        }
        storeUIClient = new APPMStoreUIClient();
    }

    /*
     * Application creation done with 'Bronze' Throttling for all resources
     * So that application is only invokable for 1 times per minute.
     */

    @Test(groups = {"wso2.appmanager.throttling"}, description = "Throttling Test Case")
    public void testThrottlingTier() throws Exception {

        String webAppUrl = appProp.getPath();
        String root = ProductConstant.getSystemResourceLocation();
        String webAppPath = root + "samples" + File.separator + "sample.war";

        TomcatDeployer deployer = new TomcatDeployer();
        tomcat = deployer.getTomcat();
        deployer.startTomcat(tomcat, webAppUrl, webAppPath);

        LogViewerClient logClient = new LogViewerClient(amServer.getBackEndUrl(), username, password);

        storeUIClient.loginDriver(driverOne, ApplicationInitializingUtil.storeURLHttp, username, password);
        storeUIClient.selectApplication(driverOne, ApplicationInitializingUtil.appId);
        driverOne.switchTo().alert().accept();
        driverTwo = BrowserManager.getWebDriver();
        storeUIClient.loginDriver(driverTwo, ApplicationInitializingUtil.storeURLHttp, username, password);
        storeUIClient.selectApplication(driverTwo, ApplicationInitializingUtil.appId);
        driverTwo.switchTo().alert().accept();

        Thread.sleep(10000);

        LogEvent[] events = logClient.getAllSystemLogs();

        boolean assertValue = false;
        for (LogEvent event : events) {
            if (event.getMessage().contains("You cannot access this service since you have exceeded the allocated quota")) {
                assertValue = true;
                break;
            }
        }
        assertTrue(assertValue,
                "Message 'You cannot access this service since you have exceeded the allocated quota' not found");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        this.cleanWebDriver(driverOne);
        this.cleanWebDriver(driverTwo);
        baseUtil.destroy();
        tomcat.stop();
    }

    private void cleanWebDriver(WebDriver driver) {
        String parentWindow = driver.getWindowHandle();
        Set<String> handles =  driver.getWindowHandles();
        for(String windowHandle  : handles) {
            if(!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }
        driver.switchTo().window(parentWindow);
        driver.close();
    }
}
