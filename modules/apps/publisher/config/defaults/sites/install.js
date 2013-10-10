/*
Description: The following class is used to the sites provided in this directory.
Filename: install.js
Created Date: 16/8/2013
 */

var installer=function(){

    var log=new Log();


    /*
    The function initializes an asset by checking if an assets rxt is present
    in the registry.If it is not present then rxt template is copied to the directory
    @context: An object containing a reference to the root of an asset
     */
    function onAssetTypeInitialisation(context){
         log.info('loading site rxt data');
    }

	function onAssetInitialization(context) {
		    log.debug('reading configuration data from ' + context.bundle.getName() + ' [json].');

		    //obtain the configuration file
		    var configFile = context.bundle.get({extension: 'json'}).result();

		    //If the configuration file does not exist then stop.
		    if (!configFile) {

		        log.debug('unable to load configuration file for ' + context.bundle.getName());
		        context['stopProcessing'] = true;
		        return;
		    }

		    //Read the contents
		    var configContents = configFile.getContents();
		    var jsonConfig = parse(configContents);

		    //Clone the object but ignore tags and rate
		    var artifact = utility.cloneObject(jsonConfig, ['tags', 'rate']);

            var now = new Date().getTime();


		    artifact.attributes.images_thumbnail = context.assetPath + artifact.attributes.images_thumbnail;
		    artifact.attributes.images_banner = context.assetPath + artifact.attributes.images_banner;
		    artifact.attributes.overview_url=artifact.attributes.overview_url;
            artifact.attributes.overview_createdtime=now;
		    //artifact.attributes.images_thumbnail = context.httpContext + artifact.attributes.images_thumbnail;
		    //artifact.attributes.images_banner = context.httpContext + artifact.attributes.images_banner;


		    //Create the deployment object
		    context['artifact'] = artifact;


		    //Set the tags
		    context['tags'] = jsonConfig.tags.split(',');

		    //Set the ratings
		    context['rate'] = jsonConfig.rate;
		    context['path'] = '/_system/governance/'+context.assetType+'/' + artifact.attributes.overview_provider +
		        '/' + artifact.attributes.overview_name + '/' + artifact.attributes.overview_version;

		    log.debug('tags located: ' + context.tags);
		    log.debug('rate located: ' + context.rate);
		}

    return{
        onAssetTypeInitialisation:onAssetTypeInitialisation,
        onAssetInitialization: onAssetInitialization
    }
};


