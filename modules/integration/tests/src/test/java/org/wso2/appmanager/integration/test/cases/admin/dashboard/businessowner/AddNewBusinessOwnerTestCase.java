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
 * Test case which verifies the ability of admin user of adding a new business owner.
 */
public class AddNewBusinessOwnerTestCase {
    private static final String TEST_DESCRIPTION = "Verify adding a new business owner";
    private static AutomationContext appMServerSuperTenant;
    private APPMAdminDashboardRestClient appmAdminDashboardRestClient;
    private String businessOwnerName = "AddNewBusinessOwnerTestCase_";


    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        appMServerSuperTenant = new AutomationContext(AppmTestConstants.APP_MANAGER, TestUserMode.SUPER_TENANT_ADMIN);
    }

    @Test(dataProvider = "validUserModeDataProvider",description = TEST_DESCRIPTION)
    public void testAddNewBusinessOwnerWithValidUsers(String userName, String password, AutomationContext
            automationContext) throws Exception {
        String backEndUrl = automationContext.getContextUrls().getWebAppURLHttps();
        appmAdminDashboardRestClient = new APPMAdminDashboardRestClient(backEndUrl);
        // login to admin dashboard rest client.
        appmAdminDashboardRestClient.login(userName, password);
        BusinessOwner businessOwner = new BusinessOwner();
        String businessOwnerUniqueName = businessOwnerName.concat(userName);
        businessOwner.setBusinessOwnerName(businessOwnerUniqueName);
        businessOwner.setBusinessOwnerEmail(businessOwnerUniqueName.concat("abc.com"));
        businessOwner.setBusinessOwnerSite(businessOwnerUniqueName.concat(".com"));
        businessOwner.setBusinessOwnerDescription("test description for ".concat(businessOwnerUniqueName));
        // add new business owner.
        HttpResponse addBusinessOwnerResponse =  appmAdminDashboardRestClient.addBusinessOwner(businessOwner);
        // logout from admin dashboard rest client.
        appmAdminDashboardRestClient.logout();

        JSONObject jsonObject = new JSONObject(addBusinessOwnerResponse.getData());
        Assert.assertTrue((Boolean) jsonObject.get(AppmTestConstants.SUCCESS), "Adding new business owner was not " +
                "successful  for user : " + userName + ".");

        JSONObject idJsonObject = (JSONObject) jsonObject.get(AppmTestConstants.RESPONSE);
        int businessOwnerId = (Integer) idJsonObject.get(AppmTestConstants.ID);
        Assert.assertNotEquals(businessOwnerId, 0 , "Adding new business owner was not  successful  for user : " +
                userName + ".");
    }

    @DataProvider
    public static Object[][] validUserModeDataProvider() throws Exception {
        User superTenantAdminUser = appMServerSuperTenant.getSuperTenant().getTenantUser(AppmTestConstants.TestUsers.ADMIN);
        return new Object[][]{
                new Object[]{superTenantAdminUser.getUserName(), superTenantAdminUser.getPassword(), appMServerSuperTenant},
        };
    }
}
