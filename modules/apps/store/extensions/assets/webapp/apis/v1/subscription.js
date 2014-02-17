var resource = (function () {

    var SubscriptionService;
    var subsApi;

    var log = new Log('subscription-api');

    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();

    subsApi.init(jagg, session);

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
        subscription['user'] = 'admin'; //TODO: Get the user from the session or as a request parameter?

        log.info('Trying to add a subscription');

        var result = subsApi.addSubscription(subscription);


        log.info("result:"+result);

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

            //Get the api name
            apis = subsApi.getSubsForApp({appName:appName, user: 'admin'});
        }
        return apis;
    };


    return{
        post: addSubscription,
        get: getSubscription
    }

})();