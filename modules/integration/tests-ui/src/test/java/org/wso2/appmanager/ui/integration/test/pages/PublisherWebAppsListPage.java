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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.wso2.appmanager.ui.integration.test.utils.Page;
import org.wso2.carbon.automation.engine.context.AutomationContext;

import java.io.IOException;


public class PublisherWebAppsListPage  extends Page {

    private static final Log log = LogFactory.getLog(PublisherWebAppsListPage.class);

    private static PublisherWebAppsListPage page;
    public static final String PAGE = "/publisher/assets/webapp/";

    public static PublisherWebAppsListPage getPage(WebDriver driver,  AutomationContext appMServer) throws IOException{
        if(page == null || page.driver != driver){
            page = new PublisherWebAppsListPage(driver, appMServer) ;
        }
        return page;
    }

    private PublisherWebAppsListPage(WebDriver driver,  AutomationContext appMServer) throws IOException {
        this.driver = driver;
        this.appMServer = appMServer;
        //check that we are on the correct page
        if (!(driver.getCurrentUrl().contains(PAGE))) {
            throw new IllegalStateException("This is not " + this.getClass().getSimpleName());
        }
    }

    public PublisherCreateWebAppPage gotoCreateWebAppPage() throws Exception{
        driver.get(appMServer.getContextUrls().getWebAppURLHttps() + PublisherCreateWebAppPage.PAGE);
        return PublisherCreateWebAppPage.getPage(driver, appMServer);
    }

    /**
     * Select and open the web app from web app list.
     * @param appName App Name
     * @param provider Provider
     * @param version Version
     * @return overview page
     * @throws IOException
     */
    public PublisherOverviewWebAppPage goToOverviewPage(String appName,String provider,String version)
            throws IOException{
        String id = appName+"-"+provider+"-"+version;
        driver.findElement(By.id(id)).click();
        return PublisherOverviewWebAppPage.getPage(driver,appMServer);
    }

    /**
     * Change the lifecylce state of given web app from current state to
     * given state.
     * @param webAppName App Name
     * @param provider Provider
     * @param version Version
     * @param state Lifecycle state
     * @param driver Web driver
     */
    public void changeLifeCycleState(String webAppName,String provider,String version, String state,WebDriver driver) {
        WebDriverWait  wait = new WebDriverWait(driver, 120);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + webAppName +"'][data-action='" + state + "']" +
                        "[data-provider='" + provider +"'][data-version='" + version + "']")));
        driver.findElement(By.cssSelector(
                "[data-name='" + webAppName +"'][data-action='" + state + "']" +
                        "[data-provider='" + provider +"'][data-version='" + version + "']"))
                .click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-dismiss='modal']")));
        driver.findElement(By.cssSelector("[data-dismiss='modal']")).click();
        driver.navigate().refresh();
    }

}
