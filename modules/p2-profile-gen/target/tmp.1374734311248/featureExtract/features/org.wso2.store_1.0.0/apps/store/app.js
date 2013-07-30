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
store.init({

});