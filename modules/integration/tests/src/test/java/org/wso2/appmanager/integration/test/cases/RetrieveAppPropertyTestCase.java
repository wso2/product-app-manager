/*
*Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.integration.test.cases;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import static org.testng.Assert.assertTrue;

public class RetrieveAppPropertyTestCase {
    private static final String TEST_DESCRIPTION = "Verify Retrieving Web Apps Properties";
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "RetrieveAppPropertyTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String trackingCode = "AM_" + appName;
    private User adminUser;
    private String userName;
    private String password;
    private String backEndUrl;


    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER,
                                                             TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);

        adminUser = appMServer.getSuperTenant().getTenantAdmin();
        userName = adminUser.getUserName();
        password = adminUser.getPassword();

        appmPublisherRestClient.login(userName, password);
    }

    @Test(description = TEST_DESCRIPTION)
    public void testAppPropertyRetrieval() throws Exception {
        HttpResponse response = appmPublisherRestClient.webAppCreate(appName, context, appVersion,
                                                                     trackingCode, userName);
        JSONObject responseData = new JSONObject(response.getData());
        String uuid = responseData.getString(AppmTestConstants.ID);
        String appType = AppmTestConstants.WEB_APP;
        appmPublisherRestClient.publishWebApp(uuid);

        HttpResponse appPropertyResponse = appmPublisherRestClient.getWebAppProperty(uuid);
        JSONObject jsonObject = new JSONObject(appPropertyResponse.getData());

        //Check App Id
        String appId = (String) jsonObject.get(AppmTestConstants.ID);
        assertTrue((appId.equals(uuid) == true), "Unable to Retrieve application id.");

        //Check App Type
        String type = (String) jsonObject.get(AppmTestConstants.TYPE);
        assertTrue((type.equals(appType) == true), "Unable to Retrieve application type.");

        //Check lifecycleState
        String lifecycleState = (String) jsonObject.get(AppmTestConstants.LIFE_CYCLE_STATE);
        assertTrue((lifecycleState.equalsIgnoreCase(AppmTestConstants.PUBLISHED) == true),
                   "Unable to Retrieve application life cycle.");

        //Check Path attribute
        String appPropertyString = userName + "/" + appName + "/" + appVersion + "/" + appType;
        String path = (String) jsonObject.get("path");
        assertTrue(path.endsWith(appPropertyString), "Unable to Retrieve application path.");
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        appmPublisherRestClient.logout();
    }
}
