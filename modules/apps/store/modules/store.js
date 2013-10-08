var ASSETS_EXT_PATH = '/extensions/assets/';

var ASSET_MANAGERS = 'asset.managers';

var TENANT_STORE = 'tenant.store';

var STORE_CONFIG_PATH = '/_system/config/store/configs/store.json';

var TAGS_QUERY_PATH = '/_system/config/repository/components/org.wso2.carbon.registry/queries/allTags';

//TODO: read from tenant config
var ASSETS_PAGE_SIZE = 'assetsPageSize';

//TODO: read from tenant config
var COMMENTS_PAGE_SIZE = 'commentsPageSize';

var SUBSCRIPTIONS_PATH = '/subscriptions';

var log = new Log();

var merge = function (def, options) {
    if (options) {
        for (var op in def) {
            if (def.hasOwnProperty(op)) {
                def[op] = options[op] || def[op];
            }
        }
    }
    return def;
};

var init = function (options) {
    //addRxtConfigs(tenantId);
    var event = require('/modules/event.js');

    event.on('tenantCreate', function (tenantId) {
        var carbon = require('carbon'),
            config = require('/store-tenant.json'),
            server = require('/modules/server.js'),
            system = server.systemRegistry(tenantId);
        //um = server.userManager(tenantId),
        //GovernanceConstants = org.wso2.carbon.governance.api.util.GovernanceConstants;
        system.put(STORE_CONFIG_PATH, {
            content: JSON.stringify(config),
            mediaType: 'application/json'
        });
        system.put(TAGS_QUERY_PATH, {
            content: 'SELECT RT.REG_TAG_ID FROM REG_RESOURCE_TAG RT ORDER BY RT.REG_TAG_ID',
            mediaType: 'application/vnd.sql.query',
            properties: {
                resultType: 'Tags'
            }
        });
        //um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);
    });

    event.on('tenantLoad', function (tenantId) {
        var user = require('/modules/user.js'),
            server = require('/modules/server.js'),
            carbon = require('carbon'),
            config = server.configs(tenantId),
            CommonUtil = Packages.org.wso2.carbon.governance.registry.extensions.utils.CommonUtil,
            GovernanceConstants = org.wso2.carbon.governance.api.util.GovernanceConstants,
            reg = server.systemRegistry(tenantId),
            um = server.userManager(tenantId);
        var securityProviderModule = require('/modules/security/storage.security.provider.js').securityModule();

        var securityProvider = securityProviderModule.cached();

        //The security provider requires the registry and user manager to work
        securityProvider.provideContext(reg, um);

        //check whether tenantCreate has been called
        if (!reg.exists(STORE_CONFIG_PATH)) {
            event.emit('tenantCreate', tenantId);
        }

        CommonUtil.addRxtConfigs(reg.registry.getChrootedRegistry("/_system/governance"), reg.tenantId);
        um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);
        config[user.USER_OPTIONS] = configs(tenantId);

        config[TENANT_STORE] = new Store(tenantId);


    });

    event.on('login', function (tenantId, user, session) {
        /*var server = require('/modules/server.js'),
         assetManagers = {};
         store(tenantId, session).assetTypes().forEach(function (type) {
         var path = ASSETS_EXT_PATH + type + '/asset.js',
         azzet = new File(path).isExists() ? require(path) : require('/modules/asset.js');
         assetManagers[type] = new azzet.Manager(server.anonRegistry(tenantId), type);
         });
         session[ASSET_MANAGERS] = assetManagers;*/
    });
};

//TODO:
var currentAsset = function () {
    var prefix = require('/store.js').config().assetsUrlPrefix,
        matcher = new URIMatcher(request.getRequestURI());
    if (matcher.match('/{context}' + prefix + '/{type}/{+any}') || matcher.match('/{context}' + prefix + '/{type}')) {
        return matcher.elements().type;
    }
    prefix = require('/store.js').config().extensionsUrlPrefix + prefix;
    if (matcher.match('/{context}' + prefix + '/{type}/{+any}') || matcher.match('/{context}' + prefix + '/{type}')) {
        return matcher.elements().type;
    }
    return null;
};

/**
 * This is a util method to get the store instance.
 * @param o This can be either the tenantId or the request object.
 * @param session
 * @return {*}
 */
