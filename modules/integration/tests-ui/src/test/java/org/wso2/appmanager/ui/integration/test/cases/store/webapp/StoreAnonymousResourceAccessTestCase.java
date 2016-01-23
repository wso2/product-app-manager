package org.wso2.appmanager.ui.integration.test.cases.store.webapp;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherCreateWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.test.pages.StoreHomePage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;

import java.util.ArrayList;
import java.util.List;

public class StoreAnonymousResourceAccessTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Verify Anonymous resource access";
    private static final String TEST_NON_ANONYMOUS_APP_NAME = "Test_non_anonymous_app";

    private static final Log log = LogFactory.getLog(StoreAnonymousResourceAccessTestCase.class);

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    private String non_anonymous_app_id;
    WebDriverWait wait;

    List<String> resources;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        resources = new ArrayList<String>();
        resources.add(0, "intl/en/policies/terms/regional.html"); //anonymous allowed
        resources.add(1, "intl/en/policies/"); //anonymous not allowed

        wait = new WebDriverWait(driver, 120);

        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();

        //create anonymous and non anonymous apps
        createApps();

        //submit,approve and publish created apps
        manageLifeCycle();

    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testAnonymousApplicationAccess() throws Exception {
        //Access anonymous disallowed web app resource using an anonymous user
        accessApps(true, resources.get(1));

        //Access anonymous allowed web app resource using an anonymous user
        accessApps(false, resources.get(0));
    }


    private void createApps() throws Exception {
        //create non anonymous app
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();


        webAppsListPage = createWebAppPage.createNonAnonymousAppWithAnonymousResources(new WebApp(
                TEST_NON_ANONYMOUS_APP_NAME,
                TEST_NON_ANONYMOUS_APP_NAME,
                TEST_NON_ANONYMOUS_APP_NAME,
                "1.0", "http://www.google.lk",
                "http"), resources);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME +
                        "'][data-action='Submit for Review']")));

    }

    private void manageLifeCycle() throws Exception {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME +
                        "'][data-action='Submit for Review']")));

        //Set app id's
        non_anonymous_app_id = driver.findElement(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME +
                        "'][data-action='Submit for Review']"))
                .getAttribute("data-app");


        driver.findElement(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME +
                        "'][data-action='Submit for Review']"))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-dismiss='modal']")));

        driver.findElement(By.cssSelector("[data-dismiss='modal']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME + "'][data-action='Approve']")));

        //click approve button
        driver.findElement(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME + "'][data-action='Approve']"))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-dismiss='modal']")));

        driver.findElement(By.cssSelector("[data-dismiss='modal']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME + "'][data-action='Publish']")));

        //click publish button
        driver.findElement(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME + "'][data-action='Publish']"))
                .click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-dismiss='modal']")));

        driver.findElement(By.cssSelector("[data-dismiss='modal']")).click();
    }

    private void accessApps(Boolean isAnonymousAllowedResource, String resourceName)
            throws Exception {
        String redirectedURL;
        String exceptionMsg;

        Thread.sleep(12000);

        driver.get(appMServer.getContextUrls().getWebAppURLHttps() + "/store");

        if (isAnonymousAllowedResource) {
            redirectedURL = TEST_NON_ANONYMOUS_APP_NAME;
            exceptionMsg = "Anonymous App URL is invalid";
        } else {
            redirectedURL = "authenticationendpoint";
            exceptionMsg = "Non Anonymous Apps do not get redirected to login page";
        }

        StoreHomePage.getPage(driver, appMServer);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "a[href*='/store/assets/webapp/" + non_anonymous_app_id + "']")));
        driver.findElement(By.cssSelector(
                "a[href*='/store/assets/webapp/" + non_anonymous_app_id + "']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gatewayURL")));
        driver.get(driver.findElement(By.id("gatewayURL")).getText() + resourceName);

        if (driver.getCurrentUrl().contains(redirectedURL) == false) {
            throw new Exception(exceptionMsg);
        }
        Thread.sleep(800);
    }


    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
