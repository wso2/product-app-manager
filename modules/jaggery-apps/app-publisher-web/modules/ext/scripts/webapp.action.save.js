var meta = {
    use: 'action',
    purpose: 'save',
    type: 'form',
    source: 'default',
    applyTo: 'webapp',
    required: ['model', 'template'],
    name: 'webapp.action.save'
};

/*
 Description:Saves the contents of the model to an artifact instance and then retrieves the
 id
 Filename: webapp.action.save.js
 Created Date: 8/8/2013
 */


var module = function () {

    var configs = require('/config/publisher.json');
    var log = new Log();

    /*
     adding asset details to Social Cache DB.
     */
    function addToSocialCache(id, type) {
        if (id) {
            var logged = require('store').server.current(session);
            var domain = (logged && logged.tenantDomain) ? logged.tenantDomain : "carbon.super";

            var CREATE_QUERY = "CREATE TABLE IF NOT EXISTS SOCIAL_CACHE (id VARCHAR(255) NOT NULL,tenant VARCHAR(255),type VARCHAR(255), " +
                "body VARCHAR(5000), rating DOUBLE,  PRIMARY KEY ( id ))";
            var server = require('store').server;
            server.privileged(function () {
                var db = new Database("SOCIAL_CACHE");
                db.query(CREATE_QUERY);
                var combinedId = type + ':' + id;
                db.query("INSERT INTO SOCIAL_CACHE (id,tenant,type,body,rating) VALUES('" + combinedId + "','" + domain + "','" + type + "','',0)");
                db.close();
            });
        }
    }

	function trim (str) {
		return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
	}

    //TODO: Change this method to take WebAppObj as argument instead of passing properties separately.
    function addToWebApp(uuid,webappProvider, webappName, webappVersion, webappContext, webappTrackingCode,asset, ssoEnabled, idpProviderUrl, saml2SsoIssuer, logoutURL,allowAnonymous) {

        var apiIdentifier = Packages.org.wso2.carbon.appmgt.api.model.APIIdentifier;
        var apiIdentifierObj = new apiIdentifier(webappProvider, webappName, webappVersion);

        var webApp = Packages.org.wso2.carbon.appmgt.api.model.WebApp;
        var webAppObj = new webApp(apiIdentifierObj);

        webAppObj.setContext(webappContext);
        webAppObj.setTrackingCode(webappTrackingCode);
	    webAppObj.setSsoEnabled(ssoEnabled);
        webAppObj.setIdpProviderURL(idpProviderUrl);
        webAppObj.setSaml2SsoIssuer(saml2SsoIssuer);
        webAppObj.setUUID(uuid);
        webAppObj.setLogoutURL(logoutURL);

        if (allowAnonymous=="TRUE")
        {
            webAppObj.setAllowAnonymous(true);
        }
        else
        {
            webAppObj.setAllowAnonymous(false);
        }


        var appMDAO = Packages.org.wso2.carbon.appmgt.impl.dao.AppMDAO;
        var appMDAOObj = new appMDAO();

        var identityUtil = Packages.org.wso2.carbon.identity.core.util.IdentityUtil;

        var index = 0;
        var attributes = asset.attributes;
        var urlPattern = attributes["uritemplate_urlPattern" + index];
        var policyPartials = attributes["uritemplate_policyPartialIds"];
        webAppObj.setPolicyPartials(policyPartials);

        while(urlPattern != null && trim(urlPattern).length > 0){

        		var URITemplate = Packages.org.wso2.carbon.appmgt.api.model.URITemplate;
        		var uriTemplate = new URITemplate();

        		uriTemplate.setHTTPVerb(attributes["uritemplate_httpVerb" + index]);
        		uriTemplate.setAuthType(attributes["uritemplate_authType" + index]);
        		uriTemplate.setUriTemplate(attributes["uritemplate_urlPattern" + index]);
        		uriTemplate.setThrottlingTier(attributes["uritemplate_tier" + index]);
        		uriTemplate.setSkipThrottling(attributes["uritemplate_skipthrottle" + index] === "True");
        		uriTemplate.setUserRoles(attributes["uritemplate_userRoles" + index]);
                uriTemplate.setAllowAnonymousURL(attributes["uritemplate_allowAnonymous" + index] === "True");

                // Set policy partial ids.
                var policyPartialMappings = attributes["uritemplate_entitlementPolicyPartialMappings" + index];

                if(policyPartialMappings){
                    policyPartialMappings = JSON.parse(policyPartialMappings);
                    log.warn(policyPartialMappings);
                    for(var i = 0; i < policyPartialMappings.length; i++){
                        var EntitlementPolicyPartialMapping = Packages.org.wso2.carbon.appmgt.api.model.entitlement.EntitlementPolicyPartialMapping;
                        var mapping = new EntitlementPolicyPartialMapping();
                        mapping.setEntitlementPolicyPartialId(policyPartialMappings[i]["entitlementPolicyPartialId"]);
                        mapping.setEffect(policyPartialMappings[i]["effect"]);
                        uriTemplate.addEntitlementPolicyPartialMapping(mapping);
                    }
                }

        		webAppObj.getUriTemplates().add(uriTemplate);

        		index++;
        		urlPattern = attributes["uritemplate_urlPattern" + index];
        }

        appMDAOObj.addWebApp(webAppObj);

        //Generate consumer/secret for web-app
        var tenantId = identityUtil.getTenantIdOFUser(webappProvider);
        appMDAOObj.addOAuthConsumer(webappProvider, tenantId, webappName, "");

        var count = 1;
        var tokenEndpoint = attributes["oauthapis_apiTokenEndpoint" + count];
        while (tokenEndpoint != null && trim(tokenEndpoint).length > 0) {

            webAppObj.setTokenEndpoint(attributes["oauthapis_apiTokenEndpoint" + count]);
            webAppObj.setApiConsumerKey(attributes["oauthapis_apiConsumerKey" + count]);
            webAppObj.setApiConsumerSecret(attributes["oauthapis_apiConsumerSecret" + count]);
            webAppObj.setApiName(attributes["oauthapis_apiName" + count]);

            count++;
            tokenEndpoint = attributes["oauthapis_apiTokenEndpoint" + count];

            //Save OAuth APIs consumer details per given web-app
            appMDAOObj.addOAuthAPIAccessInfo(webAppObj);
        }

    }


    return{
        execute: function (context) {
            var utility = require('/modules/utility.js').rxt_utility();

            log.debug('Entered : ' + meta.name);
            log.debug(stringify(context.actionMap));

            var model = context.model;
            var template = context.template;

            var now = new String(new Date().valueOf());
            var length = now.length;
            var prefix = configs.constants.assetCreatedDateLength;
            var onsetVal = '';
            if (length != prefix) {
                var onset = prefix - length;
                for (var i = 0; i < onset; i++) {
                    onsetVal += '0';
                }
            }
            model.setField('overview.createdtime', onsetVal + now);
            var provider = model.getField('overview.provider').value;
            var name = model.getField('overview.name').value;
            var version = model.getField('overview.version').value;
            var contextname = model.getField('overview.context').value;
            var allowAnonymous=model.getField('overview.allowAnonymous').value;

            if(contextname.charAt(0)!='/'){
                contextname = '/'+contextname;
            }

            var tenantDomain = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
            var tenantIdVal = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantId();

            if(tenantIdVal!='-1234'){
                contextname = '/t/'+tenantDomain+contextname;
            }

            var tracking_code = model.getField('overview.trackingCode').value;
            var logoutURL = model.getField('overview.logoutUrl').value;

            var webappURL = model.getField('overview.webAppUrl').value;
            var revisedURL = logoutURL.replace(webappURL,"");

            var shortName = template.shortName;

            log.debug('Artifact name: ' + name);

            log.debug('Converting model to an artifact for use with an artifact manager');

            //Export the model to an asset
            var asset = context.parent.export('asset.exporter');

            log.debug('Finished exporting model to an artifact');

            //Save the artifact
            log.debug('Saving artifact with name :' + name);


            //Get the artifact using the name
            var rxtManager = context.rxtManager;

            var artifactManager = rxtManager.getArtifactManager(shortName);

            artifactManager.add(asset);

            log.debug('Finished saving asset : ' + name);

            log.debug(asset);

            //The predicate object used to compare the assets
            var predicate = {
                attributes: {
                    overview_name: name,
                    overview_version: version
                }
            };
            var artifact = artifactManager.find(function (adapter) {
                //Check if the name and version are the same
                //return ((adapter.attributes.overview_name==name)&&(adapter.attributes.overview_version==version))?true:false;
                return utility.assertEqual(adapter, predicate);
            }, null);

            log.debug('Locating saved asset: ' + stringify(artifact) + ' to get the asset id.');

            var id = artifact[0].id || ' ';

            log.debug('Setting id of model to ' + id);

            //adding asset to social
            addToSocialCache(id, template.shortName);

            var artifact1 = artifactManager.get(id);
            var attributes = artifact1.attributes;


            //adding to database
            addToWebApp(id,provider, name, version, contextname, tracking_code,asset, attributes['sso_singleSignOn'], attributes['sso_idpProviderUrl'], attributes['sso_saml2SsoIssuer'],revisedURL,allowAnonymous);

            //Save the id data to the model
            model.setField('*.id', id);

            log.debug('Finished saving asset with id: ' + id);
        }
    }
};
