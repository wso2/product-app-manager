$(function() {

	History.Adapter.bind(window, 'statechange', function() {
		var state = History.getState();
		if (state.data.id === 'tags') {
			renderAssets(state.data.context);
		}
	});

	$('.store-tags').on('click', 'a', function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		caramel.data({
			title : null,
			header : ['sort-assets'],
			body : ['assets', 'pagination']
		}, {
			url : url,
			success : function(data, status, xhr) {
				//TODO: Integrate a new History.js library to fix this
				if ($.browser.msie == true && $.browser.version < 10) {
					renderAssets(data);
				} else {
					History.pushState({
						id : 'tags',
						context : data
					}, document.title, url);
				}

			},
			error : function(xhr, status, error) {
				theme.loaded($('.store-left'), '<p>Error while retrieving data.</p>');
			}
		});
		theme.loading($('.store-left'));
	});
});