var store = function (o, session) {
    var store, configs, tenantId,
        user = require('/modules/user.js'),
        server = require('/modules/server.js');

    tenantId = (o instanceof Request) ? server.tenant(o, session).tenantId : o;

    if (user.current(session)) {
        store = session.get(TENANT_STORE);
        if (store) {
            return store;
        }
        store = new Store(tenantId, session);
        session.put(TENANT_STORE, store);
        return store;
    }
    configs = server.configs(tenantId);
    store = configs[TENANT_STORE];
    if (store) {
        return store;
    }
    store = new Store(tenantId);
    configs[TENANT_STORE] = store;
    return store;
};

var assetManager = function (type, reg) {
    var azzet,
        path = ASSETS_EXT_PATH + type + '/asset.js';
    azzet = (new File(path).isExists() && (azzet = require(path)).Manager) ? azzet : require('/modules/asset.js');
    return new azzet.Manager(reg, type);
};

var configs = function (tenantId) {
    var server = require('/modules/server.js'),
        registry = server.systemRegistry(tenantId);
    return JSON.parse(registry.content(STORE_CONFIG_PATH));
};

//TODO: Remove requiring asset.js, server.js and user.js in multiple places
/**
 * If the session object is passed, then we assume this is an instance of a user. So, we assetManagers are
 * loaded on the fly.
 * @param tenantId
 * @param session
 * @constructor
 */
var Store = function (tenantId, session) {
    var assetManagers = {},
        user = require('/modules/user.js');
    this.tenantId = tenantId;
    this.servmod = require('/modules/server.js');
    this.assetManagers = assetManagers;
    if (session) {
        this.user = user.current(session);
        this.registry = user.userRegistry(session);
        this.session = session;
        this.userSpace = user.userSpace(this.user.username);
    } else {
        configs(tenantId).assets.forEach(function (type) {
            assetManagers[type] = assetManager(type, server.anonRegistry(tenantId));
        });
    }
};

Store.prototype.assetManager = function (type) {
    var manager;
    if (this.user) {
        manager = this.assetManagers[type];
        if (manager) {
            return manager;
        }
        return (this.assetManagers[type] = assetManager(type, this.registry));
    }
    return this.assetManagers[type];
};

Store.prototype.assetsPageSize = function () {
    return configs()[ASSETS_PAGE_SIZE];
};

Store.prototype.commentsPageSize = function () {
    return configs()[COMMENTS_PAGE_SIZE];
};

Store.prototype.assetsPaging = function (request) {
    var page = request.getParameter('page'),
        size = this.assetsPageSize();
    page = page ? page - 1 : 0;
    return {
        start: page * size,
        count: size,
        sort: request.getParameter('sort') || 'recent'
    };
};

Store.prototype.commentsPaging = function (request) {
    var page = request.getParameter('page'),
        size = this.commentsPageSize();
    page = page ? page - 1 : 0;
    return {
        start: page * size,
        count: size,
        sort: request.getParameter('sort') || 'recent'
    };
};

Store.prototype.subscriptionSpace = function (type) {
    return this.userSpace + SUBSCRIPTIONS_PATH + (type ? '/' + type : '');
};

Store.prototype.subscribe = function (type, id) {
    var path = this.subscriptionSpace(type) + '/' + id;
    if (!this.registry.exists(path)) {
        this.registry.put(path, {
            name: id,
            content: ''
        });
    }
};

Store.prototype.unsubscribe = function (type, id) {
    var path = this.subscriptionSpace(type) + '/' + id;
    this.registry.remove(path);
};

Store.prototype.subscriptions = function (type) {
    var fn, assets,
        that = this,
        registry = this.registry,
        path = this.subscriptionSpace(type),
        assetz = {};
    fn = function (path) {
        var type,
            items = [],
            obj = registry.content(path);
        if (!obj) {
            return;
        }
        type = path.substr(path.lastIndexOf('/') + 1);
        //obj = obj();
        obj.forEach(function (path) {
            items.push(that.asset(type, path.substr(path.lastIndexOf('/') + 1)))
        });
        assetz[type] = items;
    };
    if (type) {
        fn(path);
        return assetz;
    }
    //TODO: get this content thing done
    assets = registry.content(path);
    if (!assets) {
        return assetz;
    }
    //TODO: continue
    assets.forEach(function (path) {
        fn(path);
    });
    return assetz;
};

Store.prototype.configs = function () {
    return configs(this.tenantId);
};

/**
 * Returns all tags
 * @param type Asset type
 */
