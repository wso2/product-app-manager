var render = function (theme, data, meta, require) {
    theme('2-column-right',{
        title:'My Applications',
        header:[
            {
                partial:'header',
                context:data.header
            },
            {
                partial:'navigation',
                context:{}
            }
        ],
        body:[
            {
                partial:'myapps',
                context:data
            }
        ],
        right:[
            {
                partial: 'recent-assets',
                context: require('/helpers/asset.js').formatRatings(data.recentAssets)
            }
        ]
    });
}