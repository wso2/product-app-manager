var gadgetRxtPath = '/gadgets/';

var repoPath = '/gadgets';

var lastUpdated = 0;

var DEPLOYING_INTERVAL = 10000;

var caramel = require('caramel');
require('/app.js');

var store = require('/store.js').config();

var populate = function () {
    var i, name, length, gadgets, file, path, xml,
        log = new Log('store.deployer'),
        repo = new File(repoPath),
        deployer = require('/modules/deployer.js'),
        context = caramel.configs().context,
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
                deployer.gadget({
                    name: xml.*::ModulePrefs.@title,
                    tags: (String(xml.*::ModulePrefs.@tags)).split(','),
                    rate: Math.floor(Math.random() * 5) + 1,
                    provider: store.user.username,
                    version: '1.0.0',
                    description: xml.*::ModulePrefs.@description,
                    url: path + name + '.xml',
                    thumbnail: path + 'thumbnail.jpg',
                    banner: path + 'banner.jpg',
                    status: 'CREATED'
                });
            }
        }
        if (typeof(Session["started"]) == "undefined") {
            log.info('Default gadgets deployed');
        }
        Session["started"] = true;

    }
    lastUpdated = new Date().getTime();
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

var addSSOConfig = function () {
    var deployer = require('/modules/deployer.js');
    //Adding SSO Configs
    deployer.sso({'issuer': 'store',
        'consumerUrl': store.ssoConfiguration.storeAcs,
        'doSign': 'true',
        'singleLogout': 'true',
        'useFQUsername': 'true',
        'issuer64': 'c3RvcmU'});
};

var logstoreUrl = function () {
    log.info("UES store URL : " + store.server.http + caramel.configs().context);
};

populate();
addSSOConfig();
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
