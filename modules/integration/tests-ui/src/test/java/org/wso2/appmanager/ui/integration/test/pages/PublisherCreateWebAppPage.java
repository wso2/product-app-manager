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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.test.utils.Page;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PublisherCreateWebAppPage extends Page {

    private static final Log log = LogFactory.getLog(PublisherCreateWebAppPage.class);
    private static PublisherCreateWebAppPage page;
    public static final String PAGE = "/publisher/asset/webapp";

    public static PublisherCreateWebAppPage getPage(WebDriver driver, AutomationContext appMServer)
            throws IOException {
        if (page == null || page.driver != driver) {
            page = new PublisherCreateWebAppPage(driver, appMServer);
        }
        return page;
    }

    private PublisherCreateWebAppPage(WebDriver driver, AutomationContext appMServer)
            throws IOException {
        this.driver = driver;
        this.appMServer = appMServer;
        //check that we are on the correct page
        if (!(driver.getCurrentUrl().contains(PAGE))) {
            throw new IllegalStateException("This is not " + this.getClass().getSimpleName());
        }
    }

    public PublisherWebAppsListPage createWebApp(WebApp webapp) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 90);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_name")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_displayName")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_context")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_version")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_webAppUrl")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("optradio")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-create-asset")));

        driver.findElement(By.id("overview_name")).sendKeys(webapp.getName());
        driver.findElement(By.id("overview_displayName")).sendKeys(webapp.getDisplayName());
        driver.findElement(By.id("overview_context")).sendKeys(webapp.getContext());
        driver.findElement(By.id("overview_version")).sendKeys(webapp.getVersion());
        driver.findElement(By.id("overview_webAppUrl")).sendKeys(webapp.getWebAppUrl());
        if (webapp.getTransport() != null) {
            driver.findElement(By.name("optradio")).click();
        }


        submitApp();
        return PublisherWebAppsListPage.getPage(driver, appMServer);
    }

    public void AddAnonymousPolicyGroup() throws Exception {
        driver.findElement(By.id("btn-add-policy-group")).click();

        driver.findElement(By.id("policyGroupName")).sendKeys("anonymous_policy_group");

        Select dropdownThrottlingTier = new Select(driver.findElement(By.id("throttlingTier")));
        dropdownThrottlingTier.selectByValue("Bronze");

        Select dropdownIsAnonymous = new Select(driver.findElement(By.id(
                "anonymousAccessToUrlPattern")));
        dropdownIsAnonymous.selectByValue("true");


        Select dropdownEntPolicy = new Select(driver.findElement(By.id("xacml-rule")));
        dropdownEntPolicy.selectByValue("-1");

        driver.findElement(By.id("btn-policy-group-save-and-close")).click();
    }

    public PublisherWebAppsListPage createAnonymousWebAppUsingResources(WebApp webapp) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 90);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_name")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_displayName")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_context")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_version")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_webAppUrl")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("optradio")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-create-asset")));

        driver.findElement(By.id("policies_section")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-policy-group")));

        AddAnonymousPolicyGroup();
        driver.findElement(By.id("policies_section")).click();

        driver.findElement(By.id("overview_name")).sendKeys(webapp.getName());
        driver.findElement(By.id("overview_displayName")).sendKeys(webapp.getDisplayName());
        driver.findElement(By.id("overview_context")).sendKeys(webapp.getContext());
        driver.findElement(By.id("overview_version")).sendKeys(webapp.getVersion());
        driver.findElement(By.id("overview_webAppUrl")).sendKeys(webapp.getWebAppUrl());
        if (webapp.getTransport() != null) {
            driver.findElement(By.name("optradio")).click();
        }

        driver.findElement(By.id("webAppResources_section")).click();

        Select dropdownPolicyGroup;
        for (int i = 0; i <= 4; i++) {
            dropdownPolicyGroup = new Select(driver.findElement(By.id(
                    "uritemplate_policyGroupId" + i)));
            dropdownPolicyGroup.selectByVisibleText("anonymous_policy_group");
        }

        submitApp();

        return PublisherWebAppsListPage.getPage(driver, appMServer);
    }


    public PublisherWebAppsListPage createAnonymousWebAppUsingFlag(WebApp webapp) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 90);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_name")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_displayName")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_context")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_version")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_webAppUrl")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("optradio")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-create-asset")));

        driver.findElement(By.id("overview_name")).sendKeys(webapp.getName());
        driver.findElement(By.id("overview_displayName")).sendKeys(webapp.getDisplayName());
        driver.findElement(By.id("overview_context")).sendKeys(webapp.getContext());
        driver.findElement(By.id("overview_version")).sendKeys(webapp.getVersion());
        driver.findElement(By.id("overview_webAppUrl")).sendKeys(webapp.getWebAppUrl());
        if (webapp.getTransport() != null) {
            driver.findElement(By.name("optradio")).click();
        }

        driver.findElement(By.id("policies_section")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("global_policies_section")));
        driver.findElement(By.id("global_policies_section")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("anonymous_checkbox")));
        driver.findElement(By.id("anonymous_checkbox")).click();


        submitApp();

        return PublisherWebAppsListPage.getPage(driver, appMServer);
    }


    public PublisherWebAppsListPage createNonAnonymousAppWithAnonymousResources(WebApp webapp,
                                                                                List<String>
                                                                                        resources)
            throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 90);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_name")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_displayName")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_context")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_version")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_webAppUrl")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("optradio")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-create-asset")));

        driver.findElement(By.id("policies_section")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-add-policy-group")));

        AddAnonymousPolicyGroup();
        driver.findElement(By.id("policies_section")).click();

        driver.findElement(By.id("overview_name")).sendKeys(webapp.getName());
        driver.findElement(By.id("overview_displayName")).sendKeys(webapp.getDisplayName());
        driver.findElement(By.id("overview_context")).sendKeys(webapp.getContext());
        driver.findElement(By.id("overview_version")).sendKeys(webapp.getVersion());
        driver.findElement(By.id("overview_webAppUrl")).sendKeys(webapp.getWebAppUrl());
        if (webapp.getTransport() != null) {
            driver.findElement(By.name("optradio")).click();
        }

        driver.findElement(By.id("webAppResources_section")).click();

        //Add resources
        for (int i = 0; i < resources.size(); i++) {
            driver.findElement(By.id("url_pattern")).sendKeys(resources.get(i));
            driver.findElement(By.cssSelector("[value='GET'][class='http_verb'][type='checkbox']"))
                    .click();
            driver.findElement(By.id("add_resource")).click();
        }


        //make last item anonymous allowed
        Select dropdownPolicyGroup;
        dropdownPolicyGroup = new Select(driver.findElement(By.id("uritemplate_policyGroupId6")));
        dropdownPolicyGroup.selectByVisibleText("anonymous_policy_group");

        submitApp();

        return PublisherWebAppsListPage.getPage(driver, appMServer);
    }

    public PublisherCreateWebAppPage resetAppData(WebApp webapp) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, 90);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_name")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_displayName")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_context")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_version")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("overview_webAppUrl")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("optradio")));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btn-create-asset")));

        driver.findElement(By.id("overview_name")).sendKeys(webapp.getName());
        driver.findElement(By.id("overview_displayName")).sendKeys(webapp.getDisplayName());
        driver.findElement(By.id("overview_context")).sendKeys(webapp.getContext());
        driver.findElement(By.id("overview_version")).sendKeys(webapp.getVersion());
        driver.findElement(By.id("overview_webAppUrl")).sendKeys(webapp.getWebAppUrl());

        if (webapp.getTransport() != null) {
            driver.findElement(By.name("optradio")).click();
        }


        driver.findElement(By.className("btn-reset")).click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

        return PublisherCreateWebAppPage.getPage(driver, appMServer);

    }


    public void submitApp() {
        driver.findElement(By.id("btn-create-asset")).click();
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }


}
