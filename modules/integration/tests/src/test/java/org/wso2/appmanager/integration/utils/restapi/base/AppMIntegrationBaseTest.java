/*
*Copyright (c) 2016​, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.integration.utils.restapi.base;

import exception.AppManagerIntegrationTestException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.Assert;
import org.wso2.carbon.automation.engine.context.AutomationContext;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.engine.context.beans.User;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.appmanager.integration.utils.restapi.bean.AppMURLBean;
import org.wso2.appmanager.integration.utils.restapi.clients.AppMPublisherRestClient;
import org.wso2.appmanager.integration.utils.restapi.clients.AppMStoreRestClient;

import javax.xml.xpath.XPathExpressionException;

/**
 * Base class for all AppM Manager integration tests Users need to extend this class to write integration tests.
 */
public class AppMIntegrationBaseTest {

    private static final Log log = LogFactory.getLog(AppMIntegrationBaseTest.class);
    protected AutomationContext storeContext, publisherContext, keyManagerContext, gatewayContextMgt,
            gatewayContextWrk, backEndServer;
    protected TestUserMode userMode;
    protected AppMURLBean storeUrls, publisherUrls, gatewayUrlsMgt, gatewayUrlsWrk, keyMangerUrl, backEndServerUrl;
    protected User user;
    protected AppMPublisherRestClient apiPublisher;
    protected AppMStoreRestClient apiStore;
    protected String publisherURLHttp;
    protected String storeURLHttp;

    /**
     * This method will initialize test environment based on user mode and configuration given at automation.xml
     *
     * @throws AppManagerIntegrationTestException - if test configuration init fails
     */
    protected void init() throws AppManagerIntegrationTestException {
        userMode = TestUserMode.SUPER_TENANT_ADMIN;
        init(userMode);
    }

    /**
     * init the object with user mode , create context objects and get session cookies
     *
     * @param userMode - user mode to run the tests
     * @throws AppManagerIntegrationTestException - if test configuration init fails
     */
    protected void init(TestUserMode userMode) throws AppManagerIntegrationTestException {
        try {
            //todo: uncomment relevant initializations when needed
            //create store server instance based on configuration given at automation.xml

            /*
            storeContext =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_STORE_INSTANCE, userMode);
            storeUrls = new AppMURLBean(storeContext.getContextUrls());


            //create publisher server instance based on configuration given at automation.xml
            publisherContext =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_PUBLISHER_INSTANCE, userMode);
            publisherUrls = new AppMURLBean(publisherContext.getContextUrls());

            //create gateway server instance based on configuration given at automation.xml
            gatewayContextMgt =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_GATEWAY_MGT_INSTANCE, userMode);
            gatewayUrlsMgt = new AppMURLBean(gatewayContextMgt.getContextUrls());

            gatewayContextWrk =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_GATEWAY_WRK_INSTANCE, userMode);
            gatewayUrlsWrk = new AppMURLBean(gatewayContextWrk.getContextUrls());

            keyManagerContext = new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                                      AppMIntegrationConstants.APPM_KEY_MANAGER_INSTANCE, userMode);
            keyMangerUrl = new AppMURLBean(keyManagerContext.getContextUrls());


            backEndServer = new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                                  AppMIntegrationConstants.BACKEND_SERVER_INSTANCE, userMode);
            backEndServerUrl = new AppMURLBean(backEndServer.getContextUrls());

            executionMode = gatewayContextMgt.getConfigurationValue(ContextXpathConstants.EXECUTION_ENVIRONMENT);

            user = storeContext.getContextTenant().getContextUser();

            superTenantKeyManagerContext = new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                                                 AppMIntegrationConstants.APPM_KEY_MANAGER_INSTANCE,
                                                                 TestUserMode.SUPER_TENANT_ADMIN);

            keymanagerSessionCookie = createSession(keyManagerContext);
            publisherURLHttp = publisherUrls.getWebAppURLHttp();
            storeURLHttp = storeUrls.getWebAppURLHttp();
            apiPublisher = new AppMPublisherRestClient(publisherURLHttp);
            apiStore = new AppMStoreRestClient(storeURLHttp);

            try {
                keymanagerSuperTenantSessionCookie = new LoginLogoutClient(superTenantKeyManagerContext).login();
                userManagementClient = new UserManagementClient(
                        keyManagerContext.getContextUrls().getBackEndUrl(), keymanagerSessionCookie);
                tenantManagementServiceClient = new TenantManagementServiceClient(
                        superTenantKeyManagerContext.getContextUrls().getBackEndUrl(),
                        keymanagerSuperTenantSessionCookie);
            } catch (Exception e) {
                throw new AppManagerIntegrationTestException(e.getMessage(), e);
            }
*/


            storeContext =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME, userMode);
            storeUrls = new AppMURLBean(storeContext.getContextUrls());

            publisherContext =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME, userMode);
            publisherUrls = new AppMURLBean(publisherContext.getContextUrls());

