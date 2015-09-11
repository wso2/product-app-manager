var resource = (function () {
    var log = new Log('subscriptions-api');
    var AuthService = require('/extensions/assets/webapp/services/authentication.js').serviceModule;
    var authenticator = new AuthService.Authenticator();
    authenticator.init(jagg, session);

    var getAPIProviderObj = function (username) {
        return new (require('appmgtpublisher')).APIProvider(String(username));
    };
    var getAppsWithSubs = function () {
        var user = authenticator.getLoggedInUser();
         if (user == null) {
             response.status = 401;
             response.contentType = 'application/json';
             return {message: "You do not have permission to access this API"};
         }

         var URI = '/{context}/resources/{asset}/{version}/subscriptions/{appProvider}/{appName}/{appVersion}';
         var uriMatcher = new URIMatcher(request.getRequestURI());
         if (!uriMatcher.match(URI)) {
             response.status = 404;
             response.contentType = 'application/json';
             return {message: "API endpoint not found"};
         }
         var params = uriMatcher.elements();
         var apiProvider = getAPIProviderObj(user.username);
         return apiProvider.getSubscribersOfAPI(params.appProvider, params.appName, params.appVersion);
    };

    return {
        get: getAppsWithSubs
    }

})();