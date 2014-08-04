$(function() {
		
	$(".asset-icon").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  //var assetId = $('#asset').data('id');
			  var localIP = $("#assetsLocalIP").val();
			  var port = $("#assetshttpsPort").val()
			  location.href = localIP + ":" + port + "/store/login";		  
			  
			  e.preventDefault();
			  e.stopPropagation();
		  }
	    	
	});
	
	$(".asset-details").on('click', function(e) {
		  var loggedUser = $("#assetsloggedinuser").val();
		  if(loggedUser == "" || loggedUser == null){
			  //var assetId = $('#asset').data('id');
			  var localIP = $("#assetsLocalIP").val();
			  var port = $("#assetshttpsPort").val()
			  location.href = localIP + ":" + port + "/store/login";	
			  
			  e.preventDefault();
			  e.stopPropagation();
		  }
	    	
	});

 

});