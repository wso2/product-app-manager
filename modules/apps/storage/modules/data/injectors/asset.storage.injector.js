/*
Description: The script is used to check for the existence of a file path in a given set of properties
             and to load and store these files in the Storage Manager.
Filename:asset.storage.injector.js
Created Date: 27/9/2013
 */

var injector=function(){

    var log=new Log('asset.storage.injector');
    var utility=require('/modules/utility.js').utility();
    var dataInjectModule=require('/modules/data/data.injector.js').dataInjectorModule();
    var modes=dataInjectModule.Modes;

    /*
    The function performs any initialization logic.It is invoked only once when the handler is assembled
    @context: The context which will be used in handling requests
     */
    function init(context){
          context['storageManager']=null;
    }

    /*
     The modes in which the handler should be executed
     @return: The operation mode in which the handler should be executed
     */
    function operationModes(){
       return modes.STORAGE;
    }

    /*
    The function checks whether the provided object should be processed by the
    the injector
    @object: The object to be checked for a match
    return: True if the object is handled,else false
     */
    function isHandled(object){

        //We check for the exsistence of an attributes property
        if(object.hasOwnProperty('attributes')){
            log.info('object is handled.');
            return true;
        }

        log.info('object is not handled');
    }

    /*
    The function extracts a set of targeted properties, if they contain a
    local file path it is added to the storage
    @context: The context data to handle a particular field
    @return: True, if handled successfully,else false
     */
    function handle(context){
        var object=context.object||{};
        var fields=['images_thumbnail','images_banner'];
        var field;
        var uuid;
        var path;

        for(var index in fields){
            field=fields[index];

            utility.isPresent(object.attributes,field,function(){
                log.info('checking field '+field);
                path=object.attributes[field];

                uuid=addToStorage(path);

                if(uuid){
                    log.info('added file to storage.');
                    object.attributes[field]=uuid;
                }
            });
        }
        log.info(object);
        return true;
    }
    /*
    The function attempts to add the provided path to the storage if it is present
    and returns a uuid
    @path: A path to a resource
    @return: If the file is added to storage then a uuid is returned,else null
     */
    function addToStorage(path){
        var file=new File(path);
        var uuid=null;

        //Only add it storage if it is a valid path and get the uuid
        if(file.isExists()){

            uuid='added file';

        }

        return uuid
    }


    return{
        init:init,
        operationModes:operationModes,
        isHandled:isHandled,
        handle:handle
    };
};
