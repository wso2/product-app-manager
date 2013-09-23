var format = function (context, paging, navigation, type) {
    var start = paging.start + 1,
        end = paging.start + paging.count;
    end = end > paging.total ? paging.total : end;
    return {
        url: context.url,
        total: paging.total,
        start: start,
        end: end,
        categories : navigation.assets[type].categories
    };
};

var resources = function (page, meta) {
    return {
        js: ['sort-assets.js'],
        css: ['sort-assets.css']
    };
};