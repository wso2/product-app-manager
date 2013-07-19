var render = function (theme, data, meta, require) {
    theme('2-column-right', {
        title: data.title,
        navigation: [
            {
                partial: 'navigation',
                context: require('/helpers/navigation.js').currentPage(data.navigation, data.type)
            },
            {
                partial: 'search'
            }
        ],
        header: [
            {
                partial: 'sort-assets',
                context: require('/helpers/sort-assets.js').format(data.sorting, data.paging)
            }
        ],
        body: [
            {
                partial: 'assets',
                context: require('/helpers/assets.js').currentPage(data.assets,data.sso,data.user)
            },
            {
                partial: 'pagination',
                context: require('/helpers/pagination.js').format(data.paging)
            }
        ],
        right: [
            {
                partial: 'recent-assets',
                context: data.recentAssets
            },
            {
                partial: 'tags',
                context: data.tags
            }
        ]
    });
};