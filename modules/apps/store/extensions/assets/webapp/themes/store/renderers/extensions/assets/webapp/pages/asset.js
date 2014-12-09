var render = function(theme, data, meta, require) {
	var log = new Log();
	
	var assetsByProvider=data.assetsByProvider;
    assetsByProvider['assets']=require('/helpers/rating-provider.js').ratingProvider.formatRating(data.assetsByProvider.assets);
  
	theme('2-column-right', {
		title : data.title,
		metadata:data.metadata,
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
		
        body : [{
            partial : 'asset',
            context : require('/helpers/asset.js').format({
                user : data.user,
                sso : data.sso,
                asset : data.asset,
                type : data.type,
                tenantId : data.tenantId,
                inDashboard : data.inDashboard,
                isSubscribed: data.isSubscribed,
                subscriptionInfo : data.subscriptionInfo,
                isEnterpriseSubscriptionAllowed: data.isEnterpriseSubscriptionAllowed,
                embedURL : data.embedURL,
                isSocial : data.isSocial,
                tabs:{
                    documentation:{
                        data:data.documentation
                    }
                },
                apiData:data.apiData,
                myapps:data.metadata.myapps,
                tiers:data.metadata.tiers
            })
        }],
		right : [
			{
                partial: 'my-assets-link',
                context: data.myAssets
            },
			{
                partial: 'recent-assets',
                context: require('/helpers/asset.js').formatRatings(data.recentAssets)
            },
            {
				partial : 'tags',
				context : data.tags
			},
            {
                partial: 'morefromprovider-widget',
                context: assetsByProvider
            }
		]
	});
};
