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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class WebAppPublishTestCase {

    private static final String TEST_DESCRIPTION = "Verify Publishing a Web App";
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "WebAppPublishTestCase";
    private String context = "/WebAppPublishTestCase";
    private String trackingCode = "AM_WebAppPublishTestCase";
    private String version = "1.0.0";
    private String backEndUrl;
    private User adminUser;


    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER,
                                                             TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        adminUser = appMServer.getSuperTenant().getTenantAdmin();
        appmPublisherRestClient.login(adminUser.getUserName(), adminUser.getPassword());

    }

    @Test(description = TEST_DESCRIPTION)
    public void testPublisherCreateWebApp() throws Exception {
        HttpResponse response = appmPublisherRestClient.webAppCreate(appName, context, version,
                                                                     trackingCode);

        JSONObject responseData = new JSONObject(response.getData());
        String uuid = responseData.getString(AppmTestConstants.ID);

        HttpResponse submitHttpResponse = appmPublisherRestClient.changeState(uuid,
                                                                              "Submit for Review");

        assertTrue(submitHttpResponse.getResponseCode() == 200, "Non 200 status code received.");
        JSONObject submittedResponseData = new JSONObject(submitHttpResponse.getData());
        assertEquals(submittedResponseData.getString(AppmTestConstants.STATUS) , "Success",
                     "Asset has not submitted for review successfully" );

        HttpResponse approvedHttpResponse = appmPublisherRestClient.changeState(uuid, "Approve");
        assertTrue(approvedHttpResponse.getResponseCode() == 200, "Non 200 status code received.");
        JSONObject approvedResponseData = new JSONObject(approvedHttpResponse.getData());
        assertEquals(approvedResponseData.getString(AppmTestConstants.STATUS) , "Success",
                     "Asset has not approved" );

        HttpResponse publishedHttpResponse = appmPublisherRestClient.changeState(uuid, "Publish");
        assertTrue(publishedHttpResponse.getResponseCode() == 200, "Non 200 status code received.");
        JSONObject publishedResponseData = new JSONObject(publishedHttpResponse.getData());
        assertEquals(publishedResponseData.getString(AppmTestConstants.STATUS) , "Success",
                     "Asset has not published" );
    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        appmPublisherRestClient.logout();
    }
}
