

var installer=function(){


    var log=new Log();

    var onInit=function(context){

        //obtain the configuration file

        //Read the contents

    };

    var onCreate=function(context){
        log.info('on create called');
        log.info('name: '+context.bundle.getName());
    };

    var onLoad=function(context){
        log.info('on load called');
    };

    var permissionCheck=function(context){

    }

    return{
         onInit:onInit,
         onCreate:onCreate
    }
};


/*var module=(function(){

    var log=new Log();

    var onInit=function(context){

    };

    var onCreate=function(context){
        log.info('on create called');
    };

    var onLoad=function(context){
        log.info('on load called');
    };

    var permissionCheck=function(context,targets){

    }

    return{
        onInit:onInit,
        onCreate:onCreate,
        onLoad:onLoad
    }

})(); */

