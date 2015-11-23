/*
 * ​Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.​
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

package org.wso2.appmanager.integration.utils.bean;

/**
 * This class is used to generate the payload for application creation
 */

public class AppCreateRequest extends AbstractRequest {
    private String overview_provider = "admin";
    private String overview_name;
    private String overview_displayName = "Fifa";
    private String overview_context;
    private String overview_version;
    private String overview_transports = "http";
    private String overview_webAppUrl = "www.fifa.com";
    private String overview_description = "sample description";
    private String images_icon;
    private String images_thumbnail;
    private String images_banner;
    private String context;
    private String version;
    private String overview_tier = "Unlimited";
    private String overview_trackingCode;
    private String roles = "";
    private String tags = "";
    private String overview_allowAnonymous = "false";
    private String overview_acsUrl = "";
    private String overview_skipGateway = "false";

    private String uritemplate_policyGroupIds;


    private String uritemplate_urlPattern4 = "/*";
    private String uritemplate_httpVerb4 = "OPTIONS";
    private String uritemplate_policyGroupId4;

    private String uritemplate_urlPattern3 = "/*";
    private String uritemplate_httpVerb3 = "DELETE";
    private String uritemplate_policyGroupId3;

    private String uritemplate_urlPattern2 = "/*";
    private String uritemplate_httpVerb2 = "PUT";
    private String uritemplate_policyGroupId2;

    private String uritemplate_urlPattern1 = "/*";
    private String uritemplate_httpVerb1 = "POST";
    private String uritemplate_policyGroupId1;

    private String uritemplate_urlPattern0 = "/*";
    private String uritemplate_httpVerb0 = "GET";
    private String uritemplate_policyGroupId0;

    private String uritemplate_javaPolicyIds="[]";
    private String overview_logoutUrl;


    private String entitlementPolicies;
    private String autoConfig = "on";
    private String providers = "wso2is-5.0.0";

    private String sso_ssoProvider = "wso2is-5.0.0";
    private String claims = "http://wso2.org/claims/otherphone";


    private String claimPropertyCounter = "1";

    private String claimPropertyName0 = "http://wso2.org/claims/role";
    private String sso_singleSignOn = "Enabled";
    private String sso_idpProviderUrl = "https://localhost:10043/samlsso/";
    private String sso_saml2SsoIssuer;
    private String oauthapis_apiTokenEndpoint1;
    private String oauthapis_apiConsumerKey1;
    private String oauthapis_apiConsumerSecret1;
    private String oauthapis_apiName1;
    private String oauthapis_apiTokenEndpoint2;
    private String oauthapis_apiConsumerKey2;
    private String oauthapis_apiConsumerSecret2;
    private String oauthapis_apiName2;
    private String oauthapis_apiTokenEndpoint3;
    private String oauthapis_apiConsumerKey3;
    private String oauthapis_apiConsumerSecret3;
    private String oauthapis_apiName3;
    private String webapp = "webapp";


