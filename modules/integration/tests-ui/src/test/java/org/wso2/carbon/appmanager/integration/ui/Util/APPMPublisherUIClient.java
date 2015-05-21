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

package org.wso2.carbon.appmanager.integration.ui.Util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

/**
 * Reusable UI client for APPM Publisher
 */
public class APPMPublisherUIClient {

    private UIElementMapper uiElementMapper;
    private WebDriver oldWebDriver;

    public APPMPublisherUIClient() {
        this.uiElementMapper = new UIElementMapper();
    }

    /**
     * Log in to the APP_M store
     *
     * @param driver     selenium WebDriver.
     * @param backEndUrl store host
     * @param username   username
     * @param password   password
     */
    public void login(WebDriver driver, String backEndUrl, String username, String password) {

        driver.get(backEndUrl + uiElementMapper.getElement("publisher_url"));

        WebDriverWait wait = new WebDriverWait(driver, 30);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                new ByAll(By.className("btn-primary"), By.tagName("input"))));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("publisher_username_id_locator"))));
        WebElement usernameEle = driver
                .findElement(By.id(uiElementMapper.getElement("publisher_username_id_locator")));

        usernameEle.sendKeys(username);
        // find element password
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("publisher_password_id_locator"))));
        WebElement passwordEle = driver
                .findElement(By.id(uiElementMapper.getElement("publisher_password_id_locator")));
        // fill element
        passwordEle.sendKeys(password);
        // find submit button and click on it.
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.findElement(new ByAll(By.className("btn-primary"), By.tagName("input"))).click();

    }

    /**
     * Logout from the APP_M store
     *
     * @param driver     selenium WebDriver.
     * @param backEndUrl store host
     */
    public String logout(WebDriver driver, String backEndUrl) {

        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.get(backEndUrl + uiElementMapper.getElement("pubisher_url"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(uiElementMapper.getElement("store_dropdown_xpath_locator"))));
        driver.findElement(By.xpath(uiElementMapper.getElement("store_dropdown_xpath_locator")))
                .click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(uiElementMapper.getElement("store_sign_out_xpath_locator"))));
        driver.findElement(By.xpath(uiElementMapper.getElement("store_sign_out_xpath_locator")))
                .click();

        return driver.getCurrentUrl();

    }
}
