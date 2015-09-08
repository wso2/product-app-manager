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

package org.wso2.appmanager.ui.integration.test.publisher.pages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;

public class LoginPage {

    private static final Log log = LogFactory.getLog(LoginPage.class);
    private WebDriver driver;
    private boolean isCloudEnvironment = false;


    public LoginPage(WebDriver driver) throws IOException {
        this.driver = driver;

        //check that we are on the correct page
        if (!(driver.getCurrentUrl().contains("/login.do"))) {
            throw new IllegalStateException("This is not the login page");
        }
    }

    public LoginPage(WebDriver driver, boolean isCloudEnvironment) throws IOException {
        this.driver = driver;
        this.isCloudEnvironment = isCloudEnvironment;
    }


    public WebAppsListPage loginAs(String userName, String password) throws IOException, InterruptedException {
        log.info("login as " + userName);
        WebElement userNameField = driver.findElement(By.id("username"));
        WebElement passwordField = driver.findElement(By.id("password"));

        userNameField.sendKeys(userName);
        passwordField.sendKeys(password);

        driver.findElement(By.className("btn")).click();
        Thread.sleep(12000);
        return new WebAppsListPage(driver);
    }

}

