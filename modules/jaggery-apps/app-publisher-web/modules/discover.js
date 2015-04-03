var carbon=require('carbon');


/*
	Description: Encapsulates the web application management services to discover
	the available applications
	Filename:discover.js
	Created Date: 17/03/2015
*/

var discover_client=function(){
    var log=new Log();
    var appmgtPublisher = require('appmgtdiscoveryclient');

	function DiscoverClient(registry){
		this.registry=registry;
	}

	/*

	*/
	DiscoverClient.prototype.discoverWebapps=function(serviceUrl, userName, password, pageNumber){

		var discoverClientProviderObj = new appmgtPublisher.DiscoveryClient();

        try {
          var result = discoverClientProviderObj.discoverWebapps(serviceUrl, userName, password, pageNumber);
          return result;
        } catch (e) {
          var result = {"status": { "code": "0", "description":""}, "metadataList": []};
          result.status.code = 403;
          result.status.description = "Application server can not be reached with given credentials";
          return result;
        }
	}


	/*
    	Create the new asset object using the data from the server
    	parameters:
    		serviceUrl: The application server URL
    		username: The user Name to connect the application server
    		password: the password for the application server
    		appType: application type {webapp, mobileapp}
    		serverType: {null==wso2as, wso2as}
    		applicationId: ID to uniquely identify the application, depends on appType+serverType

		Checks if any conflicting assets exists and try to resolve. Notify the user about the status.
    	returns the RXT of the application (asset)
	*/
	DiscoverClient.prototype.createNewAsset=function(serviceUrl, userName, password,
		appType, serverType, applicationId) {
		log.debug('createNewAsset backend');
       	var discoverClientProviderObj = new appmgtPublisher.DiscoveryClient();

        var  discoveredAppInfo = discoverClientProviderObj.getDiscoveredWebappInfo(serviceUrl, userName, password,
        	serverType, applicationId);

		var url = 'https://localhost:'+request.getLocalPort()+
			'/publisher/api/asset/webapp/1212';
		log.debug("Headers "+stringify(request.getAllHeaders()));
		log.debug('Calling URL '+url);
		var res = post(url);

		print(res.data);
       	return null;
	}

	DiscoverClient.prototype.getApplicationData=function(serviceUrl, userName, password,
    		appType, serverType, applicationId) {
           	var discoverClientProviderObj = new appmgtPublisher.DiscoveryClient();
    		log.debug('createNewAsset backend 1 '+discoverClientProviderObj);
            var  discoveredAppInfo = discoverClientProviderObj.getDiscoveredWebappInfo(serviceUrl, userName, password,
            	serverType, applicationId);
            var appName = discoveredAppInfo.name;
            var appContext = discoveredAppInfo.context;
            if(appName == null) {
              appName = '<no name '+appContext+ ' >' ;
            }
            var appId = applicationId;
            var appVersion = discoveredAppInfo.version;
            var appUrl = discoveredAppInfo.httpUrl;
           log.debug('createNewAsset backend 2 '+appId + ' '+appVersion + ' appUrl ' );
            var discoveredAppInfo = {

                "uritemplate_httpVerb4": "OPTIONS",
                  "autoConfig": "on",
                  "uritemplate_httpVerb0": "GET",
                  "uritemplate_httpVerb1": "POST",
                  "uritemplate_httpVerb2": "PUT",
                  "uritemplate_httpVerb3": "DELETE",
                  "overview_tier": "Unlimited",
                  "optradio": "on",
                  "overview_transports": "http",
                  "sso_saml2SsoIssuer": "",
                  "sso_idpProviderUrl": "https://localhost:9444/samlsso/",
                  "uritemplate_urlPattern4": "/*",
                  "uritemplate_urlPattern3": "/*",
                  "uritemplate_urlPattern2": "/*",
                  "providers": "wso2is-5.0.0",
                  "webapp": "webapp",
                  "uritemplate_policyGroupId3": "14",
                  "overview_provider": "",
                  "uritemplate_policyGroupId4": "14",
                  "uritemplate_urlPattern1": "/*",
                  "uritemplate_urlPattern0": "/*",
                  "uritemplate_policyGroupId0": "14",
                  "uritemplate_policyGroupId1": "14",
                  "uritemplate_policyGroupId2": "14",
                  "claims": "http://wso2.org/claims/otherphone",
                  "oauthapis_apiConsumerSecret1": "",
                  "oauthapis_apiConsumerSecret2": "",
                  "sso_ssoProvider": "wso2is-5.0.0",
                  "oauthapis_apiConsumerSecret3": "",
                  "uritemplate_javaPolicyIds": "[]",
                  "overview_allowAnonymous": "false",
                  "overview_displayName": appName,
                  "images_thumbnail": "",
                  "overview_webAppUrl": appUrl,
                  "sso_singleSignOn": "Enabled",
                  "overview_logoutUrl": "",
                  "roles": "",
                  "images_banner": "",
                  "claimPropertyCounter": "1",
                  "uritemplate_policyPartialIdstemp": "[]",
                  "oauthapis_apiTokenEndpoint3": "",
                  "oauthapis_apiName2": "",
                  "oauthapis_apiName3": "",
                  "oauthapis_apiName1": "",
                  "claimPropertyName0": "http://wso2.org/claims/role",
                  "version": "",
                  "overview_description": appName,
                  "uritemplate_policyGroupIds": "[14]",
                  "overview_context": appContext,
                  "entitlementPolicies": "[]",
                  "oauthapis_apiTokenEndpoint1": "",
                  "oauthapis_apiTokenEndpoint2": "",
                  "tags": "",
                  "overview_trackingCode": "AM_2302006466779858",
                  "overview_name": appName,
                  "overview_skipGateway": "false",
                  "oauthapis_apiConsumerKey3": "",
                  "oauthapis_apiConsumerKey2": "",
                  "oauthapis_apiConsumerKey1": "",
                  "overview_version": appVersion,
                  "context": context
            };
           	return discoveredAppInfo;
    }

	return {
		DiscoverClient:DiscoverClient
	};
};


