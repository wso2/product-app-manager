var render = function (theme, data, meta, require) {
   
    theme('2-column-right', {
        title: data.title,
        navigation: [
            {
                partial: 'navigation',
                context: data.navigation
            },
            {
                partial: 'search'
            }
        ],
       
        body: [
            {
                partial: 'top-assets',
                context: require('/helpers/top-assets.js').currentPage(data.topAssets,data.sso,data.user)
            }
        ],
        right: [
            {
                partial: 'recent-assets',
                context: data.recentAssets
            }
        ]
    });
};