var resource = (function () {

    var SubscriptionService;
    var subsApi;

    var log = new Log('subscription-api');

    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();

	subsApi.init(jagg, session);

    AuthService = require('/extensions/assets/webapp/services/authentication.js').serviceModule;
    authenticator = new AuthService.Authenticator();
    authenticator.init(jagg, session);

    

    /*
     Subscribes the given application to an API with the provided details
     */
    var addSubscription = function (context) {

        var parameters = context.request.getAllParameters();
        var subscription = {};
        subscription['apiName'] = parameters.apiName;
        subscription['apiVersion'] = parameters.apiVersion;
        subscription['apiTier'] = parameters.apiTier;
        subscription['apiProvider'] = parameters.apiProvider;
        subscription['appName'] = parameters.appName;
        subscription['subscriptionType'] = parameters.subscriptionType;
        subscription['enterprises'] = '';

        if(subscription['subscriptionType'] == 'ENTERPRISE'){
            subscription['enterprises'] = parameters.enterprises;                     
        }

        subscription['user'] = authenticator.getLoggedInUser().username;

        log.info('Trying to add a subscription');

        var result = subsApi.addSubscription(subscription);

        return result;
    };

    /*
     Returns all of the apis to which the provided app is subscribed to
     */
    var getSubscription = function (context) {

        var uriMatcher = new URIMatcher(request.getRequestURI());
        var URI = '/{context}/resources/{asset}/{version}/{resource}/{appName}';
        var isMatch = uriMatcher.match(URI);
        var apis={};

        if (isMatch) {

            var appName = uriMatcher.elements().appName;
            var userName = authenticator.getLoggedInUser().username;

            //Get the api name
            apis = subsApi.getSubsForApp({appName:appName, user:userName });
        }
        return apis;
    };


    return{
        post: addSubscription,
        get: getSubscription
    }

})();