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

    o.js.push('logic/asset/tabs/apiconsole.js');
    o.js.push('logic/asset/overview/asset-utilization.js');
    o.js.push('logic/asset/overview/overview.js');
    o.code.push('asset/asset-metadata.hbs');
    
    o.css.push('cstyles.css');
    return o;
};
