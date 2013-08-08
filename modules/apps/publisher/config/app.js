/*
 Description: Initialization script
 Filename:app.js
 Created Date: 29/7/2013
 */

var caramel = require('caramel'),
    rxt_management = require('/modules/rxt.manager.js').rxt_management(),
    route_management = require('/modules/router-g.js').router(),
    carbon = require('carbon'),
    mediaType = 'application/vnd.wso2.registry-ext-type+xml', //TODO: Change the url
    conf = carbon.server.loadConfig('carbon.xml'),
    offset = conf.*::['Ports'].*::['Offset'].text(),
    hostName = conf.*::['HostName'].text().toString();

if (hostName === null || hostName === '') {
    hostName = 'localhost';
}

var httpPort = 9763 + parseInt(offset, 10);
var httpsPort = 9443 + parseInt(offset, 10);

var process = require('process');
process.setProperty('server.host', hostName);
process.setProperty('http.port', httpPort.toString());
process.setProperty('https.port', httpsPort.toString());

var config = require('/config/publisher.js').config();

var server = require('/modules/server.js');

server.init(config);

var user = require('/modules/user.js');
user.init(config);

//var server = new carbon.server.Server(url);
var registry = server.systemRegistry();
var rxtManager = new rxt_management.RxtManager(registry);
var routeManager = new route_management.Router();


routeManager.setRenderer(config.router.RENDERER);

//All of the rxt xml files are read and converted to a JSON object called
//a RxtTemplate(Refer rxt.domain.js)
rxtManager.loadAssets();

application.put(config.app.RXT_MANAGER, rxtManager);
application.put(config.app.ROUTE_MANAGER, routeManager);

//Configure Caramel
caramel.configs({
    context: '/publisher',
    cache: true,
    negotiation: true,
    themer: function () {
        //TODO: Hardcoded theme
        return 'publisher';
    }

});




