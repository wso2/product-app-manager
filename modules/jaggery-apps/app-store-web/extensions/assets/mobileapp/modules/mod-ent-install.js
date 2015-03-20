    var log =  new Log();
    var mod =  require('store');
    var user = mod.user;
    var server = require('store').server
    var currentUser = server.current(session);
    var SUBSCRIPTIONS_PATH = '/subscriptions/mobileapp/';



    var performAction = function performAction (action, tenantId, type, app, params) {

        registry = server.systemRegistry(tenantId);

        if( typeof params === 'string' ) {
            params = [ params ];
        }


        log.debug(action + " performs on the app " + app + " in tenant " + tenantId + " & " + type + " : " + stringify(params));


            if(type === 'device'){

                var path = user.userSpace(currentUser) + SUBSCRIPTIONS_PATH +  app;

                if(action == 'install') {
                    subscribe(path, app);
                }else if(action === 'uninstall') {
                    unsubscribe(path, app);
                }

            }else if (type === 'user'){

                params.forEach(function(username){
                    var path = user.userSpace({username: username, tenantId : tenantId }) + SUBSCRIPTIONS_PATH +  app;
                    if(action == 'install') {
                        subscribe(path, app);
                    }else if(action === 'uninstall') {
                        unsubscribe(path, app);
                    }
                });

            }else if (type === 'role'){

                var um = new carbon.user.UserManager(server, tenantId);
                params.forEach(function(role){
                    var users = parse(stringify(um.getUserListOfRole(role)));
                    users.forEach(function(username){
                        var path = user.userSpace({username: username, tenantId : tenantId }) + SUBSCRIPTIONS_PATH +  app;
                        if(action == 'install') {
                            subscribe(path, app);
                        }else if(action === 'uninstall') {
                            unsubscribe(path, app);
                        }
                    });
                });

            }


        function subscribe(path, appId){
           if (!registry.exists(path)) {
                registry.put(path, {name: appId,content: '' });
            }
        }

        function unsubscribe(path){
            if (registry.exists(path)) {
                registry.remove(path);
            }
        }


        var operationsClass = Packages.org.wso2.carbon.appmgt.mobile.store.Operations;
        var operations = new operationsClass();
        operations.performAction(stringify(currentUser), action, tenantId, type, app, params);

    };


