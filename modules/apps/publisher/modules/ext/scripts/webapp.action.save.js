var meta = {
    use: 'action',
    purpose: 'save',
    type: 'form',
    source: 'default',
    applyTo: '*',
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
                db.query("MERGE INTO SOCIAL_CACHE (id,tenant,type,body,rating) VALUES('" + combinedId + "','" + domain + "','" + type + "','',0)");
                db.close();
            });
        }
    }

	function trim (str) {
		return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
	}

    function addToWebApp(webappProvider, webappName, webappVersion, webappContext, asset) {

        var apiIdentifier = Packages.org.wso2.carbon.appmgt.api.model.APIIdentifier;
        var apiIdentifierObj = new apiIdentifier(webappProvider, webappName, webappVersion);

        var webApp = Packages.org.wso2.carbon.appmgt.api.model.WebApp;
        var webAppObj = new webApp(apiIdentifierObj);

        webAppObj.setContext(webappContext);

        var apiMgtDAO = Packages.org.wso2.carbon.appmgt.impl.dao.ApiMgtDAO;
        var apiMgtDAOObj = new apiMgtDAO();
        
        var index = 0;
        var attributes = asset.attributes;
        var urlPattern = attributes["uritemplate_urlPattern" + index];
        
        while(urlPattern != null && trim(urlPattern).length > 0){
        	        	
        		var URITemplate = Packages.org.wso2.carbon.appmgt.api.model.URITemplate;
        		var uriTemplate = new URITemplate();
        		 
        		uriTemplate.setHTTPVerb(attributes["uritemplate_httpVerb" + index]);
        		uriTemplate.setAuthType(attributes["uritemplate_authType" + index]);
        		uriTemplate.setUriTemplate(attributes["uritemplate_urlPattern" + index]);
        		uriTemplate.setThrottlingTier(attributes["uritemplate_tier" + index]);
        		uriTemplate.setSkipThrottling(attributes["uritemplate_skipThrottle" + index] === "True");
        		webAppObj.getUriTemplates().add(uriTemplate);
        		               		
        		index++;
        		urlPattern = attributes["uritemplate_urlPattern" + index];        	
        }			                
        
        apiMgtDAOObj.addWebApp(webAppObj);

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

            //name='test-gadget-7';

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

            //adding to database
            addToWebApp(provider, name, version, contextname, asset);

            //Save the id data to the model
            model.setField('*.id', id);

            log.debug('Finished saving asset with id: ' + id);
        }
    }
};
