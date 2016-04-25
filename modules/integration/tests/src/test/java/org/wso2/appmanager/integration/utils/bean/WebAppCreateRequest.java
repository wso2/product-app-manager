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

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class WebAppCreateRequest extends AbstractRequest {
    private WebApp webapp;

    public WebAppCreateRequest(WebApp webApp) {
        this.webapp = webApp;
    }

    @Override
    public void setAction() {

    }

    @Override
    public void init() {
        addParameter("overview_provider", checkValue(webapp.getOverviewProvider()));
        addParameter("overview_name", checkValue(webapp.getOverviewName()));
        addParameter("overview_displayName", checkValue(webapp.getOverviewDisplayName()));
        addParameter("overview_context", checkValue(webapp.getOverviewContext()));
        addParameter("overview_version", checkValue(webapp.getOverviewVersion()));
        addParameter("overview_transports", checkValue(webapp.getOverviewTransports()));
        addParameter("overview_webAppUrl", checkValue(webapp.getOverviewWebAppUrl()));
        addParameter("overview_description", checkValue(webapp.getOverviewDescription()));
        addParameter("overview_tier", checkValue(webapp.getOverviewTier()));
        addParameter("overview_trackingCode", checkValue(webapp.getOverviewTrackingCode()));
        addParameter("overview_treatAsASite", checkValue(webapp.getOverviewTreatAsASite()));
        addParameter("overview_allowAnonymous", checkValue(webapp.getOverviewAllowAnonymous()));
        addParameter("overview_acsUrl", checkValue(webapp.getOverviewAcsUrl()));
        addParameter("overview_skipGateway", checkValue(webapp.getOverviewSkipGateway()));
        addParameter("overview_logoutUrl", checkValue(webapp.getOverviewLogoutUrl()));
        addParameter("overview_redirectURL", checkValue(webapp.getOverviewRedirectUrl()));
        addParameter("overview_advertiseOnly", checkValue(webapp.getOverviewAdvertiseOnly()));
        addParameter("overview_appOwner", checkValue(webapp.getOverviewAppOwner()));
        addParameter("overview_appTenant", checkValue(webapp.getOverviewAppTenant()));
        addParameter("overview_advertisedAppUUID", "");
        addParameter("overview_subscriptionAvailability", checkValue(webapp.getOverviewSubscriptionAvailability()));
        addParameter("overview_tenants", checkValue(StringUtils.join(webapp.getTenants(), ",")));
        addParameter("overview_visibleRoles", checkValue(StringUtils.join(webapp.getRoles(), ",")));
        addParameter("overview_businessOwner", null);

        addParameter("images_thumbnail", checkValue(webapp.getImagesThumbnail()));
        addParameter("images_banner", checkValue(webapp.getImagesBanner()));
        //addParameter("tags", checkValue(webapp.getTags()));
        String javaPolicyIds = "[" + StringUtils.join(webapp.getUriTemplateJavaPolicyIds(), ",") + "]";
        addParameter("uritemplate_javaPolicyIds", checkValue(javaPolicyIds));
        String uriTemplatePolicyGroupIds = "[" + StringUtils.join(webapp.getUriTemplatePolicyGroupIds(), ",") + "]";
        addParameter("uritemplate_policyGroupIds", checkValue(uriTemplatePolicyGroupIds));


        addParameter("sso_singleSignOn", checkValue(webapp.getSingleSignOn()));
        addParameter("entitlementPolicies", checkValue(webapp.getEntitlementPolicies()));
        addParameter("autoConfig", checkValue(webapp.getAutoConfig()));
        addParameter("providers", checkValue(webapp.getProviders()));
        addParameter("sso_ssoProvider", checkValue(webapp.getSsoProvider()));
        addParameter("sso_idpProviderUrl", checkValue(webapp.getSsoIdpProviderUrl()));
        addParameter("sso_saml2SsoIssuer", checkValue(webapp.getSsoSaml2SsoIssuer()));
        addParameter("webapp", checkValue(webapp.getWebapp()));

        addParameter("oauthapis_apiTokenEndpoint1", checkValue(webapp.getOauthApiTokenEndpoint1()));
        addParameter("oauthapis_apiConsumerKey1", checkValue(webapp.getOauthApiConsumerKey1()));
        addParameter("oauthapis_apiConsumerSecret1", checkValue(webapp.getOauthApiConsumerSecret1()));
        addParameter("oauthapis_apiName1", checkValue(webapp.getOauthApiName1()));
        addParameter("oauthapis_apiTokenEndpoint2", checkValue(webapp.getOauthApiTokenEndpoint2()));
        addParameter("oauthapis_apiConsumerKey2", checkValue(webapp.getOauthApiConsumerKey2()));
        addParameter("oauthapis_apiConsumerSecret2", checkValue(webapp.getOauthApiConsumerSecret2()));
        addParameter("oauthapis_apiName2", checkValue(webapp.getOauthApiName2()));
        addParameter("oauthapis_apiTokenEndpoint3", checkValue(webapp.getOauthApiTokenEndpoint3()));
        addParameter("oauthapis_apiConsumerKey3", checkValue(webapp.getOauthApiConsumerKey3()));
        addParameter("oauthapis_apiConsumerSecret3", checkValue(webapp.getOauthApiConsumerSecret3()));
        addParameter("oauthapis_apiName3", checkValue(webapp.getOauthApiName3()));

        List<WebAppResource> webAppResources = webapp.getWebAppResources();
        for (int i = 0; i < webAppResources.size(); i++) {
            WebAppResource tempResource = webAppResources.get(i);
            addParameter("uritemplate_urlPattern" + i, checkValue(tempResource.getUrlPattern()));
            addParameter("uritemplate_httpVerb" + i, checkValue(tempResource.getHttpVerb()));
            addParameter("uritemplate_policyGroupId" + i, checkValue(tempResource.getPolicyGroupId()));
        }

//        addParameter("claims", checkValue(webapp.getOverviewProvider()));
//        addParameter("claimPropertyName0", checkValue(webapp.getOverviewProvider()));
//        addParameter("claimPropertyCounter", checkValue(webapp.getClaimPropertyCounter()));
    }

    private String checkValue(String input) {
        return input != null ? input : "";

    }
}
