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

package org.wso2.appmanager.ui.integration.test.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.extensions.selenium.BrowserManager;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;


public class AppManagerIntegrationTest {

    protected static final String TEST_GROUP = "wso2.appm";
    protected AutomationContext appMServer;
    protected WebDriver driver = null;
    protected LoginPage loginPage;

    protected void init() throws Exception {
        init(TestUserMode.SUPER_TENANT_ADMIN);
        driver = BrowserManager.getWebDriver();
    }

    protected void init(TestUserMode testUserMode) throws Exception {
        appMServer = new AutomationContext("App Manager", testUserMode);
    }

    protected void closeDriver(WebDriver driver){
        if(driver != null){
            driver.close();
            driver.quit();
        }
    }

    protected Page login(WebDriver driver, LoginPage.LoginTo loginTo) throws IOException, XPathExpressionException,
                        InterruptedException {
        loginPage = LoginPage.getPage(driver, appMServer, loginTo);
        return loginPage.login(appMServer.getSuperTenant().getTenantAdmin().getUserName(),
                appMServer.getSuperTenant().getTenantAdmin().getPassword(), loginTo);
    }


    protected Page login(WebDriver driver, LoginPage.LoginTo loginTo,String userName,String password) throws IOException, XPathExpressionException,
            InterruptedException {
        loginPage = LoginPage.getPage(driver, appMServer, loginTo);
        return loginPage.login(userName, password, loginTo);
    }

    protected boolean isElementExist(WebDriver driver,By by){
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

}
