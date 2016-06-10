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

import java.util.List;

public class WebApp {
    private String overviewProvider;
    private String overviewName;
    private String overviewDisplayName;
    private String overviewContext;
    private String overviewVersion;
    private String overviewTransports;
    private String overviewWebAppUrl;
    private String overviewDescription;
    private String overviewTreatAsASite;
    private String overviewTier;
    private String overviewAllowAnonymous;
    private String overviewSkipGateway;
    private String overviewTrackingCode;
    private String overviewLogoutUrl;
    private String overviewAppOwner;
    private String overviewAppTenant;
    private String overviewAdvertiseOnly;
    private String overviewRedirectUrl;
    private String overviewSubscriptionAvailability;
    private String overviewAcsUrl;
    private List<String> tenants; // overview_tenants

    private String imagesThumbnail;
    private String imagesBanner;
    private List<String> tags;
    private List<String> roles;//overview_visibleRoles
    private String webapp;
    private String optradio;
    private String businessOwner;
    private String singleSignOn;
    private String autoConfig;
    private String providers;
    private String ssoProvider;
    private String entitlementPolicies;
    private String ssoIdpProviderUrl;
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

    private List<WebAppResource> webAppResources;
    private List<String> uriTemplateJavaPolicyIds;
    private List<String> uriTemplatePolicyGroupIds;
    private List<String> claims;
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<String> getTenants() {
        return tenants;
    }

    public void setTenants(List<String> tenants) {
        this.tenants = tenants;
    }

    public String getOverviewProvider() {
        return overviewProvider;
    }

    public void setOverviewProvider(String overviewProvider) {
        this.overviewProvider = overviewProvider;
    }

    public String getOverviewName() {
        return overviewName;
    }

    public void setOverviewName(String overviewName) {
        this.overviewName = overviewName;
    }

    public String getOverviewDisplayName() {
        return overviewDisplayName;
    }

    public void setOverviewDisplayName(String overviewDisplayName) {
        this.overviewDisplayName = overviewDisplayName;
    }

    public String getOverviewContext() {
        return overviewContext;
    }

    public void setOverviewContext(String overviewContext) {
        this.overviewContext = overviewContext;
    }

    public String getOverviewVersion() {
        return overviewVersion;
    }

    public void setOverviewVersion(String overviewVersion) {
        this.overviewVersion = overviewVersion;
    }

    public String getOverviewTransports() {
        return overviewTransports;
    }

    public void setOverviewTransports(String overviewTransports) {
        this.overviewTransports = overviewTransports;
    }

    public String getOverviewWebAppUrl() {
        return overviewWebAppUrl;
    }

    public void setOverviewWebAppUrl(String overviewWebAppUrl) {
        this.overviewWebAppUrl = overviewWebAppUrl;
    }

    public String getOverviewDescription() {
        return overviewDescription;
    }

    public void setOverviewDescription(String overviewDescription) {
        this.overviewDescription = overviewDescription;
    }

    public String getOverviewTreatAsASite() {
        return overviewTreatAsASite;
    }

    public void setOverviewTreatAsASite(String overviewTreatAsASite) {
        this.overviewTreatAsASite = overviewTreatAsASite;
    }

    public String getOverviewTier() {
        return overviewTier;
    }

    public void setOverviewTier(String overviewTier) {
        this.overviewTier = overviewTier;
    }

    public String getOverviewAllowAnonymous() {
        return overviewAllowAnonymous;
    }

    public void setOverviewAllowAnonymous(String overviewAllowAnonymous) {
        this.overviewAllowAnonymous = overviewAllowAnonymous;
    }

    public String getOverviewSkipGateway() {
        return overviewSkipGateway;
    }

    public void setOverviewSkipGateway(String overviewSkipGateway) {
        this.overviewSkipGateway = overviewSkipGateway;
    }

    public String getOverviewTrackingCode() {
        return overviewTrackingCode;
    }

