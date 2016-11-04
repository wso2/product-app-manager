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
package org.wso2.appmanager.integration.test.cases.admin.dashboard.authentication;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMAdminDashboardRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * Test case which verifies the ability of appCreator, appPublisher and admin users of logging to admin dashboard.
 */
public class AdminDashboardLogin {
    private static final String TEST_DESCRIPTION = "Verify login to Admin Dashboard";
    private String backEndUrl;
    private static AutomationContext appMServer;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testAdminDashboardLoginWithValidUsers(String userName, String password) throws Exception {
        APPMAdminDashboardRestClient appmAdminDashboardRestClient = new APPMAdminDashboardRestClient(backEndUrl);
        HttpResponse response = appmAdminDashboardRestClient.login(userName, password);
        JSONObject adminDashboardLoginJsonObject = new JSONObject(response.getData());
        String dataResponse = adminDashboardLoginJsonObject.getString(AppmTestConstants.ERROR);
        assertTrue(AppmTestConstants.FALSE.equals(dataResponse), "Login to Admin dashboard is not allowed for user : " +
                userName + " who has enough privileges to login.");
        String session = response.getHeaders().get(AppmTestConstants.SET_COOKIE);
        assertNotNull(session, "Session is null");
        appmAdminDashboardRestClient.logout();
    }

    @Test(dataProvider = "inValidUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testAdminDashboardLoginWithInValidUsers(String userName, String password) throws Exception {
        APPMAdminDashboardRestClient appmAdminDashboardRestClient = new APPMAdminDashboardRestClient(backEndUrl);
        HttpResponse response = appmAdminDashboardRestClient.login(userName, password);
        JSONObject adminDashboardLoginJsonObject = new JSONObject(response.getData());
        String dataResponse = adminDashboardLoginJsonObject.getString(AppmTestConstants.ERROR);
        assertTrue(AppmTestConstants.TRUE.equals(dataResponse), "Login to Admin dashboard is allowed for user : " +
                userName + " who hasn't sufficient privileges to login.");
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        User adminUser = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.ADMIN);
        return new Object[][]{
                new Object[]{adminUser.getUserName(), adminUser.getPassword()}
        };
    }

    @DataProvider
    public static Object[][] inValidUserModeDataProvider() throws Exception {
        User appCreator = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.APP_CREATOR);
        User appPublisher = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.APP_PUBLISHER);
        return new Object[][]{
                new Object[]{appCreator.getUserName(), appCreator.getPassword()},
                new Object[]{appPublisher.getUserName(), appPublisher.getPassword()}
        };
    }
}