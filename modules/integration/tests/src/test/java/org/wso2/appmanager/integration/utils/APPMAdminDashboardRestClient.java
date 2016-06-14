/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.appmanager.integration.utils.bean.BusinessOwner;
import org.wso2.appmanager.integration.utils.bean.BusinessOwnerCreateRequest;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class APPMAdminDashboardRestClient {
    private String backEndUrl;
    private Map<String, String> requestHeaders = new HashMap<String, String>();

    public APPMAdminDashboardRestClient(String backEndUrl) throws MalformedURLException {
        this.backEndUrl = backEndUrl;
        if (requestHeaders.get(AppmTestConstants.CONTENT_TYPE) == null) {
            this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        }
    }

    /**
     * Login to Admin Dashboard.
     *
     * @param userName String.
     * @param password String.
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse login(String userName, String password) throws Exception {
        requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");

        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .LOGIN_URL), "action=login&username=" + userName + "&password=" + password + "", requestHeaders);
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
     * Logout from admin dashboard.
     *
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse logout() throws Exception {
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .LOGIN_URL), "action=logout", requestHeaders);
        if (response.getResponseCode() == 200) {
            this.requestHeaders.clear();
            return response;
        } else {
            throw new Exception("User Logout failed! " + response.getData());
        }
    }

    private String setSession(String session) {
        return requestHeaders.put(AppmTestConstants.COOKIE, session);
    }

    private String getSession(Map<String, String> responseHeaders) {
        return responseHeaders.get(AppmTestConstants.SET_COOKIE);
    }

    /**
     * Add a new business owner.
     * @param businessOwner
     * @return
     * @throws Exception
     */
    public HttpResponse addBusinessOwner(BusinessOwner businessOwner) throws Exception {
        BusinessOwnerCreateRequest businessOwnerCreateRequest = new BusinessOwnerCreateRequest(businessOwner);
        String payload = businessOwnerCreateRequest.generateRequestParameters();
        requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .BUSINESS_OWNER_URL), payload, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        JSONObject jsonObject = new JSONObject(response.getData());
        JSONObject idJsonObject = (JSONObject) jsonObject.get(AppmTestConstants.RESPONSE);
        int businessOwnerId = (Integer) idJsonObject.get(AppmTestConstants.ID);
        businessOwner.setBusinessOwnerId(businessOwnerId);
        return response;
    }

    /**
     * Delete business owner.
     * @param businessOwnerId
     * @return
     * @throws Exception
     */
    public HttpResponse deleteBusinessOwner(int businessOwnerId) throws Exception {
        HttpResponse response = HttpUtil.doDelete(new URL(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .BUSINESS_OWNER_URL + File.separator + businessOwnerId), requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }

    /**
     * Get Business owner by Id.
     * @param businessOwnerId
     * @return
     * @throws Exception
     */
    public HttpResponse getBusinessOwner(int businessOwnerId) throws Exception {
        HttpResponse response = HttpRequestUtil.doGet(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .BUSINESS_OWNER_URL + File.separator + businessOwnerId, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }

    /**
     * Get all business owners.
     * @return
     * @throws Exception
     */
    public HttpResponse getBusinessOwners() throws Exception {
        HttpResponse response = HttpRequestUtil.doGet(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .BUSINESS_OWNER_URL, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }

    /**
     * Update business owner.
     * @param businessOwner
     * @return
     * @throws Exception
     */
    public HttpResponse updateBusinessOwner(BusinessOwner businessOwner) throws Exception {
        BusinessOwnerCreateRequest businessOwnerCreateRequest = new BusinessOwnerCreateRequest(businessOwner);
        String payload = businessOwnerCreateRequest.generateRequestParameters();
        requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl + AppmTestConstants.AdminDashBoardApis
                .UPDATE_BUSINESS_OWNER_URL), payload, requestHeaders);
        VerificationUtil.verifyJsonResponse(response);
        return response;
    }
}
