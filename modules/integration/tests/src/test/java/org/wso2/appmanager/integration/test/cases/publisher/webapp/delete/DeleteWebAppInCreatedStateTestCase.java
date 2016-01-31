/*
 *
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * /
 */

package org.wso2.appmanager.integration.test.cases.publisher.webapp.delete;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.VerificationUtil;
import org.wso2.appmanager.integration.utils.WebAppUtil;
import org.wso2.appmanager.integration.utils.bean.PolicyGroup;
import org.wso2.appmanager.integration.utils.bean.WebApp;
import org.wso2.appmanager.integration.utils.bean.WebAppResource;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.util.List;

/**
 * This Test class verifies the ability of appCreator, appPublisher and admin users to delete web apps in 'Created' state
 */
public class DeleteWebAppInCreatedStateTestCase {

    private static final String TEST_DESCRIPTION = "Delete web application in Created state.";
    private static final String TEST_APP_NAME_SUFFIX = "DeleteWebAppInCreatedStateTestCase";
    private static final String creatorDeleteAppTest = AppmTestConstants.TestUsers.APP_CREATOR + TEST_APP_NAME_SUFFIX;
    private static final String publisherDeleteAppTest = AppmTestConstants.TestUsers.APP_PUBLISHER + TEST_APP_NAME_SUFFIX;
    private static final String adminDeleteAppTest = AppmTestConstants.TestUsers.ADMIN + TEST_APP_NAME_SUFFIX;

    private String appVersion = "1.0.0";
    private String contextPrefix = "/";
    private String appProvider;
    private static User appCreator;
    private static User adminUser;
    private String backEndUrl;
    private static AutomationContext appMServer;
    private static String creatorDeleteAppTestAppId, adminDeleteAppTestAppId, publisherDeleteAppTestAppId;
    private APPMPublisherRestClient appmPublisherRestClient;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {

        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER,
                TestUserMode.SUPER_TENANT_ADMIN);

        appCreator = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.APP_CREATOR);
        adminUser = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.ADMIN);
        appProvider = appCreator.getUserName();

        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);

        //Login as AppCreator
        appmPublisherRestClient.login(appCreator.getUserName(), appCreator.getPassword());
        //Create test web applications
        creatorDeleteAppTestAppId = createWebApp(creatorDeleteAppTest);
        adminDeleteAppTestAppId = createWebApp(adminDeleteAppTest);
        publisherDeleteAppTestAppId = createWebApp(publisherDeleteAppTest);
        appmPublisherRestClient.logout();
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testCreatedWebAppDeleteWithValidUsers(String username, String password, String appId)
            throws Exception {
        APPMPublisherRestClient publisherRestClient = new APPMPublisherRestClient(backEndUrl);
        publisherRestClient.login(username, password);
        //delete web application
        HttpResponse appDeleteResponse = publisherRestClient.deleteApp(appId);
        publisherRestClient.logout();
        JSONObject jsonObject = new JSONObject(appDeleteResponse.getData());

        Assert.assertTrue((Boolean) jsonObject.get("isDeleted"), "Web App delete is not allowed for user :" + username +
                " who has sufficient privileges to delete.");
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testCreatedWebAppDeleteWithInValidUsers(String username, String password, String appId)
            throws Exception {
        APPMPublisherRestClient publisherRestClient = new APPMPublisherRestClient(backEndUrl);
        publisherRestClient.login(username, password);
        //delete weapp
        HttpResponse appDeleteResponse = publisherRestClient.deleteApp(appId);
        publisherRestClient.logout();
        JSONObject jsonObject = new JSONObject(appDeleteResponse.getData());

        Assert.assertTrue(!(Boolean) jsonObject.get("isDeleted"), "Web App delete is allowed for user :" + username +
                " who has insufficient privileges to delete.");
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        return new Object[][]{
                new Object[]{appCreator.getUserName(), appCreator.getPassword(), creatorDeleteAppTestAppId},
                new Object[]{adminUser.getUserName(), adminUser.getPassword(), adminDeleteAppTestAppId}
        };
    }

    @DataProvider
    public static Object[][] inValidUserModeDataProvider() throws Exception {
        User appPublisher = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.APP_PUBLISHER);
        return new Object[][]{
                new Object[]{appPublisher.getUserName(), appPublisher.getPassword(), publisherDeleteAppTestAppId}
        };
    }

    @DataProvider
    public static Object[][] tenantModeDataProvider() {
        return new Object[][]{
                new Object[]{TestUserMode.SUPER_TENANT_ADMIN},
                new Object[]{TestUserMode.TENANT_ADMIN},
        };
    }

    public String createWebApp(String appName) throws Exception {

        PolicyGroup defaultPolicyGroup = WebAppUtil.createDefaultPolicy();
        HttpResponse response = appmPublisherRestClient.addPolicyGroup(defaultPolicyGroup);
        String policyId = WebAppUtil.getPolicyId(response);
        List<WebAppResource> webAppResources = WebAppUtil.createDefaultResources(policyId);
        WebApp webApp = WebAppUtil.createBasicWebApp(appCreator.getUserName(), appName, contextPrefix + appName, appVersion,
                "http://wso2.com/", webAppResources);
        appmPublisherRestClient.createWebApp(webApp);
        return webApp.getAppId();
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        appmPublisherRestClient.login(adminUser.getUserName(), adminUser.getPassword());
        HttpResponse response = appmPublisherRestClient.deleteApp(publisherDeleteAppTestAppId);
        VerificationUtil.checkDeleteResponse(response);
        appmPublisherRestClient.logout();
    }

}
