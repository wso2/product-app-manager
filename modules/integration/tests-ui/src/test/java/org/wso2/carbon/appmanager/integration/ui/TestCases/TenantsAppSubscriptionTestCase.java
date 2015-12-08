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
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.AppCreateRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.SubscriptionRequest;
import org.wso2.carbon.appmanager.integration.ui.Util.Bean.Tenant;
import org.wso2.carbon.appmanager.integration.ui.Util.TenantPopulator;
import org.wso2.carbon.appmanager.integration.ui.Util.TestUtils.ApplicationInitializingUtil;
import org.wso2.carbon.automation.core.utils.HttpResponse;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

/**
 * This class contain test suits for subscription control feature
 * Application subscription can be restricted by following options
 * 1 .all_tenants - > which allow to users in any tenant to subscribe
 * 2. current_tenant -> only users in the tenant where app is created can subscribe
 * 3. specific_tenants - only users in the current tenant, and users tenants which are mentioned
 * when create teh app can subscribe
 */
public class TenantsAppSubscriptionTestCase extends APPManagerIntegrationTest {
    private String username;
    private String password;
    private String appNamePrefix;
    private String version;
    private APPMPublisherRestClient appMPublisher;
    private APPMStoreRestClient appMStore;
    private ApplicationInitializingUtil baseUtil;

    private TenantPopulator tenantPopulator;
    private String allowAllAppID;
    private String currentTenantAppID;
    private String specificTenantAppID;

    private static final String HR_DOMAIN = "hr.com";
    private static final String MARKETING_DOMAIN = "mk.com";
    private static final String TENANT_ADMIN_USER_NAME = "admin";
    private static final String TENANT_ADMIN_PASSWORD = "admin123";
    private static final String ALLOW_ALL = "all_tenants";
    private static final String CURRENT_TENANT = "current_tenant";
    private static final String SPECIFIC_TENANTS = "specific_tenants";

    @BeforeClass(alwaysRun = true, description = "" +
            "1. Create tenants (hr.com,mk.com)" +
            "2. Create web app in super tenant space")
    public void init() throws Exception {
        super.init(0);
        //get super tenant username & password
        username = userInfo.getUserName();
        password = userInfo.getPassword();
        //populate tenants
        tenantPopulator = new TenantPopulator(super.getServerURLHttps(), username, password);
        populateTenants();
        baseUtil = new ApplicationInitializingUtil();
        baseUtil.init();
        //create web apps with different subscription options
        appMPublisher = new APPMPublisherRestClient(ApplicationInitializingUtil.publisherURLHttp);
        createWebApps();
        appMStore = new APPMStoreRestClient(ApplicationInitializingUtil.storeURLHttp);
        appNamePrefix = appProp.getAppName();
        version = appProp.getVersion();
    }

    @Test(description = "Application subscription test for allow all subscription option. " +
            "Users from all tenants should be able to subscribe and unsubscirbe to the app" +
            "1. Subscribe as hr.com user and unsubscribe" +
            "2. Subscribe as super tenant user and unsubscribe")
    public void testAllowAllSubscriptionOption() throws Exception {
        String appName = appNamePrefix + ALLOW_ALL;

        //Subscribe as hr domain user
        String storeUser = TENANT_ADMIN_USER_NAME + "@" + HR_DOMAIN;
        appMStore.login(storeUser, TENANT_ADMIN_PASSWORD);
        Boolean subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(true), storeUser + " Unable to subscribe to application : "
                + appProp.getAppName() + ".");
        Boolean unSubscriptionStatus = unSubscribe(appName, username, version);
        assertTrue(unSubscriptionStatus.equals(true), storeUser + " Unable to Unsubscribe to application : "
                + appProp.getAppName() + ".");
        appMStore.logout();

