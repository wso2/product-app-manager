$(function(){
	
	$("#gatewayURL").on('click', function(e) {
		  var isSubscribed =   $('#subscribed').val();
		  if(isSubscribed.toLowerCase() === 'false' ){
			  	$('#messageModal2').html($('#confirmation-data2').html());
			  	$('#messageModal2 h3.modal-title').html(('Resource forbidden'));
			  	$('#messageModal2 div.modal-body').html('\n\n'+ ('You have not subscribed to this Application.'));
			  	$('#messageModal2 a.btn-other').html('OK');
  		      		   
			  	$('#messageModal2').modal();
			  	e.preventDefault();
			  	e.stopPropagation();
		  }else{
		  }
		});
});