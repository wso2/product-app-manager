/*
Description: Handles the logic related to deployment of assets.
Filename: asset.deployment.js
Created Date:13/8/2013
 */
var deployment_logic=function(){

    var utility=require('/modules/utility.js').rxt_utility();
    var bundler=require('/modules/bundler.js').bundle_logic();
    var log=new Log();

    /*
    Deploys the assets in a provided path
     */
    function Deployer(options){
        this.config=null;
        utility.config(options,this);
        this.bundleManager=null;
        this.handlers={};
    }

    /*
    Loads the bundle manager
     */
    Deployer.prototype.init=function(){
        log.info('initializing bundle manager for '+this.config.root);

        this.bundleManager=new bundler.BundleManager({
           path:this.config.root
        });

        log.info('finished initializing bundle manager');

    };

    /*
    The function allows a third party handler to be registered for
    a given asset type
     */
    Deployer.prototype.register=function(assetType,handler){
         this.handlers[assetType]=handler;
    };

    Deployer.prototype.invoke=function(assetType,bundle){
            this.handlers[assetType](bundle,{currentPath:this.config.root+'/'+assetType+'/'+bundle.getName()});
    };

    Deployer.prototype.autoDeploy=function(){
        var that=this;
        log.info('starting auto deploying assets in '+this.config.root);
        this.bundleManager.getRoot().each(function(asset){
             log.info('auto deployment of '+asset.getName());
             that.deploy(asset.getName());
        })
    }

    /*
    The function deploys a provided asset type by invoking the handlers
    @assetType: The asset type to be deployed
     */
    Deployer.prototype.deploy=function(assetType){

         //Check if a handler is present
         if(this.handlers.hasOwnProperty(assetType)){

             //Locate the configuration information for the asset type
             var assetConfiguration=findConfig(this.config,assetType);

             //Check if a configuration block exists for the
             //provided asset type
             if(!assetConfiguration){
                 log.info('could not deploy '+assetType+' as configuration information was not found.');
                 return;
             }

             //Check if the asset type has been ignored
             if(isIgnored(this.config,assetType)){
                 log.info('asset type : '+assetType+' is ignored.');
                 return;
             }

             //Obtain the bundle for the asset type
             var rootBundle=this.bundleManager.get({name:assetType});

             if(rootBundle){

               var that=this;

               log.info('['+assetType.toUpperCase()+'] been deployed.');

               //Deploy each bundle
               rootBundle.each(function(bundle){

                   log.info('deploying '+assetType+' : '+bundle.getName());

                   if(isIgnored(assetConfiguration,bundle.getName())){
                       log.info('ignoring '+assetType+" : "+bundle.getName()+'. Please change configuration file to enable.');
                       return;
                   }
                   that.invoke(assetType,bundle);

                   log.info('finished deploying '+assetType+' : '+bundle.getName());

               });

                 log.info('['+assetType.toUpperCase()+'] ending deployment');
             }
             else{
                 log.info('could not deploy asset '+assetType+' since a bundle was not found.');
             }
         }
        else{

             log.info('could not deploy asset '+assetType+' as no handlers were registered');
         }
    };



    /*
    The function locates the configuration information on a per asset basis
     */
    function findConfig(masterConfig,assetType){
        var assetData=masterConfig.assetData||[];
        var asset;

        //Locate the asset type
        for(var index in assetData){

            asset=assetData[index];

            if(asset.type==assetType){
                return asset;
            }
        }

        return null;
    }

    /*
    The function checks whether the provided target should be ignored
    based on the presence of an ignore array
    @config: An object containing an ignore property
    @target: The string which will be checked
     */
    function isIgnored(config,target){
        var ignored=config.ignore||[];

        if(ignored.indexOf(target)!=-1){
            return true;
        }

        return false;
    }



    return{
          Deployer:Deployer
    }
};
