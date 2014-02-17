var serviceModule=(function(){

    var SubscriptionService;
    var subsApi;

    var log = new Log('subscription-api');

    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();

    subsApi.init(jagg, session);

    var getAppsWithSubs=function(){

        var apps=subsApi.getAppsWithSubs({user:'admin'});

        log.info(apps);

        return apps;
    };

    return{
        get:getAppsWithSubs
    }

})();