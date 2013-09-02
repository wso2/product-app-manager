var caramel = require('caramel');

var carbon = require('carbon');
var conf = carbon.server.loadConfig('carbon.xml');
var offset = conf.*::['Ports'].*::['Offset'].text();
var hostName = conf.*::['HostName'].text().toString();

if (hostName === null || hostName === '') {
    hostName = 'localhost';
}

var httpPort = 9763 + parseInt(offset, 10);
var httpsPort = 9443 + parseInt(offset, 10);

var process = require('process');
process.setProperty('server.host', hostName);
process.setProperty('http.port', httpPort.toString());
process.setProperty('https.port', httpsPort.toString());


/*
  Rxt stuff
 */

var rxt_management=require('/modules/rxt/rxt.manager.js').rxt_management();
var publisherConfig=require('/config/publisher.json');
var pubConfig=require('/config/publisher.js').config();

/*
Finished the parsing stuff
 */
caramel.configs({
    context: '/store',
    cache: true,
    negotiation: true,
    themer: function () {
        /*var meta = caramel.meta();
        if(meta.request.getRequestURI().indexOf('gadget') != -1) {
            return 'modern';
        }*/
        return 'store';
    }/*,
    languagesDir: '/i18n',
    language: function() {
        return 'si';
    }*/
});

var configs = require('/store.js').config();

var server = require('/modules/server.js');
server.init(configs);

var user = require('/modules/user.js');
user.init(configs);

var store = require('/modules/store.js');
store.init(configs);

/*
var url='https://localhost:9443/admin',
    username='admin',
    password='admin';

var server=new carbon.server.Server(url);
var registry=new carbon.registry.Registry(server,{
    systen:true,
    username:username,
    tenantId:carbon.server.superTenant.tenantId
});
 */

//TODO : fix this
var tenantId = -1234;
var registry = server.systemRegistry(tenantId);

var rxtManager=new rxt_management.RxtManager(registry);

//All of the rxt xml files are read and converted to a JSON object called
//a RxtTemplate(Refer rxt.domain.js)
rxtManager.loadAssets();

var ext_parser=require('/modules/rxt/ext/core/extension.parser.js').extension_parser();
var ext_core=require('/modules/rxt/ext/core/extension.core.js').extension_core();
var ext_mng=require('/modules/rxt/ext/core/extension.management.js').extension_management();

var  parser=new ext_parser.Parser();

var log=new Log();

//Go through each rxt template
for each(var rxtTemplate in rxtManager.rxtTemplates){
    parser.registerRxt(rxtTemplate);
}

parser.load(publisherConfig.paths.RXT_EXTENSION_PATH);

var adapterManager=new ext_core.AdapterManager({parser:parser});
adapterManager.init();

var fpManager=new ext_core.FieldManager({parser:parser});
fpManager.init();

var ruleParser=new ext_parser.RuleParser({parser:parser});
ruleParser.init();

var modelManager=new ext_mng.ModelManager({parser:parser,adapterManager:adapterManager});

application.put(publisherConfig.app.MODEL_MANAGER,modelManager);


