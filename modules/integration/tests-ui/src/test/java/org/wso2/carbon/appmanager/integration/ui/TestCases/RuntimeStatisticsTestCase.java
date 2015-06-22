/*
 *
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 * /
 */

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.apache.catalina.startup.Tomcat;
import org.json.JSONArray;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.GetStatisticRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.appmanager.integration.ui.Util.TomcatDeployer;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;
import org.wso2.carbon.utils.FileManipulator;
import org.wso2.carbon.utils.ServerConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This test case will test the application usage monitoring with BAM feature in WSO2 App Manager.
 * In order to run this test case, you need to configure a BAM server as described in the App Manager Documentations
 * and start the BAM server in port offset 1. As mentioned in the documentation, we need to configure the WSO2AM_STATS_DB
 * in master-datasources.xml of App Manager. So that, its required to specify BAM Server location in "<!-- Full path to BAM HOME -->" value
 * under WSO2AM_STATS_DB configuration in provided
 * "integration/tests-ui/src/test/resources/artifacts/AM/configFiles/usagemonitortest/master-datasources.xml" file.
 */
public class RuntimeStatisticsTestCase extends APPManagerIntegrationTest {

    private ServerConfigurationManager serverConfigurationManager;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;
    private String username;
    private String password;

    TomcatDeployer deployer;
    Tomcat tomcat;
    public static String publisherURLHttp;
    private APPMPublisherRestClient appMPublisher;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        if (isBuilderEnabled()) {
            serverConfigurationManager = new ServerConfigurationManager(getServerURLHttps());

            String dataSourceDestinationPath = computeDestinationPathForDataSource("master-datasources.xml");
            String dataSourceSourcePath = computeDataSourceResourcePath("master-datasources.xml");
            copyDataSourceFile(dataSourceSourcePath, dataSourceDestinationPath);
            serverConfigurationManager.applyConfiguration(new File(
                    ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME) +
                            File.separator + "configFiles/usagemonitortest/" + "app-manager.xml"));
            super.init(0);
            publisherURLHttp = getServerURLHttp();
            appMPublisher = new APPMPublisherRestClient(publisherURLHttp);
        }
        driver = BrowserManager.getWebDriver();
        storeUIClient = new APPMStoreUIClient();
        ApplicationInitializingUtil baseUtil;
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("16");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
    }

    @Test(groups = {"wso2.appmanager.statistics"}, description = "Test web application runtime statistics")
    public void testWebAppStatistics() throws Exception {

        String root = ProductConstant.getSystemResourceLocation();
        String webAppPath = root + "samples" + File.separator + "usageMonitorTest" + File.separator + "sample";
        String htmlFilePath = webAppPath;
        String appTrackingId;

        appMPublisher.login(username, password);
        appTrackingId = appMPublisher.getWebappTrackingId(ApplicationInitializingUtil.appId);
        generateHTMLFile(appTrackingId, htmlFilePath);
        deploy(webAppPath);

        storeUIClient.loginDriver(driver, ApplicationInitializingUtil.storeURLHttp, username, password);
        storeUIClient.selectApplication(driver, ApplicationInitializingUtil.appId);
        driver.switchTo().alert().accept();
        storeUIClient.logout(driver, ApplicationInitializingUtil.storeURLHttp);

        //Wait until the statics get published
        Thread.sleep(40000);

        GetStatisticRequest getStatisticRequest = new GetStatisticRequest(getDate(-1), getDate(1));

        HttpResponse getAppResponseTimeResponse = appMPublisher.getAppResponseTime(getStatisticRequest);
        Assert.assertEquals(getAppResponseTimeResponse.getResponseCode(), 200, "Response code mismatch");

        HttpResponse getSubscriberCountByAppResponse = appMPublisher.getSubscriberCountByApp(getStatisticRequest);
        Assert.assertEquals(getSubscriberCountByAppResponse.getResponseCode(), 200, "Response code mismatch");

        HttpResponse getSubscribedAppsByUsersResponse = appMPublisher.getSubscribedAppsByUsers(getStatisticRequest);
        Assert.assertEquals(getSubscribedAppsByUsersResponse.getResponseCode(), 200, "Response code mismatch");

        HttpResponse getAppUsageByUserResponse = appMPublisher.getAppUsageByUser(getStatisticRequest);
        Assert.assertEquals(getAppUsageByUserResponse.getResponseCode(), 200, "Response code mismatch");
        JSONArray getAppUsageByUserResponseData = new JSONArray(getAppUsageByUserResponse.getData());
        Assert.assertNotEquals(getAppUsageByUserResponseData.toString(), "[]", "Response object is null");
        Assert.assertEquals(getAppUsageByUserResponseData.getJSONArray(0).get(0), "testApp16","Application Name mismatch" );
        Object countValue = getAppUsageByUserResponseData.getJSONArray(0).getJSONArray(1).getJSONArray(0).
                getJSONArray(1).getJSONArray(0).get(1);
        Assert.assertEquals(Integer.parseInt(countValue.toString()), 1, "Application usage count mismatch");

    }

    private void copyDataSourceFile(String sourcePath, String destPath) {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);
        try {
            FileManipulator.copyFile(sourceFile, destFile);
        } catch (IOException e) {
            log.error("Error while copying the sample into Jaggery server", e);
        }
    }

    private String computeDestinationPathForDataSource(String fileName) {
        String serverRoot = System.getProperty(ServerConstants.CARBON_HOME);
        String deploymentPath = serverRoot + "/repository/conf/datasources";
        File depFile = new File(deploymentPath);
        if (!depFile.exists() && !depFile.mkdir()) {
            log.error("Error while creating the deployment folder : "
                    + deploymentPath);
        }
        return deploymentPath + File.separator + fileName;
    }

    private String computeDataSourceResourcePath(String fileName) {

        String sourcePath = ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME)
                + File.separator + "configFiles/usagemonitortest/" + fileName;
        return sourcePath;
    }

    private String getDate(int offset) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt = new Date();
        cal.setTime(dt);
        cal.add(Calendar.DATE, offset);
        return sdf.format(cal.getTime());
    }

    private void generateHTMLFile(String trackingId, String filePath) {
        String ipAddress = getNetworkIPAddress();
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<head>\n");
        sb.append("<title>Title Of the page</title>\n");
        sb.append("<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js\"></script>\n");
        sb.append("<script type=\"text/javascript\">\n");
        sb.append("\tvar tracking_code = \"" + trackingId + "\";\n");
        sb.append("\tvar request = $.ajax({\n");
        sb.append("\t\turl: \"http://" + ipAddress + ":8280/statistics\",\n");
        sb.append("\t\ttype: \"GET\",\n");
        sb.append("\t\theaders: { \"trackingCode\":tracking_code}\n");
        sb.append(" \t});");
        sb.append("</script>\n");
        sb.append("</head>\n");
        sb.append("<body>\n<b>Hello World</b>\n</body>\n");
        sb.append("</html>");

        File file;
        try {
            file = new File(filePath);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath + File.separator + "index.html"));
            bufferedWriter.write(sb.toString());
            bufferedWriter.close();
        } catch (IOException e) {
            log.error("Error occurred while writing the content into index.html", e);
        }
    }

    private void deploy(String webAppPath) throws Exception {

        Tomcat tomcat = new Tomcat();
        tomcat.stop();
        tomcat.setBaseDir(".");
        tomcat.addWebapp("/sample", webAppPath);
        tomcat.start();
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        if (driver != null) driver.quit();
        if (tomcat != null) tomcat.stop();
    }

}
