$(function() {
		
	$(".asset-icon").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var assetId = $('#asset').data('id');
			  $('#modal-login').data('value', assetId);
			  $("#modal-login").modal('show');
			  e.preventDefault();
			  e.stopPropagation();
		  }
	    	
	});
	
	$(".asset-details").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var assetId = $('#asset').data('id');
			  $('#modal-login').data('value', assetId);
			  $("#modal-login").modal('show');
			  e.preventDefault();
			  e.stopPropagation();
		  }
	    	
	});

 

});