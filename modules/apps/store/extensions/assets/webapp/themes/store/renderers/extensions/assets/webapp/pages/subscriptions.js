
var render = function (theme, data, meta, require) {

    var subscriptions = [];
    if(data.appsWithSubs != null && data.appsWithSubs.length > 0){
        var subscriptions = data.appsWithSubs[0].subscriptions;
    }


    theme('2-column-right', {
        title: 'Subscriptions',
        metadata:data.metadata,
        header: [
            {
              partial:'header',
              context:data.header
            },
            {
                partial: 'navigation',
				context: require('/helpers/navigation.js').currentPage(data.navigation, data.type, data.search)
            }
        ] ,
        body:[
            {
                partial:'subscriptions',
                context:{appsWithSubs:data.appsWithSubs, subscriptions: subscriptions, pages: data.pages, page: data.page}
            }
        ],
        right:[
            {
                partial:'recent-assets',
                context:require('helpers/asset.js').formatRatings(data.recentAssets)
            }
        ]
    });

};