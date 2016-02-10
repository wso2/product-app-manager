/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.appmanager.integration.utils;

import org.json.JSONObject;
import org.wso2.appmanager.integration.utils.bean.*;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APPMPublisherRestClient {
    private String backEndUrl;
    private Map<String, String> requestHeaders = new HashMap<String, String>();

    public APPMPublisherRestClient(String backEndUrl) throws MalformedURLException {
        this.backEndUrl = backEndUrl;
        if (requestHeaders.get(AppmTestConstants.CONTENT_TYPE) == null) {
            this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        }
    }

    /**
     * Login to publisher.
     *
     * @param userName String.
     * @param password String.
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse login(String userName, String password) throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
                                                        + AppmTestConstants.PubliserRestApis.LOGIN_URL
                                                        + "&username="
                                                        + userName
                                                        + "&password="
                                                        + password), "{}",
                                                       requestHeaders);
        if (response.getResponseCode() == 200) {
            String session = getSession(response.getHeaders());
            if (session == null) {
                throw new Exception("No session cookie found with response");
            }
            setSession(session);
            return response;
        } else {
            throw new Exception("User Login failed! " + response.getData());
        }
    }

    /**
     * Logout from publisher.
     *
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse logout() throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
                                                       + AppmTestConstants.PubliserRestApis.LOGOUT_URL
                                                       ), "{}", requestHeaders);
        if (response.getResponseCode() == 200) {
            this.requestHeaders.clear();
            return response;
        } else {
            throw new Exception("User Logout failed! " + response.getData());
        }
    }

    /**
     * Create New Web application.
     *
     * @param appName      String.
     * @param context      String.
     * @param appVersion   String.
     * @param trackingCode String.
     * @return appCreateResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse webAppCreate(String appName, String context, String appVersion,
                                     String trackingCode) throws Exception {
        checkAuthentication();
        String appDescription = "Default app description for " + appName;
        HttpResponse httpResponse = addPoicyGroup(appDescription);
        String httpResponseData = httpResponse.getData();
        if (httpResponse.getResponseCode() == 200) {
            JSONObject responseData = new JSONObject(httpResponseData);
            String response = responseData.getString(AppmTestConstants.RESPONSE);
            JSONObject responseObject = new JSONObject(response);

            //Retrieve the policy group id.
            String policyId = responseObject.getString(AppmTestConstants.ID);
            String policyGroupId = "[" + policyId + "]";

            //Set new policy Id to AppCreateRequest;
            AppCreateRequest appRequest = new AppCreateRequest(appName, context, appVersion,
                                                               trackingCode);
            appRequest.setUriTemplatePolicyGroupId0(policyId);
            appRequest.setUriTemplatePolicyGroupId1(policyId);
            appRequest.setUriTemplatePolicyGroupId2(policyId);
            appRequest.setUriTemplatePolicyGroupId3(policyId);
            appRequest.setUriTemplatePolicyGroupId4(policyId);
            appRequest.setUriTemplatePolicyGroupIds(policyGroupId);

            HttpResponse appCreateResponse = createApp(appRequest);
            if (appCreateResponse.getResponseCode() == 200) {
                HttpResponse addSsoProviderResponse = addSsoProvider(appRequest);
                if (addSsoProviderResponse.getResponseCode() == 200) {
                    return appCreateResponse;
                } else {
                    throw new Exception("Error occurred while service provider creating! "
                                                + appCreateResponse.getData());
                }
            } else {
                throw new Exception("Error occurred while new web app creating! "
                                            + appCreateResponse.getData());
            }
        } else {
            throw new Exception("Error occurred while policy adding! " + httpResponseData);
        }
    }

    /**
     * Add policy group before creating the application.
     *
     * @param policyDesc String.
     * @return httpResponse.
     * @throws Exception on errors.
     */
    private HttpResponse addPoicyGroup(String policyDesc) throws Exception {
        requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");

        String payload = "policyGroupName=Default&throttlingTier=Unlimited&userRoles"
                + "=&anonymousAccessToUrlPattern=false&objPartialMappings"
                + "=[]&policyGroupDesc="
                + policyDesc;

        HttpResponse response = HttpUtil.doPost(new URL(backEndUrl
                                                       + AppmTestConstants.PubliserRestApis
                                                                                  .ADD_POLICY_GROUP),
                                                       payload, requestHeaders);
        return response;
    }

    /**
     * creating web application.
     *
     * @param appRequest - to create the payload.
     * @return response HttpResponse.
     * @throws Exception on errors.
     */
    private HttpResponse createApp(AppCreateRequest appRequest) throws Exception {
        String payload = appRequest.generateRequestParameters();
        String roles = appRequest.getRoles();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE,
                                "application/x-www-form-urlencoded");
        HttpResponse response =
                HttpRequestUtil.doPost(new URL(backEndUrl
                                       + AppmTestConstants.PubliserRestApis.CREATE_APP),
                                       payload,
                                       requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkAppCreateRes(response);
            JSONObject jsonObject = new JSONObject(response.getData());
            String appId = (String) jsonObject.get(AppmTestConstants.ID);

            if (!roles.equals("")) {
                this.addRole(roles, appId);
            }

            String tag = appRequest.getTags();
            if (!tag.equals("")) {
                this.addNewTag(appId, tag);
            }
            return response;
        } else {
            throw new Exception("App creation failed> " + response.getData());
        }
    }

    /**
     * Add Sso Provider when creating new web app.
     *
     * @param appCreateRequest AppCreateRequest.
     * @return ssoProviderAddingResponse HttpResponse.
     * @throws Exception
     */
    private HttpResponse addSsoProvider(AppCreateRequest appCreateRequest) throws Exception {
        String provider = appCreateRequest.getSsoProvider();
        String logOutUrl = appCreateRequest.getOverviewLogoutUrl();
        if (logOutUrl == null) {
            logOutUrl = "";
        }
        String claims = appCreateRequest.getClaims();
        String appName = appCreateRequest.getOverviewName();
        String version = appCreateRequest.getOverviewVersion();
        String transport = appCreateRequest.getOverviewTransports();
        String context = appCreateRequest.getOverviewContext();
        String acsUrl = appCreateRequest.getOverviewAcsUrl();

        String requestBody = "{\"provider\":\"" + provider +
                "\",\"logout_url\":\"" + logOutUrl +
                "\",\"app_acsURL\":\"" + acsUrl +
                "\",\"claims\":[\"" + claims + "\"],\"app_name\":\"" + appName +
                "\",\"app_verison\":\"" + version +
                "\",\"app_transport\":\"" + transport +
                "\",\"app_context\":\"" + context +
                "\"}";
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        HttpResponse response =
                HttpUtil.doPost(new URL(
                        backEndUrl + AppmTestConstants.PubliserRestApis.ADD_SSO_PROVIDER),
                                requestBody, requestHeaders);
        return response;
    }

    /**
     * Add the roles to an application.
     *
     * @param roles String.
     * @param appId String.
     * @return roleAddedResponse HttpResponse.
     * @throws Exception on errors.
     */
    private HttpResponse addRole(String roles, String appId) throws Exception {
        String role = roles;
        this.requestHeaders.put("Content-Type", "application/json");
        HttpResponse response =
                HttpUtil.doPost(new URL(backEndUrl + "/publisher/asset/webapp/id/" +
                                                appId + "/permissions"),
                                "[{\"role\":\"" + role +
                                        "\",\"permissions\":[\"GET\",\"PUT\",\"DELETE\"," +
                                        "\"AUTHORIZE\"]}]",
                                requestHeaders);
        if (response.getResponseCode() == 200) {
            return response;
        } else {
            throw new Exception("Add role failed> " + response.getData());
        }
    }

    /**
     * Publish an application.
     *
     * @param appId String.
     * @return appPublishedResponse String.
     * @throws Exception on errors.
     */
    public HttpResponse publishWebApp(String appId) throws Exception {

        //Submit the app for review
        changeState(appId, "Submit for Review");
        //Approve the app
        changeState(appId, "Approve");
        //Publish the app
        HttpResponse response = changeState(appId, "Publish");
        return response;
    }

    /**
     * Change the life cycle state from current state to next state
     *
     * @param appId   String.
     * @param toState String.
     * @return changedStateResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse changeState(String appId, String toState) throws Exception {
        checkAuthentication();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "");
        String encodedState = toState.replaceAll(" ", "%20");

        HttpResponse response =
                HttpUtil.doPut(new URL(backEndUrl + "/publisher/api/lifecycle/"
                                       + encodedState + "/webapp/" + appId), "",
                                         requestHeaders);
        return response;
    }

    /**
     * Add a new tag
     *
     * @param id      String.
     * @param tagName String.
     * @return tagsAddedResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse addNewTag(String id, String tagName) throws Exception {
        checkAuthentication();
        requestHeaders.put("Content-Type", "application/json");
        HttpResponse response = HttpUtil.doPut(new URL(backEndUrl
                                               + AppmTestConstants.PubliserRestApis.ADD_NEW_TAGS
                                               + id),
                                               "{\"tags\":[\" " + tagName + " \"]}",
                                               requestHeaders);

        if (response.getResponseCode() == 200) {
            return response;
        } else {
            throw new Exception("Get Api Information failed> " + response.getData());
        }

    }

    /**
     * Get Web app Properties.
     * @param appId String.
     * @return
     * @throws Exception
     */
    public HttpResponse getWebAppProperty(String appId) throws Exception {
        checkAuthentication();
        HttpResponse httpResponse = HttpRequestUtil.doGet(
                backEndUrl + AppmTestConstants.PubliserRestApis.GET_WEB_APP_PROPERTY
                        + appId, requestHeaders);
        if (httpResponse.getResponseCode() == 200) {
            return httpResponse;
        } else {
            throw new Exception(
                    "Error occurred while retrieving webapp properties by app Id :" + appId);
        }
    }

    public void setHttpHeader(String headerName, String value) {
        this.requestHeaders.put(headerName, value);
    }

    public String getHttpHeader(String headerName) {
        return this.requestHeaders.get(headerName);
    }

    public void removeHttpHeader(String headerName) {
        this.requestHeaders.remove(headerName);
    }

    private String setSession(String session) {
        return requestHeaders.put(AppmTestConstants.COOKIE, session);
    }

    private String getSession(Map<String, String> responseHeaders) {
        return responseHeaders.get(AppmTestConstants.SET_COOKIE);
    }

    /**
     * Check whether user is log in.
     *
     * @return true/false.
     * @throws Exception on errors.
     */
    private boolean checkAuthentication() throws Exception {
        if (requestHeaders.get(AppmTestConstants.COOKIE) == null) {
            throw new Exception("No Session Cookie found. Please login first");
        }
        return true;
    }

    /**
     * This method create webapp
     * Creating an web app has for steps
     * 1. Add web app details to registry and APM db
     * 2. Add role permission of the webapp to registry
     * 3. Add tags of the web app to registry
     * 4. Create service provider for webapp
     *
     * @param webApp Web App
     * @throws Exception
     */
    public boolean createWebApp(WebApp webApp) throws Exception {
        HttpResponse response = addWebApp(webApp);
        VerificationUtil.checkAppCreateRes(response);
        JSONObject jsonObject = new JSONObject(response.getData());
        String appId = (String) jsonObject.get(AppmTestConstants.ID);
        webApp.setAppId(appId);
        List<String> roles = webApp.getRoles();
        if (roles != null && roles.size() > 0) {
            this.addRoles(roles, appId);
        }

        List<String> tags = webApp.getTags();
        if (tags != null && tags.size() > 0) {
            this.addNewTags(tags, appId);
        }
        addSsoProvider(webApp);
        return true;
    }

    /**
     * Add web app detail to registry and APM db
     *
     * @param webApp Web App
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse addWebApp(WebApp webApp) throws Exception {
        WebAppCreateRequest webAppCreateRequest = new WebAppCreateRequest(webApp);
        String payload = webAppCreateRequest.generateRequestParameters();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
                + AppmTestConstants.PubliserRestApis.CREATE_APP), payload, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }

    /**
     * Create service provider for given web app
     *
     * @param webApp Web App
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse addSsoProvider(WebApp webApp) throws Exception {
        SsoProviderRequest ssoProviderRequest = new SsoProviderRequest(webApp);
        String requestBody = ssoProviderRequest.getSsoProviderRequestPayload();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        HttpResponse response =
                HttpUtil.doPost(new URL(
                                backEndUrl + AppmTestConstants.PubliserRestApis.ADD_SSO_PROVIDER),
                        requestBody, requestHeaders);
        return response;
    }

    /**
     * Add a policy group
     *
     * @param policyGroup Policy Group
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse addPolicyGroup(PolicyGroup policyGroup) throws Exception {
        PolicyGroupRequest policyGroupRequest = new PolicyGroupRequest(policyGroup);
        String payload = policyGroupRequest.generateRequestParameters();
        requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");

        HttpResponse response = HttpUtil.doPost(new URL(backEndUrl
                        + AppmTestConstants.PubliserRestApis
                        .ADD_POLICY_GROUP),
                payload, requestHeaders);
        return response;
    }

    /**
     * Add given roles to given webapp(web appId)
     *
     * @param roles Visible Roles
     * @param appId Web App Id
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse addRoles(List<String> roles, String appId) throws Exception {
        RolesRequest rolesRequest = new RolesRequest(roles);
        String requestPayload = rolesRequest.getRolesRequestPayload();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        HttpResponse response =
                HttpUtil.doPost(new URL(backEndUrl + AppmTestConstants.PubliserRestApis.ADD_ROLES +
                        appId + "/permissions"), requestPayload, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }

    /**
     * Add tags to given webapp(web appId)
     *
     * @param tags  Tags
     * @param appId Web App Id
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse addNewTags(List<String> tags, String appId) throws Exception {
        checkAuthentication();
        TagsRequest tagsRequest = new TagsRequest(tags);
        String requestPayload = tagsRequest.getTagRequestPayload();
        requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        HttpResponse response = HttpUtil.doPut(new URL(backEndUrl
                + AppmTestConstants.PubliserRestApis.ADD_NEW_TAGS
                + appId), requestPayload, requestHeaders);

        VerificationUtil.verifyJsonResponse(response);
        return response;

    }

    /**
     * Edit the web app.
     * 1. Edit the content in registry and APM db
     * 2. Edit roles assigned to webapp
     * 3. Edit tags assigned to webapp
     * 4. Edit SP config of webapp
     *
     * @param webApp Modified Web App
     * @return
     * @throws Exception
     */
    public boolean editWebApp(WebApp webApp) throws Exception {
        HttpResponse response = editApp(webApp);
        VerificationUtil.checkStatusOk(response);
        String appId = webApp.getAppId();
        List<String> roles = webApp.getRoles();
        if (roles != null && roles.size() > 0) {
            this.addRoles(roles, appId);
        }

        List<String> tags = webApp.getTags();
        if (tags != null && tags.size() > 0) {
            this.addNewTags(tags, appId);
        }

        editSsoProvider(webApp);
        return true;
    }

    /**
     * Edit the content of webapp in registry and APM db
     *
     * @param webApp Modified Web App
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse editApp(WebApp webApp) throws Exception {
        WebAppCreateRequest webAppCreateRequest = new WebAppCreateRequest(webApp);
        String payload = webAppCreateRequest.generateRequestParameters();
        String appId = webApp.getAppId();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
                + AppmTestConstants.PubliserRestApis.EDIT_WEB_APP + appId), payload, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }

    /**
     * Edit the Service Provider configuration of web app
     *
     * @param webApp Modified Web App
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse editSsoProvider(WebApp webApp) throws Exception {
        SsoProviderRequest ssoProviderRequest = new SsoProviderRequest(webApp);
        String requestBody = ssoProviderRequest.getSsoProviderRequestPayload();
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        HttpResponse response =
                HttpUtil.doPost(new URL(
                                backEndUrl + AppmTestConstants.PubliserRestApis.EDIT_SSO_PROVIDER),
                        requestBody, requestHeaders);
        return response;
    }

    /**
     * Delete the webapp
     *
     * @param appId Web App Id
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse deleteApp(String appId) throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl +
                AppmTestConstants.PubliserRestApis.DELETE_WEB_APP + appId + ""), "", requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }
}
