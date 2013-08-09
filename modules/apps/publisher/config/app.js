/*
 Description: Initialization script
 Filename:app.js
 Created Date: 29/7/2013
 */

var caramel = require('caramel'),
    rxt_management = require('/modules/rxt.manager.js').rxt_management(),
    route_management = require('/modules/router-g.js').router(),
    config = require('/config/publisher.json'),
    carbon = require('carbon'),
    mediaType = 'application/vnd.wso2.registry-ext-type+xml',
    conf = carbon.server.loadConfig('carbon.xml'),
    offset = conf.*::['Ports'].*::['Offset'].text(),
    hostName = conf.*::['HostName'].text().toString();

var ext_parser = require('/modules/ext/core/extension.parser.js').extension_parser();
var ext_domain = require('/modules/ext/core/extension.domain.js').extension_domain();
var ext_core = require('/modules/ext/core/extension.core.js').extension_core();
var ext_mng = require('/modules/ext/core/extension.management.js').extension_management();


if (hostName === null || hostName === '') {
    hostName = 'localhost';
}

var httpPort = 9763 + parseInt(offset, 10);
var httpsPort = 9443 + parseInt(offset, 10);


var process = require('process');
process.setProperty('server.host', hostName);
process.setProperty('http.port', httpPort.toString());
process.setProperty('https.port', httpsPort.toString());

var pubConfig = require('/config/publisher.js').config();
var server = require('/modules/server.js');
server.init(pubConfig);

var user = require('/modules/user.js');
user.init(pubConfig);

//var server=new carbon.server.Server(url);
var registry = server.systemRegistry();
/*var registry=new carbon.registry.Registry(server,{
 systen:true,
 username:username,
 tenantId:carbon.server.superTenant.tenantId
 });*/
var rxtManager = new rxt_management.RxtManager(registry);
var routeManager = new route_management.Router();


routeManager.setRenderer(config.router.RENDERER);

//All of the rxt xml files are read and converted to a JSON object called
//a RxtTemplate(Refer rxt.domain.js)
rxtManager.loadAssets();

var parser = new ext_parser.Parser();

//Go through each rxt template
for each(var rxtTemplate in rxtManager.rxtTemplates)
{

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

application.put(config.app.MODEL_MANAGER, modelManager);
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




