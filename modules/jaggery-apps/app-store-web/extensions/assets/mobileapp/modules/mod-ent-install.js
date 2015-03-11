    var log =  new Log();

    var performAction = function performAction (action, tenantId, type, app, params) {

        if( typeof params === 'string' ) {
            params = [ params ];
        }

        log.debug(action + " performs on the app " + app + " in tenant " + tenantId + " & " + type + " : " + stringify(params));
//        var  server = require('store').server,
//                store = require('/modules/store.js').store(tenantId, session);
//        asset = store.asset('mobileapp', app);
//        log.debug("Asset retrieved : " + stringify(asset));

        var operationsClass = Packages.org.wso2.carbon.appmgt.mobile.store.Operations;
        var operations = new operationsClass();
        operations.performAction(action, tenantId, type, app, params);

    };


