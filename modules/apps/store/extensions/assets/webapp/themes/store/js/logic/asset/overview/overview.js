$(function(){
	
	$("#gatewayURL").on('click', function(e) {
		  var isSubscribed =   $('#subscribed').val();
		  if(isSubscribed.toLowerCase() === 'false' ){
			  	$('#messageModal').html($('#confirmation-data').html());
			  	$('#messageModal h3.modal-title').html(('Resource forbidden'));
			  	$('#messageModal div.modal-body').html('\n\n'+ ('You have not subscribed to this Application.'));
			  	$('#messageModal a.btn-primary').html('ok');
  		      		   
			  	$('#messageModal').modal();
			  	e.preventDefault();
			  	e.stopPropagation();
		  }else{
		  }
		});
});