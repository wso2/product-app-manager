/*
The class is used to manage the database drivers
 */
var driverManager=function(){

    var config=require('/config/storage.json');
    var utility=require('/modules/utility.js').rxt_utility();
    var bundler=require('/modules/bundler.js').bundle_logic();
    var log=new Log('driver.manager');

    var FILE_QP='query.provider.js';
    var FILE_QT='query.translator.js';
    var FILE_DRIVER='driver.js';
    var DEFAULT_DRIVER='default';

    var dsm=org.wso2.carbon.ndatasource.core.DataSourceManager;


    var DB_DRIVERS={
        MYSQL_DRIVER:'jdbc:mysql',
        ORACLE_DRIVER:'jdbc:oracle',
        H2_DRIVER:'jdbc:h2',
        UNSUPPORTED:'none'
    };

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
        this.dataSourceManager=new DataSourceManager();

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

        log.info('driver for '+driverType+' not found.');
        return null;
    };

    /*
    The function is used to locate a driver that supports the provided datasource
    @name: The name of a datasource to be accessed
     */
    DriverManager.prototype.getDriver=function(name){
        var datasource=this.dataSourceManager.get(name);
        var driverType=datasource.getDriver();

        if(datasource==DB_DRIVERS.UNSUPPORTED){
            throw 'Cannot find a driver for '+name;
        }


    };


    /*
    The class is used to store information on a DataSource used by a driver
     */
    function DataSource(dsObject){
       this.instance=dsObject;
    }

    /*
    The function returns the type of driver required by the datasource by looking
    at the connection url
    @return: The driver type based on the connection string.If there is no match UNSUPPORTED
            is returned.If there is no connection url the unsupported value is returned.
     */
    DataSource.prototype.getDriver=function(){

       var connectionUrl=this.instance.getUrl();

       if(!connectionUrl){
           return DB_DRIVERS.UNSUPPORTED;
       }

       for(var key in DB_DRIVERS){

           //Check if the url matches one of the DB_DRIVERS
           if(connectionUrl.indexOf(DB_DRIVERS[key])!=-1){
               return key;
           }
       }
       log.info('driver type in '+connectionUrl+' not found.');
       return DB_DRIVERS.UNSUPPORTED;

    };

    /*
    The class is used to access the carbon datasources repository
     */
    function DataSourceManager(){
        this.instance=new dsm();
    }

    /*
    The function is used to obtain a reference to a datasource defined in the
    master.datasources.xml.
    @name: The name of the datasource
    @return: A DataSource object
     */
    DataSourceManager.prototype.get=function(name){

        var datasource=this.instance.getInstance().getDataSourceRepository().getDataSource(name);
        var dsObject=datasource.getDSObject();

        //If the datasource is not found
        if(!datasource){
           return null;
        }

        return new DataSource(dsObject);
    };

    return{
        DriverManager:DriverManager,
        DataSourceManager:DataSourceManager
    }
};