            gatewayContextWrk =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME, userMode);
            gatewayUrlsWrk = new AppMURLBean(gatewayContextWrk.getContextUrls());

            keyManagerContext = new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME, userMode);
            keyMangerUrl = new AppMURLBean(keyManagerContext.getContextUrls());

            publisherURLHttp = publisherUrls.getWebAppURLHttp();
            storeURLHttp = storeUrls.getWebAppURLHttp();
            apiPublisher = new AppMPublisherRestClient(publisherURLHttp);
            apiStore = new AppMStoreRestClient(storeURLHttp);
            user = storeContext.getContextTenant().getContextUser();

        } catch (XPathExpressionException e) {
            log.error("AppM test environment initialization failed", e);
            throw new AppManagerIntegrationTestException("AppM test environment initialization failed", e);
        }

    }

    /**
     * init the object with tenant domain, user key and instance of store,publisher and gateway create context objects
     * and construct URL bean
     *
     * @param domainKey - tenant domain key
     * @param userKey   - tenant user key
     * @throws AppManagerIntegrationTestException - if test configuration init fails
     */
    protected void init(String domainKey, String userKey)
            throws AppManagerIntegrationTestException {

        try {
            //create store server instance based configuration given at automation.xml
            storeContext =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_STORE_INSTANCE, domainKey, userKey);
            storeUrls = new AppMURLBean(storeContext.getContextUrls());

            //create publisher server instance
            publisherContext =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_PUBLISHER_INSTANCE, domainKey, userKey);
            publisherUrls = new AppMURLBean(publisherContext.getContextUrls());

            //create gateway server instance
            gatewayContextMgt =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_GATEWAY_MGT_INSTANCE, domainKey, userKey);
            gatewayUrlsMgt = new AppMURLBean(gatewayContextMgt.getContextUrls());

            gatewayContextWrk =
                    new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                          AppMIntegrationConstants.APPM_GATEWAY_WRK_INSTANCE, domainKey, userKey);
            gatewayUrlsWrk = new AppMURLBean(gatewayContextWrk.getContextUrls());

            keyManagerContext = new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                                      AppMIntegrationConstants.APPM_KEY_MANAGER_INSTANCE, domainKey,
                                                      userKey);
            keyMangerUrl = new AppMURLBean(keyManagerContext.getContextUrls());

            backEndServer = new AutomationContext(AppMIntegrationConstants.APPM_PRODUCT_GROUP_NAME,
                                                  AppMIntegrationConstants.BACKEND_SERVER_INSTANCE, domainKey, userKey);
            backEndServerUrl = new AppMURLBean(backEndServer.getContextUrls());

            user = storeContext.getContextTenant().getContextUser();

        } catch (XPathExpressionException e) {
            log.error("Init failed", e);
            throw new AppManagerIntegrationTestException("APIM test environment initialization failed", e);
        }

    }


    protected String getStoreURLHttp() {
        return storeUrls.getWebAppURLHttp();
    }

    protected String getPublisherURLHttp() {
        return publisherUrls.getWebAppURLHttp();
    }

    protected String getGatewayURLNhttp() {
        return gatewayUrlsWrk.getWebAppURLNhttp();
    }


    protected String getKeyManagerURLHttp() {
        return keyMangerUrl.getWebAppURLHttp();
    }


    //todo: implement cleanup which remove all Apps and call in destroy method of all tests

    /**
     * Cleaning up the API manager by removing all APIs and applications other than default application
     *
     * @throws AppManagerIntegrationTestException - occurred when calling the apis
     * @throws org.json.JSONException             - occurred when reading the json
     */
    //  protected void cleanUp() throws Exception {
        /*
        AppMStoreRestClient apiStore = new AppMStoreRestClient(getStoreURLHttp());
        apiStore.login(user.getUserName(), user.getPassword());
        AppMPublisherRestClient publisherRestClient = new AppMPublisherRestClient(getPublisherURLHttp());
        publisherRestClient.login(user.getUserName(), user.getPassword());

        String apiData = apiStore.getAPI().getData();
        JSONObject jsonAPIData = new JSONObject(apiData);
        JSONArray jsonAPIArray = jsonAPIData.getJSONArray(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_APIS);

        //delete all APIs
        for (int i = 0; i < jsonAPIArray.length(); i++) {
            JSONObject api = jsonAPIArray.getJSONObject(i);
            publisherRestClient.deleteAPI(api.getString(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_API_NAME)
                    , api.getString(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_API_VERSION),
                                          user.getUserName());
        }


        HttpResponse subscriptionDataResponse = apiStore.getAllSubscriptions();
        verifyResponse(subscriptionDataResponse);
        JSONObject jsonSubscription = new JSONObject(subscriptionDataResponse.getData());

        if (!jsonSubscription.getBoolean(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_ERROR)) {
            JSONObject jsonSubscriptionsObject = jsonSubscription.getJSONObject(
                    AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_SUBSCRIPTION);
            JSONArray jsonApplicationsArray = jsonSubscriptionsObject.getJSONArray(
                    AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_APPLICATIONS);

            //Remove API Subscriptions
            for (int i = 0; i < jsonApplicationsArray.length(); i++) {
                JSONObject appObject = jsonApplicationsArray.getJSONObject(i);
                int id = appObject.getInt(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_ID);
                JSONArray subscribedAPIJSONArray = appObject.getJSONArray(
                        AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_SUBSCRIPTION);
                for (int j = 0; j < subscribedAPIJSONArray.length(); j++) {
                    JSONObject subscribedAPI = subscribedAPIJSONArray.getJSONObject(j);
                    verifyResponse(apiStore.removeAPISubscription(subscribedAPI.getString(
                                                                          AppMIntegrationConstants
                                                                                  .API_RESPONSE_ELEMENT_NAME_API_NAME)
                            , subscribedAPI.getString(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_API_VERSION),
                                                                  subscribedAPI.getString(
                                                                          AppMIntegrationConstants
                                                                                  .API_RESPONSE_ELEMENT_NAME_API_PROVIDER),
                                                                  String.valueOf(id)));
                }
            }
        }

*/
    //   }
    protected void verifyResponse(HttpResponse httpResponse) throws JSONException {
        Assert.assertNotNull(httpResponse, "Response object is null");
        log.info("Response Code : " + httpResponse.getResponseCode());
        log.info("Response Message : " + httpResponse.getData());
        Assert.assertEquals(httpResponse.getResponseCode(), HttpStatus.SC_OK, "Response code is not as expected");
        JSONObject responseData = new JSONObject(httpResponse.getData());
        Assert.assertFalse(responseData.getBoolean(AppMIntegrationConstants.API_RESPONSE_ELEMENT_NAME_ERROR),
                           "Error message received " + httpResponse.getData());

    }
}
