var resource = (function () {

    var SubscriptionService;
    var subsApi;

    var log = new Log('enterprise-subscription-api');

    SubscriptionService = require('/extensions/assets/webapp/services/subscription.js').serviceModule;
    subsApi = new SubscriptionService.SubscriptionService();

	subsApi.init(jagg, session);
  
    /*
     * Returns subscribed enterpises and allowed enterprises.
     */
    var getEnterpriseSubscriptionInfo = function (context) {
        
        var uriMatcher = new URIMatcher(request.getRequestURI());
        var URI = '/{context}/resources/{asset}/{version}/{resource}/{appName}';
        var isMatch = uriMatcher.match(URI);
        var enterprises={};

        if (isMatch) {

            var appName = context.request.getParameter('appName');
            var providerName = context.request.getParameter('meta[ssoProviderName]');
            var providerVersion = context.request.getParameter('meta[ssoProviderVersion]');

            // Get all available enterprices for the app.
            enterprises = subsApi.getEnterprisesForApplication({appName:appName, ssoProviderName:providerName, ssoProviderVersion:providerVersion });
        }
        return enterprises;
    };


    return{
        get: getEnterpriseSubscriptionInfo
    }

})();