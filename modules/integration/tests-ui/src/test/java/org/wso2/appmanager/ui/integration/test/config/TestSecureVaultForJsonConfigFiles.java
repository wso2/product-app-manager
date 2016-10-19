package org.wso2.appmanager.ui.integration.test.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.PublisherWebAppsListPage;
import org.wso2.appmanager.ui.integration.test.pages.StoreHomePage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;
import org.wso2.carbon.automation.engine.frameworkutils.FrameworkPathUtil;
import org.wso2.carbon.utils.FileManipulator;
import org.wso2.carbon.utils.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TestSecureVaultForJsonConfigFiles extends AppManagerIntegrationTest {

    private static final Log log = LogFactory.getLog(TestSecureVaultForJsonConfigFiles.class);
    private Map<String, String> configs;
    private StoreHomePage homePage;
    private PublisherWebAppsListPage webAppsListPage;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        populateConfigs();
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            String app = entry.getKey();
            String file = entry.getValue();
            String configDestinationPath = computeDestinationPathForConfig(app, file);
            String configSourcePath = computeConfigResourcePath(app, file);
            String configBackupPath = computeConfigBackupPath(app, file);
            backupConfigFile(configDestinationPath, configBackupPath);
            copyConfigFile(configSourcePath, configDestinationPath);
        }
        super.init();
    }


    private void populateConfigs() {
        configs = new HashMap<String, String>();
        configs.put("publisher", "publisher.json");
        configs.put("store", "store.json");
        configs.put("social", "social.json");
    }

    private void copyConfigFile(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        try {
            FileManipulator.copyFile(sourceFile, destFile);
        } catch (IOException e) {
            log.error("Error while copying the file to destination", e);
        }
    }

    private void backupConfigFile(String source, String backup) {
        copyConfigFile(source, backup);
    }

    private String computeDestinationPathForConfig(String webApp, String fileName) {
        String serverRoot = System.getProperty(ServerConstants.CARBON_HOME);
        String deploymentPath = serverRoot + File.separator + "repository" + File.separator
                + "deployment" + File.separator + "server" + File.separator + "jaggeryapps" + File.separator + webApp +
                File.separator;
        String configDir = "config";
        if ("social".equals(webApp)) {
            configDir = "configs";
        }

        deploymentPath = deploymentPath + configDir;
        File depFile = new File(deploymentPath);
        if (!depFile.exists() && !depFile.mkdir()) {
            log.error("Error while creating the cpnfig folder : " + deploymentPath);
        }
        return deploymentPath + File.separator + fileName;
    }

    private String computeConfigResourcePath(String webApp, String fileName) {

        String sourcePath = FrameworkPathUtil.getSystemResourceLocation() + "artifacts" + File.separator + webApp +
                File.separator + fileName;
        return sourcePath;
    }

    private String computeConfigBackupPath(String webApp, String fileName) {

        String path = FrameworkPathUtil.getTargetDirectory() + File.separator + "backup" + File.separator + webApp +
                File.separator + fileName;
        return path;
    }

    @Test(groups = TEST_GROUP, description = "Test Store login After changing configs")
    public void testStoreLogin() throws Exception {
        //login to store
        homePage = (StoreHomePage) login(driver, LoginPage.LoginTo.STORE);
        //new WebDriverWait(driver, 90).until(ExpectedConditions.titleIs("WSO2 App Manager"));

    }

    @Test(groups = TEST_GROUP, description = "Test Publisher login After changing configs")
    public void testPublisherLogin() throws Exception {
        //login to publisher
        webAppsListPage = (PublisherWebAppsListPage) login(driver, LoginPage.LoginTo.PUBLISHER);
        //new WebDriverWait(driver, 90).until(ExpectedConditions.titleIs("webapp | WSO2 App Manager"));

    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        for (Map.Entry<String, String> entry : configs.entrySet())
        {
            String app = entry.getKey();
            String file = entry.getValue();
            String configDestinationPath = computeDestinationPathForConfig(app, file);
            String configBackupPath = computeConfigBackupPath(app, file);
            backupConfigFile(configBackupPath, configDestinationPath);
        }
        closeDriver(driver);
    }

}
