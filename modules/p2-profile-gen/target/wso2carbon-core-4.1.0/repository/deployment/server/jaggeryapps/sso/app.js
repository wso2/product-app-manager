var configs = require('/sso.js').config();

var server = require('/modules/server.js');
server.init(configs);

var user = require('/modules/user.js');
user.init(configs);