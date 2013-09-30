var configs = require('/sso.js').config();

var server = require('/modules/server.js');
server.init(configs);

var user = require('/modules/user.js');
user.init(configs);

var event = require('/modules/event.js');

event.on('tenantLoad', function (tenantId) {

    var log = new Log();
    log.info('======================================================================================');
    event.emit('tenantCreate', tenantId)
    var server = require('/modules/server.js');
    var user = require('/modules/user.js');
    var config = server.configs(tenantId);
    config[user.USER_OPTIONS] = {
        "permissions": {
            "login": {
                "/permission/admin/login": ["ui.execute"]
            }
        }
    };
});