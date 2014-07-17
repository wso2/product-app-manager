var logo = function (tenantId) {

};

var footer = function (tenantId) {

};
var navigation = function (tenantId) {


    var i, type, links,
        assetLinks = {},

        storemod = require('/modules/store.js'),
        store = storemod.store(tenantId, session),
        utility = require('/modules/util.js'),
        types = store.assetTypes(),
        length = types.length;
        
       
        
    for (i = 0; i < length; i++) {
        type = types[i];
        links = store.assetLinks(type);
        if (links.isCategorySupport) {
            links.categories = utility.getCategories(tenantId, type);	
        }
        var iconPath = storemod.ASSETS_EXT_PATH + type + '/resources/' + 'icon-sprite.png';
		
		if (new File(iconPath).isExists()) {
	    	links.icon = iconPath;
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
        assets: assetLinks
    };
};

var header = function (tenantId, options) {

    var resultClass = Packages.org.wso2.carbon.appmgt.impl.dao.ApiMgtDAO;
    var result = resultClass();
    var isSelfSignUpEnabled = result.isSelfSignUpEnabled();

    var user = require('store').user;

    return {
        login: loginLinks(tenantId),
        sso: options.sso,
        user: options.user,
	selfSignUpEnabled: isSelfSignUpEnabled
    };
};

var loginLinks = function (tenantId) {
    return {
        login: {
            title: 'Login',
            url: 'login'
        },
        register: {
            title: 'Register',
            url: '/controllers/register.jag'
        }
    };
};
