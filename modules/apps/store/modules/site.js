var logo = function (tenantId) {

};

var footer = function (tenantId) {

};

var navigation = function (tenantId, options) {


    var i, j, type, link, links, length1,
        assetLinks = {},
        sso = options.sso,
        user = require('/modules/user.js'),
        store = require('/modules/store.js').store(tenantId, session),
        utility = require('/modules/util.js'),
        config = store.configs(),
        prefix = config.assetsUrlPrefix,
        types = store.assetTypes(),
        length2 = types.length;
    for (i = 0; i < length2; i++) {
        type = types[i];
        links = store.assetLinks(type);
        if (links.isCategorySupport) {
            links.categories = utility.getCategories(tenantId, type);
        }

        /*
         length1 = links.length;

         for (j = 0; j < length1; j++) {
         link = links[j];
         link.url = prefix + '/' + type + '/' + link.url;
         }*/
        assetLinks[type] = links;
    }

    return {
        assets: assetLinks,
        login: loginLinks(tenantId),
        sso: sso,
        user: options.user
    };
};

var loginLinks = function (tenantId) {
    return {
        login: {
            title: 'Login',
            url: 'login.jag'
        },
        register: {
            title: 'Register',
            url: 'register.jag'
        }
    };
};