Store.prototype.tags = function (type) {
    var tag, tags, assetType, i, length, count,
        registry = this.registry || this.servmod.anonRegistry(this.tenantId),
        tagz = [],
        tz = {};
    tags = registry.query(TAGS_QUERY_PATH);
    length = tags.length;
    if (type == undefined) {
        for (i = 0; i < length; i++) {
            tag = tags[i].split(';')[1].split(':')[1];
            count = tz[tag];
            count = count ? count + 1 : 1;
            tz[tag] = count;
        }
    } else {
        for (i = 0; i < length; i++) {
            assetType = tags[i].split(';')[0].split('/')[3];
            if (assetType != undefined) {
                if (assetType.contains(type)) {
                    tag = tags[i].split(';')[1].split(':')[1];
                    count = tz[tag];
                    count = count ? count + 1 : 1;
                    tz[tag] = count;
                }
            }
        }
    }
    //api setter
    for (tag in tz) {
        if (tz.hasOwnProperty(tag)) {
            tagz.push({
                name: String(tag),
                count: tz[tag]
            });
        }
    }
    /* 
     for (tag in tz) {
     if (tz.hasOwnProperty(tag)) {
     var result = this.assetManager(type).checkTagAssets({tag: tag });
     if (result.length > 0) {
     tagz.push({
     name: String(tag),
     count: tz[tag]
     });
     }
     }
     }
     */
    return tagz;
};

Store.prototype.comments = function (aid, paging) {
    var registry = this.registry || this.servmod.anonRegistry(this.tenantId);
    return registry.comments(aid, paging);
};

Store.prototype.commentCount = function (aid) {
    var registry = this.registry || this.servmod.anonRegistry(this.tenantId);
    return registry.commentCount(aid);
};

Store.prototype.comment = function (aid, comment) {
    var registry = this.registry || this.servmod.anonRegistry(this.tenantId);
    return registry.comment(aid, comment);
};

Store.prototype.rating = function (aid) {
    var username, registry,
        carbon = require('carbon'),
        usr = this.user;
    if (usr) {
        registry = this.registry;
        username = usr.username;
    } else {
        registry = this.servmod.anonRegistry(this.tenantId);
        username = carbon.user.anonUser;
    }
    return registry.rating(aid, username);
};

Store.prototype.rate = function (aid, rating) {
    var registry = this.registry || this.servmod.anonRegistry(this.tenantId);
    return registry.rate(aid, rating);
};

/**
 * Returns all assets for the current user
 * @param type Asset type
 * @param paging
 */
Store.prototype.assets = function (type, paging) {

    //var type=(type=='null')?null:type;

    //Check if a type has been provided
    /*if(!type){
     log.info('Returning an empty [] for Store.assets.');
     return [];
     }*/

    var options = {};
    options = obtainViewQuery(options);
    var i;

    //var assetz = this.assetManager(type).list(paging);

    var assetz = this.assetManager(type).search(options, paging);


    for (i = 0; i < assetz.length; i++) {
        assetz[i].indashboard = this.isuserasset(assetz[i].id, type);
    }
    return assetz;
};

Store.prototype.tagged = function (type, tag, paging) {

    // var type=(type=='null')?null:type;

    //Check if a type has been provided.
    /*if(!type){
     log.info('Returning an empty [] for Store.tagged.');
     return [];
     } */

    var i;
    var options = {};
    var assets;
    var length;

    options['tag'] = tag;
    options = obtainViewQuery(options);

    assets = this.assetManager(type).search(options, paging);

    length = assets.length;

    for (i = 0; i < length; i++) {
        assets[i].rating = this.rating(assets[i].path);
        assets[i].indashboard = this.isuserasset(assets[i].id, type);
    }
    return assets;
};

/**
 * Returns asset data for the current user
 * @param type Asset type
 * @param aid Asset identifier
 */
Store.prototype.asset = function (type, aid) {
    //var type=(type=='null')?null:type;

    //Check if a type has been provided.
    /*if(!type){
     log.info('Returning an empty [] for store.asset');
     return [];
     }*/

    var asset = this.assetManager(type).get(aid);
    asset.rating = this.rating(asset.path);
    return asset;
};

/**
 * Returns links of a asset for the current user
 * @param type Asset type
 */
Store.prototype.assetLinks = function (type) {
    var mod,
        path = ASSETS_EXT_PATH + type + '/asset.js',
        file = new File(path);
    if (!file.isExists()) {
        return [];
    }
    mod = require(path);
    return mod.assetLinks(this.user);
};

/**
 * @param type
 * @param count
 * @return {*}
 */