    public void setOverviewTrackingCode(String overviewTrackingCode) {
        this.overviewTrackingCode = overviewTrackingCode;
    }

    public String getOverviewLogoutUrl() {
        return overviewLogoutUrl;
    }

    public void setOverviewLogoutUrl(String overviewLogoutUrl) {
        this.overviewLogoutUrl = overviewLogoutUrl;
    }

    public String getImagesThumbnail() {
        return imagesThumbnail;
    }

    public void setImagesThumbnail(String imagesThumbnail) {
        this.imagesThumbnail = imagesThumbnail;
    }

    public String getImagesBanner() {
        return imagesBanner;
    }

    public void setImagesBanner(String imagesBanner) {
        this.imagesBanner = imagesBanner;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getOverviewAcsUrl() {
        return overviewAcsUrl;
    }

    public void setOverviewAcsUrl(String overviewAcsUrl) {
        this.overviewAcsUrl = overviewAcsUrl;
    }

    public String getOptradio() {
        return optradio;
    }

    public void setOptradio(String optradio) {
        this.optradio = optradio;
    }

    public List<String> getUriTemplateJavaPolicyIds() {
        return uriTemplateJavaPolicyIds;
    }

    public void setUriTemplateJavaPolicyIds(List<String> uriTemplateJavaPolicyIds) {
        this.uriTemplateJavaPolicyIds = uriTemplateJavaPolicyIds;
    }

    public List<String> getUriTemplatePolicyGroupIds() {
        return uriTemplatePolicyGroupIds;
    }

    public void setUriTemplatePolicyGroupIds(List<String> uriTemplatePolicyGroupIds) {
        this.uriTemplatePolicyGroupIds = uriTemplatePolicyGroupIds;
    }

    public String getSingleSignOn() {
        return singleSignOn;
    }

    public void setSingleSignOn(String singleSignOn) {
        this.singleSignOn = singleSignOn;
    }

    public String getAutoConfig() {
        return autoConfig;
    }

    public void setAutoConfig(String autoConfig) {
        this.autoConfig = autoConfig;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(String providers) {
        this.providers = providers;
    }

    public String getSsoProvider() {
        return ssoProvider;
    }

    public void setSsoProvider(String ssoProvider) {
        this.ssoProvider = ssoProvider;
    }

    public String getWebapp() {
        return webapp;
    }

    public void setWebapp(String webapp) {
        this.webapp = webapp;
    }

    public String getEntitlementPolicies() {
        return entitlementPolicies;
    }

    public void setEntitlementPolicies(String entitlementPolicies) {
        this.entitlementPolicies = entitlementPolicies;
    }


    public String getSsoIdpProviderUrl() {
        return ssoIdpProviderUrl;
    }

    public void setSsoIdpProviderUrl(String ssoIdpProviderUrl) {
        this.ssoIdpProviderUrl = ssoIdpProviderUrl;
    }

    public String getSsoSaml2SsoIssuer() {
        return ssoSaml2SsoIssuer;
    }

    public void setSsoSaml2SsoIssuer(String ssoSaml2SsoIssuer) {
        this.ssoSaml2SsoIssuer = ssoSaml2SsoIssuer;
    }

    public String getOauthApiTokenEndpoint1() {
        return oauthApiTokenEndpoint1;
    }

    public void setOauthApiTokenEndpoint1(String oauthApiTokenEndpoint1) {
        this.oauthApiTokenEndpoint1 = oauthApiTokenEndpoint1;
    }

    public String getOauthApiConsumerKey1() {
        return oauthApiConsumerKey1;
    }

    public void setOauthApiConsumerKey1(String oauthApiConsumerKey1) {
        this.oauthApiConsumerKey1 = oauthApiConsumerKey1;
    }

    public String getOauthApiConsumerSecret1() {
        return oauthApiConsumerSecret1;
    }

    public void setOauthApiConsumerSecret1(String oauthApiConsumerSecret1) {
        this.oauthApiConsumerSecret1 = oauthApiConsumerSecret1;
    }

    public String getOauthApiName1() {
        return oauthApiName1;
    }

    public void setOauthApiName1(String oauthApiName1) {
        this.oauthApiName1 = oauthApiName1;
    }

    public String getOauthApiTokenEndpoint2() {
        return oauthApiTokenEndpoint2;
    }

    public void setOauthApiTokenEndpoint2(String oauthApiTokenEndpoint2) {
        this.oauthApiTokenEndpoint2 = oauthApiTokenEndpoint2;
    }

    public String getOauthApiConsumerKey2() {
        return oauthApiConsumerKey2;
    }

    public void setOauthApiConsumerKey2(String oauthApiConsumerKey2) {
        this.oauthApiConsumerKey2 = oauthApiConsumerKey2;
    }

    public String getOauthApiConsumerSecret2() {
        return oauthApiConsumerSecret2;
    }

    public void setOauthApiConsumerSecret2(String oauthApiConsumerSecret2) {
        this.oauthApiConsumerSecret2 = oauthApiConsumerSecret2;
    }

    public String getOauthApiName2() {
        return oauthApiName2;
    }

    public void setOauthApiName2(String oauthApiName2) {
        this.oauthApiName2 = oauthApiName2;
    }

    public String getOauthApiTokenEndpoint3() {
        return oauthApiTokenEndpoint3;
    }

    public void setOauthApiTokenEndpoint3(String oauthApiTokenEndpoint3) {
        this.oauthApiTokenEndpoint3 = oauthApiTokenEndpoint3;
    }

    public String getOauthApiConsumerKey3() {
        return oauthApiConsumerKey3;
    }

    public void setOauthApiConsumerKey3(String oauthApiConsumerKey3) {
        this.oauthApiConsumerKey3 = oauthApiConsumerKey3;
    }

    public String getOauthApiConsumerSecret3() {
        return oauthApiConsumerSecret3;
    }

    public void setOauthApiConsumerSecret3(String oauthApiConsumerSecret3) {
        this.oauthApiConsumerSecret3 = oauthApiConsumerSecret3;
    }

    public String getOauthApiName3() {
        return oauthApiName3;
    }

    public void setOauthApiName3(String oauthApiName3) {
        this.oauthApiName3 = oauthApiName3;
    }

    public List<WebAppResource> getWebAppResources() {
        return webAppResources;
    }

    public void setWebAppResources(List<WebAppResource> webAppResources) {
        this.webAppResources = webAppResources;
    }

    public List<String> getClaims() {
        return claims;
    }

    public void setClaims(List<String> claims) {
        this.claims = claims;
    }

    public String getOverviewAppOwner() {
        return overviewAppOwner;
    }

    public void setOverviewAppOwner(String overviewAppOwner) {
        this.overviewAppOwner = overviewAppOwner;
    }

    public String getOverviewAppTenant() {
        return overviewAppTenant;
    }

    public void setOverviewAppTenant(String overviewAppTenant) {
        this.overviewAppTenant = overviewAppTenant;
    }

    public String getOverviewAdvertiseOnly() {
        return overviewAdvertiseOnly;
    }

    public void setOverviewAdvertiseOnly(String overviewAdvertiseOnly) {
        this.overviewAdvertiseOnly = overviewAdvertiseOnly;
    }

    public String getOverviewRedirectUrl() {
        return overviewRedirectUrl;
    }

    public void setOverviewRedirectUrl(String overviewRedirectUrl) {
        this.overviewRedirectUrl = overviewRedirectUrl;
    }

    public String getOverviewSubscriptionAvailability() {
        return overviewSubscriptionAvailability;
    }

    public void setOverviewSubscriptionAvailability(String overviewSubscriptionAvailability) {
        this.overviewSubscriptionAvailability = overviewSubscriptionAvailability;
    }
    
    public String getBusinessOwner() {
        return businessOwner;
   }

   public void setBusinessOwner(String businessOwner) {
       this.businessOwner = businessOwner;
   }

}
