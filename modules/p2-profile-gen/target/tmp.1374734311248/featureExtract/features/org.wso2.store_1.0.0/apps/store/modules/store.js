var ASSETS_EXT_PATH = '/assets/';

var ASSET_MANAGERS = 'asset.managers';

var ASSETS_PAGE_SIZE = ( function() {
	return require('/store.js').config().assetsPageSize;
}());

var COMMENTS_PAGE_SIZE = ( function() {
	return require('/store.js').config().commentsPageSize;
}());

var log = new Log();

var tags, init, assets, asset, assetLinks, tagged, popularAssets, recentAssets, assetTypes, currentAsset, cache, cached, invalidate, comments, comment, assetsPaging, commentsPaging, rate, rating, assetCount, userAssets, registry, commentCount, search, addAsset, updateAsset, removeAsset, assetManager; ( function() {
	assetManager = function(type, reg) {
		var manager, assetManagers, context, user, path = ASSETS_EXT_PATH + type + '/asset.js', azzet = new File(path).isExists() ? require(path) : require('/modules/asset.js');
		if(reg) {
			return new azzet.Manager(reg, type);
		}
		user = require('/modules/user.js').current();
		context = user ? session : application;
		assetManagers = context.get(ASSET_MANAGERS);
		if(!assetManagers) {
			assetManagers = {};
			context.put(ASSET_MANAGERS, assetManagers);
		}
		manager = assetManagers[type];
		if(manager) {
			return manager;
		}
		reg = registry();
		return (assetManagers[type] = new azzet.Manager(reg, type));
	};
	var merge = function(def, options) {
		if(options) {
			for(var op in def) {
				if(def.hasOwnProperty(op)) {
					def[op] = options[op] || def[op];
				}
			}
		}
		return def;
	};
	init = function(options) {
		var CommonUtil = Packages.org.wso2.carbon.governance.registry.extensions.utils.CommonUtil, GovernanceConstants = org.wso2.carbon.governance.api.util.GovernanceConstants, carbon = require('carbon'), server = require('/modules/server.js'), reg = server.systemRegistry(), um = server.userManager();
		CommonUtil.addRxtConfigs(reg.registry, reg.tenantId);
		um.authorizeRole(carbon.user.anonRole, GovernanceConstants.RXT_CONFIGS_PATH, carbon.registry.actions.GET);
	};
	userAssets = function() {
		var type, items, obj, path, user = require('/modules/user.js'), space = user.userSpace(), assets = space.get('userAssets'), assetz = {};
		if(!assets) {
			return assetz;
		}
		assets = parse(assets);
		for(type in assets) {
			if(assets.hasOwnProperty(type)) {
				items = [];
				obj = assets[type];
				for(path in obj) {
					if(obj.hasOwnProperty(path)) {
						items.push(asset(type, path))
					}
				}
				assetz[type] = items;
			}
		}
		return assetz;
	};
	/**
	 * Retrieves either current user's registry instance or default instance.
	 * @return {String}
	 */
	registry = function() {
		return require('/modules/user.js').userRegistry() || require('/modules/server.js').anonRegistry();
	};
	/**
	 * Returns all tags
	 * @param type Asset type
	 */
	tags = function(type) {
		var tag, tags, assetType, i, length, count, query, path, tagz = [], server = require('/modules/server.js'), system = server.systemRegistry(), tz = {};
		path = '/_system/config/repository/components/org.wso2.carbon.registry/queries/allTags';
		if(!system.exists(path)) {
			system.put(path, {
				content : 'SELECT RT.REG_TAG_ID FROM REG_RESOURCE_TAG RT ORDER BY RT.REG_TAG_ID',
				mediaType : 'application/vnd.sql.query',
				properties : {
					resultType : 'Tags'
				}
			});
		}
		tags = registry().query(path);
		length = tags.length;
		if(type == undefined) {
			for( i = 0; i < length; i++) {
				tag = tags[i].split(';')[1].split(':')[1];
				count = tz[tag];
				count = count ? count + 1 : 1;
				tz[tag] = count;
			}
		} else {
			for( i = 0; i < length; i++) {
				assetType = tags[i].split(';')[0].split('/')[3];
				if(assetType != undefined) {
					if(assetType.contains(type)) {
						tag = tags[i].split(';')[1].split(':')[1];
						count = tz[tag];
						count = count ? count + 1 : 1;
						tz[tag] = count;
					}
				}
			}
		}
		for(tag in tz) {
			if(tz.hasOwnProperty(tag)) {
				tagz.push({
					name : String(tag),
					count : tz[tag]
				});
			}
		}

		return tagz;
	};
	comments = function(aid, paging) {
		return registry().comments(aid, paging);
	};
	commentCount = function(aid) {
		return registry().commentCount(aid);
	};
	comment = function(aid, comment) {
		return registry().comment(aid, comment);
	};
	rating = function(aid) {
		var store = require('/store.js').config(), user = require('/modules/user.js').current();
		return registry().rating(aid, user ? user.username : store.user.username);
	};
	rate = function(aid, rating) {
		return registry().rate(aid, rating);
	};
	/**
	 * Returns all assets for the current user
	 * @param type Asset type
	 * @param paging
	 */
	assets = function(type, paging) {
			var assetz = assetManager(type).list(paging);
			for(i =0; i < assetz.length;i++){
				if(isuserasset(assetz[i].path,type)){
					assetz[i].indashboard = true;
						}else{
						assetz[i].indashboard = false;
					}
				}
			
			return assetz;
	};
	tagged = function(type, tag, paging) {
		var i, assets = assetManager(type).search({
			tag : tag
		}, paging), length = assets.length;
		for( i = 0; i < length; i++) {
			
			assets[i].rating = rating(assets[i].path);
			if(isuserasset(assets[i].path,type)){
			assets[i].indashboard = true;
				}else{
				assets[i].indashboard = false;
					}
		}
		return assets;
	};
	/**
	 * Returns asset data for the current user
	 * @param type Asset type
	 * @param aid Asset identifier
	 */
	asset = function(type, aid) {
		var asset = assetManager(type).get(aid);
		asset.rating = rating(aid);
		return asset;
	};
	/**
	 * Returns links of a asset for the current user
	 * @param type Asset type
	 */
	assetLinks = function(type) {
		var mod = require(ASSETS_EXT_PATH + type + '/asset.js'), user = require('/modules/user.js');
		return mod.assetLinks(user);
	};
	/**
	 *
	 * @param type
	 * @param count
	 * @return {*}
	 */
	popularAssets = function(type, count) {
		return assetManager(type).list({
			start : 0,
			count : count || 5,
			sort : 'popular'
		});
	};
	recentAssets = function(type, count) {
		var i, length;

		var recent = assetManager(type).list({
			start : 0,
			count : count || 5,
			sort : 'recent'
		});
		length = recent.length;
		for( i = 0; i < length; i++) {
			recent[i].rating = rating(recent[i].path).average;
			if(isuserasset(recent[i].path,type)){
				recent[i].indashboard = true;
					}else{
						recent[i].indashboard = false;
					}		
		}
		return recent;
	};
	assetCount = function(type, options) {
		return assetManager(type).count(options);
	};
	/**
	 * Returns all enabled asset types for the current user
	 */
	assetTypes = function() {
		return require('/store.js').config().assets;
	};
	currentAsset = function() {
		var prefix = require('/store.js').config().assetsUrlPrefix, matcher = new URIMatcher(request.getRequestURI());
		if(matcher.match('/{context}' + prefix + '/{type}/{+any}') || matcher.match('/{context}' + prefix + '/{type}')) {
			return matcher.elements().type;
		}
		prefix = require('/store.js').config().assetUrlPrefix;
		if(matcher.match('/{context}' + prefix + '/{type}/{+any}') || matcher.match('/{context}' + prefix + '/{type}')) {
			return matcher.elements().type;
		}
		return null;
	};
	assetsPaging = function(request) {
		var page = request.getParameter('page');
		page = page ? page - 1 : 0;
		return {
			start : page * ASSETS_PAGE_SIZE,
			count : ASSETS_PAGE_SIZE,
			sort : request.getParameter('sort') || 'recent'
		};
	};
	commentsPaging = function(request) {
		var page = request.getParameter('page');
		page = page ? page - 1 : 0;
		return {
			start : page * COMMENTS_PAGE_SIZE,
			count : COMMENTS_PAGE_SIZE,
			sort : request.getParameter('sort') || 'recent'
		};
	};
	cache = function(type, key, value) {
		var cache = require('/modules/cache.js'), data = cache.cached(type) || (cache.cache(type, {}));
		return (data[key] = value);
	};
	cached = function(type, key) {
		var cache = require('/modules/cache.js'), data = cache.cached(type);
		return data ? data[key] : null;
	};
	invalidate = function(type, key) {
		var cache = require('/modules/cache.js'), data = cache.cached(type);
		delete data[key];
	};
	search = function(options, paging) {
		var i, length, type, types, assets;
		type = options.type;
		if(type) {
			var assetz = assetManager(type).search(options, paging);
			for(i =0; i < assetz.length;i++){
				if(isuserasset(assetz[i].path,type)){
					assetz[i].indashboard = true;
						}else{
						assetz[i].indashboard = false;
					}
				}
			
			return assetz;


		}
		types = assetTypes();
		assets = {};
		length = types.length;
		for( i = 0; i < length; i++) {
			type = types[i];
			assets[type] = assetManager(types[i]).search(options, paging);
		}
		return assets;
	};
	isuserasset = function(aid,type){
        var store = require('/modules/store.js');
        var user = require('/modules/user.js').current();
        userown = {};
        if(user){
        var userAssets = store.userAssets();
        if(userAssets[type] != undefined){
        for(j = 0; j < userAssets[type].length; j++){
            if(userAssets[type][j]['path']==aid){
                userown = userAssets[type][j]['path'];
            }
        }
            if(userown.length>0){
                return true;
                }else{
                    return false;                    
                    }
        }
        }else{
            return false;
        }
	};
	addAsset = function(type, options) {
		assetManager(type).add(options);
	};
	updateAsset = function(type, options) {
		assetManager(type).update(options);
	};
	removeAsset = function(type, options) {
		assetManager(type).remove(options);
	}
}());
