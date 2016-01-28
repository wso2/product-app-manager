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

import java.util.Set;

public class StoreAnonymousAppAccessTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Verify Anonymous application access";
    private static final String TEST_ANONYMOUS_APP_NAME_1 = "Test_anonymous_app_1";
    private static final String TEST_ANONYMOUS_APP_NAME_2 = "Test_anonymous_app_2";
    private static final String TEST_NON_ANONYMOUS_APP_NAME = "Test_non_anonymous_app_1";

    private static final String STATE_SUBMIT = "Submit for Review";
    private static final String STATE_APPROVE = "Approve";
    private static final String STATE_PUBLISH = "Publish";

    private static final Log log = LogFactory.getLog(StoreAnonymousAppAccessTestCase.class);

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    private String anonymous_app1_id;
    private String anonymous_app2_id;
    private String non_anonymous_app_id;
    WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        wait = new WebDriverWait(driver, 90);

        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);

        //create and publish apps
        createAndPublishApps();
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testAnonymousApplicationAccess() throws Exception {

        //Access anonymous allowed web app (by resources) using an anonymous user
        accessApps(true, anonymous_app1_id, TEST_ANONYMOUS_APP_NAME_1);

        //Access anonymous allowed web app (by flag) using an anonymous user
        accessApps(true, anonymous_app2_id, TEST_ANONYMOUS_APP_NAME_2);

        //Access anonymous disallowed web app using an anonymous user
        accessApps(false, non_anonymous_app_id, "authenticationendpoint");
    }

    private void createAndPublishApps() throws Exception {
        //Anonymous app1 life cycle
        createApps(TEST_ANONYMOUS_APP_NAME_1, TEST_ANONYMOUS_APP_NAME_1, TEST_ANONYMOUS_APP_NAME_1,
                   "1.0", "http://wso2.com", "http");
        //Set app id
        anonymous_app1_id = driver.findElement(By.cssSelector(
                "[data-name='" + TEST_ANONYMOUS_APP_NAME_1 + "'][data-action='" + STATE_SUBMIT +
                        "']")).getAttribute("data-app");
        //anonymous app1: click submit button
        changeLifeCycleState(TEST_ANONYMOUS_APP_NAME_1, STATE_SUBMIT);
        //anonymous app1: click approve button
        changeLifeCycleState(TEST_ANONYMOUS_APP_NAME_1, STATE_APPROVE);
        //anonymous app1: click publish button
        changeLifeCycleState(TEST_ANONYMOUS_APP_NAME_1, STATE_PUBLISH);
        driver.navigate().refresh();

        //Anonymous app2 life cycle
        createApps(TEST_ANONYMOUS_APP_NAME_2, TEST_ANONYMOUS_APP_NAME_2, TEST_ANONYMOUS_APP_NAME_2,
                   "1.0", "http://wso2.com", "http");
        //Set app id
        anonymous_app2_id = driver.findElement(By.cssSelector(
                "[data-name='" + TEST_ANONYMOUS_APP_NAME_2 + "'][data-action='" + STATE_SUBMIT +
                        "']")).getAttribute("data-app");
        //anonymous app2: click submit button
        changeLifeCycleState(TEST_ANONYMOUS_APP_NAME_2, STATE_SUBMIT);
        //anonymous app2: click approve button
        changeLifeCycleState(TEST_ANONYMOUS_APP_NAME_2, STATE_APPROVE);
        //anonymous app2: click publish button
        changeLifeCycleState(TEST_ANONYMOUS_APP_NAME_2, STATE_PUBLISH);
        driver.navigate().refresh();

        //Non anonymous app life cycle
        createApps(TEST_NON_ANONYMOUS_APP_NAME, TEST_NON_ANONYMOUS_APP_NAME,
                   TEST_NON_ANONYMOUS_APP_NAME,
                   "1.0", "http://wso2.com", "http");
        //Set app id
        non_anonymous_app_id = driver.findElement(By.cssSelector(
                "[data-name='" + TEST_NON_ANONYMOUS_APP_NAME +
                        "'][data-action='" + STATE_SUBMIT + "']"))
                .getAttribute("data-app");
        //non anonymous app1: click submit button
        changeLifeCycleState(TEST_NON_ANONYMOUS_APP_NAME, STATE_SUBMIT);
        //non anonymous app1: click approve button
        changeLifeCycleState(TEST_NON_ANONYMOUS_APP_NAME, STATE_APPROVE);
        //non anonymous app1: click publish button
        changeLifeCycleState(TEST_NON_ANONYMOUS_APP_NAME, STATE_PUBLISH);
        driver.navigate().refresh();
    }


    private void createApps(String webAppName, String displayName, String context, String version,
                            String webAppUrl, String transport) throws Exception {
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();
        webAppsListPage = createWebAppPage.createWebApp(
                new WebApp(webAppName, displayName, context, version, webAppUrl, transport));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + webAppName +
                        "'][data-action='" + STATE_SUBMIT + "']")));
        driver.navigate().refresh();
    }

    private void changeLifeCycleState(String webAppName, String Status) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + webAppName +
                        "'][data-action='" + Status + "']")));
        driver.findElement(By.cssSelector(
                "[data-name='" + webAppName + "'][data-action='" + Status + "']"))
                .click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-dismiss='modal']")));
        driver.findElement(By.cssSelector("[data-dismiss='modal']")).click();
    }

    private void accessApps(Boolean isAnonymousApp, String appId, String redirectedURL)
            throws Exception {
        String exceptionMsg;

        // Thread.sleep(1200);

        //temp wait till the assets load, if any (repeat this 2 times to give a reasonable
        // waiting time)
        WebDriverWait tempWait = new WebDriverWait(driver, 5);
        for (int i = 0; i < 2; i++) {
            try {
                tempWait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                        "a[href*='/store/assets/webapp/" + appId + "']")));
                driver.navigate().refresh();
                break;
            } catch (org.openqa.selenium.TimeoutException e) {
                //Expected error when no element found
            }
        }

        driver.get(appMServer.getContextUrls().getWebAppURLHttps() + "/store");
        StoreHomePage.getPage(driver, appMServer);


        if (isAnonymousApp) {
            exceptionMsg = "Anonymous App URL is invalid";
        } else {
            exceptionMsg = "Non Anonymous Apps do not get redirected to login page";
        }

        driver.findElement(By.cssSelector(
                "a[href*='/store/assets/webapp/" + appId + "']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("gatewayURL")));
        driver.findElement(By.id("gatewayURL")).click();

        Set<String> afterPopup = driver.getWindowHandles();
        if (afterPopup.size() > 1) {
            if (driver.switchTo().window((String) afterPopup.toArray()[1]).getCurrentUrl()
                    .contains(redirectedURL) == false) {
                throw new Exception(exceptionMsg);
            }
            //switch to popup page and close
            driver.switchTo().window((String) afterPopup.toArray()[1]).close();
            driver.switchTo().window((String) afterPopup.toArray()[0]);
        }
    }


    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
