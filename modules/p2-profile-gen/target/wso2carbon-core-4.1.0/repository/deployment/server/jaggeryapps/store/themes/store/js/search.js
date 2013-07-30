$(function() {
	History.Adapter.bind(window, 'statechange', function() {
		var state = History.getState();
		if (state.data.id === 'sort-assets') {
			renderAssets(state.data.context);
		} else if (state.data.id === 'top-assets') {
			var el = $('.store-left'), data = state.data.context;
			//caramel.css($('head'), data.header['sort-assets'].resources.css, 'sort-assets');
			//caramel.code($('head'), data.body['assets'].resources.code);
			async.parallel({
				topAssets : function(callback) {
					caramel.render('top-assets', data.body['top-assets'].context, callback);
				}
			}, function(err, result) {
				theme.loaded(el, result.sort);
				el.html(result.topAssets);
				$("#top-asset-slideshow-gadget").carouFredSel({
					items : 4,
					width : "100%",
					infinite : false,
					auto : false,
					circular : false,
					pagination : "#top-asset-slideshow-pag-gadget"

				});

				$("#top-asset-slideshow-site").carouFredSel({
					items : 4,
					width : "100%",
					infinite : false,
					auto : false,
					circular : false,
					pagination : "#top-asset-slideshow-pag-site"

				});
				mouseStop();
				/*el.append(result.paging);
				 caramel.js($('body'), data.body['assets'].resources.js, 'assets', function () {
				 mouseStop();
				 });
				 caramel.js($('body'), data.header['sort-assets'].resources.js, 'sort-assets', function () {
				 updateSortUI();
				 });*/
				$(document).scrollTop(0);
			});
		}
	});

	var search = function() {
		var url;
		if (store.asset) {
			url = caramel.url('/assets/' + store.asset.type + '/?query=' + $('#search').val());
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
							id : 'sort-assets',
							context : data
						}, document.title, url);
					}
				},
				error : function(xhr, status, error) {
					theme.loaded($('#assets-container').parent(), '<p>Error while retrieving data.</p>');
				}
			});
			theme.loading($('#assets-container').parent());
		} else if ($('#search').val().length > 0 && $('#search').val() != undefined) {
			url = caramel.url('/assets/all/?query=' + $('#search').val());
			caramel.data({
				title : null,
				body : ['top-assets']
			}, {
				url : url,
				success : function(data, status, xhr) {
					//TODO: Integrate a new History.js library to fix this
					if ($.browser.msie == true && $.browser.version < 10) {
						renderAssets(data);
					} else {
						History.pushState({
							id : 'top-assets',
							context : data
						}, document.title, url);
					}
				},
				error : function(xhr, status, error) {
					theme.loaded($('#assets-container').parent(), '<p>Error while retrieving data.</p>');
				}
			});
			theme.loading($('#assets-container').parent());
		}
	};

	$('#search').keypress(function(e) {
		if (e.keyCode === 13) {
			search();
		}
	});

	$('#search-button').click(function() {
		search();
		return false;
	});
});
