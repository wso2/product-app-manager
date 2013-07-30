var render = function(theme, data, meta, require) {
	//print(caramel.build(data));
	theme('2-column-right', {
		title : data.title,
		navigation : [{
			partial : 'navigation',
			context : require('/helpers/navigation.js').currentPage(data.navigation, data.type)
		}, {
			partial : 'search'
		}],
		body : [{
			partial : 'asset',
			context : {
				user : data.user,
				sso : data.sso,
				asset : data.asset,
				type : data.type,
				inDashboard : data.inDashboard,
                embedURL : data.embedURL
			}
		}],
		right : [{
			partial : 'recent-assets',
			context : data.recentAssets
		}, {
			partial : 'tags',
			context : data.tags
		}]
	});
};
