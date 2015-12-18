$(function(){

	/*
	Deletes an asset type using an id value
	*/
	$('#btn-delete-asset').on('click',function(){

		var id=$('#meta-asset-id').html();
		var type=$('#meta-asset-type').html();
	
		//TODO: Replace with caramel client
		$.ajax({
			url: caramel.context + '/asset/' + type + '/' + id,
			type:'DELETE',
			success:function(response){
//				alert('asset deleted');

				window.location = caramel.context + '/assets/' + type + '/';
			},
			error:function(response){
				alert('Failed to delete asset.');
			}
		});
	});
	
});
