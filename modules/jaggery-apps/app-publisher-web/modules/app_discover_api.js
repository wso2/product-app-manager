var api = {};
(function(api){
    var HTTP_ERROR_NOT_IMPLEMENTED = 501;
    var HTTP_ERROR = 500;
    var MSG_ERROR_NOT_IMPLEMENTED = 'The provided action is not supported by this endpoint';

    var log= new Log();

    var discover_client = require('/modules/app_discover.js').discover_client();
    var storeUser=require('store').user;
    var server = require('store').server;

    var msg = function(code, message, data) {
        var obj = {};
        obj.code = code;
        obj.message = message;
        obj.data = data;
        return obj;
    };

    var successMsg = function(obj) {
        obj.success = true;
        return obj;
    };
    var errorMsg = function(obj) {
        obj.success = false;
        return obj;
    };


    /**
     * Allows request data to be sent in either the request body or
     * as url encoded query parameters
     * @param  {Object} req  Request object
     * @return {Object}      An object which contains data from request body and url encoded parameters
     */
    var obtainData = function(req) {
        var data = {};
        if (req.getContentType() === 'application/json') {
            try {
                data = req.getContent();
            } catch (e) {
                log.debug('Unable to obtain content from request', e);
            }
        }
        var params = req.getAllParameters('UTF-8');
        //Mix the content with request parameters
        for (var key in params) {
            data[key] = params[key];
        }
        return data;
    };


    var getProviderName = function(req) {
        var username = server.current(session).username;//.get('LOGGED_IN_USER');

        var tenantDomain = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        var tenantIdVal = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantId();
        var userNameWithTenant = "";
        if(tenantIdVal=='-1234'){
            userNameWithTenant = storeUser.cleanUsername(username);
        }else {
            userNameWithTenant = storeUser.cleanUsername(username)+'-AT-'+tenantDomain;
        }
        return userNameWithTenant;
    }

    api.getLoggedInUser = function(session) {
        var username = server.current(session).username;//.get('LOGGED_IN_USER');

        var tenantDomain = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantDomain();
        var tenantIdVal = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext().getTenantId();
        var provider = "";
        if(tenantIdVal=='-1234'){
            provider = storeUser.cleanUsername(username);
        }else {
            provider = storeUser.cleanUsername(username)+'@'+tenantDomain;
        }
        return provider;
    }

    api.loadCreatableAsset = function(context, req, res, session, options)  {
        var publisher = require('/modules/publisher.js').publisher(request, session);
        var proxyContext= context.post['proxy_context_path'];
        var loggedInUser = jagg.getUser().username;
        var shortName = options.appType;
        var applicationId = options.appId;

        log.debug('Asset API discover asset add called '+applicationId);
        //check for mobile
        if(shortName === "mobileapp"){
            log.error('Mobile application discovery is not yet supported.');
            result = {ok: 'false', message: 'Mobile application discovery is not yet supported.'};
            return;
        }

        var sessionData = session.get('sessionData');

        if(sessionData == null) {
            log.error('Session expired');
            result = {ok: 'false', message: 'Session expired'};
            return;
        }

        var discoverClient = session.get('DiscoverClient');
        if(discoverClient == null) {
            discoverClient = new discover_boot.DiscoverClient(null);
            session.put('DiscoverClient', discoverClient);
        }
        var discoverResult = discoverClient.getApplicationData2(sessionData.serverUrl,
                    sessionData.serverUserName, sessionData.serverPassword,
                    shortName, "wso2as", applicationId);
        if(discoverResult.status.code == 0) {
            var applicationMetaData = discoverResult.data;
            applicationMetaData.providerName = getProviderName(req);
            applicationMetaData.proxyContext = proxyContext;

            result = {ok: 'true', message: 'Asset added', applicationId : applicationMetaData.overview_name,
                    appName : applicationMetaData.overview_displayName,
                    proxyContext: proxyContext, data : applicationMetaData};
        } else {
            result = {ok: 'false', message: 'Asset addition failed', applicationId : applicationMetaData.overview_name,
                    appName : applicationMetaData.overview_displayName,
                    proxyContext: proxyContext };
        }

        return successMsg(msg(200, 'The asset is created', result));
    }

    api.resolve = function(ctx, req,res,session,uriParams){
        var action = uriParams.action;
        var result = errorMsg(msg(HTTP_ERROR_NOT_IMPLEMENTED, MSG_ERROR_NOT_IMPLEMENTED));
        var data = obtainData(req);
        ctx.post = data;
        switch(action){
            case 'loadCreatableAsset' :
                result = api.loadCreatableAsset(ctx, req, res, session, uriParams);
                break;
            default:
                break;
        }

        return result;
    };
}(api));