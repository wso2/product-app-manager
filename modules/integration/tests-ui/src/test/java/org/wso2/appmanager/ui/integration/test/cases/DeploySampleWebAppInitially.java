package org.wso2.appmanager.ui.integration.test.cases;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;
import org.wso2.appmanager.ui.integration.test.utils.AppmUiTestConstants;
import org.wso2.appmanager.ui.integration.test.utils.DeploySampleWebApp;

/**
 * Use to deploy a sample web app to be used in subsequent classes
 */
public class DeploySampleWebAppInitially extends AppManagerIntegrationTest {
    private DeploySampleWebApp deploySampleWebApp;
    private static final String TEST_DESCRIPTION = "Deploy sample web app";

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testAnonymousApplicationAccess() throws Exception {
        //deploy web app
        deploySampleWebApp = new DeploySampleWebApp();
        deploySampleWebApp.copyFileUsingFileStreams(
                AppmUiTestConstants.SAMPLE_DEPLOYED_WEB_APP_NAME);
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
