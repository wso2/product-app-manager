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

    }

    /*
    The function is invoked when setting the permission of an asset
    @context: An object containing a reference to the current asset bundle been processed
     */
    function onSetAssetPermissions(context){

    }
    /*
    The function is used to check if the current asset is present in the registry
    If the asset is present in the registry then the asset is updated.If not it is added.
     */
    function checkAssetInRegistry(context){

    }

    /*
    The function is used to add a new asset instance to the registry
     */
    function onAddAsset(context){

    }

    /*
    The function is invoked when updating an already existing asset
     */
    function onUpdateAsset(context){

    }

    /*
    The function is invoked when setting tags of an asset
     */
    function onSetTags(context){

    }

    /*
    The function is invoked when setting the ratings for an asset
     */
    function onSetRatings(context){

    }

    var onCreate=function(context){
        log.info('on create called from sites installer');
        log.info('name: '+context.bundle.getName());
    };

    return{
        onAssetInitialization:onAssetInitialization,
        onAssetTypeInitialisation:onAssetTypeInitialisation,
        onCreate:onCreate
    }
};


