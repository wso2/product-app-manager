/*
 Description: The data injector is used to convert a uuid to a link specific to an app using the injector
 E.g. uuid: 78-2383933/apple.png
 In the publisher it will made available as publisher/storage/uuid/app.png
 In the store it will be made available as store/storage/uuid/app.png
 Filename:asset.display.injector.js
 Created Date: 27/9/2013
 */

var injector = function () {

    var log = new Log('asset.display.injector');
    var dataInjectModule = require('/modules/data/data.injector.js').dataInjectorModule();
    var modes = dataInjectModule.Modes;

    /*
     The modes in which the handler should be executed
     @return: The operation mode in which the handler should be executed
     */
    function operationModes() {
        return modes.DISPLAY;
    }

    /*
     The function checks whether the provided object should be processed by the
     the injector
     @object: The object to be checked for a match
     return: True if the object is handled,else false
     */
    function isHandled(object) {

        //Check if the object has the attributes property
        log.info('checking if the object is handled');
        return true;
    }

    /*
     The function obtains a set of targeted fields and writes back
     a customized Url
     @context: The context data to handle a particular field
     @return: True, if handled successfully,else false
     */
    function handle(context) {



        return false;
    }

    function getUUID(value){
        var value=object[field];

        //Check if the uuid is present as uuid/file
        var components=value.split('/');

        //A single value,could be a uuid
        if(components.length==1){
            //Check if it is a uuid
        }
        //Not a uuid/file value
        else if(components.length<2){
           //Check if it is a uuid
        }

    }

    /*
    The function checks whether a provided value is a uuid
     */
    function isUuid(value){
        return true;
    }


    return{
        operationModes: operationModes,
        isHandled: isHandled,
        handle: handle
    };
};

