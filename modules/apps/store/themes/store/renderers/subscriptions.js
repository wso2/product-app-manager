var render = function (theme, data, meta, require) {
    theme('2-column-right', {
        title: data.title,
        navigation: [
            {
                partial: 'navigation',
                context: data.navigation
            },
            {
                partial: 'search',
                context: data.search
            }
        ],

        body: [
            {
                partial: 'subscriptions',
                context: {
                    'subscriptions': data.subscriptions,
                    'URL': data.URL
                }
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

