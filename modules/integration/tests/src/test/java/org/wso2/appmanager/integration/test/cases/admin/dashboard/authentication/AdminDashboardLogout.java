package org.wso2.appmanager.integration.test.cases.admin.dashboard.authentication;

import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMAdminDashboardRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Test case which verifies the ability of admin user of logout from admin dashboard.
 */
public class AdminDashboardLogout {
    private static final String TEST_DESCRIPTION = "Verify Admin Dashboard Logout";
    private APPMAdminDashboardRestClient appmAdminDashboardRestClient;
    private static AutomationContext appMServer;
    private String backEndUrl;
    private String userName;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        User adminUser = appMServer.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.ADMIN);

        appmAdminDashboardRestClient = new APPMAdminDashboardRestClient(backEndUrl);
        userName = adminUser.getUserName();
        String password = adminUser.getPassword();
        appmAdminDashboardRestClient.login(userName, password);
    }

    @Test(description = TEST_DESCRIPTION)
    public void testAdminDashboardLogin() throws Exception {
        HttpResponse logoutResponseData = appmAdminDashboardRestClient.logout();
        JSONObject publisherLogoutJsonObject = new JSONObject(logoutResponseData.getData());
        String dataResponse = publisherLogoutJsonObject.getString(AppmTestConstants.ERROR);
        assertTrue(dataResponse == "false", "Logout from Admin dashboard is not allowed for user : " + userName +
                "who has enough privileges.");
        String session = logoutResponseData.getHeaders().get(AppmTestConstants.SET_COOKIE);
        assertNull(session, "Session is not null");
    }

}
