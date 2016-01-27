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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * {"provider":"wso2is-5.0.0","logout_url":"",
 * "claims":["http://wso2.org/claims/role","http://wso2.org/claims/otherphone"],
 * "app_name":"TestAPP","app_verison":"1","app_transport":"http",
 * "app_context":"/testapp","app_provider":"admin","app_allowAnonymous":"false","app_acsURL":""}
 */
public class SsoProviderRequest {
    WebApp webApp;

    public SsoProviderRequest(WebApp webApp) {
        this.webApp = webApp;
    }

    public String getSsoProviderRequestPayload() throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("provider", webApp.getProviders());
        payload.put("logout_url", webApp.getOverviewLogoutUrl());
        payload.put("app_name", webApp.getOverviewName());
        payload.put("app_verison", webApp.getOverviewVersion());
        payload.put("app_transport", webApp.getOverviewTransports());
        payload.put("app_context", webApp.getOverviewContext());
        payload.put("app_provider", webApp.getOverviewProvider());
        payload.put("app_acsURL", webApp.getOverviewAcsUrl());
        payload.put("app_allowAnonymous", webApp.getOverviewAllowAnonymous());
        List<String> claims = webApp.getClaims();
        JSONArray claimsArray = new JSONArray();
        for (String claim : claims) {
            claimsArray.put(claim);
        }
        payload.put("claims", claimsArray);

        return payload.toString();
    }
}
