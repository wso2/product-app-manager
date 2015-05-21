    var log =  new Log();
    var mod =  require('store');
    var user = mod.user;
    var server = require('store').server
    var currentUser = server.current(session);
    var SUBSCRIPTIONS_PATH = '/subscriptions/mobileapp/';
    var mobileGeneric = new Packages.org.wso2.carbon.appmgt.mobile.store.Generic();
    var mdmConfig = parse(String((new Packages.org.wso2.carbon.appmgt.mobile.store.MDMConfig()).getConfigs()));

    var isMDMOperationsEnabled = mdmConfig.EnableMDMOperations == "true" ? true : false;

    var isDirectDownloadEnabled = mdmConfig.EnableDirectDownload == "true" ? true : false;


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

        if(isMDMOperationsEnabled){
            var operationsClass = Packages.org.wso2.carbon.appmgt.mobile.store.Operations;
            var operations = new operationsClass();
            operations.performAction(stringify(currentUser), action, tenantId, type, app, params);
        }




        if(isDirectDownloadEnabled){

            useragent = request.getHeader("User-Agent");

            if(useragent.match(/iPad/i) || useragent.match(/iPhone/i) || useragent.match(/Android/i)) {

                if(mdmConfig.AppDownloadURLHost == "%http%"){
                    var serverAddress = carbon.server.address('http');
                }else if(mdmConfig.AppDownloadURLHost == "%https%"){
                    var serverAddress = carbon.server.address('https');
                }else{
                    var serverAddress = mdmConfig.AppDownloadURLHost;
                }

                asset = store.asset('mobileapp', app);
                if( asset.attributes.overview_type == "enterprise" ||  asset.attributes.overview_type == "webapp"){
                    if(asset.attributes.overview_platform == "android"){
                        var location = serverAddress +  asset.attributes.overview_url;
                    }else if(asset.attributes.overview_platform == "ios"){
                        var filename = asset.attributes.overview_url.split("/").pop();
                        var location =  "itms-services://?action=download-manifest&amp;url=" + carbon.server.address('https') + "/" + mdmConfig.IosPlistPath + "/" + tenantId +  "/" + filename;
                    }
                }else if(asset.attributes.overview_type == "webapp"){
                    var location = asset.attributes.overview_url;
                }

                else{
                    if(asset.attributes.overview_platform == "android"){
                        var location = "https://play.google.com/store/apps/details?id=" + asset.attributes.overview_packagename;
                    }else if(asset.attributes.overview_platform == "ios"){
                        var location = "https://itunes.apple.com/en/app/id" + asset.attributes.overview_appid;
                    }
                }



                print({redirect: true, location : location});

            }else{
                print({});
            }


        }
    };


