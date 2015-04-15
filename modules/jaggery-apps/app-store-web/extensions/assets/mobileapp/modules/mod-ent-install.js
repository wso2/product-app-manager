    var log =  new Log();
    var mod =  require('store');
    var user = mod.user;
    var server = require('store').server
    var currentUser = server.current(session);
    var SUBSCRIPTIONS_PATH = '/subscriptions/mobileapp/';
    var mobileGeneric = new Packages.org.wso2.carbon.appmgt.mobile.store.Generic();



    var performAction = function performAction (action, tenantId, type, app, params) {

        registry = server.systemRegistry(tenantId);
        store = require('/modules/store.js').store(tenantId, session);

        if( typeof params === 'string' ) {
            params = [ params ];
        }


        log.debug(action + " performs on the app " + app + " in tenant " + tenantId + " & " + type + " : " + stringify(params));


            if(type === 'device'){

                var path = user.userSpace(currentUser) + SUBSCRIPTIONS_PATH +  app;

                if(action == 'install') {
                    subscribe(path, app, currentUser.username);
                }else if(action === 'uninstall') {
                    unsubscribe(path, app, currentUser.username);
                }

            }else if (type === 'user'){

                params.forEach(function(username){
                    var path = user.userSpace({username: username, tenantId : tenantId }) + SUBSCRIPTIONS_PATH +  app;
                    if(action == 'install') {
                        subscribe(path, app, username);
                    }else if(action === 'uninstall') {
                        unsubscribe(path, app, username);
                    }
                });

            }else if (type === 'role'){

                var um = new carbon.user.UserManager(server, tenantId);
                params.forEach(function(role){
                    var users = parse(stringify(um.getUserListOfRole(role)));
                    users.forEach(function(username){
                        var path = user.userSpace({username: username, tenantId : tenantId }) + SUBSCRIPTIONS_PATH +  app;
                        if(action == 'install') {
                            subscribe(path, app, username);
                        }else if(action === 'uninstall') {
                            unsubscribe(path, app, username);
                        }
                    });
                });

            }


        function subscribe(path, appId, username){
           if (!registry.exists(path)) {
                registry.put(path, {name: appId,content: '' });
                setVisibility(appId, username, 'ALLOW');
            }
        }

        function unsubscribe(path, appId, username){
            if (registry.exists(path)) {
                registry.remove(path);
                setVisibility(appId, username, 'DENY');
            }
        }


        function setVisibility(appId, username, opType){
            asset = store.asset('mobileapp', appId);
            var path = "/_system/governance/mobileapps/" + asset.attributes.overview_provider + "/" + asset.attributes.overview_platform + "/" + asset.attributes.overview_name + "/" + asset.attributes.overview_version;
            mobileGeneric.showAppVisibilityToUser(path, username, opType);
        }


        var operationsClass = Packages.org.wso2.carbon.appmgt.mobile.store.Operations;
        var operations = new operationsClass();
        operations.performAction(stringify(currentUser), action, tenantId, type, app, params);

    };


