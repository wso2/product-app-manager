/*
 Description: The following class is used to install all assets in a generic manner.
 Filename: install.js
 Created Date: 16/8/2013
 */

var installer = function () {


    var log = new Log('master.installer');
    var utility = require('/modules/utility.js').rxt_utility();
    var carbon = require('carbon');
    var server = require('/modules/server.js');
    var GovernanceUtils = Packages.org.wso2.carbon.governance.api.util.GovernanceUtils;
    var SUPER_TENANT_ID = -1234;
    var SEARCH_INDEX = 'overview_name';
    var DESIRED_LIFECYCLE_STATE='Published';
    var PROMOTE_COUNT=4;    //The number of promote attempts before giving up reaching desired state.
    var INVOKED_OPERATION='Promote'; //The action invoked to reach the desired state.
    var DEFAULT_LIFECYCLE='SampleLifeCycle2';

    /*
     The function is used to initialize an individual asset by first reading the
     configuration file
     @context: An object containing a reference to the root of an asset
     */
    function onAssetInitialization(context) {
        log.info('reading configuration data from ' + context.bundle.getName() + ' [json].');

        //obtain the configuration file
        var configFile = context.bundle.get({extension: 'json'}).result();

        //If the configuration file does not exist then stop.
        if (!configFile) {

            log.info('unable to load configuration file for ' + context.bundle.getName());
            context['stopProcessing'] = true;
            return;
        }

        //Read the contents
        var configContents = configFile.getContents();
        var jsonConfig = parse(configContents);

        //Clone the object but ignore tags and rate
        var artifact = utility.cloneObject(jsonConfig, ['tags', 'rate']);

        //Create the deployment object
        context['artifact'] = artifact;

        //Set the tags
        context['tags'] = jsonConfig.tags.split(',');

        //Set the ratings
        context['rate'] = jsonConfig.rate;
        context['path']='/_system/governance/gadgets/' + artifact.attributes.overview_provider +
            '/' + artifact.attributes.overview_name + '/' + artifact.attributes.overview_version;

        log.info('tags located: ' + context.tags);
        log.info('rate located: ' + context.rate);
    }

    /*
     The function initializes an asset by checking if an assets rxt is present
     in the registry.If it is not present then rxt template is copied to the directory
     @context: An object containing a reference to the root of an asset
     */
    function onAssetTypeInitialisation(context) {
        log.info('master asset type initialization called.This should be overridden by using a installer script at the '
        +' root level of the asset.');
    }

    /*
     The function is called to initialize registry logic.It is used to create an artifact manager instance
     which will be used to handle assets.
     */
    function onCreateArtifactManager(context) {

        //Create a registry instance
        var registry = new carbon.registry.Registry(server.server(), {
            username: 'admin',
            tenantId: SUPER_TENANT_ID
        });

        //Load the governance artifacts
        GovernanceUtils.loadGovernanceArtifacts(registry.registry);

        try {
            //Create a new artifact manager
            var artifactManager = new carbon.registry.ArtifactManager(registry, context.assetType);
        } catch (e) {
            log.info('unable to create artifactManager of type: ' + context.assetType);
            return;
        }

        var userManager=server.userManager(SUPER_TENANT_ID);

        context['artifactManager'] = artifactManager;
        context['registry'] = registry;
        context['userManager']=userManager;

        log.info('created artifact manager for ' + context.assetType);
    }

    /*
     The function is invoked when setting the permission of an asset.
     @context: An object containing a reference to the current asset bundle been processed
     */
    function onSetAssetPermissions(context) {
        var userManager=context.userManager;

        //userManager.authorizeRole(carbon.user.anonRole,context.path,carbon.registry.actions.GET);
    }

    /*
     The function is used to check if the current asset is present in the registry
     If the asset is present in the registry then the asset is updated.If not it is added.
     */
    function checkAssetInRegistry(context) {
        var artifactManager = context.artifactManager;
        var artifact = context.artifact;

        //Assume that the asset does not exist
        context['isExisting'] = false;

        //Check if the asset exists
        var locatedAssets = artifactManager.find(function (asset) {

            var attributes = asset.attributes;
            //Check if the search index is present
            if (attributes.hasOwnProperty(SEARCH_INDEX)) {
                log.info(attributes[SEARCH_INDEX]+' == '+artifact.attributes[SEARCH_INDEX]);
                //Check if the search index values are the same
                if (attributes[SEARCH_INDEX] == artifact.attributes[SEARCH_INDEX]) {
                    log.info('match');
                    return true;
                }
            }
            return false;
        }, {start: 0, count: 10});

        //Check if any assets were located
        if (locatedAssets.length > 0) {
            log.info('asset is present');
            context['isExisting'] = true;
            context['currentAsset']=locatedAssets[0];
        }

    }

    /*
     The function is used to add a new asset instance to the registry
     */
    function onAddAsset(context) {
        var artifactManager=context.artifactManager;
        var artifact=context.artifact;

        //Add the asset
        //artifactManager.add(artifact);
    }

    /*
     The function is invoked when updating an already existing asset
     */
    function onUpdateAsset(context) {
         var artifactManager=context.artifactManager;
         var currentAsset=context.currentAsset;
         var artifact=context.artifact;

         //Set the id
         artifact.id=currentAsset.id;

         //artifactManager.update(artifact);
    }

    /*
    The function attaches a lifecycle to the asset
     */
    function onAttachLifecycle(context){
        var artifactManager=context.artifactManager;
        var currentAsset=context.currentAsset;
        var currentLifeCycleName=currentAsset.lifecycle||null;
        var currentLifeCycleState=currentAsset.lifecycleState||null;
        var attempts=0;

       //Check if a lifecycle has been attached
      if(!currentLifeCycleName){
            //Attach the lifecycle
            //artifactManager.attachLifecycle(DEFAULT_LIFECYCLE,currentAsset);

            //Update the current asset
            currentAsset=artifactManager.get(currentAsset.id);
        }
        else{
            //We skip moving to the Published state.
            log.info('skipping promotion operations as a lifecycle has been attached');
            return;
        }

        //Try to reach the desired life-cycle state before the attempt limit
        while((currentLifeCycleState!=DESIRED_LIFECYCLE_STATE)&&(attempts<PROMOTE_COUNT)){

            //artifactManager.promoteLifecycleState(INVOKED_OPERATION,currentAsset);

            //Update the current life-cycle state.
            //currentLifeCycleState=artifactManager.getLifecycleState(currentAsset);

            //Increased the attempts by one
            attempts++;
        }
    }

    /*
     The function is invoked when setting tags of an asset
     */
    function onSetTags(context) {

        var tags = context.tags;

        log.info('adding tags ['+context.tags+'] to path '+context.path);

        //Go through all tags
        for (tag in tags) {
            //Check if the tag is present
            if (tags.hasOwnProperty(tag)) {
                //context.registry.tag(context.path, tags[tag]);
            }
        }

        log.info('finished adding tags');
    }

    /*
     The function is invoked when setting the ratings for an asset
     */
    function onSetRatings(context) {

        var rate=context.rate;

        log.info('adding rating : '+context.rate+' to path '+context.path);

        if(!rate){

        }
    }


    return{
        onAssetInitialization: onAssetInitialization,
        onAssetTypeInitialisation: onAssetTypeInitialisation,
        onCreateArtifactManager: onCreateArtifactManager,
        onSetAssetPermissions: onSetAssetPermissions,
        checkAssetInRegistry: checkAssetInRegistry,
        onAddAsset: onAddAsset,
        onUpdateAsset: onUpdateAsset,
        onAttachLifecycle:onAttachLifecycle,
        onSetTags: onSetTags,
        onSetRatings: onSetRatings
    }
};


