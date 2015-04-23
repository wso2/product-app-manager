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

package org.wso2.carbon.appmanager.integration.ui.Util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class APPMStoreUIClient {

    private UIElementMapper uiElementMapper;
    private WebDriver oldWebDriver;

    public APPMStoreUIClient() {
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
    public void login(WebDriver driver, String backEndUrl,
                      String username, String password) {

        driver.get(backEndUrl + uiElementMapper.getElement("store_url"));

        WebDriverWait wait = new WebDriverWait(driver, 30);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.linkText(uiElementMapper.getElement("store_sign_in_link_text_locator"))));
        driver.findElement(By.linkText(uiElementMapper.getElement("store_sign_in_link_text_locator"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(
                uiElementMapper.getElement("store_username_id_locator"))));
        WebElement usernameEle = driver.findElement(By.id(uiElementMapper.getElement("store_username_id_locator")));

        usernameEle.sendKeys(username);
        // find element password
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_password_id_locator"))));
        WebElement passwordEle = driver.findElement(By.id(uiElementMapper.getElement("store_password_id_locator")));
        // fill element
        passwordEle.sendKeys(password);
        // find submit button and click on it.
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.findElement(By.className(uiElementMapper.getElement("store_button_class_name_locator"))).click();

    }

    public WebDriver loginDriver(WebDriver driver, String backEndUrl,
                      String username, String password) {

        driver.get(backEndUrl + uiElementMapper.getElement("store_url"));

        WebDriverWait wait = new WebDriverWait(driver, 30);

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.linkText(uiElementMapper.getElement("store_sign_in_link_text_locator"))));
        driver.findElement(By.linkText(uiElementMapper.getElement("store_sign_in_link_text_locator"))).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(
                uiElementMapper.getElement("store_username_id_locator"))));
        WebElement usernameEle = driver.findElement(By.id(uiElementMapper.getElement("store_username_id_locator")));

        usernameEle.sendKeys(username);
        // find element password
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_password_id_locator"))));
        WebElement passwordEle = driver.findElement(By.id(uiElementMapper.getElement("store_password_id_locator")));
        // fill element
        passwordEle.sendKeys(password);
        // find submit button and click on it.
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        driver.findElement(By.className(uiElementMapper.getElement("store_button_class_name_locator"))).click();

        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }

        return driver;

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
        driver.get(backEndUrl + uiElementMapper.getElement("store_url"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(uiElementMapper.getElement("store_dropdown_xpath_locator"))));
        driver.findElement(By.xpath(uiElementMapper.getElement("store_dropdown_xpath_locator"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath(uiElementMapper.getElement("store_sign_out_xpath_locator"))));
        driver.findElement(By.xpath(uiElementMapper.getElement("store_sign_out_xpath_locator"))).click();

        return driver.getCurrentUrl();

    }

    /**
     * access the store without login
     *
     * @param driver     selenium WebDriver.
     * @param backEndUrl store host
     */
    public void accessStore(WebDriver driver, String backEndUrl) {
        driver.get(backEndUrl + uiElementMapper.getElement("store_url"));
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    public WebDriver setOldDriver(WebDriver driver){
        this.oldWebDriver = driver;
        return this.oldWebDriver;
    }

    /**
     * Go to the application hosted in the app manager. this method replicates a user selecting
     * an subscribed application on the main store page and then selecting the url provided to
     * access that hosted application.
     *
     * @param appId  application id
     */

    public void selectApplication(WebDriver driver, String appId) {
        // select the application

        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-id='"
                + appId
                + "']/div[@class='asset-details']/div[@class='asset-name']/a")));
        driver.findElement(
                By.xpath("//div[@data-id='"
                        + appId
                        + "']/div[@class='asset-details']/div[@class='asset-name']/a"))
                .click();

        //select the link
        driver.findElement(By.xpath(uiElementMapper.getElement("store_app_gateway_url_xpath_locator"))).click();
    }

    public void selectAppGadget(WebDriver driver, String appId){
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-id='"
                + appId
                + "']/div[@class='asset-details']/div[@class='asset-name']/a")));
        driver.findElement(
                By.xpath("//div[@data-id='"
                        + appId
                        + "']/div[@class='asset-details']/div[@class='asset-name']/a"))
                .click();
        driver.close();
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    public void selfSignUp(WebDriver driver, String backEndUrl, String username, String password)  {

        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.get(backEndUrl + uiElementMapper.getElement("store_url"));
        WebDriverWait wait = new WebDriverWait(driver, 30);

        wait.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath(uiElementMapper.getElement("store_reg_btn_xpath_locator"))));
        driver.findElement(
                By.xpath(uiElementMapper.getElement("store_reg_btn_xpath_locator"))).click();

        //fill credentisals
        WebDriverWait waitElem = new WebDriverWait(driver, 30);

        waitElem.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_reg_username_id_locator"))));
        WebElement usernameEle = driver.findElement(By.id(
                uiElementMapper.getElement("store_reg_username_id_locator")));
        usernameEle.sendKeys(username);

        waitElem.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_reg_username_id_locator"))));
        WebElement usernameEle1 = driver.findElement(By.id(
                uiElementMapper.getElement("store_reg_username_id_locator")));
        usernameEle1.sendKeys(username);

        waitElem.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_reg_password_id_locator"))));
        WebElement passwordEle = driver.findElement(By.id(
                uiElementMapper.getElement("store_reg_password_id_locator")));
        passwordEle.sendKeys(password);

        waitElem.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_reg_confirm_pawd_id_locator"))));
        WebElement confirmPasswordEle = driver.findElement(
                By.id(uiElementMapper.getElement("store_reg_confirm_pawd_id_locator")));
        confirmPasswordEle.sendKeys(password);

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
        waitElem.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath(uiElementMapper.getElement("store_reg_submit_xpath_locator"))));
        driver.findElement(By.xpath(uiElementMapper.getElement("store_reg_submit_xpath_locator"))).click();

        waitElem.until(ExpectedConditions.visibilityOfElementLocated
                (By.xpath(uiElementMapper.getElement("store_reg_confirm_xpath_locator"))));
        driver.findElement(By.xpath(uiElementMapper.getElement("store_reg_confirm_xpath_locator"))).click();

        waitElem.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_username_id_locator"))));
        WebElement sinInUser = driver.findElement(By.id(
                uiElementMapper.getElement("store_username_id_locator")));
        sinInUser.sendKeys(username);

        waitElem.until(ExpectedConditions.visibilityOfElementLocated(
                By.id(uiElementMapper.getElement("store_password_id_locator"))));
        WebElement sinInPassword = driver.findElement(By.id(
                uiElementMapper.getElement("store_password_id_locator")));
        sinInPassword.sendKeys(password);

        waitElem.until(ExpectedConditions.visibilityOfElementLocated
                (By.className(uiElementMapper.getElement("store_button_class_name_locator"))));
        driver.findElement(By.className(uiElementMapper.getElement("store_button_class_name_locator"))).click();
    }
}
