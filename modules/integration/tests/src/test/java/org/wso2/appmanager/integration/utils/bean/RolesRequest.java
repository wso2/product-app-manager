/*
*Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.appmanager.integration.utils.bean;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;

/**
 * [{"role":"Internal/store-admin","permissions":["GET"]},{"role":"Internal/everyone","permissions":["GET"]}]
 */
public class RolesRequest {
    List<String> roles;

    public RolesRequest(List<String> roles) {
        this.roles = roles;
    }

    public String getRolesRequestPayload() throws Exception {
        JSONArray payload = new JSONArray();
        for (String role : roles) {
            JSONObject object = new JSONObject();
            JSONArray permissionArray = new JSONArray();
            permissionArray.put("GET");
            object.put("role", role);
            object.put("permissions", permissionArray);
            payload.put(object);
        }
        return payload.toString();
    }
}
