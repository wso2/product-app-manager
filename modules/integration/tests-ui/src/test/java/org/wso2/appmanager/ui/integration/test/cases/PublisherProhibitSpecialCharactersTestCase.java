package org.wso2.appmanager.ui.integration.test.cases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.dto.WebApp;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherCreateWebAppPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;


public class PublisherProhibitSpecialCharactersTestCase extends AppManagerIntegrationTest {

    private static final String TEST_DESCRIPTION = "Prohibit users from creating a web app with special characters in the name";
    private static final Log log = LogFactory.getLog(PublisherCreateWebAppTestCase.class);

    private PublisherWebAppsListPage webAppsListPage;
    private PublisherCreateWebAppPage createWebAppPage;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();

        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testPublisherProhibitSpecialCharacters() throws Exception {

        createWebAppPage = webAppsListPage.gotoCreateWebAppPage();

        //create  web app with special characters in name
        createWebAppPage.createWebApp(new WebApp("##$@#@", "Test1", "/test1",
                "1.0", "http://wso2.com", "http"));

        Assert.assertEquals(driver.findElement(By.className("info-div")).findElement(By.tagName("span"))
                .getText().equals("Webapp Name contains one or more illegal characters (~!@#;%^*()+={}|\\<>\"',)"),
                true, TEST_DESCRIPTION + ": Failed");
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }

}
