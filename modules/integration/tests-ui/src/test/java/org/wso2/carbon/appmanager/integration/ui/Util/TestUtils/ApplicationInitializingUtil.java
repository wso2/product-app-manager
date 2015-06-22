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

package org.wso2.carbon.appmanager.integration.ui.Util.TestUtils;

import org.json.JSONObject;
import org.openqa.selenium.WebDriver;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreUIClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.AppCreateRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.MobileApplicationBean;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.SubscriptionRequest;
import org.wso2.carbon.automation.core.BrowserManager;
import org.wso2.carbon.automation.core.ProductConstant;
import org.wso2.carbon.automation.core.utils.HttpResponse;

import java.io.File;
import java.util.Set;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class ApplicationInitializingUtil extends APPManagerIntegrationTest {

    public static String storeURLHttp;
    public static String publisherURLHttp;
    public static String appId;
    public static String version;
    public static String appName;
    public static String appDisplayName;
    public static String appURL;
    public static String transport;
    public static String anonymousAccessToUrlPattern;
    public static String policyGroupName;
    public static String throttlingTier;
    public static String objPartialMappings;
    public static String policyGroupDesc;
    public static boolean appSubscriptionStatus;
    private APPMStoreRestClient appMStore;
    private APPMPublisherRestClient appMPublisher;
    private String username;
    private String password;
    private WebDriver driver;
    private APPMStoreUIClient storeUIClient;
    private String mobileAppName;
    private static final String BANNER = "banner.png";
    private static final String ICON = "icon.png";
    private static final String SCREEN1 = "screen1.png";
    private static final String SCREEN2 = "screen2.png";
    private String mobileAppDescription;


    public void init() throws Exception {
        super.init(0);
        if (isBuilderEnabled()) {
            storeURLHttp = getServerURLHttp();
            publisherURLHttp = getServerURLHttp();

        } else {
            storeURLHttp = getStoreServerURLHttp();
        }
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient(storeURLHttp);
        appMPublisher = new APPMPublisherRestClient(publisherURLHttp);
        appURL = appProp.getAppURL();
        appName = appProp.getAppName();
        appDisplayName = appProp.getAppDisplayName();
        mobileAppName = appProp.getMobileAppName();
        mobileAppDescription = appProp.getMobileAppDescription();
        version = appProp.getVersion();
        transport = appProp.getTransports();
        anonymousAccessToUrlPattern = appProp.getAnonymousAccessToUrlPattern();
        policyGroupName = appProp.getPolicyGroupName();
        throttlingTier = appProp.getThrottlingTier();
        objPartialMappings = appProp.getObjPartialMappings();
        policyGroupDesc = appProp.getPolicyGroupDesc();
        storeUIClient = new APPMStoreUIClient();
        driver = BrowserManager.getWebDriver();
    }

    public HttpResponse createWebApplicationWithExistingUser(String prefix)
            throws Exception {
        return createWebApplication(prefix, username, password);
    }

    public HttpResponse createWebApplicationWithAnonymousAccess(String prefix, String allowAnonymousAccess)
            throws Exception {
        return createWebApplication(prefix, username, password, allowAnonymousAccess);
    }

    /**
     * This method is use to create web application in given user
     *
     * @param prefix           prefix for web application name
     * @param creatorUserName  username
     * @param creatorPasssword password
     * @throws java.lang.Exception on error
     */

    public HttpResponse createWebApplicationWithGivenUser(String prefix, String creatorUserName,
                                                          String creatorPasssword)
            throws Exception {
        return createWebApplication(prefix, creatorUserName, creatorPasssword);
    }

    /**
     * This method is use to publish web application
     *
     * @throws java.lang.Exception on error
     */
    public void testWebApplicationPublish() throws Exception {
        HttpResponse appPublishResponse = appMPublisher.publishApp(appId, "webapp");
        assertEquals(appPublishResponse.getResponseCode(), 200, "Application publishing failed");
    }

    /**
     * This method is use to application subscription
     *
     * @throws java.lang.Exception on error
     */
    public void testApplicationSubscription() throws Exception {

        appMStore.login(username, password);

        long timeBefore = System.nanoTime();
        long timeElapsed = 0;
        while (true) {
            storeUIClient.accessStore(driver, storeURLHttp);
            Thread.sleep(1000);
            if (driver.getPageSource().contains(appId)) {
                SubscriptionRequest appSubscriptionRequest = new SubscriptionRequest(appName, username, version);
                HttpResponse appSubscriptionResponse = appMStore.subscribeForApplication(appSubscriptionRequest);
                JSONObject subscriptionJsonObjectPublished = new JSONObject(appSubscriptionResponse.getData());
                appSubscriptionStatus = (Boolean) subscriptionJsonObjectPublished.get("status");
                assertFalse((Boolean) subscriptionJsonObjectPublished.get("error"),
                            "Error while updating tier permission");
                assertEquals(appSubscriptionResponse.getResponseCode(), 200, "Application subscription failed");
                break;
            } else if (timeElapsed >= 6.0e+10 && timeElapsed != 0) {
                assertTrue(false, "Application " + appProp.getAppName() + " is not subscribed");
                break;
            }
            timeElapsed = System.nanoTime() - timeBefore;
        }
    }

    /**
     * This method is use to subscribe application for given id
     *
     * @throws java.lang.Exception on error
     */
    public HttpResponse testApplicationSubscription(String applicationID) throws Exception {

        appMStore.login(username, password);

        long timeBefore = System.nanoTime();
        long timeElapsed = 0;
        while (true) {
            storeUIClient.accessStore(driver, storeURLHttp);
            Thread.sleep(1000);
            if (driver.getPageSource().contains(applicationID)) {
                SubscriptionRequest appSubscriptionRequest = new SubscriptionRequest(appName, username, version);
                HttpResponse appSubscriptionResponse = appMStore.subscribeForApplication(appSubscriptionRequest);
                return appSubscriptionResponse;
            } else if (timeElapsed >= 6.0e+10 && timeElapsed != 0) {
                return null;
            }
            timeElapsed = System.nanoTime() - timeBefore;
        }
    }

    /**
     * This method is use to create mobile application
     *
     * @throws java.lang.Exception on error
     */
    public String createMobileApplication() throws Exception {
        MobileApplicationBean mobileApplicationBean = new MobileApplicationBean();
        JSONObject appmetaData = new JSONObject();
        appmetaData.put("package", "com.google.android.maps.mytracks");
        appmetaData.put("version", "1.0");
        mobileApplicationBean.setAppmeta(appmetaData.toString());
        mobileApplicationBean.setVersion(version);
        mobileApplicationBean.setProvider("1WSO2Mobile");
        mobileApplicationBean.setPlatform("android");
        mobileApplicationBean.setMarkettype("public");
        mobileApplicationBean.setName(mobileAppName);
        mobileApplicationBean.setDescription(mobileAppDescription);
        mobileApplicationBean.setBannerFilePath(getFileFromResources(BANNER));
        mobileApplicationBean.setIconFile(getFileFromResources(ICON));
        mobileApplicationBean.setScreenShot1File(getFileFromResources(SCREEN1));
        mobileApplicationBean.setScreenShot2File(getFileFromResources(SCREEN2));
        mobileApplicationBean.setScreenShot3File(getFileFromResources(SCREEN2));
        mobileApplicationBean.setMobileapp("mobileapp");
        return appMPublisher.
                createMobileApplicatopn(mobileApplicationBean);
    }

    private HttpResponse createWebApplication(String prefix, String creatorUserName, String creatorPassword) throws
            Exception {
        appMPublisher.login(creatorUserName, creatorPassword);
        String root = ProductConstant.getSystemResourceLocation();
        String policyPath = root + "samples" + File.separator + "policy.xml";
        String xml = convertXMLFileToString(policyPath);
        HttpResponse response = appMPublisher.validatePolicy(xml);
        JSONObject validateObject = new JSONObject(response.getData());
        boolean validateSucceed = validateObject.getBoolean("success");
        assertTrue(validateSucceed, "Policy validation failed.");
        String policyPartialId = "";

        if (validateSucceed) {
            HttpResponse partialIdResponse = appMPublisher.savePolicy("testPolicy", xml);
            JSONObject saveObject = new JSONObject(partialIdResponse.getData());
            JSONObject responseId = saveObject.optJSONObject("response");
            policyPartialId = responseId.getString("id");
        }

        String policyGropuId = appMPublisher.savePolicyGroup(xml, anonymousAccessToUrlPattern, policyGroupName,
                                                             throttlingTier, objPartialMappings, policyGroupDesc);

        int hostPort = 8080;
        AppCreateRequest appCreateRequest = createSingleApp(appName + prefix, appDisplayName, version, transport,
                                                            appURL, hostPort,
                                                            appProp.getTier(), policyPartialId, policyGropuId);
        appName = appCreateRequest.getOverview_name();
        version = appCreateRequest.getOverview_version();
        HttpResponse appCreateResponse = appMPublisher.createApp(appCreateRequest);
        JSONObject jsonObject = new JSONObject(appCreateResponse.getData());
        appId = (String) jsonObject.get("id");
        return appCreateResponse;
    }

    private HttpResponse createWebApplication(String prefix, String creatorUserName, String
            creatorPassword, String allowAnonymousAccess)
            throws Exception {
        appMPublisher.login(creatorUserName, creatorPassword);
        String root = ProductConstant.getSystemResourceLocation();
        String policyPath = root + "samples" + File.separator + "policy.xml";
        String xml = convertXMLFileToString(policyPath);
        HttpResponse response = appMPublisher.validatePolicy(xml);
        JSONObject validateObject = new JSONObject(response.getData());
        boolean validateSucceed = validateObject.getBoolean("success");
        assertTrue(validateSucceed, "Policy validation failed.");
        String policyPartialId = "";

        if (validateSucceed) {
            HttpResponse partialIdResponse = appMPublisher.savePolicy("testPolicy", xml);
            JSONObject saveObject = new JSONObject(partialIdResponse.getData());
            JSONObject responseId = saveObject.optJSONObject("response");
            policyPartialId = responseId.getString("id");
        }

        String policyGropuId = appMPublisher.savePolicyGroup(xml, anonymousAccessToUrlPattern, policyGroupName,
                                                             throttlingTier, objPartialMappings, policyGroupDesc);

        int hostPort = 8080;
        AppCreateRequest appCreateRequest = createSingleApp(appName + prefix, appDisplayName, version, transport,
                                                            appURL, hostPort,
                                                            appProp.getTier(), policyPartialId, policyGropuId,
                                                            allowAnonymousAccess);
        appName = appCreateRequest.getOverview_name();
        version = appCreateRequest.getOverview_version();
        HttpResponse appCreateResponse = appMPublisher.createApp(appCreateRequest);
        JSONObject jsonObject = new JSONObject(appCreateResponse.getData());
        appId = (String) jsonObject.get("id");
        return appCreateResponse;
    }

    public void destroy() throws Exception {
        super.cleanup();
        String parentWindow = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        for (String windowHandle : handles) {
            if (!windowHandle.equals(parentWindow)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }
        driver.switchTo().window(parentWindow);
        driver.close();
    }

    /**
     * This method is use to get a file that has given name from resources folder
     */
    private File getFileFromResources(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file;
    }
}
