$(document).ready(function(){
	$('.dropdown-toggle').dropdown();
	$('#asset_view_tabs a').click(function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	});
    setTimeout(function(){
        ($('.publisher-left').height() < $('.publisher-right').height()) && $('.publisher-left').height($('.publisher-right').height() + 15);
    }, 200);
});
