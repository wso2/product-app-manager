/*
The class provides the template for a database driver with common functionality
 */

var driver=function(){
    var log=new Log('default.driver');
    var utility=require('/modules/utility.js').utility();

    function DBDriver(){
        this.queryProvider=null;
        this.queryTranslator=null;
        this.instance=null; //The instance of the db
    }

    DBDriver.prototype.init=function(options){
        log.info('init method called');
        utility.config(options,this);
    };

    DBDriver.prototype.connect=function(config){
       var connectionString='';
       var username=config.username;
       var password=config.password;
       var dbConfig=config.dbConfig||{};

        try{
            log.info
            this.instance=new Database(connectionString,username,password,dbConfig);
        }
        catch(e){
            throw e;
        }
    };

    DBDriver.prototype.disconnect=function(){
        this.instance.close();
    };

    /*
    The function is used to issue a query to the database
    @query: The query to be executed
    @modelManager: An instance of the model manager which is using the db driver
    @cb: An optional callback
    @return: The results of the query after translation
     */
    DBDriver.prototype.query=function(query,schema,modelManager,options,cb){
        var result=this.instance.query(query)||[];
        var options=options||'';
        var processed;

        //Check if the raw results need to be returned
        switch(options){
            case 'RAW':
                processed=result;
                break;
            default:
                processed=this.queryTranslator.translate(schema,modelManager,result);
                break;
        }

        return processed;
    };

    return{
        DBDriver:DBDriver
    }
}


