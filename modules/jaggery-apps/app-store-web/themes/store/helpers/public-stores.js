var resources = function (page, meta) {
    new Log("[SAJITH]").info("Hit - helpers/public-stores.js");
    return {
        template: 'public-stores.hbs',
        js: [],
        css: ['assets.css', 'top-assets.css', 'mobileapp-custom.css']
    };
};