    @Override
    public void init() {
        addParameter("overview_provider", overview_provider);
        addParameter("overview_name", overview_name);
        addParameter("overview_displayName", overview_displayName);
        addParameter("overview_context", overview_context);
        addParameter("overview_version", overview_version);
        addParameter("overview_transports", overview_transports);
        addParameter("overview_webAppUrl", overview_webAppUrl);
        addParameter("overview_description", overview_description);
        addParameter("images_icon", images_icon);
        addParameter("images_thumbnail", images_thumbnail);
        addParameter("images_banner", images_banner);
        addParameter("context", context);
        addParameter("version", version);
        addParameter("overview_tier", overview_tier);
        addParameter("overview_trackingCode", overview_trackingCode);
        addParameter("roles", roles);
        addParameter("tags", tags);
        addParameter("overview_allowAnonymous", overview_allowAnonymous);
        addParameter("overview_acsUrl", overview_acsUrl);
        addParameter("overview_skipGateway", overview_skipGateway);

        addParameter("uritemplate_policyGroupIds" , uritemplate_policyGroupIds);

        addParameter("uritemplate_urlPattern4", uritemplate_urlPattern4);
        addParameter("uritemplate_httpVerb4", uritemplate_httpVerb4);
        addParameter("uritemplate_policyGroupId4" , uritemplate_policyGroupId4);

        addParameter("uritemplate_urlPattern3", uritemplate_urlPattern3);
        addParameter("uritemplate_httpVerb3", uritemplate_httpVerb3);
        addParameter("uritemplate_policyGroupId3" , uritemplate_policyGroupId3);

        addParameter("uritemplate_urlPattern2", uritemplate_urlPattern2);
        addParameter("uritemplate_httpVerb2", uritemplate_httpVerb2);
        addParameter("uritemplate_policyGroupId2" , uritemplate_policyGroupId2);

        addParameter("uritemplate_urlPattern1", uritemplate_urlPattern1);
        addParameter("uritemplate_httpVerb1", uritemplate_httpVerb1);
        addParameter("uritemplate_policyGroupId1" , uritemplate_policyGroupId1);

        addParameter("uritemplate_urlPattern0", uritemplate_urlPattern0);
        addParameter("uritemplate_httpVerb0", uritemplate_httpVerb0);
        addParameter("uritemplate_policyGroupId0" , uritemplate_policyGroupId0);

        addParameter("overview_logoutUrl", overview_logoutUrl);
        addParameter("claimPropertyCounter", claimPropertyCounter);
        addParameter("sso_singleSignOn", sso_singleSignOn);
        addParameter("entitlementPolicies", entitlementPolicies);
        addParameter("autoConfig", autoConfig);
        addParameter("providers", providers);
        addParameter("sso_ssoProvider", sso_ssoProvider);
        addParameter("claims", claims);
        addParameter("claimPropertyName0", claimPropertyName0);

        addParameter("uritemplate_javaPolicyIds", uritemplate_javaPolicyIds);

        addParameter("sso_idpProviderUrl", sso_idpProviderUrl);
        addParameter("sso_saml2SsoIssuer", sso_saml2SsoIssuer);
        addParameter("oauthapis_apiTokenEndpoint1", oauthapis_apiTokenEndpoint1);
        addParameter("oauthapis_apiConsumerKey1", oauthapis_apiConsumerKey1);
        addParameter("oauthapis_apiConsumerSecret1", oauthapis_apiConsumerSecret1);
        addParameter("oauthapis_apiName1", oauthapis_apiName1);
        addParameter("oauthapis_apiTokenEndpoint2", oauthapis_apiTokenEndpoint2);
        addParameter("oauthapis_apiConsumerKey2", oauthapis_apiConsumerKey2);
        addParameter("oauthapis_apiConsumerSecret2", oauthapis_apiConsumerSecret2);
        addParameter("oauthapis_apiName2", oauthapis_apiName2);
        addParameter("oauthapis_apiTokenEndpoint3", oauthapis_apiTokenEndpoint3);
        addParameter("oauthapis_apiConsumerKey3", oauthapis_apiConsumerKey3);
        addParameter("oauthapis_apiConsumerSecret3", oauthapis_apiConsumerSecret3);
        addParameter("oauthapis_apiName3", oauthapis_apiName3);
        addParameter("webapp", webapp);

    }

    @Override
    public void setAction() {
    }

    /**
     * App Create Request.
     * @param appName String.
     * @param context String.
     * @param version String.
     * @param trackingCode String.
     */
    public AppCreateRequest(String appName, String context, String version, String trackingCode) {
        this.overview_name = appName;
        this.overview_context = context;
        this.overview_version = version;
        this.overview_trackingCode = trackingCode;
    }

    /**
     * Get Overview provider.
     * @return overview_provider String.
     */
    public String getOverview_provider() {
        return overview_provider;
    }

    /**
     * Set Overview provider.
     * @param overview_provider String.
     */
    public void setOverview_provider(String overview_provider) {
        this.overview_provider = overview_provider;
    }

    /**
     * Get Overview Name
     * @return overview_name String.
     */
    public String getOverview_name() {
        return overview_name;
    }

    /**
     * Set Overview Name.
     * @param overview_name String.
     */
    public void setOverview_name(String overview_name) {
        this.overview_name = overview_name;
    }

    /**
     * Get Overview Context.
     * @return overview_context String.
     */
    public String getOverview_context() {
        return overview_context;
    }

    public void setOverview_context(String overview_context) {
        this.overview_context = overview_context;
    }

