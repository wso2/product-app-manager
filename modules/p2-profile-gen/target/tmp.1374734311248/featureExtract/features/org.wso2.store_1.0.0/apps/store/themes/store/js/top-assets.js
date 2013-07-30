//TODO: add delay before opening more details
/*
 var timer;
 var details;
 ;
 */

var opened = false;

$(function() {
	var details;

	$(document).on('click', '.assets-container .asset-add-btn', function(event) {
		var parent = $(this).parent().parent().parent();
		asset.process(parent.data('type'), parent.data('path'), location.href);
		event.stopPropagation();
	});

	$(document).on('click', '.asset > .asset-details', function(event) {
		var link = $(this).find('.asset-name > a').attr('href');
		location.href = link;
	});

	mouseStop();

	/*
	 $("#asset-slideshow").carouFredSel({
	 items : 1,
	 width:800,
	 height:300,

	 fx      : "elastic",

	 duration    : 1000,

	 timeoutDuration: 2000,

	 pauseOnHover: true
	 });
	 */

	$("#asset-slideshow").carouFredSel({
		items : {
			visible : 3,
			start : 2
		},
		height : 300,
		scroll : 1,
		/*
		 auto : {
		 duration : 1000,
		 timeoutDuration : 2000,
		 pauseOnHover : true
		 }
		 */
		auto : false,
		prev : {
			button : "#asset-slideshow-next",
			key : "left"
		},
		next : {
			button : "#asset-slideshow-prev",
			key : "right"
		}

	}).find(".slide").hover(function() {
		$(this).find(".asset-intro-box").slideDown("fast");
	}, function() {
		$(this).find(".asset-intro-box").slideUp("fast");
	});
	
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

});

var mouseStop = function() {
	$('.asset').bind('mousestop', 300, function() {
		//console.log("In");
		bookmark = $(this).find('.store-bookmark-icon');
		bookmark.animate({
			top : -200
		}, 200);
		details = $(this).find('.asset-details');
		details.animate({
			top : 0
		}, 200);
		opened = true;
	}).mouseleave(function() {
		//console.log("out");
		bookmark = $(this).find('.store-bookmark-icon');
		bookmark.animate({
			top : -4
		}, 200);
		opened = opened && details.stop(true, true).animate({
			top : 200
		}, 200) ? false : opened;
	});

}

