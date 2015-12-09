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


import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.appmanager.integration.ui.APPManagerIntegrationTest;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMPublisherRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.APPMStoreRestClient;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.AppCreateRequest;
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

import static org.testng.Assert.assertTrue;

/**
 * This class contain test suites for publish to external tenant store feature.
 * Test Flow,
 * 1. In the init() -> create tenants, Add the external store configuration to registry, create webapp in super tenant.
 * 2. getExternalStores() -> Retrieve the external stores configured in the registry
 * 3. pushToExternalStores -> publish the web app to external store
 * 4. removeFromExternalStores -> remove the published webapp from external store
 */
public class publishToExternalStoreTestCase extends APPManagerIntegrationTest {
    private String username;
    private String password;
    private String appName;
    private String version;
    private APPMPublisherRestClient appMPublisher;
    private ApplicationInitializingUtil baseUtil;
    private static String appPrefix = "externalStore";
    private ResourceAdminServiceClient resourceAdminServiceStub;
    private final static String ROOT_REG_PATH = "/_system/governance/appmgt/";
    private final static String EXTERNAL_STORE_NAME = "Store1";
    private TenantPopulator tenantPopulator;
    private String webAppId;

    @BeforeClass(alwaysRun = true)
    public void init() throws Exception {
        super.init(0);
        addExternalStoreConfiguration();
        super.init(0); // server restart happens in addExternalStoreConfiguration so need to init again
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        tenantPopulator = new TenantPopulator(super.getServerURLHttps(), username, password);
        populateTenants();

        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        webAppId = createWebAPP(appPrefix);

        appName = appProp.getAppName() + appPrefix;
        version = appProp.getVersion();
    }

    @Test(groups = {"wso2.appmanager.publishToExternalStoreTestCase"}, description = "Get the external store details" +
            " for given webapp which are configured in registry")
    public void getExternalStores() throws Exception {
        HttpResponse response = appMPublisher.getExternalStores(appName, username, version);
        JSONObject responseJson = new JSONObject(response.getData());
        JSONObject jsonObject = new JSONObject(responseJson.toString());
        JSONArray jsonStoreArray = (JSONArray) jsonObject.get("appStores");
        assertTrue(jsonStoreArray.length() > 0, "Unable get external store details");
    }

    @Test(groups = {"wso2.appmanager.publishToExternalStoreTestCase"}, description = "Push webapp app to " +
            "external store")
    public void pushToExternalStores() throws Exception {
        List<String> stores = new ArrayList<String>();
        stores.add(EXTERNAL_STORE_NAME);
        boolean success = appMPublisher.updateExternalStores(appName, username, version, stores);
        assertTrue(success == true, "Unable to publish web app to external store");
    }

    @Test(groups = {"wso2.appmanager.publishToExternalStoreTestCase"}, dependsOnMethods = {"pushToExternalStores"},
            description = "Remove web app pushed in the pushToExternalStores" +
                    " test suite from external store")
    public void removeFromExternalStores() throws Exception {
        List<String> stores = new ArrayList<String>();
        boolean success = appMPublisher.updateExternalStores(appName, username, version, stores);
        assertTrue(success == true, "Unable to delete web app from external store");
    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        tenantPopulator.deactivateAllTenants();
        appMPublisher.deleteApp(webAppId);
        appMPublisher.restLogout();
        super.cleanup();
        baseUtil.destroy();
    }

    /**
     * Add the external store configuration to registry
     * 1. rename the default external-app-stores.xml to external-app-stores_default.xml
     * 2. then add the new external-app-stores.xml from
     * artifacts/AM/configFiles/externalstoretest/externalstores/external-app-stores.xml to registry
     * 3. restart the server gracefully to avoid issues in test due to registry caching.
     *
     * @throws Exception
     */
    private void addExternalStoreConfiguration() throws Exception {
        String externalStoreConfig = "external-app-stores.xml";
        String renamedExternalStoreCofing = "external-app-stores_default.xml";
        String externalStoreConfigPath = ROOT_REG_PATH + "externalstores/";

        // rename the default external-app-stores.xml to external-app-stores_default.xml
        // then add the new external-app-stores.xml from configFiles/external-app-stores.xml to registry
        resourceAdminServiceStub =
                new ResourceAdminServiceClient(amServer.getBackEndUrl(), amServer.getSessionCookie());
        String resourcePath = externalStoreConfigPath + externalStoreConfig;
        resourceAdminServiceStub.renameResource(externalStoreConfigPath, resourcePath, renamedExternalStoreCofing);

        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = "artifacts/AM/configFiles/externalstoretest/" + externalStoreConfig;
        File file = new File(classLoader.getResource(filePath).getFile());
        DataHandler dh = new DataHandler(file.toURI().toURL());
        resourceAdminServiceStub.addResource(resourcePath, "application/xml", "xml files", dh);
        // restart the server to apply the changes(due to caching)
        ServerConfigurationManager serverConfigurationManager = new
                ServerConfigurationManager(amServer.getBackEndUrl());
        serverConfigurationManager.restartGracefully();
    }

    /**
     * Create tenant which are used in artifacts/AM/configFiles/externalstoretest/externalstores/external-app-stores.xml
     *
     * @throws Exception
     */
    private void populateTenants() throws Exception {
        List<Tenant> tenants = new ArrayList<Tenant>();
        Tenant tenant = new Tenant("eng.com", "admin", "admin123");
        tenants.add(tenant);
        tenantPopulator.populateTenants(tenants);
    }

    /**
     * Create webapp using publisher rest apis.
     *
     * @param appPrefix
     * @return
     * @throws Exception
     */
    private String createWebAPP(String appPrefix) throws Exception {
        appMPublisher.restLogin(username, password);
        AppCreateRequest appCreateRequest = baseUtil.createBasicAppRequest(appPrefix, appMPublisher);
        String uuid = baseUtil.createWebApp(appCreateRequest, appMPublisher);
        baseUtil.publishWebApp(uuid, appMPublisher);
        return uuid;
    }

    private boolean verifyStorePublishedStatus(String store, String jsonString, boolean flag)
            throws Exception {
        //e.g json string - {"error" : false, "appStores" : [{"name" : "Store2", "displayName" : "Marketing Dept",
        // "published" : true}, {"name" : "Store4", "displayName" : "Engineering", "published" : true}]}
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonStoreArray = (JSONArray) jsonObject.get("appStores");
        for (int i = 0; i < jsonStoreArray.length(); i++) {
            JSONObject storeObj = (JSONObject) jsonStoreArray.get(i);
            String storeName = (String) storeObj.get("name");
            boolean publishedStatus = (Boolean) storeObj.get("published");
            if (store.equals(storeName) && publishedStatus != flag) {
                return false;
            }
        }
        return true;
    }
}