        // subscribe as super domain user where app is created
        storeUser = username;
        appMStore.login(storeUser, password);
        subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(true), storeUser + " Unable to subscribe to application : "
                + appProp.getAppName() + ".");
        unSubscriptionStatus = unSubscribe(appName, username, version);
        assertTrue(unSubscriptionStatus.equals(true), storeUser + " Unable to Unsubscribe to application : "
                + appProp.getAppName() + ".");
        appMStore.logout();
    }

    @Test(description = "Application subscription test for current tenant only subscription option" +
            "Users from super tenant only should be able to subscribe to the app" +
            "1. subscribe from hr.com domain -which should not allowed" +
            "2. subscribe and unsubscribe from super tenant user")
    public void testCurrentTenantOnlySubscriptionOption() throws Exception {
        String appName = appNamePrefix + CURRENT_TENANT;

        //Subscribe as hr domain user
        String storeUser = TENANT_ADMIN_USER_NAME + "@" + HR_DOMAIN;
        appMStore.login(storeUser, TENANT_ADMIN_PASSWORD);
        Boolean subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(false), storeUser + " is able to subscribe to application : "
                + appProp.getAppName() + "when subscription is restricted to super tenant only.");
        appMStore.logout();


        // subscribe as user in super domain where app is created
        storeUser = username;
        appMStore.login(storeUser, password);
        subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(true), storeUser + " Unable to subscribe to application : "
                + appProp.getAppName() + ".");
        Boolean unSubscriptionStatus = unSubscribe(appName, username, version);
        assertTrue(unSubscriptionStatus.equals(true), storeUser + " Unable to Unsubscribe to application : "
                + appProp.getAppName() + ".");
        appMStore.logout();
    }

    @Test(description = "Application subscription test for current tenant only subscription option" +
            "App is restricted to hr.com and current domain(super tenant)" +
            "1.Subscribe and unsubscribe from hr.com" +
            "2.Subscribe and unsubscribe from super tenant " +
            "3. Subscribe from mk.com -which should not be allowed")

    public void testSpecificTenantsSubscriptionOption() throws Exception {
        //this app is allowed to subscribed by users in HR domain and users in the domain where
        //app is created(super tenant)
        String appName = appNamePrefix + SPECIFIC_TENANTS;

        //Subscribe as hr domain user
        String storeUser = TENANT_ADMIN_USER_NAME + "@" + HR_DOMAIN;
        appMStore.login(storeUser, TENANT_ADMIN_PASSWORD);
        Boolean subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(true), storeUser + " Unable to subscribe to application : "
                + appProp.getAppName() + ".");
        Boolean unSubscriptionStatus = unSubscribe(appName, username, version);
        assertTrue(unSubscriptionStatus.equals(true), storeUser + " Unable to Unsubscribe to application : "
                + appProp.getAppName() + ".");
        appMStore.logout();

        // subscribe as user in super domain where app is created
        storeUser = username;
        appMStore.login(storeUser, password);
        subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(true), storeUser + " Unable to subscribe to application : "
                + appProp.getAppName() + ".");
        unSubscriptionStatus = unSubscribe(appName, username, version);
        assertTrue(unSubscriptionStatus.equals(true), storeUser + " Unable to Unsubscribe to application : "
                + appProp.getAppName() + ".");
        appMStore.logout();

        //Subscribe as marketing domain user
        storeUser = TENANT_ADMIN_USER_NAME + "@" + MARKETING_DOMAIN;
        appMStore.login(storeUser, TENANT_ADMIN_PASSWORD);
        subscriptionStatus = subscribe(appName, username, version);
        assertTrue(subscriptionStatus.equals(false), storeUser + " is able to subscribe to application :"
                + appProp.getAppName() + "when subscription is restricted to :" + HR_DOMAIN);
        appMStore.logout();

    }

    @AfterClass(alwaysRun = true)
    public void destroy() throws Exception {
        appMPublisher.deleteApp(allowAllAppID);
        appMPublisher.deleteApp(currentTenantAppID);
        appMPublisher.deleteApp(specificTenantAppID);
        tenantPopulator.deactivateAllTenants();
        appMStore.logout();
        super.cleanup();
        baseUtil.destroy();
    }

    private Boolean subscribe(String appName, String appProvider, String appVersion) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(appName, appProvider, appVersion);
        //Send Subscription Request
        HttpResponse subscriptionResponse = appMStore.subscribeForApplication(subscriptionRequest);
        JSONObject subscriptionJsonObject = new JSONObject(subscriptionResponse.getData());
        return (Boolean) subscriptionJsonObject.get("status");
    }

    private Boolean unSubscribe(String appName, String appProvider, String appVersion) throws Exception {
        SubscriptionRequest subscriptionRequest = new SubscriptionRequest(appName, appProvider, appVersion);
        //Send Subscription Request
        HttpResponse subscriptionResponse = appMStore.unsubscribeForApplication(subscriptionRequest);
        JSONObject subscriptionJsonObject = new JSONObject(subscriptionResponse.getData());
        return (Boolean) subscriptionJsonObject.get("status");
    }

    private void createWebApps() throws Exception {
        appMPublisher.restLogin(username, password);
        //create app with allow all subscription option
        allowAllAppID = createAppWithSubscriptionOption(ALLOW_ALL, null);
        //create app with current tenant subscription option
        currentTenantAppID = createAppWithSubscriptionOption(CURRENT_TENANT, null);
        //create app with specific subscription option
        List<String> tenants = new ArrayList<String>();
        tenants.add(HR_DOMAIN);
        specificTenantAppID = createAppWithSubscriptionOption(SPECIFIC_TENANTS, tenants);
    }

    private void populateTenants() throws Exception {
        Tenant tenantHr = new Tenant(HR_DOMAIN, TENANT_ADMIN_USER_NAME, TENANT_ADMIN_PASSWORD);
        Tenant tenantMk = new Tenant(MARKETING_DOMAIN, TENANT_ADMIN_USER_NAME, TENANT_ADMIN_PASSWORD);
        List<Tenant> tenants = new ArrayList<Tenant>();
        tenants.add(tenantHr);
        tenants.add(tenantMk);
        tenantPopulator.populateTenants(tenants);
    }

    private String createAppWithSubscriptionOption(String subscriptionOption, List<String> tenantList) throws Exception {
        AppCreateRequest appCreateRequest = baseUtil.createBasicAppRequest(subscriptionOption, appMPublisher);
        appCreateRequest.setOverview_subscriptionAvailability(subscriptionOption);
        if (subscriptionOption.equals(SPECIFIC_TENANTS)) {
            appCreateRequest.setOverview_tenants(StringUtils.join(tenantList, ','));
        }
        String uuid = baseUtil.createWebApp(appCreateRequest, appMPublisher);
        baseUtil.publishWebApp(uuid, appMPublisher);
        return uuid;
    }

}
