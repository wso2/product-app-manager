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
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.utils.Page;

import java.io.IOException;


public class PublisherCreateWebAppPage extends Page {

    private static final Log log = LogFactory.getLog(PublisherWebAppsListPage.class);
    private WebDriver driver;
    private boolean isCloudEnvironment = false;
    private static PublisherCreateWebAppPage page;
    public static final String PAGE = "/publisher/asset/webapp";

    public static PublisherCreateWebAppPage getPage(WebDriver driver) throws IOException{
       if(page == null || page.driver != driver){
           page = new PublisherCreateWebAppPage(driver) ;
       }
        return page;
    }

    private PublisherCreateWebAppPage(WebDriver driver) throws IOException {
        this.driver = driver;

        //check that we are on the correct page
        if (!(driver.getCurrentUrl().contains(PAGE))) {
            throw new IllegalStateException("This is not " + this.getClass().getSimpleName());
        }
    }

    private PublisherCreateWebAppPage(WebDriver driver, boolean isCloudEnvironment) throws IOException {
        this.driver = driver;
        this.isCloudEnvironment = isCloudEnvironment;
    }



    public PublisherWebAppsListPage createWebApp(WebApp webapp) throws Exception{
        driver.findElement(By.id("overview_name")).sendKeys(webapp.getName());
        driver.findElement(By.id("overview_displayName")).sendKeys(webapp.getDisplayName());
        driver.findElement(By.id("overview_context")).sendKeys(webapp.getContext());
        driver.findElement(By.id("overview_version")).sendKeys(webapp.getVersion());
        driver.findElement(By.id("overview_webAppUrl")).sendKeys(webapp.getWebAppUrl());
        if(webapp.getTransport() != null){
            driver.findElement(By.name("optradio")).click();
        }
        driver.findElement(By.id("btn-create-asset")).click();
        return  PublisherWebAppsListPage.getPage(driver);
    }
}
