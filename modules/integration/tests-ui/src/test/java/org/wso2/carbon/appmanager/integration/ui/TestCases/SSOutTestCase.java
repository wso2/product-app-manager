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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.appmanager.integration.ui.Util.UIElementMapper;
import org.wso2.carbon.automation.core.BrowserManager;

import java.util.Set;

import static org.testng.Assert.assertTrue;

public class SSOutTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private WebDriver driver;
    private APPMStoreRestClient appMStore;
    private  APPMStoreUIClient storeUIClient;
    private UIElementMapper uiElementMapper;
    private ApplicationInitializingUtil baseUtil;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("12");
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        driver = BrowserManager.getWebDriver();
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.
                APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
        storeUIClient = new APPMStoreUIClient();
        uiElementMapper = new UIElementMapper();
    }

    @Test(groups = {"wso2.appmanager.sso"}, description = "Test Single Sign Out")
    public void testSSOut() throws Exception {

        storeUIClient.login(driver, ApplicationInitializingUtil.storeURLHttp, username, password);
        String sessionBefore = appMStore.session;
        storeUIClient.logout(driver, ApplicationInitializingUtil.storeURLHttp);
        appMStore.login(username, password);
        String session = appMStore.session;
        storeUIClient.accessStore(driver, ApplicationInitializingUtil.storeURLHttp);
        if(sessionBefore==null){
            sessionBefore="";
        }
        assertTrue(driver.findElement(By.xpath(uiElementMapper.getElement("store_sign_in_xpath_locator"))) != null &&
                !sessionBefore.equals(session), "User is still logged in. Single Single Out failed");
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
    }
}
