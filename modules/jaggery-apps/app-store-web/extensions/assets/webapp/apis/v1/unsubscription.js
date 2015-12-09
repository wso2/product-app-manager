var resource = (function () {

    var SubscriptionService;
    var subsApi;

    var log = new Log('unsubscription-api');

    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();

	subsApi.init(jagg, session);

    AuthService = require('/extensions/assets/webapp/services/authentication.js').serviceModule;
    authenticator = new AuthService.Authenticator();
    authenticator.init(jagg, session);

    

     
    var removeSubscription = function (context) {

        var parameters = context.request.getAllParameters();
        var subscription = {};
        subscription['apiName'] = parameters.apiName;
        subscription['apiVersion'] = parameters.apiVersion;
        subscription['apiTier'] = parameters.apiTier;
        subscription['apiProvider'] = parameters.apiProvider;
        subscription['appName'] = parameters.appName;
        subscription['user'] = authenticator.getLoggedInUser().username;

        log.info('Trying to add a subscription');

        var result = subsApi.removeSubscription(subscription);
        if(result.status == true){
            subscription['op_type'] = 'DENY';
            result = subsApi.updateVisibility(subscription);
        }

        return result;
    };
    
  

  

    return{
        post: removeSubscription
       
        
      
    }

})();