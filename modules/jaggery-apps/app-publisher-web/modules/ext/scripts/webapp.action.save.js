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
    var dataConfigs = require('/config/publisher.js').config();
    var log = new Log();

	function trim (str) {
		return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
	}

    //TODO: Change this method to take WebAppObj as argument instead of passing properties separately.
    function addToWebApp(uuid,webappProvider, webappName, webappVersion, webappContext,
                         webappTrackingCode,asset, ssoEnabled, idpProviderUrl, saml2SsoIssuer,
                         logoutURL,allowAnonymous, skipGateway, webAppEndpoint) {

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
        webAppObj.setUrl(webAppEndpoint);

        if (allowAnonymous=="TRUE"){
            webAppObj.setAllowAnonymous(true);
        }
        else{
            webAppObj.setAllowAnonymous(false);
        }

        if (skipGateway == "true") {
            webAppObj.setSkipGateway(true);
        }
        else {
            webAppObj.setSkipGateway(false);
        }

        var appMDAO = Packages.org.wso2.carbon.appmgt.impl.dao.AppMDAO;
        var appMDAOObj = new appMDAO();

        var identityUtil = Packages.org.wso2.carbon.identity.core.util.IdentityUtil;

        var index = 0;
        var attributes = asset.attributes;
        var urlPattern = attributes["uritemplate_urlPattern" + index];
        var policyGroups = attributes["uritemplate_policyGroupIds"];
        var javaPolicies = attributes["uritemplate_javaPolicyIds"];

        webAppObj.setPolicyGroups(policyGroups); //set policy group id list
        webAppObj.setJavaPolicies(javaPolicies); //set java policies id list

        while(urlPattern != null && trim(urlPattern).length > 0){

        		var URITemplate = Packages.org.wso2.carbon.appmgt.api.model.URITemplate;
        		var uriTemplate = new URITemplate();
        		uriTemplate.setHTTPVerb(attributes["uritemplate_httpVerb" + index]);
        		uriTemplate.setUriTemplate(attributes["uritemplate_urlPattern" + index]);
                uriTemplate.setPolicyGroupId(attributes["uritemplate_policygroupid" + index]);

                webAppObj.getUriTemplates().add(uriTemplate);
        		index++;
        		urlPattern = attributes["uritemplate_urlPattern" + index];
        }
        appMDAOObj.addWebApp(webAppObj);

        //Generate consumer/secret for web-app
        var tenantId = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantId();
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
            appMDAOObj.addOAuthAPIAccessInfo(webAppObj, tenantId);
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
            var skipGateway = model.getField('overview.skipGateway').value;

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
            var displayName = model.getField('overview.displayName').value;
            var revisedURL = logoutURL.replace(webappURL,"");

            var result = null;
            var saml2SsoIssuer = null;

            if (tenantIdVal != '-1234') {
                saml2SsoIssuer = name + "-" + tenantDomain + "-" + version;
            } else {
                saml2SsoIssuer = name + "-" + version;
            }


            var shortName = template.shortName;

            log.debug('Artifact name: ' + name);

            log.debug('Converting model to an artifact for use with an artifact manager');

            //Export the model to an asset
            var asset = context.parent.export('asset.exporter');
            //set sso details
            var idpProviderUrl = dataConfigs.ssoConfiguration.identityProviderURL;
            var ssoEnabled = dataConfigs.ssoConfiguration.enabled;
            asset.attributes.sso_idpProviderUrl = idpProviderUrl;
            asset.attributes.sso_saml2SsoIssuer = saml2SsoIssuer;
            if(ssoEnabled) {
                asset.attributes.sso_singleSignOn = "Enabled";
            } else {
                asset.attributes.sso_singleSignOn = "Disabled";
            }

            var appOwner = (asset.attributes.overview_appOwner).trim();
            if (appOwner.length == 0) {
                asset.attributes.overview_appOwner = provider;
            }
            var appTenant = (asset.attributes.overview_appTenant).trim();
            if (appTenant.length == 0) {
                asset.attributes.overview_appTenant = tenantDomain;
            }

            var isAdvertiseOnly = (asset.attributes.overview_advertiseOnly).trim();
            if (isAdvertiseOnly.toLowerCase() != "true") {
                asset.attributes.overview_advertiseOnly = "false";
            }

            var subscriptionAvailability = (asset.attributes.overview_subscriptionAvailability).trim();
            if(subscriptionAvailability == "current_tenant") {
                asset.attributes.overview_tenants = tenantDomain;
            }

            if(subscriptionAvailability == "all_tenants") {
                asset.attributes.overview_tenants = "";
            }

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

            var artifact1 = artifactManager.get(id);
            var attributes = artifact1.attributes;

            if (attributes.overview_advertiseOnly.toLowerCase() != "true") {
                //adding to database
                addToWebApp(id, provider, name, version, contextname, tracking_code, asset,
                    attributes['sso_singleSignOn'], attributes['sso_idpProviderUrl'],
                    saml2SsoIssuer, revisedURL, allowAnonymous, skipGateway, webappURL);
            }


            //Save the id data to the model
            model.setField('*.id', id);

            log.debug('Finished saving asset with id: ' + id);
        }
    }
};
