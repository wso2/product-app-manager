var ASSETS_EXT_PATH = '/extensions/assets/';

var ASSET_MANAGERS = 'asset.managers';

var TENANT_STORE = 'tenant.store';

var STORE_CONFIG_PATH = '/_system/config/store/configs/store.json';

var TAGS_QUERY_PATH = '/_system/config/repository/components/org.wso2.carbon.registry/queries/allTags';

var QUERY_PATH_TAGS_BY_TYPE_AND_LIFECYCLE = '/_system/config/repository/components/org.wso2.carbon.registry/queries/tagsByMediaTypeAndLifecycle'

//TODO: read from tenant config
var ASSETS_PAGE_SIZE = 'assetsPageSize';

//TODO: read from tenant config
var COMMENTS_PAGE_SIZE = 'commentsPageSize';

var SUBSCRIPTIONS_PATH = '/subscriptions';

var RESOURCE_TYPE_WEBAPP = 'webapp';

var RESOURCE_TYPE_MOBILEAPP = 'mobileapp';

var log = new Log();

var isUserTenantIdDifferFromUrlTenantId = function(userTenantId, urlTenantId) {

    if(urlTenantId && (userTenantId !== urlTenantId)){
        return true;
    }else{
        return false;
    }

};

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
        var role, roles,
            carbon = require('carbon'),
            mod = require('store'),
            server = mod.server,
            config = require('/config/store-tenant.json'),
            system = server.systemRegistry(tenantId),
            um = server.userManager(tenantId);
        system.put(options.tenantConfigs, {
            content: JSON.stringify(config),
            mediaType: 'application/json'
        });
		system.put(TAGS_QUERY_PATH, {
            content: 'SELECT RRT.REG_TAG_ID FROM REG_RESOURCE_TAG RRT ORDER BY RRT.REG_TAG_ID',
            mediaType: 'application/vnd.sql.query',
            properties: {
                resultType: 'Tags'
            }
        });
		system.put(QUERY_PATH_TAGS_BY_TYPE_AND_LIFECYCLE, {
			content: 'SELECT RRT.REG_TAG_ID'
		 				+ ' FROM'
						+ ' REG_RESOURCE_TAG RRT, REG_TAG RT, REG_RESOURCE R, REG_RESOURCE_PROPERTY RRP, REG_PROPERTY RP'
						+ ' WHERE'
						+ ' RT.REG_ID = RRT.REG_TAG_ID'
						+ ' AND R.REG_VERSION = RRT.REG_VERSION'
						+ ' AND RP.REG_ID = RRP.REG_PROPERTY_ID'
						+ ' AND R.REG_VERSION = RRP.REG_VERSION'
						+ ' AND R.REG_MEDIA_TYPE LIKE ?'
						+ ' AND RP.REG_NAME LIKE ?'
						+ ' AND RP.REG_VALUE LIKE ?',
			mediaType: 'application/vnd.sql.query',
			properties: {
				resultType: 'Tags'
			}
		});
        roles = config.roles;
        for (role in roles) {
            if (roles.hasOwnProperty(role)) {
                if (um.roleExists(role)) {
                    um.authorizeRole(role, roles[role]);
                } else {
                    um.addRole(role, [], roles[role]);
                }
            }
        }
        /*user = um.getUser(options.user.username);
         if (!user.hasRoles(options.userRoles)) {
         user.addRoles(options.userRoles);
         }*/
        //application.put(key, options);
        //um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);
    });

    event.on('tenantLoad', function (tenantId) {
        var mod = require('store'),
            server = mod.server,
            carbon = require('carbon'),
            config = server.configs(tenantId),
            CommonUtil = Packages.org.wso2.carbon.governance.registry.extensions.utils.CommonUtil,
            GovernanceConstants = org.wso2.carbon.governance.api.util.GovernanceConstants,
            reg = server.systemRegistry(tenantId),
            um = server.userManager(tenantId);

        CommonUtil.addRxtConfigs(reg.registry.getChrootedRegistry("/_system/governance"), reg.tenantId);
        um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);

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

    event.on('userRegister', function (tenantId, user) {
        configs(tenantId).userRoles.forEach(function (role) {
            if (user.hasRoles([role])) {
                return;
            }
            user.addRoles([role]);
        });
    });
};

