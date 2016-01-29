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

package org.wso2.appmanager.integration.test.cases.publisher.webapp.lifecycle.management;

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.integration.utils.APPMPublisherRestClient;
import org.wso2.appmanager.integration.utils.AppmTestConstants;
import org.wso2.appmanager.integration.utils.VerificationUtil;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RecycleWebAppTestCase {

    private static final String TEST_DESCRIPTION = "Verify recycling an unpublished web app";
    private APPMPublisherRestClient appmPublisherRestClient;
    private String appName = "RecycleWebAppTestCase";
    private String appVersion = "1.0.0";
    private String context = "/" + appName;
    private String trackingCode = "AM_" + appName;
    private String backEndUrl;
    private String appCreatorUserName;
    private String appCreatorPassword;
    private String uuid;


    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        AutomationContext appMServer = new AutomationContext(AppmTestConstants.APP_MANAGER,
                                                             TestUserMode.SUPER_TENANT_ADMIN);
        backEndUrl = appMServer.getContextUrls().getWebAppURLHttps();
        appmPublisherRestClient = new APPMPublisherRestClient(backEndUrl);
        User appCreator = appMServer.getSuperTenant().getTenantUser("AppCreator");
        appCreatorUserName = appCreator.getUserName();
        appCreatorPassword = appCreator.getPassword();
        appmPublisherRestClient.login(appCreator.getUserName(), appCreator.getPassword());

        // Web app is created by AppCreator user.
        HttpResponse appCreateResponse = appmPublisherRestClient.webAppCreate(appName, context,
                                                                              appVersion,
                                                                              trackingCode,
                                                                              appCreatorUserName);
        JSONObject appCreateResponseData = new JSONObject(appCreateResponse.getData());
        uuid = appCreateResponseData.getString(AppmTestConstants.ID);
        appmPublisherRestClient.changeState(uuid,
                                            AppmTestConstants.LifeCycleStatus.SUBMIT_FOR_REVIEW);
        appmPublisherRestClient.logout();

        User appPublisher = appMServer.getSuperTenant().getTenantUser("AppPublisher");
        appmPublisherRestClient.login(appPublisher.getUserName(), appPublisher.getPassword());

        // Approving and publishing the web app.
        appmPublisherRestClient.approveAndPublishWebApp(uuid);

        // Unpublishing the app.
        appmPublisherRestClient.changeState(uuid,
                                            AppmTestConstants.LifeCycleStatus.UNPUBLISH);
    }

    @Test(description = TEST_DESCRIPTION)
    public void testRecycleWebApp() throws Exception {
        //Recycle the app.
        HttpResponse httpResponse =
               appmPublisherRestClient.changeState(uuid, AppmTestConstants.LifeCycleStatus.RECYCLE);

        int httpResponseCode = httpResponse.getResponseCode();
        assertTrue(httpResponseCode == 200, httpResponseCode + " status code received.");
        JSONObject responseData = new JSONObject(httpResponse.getData());
        assertEquals(responseData.getString(AppmTestConstants.STATUS), "Success",
                     "Asset has not recycled successfully.");
    }
    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        appmPublisherRestClient.logout();

        // Deleted the web app by AppCreator.
        appmPublisherRestClient.login(appCreatorUserName, appCreatorPassword);
        HttpResponse response = appmPublisherRestClient.deleteApp(uuid);
        VerificationUtil.checkDeleteResponse(response);
        appmPublisherRestClient.logout();
    }
}



