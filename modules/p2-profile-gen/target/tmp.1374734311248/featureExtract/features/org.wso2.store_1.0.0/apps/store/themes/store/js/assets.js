//TODO: add delay before opening more details
/*
 var timer;
 var details;
 ;
 */

var opened = false;

$(function() {
	var paging = store.asset.paging;
	paging.current = 1;

	$(document).on('click', '#assets-container .asset-add-btn', function(event) {
		var parent = $(this).parent().parent().parent();
		asset.process(parent.data('type'), parent.data('path'), location.href);
		event.stopPropagation();
	});

	$(document).on('click', '.asset > .asset-details', function(event) {
		var link = $(this).find('.asset-name > a').attr('href');
		location.href = link;
	});

	mouseStop();

	History.Adapter.bind(window, 'statechange', function() {
		var state = History.getState();
		if (state.data.id === 'assets') {
			renderAssets(state.data.context);
		}
	});

	var loadAssets = function(url) {
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
						id : 'assets',
						context : data
					}, document.title, url);
				}
			},
			error : function(xhr, status, error) {
				theme.loaded($('#assets-container').parent(), '<p>Error while retrieving data.</p>');
			}
		});
		theme.loading($('.store-left'));
	};

	$(document).on('click', '#ul-sort-assets li a', function(e) {
		$('#ul-sort-assets li a').removeClass('selected-type');
		var thiz = $(this);
		thiz.addClass('selected-type');
		loadAssets(thiz.attr('href'));
		mouseStop();
		e.preventDefault();
	});

	$(document).on('click', '.pagination a', function(e) {
		e.preventDefault();
		var url = $(this).attr('href');
		if (url === '#') {
			return;
		}
		loadAssets(url);
	});

	$("a[data-toggle='tooltip']").tooltip();

	caramel.loaded('js', 'assets');
	caramel.loaded('js', 'sort-assets');
}); 