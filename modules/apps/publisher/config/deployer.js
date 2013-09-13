var publisher = require('/config/publisher.js').config();


//var log=new Log('publisher.config.deployer');

//log.info('Loaded the publisher.deployer script..');

/*
The function is used to check the default asset directory
 */
var loadDefaultAssets=function(){

    var config=require('/config/publisher-tenant.json');

    var assets=config.assetData||[];    //The default assets

    var enabled=config.enableDefaultAssets||true;   //By default the assets are enabled

    //Do not load anything if not enabled.
    if(!enabled){
      return;
    }

    //Go through each asset entry
    utility.each(assets,function(asset){

        var type=asset.type;
        var path=asset.path;
        var ignoredAssets=asset.ignored;
        var visibleTo=asset.visibleTo;

        //Get all of the folders in the path
        var assetDirectories=utility.fileio.getDir(path);

        //Go through each directory
        utility.each(assetDirectories,function(directory){

            //Check for the existence of any configuration files
            var found=false;
            var configFile;
            var directoryPath=directory.getPath();
            var fileType;
            var files;

            //Locate a configuration file
            for(var index=0; (index<SUPPORTED_CONFIG_FILE_TYPES.length)&&(!found);index++){

                  fileType=SUPPORTED_CONFIG_FILE_TYPES[index];

                  files=utility.fileio.getFiles(directoryPath,fileType);

                  if(files.length!=0){
                       configFile=files[0];
                       found=true;
                  }
            }

            //Load the asset
            if(found){
                loadAsset(asset,directoryPath,configFile,fileType);
            }

        });

        //Check if the asset should be skipped

    });
};

/*
The function is used to load a single asset
 */
var loadAsset=function(asset,dirPath,configFile,configFileType){

}







var SESSION_SIGNAL='started';
var SESSION_SIGNAL_POS_VALUE=true;

//The following file types can be used for configuration
var SUPPORTED_CONFIG_FILE_TYPES=['json','xml'];

/*
 The function
 */
function isSessioned(assetName){

    var exisitingSession=Session[SESSION_SIGNAL];

    if(exisitingSession){
        log.info('Deploying '+assetName+'.');
    }
}

/*
 The function checks whether a given asset should be skipped based
 on a provided ignore array
 @assetName: The name of the asset to be checked
 @ignoredAssets: The asset to be ignored.
 @return: True if the asset can be ignored.False otherwise
 */
function isIgnorableAsset(assetName,ignoredAssets){
    return (ignoredAssets.indexOf(assetConfigFile.getName())==-1);
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



