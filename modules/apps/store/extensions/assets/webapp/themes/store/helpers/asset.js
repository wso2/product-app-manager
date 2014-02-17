var name;
var log = new Log();
var that = this;
var hps = require('/themes/store/helpers/asset.js');


/*
 In order to inherit all variables in the default helper
 */

for (name in hps) {
    if (hps.hasOwnProperty(name)) {
        that[name] = hps[name];
    }
}

var fn = that.resources;

var resources = function (page, meta) {
    var o = fn(page, meta);

    o.js.push('libs/jquery.slideto.min.js');
    o.js.push('libs/jquery.wiggle.min.js');
    o.js.push('libs/jquery.ba-bbq.min.js');
    o.js.push('libs/underscore-min.js');
    o.js.push('libs/backbone-min.js');
    o.js.push('libs/swagger.js');
    o.js.push('libs/swagger-ui.js');

    o.js.push('logic/asset/tabs/apiconsole.js');
    o.js.push('logic/asset/overview/asset-utilization.js');
    o.code.push('asset/asset-metadata.hbs');
    return o;
};
