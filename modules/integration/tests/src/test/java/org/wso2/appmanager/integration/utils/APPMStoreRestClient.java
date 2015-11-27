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
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */

package org.wso2.appmanager.integration.utils;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.wso2.appmanager.integration.utils.bean.SubscriptionRequest;
import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class APPMStoreRestClient {
    private String backEndUrl;
    private Map<String, String> requestHeaders = new HashMap<String, String>();

    public APPMStoreRestClient(String backEndUrl) {
        this.backEndUrl = backEndUrl;
        if (requestHeaders.get(AppmTestConstants.CONTENT_TYPE) == null) {
            this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        }
    }

    /**
     * Log in to the user store.
     *
     * @param userName String.
     * @param password String.
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse login(String userName, String password)
            throws Exception {
        if (requestHeaders.get(AppmTestConstants.CONTENT_TYPE) == null) {
            this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/json");
        }
        HttpResponse response = HttpRequestUtil.doPost(new URL(backEndUrl
                                                     + "/store/apis/user/login"), "{\"username\":\""
                                                     + userName
                                                     + "\""
                                                     + ",\"password\":"
                                                     + "\""
                                                     + password
                                                     + "\""
                                                     + "}", requestHeaders);
        /*
         * On Success {"error" : false, "message" : null} status code 200 On
		 * Failure {"error" : true, "message" : null} status code 200
		 */
        if (response.getResponseCode() == 200) {
            // if error == true this will return an exception then test fail!
            VerificationUtil.checkErrors(response);
            String session = getSession(response.getHeaders());
            if (session == null) {
                throw new Exception("No session cookie found with response");
            }
            setSession(session);
            return response;
        } else {
            System.out.println(response);
            throw new Exception("User Login failed! " + response.getData());
        }
    }

    /**
     * Log out from the user store
     * @return HttpResponse
     * @throws Exception
     */
    public HttpResponse logout() throws Exception {
        this.requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "text/html");

        HttpResponse response = HttpRequestUtil.doGet(backEndUrl
                                                              + "/store/logout", requestHeaders);
        if (response.getResponseCode() == 200) {
            this.requestHeaders.clear();
            return response;
        } else {
            System.out.println(response);
            throw new Exception("User Logout failed from Store! " + response.getData());
        }

    }

    /**
     * Subscribe for application.
     *
     * @param subscriptionRequest SubscriptionRequest.
     * @return httpResponse HttpResponse.
     * @throws Exception on errors.
     */
    public HttpResponse subscribeForApplication(SubscriptionRequest subscriptionRequest)
            throws Exception {
        checkAuthentication();
       requestHeaders.put(AppmTestConstants.CONTENT_TYPE, "application/x-www-form-urlencoded");
        String payload = "apiName="+subscriptionRequest.getName()+"&apiVersion=1.0.0&apiTier=Unlimited&subscriptionType=INDIVIDUAL&apiProvider=admin&appName=DefaultApplication";
              //  subscriptionRequest.generateRequestParameters();
        HttpResponse response = HttpRequestUtil.doPost(new URL(
                backEndUrl + "/store/resources/webapp/v1/subscription/app")
                , payload
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("Application Subscription failed! " + response.getData());
        }
    }

    /**
     * Unsubscribe application.
     *
     * @param unsubscriptionRequest SubscriptionRequest.
     * @return HttpResponse HttpResponse.
     * @throws Exception on errors.
     */

    public HttpResponse unsubscribeForApplication(SubscriptionRequest unsubscriptionRequest)
            throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doPost(new URL(
                backEndUrl + "/store/resources/webapp/v1/unsubscription/app")
                , unsubscriptionRequest.generateRequestParameters()
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("Application Unsubscription failed> " + response.getData());
        }
    }

    /**
     * Rate a given application with a given rating value.
     *
     * @param id          application id.
     * @param appType     application type.
     * @param ratingValue rating value (0-5).
     * @return response with average rating value and user rating value.
     * @throws Exception on errors.
     */
    public HttpResponse rateApplication(String id, String appType, int ratingValue)
            throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doGet(backEndUrl + "/store/apis/rate?id=" +
                                                              id + "&type=" + appType + "&value=" +
                                                              ratingValue, requestHeaders);
        return response;
    }

    public HttpResponse retrieveSubscribedUsers(String appProvider, String appName, String appVersion)
            throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doGet(backEndUrl + "/store/resources/webapp/v1/subscriptions/"
                                                              + appProvider + "/" + appName + "/" + appVersion, requestHeaders);
        if (response.getResponseCode() == 200) {
            JSONArray jsonArray = new JSONArray(response.getData());
            return response;
        } else {
            throw new Exception("Retrieve Subscribed Users of " + appName + " failed. " + response.getData());
        }
    }

    public JSONArray getAllWebAppsProperties(String username, String password, int startingApp, int count)
            throws Exception {
        checkAuthentication();

        //Set Basic Authentication to request header
        String usernameAndPassword = username + ":" + password;
        byte[] authEncBytes = Base64.encodeBase64(usernameAndPassword.getBytes());
        String authStringEnc = new String(authEncBytes);
        this.requestHeaders.put("Authorization", "Basic " + authStringEnc);

        HttpResponse response = HttpUtil.doGet(backEndUrl + "/store/apis/v1/assets/webapp?start=" + startingApp +
                                                       "&count=" + count, requestHeaders);
        if (response.getResponseCode() == 200) {
            JSONArray jsonArray = new JSONArray(response.getData());
            return jsonArray;
        } else {
            throw new Exception("Retrieve User Subscribed Apps failed. " + response.getData());
        }
    }

    /**
     * Get Cookies.
     *
     * @param responseHeaders Map<String, String>.
     * @return cookie String.
     */
    private String getSession(Map<String, String> responseHeaders) {
        return responseHeaders.get(AppmTestConstants.SET_COOKIE);
    }

    /**
     * Set Cookie.
     *
     * @param session String.
     * @return
     */
    private String setSession(String session) {
        return requestHeaders.put(AppmTestConstants.COOKIE, session);
    }

    /**
     * method to check whether user is logged in.
     *
     * @return whether user log in or not.
     * @throws Exception on errors.
     */
    private boolean checkAuthentication() throws Exception {
        if (requestHeaders.get(AppmTestConstants.COOKIE) == null) {
            throw new Exception("No Session Cookie found. Please login first");
        }
        return true;
    }

}
