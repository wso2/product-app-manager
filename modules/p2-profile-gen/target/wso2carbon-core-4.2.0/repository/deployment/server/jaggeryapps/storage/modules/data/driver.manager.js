/*
The class is used to manage the database drivers
 */
var driverManager=function(){

    var config=require('/config/storage.json');
    var utility=require('/modules/utility.js').utility();
    var bundler=require('/modules/bundler.js').bundle_logic();
    var log=new Log('driver.manager');

    var FILE_QP='query.provider.js';
    var FILE_QT='query.translator.js';
    var FILE_DRIVER='driver.js';
    var DEFAULT_DRIVER='default';

    /*
   Paths
     */
    var defaultPath='/modules/data/';
    var driverPath=config.driverPath||'/drivers/';



    function DriverManager(){
        this.defaultBundleManager=new bundler.BundleManager({
             path:defaultPath
        });
        this.driverMap={};
        this.defaultQP=null;
        this.defaultQT=null;

        //Create the default which will be used to build all other drivers
        this.createDefaultDriver();
    }

    /*
    The function constructs a default driver based on the modules/data implementation
     */
    DriverManager.prototype.createDefaultDriver=function(){

        var defaultRootBundle=this.defaultBundleManager.getRoot();

        //Read the modules/data directory to get the default drivers
        var driverBundle=defaultRootBundle.get({name:FILE_DRIVER}).result();

        if(!driverBundle){
            throw 'unable to create default db driver';
        }

        var driver=require(defaultPath+FILE_DRIVER).driver();
        var driverInstance=new driver.DBDriver();

        //Obtain the default query providers
        var queryProviderBundle=defaultRootBundle.get({name:FILE_QP}).result();

        if(!queryProviderBundle){
            throw 'unable to create a default query provider';
        }

        this.defaultQP=require(defaultPath+FILE_QP).queryProvider();

        var queryTranslatorBundle=defaultRootBundle.get({name:FILE_QT}).result();

        if(!queryTranslatorBundle){
            throw 'unable to create default query translator';
        }

        this.defaultQT=require(defaultPath+FILE_QT).queryTranslator();

        this.driverMap[DEFAULT_DRIVER]=driverInstance;
    };

    /*
    The function loads drivers in the driver directory
     */
    DriverManager.prototype.loadDrivers=function(){

        var bundleManager=new bundler.BundleManager({
            path:driverPath
        });
    };

    /*
    The function returns a database driver after calling the drivers initialize method
    @driverType: The type of driver
    @return: A driver instance
     */
    DriverManager.prototype.get=function(driverType){

        //Check if the driver is supported
        if(this.driverMap.hasOwnProperty(driverType)){
            this.driverMap[driverType].init({queryProvider:this.defaultQP, queryTranslator:this.defaultQT});
            return this.driverMap[driverType];
        }

        log.debug('driver for '+driverType+' not found.');
        return null;
    };

    return{
        DriverManager:DriverManager
    }
};
