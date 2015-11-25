/*
*Copyright (c) 2005-2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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


package org.wso2.carbon.appmanager.integration.ui.TestCases;

import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;


import static org.testng.Assert.assertTrue;

public class TagsTestCase extends APPManagerIntegrationTest {

    private String username;
    private String password;
    private APPMStoreRestClient appMStore;
    private ApplicationInitializingUtil baseUtil;
    private String tag = "testTag";

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        //create a webapp which is restricted to role -internal/reviewer
        baseUtil.testApplicationCreation("tags", "internal/reviewer",tag);
        baseUtil.testApplicationPublish();
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        appMStore = new APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
    }

    @Test(groups = {"wso2.appmanager.tags"}, description = "Check whether tags of" +
            "role(internal/reviewer) restricted web app is visible to anonymous user")
    public void testTagsForAnonymousUser() throws Exception {

        HttpResponse response = appMStore.getVisbileTagsForAnonymousUser();
        JSONArray jsonArray = new JSONArray(response.getData());
        boolean tagExist = false;
        JSONObject jObj = null;
        String tempTag = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            jObj = jsonArray.getJSONObject(i);
            tempTag = jObj.getString("name");
            if (tag.equals(tempTag)) {
                tagExist = true;
            }

        }

        assertTrue(!tagExist,
                "Tag of role restricted web app is visible to anonymous user");

    }

    @Test(groups = {"wso2.appmanager.tags"}, description = "Check whether tags of role restricted" +
            "web app is visible to user with correct role")
    public void testTagsForRoleAssignedUser() throws Exception {

        appMStore.login(username, password);
        HttpResponse response = appMStore.getAllTags();
        JSONArray jsonArray = new JSONArray(response.getData());
        appMStore.logout();
        assertTrue(jsonArray.length() > 0,
                "Tags of role restricted web app is not visible to user with correct role");
        //check whether created tags are visible
        boolean tagExist = false;
        JSONObject jObj = null;
        String tempTag = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            jObj = jsonArray.getJSONObject(i);
            tempTag = jObj.getString("name");
            if (tag.equals(tempTag)) {
                tagExist = true;
            }
        }

        assertTrue(tagExist,
                "Tag of role restricted web app is not visible to  user with correct role");

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        baseUtil.destroy();
    }
}
