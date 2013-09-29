/*
Description: The script is used to check for the existence of a file path in a given set of properties
             and to load and store these files in the Storage Manager.
Filename:asset.storage.injector.js
Created Date: 27/9/2013
 */

var injector=function(){

    var log=new Log('asset.storage.injector');
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

        //Check if the object has the attributes property
        log.info('checking if the object is handled');
        return true;
    }

    /*
    The function extracts a set of targeted properties, if they contain a
    local file path it is added to the storage
    @context: The context data to handle a particular field
    @return: True, if handled successfully,else false
     */
    function handle(context){



        return false;
    }

    function handleField(){
        //Check if the value is a uuid ,if it is then do nothing

    }


    return{
        init:init,
        operationModes:operationModes,
        isHandled:isHandled,
        handle:handle
    };
};
