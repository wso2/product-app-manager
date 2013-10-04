window.showAlert = function(msg, type){
    	var html = '<div class="info-div alert">';
    		html += ' <a data-dismiss="alert" class="close">x</a><i class="icon-info-sign"></i> <span></span>';
    		html += '</div>';
    		
    	var container = $('.asset-view-container');
    	
    	if(!container.has('.alert').length){
    		container.prepend(html);
    	}
    	var alert = container.find('.alert');
    	alert.removeClass().addClass('info-div alert alert-' + type).find('span').text(msg);
    	alert.fadeIn("fast");
}
    
$(document).ready(function(){
	$('.dropdown-toggle').dropdown();
	$('#asset_view_tabs a').click(function (e) {
	  e.preventDefault();
	  $(this).tab('show');
	});
    setTimeout(function(){
        ($('.publisher-left').height() < $('.publisher-right').height()) && $('.publisher-left').height($('.publisher-right').height() + 15);
    }, 200);
    
    $('.list-asset-table').on('click', 'tr', function(){
    	var link = $(this).find('.asset-listing-name a').attr('href');
    	window.location = link;
    });
    
     
});
