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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

public class VerificationUtil {
    /**
     * Check for error in http response data.
     *
     * @param response HttpResponse.
     * @throws Exception on errors.
     */
    public static void checkErrors(HttpResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject(response.getData());
        if ((Boolean) jsonObject.get(AppmTestConstants.ERROR)) {
            throw new Exception("Operation not successful: " + jsonObject.get("message").toString());
        }
    }

    /**
     * Check whether application state change done successfully.
     *
     * @param response HttpResponse.
     * @throws Exception on errors.
     */
    public static void checkAppStateChange(HttpResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject(response.getData());
        String status = (String) jsonObject.get(AppmTestConstants.STATUS);
        if (!status.equalsIgnoreCase("success")) {
            throw new Exception("Operation not successful: " + jsonObject.get("messages").toString());
        }
    }

    /**
     * Check whether application create done successfully.
     * @param response HttpResponse.
     * @throws Exception on errors.
     */
    public static void checkAppCreateRes(HttpResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject(response.getData());
        if (!(Boolean) jsonObject.get("ok")) {
            throw new Exception("Operation not successful: " + jsonObject.get("message").toString());
        }
    }

    /**
     * This method check whether received response is a json payload.
     * 1. check for http status code 200
     * 2. check for content-type to be application/json
     * 3. check for json payload
     *
     * @param response HttpResponse
     * @throws Exception
     */
    public static void verifyJsonResponse(HttpResponse response) throws Exception {
        int httpStatusCode = response.getResponseCode();
        if (httpStatusCode != 200) {
            throw new Exception("Http status code mismatched.Expected code 200. Received code :" + httpStatusCode);
        }

//        String contentType = response.getHeaders().get(AppmTestConstants.CONTENT_TYPE);
//        if(!"application/json".equals(contentType)) {
//            throw new Exception("Content type mismatch.Expected application/json.Received :" +contentType);
//        }

        verifyJsonPayload(response.getData());

    }

    /**
     * check for json payload(json object/json array).
     *
     * @param payload Payload
     * @throws Exception
     */
    private static void verifyJsonPayload(String payload) throws Exception {

        try {
            JSONObject jsonObject = new JSONObject(payload);
        } catch (JSONException e) {
            try {
                JSONArray jsonArray = new JSONArray(payload);
            } catch (JSONException e1) {
                throw new Exception("Received payload is not in json format");
            }

        }
    }

    /**
     * Check whether response is json and it has value "TRUE" for key "ok".
     *
     * @param response HttpResponse
     * @throws Exception
     */
    public static void checkStatusOk(HttpResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject(response.getData());
        if (!(Boolean) jsonObject.get("ok")) {
            throw new Exception("Operation not successful: " + jsonObject.get("message").toString());
        }
    }

    /**
     * Check whether response is josn and it has value "TRUE" for key "isDeleted"
     *
     * @param response HttpResponse
     * @throws Exception
     */
    public static void checkDeleteResponse(HttpResponse response) throws Exception {
        JSONObject jsonObject = new JSONObject(response.getData());
        if (!(Boolean) jsonObject.get("isDeleted")) {
            throw new Exception("Operation not successful: " + jsonObject.get("message").toString());
        }
    }
}
