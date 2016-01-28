package org.wso2.appmanager.ui.integration.test.cases;

import junit.framework.Assert;
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
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;
import org.wso2.appmanager.ui.integration.test.utils.AppmUiTestConstants;


public class PublisherPaginationTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION = "Verify Publisher pagination functionality";
    private static final String TEST_WEB_APP_ALIAS = "PublisherPagination";
    private static final int TEST_NO_OF_APPS = 25;

    private static final String SUBMIT_STATE = "Submit for Review";
    private static final String APPROVE_STATE = "Approve";
    private static final String PUBLISH_STATE = "Publish";

    private static final int CUSTOM_PAGE_SIZE = 20;

    private static final Log log = LogFactory.getLog(PublisherPaginationTestCase.class);

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
        wait = new WebDriverWait(driver, 120);

        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);
        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();

        //Create sample web apps
        createApps();
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testPaginationFunctionality() throws Exception {
        //Check page size
        Assert.assertEquals("Something wrong with Pagination",
                            availableElementsCount() == CUSTOM_PAGE_SIZE,
                            true);

        //Go to next page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "a[href*='/publisher/assets/webapp/?page=2']")));
        driver.findElement(By.cssSelector("a[href*='/publisher/assets/webapp/?page=2']"))
                .click();
        //Check page size
        Assert.assertEquals("Something wrong with Pagination",
                            availableElementsCount() == (TEST_NO_OF_APPS - CUSTOM_PAGE_SIZE), true);

        //Go to previous page
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "a[href*='/publisher/assets/webapp/?page=1']")));
        //Check page size
        driver.findElement(By.cssSelector("a[href*='/publisher/assets/webapp/?page=1']"))
                .click();
        Assert.assertEquals("Something wrong with Pagination",
                            availableElementsCount() == CUSTOM_PAGE_SIZE,
                            true);

    }


    private void createApps() throws Exception {
        for (int i = 1; i <= TEST_NO_OF_APPS; i++) {
            createWebAppPage = webAppsListPage.gotoCreateWebAppPage();
            webAppsListPage = createWebAppPage.createWebApp(
                    new WebApp(
                            TEST_WEB_APP_ALIAS + i,
                            TEST_WEB_APP_ALIAS + i,
                            TEST_WEB_APP_ALIAS + i,
                            "1.0", "http://www.wso2.com",
                            "http"));

        }
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                "[data-name='" + TEST_WEB_APP_ALIAS + TEST_NO_OF_APPS +
                        "'][data-action='" + SUBMIT_STATE + "']")));
        driver.navigate().refresh();
    }


    private int availableElementsCount() throws Exception {
        //Loop through every app in the list available and check the visible app count
        int count = 0;
        try {
            count = driver.findElements(By.cssSelector(
                    "[data-action='" + AppmUiTestConstants.LifeCycleStatus.SUBMIT_FOR_REVIEW +
                            "']")).size();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            //Expected error when no elements found
        }
        return count;
    }


    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
