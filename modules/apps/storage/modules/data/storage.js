/*
Description:The class is used to provided an API to store files in a source indepedent way
 */
var storageModule=function(){

    var log=new Log('storage');
    var utility=require('/modules/utility.js').utility();
    var modelManagement = require('/modules/data/model.manager.js').modelManager();
    var driverManagement=require('/modules/data/driver.manager.js').driverManager();

    function StorageManager(options){

        this.connectionInfo=null;
        this.context=null;

        this.init(options);

       //Create an instance of the driver manager
       this.driverManager=new driverManagement.DriverManager();

       //Get a default driver
       var driver=this.driverManager.get('default');

       //Create an instance of the model manager
       this.modelManager=new modelManagement.ModelManager({driver:driver,connectionInfo:this.connectionInfo});
    };

    /*
    The function initializes the storage manager
     */
    StorageManager.prototype.init=function(options){
        utility.config(options,this);
    };

    /*
    The function puts a resource into storage
    @key: The key to use in the storage
    @value: An object containing the contentType and a file
     */
    StorageManager.prototype.put=function(key,value){

        //Obtain a resource model
        var resource=this.modelManager.get('Resource');

        //Generate a uuid for the resource
        resource.uuid='';
        resource.contentType=value.contentType;
        resource.contentLength=value.file.getLength();
        resource.content=value.file.getStream();
        resource.tenantId=key.tenantId;

        //Save the resource
        //resource.checkIfTableExists();
        //resource.checkIfTableExists();
        resource.save();

    };

    /*
    The function returns a url to the provided key
    @key: The key by which to search
    @return: A url of the form context/tenant/uuid
     */
    StorageManager.prototype.get=function(key){

        //Obtain a resource model
        var resource=this.modelManager.get('Resource');

        var tenantId=key.tenantId;
        var uuid=key.uuid;

        var results=resource.find({uuid:uuid,tenantId:tenantId});

        return results[0].content||null;
    };


    return{
        StorageManager:StorageManager
    }
};
