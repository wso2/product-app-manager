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
    var utility=require('/modules/utility.js').utility();
    var dataInjectModule = require('/modules/data/data.injector.js').dataInjectorModule();
    var modes = dataInjectModule.Modes;


    /*
     The modes in which the handler should be executed
     @return: The operation mode in which the handler should be executed
     */
    function operationModes() {
        return modes.DISPLAY;
    }

    function init(){
        log.info('Does not perform any initialization');
    }

    /*
     The function checks whether the provided object should be processed by the
     the injector
     @object: The object to be checked for a match
     return: True if the object is handled,else false
     */
    function isHandled(object) {

        //We check for the exsistence of an attributes property
        if(object.hasOwnProperty('attributes')){
            return true;
        }

        log.info('object is not handled');

        return false;
    }

    /*
     The function obtains a set of targeted fields and writes back
     a customized Url
     @context: The context data to handle a particular field
     @return: True, if handled successfully,else false
     */
    function handle(context) {

        var object=context.object||{};
        var fields=['images_banner','images_thumbnail'];
        var url;
        var uuid;
        var field;
        //Go through all of the targetted fields

        for(var index in fields){

            log.info('checking for '+fields[index]);

            utility.isPresent(object.attributes,fields[index],function(){
                field=fields[index];
                log.info('found '+field);

                //Obtain the uuid
                url=object.attributes[field];

                uuid=getUUID(url);

                //Check if it is a valid uuid and create a new url
                if((uuid)&&(utility.isValidUuid(uuid))){

                    object.attributes[field]=getUrl(url);
                }

            });
        }

        log.info(object);

        return true;
    }

    /*
    The function obtains the UUID from a value
    @value: A string containing a UUID
    @return: A UUID if one is present else null
     */
    function getUUID(value){

        //Check if the uuid is present as uuid/file
        var components=value.split('/');
        var uuid=null;

        //A single value,could be a uuid
        if(components.length==1){
            uuid=components[0];
        }
        //Not a uuid/file value
        else if(components.length>1){
            uuid=components[0];
        }

        return uuid;

    }

    function getUrl(uuid){
        var context='publisher/storage';
        return context+'/'+uuid;
    }


    return{
        init:init,
        operationModes: operationModes,
        isHandled: isHandled,
        handle: handle
    };
};

