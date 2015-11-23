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
            throw new Exception("Operation not successful: " + jsonObject.get("message")
                    .toString());
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
            throw new Exception("Operation not successful: " + jsonObject.get("messages")
                    .toString());
        }
    }
}
