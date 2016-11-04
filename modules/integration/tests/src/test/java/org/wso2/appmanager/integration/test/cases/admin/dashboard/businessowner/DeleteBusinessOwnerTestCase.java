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
package org.wso2.appmanager.integration.test.cases.admin.dashboard.businessowner;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMAdminDashboardRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.bean.BusinessOwner;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

/**
 * Test case which verifies the ability of admin user of deleting business owner.
 */
public class DeleteBusinessOwnerTestCase {
    private static final String TEST_DESCRIPTION = "Verify deleting business owner.";
    private APPMAdminDashboardRestClient appmAdminDashboardRestClient;
    private String businessOwnerName = "DeleteBusinessOwnerTestCase_";
    private static AutomationContext appMServerSuperTenant;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServerSuperTenant = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
    }

    @Test(dataProvider = "validUserModeDataProvider", description = TEST_DESCRIPTION)
    public void testDeleteBusinessOwnerWithValidUsers(String userName, String password, AutomationContext
            automationContext) throws Exception {
        String backEndUrl = automationContext.getContextUrls().getWebAppURLHttps();
        appmAdminDashboardRestClient = new APPMAdminDashboardRestClient(backEndUrl);
        appmAdminDashboardRestClient.login(userName, password);

        int businessOwnerId = createBusinessOwner(appmAdminDashboardRestClient, userName);
        HttpResponse deleteBusinessOwnerResponse =  appmAdminDashboardRestClient.deleteBusinessOwner(businessOwnerId);
        // Logout from admin dashboard rest client.
        appmAdminDashboardRestClient.logout();

        JSONObject jsonObject = new JSONObject(deleteBusinessOwnerResponse.getData());
        Assert.assertTrue((Boolean) jsonObject.get(AppmTestConstants.SUCCESS), "Deleting business owner was not " +
                "successful for user : " + userName + ".");
    }

    private int createBusinessOwner(APPMAdminDashboardRestClient appmAdminDashboardRestClient, String userName) throws
                                                                                                       Exception {
        BusinessOwner businessOwner = new BusinessOwner();
        String businessOwnerUniqueName = businessOwnerName.concat(userName);
        businessOwner.setBusinessOwnerName(businessOwnerUniqueName);
        businessOwner.setBusinessOwnerEmail(businessOwnerUniqueName.concat("@abc.com"));
        businessOwner.setBusinessOwnerSite(businessOwnerUniqueName.concat(".com"));
        appmAdminDashboardRestClient.addBusinessOwner(businessOwner);
        return businessOwner.getBusinessOwnerId();
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        User superTenantAdminUser = appMServerSuperTenant.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.ADMIN);
        return new Object[][]{
                new Object[]{superTenantAdminUser.getUserName(), superTenantAdminUser.getPassword(), appMServerSuperTenant},
        };
    }
}