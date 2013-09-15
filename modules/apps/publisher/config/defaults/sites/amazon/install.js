
var installer=function(){


    var log=new Log();


    var onCreate=function(context){
        log.info('on create called');
    };


    return{
        onCreate:onCreate
    }
};