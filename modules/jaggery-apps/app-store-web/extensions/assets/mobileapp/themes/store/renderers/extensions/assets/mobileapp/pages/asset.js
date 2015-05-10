var render = function(theme, data, meta, require) {
	
	
	var images = data.asset.attributes.images_screenshots.split(",");
	data.asset.attributes.images_screenshots = images;
	
		
	data.header.config = data.config;

	//print(caramel.build(data));
	theme('2-column-right', {
		title : data.title,
		
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
                inDashboard : data.inDashboard,
                isEnterpriseInstallEnabled : data.isEnterpriseInstallEnabled,
                isDeviceSubscriptionEnabled : data.isDeviceSubscriptionEnabled,
                isDirectDownloadEnabled : data.isDirectDownloadEnabled,
                embedURL : data.embedURL,
                isSocial : data.isSocial,
                devices : data.devices,
                config: data.config
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
            }, {
				partial : 'tags',
				context : data.tags
			}
		]
	});
};
