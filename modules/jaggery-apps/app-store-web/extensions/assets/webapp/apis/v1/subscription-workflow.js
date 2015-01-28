var resource = (function () {

    var SubscriptionService;
    var subsApi;


    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();
    subsApi.init(jagg, session);

    AuthService = require('/extensions/assets/webapp/services/authentication.js').serviceModule;
    authenticator = new AuthService.Authenticator();
    authenticator.init(jagg, session);  

     
    var checkSubscriptionWorkflow = function (context) {

        var result = subsApi.checkSubscriptionWorkflow();
        return result;
    };

    return{
        post: checkSubscriptionWorkflow   
    }

})();
