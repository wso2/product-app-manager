/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appmanager.integration.test.cases.publisher.webapp.lifecycle.management;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.WebAppUtil;
import org.wso2.appmanager.integration.utils.bean.PolicyGroup;
import org.wso2.appmanager.integration.utils.bean.WebApp;
import org.wso2.appmanager.integration.utils.bean.WebAppResource;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This Test class verifies the ability of appCreator, appPublisher and admin users of creating a new web app.
 */
public class CreateWebAppTestCase {

    private static final String TEST_DESCRIPTION = "Verify Creating a Web App";
    private AutomationContext appMServer = null;
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "CreateWebAppTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String backEndUrl;
    private String adminUserName;
    private String adminPassword;
    private List<String> appIdList = new ArrayList<String>(); //This list contains Ids of all the created web apps.

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);

        User adminUser = appMServer.getSuperTenant().getTenantAdmin();
        adminUserName = adminUser.getUserName();
        adminPassword = adminUser.getPassword();
        //Login to publisher by admin.
        appmPublisherRestClient.login(adminUserName, adminPassword);
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testCreateWebAppWithValidUsers(User user, String prefix) throws Exception {
        APPMPublisherRestClient publisherRestClient = new APPMPublisherRestClient(backEndUrl);
        // Login to publisher by valid user.
        String userName = user.getUserName();
        publisherRestClient.login(userName, user.getPassword());
        WebApp webApp = createWebAppObject(publisherRestClient, user, prefix);
        HttpResponse webAppAddedResponse = publisherRestClient.addWebApp(webApp);
        JSONObject webAppAddedResponseData = new JSONObject(webAppAddedResponse.getData());
        //Test whether app is created successfully.
        assertEquals(webAppAddedResponseData.getString(AppmTestConstants.OK), "true", "Creating web application is " +
                " not allowed for user : " + userName + " who has sufficient privileges to create web app.");
        assertEquals(webAppAddedResponseData.getString(AppmTestConstants.MESSAGE), "asset added", "Creating web " +
                "app is not allowed for user : " + userName + " who has sufficient privileges to create web app.");
        String appId = webAppAddedResponseData.getString(AppmTestConstants.ID);
        assertNotNull(appId, "user " + userName + " who has sufficient privileges to create web app, failed to create" +
                " web app");

        addTagsAndRoles(publisherRestClient, webApp);
        //Add a service provider to the created web app.
        HttpResponse ssoProviderAddedResponse = publisherRestClient.addSsoProvider(webApp);
        int responseCode = ssoProviderAddedResponse.getResponseCode();
        assertTrue(responseCode == 200, "Excepted status code is 200 for user :" + userName + ". But received status " +
                "code is responseCode");
        //Logout from publisher by valid user.
        publisherRestClient.logout();
        appIdList.add(appId);
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testCreateWebAppWithInValidUsers(User user, String prefix) throws Exception {
        APPMPublisherRestClient publisherRestClient = new APPMPublisherRestClient(backEndUrl);
        // Login to publisher by invalid user.
        String userName = user.getUserName();
        publisherRestClient.login(userName, user.getPassword());
        WebApp webApp = createWebAppObject(publisherRestClient, user, prefix);
        HttpResponse webAppAddedResponse = publisherRestClient.addWebApp(webApp);
        JSONObject webAppAddedResponseData = new JSONObject(webAppAddedResponse.getData());
        //Logout from publisher by invalid user.
        publisherRestClient.logout();
        assertEquals(webAppAddedResponseData.getString(AppmTestConstants.OK), "false", "Creating web application is " +
                "allowed for user : " + userName + " who has insufficient privileges to create web app.");
        assertNotEquals(webAppAddedResponseData.getString(AppmTestConstants.MESSAGE), "asset added", "Creating web " +
                "app is allowed for user : " + userName + " who has insufficient privileges to create web app.");
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        //delete all created apps by admin.
        for (int i = 0; i < appIdList.size(); i++) {
            appmPublisherRestClient.deleteApp(appIdList.get(i));
        }
        //Logout from publisher by admin.
        appmPublisherRestClient.logout();
    }

    private WebApp createWebAppObject(APPMPublisherRestClient publisherRestClient, User user, String appPrefix)
            throws Exception {
        PolicyGroup defaultPolicyGroup = WebAppUtil.createDefaultPolicy();
        HttpResponse response = publisherRestClient.addPolicyGroup(defaultPolicyGroup);
        String policyId = WebAppUtil.getPolicyId(response);
        List<WebAppResource> webAppResources = WebAppUtil.createDefaultResources(policyId);
        WebApp webApp = WebAppUtil.createBasicWebApp(user.getUserName(), appName + appPrefix, context + appPrefix,
                                                     appVersion, "http://wso2.com/", webAppResources);
        return webApp;
    }

    private void addTagsAndRoles(APPMPublisherRestClient publisherRestClient, WebApp webApp) throws Exception {
        String appId = webApp.getAppId();
        webApp.setAppId(appId);
        List<String> roles = webApp.getRoles();
        if (roles != null && roles.size() > 0) {
            publisherRestClient.addRoles(roles, appId);
        }
        List<String> tags = webApp.getTags();
        if (tags != null && tags.size() > 0) {
            publisherRestClient.addNewTags(tags, appId);
        }
    }

    @DataProvider
    public Object[][] validUserModeDataProvider() throws Exception {
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        User adminUser = appMServer.getSuperTenant().getTenantUser("AdminUser");
        return new Object[][]{
                new Object[]{appCreator, "1"},
                new Object[]{adminUser, "2"}
        };
    }

    @DataProvider
    public Object[][] inValidUserModeDataProvider() throws Exception {
        User appPublisher = appMServer.getSuperTenant().getTenantUser("AppPublisher");
        return new Object[][]{
                new Object[]{appPublisher, "3"}
        };
    }
}