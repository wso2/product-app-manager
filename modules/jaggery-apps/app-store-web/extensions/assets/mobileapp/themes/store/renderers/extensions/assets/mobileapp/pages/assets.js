var render = function (theme, data, meta, require) {


    data.header.config = data.config;




    //filter assets by useragent
    if(data.assets){

        useragent = request.getHeader("User-Agent");

        if(useragent.match(/iPad/i) || useragent.match(/iPhone/i)) {
            userOS = 'ios';
        } else if (useragent.match(/Android/i)) {
            userOS = 'android';
        } else {
            userOS = 'unknown';
        }

        var assets = [];

        for( i = 0; i < data.assets.length; i++){

            var platform = data.assets[i].attributes.overview_platform;
            switch(userOS){
                case "android":
                    if(platform === "android" || platform === "webapp"){
                        assets.push(data.assets[i]);
                    }
                    break;
                case "ios":
                    if(platform === "ios" || platform === "webapp"){
                        assets.push(data.assets[i]);
                    }
                    break;
                default:
                    assets.push(data.assets[i]);
            }
        }

        data.assets = assets;

    }




	
		
    var assets = require('/helpers/assets.js');
    theme('2-column-right', {
        title: data.title,
        header: [
            {
                partial: 'header',
                context: data.header
            }
        ],
        navigation: [
            {
                partial: 'navigation',
                context: require('/helpers/navigation.js').currentPage(data.navigation, data.type, assets.format(data.search))
            }
        ],
       
        body: [
        	{
                partial: 'sort-assets',
                context: require('/helpers/sort-assets.js').format(data.sorting, data.paging, data.navigation, data.type, data.selectedCategory)
            },
            {
                partial: 'assets',
                context: assets.currentPage(data.assets,data.sso,data.user, data.paging,data.config, data.myAssets.pageIndices, data.myAssets.leftNav, data.myAssets.rightNav)

            }/*,
            {
                partial: 'pagination',
                context: require('/helpers/pagination.js').format(data.paging)
            } */
        ],
        right: [
        	{
                partial: 'my-assets-link',
                context: data.myAssets
            },
            {
                partial: 'recent-assets',
                context: require('/helpers/asset.js').formatRatings(data.recentAssets)
            },
            {
                partial: 'tags',
                context: data.tags
            }
        ]
    });
};