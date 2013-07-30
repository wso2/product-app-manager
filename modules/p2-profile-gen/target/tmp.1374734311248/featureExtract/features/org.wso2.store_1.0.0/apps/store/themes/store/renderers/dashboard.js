/*
var render = function (theme, data, meta, require) {
    //print(caramel.build(data));
   
    theme('1-column', {
        title: data.title,
        navigation: [
            {
                partial: 'navigation',
                context: data.navigation
            }
        ],
        body: [
            {
                partial: 'userAssets',
                context: data.userAssets
            }
        ]
    });
};

*/


var render = function (theme, data, meta, require) {
    theme('2-column-right', {
        title: data.title,
        navigation: [
            {
                partial: 'navigation',
                context: data.navigation
            }
        ],
       
        body: [
            {
                partial: 'userAssets',
                context: {
        		'userAssets': data.userAssets,
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

