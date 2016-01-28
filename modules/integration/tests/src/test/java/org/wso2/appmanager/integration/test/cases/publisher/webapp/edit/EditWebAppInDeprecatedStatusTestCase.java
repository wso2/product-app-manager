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
package org.wso2.appmanager.integration.test.cases.publisher.webapp.edit;

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


public class EditWebAppInDeprecatedStatusTestCase {
    private static final String TEST_DESCRIPTION = "Edit web app in deprecated status";
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "EditWebAppInDeprecatedStatusTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String backEndUrl;
    private static AutomationContext appMServer;
    private WebApp webApp;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER,
                TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        appmPublisherRestClient.login(appCreator.getUserName(), appCreator.getPassword());
        createWebApp(appCreator);
        appmPublisherRestClient.logout();
        User admin = appMServer.getSuperTenant().getTenantUser("AdminUser");
        appmPublisherRestClient.login(admin.getUserName(), admin.getPassword());
        changeLifeCycleStatus();
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testEditWebAppWithValidUsers(String userName, String password) throws Exception {
        APPMPublisherRestClient publisherRestClient = new APPMPublisherRestClient(backEndUrl);
        publisherRestClient.login(userName, password);
        //edit webapp
        boolean status = publisherRestClient.editWebApp(webApp);
        publisherRestClient.logout();
        Assert.assertTrue(status, "Web App edit failed for user :" + userName +
                " who has sufficient privileges to edit.");
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testEditWebAppWithInValidUsers(String userName, String password) throws Exception {
        APPMPublisherRestClient publisherRestClient = new APPMPublisherRestClient(backEndUrl);
        publisherRestClient.login(userName, password);
        //edit webapp
        HttpResponse response = publisherRestClient.editApp(webApp);
        publisherRestClient.logout();
        JSONObject jsonObject = new JSONObject(response.getData());
        Assert.assertTrue(!(Boolean) jsonObject.get("ok"), "Web App edit is allowed for user :" + userName +
                " who has insufficient privileges to edit.");
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        HttpResponse response = appmPublisherRestClient.deleteApp(webApp.getAppId());
        VerificationUtil.checkDeleteResponse(response);
        appmPublisherRestClient.logout();
    }


    private void createWebApp(User appCreator) throws Exception {
        PolicyGroup defaultPolicyGroup = WebAppUtil.createDefaultPolicy();
        HttpResponse response = appmPublisherRestClient.addPolicyGroup(defaultPolicyGroup);
        String policyId= WebAppUtil.getPolicyId(response);
        List<WebAppResource> webAppResources = WebAppUtil.createDefaultResources(policyId);
        webApp = WebAppUtil.createBasicWebApp(appCreator.getUserName(), appName, context, appVersion,
                "http://wso2.com/", webAppResources);
        appmPublisherRestClient.createWebApp(webApp);
    }

    private void changeLifeCycleStatus() throws Exception {
        String appId = webApp.getAppId();
        appmPublisherRestClient.changeState(appId, AppmTestConstants.LifeCycleStatus.SUBMIT_FOR_REVIEW);
        appmPublisherRestClient.changeState(appId, AppmTestConstants.LifeCycleStatus.APPROVE);
        appmPublisherRestClient.changeState(appId, AppmTestConstants.LifeCycleStatus.PUBLISH);
        appmPublisherRestClient.changeState(appId, AppmTestConstants.LifeCycleStatus.DEPRECATE);
    }

    @DataProvider
    public static Object[][] tenantModeDataProvider() {
        return new Object[][]{
                new Object[]{TestUserMode.SUPER_TENANT_ADMIN},
                new Object[]{TestUserMode.TENANT_ADMIN},
        };
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        User adminUser = appMServer.getSuperTenant().getTenantUser("AdminUser");
        return new Object[][]{
                new Object[]{adminUser.getUserName(), adminUser.getPassword()}
        };
    }

    @DataProvider
    public static Object[][] inValidUserModeDataProvider() throws Exception {
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        User appPublisher = appMServer.getSuperTenant().getTenantUser("AppPublisher");
        return new Object[][]{
                new Object[]{appCreator.getUserName(), appCreator.getPassword()},
                new Object[]{appPublisher.getUserName(), appPublisher.getPassword()}
        };
    }
}