Store.prototype.popularAssets = function (type, count) {

    //var type=(type=='null')?null:type;

    //Check if a type has been provided.
    /*if(!type){
     log.info('Returning an empty [] for  store.popularAssets.');
     return [];
     }*/

    var options = {};
    options = obtainViewQuery(options);
    var paging = {
        start: 0,
        count: count || 5,
        sort: 'popular'
    };

    var assets = this.assetManager(type).search(options, paging);
    return assets;
};

Store.prototype.recentAssets = function (type, count) {

    //var type=(type=='null')?null:type;

    //If a type is not given
    /*if(!type){
     log.info('Returning an empty [] for Store.recentAssets.');
     return [];
     }*/

    var i, length;
    var paging = {
        start: 0,
        count: count || 5,
        sort: 'recent'
    };
    var options = {};
    options = obtainViewQuery(options);

    var recent = this.assetManager(type).search(options, paging);

    //log.info('re')
    /* var recent = this.assetManager(type).list({
     start: 0,
     count: count || 5,
     sort: 'recent'
     }); */
    length = recent.length;
    for (i = 0; i < length; i++) {
        recent[i].rating = this.rating(recent[i].path).average;
        recent[i].indashboard = this.isuserasset(recent[i].id, type);
    }
    return recent;
};

Store.prototype.assetCount = function (type, options) {

    //Check if the type is provided
    //var type=(type=='null')?null:type;

    //Check if the asset type is provided
    //If there is no asset type then return 0
    /*if(!type){
     log.info('Returning 0 for Store.assetCount.');
     return 0;
     }*/

    //Create the default query by lifecycle state
    options = options || {};
    options = obtainViewQuery(options);


    return this.assetManager(type).count(options);
};

/**
 * Returns all enabled asset types for the current user
 */
    //TODO
Store.prototype.assetTypes = function () {
    return this.configs().assets;
};

//TODO:
Store.prototype.cache = function (type, key, value) {
    var cache = require('/modules/cache.js'), data = cache.cached(type) || (cache.cache(type, {}));
    return (data[key] = value);
};

//TODO:
Store.prototype.cached = function (type, key) {
    var cache = require('/modules/cache.js'), data = cache.cached(type);
    return data ? data[key] : null;
};

//TODO:
Store.prototype.invalidate = function (type, key) {
    var cache = require('/modules/cache.js'), data = cache.cached(type);
    delete data[key];
};

Store.prototype.search = function (options, paging) {
    var i, length, types, assets;
    var type = options.type;

    //var attributes = options.attributes || (options.attributes = {});
    //adding status field to get only the published assets
    //attributes['overview_status'] = /^(published)$/i;

    //We should only obtain assets in the Published life-cycle state.
    options = obtainViewQuery(options);

    if (type) {
        var assetz = this.assetManager(type).search(options, paging);
        for (i = 0; i < assetz.length; i++) {
            assetz[i].indashboard = this.isuserasset(assetz[i].id, type);
        }
        return assetz;
    }
    types = this.assetTypes();
    assets = {};
    length = types.length;
    for (i = 0; i < length; i++) {
        type = types[i];
        assets[type] = this.assetManager(types[i]).search(options, paging);
    }
    return assets;
};

//TODO: check the logic
Store.prototype.isuserasset = function (aid, type) {
    var j,
        userown = {};
    if (!this.user) {
        return false;
    }
    var userAssets = this.subscriptions();
    if (!userAssets[type]) {
        return false;
    }
    for (j = 0; j < userAssets[type].length; j++) {
        if (userAssets[type][j]['id'] == aid) {
            userown = userAssets[type][j]['id'];
        }
    }
    return userown.length > 0;
};

Store.prototype.addAsset = function (type, options) {
    this.assetManager(type).add(options);
};

Store.prototype.updateAsset = function (type, options) {
    this.assetManager(type).update(options);
};

Store.prototype.removeAsset = function (type, options) {
    this.assetManager(type).remove(options);
};

var LIFECYCLE_STATE_PROPERTY = 'lifecycleState';
var DEFAULT_ASSET_VIEW_STATE = 'published'; //Unless specified otherwise, assets are always visible when Published

/*
 The function creates a query object to be used in the Manager.search
 based on the visibleIn property of the store.json.
 @options: The object to be used as the query
 @return: A modified options with the state set to the search criteria
 */
