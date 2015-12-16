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
*KIND, either express or implied. See the License for the
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
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.Tenant;
import org.wso2.carbon.appmanager.integration.ui.Util.TenantPopulator;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.api.clients.registry.ResourceAdminServiceClient;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.wso2.carbon.automation.core.utils.serverutils.ServerConfigurationManager;

import javax.activation.DataHandler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * This test class contains the test suite for tenanted self sign-up feature testing
 */
public class TenantSelfSighUpTestCase extends APPManagerIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceStub;
    private final static String TENANT_DOMAIN = "wso2.com";
    private final static String ROOT_REG_PATH = "/_system/governance/appmgt/";
    private String username;
    private String password;
    private String tenantUsername;
    private String tenantUserPassword;
    private String tenantAdminName;
    private String tenantAdminPassWord;
    private APPMStoreRestClient appMStore;
    private TenantPopulator tenantPopulator;
    private ApplicationInitializingUtil baseUtil;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        tenantPopulator = new TenantPopulator(super.getServerURLHttps(), username, password);
        tenantUsername = "storeUser" + "@" + TENANT_DOMAIN;
        tenantUserPassword = "storeUser123";
        tenantAdminName = "admin";
        tenantAdminPassWord = "admin123";
        createNewTenant();
        loadTenantedSignUpConfiguration();
        super.init(0); // server restart happens in addExternalStoreConfiguration so need to init again
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        appMStore = new APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
    }

    @Test(groups = {"wso2.appmanager.tenantSelfSignUpTestCase"}, description = "Self sign-up as a tenant user and " +
            "try to login to App store via created tenant user credentials")
    public void testTenantSignUp() throws Exception {

        //self sign-up a tenant user
        HttpResponse selfSignupResponse = appMStore.selfSignUp(tenantUsername, tenantUserPassword);
        JSONObject selfSignupResponseObj = new JSONObject(selfSignupResponse.getData());
        assertFalse((Boolean) selfSignupResponseObj.get("error"), "Error occurred during tenant user: " +
                tenantUsername + " self sign-up");
        assertTrue(("User "+tenantUsername+" has been successfully added").equals(selfSignupResponseObj.get("message")),
                "Tenant user : "+tenantUsername+ "has not been successfully created");

        //login with the registered user credentials
        HttpResponse loginResponse = appMStore.login(tenantUsername, tenantUserPassword);
        JSONObject loginResponseObj = new JSONObject(loginResponse.getData());
        assertFalse((Boolean) loginResponseObj.get("error"), "Error occurred during tenant user: " +
                tenantUsername + " login.");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        tenantPopulator = new TenantPopulator(super.getServerURLHttps(), username, password);
        //Deactivate all the tenants available
        tenantPopulator.deactivateAllTenants();
        appMStore.logout();
        super.cleanup();
        baseUtil.destroy();
    }

    /**
     * This method is to load sign-up configuration into tenant registry
     *
     * @throws Exception
     */
    private void loadTenantedSignUpConfiguration() throws Exception {
        String externalStoreConfig = "sign-up-config.xml";
        String renamedExternalStoreCofing = "sign-up-config.xml-default.xml";
        String externalStoreConfigPath = ROOT_REG_PATH + "applicationdata/";

        // rename the default sign-up-config.xml to sign-up-config.xml-default.xml
        // then add the new sign-up-config.xml from configFiles/sign-up-config.xml to registry
        resourceAdminServiceStub = new ResourceAdminServiceClient(amServer.getBackEndUrl(),
                tenantAdminName + "@" + TENANT_DOMAIN, tenantAdminPassWord);
        String resourcePath = externalStoreConfigPath + externalStoreConfig;
        resourceAdminServiceStub.renameResource(externalStoreConfigPath, resourcePath, renamedExternalStoreCofing);

        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = "artifacts/AM/configFiles/signupconfigurations/" + externalStoreConfig;
        File file = new File(classLoader.getResource(filePath).getFile());
        DataHandler dh = new DataHandler(file.toURI().toURL());
        resourceAdminServiceStub.addResource(resourcePath, "application/xml", "xml files", dh);
        // restart the server to apply the changes(due to caching)
        ServerConfigurationManager serverConfigurationManager = new
                ServerConfigurationManager(amServer.getBackEndUrl());
        serverConfigurationManager.restartGracefully();
    }

    /**
     * Create tenant which has been configured in sign-up
     *
     * @throws Exception
     */
    private void createNewTenant() throws Exception {
        List<Tenant> tenants = new ArrayList<Tenant>();
        Tenant tenant = new Tenant(TENANT_DOMAIN, tenantAdminName, tenantAdminPassWord);
        tenants.add(tenant);
        tenantPopulator.populateTenants(tenants);
    }

}
