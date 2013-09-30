var caramel = require('caramel');
var event = require('/modules/event.js');

caramel.configs({
    context: '/social',
    cache: true,
    negotiation: true,
    themer: function () {
        return 'default';
    }
});

var carbon = require('carbon');
var social = carbon.server.osgiService('org.wso2.carbon.social.service.SocialActivityService');
setTimeout(function () {
    social.publish({"verb": "post",
        "object": {"type": "comment", "content": "test"},
        "target": {"id": "test_"}
    });
}, 10000)

var configs = require('social.js').config();
var STORE_CONFIG_PATH = '/_system/config/social/configs/social.json';

var server = require('/modules/server.js');
server.init(configs);

var user = require('/modules/user.js');
user.init(configs);

var log = new Log();

event.on('tenantCreate', function (tenantId) {
    var carbon = require('carbon'),
        system = server.systemRegistry(tenantId);
    system.put(STORE_CONFIG_PATH, {
        content: JSON.stringify({
            "permissions": {
                "login": {
                    "/permission/admin/login": ["ui.execute"]
                }
            }
        }),
        mediaType: 'application/json'
    });
});

event.on('tenantLoad', function (tenantId) {
    var carbon = require('carbon'),
        config = server.configs(tenantId),
        reg = server.systemRegistry(tenantId),
        um = server.userManager(tenantId);

    //check whether tenantCreate has been called
    if (!reg.exists(STORE_CONFIG_PATH)) {
    }
    event.emit('tenantCreate', tenantId);

    config[user.USER_OPTIONS] = {
        "permissions": {
            "login": {
                "/permission/admin/login": ["ui.execute"]
            }
        }
    };
});

log.info('======================================================================================');