var obtainViewQuery = function (options) {

    var storeConfig = require('/store.json').lifeCycleBehaviour;
    var visibleStates = storeConfig.visibleIn || DEFAULT_ASSET_VIEW_STATE;

    options[LIFECYCLE_STATE_PROPERTY] = visibleStates;

    log.debug('options: ' + stringify(options));


    return options;
}

var TENANT_STORE_MANAGERS='store.managers'
var SUPER_TENANT = -1234;
var APP_MANAGERS = 'application.master.managers';
var LOGGED_IN_USER = 'LOGGED_IN_USER';

/*
 The following function can be invoked to obtain the managers used by the store.The instance is cached
 in the session.
 @o: The current request
 @session: The current session
 @return: An instance of the StoreMasterManager object containing all of the managers used by the Store
 */
var storeManagers = function (o, session) {
    var storeMasterManager;
    var tenantId;
    var server = require('/modules/server.js');

    //We check if there is a valid session
    if (session.get(LOGGED_IN_USER) != null) {
        return handleLoggedInUser(o, session);
    }
    else {
        //No session,anonymous access
        return handleAnonUser();
    }


}
/*
The function handles the initialization and caching of managers when a user is logged in
@o: The current request
@session: The current session
@return: An instance of a MasterManager object either anon or store
 */
function handleLoggedInUser(o, session) {
    var storeMasterManager = session.get(TENANT_STORE_MANAGERS);

    var tenantId = (o instanceof Request) ? server.tenant(o, session).tenantId : o;

    if (storeMasterManager) {
        return storeMasterManager;
    }

    storeMasterManager = new StoreMasterManager(tenantId, session);
    session.put(TENANT_STORE_MANAGERS, storeMasterManager);

    return storeMasterManager;
}

/*
The function handles the initialization of managers when a user is not logged in (annoymous accesS)
 */
function handleAnonUser() {
    var anonMasterManager = application.get(APP_MANAGERS);

    //Check if it is cached
    if (anonMasterManager) {

        return anonMasterManager;

    }
    anonMasterManager = new AnonStoreMasterManager();
    application.put(APP_MANAGERS, anonMasterManager);

    return anonMasterManager;
}

/*
 The class is used to encapsulate the managers used by the store
 @tenantId: The tenantId of the current tenant
 @session: The session of the currently logged in user
 */
function StoreMasterManager(tenantId, session) {
    var user = require('/modules/user.js');
    var registry = user.userRegistry(session);

    var managers = buildManagers(registry);

    this.modelManager = managers.modelManager;
    this.rxtManager = managers.rxtManager;
    this.tenantId = tenantId;
}

/*
 The class is used to encapsulate managers when a user is not logged in
 All managers user the system registry
 */
function AnonStoreMasterManager() {
    var registry = server.systemRegistry(SUPER_TENANT);

    var managers = buildManagers(registry);

    this.modelManager = managers.modelManager;
    this.rxtManager = managers.rxtManager;
    this.tenantId = SUPER_TENANT;
}

/*
 The function is used to create the managers
 @tenantId: The tenantId of the current tenant
 @registry: The registry of the current user who is logged in
 @return: The managers used by the store
 */
var buildManagers = function (registry) {

    var rxt_management = require('/modules/rxt/rxt.manager.js').rxt_management();
    var ext_parser = require('/modules/rxt/ext/core/extension.parser.js').extension_parser();
    var ext_core = require('/modules/rxt/ext/core/extension.core.js').extension_core();
    var ext_mng = require('/modules/rxt/ext/core/extension.management.js').extension_management();
    var config = require('/store-tenant.json');
    var rxtManager = new rxt_management.RxtManager(registry);

    //All of the rxt xml files are read and converted to a JSON object called
    //a RxtTemplate(Refer rxt.domain.js)
    rxtManager.loadAssets();

    var parser = new ext_parser.Parser();

    //Go through each rxt template
    for (var index in rxtManager.rxtTemplates) {
        var rxtTemplate = rxtManager.rxtTemplates[index];
        parser.registerRxt(rxtTemplate);
    }

    parser.load(config.paths.RXT_EXTENSION_PATH);

    var adapterManager = new ext_core.AdapterManager({parser: parser});
    adapterManager.init();

    var fpManager = new ext_core.FieldManager({parser: parser});
    fpManager.init();

    var ruleParser = new ext_parser.RuleParser({parser: parser});
    ruleParser.init();

    var modelManager = new ext_mng.ModelManager({parser: parser, adapterManager: adapterManager});

    return{
        modelManager: modelManager,
        rxtManager: rxtManager
    }
}