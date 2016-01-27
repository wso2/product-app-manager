/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.wso2.appmanager.ui.integration.test.utils.Page;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import java.io.IOException;


public class PublisherOverviewWebAppPage extends Page {
    private static final Log log = LogFactory.getLog(PublisherWebAppsListPage.class);

    private static PublisherOverviewWebAppPage page;
    public static final String PAGE = "/publisher/asset/webapp/";

    public static PublisherOverviewWebAppPage getPage(WebDriver driver, AutomationContext appMServer)
            throws IOException {
        if (page == null || page.driver != driver) {
            page = new PublisherOverviewWebAppPage(driver, appMServer);
        }
        return page;
    }

    private PublisherOverviewWebAppPage(WebDriver driver, AutomationContext appMServer) throws IOException {
        this.driver = driver;
        this.appMServer = appMServer;
        //check that we are on the correct page
        if (!(driver.getCurrentUrl().contains(PAGE))) {
            throw new IllegalStateException("This is not " + this.getClass().getSimpleName());
        }
    }

    public boolean isEditLinkAvailable() {
        String id = "edit-link";
        boolean present;
        try {
            driver.findElement(By.id(id));
            present = true;
        } catch (NoSuchElementException e) {
            present = false;
        }
        return present;
    }

    public LoginPage logout() throws Exception {
        driver.findElement(By.className("icon-user")).click();
        driver.findElement(By.className("icon-signout")).click();
        return LoginPage.getPage(driver, appMServer, LoginPage.LoginTo.PUBLISHER);
    }
}
