var publisher = require('/config/publisher.js').config();

var addSSOConfig = (function () {
    var deployer = require('/modules/deployer.js');
    //Adding SSO Configs
    deployer.sso({'issuer': 'publisher',
        'consumerUrl': publisher.ssoConfiguration.publisherAcs,
        'doSign': 'true',
        'singleLogout': 'true',
        'useFQUsername': 'true',
        'issuer64': 'cHVibGlzaGVy'});
})();