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
package org.wso2.appmanager.integration.utils;

import org.json.JSONObject;
import org.wso2.appmanager.integration.utils.bean.PolicyGroup;
import org.wso2.appmanager.integration.utils.bean.WebApp;
import org.wso2.appmanager.integration.utils.bean.WebAppResource;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.util.ArrayList;
import java.util.List;

public class WebAppUtil {

    public static WebApp createBasicWebApp(String provider, String name, String context,
                                           String version, String backEndUrl, List<WebAppResource> resources)
            throws Exception {
        List<String> uriTemplatePolicyGroupIds = new ArrayList<String>();
        for (WebAppResource resource : resources) {
            if (!uriTemplatePolicyGroupIds.contains(resource.getPolicyGroupId())) {
                uriTemplatePolicyGroupIds.add(resource.getPolicyGroupId());
            }
        }
        List<WebAppResource> webAppResources = resources;
        List<String> claims = new ArrayList<String>();
        claims.add("http%3A%2F%2Fwso2.org%2Fclaims%2Frole");

        WebApp webApp = new WebApp();
        webApp.setBusinessOwner(null);
        webApp.setOverviewProvider(provider);
        webApp.setOverviewName(name);
        webApp.setOverviewDisplayName(name);
        webApp.setOverviewContext(context);
        webApp.setOverviewVersion(version);
        webApp.setOverviewTransports("http");
        webApp.setOverviewWebAppUrl(backEndUrl);
        webApp.setOverviewTier("Unlimited");
        webApp.setOverviewAllowAnonymous("false");
        webApp.setOverviewSkipGateway("false");
        webApp.setOverviewAcsUrl("");
        webApp.setOverviewTreatAsASite("false");
        webApp.setOverviewAdvertiseOnly("false");
        webApp.setOverviewSubscriptionAvailability("current_tenant");
        webApp.setOptradio("on");
        webApp.setAutoConfig("on");
        webApp.setProviders("wso2is-5.0.0");
        webApp.setSsoProvider("wso2is-5.0.0");
        webApp.setSingleSignOn("Enabled");
        webApp.setWebapp("webapp");
        webApp.setUriTemplateJavaPolicyIds(new ArrayList<String>());// for stats enable
        webApp.setUriTemplatePolicyGroupIds(uriTemplatePolicyGroupIds);
        webApp.setWebAppResources(webAppResources);
        webApp.setClaims(claims);
        return webApp;
    }

    public static PolicyGroup createDefaultPolicy() {
        PolicyGroup defaultPolicy = new PolicyGroup();
        defaultPolicy.setPolicyGroupName("Default");
        defaultPolicy.setThrottlingTier("Unlimited");
        defaultPolicy.setUserRoles(new ArrayList<String>());
        defaultPolicy.setAnonymousAccessToUrlPattern("false");
        defaultPolicy.setObjPartialMappings("");
        defaultPolicy.setPolicyGroupDesc("default policy");
        return defaultPolicy;
    }

    public static List<WebAppResource> createDefaultResources(String defaultPolicyId) {
        List<WebAppResource> webAppResources = new ArrayList<WebAppResource>();
        WebAppResource defaultGetResource = new WebAppResource("GET", "/*", defaultPolicyId);
        WebAppResource defaultPostResource = new WebAppResource("POST", "/*", defaultPolicyId);
        WebAppResource defaultPutResource = new WebAppResource("PUT", "/*", defaultPolicyId);
        WebAppResource defaultDeleteResource = new WebAppResource("DELETE", "/*", defaultPolicyId);
        webAppResources.add(defaultGetResource);
        webAppResources.add(defaultPostResource);
        webAppResources.add(defaultDeleteResource);
        webAppResources.add(defaultPutResource);

        return webAppResources;
    }

    public static String getPolicyId(HttpResponse httpResponse) throws Exception {
        String httpResponseData = httpResponse.getData();
        //TODO- check for content type application/json
        if (httpResponse.getResponseCode() != 200) {
            throw new Exception("http code mismatched");
        }
        JSONObject responseData = new JSONObject(httpResponseData);
        String response = responseData.getString(AppmTestConstants.RESPONSE);
        JSONObject responseObject = new JSONObject(response);
        return responseObject.getString(AppmTestConstants.ID);
    }


}
