var serviceModule=(function(){

    var SubscriptionService;
    var subsApi;

    var log = new Log('subscription-api');

    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();

    subsApi.init(jagg, session);

    AuthService = require('/extensions/assets/webapp/services/authentication.js').serviceModule;
    authenticator = new AuthService.Authenticator();
    authenticator.init(jagg, session);

    var getAppsWithSubs=function(){

        var username = authenticator.getLoggedInUser().username;

        var apps=subsApi.getAppsWithSubs({user:username});

          return apps;
    };

    return{
        get:getAppsWithSubs
    }

})();