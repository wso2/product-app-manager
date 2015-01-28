var resource=(function(){

    var AuthService=require('/extensions/assets/webapp/services/authentication.js').serviceModule;

    var authenticator=new AuthService.Authenticator();

    authenticator.init(jagg,session);


    var login=function(context){
        var parameters=context.request.getAllParameters();

        authenticator.login({username:parameters.username,password:parameters.password,tenant:null});
    };

    var logout=function(context){
        authenticator.logout();
    };

    return{
        post:login,
        delete:logout
    }
})();