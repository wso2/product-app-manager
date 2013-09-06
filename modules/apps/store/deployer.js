var gadgetRxtPath = '/gadgets/';
var siteRxtPath = '/sites/';
var ebookRxtPath = '/ebooks/';

var repoPath = '/gadgets';
var repoSitePath = '/sites';
var repoEBookPath = '/ebooks';

var lastUpdated = 0;

var DEPLOYING_INTERVAL = 10000;

var caramel = require('caramel');
require('/app.js');

var store = require('/store.js').config();


var deployer = require('/modules/deployer.js'),
    context = caramel.configs().context,
    log = new Log('store.deployer');

//var log = new Log();

var populate = function (tenantId) {
    var i, name, length, gadgets, file, path, xml,
        repo = new File(repoPath),
        base = store.server.http + context + gadgetRxtPath;

    if (repo.isDirectory()) {
        gadgets = repo.listFiles();
        length = gadgets.length;
        for (i = 0; i < length; i++) {
            name = gadgets[i].getName();
            if (skipGadgets(name))
                continue;
            file = new File(repoPath + '/' + name + '/' + name + '.xml');
            if (file.getLastModified() > lastUpdated) {
                var existingSession = Session["started"];
                if (existingSession) {
                    log.info('Deploying Gadget : ' + name);
                }
                path = base + name + '/';

                file.open('r');
                var fileContent = file.readAll();
                fileContent = fileContent.replace(/^\s*<\?.*\?>\s*/, "");
                xml = new XML(fileContent);
                file.close();
                deployer.gadget(tenantId, {
                    name: xml.*::ModulePrefs.@title,
                    tags: (String(xml.*::ModulePrefs.@tags)).split(','),
                    rate: Math.floor(Math.random() * 5) + 1,
                    provider: store.user.username,
                    version: '1.0.0',
                    description: xml.*::ModulePrefs.@description,
                    url: path + name + '.xml',
                    thumbnail: path + 'thumbnail.jpg',
                    banner: path + 'banner.jpg',
                    status: 'PUBLISHED'
                });
            }
        }
        if (typeof(Session["started"]) == "undefined") {
            log.info('Default gadgets deployed');
        }
    }
};

var skipGadgets = function (name) {
    if (name === 'agricultural-land' ||
        name === 'wso2-carbon-dev' ||
        name === 'intro-gadget-1' ||
        name === 'intro-gadget-2' ||
        name === 'show-assets' ||
        name === 'co2-emission' ||
        name === 'electric-power' ||
        name === 'energy-use' ||
        name === 'greenhouse-gas') return true;
};

var addSSOConfig = function (tenantId) {
    var deployer = require('/modules/deployer.js');
    //Adding SSO Configs
    deployer.sso(tenantId, {'issuer': 'store',
        'consumerUrl': store.ssoConfiguration.storeAcs,
        'doSign': 'true',
        'singleLogout': 'true',
        'useFQUsername': 'true',
        'issuer64': 'c3RvcmU'});
};

var logstoreUrl = function () {
    log.info("UES store URL : " + store.server.http + caramel.configs().context);
};

var populateEBooks = function (tenantId) {
    var i, name, length, ebooks, ebookJson, file, path, xml,
        repo = new File(repoEBookPath),
        base = store.server.http + context + ebookRxtPath;

    if (repo.isDirectory()) {
        ebooks = repo.listFiles();
        length = ebooks.length;
        for (i = 0; i < length; i++) {
            name = ebooks[i].getName();
            var ebookJson = require('/ebooks/' + name + '/ebook.json');
            var path = base + name + '/';
            deployer.ebook(tenantId, {
                name: ebookJson.name,
                tags: ebookJson.tags.split(','),
                rate: ebookJson.rate,
                provider: ebookJson.attributes.overview_provider,
                version: ebookJson.attributes.overview_version,
                description: ebookJson.attributes.overview_description,
                url: path + ebookJson.attributes.overview_url,
                isbn: ebookJson.attributes.overview_isbn,
                author: ebookJson.attributes.overview_author,
                thumbnail: base + ebookJson.attributes.images_thumbnail,
                banner: base + ebookJson.attributes.images_banner,
                status: ebookJson.attributes.overview_status,
                category: ebookJson.attributes.overview_category
            });
        }

        if (typeof(Session["started"]) == "undefined") {
            log.info('Default e-books deployed');
        }
    }
};

var populateSites = function (tenantId) {
    var i, name, length, sites, siteJson, file, path, xml,
        repo = new File(repoSitePath),
        base = store.server.http + context + siteRxtPath;

    if (repo.isDirectory()) {
        sites = repo.listFiles();
        length = sites.length;
        for (i = 0; i < length; i++) {
            name = sites[i].getName();
            var siteJson = require('/sites/' + name + '/site.json');

            var path = base + name + '/';
            deployer.site(tenantId, {
                name: siteJson.name,
                tags: siteJson.tags.split(','),
                rate: siteJson.rate,
                provider: siteJson.attributes.overview_provider,
                version: siteJson.attributes.overview_version,
                description: siteJson.attributes.overview_description,
                url: siteJson.attributes.overview_url,
                thumbnail: base + siteJson.attributes.images_thumbnail,
                banner: base + siteJson.attributes.images_banner,
                status: siteJson.attributes.overview_status
            });
        }

        if (typeof(Session["started"]) == "undefined") {
            log.info('Default sites deployed');
        }
        Session["started"] = true;
    }
    lastUpdated = new Date().getTime();
};

var tenantId = -1234;
var server = require('/modules/server.js');
server.loadTenant(tenantId);

populate(tenantId);
populateEBooks(tenantId);
populateSites(tenantId);
//addSSOConfig(tenantId);
/*
 setInterval(function () {
 //TEMP fix for task not clearing properly during server shutdown
 try {
 populate();
 } catch (e) {
 }
 }, DEPLOYING_INTERVAL);
 setTimeout(logstoreUrl, 5000);
 */