$(function() {
	$(document).on( 'click',".asset-icon", function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var allowAnonymous = $(this).find("input").val();
			  if (allowAnonymous.toUpperCase() != "TRUE") {
				  var ssoEnabled = $('#sso').val();
				  console.log(ssoEnabled);
				  if (ssoEnabled == 'true') {
					  caramel.tenantedUrl('/login');
				  } else {
					  var assetId = $('#asset').data('id');
					  $('#modal-login').data('value', assetId);
					  $("#modal-login").modal('show');
				  }
				  e.preventDefault();
				  e.stopPropagation();
			  }
		  }
	    	
	});
	
	$(".asset-details").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var allowAnonymous = $(this).find("input").val();
			  if (allowAnonymous.toUpperCase() != "TRUE") {
				  var ssoEnabled = $('#sso').val();
				  if (ssoEnabled == 'true') {
					  caramel.tenantedUrl('/login');
				  } else {
					  var assetId = $('#asset').data('id');
					  $('#modal-login').data('value', assetId);
					  $("#modal-login").modal('show');
				  }
				  e.preventDefault();
				  e.stopPropagation();
			  }
		  }
	    	
	});

	$(".recent-asset-icon").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var allowAnonymous = $(this).find("input").val();
			  if (allowAnonymous.toUpperCase()!="TRUE") {
				  var ssoEnabled = $('#sso').val();
				  if (ssoEnabled == 'true') {
					  caramel.tenantedUrl('/login');
				  } else {
					  var assetId = $('#asset').data('id');
					  $('#modal-login').data('value', assetId);
					  $("#modal-login").modal('show');
				  }
				  e.preventDefault();
				  e.stopPropagation();
			  }
		  }
	    	
	});

	$(".recent-asset-details").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  var allowAnonymous = $(this).find("input").val();
			  if (allowAnonymous.toUpperCase()!="TRUE") {
				  var ssoEnabled = $('#sso').val();
				  if (ssoEnabled == 'true') {
					  location.href =  caramel.tenantedUrl('/login');
				  } else {
					  var assetId = $('#asset').data('id');
					  $('#modal-login').data('value', assetId);
					  $("#modal-login").modal('show');
				  }
				  e.preventDefault();
				  e.stopPropagation();
			  }
		  }
	    	
	});
});
