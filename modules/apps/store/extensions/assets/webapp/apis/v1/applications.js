var resource=(function(){

    var log=new Log('application-logic');

    /*
    The method returns all of the applications for the currently logged in user
     */
    var getApplications=function(context){

        var AppService = require('/extensions/assets/webapp/services/app.js').serviceModule;

        appApi = new AppService.AppService();

        appApi.init(jagg,session);

        return appApi.getApplications('admin');

    };

    return{
        get:getApplications
    }
})();