//TODO:
var currentAsset = function () {
    var prefix = require('/config/store.js').config().assetsUrlPrefix,
        matcher = new URIMatcher(request.getRequestURI());
    if (matcher.match('/{context}' + prefix + '/{type}/{+any}') || matcher.match('/{context}' + prefix + '/{type}')) {
        return matcher.elements().type;
    }else if(matcher.match('/{context}/t/{domain}' + prefix + '/{type}/') ||
        matcher.match('/{context}/t/{domain}' + prefix + '/{type}/{+any}')){
        return matcher.elements().type;
    }
    prefix = require('/config/store.js').config().extensionsUrlPrefix + prefix;
    if (matcher.match('/{context}' + prefix + '/{type}/{+any}') || matcher.match('/{context}' + prefix + '/{type}')) {
        return matcher.elements().type;
    }else if(matcher.match('/{context}/t/{domain}' + prefix + '/{type}/') ||
        matcher.match('/{context}/t/{domain}' + prefix + '/{type}/{+any}')){
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
    var user, store, configs, tenantId,
        carbon = require('carbon'),
        mod = require('store'),
        server = mod.server,
        cached = server.options().cached;

    tenantId = (o instanceof Request) ? server.tenant(o, session).tenantId : o;
    user = server.current(session);

    //Check for logged-in user. If there is a logged-in user, then check whether the user is requesting to load
    //the anonymous tenant registry of an another tenant
    if (user && !isUserTenantIdDifferFromUrlTenantId(user.tenantId, tenantId)) {
        store = session.get(TENANT_STORE);
        if (cached && store) {
            return store;
        }
        store = new Store(tenantId, session);
        session.put(TENANT_STORE, store);
        return store;
    }
    configs = server.configs(tenantId);
    store = configs[TENANT_STORE];
    if (cached && store) {
        return store;
    }
    store = new Store(tenantId);
    configs[TENANT_STORE] = store;
    return store;
};

var assetManager = function (type, reg) {
    var asset,
        azzet = require('/modules/asset.js'),
        path = ASSETS_EXT_PATH + type + '/asset.js',
        manager = new azzet.Manager(reg, type);
    if (new File(path).isExists() && (asset = require(path)).hasOwnProperty('assetManager')) {
        manager = asset.assetManager(manager);
    }
    return manager;
};

var configs = function (tenantId) {
    var server = require('store').server,
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
        mod = require('store'),
        user = mod.user,
        server = mod.server;
    this.tenantId = tenantId;
    this.servmod = server;
    this.assetManagers = assetManagers;
    if (session) {
        this.user = server.current(session);
        this.registry = user.userRegistry(session);
        this.session = session;
        this.userSpace = user.userSpace(this.user);
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

Store.prototype.getStoreConfig = function () {
    var config  = require('/config/store.js').config();
    return config;
};

Store.prototype.getPageSize = function () {
    return this.getStoreConfig().pagination.PAGE_SIZE;
};

Store.prototype.getTopAssetPageSize = function () {
    return this.getStoreConfig().pagination.TOP_ASSET_PAGE_SIZE;
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
        sort: encodeURIComponent(request.getParameter('sort')) || 'recent'
    };
};

Store.prototype.assetsPagingOverrided = function (request,availablePages) {
    var page = request.getParameter('page'),
    size = this.getPageSize();
    page = page ? page - 1 : 0;
    if(page < 0){
        page = 0
    }else if(page >= availablePages){
        page = availablePages -1;
    }
    return {
        start: page * size,
        count: size,
        sort: encodeURIComponent(request.getParameter('sort')) || 'recent'
    };
};

Store.prototype.buildNextPage = function (request,availablePages) {
    var page = request.getParameter('page'),
            size = this.getPageSize();
    page = page ? page : 1;
    return {
        start: page * size,
        count: size,
        sort: encodeURIComponent(request.getParameter('sort')) || 'recent'
    };
};

Store.prototype.pageIndexPopulator = function(pageCount,currentIndex){
    var indices = [];
    var temp={};
    for(var index=1;index<=pageCount;index++){
        temp={};
        temp.index = ''+index;
        temp.isDisabled = false;

        var PAGE_SIZE = this.getPageSize();

        var pageNumber = Math.floor(currentIndex/PAGE_SIZE);
        var remainder = (currentIndex/PAGE_SIZE) % 1;

        if(remainder || pageNumber===0){
            pageNumber = pageNumber +1;
        }
        //The current page is offset by 1 as the loop starts at 1
        if(index==pageNumber){
            temp.isDisabled=true;
        }
        indices.push(temp);
    }
    return indices;
};

Store.prototype.commentsPaging = function (request) {
    var page = request.getParameter('page'),
        size = this.commentsPageSize();
    page = page ? page - 1 : 0;
    return {
        start: page * size,
        count: size,
        sort: encodeURIComponent(request.getParameter('sort')) || 'recent'
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
        if(!registry){
            return;
        }
        var type,
            items = [],
            obj = registry.content(path);
        if (!obj) {
            return;
        }
        type = path.substr(path.lastIndexOf('/') + 1);
        //obj = obj();

        obj.forEach(function (path) {
            try {
                var iteamOut = that.asset(type, path.substr(path.lastIndexOf('/') + 1))
                if (iteamOut.lifecycleState == 'Published') {
                    iteamOut.isPublished = true;
                } else {
                    iteamOut.isPublished = false;
                }

                items.push(iteamOut);
            } catch (e) {
                log.debug('asset for path="' + path + '" could not be retrieved, try reverting it form registry.');
            }
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
    var tag, tags, assetType, i, length, count, queryParameters,
		registry = this.registry || this.servmod.anonRegistry(this.tenantId),
        tagz = [],
        tz = {},
		mediaType = "%",
		lifeCycleStateKey = "%",
		lifeCycleStateValue = "%";

    // NOTE : Supports only 'webapp' type as of now.
    // If type = undefined retrieve tags without any filtering.

	if(type == RESOURCE_TYPE_WEBAPP){
        var carbonContext = Packages.org.wso2.carbon.context.CarbonContext.getThreadLocalCarbonContext();
        var tenantdomain = carbonContext.getTenantDomain();
        var storeObj = jagg.module("manager").getAPIStoreObj();
        tagz = storeObj.getAllTags(String(tenantdomain));
        return tagz;
    }else if (type == RESOURCE_TYPE_MOBILEAPP){
        return tagz;
    }else if(type){
		log.warn("Retrieving tags : Type " + type +  " is not supported.");
		return tagz;
	}

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
     log.debug('Returning an empty [] for Store.assets.');
     return [];
     }*/

    var options = {};
    options = obtainViewQuery(options);
    options = {"attributes": options};
    var i;
    var newPaging = PaginationFormBuilder(paging);
    //var assetz = this.assetManager(type).list(paging);

    var assetz = this.assetManager(type).search(options, newPaging);


    for (i = 0; i < assetz.length; i++) {
        assetz[i].indashboard = this.isuserasset(assetz[i].id, type);
    }
    return assetz;
};

Store.prototype.assetsLazy = function (type, paging) {

    //var type=(type=='null')?null:type;

    //Check if a type has been provided
    /*if(!type){
     log.debug('Returning an empty [] for Store.assets.');
     return [];
     }*/

    var options = {};
    options = obtainViewQuery(options);
    options = {"attributes": options};
    var i;
    var newPaging = PaginationFormBuilder(paging);

    //var assetz = this.assetManager(type).list(paging);

    var assetz = this.assetManager(type).search(options, newPaging);


    for (i = 0; i < assetz.length; i++) {
        assetz[i].indashboard = this.isuserasset(assetz[i].id, type);
    }
    return assetz;
};

Store.prototype.tagged = function (type, tag, paging) {

    // var type=(type=='null')?null:type;

    //Check if a type has been provided.
    /*if(!type){
     log.debug('Returning an empty [] for Store.tagged.');
     return [];
     } */

    var i;
    var options = {};
    var assets;
    var length;

    //options['tag'] = tag;
    //options = obtainViewQuery(options);
    //TODO move this LCState to config
    options = {"tag": tag, "lifecycleState": ["published"]};

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
     log.debug('Returning an empty [] for store.asset');
     return [];
     }*/

    var asset = this.assetManager(type).get(aid);
    if(asset) {
        asset.rating = this.rating(asset.path);
    }
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

Store.prototype.getAvailablePages = function (type,req,session) {
    var pages;
    var PAGE_SIZE = this.getPageSize();
    //var rxtManager = this.rxtManager(type,session);
    var managers= storeManagers(req,session,this.tenantId);
    var rxtManager = managers.rxtManager;
    var artifactManager = rxtManager.getArtifactManager(type);
    var appCount = artifactManager.count();
    var pageNumber = Math.ceil(appCount/PAGE_SIZE);
    return pageNumber;
};

Store.prototype.getCurrentPage = function(currentIndex){
    var PAGE_SIZE = this.getPageSize();
    var pageNumber = Math.ceil(currentIndex/PAGE_SIZE);
    return pageNumber;
}

/**
 * @param type
 * @param count
 * @return {*}
 */
Store.prototype.popularAssets = function (type, count) {

    //var type=(type=='null')?null:type;

    //Check if a type has been provided.
    /*if(!type){
     log.debug('Returning an empty [] for  store.popularAssets.');
     return [];
     }*/

    var options = {};
    options = obtainViewQuery(options);
    var paging = {
        start: 0,
        count: count || 5,
        sortBy: 'overview_displayName',
        sortOrder: 'ASC'
    };

    var assets = this.assetManager(type).search(options, paging);
    return assets;
};

Store.prototype.recentAssets = function (type, count) {

    //var type=(type=='null')?null:type;

    //If a type is not given
    /*if(!type){
     log.debug('Returning an empty [] for Store.recentAssets.');
     return [];
     }*/

    var i, length;
    var paging = {
        start: 0,
        count: count || 5,
        sortBy: 'overview_createdtime',
        sort: 'older'
    };
    var options = {};
    options = obtainViewQuery(options);
    options = {"attributes": options};

    var recent = this.assetManager(type).search(options, paging);

    //log.debug('re')
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
    var builtPaging = PaginationFormBuilder(paging);
    if (type) {
        var assetz = this.assetManager(type).search(options, builtPaging);
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
        assets[type] = this.assetManager(types[i]).search(options, builtPaging);
    }
    return assets;
};

/**
 * The method is used to return a subset of the assets created by author of the provided asset
 * If no paging value is given the number of assets returned
 * @param asset The asset instance
 * @param type: The type of the asset to search
 * @param paging: The paging parameters of assets returned (OPTIONAL)
 * returns: An array of assets by the given the provider
 */
Store.prototype.assetsFromProvider = function (asset, type, paging) {
    //We have limited the number of assets to 3 since we do not have much screen real estate
    var paging = paging || {start: 0, count: 3, sort: 'recent'};
    var assetsFromProvider = {};
    var provider = asset.attributes[ATTR_PROVIDER];
    var currentAssetName=asset.attributes['overview_displayName'];
    var searchOptions = {};

    searchOptions['attributes'] = {};
    searchOptions = obtainViewQuery(searchOptions);
    searchOptions['attributes'][ATTR_PROVIDER] = provider;


    assetsFromProvider['overview_provider'] = provider;
    assetsFromProvider['type'] = type;

    var arrayOfAssets= this.search(searchOptions, paging)[asset.type] || [];

    //Filter the returned assets so as to remove the current asset
    assetsFromProvider['assets']=arrayOfAssets.filter(function(asset){
        return (asset.attributes['overview_displayName']!=currentAssetName)?true:false;
    });

    return assetsFromProvider;
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

Store.prototype.rxtManager = function (type, session, tenantId) {
    return storeManagers(tenantId, session).rxtManager.findAssetTemplate(function (tmpl) {
        return tmpl.shortName === type;
    });
};

var LIFECYCLE_STATE_PROPERTY = 'lcState';
var DEFAULT_ASSET_VIEW_STATE = 'Published'; //Unless specified otherwise, assets are always visible when Published
var DEFAULT_LC_ATTRIBUTE_NAME = LIFECYCLE_STATE_PROPERTY;
var ATTR_PROVIDER = 'overview_provider';

/*
 The function creates a query object to be used in the Manager.search
 based on the visibleIn property of the store.json.
 @options: The object to be used as the query
 @return: A modified options with the state set to the search criteria
 */
var obtainViewQuery = function (options) {

    // var storeConfig = require('/config/store.json').lifeCycleBehaviour;
    // var visibleStates = storeConfig.visibleIn || DEFAULT_ASSET_VIEW_STATE;

    // options[LIFECYCLE_STATE_PROPERTY] = visibleStates;

    // log.debug('options: ' + stringify(options));

    var storeConfig = require('/config/store.json').lifeCycleBehaviour;
    var visibleStates = storeConfig.visibleIn || DEFAULT_ASSET_VIEW_STATE;
    var attributeName = storeConfig.lcAttributeName || DEFAULT_LC_ATTRIBUTE_NAME;

    //options[LIFECYCLE_STATE_PROPERTY] = visibleStates;
    //Changed the query to check for overview_status as opposed to lifecycle state
    options[attributeName] = visibleStates;

    return options;

}

var TENANT_STORE_MANAGERS = 'store.managers';
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
var storeManagers = function (o, session, tenantId) {
    var storeMasterManager;
    var tenantId;
    var server = require('store').server;

    //We check if there is a valid session
    if (server.current(session) != null) {
        return handleLoggedInUser(o, session, tenantId);
    }
    else {
        //No session,anonymous access
        return handleAnonUser(tenantId);
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
    var server = require('store').server;

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
function handleAnonUser(tenantId) {
    var appManagerName  = APP_MANAGERS + '.' + tenantId;
    var anonMasterManager = application.get(appManagerName);

    //Check if it is cached
    if (anonMasterManager) {
        return anonMasterManager;

    }
    var anonMasterManager = new AnonStoreMasterManager(tenantId);
    application.put(appManagerName, anonMasterManager);

    return anonMasterManager;
}

/*
 The class is used to encapsulate the managers used by the store
 @tenantId: The tenantId of the current tenant
 @session: The session of the currently logged in user
 */
function StoreMasterManager(tenantId, session) {
    var user = require('store').user;
    var registry = user.userRegistry(session);

    var managers = buildManagers(registry, tenantId);

    this.modelManager = managers.modelManager;
    this.rxtManager = managers.rxtManager;
    this.storageSecurityProvider = managers.storageSecurityProvider;
    this.tenantId = tenantId;
}

function PaginationFormBuilder(pagin) {

    var DEFAULT_PAGIN = {"start": 0.0, "count": 5};
    // switch sortOrder from ES to pagination Context

    switch (pagin.sort) {

        case 'recent':
            DEFAULT_PAGIN.sortOrder = 'DES';
            DEFAULT_PAGIN.sortBy = 'overview_createdtime';
            break;
        case 'older':
            DEFAULT_PAGIN.sortOrder = 'ASC'
            DEFAULT_PAGIN.sortBy = 'overview_createdtime';
            break;
        case 'popular':
            // no regsiter pagination support, socail feature need to check
            break;
        case 'unpopular':
            // no regsiter pagination support, socail feature need to check
            break;
        case 'az':
            DEFAULT_PAGIN.sortOrder = 'ASC'
            DEFAULT_PAGIN.sortBy = 'overview_displayName';
            break;
        case 'za':
            DEFAULT_PAGIN.sortOrder = 'DES';
            DEFAULT_PAGIN.sortBy = 'overview_displayName';
            break;
        case 'usage':
            // no regsiter pagination support, socail feature need to check
            break;
        default:
            DEFAULT_PAGIN.sortOrder = 'ASC';
    }

    //sortBy only have overview_name name still for assert type attributes
    if (pagin.count != null) {
        DEFAULT_PAGIN.count = pagin.count;
    }
    if (pagin.start != null) {
        DEFAULT_PAGIN.start = pagin.start;
    }
    if (pagin.paginationLimit != null) {
        DEFAULT_PAGIN.paginationLimit = 120000;
    }
    return DEFAULT_PAGIN;


}

/*
 The class is used to encapsulate managers when a user is not logged in
 All managers user the system registry
 */
function AnonStoreMasterManager(tenantId) {
    var store = require('store');
    var registry = store.server.anonRegistry(tenantId);

    var managers = buildManagers(registry, tenantId);

    this.modelManager = managers.modelManager;
    this.rxtManager = managers.rxtManager;
    this.storageSecurityProvider = managers.storageSecurityProvider;
    this.tenantId = tenantId;
}

/*
 The function is used to create the managers
 @tenantId: The tenantId of the current tenant
 @registry: The registry of the current user who is logged in
 @return: The managers used by the store
 */
var buildManagers = function (registry, tenantId) {

    var rxt_management = require('/modules/rxt/rxt.manager.js').rxt_management();
    var ext_parser = require('/modules/rxt/ext/core/extension.parser.js').extension_parser();
    var ext_core = require('/modules/rxt/ext/core/extension.core.js').extension_core();
    var ext_mng = require('/modules/rxt/ext/core/extension.management.js').extension_management();
    //TODO: sameera need to get store.json from registry
    var config = require('/config/store-tenant.json');
    var server = require('store').server;
    var um = server.userManager(tenantId);
    var rxtManager = new rxt_management.RxtManager(registry);
    var securityProviderModule = require('/modules/security/storage.security.provider.js').securityModule();


    var securityProvider = new securityProviderModule.SecurityProvider();

    //The security provider requires the registry and user manager to work
    securityProvider.provideContext(registry, um);


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
        rxtManager: rxtManager,
        storageSecurityProvider: securityProvider
    }
};

var exec = function (fn, request, response, session) {
    var es = require('store'),
        carbon = require('carbon'),
        tenant = es.server.tenant(request, session),
        user = es.server.current(session);

    var tenantId;
    tenantId = tenant.tenantId;

    //Determine if the tenant domain was not resolved
    if(tenantId===-1){
        response.sendError(404, 'Tenant:' + tenant.domain + ' not registered');
        return;
    }

    es.server.sandbox({
        tenantId: tenantId,
        username: tenant ? tenant.username : carbon.user.anonUser,
        request: request
    }, function () {
        var configs = require('/config/store.js').config();
        return fn.call(null, {
            tenant: tenant,
            server: es.server,
            sso: configs.ssoConfiguration.enabled,
            usr: es.user,
            user: user,
            store: require('/modules/store.js').store(tenantId, session),
            configs: configs,
            request: request,
            response: response,
            session: session,
            application: application,
            event: require('event'),
            params: request.getAllParameters(),
            files: request.getAllFiles(),
            matcher: new URIMatcher(request.getRequestURI()),
            site: require('/modules/site.js'),
            isAnonymousTenantStore : isUserTenantIdDifferFromUrlTenantId(tenant.tenantId, tenantId),
            log: new Log(request.getMappedPath())
        });
    });
};