    public String getOverview_version() {
        return overview_version;
    }

    public void setOverview_version(String overview_version) {
        this.overview_version = overview_version;
    }

    public String getOverview_transports() {
        return overview_transports;
    }

    public void setOverview_transports(String overview_transports) {
        this.overview_transports = overview_transports;
    }

    public String getOverview_webAppUrl() {
        return overview_webAppUrl;
    }

    public void setOverview_webAppUrl(String overview_webAppUrl) {
        this.overview_webAppUrl = overview_webAppUrl;
    }

    public String getOverview_description() {
        return overview_description;
    }

    public void setOverview_description(String overview_description) {
        this.overview_description = overview_description;
    }

    public String getImages_icon() {
        return images_icon;
    }

    public void setImages_icon(String images_icon) {
        this.images_icon = images_icon;
    }

    public String getImages_thumbnail() {
        return images_thumbnail;
    }

    public void setImages_thumbnail(String images_thumbnail) {
        this.images_thumbnail = images_thumbnail;
    }

    public String getImages_banner() {
        return images_banner;
    }

    public void setImages_banner(String images_banner) {
        this.images_banner = images_banner;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getOverview_tier() {
        return overview_tier;
    }

    public void setOverview_tier(String overview_tier) {
        this.overview_tier = overview_tier;
    }

    public String getOverview_trackingCode() {
        return overview_trackingCode;
    }

    public void setOverview_trackingCode(String overview_trackingCode) {
        this.overview_trackingCode = overview_trackingCode;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getUritemplate_urlPattern4() {
        return uritemplate_urlPattern4;
    }

    public void setUritemplate_urlPattern4(String uritemplate_urlPattern4) {
        this.uritemplate_urlPattern4 = uritemplate_urlPattern4;
    }

    public String getUritemplate_httpVerb4() {
        return uritemplate_httpVerb4;
    }

    public void setUritemplate_httpVerb4(String uritemplate_httpVerb4) {
        this.uritemplate_httpVerb4 = uritemplate_httpVerb4;
    }

    public String getUritemplate_urlPattern3() {
        return uritemplate_urlPattern3;
    }

    public void setUritemplate_urlPattern3(String uritemplate_urlPattern3) {
        this.uritemplate_urlPattern3 = uritemplate_urlPattern3;
    }

    public String getUritemplate_httpVerb3() {
        return uritemplate_httpVerb3;
    }

    public void setUritemplate_httpVerb3(String uritemplate_httpVerb3) {
        this.uritemplate_httpVerb3 = uritemplate_httpVerb3;
    }

    public String getUritemplate_urlPattern2() {
        return uritemplate_urlPattern2;
    }

    public void setUritemplate_urlPattern2(String uritemplate_urlPattern2) {
        this.uritemplate_urlPattern2 = uritemplate_urlPattern2;
    }

    public String getUritemplate_httpVerb2() {
        return uritemplate_httpVerb2;
    }

    public void setUritemplate_httpVerb2(String uritemplate_httpVerb2) {
        this.uritemplate_httpVerb2 = uritemplate_httpVerb2;
    }

    public String getUritemplate_urlPattern1() {
        return uritemplate_urlPattern1;
    }

    public void setUritemplate_urlPattern1(String uritemplate_urlPattern1) {
        this.uritemplate_urlPattern1 = uritemplate_urlPattern1;
    }

    public String getUritemplate_httpVerb1() {
        return uritemplate_httpVerb1;
    }

    public void setUritemplate_httpVerb1(String uritemplate_httpVerb1) {
        this.uritemplate_httpVerb1 = uritemplate_httpVerb1;
    }

    public String getUritemplate_urlPattern0() {
        return uritemplate_urlPattern0;
    }

    public void setUritemplate_urlPattern0(String uritemplate_urlPattern0) {
        this.uritemplate_urlPattern0 = uritemplate_urlPattern0;
    }

    public String getUritemplate_httpVerb0() {
        return uritemplate_httpVerb0;
    }

    public void setUritemplate_httpVerb0(String uritemplate_httpVerb0) {
        this.uritemplate_httpVerb0 = uritemplate_httpVerb0;
    }

    public String getOverview_logoutUrl() {
        return overview_logoutUrl;
    }

    public void setOverview_logoutUrl(String overview_logoutUrl) {
        this.overview_logoutUrl = overview_logoutUrl;
    }

    public String getClaimPropertyCounter() {
        return claimPropertyCounter;
    }

    public void setClaimPropertyCounter(String claimPropertyCounter) {
        this.claimPropertyCounter = claimPropertyCounter;
    }

    public String getSso_singleSignOn() {
        return sso_singleSignOn;
    }

    public void setSso_singleSignOn(String sso_singleSignOn) {
        this.sso_singleSignOn = sso_singleSignOn;
    }

    public String getSso_idpProviderUrl() {
        return sso_idpProviderUrl;
    }

    public void setSso_idpProviderUrl(String sso_idpProviderUrl) {
        this.sso_idpProviderUrl = sso_idpProviderUrl;
    }

    public String getSso_saml2SsoIssuer() {
        return sso_saml2SsoIssuer;
    }

    public void setSso_saml2SsoIssuer(String sso_saml2SsoIssuer) {
        this.sso_saml2SsoIssuer = sso_saml2SsoIssuer;
    }

    public String getOauthapis_apiTokenEndpoint1() {
        return oauthapis_apiTokenEndpoint1;
    }

    public void setOauthapis_apiTokenEndpoint1(String oauthapis_apiTokenEndpoint1) {
        this.oauthapis_apiTokenEndpoint1 = oauthapis_apiTokenEndpoint1;
    }

    public String getOauthapis_apiConsumerKey1() {
        return oauthapis_apiConsumerKey1;
    }

    public void setOauthapis_apiConsumerKey1(String oauthapis_apiConsumerKey1) {
        this.oauthapis_apiConsumerKey1 = oauthapis_apiConsumerKey1;
    }

    public String getOauthapis_apiConsumerSecret1() {
        return oauthapis_apiConsumerSecret1;
    }

    public void setOauthapis_apiConsumerSecret1(String oauthapis_apiConsumerSecret1) {
        this.oauthapis_apiConsumerSecret1 = oauthapis_apiConsumerSecret1;
    }

    public String getOauthapis_apiName1() {
        return oauthapis_apiName1;
    }

    public void setOauthapis_apiName1(String oauthapis_apiName1) {
        this.oauthapis_apiName1 = oauthapis_apiName1;
    }

    public String getOauthapis_apiTokenEndpoint2() {
        return oauthapis_apiTokenEndpoint2;
    }

    public void setOauthapis_apiTokenEndpoint2(String oauthapis_apiTokenEndpoint2) {
        this.oauthapis_apiTokenEndpoint2 = oauthapis_apiTokenEndpoint2;
    }

    public String getOauthapis_apiConsumerKey2() {
        return oauthapis_apiConsumerKey2;
    }

    public void setOauthapis_apiConsumerKey2(String oauthapis_apiConsumerKey2) {
        this.oauthapis_apiConsumerKey2 = oauthapis_apiConsumerKey2;
    }

    public String getOauthapis_apiConsumerSecret2() {
        return oauthapis_apiConsumerSecret2;
    }

    public void setOauthapis_apiConsumerSecret2(String oauthapis_apiConsumerSecret2) {
        this.oauthapis_apiConsumerSecret2 = oauthapis_apiConsumerSecret2;
    }

    public String getOauthapis_apiName2() {
        return oauthapis_apiName2;
    }

    public void setOauthapis_apiName2(String oauthapis_apiName2) {
        this.oauthapis_apiName2 = oauthapis_apiName2;
    }

    public String getOauthapis_apiTokenEndpoint3() {
        return oauthapis_apiTokenEndpoint3;
    }

    public void setOauthapis_apiTokenEndpoint3(String oauthapis_apiTokenEndpoint3) {
        this.oauthapis_apiTokenEndpoint3 = oauthapis_apiTokenEndpoint3;
    }

    public String getOauthapis_apiConsumerKey3() {
        return oauthapis_apiConsumerKey3;
    }

    public void setOauthapis_apiConsumerKey3(String oauthapis_apiConsumerKey3) {
        this.oauthapis_apiConsumerKey3 = oauthapis_apiConsumerKey3;
    }

    public String getOauthapis_apiConsumerSecret3() {
        return oauthapis_apiConsumerSecret3;
    }

    public void setOauthapis_apiConsumerSecret3(String oauthapis_apiConsumerSecret3) {
        this.oauthapis_apiConsumerSecret3 = oauthapis_apiConsumerSecret3;
    }

    public String getOauthapis_apiName3() {
        return oauthapis_apiName3;
    }

    public void setOauthapis_apiName3(String oauthapis_apiName3) {
        this.oauthapis_apiName3 = oauthapis_apiName3;
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public String getUritemplate_policyGroupId4() {
        return uritemplate_policyGroupId4;
    }

    public void setUritemplate_policyGroupId4(String uritemplate_policyGroupId4) {
        this.uritemplate_policyGroupId4 = uritemplate_policyGroupId4;
    }

    public String getUritemplate_policyGroupId2() {
        return uritemplate_policyGroupId2;
    }

    public void setUritemplate_policyGroupId2(String uritemplate_policyGroupId2) {
        this.uritemplate_policyGroupId2 = uritemplate_policyGroupId2;
    }

    public String getUritemplate_policyGroupId3() {
        return uritemplate_policyGroupId3;
    }

    public void setUritemplate_policyGroupId3(String uritemplate_policyGroupId3) {
        this.uritemplate_policyGroupId3 = uritemplate_policyGroupId3;
    }

    public String getUritemplate_policyGroupId1() {
        return uritemplate_policyGroupId1;
    }

    public void setUritemplate_policyGroupId1(String uritemplate_policyGroupId1) {
        this.uritemplate_policyGroupId1 = uritemplate_policyGroupId1;
    }

    public String getUritemplate_policyGroupId0() {
        return uritemplate_policyGroupId0;
    }

    public void setUritemplate_policyGroupId0(String uritemplate_policyGroupId0) {
        this.uritemplate_policyGroupId0 = uritemplate_policyGroupId0;
    }

    public String getUritemplate_policyGroupIds() {
        return uritemplate_policyGroupIds;
    }

    public void setUritemplate_policyGroupIds(String uritemplate_policyGroupIds) {
        this.uritemplate_policyGroupIds = uritemplate_policyGroupIds;
    }

    public String getEntitlementPolicies() {
        return entitlementPolicies;
    }

    public void setEntitlementPolicies(String entitlementPolicies) {
        this.entitlementPolicies = entitlementPolicies;
    }

    public String getSso_ssoProvider() {
        return sso_ssoProvider;
    }

    public void setSso_ssoProvider(String sso_ssoProvider) {
        this.sso_ssoProvider = sso_ssoProvider;
    }

    public String getClaims() {
        return claims;
    }

    public void setClaims(String claims) {
        this.claims = claims;
    }

    public String getOverview_displayName() {
        return overview_displayName;
    }

    public void setOverview_displayName(String overview_displayName) {
        this.overview_displayName = overview_displayName;
    }

    public String getOverview_allowAnonymous() {
        return overview_allowAnonymous;
    }

    public void setOverview_allowAnonymous(String overview_allowAnonymous) {
        this.overview_allowAnonymous = overview_allowAnonymous;
    }

    public String getOverview_acsUrl() {
        return overview_acsUrl;
    }

    public void setOverview_acsUrl(String overview_acsUrl) {
        this.overview_acsUrl = overview_acsUrl;
    }

    public String getOverview_skipGateway() {
        return overview_skipGateway;
    }

    public void setOverview_skipGateway(String overview_skipGateway) {
        this.overview_skipGateway = overview_skipGateway;
    }

    public String getAutoConfig() {
        return autoConfig;
    }

    public void setAutoConfig(String autoConfig) {
        this.autoConfig = autoConfig;
    }

    public String getUritemplate_javaPolicyIds() {
        return uritemplate_javaPolicyIds;
    }

    public void setUritemplate_javaPolicyIds(String uritemplate_javaPolicyIds) {
        this.uritemplate_javaPolicyIds = uritemplate_javaPolicyIds;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(String providers) {
        this.providers = providers;
    }

    public String getClaimPropertyName0() {
        return claimPropertyName0;
    }

    public void setClaimPropertyName0(String claimPropertyName0) {
        this.claimPropertyName0 = claimPropertyName0;
    }

}
