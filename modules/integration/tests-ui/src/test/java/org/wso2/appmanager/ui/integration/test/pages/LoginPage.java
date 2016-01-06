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

package org.wso2.appmanager.ui.integration.test.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.appmanager.ui.integration.test.utils.Page;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class LoginPage extends Page {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private static LoginPage page;
    private static final String PUBLISHER_LOGIN_URI = "/publisher/login";
    private static final String STORE_LOGIN_URI = "/store/login";

    public enum LoginTo{
        PUBLISHER, STORE
    }

    public static LoginPage getPage(WebDriver driver, AutomationContext appMServer, LoginTo loginTo) throws
            IOException, XPathExpressionException {
        if(loginTo == LoginTo.PUBLISHER){
            driver.get(appMServer.getContextUrls().getWebAppURLHttps() + PUBLISHER_LOGIN_URI);
        }else{
            driver.get(appMServer.getContextUrls().getWebAppURLHttps() + STORE_LOGIN_URI);
        }

        if(page == null || page.driver != driver){
            page = new LoginPage(driver, appMServer) ;
        }
        return page;
    }

    private LoginPage(WebDriver driver,  AutomationContext appMServer) throws IOException {
        this.driver = driver;
        this.appMServer= appMServer;
        //check that we are on the correct page
        if (!(driver.getCurrentUrl().contains("/login.do"))) {
            throw new IllegalStateException("This is not " + this.getClass().getSimpleName());
        }
    }

    public Page login(String userName, String password, LoginTo loginTo) throws IOException, InterruptedException {
        log.info("login as " + userName);
        // Wait until login page loading is completed.
        WebDriverWait wait = new WebDriverWait(driver, 90);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                        new ByAll(By.className("btn-primary"), By.tagName("input"))));

        driver.findElement(By.id("username")).sendKeys(userName);
        driver.findElement(By.id("password")).sendKeys(password);

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        driver.findElement(By.className("btn-primary")).click();

        if(loginTo == LoginTo.PUBLISHER){
            return  PublisherWebAppsListPage.getPage(driver, appMServer);
        }else{
            return  StoreHomePage.getPage(driver, appMServer);
        }
    }
}

