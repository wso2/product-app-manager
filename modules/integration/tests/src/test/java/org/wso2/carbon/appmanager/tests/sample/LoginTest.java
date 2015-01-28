/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.appmanager.tests.sample;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.tests.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.tests.util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.tests.util.APPMStoreRestClient;
import org.wso2.carbon.automation.core.utils.HttpResponse;


public class LoginTest extends APPManagerIntegrationTest {
    
    private APPMStoreRestClient apiStore;
    private APPMPublisherRestClient appmPublisher;
    private String storeURLHttp;
    private String publisherURLHttp;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        if (isBuilderEnabled()) {
            storeURLHttp = getServerURLHttp();
            publisherURLHttp = getServerURLHttp();

        } else {
            storeURLHttp = getStoreServerURLHttp();
        }
        
        apiStore = new APPMStoreRestClient(storeURLHttp);
        appmPublisher= new APPMPublisherRestClient(publisherURLHttp);
    }

    @Test(groups = {"wso2.appmanager"}, description = "Login Test")
    public void testLoginCycleITestCase() throws Exception {
       
        //login to publisher 
    	HttpResponse response = apiStore.login(userInfo.getUserName(), userInfo.getPassword());
        Assert.assertEquals(response.getResponseCode(), 200, "Response code mismatch");
        

    }
    
    @Test(groups = {"wso2.appmanager"}, description = "Login Test publisher")
    public void testLoginCyclePublisherITestCase() throws Exception {
       
        //login to publisher 
    	String responseMsg = appmPublisher.login(userInfo.getUserName(), userInfo.getPassword());
        Assert.assertEquals(responseMsg, "logged in", "Login failed to publisher");
        

    }
    

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
    }
}
