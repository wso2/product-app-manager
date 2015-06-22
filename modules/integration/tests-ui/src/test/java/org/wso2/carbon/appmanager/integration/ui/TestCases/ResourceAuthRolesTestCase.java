/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.apache.catalina.startup.Tomcat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.*;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;

import java.io.File;
import java.util.Set;

import static org.testng.Assert.assertTrue;

public class ResourceAuthRolesTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;
    private UIElementMapper uiElementMapper;
    TomcatDeployer deployer;
    Tomcat tomcat;
    private ApplicationInitializingUtil baseUtil;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("4");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        driver = BrowserManager.getWebDriver();
        storeUIClient = new APPMStoreUIClient();
        uiElementMapper = new UIElementMapper();
    }

    @Test(groups = {"wso2.appmanager.roles"}, description = "Resource level auth by roles test")
    public void testResourceAuthRoles() throws Exception {

        String webAppUrl = appProp.getPath();
        String root = ProductConstant.getSystemResourceLocation();
        String webAppPath = root + "samples" + File.separator + "sample.war";

        deployer = new TomcatDeployer();
        tomcat = deployer.getTomcat();
        deployer.startTomcat(tomcat, webAppUrl, webAppPath);

        storeUIClient.login(driver, ApplicationInitializingUtil.storeURLHttp, username, password);

        storeUIClient.selectApplication(driver, ApplicationInitializingUtil.appId);
        driver.switchTo().alert().accept();
        assertTrue(driver.findElements(By.xpath(uiElementMapper.getElement("tomcat_resource_locator"))).size() == 0,
                "Resource is available.");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
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
        baseUtil.destroy();
        tomcat.stop();
    }

}
