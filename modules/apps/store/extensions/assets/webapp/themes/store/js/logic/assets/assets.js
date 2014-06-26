$(function() {
		
	$("#assetsLink").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var assetId = $('#asset').data('id');
			  $('#modal-login').data('value', assetId);
			  $("#modal-login").modal('show');
			  e.preventDefault();
			  e.stopPropagation();
		  }
	    	
	});
	
	$("#assetDeatils").on('click', function(e) {
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