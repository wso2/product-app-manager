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

import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.appmanager.integration.ui.Util.service.AuthenticationAdmin;
import org.wso2.carbon.appmanager.integration.ui.Util.service.TenantMgtAdmin;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.tenant.mgt.stub.beans.xsd.TenantInfoBean;

import java.util.logging.Logger;

import static org.testng.Assert.assertTrue;

/**
 * This class is use to test multi tenanat enviornment in appm
 */
public class MultiTenantTestCase extends APPManagerIntegrationTest {

    private static final String APP_SERVER_URL = "https://localhost:9443/services/";
    public static String appId;
    public static String version;
    public static String appName;
    public static String appDisplayName;
    public static String appURL;
    public static String transport;
    public static String anonymousAccessToUrlPattern;
    public static String policyGroupName;
    public static String throttlingTier;
    public static String objPartialMappings;
    public static String policyGroupDesc;
    private static String username;
    private static String password;
    private final String TENANT_USERNAME = "user@sample.com";
    private final String TENANT_PASSWORD = "user12345";
    private ApplicationInitializingUtil baseUtil;
    private Logger log = Logger.getLogger("MultiTenantTestCase");
    private APPMPublisherRestClient appMPublisher;


    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        //create application in super tenant mode
        baseUtil.createWebApplicationWithGivenUser("SuperTenanat", username, username);
        String cookie = AuthenticationAdmin.getCookie(APP_SERVER_URL);
        //create new tenant user
        TenantInfoBean tenantInfoBean = new TenantInfoBean();
        tenantInfoBean.setActive(true);
        tenantInfoBean.setAdmin("user");
        tenantInfoBean.setAdminPassword(TENANT_PASSWORD);
        tenantInfoBean.setEmail("user@wso2.com");
        tenantInfoBean.setFirstname("user");
        tenantInfoBean.setLastname("user");
        tenantInfoBean.setTenantDomain("sample.com");
        TenantMgtAdmin.addTenant(cookie, APP_SERVER_URL, tenantInfoBean);
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);

    }

    /**
     * Tests whether appm support for multi tenanat environment
     *
     * @throws Exception
     */
    @Test(groups = {"wso2.appmanager.multitenant"}, description = "Multi tenant test")
    public void testMultiTenanat() throws Exception {
        appMPublisher.login(username, password);
        baseUtil.init();
        appId = ApplicationInitializingUtil.appId;
        appMPublisher.deleteApp(appId);
        appMPublisher.logout();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        appMPublisher.login(TENANT_USERNAME, TENANT_PASSWORD);
        boolean isMultitenantSupport = false;
        try {
            appMPublisher.getCurrentState(appId, "webapp");
        } catch (Exception ex) {
            isMultitenantSupport = true;
        }
        assertTrue(isMultitenantSupport, "Multi tenant support not availabled");
        //create application in normal tenant mode
        HttpResponse appCreateResponse = baseUtil.createWebApplicationWithGivenUser("MultiTenanat", TENANT_USERNAME, TENANT_PASSWORD);
        JSONObject jsonObject = new JSONObject(appCreateResponse.getData());
        appId = (String) jsonObject.get("id");
        isMultitenantSupport = true;
        try {
            appMPublisher.getCurrentState(appId, "webapp");
        } catch (Exception ex) {
            isMultitenantSupport = false;
        }
        assertTrue(isMultitenantSupport, "Multi tenant support not availabled");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        super.cleanup();
        baseUtil.destroy();
        System.gc();
    }

}
