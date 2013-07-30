var resources = function (page, meta) {
    return {
        template: 'top-assets.hbs',
        js: ['asset-core.js', 'top-assets.js', 'jquery.event.mousestop.js', 'jquery.carouFredSel-6.2.1-packed.js' ],
        css: ['assets.css', 'top-assets.css']
    };
};

var currentPage = function (assets,sso,user) {
    var out  = {
        'recentAssets': assets.recentAssets,
        'popularAssets': assets.popularAssets,
        'recent': assets.recent,
        'sso': sso,
        'user': user
    };
    return out;
};
