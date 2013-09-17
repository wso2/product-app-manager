/*
Description: The following class is used to the sites provided in this directory.
Filename: install.js
Created Date: 16/8/2013
 */

var installer=function(){

    var log=new Log();


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


