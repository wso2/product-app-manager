package org.wso2.appmanager.ui.integration.test.cases.store.webapp;

import junit.framework.Assert;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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

public class StoreSearchWebAppTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Verify Web App search functionality - Store";
    private static final String TEST_WEB_APP_ALIAS = "TestStoreWebApp";
    private static final int TEST_NO_OF_APPS = 4;
    public static final String INVALID_NO_OF_MSG_FOUND_ERROR_MSG =
            "Unexpected search result(s) found when searching by ";

    private static final String STATE_SUBMIT = "Submit for Review";
    private static final String STATE_APPROVE = "Approve";
    private static final String STATE_PUBLISH = "Publish";

    private static final String SEARCH_CRITERIA_NAME = "Name";
    private static final String SEARCH_CRITERIA_PROVIDER = "Provider";


    private static final Log log = LogFactory.getLog(StoreSearchWebAppTestCase.class);
    WebDriverWait wait;

    private StoreHomePage homePage;
    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        wait = new WebDriverWait(driver, 90);

        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();

        //Create and publish multiple apps
        for (int i = 1; i <= TEST_NO_OF_APPS; i++) {
            String webAppName = TEST_WEB_APP_ALIAS + i;
            //Create sample web apps
            createApps(webAppName, webAppName, webAppName, "1.0", "http://www.wso2.com", "http");

            //Submit
            changeLifeCycleState(webAppName, STATE_SUBMIT);

            //Approve
            changeLifeCycleState(webAppName, STATE_APPROVE);

            //Publish
            changeLifeCycleState(webAppName, STATE_PUBLISH);

            driver.navigate().refresh();
        }
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testSearchFunctionality() throws Exception {
        //access store
        driver.get(appMServer.getContextUrls().getWebAppURLHttps() + "/store");
        StoreHomePage.getPage(driver, appMServer);

        //open web apps section
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("openApps")));
        driver.findElement(By.id("openApps")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "a[href*='/store/assets/webapp']")));
        driver.findElement(By.cssSelector("a[href*='/store/assets/webapp']")).click();

        //Search with exact web app name (Only one result should be displayed)
        Assert.assertEquals(INVALID_NO_OF_MSG_FOUND_ERROR_MSG + SEARCH_CRITERIA_NAME,
                            (availableElementsCount(
                                    SEARCH_CRITERIA_NAME, (TEST_WEB_APP_ALIAS +
                                            TEST_NO_OF_APPS)) == 1), true);

        //Search with invalid web app name (no result should be displayed)
        Assert.assertEquals(INVALID_NO_OF_MSG_FOUND_ERROR_MSG + " Invalid " + SEARCH_CRITERIA_NAME,
                            (availableElementsCount(
                                    SEARCH_CRITERIA_NAME, (TEST_WEB_APP_ALIAS + 1000)) == 0), true);


        //Search with exact provider name (Only one result should be displayed)
        Assert.assertEquals(INVALID_NO_OF_MSG_FOUND_ERROR_MSG + SEARCH_CRITERIA_PROVIDER,
                            (availableElementsCount(
                                    SEARCH_CRITERIA_PROVIDER,
                                    appMServer.getSuperTenant().getTenantAdmin().getUserName()) ==
                                    TEST_NO_OF_APPS), true);

        //Search with invalid provider name (no result should be displayed)
        Assert.assertEquals(
                INVALID_NO_OF_MSG_FOUND_ERROR_MSG + " Invalid " + SEARCH_CRITERIA_PROVIDER,
                (availableElementsCount(
                        SEARCH_CRITERIA_NAME, "DummyInvalidUser") == 0), true);
    }


    private int availableElementsCount(String searchCriteria, String searchKeyWord)
            throws Exception {
        //set search Criteria
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchSelect")));
        Select dropdownSearchCriteria = new Select(driver.findElement(By.id("searchSelect")));
        dropdownSearchCriteria.selectByValue(searchCriteria);

        //Set search keyword
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchTxt")));
        driver.findElement(By.id("searchTxt")).clear();
        driver.findElement(By.id("searchTxt")).sendKeys(searchKeyWord);

        //Execute search
        driver.findElement(By.id("searchTxt")).sendKeys(Keys.ENTER);

        //Element count
        int count = 0;

        //temp wait till the assets load, if any (repeat this 2 times to give a reasonable
        // waiting time)
        WebDriverWait tempWait = new WebDriverWait(driver, 5);
        for (int i = 0; i < 2; i++) {
            try {
                tempWait.until(ExpectedConditions.visibilityOfElementLocated(By.className(
                        "app-assets-name")));
                driver.navigate().refresh();
                count = driver.findElements(By.className("app-assets-name")).size();
                break;
            } catch (org.openqa.selenium.TimeoutException e) {
                //Expected error when no element found
                driver.navigate().refresh();
            }
        }
        return count;
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


    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
