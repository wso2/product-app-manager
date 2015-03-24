package org.wso2.carbon.appmanager.integration.ui.Util.Bean;

/**
 * This class is used to generate the payload for application creation
 */
public class AppCreateRequest extends AbstractRequest {
    private String overview_provider = "admin";
    private String overview_name = "defaultAPP";
    private String overview_displayName = "defaultAPP";
    private String overview_context = "/defaultAPP";
    private String overview_version = "1.0.0";
    private String overview_transports = "http";
    private String overview_webAppUrl = "www.defaultapp.com";
    private String overview_description = "TestCases description";
    private String images_icon;
    private String images_thumbnail;
    private String images_banner;
    private String context;
    private String version;
    private String overview_tier = "Unlimited";
    private String overview_trackingCode = "AM_-1305386558";
    private String roles = "";
    private String tags = "";
    private String uritemplate_policyGroupIds;
    private String uritemplate_javaPolicyIds;
    private String uritemplate_policygroupid0="1";
    private String uritemplate_policygroupid1="1";
    private String uritemplate_policygroupid2="1";
    private String uritemplate_policygroupid3="1";
    private String uritemplate_policygroupid4="1";
    private String uritemplate_urlPattern4 = "/*";
    private String uritemplate_httpVerb4 = "OPTIONS";
    private String uritemplate_tier4 = "Unlimited";
    private String uritemplate_skipthrottle4 = "False";
    private String uritemplate_urlPattern3 = "/*";
    private String uritemplate_httpVerb3 = "DELETE";
    private String uritemplate_tier3 = "Unlimited";
    private String uritemplate_skipthrottle3 = "False";
    private String uritemplate_urlPattern2 = "/*";
    private String uritemplate_httpVerb2 = "PUT";
    private String uritemplate_tier2 = "Unlimited";
    private String uritemplate_skipthrottle2 = "False";
    private String uritemplate_urlPattern1 = "/*";
    private String uritemplate_httpVerb1 = "POST";
    private String uritemplate_tier1 = "Unlimited";
    private String uritemplate_skipthrottle1 = "False";
    private String uritemplate_urlPattern0 = "/*";
    private String uritemplate_httpVerb0 = "GET";
    private String uritemplate_tier0 = "Unlimited";
    private String uritemplate_skipthrottle0 = "False";
    private String overview_logoutUrl;
    private String claimPropertyCounter = "1";
    private String sso_singleSignOn = "Enabled";
    private String sso_idpProviderUrl = "https://localhost:9444/samlsso/";
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
    private String uritemplate_entitlementPolicyId4;
    private String uritemplate_entitlementPolicyId3;
    private String uritemplate_entitlementPolicyId2;
    private String uritemplate_entitlementPolicyId1;
    private String uritemplate_entitlementPolicyId0;
    private String uritemplate_userRoles4;
    private String uritemplate_userRoles3;
    private String uritemplate_userRoles2;
    private String uritemplate_userRoles1;
    private String uritemplate_userRoles0;
    private String entitlementPolicies = "[]";
    private String providers = "wso2is-5.0.0";
    private String uritemplate_policyPartialIds = "[]";
    private String uritemplate_entitlementPolicyPartialMappings0 = "[]";
    private String uritemplate_entitlementPolicyPartialMappings1 = "[]";
    private String uritemplate_entitlementPolicyPartialMappings2 = "[]";
    private String uritemplate_entitlementPolicyPartialMappings3 = "[]";
    private String uritemplate_entitlementPolicyPartialMappings4 = "[]";
    private String sso_ssoProvider = "wso2is-5.0.0";
    private String autoConfig = "on";
    private String claims = "http://wso2.org/claims/role";
    private String overview_allowAnonymous="false";
    private String uritemplate_allowAnonymous4="False";
    private String uritemplate_allowAnonymous3="False";
    private String uritemplate_allowAnonymous2="False";
    private String uritemplate_allowAnonymous1="False";
    private String uritemplate_allowAnonymous0="False";

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
        addParameter("uritemplate_javaPolicyIds", uritemplate_javaPolicyIds);
        addParameter("uritemplate_policyGroupIds", uritemplate_policyGroupIds);
        addParameter("uritemplate_policygroupid0", uritemplate_policygroupid0);
        addParameter("uritemplate_policygroupid1", uritemplate_policygroupid1);
        addParameter("uritemplate_policygroupid2", uritemplate_policygroupid2);
        addParameter("uritemplate_policygroupid3", uritemplate_policygroupid3);
        addParameter("uritemplate_policygroupid4", uritemplate_policygroupid4);
        addParameter("uritemplate_urlPattern4", uritemplate_urlPattern4);
        addParameter("uritemplate_httpVerb4", uritemplate_httpVerb4);
        addParameter("uritemplate_tier4", uritemplate_tier4);
        addParameter("uritemplate_skipthrottle4", uritemplate_skipthrottle4);
        addParameter("uritemplate_urlPattern3", uritemplate_urlPattern3);
        addParameter("uritemplate_httpVerb3", uritemplate_httpVerb3);
        addParameter("uritemplate_tier3", uritemplate_tier3);
        addParameter("uritemplate_skipthrottle3", uritemplate_skipthrottle3);
        addParameter("uritemplate_urlPattern2", uritemplate_urlPattern2);
        addParameter("uritemplate_httpVerb2", uritemplate_httpVerb2);
        addParameter("uritemplate_tier2", uritemplate_tier2);
        addParameter("uritemplate_skipthrottle2", uritemplate_skipthrottle2);
        addParameter("uritemplate_urlPattern1", uritemplate_urlPattern1);
        addParameter("uritemplate_httpVerb1", uritemplate_httpVerb1);
        addParameter("uritemplate_tier1", uritemplate_tier1);
        addParameter("uritemplate_skipthrottle1", uritemplate_skipthrottle1);
        addParameter("uritemplate_urlPattern0", uritemplate_urlPattern0);
        addParameter("uritemplate_httpVerb0", uritemplate_httpVerb0);
        addParameter("uritemplate_tier0", uritemplate_tier0);
        addParameter("uritemplate_skipthrottle0", uritemplate_skipthrottle0);
        addParameter("overview_logoutUrl", overview_logoutUrl);
        addParameter("claimPropertyCounter", claimPropertyCounter);
        addParameter("sso_singleSignOn", sso_singleSignOn);
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
        addParameter("uritemplate_entitlementPolicyId4", uritemplate_entitlementPolicyId4);
        addParameter("uritemplate_entitlementPolicyId3", uritemplate_entitlementPolicyId3);
        addParameter("uritemplate_entitlementPolicyId2", uritemplate_entitlementPolicyId2);
        addParameter("uritemplate_entitlementPolicyId1", uritemplate_entitlementPolicyId1);
        addParameter("uritemplate_entitlementPolicyId0", uritemplate_entitlementPolicyId0);
        addParameter("uritemplate_userRoles4", uritemplate_userRoles4);
        addParameter("uritemplate_userRoles3", uritemplate_userRoles3);
        addParameter("uritemplate_userRoles2", uritemplate_userRoles2);
        addParameter("uritemplate_userRoles1", uritemplate_userRoles1);
        addParameter("uritemplate_userRoles0", uritemplate_userRoles0);
        addParameter("entitlementPolicies", entitlementPolicies);
        addParameter("providers", providers);
        addParameter("sso_ssoProvider", sso_ssoProvider);
        addParameter("claims", claims);
        addParameter("uritemplate_policyPartialIds",uritemplate_policyPartialIds);
        addParameter("uritemplate_entitlementPolicyPartialMappings0",uritemplate_entitlementPolicyPartialMappings0);
        addParameter("uritemplate_entitlementPolicyPartialMappings1",uritemplate_entitlementPolicyPartialMappings1);
        addParameter("uritemplate_entitlementPolicyPartialMappings2",uritemplate_entitlementPolicyPartialMappings2);
        addParameter("uritemplate_entitlementPolicyPartialMappings3",uritemplate_entitlementPolicyPartialMappings3);
        addParameter("uritemplate_entitlementPolicyPartialMappings4",uritemplate_entitlementPolicyPartialMappings4);
        addParameter("overview_allowAnonymous",overview_allowAnonymous);
        addParameter("uritemplate_allowAnonymous4",uritemplate_allowAnonymous4);
        addParameter("uritemplate_allowAnonymous4",uritemplate_allowAnonymous3);
        addParameter("uritemplate_allowAnonymous4",uritemplate_allowAnonymous2);
        addParameter("uritemplate_allowAnonymous4",uritemplate_allowAnonymous1);
        addParameter("uritemplate_allowAnonymous4",uritemplate_allowAnonymous0);
    }

    @Override
    public void setAction() {
        // TODO Auto-generated method stub

    }

    public String getOverview_provider() {
        return overview_provider;
    }

    public void setOverview_provider(String overview_provider) {
        this.overview_provider = overview_provider;
    }

    public String getOverview_name() {
        return overview_name;
    }

    public void setOverview_name(String overview_name) {
        this.overview_name = overview_name;
    }

    public String getOverview_displayName() {
        return overview_displayName;
    }

    public void setOverview_displayName(String overview_displayName) {
        this.overview_displayName = overview_displayName;
    }

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

    public String getUritemplate_tier4() {
        return uritemplate_tier4;
    }

    public void setUritemplate_tier4(String uritemplate_tier4) {
        this.uritemplate_tier4 = uritemplate_tier4;
    }

    public String getUritemplate_skipthrottle4() {
        return uritemplate_skipthrottle4;
    }

    public void setUritemplate_skipthrottle4(String uritemplate_skipthrottle4) {
        this.uritemplate_skipthrottle4 = uritemplate_skipthrottle4;
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

    public String getUritemplate_tier3() {
        return uritemplate_tier3;
    }

    public void setUritemplate_tier3(String uritemplate_tier3) {
        this.uritemplate_tier3 = uritemplate_tier3;
    }

    public String getUritemplate_skipthrottle3() {
        return uritemplate_skipthrottle3;
    }

    public void setUritemplate_skipthrottle3(String uritemplate_skipthrottle3) {
        this.uritemplate_skipthrottle3 = uritemplate_skipthrottle3;
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

    public String getUritemplate_tier2() {
        return uritemplate_tier2;
    }

    public void setUritemplate_tier2(String uritemplate_tier2) {
        this.uritemplate_tier2 = uritemplate_tier2;
    }

    public String getUritemplate_skipthrottle2() {
        return uritemplate_skipthrottle2;
    }

    public void setUritemplate_skipthrottle2(String uritemplate_skipthrottle2) {
        this.uritemplate_skipthrottle2 = uritemplate_skipthrottle2;
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

    public String getUritemplate_tier1() {
        return uritemplate_tier1;
    }

    public void setUritemplate_tier1(String uritemplate_tier1) {
        this.uritemplate_tier1 = uritemplate_tier1;
    }

    public String getUritemplate_skipthrottle1() {
        return uritemplate_skipthrottle1;
    }

    public void setUritemplate_skipthrottle1(String uritemplate_skipthrottle1) {
        this.uritemplate_skipthrottle1 = uritemplate_skipthrottle1;
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

    public String getUritemplate_tier0() {
        return uritemplate_tier0;
    }

    public void setUritemplate_tier0(String uritemplate_tier0) {
        this.uritemplate_tier0 = uritemplate_tier0;
    }

    public String getUritemplate_skipthrottle0() {
        return uritemplate_skipthrottle0;
    }

    public void setUritemplate_skipthrottle0(String uritemplate_skipthrottle0) {
        this.uritemplate_skipthrottle0 = uritemplate_skipthrottle0;
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

    public String getUritemplate_entitlementPolicyId4() {
        return uritemplate_entitlementPolicyId4;
    }

    public void setUritemplate_entitlementPolicyId4(String uritemplate_entitlementPolicyId4) {
        this.uritemplate_entitlementPolicyId4 = uritemplate_entitlementPolicyId4;
    }

    public String getUritemplate_entitlementPolicyId3() {
        return uritemplate_entitlementPolicyId3;
    }

    public void setUritemplate_entitlementPolicyId3(String uritemplate_entitlementPolicyId3) {
        this.uritemplate_entitlementPolicyId3 = uritemplate_entitlementPolicyId3;
    }

    public String getUritemplate_entitlementPolicyId2() {
        return uritemplate_entitlementPolicyId2;
    }

    public void setUritemplate_entitlementPolicyId2(String uritemplate_entitlementPolicyId2) {
        this.uritemplate_entitlementPolicyId2 = uritemplate_entitlementPolicyId2;
    }

    public String getUritemplate_entitlementPolicyId1() {
        return uritemplate_entitlementPolicyId1;
    }

    public void setUritemplate_entitlementPolicyId1(String uritemplate_entitlementPolicyId1) {
        this.uritemplate_entitlementPolicyId1 = uritemplate_entitlementPolicyId1;
    }

    public String getUritemplate_entitlementPolicyId0() {
        return uritemplate_entitlementPolicyId0;
    }

    public void setUritemplate_entitlementPolicyId0(String uritemplate_entitlementPolicyId0) {
        this.uritemplate_entitlementPolicyId0 = uritemplate_entitlementPolicyId0;
    }

    public String getUritemplate_userRoles4() {
        return uritemplate_userRoles4;
    }

    public void setUritemplate_userRoles4(String uritemplate_userRoles4) {
        this.uritemplate_userRoles4 = uritemplate_userRoles4;
    }

    public String getUritemplate_userRoles3() {
        return uritemplate_userRoles3;
    }

    public void setUritemplate_userRoles3(String uritemplate_userRoles3) {
        this.uritemplate_userRoles3 = uritemplate_userRoles3;
    }

    public String getUritemplate_userRoles2() {
        return uritemplate_userRoles2;
    }

    public void setUritemplate_userRoles2(String uritemplate_userRoles2) {
        this.uritemplate_userRoles2 = uritemplate_userRoles2;
    }

    public String getUritemplate_userRoles1() {
        return uritemplate_userRoles1;
    }

    public void setUritemplate_userRoles1(String uritemplate_userRoles1) {
        this.uritemplate_userRoles1 = uritemplate_userRoles1;
    }

    public String getUritemplate_userRoles0() {
        return uritemplate_userRoles0;
    }

    public void setUritemplate_userRoles0(String uritemplate_userRoles0) {
        this.uritemplate_userRoles0 = uritemplate_userRoles0;
    }

    public String getEntitlementPolicies() {
        return entitlementPolicies;
    }

    public void setEntitlementPolicies(String entitlementPolicies) {
        this.entitlementPolicies = entitlementPolicies;
    }

    public String getProviders() {
        return providers;
    }

    public void setProviders(String providers) {
        this.providers = providers;
    }

    public String getClaims() {
        return claims;
    }

    public void setClaims(String claims) {
        this.claims = claims;
    }

    public String getSso_ssoProvider() {
        return sso_ssoProvider;
    }

    public void setSso_ssoProvider(String sso_ssoProvider) {
        this.sso_ssoProvider = sso_ssoProvider;
    }


    public String getAutoConfig() {
        return autoConfig;
    }

    public void setAutoConfig(String autoConfig) {
        this.autoConfig = autoConfig;
    }

    public String getUritemplate_policyPartialIds() {
        return uritemplate_policyPartialIds;
    }

    public void setUritemplate_policyPartialIds(String uritemplate_policyPartialIds) {
        this.uritemplate_policyPartialIds = uritemplate_policyPartialIds;
    }
    public String getUritemplate_entitlementPolicyPartialMappings0() {
        return uritemplate_entitlementPolicyPartialMappings0;
    }

    public void setUritemplate_entitlementPolicyPartialMappings0(String uritemplate_entitlementPolicyPartialMappings0) {
        this.uritemplate_entitlementPolicyPartialMappings0 = uritemplate_entitlementPolicyPartialMappings0;
    }

    public String getUritemplate_entitlementPolicyPartialMappings1() {
        return uritemplate_entitlementPolicyPartialMappings1;
    }

    public void setUritemplate_entitlementPolicyPartialMappings1(String uritemplate_entitlementPolicyPartialMappings1) {
        this.uritemplate_entitlementPolicyPartialMappings1 = uritemplate_entitlementPolicyPartialMappings1;
    }

    public String getUritemplate_entitlementPolicyPartialMappings2() {
        return uritemplate_entitlementPolicyPartialMappings2;
    }

    public void setUritemplate_entitlementPolicyPartialMappings2(String uritemplate_entitlementPolicyPartialMappings2) {
        this.uritemplate_entitlementPolicyPartialMappings2 = uritemplate_entitlementPolicyPartialMappings2;
    }

    public String getUritemplate_entitlementPolicyPartialMappings3() {
        return uritemplate_entitlementPolicyPartialMappings3;
    }

    public void setUritemplate_entitlementPolicyPartialMappings3(String uritemplate_entitlementPolicyPartialMappings3) {
        this.uritemplate_entitlementPolicyPartialMappings3 = uritemplate_entitlementPolicyPartialMappings3;
    }

    public String getUritemplate_entitlementPolicyPartialMappings4() {
        return uritemplate_entitlementPolicyPartialMappings4;
    }

    public void setUritemplate_entitlementPolicyPartialMappings4(String uritemplate_entitlementPolicyPartialMappings4) {
        this.uritemplate_entitlementPolicyPartialMappings4 = uritemplate_entitlementPolicyPartialMappings4;
    }

    public String getVerview_allowAnonymous() {
        return overview_allowAnonymous;
    }

    public void setVerview_allowAnonymous(String verview_allowAnonymous) {
        this.overview_allowAnonymous = verview_allowAnonymous;
    }

    public String getUritemplate_allowAnonymous4() {
        return uritemplate_allowAnonymous4;
    }

    public void setUritemplate_allowAnonymous4(String uritemplate_allowAnonymous4) {
        this.uritemplate_allowAnonymous4 = uritemplate_allowAnonymous4;
    }

    public String getUritemplate_allowAnonymous3() {
        return uritemplate_allowAnonymous3;
    }

    public void setUritemplate_allowAnonymous3(String uritemplate_allowAnonymous3) {
        this.uritemplate_allowAnonymous3 = uritemplate_allowAnonymous3;
    }

    public String getUritemplate_allowAnonymous2() {
        return uritemplate_allowAnonymous2;
    }

    public void setUritemplate_allowAnonymous2(String uritemplate_allowAnonymous2) {
        this.uritemplate_allowAnonymous2 = uritemplate_allowAnonymous2;
    }

    public String getUritemplate_allowAnonymous1() {
        return uritemplate_allowAnonymous1;
    }

    public void setUritemplate_allowAnonymous1(String uritemplate_allowAnonymous1) {
        this.uritemplate_allowAnonymous1 = uritemplate_allowAnonymous1;
    }

    public String getUritemplate_allowAnonymous0() {
        return uritemplate_allowAnonymous0;
    }

    public void setUritemplate_allowAnonymous0(String uritemplate_allowAnonymous0) {
        this.uritemplate_allowAnonymous0 = uritemplate_allowAnonymous0;
    }
    public String getUritemplate_policygroupid0() {
        return uritemplate_policygroupid0;
    }

    public void setUritemplate_policygroupid0(String uritemplate_policygroupid0) {
        this.uritemplate_policygroupid0 = uritemplate_policygroupid0;
    }

    public String getUritemplate_policygroupid1() {
        return uritemplate_policygroupid1;
    }

    public void setUritemplate_policygroupid1(String uritemplate_policygroupid1) {
        this.uritemplate_policygroupid1 = uritemplate_policygroupid1;
    }

    public String getUritemplate_policygroupid2() {
        return uritemplate_policygroupid2;
    }

    public void setUritemplate_policygroupid2(String uritemplate_policygroupid2) {
        this.uritemplate_policygroupid2 = uritemplate_policygroupid2;
    }

    public String getUritemplate_policygroupid3() {
        return uritemplate_policygroupid3;
    }

    public void setUritemplate_policygroupid3(String uritemplate_policygroupid3) {
        this.uritemplate_policygroupid3 = uritemplate_policygroupid3;
    }

    public String getUritemplate_policygroupid4() {
        return uritemplate_policygroupid4;
    }

    public void setUritemplate_policygroupid4(String uritemplate_policygroupid4) {
        this.uritemplate_policygroupid4 = uritemplate_policygroupid4;
    }

    public String getUritemplate_policyGroupIds() {
        return uritemplate_policyGroupIds;
    }

    public void setUritemplate_policyGroupIds(String uritemplate_policyGroupIds) {
        this.uritemplate_policyGroupIds = uritemplate_policyGroupIds;
    }

    public String getUritemplate_javaPolicyIds() {
        return uritemplate_javaPolicyIds;
    }

    public void setUritemplate_javaPolicyIds(String uritemplate_javaPolicyIds) {
        this.uritemplate_javaPolicyIds = uritemplate_javaPolicyIds;
    }
}
