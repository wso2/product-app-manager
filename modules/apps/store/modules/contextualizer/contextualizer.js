var Contextualizer;

(function(){


    var defaultConfig={
      moduleDirPath:'/modules'
    };

    var log=new Log('contextualizer');

    var bundlerModule=require('/modules/bundler.js').bundle_logic();

    var readModulesDir=function readModules(options){

        var bundleManager=new bundlerModule.BundleManager(options);

        //Read all of the files and directories
        var root=bundleManager.getRoot();

        log.info(options);
        log.info('reading root');

        //Create a map of the directory structure
        root.each(function(bundle){
           log.info('bundle found: '+bundle);
        });
    };

    var readServiceConfiguration=function(serviceConfigFilePath){

    };

    function Context(options){
        this.serviceDirectory='/services';
        this.init({path:serviceDirectory});
    }

    Context.prototype.init=function(options){
        readModulesDir(options);
    } ;

    /*
    The function obtains a particular module by first checking if a module with a similar name
    exists for a given extension
     */
    Context.prototype.get=function(){

    };

    Context.prototype.getConfig=function(){

    };

   Contextualizer=Context;


})();