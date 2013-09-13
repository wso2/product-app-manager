var publisher = require('/config/publisher.js').config();

var init = function () {

    var events = require('/modules/event.js');
    var log = new Log('deployer');
    var carbon = require('carbon');
    var server = require('/modules/server.js');
    var SUPER_TENANT_ID = -1234;
    var GovernanceUtils = Packages.org.wso2.carbon.governance.api.util.GovernanceUtils;
    var deployer = require('/modules/asset.deployment.js').deployment_logic();
    var publisherConfig = require('/config/publisher-tenant.json');

    events.on('tenantLoad', function (tenantId) {

        //Only deploy for super tenant
        if (tenantId != SUPER_TENANT_ID) {
            log.info('ignoring deployment since tenant is not a super tenant.');
            return;
        }

        log.info('tenant ' + tenantId + ' loaded.');

        var registry = new carbon.registry.Registry(server.server(), {
            username: 'admin',
            tenantId: tenantId
        });

        //Load the governance artifacts
        GovernanceUtils.loadGovernanceArtifacts(registry.registry);

        /*
         The function pushes the assets to the registry after creating an appropriate artifact manager
         */
        var push = function (options, optionsTag, optionsRate, assetType, tenantId, registry) {
            var tag, tags, rate;
            var carbon = require('carbon');

            var path = '/_system/governance/' + assetType + '/' + options.attributes.overview_provider + '/' + options.attributes.overview_name
                + '/' + options.attributes.overview_version;

            var server = require('/modules/server.js');
            var um = server.userManager(tenantId);

            var artifactManager = new carbon.registry.ArtifactManager(registry,assetType);

            log.info(artifactManager);

            var asset = options;

            var assets = artifactManager.find(function (asset) {
                var attributes = asset.attributes;
                if(attributes.hasOwnProperty('overview_name')){
                    if(attributes['overview_name']==options.attributes.overview_name){

                        log.info('matching asset found.');
                        return true;
                    }
                }

                return false;
            }, {start: 0, count: 10});

            //Check if the asset has already been published
            if (assets.length > 0) {

                log.info('attaching lifecycle and promoting');

                var lifecycle=assets[0].lifecycle;
                var lifecycleState=assets[0].lifecycleState;
                log.info('currently attached lifecycle: '+lifecycle+ ' state: '+lifecycleState);

                //If there is no lifecycle attached
                if(!lifecycle){

                    log.info('no life-cycle is attached');
                    //Attach the lifecycle
                    artifactManager.attachLifecycle('SampleLifeCycle2',assets[0]);
                    lifecycle=artifactManager.getLifeCycleName(assets[0]);
                    lifecycleState=artifactManager.getLifecycleState(assets[0]);

                    log.info('current lifecycle state: '+lifecycleState+' lifecycle: '+lifecycle);

                    log.info('finished attaching life-cycle');
                }

                //If the state is not published
                if(lifecycle!='Published'){

                    var counter=3; //TODO: This will not work when the new life-cycle is attached.

                    //Get a new instance of the asset
                    var newAssetInstance=artifactManager.get(assets[0].id);

                    //Keep promoting till we get to the Published state or our promote
                    //attempts have run out.
                    while((lifecycleState!='Published')&&(counter>0)){
                        log.info
                        //Promote the state of the assets.
                        artifactManager.promoteLifecycleState('Promote',newAssetInstance);

                        //Get the new lifecycle
                        lifecycleState=artifactManager.getLifecycleState(newAssetInstance);

                        log.info('new state: '+lifecycleState);
                        counter--;
                    }
                }
            }
            //The asset has not been added.
            else {
                log.info('asset '+asset.attributes.overview_name+' does not exist');

                //TODO: Create the asset.

                /*artifactManager.add(asset);
                 artifactManager.attachLifecycle('SampleLifeCycle2',asset);

                 //Promote to the published state
                 for(var index=0;index<2;index++){
                 artifactManager.promoteLifecycleState('Promote',asset);
                 }*/
            }


            /* um.authorizeRole(carbon.user.anonRole, path, carbon.registry.actions.GET);
             tags = optionsTag;
             for (tag in tags) {
             if (tags.hasOwnProperty(tag)) {
             registry.tag(path, options.tags[tag]);
             }
             }
             rate = optionsRate;
             if (options.rate != undefined) {
             registry.rate(path, rate);
             }  */
        };

        /*
         Deploys the gadgets
         */
        var deployGadget = function deployGadgets(bundle, config) {
            log.info('executing deployment logic for ' + bundle.getName());

            //Read the configuration file
            var configBundle = bundle.get({extension: 'xml'}).result();
            var base = 'X/' + config.currentPath;
            var path = base + '/' + configBundle.getName() + '/';

            //Check if a configuration bundle is present
            if (!configBundle) {
                log.info('configuration bundle for ' + bundle.getName() + ' not found,stopping deployment.');
                return;
            }

            var xmlContent = configBundle.getContents();
            var removedXmlContent = xmlContent.replace(/^\s*<\?.*\?>\s*/, "");
            var xml = new XML(removedXmlContent);


            //Create an object to hold the data in the configuration file
            var obj = {};

            var tags = (String(xml.*::ModulePrefs.@tags)).split(',');
            var rate = Math.floor(Math.random() * 5) + 1;
            obj['attributes'] = {};
            obj['attributes']['overview_name'] = xml.*::ModulePrefs.@title;
            obj['attributes']['overview_provider'] = 'admin';
            obj['attributes']['overview_version'] = '1.0.0';
            obj['attributes']['overview_description'] = xml.*::ModulePrefs.@description;
            obj['attributes']['overview_url'] = path + configBundle.getName() + '.xml';
            obj['attributes']['images_thumbnail'] = path + 'thumbnail.jpg';
            obj['attributes']['images_banner'] = path + 'banner.jpg';

            push(obj, tags, rate,'gadget', SUPER_TENANT_ID,registry);
        };

        /*
         Deploys the sites and ebooks
         */
        var deployEbook = function deployJSONAsset(bundle, config) {
            log.info('executing deployment logic for ' + bundle.getName());

            var base = 'X' + config.currentPath;
            var configBundle = bundle.get({extension: 'json'}).result();

            //Check if a configuration file is present
            if (!configBundle) {
                log.info('configuration bundle for ' + bundle.getName() + ' not found,stopping deployment.');
                return;
            }

            var content = configBundle.getContents();
            var jsonConfig = parse(content);
            var tags = jsonConfig.tags.split(',');
            var rate = jsonConfig.tags.split(',');

            push(jsonConfig, tags, rate,'ebook', SUPER_TENANT_ID,registry);
        };

        /*
         Deploys the sites and ebooks
         */
        var deploySite = function deployJSONAsset(bundle, config) {
            log.info('executing deployment logic for ' + bundle.getName());

            var base = 'X' + config.currentPath;
            var configBundle = bundle.get({extension: 'json'}).result();

            //Check if a configuration file is present
            if (!configBundle) {
                log.info('configuration bundle for ' + bundle.getName() + ' not found,stopping deployment.');
                return;
            }

            var content = configBundle.getContents();
            var jsonConfig = parse(content);
            var tags = jsonConfig.tags.split(',');
            var rate = jsonConfig.tags.split(',');

            push(jsonConfig, tags, rate,'site', SUPER_TENANT_ID,registry);
        };


        //Create a deployment manager instance
        var deploymentManager = new deployer.Deployer({
            config: publisherConfig.defaultAssets
        });

        deploymentManager.init();
        deploymentManager.register('gadgets', deployGadget);
        deploymentManager.register('sites', deploySite);
        deploymentManager.register('ebooks', deployEbook);
        deploymentManager.autoDeploy();

    });
}


/*
 var addSSOConfig = (function () {
 var deployer = require('/modules/deployer.js');
 //Adding SSO Configs
 deployer.sso({'issuer': 'publisher',
 'consumerUrl': publisher.ssoConfiguration.publisherAcs,
 'doSign': 'true',
 'singleLogout': 'true',
 'useFQUsername': 'true',
 'issuer64': 'cHVibGlzaGVy'});
 })();*/



