/*
 *Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *WSO2 Inc. licenses this file to you under the Apache License,
 *Version 2.0 (the "License"); you may not use this file except
 *in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an
 *"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */
package org.wso2.carbon.appmanager.integration.ui.TestCases;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.catalina.startup.Tomcat;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.appmanager.integration.ui.Util.TomcatDeployer;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpRequestUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.testng.Assert.assertEquals;
/**
 *
 * This class is for sort web app according to its usage.
 * step1:Add two web apps,publish and subscribe.
 * step2:Generate HTML file for the second added app and access the web page
 * step3:sort apps by usage and assert equal by the order of app IDs in JSON array.
 *
 * @throws Exception
 */
public class SortAppsByUsageTestCase extends APPManagerIntegrationTest {
    private ServerConfigurationManager serverConfigurationManager;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;
    private String username;
    private String password;
    private String appId;
    TomcatDeployer deployer;
    Tomcat tomcat;
    public static String publisherURLHttp;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appMStore;
    private Map<String, String> requestHeaders = new HashMap<String, String>();


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        requestHeaders.put("X-Caramel-Data", "{\"title\":null,\"header\":[\"header\"],\"body\":[\"assets\"," +
                "\"sort-assets\"]}");
        super.init(0);
        publisherURLHttp = getServerURLHttp();
        appMPublisher = new APPMPublisherRestClient(publisherURLHttp);

        //create, publish and subscribe webAppOne
        driver = BrowserManager.getWebDriver();
        storeUIClient = new APPMStoreUIClient();
        ApplicationInitializingUtil baseUtil;
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("webapp1");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        appId = baseUtil.appId;
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient(
                ApplicationInitializingUtil.storeURLHttp);

        //create, publish and subscribe webAppTwo
        ApplicationInitializingUtil baseUtilAppTwo;
        baseUtilAppTwo = new ApplicationInitializingUtil();
        baseUtilAppTwo.init();
        baseUtilAppTwo.createWebApplicationWithExistingUser("webApp2");
        baseUtilAppTwo.testWebApplicationPublish();
        baseUtilAppTwo.testApplicationSubscription();
    }

    /**
     * Tests Sort web app according to usage.
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.SortAppByUsage"}, description = "Sort web apps by its usage")
    public void testApplicationDeletion() throws Exception {

        String root = ProductConstant.getSystemResourceLocation();
        String webAppPath = root + "samples" + File.separator + "usageMonitorTest" + File.separator + "sample";
        String htmlFilePath = webAppPath;
        String appTrackingId;
        //generate HTML file for the web app
        appMPublisher.login(username, password);
        appMStore.login(username, password);
        appTrackingId = appMPublisher.getWebappTrackingId(appId);
        generateHTMLFile(appTrackingId, htmlFilePath);
        //deploy web app with the html page
        deploy(webAppPath);
        storeUIClient.loginDriver(driver, ApplicationInitializingUtil.storeURLHttp, username, password);
        //access web page
        storeUIClient.selectApplication(driver, ApplicationInitializingUtil.appId);
        driver.switchTo().alert().accept();
        storeUIClient.logout(driver, ApplicationInitializingUtil.storeURLHttp);
        storeUIClient.loginDriver(driver, ApplicationInitializingUtil.storeURLHttp, username, password);

        //Request for sort web apps by usage
        HttpResponse appAvailResponse = HttpRequestUtil.doGet(
                "http://localhost:9763/store/assets/webapp/", requestHeaders);
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(appAvailResponse.getData());
        JsonObject editResponse = jsonElement.getAsJsonObject();
        JsonObject appList = (JsonObject) editResponse.getAsJsonObject().get("body").getAsJsonObject().get
                ("assets").getAsJsonObject().get("context");
        JsonArray appListArray = appList.getAsJsonArray("assets").getAsJsonArray();
        assertEquals(appListArray.get(0).getAsJsonObject().get("id").getAsString(),appId, "Web App sort fails");
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
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(filePath + File.separator + "index.html"));
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
}
