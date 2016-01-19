/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.appmanager.ui.integration.test.cases;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.appmanager.ui.integration.test.pages.LoginPage;
import org.wso2.appmanager.ui.integration.test.pages.StoreHomePage;
import org.wso2.appmanager.ui.integration.test.utils.AppManagerIntegrationTest;


public class StoreLoginTestCase extends AppManagerIntegrationTest {

    private static final String TEST_DESCRIPTION = "Verify login to App Manager Store";
    private static final Log log = LogFactory.getLog(PublisherLoginTestCase.class);

    private StoreHomePage homePage;

    @BeforeClass(alwaysRun = true)
    public void startUp() throws Exception {
        super.init();
    }

    @Test(groups = TEST_GROUP, description = TEST_DESCRIPTION)
    public void testStoreLogin() throws Exception {
        //login to store
        homePage = (StoreHomePage) login(driver, LoginPage.LoginTo.STORE);
        new WebDriverWait(driver, 90).until(ExpectedConditions.titleIs("WSO2 App Manager"));

    }

    @AfterClass(alwaysRun = true)
    public void closeDown() throws Exception {
        closeDriver(driver);
    }
}
