/*
 *
 *   Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.UserInfo;
import org.wso2.carbon.automation.core.utils.UserListCsvReader;

public class RatingTestCase extends APPManagerIntegrationTest {

    private String appType;
    private APPMStoreRestClient appMStore;
    protected UserInfo userInfo1;
    protected UserInfo userInfo2;
    private ApplicationInitializingUtil baseUtil;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        baseUtil.createWebApplicationWithExistingUser("15");
        baseUtil.testWebApplicationPublish();
        baseUtil.testApplicationSubscription();
        appType = "webapp";
        userInfo1 = UserListCsvReader.getUserInfo(1);
        userInfo2 = UserListCsvReader.getUserInfo(2);
        appMStore = new org.wso2.carbon.appmanager.integration.ui.Util.
                APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
    }

    @Test(groups = {"wso2.appmanager.ratings"}, description = "Test web application ratings")
    public void testWebAppRating() throws Exception {

        int ratingValue1 = 3;
        int ratingValue2 = 5;
        appMStore.login(userInfo1.getUserName(), userInfo1.getPassword());

        HttpResponse response1 = appMStore.rateApplication(ApplicationInitializingUtil.appId, appType, ratingValue1);
        JSONObject responseData1 = new JSONObject(response1.getData());
        Assert.assertEquals(response1.getResponseCode(), 200, "Response code mismatch");
        Assert.assertEquals(responseData1.getDouble("user"), 3.0,
                "Error while rating the web application as user : " + userInfo1.getUserName());
        Assert.assertEquals(responseData1.getDouble("average"), 3.0,
                "Error while retrieving the average rating for user : " + userInfo1.getUserName());
        appMStore.logout();
        appMStore.login(userInfo2.getUserName(), userInfo2.getPassword());
        HttpResponse response2 = appMStore.rateApplication(ApplicationInitializingUtil.appId, appType, ratingValue2);
        Assert.assertEquals(response1.getResponseCode(), 200, "Response code mismatch");
        JSONObject responseData2 = new JSONObject(response2.getData());
        Assert.assertEquals(responseData2.getDouble("user"), 5.0,
                "Error while rating the web application as user : " + userInfo2.getUserName());
        Assert.assertEquals(responseData2.getDouble("average"), 4.0,
                "Error while retrieving the average rating for user : " + userInfo2.getUserName());

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        baseUtil.destroy();
    }

}
