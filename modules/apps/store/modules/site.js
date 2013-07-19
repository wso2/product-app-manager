var logo = function () {

};

var footer = function () {

};

var navigation = function (options) {
	

    var i, j, type, link, links, length1,
        assetLinks = {},
        sso = options.sso,
        config = require('/store.js').config(),
        prefix = config.assetsUrlPrefix,
        user = require('/modules/user.js'),
        store = require('/modules/store.js'),
        types = store.assetTypes(),
        length2 = types.length;
    for (i = 0; i < length2; i++) {
        type = types[i];

        links = store.assetLinks(type);
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
        login: loginLinks(),
        sso: sso,
        user: options.user
    };
};

var loginLinks = function () {
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