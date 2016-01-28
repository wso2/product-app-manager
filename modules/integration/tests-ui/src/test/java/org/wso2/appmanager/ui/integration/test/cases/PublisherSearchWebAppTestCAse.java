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

public class PublisherSearchWebAppTestCase extends AppManagerIntegrationTest {
    private static final String TEST_DESCRIPTION =
            "Verify Web App search functionality - Publisher";
    private static final String TEST_WEB_APP_ALIAS = "PublisherSearchWebApp";
    private static final int TEST_NO_OF_APPS = 4;

    private static final String STATE_SUBMIT = "Submit for Review";
    public static final String INVALID_NO_OF_MSG_FOUND_ERROR_MSG =
            "Unexpected search result(s) found when searching by App Name";

    private static final Log log = LogFactory.getLog(PublisherSearchWebAppTestCase.class);

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
    public void testSearchFunctionality() throws Exception {

        //Search with exact name (Only one result should be displayed)
        Assert.assertEquals(INVALID_NO_OF_MSG_FOUND_ERROR_MSG, availableElementsCount(1) == 1,
                            true);

        //Search with alias (All apps contain the alias should be listed)
        Assert.assertEquals(INVALID_NO_OF_MSG_FOUND_ERROR_MSG, availableElementsCount(0) ==
                TEST_NO_OF_APPS, true);

        //Search with an invalid alias (Search result should be empty)
        Assert.assertEquals(INVALID_NO_OF_MSG_FOUND_ERROR_MSG, availableElementsCount(-100) == 0,
                            true);

        //Search with no key words (Validation should be triggered)
        driver.findElement(By.id("search-button")).click();
        driver.findElement(By.cssSelector("[class='btn btn-default'][style='margin-left: 0px;']"))
                .click();
    }


    private int availableElementsCount(int webAppId) throws Exception {
        //Assign alias (eg: testApp)
        String inputWebAppName = TEST_WEB_APP_ALIAS;

        //If specific name specified (eg: testApp1)
        if (webAppId != 0) {
            inputWebAppName = TEST_WEB_APP_ALIAS + webAppId;
        }

        //Set search keyword
        driver.findElement(By.id("inp_searchAsset")).sendKeys(inputWebAppName);

        //Execute search
        driver.findElement(By.id("search-button")).click();

        //Loop through every app in the list available and check the visible app count
        int count = 0;
        for (int i = 1; i <= TEST_NO_OF_APPS; i++) {
            try {
                driver.findElement(By.cssSelector(
                        "[data-name='" + TEST_WEB_APP_ALIAS + i +
                                "'][data-action='" + STATE_SUBMIT + "']"));
                //If element is found, increase the count
                count++;
            } catch (org.openqa.selenium.NoSuchElementException e) {
                //Expected error when no elements found
            }
        }
        return count;

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

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(
                    "[data-name='" + TEST_WEB_APP_ALIAS + i +
                            "'][data-action='" + STATE_SUBMIT + "']")));
            driver.navigate().refresh();
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
