/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import com.google.gdata.client.GoogleAuthTokenFactory;
import com.google.gdata.client.GoogleService;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;
import org.wso2.carbon.utils.ServerConstants;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.ServletException;
import java.io.*;
import java.net.URL;
import java.util.Properties;

import static org.testng.Assert.assertTrue;

/**
 * This class contains test case to check whether stats are published to google analytics.
 * since google analytics takes 24 hours to publish the stats , we check for the real time user
 * to ensure that stats are published.
 * ref - https://developers.google.com/analytics/devguides/reporting/realtime/v3/reference/data/realtime/get
 *
 * Flow of the test
 * 1. Enable google analytics in app-manager.xml
 * 2. Deploy the webb app "sample" which exists in resources/artifacts/AM/googleAnalyticsTestCase
 * 3. Create,publish and subscribe web app in app manager
 * 4. login to store and access the webapp
 * 5. login to google analytic and check for the active user (real time data)
 * 6. if active user count is > 0 then assume stats are successfully published.
 */
public class GoogleAnalyticsTestCase extends APPManagerIntegrationTest {
    //TODO -How to add the ip address to webbapp?
    private String userName;
    private String password;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;
    private Properties ganalyticProp;
    private Tomcat tomcat;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        // enable google analytic in app-manager.xml
        ServerConfigurationManager serverConfigurationManager = new
                ServerConfigurationManager(amServer.getBackEndUrl());
        File srcFile = new File(ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME)
                + File.separator + "googleAnalyticsTestCase" + File.separator + "app-manager.xml");
        File trgFile = new File(System.getProperty(ServerConstants.CARBON_HOME)
                + File.separator + "repository" + File.separator + "conf" + File.separator + "app-manager.xml");
        serverConfigurationManager.applyConfiguration(srcFile, trgFile);
        // deploy the web app in tomcat server
        deployWebAPP();
        //load the google analytic related properties
        this.loadProperties();
        //create,publish and subscribe to app
        ApplicationInitializingUtil baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("GoogleAnalytic");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        storeUIClient = new APPMStoreUIClient();
        userName = userInfo.getUserName();
        password = userInfo.getPassword();
        driver = BrowserManager.getWebDriver();
    }

    @Test(groups = {"wso2.appmanager.googleanalytics"}, description = "Google Analytics test")
    public void statPublishTestCase() throws Exception {
        //login to store and access the web app url
        storeUIClient.login(driver, ApplicationInitializingUtil.storeURLHttp, userName, password);
        storeUIClient.selectApplication(driver, ApplicationInitializingUtil.appId);
        driver.switchTo().alert().accept();

        // wait for stats to be published
        Thread.sleep(10000);
        // login to the google analytic and check for the real time user count
        String guserName = ganalyticProp.getProperty("username");
        String gpassword = ganalyticProp.getProperty("pass");
        //Analytics view (profile) ID
        String profileId = ganalyticProp.getProperty("profileId");
        GoogleService analy = new GoogleService("analytics", "myapplication");
        analy.setUserCredentials(guserName, gpassword);
        GoogleAuthTokenFactory.AuthToken authToken = analy.getAuthTokenFactory().getAuthToken();
        String authKey = ((GoogleAuthTokenFactory.UserToken) authToken).getValue();

        String queryUrl = "https://www.googleapis.com/analytics/v3/data/realtime?ids=ga:" + profileId +
                "&metrics=rt:activeUsers";
        URL obj = new URL(queryUrl);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        String authVal = "GoogleLogin auth=" + authKey;
        con.setRequestProperty("Authorization", authVal);
        BufferedReader in = null;
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            // check whether rt:activeUsers value is > 0 is exists
            boolean success = response.toString().matches(".*\"rt:activeUsers\":\"[1-9]+\".*");
            assertTrue(success, "Stats publish to Google analytic failed");
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        if (driver != null) driver.quit();
        if (tomcat != null) tomcat.stop();
        super.cleanup();

    }

    /**
     * deploy the web app to tomcat server which runs on port 8080
     *
     * @throws Exception
     */
    private void deployWebAPP() throws ServletException, LifecycleException {
        String srcPath = ProductConstant.getResourceLocations(ProductConstant.AM_SERVER_NAME)
                + File.separator + "googleAnalyticsTestCase" + File.separator + "sample";
        tomcat = new Tomcat();
        tomcat.setBaseDir(".");
        tomcat.addWebapp("/sample", srcPath);
        tomcat.start();
    }

    /**
     * load the google analytic related properties
     *
     * @throws IOException
     */
    private void loadProperties() throws IOException {
        ganalyticProp = new Properties();
        String propFileName = "ganalytics.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        ganalyticProp.load(inputStream);

        if (inputStream == null) {
            throw new FileNotFoundException("Property file '" + propFileName + "' is not found in the classpath");
        }
    }

}
