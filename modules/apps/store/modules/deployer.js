var site = function (options) {
    var tag, tags,
        carbon = require('carbon'),
        path = '/_system/governance/sites/' + options.provider + '/' + options.name + '/' + options.version,
        server = require('/modules/server.js'),
        //site = require('/modules/site-browser.js'),
        registry = server.systemRegistry(),
        um = server.userManager();
        //metapath = site.SITE_METADATA + options.site;

    registry.put(path, {
        mediaType: 'application/vnd.wso2-site+xml',
        content: buildSiteRXT(options).toXMLString()
    });
    um.authorizeRole(carbon.user.anonRole, path, carbon.registry.actions.GET);

	/*if(options.roles != undefined){
		site.authorizeRoles(options.site, options.roles, options.action);
	}
	if (options.users != undefined){
		site.authorizeUsers(options.site, options.users, options.action);
	}*/
    //meta = registry.exists(metapath) ? parse(registry.content(metapath).toString()) : {};
    //meta.aid = path;
    /*registry.put(metapath, {
        content: stringify(meta)
    }); */
    tags = options.tags;
    for (tag in tags) {
        if (tags.hasOwnProperty(tag)) {
            registry.tag(path, options.tags[tag]);
        }
    }
    rate = options.rate;
    if(options.rate != undefined){
        registry.rate(path, rate);
    }

};

var gadget = function (options) {
    var tag, tags, rate, asset, assets,
        carbon = require('carbon'),
        store = require('/modules/store.js'),
        path = '/_system/governance/gadgets/' + options.provider + '/' + options.name + '/' + options.version,
        server = require('/modules/server.js'),
        um = server.userManager(),
        registry = server.systemRegistry(),
        am = store.assetManager('gadget', registry);

    asset = {
        "name": options.name,
        "lifecycle": null,
        "lifecycleState": null,
        "attributes": {
            "overview_status": options.status,
            "overview_name": options.name,
            "overview_version": options.version,
            "overview_description": options.description,
            "overview_url": options.url,
            "overview_provider": options.provider,
            "images_banner": options.banner,
            "images_thumbnail": options.thumbnail
        }
    };

    assets = am.search({
        attributes: {
            overview_name: options.name,
            overview_provider: options.provider,
            overview_version: options.version
        }
    }, { start: 0, count: 10 });

    if (assets.length > 0) {
        asset.id = assets[0].id;
        am.update(asset);
    } else {
        am.add(asset);
    }

    um.authorizeRole(carbon.user.anonRole, path, carbon.registry.actions.GET);
    tags = options.tags;
    for (tag in tags) {
        if (tags.hasOwnProperty(tag)) {
            registry.tag(path, options.tags[tag]);
        }
    }
    rate = options.rate;
    if(options.rate != undefined){
        registry.rate(path, rate);
    }
};

var buildSiteRXT = function (options) {
    var rxt = <metadata xmlns="http://www.wso2.org/governance/metadata">
        <overview>
            <provider>{options.provider}</provider>
            <name>{options.name}</name>
            <version>{options.version}</version>
            <url>{options.url}</url>
            <status>{options.status}</status>
            <description>{options.description}</description>
        </overview>
        <images>
            <thumbnail>{options.thumbnail}</thumbnail>
            <banner>{options.banner}</banner>
        </images>
    </metadata>;
    return rxt;
};

var sso = function (options) {
    var path = '/_system/config/repository/identity/SAMLSSO/' + options.issuer64,
        server = require('/modules/server.js'),
        registry = server.systemRegistry();
    registry.put(path, {
        properties: {
            'Issuer': options.issuer,
            'SAMLSSOAssertionConsumerURL': options.consumerUrl,
            'doSignAssertions': options.doSign,
            'doSingleLogout': options.singleLogout,
            'useFullyQualifiedUsername': options.useFQUsername
        }
    });
};

