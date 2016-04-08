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
    private String overviewProvider = "admin";
    private String overviewName;
    private String overviewDisplayName = "Fifa";
    private String overviewContext;
    private String overviewVersion;
    private String overviewTransports = "http";
    private String overviewWebAppUrl = "www.fifa.com";
    private String overviewDescription = "sample description";
    private String imagesIcon;
    private String imagesThumbnail;
    private String imagesBanner;
    private String context;
    private String version;
    private String overviewTier = "Unlimited";
    private String overviewTrackingCode;
    private String roles = "";
    private String tags = "";
    private String overviewTreatAsASite = "false";
    private String overviewAllowAnonymous = "false";
    private String overviewAcsUrl = "";
    private String overviewSkipGateway = "false";

    private String uriTemplatePolicyGroupIds;


    private String uriTemplateUrlPattern4 = "/*";
    private String uriTemplateHttpVerb4 = "OPTIONS";
    private String uriTemplatePolicyGroupId4;

    private String uriTemplateUrlPattern3 = "/*";
    private String uriTemplateHttpVerb3 = "DELETE";
    private String uriTemplatePolicyGroupId3;

    private String uriTemplateUrlPattern2 = "/*";
    private String uriTemplateHttpVerb2 = "PUT";
    private String uriTemplatePolicyGroupId2;

    private String uriTemplateUrlPattern1 = "/*";
    private String uriTemplateHttpVerb1 = "POST";
    private String uriTemplatePolicyGroupId1;

    private String uriTemplateUrlPattern0 = "/*";
    private String uriTemplateHttpVerb0 = "GET";
    private String uriTemplatePolicyGroupId0;

    private String uriTemplateJavaPolicyIds="[]";
    private String overviewLogoutUrl;


    private String entitlementPolicies;
    private String autoConfig = "on";
    private String providers = "wso2is-5.0.0";

    private String ssoProvider = "wso2is-5.0.0";
    private String claims = "http://wso2.org/claims/otherphone";


    private String claimPropertyCounter = "1";

    private String claimPropertyName0 = "http://wso2.org/claims/role";
    private String singleSignOn = "Enabled";
    private String ssoIdpProviderUrl = "https://localhost:10043/samlsso/";
    private String ssoSaml2SsoIssuer;
    private String oauthApiTokenEndpoint1;
    private String oauthApiConsumerKey1;
    private String oauthApiConsumerSecret1;
    private String oauthApiName1;
    private String oauthApiTokenEndpoint2;
    private String oauthApiConsumerKey2;
    private String oauthApiConsumerSecret2;
    private String oauthApiName2;
    private String oauthApiTokenEndpoint3;
    private String oauthApiConsumerKey3;
    private String oauthApiConsumerSecret3;
    private String oauthApiName3;
    private String webapp = "webapp";
    private String businessOwner = null;


    @Override
    public void init() {
        addParameter("overview_provider", overviewProvider);
        addParameter("overview_name", overviewName);
        addParameter("overview_displayName", overviewDisplayName);
        addParameter("overview_context", overviewContext);
        addParameter("overview_version", overviewVersion);
        addParameter("overview_transports", overviewTransports);
        addParameter("overview_webAppUrl", overviewWebAppUrl);
        addParameter("overview_description", overviewDescription);
        addParameter("images_icon", imagesIcon);
        addParameter("images_thumbnail", imagesThumbnail);
        addParameter("images_banner", imagesBanner);
        addParameter("context", context);
        addParameter("version", version);
        addParameter("overview_tier", overviewTier);
        addParameter("overview_trackingCode", overviewTrackingCode);
        addParameter("roles", roles);
        addParameter("tags", tags);
        addParameter("overview_treatAsASite", overviewTreatAsASite);
        addParameter("overview_allowAnonymous", overviewAllowAnonymous);
        addParameter("overview_acsUrl", overviewAcsUrl);
        addParameter("overview_skipGateway", overviewSkipGateway);
        addParameter("overview_owner", businessOwner);

        addParameter("uritemplate_policyGroupIds" , uriTemplatePolicyGroupIds);

        addParameter("uritemplate_urlPattern4", uriTemplateUrlPattern4);
        addParameter("uritemplate_httpVerb4", uriTemplateHttpVerb4);
        addParameter("uritemplate_policyGroupId4" , uriTemplatePolicyGroupId4);

        addParameter("uritemplate_urlPattern3", uriTemplateUrlPattern3);
        addParameter("uritemplate_httpVerb3", uriTemplateHttpVerb3);
        addParameter("uritemplate_policyGroupId3" , uriTemplatePolicyGroupId3);

        addParameter("uritemplate_urlPattern2", uriTemplateUrlPattern2);
        addParameter("uritemplate_httpVerb2", uriTemplateHttpVerb2);
        addParameter("uritemplate_policyGroupId2" , uriTemplatePolicyGroupId2);

        addParameter("uritemplate_urlPattern1", uriTemplateUrlPattern1);
        addParameter("uritemplate_httpVerb1", uriTemplateHttpVerb1);
        addParameter("uritemplate_policyGroupId1" , uriTemplatePolicyGroupId1);

        addParameter("uritemplate_urlPattern0", uriTemplateUrlPattern0);
        addParameter("uritemplate_httpVerb0", uriTemplateHttpVerb0);
        addParameter("uritemplate_policyGroupId0" , uriTemplatePolicyGroupId0);

        addParameter("overview_logoutUrl", overviewLogoutUrl);
        addParameter("claimPropertyCounter", claimPropertyCounter);
        addParameter("sso_singleSignOn", singleSignOn);
        addParameter("entitlementPolicies", entitlementPolicies);
        addParameter("autoConfig", autoConfig);
        addParameter("providers", providers);
        addParameter("sso_ssoProvider", ssoProvider);
        addParameter("claims", claims);
        addParameter("claimPropertyName0", claimPropertyName0);

        addParameter("uritemplate_javaPolicyIds", uriTemplateJavaPolicyIds);

        addParameter("sso_idpProviderUrl", ssoIdpProviderUrl);
        addParameter("sso_saml2SsoIssuer", ssoSaml2SsoIssuer);
        addParameter("oauthapis_apiTokenEndpoint1", oauthApiTokenEndpoint1);
        addParameter("oauthapis_apiConsumerKey1", oauthApiConsumerKey1);
        addParameter("oauthapis_apiConsumerSecret1", oauthApiConsumerSecret1);
        addParameter("oauthapis_apiName1", oauthApiName1);
        addParameter("oauthapis_apiTokenEndpoint2", oauthApiTokenEndpoint2);
        addParameter("oauthapis_apiConsumerKey2", oauthApiConsumerKey2);
        addParameter("oauthapis_apiConsumerSecret2", oauthApiConsumerSecret2);
        addParameter("oauthapis_apiName2", oauthApiName2);
        addParameter("oauthapis_apiTokenEndpoint3", oauthApiTokenEndpoint3);
        addParameter("oauthapis_apiConsumerKey3", oauthApiConsumerKey3);
        addParameter("oauthapis_apiConsumerSecret3", oauthApiConsumerSecret3);
        addParameter("oauthapis_apiName3", oauthApiName3);
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
        this.overviewName = appName;
        this.overviewContext = context;
        this.overviewVersion = version;
        this.overviewTrackingCode = trackingCode;
    }

    /**
     * Get Images Icon.
     * @return imageIcon String.
     */
    public String getImagesIcon() {
        return imagesIcon;
    }

    /**
     * Set Images Icon.
     * @param imagesIcon String.
     */
    public void setImagesIcon(String imagesIcon) {
        this.imagesIcon = imagesIcon;
    }

    /**
     * Get Overview Provider.
     * @return overviewProvider String.
     */
    public String getOverviewProvider() {
        return overviewProvider;
    }

    /**
     *
     * @return
     */
    public String getBusinessOwner() {
        return businessOwner;
    }

    /**
     *
     * @param businessOwner
     */
    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    /**
     * Set Overview Provider.
     * @param overviewProvider String.
     */
    public void setOverviewProvider(String overviewProvider) {
        this.overviewProvider = overviewProvider;
    }

    /**
     * Get Overview Name.
     * @return overviewName String.
     */
    public String getOverviewName() {
        return overviewName;
    }

    /**
     * Set Overview Name.
     * @param overviewName String.
     */
    public void setOverviewName(String overviewName) {
        this.overviewName = overviewName;
    }

    /**
     * Get Overview Display Name.
     * @return overviewDisplayName String.
     */
    public String getOverviewDisplayName() {
        return overviewDisplayName;
    }

    /**
     * Set Overview Display Name.
     * @param overviewDisplayName String.
     */
    public void setOverviewDisplayName(String overviewDisplayName) {
        this.overviewDisplayName = overviewDisplayName;
    }

    /**
     * Get Overview Context.
     * @return overviewContext String.
     */
    public String getOverviewContext() {
        return overviewContext;
    }

    /**
     * Set Overview Context.
     * @param overviewContext String.
     */
    public void setOverviewContext(String overviewContext) {
        this.overviewContext = overviewContext;
    }

    /**
     * Get Overview Version.
     * @return overviewVersion String.
     */
    public String getOverviewVersion() {
        return overviewVersion;
    }

    /**
     * Set Overview Version.
     * @param overviewVersion String.
     */
    public void setOverviewVersion(String overviewVersion) {
        this.overviewVersion = overviewVersion;
    }

    /**
     * Get Overview Transports.
     * @return overviewTransports String.
     */
    public String getOverviewTransports() {
        return overviewTransports;
    }

    /**
     * Set Overview Transports.
     * @param overviewTransports String.
     */
    public void setOverviewTransports(String overviewTransports) {
        this.overviewTransports = overviewTransports;
    }

    /**
     * Get Overview WebApp Url.
     * @return overviewWebAppUrl String.
     */
    public String getOverviewWebAppUrl() {
        return overviewWebAppUrl;
    }

    /**
     * Set Overview WebApp Url.
     * @param overviewWebAppUrl String.
     */
    public void setOverviewWebAppUrl(String overviewWebAppUrl) {
        this.overviewWebAppUrl = overviewWebAppUrl;
    }

    /**
     * Get Overview Description.
     * @return overviewDescription String.
     */
    public String getOverviewDescription() {
        return overviewDescription;
    }

    /**
     * Set Overview Description.
     * @param overviewDescription String.
     */
    public void setOverviewDescription(String overviewDescription) {
        this.overviewDescription = overviewDescription;
    }

    /**
     * Get Images Thumbnail.
     * @return imagesThumbnail String.
     */
    public String getImagesThumbnail() {
        return imagesThumbnail;
    }

    /**
     * Set Images Thumbnail.
     * @param imagesThumbnail String.
     */
    public void setImagesThumbnail(String imagesThumbnail) {
        this.imagesThumbnail = imagesThumbnail;
    }

    /**
     * Get Images Banner.
     * @return imagesBanner String.
     */
    public String getImagesBanner() {
        return imagesBanner;
    }

    /**
     * Set Images Banner.
     * @param imagesBanner String.
     */
    public void setImagesBanner(String imagesBanner) {
        this.imagesBanner = imagesBanner;
    }

    /**
     * Get Context.
     * @return context String.
     */
    public String getContext() {
        return context;
    }

    /**
     * Set Context.
     * @param context String.
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Get Version.
     * @return version String.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set Version.
     * @param version String.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Get Overview Tier String.
     * @return overviewTier String.
     */
    public String getOverviewTier() {
        return overviewTier;
    }

    /**
     * Set Overview Tier String.
     * @param overviewTier String.
     */
    public void setOverviewTier(String overviewTier) {
        this.overviewTier = overviewTier;
    }

    /**
     * Get Overview Tracking Code.
     * @return overviewTrackingCode String.
     */
    public String getOverviewTrackingCode() {
        return overviewTrackingCode;
    }

    /**
     * Set Overview Tracking Code.
     * @param overviewTrackingCode String.
     */
    public void setOverviewTrackingCode(String overviewTrackingCode) {
        this.overviewTrackingCode = overviewTrackingCode;
    }

    /**
     * Get Roles.
     * @return roles String.
     */
    public String getRoles() {
        return roles;
    }

    /**
     * Set Roles.
     * @param roles String.
     */
    public void setRoles(String roles) {
        this.roles = roles;
    }

    /**
     * Get Tags.
     * @return tags String.
     */
    public String getTags() {
        return tags;
    }

    /**
     * Set Tags.
     * @param tags String.
     */
    public void setTags(String tags) {
        this.tags = tags;
    }

    /**
     * Get Overview Allow Anonymous.
     * @return overviewAllowAnonymous String.
     */
    public String getOverviewAllowAnonymous() {
        return overviewAllowAnonymous;
    }

    /**
     * Set Overview Allow Anonymous.
     * @param overviewAllowAnonymous String.
     */
    public void setOverviewAllowAnonymous(String overviewAllowAnonymous) {
        this.overviewAllowAnonymous = overviewAllowAnonymous;
    }

    /**
     * Get Overview Acs Url.
     * @return overviewAcsUrl String.
     */
    public String getOverviewAcsUrl() {
        return overviewAcsUrl;
    }

    /**
     * Set Overview Acs Url.
     * @param overviewAcsUrl String.
     */
    public void setOverviewAcsUrl(String overviewAcsUrl) {
        this.overviewAcsUrl = overviewAcsUrl;
    }

    /**
     * Get Overview Skip Gateway enable or disable.
     * @return overviewSkipGateway String.
     */
    public String getOverviewSkipGateway() {
        return overviewSkipGateway;
    }

    /**
     * Set Overview Skip Gateway enable or disable.
     * @param overviewSkipGateway String.
     */
    public void setOverviewSkipGateway(String overviewSkipGateway) {
        this.overviewSkipGateway = overviewSkipGateway;
    }

    /**
     * Get Uri Template Policy Group Ids.
     * @return uriTemplatePolicyGroupIds String.
     */
    public String getUriTemplatePolicyGroupIds() {
        return uriTemplatePolicyGroupIds;
    }

    /**
     * Set Uri Template Policy Group Ids.
     * @param uriTemplatePolicyGroupIds String.
     */
    public void setUriTemplatePolicyGroupIds(String uriTemplatePolicyGroupIds) {
        this.uriTemplatePolicyGroupIds = uriTemplatePolicyGroupIds;
    }

    /**
     * Get Uri Template Url Pattern4.
     * @return getUriTemplateUrlPattern4 String.
     */
    public String getUriTemplateUrlPattern4() {
        return uriTemplateUrlPattern4;
    }

    /**
     * Set Uri Template Url Pattern4.
     * @param uriTemplateUrlPattern4 String.
     */
    public void setUriTemplateUrlPattern4(String uriTemplateUrlPattern4) {
        this.uriTemplateUrlPattern4 = uriTemplateUrlPattern4;
    }

    /**
     * Get Uri Template Http Verb4.
     * @return uriTemplateHttpVerb4 String.
     */
    public String getUriTemplateHttpVerb4() {
        return uriTemplateHttpVerb4;
    }

    /**
     * Set Uri Template Http Verb4.
     * @param uriTemplateHttpVerb4 String.
     */
    public void setUriTemplateHttpVerb4(String uriTemplateHttpVerb4) {
        this.uriTemplateHttpVerb4 = uriTemplateHttpVerb4;
    }

    /**
     * Get Uri Template Policy Group Id4.
     * @return uriTemplatePolicyGroupId4 String.
     */
    public String getUriTemplatePolicyGroupId4() {
        return uriTemplatePolicyGroupId4;
    }

    /**
     * Set Uri Template Policy Group Id4.
     * @param uriTemplatePolicyGroupId4 String.
     */
    public void setUriTemplatePolicyGroupId4(String uriTemplatePolicyGroupId4) {
        this.uriTemplatePolicyGroupId4 = uriTemplatePolicyGroupId4;
    }

    /**
     * Get Uri Template Url Pattern3.
     * @return getUriTemplateUrlPattern3 String.
     */
    public String getUriTemplateUrlPattern3() {
        return uriTemplateUrlPattern3;
    }

    /**
     * Set Uri Template Url Pattern3.
     * @param uriTemplateUrlPattern3 String.
     */
    public void setUriTemplateUrlPattern3(String uriTemplateUrlPattern3) {
        this.uriTemplateUrlPattern3 = uriTemplateUrlPattern3;
    }

    /**
     * Get Uri Template Http Verb3.
     * @return uriTemplateHttpVerb3 String.
     */
    public String getUriTemplateHttpVerb3() {
        return uriTemplateHttpVerb3;
    }

    /**
     * Set Uri Template Http Verb3.
     * @param uriTemplateHttpVerb3 String.
     */
    public void setUriTemplateHttpVerb3(String uriTemplateHttpVerb3) {
        this.uriTemplateHttpVerb3 = uriTemplateHttpVerb3;
    }

    /**
     * Get Uri Template Policy Group Id3.
     * @return uriTemplatePolicyGroupId3 String.
     */
    public String getUriTemplatePolicyGroupId3() {
        return uriTemplatePolicyGroupId3;
    }

    /**
     * Set Uri Template Policy Group Id3.
     * @param uriTemplatePolicyGroupId3 String.
     */
    public void setUriTemplatePolicyGroupId3(String uriTemplatePolicyGroupId3) {
        this.uriTemplatePolicyGroupId3 = uriTemplatePolicyGroupId3;
    }

    /**
     * Get Uri Template Url Pattern2.
     * @return getUriTemplateUrlPattern2 String.
     */
    public String getUriTemplateUrlPattern2() {
        return uriTemplateUrlPattern2;
    }

    /**
     * Set Uri Template Url Pattern2.
     * @param uriTemplateUrlPattern2 String.
     */
    public void setUriTemplateUrlPattern2(String uriTemplateUrlPattern2) {
        this.uriTemplateUrlPattern2 = uriTemplateUrlPattern2;
    }

    /**
     * Get Uri Template Http Verb2.
     * @return uriTemplateHttpVerb2 String.
     */
    public String getUriTemplateHttpVerb2() {
        return uriTemplateHttpVerb2;
    }

    /**
     * Set Uri Template Http Verb2.
     * @param uriTemplateHttpVerb2 String.
     */
    public void setUriTemplateHttpVerb2(String uriTemplateHttpVerb2) {
        this.uriTemplateHttpVerb2 = uriTemplateHttpVerb2;
    }

    /**
     * Get Uri Template Policy Group Id2.
     * @return uriTemplatePolicyGroupId2 String.
     */
    public String getUriTemplatePolicyGroupId2() {
        return uriTemplatePolicyGroupId2;
    }

    /**
     * Set Uri Template Policy Group Id2.
     * @param uriTemplatePolicyGroupId2 String.
     */
    public void setUriTemplatePolicyGroupId2(String uriTemplatePolicyGroupId2) {
        this.uriTemplatePolicyGroupId2 = uriTemplatePolicyGroupId2;
    }

    /**
     * Get Uri Template Url Pattern1.
     * @return getUriTemplateUrlPattern1 String.
     */
    public String getUriTemplateUrlPattern1() {
        return uriTemplateUrlPattern1;
    }

    /**
     * Set Uri Template Url Pattern1.
     * @param uriTemplateUrlPattern1 String.
     */
    public void setUriTemplateUrlPattern1(String uriTemplateUrlPattern1) {
        this.uriTemplateUrlPattern1 = uriTemplateUrlPattern1;
    }

    /**
     * Get Uri Template Http Verb1.
     * @return uriTemplateHttpVerb1 String.
     */
    public String getUriTemplateHttpVerb1() {
        return uriTemplateHttpVerb1;
    }

    /**
     * Set Uri Template Http Verb1.
     * @param uriTemplateHttpVerb1 String.
     */
    public void setUriTemplateHttpVerb1(String uriTemplateHttpVerb1) {
        this.uriTemplateHttpVerb1 = uriTemplateHttpVerb1;
    }

    /**
     * Get Uri Template Policy Group Id1.
     * @return uriTemplatePolicyGroupId1 String.
     */
    public String getUriTemplatePolicyGroupId1() {
        return uriTemplatePolicyGroupId1;
    }

    /**
     * Set Uri Template Policy Group Id1.
     * @param uriTemplatePolicyGroupId1 String.
     */
    public void setUriTemplatePolicyGroupId1(String uriTemplatePolicyGroupId1) {
        this.uriTemplatePolicyGroupId1 = uriTemplatePolicyGroupId1;
    }

    /**
     * Get Uri Template Url Pattern0.
     * @return getUriTemplateUrlPattern0 String.
     */
    public String getUriTemplateUrlPattern0() {
        return uriTemplateUrlPattern0;
    }

    /**
     * Set Uri Template Url Pattern0.
     * @param uriTemplateUrlPattern0 String.
     */
    public void setUriTemplateUrlPattern0(String uriTemplateUrlPattern0) {
        this.uriTemplateUrlPattern0 = uriTemplateUrlPattern0;
    }

    /**
     * Get Uri Template Http Verb0.
     * @return uriTemplateHttpVerb0 String.
     */
    public String getUriTemplateHttpVerb0() {
        return uriTemplateHttpVerb0;
    }

    /**
     * Set Uri Template Http Verb0.
     * @param uriTemplateHttpVerb0 String.
     */
    public void setUriTemplateHttpVerb0(String uriTemplateHttpVerb0) {
        this.uriTemplateHttpVerb0 = uriTemplateHttpVerb0;
    }

    /**
     * Get Uri Template Policy Group Id0.
     * @return uriTemplatePolicyGroupId0 String.
     */
    public String getUriTemplatePolicyGroupId0() {
        return uriTemplatePolicyGroupId0;
    }

    /**
     * Set Uri Template Policy Group Id0.
     * @param uriTemplatePolicyGroupId0 String.
     */
    public void setUriTemplatePolicyGroupId0(String uriTemplatePolicyGroupId0) {
        this.uriTemplatePolicyGroupId0 = uriTemplatePolicyGroupId0;
    }

    /**
     * Get Uri Template Java Policy Ids.
     * @return uriTemplateJavaPolicyIds String.
     */
    public String getUriTemplateJavaPolicyIds() {
        return uriTemplateJavaPolicyIds;
    }

    /**
     * Set Uri Template Java Policy Ids.
     * @param uriTemplateJavaPolicyIds String.
     */
    public void setUriTemplateJavaPolicyIds(String uriTemplateJavaPolicyIds) {
        this.uriTemplateJavaPolicyIds = uriTemplateJavaPolicyIds;
    }

    /**
     * Get overview LogoutUrl
     * @return overviewLogoutUrl String.
     */
    public String getOverviewLogoutUrl() {
        return overviewLogoutUrl;
    }

    /**
     * Set overview LogoutUrl.
     * @param overviewLogoutUrl String.
     */
    public void setOverviewLogoutUrl(String overviewLogoutUrl) {
        this.overviewLogoutUrl = overviewLogoutUrl;
    }

    /**
     * Get Entitlement Policies.
     * @return entitlementPolicies String.
     */
    public String getEntitlementPolicies() {
        return entitlementPolicies;
    }

    /**
     * Set Entitlement Policies.
     * @param entitlementPolicies String.
     */
    public void setEntitlementPolicies(String entitlementPolicies) {
        this.entitlementPolicies = entitlementPolicies;
    }

    /**
     * Get Auto Configs.
     * @return autoConfig String.
     */
    public String getAutoConfig() {
        return autoConfig;
    }

    /**
     * Set Auto Configs.
     * @param autoConfig String.
     */
    public void setAutoConfig(String autoConfig) {
        this.autoConfig = autoConfig;
    }

    /**
     * Get Providers.
     * @return providers String.
     */
    public String getProviders() {
        return providers;
    }

    /**
     * Set Providers.
     * @param providers String.
     */
    public void setProviders(String providers) {
        this.providers = providers;
    }

    /**
     * Get SSO Provider.
     * @return ssoProvider String.
     */
    public String getSsoProvider() {
        return ssoProvider;
    }

    /**
     * Set SSO Provider.
     * @param ssoProvider String.
     */
    public void setSsoProvider(String ssoProvider) {
        this.ssoProvider = ssoProvider;
    }

    /**
     * Get Claims.
     * @return claims String.
     */
    public String getClaims() {
        return claims;
    }

    /**
     * Set Claims.
     * @param claims String.
     */
    public void setClaims(String claims) {
        this.claims = claims;
    }

    /**
     * Get Claim Property Counter.
     * @return claimPropertyCounter String.
     */
    public String getClaimPropertyCounter() {
        return claimPropertyCounter;
    }

    /**
     * Set Claim Property Counter.
     * @param claimPropertyCounter String.
     */
    public void setClaimPropertyCounter(String claimPropertyCounter) {
        this.claimPropertyCounter = claimPropertyCounter;
    }

    /**
     * Get Claim Property Name.
     * @return claimPropertyName0 String.
     */
    public String getClaimPropertyName0() {
        return claimPropertyName0;
    }

    /**
     * Set Claim Property Name.
     * @param claimPropertyName0 String.
     */
    public void setClaimPropertyName0(String claimPropertyName0) {
        this.claimPropertyName0 = claimPropertyName0;
    }

    /**
     * Get Single Sign On.
     * @return singleSignOn String.
     */
    public String getSingleSignOn() {
        return singleSignOn;
    }

    /**
     * Set Single Sign On.
     * @param singleSignOn String.
     */
    public void setSingleSignOn(String singleSignOn) {
        this.singleSignOn = singleSignOn;
    }

    /**
     * Get Sso Idp Provider Url.
     * @return ssoIdpProviderUrl String.
     */
    public String getSsoIdpProviderUrl() {
        return ssoIdpProviderUrl;
    }

    /**
     * Set Sso Idp Provider Url.
     * @param ssoIdpProviderUrl String.
     */
    public void setSsoIdpProviderUrl(String ssoIdpProviderUrl) {
        this.ssoIdpProviderUrl = ssoIdpProviderUrl;
    }

    /**
     * Get Sso Saml2Sso Issuer.
     * @return ssoSaml2SsoIssuer String.
     */
    public String getSsoSaml2SsoIssuer() {
        return ssoSaml2SsoIssuer;
    }

    /**
     * Set Sso Saml2Sso Issuer.
     * @param ssoSaml2SsoIssuer String.
     */
    public void setSsoSaml2SsoIssuer(String ssoSaml2SsoIssuer) {
        this.ssoSaml2SsoIssuer = ssoSaml2SsoIssuer;
    }

    /**
     * Get Oauth Api Token Endpoint1.
     * @return oauthApiTokenEndpoint1 String.
     */
    public String getOauthApiTokenEndpoint1() {
        return oauthApiTokenEndpoint1;
    }

    /**
     * Set Oauth Api Token Endpoint1.
     * @param oauthApiTokenEndpoint1 String.
     */
    public void setOauthApiTokenEndpoint1(String oauthApiTokenEndpoint1) {
        this.oauthApiTokenEndpoint1 = oauthApiTokenEndpoint1;
    }

    /**
     * Get Oauth Api Consumer Secret1.
     * @return oauthApiConsumerSecret1 String.
     */
    public String getOauthApiConsumerKey1() {
        return oauthApiConsumerKey1;
    }

    /**
     * Set Oauth Api Consumer Key1.
     * @param oauthApiConsumerKey1 String.
     */
    public void setOauthApiConsumerKey1(String oauthApiConsumerKey1) {
        this.oauthApiConsumerKey1 = oauthApiConsumerKey1;
    }

    /**
     * Get Oauth Api Consumer Secret2.
     * @return oauthApiConsumerSecret2 String.
     */
    public String getOauthApiConsumerSecret1() {
        return oauthApiConsumerSecret1;
    }

    /**
     * Set Oauth Api Consumer Secret1.
     * @param oauthApiConsumerSecret1 String.
     */
    public void setOauthApiConsumerSecret1(String oauthApiConsumerSecret1) {
        this.oauthApiConsumerSecret1 = oauthApiConsumerSecret1;
    }

    /**
     * Get Oath Api Name1.
     * @return oauthApiName1 String.
     */
    public String getOauthApiName1() {
        return oauthApiName1;
    }

    /**
     * Set Oath Api Name1.
     * @param oauthApiName1
     */
    public void setOauthApiName1(String oauthApiName1) {
        this.oauthApiName1 = oauthApiName1;
    }

    /**
     * Get Oauth Api Token Endpoint2.
     * @return oauthApiTokenEndpoint2 String.
     */
    public String getOauthApiTokenEndpoint2() {
        return oauthApiTokenEndpoint2;
    }

    /**
     * Set Oauth Api Token Endpoint2.
     * @param oauthApiTokenEndpoint2 String.
     */
    public void setOauthApiTokenEndpoint2(String oauthApiTokenEndpoint2) {
        this.oauthApiTokenEndpoint2 = oauthApiTokenEndpoint2;
    }

    /**
     * Get Oauth Api Consumer Secret1.
     * @return oauthApiConsumerSecret1 String.
     */
    public String getOauthApiConsumerKey2() {
        return oauthApiConsumerKey2;
    }

    /**
     * Set Oauth Api Consumer Key2.
     * @param oauthApiConsumerKey2 String.
     */
    public void setOauthApiConsumerKey2(String oauthApiConsumerKey2) {
        this.oauthApiConsumerKey2 = oauthApiConsumerKey2;
    }

    /**
     * Get Oauth Api Consumer Secret2.
     * @return oauthApiConsumerSecret2 String.
     */
    public String getOauthApiConsumerSecret2() {
        return oauthApiConsumerSecret2;
    }

    /**
     * Set Oauth Api Consumer Secret2.
     * @param oauthApiConsumerSecret2 String.
     */
    public void setOauthApiConsumerSecret2(String oauthApiConsumerSecret2) {
        this.oauthApiConsumerSecret2 = oauthApiConsumerSecret2;
    }

    /**
     * Get Oath Api Name2.
     * @return oauthApiName2 String.
     */
    public String getOauthApiName2() {
        return oauthApiName2;
    }

    /**
     * Set Oath Api Name2.
     * @param oauthApiName2
     */
    public void setOauthApiName2(String oauthApiName2) {
        this.oauthApiName2 = oauthApiName2;
    }

    /**
     * Get Oauth Api Token Endpoint3.
     * @return oauthApiTokenEndpoint3 String.
     */
    public String getOauthApiTokenEndpoint3() {
        return oauthApiTokenEndpoint3;
    }

    /**
     * Set Oauth Api Token Endpoint3.
     * @param oauthApiTokenEndpoint3 String.
     */
    public void setOauthApiTokenEndpoint3(String oauthApiTokenEndpoint3) {
        this.oauthApiTokenEndpoint3 = oauthApiTokenEndpoint3;
    }

    /**
     * Get Oauth Api Consumer Key3.
     * @return oauthApiConsumerKey3 String.
     */
    public String getOauthApiConsumerKey3() {
        return oauthApiConsumerKey3;
    }

    /**
     * Set Oauth Api Consumer Key3.
     * @param oauthApiConsumerKey3 String.
     */
    public void setOauthApiConsumerKey3(String oauthApiConsumerKey3) {
        this.oauthApiConsumerKey3 = oauthApiConsumerKey3;
    }

    /**
     * Get Oauth Api Consumer Secret3.
     * @return oauthApiConsumerSecret3 String.
     */
    public String getOauthApiConsumerSecret3() {
        return oauthApiConsumerSecret3;
    }

    /**
     * Set Oauth Api Consumer Secret3.
     * @param oauthApiConsumerSecret3 String.
     */
    public void setOauthApiConsumerSecret3(String oauthApiConsumerSecret3) {
        this.oauthApiConsumerSecret3 = oauthApiConsumerSecret3;
    }

    /**
     * Get Oath Api Name3.
     * @return oauthApiName3 String.
     */
    public String getOauthApiName3() {
        return oauthApiName3;
    }

    /**
     * Set Oath Api Name3.
     * @param oauthApiName3
     */
    public void setOauthApiName3(String oauthApiName3) {
        this.oauthApiName3 = oauthApiName3;
    }

    /**
     * Get Web app.
     * @return webapp String.
     */
    public String getWebapp() {
        return webapp;
    }

    /**
     * Set Web App.
     * @param webapp String.
     */
    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }



}
