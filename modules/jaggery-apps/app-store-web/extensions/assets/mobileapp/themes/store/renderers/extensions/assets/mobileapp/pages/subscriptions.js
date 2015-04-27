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

    if(data.userAssets){


        useragent = request.getHeader("User-Agent");


        if(useragent.match(/iPad/i) || useragent.match(/iPhone/i)) {
            userOS = 'ios';
        } else if (useragent.match(/Android/i)) {
            userOS = 'android';
        } else {
            userOS = 'unknown';
        }

        var assets = [];


        for( i = 0; i < data.userAssets.mobileapp.length; i++){

            var platform = data.userAssets.mobileapp[i].attributes.overview_platform;
            switch(userOS){
                case "android":
                    if(platform === "android" || platform === "webapp"){
                        assets.push(data.userAssets.mobileapp[i]);
                    }
                    break;
                case "ios":
                    if(platform === "ios" || platform === "webapp"){
                        assets.push(data.userAssets.mobileapp[i]);
                    }
                    break;
                default:
                    assets.push(data.userAssets.mobileapp[i]);
            }
        }

        data.userAssets.mobileapp = assets;




        for(i = 0; i < data.userAssets.mobileapp.length; i++){
            //print(data.userAssets.mobileapp[i].lifecycleState);
            if(data.userAssets.mobileapp[i].lifecycleState == 'Unpublished'){
                delete data.userAssets.mobileapp.splice (i, 1);;
            }
        }
    }






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
                context: require('/helpers/navigation.js').currentPage(data.navigation, data.type, data.search)
            }
        ],
        /*
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
         */

        body: [
            {
                partial: 'userAssets',
                context: {
                    'userAssets': data.userAssets,
                    'URL': data.URL,
                    'devices': data.devices,
                    'selfUnsubscription' : data.selfUnsubscription,
                    'isDeviceSubscriptionEnabled' : data.isDeviceSubscriptionEnabled
                }
            }
        ],
        right: [
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
