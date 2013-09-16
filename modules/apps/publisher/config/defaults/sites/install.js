/*
Description: The following class is used to the sites provided in this directory.
Filename: install.js
Created Date: 16/8/2013
 */

var installer=function(){

    var log=new Log();

    /*
    The function is used to initialize an individual asset by first reading the
    configuration file
    @context: An object containing a reference to the root of an asset
     */
    function onAssetInitialization(context){

        log.info('loading '+context.bundle.getName()+' configuration data');

        //obtain the configuration file

        //Read the contents

        //Create the deployment object

        //Set the tags

        //Set the ratings

    }

    /*
    The function initializes an asset by checking if an assets rxt is present
    in the registry.If it is not present then rxt template is copied to the directory
    @context: An object containing a reference to the root of an asset
     */
    function onAssetTypeInitialisation(context){
         log.info('loading site rxt data');
    }


    return{
        onAssetTypeInitialisation:onAssetTypeInitialisation
    }
};


