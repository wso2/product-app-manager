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
        if (requestHeaders.get("Content-Type") == null) {
            this.requestHeaders.put("Content-Type", "application/json");
        }
    }

    /**
     * logs in to the user store
     *
     * @param userName
     * @param password
     * @return
     * @throws Exception
     */
    public HttpResponse login(String userName, String password)
            throws Exception {

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
     * Subscribe application
     *
     * @param subscriptionRequest
     * @return
     * @throws Exception
     */
  public HttpResponse subscribeForApplication(SubscriptionRequest subscriptionRequest)
            throws Exception {
        checkAuthentication();
        HttpResponse response = HttpRequestUtil.doPost(new URL(
                backEndUrl + "/store/resources/webapp/v1/subscription/app")
                , subscriptionRequest.generateRequestParameters()
                , requestHeaders);
        if (response.getResponseCode() == 200) {
            VerificationUtil.checkErrors(response);
            return response;
        } else {
            throw new Exception("Application Subscription failed! " + response.getData());
        }
    }

    private String getSession(Map<String, String> responseHeaders) {
        return responseHeaders.get("Set-Cookie");
    }

    private String setSession(String session) {
        return requestHeaders.put("Cookie", session);
    }

    /**
     * method to check whether user is logged in
     *
     * @return
     * @throws Exception
     */
    private boolean checkAuthentication() throws Exception {
        if (requestHeaders.get("Cookie") == null) {
            throw new Exception("No Session Cookie found. Please login first");
        }
        return true;
    }

}
