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
		var url, searchVal = $('#search').val();
		currentPage = 1;
		if (store.asset) {
			url = caramel.url('/assets/' + store.asset.type + '/?query=' + searchVal);
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
		} else if (searchVal.length > 0 && searchVal != undefined) {
			url = caramel.url('/assets/all/?query=' + searchVal);
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

		$('.search-bar h2').find('.page').text(' / Search: "' + searchVal + '"');
	};

	$('#search').keypress(function(e) {
		if (e.keyCode === 13) {
			search();
		} else if (e.keyCode == 27) {

			$('#search-dropdown-cont').toggle();
		}

	});
	/*
	 $('#search').blur(function(){
	 $(this).fadeOut();
	 $('#search-button').fadeIn("fast");
	 });*/

	$('#search-button').click(function() {
		search();
		/*
		 $(this).fadeOut("fast", function(){
		 $('#search').fadeIn("fast").focus();
		 });*/
	});

	$('#search-dropdown-arrow').click(function(e) {
		e.stopPropagation();
		var icon = $(this).find('i'), cls = icon.attr('class');
		icon.removeClass().addClass(cls == 'icon-sort-down' ? 'icon-sort-up' : 'icon-sort-down');
		$('#search-dropdown-cont').toggle();
	});

	$('#search-dropdown-close').click(function(e) {
		e.stopPropagation();
		$('#search-dropdown-cont').toggle();
	});
	
	$('html').click(function(){
		$('#search-dropdown-cont').hide();
	});
	
	$('#search-dropdown-cont').click(function(e){
		e.stopPropagation();
	});

});
