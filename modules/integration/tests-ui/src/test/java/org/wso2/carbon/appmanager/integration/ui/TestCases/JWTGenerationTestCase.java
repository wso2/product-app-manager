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

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.appmanager.integration.ui.Util.WireMonitorServer;
import org.wso2.carbon.automation.core.BrowserManager;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class JWTGenerationTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;
    private String claim;
    private ApplicationInitializingUtil baseUtil;
    private static final String ISSUER = "wso2.org/products/appm";
    private static final String SUBJECT = "sub";
    private static final String ISS = "iss";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.testApplicationCreation("2");
        baseUtil.testApplicationPublish();
        baseUtil.testApplicationSubscription();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        claim = "http://wso2.org/claims/role";
        driver = BrowserManager.getWebDriver();
        storeUIClient = new APPMStoreUIClient();
    }

    @Test(groups = {"wso2.appmanager.JWTGenration"}, description = "JWT Generation Test Case")
    public void testJWTGeneration() throws Exception {

        int hostPort = 8080;

        WireMonitorServer server = new WireMonitorServer(hostPort);
        server.start();

        storeUIClient.login(driver, ApplicationInitializingUtil.storeURLHttp, username, password);
        storeUIClient.selectApplication(driver, ApplicationInitializingUtil.appId);
        driver.switchTo().alert().accept();

        String serverMessage = server.getCapturedMessage();

        String jwtBodyRegex =  "X-JWT-Assertion: ([^\\.]+)\\.([^\\.]+)";
        Pattern pattern = Pattern.compile(jwtBodyRegex);
        Matcher matcher = pattern.matcher(serverMessage);

        boolean isJWTPresent = matcher.find();
        assertTrue(isJWTPresent, "JWT header is not present in server message : " + serverMessage);

        String jwtEncodedString = matcher.group(2);
        byte[] jwtByteArray = Base64.decodeBase64(jwtEncodedString.getBytes());
        String decodedJWTString = new String(jwtByteArray);

        JSONObject parsedJWT = new JSONObject(decodedJWTString);

        assertNotNull(parsedJWT.get(claim), String.format("%s claim is not present in the JWT : %s",
                claim, decodedJWTString));
        assertNotNull(parsedJWT.get(ISS), String.format("%s issuer is not present in the JWT : %s",
                ISS, decodedJWTString));
        assertNotNull(parsedJWT.get(SUBJECT), String.format("%s subject is not present in the JWT : %s",
                SUBJECT, decodedJWTString));

        String roles = parsedJWT.get(claim).toString();
        String issuer= parsedJWT.get(ISS).toString();
        String expectedRole = "Internal/" + appProp.getAppName();
        assertTrue(roles.contains(expectedRole), String.format("Expected role '%s' doesn't exist : %s",
                expectedRole, decodedJWTString));
        assertTrue(ISSUER.equals(issuer), String.format("Expected issuer '%s' doesn't exist : %s ", ISSUER, decodedJWTString));
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        String parentWindow = driver.getWindowHandle();
        Set<String> handles =  driver.getWindowHandles();
        for(String windowHandle  : handles) {
            if(!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }
        driver.switchTo().window(parentWindow);
        driver.close();
        baseUtil.destroy();
    }
}
