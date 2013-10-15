var dbScriptManagerModule=function(){

    var bundler=require('/modules/bundler.js').bundle_logic();
    var carbon=require('carbon');
    var HOME=carbon.server.home;
    var DBSCRIPT_HOME='/dbscripts';
    var CACHE_DBSCMANAGER='db.script.manager';
    var STORAGE='/storage';
    var log=new Log('db.script.manager');


    /*
    The class is used to read the scripts
     */
    function DBScriptManager(){
        this.map={};
        this.bundleManager=new bundler.BundleManager({path:HOME+DBSCRIPT_HOME+STORAGE});

    }

    /*
    The function reads the contents of the dbscripts/storage directory
     */
    DBScriptManager.prototype.load=function(){
        var rootBundle=this.bundleManager.getRoot();
        var that=this;
        rootBundle.each(function(bundle){
             if(!bundle.isDirectory()){
                 return;
             }

            handleDBType(bundle,that.map);
        });
    };

    /*
    The function returns the schema create script for a given db type
    @dbType: The type of database (e.g. h2, mysql or oracle)
    @schema: The type of schema
    @return: A string copy of the sql script
     */
    DBScriptManager.prototype.find=function(dbType,schema){

        //Check if the db type is handled
        if(!this.map.hasOwnProperty(dbType)){
            return '';
        }

        //Check if the schema is handled
        if(!this.map[dbType].hasOwnProperty(schema)){
            return '';
        }

        var script=this.map[dbType][schema];
        return script;
    };


    /*
    The function is used to read a single directory in the storage folder for
    scripts
    @bundle: A bundle object pointing to a script directory in the storage folder
    @map: The map of dbtype to schema
     */
    function handleDBType(bundle,map){

        //Get the name of bundle
        var bundleName=bundle.getName();
        map[bundleName]={};


        //Get all of the scripts in the current bundle
        bundle.each(function(scriptBundle){

            //Get all of the sql files
            if(scriptBundle.getExtension()=='sql'){

                //Get the name of the file
                var scriptName=scriptBundle.getName().replace('.'+scriptBundle.getExtension(),'');


                //Store the contents of script
                map[bundleName][scriptName]=scriptBundle.getContents();
            }

        });
    }

    /*
    The function caches the DBScriptManager in the application context
     */
    function getInstance(){

        var instance=application.get(CACHE_DBSCMANAGER);

        //Check if there is already a cached copy of the script manager
       // if(!instance){
            instance=new DBScriptManager();
        instance.load();
          //  application.put(CACHE_DBSCMANAGER,instance);
      //  }

        return instance;
    }

    return {
        DBScriptManager:DBScriptManager,
        getInstance:getInstance
    }

};